package cpw.mods.fml.relauncher;

import java.awt.HeadlessException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.FileChannel.MapMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.ProgressMonitorInputStream;

import cpw.mods.fml.common.discovery.ModCandidate;

public class RelaunchLibraryManager
{
    private static String[] rootPlugins =  { "cpw.mods.fml.relauncher.FMLCorePlugin" , "net.minecraftforge.classloading.FMLForgePlugin" };
    private static final String HEXES = "0123456789abcdef";
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

        // Now that we have the root plugins loaded - lets see what else might be around
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
                    String checksum = lib.getHashes()[i];
                    File libFile = new File(libDir, libName);
                    if (!libFile.exists())
                    {
                        try
                        {
                            downloadFile(libFile, lib.getRootURL());
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

                    String fileChecksum = generateChecksum(libFile);
                    // bad checksum and I did not download this file
                    if (!checksum.equals(fileChecksum) && !download)
                    {
                        caughtErrors.add(new RuntimeException(String.format("The file %s was found in your lib directory and has an invalid checksum %s (expecting %s) - it is unlikely to be the correct download, please move it out of the way and try again.", libName, fileChecksum, checksum)));
                        continue;
                    }
                    // bad checksum but file was downloaded this session
                    else if (!checksum.equals(fileChecksum))
                    {
                        caughtErrors.add(new RuntimeException(String.format("The downloaded file %s has an invalid checksum %s (expecting %s). The download did not succeed correctly and the file has been deleted. Please try launching again.", libName, fileChecksum, checksum)));
                        libFile.delete();
                        continue;
                    }

                    if (!download)
                    {
                        System.out.printf("Found library file %s present and correct in lib dir\n", libName);
                    }
                    else
                    {
                        System.out.printf("Library file %s was downloaded and verified successfully\n", libName);
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
                    FMLRelaunchLog.severe(t.getMessage());
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

        Map<String,Object> data = new HashMap<String,Object>();
        data.put("mcLocation", mcDir);
        data.put("coremodList", loadPlugins);
        for (IFMLLoadingPlugin plugin : loadPlugins)
        {
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

            String modContainer = plugin.getModContainerClass();
            if (modContainer != null)
            {
                FMLInjectionData.containers.add(modContainer);
            }
        }
        try
        {
            Class<?> loaderClazz = Class.forName("cpw.mods.fml.common.Loader", true, actualClassLoader);
            Method m = loaderClazz.getMethod("injectData", Object[].class);
            m.invoke(null, (Object)FMLInjectionData.data());
        }
        catch (Exception e)
        {
            // Load in the Loader, make sure he's ready to roll - this will initialize most of the rest of minecraft here
            System.out.println("RelaunchLibraryManager.handleLaunch");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static void discoverCoreMods(File mcDir, RelaunchClassLoader classLoader, List<IFMLLoadingPlugin> loadPlugins, List<ILibrarySet> libraries)
    {
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
            FMLRelaunchLog.fine("Found a candidate coremod %s", coreMod.getName());
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
                Class<?> coreModClass = Class.forName(fmlCorePlugin, true, classLoader);
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
                FMLRelaunchLog.fine("Loaded coremod %s", coreMod.getName());
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

    private static String generateChecksum(File file)
    {
        try
        {
            FileInputStream fis = new FileInputStream(file);
            FileChannel chan = fis.getChannel();
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            MappedByteBuffer mappedFile = chan.map(MapMode.READ_ONLY, 0, file.length());
            digest.update(mappedFile);
            chan.close();
            fis.close();
            byte[] chksum = digest.digest();
            final StringBuilder hex = new StringBuilder( 2 * chksum.length );
            for ( final byte b : chksum ) {
              hex.append(HEXES.charAt((b & 0xF0) >> 4))
                 .append(HEXES.charAt((b & 0x0F)));
            }
            return hex.toString();
        }
        catch (Exception e)
        {
            return null;
        }
    }
    private static void downloadFile(File libFile, String rootUrl)
    {
        try
        {
            URL libDownload = new URL(String.format(rootUrl,libFile.getName()));
            String infoString = String.format("Downloading file %s", libDownload.toString());
            FMLRelaunchLog.info(infoString);
            InputStream urlStream = libDownload.openStream();
            urlStream = FMLRelauncher.instance().wrapStream(urlStream, infoString);
            ReadableByteChannel urlChannel = Channels.newChannel(urlStream);
            FileOutputStream libFileStream = new FileOutputStream(libFile);
            FileChannel libFileChannel = libFileStream.getChannel();
            libFileChannel.transferFrom(urlChannel, 0, 1<<24);
            libFileChannel.close();
            libFileStream.close();
            urlChannel.close();
            urlStream.close();
            FMLRelaunchLog.info("Download complete");
        }
        catch (Exception e)
        {
            FMLRelaunchLog.severe("There was a problem downloading the file %s automatically. Perhaps you" +
            		"have an environment without internet access. You will need to download " +
            		"the file manually\n", libFile.getName());
            libFile.delete();
            throw new RuntimeException("A download error occured", e);
        }
    }

    public static List<String> getLibraries()
    {
        return loadedLibraries;
    }
}
