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

package net.minecraftforge.fml.common.asm.transformers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class SideTransformer implements IClassTransformer
{
    private static String SIDE = FMLLaunchHandler.side().name();
    private static final boolean DEBUG = false;
    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes)
    {
        if (bytes == null) { return null; }

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        if (remove(classNode.visibleAnnotations, SIDE))
        {
            if (DEBUG)
            {
                System.out.println(String.format("Attempted to load class %s for invalid side %s", classNode.name, SIDE));
            }
            throw new RuntimeException(String.format("Attempted to load class %s for invalid side %s", classNode.name, SIDE));
        }

        Iterator<FieldNode> fields = classNode.fields.iterator();
        while(fields.hasNext())
        {
            FieldNode field = fields.next();
            if (remove(field.visibleAnnotations, SIDE))
            {
                if (DEBUG)
                {
                    System.out.println(String.format("Removing Field: %s.%s", classNode.name, field.name));
                }
                fields.remove();
            }
        }

        LambdaGatherer lambdaGatherer = new LambdaGatherer();
        Iterator<MethodNode> methods = classNode.methods.iterator();
        while(methods.hasNext())
        {
            MethodNode method = methods.next();
            if (remove(method.visibleAnnotations, SIDE))
            {
                if (DEBUG)
                {
                    System.out.println(String.format("Removing Method: %s.%s%s", classNode.name, method.name, method.desc));
                }
                methods.remove();
                lambdaGatherer.accept(method);
            }
        }

        // remove dynamic lambda methods that are inside of removed methods
        List<Handle> dynamicLambdaHandles = lambdaGatherer.getDynamicLambdaHandles();
        if (!dynamicLambdaHandles.isEmpty())
        {
            methods = classNode.methods.iterator();
            while (methods.hasNext())
            {
                MethodNode method = methods.next();
                for (Handle dynamicLambdaHandle : dynamicLambdaHandles)
                {
                    if (method.name.equals(dynamicLambdaHandle.getName()) && method.desc.equals(dynamicLambdaHandle.getDesc()))
                    {
                        if (DEBUG)
                        {
                            System.out.println(String.format("Removing Method: %s.%s%s", classNode.name, method.name, method.desc));
                        }
                        methods.remove();
                    }
                }
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    private boolean remove(List<AnnotationNode> anns, String side)
    {
        if (anns == null)
        {
            return false;
        }
        for (AnnotationNode ann : anns)
        {
            if (ann.desc.equals(Type.getDescriptor(SideOnly.class)))
            {
                if (ann.values != null)
                {
                    for (int x = 0; x < ann.values.size() - 1; x += 2)
                    {
                        Object key = ann.values.get(x);
                        Object value = ann.values.get(x+1);
                        if (key instanceof String && key.equals("value"))
                        {
                            if (value instanceof String[] )
                            {
                                if (!((String[])value)[1].equals(side))
                                {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private static class LambdaGatherer extends MethodVisitor {
        private static final Handle META_FACTORY = new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory",
                "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;",
                false);
        private final List<Handle> dynamicLambdaHandles = new ArrayList<Handle>();

        public LambdaGatherer() {
            super(Opcodes.ASM5);
        }

        public void accept(MethodNode method) {
            ListIterator<AbstractInsnNode> insnNodeIterator = method.instructions.iterator();
            while (insnNodeIterator.hasNext())
            {
                AbstractInsnNode insnNode = insnNodeIterator.next();
                if (insnNode.getType() == AbstractInsnNode.INVOKE_DYNAMIC_INSN)
                {
                    insnNode.accept(this);
                }
            }
        }

        @Override
        public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs)
        {
            if (META_FACTORY.equals(bsm))
            {
                Handle dynamicLambdaHandle = (Handle) bsmArgs[1];
                dynamicLambdaHandles.add(dynamicLambdaHandle);
            }
        }

        public List<Handle> getDynamicLambdaHandles()
        {
            return dynamicLambdaHandles;
        }
    }
}
