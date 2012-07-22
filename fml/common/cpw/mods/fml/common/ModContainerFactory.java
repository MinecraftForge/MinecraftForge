package cpw.mods.fml.common;

import java.io.File;

import org.objectweb.asm.Type;

import cpw.mods.fml.common.discovery.asm.ASMModParser;
import cpw.mods.fml.common.discovery.asm.ModAnnotation;
import cpw.mods.fml.common.modloader.ModLoaderModContainer;

public class ModContainerFactory
{
    private static ModContainerFactory INSTANCE = new ModContainerFactory();
    public static ModContainerFactory instance() {
        return INSTANCE;
    }
    public ModContainer build(ASMModParser modParser, File modSource)
    {
        if (modParser.getASMSuperType().equals(Type.getType("BaseMod")))
        {
            return new ModLoaderModContainer(modParser.getASMType().getClassName(), modSource);
        }
        
        for (ModAnnotation ann : modParser.getAnnotations()) 
        {
            if (ann.getASMType().equals(Type.getType(Mod.class)))
            {
                return new FMLModContainer(modParser.getASMType().getClassName(), modSource, ann.getValues());
            }
        }
        
        return null;
    }
}
