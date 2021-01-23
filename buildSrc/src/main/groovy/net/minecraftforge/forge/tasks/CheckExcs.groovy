package net.minecraftforge.forge.tasks

import java.util.ArrayList
import java.util.TreeMap

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

public class CheckExcs extends DefaultTask {
    @InputFile File binary
    @InputFiles File[] excs

    @TaskAction
    protected void exec() {
        Util.init()
        def known = []
        binary.withInputStream { i ->
            new ZipInputStream(i).withCloseable { zin ->
                def visitor = new ClassVisitor(Opcodes.ASM7) {
                    private String cls
                    @Override
                    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                        this.cls = name
                    }

                    @Override
                    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                        known.add(this.cls + '.' + name + descriptor)
                        super.visitMethod(access, name, descriptor, signature, exceptions)
                    }
                }
                ZipEntry zein
                while ((zein = zin.nextEntry) != null) {
                    if (zein.name.endsWith('.class')) {
                        ClassReader reader = new ClassReader(zin)
                        reader.accept(visitor, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES)
                    }
                }
            }
        }

        excs.each { f ->
            def lines = []
            f.eachLine { line ->
                def idx = line.indexOf('#')
                if (idx == 0 || line.isEmpty()) {
                    return
                }

                def comment = idx == -1 ? null : line.substring(idx)
                if (idx != -1) line = line.substring(0, idx - 1)

                if (!line.contains('=')) {
                    println('Invalid: ' + line)
                    return
                }

                def (key, value) = line.split('=', 2)
                if (!known.contains(key)) {
                    println(key)
                    println('Invalid: ' + line)
                    return
                }

                def (cls, desc) = key.split('\\.', 2)
                if (!desc.contains('(')) {
                    println('Invalid: ' + line)
                    return
                }
                def name = desc.split('\\(', 2)[0]
                desc = '(' + desc.split('\\(', 2)[1]

                def (exceptions, args) = value.contains('|') ? value.split('|', 2) : [value, '']

                if (args.split(',').length != Type.getArgumentTypes(desc).length) {
                    println('Invalid: ' + line)
                    return
                }
                lines.add(line)
            }
            f.text = lines.sort().join('\n')
        }
    }
}
