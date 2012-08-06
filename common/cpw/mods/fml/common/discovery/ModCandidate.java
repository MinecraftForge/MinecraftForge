package cpw.mods.fml.common.discovery;

import java.io.File;
import java.util.List;

import cpw.mods.fml.common.ModContainer;


public class ModCandidate
{
    private File classPathRoot;
    private File modContainer;
    private ContainerType sourceType;
    private boolean classpath;

    public ModCandidate(File classPathRoot, File modContainer, ContainerType sourceType)
    {
        this(classPathRoot, modContainer, sourceType, false);
    }
    public ModCandidate(File classPathRoot, File modContainer, ContainerType sourceType, boolean classpath)
    {
        this.classPathRoot = classPathRoot;
        this.modContainer = modContainer;
        this.sourceType = sourceType;
        this.classpath = classpath;
    }

    public File getClassPathRoot()
    {
        return classPathRoot;
    }

    public File getModContainer()
    {
        return modContainer;
    }

    public ContainerType getSourceType()
    {
        return sourceType;
    }
    public List<ModContainer> explore(ASMDataTable table)
    {
        return sourceType.findMods(this, table);
    }
}