package net.minecraftforge.test;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid="EntityTravelToDimensionEventTest", name="EntityTravelToDimensionEventTest", version="0.0.0")
public class EntityTravelToDimensionEventTest
{
    public static final boolean ENABLE = false;

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onDimensionTravel(EntityTravelToDimensionEvent event)
    {
        if(ENABLE) {
            System.out.println("Travelling to Dimension " + event.dimension);
            event.setCanceled(true);
        }
    }
}
