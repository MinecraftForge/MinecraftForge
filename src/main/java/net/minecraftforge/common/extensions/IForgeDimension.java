package net.minecraftforge.common.extensions;

import javax.annotation.Nullable;

import net.minecraft.world.World;
import net.minecraftforge.client.IRenderHandler;

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

    @Nullable
    IRenderHandler getSkyRenderer();

    void setSkyRenderer(IRenderHandler skyRenderer);

    @Nullable
    IRenderHandler getCloudRenderer();

    void setCloudRenderer(IRenderHandler renderer);

    @Nullable
    IRenderHandler getWeatherRenderer();

    void setWeatherRenderer(IRenderHandler renderer);
}
