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
package fml;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.minecraft.src.BaseMod;
import fml.ml.ModLoaderModContainer;
import fml.obf.FMLHandler;

public class Loader {
  private enum State {
    NOINIT, LOADING, PREINIT, INIT, POSTINIT, UP, ERRORED
  };

  private static Loader instance;
  public static Logger log = Logger.getLogger("ForgeModLoader");

  private static Pattern zipJar = Pattern.compile("([^\\s]+).(zip|jar)$");
  private static Pattern modClass = Pattern.compile("(.*/?)(mod\\_[^\\s]+).class$");

  private static String major="@MAJOR@";
  private static String minor="@MINOR@";
  private static String rev  ="@REV@";
  private static String build="@BUILD@";
  private static String mcversion="@MCVERSION@";
  
  private State state;
  private ModClassLoader modClassLoader;
  private List<ModContainer> mods;
  private Map<String,ModContainer> namedMods;

  public static Loader instance() {
    if (instance==null) {
      instance=new Loader();
    }
    return instance; 
  }
  private Loader() {
    Loader.log.setParent(FMLHandler.getMinecraftLogger());
    Loader.log.setLevel(Level.ALL);
    FileHandler fileHandler;
    try {
      fileHandler = new FileHandler("ForgeModLoader-%g.log", 0, 3);
      // We're stealing minecraft's log formatter
      fileHandler.setFormatter(FMLHandler.getMinecraftLogger().getHandlers()[0].getFormatter());
      fileHandler.setLevel(Level.ALL);
      Loader.log.addHandler(fileHandler);
    } catch (Exception e) {
      // Whatever - give up
    }
    log.info(String.format("Forge Mod Loader version %s.%s.%s.%s for Minecraft %s loading",major,minor,rev,build,mcversion));
  }

  private void sortModList() {
    // NOOP for a minute
  }

  private void preModInit() {
    state = State.PREINIT;
    log.fine("Beginning mod pre-initialization");
    for (ModContainer mod : mods) {
      if (mod.wantsPreInit()) {
        log.finer(String.format("Pre-initializing %s", mod.getSource()));
        mod.preInit();
        namedMods.put(mod.getName(), mod);
      }
    }
    log.fine("Mod pre-initialization complete");
  }

  private void modInit() {
    state = State.INIT;
    log.fine("Beginning mod initialization");
    for (ModContainer mod : mods) {
      log.finer(String.format("Initializing %s", mod.getName()));
      mod.init();
    }
    log.fine("Mod initialization complete");
  }

  private void postModInit() {
    state = State.POSTINIT;
    log.fine("Beginning mod post-initialization");
    for (ModContainer mod : mods) {
      if (mod.wantsPostInit()) {
        log.finer(String.format("Post-initializing %s", mod.getName()));
        mod.postInit();
      }
    }
    log.fine("Mod post-initialization complete");
  }

  private void load() {
    log.fine("Attempting to load mods contained in the minecraft jar file");
    
    attemptFileLoad(new File(".","minecraft_server.jar"));
    
    File modsDir = new File(".", "mods");
    String canonicalModsPath;
    try {
      canonicalModsPath = modsDir.getCanonicalPath();
    } catch (IOException ioe) {
      log.severe(String.format("Failed to resolve mods directory mods %s", modsDir.getAbsolutePath()));
      log.throwing("fml.server.Loader", "initialize", ioe);
      throw new LoaderException(ioe);
    }
    if (!modsDir.exists()) {
      log.fine(String.format("No mod directory found, creating one: %s", canonicalModsPath));
      try {
        modsDir.mkdir();
      } catch (Exception e) {
        log.throwing("fml.server.Loader", "initialize", e);
        throw new LoaderException(e);
      }
    }
    if (!modsDir.isDirectory()) {
      log.severe(String.format("Attempting to load mods from %s, which is not a directory", canonicalModsPath));
      LoaderException loaderException = new LoaderException();
      log.throwing("fml.server.Loader", "initialize", loaderException);
      throw loaderException;
    }
    log.info(String.format("Loading mods from %s",canonicalModsPath));
    File[] modList = modsDir.listFiles();
    // Sort the files into alphabetical order first
    Arrays.sort(modList);

    state = State.LOADING;
    for (File modFile : modList) {
      if (modFile.isDirectory()) {
        log.fine(String.format("Found a directory %s, attempting to load it", modFile.getName()));
        boolean modFound=attemptDirLoad(modFile);
        if (modFound) {
          log.fine(String.format("Directory %s loaded successfully", modFile.getName()));
        } else {
          log.info(String.format("Directory %s contained no mods", modFile.getName()));
        }
      } else {
        Matcher matcher = zipJar.matcher(modFile.getName());
        if (matcher.matches()) {
          log.fine(String.format("Found a zip or jar file %s, attempting to load it", matcher.group(0)));
          boolean modFound=attemptFileLoad(modFile);
          if (modFound) {
            log.fine(String.format("File %s loaded successfully", matcher.group(0)));
          } else {
            log.info(String.format("File %s contained no mods", matcher.group(0)));
          }
        }
      }
    }
    if (state == State.ERRORED) {
      log.severe("A problem has occured during mod loading, giving up now");
      throw new RuntimeException("Giving up please");
    }
    log.info(String.format("Forge Mod Loader has loaded %d mods",mods.size()));
  }

