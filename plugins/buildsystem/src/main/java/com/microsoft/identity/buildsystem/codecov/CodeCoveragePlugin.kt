//  Copyright (c) Microsoft Corporation.
//  All rights reserved.
//
//  This code is licensed under the MIT License.
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files(the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions :
//
//  The above copyright notice and this permission notice shall be included in
//  all copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//  THE SOFTWARE.
package com.microsoft.identity.buildsystem.codecov

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.api.SourceKind
import org.gradle.api.DomainObjectSet
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.plugins.PluginContainer
import org.gradle.api.tasks.TaskContainer
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport
import java.io.File

/**
 * This class creates code coverage tasks in the given project
 */
object CodeCoveragePlugin {

    private lateinit var reportExtension: CodeCoverageReportExtension

    /**
     * Gets the codeCoverageReport configurations and uses them to create the code coverage tasks
     */
    @JvmStatic
    fun applyCodeCoveragePlugin(project: Project) {
        // get the configurations under codeCoverageReport
        reportExtension = project.extensions.create("codeCoverageReport", CodeCoverageReportExtension::class.java)

        // apply jacoco
        if (project.plugins.withType(JacocoPlugin::class.java).isEmpty()) {
            project.plugins.apply(JacocoPlugin::class.java)
        }

        // after build file has been evaluated ... add tasks
        project.afterEvaluate { evaluatedProject ->
            // apply plugin after android/android-library
            findAndroidPlugin(evaluatedProject.plugins)

            val jacocoTestReportTask = findOrCreateJacocoTestReportTask(evaluatedProject.tasks)

            if (reportExtension.unitTests.enabled) {
                createTask(project, jacocoTestReportTask, TestTypes.UnitTest)
            }
        }
    }

    /**
     * Creates the code coverage tasks for the different build variants
     */
    private fun createTask(project: Project, jacocoTestReportTask: Task, testType: String) {
        val excludeFlavours = (reportExtension.excludeFlavours ?: setOf("")).map { it.toLowerCase() }
        project.android().variants().all { variant ->
            if (variant.buildType.isTestCoverageEnabled && !excludeFlavours.contains(variant.flavorName.toLowerCase())) {
                val reportTask = createReportTask(project, variant, testType)
                jacocoTestReportTask.dependsOn(reportTask)
            }
        }
    }

    /**
     * Creates the code coverage task for the given build variant and adds it to a group (Reporting)
     */
    private fun createReportTask(project: Project, variant: BaseVariant, testType: String): JacocoReport {
        // get the sources
        val sourceDirs = variant.getSourceFolders(SourceKind.JAVA).map { file -> file.dir }
        // get the classes
        val classesDir = variant.javaCompileProvider.get().destinationDir
        // get the test task for this variant
        val testTask = testTask(project.tasks, variant, testType)
        // get JacocoTaskExtension execution destination
        val executionData = executionDataFile(testTask)

        val taskName = "${variant.name}${project.name.capitalize()}${testType}CoverageReport"
        return project.tasks.create(taskName, JacocoReport::class.java) { reportTask ->
            // set the task attributes
            reportTask.dependsOn(testTask)
            reportTask.group = "Reporting"
            reportTask.description = "Generates Jacoco coverage reports for the ${variant.name} variant."
            reportTask.executionData.setFrom(project.files(executionData))
            reportTask.sourceDirectories.setFrom(project.files(sourceDirs))

            // get the java project tree and exclude the defined excluded classes
            val javaTree = project.fileTree(classesDir, excludes = getFileFilterPatterns())
            // if kotlin is available, get the kotlin project tree and exclude the defined excluded classes
            if (hasKotlin(project.plugins)) {
                val kotlinClassesDir = "${project.buildDir}/tmp/kotlin-classes/${variant.name}"
                val kotlinTree = project.fileTree(kotlinClassesDir, excludes = getFileFilterPatterns())
                reportTask.classDirectories.setFrom(javaTree + kotlinTree)
            } else {
                reportTask.classDirectories.setFrom(javaTree)
            }

            reportTask.reports { task ->
                // set the outputs enabled according to configs
                task.html.isEnabled = reportExtension.html.enabled
                task.xml.isEnabled = reportExtension.xml.enabled
                task.csv.isEnabled = reportExtension.csv.enabled

                // default reports path
                val defaultCommonPath = "${project.buildDir}/reports/jacoco/$taskName"
                val configuredDestination = reportExtension.destination

                // configure destination for html code coverage output
                if (reportExtension.html.enabled) {
                    val path = File(if (configuredDestination.isNullOrBlank()) "$defaultCommonPath/html" else "${configuredDestination.trim()}/html")
                    task.html.destination = path
                }

                // configure destination for xml code coverage output
                if (reportExtension.xml.enabled) {
                    val path = File(if (configuredDestination.isNullOrBlank()) "$defaultCommonPath/${taskName}.xml" else "${configuredDestination.trim()}/${taskName}.xml")
                    task.xml.destination = path
                }

                // configure destination for csv code coverage output
                if (reportExtension.csv.enabled) {
                    val path = File(if (configuredDestination.isNullOrBlank()) "$defaultCommonPath/${taskName}.csv" else "${configuredDestination.trim()}/${taskName}.csv")
                    task.csv.destination = path
                }
            }
        }
    }

    /**
     * Checks whether android/android-library plugins are available
     */
    private fun findAndroidPlugin(plugins: PluginContainer) {
        plugins.findPlugin("android") ?: plugins.findPlugin("android-library")
        ?: throw GradleException("You must apply the Android plugin or the Android library plugin before using the jacoco-android plugin")
    }

    /**
     * Creates a combined code coverage task
     */
    private fun findOrCreateJacocoTestReportTask(tasks: TaskContainer): Task {
        var task: Task? = tasks.findByName("combinedCoverageReport")
        if (task == null) {
            task = tasks.create("combinedCoverageReport") { tsk ->
                tsk.group = "Reporting"
            }
        }
        return task!!
    }

    /**
     * Get the test task for this variant
     * todo - getting for android test
     */
    private fun testTask(tasks: TaskContainer, variant: BaseVariant, testType: String): Task {
        // todo - activate and solve issue with androidTests code coverage
        // val name = if (testType == TestTypes.UnitTest) "test${variant.name.capitalize()}UnitTest" else "connected${variant.name.capitalize()}AndroidTest"
        val name = "test${variant.name.capitalize()}UnitTest"
        return tasks.getByName(name)
    }

    // get JacocoTaskExtension execution destination
    private fun executionDataFile(testTask: Task): File? {
        return testTask.extensions.findByType(JacocoTaskExtension::class.java)?.destinationFile
    }

    // kotlin is available
    private fun hasKotlin(plugins: PluginContainer) = plugins.hasPlugin("kotlin-android")

    /**
     * method to get the android extension - as a Project class extension method!
     */
    private fun Project.android(): BaseExtension {
        val android = project.extensions.findByType(BaseExtension::class.java)
        if (android != null) {
            return android
        } else {
            throw GradleException("Project $name is not an Android project")
        }
    }

    /**
     * method to get variants
     */
    private fun BaseExtension.variants(): DomainObjectSet<out BaseVariant> {
        return when (this) {
            is AppExtension -> {
                applicationVariants
            }

            is LibraryExtension -> {
                libraryVariants
            }

            else -> throw GradleException("Unsupported BaseExtension type!")
        }
    }

    // get files to exclude
    private fun getFileFilterPatterns(): Set<String> = DEFAULT_EXCLUDES +
            (reportExtension.excludeClasses ?: setOf(""))

    // utility method to get the file tree
    private fun Project.fileTree(dir: Any, excludes: Set<String> = setOf(), includes: Set<String> = setOf()): ConfigurableFileTree =
            fileTree(mapOf("dir" to dir, "excludes" to excludes, "includes" to includes))
}