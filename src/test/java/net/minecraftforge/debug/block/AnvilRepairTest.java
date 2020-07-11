package net.minecraftforge.debug.block;

import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(AnvilRepairTest.MODID)
@Mod.EventBusSubscriber(modid = AnvilRepairTest.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AnvilRepairTest
{
    static final String MODID = "anvil_repair_test";
    static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    static void onAnvilRepair(AnvilRepairEvent event)
    {
        final float newChance = 1.0F;
        LOGGER.info("AnvilRepairEvent: {} + {} -> {}, chance is {}. setting to {}", event.getItemInput(), event.getIngredientInput(), event.getItemResult(), event.getBreakChance(), newChance);
        event.setBreakChance(newChance);
    }
}
