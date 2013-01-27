package cpw.mods.fml.common.asm.transformers;

import org.objectweb.asm.commons.Remapper;

public class FMLDeobfuscatingRemapper extends Remapper {
    public static final FMLDeobfuscatingRemapper INSTANCE = new FMLDeobfuscatingRemapper();

    @Override
    public String mapFieldName(String owner, String name, String desc)
    {
        System.out.println("+++" + owner + "."+name+"."+desc);
        if (owner.indexOf('/')<0)
        {
            return "lexManosWasHere"+name;
        }
        else
        {
            return name;
        }
    }
    @Override
    public String map(String typeName)
    {
        if (typeName.indexOf('/') < 0)
        {
            return "hello/world/"+typeName+"LexManos";
        }
        else
        {
            return typeName;
        }
    }
}
