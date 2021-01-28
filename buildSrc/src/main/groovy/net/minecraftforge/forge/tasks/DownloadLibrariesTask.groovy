package net.minecraftforge.forge.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.OutputDirectory

import java.io.File
import java.util.HashSet
import java.util.Set
import java.net.URL

public class DownloadLibrariesTask extends DefaultTask {
    @InputFile File input
    @OutputDirectory File output = project.file("build/${name}/")
    Set<File> libraries = new HashSet<>()

    @TaskAction
    def run() {
        Util.init()

        def json = input.json().libraries.each { lib ->
            //TODO: Thread?
            def artifacts = [lib.downloads.artifact] + lib.downloads.get('classifiers', [:]).values()
            artifacts.each{ art ->
                def target = new File(output, art.path)
                libraries.add(target)
                if (!target.exists() || !art.sha1.equals(target.sha1())) {
                    project.logger.lifecycle("Downloading ${art.url}")
                    if (!target.parentFile.exists()) {
                        target.parentFile.mkdirs()
                    }
                    new URL(art.url).withInputStream { i ->
                        target.withOutputStream { it << i }
                    }
                    if (!art.sha1.equals(target.sha1())) {
                        throw new IllegalStateException("Failed to download ${art.url} to ${target.canonicalPath} SHA Mismatch")
                    }
                }
            }
        }
    }
}
