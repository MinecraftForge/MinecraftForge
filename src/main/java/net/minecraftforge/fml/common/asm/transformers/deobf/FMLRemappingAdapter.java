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

package net.minecraftforge.fml.common.asm.transformers.deobf;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.RemappingClassAdapter;
import org.objectweb.asm.commons.RemappingMethodAdapter;

public class FMLRemappingAdapter extends RemappingClassAdapter {
    public FMLRemappingAdapter(ClassVisitor cv)
    {
        super(cv, FMLDeobfuscatingRemapper.INSTANCE);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
    {
        if (interfaces == null)
        {
            interfaces = new String[0];
        }
        FMLDeobfuscatingRemapper.INSTANCE.mergeSuperMaps(name, superName, interfaces);
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    protected MethodVisitor createRemappingMethodAdapter(int access, String newDesc, MethodVisitor mv)
    {
        return new StaticFixingMethodVisitor(access, newDesc, mv, remapper);
    }

    private static class StaticFixingMethodVisitor extends RemappingMethodAdapter
    {

        public StaticFixingMethodVisitor(int access, String desc, MethodVisitor mv, Remapper remapper)
        {
            super(access, desc, mv, remapper);
        }

        @Override
        public void visitFieldInsn(int opcode, String originalType, String originalName, String desc)
        {
            // This method solves the problem of a static field reference changing type. In all probability it is a
            // compatible change, however we need to fix up the desc to point at the new type
            String type = remapper.mapType(originalType);
            String fieldName = remapper.mapFieldName(originalType, originalName, desc);
            String newDesc = remapper.mapDesc(desc);
            if (opcode == Opcodes.GETSTATIC && type.startsWith("net/minecraft/") && newDesc.startsWith("Lnet/minecraft/"))
            {
                String replDesc = FMLDeobfuscatingRemapper.INSTANCE.getStaticFieldType(originalType, originalName, type, fieldName);
                if (replDesc != null)
                {
                    newDesc = remapper.mapDesc(replDesc);
                }
            }
            // super.super
            if (mv != null) {
                mv.visitFieldInsn(opcode, type, fieldName, newDesc);
            }
        }
    }
}
