package net.minecraftforge.forge.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile

import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import java.net.URL

public class CrowdinTask extends DefaultTask {
    @Input String id = 'minecraft-forge'
    @Input @Optional String key
    @Input boolean json = true
    @OutputFile output = project.file("build/${name}/output.zip")
    @OutputFile export = project.file("build/${name}/export.json")

    CrowdinTask() {
        outputs.upToDateWhen{ false }
    }

    @TaskAction
    def run() {
        if (output.exists())
            output.delete()

        if (key == null)
            return

        // Force an export
        new URL("https://api.crowdin.com/api/project/${id}/export?key=${key}").withInputStream { i ->
            export.withOutputStream { it << i }
        }

        if (!export.text.contains('success')) {
            throw new RuntimeException("Crowdin export failed, see ${export} for more info")
        }

        new URL("https://api.crowdin.com/api/project/${id}/download/all.zip?key=${key}").withInputStream { i ->
            new ZipInputStream(i).withCloseable { zin ->
                output.withOutputStream { out ->
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
