package net.minecraftforge.forge.tasks

import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor
import net.minecraftforge.srgutils.IMappingFile
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.objectweb.asm.Opcodes

@CompileStatic
abstract class CheckATs extends CheckTask {

	@InputFile abstract RegularFileProperty getInheritance()
	@InputFiles abstract ConfigurableFileCollection getAts()
    @InputFile @Optional abstract RegularFileProperty getMappings()

	@Override
	void check(Reporter reporter, boolean fix) {
		final IMappingFile mappings = mappings.map { RegularFile it -> IMappingFile.load(it.asFile) }.getOrNull()
		final inheritance = InheritanceData.parse(this.inheritance.get().asFile)

		ats.each {
			final lines = process(it, reporter, inheritance)
			if (fix) {
				it.text = joinBack(lines, inheritance, mappings).join('\n')
			}
		}
	}

	private static TreeMap<String, ATParser.Entry> process(File file, Reporter reporter, Map<String, InheritanceData> inheritance) {
		final TreeMap<String, ATParser.Entry> lines = ATParser.parse(file.readLines(), reporter)

		final itr = lines.entrySet().iterator()
		final toRemove = []
		while (itr.hasNext()) {
			final next = itr.next()
			String key = next.key
			final entry = next.value
			if (entry === null) continue

			// Process Groups, this will remove any entries outside the group that is covered by the group
			if (entry.group) {
				final jcls = inheritance[entry.cls.replaceAll('\\.', '/')]
				if (jcls === null) {
					itr.remove()
					reporter.report("Invalid group: $key")
				} else if ('*' == entry['desc']) {
					if (!jcls.fields) {
						itr.remove()
						reporter.report("Invalid group, class has no fields: $key")
					} else {
						jcls.fields.each { field, value ->
							final fkey = entry.cls + ' ' + field
							if (accessLevel(value.access) < accessStr(entry.modifier)) {
								if (lines.containsKey(fkey)) {
									toRemove.add(fkey)
								} else if (!entry.existing.contains(fkey)) {
									println('Added: ' + fkey)
								}
								entry.children.add(fkey)
							} else if (lines.containsKey(fkey)) {
								toRemove.add(fkey)
								println('Removed: ' + fkey)
							}
						}
						entry.existing.findAll { it !in entry.children }.each { println('Removed: ' + it) }
					}
				} else if ('*()' == entry.desc) {
					if (!jcls.methods) {
						itr.remove()
						reporter.report("Invalid group, class has no methods: $key")
					} else {
						jcls.methods.each { mtd, value ->
							if (mtd.startsWith('<clinit>') || mtd.startsWith('lambda$')) return
							key = entry.cls + ' ' + mtd.replace(' ', '')
							if (accessLevel(value.access) < accessStr(entry.modifier)) {
								if (lines.containsKey(key)) {
									toRemove.add(key)
								} else if (!entry.existing.contains(key)) {
									println('Added: ' + key)
								}
								entry.children.add(key)
							} else if (lines.containsKey(key)) {
								toRemove.add(key)
								println('Removed: ' + key)
							}
						}
						entry.existing.findAll { it !in entry.children }.each { println('Removed: ' + it) }
					}
				} else if ('<init>' == entry.desc) { //Make all public non-abstract subclasses
					inheritance.each { tcls, value ->
						if (!value.methods || ((value.access & Opcodes.ACC_ABSTRACT) != 0)) return
						final parents = [] as Set
						String parent = tcls
						while (parent !== null && inheritance.containsKey(parent)) {
							parents.add(parent)
							def p = inheritance[parent]
							parent = p === null ? null : p.superName
						}
						if (parents.contains(entry.cls.replaceAll('\\.', '/'))) {
							value.methods.each { mtd, v ->
								if (mtd.startsWith('<init>')) {
									def child = tcls.replaceAll('/', '\\.') + ' ' + mtd.replace(' ', '')
									if (call(v.access) < 3) {
										if (lines.containsKey(child)) {
											toRemove.add(child)
										} else if (child !in entry.existing) {
											println('Added: ' + child)
										}
										entry.children.add(child)
									} else if (lines.containsKey(child)) {
										toRemove.add(child)
										println('Removed: ' + child)
									}
								}
							}
						}
					}
					entry.existing.findAll { it !in entry.children }.each{ println('Removed: ' + it) }
				}
			}

			// Process normal lines, remove invalid and remove narrowing
			else {
				def jcls = inheritance.get(entry.cls.replaceAll('\\.', '/'))
				if (jcls == null) {
					itr.remove()
					reporter.report("Invalid: $key")
				} else if (entry.desc == '') {
					if (accessLevel(jcls.access) > accessStr(entry.modifier) && (entry.comment == null || !entry.comment.startsWith('#force '))) {
						itr.remove()
						reporter.report("Invalid Narrowing: $key")
					}
				} else if (!entry.desc.contains('(')) {
					if (!jcls.fields || !jcls.fields.containsKey(entry.desc)) {
						itr.remove()
						reporter.report("Invalid: $key")
					} else {
						final value = jcls.fields[entry.desc]
						if (accessLevel(value.access) > accessStr(entry.modifier) && (entry.comment === null || !entry.comment.startsWith('#force '))) {
							itr.remove()
							reporter.report("Invalid Narrowing: $key - ${entry.comment}")
						}
					}
				} else {
					final jdesc = entry.desc.replace('(', ' (')
					if (!jcls.methods || !jcls.methods.containsKey(jdesc)) {
						itr.remove()
						reporter.report("Invalid: $key")
					} else {
						final value = jcls.methods[jdesc]
						if (accessLevel(value.access) > accessStr(entry.modifier) && (entry.comment === null || !entry.comment.startsWith('#force '))) {
							itr.remove()
							reporter.report("Invalid Narrowing: $key")
						}
					}
				}
			}
		}

		toRemove.each(lines.&remove)

		return lines
	}

