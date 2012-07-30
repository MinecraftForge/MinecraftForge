package cpw.mods.fml.relauncher;

public class FMLCorePlugin implements IFMLLoadingPlugin
{
    @Override
    public String[] getLibraryRequestClass()
    {
        return new String[] {"cpw.mods.fml.relauncher.CoreFMLLibraries"};
    }

    @Override
    public String[] getASMTransformerClass()
    {
        return new String[] {"cpw.mods.fml.common.asm.ASMTransformer"};
    }
}
