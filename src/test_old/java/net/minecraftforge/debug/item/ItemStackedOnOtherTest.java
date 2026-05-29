/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftsingularity.common.MinecraftForge;
import net.minecraftsingularity.event.ItemStackedOnOtherEvent;
import net.minecraftsingularity.fml.common.Mod;

@Mod(ItemStackedOnOtherTest.MODID)
public final class ItemStackedOnOtherTest {
    public static final String MODID = "item_stacked_on_other_test";

    private static final boolean ENABLED = true;

    public ItemStackedOnOtherTest() {
        if (ENABLED) {
            ItemStackedOnOtherEvent.BUS.addListener(this::onStackedOn);
        }
    }

    /**
     * When right clicking on a damageable item with a diamond sword
     */
    private boolean onStackedOn(ItemStackedOnOtherEvent event) {
        Player player = event.getPlayer();
        if (!player.isCreative() && event.getClickAction() == ClickAction.SECONDARY) {
            ItemStack carried = event.getCarriedItem();
            ItemStack current = event.getStackedOnItem();
            if (carried.is(Items.DIAMOND_SWORD) && current.isDamageableItem()) {
                current.hurtAndBreak(1, player, p -> {});
                return true;
            }
        }
        return false;
    }
}
