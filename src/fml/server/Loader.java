package fml.server;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import fml.Mod;
import fml.stubs.mcpserver.BaseMod;

public class Loader {
  private enum State { NOINIT, LOADING, PREINIT, INIT, POSTINIT, UP, ERRORED };
  private static State state;
  private static Logger LOG = Logger.getLogger("ForgeModLoader.Loader");

  private static List<ModContainer> mods;
  
  private static ModClassLoader modClassLoader;

  private static Pattern zipJar = Pattern.compile("([\\w]+).(zip|jar)$");
  private static Pattern modClass = Pattern.compile("(.*)(mod_[^\\s]+)\\.class$");

  public static Loader instance;
  
  public static void run() {
    instance=new Loader();
  }
  
  private Loader() {
    state=State.NOINIT;
    load();
    preModInit();
    modInit();
    postModInit();
    state=State.UP;
  }

  private void preModInit() {
    state=State.PREINIT;
    for (ModContainer mod : mods) {
      if (mod.wantsPreInit()) {
        mod.preInit();
      }
    }
  }

  private void modInit() {
    state=State.INIT;
    for (ModContainer mod : mods) {
       mod.init();
    }
  }

  private void postModInit() {
    state=State.POSTINIT;
    for (ModContainer mod : mods) {
      if (mod.wantsPostInit()) {
        mod.postInit();
      }
    }
  }

  private void load() {
    File modsDir = new File(".", "mods");
    String canonicalModsPath;
    try {
      canonicalModsPath = modsDir.getCanonicalPath();
    } catch (IOException ioe) {
      LOG.severe(String.format("Failed to resolve mods directory mods %s",modsDir.getAbsolutePath()));
      LOG.throwing("fml.server.Loader", "initialize", ioe);
      throw new LoaderException(ioe);
    }
    if (!modsDir.exists()) {
      LOG.fine(String.format("No mod directory found, creating one: %s", canonicalModsPath));
      try {
        modsDir.mkdir();
      } catch (Exception e) {
        LOG.throwing("fml.server.Loader", "initialize", e);
        throw new LoaderException(e);
      }
    }
    if (!modsDir.isDirectory()) {
      LOG.severe(String.format("Attempting to load mods from %s, which is not a directory", canonicalModsPath));
      LoaderException loaderException = new LoaderException();
      LOG.throwing("fml.server.Loader", "initialize", loaderException);
      throw loaderException;
    }
    File[] modList = modsDir.listFiles();
    // Sort the files into alphabetical order first
    Arrays.sort(modList);

    state=State.LOADING;
    for (File modFile : modList) {
      if (modFile.isDirectory()) {
        LOG.info(String.format("Found directory %s. Attempting load", modFile.getName()));
        attemptDirLoad(modFile);
      } else {
        Matcher matcher = zipJar.matcher(modFile.getName());
        if (matcher.find()) {
          LOG.info(String.format("Found zip or jar file %s. Attempting load.", matcher.group(0)));
          attemptFileLoad(modFile);
        }
      }
    }
    if (state==State.ERRORED) {
      LOG.severe("A problem has occured during mod loading. Giving up now");
      throw new RuntimeException("Giving up please");
    }
  }

  private void attemptDirLoad(File modDir) {
    extendClassLoader(modDir);
    
    File[] content=modDir.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        return modClass.matcher(name).find();
      }
    });
    for (File modClassFile : content) {
      LOG.fine(String.format("Found a mod class %s in directory %s. Attempting to load it",modClassFile.getName(),modDir.getName()));
      String clazzName=modClass.matcher(modClassFile.getName()).group(2);
      loadModClass(modDir, modClassFile.getName(), clazzName);
      LOG.fine(String.format("Successfully loaded mod class %s",modClassFile.getName()));
    }
  }

  private void loadModClass(File classSource, String classFileName, String clazzName) {
    try {
      Class<?> clazz=Class.forName(clazzName,true,modClassLoader);
      if (clazz.isAnnotationPresent(Mod.class)) {
        // an FML mod
        mods.add(FMLModContainer.buildFor(clazz));
      } else if (clazz.isAssignableFrom(BaseMod.class)) {
        // a modloader mod
      } else {
        // Unrecognized
      }
    } catch (ClassNotFoundException e) {
      LOG.warning(String.format("Failed to load mod class %s in %s",classFileName,classSource.getName()));
      LOG.throwing("fml.server.Loader", "attemptDirLoad", e);
      state=State.ERRORED;
    }
  }

  private void extendClassLoader(File file) {
    if (modClassLoader==null) {
      modClassLoader=new ModClassLoader();
    }
    try {
      modClassLoader.addFile(file);
    } catch (MalformedURLException e) {
      throw new LoaderException(e);
    }
  }

  private void attemptFileLoad(File modFile) {
    extendClassLoader(modFile);
    
    try {
      ZipFile jar=new ZipFile(modFile);
      for (ZipEntry ze : Collections.list(jar.entries())) {
        Matcher match=modClass.matcher(ze.getName());
        if (match.find()) {
          String pkg=match.group(1).replace('/', '.');
          String clazzName=pkg+match.group(2);
          loadModClass(modFile, ze.getName(), clazzName);
        }
      }
    } catch (Exception e) {
      LOG.warning(String.format("Zip file %s failed to read properly", modFile.getName()));
      LOG.throwing("fml.server.Loader", "attemptFileLoad", e);
      state=State.ERRORED;
    }
  }

  class LoaderException extends RuntimeException {
    public LoaderException(Exception wrapped) {
      super(wrapped);
    }

    public LoaderException() {
    }
  }
}
