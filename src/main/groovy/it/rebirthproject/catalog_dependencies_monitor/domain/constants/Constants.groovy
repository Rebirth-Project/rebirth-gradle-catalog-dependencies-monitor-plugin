package it.rebirthproject.catalog_dependencies_monitor.domain.constants

import it.rebirthproject.catalog_dependencies_monitor.domain.services.repositories.MavenRepositoryVersion
import it.rebirthproject.catalog_dependencies_monitor.gradle.plugins.CatalogDependenciesMonitorPlugin

class Constants {

    private Constants() {}

    public static final String PLUGIN_NAME = CatalogDependenciesMonitorPlugin.class.getSimpleName()
    public static final String PLUGIN_TASKS_GROUP = "catalog-monitor"
    public static final String MIN_GRADLE_VERSION = "7.0"
    public static final String DEFAULT_REPORT_FOLDER = "catalog-dependencies-monitor"
    public static final String DEFAULT_REPORT_FILE_NAME = "catalog_report"
    public static final String DEFAULT_CATALOG_TOML_FOLDER = "gradle"
    public static final String DEFAULT_CATALOG_TOML_FILE = "libs.versions.toml"
    public static final MavenRepositoryVersion DEFAULT_MAVEN_CENTRAL_REPOSITORY_VERSION = MavenRepositoryVersion.V2
    public static final String DEFAULT_MAVEN_CENTRAL_REPOSITORY_STRING_VERSION = DEFAULT_MAVEN_CENTRAL_REPOSITORY_VERSION.name()
}
