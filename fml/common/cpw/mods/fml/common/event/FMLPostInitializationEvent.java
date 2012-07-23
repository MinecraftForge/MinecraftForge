package cpw.mods.fml.common.event;

import cpw.mods.fml.common.LoaderState.ModState;

public class FMLPostInitializationEvent extends FMLStateEvent
{
    public FMLPostInitializationEvent(Object... data)
    {
        super(data);
    }

    @Override
    public ModState getModState()
    {
        return ModState.POSTINITIALIZED;
    }

}
