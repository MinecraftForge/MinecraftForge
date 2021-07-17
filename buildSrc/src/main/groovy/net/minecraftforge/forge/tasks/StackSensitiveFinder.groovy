package net.minecraftforge.forge.tasks

import groovy.transform.EqualsAndHashCode

import java.util.HashMap
import java.util.stream.IntStream
import java.util.TreeMap
import java.util.TreeSet

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional

import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode

import static org.objectweb.asm.Opcodes.*

public class StackSensitiveFinder extends BytecodeFinder {
    @Nested
    Map<String, Search> methods = [:] as HashMap //maps non-stack sensitive to stack sensitive
    Map<String, String> methodsReverse = [:] as HashMap //maps non-stack sensitive method to name
    Map<String, Set<StackSensitiveTarget>> targets = [:] as TreeMap //maps non-stack sensitive method to targets

    private static final String ITEMSTACK_DESC = 'Lnet/minecraft/item/ItemStack;'
    private static final String GET_ITEM = 'func_77973_b' //ItemStack#getItem method

    @Override
    protected pre() {
        //methods.each{ k,v -> logger.lifecycle("Methods: " + k + ' = ' + v) }
    }

    @Override
    protected process(ClassNode parent, MethodNode node) {
        def parentInstance = new ObjectTarget(owner: parent.name, name: '', desc: '')
        def lastItemStack = null
        def startLabel = IntStream.range(0, node.instructions.size())
            .mapToObj({ i -> node.instructions.get(i) })
            .filter({ n -> n.getType() == AbstractInsnNode.LABEL })
            .findFirst()
            .orElse(null)

        for (int x = 0; x < node.instructions.size(); x++) {
            def current = node.instructions.get(x)
            if (current.getType() == AbstractInsnNode.METHOD_INSN) {
                def target = methods.get(current.name)
                if (target != null) {
                    def instance = new ObjectTarget(owner: parent.name, name: node.name, desc: node.desc)
                    if (target.blacklist == null || (!target.blacklist.contains(instance) && !target.blacklist.contains(parentInstance))) {
                        if (lastItemStack == null && node.localVariables != null && startLabel != null) {
                            //no call to 'ItemStack#getItem' found, check if 'this' is an itemStack or if there is an ItemStack parameter
                            lastItemStack = node.localVariables.stream()
                                .filter({ n -> n.desc.equals(ITEMSTACK_DESC) })
                                .filter({ n -> n.start == startLabel || n.name.equals('this') }) //only keep ItemStack parameter and 'this' if its an ItemStack
                                .findFirst()
                                .orElse(null)
                        }

                        if (lastItemStack != null) {
                            targets.computeIfAbsent(current.name, { k -> new TreeSet() }).add(new StackSensitiveTarget(owner: parent.name, name: node.name, desc: node.desc, varIndex: lastItemStack.index))
                        }else {
                            logger.warn('no ItemStack available: ' + parent.name + '.' + node.name + node.desc)
                        }
                    }
                }else if (current.name.equals(GET_ITEM)) {
                    def lastIns = node.instructions.get(x - 1)
                    if (lastIns.getType() == AbstractInsnNode.VAR_INSN && node.localVariables != null) {
                        //Check if pushed var is itemStack, than save it
                        lastItemStack = node.localVariables.stream()
                            .filter({ n -> n.index == lastIns.var })
                            .filter({ n -> n.desc.equals(ITEMSTACK_DESC) })
                            .findFirst()
                            .orElse(lastItemStack)
                    }
                }
            }
        }
    }

    @Override
    protected Object getData() {
        def ret = [:] as HashMap
        targets.forEach { k, v ->
            def e = methods.get(k)
            ret[methodsReverse.get(k)] = [
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
        String name //The non-stack sensitive method

        @Input
        String cls //The class containing the stack sensitive method

        @Input
        String replacement //The stack sensitive method

        @Nested
        @Optional
        Set<ObjectTarget> blacklist

        @Override
        String toString() {
            return name + ' -> ' + cls + '.' + replacement
        }

        def blacklist(def owner, def name, def desc) {
            if (blacklist == null) {
                blacklist = new HashSet()
            }
            blacklist.add(new ObjectTarget(owner: owner, name: name, desc: desc))
        }
        def blacklist(def owner) {
            blacklist(owner, '', '')
        }

    }

    private static class StackSensitiveTarget extends ObjectTarget {

        @Input
        int varIndex;

    }

    def methods(Closure cl) {
        new ClosureHelper(cl, { name, ccl ->
            def search = ClosureHelper.apply(new Search(), ccl)
            search.blacklist(search.cls) //dont redirect stack sensitive method implementation
            this.methods.put(search.name, search)
            this.methodsReverse.put(search.name, name)
        })
    }

}
