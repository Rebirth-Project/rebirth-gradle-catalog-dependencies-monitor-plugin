import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.regex.Pattern

/**
 * Script to automatically update all the copyright years and latest version in README.md, instead of doing it manually.
 * 
 * Usage: groovy CopyrightUpdaterScript.groovy rootPathOfTheGradleProject
 * Other usage: java -jar GrovyJarPath CopyrightUpdaterScript.groovy rootPathOfTheGradleProject
 */
final class CopyrightUpdaterScript {
    private static final String NEW_YEAR = "2025"
    private static String NEW_VERSION = "" 
    private static final Pattern COPYRIGHT_PATTERN = ~/(?i)Copyright\s*\(C\)\s*\d{4}(?:\/\d{4})?/
    private static final Pattern LATEST_VERSION_PATTERN = ~/(?i)Latest\s+Version\s+(\d+(?:\.\d+){1,3})/
    private static final Pattern PLUGINS_VERSION_PATTERN = ~/\)\s*version\s+"(\d+(?:\.\d+){1,3})"/

    private static final List<String> excludedDirs = ["build", "gradle"]

    private CopyrightUpdaterScript() {}

    static void main(String[] args) {
        String dirPath = args ? args[0] : "."
        Path path = Paths.get(dirPath)
        System.out.println("Searching for copyright years to update in folder: " + path.toAbsolutePath())
        
        NEW_VERSION = readProjectVersion(path)
        updateCopyrights(path)
        updateVersion(path)
    }

    private static String readProjectVersion(Path dir) {
        Path props = dir.resolve("gradle.properties")   
        if (!Files.exists(props)) {
            throw new FileNotFoundException("gradle.properties non trovato in: $props")
        }
        def line = Files.readAllLines(props)
        .find { it.trim().startsWith("version=") }
        return line?.split("=", 2)[1]?.trim() ?: "UNKNOWN VERSION"
    }
    
    private static void updateCopyrights(Path dir) {
        Files.walk(dir)
        .filter { path ->
            def parts = path.normalize().toString().split(Pattern.quote(File.separator))
            if (parts.any { it.startsWith(".") }) {
                return false
            }

            if (excludedDirs.any { excludedDir -> path.toString().contains(File.separator + excludedDir + File.separator) }) {
                return false
            }

            return Files.isRegularFile(path)
        }
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

    private static void updateVersion(Path dir) {
        Path file = dir.resolve("README.md")

        String content = Files.readString(file, StandardCharsets.UTF_8)
        String updated = content.replaceAll(LATEST_VERSION_PATTERN, "Latest Version " + NEW_VERSION)
        if (content != updated) {            
            System.out.println("✓ Updated first version: " + file)
        }
        String secondUpdated = updated.replaceAll(PLUGINS_VERSION_PATTERN, ") version \"" + NEW_VERSION + "\"")
        if (updated != secondUpdated) {
            Files.writeString(file, secondUpdated, StandardCharsets.UTF_8)
            System.out.println("✓ Updated second version: " + file)
        }        
    }
}