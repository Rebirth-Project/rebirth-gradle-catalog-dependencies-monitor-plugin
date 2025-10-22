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
package it.rebirthproject.catalog_dependencies_monitor.domain.services.mappers


import groovy.util.logging.Slf4j
import groovy.xml.DOMBuilder
import it.rebirthproject.catalog_dependencies_monitor.domain.data.dependencies.DependencyMetadata
import it.rebirthproject.catalog_dependencies_monitor.domain.data.dependencies.LibraryMetadata
import it.rebirthproject.versioncomparator.comparator.VersionComparator
import org.w3c.dom.Document

@Slf4j
class MavenV2DependencyResponseMapper implements RepositoryResponseMapper {

    private final VersionComparator versionComparator
    private final List<String> librariesFilters

    MavenV2DependencyResponseMapper(VersionComparator versionComparator, List<String> librariesFilters) {
        this.versionComparator = versionComparator
        this.librariesFilters = librariesFilters
    }

    @Override
    Optional<DependencyMetadata> map(String xmlResponse) {
        try {
            final Document rootNode = DOMBuilder.parse(new StringReader(xmlResponse))

            return getMostRecentDependencyLibraryIfPresent(rootNode)
        } catch (Exception e) {
            log.error("Errore durante il mapping della risposta XML", e)
            return Optional<DependencyMetadata>.empty()
        }
    }

    private Optional<DependencyMetadata> getMostRecentDependencyLibraryIfPresent(Document rootNode) {
        final String groupId = rootNode.getElementsByTagName('groupId').item(0).firstChild.nodeValue
        final String artifactId = rootNode.getElementsByTagName('artifactId').item(0).firstChild.nodeValue

        if (!groupId || !artifactId) {
            log.warn("Maven response missing groupId or artifactId")
            return Optional.empty()
        }

        if (librariesFilters.isEmpty()) {
            final String providedXmlLatestVersion = rootNode.getElementsByTagName('latest')?.item(0)?.firstChild?.nodeValue

            return Optional.ofNullable(providedXmlLatestVersion)
                    .map { version -> new LibraryMetadata(groupId, artifactId, version) }
                    .or(() -> calculateMostRecentDependencyLibraryIfPresent(rootNode, groupId, artifactId))
        } else {
            return calculateMostRecentDependencyLibraryIfPresent(rootNode, groupId, artifactId)
        }
    }

    private Optional<DependencyMetadata> calculateMostRecentDependencyLibraryIfPresent(Document rootNode, String groupId, String artifactId) {
        def versionNodes = rootNode.getElementsByTagName('version')
        if (versionNodes == null || versionNodes.length == 0) {
            log.warn("No <version> tags found for ${groupId}:${artifactId}")
            return Optional.empty()
        }

        LibraryMetadata mostRecent = null
        for (int i = 0; i < versionNodes.length; i++) {
            String version = versionNodes.item(i)?.textContent
            if (version && isVersionNotToFiltered(version, librariesFilters)) {
                LibraryMetadata lib = new LibraryMetadata(groupId, artifactId, version)
                if (mostRecent == null || versionComparator.compare(lib.dependencyVersion, mostRecent.dependencyVersion) > 0) {
                    mostRecent = lib
                }
            }
        }

        return Optional.ofNullable(mostRecent)
    }
}