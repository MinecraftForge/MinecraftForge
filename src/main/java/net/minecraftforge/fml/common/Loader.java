/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.fml.common;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.LoaderState.ModState;
import net.minecraftforge.fml.common.ModContainer.Disableable;
import net.minecraftforge.fml.common.ProgressManager.ProgressBar;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ModDiscoverer;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLLoadEvent;
import net.minecraftforge.fml.common.event.FMLModIdMappingEvent;
import net.minecraftforge.fml.common.registry.*;
import net.minecraftforge.fml.common.toposort.ModSorter;
import net.minecraftforge.fml.common.toposort.ModSortingException;
import net.minecraftforge.fml.common.toposort.TopologicalSort;
import net.minecraftforge.fml.common.toposort.ModSortingException.SortingExceptionData;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.VersionParser;
import net.minecraftforge.fml.relauncher.ModListHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.ObjectHolderRegistry;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;

import com.google.common.base.CharMatcher;
import java.util.function.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Multiset.Entry;
import com.google.common.collect.Multisets;
import com.google.common.collect.Ordering;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeMultimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.annotation.Nullable;

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
@SuppressWarnings("unused")
public class Loader
{
    public static final String MC_VERSION = net.minecraftforge.common.ForgeVersion.mcVersion;
    private static final ImmutableList<String> DEPENDENCYINSTRUCTIONS = ImmutableList.of("client", "server", "required", "before", "after");
    private static final Splitter DEPENDENCYINSTRUCTIONSSPLITTER = Splitter.on("-").omitEmptyStrings().trimResults();
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
     * A reverse dependency graph for mods
     */
    private ListMultimap<String, String> reverseDependencies;
    /**
     * The canonical configuration directory
     */
    private File canonicalConfigDir;
    private File canonicalModsDir;
    private LoadController modController;
    private MinecraftDummyContainer minecraft;
    private MCPDummyContainer mcp;

    private static File minecraftDir;
    private static List<String> injectedContainers;
    private ImmutableMap<String, String> fmlBrandingProperties;
    private File forcedModFile;
    private ModDiscoverer discoverer;
    private ProgressBar progressBar;

    public static Loader instance()
    {
        if (instance == null)
        {
            instance = new Loader();
        }

        return instance;
    }

    @SuppressWarnings("unchecked")
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
        if (mccversion !=null && !mccversion.equals(MC_VERSION))
        {
            FMLLog.log.fatal("This version of FML is built for Minecraft {}, we have detected Minecraft {} in your minecraft jar file", mccversion, MC_VERSION);
            throw new LoaderException(String.format("This version of FML is built for Minecraft %s, we have detected Minecraft %s in your minecraft jar file", mccversion, MC_VERSION));
        }

