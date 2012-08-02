package cpw.mods.fml.common.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import cpw.mods.fml.common.registry.BlockProxy;
import cpw.mods.fml.relauncher.IClassTransformer;

public class ASMTransformer implements IClassTransformer
{
    @Override
    public byte[] transform(String name, byte[] bytes)
    {
        if ("net.minecraft.src.Block".equals(name))
        {
            ClassReader cr = new ClassReader(bytes);
            ClassNode cn = new ClassNode(Opcodes.ASM4);
            cr.accept(cn, ClassReader.EXPAND_FRAMES);
            cn.interfaces.add(Type.getInternalName(BlockProxy.class));
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            cn.accept(cw);
            return cw.toByteArray();
        }
        
        return bytes;
    }

}
