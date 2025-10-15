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
package it.rebirthproject.catalog_dependencies_monitor.domain.services.report

import it.rebirthproject.catalog_dependencies_monitor.domain.data.dependencies.DependencyMetadata
import it.rebirthproject.catalog_dependencies_monitor.domain.data.dependencies.LibraryMetadata
import it.rebirthproject.catalog_dependencies_monitor.domain.data.reports.DependenciesReport
import it.rebirthproject.catalog_dependencies_monitor.domain.data.reports.DependenciesReportType
import it.rebirthproject.catalog_dependencies_monitor.domain.data.reports.DependencyReport
import it.rebirthproject.catalog_dependencies_monitor.domain.data.reports.DependencyReportState
import it.rebirthproject.catalog_dependencies_monitor.domain.services.factory.DependenciesRepositoryFactory
import it.rebirthproject.catalog_dependencies_monitor.domain.services.repositories.DependenciesRepository
import it.rebirthproject.versioncomparator.comparator.VersionComparator
import it.rebirthproject.versioncomparator.comparator.VersionComparatorBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class DependenciesReportCalculatorTest {

    private static final DependencyMetadata EXISTING_LIBRARY_1 = new LibraryMetadata("org.slf4j", "slf4j-api", "2.0.12")
    private static final DependencyMetadata EXISTING_LIBRARY_1_MORE_UPDATED = new LibraryMetadata("org.slf4j", "slf4j-api", "2.0.13")
    private static final DependencyMetadata EXISTING_LIBRARY_1_WITH_NO_VERSION = new LibraryMetadata("org.slf4j", "slf4j-api", null)
    private static final DependencyMetadata EXISTING_LIBRARY_2 = new LibraryMetadata("org.mockito", "mockito-core", "5.11.0")
    private static final DependencyMetadata FAKE_LIBRARY = new LibraryMetadata("group", "artifact", "1")
    private static final DependencyMetadata EXISTING_LIBRARY_PROBLEMATIC = new LibraryMetadata("org.cache2k", "cache2k-api", "2.6.1.Final") 

    private DependenciesRepositoryFactory dependenciesRepositoryFactory
    private DependenciesRepository mavenCentralMock
    private DependenciesRepository gradlePortalsPluginMock
    private DependenciesReportCalculator reportCalculator
    private DependenciesReport dependenciesReport
    private VersionComparator versionComparator

    @BeforeEach
    void setUp() {
        versionComparator = new VersionComparatorBuilder().useMavenRulesVersionParser().build()
        mavenCentralMock = mock(DependenciesRepository.class)
        gradlePortalsPluginMock = mock(DependenciesRepository.class)
        dependenciesRepositoryFactory = new DependenciesRepositoryFactory(mavenCentralMock, gradlePortalsPluginMock)
        reportCalculator = new DependenciesReportCalculator(versionComparator, dependenciesRepositoryFactory)
        dependenciesReport = new DependenciesReport(DependenciesReportType.LIBRARIES_REPORT)
    }

    @Test
    void testReportCalculatorHandlesExcludedLibrary() {
        when(mavenCentralMock.getDependencies(EXISTING_LIBRARY_1)).thenReturn(Optional.of(EXISTING_LIBRARY_1))

        reportCalculator.populateReportWithCatalogDependency(dependenciesReport, EXISTING_LIBRARY_1, Collections.singletonList(EXISTING_LIBRARY_1.getDependencyId()))

        assertEquals(0, dependenciesReport.getUpdated().getCount())
        assertEquals(0, dependenciesReport.getOutdated().getCount())
        assertEquals(0, dependenciesReport.getExceeding().getCount())
        assertEquals(0, dependenciesReport.getNotFound().getCount())
        assertEquals(1, dependenciesReport.getExcluded().getCount())
        assertEquals(0, dependenciesReport.getSkipped().getCount())
        assertEquals(DependenciesReportType.LIBRARIES_REPORT, dependenciesReport.getReportType())

        DependencyReport excludedReport = dependenciesReport.getExcluded().getReports().get(0)
        assertEquals(mavenCentralMock.getType(), excludedReport.getRepositoryType())
        assertEquals(DependencyReportState.EXCLUDED, excludedReport.getReportState())
        assertEquals(EXISTING_LIBRARY_1, excludedReport.getDependencyInCatalog())
        assertNull(excludedReport.getDependencyInRepo())
    }

    @Test
    void testReportCalculatorHandlesOutdatedLibraries() {
        when(mavenCentralMock.getDependencies(EXISTING_LIBRARY_1)).thenReturn(Optional.of(EXISTING_LIBRARY_1_MORE_UPDATED))

        reportCalculator.populateReportWithCatalogDependency(dependenciesReport, EXISTING_LIBRARY_1, Collections.emptyList())

        assertEquals(0, dependenciesReport.getUpdated().getCount())
        assertEquals(1, dependenciesReport.getOutdated().getCount())
        assertEquals(0, dependenciesReport.getExceeding().getCount())
        assertEquals(0, dependenciesReport.getNotFound().getCount())
        assertEquals(0, dependenciesReport.getExcluded().getCount())
        assertEquals(0, dependenciesReport.getSkipped().getCount())
        assertEquals(DependenciesReportType.LIBRARIES_REPORT, dependenciesReport.getReportType())

        DependencyReport outdatedReport = dependenciesReport.getOutdated().getReports().get(0)
        assertEquals(mavenCentralMock.getType(), outdatedReport.getRepositoryType())
        assertEquals(DependencyReportState.OUTDATED, outdatedReport.getReportState())
        assertEquals(EXISTING_LIBRARY_1, outdatedReport.getDependencyInCatalog())
        assertEquals(EXISTING_LIBRARY_1_MORE_UPDATED, outdatedReport.getDependencyInRepo())
    }

    @Test
    void testReportCalculatorHandlesUpdatedLibraries() {
        when(mavenCentralMock.getDependencies(EXISTING_LIBRARY_1)).thenReturn(Optional.of(EXISTING_LIBRARY_1))
        when(mavenCentralMock.getDependencies(EXISTING_LIBRARY_2)).thenReturn(Optional.of(EXISTING_LIBRARY_2))

        reportCalculator.populateReportWithCatalogDependency(dependenciesReport, EXISTING_LIBRARY_1, Collections.emptyList())
        reportCalculator.populateReportWithCatalogDependency(dependenciesReport, EXISTING_LIBRARY_2, Collections.emptyList())

        assertEquals(2, dependenciesReport.getUpdated().getCount())
        assertEquals(0, dependenciesReport.getOutdated().getCount())
        assertEquals(0, dependenciesReport.getExceeding().getCount())
        assertEquals(0, dependenciesReport.getNotFound().getCount())
        assertEquals(0, dependenciesReport.getExcluded().getCount())
        assertEquals(0, dependenciesReport.getSkipped().getCount())

        dependenciesReport.getUpdated().getReports().forEach(updatedReport -> {
                assertEquals(mavenCentralMock.getType(), updatedReport.getRepositoryType())
                assertEquals(DependencyReportState.UPDATED, updatedReport.getReportState())
                assertTrue(updatedReport.getDependencyInCatalog().equals(EXISTING_LIBRARY_1) || updatedReport.getDependencyInCatalog().equals(EXISTING_LIBRARY_2))
                assertTrue(updatedReport.getDependencyInRepo().equals(EXISTING_LIBRARY_1) || updatedReport.getDependencyInRepo().equals(EXISTING_LIBRARY_2))
            })
    }

    @Test
    void testReportCalculatorHandlesNotFoundLibraries() {
        when(mavenCentralMock.getDependencies(FAKE_LIBRARY)).thenReturn(Optional.empty())

        reportCalculator.populateReportWithCatalogDependency(dependenciesReport, FAKE_LIBRARY, Collections.emptyList())

        assertEquals(0, dependenciesReport.getUpdated().getCount())
        assertEquals(0, dependenciesReport.getOutdated().getCount())
        assertEquals(0, dependenciesReport.getExceeding().getCount())
        assertEquals(1, dependenciesReport.getNotFound().getCount())
        assertEquals(0, dependenciesReport.getExcluded().getCount())
        assertEquals(0, dependenciesReport.getSkipped().getCount())

        DependencyReport notFoundReport = dependenciesReport.getNotFound().getReports().get(0)
        assertEquals(mavenCentralMock.getType(), notFoundReport.getRepositoryType())
        assertEquals(DependencyReportState.NOT_FOUND, notFoundReport.getReportState())
        assertEquals(FAKE_LIBRARY, notFoundReport.getDependencyInCatalog())
        assertNull(notFoundReport.getDependencyInRepo())
    }

    @Test
    void testReportCalculatorHandlesSkippedLibraries() {
        when(mavenCentralMock.getDependencies(EXISTING_LIBRARY_1_WITH_NO_VERSION)).thenReturn(Optional.of(EXISTING_LIBRARY_1))

        reportCalculator.populateReportWithCatalogDependency(dependenciesReport, EXISTING_LIBRARY_1_WITH_NO_VERSION, Collections.emptyList())

        assertEquals(0, dependenciesReport.getUpdated().getCount())
        assertEquals(0, dependenciesReport.getOutdated().getCount())
        assertEquals(0, dependenciesReport.getExceeding().getCount())
        assertEquals(0, dependenciesReport.getNotFound().getCount())
        assertEquals(0, dependenciesReport.getExcluded().getCount())
        assertEquals(1, dependenciesReport.getSkipped().getCount())

        DependencyReport skippedReport = dependenciesReport.getSkipped().getReports().get(0)
        assertEquals(mavenCentralMock.getType(), skippedReport.getRepositoryType())
        assertEquals(DependencyReportState.SKIPPED, skippedReport.getReportState())
        assertEquals(EXISTING_LIBRARY_1_WITH_NO_VERSION, skippedReport.getDependencyInCatalog())
        assertEquals(EXISTING_LIBRARY_1, skippedReport.getDependencyInRepo())
    }

    @Test
    void testReportCalculatorHandlesExceedingLibraries() {
        when(mavenCentralMock.getDependencies(EXISTING_LIBRARY_1_MORE_UPDATED)).thenReturn(Optional.of(EXISTING_LIBRARY_1))

        reportCalculator.populateReportWithCatalogDependency(dependenciesReport, EXISTING_LIBRARY_1_MORE_UPDATED, Collections.emptyList())

        assertEquals(0, dependenciesReport.getUpdated().getCount())
        assertEquals(0, dependenciesReport.getOutdated().getCount())
        assertEquals(1, dependenciesReport.getExceeding().getCount())
        assertEquals(0, dependenciesReport.getNotFound().getCount())
        assertEquals(0, dependenciesReport.getExcluded().getCount())
        assertEquals(0, dependenciesReport.getSkipped().getCount())

        DependencyReport exceedingReport = dependenciesReport.getExceeding().getReports().get(0)
        assertEquals(mavenCentralMock.getType(), exceedingReport.getRepositoryType())
        assertEquals(DependencyReportState.EXCEEDING, exceedingReport.getReportState())
        assertEquals(EXISTING_LIBRARY_1_MORE_UPDATED, exceedingReport.getDependencyInCatalog())
        assertEquals(EXISTING_LIBRARY_1, exceedingReport.getDependencyInRepo())
    }
    
    @Test
    void testProblematicLibrary() {
        when(mavenCentralMock.getDependencies(EXISTING_LIBRARY_PROBLEMATIC)).thenReturn(Optional.of(EXISTING_LIBRARY_PROBLEMATIC))

        reportCalculator.populateReportWithCatalogDependency(dependenciesReport, EXISTING_LIBRARY_PROBLEMATIC, Collections.emptyList())

        assertEquals(1, dependenciesReport.getUpdated().getCount())
        assertEquals(0, dependenciesReport.getOutdated().getCount())
        assertEquals(0, dependenciesReport.getExceeding().getCount())
        assertEquals(0, dependenciesReport.getNotFound().getCount())
        assertEquals(0, dependenciesReport.getExcluded().getCount())
        assertEquals(0, dependenciesReport.getSkipped().getCount())

        DependencyReport updatedReport = dependenciesReport.getUpdated().getReports().get(0)
        assertEquals(mavenCentralMock.getType(), updatedReport.getRepositoryType())
        assertEquals(DependencyReportState.UPDATED, updatedReport.getReportState())
        assertTrue(updatedReport.getDependencyInCatalog().equals(EXISTING_LIBRARY_PROBLEMATIC))
        assertTrue(updatedReport.getDependencyInRepo().equals(EXISTING_LIBRARY_PROBLEMATIC))
    }
}