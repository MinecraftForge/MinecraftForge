/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.item;

import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@Mod(EnderMaskTest.MODID)
public class EnderMaskTest
{
    public static final String MODID = "ender_mask_test";
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static RegistryObject<Item> ender_mask = ITEMS.register("ender_mask", () ->
            new ArmorItem(ArmorMaterials.LEATHER, EquipmentSlot.HEAD, (new Item.Properties().tab(CreativeModeTab.TAB_MISC)))
            {
                @Override
                public boolean isEnderMask(ItemStack stack, Player player, EnderMan endermanEntity)
                {
                    return player.experienceLevel > 10;
                }
            }
    );

    public EnderMaskTest()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modEventBus);
    }
}
