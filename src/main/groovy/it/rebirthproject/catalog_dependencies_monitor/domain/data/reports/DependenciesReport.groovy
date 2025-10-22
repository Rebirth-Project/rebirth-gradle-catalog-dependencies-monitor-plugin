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
package it.rebirthproject.catalog_dependencies_monitor.domain.data.reports

import it.rebirthproject.catalog_dependencies_monitor.domain.services.repositories.DependenciesRepositoryType

class DependenciesReport {
    final DependencyReportList updated = new DependencyReportList("updated", "Updated Dependencies")
    final DependencyReportList outdated = new DependencyReportList("outdated", "Outdated Dependencies")
    final DependencyReportList exceeding = new DependencyReportList("exceeding", "Exceeding Dependencies")
    final DependencyReportList notFound = new DependencyReportList("notfound", "Not Found Dependencies")
    final DependencyReportList excluded = new DependencyReportList("excluded", "Excluded Dependencies")
    final DependencyReportList skipped = new DependencyReportList("skipped", "Skipped Dependencies")
    final List<DependencyReportList> reports = [outdated, exceeding, updated, notFound, excluded, skipped]
    final DependenciesRepositoryType dependenciesRepositoryType
    String dateAndTime

    DependenciesReport(DependenciesRepositoryType dependenciesRepositoryType) {
        this.dependenciesRepositoryType = dependenciesRepositoryType
    }
}