# Gradle Catalog Dependencies Monitor Plugin

Gradle plugin for monitoring Gradle catalog dependencies. It generates reports to inform you about which dependencies in
your catalog are up to date or need updating.

**```Latest Version 1.0.72```**

![Build Status](https://github.com/Rebirth-Project/rebirth-gradle-catalog-dependencies-monitor-plugin/actions/workflows/build.yml/badge.svg?raw=true)

## Requirements

- Minimum Gradle version: 8.4

## Main features

* Checks all the defined dependencies in a standard Gradle catalog
* Auto-update old catalog dependencies

## Goals

* Provide a simple-to-use plugin to speed up and simplify dependencies' version check and management

## How to import the plugin in your Gradle project

```
plugins {
    id("it.rebirth-project.catalog-dependencies-monitor") version "1.0.72"
}
```

## Introduction

The Gradle Catalog Dependencies Monitor Plugin is a plugin designed to simplify the management and monitoring of dependencies defined in a
standard Gradle catalog project.
In practice, instead of using similar plugins in every project or library, everything is centralized by moving the checks to the catalog
side.
By default, this plugin connects to the **Maven Central repository** and the **Gradle Plugin repository** to retrieve dependency metadata
and verify whether newer versions are available.
It produces a detailed report in **HTML** or a **JSON** format, showing the results of this lookup and comparison process.
The report is divided into two parts: the libraries' report and the plugins' report. Each report will visualize the status of the relative
libraries or plugins.

## Plugin Video Tutorial

If you prefer learning by watching, here’s a video tutorial you can follow instead of reading the entire documentation :)

[![Watch the video tutorial](https://img.youtube.com/vi/nPUohtjVACg/0.jpg)](https://www.youtube.com/watch?v=nPUohtjVACg)

## Plugin Configuration and General Usage

After importing the plugin, you can use the Gradle definition to set up the plugin in the build.gradle file and after that running the
plugin tasks.

```
catalogDependenciesMonitor {
    // (mandatory) use the name of the catalog to monitor. "libs" in this case
    versionCatalog = project.extensions.getByType(VersionCatalogsExtension).named("libs") 
        
    // (optional) exclude libraries by group:artifact
    excludedLibraries = ["net.researchgate:gradle-release", "org.hidetake:core"]          

    // (optional) exclude plugins by pluginId
    excludedPlugins = []  

    // (optional) exclude library versions containing these strings (alpha and beta if you want to monitor only production ready libs)
    libraryVersionFilters = ["alpha", "beta"]
    
    // (optional) exclude plugin versions containing these strings (alpha and beta if you want to monitor only production ready plugins)
    pluginVersionFilters = ["alpha", "beta"]                               

    // (optional) specify the name of the generated report (catalog_report is the default if not specified)
    // The report name accepts only letters, numbers, dots, hyphens and underscores
    reportName = "catalog_report"                                                                                                                         

    // (optional) If nothing is specified here the Maven repository will be MAVEN_CENTRAL_V2 used as default
    // This repository is https://repo.maven.apache.org and is used to retrieve xml metadata of artifacts
    // The MAVEN_CENTRAL_V1 is deprecated and will be removed in future versions
    // This repository is https://search.maven.org and is used to retrieve metadata artifacts in json format
    // Is very discouraged to use MAVEN_CENTRAL_V1 sonce is deprecated and also a lot slower
    // However if in future new repositories will be used this is the way to change them.
    mavenRepositoryType = "MAVEN_CENTRAL_V2"
}
```

## Plugin Tasks

You can find the plugin tasks under the task group **Catalog-monitor**.
The tasks whose name starts with **_** are internal and are not meant to be used, whereas the other plugin tasks are:

### Main Tasks

* **generateReport** - Generates reports (catalog_report.html or catalog_report.json, or both) inside the
  build/catalog-dependencies-monitor/ directory.
* **updateDependenciesInTomlCatalog** - Automatically update your catalog toml file. We’ve been using this task for quite
  some time, although we still consider it experimental. It works very well, but it may contain some bugs. Please use it and let us know
  what you think or report any issues you encounter. Note that it simply updates the versions in the toml file. It never commits any changes
  to git.

### Print Tasks
* **printCatalogContent** - Prints out the catalog content and all defined dependencies
* **printCatalogResolved** - Print all the plugins and dependencies declared in the build.gradle with their actual form (plugins { ... id("plugin_id") version "plugin_version" ...} dependencies { ... implementation("group:name:version") ... })

## Contributors

If you would like to help but don't know where to start, please note that finding bugs and debugging the code is always a good start.
Simple Pull Requests that fix anything other than Version Comparator core code (documentation, Javadoc, typos, test cases, etc.) are always 
appreciated and would be merged quickly.
However, if you want or feel the need to change the main code or add a new functionality, please do not issue a pull request without [creating a new  issue](https://github.com/Rebirth-Project/rebirth-gradle-catalog-dependencies-monitor-plugin/issues/new)
and discussing your desired changes, _**before you start working on it**_. 
It would be a shame to reject your pull request if it might not align with the project's goals, design expectations or planned functionality.

For direct communications, you can use this [email](mailto:rebirthproject2021@gmail.com)

## Credits and License

Copyright (C) 2025 [Andrea Paternesi](https://github.com/patton73)

Copyright (C) 2025 [Matteo Veroni](https://github.com/mavek87)

Current website under creation [Rebirth Project](https://www.rebirth-project.it)

Gradle catalog dependencies monitor Plugin binaries and source code can be used according to the [Apache License, Version 2.0](LICENSE.md).