package net.minecraftforge.forge.tasks

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction

abstract class CheckSAS extends DefaultTask {
	@InputFile abstract RegularFileProperty getInheritance()
	@InputFiles abstract ConfigurableFileCollection getSass()
	
    @TaskAction
    protected void exec() {
		Util.init()
		def json = inheritance.get().asFile.json()
		
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
				def (cls, name, desc) = (line.trim().replace('(', ' (') + '     ').split(' ', -1)
				cls = cls.replaceAll('\\.', '/')

				if (json[cls] == null) {
					println('Invalid: ' + line)
				} else if (name.isEmpty()) { //Class SAS
                    def toAdd = []
                    def clsSided = isSided(json[cls])
                    def sided = clsSided
                    
                    /* TODO: MergeTool doesn't do fields
                    if (json[cls]['fields'] != null) {
						for (entry in json[cls]['fields']) {
                            if (isSided(entry.value)) {
                                sided = true
                                toAdd.add('\t' + cls + ' ' + entry.key)
                            }
						}
                    }
                    */
					if (json[cls]['methods'] != null) {
                        for (entry in json[cls]['methods']) {
                            if (isSided(entry.value)) {
                                sided = true
                                toAdd.add('\t' + cls + ' ' + entry.key.replaceAll(' ', ''))
                                findChildMethods(json, cls, entry.key).each { lines.add('\t' + it) }
                                findChildMethods(json, cls, entry.key).each { println(line + ' -- ' + it) }
                            } else if (clsSided) {
                                findChildMethods(json, cls, entry.key).each { lines.add('\t' + it) }
                                findChildMethods(json, cls, entry.key).each { println(line + ' -- ' + it) }
                            }
						}
                    }
                    
                    if (sided) {
                        lines.add(cls + (comment == null ? '' : ' ' + comment))
                        lines.addAll(toAdd.sort())
                    } else
                        println('Invalid: ' + line)
                    
                } else if (desc.isEmpty()) { // Fields
                    /* TODO: MergeTool doesn't do fields
                    if (json[cls]['fields'] != null && isSided(json[cls]['fields'][name]))
                        lines.add(cls + ' ' + name + (comment == null ? '' : ' ' + comment))
                    else
                    */
                        println('Invalid: ' + line)
                } else { // Methods
                    if (json[cls]['methods'] == null || !isSided(json[cls]['methods'][name + ' ' + desc]))
                        println('Invalid: ' + line)
                    else {
                        lines.add(cls + ' ' + name + desc + (comment == null ? '' : ' ' + comment))
                        findChildMethods(json, cls, name + ' ' + desc).each { println(line + ' -- ' + it) }
                        findChildMethods(json, cls, name + ' ' + desc).each { lines.add('\t' + it) }
                    }
                }
			}
			f.text = lines.join('\n')
		}
	}

    protected static isSided(json) {
        if (json == null || json['annotations'] == null)
            return false
        for (ann in json['annotations']) {
            if ('Lnet/minecraftforge/api/distmarker/OnlyIn;'.equals(ann.desc))
                return true
        }
        return false
    }
    
	protected static findChildMethods(json, cls, desc)
	{
		return json.values().findAll{ it.methods != null && it.methods[desc] != null && it.methods[desc].override == cls && isSided(it.methods[desc]) }
				.collect { it.name + ' ' + desc.replace(' ', '') } as TreeSet
	}
}
