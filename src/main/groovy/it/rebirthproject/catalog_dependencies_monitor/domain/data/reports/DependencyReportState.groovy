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
package it.rebirthproject.catalog_dependencies_monitor.domain.data.reports

enum DependencyReportState {
    UPDATED("Catalog dependency version equal to Maven dependency version"),
    OUTDATED("Catalog dependency version is older than the newest Maven dependency version"),
    EXCEEDING("Catalog dependency version is newer than the newest Maven dependency version"),
    NOT_FOUND("Catalog dependency not found in Maven"),
    EXCLUDED("Catalog dependency excluded manually"),
    SKIPPED("Catalog dependency skipped because of null version (BOM case)")

    final String message

    DependencyReportState(String message) {
        this.message = message
    }
}