/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.forge.build

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.WriteProperties
import org.jspecify.annotations.Nullable

import javax.inject.Inject
import javax.naming.spi.ObjectFactory

@CompileStatic
class Runs implements Meta {
    private final ConventionExtension ext
    private boolean hasDependency = false

    @Inject
    Runs(ConventionExtension ext) {
        this.ext = ext
    }

    @Override
    Project getProject() {
        this.ext.project
    }

    void main(@Nullable String mods, @Nullable File output) {
        final project = this.project
        final genAllData = this.ext.genAllData

        project.with {
            final SourceSet sourceSet = getJava().getSourceSets().named("main").get()
            if (!hasDependency && project != project.rootProject) {
                dependencies.with {
                    add('implementation', it.project(':')) // Root project is forge itself
                }
                if (output != null)
                    sourceSet.resources.with { srcDir 'src/main/generated' }
                hasDependency = true
            }

            forgedev.runs.with {
                final File existing = sourceSet.getResources().getSrcDirs()[0]

                configureEach {
                    it.with {
                        cache.set(project.layout.projectDirectory.dir('runs/cache'))
                        eclipsePrefix = project.name

                        // If we're not forge, we need to get the metadata from the forge project
                        if (project != project.rootProject)
                            metadata = project.files(getForgedev().consumeFile('metadata', project.rootProject))

                        var runName = it.name.toLowerCase(Locale.ENGLISH)

                        if (runName.contains('client') || runName.contains('data')) {
                            options.with {
                                args '--assetsDir', '{assets_root}', '--assetIndex', '{asset_index}'
                                systemProperty 'org.lwjgl.system.SharedLibraryExtractDirectory', 'lwjgl_dll'
                            }
                        }

                        if (runName.contains('data')) {
                            options.with(sourceSet).with {
                                systemProperties.put("forge.project.list", getProjectList().flatMap { it.destinationFile.map{ it.asFile.absolutePath } })
                                if (mods != null)   args('--mod', mods)
                                if (output != null) args('--output', output)
                            }
                        }

                        options.with {
                            getWorkingDir().set(project.layout.projectDirectory.dir("runs/$name"))
                            getMainClass().set('net.minecraftforge.bootstrap.ForgeBootstrap')
                            args '--gameDir', '.'
                            jvmArgs '-Djava.net.preferIPv6Addresses=system', '-XX:+UseCompactObjectHeaders', '-XX:StackShadowPages=32'

                            systemProperty 'bsl.debug', 'true'
                            systemProperty 'terminal.jline', 'true'
                            systemProperty 'forge.enableGameTest', 'true'
                            systemProperty 'eventbus.api.strictRuntimeChecks', 'true'
                        }
                    }
                }

                maybeCreate('client').with {
                    options.with {
                        args '--launchTarget', 'forge_dev_client',
                            '--username', 'Dev',
                            '--version', project.name,
                            '--accessToken', '0',
                            '--userType', 'mojang',
                            '--versionType', 'release'
                    }
                }

                maybeCreate('server').with {
                    options.with {
                        args '--launchTarget', 'forge_dev_server'
                    }
                }

                maybeCreate('gameTestServer').with {
                    options.with {
                        args '--launchTarget', 'forge_dev_server_gametest',
                            '--uniqueWorld' // Unique world is used so that the world regenerates, as well as the world config isn't influenced by other runs
                    }
                }

                maybeCreate('data').with {
                    options.with {
                        args '--launchTarget', 'forge_dev_data',
                            '--all',
                            '--existing', existing
                    }
                }

                maybeCreate('clientData').with {
                    options.with {
                        args '--launchTarget', 'forge_dev_client_data',
                            '--all',
                            '--existing', existing
                    }
                }
            }
            genAllData.configure {
                it.dependsOn forgedev.runs.getByName('data').with(sourceSet).run, forgedev.runs.getByName('clientData').with(sourceSet).run
            }
        }
    }

    void test(@Nullable String mods, @Nullable File output) {
        final project = this.project
        final genAllData = this.ext.genAllData

        project.with {
            final SourceSet sourceSet = getJava().getSourceSets().named("test").get()
            forgedev.runs.with {
                final File existing = sourceSet.getResources().getSrcDirs()[0]

                configureEach {
                    it.with {
                        var runName = it.name.toLowerCase(Locale.ENGLISH)
                        options.with(sourceSet).with {
                            systemProperty 'forgedev.enableTestMods', 'true'
                        }

                        if (runName.contains('data')) {
                            options.with(sourceSet).with {
                                if (mods != null)   args('--mod', mods)
                                if (output != null) args('--output', output)
                                args('--existing', existing)
                            }
                        }
                    }
                }
            }
            genAllData.configure {
                it.dependsOn forgedev.runs.getByName('data').with(sourceSet).run, forgedev.runs.getByName('clientData').with(sourceSet).run
            }
        }
    }

    private TaskProvider<WriteProperties> projectInfo = null
    TaskProvider<WriteProperties> getProjectInfo() {
        if (projectInfo == null) {
            projectInfo = this.project.tasks.register("writeProjectInfo", WriteProperties)
            projectInfo.configure {
                it.destinationFile.set(this.project.file('src/main/resources/.project_info.properties'))
                it.property('name', this.project.rootProject.name + this.project.isolated.path.replaceAll(':', '.'))
                it.property('output', 'src/main/generated')
            }
            this.project.tasks.named("generateResources").configure { it.dependsOn(projectInfo) }
        }
        return projectInfo
    }
    TaskProvider<WriteProperties> projectInfo(Action<? extends WriteProperties> action) {
        getProjectInfo().configure(action)
        return getProjectInfo()
    }
    void setFlat(boolean value) {
        projectInfo(task -> task.property('flat', value))
    }

    private TaskProvider<WriteProperties> projectList = null
    TaskProvider<WriteProperties> getProjectList() {
        if (projectList == null) {
            projectList = this.project.tasks.register("writeProjectList", WriteProperties)
            projectList.configure { task ->
                task.destinationFile.set(this.project.layout.buildDirectory.file('project_list.properties'))
                task.encoding = 'UTF-8'
                final root = this.project.rootProject.name
                this.project.rootProject.allprojects.forEach { p ->
                    task.property(root + p.isolated.path.replaceAll(':', '.'), p.isolated.projectDirectory.asFile.absolutePath)
                }
            }
        }
        return projectList
    }
    TaskProvider<WriteProperties> projectList(Action<? extends WriteProperties> action) {
        getProjectList().configure(action)
        return getProjectList()
    }
}
