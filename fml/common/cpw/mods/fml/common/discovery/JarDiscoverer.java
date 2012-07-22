package cpw.mods.fml.common.discovery;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.MetadataCollection;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.ModContainerFactory;
import cpw.mods.fml.common.discovery.asm.ASMModParser;

public class JarDiscoverer implements ITypeDiscoverer
{
    private static Logger log = Loader.log;
    @Override
    public List<ModContainer> discover(ModCandidate candidate)
    {
        List<ModContainer> foundMods = Lists.newArrayList();
        log.fine(String.format("Examining file %s for potential mods", candidate.getModContainer().getName()));
        ZipFile jar = null;
        try
        {
            jar = new ZipFile(candidate.getModContainer());

            ZipEntry modInfo = jar.getEntry("mcmod.info");
            MetadataCollection mc = null;
            if (modInfo != null)
            {
                log.finer(String.format("Located mcmod.info file in file %s", candidate.getModContainer().getName()));
                mc = MetadataCollection.from(jar.getInputStream(modInfo));
            }
            for (ZipEntry ze : Collections.list(jar.entries()))
            {
                Matcher match = classFile.matcher(ze.getName());
                if (match.matches())
                {
                    ASMModParser modParser = new ASMModParser(jar.getInputStream(ze));
                    modParser.validate();
                    ModContainer container = ModContainerFactory.instance().build(modParser, candidate.getModContainer());
                    if (container!=null)
                    {
                        foundMods.add(container);
                        if (mc!=null)
                        {
                            container.bindMetadata(mc);
                        }
                    }
                }

                match = modClass.matcher(ze.getName());

                if (match.matches())
                {
                    log.warning(String.format("Candidate mod %s found in mod file %s but is missing mod information", ze.getName().replace("/","."), candidate.getModContainer().getName()));
                }
            }
        }
        catch (Exception e)
        {
            log.warning(String.format("Zip file %s failed to read properly, it will be ignored", candidate.getModContainer().getName()));
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
