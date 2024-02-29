package net.minecraftforge.forge.tasks

import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*

import java.util.zip.ZipFile

@CompileStatic
abstract class ExtractFile extends DefaultTask {
    @InputFile abstract RegularFileProperty getInput()
    @Input abstract Property<String> getTarget()
    @OutputFile abstract RegularFileProperty getOutput()

    @TaskAction
    protected void exec() {
        var zip = new ZipFile(input.get().asFile);
        var entry = zip.getEntry(target.get())
        if (entry == null)
            throw new IllegalStateException(input.get().asFile.absolutePath + " does not contain " + target.get())
        var stream = zip.getInputStream(entry)
        output.get().asFile.withOutputStream { out ->
            stream.transferTo(out)
        }
    }
}
