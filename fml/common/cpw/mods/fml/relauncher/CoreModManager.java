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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.logging.Level;

import com.google.common.collect.ObjectArrays;

import net.minecraft.launchwrapper.LaunchClassLoader;

import cpw.mods.fml.common.CertificateHelper;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

public class CoreModManager
{
    private static String[] rootPlugins =  { "cpw.mods.fml.relauncher.FMLCorePlugin" , "net.minecraftforge.classloading.FMLForgePlugin" };
    private static List<String> loadedCoremods = new ArrayList<String>();
    private static Map<IFMLLoadingPlugin, File> pluginLocations;
    private static List<IFMLLoadingPlugin> loadPlugins;
    private static boolean deobfuscatedEnvironment;

    public static void handleLaunch(File mcDir, LaunchClassLoader classLoader)
    {
        try
        {
            // Are we in a 'decompiled' environment?
            byte[] bs = classLoader.getClassBytes("net.minecraft.world.World");
            if (bs != null)
            {
                FMLRelaunchLog.info("Managed to load a deobfuscated Minecraft name- we are in a deobfuscated environment. Skipping runtime deobfuscation");
                deobfuscatedEnvironment = true;
            }
        }
        catch (IOException e1)
        {
        }

        if (!deobfuscatedEnvironment)
        {
            FMLRelaunchLog.fine("Enabling runtime deobfuscation");
        }
        pluginLocations = new HashMap<IFMLLoadingPlugin, File>();
        loadPlugins = new ArrayList<IFMLLoadingPlugin>();
        for (String s : rootPlugins)
        {
            try
            {
                FMLRelaunchLog.finest("Adding coremod for loading %s", s);
                IFMLLoadingPlugin plugin = (IFMLLoadingPlugin) Class.forName(s, true, classLoader).newInstance();
                loadPlugins.add(plugin);
            }
            catch (Exception e)
            {
                // HMMM
            }
        }

        if (loadPlugins.isEmpty())
        {
            throw new RuntimeException("A fatal error has occured - no valid fml load plugin was found - this is a completely corrupt FML installation.");
        }

        FMLRelaunchLog.fine("All core mods are successfully located");
        // Now that we have the root plugins loaded - lets see what else might be around
        String commandLineCoremods = System.getProperty("fml.coreMods.load","");
        for (String s : commandLineCoremods.split(","))
        {
            if (s.isEmpty())
            {
                continue;
            }
            FMLRelaunchLog.info("Found a command line coremod : %s", s);
            try
            {
                classLoader.addTransformerExclusion(s);
                Class<?> coreModClass = Class.forName(s, true, classLoader);
                TransformerExclusions trExclusions = coreModClass.getAnnotation(IFMLLoadingPlugin.TransformerExclusions.class);
                if (trExclusions!=null)
                {
                    for (String st : trExclusions.value())
                    {
                        classLoader.addTransformerExclusion(st);
                    }
                }
                IFMLLoadingPlugin plugin = (IFMLLoadingPlugin) coreModClass.newInstance();
                loadPlugins.add(plugin);
            }
            catch (Throwable e)
            {
                FMLRelaunchLog.log(Level.SEVERE,e,"Exception occured trying to load coremod %s",s);
                throw new RuntimeException(e);
            }
        }
        discoverCoreMods(mcDir, classLoader, loadPlugins);

        for (IFMLLoadingPlugin plug : loadPlugins)
        {
            if (plug.getASMTransformerClass()!=null)
            {
                for (String xformClass : plug.getASMTransformerClass())
                {
                    FMLRelaunchLog.finest("Registering transformer %s", xformClass);
                    classLoader.registerTransformer(xformClass);
                }
            }
        }
        // Deobfuscation transformer, always last
        if (!deobfuscatedEnvironment)
        {
            classLoader.registerTransformer("cpw.mods.fml.common.asm.transformers.DeobfuscationTransformer");
        }
        FMLRelaunchLog.fine("Running coremod plugins");
        Map<String,Object> data = new HashMap<String,Object>();
        data.put("mcLocation", mcDir);
        data.put("coremodList", loadPlugins);
        data.put("runtimeDeobfuscationEnabled", !deobfuscatedEnvironment);
        for (IFMLLoadingPlugin plugin : loadPlugins)
        {
            FMLRelaunchLog.fine("Running coremod plugin %s", plugin.getClass().getSimpleName());
            data.put("coremodLocation", pluginLocations.get(plugin));
            plugin.injectData(data);
            String setupClass = plugin.getSetupClass();
            if (setupClass != null)
            {
                try
                {
                    IFMLCallHook call = (IFMLCallHook) Class.forName(setupClass, true, classLoader).newInstance();
                    Map<String,Object> callData = new HashMap<String, Object>();
                    callData.put("mcLocation", mcDir);
                    callData.put("classLoader", classLoader);
                    callData.put("coremodLocation", pluginLocations.get(plugin));
                    callData.put("deobfuscationFileName", FMLInjectionData.debfuscationDataName());
                    call.injectData(callData);
                    call.call();
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
            FMLRelaunchLog.fine("Coremod plugin %s run successfully", plugin.getClass().getSimpleName());

            String modContainer = plugin.getModContainerClass();
            if (modContainer != null)
            {
                FMLInjectionData.containers.add(modContainer);
            }
        }
        try
        {
            FMLRelaunchLog.fine("Validating minecraft");
            Class<?> loaderClazz = Class.forName("cpw.mods.fml.common.Loader", true, classLoader);
            Method m = loaderClazz.getMethod("injectData", Object[].class);
            m.invoke(null, (Object)FMLInjectionData.data());
            m = loaderClazz.getMethod("instance");
            m.invoke(null);
            FMLRelaunchLog.fine("Minecraft validated, launching...");
        }
        catch (Exception e)
        {
            // Load in the Loader, make sure he's ready to roll - this will initialize most of the rest of minecraft here
            System.out.println("A CRITICAL PROBLEM OCCURED INITIALIZING MINECRAFT - LIKELY YOU HAVE AN INCORRECT VERSION FOR THIS FML");
            throw new RuntimeException(e);
        }
    }

    private static void discoverCoreMods(File mcDir, LaunchClassLoader classLoader, List<IFMLLoadingPlugin> loadPlugins)
    {
        FMLRelaunchLog.fine("Discovering coremods");
        File coreMods = setupCoreModDir(mcDir);
        FilenameFilter ff = new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String name)
            {
                return name.endsWith(".jar");
            }
        };
        File[] coreModList = coreMods.listFiles(ff);
        File versionedModDir = new File(coreMods,FMLInjectionData.mccversion);
        if (versionedModDir.isDirectory())
        {
            File[] versionedCoreMods = versionedModDir.listFiles(ff);
            coreModList = ObjectArrays.concat(coreModList,versionedCoreMods, File.class);
        }

        Arrays.sort(coreModList);

        for (File coreMod : coreModList)
        {
            FMLRelaunchLog.fine("Examining for coremod candidacy %s", coreMod.getName());
            JarFile jar;
            Attributes mfAttributes;
            try
            {
                jar = new JarFile(coreMod);
                if (jar.getManifest() == null)
                {
                    // Not a coremod
                    continue;
                }
                mfAttributes = jar.getManifest().getMainAttributes();
            }
            catch (IOException ioe)
            {
                FMLRelaunchLog.log(Level.SEVERE, ioe, "Unable to read the jar file %s - ignoring", coreMod.getName());
                continue;
            }

            String fmlCorePlugin = mfAttributes.getValue("FMLCorePlugin");
            if (fmlCorePlugin == null)
            {
                // Not a coremod
                continue;
            }

            try
            {
                classLoader.addURL(coreMod.toURI().toURL());
                loadedCoremods.add(coreMod.getName());
            }
            catch (MalformedURLException e)
            {
                FMLRelaunchLog.log(Level.SEVERE, e, "Unable to convert file into a URL. weird");
                continue;
            }
            try
            {
                FMLRelaunchLog.fine("Loading coremod %s", coreMod.getName());
                classLoader.addTransformerExclusion(fmlCorePlugin);
                Class<?> coreModClass = Class.forName(fmlCorePlugin, true, classLoader);
                MCVersion requiredMCVersion = coreModClass.getAnnotation(IFMLLoadingPlugin.MCVersion.class);
                String version = "";
                if (requiredMCVersion == null)
                {
                    FMLRelaunchLog.log(Level.WARNING, "The coremod %s does not have a MCVersion annotation, it may cause issues with this version of Minecraft", fmlCorePlugin);
                }
                else
                {
                    version = requiredMCVersion.value();
                }
                if (!"".equals(version) && !FMLInjectionData.mccversion.equals(version))
                {
                    FMLRelaunchLog.log(Level.SEVERE, "The coremod %s is requesting minecraft version %s and minecraft is %s. It will be ignored.", fmlCorePlugin, version, FMLInjectionData.mccversion);
                    continue;
                }
                else if (!"".equals(version))
                {
                    FMLRelaunchLog.log(Level.FINE, "The coremod %s requested minecraft version %s and minecraft is %s. It will be loaded.", fmlCorePlugin, version, FMLInjectionData.mccversion);
                }
                TransformerExclusions trExclusions = coreModClass.getAnnotation(IFMLLoadingPlugin.TransformerExclusions.class);
                if (trExclusions!=null)
                {
                    for (String st : trExclusions.value())
                    {
                        classLoader.addTransformerExclusion(st);
                    }
                }
                IFMLLoadingPlugin plugin = (IFMLLoadingPlugin) coreModClass.newInstance();
                loadPlugins.add(plugin);
                pluginLocations .put(plugin, coreMod);
                FMLRelaunchLog.fine("Loaded coremod %s", coreMod.getName());
            }
            catch (ClassNotFoundException cnfe)
            {
                FMLRelaunchLog.log(Level.SEVERE, cnfe, "Coremod %s: Unable to class load the plugin %s", coreMod.getName(), fmlCorePlugin);
            }
            catch (ClassCastException cce)
            {
                FMLRelaunchLog.log(Level.SEVERE, cce, "Coremod %s: The plugin %s is not an implementor of IFMLLoadingPlugin", coreMod.getName(), fmlCorePlugin);
            }
            catch (InstantiationException ie)
            {
                FMLRelaunchLog.log(Level.SEVERE, ie, "Coremod %s: The plugin class %s was not instantiable", coreMod.getName(), fmlCorePlugin);
            }
            catch (IllegalAccessException iae)
            {
                FMLRelaunchLog.log(Level.SEVERE, iae, "Coremod %s: The plugin class %s was not accessible", coreMod.getName(), fmlCorePlugin);
            }
        }
    }

    /**
     * @param mcDir the minecraft home directory
     * @return the coremod directory
     */
    private static File setupCoreModDir(File mcDir)
    {
        File coreModDir = new File(mcDir,"mods");
        try
        {
            coreModDir = coreModDir.getCanonicalFile();
        }
        catch (IOException e)
        {
            throw new RuntimeException(String.format("Unable to canonicalize the coremod dir at %s", mcDir.getName()),e);
        }
        if (!coreModDir.exists())
        {
            coreModDir.mkdir();
        }
        else if (coreModDir.exists() && !coreModDir.isDirectory())
        {
            throw new RuntimeException(String.format("Found a coremod file in %s that's not a directory", mcDir.getName()));
        }
        return coreModDir;
    }

    public static List<String> getLoadedCoremods()
    {
        return loadedCoremods;
    }
}
