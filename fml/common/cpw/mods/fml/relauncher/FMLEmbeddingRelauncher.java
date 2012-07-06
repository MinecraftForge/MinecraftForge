package cpw.mods.fml.relauncher;

import java.applet.Applet;
import java.applet.AppletStub;
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
    private Object newApplet;
    private Class<? super Object> appletClass;

    public static void relaunch(ArgsWrapper wrap)
    {
        instance().relaunchClient(wrap);
    }

    private static FMLEmbeddingRelauncher instance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new FMLEmbeddingRelauncher();
            System.out.println("FML relaunch active");
        }
        return INSTANCE;
    }

    private FMLEmbeddingRelauncher()
    {
        URLClassLoader ucl = (URLClassLoader)getClass().getClassLoader();

        clientLoader = new RelaunchClassLoader(ucl.getURLs());
    }

    private void relaunchClient(ArgsWrapper wrap)
    {
        File minecraftHome = setupHome();

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

    private File setupHome()
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
        return minecraftHome;
    }

    public static void appletEntry(Applet minecraftApplet)
    {
        instance().relaunchApplet(minecraftApplet);
    }

    private void relaunchApplet(Applet minecraftApplet)
    {
        appletClass = ReflectionHelper.getClass(clientLoader, "net.minecraft.client.MinecraftApplet");
        if (minecraftApplet.getClass().getClassLoader() == clientLoader)
        {
            try
            {
                newApplet = minecraftApplet;
                ReflectionHelper.findMethod(appletClass, newApplet, new String[] {"fmlInitReentry"}).invoke(newApplet);
                return;
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }

        setupHome();

        Class<? super Object> parentAppletClass = ReflectionHelper.getClass(getClass().getClassLoader(), "java.applet.Applet");

        try
        {
            newApplet = appletClass.newInstance();
            Object appletContainer = ReflectionHelper.getPrivateValue(ReflectionHelper.getClass(getClass().getClassLoader(), "java.awt.Component"), minecraftApplet, "parent");

            Class<? super Object> launcherClass = ReflectionHelper.getClass(getClass().getClassLoader(), "net.minecraft.Launcher");
            if (launcherClass.isInstance(appletContainer))
            {
                ReflectionHelper.findMethod(ReflectionHelper.getClass(getClass().getClassLoader(), "java.awt.Container"), minecraftApplet, new String[] { "removeAll" }).invoke(appletContainer);
                ReflectionHelper.findMethod(launcherClass, appletContainer, new String[] { "replace" }, parentAppletClass).invoke(appletContainer, newApplet);
            }
            else
            {
                System.out.printf("Found unknown applet parent %s, unable to inject!\n", launcherClass);
                throw new RuntimeException();
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void appletStart(Applet applet)
    {
        instance().startApplet(applet);
    }

    private void startApplet(Applet applet)
    {
        if (applet.getClass().getClassLoader() == clientLoader)
        {
            try
            {
                ReflectionHelper.findMethod(appletClass, newApplet, new String[] {"fmlStartReentry"}).invoke(newApplet);
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        return;
    }
}
