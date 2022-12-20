package net.minecraftforge.forge.tasks

import org.apache.commons.io.IOUtils
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

abstract class MergeJars extends DefaultTask {
    MergeJars() {
        output.convention(project.layout.buildDirectory.dir(name).map { it.file('output.jar') })
    }

    @TaskAction
    void run() {
        def jars = inputJars.files

        try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(output.get().asFile))) {
            for (def jar : jars) {
                try (ZipInputStream zin = new ZipInputStream(new FileInputStream(jar))) {
                    def entry
                    while ((entry = zin.getNextEntry()) != null) {
                        ZipEntry _new = new ZipEntry(entry.getName())
                        _new.setTime(0) //SHOULD be the same time as the main entry, but NOOOO _new.setTime(entry.getTime()) throws DateTimeException, so you get 0, screw you!
                        zout.putNextEntry(_new)
                        IOUtils.copy(zin, zout)
                    }
                }
            }
        }
    }

    @InputFiles
    abstract ConfigurableFileCollection getInputJars()

    @OutputFile
    abstract RegularFileProperty getOutput()
}
