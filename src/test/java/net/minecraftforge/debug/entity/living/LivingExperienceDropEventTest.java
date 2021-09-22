package net.minecraftforge.debug.entity.living;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("living_experience_drop_event_test")
public class LivingExperienceDropEventTest
{
    private static final boolean ENABLE = false;
    private static final Logger logger = LogManager.getLogger(LivingExperienceDropEventTest.class);

    public LivingExperienceDropEventTest()
    {
        MinecraftForge.EVENT_BUS.addListener(this::onLivingXpDropEvent);
    }

    public void onLivingXpDropEvent(LivingExperienceDropEvent event)
    {
        if (!ENABLE) return;
        event.setDroppedExperience(event.getDroppedExperience() * 2);
        logger.info("{} killed {} resulting in {} xp, after modification {}", event.getAttackingPlayer().getName().getString(), event.getEntity().getName().getString(), event.getOriginalExperience(), event.getDroppedExperience());
    }

}
