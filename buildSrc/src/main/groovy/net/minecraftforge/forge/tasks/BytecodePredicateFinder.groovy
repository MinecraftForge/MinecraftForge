package net.minecraftforge.forge.tasks

import groovy.transform.CompileStatic
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode

@CompileStatic
abstract class BytecodePredicateFinder extends BytecodeFinder {

    @Internal
    abstract Property<Closure<Boolean>> getPredicate()

    private final Set<String> matches = new HashSet()

    @Override
    protected process(ClassNode parent, MethodNode node) {
        for (final current : node.instructions) {
            if (predicate.get().call(parent, node, current)) {
                matches.add(parent.name)
                return
            }
        }
    }

    @Internal
    @Override
    protected Object getData() {
        return matches.toSorted(Comparator.naturalOrder()) ?: {throw new RuntimeException('Failed to find any targets, please ensure that method names and descriptors are correct.')}()
    }
}