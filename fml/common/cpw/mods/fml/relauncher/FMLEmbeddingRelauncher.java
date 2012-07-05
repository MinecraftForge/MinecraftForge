package cpw.mods.fml.relauncher;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.Arrays;

import net.minecraft.src.WorldSettings;

public class FMLEmbeddingRelauncher
{
    private static FMLEmbeddingRelauncher INSTANCE;
    private RelaunchClassLoader clientLoader;

    public static void relaunch(ArgsWrapper wrap)
    {
        INSTANCE = new FMLEmbeddingRelauncher();
        INSTANCE.relaunchClient(wrap);
    }

    private FMLEmbeddingRelauncher()
    {
        URLClassLoader ucl = (URLClassLoader)getClass().getClassLoader();

        clientLoader = new RelaunchClassLoader(ucl.getURLs());
    }

    private void relaunchClient(ArgsWrapper wrap)
    {
        Class<? super Object> mcMaster = ReflectionHelper.getClass(getClass().getClassLoader(), "net.minecraft.client.Minecraft");
        // We force minecraft to setup it's homedir very early on so we can inject stuff into it
        Method setupHome = ReflectionHelper.findMethod(mcMaster, null, new String[] { "func_6240_b", "getMinecraftDir", "b"} );
        try
        {
            setupHome.invoke(null);
        }
        catch (Exception e)
        {
            // Hmmm
        }
        File minecraftHome = ReflectionHelper.getPrivateValue(mcMaster, null, "field_6275_Z", "ap", "minecraftDir");

        RelaunchLibraryManager.handleLaunch(minecraftHome, clientLoader);

        // Now we re-inject the home into the "new" minecraft under our control
        Class<? super Object> client = ReflectionHelper.getClass(clientLoader, "net.minecraft.client.Minecraft");
        ReflectionHelper.setPrivateValue(client, null, minecraftHome, "field_6275_Z", "ap", "minecraftDir");

        try
        {
            ReflectionHelper.findMethod(client, null, new String[] { "fmlReentry" }, ArgsWrapper.class).invoke(null, wrap);
        }
        catch (Exception e)
        {
            // Hmmm
        }
    }
}
