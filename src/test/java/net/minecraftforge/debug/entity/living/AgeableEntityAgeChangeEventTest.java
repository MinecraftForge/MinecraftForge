package net.minecraftforge.debug.entity.living;

import net.minecraft.entity.AgeableEntity;
import net.minecraftforge.event.entity.living.AgeableEntityAgeChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("ageable_entity_age_change_event_test")
@Mod.EventBusSubscriber
public class AgeableEntityAgeChangeEventTest
{
    private static Logger LOGGER = LogManager.getLogger(AgeableEntityAgeChangeEventTest.class);

    @SubscribeEvent
    public static void onAgeableEntityGrowingAdult(AgeableEntityAgeChangeEvent event)
    {
        AgeableEntity entity = event.getAgeableEntity();
        LOGGER.info("{} age change, it is now {}", entity, entity.isChild() ? "a child" : "an adult");
    }
}
