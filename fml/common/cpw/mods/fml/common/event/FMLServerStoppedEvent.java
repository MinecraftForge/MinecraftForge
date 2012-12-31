package cpw.mods.fml.common.event;

import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.common.LoaderState.ModState;

public class FMLServerStoppedEvent extends FMLStateEvent {

    public FMLServerStoppedEvent(Object... data)
    {
        super(data);
    }
    @Override
    public ModState getModState()
    {
        return ModState.AVAILABLE;
    }

}
