package net.minecraftforge.forge.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*

abstract class ExtractFile extends DefaultTask {
    @InputFile abstract RegularFileProperty getInput()
    @Input abstract Property<String> getTarget()
    @OutputFile abstract RegularFileProperty getOutput()

    @TaskAction
    protected void exec() {
        def zip = new java.util.zip.ZipFile(input.get().asFile);
        def entry = zip.getEntry(target.get())
        if (entry == null)
            throw new IllegalStateException(input.get().asFile.absolutePath + " does not contain " + target.get())
        def stream = zip.getInputStream(entry)
        output.get().asFile.withOutputStream { out ->
            stream.transferTo(out)
        }
    }
}
