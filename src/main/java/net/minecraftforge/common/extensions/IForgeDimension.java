package net.minecraftforge.common.extensions;

public interface IForgeDimension
{
    /**
     * Called from {@link World#initCapabilities()}, to gather capabilities for this
     * world. It's safe to access world here since this is called after world is
     * registered.
     *
     * On server, called directly after mapStorage and world data such as Scoreboard
     * and VillageCollection are initialized. On client, called when world is
     * constructed, just before world load event is called. Note that this method is
     * always called before the world load event.
     * 
     * @return initial holder for capabilities on the world
     */
    default net.minecraftforge.common.capabilities.ICapabilityProvider initCapabilities()
    {
        return null;
    }
}
