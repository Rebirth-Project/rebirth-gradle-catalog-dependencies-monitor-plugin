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

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * Print all the plugins and dependencies declared in the build.gradle with their actual form
 * The output format is compatible both for Groovy and Kotlin build.gradle files
 *
 * plugins { ... id("plugin_id") version "plugin_version" ...}
 *
 * dependencies { ... implementation("group:name:version") ... }
 *
 */
abstract class PrintCatalogResolvedTask extends DefaultTask {

    // NOTE 1: at the moment not used. Maybe, IF NEEDED it's better to pass an EXCLUSION filter from outside instead of this hardcoded INCLUSION filter
    private static final def MAIN_CONFIGS = [
            'api', 'implementation', 'testImplementation', 'annotationProcessor', 'testAnnotationProcessor',
            'compileOnly', 'testCompileOnly', 'runtimeOnly', 'testRuntimeOnly'
    ]

    @Input
    abstract Property<VersionCatalog> getVersionCatalog()

    @TaskAction
    def printDeps() {
        final VersionCatalog catalog = versionCatalog.get()

        println("plugins {")
        resolveCatalogPlugins(catalog)
        println("}")

        println("\ndependencies {")
        resolveCatalogDependencies()
        println("}")
    }

    private void resolveCatalogPlugins(VersionCatalog catalog) {
        catalog.pluginAliases.each { alias ->
            catalog.findPlugin(alias).ifPresent { pluginDep ->
                def id = pluginDep.get().pluginId
                def version = pluginDep.get().version

                if (project.pluginManager.hasPlugin(id)) {
                    String idStarter = "\tid(\"${id}\""
                    String versionFinisher = (version) ? ") version \"${version}\"" : ")"
                    println "${idStarter}${versionFinisher}"
                }
            }
        }
    }

    private void resolveCatalogDependencies() {
        project.configurations.each { Configuration config ->
            config.dependencies.each { dep ->

                // NOTE 2: No filter applied here yet. It should work for all the gradle configurations, but maybe there are cases where
                // to esclude one of them make sense? In that case see also NOTE 1.

//                if (!MAIN_CONFIGS.contains(config.name)) return
                try {
                    def depStr
                    if (dep.hasProperty("group") && dep.hasProperty("name") && dep.hasProperty("version")) {
                        depStr = "${dep.group}:${dep.name}${dep.version ? ":" + dep.version : ''}"
                    } else {
                        depStr = dep.toString()
                    }
                    println "\t${config.name}(\"${depStr}\")"
                } catch (Exception ignored) {
                    println "\t${config.name} ${dep} (cannot resolve)"
                }
            }
        }
    }
}