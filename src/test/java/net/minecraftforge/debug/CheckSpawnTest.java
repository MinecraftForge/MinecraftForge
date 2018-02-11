package net.minecraftforge.debug;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = CheckSpawnTest.MODID, name = "CheckSpawnTest", version = "1.0", acceptableRemoteVersions = "*")
public class CheckSpawnTest
{
    public static final String MODID = "checkspawntest";
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
    public void canMobSpawn(CheckSpawn event) 
    {   	
    	event.setResult(Result.DENY);
    }
}
