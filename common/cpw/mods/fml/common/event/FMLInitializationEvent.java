package cpw.mods.fml.common.event;

import cpw.mods.fml.common.LoaderState.ModState;

public class FMLInitializationEvent extends FMLStateEvent
{

    public FMLInitializationEvent(Object... data)
    {
        super(data);
    }
    
    @Override
    public ModState getModState()
    {
        return ModState.INITIALIZED;
    }

}
