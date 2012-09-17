package cpw.mods.fml.common.event;

import java.io.File;
import java.util.Properties;

import cpw.mods.fml.common.LoaderState.ModState;
import cpw.mods.fml.common.FMLModContainer;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.discovery.ASMDataTable;

public class FMLPreInitializationEvent extends FMLStateEvent
{
    private ModMetadata modMetadata;
    private File sourceFile;
    private File configurationDir;
    private File suggestedConfigFile;
    private ASMDataTable asmData;
    private ModContainer modContainer;

    public FMLPreInitializationEvent(Object... data)
    {
        super(data);
        this.asmData = (ASMDataTable)data[0];
        this.configurationDir = (File)data[1];
    }

    @Override
    public ModState getModState()
    {
        return ModState.PREINITIALIZED;
    }

    @Override
    public void applyModContainer(ModContainer activeContainer)
    {
        this.modContainer = activeContainer;
        this.modMetadata = activeContainer.getMetadata();
        this.sourceFile = activeContainer.getSource();
        this.suggestedConfigFile = new File(configurationDir, activeContainer.getModId()+".cfg");
    }

    public File getSourceFile()
    {
        return sourceFile;
    }

    public ModMetadata getModMetadata()
    {
        return modMetadata;
    }

    public File getModConfigurationDirectory()
    {
        return configurationDir;
    }

    public File getSuggestedConfigurationFile()
    {
        return suggestedConfigFile;
    }

    public ASMDataTable getAsmData()
    {
        return asmData;
    }

    public Properties getVersionProperties()
    {
        if (this.modContainer instanceof FMLModContainer)
        {
            return ((FMLModContainer)this.modContainer).searchForVersionProperties();
        }

        return null;
    }
}