	private static List<String> joinBack(TreeMap<String, ATParser.Entry> lines, Map<String, InheritanceData> inheritance, IMappingFile mappings) {
		final data = []
		final remapComment = { ATParser.Entry entry ->
			if (!mappings || !entry || !entry.desc) return null
			final comment = entry.comment?.substring(1)?.trim()
			final jsonCls = inheritance.get(entry.cls.replaceAll('\\.', '/'))
			final mappingsClass = mappings?.getClass(jsonCls.name)
			if (mappingsClass === null) return entry.comment
			final idx = entry.desc.indexOf('(')

			String mappedName = idx == -1
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
					final line = value.modifier + ' ' + it
					final entry = ATParser.parseEntry(line)
					final comment = remapComment(entry)
					data.add(line + (comment ? ' ' + comment : ''))
				}
				data.add('#endgroup')
			}
		}
		return data
	}

	static int accessStr(String access) {
		if (access.endsWith('-f') || access.endsWith('+f')) return 4
		switch (access.toLowerCase()) {
			case 'public':    return 3
			case 'protected': return 2
			case 'default':   return 1
			case 'private':   return 0
			default:          return -1
		}
	}

	static int accessLevel(int access) {
		if ((access & Opcodes.ACC_PUBLIC)    !== 0) return 3
		if ((access & Opcodes.ACC_PROTECTED) !== 0) return 2
		if ((access & Opcodes.ACC_PRIVATE)   !== 0) return 0
		return 1
	}
}

@CompileStatic
class ATParser {
	static TreeMap<String, Entry> parse(List<String> lines, CheckTask.Reporter reporter) {
		TreeMap<String, Entry> outLines = new TreeMap<>()
		Entry group = null
		for (final line : lines) {
			if (line.isEmpty()) continue
			if (line.startsWith('#group ')) {
				final entry = parseEntry(line.substring(7))

				if (entry.desc != '*' && entry.desc != '*()' && entry.desc != '<init>') {
					reporter.report("Invalid group: $line", false)
				}

				entry.group = true
				entry.children = []
				entry.existing = []

				group = entry

				if (outLines.containsKey(entry.key)) {
					reporter.report("Duplicate group: $line", false)
				}

				outLines[entry.key] = group
			} else if (group != null) {
				if (line.startsWith('#endgroup')) {
					group = null
				} else {
					final key = parseEntry(line).key
					group.existing.add(key)
				}
			} else if (line.startsWith('#endgroup')) {
				reporter.report("Invalid group ending: $line", false)
			} else if (line.startsWith('#')) {
				//Nom
			} else {
				final entry = parseEntry(line)
				if (outLines.containsKey(entry.key)) {
					reporter.report("Found duplicate: $line")
					continue
				}
				outLines[entry.key] = entry
			}
		}
		return outLines
	}

	static Entry parseEntry(String line) {
		final idx = line.indexOf('#')
		final String comment = idx == -1 ? null : line.substring(idx)
		if (idx != -1) line = line.substring(0, idx - 1)
		final data = (line.trim() + '     ').split(' ', -1)
		new Entry(data[0], data[1], data[2], comment)
	}

	@TupleConstructor
	static final class Entry {
		String modifier, cls, desc, comment

		Set<String> existing
		TreeSet<String> children
		boolean group = false

		@Lazy
		String key = {cls + (desc.isEmpty() ? '' : ' ' + desc)}()
	}
}
