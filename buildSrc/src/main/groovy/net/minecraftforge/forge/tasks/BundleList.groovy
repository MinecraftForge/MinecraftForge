package net.minecraftforge.forge.tasks

import org.gradle.api.tasks.*
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.bundling.AbstractArchiveTask

import java.util.zip.ZipFile

abstract class BundleList extends DefaultTask {
    @InputFiles abstract ConfigurableFileCollection getConfig()
    @InputFile abstract RegularFileProperty getServerBundle()
    @OutputFile abstract RegularFileProperty getOutput()

    BundleList() {
        config.setFrom(project.configurations.installer)
        output.convention(project.layout.buildDirectory.file("$name/output.list"))
        configure {
            dependsOn(project.tasks.universalJar)
            inputs.file(project.tasks.universalJar.archiveFile)
        }
    }
    
    @TaskAction
    void run() {
        def entries = [:] as TreeMap
        def resolved = project.configurations.installer.resolvedConfiguration.resolvedArtifacts
        for (def dep : resolved) {
            def info = Util.getMavenInfoFromDep(dep)
            //println("$dep.file.sha1\t$info.name\t$info.path")
            entries.put("$info.art.group:$info.art.name", "$dep.file.sha256\t$info.name\t$info.path")
        }

        var packed = (AbstractArchiveTask) project.tasks.universalJar
        var info = Util.getMavenInfoFromTask(packed)
        def file = packed.archiveFile.get().asFile
        entries.put("$info.art.group:$info.art.name:$info.art.classifier", "$file.sha256\t$info.name\t$info.path")

        var classifier = 'server'
        var genned = project.tasks.applyServerBinPatches
        info = Util.getMavenInfoFromTask(genned, classifier)
        file = genned.output.get().asFile
        entries.put("$info.art.group:$info.art.name:$info.art.classifier", "$file.sha256\t$info.name\t$info.path")
        
        try (def zip = new ZipFile(serverBundle.get().asFile)) {
            def entry = zip.getEntry('META-INF/libraries.list')
            def data = zip.getInputStream(entry).text.split('\n')
            for (def line : data) {
                def (sha, artifact, path) = line.split('\t')
                def (group, name, other) = artifact.split(':', 3)
                //println("Group: $group Name: $name")
                def key = "$group:$name"
                if (!entries.containsKey(key))
                    entries.put(key, line)
            }
        }
        
        output.get().asFile.text = entries.values().join('\n')
    }
}
