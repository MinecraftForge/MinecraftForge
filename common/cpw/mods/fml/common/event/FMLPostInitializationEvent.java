package cpw.mods.fml.common.event;

import cpw.mods.fml.common.LoaderState.ModState;

public class FMLPostInitializationEvent extends FMLStateEvent
{

    @Override
    public ModState getModState()
    {
        return ModState.POSTINITIALIZED;
    }

}
