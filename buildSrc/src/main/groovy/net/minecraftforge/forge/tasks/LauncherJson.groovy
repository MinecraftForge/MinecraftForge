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
    @Input @Optional abstract SetProperty<String> getPackedDependencies()
    
    @Internal final vanilla = project.project(':mcp').file('build/mcp/downloadJson/version.json')
    @Internal final timestamp = iso8601Now()
    @Internal final comment = [
        "Please do not automate the download and installation of Forge.",
        "Our efforts are supported by ads from the download page.",
        "If you MUST automate this, please consider supporting the project through https://www.patreon.com/LexManos/"
    ]
    @Internal final id = "${project.rootProject.ext.MC_VERSION}-${project.name}${project.version.substring(project.rootProject.ext.MC_VERSION.length())}"

    LauncherJson() {
        getOutput().convention(project.layout.buildDirectory.file('version.json'))

        dependsOn(':fmlloader:jar', 'universalJar')
        getInput().from(project.tasks.universalJar.archiveFile,
                project.project(':fmlloader').jar.archiveFile,
                vanilla)
                
        project.afterEvaluate {
            packedDependencies.get().forEach {
                def jarTask = project.rootProject.tasks.findByPath(it)
                dependsOn(jarTask)
                input.from jarTask.archiveFile
            }
        }
    }

    @TaskAction
    protected void exec() {
        if (!json.libraries)
            json.libraries = []
        def libs = [:]
        getArtifacts(project, project.configurations.installer, false).each { key, lib -> libs[key] = lib }
        getArtifacts(project, project.configurations.moduleonly, false).each { key, lib -> libs[key] = lib }

        packedDependencies.get().collect{ project.rootProject.tasks.findByPath(it) }.forEach {
            def path = Util.getMavenPath(it)
            def key = Util.getMavenDep(it)
            
            libs[key] = [
                name: key,
                downloads: [
                    artifact: [
                        path: path,
                        url: "https://maven.minecraftforge.net/${path}",
                        sha1: it.archiveFile.get().asFile.sha1(),
                        size: it.archiveFile.get().asFile.length()
                    ]
                ]
            ]
        }
        libs.each { key, lib -> json.libraries.add(lib) }
        Files.writeString(output.get().asFile.toPath(), new JsonBuilder(json).toPrettyString())
    }
}
