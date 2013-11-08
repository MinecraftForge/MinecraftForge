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

package cpw.mods.fml.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import net.minecraft.crash.CallableMinecraftVersion;
import net.minecraft.item.ItemStack;

import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableListMultimap.Builder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Multiset.Entry;
import com.google.common.collect.Multisets;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets.SetView;
import com.google.common.collect.Table;
import com.google.common.collect.TreeMultimap;
import com.google.common.io.Files;

import cpw.mods.fml.common.LoaderState.ModState;
import cpw.mods.fml.common.discovery.ModDiscoverer;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLLoadEvent;
import cpw.mods.fml.common.functions.ArtifactVersionNameFunction;
import cpw.mods.fml.common.functions.ModIdFunction;
import cpw.mods.fml.common.modloader.BaseModProxy;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.toposort.ModSorter;
import cpw.mods.fml.common.toposort.ModSortingException;
import cpw.mods.fml.common.toposort.ModSortingException.SortingExceptionData;
import cpw.mods.fml.common.toposort.TopologicalSort;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.VersionParser;
import cpw.mods.fml.relauncher.FMLRelaunchLog;

/**
 * The loader class performs the actual loading of the mod code from disk.
 *
 * <p>
 * There are several {@link LoaderState}s to mod loading, triggered in two
 * different stages from the FML handler code's hooks into the minecraft code.
 * </p>
 *
 * <ol>
 * <li>LOADING. Scanning the filesystem for mod containers to load (zips, jars,
 * directories), adding them to the {@link #modClassLoader} Scanning, the loaded
 * containers for mod classes to load and registering them appropriately.</li>
 * <li>PREINIT. The mod classes are configured, they are sorted into a load
 * order, and instances of the mods are constructed.</li>
 * <li>INIT. The mod instances are initialized. For BaseMod mods, this involves
 * calling the load method.</li>
 * <li>POSTINIT. The mod instances are post initialized. For BaseMod mods this
 * involves calling the modsLoaded method.</li>
 * <li>UP. The Loader is complete</li>
 * <li>ERRORED. The loader encountered an error during the LOADING phase and
 * dropped to this state instead. It will not complete loading from this state,
 * but it attempts to continue loading before abandoning and giving a fatal
 * error.</li>
 * </ol>
 *
 * Phase 1 code triggers the LOADING and PREINIT states. Phase 2 code triggers
 * the INIT and POSTINIT states.
 *
 * @author cpw
 *
 */
public class Loader
{
    private static final Splitter DEPENDENCYPARTSPLITTER = Splitter.on(":").omitEmptyStrings().trimResults();
    private static final Splitter DEPENDENCYSPLITTER = Splitter.on(";").omitEmptyStrings().trimResults();
    /**
     * The singleton instance
     */
    private static Loader instance;
    /**
     * Build information for tracking purposes.
     */
    private static String major;
    private static String minor;
    private static String rev;
    private static String build;
    private static String mccversion;
    private static String mcpversion;

    /**
     * The class loader we load the mods into.
     */
    private ModClassLoader modClassLoader;
    /**
     * The sorted list of mods.
     */
    private List<ModContainer> mods;
    /**
     * A named list of mods
     */
    private Map<String, ModContainer> namedMods;
    /**
     * The canonical configuration directory
     */
    private File canonicalConfigDir;
    /**
     * The canonical minecraft directory
     */
    private File canonicalMinecraftDir;
    /**
     * The captured error
     */
    private Exception capturedError;
    private File canonicalModsDir;
    private LoadController modController;
    private MinecraftDummyContainer minecraft;
    private MCPDummyContainer mcp;

    private static File minecraftDir;
    private static List<String> injectedContainers;
    private File loggingProperties;
    private ImmutableMap<String, String> fmlBrandingProperties;

    public static Loader instance()
    {
        if (instance == null)
        {
            instance = new Loader();
        }

        return instance;
    }

