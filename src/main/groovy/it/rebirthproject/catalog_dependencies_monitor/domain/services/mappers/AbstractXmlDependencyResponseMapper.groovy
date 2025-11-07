package it.rebirthproject.catalog_dependencies_monitor.domain.services.mappers

import groovy.util.logging.Slf4j
import groovy.xml.DOMBuilder
import it.rebirthproject.catalog_dependencies_monitor.domain.data.dependencies.DependencyMetadata
import it.rebirthproject.versioncomparator.comparator.VersionComparator
import org.w3c.dom.Document

@Slf4j
abstract class AbstractXmlDependencyResponseMapper implements RepositoryResponseMapper {

    protected final VersionComparator versionComparator
    protected final List<String> filters

    AbstractXmlDependencyResponseMapper(VersionComparator versionComparator, List<String> filters) {
        this.versionComparator = versionComparator
        this.filters = filters
    }

    @Override
    Optional<DependencyMetadata> map(String xmlResponse) {
        try {
            final Document rootNode = DOMBuilder.parse(new StringReader(xmlResponse))
            return getMostRecentDependencyIfPresent(rootNode)
        } catch (Exception e) {
            log.error("Error trying to map the XML response", e)
            return Optional.empty()
        }
    }

    // Abstract method to be implemented by subclasses!
    protected abstract DependencyMetadata createDependencyMetadata(String groupId, String artifactId, String version)

    private Optional<DependencyMetadata> getMostRecentDependencyIfPresent(Document rootNode) {
        final String groupId = rootNode.getElementsByTagName('groupId').item(0)?.firstChild?.nodeValue
        final String artifactId = rootNode.getElementsByTagName('artifactId').item(0)?.firstChild?.nodeValue

        if (!groupId || !artifactId) {
            log.warn("Response missing groupId or artifactId")
            return Optional.empty()
        }

        final String providedXmlLatestVersion = rootNode.getElementsByTagName('release')?.item(0)?.firstChild?.nodeValue

        if (providedXmlLatestVersion && isVersionNotToFilter(providedXmlLatestVersion, filters)) {
            return Optional.of(createDependencyMetadata(groupId, artifactId, providedXmlLatestVersion))
        } else {
            return calculateMostRecentDependencyIfPresent(rootNode, groupId, artifactId)
        }
    }

    private Optional<DependencyMetadata> calculateMostRecentDependencyIfPresent(Document rootNode, String groupId, String artifactId) {
        def versionNodes = rootNode.getElementsByTagName('version')
        if (!versionNodes || versionNodes.length == 0) {
            log.warn("No <version> tags found for ${groupId}:${artifactId}")
            return Optional.empty()
        }

        DependencyMetadata mostRecent = null
        for (int i = 0; i < versionNodes.length; i++) {
            String version = versionNodes.item(i)?.textContent
            if (version && isVersionNotToFilter(version, filters)) {
                try {
                    DependencyMetadata current = createDependencyMetadata(groupId, artifactId, version)
                    if (mostRecent == null || versionComparator.compare(current.dependencyVersion, mostRecent.dependencyVersion) > 0) {
                        mostRecent = current
                    }
                } catch (IllegalArgumentException ex) {
                    log.warn("VersionComparator error", ex)
                }
            }
        }

        return Optional.ofNullable(mostRecent)
    }
}