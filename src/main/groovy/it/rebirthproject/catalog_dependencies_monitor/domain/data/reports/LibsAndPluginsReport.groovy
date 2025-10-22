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
package it.rebirthproject.catalog_dependencies_monitor.domain.data.reports;

/**
 * Represents a report containing information about libraries and plugins in a project.
 * This class provides access to detailed reports for both libraries and plugins,
 * encapsulating their respective dependencies.
 */
class LibsAndPluginsReport {

    DependenciesReport librariesReport;
    DependenciesReport pluginsReport;

    /**
     * Constructs a new {@code LibsAndPluginsReport} with the given libraries and plugins reports.
     *
     * @param librariesReport the report containing details about library dependencies
     * @param pluginsReport the report containing details about plugin dependencies
     */
    LibsAndPluginsReport(DependenciesReport librariesReport, DependenciesReport pluginsReport) {
        this.librariesReport = librariesReport;
        this.pluginsReport = pluginsReport;
    }

    /**
     * Returns the report containing details about library dependencies.
     *
     * @return the libraries report
     */
    DependenciesReport getLibrariesReport() {
        return librariesReport;
    }

    /**
     * Returns the report containing details about plugin dependencies.
     *
     * @return the plugins report
     */
    DependenciesReport getPluginsReport() {
        return pluginsReport;
    }
}
