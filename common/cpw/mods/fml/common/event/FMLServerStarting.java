package cpw.mods.fml.common.event;

import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.common.LoaderState.ModState;

public class FMLServerStarting extends FMLStateEvent
{

    private Object server;
    
    public FMLServerStarting(Object... data)
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
