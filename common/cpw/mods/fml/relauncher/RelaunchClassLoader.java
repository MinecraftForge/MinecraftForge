package cpw.mods.fml.relauncher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class RelaunchClassLoader extends URLClassLoader
{
    // Left behind for CCC/NEI compatibility
    private static String[] excludedPackages = new String[0];
    // Left behind for CCC/NEI compatibility
    private static String[] transformerExclusions = new String[0];

    private List<URL> sources;
    private ClassLoader parent;

    private List<IClassTransformer> transformers;
    private Map<String, Class> cachedClasses;
    private Set<String> invalidClasses;

    private Set<String> classLoaderExceptions = new HashSet<String>();
    private Set<String> transformerExceptions = new HashSet<String>();

    public RelaunchClassLoader(URL[] sources)
    {
        super(sources, null);
        this.sources = new ArrayList<URL>(Arrays.asList(sources));
        this.parent = getClass().getClassLoader();
        this.cachedClasses = new HashMap<String,Class>(1000);
        this.invalidClasses = new HashSet<String>(1000);
        this.transformers = new ArrayList<IClassTransformer>(2);
//        ReflectionHelper.setPrivateValue(ClassLoader.class, null, this, "scl");
        Thread.currentThread().setContextClassLoader(this);

        // standard classloader exclusions
        addClassLoaderExclusion("java.");
        addClassLoaderExclusion("sun.");
        addClassLoaderExclusion("cpw.mods.fml.relauncher.");
        addClassLoaderExclusion("net.minecraftforge.classloading.");

        // standard transformer exclusions
        addTransformerExclusion("javax.");
        addTransformerExclusion("org.objectweb.asm.");
        addTransformerExclusion("com.google.common.");
        addTransformerExclusion("cpw.mods.fml.common.asm.SideOnly");
        addTransformerExclusion("cpw.mods.fml.common.Side");
    }

    public void registerTransformer(String transformerClassName)
    {
        try
        {
            transformers.add((IClassTransformer) loadClass(transformerClassName).newInstance());
        }
        catch (Exception e)
        {
            FMLRelaunchLog.log(Level.SEVERE, e, "A critical problem occured registering the ASM transformer class %s", transformerClassName);
        }
    }
    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException
    {
        if (invalidClasses.contains(name))
        {
            throw new ClassNotFoundException(name);
        }
        // NEI/CCC compatibility code
        if (excludedPackages.length != 0)
        {
            classLoaderExceptions.addAll(Arrays.asList(excludedPackages));
            excludedPackages = new String[0];
        }
        if (transformerExclusions.length != 0)
        {
            transformerExceptions.addAll(Arrays.asList(transformerExclusions));
            transformerExclusions = new String[0];
        }

        for (String st : classLoaderExceptions)
        {
            if (name.startsWith(st))
            {
                return parent.loadClass(name);
            }
        }

        if (cachedClasses.containsKey(name))
        {
            return cachedClasses.get(name);
        }

        for (String st : transformerExceptions)
        {
            if (name.startsWith(st))
            {
                try
                {
                    Class<?> cl = super.findClass(name);
                    cachedClasses.put(name, cl);
                    return cl;
                }
                catch (ClassNotFoundException e)
                {
                    invalidClasses.add(name);
                    throw e;
                }
            }
        }

        try
        {
            int lastDot = name.lastIndexOf('.');
            if (lastDot > -1)
            {
                String pkgname = name.substring(0, lastDot);
                if (getPackage(pkgname)==null)
                {
                    definePackage(pkgname, null, null, null, null, null, null, null);
                }
            }
            byte[] basicClass = getClassBytes(name);
            byte[] transformedClass = runTransformers(name, basicClass);
            Class<?> cl = defineClass(name, transformedClass, 0, transformedClass.length);
            cachedClasses.put(name, cl);
            return cl;
        }
        catch (Throwable e)
        {
            invalidClasses.add(name);
            throw new ClassNotFoundException(name, e);
        }
    }

    public byte[] getClassBytes(String name) throws IOException
    {
        InputStream classStream = null;
        try
        {
            URL classResource = findResource(name.replace('.', '/').concat(".class"));
            if (classResource == null)
            {
                return null;
            }
            classStream = classResource.openStream();
            return readFully(classStream);
        }
        finally
        {
            if (classStream != null)
            {
                try
                {
                    classStream.close();
                }
                catch (IOException e)
                {
                    // Swallow the close exception
                }
            }
        }
    }

    private byte[] runTransformers(String name, byte[] basicClass)
    {
        for (IClassTransformer transformer : transformers)
        {
            basicClass = transformer.transform(name, basicClass);
        }
        return basicClass;
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


    private byte[] readFully(InputStream stream)
    {
        try
        {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(stream.available());
            int r;
            while ((r = stream.read()) != -1)
            {
                bos.write(r);
            }

            return bos.toByteArray();
        }
        catch (Throwable t)
        {
            /// HMMM
            return new byte[0];
        }
    }

    public List<IClassTransformer> getTransformers()
    {
        return Collections.unmodifiableList(transformers);
    }

    private void addClassLoaderExclusion(String toExclude)
    {
        classLoaderExceptions.add(toExclude);
    }

    void addTransformerExclusion(String toExclude)
    {
        transformerExceptions.add(toExclude);
    }
}
