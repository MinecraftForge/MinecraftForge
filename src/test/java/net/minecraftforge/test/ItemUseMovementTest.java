package net.minecraftforge.test;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/** Adds a light bow that does not hinder player movement when drawn */
@Mod(modid = ItemUseMovementTest.MODID, name = "ItemUseMovementTest", version = "1.0")
@Mod.EventBusSubscriber
public class ItemUseMovementTest
{
    public static final String MODID = "itemusemovementtest";

    public static class ItemLightBow extends ItemBow {
        @Override
        public boolean shouldItemUsePreventSprinting(ItemStack stack, EntityLivingBase entity)
        {
            return false;
        }

        @Override
        public float getItemUseMovementSpeed(ItemStack stack, EntityLivingBase entity)
        {
            return 1.0F;
        }
    }

    private static final Item LIGHT_BOW = new ItemLightBow().setRegistryName(MODID, "light_bow").setUnlocalizedName(MODID + ".lightBow");

    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<Item> e)
    {
        e.getRegistry().register(LIGHT_BOW);
    }
}