  private boolean attemptDirLoad(File modDir) {
    extendClassLoader(modDir);

    boolean foundAModClass=false;
    File[] content = modDir.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        return modClass.matcher(name).find();
      }
    });
    for (File modClassFile : content) {
      String clazzName = modClass.matcher(modClassFile.getName()).group(2);
      log.fine(String.format("Found a mod class %s in directory %s, attempting to load it", clazzName, modDir.getName()));
      loadModClass(modDir, modClassFile.getName(), clazzName);
      log.fine(String.format("Successfully loaded mod class %s", modClassFile.getName()));
      foundAModClass=true;
    }
    
    return foundAModClass;
  }

  private void loadModClass(File classSource, String classFileName, String clazzName) {
    try {
      Class<?> clazz = Class.forName(clazzName, false, modClassLoader);
      if (clazz.isAnnotationPresent(Mod.class)) {
        // an FML mod
        mods.add(FMLModContainer.buildFor(clazz));
      } else if (BaseMod.class.isAssignableFrom(clazz)) {
        log.fine(String.format("ModLoader BaseMod class %s found, loading", clazzName));
        @SuppressWarnings("unchecked")
        Class<? extends BaseMod> bmClazz = (Class<? extends BaseMod>) clazz;
        ModContainer mc=new ModLoaderModContainer(bmClazz,classSource.getCanonicalPath());
        mods.add(mc);
        log.fine(String.format("ModLoader BaseMod class %s loaded", clazzName));
      } else {
        // Unrecognized
      }
    } catch (Exception e) {
      log.warning(String.format("Failed to load mod class %s in %s", classFileName, classSource.getAbsoluteFile()));
      log.throwing("fml.server.Loader", "attemptLoad", e);
      state = State.ERRORED;
    }
  }

  private void extendClassLoader(File file) {
    if (modClassLoader == null) {
      modClassLoader = new ModClassLoader();
    }
    try {
      modClassLoader.addFile(file);
    } catch (MalformedURLException e) {
      throw new LoaderException(e);
    }
  }

  private boolean attemptFileLoad(File modFile) {
    extendClassLoader(modFile);

    boolean foundAModClass=false;
    try {
      ZipFile jar = new ZipFile(modFile);
      for (ZipEntry ze : Collections.list(jar.entries())) {
        Matcher match = modClass.matcher(ze.getName());
        if (match.matches()) {
          String pkg = match.group(1).replace('/', '.');
          String clazzName = pkg + match.group(2);
          log.fine(String.format("Found a mod class %s in file %s, attempting to load it", clazzName, modFile.getName()));
          loadModClass(modFile, ze.getName(), clazzName);
          log.fine(String.format("Mod class %s loaded successfully", clazzName, modFile.getName()));
          foundAModClass=true;
        }
      }
    } catch (Exception e) {
      log.warning(String.format("Zip file %s failed to read properly", modFile.getName()));
      log.throwing("fml.server.Loader", "attemptFileLoad", e);
      state = State.ERRORED;
    }
    return foundAModClass;
  }

  public static List<ModContainer> getModList() {
    return instance().mods;
  }

  public void loadMods() {
    state = State.NOINIT;
    mods = new ArrayList<ModContainer>();
    namedMods = new HashMap<String,ModContainer>();
    load();
    sortModList();
    // Make mod list immutable
    mods=Collections.unmodifiableList(mods);
    preModInit();
  }

  public void initializeMods() {
    modInit();
    postModInit();
    state = State.UP;
    log.info(String.format("Forge Mod Loader load complete, %d mods loaded",mods.size()));
  }
  
  public static boolean isModLoaded(String modname) {
    return instance().namedMods.containsKey(modname);
  }
}
