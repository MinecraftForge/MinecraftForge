package cpw.mods.fml.common.asm.transformers;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.RemappingClassAdapter;
import org.objectweb.asm.tree.ClassNode;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import cpw.mods.fml.common.asm.transformers.deobf.FMLRemappingAdapter;
import cpw.mods.fml.relauncher.IClassNameTransformer;
import cpw.mods.fml.relauncher.IClassTransformer;

public class DeobfuscationTransformer implements IClassTransformer, IClassNameTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes)
    {
        System.out.println("***" + name + ":"+transformedName);
        if (bytes == null)
        {
            return null;
        }
        ClassReader classReader = new ClassReader(bytes);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        RemappingClassAdapter remapAdapter = new FMLRemappingAdapter(classWriter);
        classReader.accept(remapAdapter, ClassReader.EXPAND_FRAMES);
        return classWriter.toByteArray();
    }

    @Override
    public String remapClassName(String name)
    {
        return FMLDeobfuscatingRemapper.INSTANCE.map(name.replace('.','/')).replace('/', '.');
    }

    @Override
    public String unmapClassName(String name)
    {
        return FMLDeobfuscatingRemapper.INSTANCE.unmap(name.replace('.', '/')).replace('/','.');
    }

}
