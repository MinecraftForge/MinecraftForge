/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.forge.build

import groovy.transform.CompileStatic
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion

@CompileStatic
class ConventionPlugin implements Plugin<Project>, Meta {
    Project project
    @Override
    Project getProject() {
        this.project
    }

    @Override
    void apply(Project project) {
        this.project = project
        project.with {
            pluginManager.with {
                apply 'java-library'
                apply 'maven-publish'
                apply pluginId('licenser')
                apply pluginId('gradleutils')
                apply pluginId('gitversion')
                apply pluginId('changelog')
                apply pluginId('forgedev')
            }

            final versions = extensions.create("versions", VersionsExtension, project)
            final convention = extensions.create('convention', ConventionExtension, project)

            logger.lifecycle(name + " " + version)

            gradleutils.with {
                vendor.set('Forge Development LLC')
                version.set(gitversion.version)
            }
            project.setGroup('net.minecraftforge')

            java.with {
                toolchain.languageVersion.set(JavaLanguageVersion.of(versions.java))
                // This is needed for IDE runtime classpath to be correct/not have multiple of the same artifact
                consistentResolution {
                    it.useCompileClasspathVersions()
                }
                // Forge needs to build its own sources jar due to patching, everything else should use the default
                if (project != rootProject)
                    it.withSourcesJar()
            }

            tasks.withType(JavaCompile).configureEach {
                it.options.with {
                    encoding = 'UTF-8'
                    compilerArgs << '-Xlint:-unchecked' // This is mainly for vanilla code, but just makes it quiet down outside the IDE
                }
            }

            // NOTE: We are adding dependencies to projects instead of through settings, so we don't have to apply forgedev in settings.
            // Applying forgedev in settings will break IDE linting support, which makes it very hard to work with.
            // We can reconsider this if/when JetBrains gets their shit together.
            //   - I doubt they will ever fix their shit 8/28/2006 - Lex
            repositories.with {
                // Libraries has to be before maven central because Mojang hosts a classifer that central doesn't (org.lwjgl:lwjgl-freetype:3.3.3:natives-macos-patch)
                maven { url = 'https://libraries.minecraft.net/' }
                mavenCentral()
                maven gradleutils.forgeMaven
                project.pluginManager.withPlugin('net.minecraftforge.forgedev') {
                    maven forgedev.mavenizer
                }

                mavenLocal()
            }

            changelog.with {
                from versions.changelogBase
            }

            license.with {
                header = rootProject.file('LICENSE-header.txt')
                exclude('.project_info.properties')
            }

            // Common dependencies,
            project.dependencies.with {
                add('compileOnly', libs.findLibrary('jetbrains.annotations').get())
            }
        }
    }
}
