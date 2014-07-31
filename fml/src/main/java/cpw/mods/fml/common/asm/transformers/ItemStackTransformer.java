package cpw.mods.fml.common.asm.transformers;

import java.util.ListIterator;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class ItemStackTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (!"net.minecraft.item.ItemStack".equals(name))
            return basicClass;
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
                    if ("field_151002_e".equals(fi.name) && fi.getOpcode() == Opcodes.GETFIELD)
                    {
                        it.remove();
                        MethodInsnNode replace = new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/item/ItemStack","getItem", "()Lnet/minecraft/item/Item;");
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
