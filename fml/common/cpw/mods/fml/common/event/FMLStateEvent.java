package cpw.mods.fml.common.event;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.LoaderState.ModState;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.Side;

public abstract class FMLStateEvent extends FMLEvent
{
    public FMLStateEvent(Object... data)
    {

    }

    public abstract ModState getModState();

    public void applyModContainer(ModContainer activeContainer)
    {
        // NO OP
    }

    public Side getSide()
    {
        return FMLCommonHandler.instance().getSide();
    }
}
