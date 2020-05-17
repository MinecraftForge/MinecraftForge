package net.minecraftforge.debug.world;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.world.raid.Raid.WaveMember;
import net.minecraftforge.event.world.RaidEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("raid_event_and_more_test")
@Mod.EventBusSubscriber
public class RaidEventTest 
{
	public static final boolean ENABLE = false;
	private static Logger LOGGER = LogManager.getLogger(RaidEventTest.class);
	
    public RaidEventTest()
    {
        if (ENABLE)
        {
           WaveMember.create("ILLUSIONER", EntityType.ILLUSIONER, new int[]{0, 4, 3, 3, 4, 4, 4, 2});	
        }
    }
    
    @SubscribeEvent
    public static void onRaidSpawnRaiders(RaidEvent.RaidSpawnRaidersEvent event)
    {
       if (ENABLE)
       {
    	   Minecraft.getInstance().player.sendChatMessage(event.getRaid().getRaiderCount() + " Raiders spawned");
       }
    }
    
    @SubscribeEvent
    public static void onRaidStart(RaidEvent.RaidStart event)
    {
       if (ENABLE)
       {
    	   Minecraft.getInstance().player.sendChatMessage("Raid Started.");
       }
    }
    
    @SubscribeEvent
    public static void onRaidStop(RaidEvent.RaidStopEvent event)
    {
       if (ENABLE)
       {
    	   LOGGER.info("Raid Stopped.");
       }
    }
    
    @SubscribeEvent
    public static void onRaidTick(RaidEvent.RaidTickEvent event)
    {
       if (ENABLE)
       {
    	   LOGGER.info("tick");
       }
    }
}