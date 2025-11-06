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
package it.rebirthproject.catalog_dependencies_monitor.domain.services.mappers

import groovy.util.logging.Slf4j
import groovy.xml.DOMBuilder
import it.rebirthproject.catalog_dependencies_monitor.domain.data.dependencies.DependencyMetadata
import it.rebirthproject.catalog_dependencies_monitor.domain.data.dependencies.PluginMetadata
import it.rebirthproject.versioncomparator.comparator.VersionComparator
import org.w3c.dom.Document

@Slf4j
// TODO: this and MavenV2 mapper has a lot of similar/duplicated code to refactor
class GradlePortalsPluginsResponseMapper implements RepositoryResponseMapper {

    private final VersionComparator versionComparator
    private final List<String> pluginsFilters

    GradlePortalsPluginsResponseMapper(VersionComparator versionComparator, List<String> pluginsFilters) {
        this.versionComparator = versionComparator
        this.pluginsFilters = pluginsFilters
    }

    @Override
    Optional<DependencyMetadata> map(String xmlResponse) {
        try {
            final Document rootNode = DOMBuilder.parse(new StringReader(xmlResponse))

            return getMostRecentDependencyLibraryIfPresent(rootNode)
        } catch (Exception e) {
            log.error("Error trying to map the XML response", e)
            return Optional<DependencyMetadata>.empty()
        }
    }

    private Optional<DependencyMetadata> getMostRecentDependencyLibraryIfPresent(Document rootNode) {
        final String groupId = rootNode.getElementsByTagName('groupId').item(0).firstChild.nodeValue
        final String artifactId = rootNode.getElementsByTagName('artifactId').item(0).firstChild.nodeValue

        if (!groupId || !artifactId) {
            log.warn("Response missing groupId or artifactId")
            return Optional.empty()
        }

        final String providedXmlLatestVersion = rootNode.getElementsByTagName('release')?.item(0)?.firstChild?.nodeValue

        if (providedXmlLatestVersion != null && isVersionNotToFilter(providedXmlLatestVersion, pluginsFilters)) {
            // First difference PluginMetadata instead of LibraryMetadata. and it takes 2 constructor args instead of 3
            return Optional.of(new PluginMetadata(groupId, providedXmlLatestVersion))
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

        PluginMetadata mostRecent = null
        for (int i = 0; i < versionNodes.length; i++) {
            String version = versionNodes.item(i)?.textContent
            if (version && isVersionNotToFilter(version, pluginsFilters)) {
                try {
                    PluginMetadata plugin = new PluginMetadata(groupId, version)
                    if (mostRecent == null || versionComparator.compare(plugin.dependencyVersion, mostRecent.dependencyVersion) > 0) {
                        mostRecent = plugin
                    }
                } catch (IllegalArgumentException ex) {
                    log.warn("VersionComparator error", ex)
                }
            }
        }

        return Optional.ofNullable(mostRecent)
    }
}