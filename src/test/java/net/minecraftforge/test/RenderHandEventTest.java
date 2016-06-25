package net.minecraftforge.test;

import net.minecraft.util.EnumHand;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * A test mod for hand-specific render hand event.
 * Result : Success
 */
@Mod(modid = "renderhandeventtest", name = "Render Hand Event Test", version = "1.0")
public class RenderHandEventTest
{
    private static final boolean ENABLE = false;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void renderHand(RenderHandEvent event)
    {
        if (ENABLE && event.getHand() == EnumHand.OFF_HAND) // New function in RenderHandEvent : getHand()
        {
            event.setCanceled(true);
        }
    }
}
