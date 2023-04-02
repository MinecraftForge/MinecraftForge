package net.minecraftforge.forge.tasks

import groovy.json.JsonBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodNode

abstract class BytecodeFinder extends DefaultTask {
    @InputFile abstract RegularFileProperty getJar()
    // It should be fine to mark the output as internal as we want to control when we run it anyways.
    // This also shuts Gradle 8 up about implicit task dependencies.
    @Internal abstract RegularFileProperty getOutput()

    BytecodeFinder() {
        output.convention(project.layout.buildDirectory.dir(name).map { it.file("output.json") })
    }

    @TaskAction
    protected void exec() {
        Util.init()

        def outputFile = output.get().asFile
        if (outputFile.exists())
            outputFile.delete()

        pre()

        Util.processClassNodes(jar.get().asFile, this::process)

        post()
        outputFile.text = new JsonBuilder(getData()).toPrettyString()
    }


    protected process(ClassNode node) {
        if (node.fields != null) node.fields.each { process(node, it) }
        if (node.methods != null) node.methods.each { process(node, it) }
    }

    protected pre() {}
    protected process(ClassNode parent, FieldNode node) {}
    protected process(ClassNode parent, MethodNode node) {}
    protected post() {}
    protected abstract Object getData()
}
