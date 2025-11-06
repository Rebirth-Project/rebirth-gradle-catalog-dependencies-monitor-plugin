package it.rebirthproject.catalog_dependencies_monitor.domain.services.mappers

import it.rebirthproject.catalog_dependencies_monitor.domain.data.dependencies.DependencyMetadata
import it.rebirthproject.versioncomparator.comparator.VersionComparator
import it.rebirthproject.versioncomparator.comparator.VersionComparatorBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource

import java.util.stream.Collectors
import java.util.stream.Stream

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

class GradlePortalsPluginsResponseMapperTest {

    private static final String XML_RESPONSE_FOLDER = "gradle"
    private static final String BERYX_XML_FILE_RESPONSE = "beryx-gradle-response.xml"
    private static final String SPRINGBOOT_XML_FILE_RESPONSE = "springboot-gradle-response.xml"
    private static final String BERYX_ID = "org.beryx.runtime"
    private static final String SPRINGBOOT_ID = "org.springframework.boot"

    private VersionComparator versionComparator

    @BeforeEach
    void setUp() {
        versionComparator = new VersionComparatorBuilder().useMavenRulesVersionParser().build()
    }

    @ParameterizedTest
    @CsvSource([
            "'beryx-gradle-response.xml', 'org.beryx.runtime', '2.0.1'",
            "'springboot-gradle-response.xml', 'org.springframework.boot', '4.0.0-RC1'",
    ])
    void find_release_version_when_no_filter_set(String xmlFileResponse, String expectedId, String expectedVersion) throws IOException {
        final def mapper = new GradlePortalsPluginsResponseMapper(versionComparator, new ArrayList<>())

        Optional<DependencyMetadata> gradleDependencyMetadata = mapper.map(readFileResourceContent(xmlFileResponse))

        assertTrue(gradleDependencyMetadata.isPresent())
        assertEquals(expectedId, gradleDependencyMetadata.get().dependencyId)
        assertEquals(expectedVersion, gradleDependencyMetadata.get().dependencyVersion)
    }

    @Test
    void find_release_version_when_filters_not_match() {
        final List<String> versionFilters = Arrays.asList("alpha", "beta")
        final def mapper = new GradlePortalsPluginsResponseMapper(versionComparator, versionFilters)

        Optional<DependencyMetadata> gradleDependencyMetadata = mapper.map(readFileResourceContent(BERYX_XML_FILE_RESPONSE))

        assertTrue(gradleDependencyMetadata.isPresent())
        assertEquals(BERYX_ID, gradleDependencyMetadata.get().dependencyId)
        assertEquals("2.0.1", gradleDependencyMetadata.get().dependencyVersion)
    }

    @ParameterizedTest
    @MethodSource("versionFiltersNotMatchingForSpringBoot")
    void find_most_recent_version_when_filters_not_match(List<String> versionFilters) {
        final def mapper = new GradlePortalsPluginsResponseMapper(versionComparator, versionFilters)

        Optional<DependencyMetadata> gradleDependencyMetadata = mapper.map(readFileResourceContent(SPRINGBOOT_XML_FILE_RESPONSE))

        assertTrue(gradleDependencyMetadata.isPresent())
        assertEquals(SPRINGBOOT_ID, gradleDependencyMetadata.get().dependencyId)
        assertEquals("4.0.0-RC1", gradleDependencyMetadata.get().dependencyVersion)
    }

    @ParameterizedTest
    @MethodSource("versionFiltersMatchingForSpringBoot")
    void find_most_recent_version_when_filters_match(List<String> versionFilters) {
        final def mapper = new GradlePortalsPluginsResponseMapper(versionComparator, versionFilters)

        Optional<DependencyMetadata> gradleDependencyMetadata = mapper.map(readFileResourceContent(SPRINGBOOT_XML_FILE_RESPONSE))

        assertTrue(gradleDependencyMetadata.isPresent())
        assertEquals(SPRINGBOOT_ID, gradleDependencyMetadata.get().dependencyId)
        assertEquals("3.5.7", gradleDependencyMetadata.get().dependencyVersion)
    }

    private String readFileResourceContent(String fileName) throws IOException {
        try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(XML_RESPONSE_FOLDER + "/" + fileName)) {
            if (resourceAsStream == null) {
                throw new IllegalArgumentException("File not found: " + fileName)
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream))) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()))
            }
        }
    }

    private static Stream<Arguments> versionFiltersNotMatchingForSpringBoot() {
        return Stream.of(
                Arguments.of(["m", "rc"]),
                Arguments.of(["rc", "m"]),
                Arguments.of(["rc1", "m1", "m2", "m3"]),
                Arguments.of(["m1", "m2", "m3", "rc1"]),
                Arguments.of(["ALPHA", "m", "rc"]),
                Arguments.of(["ALPHA", "m", "BETA", "rc"])
        )
    }

    private static Stream<Arguments> versionFiltersMatchingForSpringBoot() {
        // The only real filters matching are M1, M2, M3 and RC1
        return Stream.of(
                Arguments.of(["M", "RC"]),
                Arguments.of(["RC", "M"]),
                Arguments.of(["M1", "M2", "M3", "RC1"]),
                Arguments.of(["RC1", "M1", "M2", "M3"]),
                Arguments.of(["ALPHA", "M", "RC"]),
                Arguments.of(["ALPHA", "M", "BETA", "RC"])
        )
    }
}