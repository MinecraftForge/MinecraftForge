/*
 * The FML Forge Mod Loader suite.
 * Copyright (C) 2012 cpw
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package cpw.mods.fml.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Splitter.MapSplitter;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cpw.mods.fml.common.discovery.ContainerType;
import cpw.mods.fml.common.discovery.ModDiscoverer;
import cpw.mods.fml.common.functions.ModIdFunction;
import cpw.mods.fml.common.toposort.ModSorter;
import cpw.mods.fml.common.toposort.ModSortingException;
import cpw.mods.fml.common.toposort.TopologicalSort;

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
     * Our special logger for logging issues to. We copy various assets from the
     * Minecraft logger to acheive a similar appearance.
     */
    public static Logger log = Logger.getLogger("ForgeModLoader");

    /**
     * Build information for tracking purposes.
     */
    private static String major;
    private static String minor;
    private static String rev;
    private static String build;
    private static String mccversion;
    private static String mcsversion;

    /**
     * The {@link LoaderState} of the loader
     */
    private LoaderState state;
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

    public static Loader instance()
    {
        if (instance == null)
        {
            instance = new Loader();
        }

        return instance;
    }

    private Loader()
    {
        configureLogging();
        
        InputStream stream = getClass().getClassLoader().getResourceAsStream("fmlversion.properties");
        Properties properties = new Properties();

        if (stream != null)
        {
            try
            {
                properties.load(stream);
            }
            catch (IOException ex)
            {
                Loader.log.log(Level.SEVERE, "Could not get FML version information - corrupted installation detected!", ex);
                throw new LoaderException(ex);
            }
        }

        major = properties.getProperty("fmlbuild.major.number", "missing");
        minor = properties.getProperty("fmlbuild.minor.number", "missing");
        rev = properties.getProperty("fmlbuild.revision.number", "missing");
        build = properties.getProperty("fmlbuild.build.number", "missing");
        mccversion = properties.getProperty("fmlbuild.mcclientversion", "missing");
        mcsversion = properties.getProperty("fmlbuild.mcserverversion", "missing");
        
        log.info(String.format("Forge Mod Loader version %s.%s.%s.%s for Minecraft c:%s, s:%s loading", major, minor, rev, build, mccversion, mcsversion));
        
        modClassLoader = new ModClassLoader(getClass().getClassLoader());
    }

    /**
     * Configure the FML logger
     */
    private static void configureLogging()
    {
        FMLLogFormatter formatter = new FMLLogFormatter();
        if (FMLCommonHandler.instance().getMinecraftLogger() != null)
        {
            Loader.log.setParent(FMLCommonHandler.instance().getMinecraftLogger());
        }
        else
        {
            ConsoleHandler ch = new ConsoleHandler();
            Loader.log.setUseParentHandlers(false);
            Loader.log.addHandler(ch);
            ch.setFormatter(formatter);

        }
        Loader.log.setLevel(Level.ALL);
        try
        {
            File logPath = new File(FMLCommonHandler.instance().getMinecraftRootDirectory().getCanonicalPath(), "ForgeModLoader-%g.log");
            FileHandler fileHandler = new FileHandler(logPath.getPath(), 0, 3);
            fileHandler.setFormatter(new FMLLogFormatter());
            fileHandler.setLevel(Level.ALL);
            Loader.log.addHandler(fileHandler);
        }
        catch (Exception e)
        {
            // Whatever - give up
        }
    }

    /**
     * Sort the mods into a sorted list, using dependency information from the
     * containers. The sorting is performed using a {@link TopologicalSort}
     * based on the pre- and post- dependency information provided by the mods.
     */
    private void sortModList()
    {
        log.fine("Verifying mod requirements are satisfied");
        try
        {
            for (ModContainer mod : mods)
            {
                if (!namedMods.keySet().containsAll(mod.getRequirements()))
                {
                    log.log(Level.SEVERE, String.format("The mod %s (%s) requires mods %s to be available, one or more are not", mod.getModId(), mod.getName(), mod.getRequirements()));
                    throw new LoaderException();
                }
            }

            log.fine("All mod requirements are satisfied");
            
            ModSorter sorter = new ModSorter(mods, namedMods);

            try
            {
                log.fine("Sorting mods into an ordered list");
                mods = sorter.sort();
                log.fine("Mod sorting completed successfully");
            }
            catch (ModSortingException sortException)
            {
                log.severe("A dependency cycle was detected in the input mod set so an ordering cannot be determined");
                log.severe(String.format("The visited mod list is %s", sortException.getExceptionData().getVisitedNodes()));
                log.severe(String.format("The first mod in the cycle is %s", sortException.getExceptionData().getFirstBadNode()));
                log.throwing("Loader", "sortModList", sortException);
                throw new LoaderException(sortException);
            }
        }
        finally
        {
            log.fine("Mod sorting data:");
            for (ModContainer mod : mods)
            {
                log.fine(String.format("\t%s(%s): %s (%s)", mod.getModId(), mod.getName(), mod.getSource().getName(), mod.getSortingRules()));
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
     * determined to be {@link BaseMod} subclasses they are loaded as such.
     * 
     * Finally, if they are successfully loaded as classes, they are then added
     * to the available mod list.
     */
    private void identifyMods()
    {
        ModDiscoverer discoverer = new ModDiscoverer();
        log.fine("Attempting to load mods contained in the minecraft jar file and associated classes");
        discoverer.findClasspathMods(modClassLoader);
        log.fine("Minecraft jar mods loaded successfully");

        log.info(String.format("Searching %s for mods", canonicalModsDir.getAbsolutePath()));
        discoverer.findModDirMods(canonicalModsDir);

        mods = discoverer.identifyMods();
        namedMods = Maps.uniqueIndex(mods, new ModIdFunction());
        log.info(String.format("Forge Mod Loader has identified %d mod%s to load", mods.size(), mods.size() != 0 ? "s" : ""));
    }

    /**
     * @return
     */
    private void initializeLoader()
    {
        File minecraftDir = FMLCommonHandler.instance().getMinecraftRootDirectory();
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
            log.log(Level.SEVERE,
                    String.format("Failed to resolve loader directories: mods : %s ; config %s", canonicalModsDir.getAbsolutePath(),
                            configDir.getAbsolutePath()), ioe);
            throw new LoaderException(ioe);
        }

        if (!canonicalModsDir.exists())
        {
            log.log(Level.INFO, String.format("No mod directory found, creating one: %s", canonicalModsPath));
            boolean dirMade = canonicalModsDir.mkdir();
            if (!dirMade)
            {
                log.log(Level.SEVERE, String.format("Unable to create the mod directory %s", canonicalModsPath));
                throw new LoaderException();
            }
            log.log(Level.INFO, "Mod directory created successfully");
        }

        if (!canonicalConfigDir.exists())
        {
            log.fine(String.format("No config directory found, creating one: %s", canonicalConfigPath));
            boolean dirMade = canonicalConfigDir.mkdir();
            if (!dirMade)
            {
                log.log(Level.SEVERE, String.format("Unable to create the config directory %s", canonicalConfigPath));
                throw new LoaderException();
            }
            log.log(Level.INFO, "Config directory created successfully");
        }

        if (!canonicalModsDir.isDirectory())
        {
            log.log(Level.SEVERE, String.format("Attempting to load mods from %s, which is not a directory", canonicalModsPath));
            throw new LoaderException();
        }

        if (!configDir.isDirectory())
        {
            log.log(Level.SEVERE, String.format("Attempting to load configuration from %s, which is not a directory", canonicalConfigPath));
            throw new LoaderException();
        }
    }

    public static List<ModContainer> getModList()
    {
        return ImmutableList.copyOf(instance().mods);
    }

    /**
     * Called from the hook to start mod loading. We trigger the
     * {@link #identifyMods()} and {@link #preModInit()} phases here. Finally,
     * the mod list is frozen completely and is consider immutable from then on.
     */
    public void loadMods()
    {
        initializeLoader();
        state = LoaderState.NOINIT;
        mods = new ArrayList<ModContainer>();
        namedMods = new HashMap<String, ModContainer>();
        state = LoaderState.LOADING;
        identifyMods();
        disableRequestedMods();
        sortModList();
        mods = ImmutableList.copyOf(mods);
        modController = new LoadController(this);
        modController.runNextPhase(modClassLoader);
    }

    private void disableRequestedMods()
    {
        String disabledModList = System.getProperty("fml.disabledMods", "");
        log.log(Level.FINE, String.format("Received a system property request \'%s\'",disabledModList));
        Map<String, String> sysPropertyStateList = Splitter.on(CharMatcher.anyOf(";:"))
                .omitEmptyStrings().trimResults().withKeyValueSeparator("=")
                .split(disabledModList);
        log.log(Level.FINE, String.format("System property request managing the state of %d mods", sysPropertyStateList.size()));
        Map<String, String> modStates = Maps.newHashMap();
        
        File disabledModFile = new File(canonicalConfigDir, "fmlModState.properties");
        Properties disabledModListProperties = new Properties();
        if (disabledModFile.exists() && disabledModFile.isFile())
        {
            log.log(Level.FINE, String.format("Found a mod state file %s", disabledModFile.getName()));
            try
            {
                disabledModListProperties.load(new FileReader(disabledModFile));
                log.log(Level.FINE, String.format("Loaded states for %d mods from file", disabledModListProperties.size()));
            }
            catch (Exception e)
            {
                log.log(Level.INFO, "An error occurred reading the fmlModState.properties file", e);
            }
            log.log(Level.FINE, String.format("Found a mod state file %s", disabledModFile.getName()));
        }
        modStates.putAll(Maps.fromProperties(disabledModListProperties));
        modStates.putAll(sysPropertyStateList);
        log.log(Level.FINE, String.format("After merging, found state information for %d mods", modStates.size()));

        Map<String, Boolean> isEnabled = Maps.transformValues(modStates, new Function<String, Boolean>()
        {
            public Boolean apply(String input)
            {
                return !Boolean.parseBoolean(input);
            }
        });
        
        for (Map.Entry<String, Boolean> entry : isEnabled.entrySet())
        {
            if (namedMods.containsKey(entry.getKey()))
            {
                log.log(Level.INFO, String.format("Setting mod %s to enabled state %b", entry.getKey(), entry.getValue()));
                namedMods.get(entry.getKey()).setEnabledState(entry.getValue());
            }
        }
    }

    /**
     * Query if we know of a mod named modname
     * 
     * @param modname
     * @return
     */
    public static boolean isModLoaded(String modname)
    {
        return instance().namedMods.containsKey(modname);
    }

    /**
     * @return
     */
    public File getConfigDir()
    {
        return canonicalConfigDir;
    }

    public String getCrashInformation()
    {
        StringBuilder ret = new StringBuilder();
        Joiner.on('\n').appendTo(ret, FMLCommonHandler.instance().getBrandingStrings(getFMLVersionString()));
        modController.printModStates(ret);
        return ret.toString();
    }

    /**
     * @return
     */
    public String getFMLVersionString()
    {
        return String.format("FML v%s.%s.%s.%s", major, minor, rev, build);
    }

    /**
     * @return
     */
    public ClassLoader getModClassLoader()
    {
        return modClassLoader;
    }

    public void computeDependencies(String dependencyString, List<String> requirements, List<String> dependencies, List<String> dependants)
    {
        if (dependencyString == null || dependencyString.length() == 0)
        {
            return;
        }
    
        boolean parseFailure=false;
        
        for (String dep : DEPENDENCYSPLITTER.split(dependencyString))
        {
            List<String> depparts = Lists.newArrayList(DEPENDENCYPARTSPLITTER.split(dep));
            String instruction = depparts.get(0);
            String target = depparts.get(1);
            boolean targetIsAll = target.equals("*");
            // If we don't have two parts to each substring, this is an invalid dependency string
            if (depparts.size() != 2)
            {
                parseFailure=true;
                continue;
            }
            
            // If this is a required element, add it to the required list
            if ("required-before".equals(instruction) || "required-after".equals(instruction))
            {
                // You can't require everything
                if (!targetIsAll)
                {
                    requirements.add(target);
                }
                else
                {
                    parseFailure=true;
                    continue;
                }
            }
    
            // before elements are things we are loaded before (so they are our dependants)
            if ("required-before".equals(instruction) || "before".equals(instruction))
            {
            	dependants.add(target);
            }
            // after elements are things that load before we do (so they are out dependencies)
            else if ("required-after".equals(instruction) || "after".equals(instruction))
            {
                dependencies.add(target);
            }
            else
            {
                parseFailure=true;
            }
        }
    
        if (parseFailure)
        {
            throw new LoaderException();
        }
    }

    public Map<String,ModContainer> getIndexedModList()
    {
        return ImmutableMap.copyOf(namedMods);
    }
}
