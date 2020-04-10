/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml.relauncher;

import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.ToIntFunction;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.common.CertificateHelper;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.asm.ASMTransformerWrapper;
import net.minecraftforge.fml.common.asm.transformers.ModAccessTransformer;
import net.minecraftforge.fml.common.launcher.FMLInjectionAndSortingTweaker;
import net.minecraftforge.fml.common.launcher.FMLTweaker;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.DependsOn;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.Name;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;
import net.minecraftforge.fml.relauncher.libraries.Artifact;
import net.minecraftforge.fml.relauncher.libraries.LibraryManager;
import net.minecraftforge.fml.relauncher.libraries.Repository;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;

public class CoreModManager {
    private static final Attributes.Name COREMODCONTAINSFMLMOD = new Attributes.Name("FMLCorePluginContainsFMLMod");
    private static final Attributes.Name MODTYPE = new Attributes.Name("ModType");
    private static String[] rootPlugins = { "net.minecraftforge.fml.relauncher.FMLCorePlugin", "net.minecraftforge.classloading.FMLForgePlugin" };
    private static List<String> ignoredModFiles = Lists.newArrayList();
    private static Map<String, List<String>> transformers = Maps.newHashMap();
    private static List<FMLPluginWrapper> loadPlugins;
    private static FMLTweaker tweaker;
    private static File mcDir;
    private static List<String> candidateModFiles = Lists.newArrayList();
    private static List<String> accessTransformers = Lists.newArrayList();
    private static Set<String> rootNames = Sets.newHashSet();

    static boolean deobfuscatedEnvironment;

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
            FMLLog.log.debug("Injecting coremod {} \\{{}\\} class transformers", name, coreModInstance.getClass().getName());
            List<String> ts = Lists.newArrayList();
            if (coreModInstance.getASMTransformerClass() != null) for (String transformer : coreModInstance.getASMTransformerClass())
            {
                FMLLog.log.trace("Registering transformer {}", transformer);
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
            FMLLog.log.debug("Injection complete");

            FMLLog.log.debug("Running coremod plugin for {} \\{{}\\}", name, coreModInstance.getClass().getName());
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("mcLocation", mcDir);
            data.put("coremodList", loadPlugins);
            data.put("runtimeDeobfuscationEnabled", !deobfuscatedEnvironment);
            FMLLog.log.debug("Running coremod plugin {}", name);
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
            FMLLog.log.debug("Coremod plugin class {} run successfully", coreModInstance.getClass().getSimpleName());

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

        //TODO: Detect this better? Support install time deobfusication? Decouple deobf from dev? Add dev flag in GradleStart?
        try
        {
            // Are we in a 'decompiled' environment?
            byte[] bs = classLoader.getClassBytes("net.minecraft.world.World");
            if (bs != null)
            {
                FMLLog.log.info("Managed to load a deobfuscated Minecraft name- we are in a deobfuscated environment. Skipping runtime deobfuscation");
                deobfuscatedEnvironment = true;
            }
        }
        catch (IOException e1)
        {
            // NOOP
        }

        if (!deobfuscatedEnvironment)
        {
            FMLLog.log.debug("Enabling runtime deobfuscation");
        }
        else
        {
            if (System.getProperty("log4j.configurationFile") == null)
            {
                FMLLog.log.info("Detected deobfuscated environment, loading log configs for colored console logs.");
                // use server logging configs in deobfuscated environment so developers get nicely colored console logs
                System.setProperty("log4j.configurationFile", "log4j2_server.xml");
                ((LoggerContext) LogManager.getContext(false)).reconfigure();
            }
        }

        tweaker.injectCascadingTweak("net.minecraftforge.fml.common.launcher.FMLInjectionAndSortingTweaker");
        try
        {
            classLoader.registerTransformer("net.minecraftforge.fml.common.asm.transformers.PatchingTransformer");
        }
        catch (Exception e)
        {
            throw new RuntimeException("The patch transformer failed to load! This is critical, loading cannot continue!", e);
        }

        loadPlugins = new ArrayList<FMLPluginWrapper>();
        for (String rootPluginName : rootPlugins)
        {
            loadCoreMod(classLoader, rootPluginName, new File(FMLTweaker.getJarLocation()));
        }

        if (loadPlugins.isEmpty())
        {
            throw new RuntimeException("A fatal error has occurred - no valid fml load plugin was found - this is a completely corrupt FML installation.");
        }

        FMLLog.log.debug("All fundamental core mods are successfully located");
        // Now that we have the root plugins loaded - lets see what else might
        // be around
        String commandLineCoremods = System.getProperty("fml.coreMods.load", "");
        for (String coreModClassName : commandLineCoremods.split(","))
        {
            if (coreModClassName.isEmpty())
            {
                continue;
            }
            FMLLog.log.info("Found a command line coremod : {}", coreModClassName);
            loadCoreMod(classLoader, coreModClassName, null);
        }
        discoverCoreMods(mcDir, classLoader);

    }

