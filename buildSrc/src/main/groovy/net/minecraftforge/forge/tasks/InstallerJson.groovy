package net.minecraftforge.forge.tasks

import groovy.json.JsonBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.*

import java.nio.file.Files

abstract class InstallerJson extends DefaultTask {
    @OutputFile abstract RegularFileProperty getOutput()
    @InputFiles abstract ConfigurableFileCollection getInput()
    @Input @Optional abstract SetProperty<String> getPackedDependencies()
    @Input @Optional final Map<String, Object> libraries = new LinkedHashMap<>()
    @Input Map<String, Object> json = new LinkedHashMap<>()
    @InputFile abstract RegularFileProperty getIcon()
    @Input abstract Property<String> getLauncherJsonName()
    @Input abstract Property<String> getLogo()
    @Input abstract Property<String> getMirrors()
    @Input abstract Property<String> getWelcome()

    InstallerJson() {
        getLauncherJsonName().convention('/version.json')
        getLogo().convention('/big_logo.png')
        getMirrors().convention('https://files.minecraftforge.net/mirrors-2.0.json')
        getWelcome().convention("Welcome to the simple ${project.name.capitalize()} installer.")
                
        getOutput().convention(project.layout.buildDirectory.file('install_profile.json'))

        ['client', 'server'].each { side ->
            ['slim', 'extra'].each { type ->
                def tsk = project.tasks.getByName("download${side.capitalize()}${type.capitalize()}")
                dependsOn(tsk)
                input.from(tsk.output)
            }
            def tsk = project.tasks.getByName("create${side.capitalize()}SRG")
            dependsOn(tsk)
            input.from(tsk.output)
        }

        
        project.afterEvaluate {
            (packedDependencies.get().collect{ project.rootProject.tasks.findByPath(it) } + [project.universalJar]).forEach {
                dependsOn(it)
                input.from it.archiveFile
            }
        }
    }

    @TaskAction
    protected void exec() {
        def libs = libraries
        for (def child : packedDependencies.get().collect{ project.rootProject.tasks.findByPath(it) } + [project.universalJar]) {
            def dep = Util.getMavenDep(child)
            def path = Util.getMavenPath(child)
            libs.put(dep.toString(), [
                name: dep,
                downloads: [
                    artifact: [
                        path: path,
                        url: "https://maven.minecraftforge.net/${path}",
                        sha1: child.archiveFile.get().asFile.sha1(),
                        size: child.archiveFile.get().asFile.length()
                    ]
                ]
            ])
        }
        json.libraries = libs.values().sort{a,b -> a.name.compareTo(b.name)}
        json.icon = "data:image/png;base64," + new String(Base64.getEncoder().encode(Files.readAllBytes(icon.get().asFile.toPath())))
        json.json = launcherJsonName.get()
        json.logo = logo.get()
        if (!mirrors.get().isEmpty())
            json.mirrorList = mirrors.get()
        json.welcome = welcome.get()

        Files.writeString(output.get().getAsFile().toPath(), new JsonBuilder(json).toPrettyString())
    }
}
