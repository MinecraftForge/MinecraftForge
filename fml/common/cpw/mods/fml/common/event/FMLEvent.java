package cpw.mods.fml.common.event;

import cpw.mods.fml.common.ModContainer;

public class FMLEvent
{
    public final String getEventType()
    {
        return getClass().getSimpleName();
    }

	public void applyModContainer(ModContainer activeContainer) {
	    // NO OP
	}
}
