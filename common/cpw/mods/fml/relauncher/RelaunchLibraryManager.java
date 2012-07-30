package cpw.mods.fml.relauncher;

import java.awt.HeadlessException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.FileChannel.MapMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.ProgressMonitorInputStream;

import cpw.mods.fml.common.discovery.ModCandidate;

public class RelaunchLibraryManager
{
    private static String[] plugins =  { "cpw.mods.fml.relauncher.FMLCorePlugin" , "net.minecraftforge.classloading.FMLForgePlugin" };
    private static final String HEXES = "0123456789abcdef";
    private static List<String> loadedLibraries = new ArrayList<String>();
    public static void handleLaunch(File mcDir, RelaunchClassLoader actualClassLoader)
    {
        List<IFMLLoadingPlugin> loadPlugins = new ArrayList<IFMLLoadingPlugin>();
        List<ILibrarySet> libraries = new ArrayList<ILibrarySet>();
        for (String s : plugins)
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
                FMLLog.severe("There were errors during initial FML setup. " +
                		"Some files failed to download or were otherwise corrupted. " +
                		"You will need to manually obtain the following files from " +
                		"these download links and ensure your lib directory is clean. ");
                for (ILibrarySet set : libraries)
                {
                    for (String file : set.getLibraries())
                    {
                        FMLLog.severe("*** Download "+set.getRootURL(), file);
                    }
                }
                FMLLog.severe("<===========>");
                FMLLog.severe("The following is the errors that caused the setup to fail. " +
                		"They may help you diagnose and resolve the issue");
                for (Throwable t : caughtErrors)
                {
                    FMLLog.severe(t.getMessage());
                }
                FMLLog.severe("<<< ==== >>>");
                FMLLog.severe("The following is diagnostic information for developers to review.");
                for (Throwable t : caughtErrors)
                {
                    FMLLog.log(Level.SEVERE, t, "Error details");
                }
                throw new RuntimeException("A fatal error occured and FML cannot continue");
            }
        }

        for (IFMLLoadingPlugin plug : loadPlugins)
        {
            for (String xformClass : plug.getASMTransformerClass())
            {
                actualClassLoader.registerTransformer(xformClass);
            }
        }
        try
        {
            Class<?> loaderClazz = Class.forName("cpw.mods.fml.common.Loader", true, actualClassLoader);
        }
        catch (Exception e)
        {
            // Load in the Loader, make sure he's ready to roll - this will initialize most of the rest of minecraft here
            throw new RuntimeException(e);
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
            FMLLog.info(infoString);
            InputStream urlStream = libDownload.openStream();
            urlStream = FMLEmbeddingRelauncher.instance().wrapStream(urlStream, infoString);
            ReadableByteChannel urlChannel = Channels.newChannel(urlStream);
            FileOutputStream libFileStream = new FileOutputStream(libFile);
            FileChannel libFileChannel = libFileStream.getChannel();
            libFileChannel.transferFrom(urlChannel, 0, 1<<24);
            libFileChannel.close();
            libFileStream.close();
            urlChannel.close();
            urlStream.close();
            FMLLog.info("Download complete");
        }
        catch (Exception e)
        {
            FMLLog.severe("There was a problem downloading the file %s automatically. Perhaps you" +
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
