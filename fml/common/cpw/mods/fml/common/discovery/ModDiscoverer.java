package cpw.mods.fml.common.discovery;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModClassLoader;
import cpw.mods.fml.common.ModContainer;

public class ModDiscoverer
{
    private Logger log = Loader.log;

    private static Pattern zipJar = Pattern.compile("(.+).(zip|jar)$");

    private List<ModCandidate> candidates = Lists.newArrayList();

    public void findClasspathMods(ModClassLoader modClassLoader)
    {
        File[] minecraftSources = modClassLoader.getParentSources();
        if (minecraftSources.length == 1 && minecraftSources[0].isFile())
        {
            log.fine(String.format("Minecraft is a file at %s, loading", minecraftSources[0].getAbsolutePath()));
            candidates.add(new ModCandidate(minecraftSources[0], minecraftSources[0], ContainerType.JAR));
        }
        else
        {
            for (int i = 0; i < minecraftSources.length; i++)
            {
                if (minecraftSources[i].isFile())
                {
                    log.fine(String.format("Found a minecraft related file at %s, examining for mod candidates", minecraftSources[i].getAbsolutePath()));
                    candidates.add(new ModCandidate(minecraftSources[i], minecraftSources[i], ContainerType.JAR, true));
                }
                else if (minecraftSources[i].isDirectory())
                {
                    log.fine(String.format("Found a minecraft related directory at %s, examining for mod candidates", minecraftSources[i].getAbsolutePath()));
                    candidates.add(new ModCandidate(minecraftSources[i], minecraftSources[i], ContainerType.DIR, true));
                }
            }
        }

    }

    public void findModDirMods(File modsDir)
    {
        File[] modList = modsDir.listFiles();
        // Sort the files into alphabetical order first
        Arrays.sort(modList);

        for (File modFile : modList)
        {
            if (modFile.isDirectory())
            {
                log.fine(String.format("Found a candidate mod directory %s", modFile.getName()));
                candidates.add(new ModCandidate(modFile, modFile, ContainerType.DIR));
            }
            else
            {
                Matcher matcher = zipJar.matcher(modFile.getName());

                if (matcher.matches())
                {
                    log.fine(String.format("Found a candidate zip or jar file %s", matcher.group(0)));
                    candidates.add(new ModCandidate(modFile, modFile, ContainerType.JAR));
                }
                else
                {
                    log.fine(String.format("Ignoring unknown file %s in mods directory", modFile.getName()));
                }
            }
        }
    }

    public List<ModContainer> identifyMods()
    {
        List<ModContainer> modList = Lists.newArrayList();
        
        for (ModCandidate candidate : candidates)
        {
            List<ModContainer> mods = candidate.explore();
            modList.addAll(mods);
        }
        
        return modList;
    }
}
