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
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.logging.Level;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.ObjectArrays;
import com.google.common.primitives.Ints;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.launcher.FMLInjectionAndSortingTweaker;
import cpw.mods.fml.common.launcher.FMLTweaker;
import cpw.mods.fml.common.toposort.TopologicalSort;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.DependsOn;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.Name;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

public class CoreModManager {
    private static final Attributes.Name COREMODCONTAINSFMLMOD = new Attributes.Name("FMLCorePluginContainsFMLMod");
    private static String[] rootPlugins = { "cpw.mods.fml.relauncher.FMLCorePlugin", "net.minecraftforge.classloading.FMLForgePlugin" };
    private static List<String> loadedCoremods = Lists.newArrayList();
    private static List<FMLPluginWrapper> loadPlugins;
    private static boolean deobfuscatedEnvironment;
    private static FMLTweaker tweaker;
    private static File mcDir;
    private static List<String> reparsedCoremods = Lists.newArrayList();

    private static class FMLPluginWrapper implements ITweaker {
        public final String name;
        public final IFMLLoadingPlugin coreModInstance;
        public final List<String> predepends;
        public final File location;
        public final int sortIndex;

        public FMLPluginWrapper(String name, IFMLLoadingPlugin coreModInstance, File location, int sortIndex, String... predepends)
        {
            super();
            this.name = name;
            this.coreModInstance = coreModInstance;
            this.location = location;
            this.sortIndex = sortIndex;
            this.predepends = Lists.newArrayList(predepends);
        }

        @Override
        public String toString()
        {
            return String.format("%s {%s}", this.name, this.predepends);
        }

        @Override
        public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile)
        {
            // NO OP
        }

        @Override
        public void injectIntoClassLoader(LaunchClassLoader classLoader)
        {
            FMLRelaunchLog.fine("Injecting coremod %s {%s} class transformers", name, coreModInstance.getClass().getName());
            if (coreModInstance.getASMTransformerClass() != null) for (String transformer : coreModInstance.getASMTransformerClass())
            {
                FMLRelaunchLog.finest("Registering transformer %s", transformer);
                classLoader.registerTransformer(transformer);
            }
            FMLRelaunchLog.fine("Injection complete");

            FMLRelaunchLog.fine("Running coremod plugin for %s {%s}", name, coreModInstance.getClass().getName());
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("mcLocation", mcDir);
            data.put("coremodList", loadPlugins);
            data.put("runtimeDeobfuscationEnabled", !deobfuscatedEnvironment);
            FMLRelaunchLog.fine("Running coremod plugin %s", name);
            data.put("coremodLocation", location);
            coreModInstance.injectData(data);
            String setupClass = coreModInstance.getSetupClass();
            if (setupClass != null)
            {
                try
                {
                    IFMLCallHook call = (IFMLCallHook) Class.forName(setupClass, true, classLoader).newInstance();
                    Map<String, Object> callData = new HashMap<String, Object>();
                    callData.put("mcLocation", mcDir);
                    callData.put("classLoader", classLoader);
                    callData.put("coremodLocation", location);
                    callData.put("deobfuscationFileName", FMLInjectionData.debfuscationDataName());
                    call.injectData(callData);
                    call.call();
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
            FMLRelaunchLog.fine("Coremod plugin class %s run successfully", coreModInstance.getClass().getSimpleName());

            String modContainer = coreModInstance.getModContainerClass();
            if (modContainer != null)
            {
                FMLInjectionData.containers.add(modContainer);
            }
        }

        @Override
        public String getLaunchTarget()
        {
            return "";
        }

        @Override
        public String[] getLaunchArguments()
        {
            return new String[0];
        }

    }

    public static void handleLaunch(File mcDir, LaunchClassLoader classLoader, FMLTweaker tweaker)
    {
        CoreModManager.mcDir = mcDir;
        CoreModManager.tweaker = tweaker;
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

        tweaker.injectCascadingTweak("cpw.mods.fml.common.launcher.FMLInjectionAndSortingTweaker");
        try
        {
            classLoader.registerTransformer("cpw.mods.fml.common.asm.transformers.PatchingTransformer");
        }
        catch (Exception e)
        {
            FMLRelaunchLog.log(Level.SEVERE, e, "The patch transformer failed to load! This is critical, loading cannot continue!");
            throw Throwables.propagate(e);
        }

        loadPlugins = new ArrayList<FMLPluginWrapper>();
        for (String rootPluginName : rootPlugins)
        {
            loadCoreMod(classLoader, rootPluginName, new File(FMLTweaker.getJarLocation()));
        }

        if (loadPlugins.isEmpty())
        {
            throw new RuntimeException("A fatal error has occured - no valid fml load plugin was found - this is a completely corrupt FML installation.");
        }

        FMLRelaunchLog.fine("All fundamental core mods are successfully located");
        // Now that we have the root plugins loaded - lets see what else might
        // be around
        String commandLineCoremods = System.getProperty("fml.coreMods.load", "");
        for (String coreModClassName : commandLineCoremods.split(","))
        {
            if (coreModClassName.isEmpty())
            {
                continue;
            }
            FMLRelaunchLog.info("Found a command line coremod : %s", coreModClassName);
            loadCoreMod(classLoader, coreModClassName, null);
        }
        discoverCoreMods(mcDir, classLoader);

    }

    private static void discoverCoreMods(File mcDir, LaunchClassLoader classLoader)
    {
        FMLRelaunchLog.fine("Discovering coremods");
        File coreMods = setupCoreModDir(mcDir);
        FilenameFilter ff = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name)
            {
                return name.endsWith(".jar");
            }
        };
        File[] coreModList = coreMods.listFiles(ff);
        File versionedModDir = new File(coreMods, FMLInjectionData.mccversion);
        if (versionedModDir.isDirectory())
        {
            File[] versionedCoreMods = versionedModDir.listFiles(ff);
            coreModList = ObjectArrays.concat(coreModList, versionedCoreMods, File.class);
        }

