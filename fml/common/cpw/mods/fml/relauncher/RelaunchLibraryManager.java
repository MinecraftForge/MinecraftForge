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

import cpw.mods.fml.common.CertificateHelper;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

public class RelaunchLibraryManager
{
    private static String[] rootPlugins =  { "cpw.mods.fml.relauncher.FMLCorePlugin" , "net.minecraftforge.classloading.FMLForgePlugin" };
    private static List<String> loadedLibraries = new ArrayList<String>();
    private static Map<IFMLLoadingPlugin, File> pluginLocations;
    private static List<IFMLLoadingPlugin> loadPlugins;
    private static List<ILibrarySet> libraries;
    public static void handleLaunch(File mcDir, RelaunchClassLoader actualClassLoader)
    {
        pluginLocations = new HashMap<IFMLLoadingPlugin, File>();
        loadPlugins = new ArrayList<IFMLLoadingPlugin>();
        libraries = new ArrayList<ILibrarySet>();
        for (String s : rootPlugins)
        {
            try
            {
                IFMLLoadingPlugin plugin = (IFMLLoadingPlugin) Class.forName(s, true, actualClassLoader).newInstance();
                loadPlugins.add(plugin);
                for (String libName : plugin.getLibraryRequestClass())
                {
                    libraries.add((ILibrarySet) Class.forName(libName, true, actualClassLoader).newInstance());
                }
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

        downloadMonitor.updateProgressString("All core mods are successfully located");
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
                actualClassLoader.addTransformerExclusion(s);
                Class<?> coreModClass = Class.forName(s, true, actualClassLoader);
                TransformerExclusions trExclusions = coreModClass.getAnnotation(IFMLLoadingPlugin.TransformerExclusions.class);
                if (trExclusions!=null)
                {
                    for (String st : trExclusions.value())
                    {
                        actualClassLoader.addTransformerExclusion(st);
                    }
                }
                IFMLLoadingPlugin plugin = (IFMLLoadingPlugin) coreModClass.newInstance();
                loadPlugins.add(plugin);
                if (plugin.getLibraryRequestClass()!=null)
                {
                    for (String libName : plugin.getLibraryRequestClass())
                    {
                        libraries.add((ILibrarySet) Class.forName(libName, true, actualClassLoader).newInstance());
                    }
                }
            }
            catch (Throwable e)
            {
                FMLRelaunchLog.log(Level.SEVERE,e,"Exception occured trying to load coremod %s",s);
                throw new RuntimeException(e);
            }
        }
        discoverCoreMods(mcDir, actualClassLoader, loadPlugins, libraries);

        List<Throwable> caughtErrors = new ArrayList<Throwable>();
        try
        {
            File libDir;
            try
            {
                libDir = setupLibDir(mcDir);
            }
            catch (Exception e)
            {
                caughtErrors.add(e);
                return;
            }

            for (ILibrarySet lib : libraries)
            {
                for (int i=0; i<lib.getLibraries().length; i++)
                {
                    boolean download = false;
                    String libName = lib.getLibraries()[i];
                    String targFileName = libName.lastIndexOf('/')>=0 ? libName.substring(libName.lastIndexOf('/')) : libName;
                    String checksum = lib.getHashes()[i];
                    File libFile = new File(libDir, targFileName);
                    if (!libFile.exists())
                    {
                        try
                        {
                            downloadFile(libFile, lib.getRootURL(), libName, checksum);
                            download = true;
                        }
                        catch (Throwable e)
                        {
                            caughtErrors.add(e);
                            continue;
                        }
                    }

                    if (libFile.exists() && !libFile.isFile())
                    {
                        caughtErrors.add(new RuntimeException(String.format("Found a file %s that is not a normal file - you should clear this out of the way", libName)));
                        continue;
                    }

                    if (!download)
                    {
                        try
                        {
                            FileInputStream fis = new FileInputStream(libFile);
                            FileChannel chan = fis.getChannel();
                            MappedByteBuffer mappedFile = chan.map(MapMode.READ_ONLY, 0, libFile.length());
                            String fileChecksum = generateChecksum(mappedFile);
                            fis.close();
                            // bad checksum and I did not download this file
                            if (!checksum.equals(fileChecksum))
                            {
                                caughtErrors.add(new RuntimeException(String.format("The file %s was found in your lib directory and has an invalid checksum %s (expecting %s) - it is unlikely to be the correct download, please move it out of the way and try again.", libName, fileChecksum, checksum)));
                                continue;
                            }
                        }
                        catch (Exception e)
                        {
                            FMLRelaunchLog.log(Level.SEVERE, e, "The library file %s could not be validated", libFile.getName());
                            caughtErrors.add(new RuntimeException(String.format("The library file %s could not be validated", libFile.getName()),e));
                            continue;
                        }
                    }

                    if (!download)
                    {
                        downloadMonitor.updateProgressString("Found library file %s present and correct in lib dir", libName);
                    }
                    else
                    {
                        downloadMonitor.updateProgressString("Library file %s was downloaded and verified successfully", libName);
                    }

                    try
                    {
                        actualClassLoader.addURL(libFile.toURI().toURL());
                        loadedLibraries.add(libName);
                    }
                    catch (MalformedURLException e)
                    {
                        caughtErrors.add(new RuntimeException(String.format("Should never happen - %s is broken - probably a somehow corrupted download. Delete it and try again.", libFile.getName()), e));
                    }
                }
            }
        }
        finally
        {
            if (downloadMonitor.shouldStopIt())
            {
                return;
            }
            if (!caughtErrors.isEmpty())
            {
                FMLRelaunchLog.severe("There were errors during initial FML setup. " +
                		"Some files failed to download or were otherwise corrupted. " +
                		"You will need to manually obtain the following files from " +
                		"these download links and ensure your lib directory is clean. ");
                for (ILibrarySet set : libraries)
                {
                    for (String file : set.getLibraries())
                    {
                        FMLRelaunchLog.severe("*** Download "+set.getRootURL(), file);
                    }
                }
                FMLRelaunchLog.severe("<===========>");
                FMLRelaunchLog.severe("The following is the errors that caused the setup to fail. " +
                		"They may help you diagnose and resolve the issue");
                for (Throwable t : caughtErrors)
                {
                    if (t.getMessage()!=null)
                    {
                        FMLRelaunchLog.severe(t.getMessage());
                    }
                }
                FMLRelaunchLog.severe("<<< ==== >>>");
                FMLRelaunchLog.severe("The following is diagnostic information for developers to review.");
                for (Throwable t : caughtErrors)
                {
                    FMLRelaunchLog.log(Level.SEVERE, t, "Error details");
                }
                throw new RuntimeException("A fatal error occured and FML cannot continue");
            }
        }

        for (IFMLLoadingPlugin plug : loadPlugins)
        {
            if (plug.getASMTransformerClass()!=null)
            {
                for (String xformClass : plug.getASMTransformerClass())
                {
                    actualClassLoader.registerTransformer(xformClass);
                }
            }
        }

        downloadMonitor.updateProgressString("Running coremod plugins");
        Map<String,Object> data = new HashMap<String,Object>();
        data.put("mcLocation", mcDir);
        data.put("coremodList", loadPlugins);
        for (IFMLLoadingPlugin plugin : loadPlugins)
        {
            downloadMonitor.updateProgressString("Running coremod plugin %s", plugin.getClass().getSimpleName());
            data.put("coremodLocation", pluginLocations.get(plugin));
            plugin.injectData(data);
            String setupClass = plugin.getSetupClass();
            if (setupClass != null)
            {
                try
                {
                    IFMLCallHook call = (IFMLCallHook) Class.forName(setupClass, true, actualClassLoader).newInstance();
                    Map<String,Object> callData = new HashMap<String, Object>();
                    callData.put("classLoader", actualClassLoader);
                    call.injectData(callData);
                    call.call();
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
            downloadMonitor.updateProgressString("Coremod plugin %s run successfully", plugin.getClass().getSimpleName());

            String modContainer = plugin.getModContainerClass();
            if (modContainer != null)
            {
                FMLInjectionData.containers.add(modContainer);
            }
        }
        try
        {
            downloadMonitor.updateProgressString("Validating minecraft");
            Class<?> loaderClazz = Class.forName("cpw.mods.fml.common.Loader", true, actualClassLoader);
            Method m = loaderClazz.getMethod("injectData", Object[].class);
            m.invoke(null, (Object)FMLInjectionData.data());
            m = loaderClazz.getMethod("instance");
            m.invoke(null);
            downloadMonitor.updateProgressString("Minecraft validated, launching...");
            downloadBuffer = null;
        }
        catch (Exception e)
        {
            // Load in the Loader, make sure he's ready to roll - this will initialize most of the rest of minecraft here
            System.out.println("A CRITICAL PROBLEM OCCURED INITIALIZING MINECRAFT - LIKELY YOU HAVE AN INCORRECT VERSION FOR THIS FML");
            throw new RuntimeException(e);
        }
    }

    private static void discoverCoreMods(File mcDir, RelaunchClassLoader classLoader, List<IFMLLoadingPlugin> loadPlugins, List<ILibrarySet> libraries)
    {
        downloadMonitor.updateProgressString("Discovering coremods");
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
        Arrays.sort(coreModList);

        for (File coreMod : coreModList)
        {
            downloadMonitor.updateProgressString("Found a candidate coremod %s", coreMod.getName());
            JarFile jar;
            Attributes mfAttributes;
            try
            {
                jar = new JarFile(coreMod);
                mfAttributes = jar.getManifest().getMainAttributes();
            }
            catch (IOException ioe)
            {
                FMLRelaunchLog.log(Level.SEVERE, ioe, "Unable to read the coremod jar file %s - ignoring", coreMod.getName());
                continue;
            }

            String fmlCorePlugin = mfAttributes.getValue("FMLCorePlugin");
            if (fmlCorePlugin == null)
            {
                FMLRelaunchLog.severe("The coremod %s does not contain a valid jar manifest- it will be ignored", coreMod.getName());
                continue;
            }

//            String className = fmlCorePlugin.replace('.', '/').concat(".class");
//            JarEntry ent = jar.getJarEntry(className);
//            if (ent ==null)
//            {
//                FMLLog.severe("The coremod %s specified %s as it's loading class but it does not include it - it will be ignored", coreMod.getName(), fmlCorePlugin);
//                continue;
//            }
//            try
//            {
//                Class<?> coreModClass = Class.forName(fmlCorePlugin, false, classLoader);
//                FMLLog.severe("The coremods %s specified a class %s that is already present in the classpath - it will be ignored", coreMod.getName(), fmlCorePlugin);
//                continue;
//            }
//            catch (ClassNotFoundException cnfe)
//            {
//                // didn't find it, good
//            }
            try
            {
                classLoader.addURL(coreMod.toURI().toURL());
            }
            catch (MalformedURLException e)
            {
                FMLRelaunchLog.log(Level.SEVERE, e, "Unable to convert file into a URL. weird");
                continue;
            }
            try
            {
                downloadMonitor.updateProgressString("Loading coremod %s", coreMod.getName());
                classLoader.addTransformerExclusion(fmlCorePlugin);
                Class<?> coreModClass = Class.forName(fmlCorePlugin, true, classLoader);
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
                if (plugin.getLibraryRequestClass()!=null)
                {
                    for (String libName : plugin.getLibraryRequestClass())
                    {
                        libraries.add((ILibrarySet) Class.forName(libName, true, classLoader).newInstance());
                    }
                }
                downloadMonitor.updateProgressString("Loaded coremod %s", coreMod.getName());
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
     * @param mcDir
     * @return
     */
    private static File setupLibDir(File mcDir)
    {
        File libDir = new File(mcDir,"lib");
        try
        {
            libDir = libDir.getCanonicalFile();
        }
        catch (IOException e)
        {
            throw new RuntimeException(String.format("Unable to canonicalize the lib dir at %s", mcDir.getName()),e);
        }
        if (!libDir.exists())
        {
            libDir.mkdir();
        }
        else if (libDir.exists() && !libDir.isDirectory())
        {
            throw new RuntimeException(String.format("Found a lib file in %s that's not a directory", mcDir.getName()));
        }
        return libDir;
    }

    /**
     * @param mcDir
     * @return
     */
    private static File setupCoreModDir(File mcDir)
    {
        File coreModDir = new File(mcDir,"coremods");
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

    private static void downloadFile(File libFile, String rootUrl,String realFilePath, String hash)
    {
        try
        {
            URL libDownload = new URL(String.format(rootUrl,realFilePath));
            downloadMonitor.updateProgressString("Downloading file %s", libDownload.toString());
            FMLRelaunchLog.info("Downloading file %s", libDownload.toString());
            URLConnection connection = libDownload.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestProperty("User-Agent", "FML Relaunch Downloader");
            int sizeGuess = connection.getContentLength();
            performDownload(connection.getInputStream(), sizeGuess, hash, libFile);
            downloadMonitor.updateProgressString("Download complete");
            FMLRelaunchLog.info("Download complete");
        }
        catch (Exception e)
        {
            if (downloadMonitor.shouldStopIt())
            {
                FMLRelaunchLog.warning("You have stopped the downloading operation before it could complete");
                return;
            }
            if (e instanceof RuntimeException) throw (RuntimeException)e;
            FMLRelaunchLog.severe("There was a problem downloading the file %s automatically. Perhaps you " +
            		"have an environment without internet access. You will need to download " +
            		"the file manually or restart and let it try again\n", libFile.getName());
            libFile.delete();
            throw new RuntimeException("A download error occured", e);
        }
    }

    public static List<String> getLibraries()
    {
        return loadedLibraries;
    }

    private static ByteBuffer downloadBuffer = ByteBuffer.allocateDirect(1 << 22);
    static IDownloadDisplay downloadMonitor;

    private static void performDownload(InputStream is, int sizeGuess, String validationHash, File target)
    {
        if (sizeGuess > downloadBuffer.capacity())
        {
            throw new RuntimeException(String.format("The file %s is too large to be downloaded by FML - the coremod is invalid", target.getName()));
        }
        downloadBuffer.clear();

        int bytesRead, fullLength = 0;

        downloadMonitor.resetProgress(sizeGuess);
        try
        {
            downloadMonitor.setPokeThread(Thread.currentThread());
            byte[] smallBuffer = new byte[1024];
            while ((bytesRead = is.read(smallBuffer)) >= 0) {
                downloadBuffer.put(smallBuffer, 0, bytesRead);
                fullLength += bytesRead;
                if (downloadMonitor.shouldStopIt())
                {
                    break;
                }
                downloadMonitor.updateProgress(fullLength);
            }
            is.close();
            downloadMonitor.setPokeThread(null);
            downloadBuffer.limit(fullLength);
            downloadBuffer.position(0);
        }
        catch (InterruptedIOException e)
        {
            // We were interrupted by the stop button. We're stopping now.. clear interruption flag.
            Thread.interrupted();
            return;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }


        try
        {
            String cksum = generateChecksum(downloadBuffer);
            if (cksum.equals(validationHash))
            {
                downloadBuffer.position(0);
                FileOutputStream fos = new FileOutputStream(target);
                fos.getChannel().write(downloadBuffer);
                fos.close();
            }
            else
            {
                throw new RuntimeException(String.format("The downloaded file %s has an invalid checksum %s (expecting %s). The download did not succeed correctly and the file has been deleted. Please try launching again.", target.getName(), cksum, validationHash));
            }
        }
        catch (Exception e)
        {
            if (e instanceof RuntimeException) throw (RuntimeException)e;
            throw new RuntimeException(e);
        }



    }

    private static String generateChecksum(ByteBuffer buffer)
    {
        return CertificateHelper.getFingerprint(buffer);
    }
}
