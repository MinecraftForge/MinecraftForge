package cpw.mods.fml.relauncher;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RelaunchClassLoader extends URLClassLoader
{
    private List<URL> sources;

    public RelaunchClassLoader(URL[] sources)
    {
        super(sources);
        this.sources = new ArrayList<URL>(Arrays.asList(sources));
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException
    {
        if (name.startsWith("cpw.mods.fml.relauncher"))
        {
            return getClass().getClassLoader().loadClass(name);
        }
        try
        {
            return findClass(name);
        }
        catch (ClassNotFoundException cnfe)
        {
            return getClass().getClassLoader().loadClass(name);
        }
    }

    @Override
    public void addURL(URL url)
    {
        super.addURL(url);
        sources.add(url);
    }

    public List<URL> getSources()
    {
        return sources;
    }
}
