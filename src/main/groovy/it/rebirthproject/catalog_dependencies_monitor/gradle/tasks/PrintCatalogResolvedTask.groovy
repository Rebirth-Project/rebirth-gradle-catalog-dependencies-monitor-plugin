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
import org.gradle.api.provider.ListProperty
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
    final ListProperty<String> resolvedPlugins = project.objects.listProperty(String)

    @Input
    final ListProperty<String> resolvedDeps = project.objects.listProperty(String)

    @TaskAction
    def printDeps() {
        println("\nplugins {")
        resolvedPlugins.get().each { println("\t${it}")}
        println("}")

        println("\ndependencies {")
        resolvedDeps.get().each { println("\t${it}") }
        println("}")
    }
}