    public static void injectData(Object... data)
    {
        major = (String) data[0];
        minor = (String) data[1];
        rev = (String) data[2];
        build = (String) data[3];
        mccversion = (String) data[4];
        mcpversion = (String) data[5];
        minecraftDir = (File) data[6];
        injectedContainers = (List<String>)data[7];
    }

    private Loader()
    {
        modClassLoader = new ModClassLoader(getClass().getClassLoader());
        String actualMCVersion = new CallableMinecraftVersion(null).func_71493_a();
        if (!mccversion.equals(actualMCVersion))
        {
            FMLLog.severe("This version of FML is built for Minecraft %s, we have detected Minecraft %s in your minecraft jar file", mccversion, actualMCVersion);
            throw new LoaderException();
        }

        minecraft = new MinecraftDummyContainer(actualMCVersion);
        mcp = new MCPDummyContainer(MetadataCollection.from(getClass().getResourceAsStream("/mcpmod.info"), "MCP").getMetadataForId("mcp", null));
    }

    /**
     * Sort the mods into a sorted list, using dependency information from the
     * containers. The sorting is performed using a {@link TopologicalSort}
     * based on the pre- and post- dependency information provided by the mods.
     */
    private void sortModList()
    {
        FMLLog.finer("Verifying mod requirements are satisfied");
        try
        {
            BiMap<String, ArtifactVersion> modVersions = HashBiMap.create();
            for (ModContainer mod : getActiveModList())
            {
                modVersions.put(mod.getModId(), mod.getProcessedVersion());
            }

            for (ModContainer mod : getActiveModList())
            {
                if (!mod.acceptableMinecraftVersionRange().containsVersion(minecraft.getProcessedVersion()))
                {
                    FMLLog.severe("The mod %s does not wish to run in Minecraft version %s. You will have to remove it to play.", mod.getModId(), getMCVersionString());
                    throw new WrongMinecraftVersionException(mod);
                }
                Map<String,ArtifactVersion> names = Maps.uniqueIndex(mod.getRequirements(), new ArtifactVersionNameFunction());
                Set<ArtifactVersion> versionMissingMods = Sets.newHashSet();
                Set<String> missingMods = Sets.difference(names.keySet(), modVersions.keySet());
                if (!missingMods.isEmpty())
                {
                    FMLLog.severe("The mod %s (%s) requires mods %s to be available", mod.getModId(), mod.getName(), missingMods);
                    for (String modid : missingMods)
                    {
                        versionMissingMods.add(names.get(modid));
                    }
                    throw new MissingModsException(versionMissingMods);
                }
                ImmutableList<ArtifactVersion> allDeps = ImmutableList.<ArtifactVersion>builder().addAll(mod.getDependants()).addAll(mod.getDependencies()).build();
                for (ArtifactVersion v : allDeps)
                {
                    if (modVersions.containsKey(v.getLabel()))
                    {
                        if (!v.containsVersion(modVersions.get(v.getLabel())))
                        {
                            versionMissingMods.add(v);
                        }
                    }
                }
                if (!versionMissingMods.isEmpty())
                {
                    FMLLog.severe("The mod %s (%s) requires mod versions %s to be available", mod.getModId(), mod.getName(), versionMissingMods);
                    throw new MissingModsException(versionMissingMods);
                }
            }

            FMLLog.finer("All mod requirements are satisfied");

            ModSorter sorter = new ModSorter(getActiveModList(), namedMods);

            try
            {
                FMLLog.finer("Sorting mods into an ordered list");
                List<ModContainer> sortedMods = sorter.sort();
                // Reset active list to the sorted list
                modController.getActiveModList().clear();
                modController.getActiveModList().addAll(sortedMods);
                // And inject the sorted list into the overall list
                mods.removeAll(sortedMods);
                sortedMods.addAll(mods);
                mods = sortedMods;
                FMLLog.finer("Mod sorting completed successfully");
            }
            catch (ModSortingException sortException)
            {
                FMLLog.severe("A dependency cycle was detected in the input mod set so an ordering cannot be determined");
                SortingExceptionData<ModContainer> exceptionData = sortException.getExceptionData();
                FMLLog.severe("The first mod in the cycle is %s", exceptionData.getFirstBadNode());
                FMLLog.severe("The mod cycle involves");
                for (ModContainer mc : exceptionData.getVisitedNodes())
                {
                    FMLLog.severe("%s : before: %s, after: %s", mc.toString(), mc.getDependants(), mc.getDependencies());
                }
                FMLLog.log(Level.SEVERE, sortException, "The full error");
                throw sortException;
            }
        }
        finally
        {
            FMLLog.fine("Mod sorting data");
            int unprintedMods = mods.size();
            for (ModContainer mod : getActiveModList())
            {
                if (!mod.isImmutable())
                {
                    FMLLog.fine("\t%s(%s:%s): %s (%s)", mod.getModId(), mod.getName(), mod.getVersion(), mod.getSource().getName(), mod.getSortingRules());
                    unprintedMods--;
                }
            }
            if (unprintedMods == mods.size())
            {
                FMLLog.fine("No user mods found to sort");
            }
        }

    }

