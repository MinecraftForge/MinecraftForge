package net.minecraftforge.forge.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

abstract class Crowdin extends DefaultTask {
    @Input abstract Property<String> getId()
	@Input @Optional abstract Property<String> getKey()
	@Input boolean json = true
	@OutputFile abstract RegularFileProperty getOutput()
	@OutputFile abstract RegularFileProperty getExport()

    Crowdin() {
		outputs.upToDateWhen{ false }
		id.convention('minecraft-forge')
		output.convention(project.layout.buildDirectory.dir(name).map {it.file("output.zip") })
		export.convention(project.layout.buildDirectory.dir(name).map {it.file("export.json") })
	}

    @TaskAction
    def run() {
		File outputFile = output.get().asFile
		File exportFile = export.get().asFile
		if (outputFile.exists())
			outputFile.delete()
		
		if (!key.isPresent())
			return
		String key = this.key.get()
		String id = this.id.get()
		
		// Force an export
		new URL("https://api.crowdin.com/api/project/${id}/export?key=${key}").withInputStream { i ->
			exportFile.withOutputStream { it << i }
		}
		
		if (!exportFile.text.contains('success')) {
			throw new RuntimeException("Crowdin export failed, see ${exportFile} for more info")
		}

		new URL("https://api.crowdin.com/api/project/${id}/download/all.zip?key=${key}").withInputStream { i -> 
			new ZipInputStream(i).withCloseable { zin ->
				outputFile.withOutputStream { out ->
					new ZipOutputStream(out).withCloseable { zout -> 
						ZipEntry zein
						while ((zein = zin.nextEntry) != null) {
							if (zein.isDirectory()) {
								zout.putNextEntry(new ZipEntry(zein.name))
							} else {
								// 1.13+ uses json
								if (zein.name.endsWith('.json') == json) {
									ZipEntry zeout = new ZipEntry(json ? zein.name.toLowerCase() : zein.name)
									zeout.time = 1
									zout.putNextEntry(zeout)
									zout << zin
								}
							}
						}
					}
				}
			}
		}
    }
}
