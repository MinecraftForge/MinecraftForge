package cpw.mods.fml.common;

import java.io.File;
import java.util.regex.Pattern;

import org.objectweb.asm.Type;

import cpw.mods.fml.common.discovery.asm.ASMModParser;
import cpw.mods.fml.common.discovery.asm.ModAnnotation;
import cpw.mods.fml.common.modloader.ModLoaderModContainer;

public class ModContainerFactory
{
    private static Pattern modClass = Pattern.compile(".*(\\.|)(mod\\_[^\\s$]+)$");
    private static ModContainerFactory INSTANCE = new ModContainerFactory();
    public static ModContainerFactory instance() {
        return INSTANCE;
    }
    public ModContainer build(ASMModParser modParser, File modSource)
    {
        String className = modParser.getASMType().getClassName();
        if (modParser.isBaseMod() && modClass.matcher(className).find())
        {
            FMLLog.fine("Identified a BaseMod type mod %s", className);
            return new ModLoaderModContainer(className, modSource, modParser.getBaseModProperties());
        }

        for (ModAnnotation ann : modParser.getAnnotations())
        {
            if (ann.getASMType().equals(Type.getType(Mod.class)))
            {
                FMLLog.fine("Identified a FMLMod type mod %s", className);
                return new FMLModContainer(className, modSource, ann.getValues());
            }
        }

        return null;
    }
}
