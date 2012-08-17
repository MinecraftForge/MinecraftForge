package cpw.mods.fml.common.event;

import cpw.mods.fml.common.LoaderState.ModState;

public class FMLServerStartedEvent extends FMLStateEvent
{

    public FMLServerStartedEvent(Object... data)
    {
        super(data);
    }
    
    @Override
    public ModState getModState()
    {
        return ModState.AVAILABLE;
    }

}
