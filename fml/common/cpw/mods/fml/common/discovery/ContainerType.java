package cpw.mods.fml.common.discovery;

import java.util.List;

import com.google.common.base.Throwables;

import cpw.mods.fml.common.ModContainer;

public enum ContainerType
{
    JAR(JarDiscoverer.class),
    DIR(DirectoryDiscoverer.class);

    private ITypeDiscoverer discoverer;

    private ContainerType(Class<? extends ITypeDiscoverer> discovererClass)
    {
        try
        {
            this.discoverer = discovererClass.newInstance();
        }
        catch (Exception e)
        {
            throw Throwables.propagate(e);
        }
    }

    public List<ModContainer> findMods(ModCandidate candidate, ASMDataTable table)
    {
        return discoverer.discover(candidate, table);
    }
}