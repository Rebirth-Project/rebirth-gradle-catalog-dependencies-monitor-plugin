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
package it.rebirthproject.catalog_dependencies_monitor.gradle.tasks

import groovy.json.JsonOutput
import groovy.util.logging.Slf4j
import it.rebirthproject.catalog_dependencies_monitor.domain.data.reports.DependenciesReport
import it.rebirthproject.catalog_dependencies_monitor.domain.data.reports.LibsAndPluginsReport
import it.rebirthproject.catalog_dependencies_monitor.domain.services.context.CatalogMonitorContext
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.services.ServiceReference
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

@Slf4j
abstract class GenerateReportTask extends DefaultTask {

    @OutputFile
    abstract RegularFileProperty getFileHtmlReport()

    @OutputFile
    abstract RegularFileProperty getFileJsonReport()

    @ServiceReference("catalogMonitorContext")
    abstract Property<CatalogMonitorContext> getCatalogMonitorContext()

    @TaskAction
    void executeTask() {
        final CatalogMonitorContext context = catalogMonitorContext.get()
        final DependenciesReport librariesReport = context.librariesReport
        final DependenciesReport pluginsReport = context.pluginsReport

        final File htmlReportFile = fileHtmlReport.get().asFile
        final String htmlReportContent = context.htmlReport.generateReport(librariesReport, pluginsReport)
        generateReport("html", htmlReportFile, htmlReportContent)

        final File jsonReportFile = fileJsonReport.get().asFile
        final String jsonReportContent = JsonOutput.toJson(new LibsAndPluginsReport(librariesReport, pluginsReport))
        generateReport("json", jsonReportFile, jsonReportContent)
    }

    def generateReport(def reportType, def reportFile, def reportContent) {
        reportFile.createNewFile()
        reportFile.setText(reportContent)
        println(String.format("report '%s' successfully generated! (path: %s)\"", reportType, reportFile.absolutePath))
    }
}