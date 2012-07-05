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

import javax.swing.JOptionPane;

public class RelaunchLibraryManager
{
    private static String[] libraries = { "argo-2.25.jar","guava-12.0.jar","asm-all-4.0.jar" };
    private static String[] checksums = { "bb672829fde76cb163004752b86b0484bd0a7f4b", "5bc66dd95b79db1e437eb08adba124a3e4088dc0", "98308890597acb64047f7e896638e0d98753ae82" };
    private static final String HEXES = "0123456789abcdef";
    public static void handleLaunch(File mcDir, RelaunchClassLoader actualClassLoader)
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

        for (int i=0; i<libraries.length; i++)
        {
            String libName = libraries[i];
            String checksum = checksums[i];
            File libFile = new File(libDir, libName);
            if (!libFile.exists())
            {
                downloadFile(libFile);
            }

            if (libFile.exists() && !libFile.isFile())
            {
                throw new RuntimeException(String.format("Found a file %s that is not a normal file", libName));
            }

            String fileChecksum = generateChecksum(libFile);
            if (!checksum.equals(fileChecksum))
            {
                throw new RuntimeException(String.format("The file %s has an invalid checksum %s (expecting %s)", libName, fileChecksum, checksum));
            }
            try
            {
                actualClassLoader.addURL(libFile.toURI().toURL());
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }

        }

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
    private static void downloadFile(File libFile)
    {
        //                try
//                {
//                    JOptionPane.showMessageDialog(null, String.format("Downloading required FML library %s from github.com",libName));
//                }
//                catch (HeadlessException he)
//                {
//                    // Ignore
//                }
        try
        {
            URL libDownload = new URL(String.format("http://cloud.github.com/downloads/cpw/FML/%s",libFile.getName()));
            System.out.printf("Downloading %s..", libDownload.toString());
            InputStream urlConn = libDownload.openStream();
            ReadableByteChannel urlChannel = Channels.newChannel(urlConn);
            FileOutputStream libFileStream = new FileOutputStream(libFile);
            FileChannel libFileChannel = libFileStream.getChannel();
            libFileChannel.transferFrom(urlChannel, 0, 1<<24);
            libFileChannel.close();
            libFileStream.close();
            urlChannel.close();
            urlConn.close();
            System.out.println("download successful");
        }
        catch (Exception e)
        {

        }
    }
}
