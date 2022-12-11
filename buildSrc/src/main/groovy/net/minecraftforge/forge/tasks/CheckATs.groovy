package net.minecraftforge.forge.tasks

import net.minecraftforge.srgutils.IMappingFile
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.objectweb.asm.Opcodes

abstract class CheckATs extends CheckTask {

	@InputFile abstract RegularFileProperty getInheritance()
	@InputFiles abstract ConfigurableFileCollection getAts()
    @InputFile @Optional abstract RegularFileProperty getMappings()

	@Override
	void check(Reporter reporter, boolean fix) {
		final mappings = mappings.map { IMappingFile.load(it.asFile) }.getOrNull()
		Util.init()
		final parser = { String line ->
			def idx = line.indexOf('#')
			def comment = idx == -1 ? null : line.substring(idx)
			if (idx != -1) line = line.substring(0, idx - 1)
			def (modifier, cls, desc) = (line.trim() + '     ').split(' ', -1)
			def key = cls + (desc.isEmpty() ? '' : ' ' + desc)
			return [modifier, cls, desc, comment, key]
		}
		def accessLevel = { int access ->
			if ((access & Opcodes.ACC_PUBLIC)    !== 0) return 3
			if ((access & Opcodes.ACC_PROTECTED) !== 0) return 2
			if ((access & Opcodes.ACC_PRIVATE)   !== 0) return 0
			return 1
		}
		def accessStr = { String access ->
			if (access.endsWith('-f') || access.endsWith('+f')) return 4
			switch (access.toLowerCase()) {
				case 'public':    return 3
				case 'protected': return 2
				case 'default':   return 1
				case 'private':   return 0
				default:          return -1
			}
		}
		final json = (Map)inheritance.get().asFile.json()

		ats.each { f ->
			final TreeMap lines = [:]
			def group = null
			for (final line : f.readLines()) {
				if (line.isEmpty()) continue
				if (line.startsWith('#group ')) {
					def (modifier, cls, desc, comment, key) = parser.call(line.substring(7))

					if (desc != '*' && desc != '*()' && desc != '<init>') {
						reporter.report("Invalid group: $line", false)
					}

					group = [
							modifier: modifier, cls: cls, desc: desc, comment: comment,
							existing: [] as Set,
							children: [] as TreeSet,
							group: true
					]
					if (lines.containsKey(key)) {
						reporter.report("Duplicate group: $line", false)
					}

					lines[key] = group
				} else if (group != null) {
					if (line.startsWith('#endgroup')) {
						group = null
					} else {
						final key = parser.call(line)[4]
						group['existing'].add(key)
					}
				} else if (line.startsWith('#endgroup')) {
					reporter.report("Invalid group ending: $line", false)
				} else if (line.startsWith('#')) {
					//Nom
				} else {
					def (modifier, cls, desc, comment, key) = parser.call(line)
					if (lines.containsKey(key)) {
						reporter.report("Found duplicate: $line")
						continue
					}
					lines[key] = [modifier: modifier, cls: cls, desc: desc, comment: comment, group: false]
				}
			}

			final itr = lines.entrySet().iterator()
			final toRemove = []
			while (itr.hasNext()) {
				final next = itr.next()
				def key = next.key
				final entry = (Map)next.value
				if (entry === null) continue

				// Process Groups, this will remove any entries outside the group that is covered by the group
				if (entry['group']) {
					def cls = entry['cls']
					def jcls = json[cls.replaceAll('\\.', '/')]
					if (jcls === null) {
						itr.remove()
						reporter.report("Invalid group: $key")
					} else if ('*' == entry['desc']) {
						if (!jcls.containsKey('fields')) {
							itr.remove()
							reporter.report("Invalid group, class has no fields: $key")
						} else {
							jcls['fields'].each { field, value ->
								def fkey = cls + ' ' + field
								if (accessLevel((int)value['access']) < accessStr((String)entry['modifier'])) {
									if (lines.containsKey(fkey)) {
										toRemove.add(fkey)
									} else if (!entry['existing'].contains(fkey)) {
										println('Added: ' + fkey)
									}
									entry['children'].add(fkey)
								} else if (lines.containsKey(fkey)) {
									toRemove.add(fkey)
									println('Removed: ' + fkey)
								}
							}
							entry['existing'].stream().findAll{ !entry['children'].contains(it) }.each{ println('Removed: ' + it) }
						}
					} else if ('*()' == entry['desc']) {
						if (!jcls.containsKey('methods')) {
							itr.remove()
							reporter.report("Invalid group, class has no methods: $key")
						} else {
							jcls['methods'].each{ String mtd, Map value ->
								if (mtd.startsWith('<clinit>') || mtd.startsWith('lambda$')) return
								key = cls + ' ' + mtd.replace(' ', '')
								if (accessLevel.call((int)value['access']) < accessStr.call((String)entry['modifier'])) {
									if (lines.containsKey(key)) {
										toRemove.add(key)
									} else if (!entry['existing'].contains(key)) {
										println('Added: ' + key)
									}
									entry['children'].add(key)
								} else if (lines.containsKey(key)) {
									toRemove.add(key)
									println('Removed: ' + key)
								}
							}
							entry['existing'].stream().findAll{ !entry['children'].contains(it) }.each{ println('Removed: ' + it) }
						}
					} else if ('<init>' == entry['desc']) { //Make all public non-abstract subclasses
						json.each { tcls,value ->
							if (value !instanceof Map || !value.containsKey('methods') || ((value['access'] & Opcodes.ACC_ABSTRACT) != 0))
								return
							final parents = [] as Set
							def parent = tcls
							while (parent !== null && json.containsKey(parent)) {
								parents.add(parent)
								def p = json[parent]
								parent = p === null ? null : p['superName']
							}
							if (parents.contains(cls.replaceAll('\\.', '/'))) {
								value['methods'].each { String mtd, Map v ->
									if (mtd.startsWith('<init>')) {
										def child = tcls.replaceAll('/', '\\.') + ' ' + mtd.replace(' ', '')
										if (accessLevel.call((int)v['access']) < 3) {
											if (lines.containsKey(child)) {
												toRemove.add(child)
											} else if (child !in entry['existing']) {
												println('Added: ' + child)
											}
											entry['children'].add(child)
										} else if (lines.containsKey(child)) {
											toRemove.add(child)
											println('Removed: ' + child)
										}
									}
								}
							}
						}
						entry['existing'].stream().findAll{ !entry['children'].contains(it) }.each{ println('Removed: ' + it) }
					}
				}

				// Process normal lines, remove invalid and remove narrowing
				else {
					def cls = entry['cls']
					def jcls = json.get(cls.replaceAll('\\.', '/'))
					if (jcls == null) {
						itr.remove()
						reporter.report("Invalid: $key")
					} else if (entry['desc'] == '') {
						if (accessLevel.call((int)jcls['access']) > accessStr.call((String)entry['modifier']) && (entry.comment == null || !entry.comment.startsWith('#force '))) {
							itr.remove()
							reporter.report("Invalid Narrowing: $key")
						}
					} else if (!entry['desc'].contains('(')) {
						if (!jcls.containsKey('fields') || !jcls['fields'].containsKey(entry['desc'])) {
							itr.remove()
							reporter.report("Invalid: $key")
						} else {
							def value = jcls['fields'][(String)entry['desc']]
							if (accessLevel.call((int)value['access']) > accessStr.call((String)entry['modifier']) && (entry.comment == null || !entry.comment.startsWith('#force '))) {
								itr.remove()
								reporter.report("Invalid Narrowing: $key - ${entry.comment}")
							}
						}
					} else {
						def jdesc = entry['desc'].replace('(', ' (')
						if (!jcls.containsKey('methods') || !jcls['methods'].containsKey(jdesc)) {
							itr.remove()
							reporter.report("Invalid: $key")
						} else {
							def value = jcls['methods'][jdesc]
							if (accessLevel.call((int)value['access']) > accessStr.call((String)entry['modifier']) && (entry.comment == null || !entry.comment.startsWith('#force '))) {
								itr.remove()
								reporter.report("Invalid Narrowing: $key")
							}
						}
					}
				}
			}

			toRemove.each(lines.&remove)

			if (fix) {
				def data = []
				def remapComment = { entry ->
					if (!mappings || !entry || !entry.desc) return null
					def comment = entry.comment?.substring(1)?.trim()
					def jsonCls = json.get(entry.cls.replaceAll('\\.', '/'))
					def mappingsClass = mappings?.getClass(jsonCls.name)
					if (!mappingsClass) return entry.comment
					def idx = entry.desc.indexOf('(')
					def mappedName = idx == -1
							? mappingsClass.remapField(entry.desc)
							: mappingsClass.remapMethod(entry.desc.substring(0, idx), entry.desc.substring(idx))
					if (!mappedName) return entry.comment
					if (mappedName == '<init>')
						mappedName = 'constructor'

					if (comment?.startsWith(mappedName))
						return '# ' + comment
					if (comment && comment.indexOf(' ') != -1) {
						def split = comment.split(' - ').toList()
						if (split[0].indexOf(' ') != -1)
						// The first string is more than one word, so append before it
							return "# ${mappedName} - ${comment}"
						split.remove(0)
						return "# ${mappedName} - ${String.join(' - ', split)}"
					}
					return '# ' + mappedName
				}
				lines.each { key, value ->
					if (!value.group) {
						def comment = remapComment.call(value)
						data.add(value.modifier + ' ' + key + (comment ? ' ' + comment : ''))
					} else {
						data.add('#group ' + value.modifier + ' ' + key + (value.comment == null ? '' : ' ' + value.comment))
						value.children.each {
							def line = value.modifier + ' ' + it
							def (modifier, cls, desc, comment) = parser.call(line)
							comment = remapComment.call([modifier: modifier, cls: cls, desc: desc, comment: comment])
							data.add(line + (comment ? ' ' + comment : ''))
						}
						data.add('#endgroup')
					}
				}
				f.text = data.join('\n')
			}
		}
	}
}