    /**
     * The primary loading code
     *
     * This is visited during first initialization by Minecraft to scan and load
     * the mods from all sources 1. The minecraft jar itself (for loading of in
     * jar mods- I would like to remove this if possible but forge depends on it
     * at present) 2. The mods directory with expanded subdirs, searching for
     * mods named mod_*.class 3. The mods directory for zip and jar files,
     * searching for mod classes named mod_*.class again
     *
     * The found resources are first loaded into the {@link #modClassLoader}
     * (always) then scanned for class resources matching the specification
     * above.
     *
     * If they provide the {@link Mod} annotation, they will be loaded as
     * "FML mods", which currently is effectively a NO-OP. If they are
     * determined to be {@link BaseModProxy} subclasses they are loaded as such.
     *
     * Finally, if they are successfully loaded as classes, they are then added
     * to the available mod list.
     */
    private ModDiscoverer identifyMods()
    {
        FMLLog.fine("Building injected Mod Containers %s", injectedContainers);
        // Add in the MCP mod container
        mods.add(new InjectedModContainer(mcp,new File("minecraft.jar")));
        for (String cont : injectedContainers)
        {
            ModContainer mc;
            try
            {
                mc = (ModContainer) Class.forName(cont,true,modClassLoader).newInstance();
            }
            catch (Exception e)
            {
                FMLLog.log(Level.SEVERE, e, "A problem occured instantiating the injected mod container %s", cont);
                throw new LoaderException(e);
            }
            mods.add(new InjectedModContainer(mc,mc.getSource()));
        }
        ModDiscoverer discoverer = new ModDiscoverer();
        FMLLog.fine("Attempting to load mods contained in the minecraft jar file and associated classes");
        discoverer.findClasspathMods(modClassLoader);
        FMLLog.fine("Minecraft jar mods loaded successfully");

        FMLLog.info("Searching %s for mods", canonicalModsDir.getAbsolutePath());
        discoverer.findModDirMods(canonicalModsDir);
        File versionSpecificModsDir = new File(canonicalModsDir,mccversion);
        if (versionSpecificModsDir.isDirectory())
        {
            FMLLog.info("Also searching %s for mods", versionSpecificModsDir);
            discoverer.findModDirMods(versionSpecificModsDir);
        }

        mods.addAll(discoverer.identifyMods());
        identifyDuplicates(mods);
        namedMods = Maps.uniqueIndex(mods, new ModIdFunction());
        FMLLog.info("Forge Mod Loader has identified %d mod%s to load", mods.size(), mods.size() != 1 ? "s" : "");
        for (String modId: namedMods.keySet())
        {
            FMLLog.makeLog(modId);
        }
        return discoverer;
    }

