/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.asm;

import java.nio.file.Path;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;

/**
 * Removes the final modifier from fields with the @CapabilityInject annotation, prevents the JITer from in lining them so our runtime replacements can work.
 */
public class CapabilityInjectDefinalize implements ILaunchPluginService {

    private final String CAP        = "Lnet/minecraftforge/common/capabilities/Capability;";       //Don't directly reference this to prevent class loading.
    private final String CAP_INJECT = "Lnet/minecraftforge/common/capabilities/CapabilityInject;"; //Don't directly reference this to prevent class loading.

    @Override
    public String name() {
        return "capability_inject_definalize";
    }

    private static final EnumSet<Phase> YAY = EnumSet.of(Phase.AFTER);
    private static final EnumSet<Phase> NAY = EnumSet.noneOf(Phase.class);

    @Override
    public EnumSet<Phase> handlesClass(Type classType, boolean isEmpty)
    {
        return isEmpty ? NAY : YAY;
    }

    private boolean hasHolder(List<AnnotationNode> lst)
    {
        return lst != null && lst.stream().anyMatch(n -> n.desc.equals(CAP_INJECT));
    }

    @Override
    public int processClassWithFlags(final Phase phase, final ClassNode classNode, final Type classType, final String reason)
    {
        final int flags = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL;
        final AtomicBoolean changed = new AtomicBoolean();
        classNode.fields.stream().filter(f -> ((f.access & flags) == flags) && f.desc.equals(CAP) && hasHolder(f.visibleAnnotations)).forEach(f ->
        {
            int prev = f.access;
            f.access &= ~Opcodes.ACC_FINAL; //Strip final
            f.access |= Opcodes.ACC_SYNTHETIC; //Add Synthetic so we can check in runtime.
            changed.compareAndSet(false, prev != f.access);
        });

        return changed.get() ? ComputeFlags.SIMPLE_REWRITE : ComputeFlags.NO_REWRITE;
    }

}
