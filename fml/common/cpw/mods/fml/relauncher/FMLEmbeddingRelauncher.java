package cpw.mods.fml.relauncher;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URLClassLoader;

import net.minecraft.client.Minecraft;
import net.minecraft.src.WorldSettings;

public class FMLEmbeddingRelauncher
{
    private static FMLEmbeddingRelauncher INSTANCE;
    private RelaunchClassLoader clientLoader;
    private RelaunchClassLoader serverLoader;

    public static void relaunch(ArgsWrapper wrap)
    {
        INSTANCE = new FMLEmbeddingRelauncher();
        INSTANCE.relaunchClient(wrap);
    }

    private FMLEmbeddingRelauncher()
    {
        URLClassLoader ucl = (URLClassLoader)getClass().getClassLoader();

        clientLoader = new RelaunchClassLoader(ucl.getURLs());
        serverLoader = new RelaunchClassLoader(ucl.getURLs());
    }

    private void relaunchClient(ArgsWrapper wrap)
    {
        try
        {
            Class<?> original = Class.forName("net.minecraft.client.Minecraft", false, getClass().getClassLoader());
            Field origDir = original.getDeclaredField("field_6275_Z");
            origDir.setAccessible(true);
            Class client = Class.forName("net.minecraft.client.Minecraft", false, clientLoader);
            Field homeDir = client.getDeclaredField("field_6275_Z");
            homeDir.setAccessible(true);
            homeDir.set(null, origDir.get(null));
            client.getMethod("fmlReentry", ArgsWrapper.class).invoke(null, wrap);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
