package net.minecraftforge.forge.tasks.checks

import net.minecraftforge.forge.tasks.Util
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Type

import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

abstract class CheckExcs extends CheckTask {

    @InputFile abstract RegularFileProperty getBinary()
    @InputFiles abstract ConfigurableFileCollection getExcs()

    @Override
    void check(Reporter reporter, boolean fix) {
        final Set<String> known = []
        collectKnown(known)

        excs.each { f ->
            final lines = []
            f.eachLine { line ->
                def idx = line.indexOf('#')
                if (idx == 0 || line.isEmpty()) {
                    return
                }

                if (idx != -1) line = line.substring(0, idx - 1)

                if (!line.contains('=')) {
                    reporter.report("Invalid: $line")
                    return
                }

                def (String key, String value) = line.split('=', 2)
                if (!known.contains(key)) {
                    reporter.report("Unknown: $line")
                    return
                }

                String desc = key.split('\\.', 2)[1]
                if (!desc.contains('(')) {
                    reporter.report("Invalid: $line")
                    return
                }
                desc = '(' + desc.split('\\(', 2)[1]

                def (exceptions, String args) = value.contains('|') ? value.split('|', 2) : [value, '']

                if (args.split(',').length !== Type.getArgumentTypes(desc).length) {
                    reporter.report("Invalid: $line")
                    return
                }
                lines.add(line)
            }

            if (fix) f.text = lines.sort().join('\n')
        }
    }

    private void collectKnown(Collection<String> known) {
        binary.get().asFile.withInputStream { i ->
            new ZipInputStream(i).withCloseable { zin ->
                final visitor = new ClassVisitor(Util.ASM_LEVEL) {
                    private String cls
                    @Override
                    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                        this.cls = name
                    }

                    @Override
                    MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                        known.add(this.cls + '.' + name + descriptor)
                        super.visitMethod(access, name, descriptor, signature, exceptions)
                    }
                }
                ZipEntry zein
                while ((zein = zin.nextEntry) !== null) {
                    if (zein.name.endsWith('.class')) {
                        ClassReader reader = new ClassReader(zin)
                        reader.accept(visitor, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES)
                    }
                }
            }
        }
    }
}
