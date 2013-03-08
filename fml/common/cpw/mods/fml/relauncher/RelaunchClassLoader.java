/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     cpw - implementation
 */

package cpw.mods.fml.relauncher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes.Name;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;

import cpw.mods.fml.common.FMLLog;

public class RelaunchClassLoader extends URLClassLoader
{
    private List<URL> sources;
    private ClassLoader parent;

    private List<IClassTransformer> transformers;
    private Map<String, Class> cachedClasses;
    private Set<String> invalidClasses;

    private Set<String> classLoaderExceptions = new HashSet<String>();
    private Set<String> transformerExceptions = new HashSet<String>();
    private Map<Package,Manifest> packageManifests = new HashMap<Package,Manifest>();
    private IClassNameTransformer renameTransformer;

    private static Manifest EMPTY = new Manifest();

    private ThreadLocal<byte[]> loadBuffer = new ThreadLocal<byte[]>();

    private static final String[] RESERVED = {"CON", "PRN", "AUX", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};

    private static final boolean DEBUG_CLASSLOADING = Boolean.parseBoolean(System.getProperty("fml.debugClassLoading", "false"));

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
        addClassLoaderExclusion("org.lwjgl.");
        addClassLoaderExclusion("cpw.mods.fml.relauncher.");
        addClassLoaderExclusion("net.minecraftforge.classloading.");