    private class ModIdComparator implements Comparator<ModContainer>
    {
        @Override
        public int compare(ModContainer o1, ModContainer o2)
        {
            return o1.getModId().compareTo(o2.getModId());
        }
    }

    private void identifyDuplicates(List<ModContainer> mods)
    {
        TreeMultimap<ModContainer, File> dupsearch = TreeMultimap.create(new ModIdComparator(), Ordering.arbitrary());
        for (ModContainer mc : mods)
        {
            if (mc.getSource() != null)
            {
                dupsearch.put(mc, mc.getSource());
            }
        }

        ImmutableMultiset<ModContainer> duplist = Multisets.copyHighestCountFirst(dupsearch.keys());
        SetMultimap<ModContainer, File> dupes = LinkedHashMultimap.create();
        for (Entry<ModContainer> e : duplist.entrySet())
        {
            if (e.getCount() > 1)
            {
                FMLLog.severe("Found a duplicate mod %s at %s", e.getElement().getModId(), dupsearch.get(e.getElement()));
                dupes.putAll(e.getElement(),dupsearch.get(e.getElement()));
            }
        }
        if (!dupes.isEmpty())
        {
            throw new DuplicateModsFoundException(dupes);
        }
    }

    /**
     *
     */
    private void initializeLoader()
    {
        File modsDir = new File(minecraftDir, "mods");
        File configDir = new File(minecraftDir, "config");
        String canonicalModsPath;
        String canonicalConfigPath;

        try
        {
            canonicalMinecraftDir = minecraftDir.getCanonicalFile();
            canonicalModsPath = modsDir.getCanonicalPath();
            canonicalConfigPath = configDir.getCanonicalPath();
            canonicalConfigDir = configDir.getCanonicalFile();
            canonicalModsDir = modsDir.getCanonicalFile();
        }
        catch (IOException ioe)
        {
            FMLLog.log(Level.SEVERE, ioe, "Failed to resolve loader directories: mods : %s ; config %s", canonicalModsDir.getAbsolutePath(),
                            configDir.getAbsolutePath());
            throw new LoaderException(ioe);
        }

        if (!canonicalModsDir.exists())
        {
            FMLLog.info("No mod directory found, creating one: %s", canonicalModsPath);
            boolean dirMade = canonicalModsDir.mkdir();
            if (!dirMade)
            {
                FMLLog.severe("Unable to create the mod directory %s", canonicalModsPath);
                throw new LoaderException();
            }
            FMLLog.info("Mod directory created successfully");
        }

        if (!canonicalConfigDir.exists())
        {
            FMLLog.fine("No config directory found, creating one: %s", canonicalConfigPath);
            boolean dirMade = canonicalConfigDir.mkdir();
            if (!dirMade)
            {
                FMLLog.severe("Unable to create the config directory %s", canonicalConfigPath);
                throw new LoaderException();
            }
            FMLLog.info("Config directory created successfully");
        }

        if (!canonicalModsDir.isDirectory())
        {
            FMLLog.severe("Attempting to load mods from %s, which is not a directory", canonicalModsPath);
            throw new LoaderException();
        }

        if (!configDir.isDirectory())
        {
            FMLLog.severe("Attempting to load configuration from %s, which is not a directory", canonicalConfigPath);
            throw new LoaderException();
        }

        loggingProperties = new File(canonicalConfigDir, "logging.properties");
        FMLLog.info("Reading custom logging properties from %s", loggingProperties.getPath());
        FMLRelaunchLog.loadLogConfiguration(loggingProperties);
        FMLLog.log(Level.OFF,"Logging level for ForgeModLoader logging is set to %s", FMLRelaunchLog.log.getLogger().getLevel());
    }

    public List<ModContainer> getModList()
    {
        return instance().mods != null ? ImmutableList.copyOf(instance().mods) : ImmutableList.<ModContainer>of();
    }

