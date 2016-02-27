package net.minecraftforge.test;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid="PlayerInteractEventLocalTest", name="PlayerInteractEventLocalTest", version="0.0.0")
public class PlayerInteractEventLocalTest
{

    public static final boolean ENABLE = false;
    
    @EventHandler
    public void init(FMLInitializationEvent event) 
    {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        if(ENABLE && event.localPos != null) {
            System.out.println(event.localPos.toString());
        }
    }
}
