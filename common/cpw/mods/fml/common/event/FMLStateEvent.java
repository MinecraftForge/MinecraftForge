package cpw.mods.fml.common.event;

import cpw.mods.fml.common.LoaderState.ModState;

public abstract class FMLStateEvent extends FMLEvent
{
    public FMLStateEvent(Object... data)
    {
        
    }

    public abstract ModState getModState();
}
