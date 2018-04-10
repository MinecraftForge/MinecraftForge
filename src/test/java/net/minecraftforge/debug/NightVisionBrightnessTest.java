package net.minecraftforge.debug;

import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "nightvisionbrightnesstest", name = "NightVisionBrightnessTest", version = "1.0", acceptableRemoteVersions = "*")
public class NightVisionBrightnessTest
{
    public static final boolean ENABLED = false;

    @EventHandler
    public void onPreInit(FMLPreInitializationEvent event)
    {
        if (ENABLED)
        {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @SubscribeEvent
    public void nightVisionBrightness(EntityViewRenderEvent.NightVisionBrightness event)
    {
        event.setBrightness(1);
        event.setCanceled(true);
    }
}
