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
package it.rebirthproject.catalog_dependencies_monitor.gradle.plugins

import it.rebirthproject.catalog_dependencies_monitor.domain.services.context.CatalogMonitorContext
import it.rebirthproject.catalog_dependencies_monitor.gradle.extensions.CatalogMonitorExtension
import it.rebirthproject.catalog_dependencies_monitor.gradle.tasks.*
import org.gradle.api.Plugin
import org.gradle.api.Project

import java.util.jar.JarFile

import static it.rebirthproject.catalog_dependencies_monitor.domain.constants.Constants.*

class CatalogDependenciesMonitorPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        if (project.gradle.gradleVersion < MIN_GRADLE_VERSION) {
            project.logger.error("The plugin \"${PLUGIN_NAME}\" requires at least Gradle ${MIN_GRADLE_VERSION}.")
            return
        }

        println "The plugin ${PLUGIN_NAME} v${readPluginVersionFromJar(project.getLogger())} has been applied to the project ${project.name}"

        final CatalogMonitorExtension catalogMonitorExtension = project.extensions.create("catalogDependenciesMonitor", CatalogMonitorExtension)
        catalogMonitorExtension.mavenRepositoryVersion.convention(DEFAULT_MAVEN_CENTRAL_REPOSITORY_STRING_VERSION)
        catalogMonitorExtension.excludedLibraries.convention([])
        catalogMonitorExtension.excludedPlugins.convention([])
        catalogMonitorExtension.libraryVersionFilters.convention([])
        catalogMonitorExtension.fileCatalogToml.convention(project.layout.projectDirectory.file("${DEFAULT_CATALOG_TOML_FOLDER}/${DEFAULT_CATALOG_TOML_FILE}"))
        catalogMonitorExtension.fileHtmlReport.convention(project.layout.buildDirectory.file("${DEFAULT_REPORT_FOLDER}/${DEFAULT_REPORT_FILE_NAME}.html"))
        catalogMonitorExtension.fileJsonReport.convention(project.layout.buildDirectory.file("${DEFAULT_REPORT_FOLDER}/${DEFAULT_REPORT_FILE_NAME}.json"))

        project.gradle.sharedServices.registerIfAbsent("catalogMonitorContext", CatalogMonitorContext.class, spec -> {
            spec.parameters.mavenRepositoryVersion.convention(catalogMonitorExtension.mavenRepositoryVersion)
            spec.parameters.libraryVersionFilters.convention(catalogMonitorExtension.libraryVersionFilters)
            spec.maxParallelUsages.set(1)
        })

        final def taskGenerateCss = project.tasks.register("_taskGenerateCss", GenerateCssTask) {
            description = "Copy the CSS to the build/report/css folder"
            group = PLUGIN_TASKS_GROUP
            outputs.file(project.layout.buildDirectory.file("${DEFAULT_REPORT_FOLDER}/css/styles.css"))
        }

        final def taskCalculateDependenciesUpdates = project.tasks.register("_calculateDependenciesUpdates", CalculateDependenciesUpdatesTask) {
            description = "Calculate a report on the dependency update status in the rebirth-catalog"
            group = PLUGIN_TASKS_GROUP
            versionCatalog.convention(catalogMonitorExtension.versionCatalog)
            mavenRepositoryVersion.convention(catalogMonitorExtension.mavenRepositoryVersion)
            excludedLibraries.convention(catalogMonitorExtension.excludedLibraries)
            excludedPlugins.convention(catalogMonitorExtension.excludedPlugins)
        }

        def taskGenerateReport = project.tasks.register("generateReport", GenerateReportTask) {
            description = "Generates a report on the dependency update status in the catalog"
            group = PLUGIN_TASKS_GROUP
            dependsOn(taskCalculateDependenciesUpdates, taskGenerateCss)
            fileHtmlReport.convention(catalogMonitorExtension.fileHtmlReport)
            fileJsonReport.convention(catalogMonitorExtension.fileJsonReport)
            outputs.upToDateWhen { false }
        }

        project.tasks.register("_printCatalogContent", PrintCatalogContentTask) {
            description = "Print the catalog content"
            group = PLUGIN_TASKS_GROUP
            versionCatalog.convention(catalogMonitorExtension.versionCatalog)
        }

        project.tasks.register("updateDependenciesInTomlCatalog", UpdateCatalogTask) {
            description = "Updates automatically the dependencies in the catalog toml file"
            group = PLUGIN_TASKS_GROUP
            dependsOn(taskGenerateReport)
            fileCatalogToml.convention(catalogMonitorExtension.fileCatalogToml)
            fileJsonReport.convention(catalogMonitorExtension.fileJsonReport)
        }
    }

    String readPluginVersionFromJar(logger) {
        def codeSource = this.getClass().protectionDomain?.codeSource
        if (codeSource) {
            def jarUrl = codeSource.location
            if (jarUrl) {
                def jarFile = new JarFile(new File(jarUrl.toURI()))
                def manifest = jarFile.getManifest()
                def version = manifest.getMainAttributes().getValue("Implementation-Version")
                if (!version) {
                    logger.error("Plugin version is: Unknown")
                }
                return version
            } else {
                logger.error("Error reading the plugin's version. Plugin's JAR not found.")
            }
        } else {
            logger.error("Error reading the plugin's version. Plugin's source not found.")
        }
    }
}