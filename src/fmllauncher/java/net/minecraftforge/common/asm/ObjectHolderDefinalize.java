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
 * Removes the final modifier from fields with the @ObjectHolder annotation, prevents the JITer from in lining them so our runtime replacements can work.
 * Will also de-finalize all fields in on class level annotations.
 */
public class ObjectHolderDefinalize implements ILaunchPluginService {

    private final String OBJECT_HOLDER = "Lnet/minecraftforge/registries/ObjectHolder;"; //Don't directly reference this to prevent class loading.

    @Override
    public String name() {
        return "object_holder_definalize";
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
        return lst != null && lst.stream().anyMatch(n -> n.desc.equals(OBJECT_HOLDER));
    }

    private String getValue(List<AnnotationNode> lst)
    {
        AnnotationNode ann = lst.stream().filter(n -> n.desc.equals(OBJECT_HOLDER)).findFirst().get();
        if (ann.values != null)
        {
            for (int x = 0; x < ann.values.size() - 1; x += 2) {
                if (ann.values.get(x).equals("value")) {
                    return (String)ann.values.get(x + 1);
                }
            }
        }
        return null;
    }

    @Override
    public boolean processClass(Phase phase, ClassNode classNode, Type classType)
    {
        AtomicBoolean changes = new AtomicBoolean();
        //Must be public static finals, and non-array objects
        final int flags = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL;

        //Fix Annotated Fields before injecting from class level
        classNode.fields.stream().filter(f -> ((f.access & flags) == flags) && f.desc.startsWith("L") && hasHolder(f.visibleAnnotations)).forEach(f ->
        {
            int prev = f.access;
            f.access &= ~Opcodes.ACC_FINAL; //Strip final
            f.access |= Opcodes.ACC_SYNTHETIC; //Add Synthetic so we can check in runtime. ? Good idea?
            changes.compareAndSet(false, prev != f.access);
        });

        if (hasHolder(classNode.visibleAnnotations)) //Class level, de-finalize all fields and add @ObjectHolder to them!
        {
            @SuppressWarnings("unused")
            String value = getValue(classNode.visibleAnnotations);
            classNode.fields.stream().filter(f -> ((f.access & flags) == flags) && f.desc.startsWith("L")).forEach(f ->
            {
                int prev = f.access;
                f.access &= ~Opcodes.ACC_FINAL;
                f.access |= Opcodes.ACC_SYNTHETIC;
                /*if (!hasHolder(f.visibleAnnotations)) //Add field level annotation, doesn't do anything until after we figure out how ASMDataTable is gatherered
                {
                   if (value == null)
                       f.visitAnnotation(OBJECT_HOLDER, true);
                   else
                       f.visitAnnotation(OBJECT_HOLDER, true).visit("value", value + ":" + f.name.toLowerCase());
                }*/
                changes.compareAndSet(false, prev != f.access);
            });
        }
        return changes.get();
    }

}
