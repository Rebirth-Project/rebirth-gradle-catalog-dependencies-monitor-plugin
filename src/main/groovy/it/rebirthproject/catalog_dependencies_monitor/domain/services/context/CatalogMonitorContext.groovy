/*
 * Copyright (C) 2025 Matteo Veroni Rebirth project
 * Modifications copyright (C) 2025 Andrea Paternesi Rebirth project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.rebirthproject.catalog_dependencies_monitor.domain.services.context

import groovy.json.JsonSlurper
import groovy.toml.TomlSlurper
import it.rebirthproject.catalog_dependencies_monitor.domain.data.reports.DependenciesReport
import it.rebirthproject.catalog_dependencies_monitor.domain.data.reports.DependenciesReportType
import it.rebirthproject.catalog_dependencies_monitor.domain.services.factory.DependenciesRepositoryFactory
import it.rebirthproject.catalog_dependencies_monitor.domain.services.html.HtmlReportGenerator
import it.rebirthproject.catalog_dependencies_monitor.domain.services.http.HttpClient
import it.rebirthproject.catalog_dependencies_monitor.domain.services.mappers.GradlePortalsPluginsResponseMapper
import it.rebirthproject.catalog_dependencies_monitor.domain.services.mappers.MavenV1DependencyResponseMapper
import it.rebirthproject.catalog_dependencies_monitor.domain.services.mappers.MavenV2DependencyResponseMapper
import it.rebirthproject.catalog_dependencies_monitor.domain.services.report.DependenciesReportCalculator
import it.rebirthproject.catalog_dependencies_monitor.domain.services.repositories.DependenciesRepository
import it.rebirthproject.catalog_dependencies_monitor.domain.services.repositories.DependenciesRepositoryType
import it.rebirthproject.catalog_dependencies_monitor.domain.services.repositories.GradlePortalRepository
import it.rebirthproject.catalog_dependencies_monitor.domain.services.repositories.MavenV1Repository
import it.rebirthproject.catalog_dependencies_monitor.domain.services.repositories.MavenV2Repository
import it.rebirthproject.catalog_dependencies_monitor.domain.services.update.CatalogUpdateService
import it.rebirthproject.versioncomparator.comparator.VersionComparator
import it.rebirthproject.versioncomparator.comparator.VersionComparatorBuilder
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters

// https://docs.gradle.org/current/userguide/build_services.html
abstract class CatalogMonitorContext implements BuildService<Params>, AutoCloseable {

    interface Params extends BuildServiceParameters {
        Property<String> getMavenRepositoryVersion()

        ListProperty<String> getLibraryVersionFilters()
    }

    private final DependenciesReportCalculator reportCalculator
    private final CatalogUpdateService catalogUpdateService
    private final HtmlReportGenerator htmlReport
    private final DependenciesReport librariesReport
    private final DependenciesReport pluginsReport
    private final String mavenRepositoryStringType

    CatalogMonitorContext() {
        final VersionComparator versionComparator = new VersionComparatorBuilder().useMavenRulesVersionParser().build()
        final JsonSlurper jsonReader = new JsonSlurper()
        final TomlSlurper tomlReader = new TomlSlurper()
        final HttpClient httpClient = new HttpClient()
        final List<String> libVersionFilters = getParameters().getLibraryVersionFilters().get()
        
        mavenRepositoryStringType = getParameters().getMavenRepositoryVersion().get()  
        final DependenciesRepositoryType mavenRepositoryType = DependenciesRepositoryType.getMavenRepositoryTypeFromString(mavenRepositoryStringType)        
        final DependenciesRepositoryFactory dependenciesRepositoryFactory = new DependenciesRepositoryFactory(versionComparator,jsonReader,libVersionFilters)

        this.mavenRepositoryVersion = mavenCentralRepository.getVersion().name()
        
        this.librariesReport = new DependenciesReport(DependenciesRepositoryType.MAVEN_CENTRAL_V2)
        this.pluginsReport = new DependenciesReport(DependenciesRepositoryType.GRADLE_PLUGINS_PORTAL)
        
        //TODO vediamo il version comparator?
        this.reportCalculator = new DependenciesReportCalculator(versionComparator, dependenciesRepositoryFactory)
        this.htmlReport = new HtmlReportGenerator(dependenciesRepositoryFactory)
        this.catalogUpdateService = new CatalogUpdateService(jsonReader, tomlReader)
    }
    
    String getMavenRepositoryType() { mavenRepositoryStringType }
    
    List<String> getLibraryVersionFilters() { getParameters().libraryVersionFilters.get() }

    DependenciesReport getLibrariesReport() { librariesReport }

    DependenciesReport getPluginsReport() { pluginsReport }

    DependenciesReportCalculator getDependenciesReportCalculator() { reportCalculator }

    HtmlReportGenerator getHtmlReport() { htmlReport }

    CatalogUpdateService getCatalogUpdateService() { catalogUpdateService }

    @Override
    void close() {
        // dispose something here if needed ...
    }
}
