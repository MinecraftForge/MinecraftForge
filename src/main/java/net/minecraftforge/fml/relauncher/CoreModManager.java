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

package net.minecraftforge.fml.relauncher;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.common.asm.ASMTransformerWrapper;
import net.minecraftforge.fml.common.asm.transformers.ModAccessTransformer;
import net.minecraftforge.fml.common.launcher.FMLInjectionAndSortingTweaker;
import net.minecraftforge.fml.common.launcher.FMLTweaker;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.DependsOn;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.Name;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

import org.apache.logging.log4j.Level;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;

public class CoreModManager {
    private static final Attributes.Name COREMODCONTAINSFMLMOD = new Attributes.Name("FMLCorePluginContainsFMLMod");
    private static final Attributes.Name MODTYPE = new Attributes.Name("ModType");
    private static final Attributes.Name MODSIDE = new Attributes.Name("ModSide");
    private static final Attributes.Name MODCONTAINSDEPS = new Attributes.Name("ContainedDeps");
    private static String[] rootPlugins = { "net.minecraftforge.fml.relauncher.FMLCorePlugin", "net.minecraftforge.classloading.FMLForgePlugin" };
    private static List<String> ignoredModFiles = Lists.newArrayList();
    private static Map<String, List<String>> transformers = Maps.newHashMap();
    private static List<FMLPluginWrapper> loadPlugins;
    private static boolean deobfuscatedEnvironment;
    private static FMLTweaker tweaker;
    private static File mcDir;
    private static List<String> candidateModFiles = Lists.newArrayList();
    private static List<String> accessTransformers = Lists.newArrayList();
    private static Set<String> rootNames = Sets.newHashSet();
    private static final List<String> skipContainedDeps = Arrays.asList(System.getProperty("fml.skipContainedDeps","").split(","));

    static
    {
        for(String cls : rootPlugins)
        {
            rootNames.add(cls.substring(cls.lastIndexOf('.') + 1));
        }
    }

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
            List<String> ts = Lists.newArrayList();
            if (coreModInstance.getASMTransformerClass() != null) for (String transformer : coreModInstance.getASMTransformerClass())
            {
                FMLRelaunchLog.finer("Registering transformer %s", transformer);
                classLoader.registerTransformer(ASMTransformerWrapper.getTransformerWrapper(classLoader, transformer, name));
                ts.add(transformer);
            }
            if(!rootNames.contains(name))
            {
                String loc;
                if(location == null) loc = "unknown";
                else loc = location.getName();
                transformers.put(name + " (" + loc + ")", ts);
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
                    callData.put("runtimeDeobfuscationEnabled", !deobfuscatedEnvironment);
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
            // NOOP
        }

        if (!deobfuscatedEnvironment)
        {
            FMLRelaunchLog.fine("Enabling runtime deobfuscation");
        }

