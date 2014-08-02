package cpw.mods.fml.relauncher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import net.minecraft.launchwrapper.Launch;

public class ModListHelper {
    public static class JsonModList {
        public String repositoryRoot;
        public List<String> modRef;
    }
    public static final ArrayList<File> additionalMods = Lists.newArrayList();
    static void parseModList()
    {
        FMLRelaunchLog.fine("Attempting to load commandline specified mods");
        @SuppressWarnings("unchecked")
        Map<String,String> args = (Map<String, String>) Launch.blackboard.get("launchArgs");
        String listFile = args.get("--modListFile");
        if (listFile != null)
        {
            parseListFile(listFile);
            args.remove("--modListFile");
        }
        String extraMods = args.get("--mods");
        if (extraMods != null)
        {
            String[] split = extraMods.split(",");
            for (String modFile : split)
            {
                tryAddFile(modFile, null, modFile);
            }
            args.remove("--mods");
        }
    }
    private static void parseListFile(String listFile) {
        File f = new File(listFile);
        if (!f.exists())
        {
            FMLRelaunchLog.info("Failed to find modList file %s", listFile);
            return;
        }
        String json;
        try {
            json = Files.asCharSource(f, Charsets.UTF_8).read();
        } catch (IOException e1) {
            FMLRelaunchLog.log(Level.INFO, e1, "Failed to read modList json file %s.", listFile);
            return;
        }
        Gson gsonParser = new Gson();
        JsonModList modList;
        try {
            modList = gsonParser.fromJson(json, JsonModList.class);
        } catch (JsonSyntaxException e) {
            FMLRelaunchLog.log(Level.INFO, e, "Failed to parse modList json file %s.", listFile);
            return;
        }
        File repoRoot = new File(modList.repositoryRoot);
        if (!repoRoot.exists())
        {
            FMLRelaunchLog.info("Failed to find the specified repository root %s", modList.repositoryRoot);
            return;
        }
        
        for (String s : modList.modRef)
        {
            StringBuilder sb = new StringBuilder();
            String[] parts = s.split(":");
            sb.append(parts[0].replace('.', File.separatorChar));
            sb.append(File.separatorChar);
            sb.append(parts[1]).append(File.separatorChar);
            sb.append(parts[2]).append(File.separatorChar);
            sb.append(parts[1]).append('-').append(parts[2]);
            if (parts.length == 4)
            {
                sb.append('-').append(parts[3]);
            }
            sb.append(".jar");
            tryAddFile(sb.toString(), repoRoot, s);
        }
    }
    private static void tryAddFile(String modFileName, File repoRoot, String descriptor) {
        File modFile = repoRoot != null ? new File(repoRoot,modFileName) : new File(modFileName);  
        if (!modFile.exists())
        {
            FMLRelaunchLog.info("Failed to find mod file %s (%s)", descriptor, modFile.getAbsolutePath());
        }
        else
        {
            FMLRelaunchLog.fine("Adding ;%s (%s) to the mod list", descriptor, modFile.getAbsolutePath());
            additionalMods.add(modFile);
        }
    }
}
