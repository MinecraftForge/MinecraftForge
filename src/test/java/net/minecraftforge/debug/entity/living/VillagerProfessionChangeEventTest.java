package net.minecraftforge.debug.entity.living;

import net.minecraftforge.event.entity.living.VillagerProfessionChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("villager_profession_change_event_test")
@Mod.EventBusSubscriber
public class VillagerProfessionChangeEventTest
{
    private static Logger LOGGER = LogManager.getLogger(VillagerProfessionChangeEventTest.class);

    @SubscribeEvent
    public static void onVillagerProfessionChanged(VillagerProfessionChangeEvent event)
    {
        LOGGER.info("{} changed profession. The old profession was {}", event.getVillager(), event.getOldProfession());
    }
}
