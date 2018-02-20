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

            classNode.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "removed", "Z", null, null));

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
                if (m.name.equals("removeSource") && m.desc.equals("(Ljava/lang/String;)V"))
                {
                    method = m;
                    break;
                }
            }
            if (method == null)
                throw new RuntimeException("Error processing " + transformedName + " - no removeSource method found");

            for (Iterator<AbstractInsnNode> iterator = method.instructions.iterator(); iterator.hasNext();)
            {
                AbstractInsnNode insn = iterator.next();
                if (insn instanceof MethodInsnNode && ((MethodInsnNode) insn).owner.equals("paulscode/sound/Source")
                        && ((MethodInsnNode) insn).name.equals("cleanup"))
                {
                    LabelNode after = (LabelNode) insn.getNext();

                    AbstractInsnNode beginning = insn.getPrevious();

                    int varIndex = ((VarInsnNode) beginning).var;

                    method.instructions.insertBefore(beginning, new VarInsnNode(Opcodes.ALOAD, varIndex));
                    method.instructions.insertBefore(beginning, new FieldInsnNode(Opcodes.GETFIELD, "paulscode/sound/Source", "toStream", "Z"));
                    LabelNode elseNode = new LabelNode();
                    method.instructions.insertBefore(beginning, new JumpInsnNode(Opcodes.IFEQ, elseNode));

                    method.instructions.insertBefore(beginning, new VarInsnNode(Opcodes.ALOAD, varIndex));
                    method.instructions.insertBefore(beginning, new InsnNode(Opcodes.ICONST_1));
                    method.instructions.insertBefore(beginning, new FieldInsnNode(Opcodes.PUTFIELD, "paulscode/sound/Source", "removed", "Z"));

                    method.instructions.insertBefore(beginning, new JumpInsnNode(Opcodes.GOTO, after));

                    method.instructions.insertBefore(beginning, elseNode);
                    break;
                }
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
                if (m.name.equals("run") && m.desc.equals("()V"))
                {
                    method = m;
                    break;
                }
            }
            if (method == null)
                throw new RuntimeException("Error processing " + transformedName + " - no run method found");

            for (Iterator<AbstractInsnNode> iterator = method.instructions.iterator(); iterator.hasNext();)
            {
                AbstractInsnNode insn = iterator.next();
                if (insn instanceof MethodInsnNode && ((MethodInsnNode) insn).owner.equals("java/util/ListIterator")
                        && ((MethodInsnNode) insn).name.equals("next"))
                {
                    insn = insn.getNext().getNext();

                    int varIndex = ((VarInsnNode) insn).var;

                    LabelNode after = (LabelNode) insn.getNext();
                    method.instructions.insertBefore(after, new VarInsnNode(Opcodes.ALOAD, varIndex));
                    method.instructions.insertBefore(after, new FieldInsnNode(Opcodes.GETFIELD, "paulscode/sound/Source", "removed", "Z"));
                    method.instructions.insertBefore(after, new JumpInsnNode(Opcodes.IFEQ, after));

                    method.instructions.insertBefore(after, new VarInsnNode(Opcodes.ALOAD, varIndex));
                    method.instructions.insertBefore(after, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "paulscode/sound/Source", "cleanup", "()V", false));
                    method.instructions.insertBefore(after, new InsnNode(Opcodes.ACONST_NULL));
                    method.instructions.insertBefore(after, new VarInsnNode(Opcodes.ASTORE, varIndex));
                    break;
                }
            }

            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            classNode.accept(writer);
            return writer.toByteArray();
        }

        return basicClass;
    }

}
