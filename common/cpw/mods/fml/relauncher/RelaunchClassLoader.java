package cpw.mods.fml.relauncher;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

public class RelaunchClassLoader extends URLClassLoader
{
    public RelaunchClassLoader(URL[] sources)
    {
        super(sources);
        System.out.println(Arrays.toString(sources));
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException
    {
        if (name.startsWith("cpw.mods.fml.relauncher"))
        {
            return super.loadClass(name);
        }
        try
        {
            return findClass(name);
        }
        catch (ClassNotFoundException cnfe)
        {
            return super.loadClass(name);
        }
    }

    @Override
    protected void addURL(URL url)
    {
        super.addURL(url);
        System.out.println(Arrays.toString(getURLs()));
    }
}
