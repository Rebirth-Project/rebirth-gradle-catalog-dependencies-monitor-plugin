package it.rebirthproject.catalog_dependencies_monitor.scripts

import groovy.util.logging.Slf4j

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.regex.Pattern

@Slf4j
class CopyrightUpdater {
    private static final int NEW_YEAR = 2026
    private static final String FILE_EXTENSION = ".groovy"
    private static final Pattern COPYRIGHT_PATTERN = ~"(?i)Copyright\\s*\\(C\\)\\s*\\d{4}"

    static void main(String[] args) {
        String dirPath = args ? args[0] : ".."
        String testDirPath = args ? args[1] : "../../../../test"
        updateCopyrights(Paths.get(dirPath))
        updateCopyrights(Paths.get(testDirPath))
    }

    private static void updateCopyrights(Path dir) {
        Files.walk(dir)
                .filter { path -> path.toString().endsWith(FILE_EXTENSION) }
                .forEach { file ->
                    try {
                        String content = Files.readString(file, StandardCharsets.UTF_8)
                        String updated = content.replaceAll(COPYRIGHT_PATTERN, "Copyright (C) " + NEW_YEAR)

                        if (content != updated) {
                            Files.writeString(file, updated, StandardCharsets.UTF_8)
                            log.info("✓ Updated: {}", file)
                        }
                    } catch (Exception e) {
                        log.error("✗ Error in {}: {}", file, e.message)
                    }
                }
    }
}