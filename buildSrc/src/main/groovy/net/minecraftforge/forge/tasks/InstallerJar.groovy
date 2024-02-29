package net.minecraftforge.forge.tasks

import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.api.tasks.bundling.Zip
import org.gradle.api.tasks.*
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import groovy.json.JsonSlurper

abstract class InstallerJar extends Zip {
    @Input @Optional abstract Property<Boolean> getFat()
    @Input @Optional abstract Property<Boolean> getOffline()

    InstallerJar() {
        archiveClassifier.set('installer')
        archiveExtension.set('jar') // Needs to be Zip task to not override Manifest, so set extension
        destinationDirectory.set(project.layout.buildDirectory.dir('libs'))
        fat.convention(false)
        offline.convention(false)

        def installerJson = project.tasks.installerJson
        def launcherJson = project.tasks.launcherJson
        def downloadInstaller = project.tasks.downloadInstaller

        dependsOn(installerJson, launcherJson, downloadInstaller, project.configurations.installer)
        from(installerJson, launcherJson)

        from(project.rootProject.file('/src/main/resources/url.png'))
        project.afterEvaluate {
            from(project.zipTree(downloadInstaller.output)) {
                duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            }
            
            if (fat.get() || offline.get()) {
                def cfg = project.tasks.register(name + "Config", Configure)
                cfg.get().configure {
                    parent = this
                    dependsOn(project.tasks.installerJson, project.tasks.launcherJson)
                }
                dependsOn(cfg)
            } else {
                // Things we ALWAYS bundle, this just the server shim jar, because the installer spec only says to extract the file. 
                // I should make it allow downloads but thats a spec break, and this is just a ~14KB jar
                [
                    project.tasks.serverShimJar // Server bootstrap executable jar
                ].forEach { AbstractArchiveTask packed ->
                    def path = Util.getMavenInfoFromTask(packed).path
                    from(packed) {
                        rename { "maven/$path" }
                    }
                }
            }
        }
    }
    
    static abstract class Configure extends DefaultTask {
        public Zip parent
        private int count = 0;
        
        @TaskAction
        protected void exec() {
            def deps = [:] as java.util.TreeMap
            
            // Gather all things that need downloading
            [
                project.tasks.installerJson, 
                project.tasks.launcherJson
            ].each { task ->
                def json = task.output.get().asFile.json
                json.libraries.each { lib -> 
                    if (lib.downloads?.artifact?.url !== null && !lib.downloads.artifact.url.isEmpty())
                        deps.put(lib.name, lib.downloads.artifact)
                }
            }
            
            // First find things we build in this project.
            [
                project.tasks.universalJar, // Forge runtime code
                project.tasks.serverShimJar // Shim jar for dedicated server
            ].forEach { AbstractArchiveTask packed ->
                def name = Util.getMavenInfoFromTask(packed).name
                def info = deps.remove(name)
                if (info !== null) {
                    println("Adding: $packed.path $name")
                    parent.from(packed) {
                        rename { "maven/$info.path" }
                    }
                }
            }
            
            // Find any artifacts from the 'installer' config
            // This config specifies the runtime files we intend for the interaller to have.
            // And are typically what we would be developing and testing alongside Forge. 
            // So we may have local modified versions
            def cfg = project.configurations.installer
            while (cfg != null) {
                //println('')
                def resolved = cfg.resolvedConfiguration.resolvedArtifacts
                int found = 0
                for (def dep : resolved) {
                    def name = Util.getMavenInfoFromDep(dep).name
                    def info = deps.remove(name)
                    if (info == null) {
                        //println("Skipping: $name")
                        continue
                    }
                    //println("-$name")
                    found++
                    addFile(dep.file, info)
                }
                
                if (deps.isEmpty()) {
                    cfg = null
                    continue
                }
                
                // Prevent infinite loops if something fucky happens
                if (found == 0)
                    throw new IllegalStateException("Failed to find any installer dependencies") 
                
                def seen = [] as Set
                cfg = project.configurations.detachedConfiguration()
                cfg.transitive = false
                for (def key : deps.keySet()) {
                    def (group, artifact, other) = key.split(':', 3)
                    // Only resolve unique group:artifact so we don't get version overrides
                    if (seen.add(group + ':' + artifact)) {
                        //println("+$key")
                        cfg.dependencies.add(project.dependencies.create(key))
                    }
                }
            }
        }
        
        void addFile(file, info) {
            boolean pack = parent.offline.get() || info.url.isEmpty()
            
            // If it's a offline jar just always pack
            if (!pack) {
                try {
                    var remote = new URL("${info.url}.sha1").getText('UTF-8')
                    pack = info.sha1 != remote
                } catch (FileNotFoundException e) {
                    pack = !info.url.startsWith('https://libraries.minecraft.net/')
                    // Oh noes its not there!
                }
            }
            
            if (pack) {
                println("Adding: $file.absolutePath")
                parent.from(file) {
                    rename { "maven/$info.path" }
                }
            }
        }
    }
}
