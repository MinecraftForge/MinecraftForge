package cpw.mods.fml.relauncher;

import java.io.File;
import java.net.URLClassLoader;
import java.util.logging.Level;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

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
        Launch.main(args);
    }

}
