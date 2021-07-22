package net.minecraftforge.forge.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.OutputDirectory

import java.nio.file.Files

abstract class DownloadLibraries extends DefaultTask {
	@InputFile abstract RegularFileProperty getInput()
	@OutputDirectory abstract DirectoryProperty getOutput()
	@OutputFile abstract RegularFileProperty getLibrariesOutput()

    DownloadLibraries() {
		output.convention(project.layout.buildDirectory.dir(name))
        librariesOutput.convention(project.layout.buildDirectory.dir(name).map(d -> d.file('libraries.txt')))
	}

    @TaskAction
    def run() {
		Util.init()
		File outputDir = output.get().asFile
        def libraries = new ArrayList()

		def json = input.get().asFile.json().libraries.each { lib ->
		    //TODO: Thread?
			def artifacts = [lib.downloads.artifact] + lib.downloads.get('classifiers', [:]).values()
			artifacts.each{ art -> 
				def target = new File(outputDir, art.path)
				libraries.add(target.absolutePath)
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
        Files.write(librariesOutput.get().asFile.toPath(), libraries)
    }
}