    /**
     * Called from the hook to start mod loading. We trigger the
     * {@link #identifyMods()} and Constructing, Preinitalization, and Initalization phases here. Finally,
     * the mod list is frozen completely and is consider immutable from then on.
     */
    public void loadMods()
    {
        initializeLoader();
        mods = Lists.newArrayList();
        namedMods = Maps.newHashMap();
        modController = new LoadController(this);
        modController.transition(LoaderState.LOADING, false);
        ModDiscoverer disc = identifyMods();
        ModAPIManager.INSTANCE.manageAPI(modClassLoader, disc);
        disableRequestedMods();
        FMLLog.fine("Reloading logging properties from %s", loggingProperties.getPath());
        FMLRelaunchLog.loadLogConfiguration(loggingProperties);
        FMLLog.fine("Reloaded logging properties");
        modController.distributeStateMessage(FMLLoadEvent.class);
        sortModList();
        ModAPIManager.INSTANCE.cleanupAPIContainers(modController.getActiveModList());
        ModAPIManager.INSTANCE.cleanupAPIContainers(mods);
        mods = ImmutableList.copyOf(mods);
        for (File nonMod : disc.getNonModLibs())
        {
            if (nonMod.isFile())
            {
                FMLLog.info("FML has found a non-mod file %s in your mods directory. It will now be injected into your classpath. This could severe stability issues, it should be removed if possible.", nonMod.getName());
                try
                {
                    modClassLoader.addFile(nonMod);
                }
                catch (MalformedURLException e)
                {
                    FMLLog.log(Level.SEVERE, e, "Encountered a weird problem with non-mod file injection : %s", nonMod.getName());
                }
            }
        }
        modController.transition(LoaderState.CONSTRUCTING, false);
        modController.distributeStateMessage(LoaderState.CONSTRUCTING, modClassLoader, disc.getASMTable());
        FMLLog.fine("Mod signature data");
        for (ModContainer mod : getActiveModList())
        {
            FMLLog.fine("\t%s(%s:%s): %s (%s)", mod.getModId(), mod.getName(), mod.getVersion(), mod.getSource().getName(), CertificateHelper.getFingerprint(mod.getSigningCertificate()));
        }
        if (getActiveModList().isEmpty())
        {
            FMLLog.fine("No user mod signature data found");
        }
        modController.transition(LoaderState.PREINITIALIZATION, false);
        modController.distributeStateMessage(LoaderState.PREINITIALIZATION, disc.getASMTable(), canonicalConfigDir);
        modController.transition(LoaderState.INITIALIZATION, false);
        GameData.validateRegistry();
    }

    private void disableRequestedMods()
    {
        String forcedModList = System.getProperty("fml.modStates", "");
        FMLLog.finer("Received a system property request \'%s\'",forcedModList);
        Map<String, String> sysPropertyStateList = Splitter.on(CharMatcher.anyOf(";:"))
                .omitEmptyStrings().trimResults().withKeyValueSeparator("=")
                .split(forcedModList);
        FMLLog.finer("System property request managing the state of %d mods", sysPropertyStateList.size());
        Map<String, String> modStates = Maps.newHashMap();

        File forcedModFile = new File(canonicalConfigDir, "fmlModState.properties");
        Properties forcedModListProperties = new Properties();
        if (forcedModFile.exists() && forcedModFile.isFile())
        {
            FMLLog.finer("Found a mod state file %s", forcedModFile.getName());
            try
            {
                forcedModListProperties.load(new FileReader(forcedModFile));
                FMLLog.finer("Loaded states for %d mods from file", forcedModListProperties.size());
            }
            catch (Exception e)
            {
                FMLLog.log(Level.INFO, e, "An error occurred reading the fmlModState.properties file");
            }
        }
        modStates.putAll(Maps.fromProperties(forcedModListProperties));
        modStates.putAll(sysPropertyStateList);
        FMLLog.fine("After merging, found state information for %d mods", modStates.size());

        Map<String, Boolean> isEnabled = Maps.transformValues(modStates, new Function<String, Boolean>()
        {
            public Boolean apply(String input)
            {
                return Boolean.parseBoolean(input);
            }
        });

        for (Map.Entry<String, Boolean> entry : isEnabled.entrySet())
        {
            if (namedMods.containsKey(entry.getKey()))
            {
                FMLLog.info("Setting mod %s to enabled state %b", entry.getKey(), entry.getValue());
                namedMods.get(entry.getKey()).setEnabledState(entry.getValue());
            }
        }
    }

