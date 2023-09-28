/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ItemStackedOnOtherEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(ItemStackedOnOtherTest.MODID)
public class ItemStackedOnOtherTest
{
    public static final String MODID = "item_stacked_on_other_test";

    private static final boolean ENABLED = true;

    public ItemStackedOnOtherTest()
    {
        if (ENABLED)
        {
            MinecraftForge.EVENT_BUS.addListener(this::onStackedOn);
        }
    }

    /**
     * When right clicking on a damageable item with a diamond sword
     */
    private void onStackedOn(ItemStackedOnOtherEvent event)
    {
        Player player = event.getPlayer();
        if (!player.isCreative() && event.getClickAction() == ClickAction.SECONDARY)
        {
            ItemStack carried = event.getCarriedItem();
            ItemStack current = event.getStackedOnItem();
            if (carried.is(Items.DIAMOND_SWORD) && current.isDamageableItem())
            {
                current.hurtAndBreak(1, player, p -> {});
                event.setCanceled(true);
            }
        }
    }
}
