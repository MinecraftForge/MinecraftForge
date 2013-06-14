package cpw.mods.fml.relauncher;

import java.io.File;
import java.net.URLClassLoader;
import java.util.logging.Level;

import com.google.common.base.Throwables;

public class ServerLaunchWrapper {

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        new ServerLaunchWrapper().run(args);
    }

    private ServerLaunchWrapper()
    {

    }

    private void run(String[] args)
    {
        File minecraftHome = new File(".");
        FMLRelaunchLog.minecraftHome = minecraftHome;
        FMLRelauncher.logFileNamePattern = "ForgeModLoader-server-%g.log";
        FMLRelauncher.side = "SERVER";
        URLClassLoader ucl = (URLClassLoader) getClass().getClassLoader();

        RelaunchClassLoader classLoader = new RelaunchClassLoader(ucl.getURLs());

        RelaunchLibraryManager.downloadMonitor = new DummyDownloader();
        Class<? super Object> server;
        FMLInjectionData.build(minecraftHome, classLoader);
        FMLRelaunchLog.info("Forge Mod Loader version %s.%s.%s.%s for Minecraft %s loading", FMLInjectionData.major, FMLInjectionData.minor,
                FMLInjectionData.rev, FMLInjectionData.build, FMLInjectionData.mccversion, FMLInjectionData.mcpversion);
        FMLRelaunchLog.info("Java is %s, version %s, running on %s:%s:%s, installed at %s", System.getProperty("java.vm.name"), System.getProperty("java.version"), System.getProperty("os.name"), System.getProperty("os.arch"), System.getProperty("os.version"), System.getProperty("java.home"));
        FMLRelaunchLog.fine("Java classpath at launch is %s", System.getProperty("java.class.path"));
        FMLRelaunchLog.fine("Java library path at launch is %s", System.getProperty("java.library.path"));

        try
        {
            RelaunchLibraryManager.handleLaunch(minecraftHome, classLoader);
        }
        catch (Throwable t)
        {
            throw Throwables.propagate(t);
        }

        server = ReflectionHelper.getClass(classLoader, "net.minecraft.server.MinecraftServer");
        try
        {
            ReflectionHelper.findMethod(server, null, new String[] { "main" }, String[].class).invoke(null, (Object)args);
        }
        catch (Exception t)
        {
            throw Throwables.propagate(t);
        }

    }

}
