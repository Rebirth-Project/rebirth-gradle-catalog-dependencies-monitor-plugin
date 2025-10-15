/*
 * Copyright (C) 2024 Matteo Veroni Rebirth project
 * Modifications copyright (C) 2024 Andrea Paternesi Rebirth project
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
import it.rebirthproject.catalog_dependencies_monitor.domain.services.http.HttpClient
import it.rebirthproject.catalog_dependencies_monitor.domain.services.mappers.RepositoryResponseMapper

/**
 API Maven (libraries)

 EXAMPLE: https://search.maven.org/solrsearch/select?q=g:it.rebirthproject+AND+a:ufo-event-bus&core=gav&rows=10&wt=json
 EXAMPLE: https://search.maven.org/solrsearch/select?q=g:it.rebirthproject+AND+a:ufo-event-bus&core=gav&rows=10&wt=xml
 */
@Slf4j
class MavenRepository extends DependenciesRepository {

    private final short resultRows = 50

    MavenRepository(HttpClient httpClient, RepositoryResponseMapper mavenRepositoryResponseMapper) {
        super(httpClient, mavenRepositoryResponseMapper)
    }

    @Override
    Optional<DependencyMetadata> getDependencies(DependencyMetadata dependencyMetadata) {
        final String[] splitLibraryGroupAndArtifact = dependencyMetadata.dependencyId.split(":")
        final String libraryGroup = splitLibraryGroupAndArtifact[0]
        final String libraryArtifact = splitLibraryGroupAndArtifact[1]
        final String urlMavenCentral = "https://search.maven.org/solrsearch/select?q=g:${libraryGroup}+AND+a:${libraryArtifact}&core=gav&rows=${resultRows}&wt=json"
        log.info("maven => GET {}", urlMavenCentral)
        return getDependenciesFromRepository(urlMavenCentral)
    }

    @Override
    DependenciesRepositoryType getType() {
        return DependenciesRepositoryType.MAVEN_CENTRAL
    }

    @Override
    String getHttpUrlOfDependencyInRepository(String repositoryBaseUrl, DependencyMetadata dependencyMetadata) {
        final LibraryMetadata library = dependencyMetadata as LibraryMetadata
        if (library == null) {
            return repositoryBaseUrl
        } else {
            return "${repositoryBaseUrl}/artifact/${library.group}/${library.artifact}"
        }
    }
}