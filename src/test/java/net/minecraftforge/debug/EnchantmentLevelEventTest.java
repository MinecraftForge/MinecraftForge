package net.minecraftforge.debug;

import net.minecraftforge.event.entity.living.LivingEnchantmentLevelEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = EnchantmentLevelEventTest.MOD_ID, name = "Living Enchantment Level Event Test", version = "1.0", acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public class EnchantmentLevelEventTest
{
    static final String MOD_ID = "enchantmentleveleventtest";
    static final boolean ENABLED = false;
    private static Logger logger;

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent evt)
    {
        logger = evt.getModLog();
    }

    @SubscribeEvent
    public static void onInteract(LivingEnchantmentLevelEvent event)
    {
        if (!ENABLED)
        {
            return;
        }

        if (event.getEntityLiving().isSneaking())
        {
            int oldLevel = event.getLevel();
            int newLevel = oldLevel * 10;
            event.setLevel(newLevel);
            logger.info("Set enchantment level from " + oldLevel + " to " + newLevel + " for " + event.getEnchantment().getName());
        }
    }
}