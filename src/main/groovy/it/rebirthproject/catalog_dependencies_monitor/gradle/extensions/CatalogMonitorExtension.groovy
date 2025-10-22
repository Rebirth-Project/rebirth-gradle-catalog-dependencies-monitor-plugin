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
package it.rebirthproject.catalog_dependencies_monitor.gradle.extensions

import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

interface CatalogMonitorExtension {
    Property<VersionCatalog> getVersionCatalog()

    // If nothing is specified here the Maven repository will be MAVEN_CENTRAL_V2 used as default
    // The MAVEN_CENTRAL_V1 is deprecated and will be removed in future versions
    Property<String> getMavenRepositoryType()

    // NOTE: To exclude a library add the group and artifact separated by a semicolon (net.researchgate:gradle-release)
    ListProperty<String> getExcludedLibraries()

    // NOTE: To exclude a plugin add the id
    ListProperty<String> getExcludedPlugins()

    // NOTE: To filter out library versions containing some string (eg "alpha", "beta")
    ListProperty<String> getLibraryVersionFilters()

//    Property<String> getReportName()

    RegularFileProperty getFileCatalogToml()

    RegularFileProperty getFileHtmlReport()

    RegularFileProperty getFileJsonReport()
}