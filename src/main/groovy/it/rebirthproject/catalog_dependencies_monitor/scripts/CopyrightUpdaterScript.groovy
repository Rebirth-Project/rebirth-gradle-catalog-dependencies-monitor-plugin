package it.rebirthproject.catalog_dependencies_monitor.scripts

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.regex.Pattern

final class CopyrightUpdaterScript {
    private static final int NEW_YEAR = 2025
    private static final String FILE_EXTENSION = ".groovy"
    private static final Pattern COPYRIGHT_PATTERN = ~"(?i)Copyright\\s*\\(C\\)\\s*\\d{4}"

    private CopyrightUpdaterScript() {}

    static void main(String[] args) {
        String dirPath = args ? args[0] : "."
        Path path = Paths.get(dirPath)
        System.out.println("Searching for copyright years to update in folder: " + path.toAbsolutePath())
        updateCopyrights(Paths.get(dirPath))
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
                            System.out.println("✓ Updated: " + file)
                        }
                    } catch (Exception e) {
                        System.err.println("✗ Error in " + file + ": " + e.message)
                    }
                }
    }
}