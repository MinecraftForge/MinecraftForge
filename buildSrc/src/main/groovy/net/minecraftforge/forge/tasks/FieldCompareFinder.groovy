package net.minecraftforge.forge.tasks

import groovy.transform.EqualsAndHashCode

import java.util.ArrayList
import java.util.HashMap
import java.util.TreeMap
import java.util.TreeSet
import java.util.function.BiConsumer

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*

import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodNode

import static org.objectweb.asm.Opcodes.*

abstract class FieldCompareFinder extends BytecodeFinder {
    @Nested
    Map<String, Search> fields = [:] as HashMap
    @Internal
    Map<Search, String> fieldsReverse = [:] as HashMap
    @Internal
    Map<String, Set<ObjectTarget>> targets = [:] as TreeMap
    
    @Override
    protected pre() {
        //fields.each{ k,v -> logger.lifecycle("Fields: " + k + ' ' + v) }
    }
    
    @Override
    protected process(ClassNode parent, MethodNode node) {
        def last = null
        def parentInstance = new ObjectTarget(owner: parent.name, name: '', desc: '')
        for (int x = 0; x < node.instructions.size(); x++) {
            def current = node.instructions.get(x)
            if (current.opcode == IF_ACMPEQ || current.opcode == IF_ACMPNE) {
                if (last != null && (last.opcode == GETSTATIC || last.opcode == GETFIELD)) {
                    def target = new Search(cls: last.owner, name: last.name)
                    def wanted = fieldsReverse.get(target)
                    def original = fields.get(wanted)
                    def instance = new ObjectTarget(owner: parent.name, name: node.name, desc: node.desc)
                    if (wanted != null && (original.blacklist == null || (!original.blacklist.contains(instance) && !original.blacklist.contains(parentInstance)))) {
                        targets.computeIfAbsent(wanted, { k -> new TreeSet() }).add(instance)
                    }
                }
            }
            last = current
        }
    }
    
    @Internal
    @Override
    protected Object getData() {
		def ret = [:] as HashMap
		targets.forEach{ k, v -> 
			def e = fields.get(k)
			ret[k] = [
				cls: e.cls, 
				name: e.name, 
				replacement: e.replacement, 
				targets: v
			]
		}
        return ret
    }
    
    @EqualsAndHashCode(excludes = ['replacement', 'blacklist'])
    public static class Search {
        @Input
        String cls
        
        @Input
        String name
		
		@Input
		String replacement
    
        @Nested
        @Optional
        Set<ObjectTarget> blacklist
        
        @Override
        String toString() {
            return cls + '.' + name
        }
        
        def blacklist(def owner, def name, def desc) {
            if (blacklist == null)
                blacklist = new HashSet()
            blacklist.add(new ObjectTarget(owner: owner, name: name, desc: desc))
        }
        def blacklist(def owner) {
            blacklist(owner, '', '')
        }
    }
    
    def fields(Closure cl) {
        new ClosureHelper(cl, {name, ccl ->
            def search = ClosureHelper.apply(new Search(), ccl)
            this.fields.put(name, search)
            this.fieldsReverse.put(search, name)
        })
    }
}
