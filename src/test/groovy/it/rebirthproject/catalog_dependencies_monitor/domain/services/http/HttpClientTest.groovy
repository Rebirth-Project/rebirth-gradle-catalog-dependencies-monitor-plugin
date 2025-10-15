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
package it.rebirthproject.catalog_dependencies_monitor.domain.services.http

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertFalse
import static org.junit.jupiter.api.Assertions.assertTrue

class HttpClientTest {

    private static final String VALID_HTTP_URL_RETURNING_HTML = "https://www.google.com"
    private static final String VALID_HTTP_URL_RETURNING_JSON = "https://search.maven.org/solrsearch/select?q=g:ch.qos.logback+AND+a:logback-classic&core=gav&rows=1&wt=json"
    private static final String INVALID_HTTP_URL = "invalid url"

    private HttpClient httpClient

    @BeforeEach
    void setUp() {
        httpClient = new HttpClient()
    }

    @Test
    void validHttpUrlReturnsAValidHtmlResponse() {
        Optional<String> httpResponse = httpClient.get(VALID_HTTP_URL_RETURNING_HTML)

        assertTrue(httpResponse.isPresent(), "Response should be present")
        String response = httpResponse.get()
        assertTrue(response.length() > 0, "Response should not be empty")
        assertTrue(response.contains("<html"), "Response should contain <html>")
        assertTrue(response.contains("</html"), "Response should contain </html>")
    }

    @Test
    void validHttpUrlReturnsAValidJsonResponse() {
        Optional<String> httpResponse = httpClient.get(VALID_HTTP_URL_RETURNING_JSON)

        assertTrue(httpResponse.isPresent(), "Response should be present")
        String response = httpResponse.get()
        assertTrue(response.length() > 0, "Response should not be empty")
        assertTrue(response.startsWith("{") || response.startsWith("["), "Response should start with '{' or '['")
        assertTrue(response.endsWith("}") || response.endsWith("]"), "Response should end with '}' or ']'")
    }

    @Test
    void httpClientReturnsEmptyResponseWhenCallingNonExistingURL() {
        Optional<String> httpResponse = httpClient.get(INVALID_HTTP_URL)

        assertFalse(httpResponse.isPresent(), "Response should be empty for invalid URL")
    }
}