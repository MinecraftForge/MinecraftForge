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
import java.io.FileFilter;
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
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import cpw.mods.fml.common.ModContainer.SourceType;
import cpw.mods.fml.common.toposort.ModSorter;
import cpw.mods.fml.common.toposort.TopologicalSort;

/**
 * The loader class performs the actual loading of the mod code from disk.
 *
 * <p>There are several {@link State}s to mod loading, triggered in two different stages from the FML handler code's hooks into the
 * minecraft code.</p>
 *
 * <ol>
 * <li>LOADING. Scanning the filesystem for mod containers to load (zips, jars, directories), adding them to the {@link #modClassLoader}
 * Scanning, the loaded containers for mod classes to load and registering them appropriately.</li>
 * <li>PREINIT. The mod classes are configured, they are sorted into a load order, and instances of the mods are constructed.</li>
 * <li>INIT. The mod instances are initialized. For BaseMod mods, this involves calling the load method.</li>
 * <li>POSTINIT. The mod instances are post initialized. For BaseMod mods this involves calling the modsLoaded method.</li>
 * <li>UP. The Loader is complete</li>
 * <li>ERRORED. The loader encountered an error during the LOADING phase and dropped to this state instead. It will not complete
 * loading from this state, but it attempts to continue loading before abandoning and giving a fatal error.</li>
 * </ol>
 *
 * Phase 1 code triggers the LOADING and PREINIT states. Phase 2 code triggers the INIT and POSTINIT states.
 *
 * @author cpw
 *
 */
public class Loader
{
    private static Pattern zipJar = Pattern.compile("(.+).(zip|jar)$");
    private static Pattern modClass = Pattern.compile("(.+/|)(mod\\_[^\\s$]+).class$");

    /**
     * The state enum used to help track state progression for the loader
     * @author cpw
     *
     */
    private enum State
    {
        NOINIT, LOADING, PREINIT, INIT, POSTINIT, UP, ERRORED
    };

    /**
     * The singleton instance
     */
    private static Loader instance;
    /**
     * Our special logger for logging issues to. We copy various assets from the Minecraft logger to acheive a similar appearance.
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
     * The {@link State} of the loader
     */
    private State state;
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
    private Exception capturedError;


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
        FMLLogFormatter formatter=new FMLLogFormatter();
        if (FMLCommonHandler.instance().getMinecraftLogger()!=null) {
            Loader.log.setParent(FMLCommonHandler.instance().getMinecraftLogger());
        } else {
            ConsoleHandler ch=new ConsoleHandler();
            Loader.log.setUseParentHandlers(false);
            Loader.log.addHandler(ch);
            ch.setFormatter(formatter);

        }
        Loader.log.setLevel(Level.ALL);
        try
        {
            File logPath=new File(FMLCommonHandler.instance().getMinecraftRootDirectory().getCanonicalPath(),"ForgeModLoader-%g.log");
            FileHandler fileHandler = new FileHandler(logPath.getPath(), 0, 3);
            // We're stealing minecraft's log formatter
            fileHandler.setFormatter(new FMLLogFormatter());
            fileHandler.setLevel(Level.ALL);
            Loader.log.addHandler(fileHandler);
        }
        catch (Exception e)
        {
            // Whatever - give up
        }
        InputStream stream = Loader.class.getClassLoader().getResourceAsStream("fmlversion.properties");
        Properties properties = new Properties();

        if (stream != null) {
            try {
                properties.load(stream);
                major      = properties.getProperty("fmlbuild.major.number","none");
                minor      = properties.getProperty("fmlbuild.minor.number","none");
                rev        = properties.getProperty("fmlbuild.revision.number","none");
                build      = properties.getProperty("fmlbuild.build.number","none");
                mccversion = properties.getProperty("fmlbuild.mcclientversion","none");
                mcsversion = properties.getProperty("fmlbuild.mcserverversion","none");
            } catch (IOException ex) {
                Loader.log.log(Level.SEVERE,"Could not get FML version information - corrupted installation detected!", ex);
                throw new LoaderException(ex);
            }
        }