        tweaker.injectCascadingTweak("net.minecraftforge.fml.common.launcher.FMLInjectionAndSortingTweaker");
        try
        {
            classLoader.registerTransformer("net.minecraftforge.fml.common.asm.transformers.PatchingTransformer");
        }
        catch (Exception e)
        {
            FMLRelaunchLog.log(Level.ERROR, e, "The patch transformer failed to load! This is critical, loading cannot continue!");
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
        ModListHelper.parseModList(mcDir);
        FMLRelaunchLog.fine("Discovering coremods");
        File coreMods = setupCoreModDir(mcDir);
        FilenameFilter ff = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name)
            {
                return name.endsWith(".jar");
            }
        };
        FilenameFilter derpfilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name)
            {
                return name.endsWith(".jar.zip");
            }
        };
        File[] derplist = coreMods.listFiles(derpfilter);
        if (derplist != null && derplist.length > 0)
        {
            FMLRelaunchLog.severe("FML has detected several badly downloaded jar files,  which have been named as zip files. You probably need to download them again, or they may not work properly");
            for (File f : derplist)
            {
                FMLRelaunchLog.severe("Problem file : %s", f.getName());
            }
        }
        FileFilter derpdirfilter = new FileFilter() {
            @Override
            public boolean accept(File pathname)
            {
                return pathname.isDirectory() && new File(pathname,"META-INF").isDirectory();
            }

        };
        File[] derpdirlist = coreMods.listFiles(derpdirfilter);
        if (derpdirlist != null && derpdirlist.length > 0)
        {
            FMLRelaunchLog.log.getLogger().log(Level.FATAL, "There appear to be jars extracted into the mods directory. This is VERY BAD and will almost NEVER WORK WELL");
            FMLRelaunchLog.log.getLogger().log(Level.FATAL, "You should place original jars only in the mods directory. NEVER extract them to the mods directory.");
            FMLRelaunchLog.log.getLogger().log(Level.FATAL, "The directories below appear to be extracted jar files. Fix this before you continue.");

            for (File f : derpdirlist)
            {
                FMLRelaunchLog.log.getLogger().log(Level.FATAL, "Directory {} contains {}", f.getName(), Arrays.asList(new File(f,"META-INF").list()));
            }

            RuntimeException re = new RuntimeException("Extracted mod jars found, loading will NOT continue");
            // We're generating a crash report for the launcher to show to the user here
            try
            {
                Class<?> crashreportclass = classLoader.loadClass("b");
                Object crashreport = crashreportclass.getMethod("a", Throwable.class, String.class).invoke(null, re, "FML has discovered extracted jar files in the mods directory.\nThis breaks mod loading functionality completely.\nRemove the directories and replace with the jar files originally provided.");
                File crashreportfile = new File(new File(coreMods.getParentFile(),"crash-reports"),String.format("fml-crash-%1$tY-%1$tm-%1$td_%1$tT.txt",Calendar.getInstance()));
                crashreportclass.getMethod("a",File.class).invoke(crashreport, crashreportfile);
                System.out.println("#@!@# FML has crashed the game deliberately. Crash report saved to: #@!@# " + crashreportfile.getAbsolutePath());
            } catch (Exception e)
            {
                e.printStackTrace();
                // NOOP - hopefully
            }
            throw re;
        }
        File[] coreModList = coreMods.listFiles(ff);
        File versionedModDir = new File(coreMods, FMLInjectionData.mccversion);
        if (versionedModDir.isDirectory())
        {
            File[] versionedCoreMods = versionedModDir.listFiles(ff);
            coreModList = ObjectArrays.concat(coreModList, versionedCoreMods, File.class);
        }

        coreModList = ObjectArrays.concat(coreModList, ModListHelper.additionalMods.values().toArray(new File[0]), File.class);

        coreModList = FileListHelper.sortFileList(coreModList);

        for (File coreMod : coreModList)
        {
            FMLRelaunchLog.fine("Examining for coremod candidacy %s", coreMod.getName());
            JarFile jar = null;
            Attributes mfAttributes;
            String fmlCorePlugin;
            try
            {
                jar = new JarFile(coreMod);
                if (jar.getManifest() == null)
                {
                    // Not a coremod and no access transformer list
                    continue;
                }
                ModAccessTransformer.addJar(jar);
                mfAttributes = jar.getManifest().getMainAttributes();
                String cascadedTweaker = mfAttributes.getValue("TweakClass");
                if (cascadedTweaker != null)
                {
                    FMLRelaunchLog.info("Loading tweaker %s from %s", cascadedTweaker, coreMod.getName());
                    Integer sortOrder = Ints.tryParse(Strings.nullToEmpty(mfAttributes.getValue("TweakOrder")));
                    sortOrder = (sortOrder == null ? Integer.valueOf(0) : sortOrder);
                    handleCascadingTweak(coreMod, jar, cascadedTweaker, classLoader, sortOrder);
                    ignoredModFiles.add(coreMod.getName());
                    continue;
                }
                List<String> modTypes = mfAttributes.containsKey(MODTYPE) ? Arrays.asList(mfAttributes.getValue(MODTYPE).split(",")) : ImmutableList.of("FML");

                if (!modTypes.contains("FML"))
                {
                    FMLRelaunchLog.fine("Adding %s to the list of things to skip. It is not an FML mod,  it has types %s", coreMod.getName(), modTypes);
                    ignoredModFiles.add(coreMod.getName());
                    continue;
                }
                String modSide = mfAttributes.containsKey(MODSIDE) ? mfAttributes.getValue(MODSIDE) : "BOTH";
                if (! ("BOTH".equals(modSide) || FMLLaunchHandler.side.name().equals(modSide)))
                {
                    FMLRelaunchLog.fine("Mod %s has ModSide meta-inf value %s, and we're %s. It will be ignored", coreMod.getName(), modSide, FMLLaunchHandler.side.name());
                    ignoredModFiles.add(coreMod.getName());
                    continue;
                }
                ModListHelper.additionalMods.putAll(extractContainedDepJars(jar, versionedModDir));
                fmlCorePlugin = mfAttributes.getValue("FMLCorePlugin");
                if (fmlCorePlugin == null)
                {
                    // Not a coremod
                    FMLRelaunchLog.fine("Not found coremod data in %s", coreMod.getName());
                    continue;
                }
            }
            catch (IOException ioe)
            {
                FMLRelaunchLog.log(Level.ERROR, ioe, "Unable to read the jar file %s - ignoring", coreMod.getName());
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
            // Support things that are mod jars, but not FML mod jars
            try
            {
                classLoader.addURL(coreMod.toURI().toURL());
                if (!mfAttributes.containsKey(COREMODCONTAINSFMLMOD))
                {
                    FMLRelaunchLog.finer("Adding %s to the list of known coremods, it will not be examined again", coreMod.getName());
                    ignoredModFiles.add(coreMod.getName());
                }
                else
                {
                    FMLRelaunchLog.finer("Found FMLCorePluginContainsFMLMod marker in %s, it will be examined later for regular @Mod instances",
                            coreMod.getName());
                    candidateModFiles.add(coreMod.getName());
                }
            }
            catch (MalformedURLException e)
            {
                FMLRelaunchLog.log(Level.ERROR, e, "Unable to convert file into a URL. weird");
                continue;
            }
            loadCoreMod(classLoader, fmlCorePlugin, coreMod);
        }
    }

    private static Map<String,File> extractContainedDepJars(JarFile jar, File versionedModsDir) throws IOException
    {
        Map<String,File> result = Maps.newHashMap();
        if (!jar.getManifest().getMainAttributes().containsKey(MODCONTAINSDEPS)) return result;

        String deps = jar.getManifest().getMainAttributes().getValue(MODCONTAINSDEPS);
        String[] depList = deps.split(" ");
        for (String dep : depList)
        {
            if (skipContainedDeps.contains(dep))
            {
                FMLRelaunchLog.log(Level.ERROR, "Skipping dep at request: %s", dep);
                continue;
            }
            final JarEntry jarEntry = jar.getJarEntry(dep);
            if (jarEntry == null)
            {
                FMLRelaunchLog.log(Level.ERROR, "Found invalid ContainsDeps declaration %s in %s", dep, jar.getName());
                continue;
            }
            File target = new File(versionedModsDir, dep);
            if (target.exists())
            {
                FMLRelaunchLog.log(Level.DEBUG, "Found existing ContainsDep extracted to %s, skipping extraction", target.getCanonicalPath());
                result.put(dep,target);
                continue;
            }
            FMLRelaunchLog.log(Level.DEBUG, "Extracted ContainedDep %s from %s to %s", dep, jar.getName(), target.getCanonicalPath());
            try
            {
                Files.createParentDirs(target);
                FileOutputStream targ = new FileOutputStream(target);
                ByteStreams.copy(jar.getInputStream(jarEntry), targ);
                targ.close();
            } catch (IOException e)
            {
                FMLRelaunchLog.log(Level.ERROR, e, "An error occurred extracting dependency");
                continue;
            }
            result.put(dep,target);
        }
        return result;
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

    public static List<String> getIgnoredMods()
    {
        return ignoredModFiles;
    }

    public static Map<String, List<String>> getTransformers()
    {
        return transformers;
    }

    public static List<String> getReparseableCoremods()
    {
        return candidateModFiles;
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
                FMLRelaunchLog.finer("coremod named %s is loading", coreModName);
            }
            MCVersion requiredMCVersion = coreModClazz.getAnnotation(IFMLLoadingPlugin.MCVersion.class);
            if (!Arrays.asList(rootPlugins).contains(coreModClass) && (requiredMCVersion == null || Strings.isNullOrEmpty(requiredMCVersion.value())))
            {
                FMLRelaunchLog.log(Level.WARN, "The coremod %s does not have a MCVersion annotation, it may cause issues with this version of Minecraft",
                        coreModClass);
            }
            else if (requiredMCVersion != null && !FMLInjectionData.mccversion.equals(requiredMCVersion.value()))
            {
                FMLRelaunchLog.log(Level.ERROR, "The coremod %s is requesting minecraft version %s and minecraft is %s. It will be ignored.", coreModClass,
                        requiredMCVersion.value(), FMLInjectionData.mccversion);
                return null;
            }
            else if (requiredMCVersion != null)
            {
                FMLRelaunchLog.log(Level.DEBUG, "The coremod %s requested minecraft version %s and minecraft is %s. It will be loaded.", coreModClass,
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
            String accessTransformerClass = plugin.getAccessTransformerClass();
            if (accessTransformerClass != null)
            {
                FMLRelaunchLog.log(Level.DEBUG, "Added access transformer class %s to enqueued access transformers", accessTransformerClass);
                accessTransformers.add(accessTransformerClass);
            }
            FMLPluginWrapper wrap = new FMLPluginWrapper(coreModName, plugin, location, sortIndex, dependencies);
            loadPlugins.add(wrap);
            FMLRelaunchLog.fine("Enqueued coremod %s", coreModName);
            return wrap;
        }
        catch (ClassNotFoundException cnfe)
        {
            if (!Lists.newArrayList(rootPlugins).contains(coreModClass))
                FMLRelaunchLog.log(Level.ERROR, cnfe, "Coremod %s: Unable to class load the plugin %s", coreModName, coreModClass);
            else
                FMLRelaunchLog.fine("Skipping root plugin %s", coreModClass);
        }
        catch (ClassCastException cce)
        {
            FMLRelaunchLog.log(Level.ERROR, cce, "Coremod %s: The plugin %s is not an implementor of IFMLLoadingPlugin", coreModName, coreModClass);
        }
        catch (InstantiationException ie)
        {
            FMLRelaunchLog.log(Level.ERROR, ie, "Coremod %s: The plugin class %s was not instantiable", coreModName, coreModClass);
        }
        catch (IllegalAccessException iae)
        {
            FMLRelaunchLog.log(Level.ERROR, iae, "Coremod %s: The plugin class %s was not accessible", coreModName, coreModClass);
        }
        return null;
    }

    public static void injectTransformers(LaunchClassLoader classLoader)
    {

        Launch.blackboard.put("fml.deobfuscatedEnvironment", deobfuscatedEnvironment);
        tweaker.injectCascadingTweak("net.minecraftforge.fml.common.launcher.FMLDeobfTweaker");
        tweakSorting.put("net.minecraftforge.fml.common.launcher.FMLDeobfTweaker", Integer.valueOf(1000));
    }

    public static void injectCoreModTweaks(FMLInjectionAndSortingTweaker fmlInjectionAndSortingTweaker)
    {
        @SuppressWarnings("unchecked")
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
        @SuppressWarnings("unchecked")
        List<ITweaker> tweakers = (List<ITweaker>) Launch.blackboard.get("Tweaks");
        // Basically a copy of Collections.sort pre 8u20, optimized as we know we're an array list.
        // Thanks unhelpful fixer of http://bugs.java.com/view_bug.do?bug_id=8032636
        ITweaker[] toSort = tweakers.toArray(new ITweaker[tweakers.size()]);
        Arrays.sort(toSort, new Comparator<ITweaker>() {
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
        // Basically a copy of Collections.sort, optimized as we know we're an array list.
        // Thanks unhelpful fixer of http://bugs.java.com/view_bug.do?bug_id=8032636
        for (int j = 0; j < toSort.length; j++) {
            tweakers.set(j, toSort[j]);
        }
    }

    public static List<String> getAccessTransformers()
    {
        return accessTransformers;
    }

    public static void onCrash(StringBuilder builder)
    {
        if(!ignoredModFiles.isEmpty() || !candidateModFiles.isEmpty())
        {
            builder.append("\nWARNING: coremods are present:\n");
            for(String coreMod : transformers.keySet())
            {
                builder.append("  ").append(coreMod).append('\n');
            }
            builder.append("Contact their authors BEFORE contacting forge\n\n");
        }
    }
}
