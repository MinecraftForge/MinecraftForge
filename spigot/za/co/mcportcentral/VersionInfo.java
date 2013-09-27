package za.co.mcportcentral;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;

import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.common.io.OutputSupplier;

public class VersionInfo {
    public static final VersionInfo INSTANCE = new VersionInfo();
    public final JsonRootNode versionData;

    public VersionInfo()
    {
        InputStream installProfile = getClass().getResourceAsStream("/install_profile.json");
        JdomParser parser = new JdomParser();

        try
        {
            versionData = parser.parse(new InputStreamReader(installProfile, Charsets.UTF_8));
        }
        catch (Exception e)
        {
            throw Throwables.propagate(e);
        }
    }

    public static String getProfileName()
    {
        return INSTANCE.versionData.getStringValue("install","profileName");
    }

    public static String getVersionTarget()
    {
        return INSTANCE.versionData.getStringValue("install","target");
    }
    public static File getLibraryPath(File root)
    {
        String path = INSTANCE.versionData.getStringValue("install","path");
        String[] split = Iterables.toArray(Splitter.on(':').omitEmptyStrings().split(path), String.class);
        File dest = root;
        Iterable<String> subSplit = Splitter.on('.').omitEmptyStrings().split(split[0]);
        for (String part : subSplit)
        {
            dest = new File(dest, part);
        }
        dest = new File(new File(dest, split[1]), split[2]);
        String fileName = split[1]+"-"+split[2]+".jar";
        return new File(dest,fileName);
    }

    public static String getVersion()
    {
        return INSTANCE.versionData.getStringValue("install","version");
    }

    public static String getWelcomeMessage()
    {
        return INSTANCE.versionData.getStringValue("install","welcome");
    }

    public static String getLogoFileName()
    {
        return INSTANCE.versionData.getStringValue("install","logo");
    }

    public static JsonNode getVersionInfo()
    {
        return INSTANCE.versionData.getNode("versionInfo");
    }

    public static File getMinecraftFile(File path)
    {
        return new File(new File(path, getMinecraftVersion()),getMinecraftVersion()+".jar");
    }
    public static String getContainedFile()
    {
        return INSTANCE.versionData.getStringValue("install","filePath");
    }
    public static void extractFile(File path) throws IOException
    {
        INSTANCE.doFileExtract(path);
    }

    private void doFileExtract(File path) throws IOException
    {
        InputStream inputStream = getClass().getResourceAsStream("/"+getContainedFile());
        OutputSupplier<FileOutputStream> outputSupplier = Files.newOutputStreamSupplier(path);
        System.out.println("doFileExtract path = " + path.getAbsolutePath() + ", inputStream = " + inputStream + ", outputSupplier = " + outputSupplier);
        ByteStreams.copy(inputStream, outputSupplier);
    }

    public static String getMinecraftVersion()
    {
        return INSTANCE.versionData.getStringValue("install","minecraft");
    }
}