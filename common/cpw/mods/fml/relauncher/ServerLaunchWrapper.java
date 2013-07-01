package cpw.mods.fml.relauncher;

import java.io.File;
import java.lang.reflect.Method;
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
        Class<?> launchwrapper = null;
        try
        {
            launchwrapper = Class.forName("net.minecraft.launchwrapper.Launch",true,getClass().getClassLoader());
            Class.forName("org.objectweb.asm.Type",true,getClass().getClassLoader());
        }
        catch (Exception e)
        {
            System.err.printf("We appear to be missing one or more essential library files.\n" +
            		"You will need to add them to your server before FML and Forge will run successfully.");
            e.printStackTrace(System.err);
            System.exit(1);
        }

        try
        {
            Method main = launchwrapper.getMethod("main", String[].class);
            String[] allArgs = new String[args.length + 2];
            allArgs[0] = "--tweakClass";
            allArgs[1] = "cpw.mods.fml.common.launcher.FMLServerTweaker";
            System.arraycopy(args, 0, allArgs, 2, args.length);
            main.invoke(null,(Object)allArgs);
        }
        catch (Exception e)
        {
            System.err.printf("A problem occurred running the Server launcher.");
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

}
