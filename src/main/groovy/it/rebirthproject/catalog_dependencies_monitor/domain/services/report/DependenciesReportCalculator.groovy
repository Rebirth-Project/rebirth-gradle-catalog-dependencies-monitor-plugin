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
package it.rebirthproject.catalog_dependencies_monitor.domain.services.report

import groovy.util.logging.Slf4j
import it.rebirthproject.catalog_dependencies_monitor.domain.data.dependencies.DependencyMetadata
import it.rebirthproject.catalog_dependencies_monitor.domain.data.reports.DependenciesReport
import it.rebirthproject.catalog_dependencies_monitor.domain.data.reports.DependencyReport
import it.rebirthproject.catalog_dependencies_monitor.domain.data.reports.DependencyReportState
import it.rebirthproject.catalog_dependencies_monitor.domain.services.factory.DependenciesRepositoryFactory
import it.rebirthproject.catalog_dependencies_monitor.domain.services.repositories.DependenciesRepository
import it.rebirthproject.versioncomparator.comparator.VersionComparator

@Slf4j
class DependenciesReportCalculator {

    private DependenciesRepositoryFactory dependenciesRepositoryFactory
    private final VersionComparator versionComparator

    DependenciesReportCalculator(VersionComparator versionComparator, DependenciesRepositoryFactory dependenciesRepositoryFactory) {
        this.versionComparator = versionComparator
        this.dependenciesRepositoryFactory = dependenciesRepositoryFactory
    }

    void populateReportWithCatalogDependency(DependenciesReport report, DependencyMetadata catalogDependency, List<String> excludedCatalogDependencies) {
        final DependencyReport reportElement = new DependencyReport()
        reportElement.dependencyInCatalog = catalogDependency

        final DependenciesRepository dependenciesRepository = dependenciesRepositoryFactory.create(report.dependenciesRepositoryType)
        reportElement.repositoryType = dependenciesRepository.getType()

        if (catalogDependency.dependencyId == null) {
            return
        }

        if (excludedCatalogDependencies.contains(catalogDependency.dependencyId)) {
            setReportElementState(reportElement, DependencyReportState.EXCLUDED)
            report.excluded.add(reportElement)
        } else {
            final Optional<DependencyMetadata> optRepoDependency = dependenciesRepository.getDependencies(catalogDependency)
            if (optRepoDependency.isPresent()) {
                final DependencyMetadata repoDependency = optRepoDependency.get()
                reportElement.dependencyInRepo = repoDependency
                log.info("gradle library version: {}", repoDependency.dependencyVersion)
                                
                String v1 = catalogDependency.dependencyVersion;
                String v2 = repoDependency.dependencyVersion;

                if (v1 == null || v2 == null) {
                    log.info("This catalog library has a null version or the library version is null in the repository. catalog='{}', repo='{}'", v1, v2);
                    setReportElementState(reportElement, DependencyReportState.SKIPPED);
                    report.skipped.add(reportElement);
                } else {
                    v1 = v1.trim();
                    v2 = v2.trim();
                    
                    try {
                        int compareResult = versionComparator.compare(v1, v2);
    
                        if (compareResult < 0) {
                            setReportElementState(reportElement, DependencyReportState.OUTDATED);
                            report.outdated.add(reportElement);
                        } else if (compareResult > 0) {
                            setReportElementState(reportElement, DependencyReportState.EXCEEDING);
                            report.exceeding.add(reportElement);
                        } else {
                            setReportElementState(reportElement, DependencyReportState.UPDATED);
                            report.updated.add(reportElement);
                        }
                    }
                    catch (IllegalArgumentException ex) {
                        log.warn("VersionComparator error", ex)
                        setReportElementState(reportElement, DependencyReportState.SKIPPED);
                        report.skipped.add(reportElement);
                    }
                }
            } else {
                log.info("No dependency found in {} with this name: {}", dependenciesRepository.getType().description, catalogDependency?.dependencyId)
                setReportElementState(reportElement, DependencyReportState.NOT_FOUND)
                report.notFound.add(reportElement)
            }
        }
        log.info("----------------------------------------------------------------")
    }

    private static void setReportElementState(DependencyReport reportElement, DependencyReportState reportState) {
        reportElement.reportState = reportState
        log.info(reportElement.toString())
    }
}
