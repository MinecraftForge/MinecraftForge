package net.minecraftforge.debug;

import net.minecraftforge.client.event.RenderersLoadedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "RenderersLoadedEventTest", version = "0.1")
public class RenderersLoadedEventTest
{
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
	MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent public void onRenderersLoaded(RenderersLoadedEvent event) {
	System.out.println(event.mc.gameSettings.fancyGraphics);
    }
}