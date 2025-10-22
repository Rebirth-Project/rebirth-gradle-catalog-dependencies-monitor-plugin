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
package it.rebirthproject.catalog_dependencies_monitor.gradle.tasks

import groovy.util.logging.Slf4j
import it.rebirthproject.catalog_dependencies_monitor.domain.data.dependencies.CatalogAlias
import it.rebirthproject.catalog_dependencies_monitor.domain.data.dependencies.DependencyMetadata
import it.rebirthproject.catalog_dependencies_monitor.domain.data.dependencies.LibraryMetadata
import it.rebirthproject.catalog_dependencies_monitor.domain.data.dependencies.PluginMetadata
import it.rebirthproject.catalog_dependencies_monitor.domain.data.reports.DependenciesReport
import it.rebirthproject.catalog_dependencies_monitor.domain.services.context.CatalogMonitorContext
import it.rebirthproject.catalog_dependencies_monitor.domain.services.report.DependenciesReportCalculator
import it.rebirthproject.catalog_dependencies_monitor.domain.services.repositories.DependenciesRepositoryType
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.services.ServiceReference
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Slf4j
abstract class CalculateDependenciesUpdatesTask extends DefaultTask {

    @Input
    abstract Property<VersionCatalog> getVersionCatalog()

    @Input
    abstract ListProperty<String> getExcludedLibraries()

    @Input
    abstract ListProperty<String> getExcludedPlugins()

    @ServiceReference("catalogMonitorContext")
    abstract Property<CatalogMonitorContext> getCatalogMonitorContext();

    @TaskAction
    void executeTask() {
        final VersionCatalog versionCatalog = versionCatalog.get()
        final List<String> excludedLibraries = excludedLibraries.get()
        final List<String> excludedPlugins = excludedPlugins.get()
        final DependenciesRepositoryType mavenRepositoryType = catalogMonitorContext.get().mavenRepositoryType
        final List<String> libraryVersionFilters = catalogMonitorContext.get().libraryVersionFilters
        final CatalogMonitorContext context = catalogMonitorContext.get()
        final DependenciesReport librariesReport = context.librariesReport
        final DependenciesReport pluginsReport = context.pluginsReport
        final DependenciesReportCalculator reportCalculator = context.dependenciesReportCalculator

        final String formattedDateTime = calculateFormattedDateAndTime()
        librariesReport.dateAndTime = formattedDateTime
        pluginsReport.dateAndTime = formattedDateTime

        log.info("\nCatalog dependencies check started: {}", formattedDateTime)
        log.info("maven repository version: {}", mavenRepositoryType)
        log.info("version catalog name: {}", versionCatalog.name)
        log.info("excluded libraries: {}", excludedLibraries)
        log.info("excluded plugins: {}", excludedPlugins)
        log.info("library version filters: {}\n", libraryVersionFilters)

        versionCatalog.getLibraryAliases().stream()
                .forEach { libCatalogAlias ->
                    versionCatalog.findLibrary(libCatalogAlias).ifPresent { catalogLibraryGroupArtifactAndVersion ->
                        final DependencyMetadata catalogLibrary = new LibraryMetadata(catalogLibraryGroupArtifactAndVersion.get().toString(), new CatalogAlias(libCatalogAlias))
                        log.info("catalog library: {}", catalogLibrary)
                        reportCalculator.populateReportWithCatalogDependency(librariesReport, catalogLibrary, excludedLibraries)
                    }
                }

        versionCatalog.getPluginAliases().stream()
                .forEach { pluginCatalogAlias ->
                    versionCatalog.findPlugin(pluginCatalogAlias).ifPresent { catalogPluginIdAndVersion ->
                        final DependencyMetadata catalogPlugin = new PluginMetadata(catalogPluginIdAndVersion.get().toString(), new CatalogAlias(pluginCatalogAlias))
                        log.info("catalog plugin: {}", catalogPlugin)
                        reportCalculator.populateReportWithCatalogDependency(pluginsReport, catalogPlugin, excludedPlugins)
                    }
                }
    }

    private static String calculateFormattedDateAndTime() {
        final LocalDateTime now = LocalDateTime.now()
        final Locale locale = Locale.getDefault()
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss", locale)
        return now.format(formatter)
    }
}