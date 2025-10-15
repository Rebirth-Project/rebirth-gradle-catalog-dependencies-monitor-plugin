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
package it.rebirthproject.catalog_dependencies_monitor.gradle.tasks

import groovy.util.logging.Slf4j
import it.rebirthproject.catalog_dependencies_monitor.domain.data.reports.DependenciesReportType
import it.rebirthproject.catalog_dependencies_monitor.domain.services.context.CatalogMonitorContext
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.services.ServiceReference
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction

@Slf4j
abstract class UpdateCatalogTask extends DefaultTask {

    @InputFile
    abstract RegularFileProperty getFileCatalogToml()

    @InputFile
    abstract RegularFileProperty getFileJsonReport()

    @ServiceReference("catalogMonitorContext")
    abstract Property<CatalogMonitorContext> getCatalogMonitorContext();

    @TaskAction
    void executeTask() {
        final def catalogUpdateService = catalogMonitorContext.get().catalogUpdateService

        final File catalogFile = fileCatalogToml.get().asFile
        checkIfFileExistsOrThrowException(catalogFile)

        final File jsonReportFile = fileJsonReport.get().asFile
        checkIfFileExistsOrThrowException(jsonReportFile)

        log.info("updating libraries in catalog toml file...")
        catalogUpdateService.updateOutdatedDependencies(catalogFile, jsonReportFile, DependenciesReportType.LIBRARIES_REPORT)
        log.info("updating plugins in catalog toml file...")
        catalogUpdateService.updateOutdatedDependencies(catalogFile, jsonReportFile, DependenciesReportType.PLUGINS_REPORT)

        println "Dependencies updated in toml catalog file"
    }

    private static void checkIfFileExistsOrThrowException(File file) {
        log.debug("checking existance of file: ${file}")
        if (file == null || !file.exists() || !file.isFile()) {
            throw new GradleException("file ${file?.name} doesn't exist or is not a file")
        }
        log.debug("file ${file.name} exists")
    }
}
