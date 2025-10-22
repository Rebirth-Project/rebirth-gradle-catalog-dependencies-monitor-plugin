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
package it.rebirthproject.catalog_dependencies_monitor.domain.services.repositories

import groovy.util.logging.Slf4j
import it.rebirthproject.catalog_dependencies_monitor.domain.data.dependencies.DependencyMetadata
import it.rebirthproject.catalog_dependencies_monitor.domain.data.dependencies.LibraryMetadata
import it.rebirthproject.catalog_dependencies_monitor.domain.data.dependencies.PluginMetadata
import it.rebirthproject.catalog_dependencies_monitor.domain.services.http.HttpClient
import it.rebirthproject.catalog_dependencies_monitor.domain.services.mappers.RepositoryResponseMapper

@Slf4j
abstract class DependenciesRepository {

    private final HttpClient httpClient
    private final RepositoryResponseMapper repositoryResponseMapper

    DependenciesRepository(HttpClient httpClient, RepositoryResponseMapper repositoryResponseMapper) {
        this.httpClient = httpClient
        this.repositoryResponseMapper = repositoryResponseMapper
    }

    abstract Optional<DependencyMetadata> getDependencies(DependencyMetadata dependencyMetadata)

    abstract DependenciesRepositoryType getType()

    Optional<DependencyMetadata> getDependenciesFromRepository(String urlRepository) {
        final Optional<String> optionalResponse = httpClient.get(urlRepository)
        if (optionalResponse.isPresent()) {
            final String response = optionalResponse.get()
            log.debug("response: {}", response)
            return repositoryResponseMapper.map(response)
        } else {
            return Optional.empty()
        }
    }    
    
    String getHttpUrlOfDependencyInRepository(String repositoryBaseUrl, DependencyMetadata dependencyMetadata) {
        if (dependencyMetadata instanceof PluginMetadata) {
            def plugin = (PluginMetadata) dependencyMetadata
            return "${repositoryBaseUrl}/plugin/${plugin.id}"
        } else if (dependencyMetadata instanceof LibraryMetadata) {
            def library = (LibraryMetadata) dependencyMetadata
            return "${repositoryBaseUrl}/artifact/${library.group}/${library.artifact}"
        } else {
            return repositoryBaseUrl
        }
    }
}