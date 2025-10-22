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

package it.rebirthproject.catalog_dependencies_monitor.domain.services.repositories

import groovy.json.JsonSlurper
import it.rebirthproject.catalog_dependencies_monitor.domain.data.dependencies.DependencyMetadata
import it.rebirthproject.catalog_dependencies_monitor.domain.data.dependencies.LibraryMetadata
import it.rebirthproject.catalog_dependencies_monitor.domain.data.dependencies.PluginMetadata
import it.rebirthproject.catalog_dependencies_monitor.domain.services.http.HttpClient
import it.rebirthproject.catalog_dependencies_monitor.domain.services.mappers.GradlePortalsPluginsResponseMapper
import it.rebirthproject.catalog_dependencies_monitor.domain.services.mappers.MavenV1DependencyResponseMapper
import it.rebirthproject.catalog_dependencies_monitor.domain.services.mappers.MavenV2DependencyResponseMapper
import it.rebirthproject.versioncomparator.comparator.VersionComparator
import it.rebirthproject.versioncomparator.comparator.VersionComparatorBuilder
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

import java.util.stream.Stream

import static org.junit.jupiter.api.Assertions.*

class DependenciesRepositoryTest {

    private static final DependencyMetadata EXISTING_LIBRARY = new LibraryMetadata("org.slf4j", "slf4j-api", "2.0.12")
    private static final DependencyMetadata EXISTING_PLUGIN = new PluginMetadata("net.researchgate.release", "3.0.2")
    private static final DependencyMetadata FAKE_LIBRARY = new LibraryMetadata("@@@@xyzFaKePlUgIn!1!1!1!@@@@", "@@@@xyzFaKePlUgIn!1!1!1!@@@@", "0.0.0")
    private static final DependencyMetadata FAKE_PLUGIN = new PluginMetadata("@@@@xyzFaKePlUgIn!1!1!1!@@@@", "0.0.0")

    private static MavenRepository mavenV1Repository
    private static MavenRepository mavenV2Repository
    private static DependenciesRepository gradleRepository
    private static VersionComparator versionComparator

    @BeforeAll
    static void setUp() {
        versionComparator = new VersionComparatorBuilder().useMavenRulesVersionParser().build()
        final JsonSlurper jsonSlurper = new JsonSlurper()
        final HttpClient httpClient = new HttpClient()
        final MavenV1DependencyResponseMapper mavenV1DependencyResponseMapper = new MavenV1DependencyResponseMapper(versionComparator, jsonSlurper, new ArrayList<>())
        final MavenV2DependencyResponseMapper mavenV2DependencyResponseMapper = new MavenV2DependencyResponseMapper(versionComparator, new ArrayList<>())
        final GradlePortalsPluginsResponseMapper gradlePortalsPluginsResponseMapper = new GradlePortalsPluginsResponseMapper()
        mavenV1Repository = new MavenV1Repository(httpClient, mavenV1DependencyResponseMapper)
        mavenV2Repository = new MavenV2Repository(httpClient, mavenV2DependencyResponseMapper)
        gradleRepository = new GradlePortalRepository(httpClient, gradlePortalsPluginsResponseMapper)
    }

    @ParameterizedTest
    @MethodSource("getMavenRepositoryInstance")
    void testMavenRepositoryFindExistingLibrary(MavenRepository mavenRepositoryInstance) {
        Optional<DependencyMetadata> response = mavenRepositoryInstance.getDependencies(EXISTING_LIBRARY)

        assertTrue(response.isPresent())
        DependencyMetadata foundMavenLibrary = response.get()
        assertEquals(EXISTING_LIBRARY.getDependencyId(), foundMavenLibrary.getDependencyId())
        assertTrue(versionComparator.compare(foundMavenLibrary.getDependencyVersion(), EXISTING_LIBRARY.getDependencyVersion()) >= 0)
    }

    @ParameterizedTest
    @MethodSource("getMavenRepositoryInstance")
    void testMavenRepositoryDoesNotFindFakeLibrary(DependenciesRepository mavenRepositoryInstance) {
        Optional<DependencyMetadata> response = mavenRepositoryInstance.getDependencies(FAKE_LIBRARY)
        assertFalse(response.isPresent())
    }

    @Test
    void testGradleRepositoryFindExistingPlugin() {
        Optional<DependencyMetadata> response = gradleRepository.getDependencies(EXISTING_PLUGIN)

        assertTrue(response.isPresent())
        DependencyMetadata foundGradlePlugin = response.get()
        assertEquals(EXISTING_PLUGIN.getDependencyId(), foundGradlePlugin.getDependencyId())
        assertTrue(versionComparator.compare(foundGradlePlugin.getDependencyVersion(), EXISTING_PLUGIN.getDependencyVersion()) >= 0)
    }

    @Test
    void testGradleRepositoryDoesNotFindFakePlugin() {
        Optional<DependencyMetadata> response = gradleRepository.getDependencies(FAKE_PLUGIN)

        assertFalse(response.isPresent())
    }

    private static Stream<MavenRepository> getMavenRepositoryInstance() {
        Stream.of(
                mavenV1Repository,
                mavenV2Repository
        )
    }
}
