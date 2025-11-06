package it.rebirthproject.catalog_dependencies_monitor.domain.services.mappers

import it.rebirthproject.catalog_dependencies_monitor.domain.data.dependencies.DependencyMetadata
import it.rebirthproject.versioncomparator.comparator.VersionComparator
import it.rebirthproject.versioncomparator.comparator.VersionComparatorBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

import java.util.stream.Collectors

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

class MavenV2DependencyResponseMapperTest {

    private static final String JUNIT_XML_FILE_RESPONSE = "junit-maven-response.xml"
    private static final String SLF4J_XML_FILE_RESPONSE = "slf4j-maven-response.xml"
    private static final String JUNIT_ID = "org.junit:junit-bom"
    private static final String SLF4J_ID = "org.slf4j:slf4j-api"

    private VersionComparator versionComparator

    @BeforeEach
    void setUp() {
        versionComparator = new VersionComparatorBuilder().useMavenRulesVersionParser().build()
    }

    @ParameterizedTest
    @CsvSource([
            "'junit-maven-response.xml', 'org.junit:junit-bom', '6.0.1'",
            "'slf4j-maven-response.xml', 'org.slf4j:slf4j-api', '2.1.0-alpha1'",
    ])
    void find_release_version_when_no_filter_set(String xmlFileResponse, String expectedId, String expectedVersion) throws IOException {
        final mapper = new MavenV2DependencyResponseMapper(versionComparator, new ArrayList<>())

        Optional<DependencyMetadata> mavenDependencyMetadata = mapper.map(readFileResourceContent(xmlFileResponse))

        assertTrue(mavenDependencyMetadata.isPresent())
        assertEquals(expectedId, mavenDependencyMetadata.get().dependencyId)
        assertEquals(expectedVersion, mavenDependencyMetadata.get().dependencyVersion)
    }

    @Test
    void find_release_version_when_filters_not_match() {
        final List<String> versionFilters = Arrays.asList("alpha", "beta")
        final mapper = new MavenV2DependencyResponseMapper(versionComparator, versionFilters)

        Optional<DependencyMetadata> mavenDependencyMetadata = mapper.map(readFileResourceContent(JUNIT_XML_FILE_RESPONSE))

        assertTrue(mavenDependencyMetadata.isPresent())
        assertEquals(JUNIT_ID, mavenDependencyMetadata.get().dependencyId)
        assertEquals("6.0.1", mavenDependencyMetadata.get().dependencyVersion)
    }

    @Test
    void find_most_recent_version_when_filters_not_match_for_case() {
        final List<String> versionFilters = Arrays.asList("ALPHA", "BETA")
        final mapper = new MavenV2DependencyResponseMapper(versionComparator, versionFilters)

        Optional<DependencyMetadata> mavenDependencyMetadata = mapper.map(readFileResourceContent(SLF4J_XML_FILE_RESPONSE))

        assertTrue(mavenDependencyMetadata.isPresent())
        assertEquals(SLF4J_ID, mavenDependencyMetadata.get().dependencyId)
        assertEquals("2.1.0-alpha1", mavenDependencyMetadata.get().dependencyVersion)
    }

    @Test
    void find_most_recent_version_when_filters_match() {
        final List<String> versionFilters = Arrays.asList("alpha", "beta")
        final mapper = new MavenV2DependencyResponseMapper(versionComparator, versionFilters)

        Optional<DependencyMetadata> mavenDependencyMetadata = mapper.map(readFileResourceContent(SLF4J_XML_FILE_RESPONSE))

        assertTrue(mavenDependencyMetadata.isPresent())
        assertEquals(SLF4J_ID, mavenDependencyMetadata.get().dependencyId)
        assertEquals("2.0.17", mavenDependencyMetadata.get().dependencyVersion)
    }

    private String readFileResourceContent(String fileName) throws IOException {
        try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (resourceAsStream == null) {
                throw new IllegalArgumentException("File not found: " + fileName)
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream))) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()))
            }
        }
    }
}