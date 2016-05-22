package net.minecraftforge.fml.common.discovery;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.io.InputStream;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.TweakModContainer;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;

public class TweakDiscoverer implements ITypeDiscoverer
{

    @Override
    public List<ModContainer> discover(ModCandidate candidate, ASMDataTable table)
    {
        List<ModContainer> containers = Lists.newArrayList();

        JarFile jar = null;
        try
        {
            jar = new JarFile(candidate.getModContainer());
            Attributes manifest = jar.getManifest().getMainAttributes();
            String path = manifest.getValue("TweakMetaFile");
            if (Strings.isNullOrEmpty(path))
            {
                // No meta file is defined in this tweaker, let's just ignore it
                return containers;
            }

            path = FilenameUtils.normalize(FilenameUtils.concat("META-INF", path), true);
            if (path.startsWith("/"))
            {
                // Remove the slash from paths starting with it
                path = path.substring(1);
            }

            ZipEntry entry = jar.getEntry(path);
            if (entry == null)
            {
                FMLLog.log(Level.WARN, "Tweak %s meta file was not found: %s. It will be ignored", candidate.getModContainer().getName(), path);
                return containers;
            }

            InputStream in = jar.getInputStream(entry);
            TweakModContainer container = TweakModContainer.from(in, candidate.getModContainer());
            if (container != null)
            {
                containers.add(container);
            }
        }
        catch (Exception e)
        {
            FMLLog.log(Level.WARN, e, "Zip file %s failed to read properly, it will be ignored", candidate.getModContainer().getName());
        }
        finally
        {
            IOUtils.closeQuietly(jar);
        }

        return containers;
    }

}