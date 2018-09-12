package net.minecraftforge.common.extensions;

import javax.annotation.Nullable;

import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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

    @OnlyIn(Dist.CLIENT)
    @Nullable
    IRenderHandler getSkyRenderer();

    @OnlyIn(Dist.CLIENT)
    void setSkyRenderer(IRenderHandler skyRenderer);

    @OnlyIn(Dist.CLIENT)
    @Nullable
    IRenderHandler getCloudRenderer();

    @OnlyIn(Dist.CLIENT)
    void setCloudRenderer(IRenderHandler renderer);

    @OnlyIn(Dist.CLIENT)
    @Nullable
    IRenderHandler getWeatherRenderer();

    @OnlyIn(Dist.CLIENT)
    void setWeatherRenderer(IRenderHandler renderer);
}
