/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
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
    public boolean processClass(Phase phase, ClassNode classNode, Type classType)
    {
        final int flags = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL;
        AtomicBoolean changed = new AtomicBoolean();
        classNode.fields.stream().filter(f -> ((f.access & flags) == flags) && f.desc.equals(CAP) && hasHolder(f.visibleAnnotations)).forEach(f ->
        {
            int prev = f.access;
            f.access &= ~Opcodes.ACC_FINAL; //Strip final
            f.access |= Opcodes.ACC_SYNTHETIC; //Add Synthetic so we can check in runtime.
            changed.compareAndSet(false, prev != f.access);
        });

        return changed.get();
    }

}