    /**
     * Query if we know of a mod named modname
     *
     * @param modname
     * @return If the mod is loaded
     */
    public static boolean isModLoaded(String modname)
    {
        return instance().namedMods.containsKey(modname) && instance().modController.getModState(instance.namedMods.get(modname))!=ModState.DISABLED;
    }

    public File getConfigDir()
    {
        return canonicalConfigDir;
    }

    public String getCrashInformation()
    {
        // Handle being called before we've begun setup
        if (modController == null)
        {
            return "";
        }
        StringBuilder ret = new StringBuilder();
        List<String> branding = FMLCommonHandler.instance().getBrandings();

        Joiner.on(' ').skipNulls().appendTo(ret, branding.subList(1, branding.size()));
        if (modController != null)
        {
            modController.printModStates(ret);
        }
        return ret.toString();
    }

    public String getFMLVersionString()
    {
        return String.format("%s.%s.%s.%s", major, minor, rev, build);
    }

    public ClassLoader getModClassLoader()
    {
        return modClassLoader;
    }

    public void computeDependencies(String dependencyString, Set<ArtifactVersion> requirements, List<ArtifactVersion> dependencies, List<ArtifactVersion> dependants)
    {
        if (dependencyString == null || dependencyString.length() == 0)
        {
            return;
        }

        boolean parseFailure = false;

        for (String dep : DEPENDENCYSPLITTER.split(dependencyString))
        {
            List<String> depparts = Lists.newArrayList(DEPENDENCYPARTSPLITTER.split(dep));
            // Need two parts to the string
            if (depparts.size() != 2)
            {
                parseFailure = true;
                continue;
            }
            String instruction = depparts.get(0);
            String target = depparts.get(1);
            boolean targetIsAll = target.startsWith("*");

            // Cannot have an "all" relationship with anything except pure *
            if (targetIsAll && target.length() > 1)
            {
                parseFailure = true;
                continue;
            }

            // If this is a required element, add it to the required list
            if ("required-before".equals(instruction) || "required-after".equals(instruction))
            {
                // You can't require everything
                if (!targetIsAll)
                {
                    requirements.add(VersionParser.parseVersionReference(target));
                }
                else
                {
                    parseFailure = true;
                    continue;
                }
            }

            // You cannot have a versioned dependency on everything
            if (targetIsAll && target.indexOf('@') > -1)
            {
                parseFailure = true;
                continue;
            }
            // before elements are things we are loaded before (so they are our dependants)
            if ("required-before".equals(instruction) || "before".equals(instruction))
            {
            	dependants.add(VersionParser.parseVersionReference(target));
            }
            // after elements are things that load before we do (so they are out dependencies)
            else if ("required-after".equals(instruction) || "after".equals(instruction))
            {
                dependencies.add(VersionParser.parseVersionReference(target));
            }
            else
            {
                parseFailure = true;
            }
        }

        if (parseFailure)
        {
            FMLLog.log(Level.WARNING, "Unable to parse dependency string %s", dependencyString);
            throw new LoaderException();
        }
    }

    public Map<String,ModContainer> getIndexedModList()
    {
        return ImmutableMap.copyOf(namedMods);
    }

