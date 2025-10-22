# Gradle Catalog Dependencies Monitor Plugin

Gradle plugin for monitoring Gradle catalog dependencies. It generates reports to inform you about which dependencies in
your catalog are up-to-date or need updating.

**```Latest Version 1.0.0```**

![Build Status](https://github.com/Rebirth-Project/rebirth-gradle-catalog-dependencies-monitor-plugin/actions/workflows/build.yml/badge.svg?raw=true)

## Requirements

- Minimum gradle version: 8

## Main features

* Checks all the defined dependencies in a standard gradle catalog

## Goals

* Provide a simple-to-use plugin to speed up and simplify dependencies's version check

## How to add Catalog Dependencies Monitor plugin in your gradle project

```
//va poi aggiornata la documentazione per mettere la versione e scaricare il plugin giusto dal gradle plugin site
plugins {
    id("it.rebirthproject.catalog-dependencies-monitor")
}
```

## Introduction

The Gradle Catalog Dependencies Monitor Plugin is a plugin designed to simplify the management and monitoring of dependencies defined in a standard gradle catalog.
In practice, instead of using similar plugins in every project or library, everything is centralized by moving the checks to the catalog side.
By default, this plugin connects to the **Maven Central repository** and the **Gradle Plugin repository** to retrieve dependency metadata and verify whether newer versions are available.
It produces a detailed report in **HTML** or a **JSON** format, showing the results of this lookup and comparison process.
The report is divided in two parts: the libraries' report and the plugins' report. Each report will visualize the status of the relative library or plugin. 

## Usage

```
catalogDependenciesMonitor {
    // use the name of the catalog to monitor. "libs" in this case
    versionCatalog = project.extensions.getByType(VersionCatalogsExtension).named("libs") 
    // (optional) If nothing is specified here the Maven repository will be MAVEN_CENTRAL_V2 used as default
    // The MAVEN_CENTRAL_V1 is deprecated and will be removed in future versions
    mavenRepositoryType = "MAVEN_CENTRAL_V2"
    // (optional) exclude libraries by group:artifact
    excludedLibraries = ["net.researchgate:gradle-release", "org.hidetake:core"]          
    // (optional) exclude plugins by pluginId
    excludedPlugins = []  
    // (optional) exclude library containing these strings (alpha and beta if you want to monitor only production ready libs)
    libraryVersionFilters = ["alpha", "beta"]                                             
    // (optional) specify the name of the generated report (catalog_report is the default if not specified)
    reportName = "catalog_report"                                                         
    // (optional) specify the type of the generated report (html or json are the possible choices. If not specified html will be used)
    reportType = "html"                                                                   
}
```

The gradle task to call to generate the report is named **generateReport**.

After that a report (html or json) will be generated inside the build directory.

## Contributors

If you would like to help, but don't know where to start, please note that finding bugs and debugging the code is always
a good start.
Simple Pull Requests that fix anything other than Version Comparator core code (documentation, JavaDoc, typos, test
cases, etc) are
always appreciated and would be merged quickly.
However, if you want or feel the need to change the main code or add a new functionality, please do not issue a pull
request
without [creating a new  issue](https://github.com/Rebirth-Project/rebirth-gradle-catalog-dependencies-monitor-plugin/issues/new)
and discussing your desired
changes,  _**before you start working on it**_.
It would be a shame to reject your pull request if it might not align with the project's goals, design expectations or
planned functionality.

For direct communications, you can use this [email](mailto:rebirthproject2021@gmail.com)

## Credits and License

Copyright (C) 2025 [Andrea Paternesi](https://github.com/patton73)

Copyright (C) 2025 [Matteo Veroni](https://github.com/mavek87)

Current website under creation [Rebirth Project](https://www.rebirth-project.it)

Gradle catalog sependencies monitor Plugin binaries and source code can be used according to the [Apache License, Version 2.0](LICENSE.md).

