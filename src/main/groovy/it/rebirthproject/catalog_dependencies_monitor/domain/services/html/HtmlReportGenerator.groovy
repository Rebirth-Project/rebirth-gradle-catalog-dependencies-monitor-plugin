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
package it.rebirthproject.catalog_dependencies_monitor.domain.services.html

import groovy.util.logging.Slf4j
import it.rebirthproject.catalog_dependencies_monitor.domain.data.reports.DependenciesReport
import it.rebirthproject.catalog_dependencies_monitor.domain.services.factory.DependenciesRepositoryFactory
import it.rebirthproject.catalog_dependencies_monitor.domain.services.repositories.DependenciesRepository
import it.rebirthproject.catalog_dependencies_monitor.domain.services.repositories.DependenciesRepositoryType

@Slf4j
class HtmlReportGenerator {

    private final DependenciesRepositoryFactory dependenciesRepositoryFactory

    HtmlReportGenerator(DependenciesRepositoryFactory dependenciesRepositoryFactory) {
        this.dependenciesRepositoryFactory = dependenciesRepositoryFactory
    }

    String generateReport(DependenciesReport librariesReport, DependenciesReport pluginsReport) {
        String htmlContent = """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Report Dependencies</title>
            <link rel="stylesheet" href="css/styles.css">
        </head>
        <body>
        """

        htmlContent += """
        <nav class='navbar'>
            <section>
                <div><a href="#lib_report">Libraries Report</a></div>
                <div><a href="#plugins_report">Plugins Report</a></div>
            </section>
        </nav>
        <div class='container'>
            <p id="lib_report"></p>
            <br/>
            <br/>
            <br/>
        """
        htmlContent += "<h1>${librariesReport?.dependenciesRepositoryType?.description}</h1>"
        htmlContent += "<p>last update: ${librariesReport?.dateAndTime}</p>"
        htmlContent += getHtmlDependencyTable("Libraries", librariesReport)

        htmlContent += """
            <p id="plugins_report"></p>
            <br/>
            <br/>
            <br/>
        """
        htmlContent += "<h1>${pluginsReport?.dependenciesRepositoryType?.description}</h1>"
        htmlContent += "<p>last update: ${pluginsReport?.dateAndTime}</p>"
        htmlContent += getHtmlDependencyTable("Plugins", pluginsReport)
        htmlContent += getFooter()
        htmlContent += "</div>"

        htmlContent += """
            </body>
        </html>
        """

        return htmlContent
    }

    private String getHtmlDependencyTable(String title, DependenciesReport dependenciesReport) {
        String htmlContent = ""
        dependenciesReport.reports.eachWithIndex { report, reportIndex ->
            {
                reportIndex += 1
                log.debug("{} {} {} {}", reportIndex, report?.name, report?.description, report?.count)
                final Integer reportElements = report?.count
                final Boolean emptyReport = (reportElements == 0)
                final String tabId = "${dependenciesReport?.dependenciesRepositoryType?.name()}_${reportIndex}"
                final String accordionLabel = report?.description?.replaceAll("Dependencies", title)
                final String accordionChecked = (emptyReport) ? "" : "checked"

                htmlContent += """
                <div class="accordion">
                    <input type="checkbox" id="%s" %s />
                    <label class="accordion-label" for="%s"><h3>%s (%s)</h3></label>
                    <div class="accordion-content">        
                """.formatted(
                        tabId, accordionChecked,
                        tabId, accordionLabel, reportElements
                )

                if (emptyReport) {
                    htmlContent += """
                        </div>
                    </div>    
                    """
                    return
                }

                htmlContent += """        
                        <table>
                            <tr>
                                <th>#</th>
                                <th>Catalog %s</th>
                                <th>Repository</th>
                                <th>Repository %s</th>
                                <th>State</th>
                            </tr>
                """.formatted(title, title)

                report.reports.eachWithIndex { depReport, depReportIndex ->
                    {                        
                        final DependenciesRepository repository = dependenciesRepositoryFactory.create(dependenciesReport.dependenciesRepositoryType)
                        final String dependencyUrlInRepo = repository.getHttpUrlOfDependencyInRepository(depReport?.repositoryType?.website, depReport?.dependencyInRepo)                        
                        final String catalogDependencyIdAndVersion = concatDependencyIdAndVersion(depReport?.dependencyInCatalog?.dependencyId, depReport?.dependencyInCatalog?.dependencyVersion)
                        log.debug("catalogDependencyIdAndVersion: {}", catalogDependencyIdAndVersion)
                        final String repositoryDependencyIdAndVersion = concatDependencyIdAndVersion(depReport?.dependencyInRepo?.dependencyId, depReport?.dependencyInRepo?.dependencyVersion)
                        log.debug("repositoryDependencyIdAndVersion: {}", repositoryDependencyIdAndVersion)
                        log.debug("dependency state: {}", depReport?.reportState)
                        htmlContent += """
                            <tr>
                                <td class="%s-light">%s</td>
                                <td class="%s-light">%s</td>
                                <td class="%s-light"><a href="%s" target="_blank">%s</a></td>
                                <td class="%s-light">%s</td>
                                <td class="%s">%s</td>
                            <tr>
                            """.formatted(
                                report?.name, ++depReportIndex,
                                report?.name, catalogDependencyIdAndVersion,
                                report?.name, dependencyUrlInRepo, depReport?.repositoryType?.description,
                                report?.name, repositoryDependencyIdAndVersion,
                                report?.name, depReport?.reportState
                        )
                    }
                }

                htmlContent += """
                        </table>
                    </div>
                </div>      
                """
            }
        }
        return htmlContent
    }

    private static String getFooter() {
        return """
        <footer>
            <div class="footer">
                <p>&copy; ${Calendar.getInstance().get(Calendar.YEAR)} Rebirth-Project. All rights reserved.</p>
            </div>
        </footer>
        """
    }

    private static String concatDependencyIdAndVersion(String dependencyId, String dependencyVersion) {
        if (!isNullOrBlank(dependencyId) && !isNullOrBlank(dependencyVersion)) {
            return dependencyId + ":" + dependencyVersion
        } else if (!isNullOrBlank(dependencyId)) {
            return dependencyId
        } else if (!isNullOrBlank(dependencyVersion)) {
            return dependencyVersion
        } else {
            return ""
        }
    }

    private static boolean isNullOrBlank(String str) {
        return str == null || str.isBlank()
    }
}
