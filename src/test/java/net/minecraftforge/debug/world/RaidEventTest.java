package net.minecraftforge.debug.world;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    public static void onRaid(RaidEvent event)
    {
       if (event.getRaid().isActive() && ENABLE)
       {
    	   LOGGER.info("if you see this, it probably means the event works");
       }
    }
}