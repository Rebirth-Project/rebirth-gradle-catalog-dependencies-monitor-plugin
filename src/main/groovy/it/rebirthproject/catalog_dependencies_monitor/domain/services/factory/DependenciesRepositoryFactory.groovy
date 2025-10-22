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
package it.rebirthproject.catalog_dependencies_monitor.domain.services.factory

import groovy.json.JsonSlurper
import it.rebirthproject.catalog_dependencies_monitor.domain.services.http.HttpClient
import it.rebirthproject.catalog_dependencies_monitor.domain.services.mappers.GradlePortalsPluginsResponseMapper
import it.rebirthproject.catalog_dependencies_monitor.domain.services.mappers.MavenV1DependencyResponseMapper
import it.rebirthproject.catalog_dependencies_monitor.domain.services.mappers.MavenV2DependencyResponseMapper
import it.rebirthproject.catalog_dependencies_monitor.domain.services.repositories.*
import it.rebirthproject.versioncomparator.comparator.VersionComparator

class DependenciesRepositoryFactory {

    private final HttpClient httpClient
    private final VersionComparator versionComparator
    private final JsonSlurper jsonReader
    private final List<String> libVersionFilters

    DependenciesRepositoryFactory(HttpClient httpClient, VersionComparator versionComparator, JsonSlurper jsonReader, List<String> libVersionFilters) {
        this.httpClient = httpClient
        this.versionComparator = versionComparator
        this.jsonReader = jsonReader
        this.libVersionFilters = libVersionFilters
    }

    DependenciesRepository create(DependenciesRepositoryType reportType) {
        switch (reportType) {
            case DependenciesRepositoryType.MAVEN_CENTRAL_V1: return new MavenV1Repository(httpClient, new MavenV1DependencyResponseMapper(versionComparator, jsonReader, libVersionFilters))
            case DependenciesRepositoryType.MAVEN_CENTRAL_V2: return new MavenV2Repository(httpClient, new MavenV2DependencyResponseMapper(versionComparator, libVersionFilters))
            case DependenciesRepositoryType.GRADLE_PLUGINS_PORTAL: return new GradlePortalRepository(httpClient, new GradlePortalsPluginsResponseMapper())
        }
    }
}
        