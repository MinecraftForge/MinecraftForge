/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.forge.build

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.ExternalModuleDependencyBundle
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar

import javax.inject.Inject

@CompileStatic
abstract class ConventionExtension implements Meta {
    private final Project project;
    private final Runs runs

    protected abstract @Inject ObjectFactory getObjects()
    protected abstract @Inject ProviderFactory getProviders()

    @Inject
    ConventionExtension(Project project) {
        this.project = project
        this.runs = objects.newInstance(Runs, this)
    }

    @Override
    Project getProject() {
        this.project
    }

    void manifest(String pkg) {
        this.project.tasks.named('jar', Jar).configure {
            this.gradleutils.manifestDefaults(it.manifest, pkg)
        }
    }
    void manifest(String pkg, Map<String, String> info) {
        manifest(pkg)
        this.project.tasks.named('jar', Jar).configure {
            it.manifest.getAttributes().with {
                for (var entry : info.entrySet())
                    put(entry.key, entry.value)
            }
        }
    }

    // region Maven publishing ==============================================================
    void publish() {
        publish(pub -> pub.from(project.components.getByName('java')))
    }
    void publish(Action<MavenPublication> config) {
        project.with {
            publishing.with {
                repositories.with {
                    maven gradleutils.publishingForgeMaven
                }
                publications.register('mavenJava', MavenPublication) {
                    changelog.publish(it)
                    gradleutils.promote(it)

                    // The actual artifacts to be published should be configured, so run the config
                    config.execute(it)

                    it.pom.with {
                        description = project.description
                        gradleutils.pom.addRemoteDetails(it)
                        license.with {
                            gradleutils.pom.licenses.LGPLv2_1
                        }
                    }
                }
            }
        }
    }
    // endregion

    // region Runs ================================================================
    private TaskProvider<Task> genAllData = null
    TaskProvider<Task> getGenAllData() {
        if (genAllData == null)
            genAllData = this.project.tasks.register('genAllData')
        return genAllData
    }
    TaskProvider<Task> getGenAllData(Action<Task> config) {
        getGenAllData().configure(config)
        return getGenAllData()
    }

    Runs getRuns() {
        return this.runs
    }
    Runs runs(Action<Runs> config) {
        config.execute(this.runs)
        return this.runs
    }
    Runs basicRuns() {
        return basicRuns({})
    }
    Runs basicRuns(Action<Runs> config) {
        this.runs.main(null, null)
        config.execute(this.runs)
        return getRuns()
    }
    Runs basicRuns(String mods, File output) {
        return basicRuns(mods, output, {})
    }
    Runs basicRuns(String mods, File output, Action<Runs> config) {
        this.runs.main(mods, output)
        config.execute(this.runs)
        return getRuns()
    }
    Runs testRuns(String mods, File output) {
        return testRuns(mods, output, {})
    }
    Runs testRuns(String mods, File output, Action<Runs> config) {
        this.runs.test(mods, output)
        config.execute(this.runs)
        return getRuns()
    }
    // endregion

    // region Forge Version Json ===================================================
    private TaskProvider<WriteForgeVersionJson> writeForgeVersionJson = null
    TaskProvider<WriteForgeVersionJson> getForgeVersionJson() {
        if (writeForgeVersionJson == null) {
            writeForgeVersionJson = this.project.tasks.register('writeForgeVersionJson', WriteForgeVersionJson)
            final versions = this.versions
            writeForgeVersionJson.configure {
                it.forgeVersion.set(versions.forge)
                it.minecraftVersion.set(versions.minecraft)
                it.mcpVersion.set(versions.mcp)
            }
        }
        return writeForgeVersionJson
    }
    TaskProvider<WriteForgeVersionJson> forgeVersionJson(Action<WriteForgeVersionJson> config) {
        forgeVersionJson.configure(config)
        return forgeVersionJson
    }
    // endregion

    // region Natives helper =========================================================
    // See gradle/gradle#35070 (https://github.com/gradle/gradle/issues/35070)
    // We are making the list of providers first BEFORE mapping them to a provider of the resolved objects so
    // that they can be finalized after configuration.
    List<MinimalExternalModuleDependency> nativeVariants(Provider<ExternalModuleDependencyBundle> bundle) {
        final natives = "natives-" + forgedev.os
        final deps = this.project.dependencies
        bundle.get().collect { dep ->
            (deps.variantOf(providers.provider { (MinimalExternalModuleDependency)dep }) {
                it.classifier(natives)
            }).get()
        }
    }
    // endregion
}
