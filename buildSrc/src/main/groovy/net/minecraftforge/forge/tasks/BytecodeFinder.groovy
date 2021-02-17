package net.minecraftforge.forge.tasks

import groovy.json.JsonBuilder

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodNode

import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

public abstract class BytecodeFinder extends DefaultTask {
    @InputFile File jar
    @OutputFile output = project.file("build/${name}/output.json")
    
    @TaskAction
    protected void exec() {
        Util.init()
        
        if (output.exists())
            output.delete()
            
        pre()
        
        jar.withInputStream { i -> 
            new ZipInputStream(i).withCloseable { zin ->
                ZipEntry zein
                while ((zein = zin.nextEntry) != null) {
                    if (zein.name.endsWith('.class')) {
                        def node = new ClassNode(Opcodes.ASM7)
                        new ClassReader(zin).accept(node, 0)
                        process(node)
                    }
                }
            }
        }
        
        post()
        output.text = new JsonBuilder(getData()).toPrettyString()
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
