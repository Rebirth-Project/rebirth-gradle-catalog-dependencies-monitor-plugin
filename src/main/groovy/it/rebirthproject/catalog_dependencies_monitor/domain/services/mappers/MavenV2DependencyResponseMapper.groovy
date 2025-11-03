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
import it.rebirthproject.catalog_dependencies_monitor.domain.data.dependencies.LibraryMetadata
import it.rebirthproject.versioncomparator.comparator.VersionComparator
import org.w3c.dom.Document

@Slf4j
class MavenV2DependencyResponseMapper implements RepositoryResponseMapper {
    
    //can be "release" or "latest"
    private final String versionType

    MavenV2DependencyResponseMapper(String versionType) {
        this.versionType= versionType;
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
        
        final String providedXmlLatestVersion = rootNode.getElementsByTagName(versionType)?.item(0)?.firstChild?.nodeValue
        
        return Optional.ofNullable(providedXmlLatestVersion)        
        .map(version -> new LibraryMetadata(groupId, artifactId, version));        
    }
}