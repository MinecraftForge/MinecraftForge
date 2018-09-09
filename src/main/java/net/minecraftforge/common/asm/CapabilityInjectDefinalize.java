/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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
import java.util.List;

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

    @Override public void addResource(Path resource, String name) { }

    @Override public <T> T getExtension() { return null; } // ?

    @Override
    public boolean handlesClass(Type classType, boolean isEmpty)
    {
        return !isEmpty; //Check for annotations?
    }

    private boolean hasHolder(List<AnnotationNode> lst)
    {
        return lst != null && lst.stream().anyMatch(n -> n.desc.equals(CAP_INJECT));
    }

    @Override
    public ClassNode processClass(ClassNode classNode, Type classType)
    {
        final int flags = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL;

        classNode.fields.stream().filter(f -> ((f.access & flags) == flags) && f.desc.equals(CAP) && hasHolder(f.visibleAnnotations)).forEach(f ->
        {
           f.access &= ~Opcodes.ACC_FINAL; //Strip final
           f.access |= Opcodes.ACC_SYNTHETIC; //Add Synthetic so we can check in runtime.
        });

        return classNode;
    }

}
