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
package it.rebirthproject.catalog_dependencies_monitor.domain.services.mappers

import groovy.json.JsonSlurper
import it.rebirthproject.catalog_dependencies_monitor.domain.data.dependencies.DependencyMetadata
import it.rebirthproject.catalog_dependencies_monitor.domain.data.dependencies.LibraryMetadata
import it.rebirthproject.versioncomparator.comparator.VersionComparator

class MavenDependencyResponseMapper implements RepositoryResponseMapper {

    private final JsonSlurper jsonReader
    private final List<String> librariesFilters
    private final VersionComparator versionComparator

    MavenDependencyResponseMapper(VersionComparator versionComparator, JsonSlurper jsonReader, List<String> librariesFilters) {
        this.versionComparator = versionComparator
        this.jsonReader = jsonReader
        this.librariesFilters = librariesFilters
    }

    @Override
    Optional<DependencyMetadata> map(String jsonMavenResponse) {
        final List<?> docs = jsonReader.parseText(jsonMavenResponse)?.response?.docs
        return getMostRecentDependencyLibrary(docs)
    }

    private Optional<DependencyMetadata> getMostRecentDependencyLibrary(List<?> docs) {
        DependencyMetadata latestVersionMavenDependency = null
        docs.stream()
                .filter { doc -> doc != null && doc.v != null }
                .filter { doc -> isVersionNotToFilter(doc.v as String, librariesFilters) }
                .forEach { doc ->
                    DependencyMetadata mavenDependency = new LibraryMetadata(doc.g, doc.a, doc.v)
                    if (latestVersionMavenDependency == null || isDependencyVersionMoreRecentThan(mavenDependency, latestVersionMavenDependency)) {
                        latestVersionMavenDependency = mavenDependency
                    }
                }
        return Optional.ofNullable(latestVersionMavenDependency)
    }

    private boolean isDependencyVersionMoreRecentThan(DependencyMetadata mavenDependency1, DependencyMetadata mavenDependency2) {
        final String v1 = mavenDependency1.getDependencyVersion()
        final String v2 = mavenDependency2.getDependencyVersion()
        return versionComparator.compare(v1, v2) > 0
    }
}