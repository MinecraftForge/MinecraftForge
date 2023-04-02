/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.misc;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.GrindstoneEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("grindstone_event_test")
public class GrindstoneEventTest {
    private static final boolean ENABLED = false;
    public GrindstoneEventTest() {
        if (ENABLED) {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @SubscribeEvent
    public void onGrindstonePlace(GrindstoneEvent.OnPlaceItem event)
    {
        // TODO 1.20: This will not work once IForgeItem#canGrindstoneRepair is changed to have items opt-in to being able to place
        //  rather than the current opt-out (the hook will no longer fire after the change). Fix?
        // all of these "recipes" are slot sensitive, the top and bottom must match exactly for the behavior to change
        // switching the order will cause the "recipe" to fail
        ItemStack topItem = event.getTopItem();
        ItemStack bottomItem = event.getBottomItem();
        // craft lapis + netherite to get diamond and 5 XP
        // this "recipe" is handled in OnTakeItem so the inputs only shrink by 1 each time
        if (topItem.is(Items.LAPIS_LAZULI) && bottomItem.is(Items.NETHERITE_INGOT))
        {
            event.setOutput(new ItemStack(Items.DIAMOND, 1));
            event.setXp(5);
        }

        // craft iron ore and flint to make raw iron ore, no XP is rewarded
        // this "recipe" is *not* handled in OnTakeItem, so the inputs will always be set to empty regardless of stack size
        if (topItem.is(Items.IRON_ORE) && bottomItem.is(Items.FLINT))
        {
            event.setOutput(new ItemStack(Items.RAW_IRON, 3));
            event.setXp(0);
        }

        // when placing an iron axe in the top slot, simply copy it to the output without change (do not remove enchants)
        // still grant XP equivalent to all enchantments on the axe
        if (topItem.is(Items.IRON_AXE) && bottomItem.is(Items.AIR))
        {
            event.setOutput(topItem.copy());
            event.setXp(-1);
        }

        // setting the output to empty will run default behavior, effectively ignoring all previous overrides to the event
        // note this will ignore any XP value you set, effectively for this test mod this has no impact
        if (topItem.is(Items.IRON_SHOVEL) && bottomItem.is(Items.AIR))
        {
            event.setOutput(ItemStack.EMPTY);
        }

        // canceling the event will prevent disenchanting an iron sword in the top slot
        if (topItem.is(Items.IRON_SWORD) && bottomItem.is(Items.AIR))
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onGrindstoneTake(GrindstoneEvent.OnTakeItem event)
    {
        ItemStack topItem = event.getTopItem();
        ItemStack bottomItem = event.getBottomItem();
        // only shrink stacks by 1 for the lapis + netherite "recipe"
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