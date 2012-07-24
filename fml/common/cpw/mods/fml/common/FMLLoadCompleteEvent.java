package cpw.mods.fml.common;

import cpw.mods.fml.common.LoaderState.ModState;
import cpw.mods.fml.common.event.FMLStateEvent;

public class FMLLoadCompleteEvent extends FMLStateEvent
{

    public FMLLoadCompleteEvent(Object... data)
    {
        super(data);
    }
    
    @Override
    public ModState getModState()
    {
        return ModState.AVAILABLE;
    }

}
