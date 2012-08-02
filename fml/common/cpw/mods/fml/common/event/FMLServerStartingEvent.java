package cpw.mods.fml.common.event;

import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.common.LoaderState.ModState;

public class FMLServerStartingEvent extends FMLStateEvent
{

    private Object server;
    
    public FMLServerStartingEvent(Object... data)
    {
        super(data);
        this.server = data[0];
    }
    @Override
    public ModState getModState()
    {
        return ModState.AVAILABLE;
    }

}