        Arrays.sort(coreModList);

        for (File coreMod : coreModList)
        {
            FMLRelaunchLog.fine("Examining for coremod candidacy %s", coreMod.getName());
            JarFile jar = null;
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
            finally
            {
                if (jar != null)
                {
                    try
                    {
                        jar.close();
                    }
                    catch (IOException e)
                    {
                        // Noise
                    }
                }
            }
            String cascadedTweaker = mfAttributes.getValue("TweakClass");
            if (cascadedTweaker != null)
            {
                FMLRelaunchLog.info("Loading tweaker %s from %s", cascadedTweaker, coreMod.getName());
                Integer sortOrder = Ints.tryParse(Strings.nullToEmpty(mfAttributes.getValue("TweakOrder")));
                sortOrder = (sortOrder == null ? Integer.valueOf(0) : sortOrder);
                handleCascadingTweak(coreMod, jar, cascadedTweaker, classLoader, sortOrder);
                loadedCoremods.add(coreMod.getName());
                continue;
            }

            String fmlCorePlugin = mfAttributes.getValue("FMLCorePlugin");
            if (fmlCorePlugin == null)
            {
                // Not a coremod
                FMLRelaunchLog.fine("Not found coremod data in %s", coreMod.getName());
                continue;
            }

            try
            {
                classLoader.addURL(coreMod.toURI().toURL());
                if (!mfAttributes.containsKey(COREMODCONTAINSFMLMOD))
                {
                    FMLRelaunchLog.finest("Adding %s to the list of known coremods, it will not be examined again", coreMod.getName());
                    loadedCoremods.add(coreMod.getName());
                }
                else
                {
                    FMLRelaunchLog.finest("Found FMLCorePluginContainsFMLMod marker in %s, it will be examined later for regular @Mod instances",
                            coreMod.getName());
                    reparsedCoremods.add(coreMod.getName());
                }
            }
            catch (MalformedURLException e)
            {
                FMLRelaunchLog.log(Level.SEVERE, e, "Unable to convert file into a URL. weird");
                continue;
            }
            loadCoreMod(classLoader, fmlCorePlugin, coreMod);
        }
    }

    private static Method ADDURL;

    private static void handleCascadingTweak(File coreMod, JarFile jar, String cascadedTweaker, LaunchClassLoader classLoader, Integer sortingOrder)
    {
        try
        {
            // Have to manually stuff the tweaker into the parent classloader
            if (ADDURL == null)
            {
                ADDURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                ADDURL.setAccessible(true);
            }
            ADDURL.invoke(classLoader.getClass().getClassLoader(), coreMod.toURI().toURL());
            classLoader.addURL(coreMod.toURI().toURL());
            CoreModManager.tweaker.injectCascadingTweak(cascadedTweaker);
            tweakSorting.put(cascadedTweaker,sortingOrder);
        }
        catch (Exception e)
        {
            FMLRelaunchLog.log(Level.INFO, e, "There was a problem trying to load the mod dir tweaker %s", coreMod.getAbsolutePath());
        }
    }

    private static void injectTweakWrapper(FMLPluginWrapper wrapper)
    {
        loadPlugins.add(wrapper);
    }

    /**
     * @param mcDir
     *            the minecraft home directory
     * @return the coremod directory
     */
    private static File setupCoreModDir(File mcDir)
    {
        File coreModDir = new File(mcDir, "mods");
        try
        {
            coreModDir = coreModDir.getCanonicalFile();
        }
        catch (IOException e)
        {
            throw new RuntimeException(String.format("Unable to canonicalize the coremod dir at %s", mcDir.getName()), e);
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

    public static List<String> getReparseableCoremods()
    {
        return reparsedCoremods;
    }

    private static FMLPluginWrapper loadCoreMod(LaunchClassLoader classLoader, String coreModClass, File location)
    {
        String coreModName = coreModClass.substring(coreModClass.lastIndexOf('.') + 1);
        try
        {
            FMLRelaunchLog.fine("Instantiating coremod class %s", coreModName);
            classLoader.addTransformerExclusion(coreModClass);
            Class<?> coreModClazz = Class.forName(coreModClass, true, classLoader);
            Name coreModNameAnn = coreModClazz.getAnnotation(IFMLLoadingPlugin.Name.class);
            if (coreModNameAnn != null && !Strings.isNullOrEmpty(coreModNameAnn.value()))
            {
                coreModName = coreModNameAnn.value();
                FMLRelaunchLog.finest("coremod named %s is loading", coreModName);
            }
            MCVersion requiredMCVersion = coreModClazz.getAnnotation(IFMLLoadingPlugin.MCVersion.class);
            if (!Arrays.asList(rootPlugins).contains(coreModClass) && (requiredMCVersion == null || Strings.isNullOrEmpty(requiredMCVersion.value())))
            {
                FMLRelaunchLog.log(Level.WARNING, "The coremod %s does not have a MCVersion annotation, it may cause issues with this version of Minecraft",
                        coreModClass);
            }
            else if (requiredMCVersion != null && !FMLInjectionData.mccversion.equals(requiredMCVersion.value()))
            {
                FMLRelaunchLog.log(Level.SEVERE, "The coremod %s is requesting minecraft version %s and minecraft is %s. It will be ignored.", coreModClass,
                        requiredMCVersion.value(), FMLInjectionData.mccversion);
                return null;
            }
            else if (requiredMCVersion != null)
            {
                FMLRelaunchLog.log(Level.FINE, "The coremod %s requested minecraft version %s and minecraft is %s. It will be loaded.", coreModClass,
                        requiredMCVersion.value(), FMLInjectionData.mccversion);
            }
            TransformerExclusions trExclusions = coreModClazz.getAnnotation(IFMLLoadingPlugin.TransformerExclusions.class);
            if (trExclusions != null)
            {
                for (String st : trExclusions.value())
                {
                    classLoader.addTransformerExclusion(st);
                }
            }
            DependsOn deplist = coreModClazz.getAnnotation(IFMLLoadingPlugin.DependsOn.class);
            String[] dependencies = new String[0];
            if (deplist != null)
            {
                dependencies = deplist.value();
            }
            SortingIndex index = coreModClazz.getAnnotation(IFMLLoadingPlugin.SortingIndex.class);
            int sortIndex = index != null ? index.value() : 0;

            IFMLLoadingPlugin plugin = (IFMLLoadingPlugin) coreModClazz.newInstance();
            FMLPluginWrapper wrap = new FMLPluginWrapper(coreModName, plugin, location, sortIndex, dependencies);
            loadPlugins.add(wrap);
            FMLRelaunchLog.fine("Enqueued coremod %s", coreModName);
            return wrap;
        }
        catch (ClassNotFoundException cnfe)
        {
            if (!Lists.newArrayList(rootPlugins).contains(coreModClass))
                FMLRelaunchLog.log(Level.SEVERE, cnfe, "Coremod %s: Unable to class load the plugin %s", coreModName, coreModClass);
            else
                FMLRelaunchLog.fine("Skipping root plugin %s", coreModClass);
        }
        catch (ClassCastException cce)
        {
            FMLRelaunchLog.log(Level.SEVERE, cce, "Coremod %s: The plugin %s is not an implementor of IFMLLoadingPlugin", coreModName, coreModClass);
        }
        catch (InstantiationException ie)
        {
            FMLRelaunchLog.log(Level.SEVERE, ie, "Coremod %s: The plugin class %s was not instantiable", coreModName, coreModClass);
        }
        catch (IllegalAccessException iae)
        {
            FMLRelaunchLog.log(Level.SEVERE, iae, "Coremod %s: The plugin class %s was not accessible", coreModName, coreModClass);
        }
        return null;
    }

    private static void sortCoreMods()
    {
        TopologicalSort.DirectedGraph<FMLPluginWrapper> sortGraph = new TopologicalSort.DirectedGraph<FMLPluginWrapper>();
        Map<String, FMLPluginWrapper> pluginMap = Maps.newHashMap();
        for (FMLPluginWrapper plug : loadPlugins)
        {
            sortGraph.addNode(plug);
            pluginMap.put(plug.name, plug);
        }

        for (FMLPluginWrapper plug : loadPlugins)
        {
            for (String dep : plug.predepends)
            {
                if (!pluginMap.containsKey(dep))
                {
                    FMLRelaunchLog.log(Level.SEVERE, "Missing coremod dependency - the coremod %s depends on coremod %s which isn't present.", plug.name, dep);
                    throw new RuntimeException();
                }
                sortGraph.addEdge(plug, pluginMap.get(dep));
            }
        }
        try
        {
            loadPlugins = TopologicalSort.topologicalSort(sortGraph);
            FMLRelaunchLog.fine("Sorted coremod list %s", loadPlugins);
        }
        catch (Exception e)
        {
            FMLLog.log(Level.SEVERE, e, "There was a problem performing the coremod sort");
            throw Throwables.propagate(e);
        }
    }

    public static void injectTransformers(LaunchClassLoader classLoader)
    {

        Launch.blackboard.put("fml.deobfuscatedEnvironment", deobfuscatedEnvironment);
        tweaker.injectCascadingTweak("cpw.mods.fml.common.launcher.FMLDeobfTweaker");
        tweakSorting.put("cpw.mods.fml.common.launcher.FMLDeobfTweaker", Integer.valueOf(1000));
    }

    public static void injectCoreModTweaks(FMLInjectionAndSortingTweaker fmlInjectionAndSortingTweaker)
    {
        List<ITweaker> tweakers = (List<ITweaker>) Launch.blackboard.get("Tweaks");
        // Add the sorting tweaker first- it'll appear twice in the list
        tweakers.add(0, fmlInjectionAndSortingTweaker);
        for (FMLPluginWrapper wrapper : loadPlugins)
        {
            tweakers.add(wrapper);
        }
    }

    private static Map<String,Integer> tweakSorting = Maps.newHashMap();

    public static void sortTweakList()
    {
        List<ITweaker> tweakers = (List<ITweaker>) Launch.blackboard.get("Tweaks");
        Collections.sort(tweakers, new Comparator<ITweaker>() {
            @Override
            public int compare(ITweaker o1, ITweaker o2)
            {
                Integer first = null;
                Integer second = null;
                if (o1 instanceof FMLInjectionAndSortingTweaker)
                {
                    first = Integer.MIN_VALUE;
                }
                if (o2 instanceof FMLInjectionAndSortingTweaker)
                {
                    second = Integer.MIN_VALUE;
                }

                if (o1 instanceof FMLPluginWrapper)
                {
                    first = ((FMLPluginWrapper) o1).sortIndex;
                }
                else if (first == null)
                {
                    first = tweakSorting.get(o1.getClass().getName());
                }
                if (o2 instanceof FMLPluginWrapper)
                {
                    second = ((FMLPluginWrapper) o2).sortIndex;
                }
                else if (second == null)
                {
                    second = tweakSorting.get(o2.getClass().getName());
                }
                if (first == null)
                {
                    first = 0;
                }
                if (second == null)
                {
                    second = 0;
                }

                return Ints.saturatedCast((long)first - (long)second);
            }
        });
    }
}
