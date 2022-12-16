package net.minecraftforge.forge.tasks.checks

import groovy.transform.CompileStatic
import net.minecraftforge.forge.tasks.Annotatable
import net.minecraftforge.forge.tasks.InheritanceData
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles

@CompileStatic
abstract class CheckSAS extends CheckTask {

    @InputFile abstract RegularFileProperty getInheritance()
    @InputFiles abstract ConfigurableFileCollection getSass()

    @Override
    void check(Reporter reporter, boolean fix) {
        final inheritance = InheritanceData.parse(this.inheritance.get().asFile)

        sass.each { f ->
            final lines = []
            f.eachLine { line ->
                if (line[0] == '\t') return // Skip any tabbed lines, those are ones we add
                final idx = line.indexOf('#')
                if (idx == 0 || line.isEmpty()) {
                    lines.add(line)
                    return
                }
                def comment = idx == -1 ? null : line.substring(idx)
                if (idx != -1) line = line.substring(0, idx - 1)
                final spl = (line.trim().replace('(', ' (') + '     ').split(' ', -1)
                def (String cls, String name, String desc) = [spl[0], spl[1], spl[2]]
                cls = cls.replaceAll('\\.', '/')

                if (inheritance[cls] === null) {
                    reporter.report("Invalid: $line")
                } else if (name.isEmpty()) { //Class SAS
                    final toAdd = []
                    final clsSided = isSided(inheritance[cls])
                    boolean sided = clsSided

                    /* TODO: MergeTool doesn't do fields
                    if (json[cls]['fields'] != null) {
                        for (entry in json[cls]['fields']) {
                            if (isSided(entry.value)) {
                                sided = true
                                toAdd.add('\t' + cls + ' ' + entry.key)
                            }
						}
                    } */

                    final clsInh = inheritance[cls]
                    if (clsInh.methods) {
                        for (entry in clsInh.methods) {
                            if (isSided(entry.value)) {
                                sided = true
                                toAdd.add('\t' + cls + ' ' + entry.key.replaceAll(' ', ''))
                                findChildMethods(inheritance, cls, entry.key).each { lines.add('\t' + it) }
                                findChildMethods(inheritance, cls, entry.key).each { println(line + ' -- ' + it) }
                            } else if (clsSided) {
                                findChildMethods(inheritance, cls, entry.key).each { lines.add('\t' + it) }
                                findChildMethods(inheritance, cls, entry.key).each { println(line + ' -- ' + it) }
                            }
                        }
                    }

                    if (sided) {
                        lines.add(cls + (comment == null ? '' : ' ' + comment))
                        lines.addAll(toAdd.sort())
                    } else {
                        reporter.report("Invalid: $line")
                    }

                } else if (desc.isEmpty()) { // Fields
                    /* TODO: MergeTool doesn't do fields
                    if (json[cls]['fields'] != null && isSided(json[cls]['fields'][name]))
                        lines.add(cls + ' ' + name + (comment == null ? '' : ' ' + comment))
                    else */
                    reporter.report("Invalid: $line")
                } else { // Methods
                    final clsInh = inheritance[cls]
                    if (clsInh.methods === null || !isSided(clsInh.methods[name + ' ' + desc]))
                        reporter.report("Invalid: $line")
                    else {
                        lines.add(cls + ' ' + name + desc + (comment == null ? '' : ' ' + comment))
                        findChildMethods(inheritance, cls, name + ' ' + desc).each { println(line + ' -- ' + it) }
                        findChildMethods(inheritance, cls, name + ' ' + desc).each { lines.add('\t' + it) }
                    }
                }
            }

            if (fix) f.text = lines.join('\n')
        }
    }

    protected static isSided(Annotatable annotatable) {
        if (annotatable === null) return false
        for (ann in annotatable.annotations) {
            if ('Lnet/minecraftforge/api/distmarker/OnlyIn;' == ann.desc)
                return true
        }
        return false
    }
    
    protected static findChildMethods(Map<String, InheritanceData> json, String cls, String desc) {
        return json.values().findAll{ it.methods != null && it.methods[desc] != null && it.methods[desc].override == cls && isSided(it.methods[desc]) }
                .collect { it.name + ' ' + desc.replace(' ', '') } as TreeSet
    }
}
