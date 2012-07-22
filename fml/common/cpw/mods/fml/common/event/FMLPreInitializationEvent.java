package cpw.mods.fml.common.event;

import com.google.common.eventbus.EventBus;

import cpw.mods.fml.common.LoaderState.ModState;

public class FMLPreInitializationEvent extends FMLStateEvent
{

    @Override
    public ModState getModState()
    {
        return ModState.PREINITIALIZED;
    }

}