        // standard transformer exclusions
        addTransformerExclusion("javax.");
        addTransformerExclusion("argo.");
        addTransformerExclusion("org.objectweb.asm.");
        addTransformerExclusion("com.google.common.");
        addTransformerExclusion("org.bouncycastle.");
        addTransformerExclusion("cpw.mods.fml.common.asm.transformers.deobf.");
    }

    public void registerTransformer(String transformerClassName)
    {
        try
        {
            IClassTransformer transformer = (IClassTransformer) loadClass(transformerClassName).newInstance();
            transformers.add(transformer);
            if (transformer instanceof IClassNameTransformer && renameTransformer == null)
            {
                renameTransformer = (IClassNameTransformer) transformer;
            }
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
            CodeSigner[] signers = null;
            String transformedName = transformName(name);
            String untransformedName = untransformName(name);
            int lastDot = untransformedName.lastIndexOf('.');
            String pkgname = lastDot == -1 ? "" : untransformedName.substring(0, lastDot);
            String fName = untransformedName.replace('.', '/').concat(".class");
            String pkgPath = pkgname.replace('.', '/');
            URLConnection urlConnection = findCodeSourceConnectionFor(fName);
            if (urlConnection instanceof JarURLConnection && lastDot > -1 && !untransformedName.startsWith("net.minecraft."))
            {
                JarURLConnection jarUrlConn = (JarURLConnection)urlConnection;
                JarFile jf = jarUrlConn.getJarFile();
                if (jf != null && jf.getManifest() != null)
                {
                    Manifest mf = jf.getManifest();
                    JarEntry ent = jf.getJarEntry(fName);
                    Package pkg = getPackage(pkgname);
                    getClassBytes(untransformedName);
                    signers = ent.getCodeSigners();
                    if (pkg == null)
                    {
                        pkg = definePackage(pkgname, mf, jarUrlConn.getJarFileURL());
                        packageManifests.put(pkg, mf);
                    }
                    else
                    {
                        if (pkg.isSealed() && !pkg.isSealed(jarUrlConn.getJarFileURL()))
                        {
                            FMLLog.severe("The jar file %s is trying to seal already secured path %s", jf.getName(), pkgname);
                        }
                        else if (isSealed(pkgname, mf))
                        {
                            FMLLog.severe("The jar file %s has a security seal for path %s, but that path is defined and not secure", jf.getName(), pkgname);
                        }
                    }
                }
            }
            else if (lastDot > -1 && !untransformedName.startsWith("net.minecraft."))
            {
                Package pkg = getPackage(pkgname);
                if (pkg == null)
                {
                    pkg = definePackage(pkgname, null, null, null, null, null, null, null);
                    packageManifests.put(pkg, EMPTY);
                }
                else if (pkg.isSealed())
                {
                    FMLLog.severe("The URL %s is defining elements for sealed path %s", urlConnection.getURL(), pkgname);
                }
            }
            byte[] basicClass = getClassBytes(untransformedName);
            byte[] transformedClass = runTransformers(untransformedName, transformedName, basicClass);
            Class<?> cl = defineClass(transformedName, transformedClass, 0, transformedClass.length, new CodeSource(urlConnection.getURL(), signers));
            cachedClasses.put(transformedName, cl);
            return cl;
        }
        catch (Throwable e)
        {
            invalidClasses.add(name);
            if (DEBUG_CLASSLOADING)
            {
                FMLLog.log(Level.FINEST, e, "Exception encountered attempting classloading of %s", name);
            }
            throw new ClassNotFoundException(name, e);
        }
    }

    private String untransformName(String name)
    {
        if (renameTransformer != null)
        {
            return renameTransformer.unmapClassName(name);
        }
        else
        {
            return name;
        }
    }

    private String transformName(String name)
    {
        if (renameTransformer != null)
        {
            return renameTransformer.remapClassName(name);
        }
        else
        {
            return name;
        }
    }

    private boolean isSealed(String path, Manifest man)
    {
        Attributes attr = man.getAttributes(path);
        String sealed = null;
        if (attr != null) {
            sealed = attr.getValue(Name.SEALED);
        }
        if (sealed == null) {
            if ((attr = man.getMainAttributes()) != null) {
                sealed = attr.getValue(Name.SEALED);
            }
        }
        return "true".equalsIgnoreCase(sealed);
    }

    private URLConnection findCodeSourceConnectionFor(String name)
    {
        URL res = findResource(name);
        if (res != null)
        {
            try
            {
                return res.openConnection();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        else
        {
            return null;
        }
    }

    private byte[] runTransformers(String name, String transformedName, byte[] basicClass)
    {
        for (IClassTransformer transformer : transformers)
        {
            basicClass = transformer.transform(name, transformedName, basicClass);
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
            byte[] buf = loadBuffer.get();
            if (buf == null)
            {
                loadBuffer.set(new byte[1 << 12]);
                buf = loadBuffer.get();
            }

            int r, totalLength = 0;
            while ((r = stream.read(buf, totalLength, buf.length - totalLength)) != -1)
            {
                totalLength += r;
                if (totalLength >= buf.length - 1)
                {
                    byte[] oldbuf = buf;
                    buf = new byte[ oldbuf.length + (1 << 12 )];
                    System.arraycopy(oldbuf, 0, buf, 0, oldbuf.length);
                }
            }

            byte[] result = new byte[totalLength];
            System.arraycopy(buf, 0, result, 0, totalLength);
            return result;
        }
        catch (Throwable t)
        {
            FMLRelaunchLog.log(Level.WARNING, t, "Problem loading class");
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

    public byte[] getClassBytes(String name) throws IOException
    {
        if (name.indexOf('.') == -1)
        {
            for (String res : RESERVED)
            {
                if (name.toUpperCase(Locale.ENGLISH).startsWith(res))
                {
                    byte[] data = getClassBytes("_" + name);
                    if (data != null)
                    {
                        return data;
                    }
                }
            }
        }

        InputStream classStream = null;
        try
        {
            URL classResource = findResource(name.replace('.', '/').concat(".class"));
            if (classResource == null)
            {
                if (DEBUG_CLASSLOADING)
                {
                    FMLLog.finest("Failed to find class resource %s", name.replace('.', '/').concat(".class"));
                }
                return null;
            }
            classStream = classResource.openStream();
            if (DEBUG_CLASSLOADING)
            {
                FMLLog.finest("Loading class %s from resource %s", name, classResource.toString());
            }
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
}
