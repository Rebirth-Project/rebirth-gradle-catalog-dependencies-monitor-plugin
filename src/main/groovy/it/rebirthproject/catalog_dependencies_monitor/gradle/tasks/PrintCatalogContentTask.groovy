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
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

@Slf4j
abstract class PrintCatalogContentTask extends DefaultTask {

    @Input
    abstract Property<VersionCatalog> getVersionCatalog()

    @TaskAction
    void executeTask() {
        final VersionCatalog versionCatalog = versionCatalog.get()
        println "version catalog name: ${versionCatalog.name}"
        println ""
        println "[versions]"
        versionCatalog.getVersionAliases().stream()
                .forEach { versionCatalogAlias ->
                    versionCatalog.findVersion(versionCatalogAlias).ifPresent { catalogVersion ->
                        println "${versionCatalogAlias} = ${catalogVersion}"
                    }
                }
        println ""
        println "[libraries]"
        versionCatalog.getLibraryAliases().stream()
                .forEach { libCatalogAlias ->
                    versionCatalog.findLibrary(libCatalogAlias).ifPresent { catalogLibraryGroupArtifactAndVersion ->
                        println "${libCatalogAlias} = ${catalogLibraryGroupArtifactAndVersion.get()}"
                    }
                }
        println ""
        println "[bundles]"
        versionCatalog.getBundleAliases().stream()
                .forEach { bundleCatalogAlias ->
                    versionCatalog.findBundle(bundleCatalogAlias).ifPresent { catalogBundle ->
                        println "${bundleCatalogAlias} = ${catalogBundle.get()}"
                    }
                }
        println ""
        println "[plugins]"
        versionCatalog.getPluginAliases().stream()
                .forEach { pluginCatalogAlias ->
                    versionCatalog.findPlugin(pluginCatalogAlias).ifPresent { catalogPluginIdAndVersion ->
                        println "${pluginCatalogAlias} = ${catalogPluginIdAndVersion.get()}"
                    }
                }
    }
}