        log.info(String.format("Forge Mod Loader version %s.%s.%s.%s for Minecraft c:%s, s:%s loading", major, minor, rev, build, mccversion, mcsversion));
        modClassLoader = new ModClassLoader();
    }

    /**
     * Sort the mods into a sorted list, using dependency information from the containers. The sorting is performed
     * using a {@link TopologicalSort} based on the pre- and post- dependency information provided by the mods.
     */
    private void sortModList()
    {
        log.fine("Verifying mod dependencies are satisfied");

        for (ModContainer mod : mods)
        {
            if (!namedMods.keySet().containsAll(mod.getDependencies()))
            {
                log.severe(String.format("The mod %s requires mods %s to be available, one or more are not", mod.getName(), mod.getDependencies()));
                LoaderException le = new LoaderException();
                log.throwing("Loader", "sortModList", le);
                throw new LoaderException();
            }
        }

        log.fine("All dependencies are satisfied");
        ModSorter sorter = new ModSorter(mods, namedMods);

        try
        {
            log.fine("Sorting mods into an ordered list");
            mods = sorter.sort();
            log.fine("Sorted mod list:");
            for (ModContainer mod : mods)
            {
                log.fine(String.format("\t%s: %s (%s)", mod.getName(), mod.getSource().getName(), mod.getSortingRules()));
            }
        }
        catch (IllegalArgumentException iae)
        {
            log.severe("A dependency cycle was detected in the input mod set so they cannot be loaded in order");
            log.throwing("Loader", "sortModList", iae);
            throw new LoaderException(iae);
        }
    }

    /**
     * The first mod initialization stage, performed immediately after the jar files and mod classes are loaded,
     * {@link State#PREINIT}. The mods are configured from their configuration data and instantiated (for BaseMod mods).
     */
    private void preModInit()
    {
        state = State.PREINIT;
        log.fine("Beginning mod pre-initialization");

        for (ModContainer mod : mods)
        {
            if (mod.wantsPreInit())
            {
                log.finer(String.format("Pre-initializing %s", mod.getSource()));
                try
                {
                    mod.preInit();
                }
                catch (Throwable t)
                {
                    log.log(Level.SEVERE, String.format("The mod from file %s has failed to load. This is likely a mod installation error.", mod.getSource().getName()), t);
                    throw new LoaderException(t);
                }
                namedMods.put(mod.getName(), mod);
            }
            mod.nextState();
        }
        // Link up mod metadatas

        for (ModContainer mod : mods) {
            if (mod.getMetadata()!=null) {
                mod.getMetadata().associate(namedMods);
            }

            FMLCommonHandler.instance().injectSidedProxyDelegate(mod);
        }
        log.fine("Mod pre-initialization complete");
    }

    /**
     * The main mod initialization stage, performed on the sorted mod list.
     */
    private void modInit()
    {
        state = State.INIT;
        log.fine("Beginning mod initialization");

        for (ModContainer mod : mods)
        {
            log.finer(String.format("Initializing %s", mod.getName()));
            mod.init();
            mod.nextState();
        }

        log.fine("Mod initialization complete");
    }

    private void postModInit()
    {
        state = State.POSTINIT;
        log.fine("Beginning mod post-initialization");

        for (ModContainer mod : mods)
        {
            if (mod.wantsPostInit())
            {
                log.finer(String.format("Post-initializing %s", mod.getName()));
                mod.postInit();
                mod.nextState();
            }
        }

        log.fine("Mod post-initialization complete");
    }

    /**
     * The primary loading code
     *
     * This is visited during first initialization by Minecraft to scan and load the mods
     * from all sources
     * 1. The minecraft jar itself (for loading of in jar mods- I would like to remove this if possible but forge depends on it at present)
     * 2. The mods directory with expanded subdirs, searching for mods named mod_*.class
     * 3. The mods directory for zip and jar files, searching for mod classes named mod_*.class again
     *
     * The found resources are first loaded into the {@link #modClassLoader} (always) then scanned for class resources matching the specification above.
     *
     * If they provide the {@link Mod} annotation, they will be loaded as "FML mods", which currently is effectively a NO-OP.
     * If they are determined to be {@link BaseMod} subclasses they are loaded as such.
     *
     * Finally, if they are successfully loaded as classes, they are then added to the available mod list.
     */
    private void load()
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
        }
        catch (IOException ioe)
        {
            log.severe(String.format("Failed to resolve mods directory mods %s", modsDir.getAbsolutePath()));
            log.throwing("fml.server.Loader", "initialize", ioe);
            throw new LoaderException(ioe);
        }

        if (!modsDir.exists())
        {
            log.fine(String.format("No mod directory found, creating one: %s", canonicalModsPath));

            try
            {
                modsDir.mkdir();
            }
            catch (Exception e)
            {
                log.throwing("fml.server.Loader", "initialize", e);
                throw new LoaderException(e);
            }
        }

        if (!configDir.exists())
        {
            log.fine(String.format("No config directory found, creating one: %s", canonicalConfigPath));

            try
            {
                configDir.mkdir();
            }
            catch (Exception e)
            {
                log.throwing("fml.server.Loader", "initialize", e);
                throw new LoaderException(e);
            }
        }

        if (!modsDir.isDirectory())
        {
            log.severe(String.format("Attempting to load mods from %s, which is not a directory", canonicalModsPath));
            LoaderException loaderException = new LoaderException();
            log.throwing("fml.server.Loader", "initialize", loaderException);
            throw loaderException;
        }

        if (!configDir.isDirectory())
        {
            log.severe(String.format("Attempting to load configuration from %s, which is not a directory", canonicalConfigPath));
            LoaderException loaderException = new LoaderException();
            log.throwing("fml.server.Loader", "initialize", loaderException);
            throw loaderException;
        }

        state = State.LOADING;
        log.fine("Attempting to load mods contained in the minecraft jar file and associated classes");
        File[] minecraftSources=modClassLoader.getParentSources();
        if (minecraftSources.length==1 && minecraftSources[0].isFile()) {
            log.fine(String.format("Minecraft is a file at %s, loading",minecraftSources[0].getAbsolutePath()));
            attemptFileLoad(minecraftSources[0], SourceType.CLASSPATH);
        } else {
            for (int i=0; i<minecraftSources.length; i++) {
                if (minecraftSources[i].isFile()) {
                    log.fine(String.format("Found a minecraft related file at %s, loading",minecraftSources[i].getAbsolutePath()));
                    attemptFileLoad(minecraftSources[i], SourceType.CLASSPATH);
                } else if (minecraftSources[i].isDirectory()) {
                    log.fine(String.format("Found a minecraft related directory at %s, loading",minecraftSources[i].getAbsolutePath()));
                    attemptDirLoad(minecraftSources[i],"",SourceType.CLASSPATH);
                }
            }
        }
        log.fine("Minecraft jar mods loaded successfully");

        log.info(String.format("Loading mods from %s", canonicalModsPath));
        File[] modList = modsDir.listFiles();
        // Sort the files into alphabetical order first
        Arrays.sort(modList);

        for (File modFile : modList)
        {
            if (modFile.isDirectory())
            {
                log.fine(String.format("Found a directory %s, attempting to load it", modFile.getName()));
                boolean modFound = attemptDirLoad(modFile,"", SourceType.DIR);

                if (modFound)
                {
                    log.fine(String.format("Directory %s loaded successfully", modFile.getName()));
                }
                else
                {
                    log.info(String.format("Directory %s contained no mods", modFile.getName()));
                }
            }
            else
            {
                Matcher matcher = zipJar.matcher(modFile.getName());

                if (matcher.matches())
                {
                    log.fine(String.format("Found a zip or jar file %s, attempting to load it", matcher.group(0)));
                    boolean modFound = attemptFileLoad(modFile, SourceType.JAR);

                    if (modFound)
                    {
                        log.fine(String.format("File %s loaded successfully", matcher.group(0)));
                    }
                    else
                    {
                        log.info(String.format("File %s contained no mods", matcher.group(0)));
                    }
                }
            }
        }

        if (state == State.ERRORED)
        {
            log.severe("A problem has occured during mod loading. Likely a corrupt jar is located in your mods directory");
            throw new LoaderException(capturedError);
        }

        log.info(String.format("Forge Mod Loader has loaded %d mods", mods.size()));
    }

    private boolean attemptDirLoad(File modDir, String path, SourceType sourceType)
    {
        if (path.length()==0) {
            extendClassLoader(modDir);
        }
        boolean foundAModClass = false;
        File[] content = modDir.listFiles(new FileFilter()
        {
            @Override
            public boolean accept(File file)
            {
                return (file.isFile() && modClass.matcher(file.getName()).find()) || file.isDirectory();
            }
        });

        // Always sort our content
        Arrays.sort(content);
        for (File file : content)
        {
            if (file.isDirectory()) {
                log.finest(String.format("Recursing into package %s", path+file.getName()));
                foundAModClass|=attemptDirLoad(file,path+file.getName()+".", sourceType);
                continue;
            }
            Matcher fname = modClass.matcher(file.getName());
            if (!fname.find()) {
                continue;
            }
            String clazzName=path+fname.group(2);
            try
            {
                log.fine(String.format("Found a mod class %s in directory %s, attempting to load it", clazzName, modDir.getName()));
                loadModClass(modDir, file.getName(), clazzName, sourceType);
                log.fine(String.format("Successfully loaded mod class %s", file.getName()));
                foundAModClass = true;
            }
            catch (Exception e)
            {
                log.severe(String.format("File %s failed to read properly", file.getName()));
                log.throwing("fml.server.Loader", "attemptDirLoad", e);
                state = State.ERRORED;
                capturedError = e;
            }
        }

        return foundAModClass;
    }

    private void loadModClass(File classSource, String classFileName, String clazzName, SourceType sourceType)
    {
        try
        {
            Class<?> clazz = Class.forName(clazzName, false, modClassLoader);

            ModContainer mod=null;
            if (clazz.isAnnotationPresent(Mod.class))
            {
                // an FML mod
                log.severe("Currently, the FML mod type is disabled");
                throw new LoaderException();
//                log.fine(String.format("FML mod class %s found, loading", clazzName));
//                mod = FMLModContainer.buildFor(clazz);
//                log.fine(String.format("FML mod class %s loaded", clazzName));
            }
            else if (FMLCommonHandler.instance().isModLoaderMod(clazz))
            {
                log.fine(String.format("ModLoader BaseMod class %s found, loading", clazzName));
                mod = FMLCommonHandler.instance().loadBaseModMod(clazz, classSource.getCanonicalFile());
                log.fine(String.format("ModLoader BaseMod class %s loaded", clazzName));
            }
            else
            {
                // Unrecognized
            }
            if (mod!=null) {
                mod.setSourceType(sourceType);
                FMLCommonHandler.instance().loadMetadataFor(mod);
                mods.add(mod);
                mod.nextState();
            }
        }
        catch (Throwable e)
        {
            log.warning(String.format("Failed to load mod class %s in %s", classFileName, classSource.getAbsoluteFile()));
            log.throwing("fml.server.Loader", "attemptLoad", e);
            throw new LoaderException(e);
        }
    }

    private void extendClassLoader(File file)
    {
        try
        {
            modClassLoader.addFile(file);
        }
        catch (MalformedURLException e)
        {
            throw new LoaderException(e);
        }
    }

    private boolean attemptFileLoad(File modFile, SourceType sourceType)
    {
        extendClassLoader(modFile);
        boolean foundAModClass = false;

        ZipFile jar = null;
        try
        {
            jar = new ZipFile(modFile);

            for (ZipEntry ze : Collections.list(jar.entries()))
            {
                Matcher match = modClass.matcher(ze.getName());

                if (match.matches())
                {
                    String pkg = match.group(1).replace('/', '.');
                    String clazzName = pkg + match.group(2);
                    log.fine(String.format("Found a mod class %s in file %s, attempting to load it", clazzName, modFile.getName()));
                    loadModClass(modFile, ze.getName(), clazzName, sourceType);
                    log.fine(String.format("Mod class %s loaded successfully", clazzName, modFile.getName()));
                    foundAModClass = true;
                }
            }
        }
        catch (Exception e)
        {
            log.severe(String.format("Zip file %s failed to read properly", modFile.getName()));
            log.throwing("fml.server.Loader", "attemptFileLoad", e);
            state = State.ERRORED;
            capturedError = e;
        }
        finally
        {
            if (jar != null)
            {
                try
                {
                    jar.close();
                }
                catch (Exception e)
                {
                }
            }
        }

        return foundAModClass;
    }

    public static List<ModContainer> getModList()
    {
        return instance().mods;
    }

    /**
     * Called from the hook to start mod loading. We trigger the {@link #load()} and {@link #preModInit()} phases here.
     * Finally, the mod list is frozen completely and is consider immutable from then on.
     */
    public void loadMods()
    {
        state = State.NOINIT;
        mods = new ArrayList<ModContainer>();
        namedMods = new HashMap<String, ModContainer>();
        load();
        preModInit();
        sortModList();
        // Make mod list immutable
        mods = Collections.unmodifiableList(mods);
    }

    /**
     * Complete the initialization of the mods {@link #initializeMods()} and {@link #postModInit()} and mark ourselves up and ready to run.
     */
    public void initializeMods()
    {
        modInit();
        postModInit();
        for (ModContainer mod : getModList()) {
            mod.nextState();
        }
        state = State.UP;
        log.info(String.format("Forge Mod Loader load complete, %d mods loaded", mods.size()));
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
        StringBuffer ret = new StringBuffer();
        for (String brand : FMLCommonHandler.instance().getBrandingStrings(String.format("Forge Mod Loader version %s.%s.%s.%s for Minecraft %s", major, minor, rev, build, mccversion))) {
            ret.append(brand).append("\n");
        }
        for (ModContainer mod : mods)
        {
            ret.append(String.format("\t%s : %s (%s)\n",mod.getName(), mod.getModState(), mod.getSource().getName()));
        }
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
}
