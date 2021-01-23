package net.minecraftforge.forge.tasks

import java.util.ArrayList
import java.util.TreeMap

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import org.objectweb.asm.Opcodes

public class CheckSAS extends DefaultTask {
    @InputFile File inheritance
    @InputFiles File[] sass

    @TaskAction
    protected void exec() {
        Util.init()
        def json = inheritance.json()

        sass.each { f ->
            def lines = []
            f.eachLine { line ->
                if (line[0] == '\t') return //Skip any tabbed lines, those are ones we add
                def idx = line.indexOf('#')
                if (idx == 0 || line.isEmpty()) {
                    lines.add(line)
                    return
                }
                def comment = idx == -1 ? null : line.substring(idx)
                if (idx != -1) line = line.substring(0, idx - 1)
                def (cls, desc) = (line.trim() + '    ').split(' ', -1)
                cls = cls.replaceAll('\\.', '/')
                desc = desc.replace('(', ' (')

                if (json[cls] == null || (!desc.isEmpty() && (json[cls]['methods'] == null || json[cls]['methods'][desc] == null))) {
                    println('Invalid: ' + line)
                    return
                }

                //Class SAS
                if (desc.isEmpty()) {
                    lines.add(cls + (comment == null ? '' : ' ' + comment))
                    if (json[cls]['methods'] != null)
                        (json[cls]['methods'] as TreeMap).each {
                            findChildMethods(json, cls, it.key).each { lines.add('\t' + it) }
                        }
                    return
                }

                //Method SAS
                lines.add(cls + ' ' + desc.replace(' ', '') + (comment == null ? '' : ' ' + comment))
                findChildMethods(json, cls, desc).each { lines.add('\t' + it) }
            }
            f.text = lines.join('\n')
        }
    }

    protected static findChildMethods(json, cls, desc)
    {
        return json.values().findAll{ it.methods != null && it.methods[desc] != null && it.methods[desc].override == cls}
                .collect { it.name + ' ' + desc.replace(' ', '') } as TreeSet
    }
}
