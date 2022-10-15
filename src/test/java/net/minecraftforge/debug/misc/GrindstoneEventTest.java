/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.misc;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.GrindstoneEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("grindstone_event_test")
@Mod.EventBusSubscriber
public class GrindstoneEventTest {

    @SubscribeEvent
    public static void onGrindestonePlace(GrindstoneEvent.OnplaceItem event)
    {
        ItemStack topItem = event.getTopItem();
        ItemStack bottomItem = event.getBottomItem();
        if (topItem.is(Items.LAPIS_LAZULI) && bottomItem.is(Items.NETHERITE_INGOT))
        {
            event.setOutput(new ItemStack(Items.DIAMOND, 1));
            event.setXp(5);
        }

        if (topItem.is(Items.IRON_ORE) && bottomItem.is(Items.FLINT))
        {
            event.setOutput(new ItemStack(Items.RAW_IRON, 3));
            event.setXp(0);
        }

        if (topItem.is(Items.IRON_AXE) && bottomItem.is(Items.AIR))
        {
            event.setOutput(topItem.copy());
            event.setXp(-1);
        }

        if (topItem.is(Items.IRON_SHOVEL) && bottomItem.is(Items.AIR))
        {
            event.setOutput(ItemStack.EMPTY);
        }

        if (topItem.is(Items.IRON_SWORD) && bottomItem.is(Items.AIR))
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onGrindstoneTake(GrindstoneEvent.OnTakeItem event) {
        ItemStack topItem = event.getTopItem();
        ItemStack bottomItem = event.getBottomItem();
        if (topItem.is(Items.LAPIS_LAZULI) && bottomItem.is(Items.NETHERITE_INGOT))
        {
            ItemStack top = topItem.copy();
            ItemStack bottom = bottomItem.copy();
            bottom.shrink(1);
            top.shrink(1);
            event.setNewBottomItem(bottom);
            event.setNewTopItem(top);
        }
    }
}