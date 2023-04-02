/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

@Mod("custom_shield_test")
public class CustomShieldTest
{
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "custom_shield_test");

    private static final RegistryObject<CustomShieldItem> CUSTOM_SHIELD_ITEM = ITEMS.register("custom_shield",
            () -> new CustomShieldItem((new Item.Properties()).durability(336)));

    public CustomShieldTest()
    {
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modBus);
        modBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.COMBAT)
            event.accept(CUSTOM_SHIELD_ITEM);
    }

    private static class CustomShieldItem extends Item
    {
        public CustomShieldItem(Properties properties)
        {
            super(properties);
        }

        @Override
        public UseAnim getUseAnimation(ItemStack stack)
        {
            return UseAnim.BLOCK;
        }

        @Override
        public int getUseDuration(ItemStack stack)
        {
            return 72000;
        }

        @Override
        public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand)
        {
            ItemStack itemstack = player.getItemInHand(hand);
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(itemstack);
        }

        @Override
        public boolean canPerformAction(ItemStack stack, ToolAction toolAction)
        {
            return toolAction == ToolActions.SHIELD_BLOCK;
        }
    }
}
