package net.minecraftforge.debug;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = "enchantmentlevelsettest", name = "EnchantmentLevelSetTest", version = "1.0")
public class EnchantmentLevelSetTest
{
    public static final boolean ENABLE = false;
    private static Logger logger;

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        if (ENABLE)
        {
            logger = event.getModLog();
            logger.info("Wiring up enchantment level set test");
            MinecraftForge.EVENT_BUS.register(EnchantmentLevelSetTest.class);
        }
    }

    @SubscribeEvent
    public static void onEnchantmentLevelSet(EnchantmentLevelSetEvent event)
    {
        // invert enchantment level, just for fun
        logger.info("Enchantment row: " + event.getEnchantRow() + ", level: " + event.getLevel());
        event.setLevel(30 - event.getLevel());
    }
}
