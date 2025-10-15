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
package it.rebirthproject.catalog_dependencies_monitor.domain.data.dependencies

import groovy.transform.Canonical

@Canonical
class LibraryMetadata implements DependencyMetadata {
    String group
    String artifact
    String version
    CatalogAlias catalogAlias

    LibraryMetadata(String group, String artifact, String version) {
        this.group = group
        this.artifact = artifact
        this.version = version
    }

    LibraryMetadata(String libGroupArtifactAndVersion, CatalogAlias catalogAlias) {
        final List<String> libGroupArtifactAndVersionSplit = libGroupArtifactAndVersion.split(":")
        for (int i = 0; i < libGroupArtifactAndVersionSplit.size(); i++) {
            if (i == 0) {
                this.group = libGroupArtifactAndVersionSplit.get(0)
            } else if (i == 1) {
                this.artifact = libGroupArtifactAndVersionSplit.get(1)
            } else if (i == 2) {
                this.version = libGroupArtifactAndVersionSplit.get(2)
            } else {
                break
            }
        }
        this.catalogAlias = catalogAlias
    }

    @Override
    String getDependencyId() {
        if (group != null && artifact != null) {
            return group + ":" + artifact
        }
        return ""
    }

    @Override
    String getDependencyVersion() { version }
}
    
