/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class SoundEngineFixTransformer implements IClassTransformer
{
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass)
    {
        if (transformedName.equals("paulscode.sound.Source"))
        {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(basicClass);
            classReader.accept(classNode, 0);

            classNode.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "removed", "Z", null, null)); // adding field 'public boolean removed;'

            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            classNode.accept(writer);
            return writer.toByteArray();
        }
        else if (transformedName.equals("paulscode.sound.Library"))
        {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(basicClass);
            classReader.accept(classNode, 0);

            MethodNode method = null;
            for (MethodNode m : classNode.methods)
            {
                if (m.name.equals("removeSource") && m.desc.equals("(Ljava/lang/String;)V")) // trying to find paulscode.sound.Library.removeSource(String)
                {
                    method = m;
                    break;
                }
            }
            if (method == null)
                throw new RuntimeException("Error processing " + transformedName + " - no removeSource method found");

            AbstractInsnNode referenceNode = null;
            
            for (Iterator<AbstractInsnNode> iterator = method.instructions.iterator(); iterator.hasNext();)
            {
                AbstractInsnNode insn = iterator.next();
                if (insn instanceof MethodInsnNode && ((MethodInsnNode) insn).owner.equals("paulscode/sound/Source") // searching for mySource.cleanup() node (line 1086)
                        && ((MethodInsnNode) insn).name.equals("cleanup"))
                {
                    referenceNode = insn;
                    break;
                }
            }
            
            if(referenceNode != null)
            {
                LabelNode after = (LabelNode) referenceNode.getNext();

                AbstractInsnNode beginning = referenceNode.getPrevious();

                int varIndex = ((VarInsnNode) beginning).var;

                method.instructions.insertBefore(beginning, new VarInsnNode(Opcodes.ALOAD, varIndex)); // adding extra if (mySource.toStream)
                method.instructions.insertBefore(beginning, new FieldInsnNode(Opcodes.GETFIELD, "paulscode/sound/Source", "toStream", "Z"));
                LabelNode elseNode = new LabelNode();
                method.instructions.insertBefore(beginning, new JumpInsnNode(Opcodes.IFEQ, elseNode)); // if fails (else) -> go to mySource.cleanup();

                method.instructions.insertBefore(beginning, new VarInsnNode(Opcodes.ALOAD, varIndex)); // if (mySource.toStream) { mySource.removed = true; }
                method.instructions.insertBefore(beginning, new InsnNode(Opcodes.ICONST_1));
                method.instructions.insertBefore(beginning, new FieldInsnNode(Opcodes.PUTFIELD, "paulscode/sound/Source", "removed", "Z"));

                method.instructions.insertBefore(beginning, new JumpInsnNode(Opcodes.GOTO, after)); // still inside if -> jump to sourceMap.remove( sourcename );

                method.instructions.insertBefore(beginning, elseNode);
            }

            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            classNode.accept(writer);
            return writer.toByteArray();
        }
        else if (transformedName.equals("paulscode.sound.StreamThread"))
        {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(basicClass);
            classReader.accept(classNode, 0);

            MethodNode method = null;
            for (MethodNode m : classNode.methods)
            {
                if (m.name.equals("run") && m.desc.equals("()V")) // trying to find paulscode.sound.StreamThread.run();
                {
                    method = m;
                    break;
                }
            }
            if (method == null)
                throw new RuntimeException("Error processing " + transformedName + " - no run method found");
            
            AbstractInsnNode referenceNode = null;
            
            for (Iterator<AbstractInsnNode> iterator = method.instructions.iterator(); iterator.hasNext();)
            {
                AbstractInsnNode insn = iterator.next();
                if (insn instanceof MethodInsnNode && ((MethodInsnNode) insn).owner.equals("java/util/ListIterator") // searching for 'src = iter.next();' node (line 110)
                        && ((MethodInsnNode) insn).name.equals("next"))
                {
                    referenceNode = insn.getNext().getNext();
                    break;
                }
            }
            
            if(referenceNode != null)
            {
                int varIndex = ((VarInsnNode) referenceNode).var;

                LabelNode after = (LabelNode) referenceNode.getNext();
                method.instructions.insertBefore(after, new VarInsnNode(Opcodes.ALOAD, varIndex)); // add if(removed)
                method.instructions.insertBefore(after, new FieldInsnNode(Opcodes.GETFIELD, "paulscode/sound/Source", "removed", "Z"));
                method.instructions.insertBefore(after, new JumpInsnNode(Opcodes.IFEQ, after));

                // if the source has been marked as removed, clean it up and set the variable to null so it will be removed from the list
                method.instructions.insertBefore(after, new VarInsnNode(Opcodes.ALOAD, varIndex)); // src.cleanup();
                method.instructions.insertBefore(after, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "paulscode/sound/Source", "cleanup", "()V", false));
                method.instructions.insertBefore(after, new InsnNode(Opcodes.ACONST_NULL)); // src = null;
                method.instructions.insertBefore(after, new VarInsnNode(Opcodes.ASTORE, varIndex));
            }

            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            classNode.accept(writer);
            return writer.toByteArray();
        }

        return basicClass;
    }

}
