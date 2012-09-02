package cpw.mods.fml.common.discovery;

import java.io.File;
import java.util.List;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.ModContainer;


public class ModCandidate
{
    private File classPathRoot;
    private File modContainer;
    private ContainerType sourceType;
    private boolean classpath;
    private List<String> baseModTypes = Lists.newArrayList();
    private boolean isMinecraft;

    public ModCandidate(File classPathRoot, File modContainer, ContainerType sourceType)
    {
        this(classPathRoot, modContainer, sourceType, false, false);
    }
    public ModCandidate(File classPathRoot, File modContainer, ContainerType sourceType, boolean isMinecraft, boolean classpath)
    {
        this.classPathRoot = classPathRoot;
        this.modContainer = modContainer;
        this.sourceType = sourceType;
        this.isMinecraft = isMinecraft;
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

    public boolean isClasspath()
    {
        return classpath;
    }
    public void rememberBaseModType(String className)
    {
        baseModTypes.add(className);
    }
    public List<String> getRememberedBaseMods()
    {
        return baseModTypes;
    }
    public boolean isMinecraftJar()
    {
        return isMinecraft;
    }
}