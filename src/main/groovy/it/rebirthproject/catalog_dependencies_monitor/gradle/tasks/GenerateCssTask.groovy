/*
 * Copyright (C) 2024 Matteo Veroni Rebirth project
 * Modifications copyright (C) 2024 Andrea Paternesi Rebirth project
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
package it.rebirthproject.catalog_dependencies_monitor.gradle.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class GenerateCssTask extends DefaultTask {

    @TaskAction
    void executeTask() {
        outputs.files.singleFile.setText(cssContent)
    }

    static final String cssContent = """
html {
    scroll-behavior: smooth;
}

body {
    font-family: Arial, sans-serif;
    margin: 0;
    padding: 0;
    background-color: #f7f7f7;
}

h1 {
    text-align: center;
    margin-top: 20px;
    font-size: 3rem;
}

.container {
    max-width: 1920px;
    margin: 20px auto;
    padding: 20px;
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

table {
    width: 100%;
    border-collapse: collapse;
    margin-bottom: 20px;
    table-layout: fixed;
}

th, td {
    border: 1px solid #ddd;
    padding: 12px;
    text-align: left;
    word-wrap: break-word;
    max-width: 0;
}

th {
    background-color: #f2f2f2;
    font-weight: bold;
    font-size: 22px;
}

td {
    font-size: 20px;
}

th:first-child,
td:first-child {
    width: 2%;
}

th:nth-child(3),
td:nth-child(3) {
    width: 12%;
    text-align: center;
}

th:last-child,
td:last-child {
    width: 10%;
}

th:last-child,
td:last-child {
    text-align: center;
}

h3 {
    margin: 2px;
    font-size: 2rem;
}

.updated {
    background-color: #15b525; /* Verde */
    font-weight: bold;
}

.updated-light {
    background-color: #89f192; /* Verde chiaro */
}

.outdated {
    background-color: #FF6347; /* Rosso */
    font-weight: bold;
}

.outdated-light {
    background-color: #ffab9d; /* Rosso chiaro */
}

.exceeding {
    background-color: #ffda47; /* Giallo */
    font-weight: bold;
}

.exceeding-light {
    background-color: #ffe998; /* Giallo chiaro */
}

.excluded,
.skipped {
    background-color: rgb(204, 204, 204); /* Grigio */
    font-weight: bold;
}

.excluded-light,
.skipped-light {
    background-color: rgb(238, 238, 238); /* Grigio chiaro */
}

/* CSS Accordion */

input {
    position: absolute;
    opacity: 0;
    z-index: -1;
}

.accordion {
    margin-bottom: 2rem;
    border-radius: 5px;
    overflow: hidden;
    box-shadow: 0 4px 4px -2px rgba(0, 0, 0, 0.5);
}

.accordion-label {
    display: flex;
    justify-content: space-between;
    padding: 1em;
    font-weight: bold;
    cursor: pointer;
    background: #333;
    color: #fff;
}

.accordion-content {
    max-height: 0;
    padding: 0 1em;
    background: white;
    transition: all 0.25s;
    overflow: hidden;
}

input:checked ~ .accordion-content {
    max-height: max-content;
    padding: 1em;
}

nav > section {
    display: flex;
    flex-direction: row;
    justify-content: space-evenly;
    align-content: center;
    position: fixed;
    top: 0;
    width: 100%;
    font-size: 22px;
    height: 60px;
    background: lightblue;
    opacity: 0.98;
}

nav div {
    display: flex;
    flex-direction: column;
    justify-content: center;
}

section > div > a {
    text-decoration: none;
    font-weight: bold;
    color: #1e4dcd;
}

.footer {
    margin: 0 auto;
    width: max-content;
}
"""
}