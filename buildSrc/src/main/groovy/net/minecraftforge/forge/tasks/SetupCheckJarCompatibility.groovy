package net.minecraftforge.forge.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Dependency
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

import java.nio.file.Files
import java.nio.file.StandardCopyOption

abstract class SetupCheckJarCompatibility extends DefaultTask {
    SetupCheckJarCompatibility() {
        group = 'jar compatibility'
        onlyIf {
            inputVersion.getOrNull() != null
        }
        outputs.upToDateWhen { false } // Never up to date, because this setup task should always run

        baseBinPatchesOutput.convention(project.layout.buildDirectory.dir(name).map { it.file('joined.lzma') })
    }

    @TaskAction
    void run() {
        def inputVersion = inputVersion.get()
        def fmlLibs = project.configurations.detachedConfiguration(project.PACKED_DEPS.collect {
            def artifactId = it.split(':')[1]
            return project.dependencies.create("net.minecraftforge:${artifactId}:${inputVersion}")
        }.toArray(Dependency[]::new))

        project.tasks.named('checkJarCompatibility') {
            baseLibraries.from(project.provider {
                fmlLibs.resolvedConfiguration.lenientConfiguration.files
            })
        }

        def baseForgeUserdev = project.layout.buildDirectory.dir(name).map { it.file("forge-${inputVersion}-userdev.jar") }.get().asFile
        project.rootProject.extensions.download.run {
            src "https://maven.minecraftforge.net/net/minecraftforge/forge/${inputVersion}/forge-${inputVersion}-userdev.jar"
            dest baseForgeUserdev
        }

        def joinedLzma = project.zipTree(baseForgeUserdev).matching { it.include('joined.lzma') }.singleFile

        Files.copy(joinedLzma.toPath(), baseBinPatchesOutput.get().asFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
    }

    @Input
    @Optional
    abstract Property<String> getInputVersion()

    @OutputFile
    abstract RegularFileProperty getBaseBinPatchesOutput()
}
