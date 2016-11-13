package net.minecraftforge.test;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "enchantmentlevelsettest", name = "EnchantmentLevelSetTest", version = "1.0")
public class EnchantmentLevelSetTest
{
    public static final boolean ENABLE = false;

    @Mod.EventHandler
    public static void init(FMLInitializationEvent event)
    {
        if (ENABLE)
            MinecraftForge.EVENT_BUS.register(EnchantmentLevelSetTest.class);
    }

    @SubscribeEvent
    public static void onEnchantmentLevelSet(EnchantmentLevelSetEvent event)
    {
        // invert enchantment level, just for fun
        FMLLog.info("Enchantment number: " + event.getEnchantNum() + ", level: " + event.getLevel());
        event.setLevel(30 - event.getLevel());
    }
}
