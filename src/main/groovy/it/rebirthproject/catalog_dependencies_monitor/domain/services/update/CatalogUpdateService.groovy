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
package it.rebirthproject.catalog_dependencies_monitor.domain.services.update

import groovy.json.JsonSlurper
import groovy.toml.TomlSlurper
import groovy.util.logging.Slf4j
import it.rebirthproject.catalog_dependencies_monitor.domain.services.repositories.DependenciesRepositoryType

import static it.rebirthproject.catalog_dependencies_monitor.domain.services.repositories.DependenciesRepositoryType.*

@Slf4j
class CatalogUpdateService {

    private final JsonSlurper jsonReader
    private final TomlSlurper tomlReader

    CatalogUpdateService(JsonSlurper jsonReader, TomlSlurper tomlReader) {
        this.jsonReader = jsonReader
        this.tomlReader = tomlReader
    }

    def updateOutdatedDependencies(File catalogFile, File jsonReportFile, DependenciesRepositoryType dependenciesRepositoryType) {
        final def jsonReportContent = jsonReader.parse(jsonReportFile)
        log.debug("jsonReportContent: ${jsonReportContent}")

        def specificJsonReportContent = switch (dependenciesRepositoryType) {
            case MAVEN_CENTRAL_V1, MAVEN_CENTRAL_V2 -> jsonReportContent?.librariesReport
            case GRADLE_PLUGINS_PORTAL -> jsonReportContent?.pluginsReport
        }

        log.debug("specificJsonReportContent: ${specificJsonReportContent}")

        specificJsonReportContent?.outdated?.reports?.each { outdatedDependency ->
            log.info("outdated dependency to update => ${outdatedDependency}")

            def isDependencyUpdated = false

            final def catalogAlias = outdatedDependency?.dependencyInCatalog?.catalogAlias?.name?.replaceAll("\\.", "-")
            final def versionInRepo = outdatedDependency?.dependencyInRepo?.dependencyVersion

            log.info("catalogAlias: ${catalogAlias}")
            log.info("versionInRepo: ${versionInRepo}")

            if (catalogAlias && versionInRepo) {
                final def catalogContent = catalogFile.text
                final def catalogTomlContent = tomlReader.parseText(catalogContent)

                final def version = catalogTomlContent?."${reportType.tomlDescription}"?."${catalogAlias}"?.version
                if (version) {
                    if (version instanceof String) {
                        final def updatedCatalogContent = catalogContent.replaceAll(/(${catalogAlias}\s*=\s*.*version\s*=\s*")([^"]*)(?=")/, "\$1${versionInRepo}")
                        catalogFile.write(updatedCatalogContent)
                        isDependencyUpdated = true
                        log.info("dependency ${outdatedDependency} updated with version ${versionInRepo}")
                    } else {
                        def versionRef = version?.ref
                        if (versionRef) {
                            final def updatedCatalogContent = catalogContent.replaceAll(/(${versionRef}\s*=\s*")([^"]*)(?=")/, "\$1${versionInRepo}")
                            catalogFile.write(updatedCatalogContent)
                            isDependencyUpdated = true
                            log.info("dependency ${outdatedDependency} updated with version ${versionInRepo}")
                        }
                    }
                }
            }

            if (!isDependencyUpdated) {
                log.warn("WARNING: dependency ${outdatedDependency} not updated")
            }

            log.info("-------------------------------------------------------")
        }
    }
}
