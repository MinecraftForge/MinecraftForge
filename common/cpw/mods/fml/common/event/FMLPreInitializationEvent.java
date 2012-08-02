package cpw.mods.fml.common.event;

import java.io.File;

import com.google.common.eventbus.EventBus;

import cpw.mods.fml.common.LoaderState.ModState;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.ModMetadata;

public class FMLPreInitializationEvent extends FMLStateEvent
{
    private ModMetadata modMetadata;
    private File sourceFile;

    public FMLPreInitializationEvent(Object... data)
    {
        super(data);
    }

    @Override
    public ModState getModState()
    {
        return ModState.PREINITIALIZED;
    }

    @Override
    public void applyModContainer(ModContainer activeContainer)
    {
        this.modMetadata = activeContainer.getMetadata();
        this.sourceFile = activeContainer.getSource();
    }

    public File getSourceFile()
    {
        return sourceFile;
    }

    public ModMetadata getModMetadata()
    {
        return modMetadata;
    }
}