    private static void findDerpMods(LaunchClassLoader classLoader, File modDir, File modDirVer)
    {
        File[] derplist = listFiles(path -> path.getName().endsWith(".jar.zip"), modDir, modDirVer);
        if (derplist != null && derplist.length > 0)
        {
            FMLLog.log.fatal("FML has detected several badly downloaded jar files,  which have been named as zip files. You probably need to download them again, or they may not work properly");
            for (File f : derplist)
                FMLLog.log.fatal("Problem file : {}", f.getName());
        }

        FileFilter derpdirfilter = pathname -> pathname.isDirectory() && new File(pathname,"META-INF").isDirectory();
        File[] derpdirlist = listFiles(derpdirfilter, modDir, modDirVer);
        if (derpdirlist != null && derpdirlist.length > 0)
        {
            FMLLog.log.fatal("There appear to be jars extracted into the mods directory. This is VERY BAD and will almost NEVER WORK WELL");
            FMLLog.log.fatal("You should place original jars only in the mods directory. NEVER extract them to the mods directory.");
            FMLLog.log.fatal("The directories below appear to be extracted jar files. Fix this before you continue.");

            for (File f : derpdirlist)
            {
                FMLLog.log.fatal("Directory {} contains {}", f.getName(), Arrays.asList(new File(f,"META-INF").list()));
            }

            RuntimeException re = new RuntimeException("Extracted mod jars found, loading will NOT continue");
            // We're generating a crash report for the launcher to show to the user here
            // Does this actually work with the obfed names?
            try
            {
                Class<?> crashreportclass = classLoader.loadClass("b");
                Object crashreport = crashreportclass.getMethod("a", Throwable.class, String.class).invoke(null, re, "FML has discovered extracted jar files in the mods directory.\nThis breaks mod loading functionality completely.\nRemove the directories and replace with the jar files originally provided.");
                File crashreportfile = new File(new File(modDir.getParentFile(),"crash-reports"),String.format("fml-crash-%1$tY-%1$tm-%1$td_%1$tH.%1$tM.%1$tS.txt",Calendar.getInstance()));
                crashreportclass.getMethod("a",File.class).invoke(crashreport, crashreportfile);
                FMLLog.log.fatal("#@!@# FML has crashed the game deliberately. Crash report saved to: #@!@# {}", crashreportfile.getAbsolutePath());
            } catch (Exception e)
            {
                FMLLog.log.fatal("#@!@# FML has crashed while generating a crash report, please report this. #@!@#", e);
                // NOOP - hopefully
            }
            throw re;
        }
    }

    private static File[] listFiles(FileFilter filter, File ... dirs)
    {
        File[] ret = null;
        for (File dir : dirs)
        {
            if (!dir.isDirectory() || !dir.exists())
                continue;
            if (ret == null)
                ret = dir.listFiles(filter);
            else
                ret = ObjectArrays.concat(ret, dir.listFiles(filter), File.class);
        }
        return ret == null ? new File[0] : ret;
    }