    public void initializeMods()
    {
        // Mod controller should be in the initialization state here
        modController.distributeStateMessage(LoaderState.INITIALIZATION);
        modController.transition(LoaderState.POSTINITIALIZATION, false);
        // Construct the "mod object table" so mods can refer to it in IMC and postinit
        GameData.buildModObjectTable();
        modController.distributeStateMessage(FMLInterModComms.IMCEvent.class);
        modController.distributeStateMessage(LoaderState.POSTINITIALIZATION);
        modController.transition(LoaderState.AVAILABLE, false);
        modController.distributeStateMessage(LoaderState.AVAILABLE);
        // Dump the custom registry data map, if necessary
        GameData.dumpRegistry(minecraftDir);
        FMLLog.info("Forge Mod Loader has successfully loaded %d mod%s", mods.size(), mods.size() == 1 ? "" : "s");
    }

    public ICrashCallable getCallableCrashInformation()
    {
        return new ICrashCallable() {
            @Override
            public String call() throws Exception
            {
                return getCrashInformation();
            }

            @Override
            public String getLabel()
            {
                return "FML";
            }
        };
    }

    public List<ModContainer> getActiveModList()
    {
        return modController != null ? modController.getActiveModList() : ImmutableList.<ModContainer>of();
    }

    public ModState getModState(ModContainer selectedMod)
    {
        return modController.getModState(selectedMod);
    }

    public String getMCVersionString()
    {
        return "Minecraft " + mccversion;
    }

    public boolean serverStarting(Object server)
    {
        try
        {
            modController.distributeStateMessage(LoaderState.SERVER_STARTING, server);
            modController.transition(LoaderState.SERVER_STARTING, false);
        }
        catch (Throwable t)
        {
            FMLLog.log(Level.SEVERE, t, "A fatal exception occurred during the server starting event");
            return false;
        }
        return true;
    }

    public void serverStarted()
    {
        modController.distributeStateMessage(LoaderState.SERVER_STARTED);
        modController.transition(LoaderState.SERVER_STARTED, false);
    }

    public void serverStopping()
    {
        modController.distributeStateMessage(LoaderState.SERVER_STOPPING);
        modController.transition(LoaderState.SERVER_STOPPING, false);
    }

    public BiMap<ModContainer, Object> getModObjectList()
    {
        return modController.getModObjectList();
    }

    public BiMap<Object, ModContainer> getReversedModObjectList()
    {
        return getModObjectList().inverse();
    }

    public ModContainer activeModContainer()
    {
        return modController != null ? modController.activeContainer() : null;
    }

    public boolean isInState(LoaderState state)
    {
        return modController.isInState(state);
    }

    public MinecraftDummyContainer getMinecraftModContainer()
    {
        return minecraft;
    }

    public boolean hasReachedState(LoaderState state)
    {
        return modController != null ? modController.hasReachedState(state) : false;
    }

    public String getMCPVersionString()
    {
        return String.format("MCP v%s", mcpversion);
    }

    public void serverStopped()
    {
        modController.distributeStateMessage(LoaderState.SERVER_STOPPED);
        modController.transition(LoaderState.SERVER_STOPPED, true);
        modController.transition(LoaderState.AVAILABLE, true);
    }

    public boolean serverAboutToStart(Object server)
    {
        try
        {
            modController.distributeStateMessage(LoaderState.SERVER_ABOUT_TO_START, server);
            modController.transition(LoaderState.SERVER_ABOUT_TO_START, false);
        }
        catch (Throwable t)
        {
            FMLLog.log(Level.SEVERE, t, "A fatal exception occurred during the server about to start event");
            return false;
        }
        return true;
    }

    public Map<String,String> getFMLBrandingProperties()
    {
        if (fmlBrandingProperties == null)
        {
            Properties loaded = new Properties();
            try
            {
                loaded.load(getClass().getClassLoader().getResourceAsStream("fmlbranding.properties"));
            }
            catch (Exception e)
            {
                // File not found - ignore
            }
            fmlBrandingProperties = Maps.fromProperties(loaded);
        }
        return fmlBrandingProperties;
    }


    public Map<String,String> getCustomModProperties(String modId)
    {
        return getIndexedModList().get(modId).getCustomModProperties();
    }
}
