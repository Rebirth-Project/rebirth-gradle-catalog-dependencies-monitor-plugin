/*
 * Copyright (C) 2025 Matteo Veroni Rebirth project
 * Modifications Copyright (C) 2025 Andrea Paternesi Rebirth project
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
package it.rebirthproject.catalog_dependencies_monitor.domain.services.repositories

import groovy.util.logging.Slf4j
import it.rebirthproject.catalog_dependencies_monitor.domain.data.dependencies.DependencyMetadata
import it.rebirthproject.catalog_dependencies_monitor.domain.data.dependencies.PluginMetadata
import it.rebirthproject.catalog_dependencies_monitor.domain.services.http.HttpClient
import it.rebirthproject.catalog_dependencies_monitor.domain.services.mappers.RepositoryResponseMapper

/**
 GRADLE API Plugin portal (plugins)

 https://plugins.gradle.org/m2/${pluginIdWithSlashes}/${pluginId}.gradle.plugin/maven-metadata.xml

 EXAMPLE: https://plugins.gradle.org/m2/org/beryx/runtime/org.beryx.runtime.gradle.plugin/maven-metadata.xml
 */
@Slf4j
class GradlePortalRepository extends DependenciesRepository {

    GradlePortalRepository(HttpClient httpClient, RepositoryResponseMapper gradlePortalsRepositoryResponseMapper) {
        super(httpClient, gradlePortalsRepositoryResponseMapper)
    }

    @Override
    Optional<DependencyMetadata> getDependencies(DependencyMetadata dependencyMetadata) {
        log.info(dependencyMetadata.dependencyId)
        final String pluginIdWithSlashes = dependencyMetadata.dependencyId.replaceAll("\\.", "/")
        final String pluginId = dependencyMetadata.dependencyId
        final String urlGradlePortal = "${DependenciesRepositoryType.GRADLE_PLUGINS_PORTAL.apiUrl}/m2/${pluginIdWithSlashes}/${pluginId}.gradle.plugin/maven-metadata.xml"
        log.info("gradle portal => GET {}", urlGradlePortal)
        return getDependenciesFromRepository(urlGradlePortal)
    }

    @Override
    DependenciesRepositoryType getType() {
        return DependenciesRepositoryType.GRADLE_PLUGINS_PORTAL
    }
}