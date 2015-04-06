package net.minecraftforge.test;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid="NeighborNotifyEventTest", name="NeighborNotifyEventTest", version="0.0.0")
public class NeighborNotifyEventTest 
{

    public static final boolean ENABLE = false;
    
    @EventHandler
    public void init(FMLInitializationEvent event) 
    {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onNeighborNotify(NeighborNotifyEvent event) 
    {
        if(ENABLE) {
            System.out.println(event.pos.toString());
            event.setCanceled(true);
        }
    }
}
