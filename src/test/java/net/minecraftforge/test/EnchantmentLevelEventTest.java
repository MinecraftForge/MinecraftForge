package net.minecraftforge.test;

import net.minecraft.init.Enchantments;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEnchantmentLevelEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid="forge.enchantmentleveleventtest",version="1.0")
public class EnchantmentLevelEventTest
{

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent evt)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onInteract(LivingEnchantmentLevelEvent event)
    {
        if (event.getEnchantment() == Enchantments.fortune && event.getEntityLiving().isSneaking())
        {
            int oldLevel = event.getLevel();
            int newLevel = (oldLevel + 1) * 10;
            event.setLevel(newLevel);
            System.out.println("Set fortune level from " + oldLevel + " to " + newLevel);
        }
    }
}