    private static void discoverCoreMods(File mcDir, LaunchClassLoader classLoader)
    {

        File modsDir = setupCoreModDir(mcDir);
        File modsDirVer = new File(modsDir, FMLInjectionData.mccversion);

        findDerpMods(classLoader, modsDir, modsDirVer);

        //By the time we get here, all bundeled jars should be extracted to the proper repos.
        //As well as the mods folders being cleaned up {any files that have maven info being moved to maven folder}

        FMLLog.log.debug("Discovering coremods");
        List<Artifact> maven_canidates = LibraryManager.flattenLists(mcDir);
        List<File> file_canidates = LibraryManager.gatherLegacyCanidates(mcDir);

        for (Artifact artifact : maven_canidates)
        {
            artifact = Repository.resolveAll(artifact);
            if (artifact != null)
            {
                File target = artifact.getFile();
                if (!file_canidates.contains(target))
                    file_canidates.add(target);
            }
        }
        //Do we want to sort the full list after resolving artifacts?
        //TODO: Add dependency gathering?

        for (File coreMod : file_canidates)
        {
            FMLLog.log.debug("Examining for coremod candidacy {}", coreMod.getName());
            JarFile jar = null;
            Attributes mfAttributes;
            String fmlCorePlugin;
            try
            {
                File manifest = new File(coreMod.getAbsolutePath() + ".meta");

                if (LibraryManager.DISABLE_EXTERNAL_MANIFEST || !manifest.exists())
                {
                    jar = new JarFile(coreMod);
                    mfAttributes = jar.getManifest() == null ? null : jar.getManifest().getMainAttributes();
                }
                else
                {
                    FileInputStream fis = new FileInputStream(manifest);
                    mfAttributes = new Manifest(fis).getMainAttributes();
                    fis.close();
                }

                if (mfAttributes == null) // Not a coremod and no access transformer list
                    continue;

                String modSide = mfAttributes.getValue(LibraryManager.MODSIDE);
                if (modSide != null && !"BOTH".equals(modSide) && !FMLLaunchHandler.side().name().equals(modSide))
                {
                    FMLLog.log.debug("Mod {} has ModSide meta-inf value {}, and we're {} It will be ignored", coreMod.getName(), modSide, FMLLaunchHandler.side.name());
                    ignoredModFiles.add(coreMod.getName());
                    continue;
                }

                String ats = mfAttributes.getValue(ModAccessTransformer.FMLAT);
                if (ats != null && !ats.isEmpty())
                {
                    if (jar == null) //We could of loaded the external manifest earlier, if so the jar isn't loaded.
                        jar = new JarFile(coreMod);
                    ModAccessTransformer.addJar(jar, ats);
                }

                String cascadedTweaker = mfAttributes.getValue("TweakClass");
                if (cascadedTweaker != null)
                {
                    FMLLog.log.info("Loading tweaker {} from {}", cascadedTweaker, coreMod.getName());
                    Integer sortOrder = Ints.tryParse(Strings.nullToEmpty(mfAttributes.getValue("TweakOrder")));
                    sortOrder = (sortOrder == null ? Integer.valueOf(0) : sortOrder);
                    handleCascadingTweak(coreMod, jar, cascadedTweaker, classLoader, sortOrder);
                    ignoredModFiles.add(coreMod.getName());
                    continue;
                }
                List<String> modTypes = mfAttributes.containsKey(MODTYPE) ? Arrays.asList(mfAttributes.getValue(MODTYPE).split(",")) : ImmutableList.of("FML");

                if (!modTypes.contains("FML"))
                {
                    FMLLog.log.debug("Adding {} to the list of things to skip. It is not an FML mod, it has types {}", coreMod.getName(), modTypes);
                    ignoredModFiles.add(coreMod.getName());
                    continue;
                }
                fmlCorePlugin = mfAttributes.getValue("FMLCorePlugin");
                if (fmlCorePlugin == null)
                {
                    // Not a coremod
                    FMLLog.log.debug("Not found coremod data in {}", coreMod.getName());
                    continue;
                }
            }
            catch (IOException ioe)
            {
                FMLLog.log.error("Unable to read the jar file {} - ignoring", coreMod.getName(), ioe);
                continue;
            }
            finally
            {
                closeQuietly(jar);
            }
            // Support things that are mod jars, but not FML mod jars
            try
            {
                classLoader.addURL(coreMod.toURI().toURL());
                if (!mfAttributes.containsKey(COREMODCONTAINSFMLMOD))
                {
                    FMLLog.log.trace("Adding {} to the list of known coremods, it will not be examined again", coreMod.getName());
                    ignoredModFiles.add(coreMod.getName());
                }
                else
                {
                    FMLLog.log.warn("Found FMLCorePluginContainsFMLMod marker in {}. This is not recommended, @Mods should be in a separate jar from the coremod.",
                            coreMod.getName());
                    candidateModFiles.add(coreMod.getName());
                }
            }
            catch (MalformedURLException e)
            {
                FMLLog.log.error("Unable to convert file into a URL. weird", e);
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
            FMLLog.log.info("There was a problem trying to load the mod dir tweaker {}", coreMod.getAbsolutePath(), e);
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
            FMLLog.log.debug("Instantiating coremod class {}", coreModName);
            classLoader.addTransformerExclusion(coreModClass);
            Class<?> coreModClazz = Class.forName(coreModClass, true, classLoader);
            Name coreModNameAnn = coreModClazz.getAnnotation(IFMLLoadingPlugin.Name.class);
            if (coreModNameAnn != null && !Strings.isNullOrEmpty(coreModNameAnn.value()))
            {
                coreModName = coreModNameAnn.value();
                FMLLog.log.trace("coremod named {} is loading", coreModName);
            }
            MCVersion requiredMCVersion = coreModClazz.getAnnotation(IFMLLoadingPlugin.MCVersion.class);
            if (!Arrays.asList(rootPlugins).contains(coreModClass) && (requiredMCVersion == null || Strings.isNullOrEmpty(requiredMCVersion.value())))
            {
                FMLLog.log.warn("The coremod {} does not have a MCVersion annotation, it may cause issues with this version of Minecraft",
                        coreModClass);
            }
            else if (requiredMCVersion != null && !FMLInjectionData.mccversion.equals(requiredMCVersion.value()))
            {
                FMLLog.log.error("The coremod {} is requesting minecraft version {} and minecraft is {}. It will be ignored.", coreModClass,
                        requiredMCVersion.value(), FMLInjectionData.mccversion);
                return null;
            }
            else if (requiredMCVersion != null)
            {
                FMLLog.log.debug("The coremod {} requested minecraft version {} and minecraft is {}. It will be loaded.", coreModClass,
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

            Certificate[] certificates = coreModClazz.getProtectionDomain().getCodeSource().getCertificates();
            ImmutableList<String> certList = CertificateHelper.getFingerprints(certificates);
            if (certList.isEmpty())
            {
                if (deobfuscatedEnvironment && Arrays.asList(rootPlugins).contains(coreModClass)) //This is probably a forge/mod dev environment - ignore missing forge certificates
                {
                    FMLLog.log.info("Ignoring missing certificate for coremod {} ({}), we are in deobf and it's a forge core plugin", coreModName, coreModClass);
                }
                else if (deobfuscatedEnvironment && location == null) // This is probably a mod dev workspace
                {
                    FMLLog.log.info("Ignoring missing certificate for coremod {} ({}), as this is probably a dev workspace", coreModName, coreModClass);
                }
                else // This is a probably a normal minecraft workspace - log at warn
                {
                    FMLLog.log.warn("The coremod {} ({}) is not signed!", coreModName, coreModClass);
                }
            }
            else
            {
                FMLLog.log.debug("Found signing certificates for coremod {} ({})", coreModName, coreModClass);
                for (String cert : certList)
                {
                    FMLLog.log.debug("Found certificate {}", cert);
                }
            }

            IFMLLoadingPlugin plugin = (IFMLLoadingPlugin) coreModClazz.newInstance();
            String accessTransformerClass = plugin.getAccessTransformerClass();
            if (accessTransformerClass != null)
            {
                FMLLog.log.debug("Added access transformer class {} to enqueued access transformers", accessTransformerClass);
                accessTransformers.add(accessTransformerClass);
            }
            FMLPluginWrapper wrap = new FMLPluginWrapper(coreModName, plugin, location, sortIndex, dependencies);
            loadPlugins.add(wrap);
            FMLLog.log.debug("Enqueued coremod {}", coreModName);
            return wrap;
        }
        catch (ClassNotFoundException cnfe)
        {
            if (!Lists.newArrayList(rootPlugins).contains(coreModClass))
                FMLLog.log.error("Coremod {}: Unable to class load the plugin {}", coreModName, coreModClass, cnfe);
            else
                FMLLog.log.debug("Skipping root plugin {}", coreModClass);
        }
        catch (ClassCastException cce)
        {
            FMLLog.log.error("Coremod {}: The plugin {} is not an implementor of IFMLLoadingPlugin", coreModName, coreModClass, cce);
        }
        catch (InstantiationException ie)
        {
            FMLLog.log.error("Coremod {}: The plugin class {} was not instantiable", coreModName, coreModClass, ie);
        }
        catch (IllegalAccessException iae)
        {
            FMLLog.log.error("Coremod {}: The plugin class {} was not accessible", coreModName, coreModClass, iae);
        }
        return null;
    }

    public static void injectTransformers(LaunchClassLoader classLoader)
    {

        Launch.blackboard.put("fml.deobfuscatedEnvironment", deobfuscatedEnvironment);
        tweaker.injectCascadingTweak("net.minecraftforge.fml.common.launcher.FMLDeobfTweaker");
        tweakSorting.put("net.minecraftforge.fml.common.launcher.FMLDeobfTweaker", 1000);
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
        ToIntFunction<ITweaker> getOrder = o -> o instanceof FMLInjectionAndSortingTweaker ? Integer.MIN_VALUE : o instanceof FMLPluginWrapper ? ((FMLPluginWrapper)o).sortIndex : tweakSorting.getOrDefault(o.getClass().getName(), 0);
        Arrays.sort(toSort, (o1, o2) -> Ints.saturatedCast((long)getOrder.applyAsInt(o1) - (long)getOrder.applyAsInt(o2)));
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

    private  static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null)
                closeable.close();
        } catch (final IOException ioe){}
    }

}
