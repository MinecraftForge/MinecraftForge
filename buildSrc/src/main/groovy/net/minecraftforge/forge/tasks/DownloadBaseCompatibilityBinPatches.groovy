package net.minecraftforge.forge.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

import java.nio.file.Files

abstract class DownloadBaseCompatibilityBinPatches extends DefaultTask {
    DownloadBaseCompatibilityBinPatches() {
        group = 'jar compatibility'
        onlyIf {
            inputVersion.getOrNull() != null
        }

        output.convention(project.layout.buildDirectory.dir(name).map { it.file('joined.lzma') })
    }

    @TaskAction
    void run() {
        def dep = project.dependencies.create("net.minecraftforge:forge:${inputVersion.get()}:userdev")
        def baseForgeUserdev = project.configurations.detachedConfiguration(dep).resolve().iterator().next()

        def joinedLzma = project.zipTree(baseForgeUserdev).matching { it.include('joined.lzma') }.singleFile

        Files.copy(joinedLzma.toPath(), output.get().asFile.toPath())
    }

    @Input
    @Optional
    abstract Property<String> getInputVersion()

    @OutputFile
    abstract RegularFileProperty getOutput()
}
