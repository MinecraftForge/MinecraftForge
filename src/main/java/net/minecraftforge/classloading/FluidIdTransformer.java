package net.minecraftforge.classloading;

import java.util.ListIterator;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import cpw.mods.fml.common.FMLLog;

public class FluidIdTransformer implements IClassTransformer {
    private static final String FLUID_TYPE = "net/minecraftforge/fluids/FluidStack";
    private static final String GETID_NAME = "getFluidID";
    private static final String LEGACY_FIELDNAME = "fluidID";
    private static final String GETID_DESC = "()I";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null)
            return null;
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);

        for (MethodNode m: classNode.methods)
        {
            for (ListIterator<AbstractInsnNode> it = m.instructions.iterator(); it.hasNext(); )
            {
                AbstractInsnNode insnNode = it.next();
                if (insnNode.getType() == AbstractInsnNode.FIELD_INSN)
                {
                    FieldInsnNode fi = (FieldInsnNode)insnNode;
                    if (FLUID_TYPE.equals(fi.owner) && LEGACY_FIELDNAME.equals(fi.name) && fi.getOpcode() == Opcodes.GETFIELD)
                    {
                        FMLLog.fine("Method %s.%s%s: Replacing GETFIELD fluidID with INVOKEVIRTUAL getFluidID", name, m.name, m.desc);
                        it.remove();
                        MethodInsnNode replace = new MethodInsnNode(Opcodes.INVOKEVIRTUAL, FLUID_TYPE, GETID_NAME, GETID_DESC, false);
                        it.add(replace);
                    }
                }
            }
        }
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

}
