package cpw.mods.fml.common.discovery;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.MetadataCollection;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.ModContainerFactory;
import cpw.mods.fml.common.discovery.asm.ASMModParser;

public class JarDiscoverer implements ITypeDiscoverer
{
    @Override
    public List<ModContainer> discover(ModCandidate candidate, ASMDataTable table)
    {
        List<ModContainer> foundMods = Lists.newArrayList();
        FMLLog.fine("Examining file %s for potential mods", candidate.getModContainer().getName());
        ZipFile jar = null;
        try
        {
            jar = new ZipFile(candidate.getModContainer());

            ZipEntry modInfo = jar.getEntry("mcmod.info");
            MetadataCollection mc = null;
            if (modInfo != null)
            {
                FMLLog.finer("Located mcmod.info file in file %s", candidate.getModContainer().getName());
                mc = MetadataCollection.from(jar.getInputStream(modInfo));
            }
            for (ZipEntry ze : Collections.list(jar.entries()))
            {
                Matcher match = classFile.matcher(ze.getName());
                if (match.matches())
                {
                    ASMModParser modParser = new ASMModParser(jar.getInputStream(ze));
                    modParser.validate();
                    modParser.sendToTable(table, candidate);
                    ModContainer container = ModContainerFactory.instance().build(modParser, candidate.getModContainer());
                    if (container!=null)
                    {
                        table.addContainer(container);
                        foundMods.add(container);
                        container.bindMetadata(mc);
                    }
                }
            }
        }
        catch (Exception e)
        {
            FMLLog.warning("Zip file %s failed to read properly, it will be ignored", candidate.getModContainer().getName());
        }
        finally
        {
            if (jar != null)
            {
                try
                {
                    jar.close();
                }
                catch (Exception e)
                {
                }
            }
        }
        return foundMods;
    }

}