        minecraft = new MinecraftDummyContainer(MC_VERSION);
        InputStream mcpModInputStream = getClass().getResourceAsStream("/mcpmod.info");
        try
        {
            mcp = new MCPDummyContainer(MetadataCollection.from(mcpModInputStream, "MCP").getMetadataForId("mcp", null));
        }
        finally
        {
            IOUtils.closeQuietly(mcpModInputStream);
        }
    }

    /**
     * Sort the mods into a sorted list, using dependency information from the
     * containers. The sorting is performed using a {@link TopologicalSort}
     * based on the pre- and post- dependency information provided by the mods.
     */
    private void sortModList()
    {
        FMLLog.log.trace("Verifying mod requirements are satisfied");
        List<WrongMinecraftVersionException> wrongMinecraftExceptions = new ArrayList<WrongMinecraftVersionException>();
        List<MissingModsException> missingModsExceptions = new ArrayList<MissingModsException>();
        try
        {
            BiMap<String, ArtifactVersion> modVersions = HashBiMap.create();
            for (ModContainer mod : Iterables.concat(getActiveModList(), ModAPIManager.INSTANCE.getAPIList()))
            {
                modVersions.put(mod.getModId(), mod.getProcessedVersion());
            }

            ArrayListMultimap<String, String> reqList = ArrayListMultimap.create();
            for (ModContainer mod : getActiveModList())
            {
                if (!mod.acceptableMinecraftVersionRange().containsVersion(minecraft.getProcessedVersion()))
                {
                    FMLLog.log.fatal("The mod {} does not wish to run in Minecraft version {}. You will have to remove it to play.", mod.getModId(), getMCVersionString());
                    WrongMinecraftVersionException ret = new WrongMinecraftVersionException(mod, getMCVersionString());
                    FMLLog.log.fatal(ret.getMessage());
                    wrongMinecraftExceptions.add(ret);
                    continue;
                }
                Map<String,ArtifactVersion> names = Maps.uniqueIndex(mod.getRequirements(), ArtifactVersion::getLabel);
                Set<ArtifactVersion> versionMissingMods = Sets.newHashSet();

                Set<String> missingMods = Sets.difference(names.keySet(), modVersions.keySet());
                if (!missingMods.isEmpty())
                {
                    FMLLog.log.fatal("The mod {} ({}) requires mods {} to be available", mod.getModId(), mod.getName(), missingMods);
                    for (String modid : missingMods)
                    {
                        versionMissingMods.add(names.get(modid));
                    }
                    MissingModsException ret = new MissingModsException(versionMissingMods, mod.getModId(), mod.getName());
                    FMLLog.log.fatal(ret.getMessage());
                    missingModsExceptions.add(ret);
                    continue;
                }
                reqList.putAll(mod.getModId(), names.keySet());
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
                    FMLLog.log.fatal("The mod {} ({}) requires mod versions {} to be available", mod.getModId(), mod.getName(), versionMissingMods);
                    MissingModsException ret = new MissingModsException(versionMissingMods, mod.getModId(), mod.getName());
                    FMLLog.log.fatal(ret.toString());
                    missingModsExceptions.add(ret);
                }
            }

            if (wrongMinecraftExceptions.isEmpty() && missingModsExceptions.isEmpty())
            {
                FMLLog.log.trace("All mod requirements are satisfied");
            }
            else if (missingModsExceptions.size()==1 && wrongMinecraftExceptions.isEmpty())
            {
                throw missingModsExceptions.get(0);
            }
            else if (wrongMinecraftExceptions.size()==1 && missingModsExceptions.isEmpty())
            {
                throw wrongMinecraftExceptions.get(0);
            }
            else
            {
                throw new MultipleModsErrored(wrongMinecraftExceptions, missingModsExceptions);
            }

            reverseDependencies = Multimaps.invertFrom(reqList, ArrayListMultimap.<String,String>create());
            ModSorter sorter = new ModSorter(getActiveModList(), namedMods);

            try
            {
                FMLLog.log.trace("Sorting mods into an ordered list");
                List<ModContainer> sortedMods = sorter.sort();
                // Reset active list to the sorted list
                modController.getActiveModList().clear();
                modController.getActiveModList().addAll(sortedMods);
                // And inject the sorted list into the overall list
                mods.removeAll(sortedMods);
                sortedMods.addAll(mods);
                mods = sortedMods;
                FMLLog.log.trace("Mod sorting completed successfully");
            }
            catch (ModSortingException sortException)
            {
                FMLLog.log.fatal("A dependency cycle was detected in the input mod set so an ordering cannot be determined");
                SortingExceptionData<ModContainer> exceptionData = sortException.getExceptionData();
                FMLLog.log.fatal("The first mod in the cycle is {}", exceptionData.getFirstBadNode());
                FMLLog.log.fatal("The mod cycle involves");
                for (ModContainer mc : exceptionData.getVisitedNodes())
                {
                    FMLLog.log.fatal("{} : before: {}, after: {}", mc.toString(), mc.getDependants(), mc.getDependencies());
                }
                FMLLog.log.error("The full error", sortException);
                throw sortException;
            }
        }
        finally
        {
            FMLLog.log.debug("Mod sorting data");
            int unprintedMods = mods.size();
            for (ModContainer mod : getActiveModList())
            {
                if (!mod.isImmutable())
                {
                    FMLLog.log.debug("\t{}({}:{}): {} ({})", mod.getModId(), mod.getName(), mod.getVersion(), mod.getSource().getName(), mod.getSortingRules());
                    unprintedMods--;
                }
            }
            if (unprintedMods == mods.size())
            {
                FMLLog.log.debug("No user mods found to sort");
            }
        }

    }

    /**
     * The primary loading code
     *
     *
     * The found resources are first loaded into the {@link #modClassLoader}
     * (always) then scanned for class resources matching the specification
     * above.
     *
     * If they provide the {@link Mod} annotation, they will be loaded as
     * "FML mods"
     *
     * Finally, if they are successfully loaded as classes, they are then added
     * to the available mod list.
     */
    private ModDiscoverer identifyMods(List<String> additionalContainers)
    {
        injectedContainers.addAll(additionalContainers);
        FMLLog.log.debug("Building injected Mod Containers {}", injectedContainers);
        mods.add(minecraft);
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
                FMLLog.log.error("A problem occurred instantiating the injected mod container {}", cont, e);
                throw new LoaderException(e);
            }
            mods.add(new InjectedModContainer(mc,mc.getSource()));
        }
        ModDiscoverer discoverer = new ModDiscoverer();
        FMLLog.log.debug("Attempting to load mods contained in the minecraft jar file and associated classes");
        discoverer.findClasspathMods(modClassLoader);
        FMLLog.log.debug("Minecraft jar mods loaded successfully");

        FMLLog.log.info("Found {} mods from the command line. Injecting into mod discoverer", ModListHelper.additionalMods.size());
        FMLLog.log.info("Searching {} for mods", canonicalModsDir.getAbsolutePath());
        discoverer.findModDirMods(canonicalModsDir, ModListHelper.additionalMods.values().toArray(new File[0]));
        File versionSpecificModsDir = new File(canonicalModsDir,mccversion);
        if (versionSpecificModsDir.isDirectory())
        {
            FMLLog.log.info("Also searching {} for mods", versionSpecificModsDir);
            discoverer.findModDirMods(versionSpecificModsDir);
        }

        mods.addAll(discoverer.identifyMods());
        identifyDuplicates(mods);
        namedMods = Maps.uniqueIndex(mods, ModContainer::getModId);
        FMLLog.log.info("Forge Mod Loader has identified {} mod{} to load", mods.size(), mods.size() != 1 ? "s" : "");
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
                FMLLog.log.fatal("Found a duplicate mod {} at {}", e.getElement().getModId(), dupsearch.get(e.getElement()));
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
            canonicalModsPath = modsDir.getCanonicalPath();
            canonicalConfigPath = configDir.getCanonicalPath();
            canonicalConfigDir = configDir.getCanonicalFile();
            canonicalModsDir = modsDir.getCanonicalFile();
        }
        catch (IOException ioe)
        {
            FMLLog.log.error("Failed to resolve loader directories: mods : {} ; config {}", canonicalModsDir.getAbsolutePath(),
                            configDir.getAbsolutePath(), ioe);
            throw new LoaderException(ioe);
        }

        if (!canonicalModsDir.exists())
        {
            FMLLog.log.info("No mod directory found, creating one: {}", canonicalModsPath);
            boolean dirMade = canonicalModsDir.mkdir();
            if (!dirMade)
            {
                FMLLog.log.fatal("Unable to create the mod directory {}", canonicalModsPath);
                throw new LoaderException(String.format("Unable to create the mod directory %s", canonicalModsPath));
            }
            FMLLog.log.info("Mod directory created successfully");
        }

        if (!canonicalConfigDir.exists())
        {
            FMLLog.log.debug("No config directory found, creating one: {}", canonicalConfigPath);
            boolean dirMade = canonicalConfigDir.mkdir();
            if (!dirMade)
            {
                FMLLog.log.fatal("Unable to create the config directory {}", canonicalConfigPath);
                throw new LoaderException();
            }
            FMLLog.log.info("Config directory created successfully");
        }

        if (!canonicalModsDir.isDirectory())
        {
            FMLLog.log.fatal("Attempting to load mods from {}, which is not a directory", canonicalModsPath);
            throw new LoaderException();
        }

        if (!configDir.isDirectory())
        {
            FMLLog.log.fatal("Attempting to load configuration from {}, which is not a directory", canonicalConfigPath);
            throw new LoaderException();
        }

        readInjectedDependencies();
    }

    public List<ModContainer> getModList()
    {
        return instance().mods != null ? ImmutableList.copyOf(instance().mods) : ImmutableList.<ModContainer>of();
    }

    /**
     * Used to setup a testharness with a single dummy mod instance for use with various testing hooks
     * @param containers A list of dummy containers that will be returned as "active" for all queries
     */
    public void setupTestHarness(ModContainer... containers)
    {
        modController = new LoadController(this);
        mods = Lists.newArrayList(containers);
        namedMods = Maps.uniqueIndex(mods, ModContainer::getModId);
        modController.transition(LoaderState.LOADING, false);
        modController.transition(LoaderState.CONSTRUCTING, false);
        ObjectHolderRegistry.INSTANCE.findObjectHolders(new ASMDataTable());
        modController.forceActiveContainer(containers[0]);
    }
    /**
     * Called from the hook to start mod loading. We trigger the
     * {@link #identifyMods()} and Constructing, Preinitalization, and Initalization phases here. Finally,
     * the mod list is frozen completely and is consider immutable from then on.
     * @param injectedModContainers containers to inject
     */
    public void loadMods(List<String> injectedModContainers)
    {
        progressBar = ProgressManager.push("Loading", 7);
        progressBar.step("Constructing Mods");
        initializeLoader();
        mods = Lists.newArrayList();
        namedMods = Maps.newHashMap();
        modController = new LoadController(this);
        modController.transition(LoaderState.LOADING, false);
        discoverer = identifyMods(injectedModContainers);
        ModAPIManager.INSTANCE.manageAPI(modClassLoader, discoverer);
        disableRequestedMods();
        modController.distributeStateMessage(FMLLoadEvent.class);
        sortModList();
        ModAPIManager.INSTANCE.cleanupAPIContainers(modController.getActiveModList());
        ModAPIManager.INSTANCE.cleanupAPIContainers(mods);
        mods = ImmutableList.copyOf(mods);
        for (File nonMod : discoverer.getNonModLibs())
        {
            if (nonMod.isFile())
            {
                FMLLog.log.info("FML has found a non-mod file {} in your mods directory. It will now be injected into your classpath. This could severe stability issues, it should be removed if possible.", nonMod.getName());
                try
                {
                    modClassLoader.addFile(nonMod);
                }
                catch (MalformedURLException e)
                {
                    FMLLog.log.error("Encountered a weird problem with non-mod file injection : {}", nonMod.getName(), e);
                }
            }
        }

        ConfigManager.loadData(discoverer.getASMTable());

        modController.transition(LoaderState.CONSTRUCTING, false);
        modController.distributeStateMessage(LoaderState.CONSTRUCTING, modClassLoader, discoverer.getASMTable(), reverseDependencies);

        FMLLog.log.debug("Mod signature data");
        FMLLog.log.debug(" \tValid Signatures:");
        for (ModContainer mod : getActiveModList())
        {
            if (mod.getSigningCertificate() != null)
                FMLLog.log.debug("\t\t({}) {}\t({}\t{})\t{}", CertificateHelper.getFingerprint(mod.getSigningCertificate()), mod.getModId(), mod.getName(), mod.getVersion(), mod.getSource().getName());
        }
        FMLLog.log.debug(" \tMissing Signatures:");
        for (ModContainer mod : getActiveModList())
        {
            if (mod.getSigningCertificate() == null)
                FMLLog.log.debug("\t\t{}\t({}\t{})\t{}", mod.getModId(), mod.getName(), mod.getVersion(), mod.getSource().getName());
        }
        if (getActiveModList().isEmpty())
        {
            FMLLog.log.debug("No user mod signature data found");
        }
        progressBar.step("Initializing mods Phase 1");
        modController.transition(LoaderState.PREINITIALIZATION, false);
    }

    public void preinitializeMods()
    {
        if (!modController.isInState(LoaderState.PREINITIALIZATION))
        {
            FMLLog.log.warn("There were errors previously. Not beginning mod initialization phase");
            return;
        }
        GameData.fireCreateRegistryEvents();
        ObjectHolderRegistry.INSTANCE.findObjectHolders(discoverer.getASMTable());
        ItemStackHolderInjector.INSTANCE.findHolders(discoverer.getASMTable());
        CapabilityManager.INSTANCE.injectCapabilities(discoverer.getASMTable());
        modController.distributeStateMessage(LoaderState.PREINITIALIZATION, discoverer.getASMTable(), canonicalConfigDir);
        GameData.fireRegistryEvents(rl -> !rl.equals(GameData.RECIPES));
        FMLCommonHandler.instance().fireSidedRegistryEvents();
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();
        ItemStackHolderInjector.INSTANCE.inject();
        modController.transition(LoaderState.INITIALIZATION, false);
        progressBar.step("Initializing Minecraft Engine");
    }

    private void disableRequestedMods()
    {
        String forcedModList = System.getProperty("fml.modStates", "");
        FMLLog.log.trace("Received a system property request \'{}\'",forcedModList);
        Map<String, String> sysPropertyStateList = Splitter.on(CharMatcher.anyOf(";:"))
                .omitEmptyStrings().trimResults().withKeyValueSeparator("=")
                .split(forcedModList);
        FMLLog.log.trace("System property request managing the state of {} mods", sysPropertyStateList.size());
        Map<String, String> modStates = Maps.newHashMap();

        forcedModFile = new File(canonicalConfigDir, "fmlModState.properties");
        Properties forcedModListProperties = new Properties();
        if (forcedModFile.exists() && forcedModFile.isFile())
        {
            FMLLog.log.trace("Found a mod state file {}", forcedModFile.getName());
            try
            {
                forcedModListProperties.load(new FileReader(forcedModFile));
                FMLLog.log.trace("Loaded states for {} mods from file", forcedModListProperties.size());
            }
            catch (Exception e)
            {
                FMLLog.log.info("An error occurred reading the fmlModState.properties file", e);
            }
        }
        modStates.putAll(Maps.fromProperties(forcedModListProperties));
        modStates.putAll(sysPropertyStateList);
        FMLLog.log.debug("After merging, found state information for {} mods", modStates.size());

        Map<String, Boolean> isEnabled = Maps.transformValues(modStates, Boolean::parseBoolean);

        for (Map.Entry<String, Boolean> entry : isEnabled.entrySet())
        {
            if (namedMods.containsKey(entry.getKey()))
            {
                FMLLog.log.info("Setting mod {} to enabled state {}", entry.getKey(), entry.getValue());
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
        List<String> branding = FMLCommonHandler.instance().getBrandings(false);

        Joiner.on(' ').skipNulls().appendTo(ret, branding);
        if (modController != null)
        {
            modController.printModStates(ret);
        }
        return ret.toString();
    }

    public String getFMLVersionString()
    {
        return "8.0.99.99";
    }

    public ModClassLoader getModClassLoader()
    {
        return modClassLoader;
    }

    public static void computeDependencies(String dependencyString, Set<ArtifactVersion> requirements, List<ArtifactVersion> dependencies, List<ArtifactVersion> dependants)
    {
        if (dependencyString == null || dependencyString.length() == 0)
        {
            return;
        }

        for (String dep : DEPENDENCYSPLITTER.split(dependencyString))
        {
            List<String> depparts = Lists.newArrayList(DEPENDENCYPARTSPLITTER.split(dep));
            if (depparts.size() != 2)
            {
                onParseFailure(dep, "Dependecy string needs 2 parts");
            }
            String target = depparts.get(1);
            boolean targetIsAll = target.startsWith("*");

            if (targetIsAll && target.length() > 1)
            {
                onParseFailure(dep, "Cannot have an \"all\" relationship with anything except pure *");
            }

            if (targetIsAll && target.indexOf('@') > -1)
            {
                onParseFailure(dep, "You cannot have a versioned dependency on everything");
            }

            List<String> instructions = Lists.newArrayList(DEPENDENCYINSTRUCTIONSSPLITTER.split(depparts.get(0)));
            if (!DEPENDENCYINSTRUCTIONS.containsAll(instructions))
            {
                onParseFailure(dep, String.format("Found invalid instructions. Only %s are allowed.", DEPENDENCYINSTRUCTIONS.toString().replace('[', ' ').replace(']', ' ').trim()));
            }
            boolean hasSide = false;
            boolean hasRequired = false;
            boolean hasOrder = false;
            for (String instruction : instructions)
            {
                if (!hasSide && (("client".equals(instruction) && FMLCommonHandler.instance().getSide() != Side.CLIENT) || ("server".equals(instruction) && FMLCommonHandler.instance().getSide() != Side.SERVER)))
                {
                    break;
                }
                else if (hasSide && ("client".equals(instruction) || "server".equals(instruction)))
                {
                    onParseFailure(dep, "Side or other instructions after Side have already been defined");
                }
                else
                {
                    hasSide = true;
                }

                if (hasSide)
                {
                    // If this is a required element, add it to the required list
                    if (!hasRequired && "required".equals(instruction))
                    {
                        if (!targetIsAll)
                        {
                            requirements.add(VersionParser.parseVersionReference(target));
                            hasRequired = true;
                        }
                        else
                        {
                            onParseFailure(dep, "You can't require everything");
                        }
                    }
                    else if (hasRequired && "required".equals(instruction))
                    {
                        onParseFailure(dep, "Required or other instructions after Required have already been defined");
                    }
                    else
                    {
                        hasRequired = true;
                    }

                    if (hasRequired)
                    {
                        if (!hasOrder)
                        {
                            // before elements are things we are loaded before (so they are our dependants)
                            if ("before".equals(instruction))
                            {
                                dependants.add(VersionParser.parseVersionReference(target));
                                hasOrder = true;
                            }
                            // after elements are things that load before we do (so they are out dependencies)
                            else if ("after".equals(instruction))
                            {
                                dependencies.add(VersionParser.parseVersionReference(target));
                                hasOrder = true;
                            }
                        }
                        else
                        {
                            onParseFailure(dep, "Order instruction has already been defined");
                        }
                    }
                }
            }
        }
    }
    
    private static void onParseFailure(String dependencyString, String cause)
    {
        FMLLog.log.warn("Unable to parse dependency string {}, cause - \"{}\"", dependencyString, cause);
        throw new LoaderException(String.format("Unable to parse dependency string %s, cause - \"%s\"", dependencyString, cause));
    }

    public Map<String,ModContainer> getIndexedModList()
    {
        return ImmutableMap.copyOf(namedMods);
    }

    public void initializeMods()
    {
        progressBar.step("Initializing mods Phase 2");
        CraftingHelper.loadRecipes(false);
        // Mod controller should be in the initialization state here
        modController.distributeStateMessage(LoaderState.INITIALIZATION);
        progressBar.step("Initializing mods Phase 3");
        modController.transition(LoaderState.POSTINITIALIZATION, false);
        modController.distributeStateMessage(FMLInterModComms.IMCEvent.class);
        ItemStackHolderInjector.INSTANCE.inject();
        modController.distributeStateMessage(LoaderState.POSTINITIALIZATION);
        progressBar.step("Finishing up");
        modController.transition(LoaderState.AVAILABLE, false);
        modController.distributeStateMessage(LoaderState.AVAILABLE);
        GameData.freezeData();
        FMLLog.log.info("Forge Mod Loader has successfully loaded {} mod{}", mods.size(), mods.size() == 1 ? "" : "s");
        progressBar.step("Completing Minecraft initialization");
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
            FMLLog.log.error("A fatal exception occurred during the server starting event", t);
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

    @Nullable
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
        return String.format("MCP %s", mcpversion);
    }

    public void serverStopped()
    {
        GameData.revertToFrozen();
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
            FMLLog.log.error("A fatal exception occurred during the server about to start event", t);
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

    boolean checkRemoteModList(Map<String, String> modList, Side side)
    {
        Set<String> remoteModIds = modList.keySet();
        Set<String> localModIds = namedMods.keySet();

        Set<String> difference = Sets.newLinkedHashSet(Sets.difference(localModIds, remoteModIds));
        for (Iterator<String> iterator = difference.iterator(); iterator.hasNext();)
        {
            String missingRemotely = iterator.next();
            ModState modState = modController.getModState(namedMods.get(missingRemotely));
            if (modState == ModState.DISABLED)
            {
                iterator.remove();
            }
        }

        if (difference.size() > 0)
            FMLLog.log.info("Attempting connection with missing mods {} at {}", difference, side);
        return true;
    }

    public void fireRemapEvent(Map<ResourceLocation, Map<ResourceLocation, Integer[]>> remaps, boolean isFreezing)
    {
        if (modController!=null)
        {
            modController.propogateStateMessage(new FMLModIdMappingEvent(remaps, isFreezing));
        }
    }

    public void runtimeDisableMod(String modId)
    {
        ModContainer mc = namedMods.get(modId);
        Disableable disableable = mc.canBeDisabled();
        if (disableable == Disableable.NEVER)
        {
            FMLLog.log.info("Cannot disable mod {} - it is never allowed to be disabled", modId);
            return;
        }
        if (disableable == Disableable.DEPENDENCIES)
        {
            FMLLog.log.info("Cannot disable mod {} - there are dependent mods that require its presence", modId);
            return;
        }
        if (disableable == Disableable.YES)
        {
            FMLLog.log.info("Runtime disabling mod {}", modId);
            modController.disableMod(mc);
            List<ModContainer> localmods = Lists.newArrayList(mods);
            localmods.remove(mc);
            mods = ImmutableList.copyOf(localmods);
        }

        try
        {
            Properties props = new Properties();
            props.load(new FileReader(forcedModFile));
            props.put(modId, "false");
            props.store(new FileWriter(forcedModFile), null);
        }
        catch (Exception e)
        {
            FMLLog.log.info("An error occurred writing the fml mod states file, your disabled change won't persist", e);
        }
    }

    public void loadingComplete()
    {
        ProgressManager.pop(progressBar);
        progressBar = null;
    }

    private ListMultimap<String,ArtifactVersion> injectedBefore = ArrayListMultimap.create();
    private ListMultimap<String,ArtifactVersion> injectedAfter = ArrayListMultimap.create();

    private void readInjectedDependencies()
    {
        File injectedDepFile = new File(getConfigDir(),"injectedDependencies.json");
        if (!injectedDepFile.exists())
        {
            FMLLog.log.debug("File {} not found. No dependencies injected", injectedDepFile.getAbsolutePath());
            return;
        }
        JsonParser parser = new JsonParser();
        JsonElement injectedDeps;
        try
        {
            injectedDeps = parser.parse(new FileReader(injectedDepFile));
            for (JsonElement el : injectedDeps.getAsJsonArray())
            {
                JsonObject jo = el.getAsJsonObject();
                String modId = jo.get("modId").getAsString();
                JsonArray deps = jo.get("deps").getAsJsonArray();
                for (JsonElement dep : deps)
                {
                    JsonObject depObj = dep.getAsJsonObject();
                    String type = depObj.get("type").getAsString();
                    if (type.equals("before")) {
                        injectedBefore.put(modId, VersionParser.parseVersionReference(depObj.get("target").getAsString()));
                    } else if (type.equals("after")) {
                        injectedAfter.put(modId, VersionParser.parseVersionReference(depObj.get("target").getAsString()));
                    } else {
                        FMLLog.log.error("Invalid dependency type {}", type);
                        throw new RuntimeException("Unable to parse type");
                    }
                }
            }
        } catch (Exception e)
        {
            FMLLog.log.error("Unable to parse {} - skipping", injectedDepFile);
            FMLLog.log.throwing(Level.ERROR, e);
            return;
        }
        FMLLog.log.debug("Loaded {} injected dependencies on modIds: {}", injectedBefore.size(), injectedBefore.keySet());
    }

    List<ArtifactVersion> getInjectedBefore(String modId)
    {
        return injectedBefore.get(modId);
    }
    List<ArtifactVersion> getInjectedAfter(String modId)
    {
        return injectedAfter.get(modId);
    }

    public final LoaderState getLoaderState()
    {
        return modController != null ? modController.getState() : LoaderState.NOINIT;
    }

    public void setActiveModContainer(@Nullable ModContainer container)
    {
        this.modController.forceActiveContainer(container);
    }
}
