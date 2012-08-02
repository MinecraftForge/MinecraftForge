package cpw.mods.fml.common.event;

import cpw.mods.fml.common.LoaderState.ModState;
import cpw.mods.fml.common.ModClassLoader;

public class FMLConstructionEvent extends FMLStateEvent
{
    private ModClassLoader modClassLoader;

    public FMLConstructionEvent(Object... eventData)
    {
        this.modClassLoader = (ModClassLoader)eventData[0];
    }
    
    public ModClassLoader getModClassLoader()
    {
        return modClassLoader;
    }

    @Override
    public ModState getModState()
    {
        return ModState.CONSTRUCTED;
    }
}
