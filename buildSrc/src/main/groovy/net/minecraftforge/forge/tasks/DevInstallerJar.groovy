package net.minecraftforge.forge.tasks

import org.gradle.api.tasks.*
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import groovy.json.JsonSlurper
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

abstract class DevInstallerJar extends DefaultTask {
    @OutputFile abstract RegularFileProperty getOutput()
    @InputFile abstract RegularFileProperty getInput()
    
    DevInstallerJar() {
        output.convention(project.layout.buildDirectory.file('libs/' + project.name + '-' + project.version + '-installer-dev.jar'))
    }
    
    @TaskAction
    protected void exec() {
        try (def zos = new ZipOutputStream(output.get().asFile.newDataOutputStream())) {
            def seen = [] as Set
            def deps = [:] as java.util.TreeMap
            try (def base = new ZipFile(input.get().asFile)) {
                base.entries().each { entry ->
                    zos.putNextEntry(entry)
                    def data = base.getInputStream(entry).bytes
                    zos.write(data)
                    
                    if (entry.name == 'install_profile.json' || entry.name == 'version.json') {
                        def json = new JsonSlurper().parse(data)
                        json.libraries.each { lib ->
                            deps.put(lib.name, lib.downloads.artifact)
                        }
                    }
                    
                    if (entry.name.startsWith('maven/'))
                        seen.add(entry.name.substring(6))
                }
            }
            
            def resolved = project.configurations.installer.resolvedConfiguration.resolvedArtifacts
            
            deps.each { name, info -> 
                if (seen.contains(info.path))
                    return
                
                def art = resolved.find { "${it.moduleVersion.id.group}:${it.moduleVersion.id.name}:${it.moduleVersion.id.version}" == name }
                
                def file = null
                if (art != null) {
                    if (!Util.checkExists(info.url))
                        file = art.file
                } else {
                    // TODO download missing things
                }
                
                if (file != null) {
                    //println('maven/' + info.path)
                    zos.putNextEntry(new ZipEntry('maven/' + info.path))
                    zos.write(file.bytes)
                    seen.add(info.path)
                }
            }
        }
    }
}
