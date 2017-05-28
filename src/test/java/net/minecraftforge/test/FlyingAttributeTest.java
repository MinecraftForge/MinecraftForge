package net.minecraftforge.test;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Adds two potions, one that gives flight and one that prevents flight. Both potions use the ALLOW_FLYING attribute.
 */
@Mod(modid = FlyingAttributeTest.MODID, name = "FlyingAttributeTest", version = "1.0")
@Mod.EventBusSubscriber
public class FlyingAttributeTest
{
    public static final String MODID = "flyingattributetest";

    public static final Potion FLYING_POTION = new TestPotion(false, 0)
            .setPotionName("effect." + MODID + ".flying")
            .registerPotionAttributeModifier(EntityPlayer.ALLOW_FLYING, "74DA1959-ECB7-43E1-A2F6-7426E390F331", 1.0D, 0)
            .setBeneficial()
            .setRegistryName(MODID, "flying");
    public static final Potion EARTHBIND_POTION = new TestPotion(true, 0)
            .setPotionName("effect." + MODID + ".earthbind")
            .registerPotionAttributeModifier(EntityPlayer.ALLOW_FLYING, "915FD6BE-89DC-4480-A2F2-8EED01A00D67", -1.0D, 2)
            .setRegistryName(MODID, "earthbind");

    @SubscribeEvent
    public static void registerPotions(RegistryEvent.Register<Potion> e)
    {
        e.getRegistry().register(FLYING_POTION);
        e.getRegistry().register(EARTHBIND_POTION);
    }

    @SubscribeEvent
    public static void registerPotionTypes(RegistryEvent.Register<PotionType> e)
    {
        e.getRegistry().register(new PotionType(new PotionEffect(FLYING_POTION, 3600)).setRegistryName(MODID, "flying_potion_type"));
        e.getRegistry().register(new PotionType(new PotionEffect(EARTHBIND_POTION, 3600)).setRegistryName(MODID, "earthbind_potion_type"));
    }

    public static class TestPotion extends Potion {
        public TestPotion(boolean isBadEffectIn, int liquidColorIn) {
            super(isBadEffectIn, liquidColorIn);
        }
    }
}
