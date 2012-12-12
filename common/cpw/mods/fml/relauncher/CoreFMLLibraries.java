package cpw.mods.fml.relauncher;

public class CoreFMLLibraries implements ILibrarySet
{
    private static String[] libraries = { "argo-2.25.jar","guava-12.0.1.jar","asm-all-4.0.jar", "bcprov-jdk15on-147.jar" };
    private static String[] checksums = { "bb672829fde76cb163004752b86b0484bd0a7f4b", "b8e78b9af7bf45900e14c6f958486b6ca682195f", "98308890597acb64047f7e896638e0d98753ae82", "b6f5d9926b0afbde9f4dbe3db88c5247be7794bb" };

    @Override
    public String[] getLibraries()
    {
        return libraries;
    }

    @Override
    public String[] getHashes()
    {
        return checksums;
    }

    @Override
    public String getRootURL()
    {
        return "http://files.minecraftforge.net/fmllibs/%s";
    }

}
