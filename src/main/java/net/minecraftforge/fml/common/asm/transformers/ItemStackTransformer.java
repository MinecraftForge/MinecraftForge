package net.minecraftforge.fml.common.asm.transformers;

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

public class ItemStackTransformer implements IClassTransformer {
    private static final String ITEM_TYPE = "Lnet/minecraft/item/Item;";
    private static final String GETITEM_DESC = "()"+ ITEM_TYPE;

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (!"net.minecraft.item.ItemStack".equals(name))
            return basicClass;
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);

        FieldNode itemField = null;
        for (FieldNode f : classNode.fields)
        {
            if (ITEM_TYPE.equals(f.desc) && itemField == null)
            {
                itemField = f;
            }
            else if (ITEM_TYPE.equals(f.desc))
            {
                throw new RuntimeException("Error processing ItemStack - found a duplicate Item field");
            }
        }
        if (itemField == null)
        {
            throw new RuntimeException("Error processing ItemStack - no Item field declared (is the code somehow obfuscated?)");
        }

        MethodNode getItemMethod = null;
        for (MethodNode m: classNode.methods)
        {
            if (GETITEM_DESC.equals(m.desc) && getItemMethod == null)
            {
                getItemMethod = m;
            }
            else if (GETITEM_DESC.equals(m.desc))
            {
                throw new RuntimeException("Error processing ItemStack - duplicate getItem method found");
            }
        }
        if (getItemMethod == null)
        {
            throw new RuntimeException("Error processing ItemStack - no getItem method found (is the code somehow obfuscated?)");
        }

        for (MethodNode m: classNode.methods)
        {
            for (ListIterator<AbstractInsnNode> it = m.instructions.iterator(); it.hasNext(); )
            {
                AbstractInsnNode insnNode = it.next();
                if (insnNode.getType() == AbstractInsnNode.FIELD_INSN)
                {
                    FieldInsnNode fi = (FieldInsnNode)insnNode;
                    if (itemField.name.equals(fi.name) && fi.getOpcode() == Opcodes.GETFIELD)
                    {
                        it.remove();
                        MethodInsnNode replace = new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/item/ItemStack",getItemMethod.name, getItemMethod.desc, false);
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
