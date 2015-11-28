package net.minecraftforge.test;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid="playersetspawntest", name="Player Set Spawn Test", version="0.0.0")
public class PlayerSetSpawnTest {
    
    @EventHandler
    public void init(FMLInitializationEvent event) 
    {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onPlayerSetSpawn(PlayerSetSpawnEvent event) 
    {
        System.out.println(event.forced + " " + event.newSpawn.toString());
        event.setCanceled(true);
    }
}
