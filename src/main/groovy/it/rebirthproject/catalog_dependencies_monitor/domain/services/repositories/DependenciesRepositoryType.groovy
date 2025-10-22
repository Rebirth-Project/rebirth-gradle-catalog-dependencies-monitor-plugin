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

import java.util.stream.Stream

enum DependenciesRepositoryType {

    // The MAVEN_CENTRAL_V1 is deprecated and will be removed in future versions
    MAVEN_CENTRAL_V1("Maven Central", "Maven Central", "https://mvnrepository.com","https://search.maven.org","Maven Central Libraries Report", "libraries"),
    MAVEN_CENTRAL_V2("Maven Central Sonatype", "Maven Central Sonatype", "https://central.sonatype.com/","https://repo.maven.apache.org","Maven Central Sonatype Libraries Report", "libraries"),
    GRADLE_PLUGINS_PORTAL("Gradle", "Gradle Plugins Portal", "https://plugins.gradle.org","https://plugins.gradle.org","Gradle Plugins Report", "plugins")
    
    final String title
    final String description
    final String website
    final String apiUrl
    final String reportDescription
    final String tomlDescription

    DependenciesRepositoryType(String title, String description, String website, String apiUrl, String reportDescription, String tomlDescription) {
        this.title = title
        this.description = description
        this.website = website
        this.apiUrl = apiUrl
        this.reportDescription = reportDescription
        this.tomlDescription = tomlDescription
    }
        
    static DependenciesRepositoryType getMavenRepositoryTypeFromString(String mavenRepositoryType) {
        return Stream.of(values())
        .filter(enumVersion -> enumVersion!= GRADLE_PLUGINS_PORTAL)
        .filter(enumVersion -> enumVersion.name().equalsIgnoreCase(mavenRepositoryType))
        .findFirst()
        .orElse(MAVEN_CENTRAL_V2);
    }
}