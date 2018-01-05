/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.classloading;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import javax.annotation.Nullable;

/**
 * Transforms the field {@code private static final Enum[] $VALUES} in enums so
 * that it is not considered final and got inlined.
 */
public class EnumValuesTransformer implements IClassTransformer, Opcodes
{
    @Override
    public byte[] transform(String name, String transformedName, @Nullable byte[] basicClass)
    {
        if (basicClass == null)
            return null;
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);

        if ((classNode.access & ACC_ENUM) == 0)
            return basicClass; // We only check enums

        for (FieldNode f : classNode.fields)
        {
            if ((f.name.equals("$VALUES") || f.name.equals("ENUM$VALUES")) && (f.access & ACC_STATIC) != 0)
            {
                f.access &= ~ACC_FINAL;
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }
}
