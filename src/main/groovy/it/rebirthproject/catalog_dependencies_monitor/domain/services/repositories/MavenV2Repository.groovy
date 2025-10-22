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
import it.rebirthproject.catalog_dependencies_monitor.domain.services.http.HttpClient
import it.rebirthproject.catalog_dependencies_monitor.domain.services.mappers.RepositoryResponseMapper

/**
 API Maven V2 (libraries)

 EXAMPLE: https://repo.maven.apache.org/maven2/org/junit/junit-bom/maven-metadata.xml
 EXAMPLE: https://repo.maven.apache.org/maven2/org/slf4j/slf4j-api/maven-metadata.xml
 EXAMPLE: https://repo.maven.apache.org/maven2/org/postgresql/postgresql/maven-metadata.xml
 EXAMPLE: https://repo1.maven.org/maven2/org/postgresql/postgresql/maven-metadata.xml

 */
@Slf4j
class MavenV2Repository extends MavenRepository {

    private static final String REPOSITORY_BASE_URL = "https://repo.maven.apache.org"

    MavenV2Repository(HttpClient httpClient, RepositoryResponseMapper mavenRepositoryResponseMapper) {
        super(httpClient, mavenRepositoryResponseMapper)
    }

    @Override
    Optional<DependencyMetadata> getDependencies(DependencyMetadata dependencyMetadata) {
        final String[] splitLibraryGroupAndArtifact = dependencyMetadata.dependencyId.split(":")
        final String libraryGroup = splitLibraryGroupAndArtifact[0]
        final String libraryGroupWithSlashSeparators = libraryGroup.replaceAll("\\.", "\\/")
        final String libraryArtifact = splitLibraryGroupAndArtifact[1]

        final String urlMavenCentral = "${REPOSITORY_BASE_URL}/maven2/${libraryGroupWithSlashSeparators}/${libraryArtifact}/maven-metadata.xml"
        log.info("maven2 => GET {}", urlMavenCentral)

        return getDependenciesFromRepository(urlMavenCentral)
    }
}