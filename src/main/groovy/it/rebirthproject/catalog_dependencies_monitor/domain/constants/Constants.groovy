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
package it.rebirthproject.catalog_dependencies_monitor.domain.constants

import it.rebirthproject.catalog_dependencies_monitor.domain.services.repositories.DependenciesRepositoryType
import it.rebirthproject.catalog_dependencies_monitor.gradle.plugins.CatalogDependenciesMonitorPlugin

class Constants {

    private Constants() {}

    public static final String PLUGIN_NAME = CatalogDependenciesMonitorPlugin.class.getSimpleName()
    public static final String PLUGIN_TASKS_GROUP = "catalog-monitor"
    public static final String MIN_GRADLE_VERSION = "8.4"
    public static final String DEFAULT_REPORT_FOLDER = "catalog-dependencies-monitor"
    public static final String DEFAULT_REPORT_FILE_NAME = "catalog_report"
    public static final String DEFAULT_CATALOG_TOML_FOLDER = "gradle"
    public static final String DEFAULT_CATALOG_TOML_FILE = "libs.versions.toml"
    public static final String DEFAULT_MAVEN_CENTRAL_REPOSITORY_STRING_VERSION = DependenciesRepositoryType.MAVEN_CENTRAL_V2.name()
}
