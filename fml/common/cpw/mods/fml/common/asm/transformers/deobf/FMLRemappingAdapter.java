package cpw.mods.fml.common.asm.transformers.deobf;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.RemappingClassAdapter;

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
}
