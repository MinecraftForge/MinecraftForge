package net.minecraftforge.forge.tasks

import java.util.ArrayList
import java.util.TreeMap

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import org.objectweb.asm.Opcodes

public class CheckATs extends DefaultTask {
    @InputFile File inheritance
    @InputFiles File[] ats

    @TaskAction
    protected void exec() {
        Util.init()
        def parse = { line ->
            def idx = line.indexOf('#')
            def comment = idx == -1 ? null : line.substring(idx)
            if (idx != -1) line = line.substring(0, idx - 1)
            def (modifier, cls, desc) = (line.trim() + '     ').split(' ', -1)
            def key = cls + (desc.isEmpty() ? '' : ' ' + desc)
            return [modifier, cls, desc, comment, key]
        }
        def accessLevel = { access ->
            if ((access & Opcodes.ACC_PUBLIC)    != 0) return 3
            if ((access & Opcodes.ACC_PROTECTED) != 0) return 2
            if ((access & Opcodes.ACC_PRIVATE)   != 0) return 0
            return 1
        }
        def accessStr = { access ->
            if (access.endsWith('-f') || access.endsWith('+f'))
                return 4
            switch (access.toLowerCase()) {
                case 'public':    return 3
                case 'protected': return 2
                case 'default':   return 1
                case 'private':   return 0
                default:          return -1
            }
        }
        def json = inheritance.json()

        ats.each { f ->
            TreeMap lines = [:]
            def group = null
            for (def line : f.readLines()) {
                if (line.isEmpty()) continue
                if (line.startsWith('#group ')) {
                    def (modifier, cls, desc, comment, key) = parse.call(line.substring(7))

                    if (!desc.equals('*') && !desc.equals('*()') && !desc.equals('<init>'))
                        throw new IllegalStateException('Invalid group: ' + line)

                    group = [modifier: modifier, cls: cls, desc: desc, comment: comment,
                        'existing': [] as Set,
                        'children': [] as TreeSet,
                        group: true
                    ]
                    if (lines.containsKey(key))
                        throw new IllegalStateException('Duplicate group: ' + line)

                    lines[key] = group
                } else if (group != null) {
                    if (line.startsWith('#endgroup')) {
                        group = null
                    } else {
                        def (modifier, cls, desc, comment, key) = parse.call(line)
                        group['existing'].add(key)
                    }
                } else if (line.startsWith('#endgroup')) {
                    throw new IllegalStateException('Invalid group ending: ' + line)
                } else if (line.startsWith('#')) {
                    //Nom
                } else {
                    def (modifier, cls, desc, comment, key) = parse.call(line)
                    if (lines.containsKey(key)) {
                        println('Duplicate: ' + line)
                        continue
                    }
                    lines[key] = [modifier: modifier, cls: cls, desc: desc, comment: comment, group: false]
                }
            }

            // Process Groups, this will remove any entries outside the group that is covered by the group
            for (def key : new ArrayList<>(lines.keySet())) {
                def entry = lines.get(key)
                if (entry != null && entry['group']) {
                    def cls = entry['cls']
                    def jcls = json.get(cls.replaceAll('\\.', '/'))
                    if (jcls == null) {
                        lines.remove(key)
                        println('Invalid Group: ' + key)
                    } else if ('*'.equals(entry['desc'])) {
                        if (!jcls.containsKey('fields')) {
                            lines.remove(key)
                            println('Invalid Group, Class has no fields: ' + key)
                        } else {
                            jcls['fields'].each { field, value ->
                                def fkey = cls + ' ' + field
                                if (accessLevel.call(value['access']) < accessStr.call(entry['modifier'])) {
                                    if (lines.containsKey(fkey)) {
                                        lines.remove(fkey)
                                    } else if (!entry['existing'].contains(fkey)) {
                                        println('Added: ' + fkey)
                                    }
                                    entry['children'].add(fkey)
                                } else if (lines.containsKey(fkey)) {
                                    lines.remove(fkey)
                                    println('Removed: ' + fkey)
                                }
                            }
                            entry['existing'].stream().findAll{ !entry['children'].contains(it) }.each{ println('Removed: ' + it) }
                        }
                    } else if ('*()'.equals(entry['desc'])) {
                        if (!jcls.containsKey('methods')) {
                            lines.remove(key)
                            println('Invalid Group, Class has no methods: ' + key)
                        } else {
                            jcls['methods'].each{ mtd, value ->
                                if (mtd.startsWith('<clinit>'))
                                    return
                                key = cls + ' ' + mtd.replace(' ', '')
                                if (accessLevel.call(value['access']) < accessStr.call(entry['modifier'])) {
                                    if (lines.containsKey(key)) {
                                        lines.remove(key)
                                    } else if (!entry['existing'].contains(key)) {
                                        println('Added: ' + key)
                                    }
                                    entry['children'].add(key)
                                } else if (lines.containsKey(key)) {
                                    lines.remove(key)
                                    println('Removed: ' + key)
                                }
                            }
                            entry['existing'].stream().findAll{ !entry['children'].contains(it) }.each{ println('Removed: ' + it) }
                        }
                    } else if ('<init>'.equals(entry['desc'])) { //Make all public non-abstract subclasses
                        json.each{ tcls,value ->
                            if (!value.containsKey('methods') || ((value['access'] & Opcodes.ACC_ABSTRACT) != 0))
                                return
                            def parents = [] as Set
                            def parent = tcls
                            while (parent != null && json.containsKey(parent)) {
                                parents.add(parent)
                                def p = json[parent]
                                parent = p == null ? null : p['superName']
                            }
                            if (parents.contains(cls.replaceAll('\\.', '/'))) {
                                value['methods'].each{ mtd, v ->
                                    if (mtd.startsWith('<init>')) {
                                        def child = tcls.replaceAll('/', '\\.') + ' ' + mtd.replace(' ', '')
                                        if (accessLevel.call(v['access']) < 3) {
                                            if (lines.containsKey(child)) {
                                                lines.remove(child)
                                            } else if (!entry['existing'].contains(child)) {
                                                println('Added: ' + child)
                                            }
                                            entry['children'].add(child)
                                        } else if (lines.containsKey(child)) {
                                            lines.remove(child)
                                            println('Removed: ' + child)
                                        }
                                    }
                                }
                            }
                        }
                        entry['existing'].stream().findAll{ !entry['children'].contains(it) }.each{ println('Removed: ' + it) }
                    }
                }
            }

            // Process normal lines, remove invalid and remove narrowing
            for (def key : new ArrayList<>(lines.keySet())) {
                def entry = lines.get(key)
                if (entry != null && !entry['group']) {
                    def cls = entry['cls']
                    def jcls = json.get(cls.replaceAll('\\.', '/'))
                    if (jcls == null) {
                        lines.remove(key)
                        println('Invalid: ' + key)
                    } else if (entry['desc'] == '') {
                        if (accessLevel.call(jcls['access']) >= accessStr.call(entry['modifier']) && (entry.comment == null || !entry.comment.startsWith('#force '))) {
                            lines.remove(key)
                            println('Invalid Narrowing: ' + key)
                        }
                    } else if (!entry['desc'].contains('(')) {
                        if (!jcls.containsKey('fields') || !jcls['fields'].containsKey(entry['desc'])) {
                            lines.remove(key)
                            println('Invalid: ' + key)
                        } else {
                            def value = jcls['fields'][entry['desc']]
                            if (accessLevel.call(value['access']) >= accessStr.call(entry['modifier']) && (entry.comment == null || !entry.comment.startsWith('#force '))) {
                                lines.remove(key)
                                println('Invalid Narrowing: ' + key)
                                println(entry.comment)
                            }
                        }
                    } else {
                        def jdesc = entry['desc'].replace('(', ' (')
                        if (!jcls.containsKey('methods') || !jcls['methods'].containsKey(jdesc)) {
                            lines.remove(key)
                            println('Invalid: ' + key)
                        } else {
                            def value = jcls['methods'][jdesc]
                            if (accessLevel.call(value['access']) >= accessStr.call(entry['modifier']) && (entry.comment == null || !entry.comment.startsWith('#force '))) {
                                lines.remove(key)
                                println('Invalid Narrowing: ' + key)
                            }
                        }
                    }
                }
            }


            def data = []
            lines.each { key,value ->
                if (!value.group) {
                    data.add(value.modifier + ' ' + key + (value.comment == null ? '' : ' ' + value.comment))
                } else {
                    data.add('#group ' + value.modifier + ' ' + key + (value.comment == null ? '' : ' ' + value.comment))
                    value.children.each{ data.add(value.modifier + ' ' + it) }
                    data.add('#endgroup')
                }
            }
            f.text = data.join('\n')
        }
    }
}
