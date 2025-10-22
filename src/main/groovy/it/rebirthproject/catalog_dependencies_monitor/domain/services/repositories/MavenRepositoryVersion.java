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
package it.rebirthproject.catalog_dependencies_monitor.domain.services.repositories;

import java.util.Optional;
import java.util.stream.Stream;

@Deprecated
public enum MavenRepositoryVersion {

    V1("version1"),
    V2("version2");

    private final String description;

    MavenRepositoryVersion(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static Optional<MavenRepositoryVersion> fromStringIgnoreCase(String version) {
        return Stream.of(values())
                .filter(enumVersion ->
                        enumVersion.getDescription().equalsIgnoreCase(version) || enumVersion.name().equalsIgnoreCase(version)
                )
                .findFirst();
    }
}
