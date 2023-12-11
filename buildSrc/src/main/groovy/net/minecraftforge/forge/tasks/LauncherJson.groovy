package net.minecraftforge.forge.tasks

import groovy.json.JsonBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.*

import java.nio.file.Files

import static net.minecraftforge.forge.tasks.Util.getArtifacts
import static net.minecraftforge.forge.tasks.Util.iso8601Now

abstract class LauncherJson extends DefaultTask {
    @OutputFile abstract RegularFileProperty getOutput()
    @InputFiles abstract ConfigurableFileCollection getInput()
    @Input Map<String, Object> json = new LinkedHashMap<>()

    LauncherJson() {
        output.convention(project.layout.buildDirectory.file('libs/version.json'))

        dependsOn(project.tasks.universalJar)
        input.from(project.tasks.universalJar.archiveFile)
        input.from(project.configurations.installer)
        configure {
            def mc    = project.rootProject.ext.MC_VERSION
            def forge = project.rootProject.ext.FORGE_VERSION
            def timestamp = iso8601Now()
            json.putAll([
                _comment: [
                    "Please do not automate the download and installation of Forge.",
                    "Our efforts are supported by ads from the download page.",
                    "If you MUST automate this, please consider supporting the project through https://www.patreon.com/LexManos/"
                ],
                id: "$mc-$project.name$forge",
                time: timestamp,
                releaseTime: timestamp,
                inheritsFrom: mc,
                type: 'release',
                logging: [:],
                mainClass: '',
                libraries: []
            ] as LinkedHashMap)
            
            [
                project.tasks.universalJar
            ].forEach { packed ->
                dependsOn(packed)
                input.from packed.archiveFile
            }
            
            def patched = project.tasks.applyClientBinPatches
            dependsOn(patched)
            input.from patched.output
        }
    }

    @TaskAction
    protected void exec() {
        [
            project.tasks.universalJar
        ].forEach { packed ->
            def info = Util.getMavenInfoFromTask(packed)
            json.libraries.add([
                name: info.name,
                downloads: [
                    artifact: [
                        path: info.path,
                        url: "https://maven.minecraftforge.net/$info.path",
                        sha1: packed.archiveFile.get().asFile.sha1(),
                        size: packed.archiveFile.get().asFile.length()
                    ]
                ]
            ])
        }
        [
            'client': project.tasks.applyClientBinPatches
        ].forEach { classifier, genned ->
            def info = Util.getMavenInfoFromTask(genned, classifier)
            json.libraries.add([
                name: info.name,
                downloads: [
                    artifact: [
                        path: info.path,
                        url: "",
                        sha1: genned.output.get().asFile.sha1(),
                        size: genned.output.get().asFile.length()
                    ]
                ]
            ])
        }
        getArtifacts(project, project.configurations.installer).each { key, lib -> 
            json.libraries.add(lib)
        }
        Files.writeString(output.get().asFile.toPath(), new JsonBuilder(json).toPrettyString())
    }
}
