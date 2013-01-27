package cpw.mods.fml.common.asm.transformers;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.RemappingClassAdapter;
import org.objectweb.asm.tree.ClassNode;

import cpw.mods.fml.relauncher.IClassNameTransformer;
import cpw.mods.fml.relauncher.IClassTransformer;

public class DeobfuscationTransformer implements IClassTransformer, IClassNameTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes)
    {
        System.out.println("***" + name + ":"+transformedName);
        ClassReader classReader = new ClassReader(bytes);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        RemappingClassAdapter remapAdapter = new RemappingClassAdapter(classWriter, FMLDeobfuscatingRemapper.INSTANCE);
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
        if (name.startsWith("hello.world."))
        {
            return name.replace("hello.world.","").replace("LexManos", "");
        }
        else
        {
            return name;
        }
    }

}
