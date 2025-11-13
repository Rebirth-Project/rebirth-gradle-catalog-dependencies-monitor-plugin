/*
 * Copyright (C) 2025 Matteo Veroni Rebirth project
 * Modifications Copyright (C) 2025 Andrea Paternesi Rebirth project
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
package it.rebirthproject.catalog_dependencies_monitor.domain.services.http

import groovy.util.logging.Slf4j

import java.util.stream.Collectors

@Slf4j
class HttpClient {

    Optional<String> get(String url) {
        try {
            final HttpURLConnection connection = new URI(url).toURL().openConnection() as HttpURLConnection
            connection.setRequestMethod("GET")

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                final Reader reader = new BufferedReader(new InputStreamReader(connection.inputStream))
                final String response = reader.lines().collect(Collectors.joining("\n"))
                return Optional.of(response)
            } else {
                log.error("Error during the GET request: {} {} {}", connection.responseCode, connection.responseMessage, url)
            }
        } catch (Exception e) {
            log.error("exception: {}", e.getMessage())
        }
        return Optional.empty()
    }
}