/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.ApiStatus;

public class LivingSwapItemsEvent extends LivingEvent
{

    @ApiStatus.Internal
    public LivingSwapItemsEvent(LivingEntity entity)
    {
        super(entity);
    }

    /**
     * This event is fired when a living entity is about to swap the items in their main and offhand.
     * This event is executed in {@link ServerGamePacketListenerImpl#handlePlayerAction}
     *
     * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
     */
    @Cancelable
    public static class Hands extends LivingSwapItemsEvent
    {
        private ItemStack toMainHand;
        private ItemStack toOffHand;

        @ApiStatus.Internal
        public Hands(LivingEntity entity)
        {
            super(entity);
            this.toMainHand = entity.getOffhandItem(); //the main hand will be swapped with the offhand
            this.toOffHand = entity.getMainHandItem(); //the offhand will be swapped with the main hand
        }

        /**
         * Gets the item that will be swapped to the main hand of the entity.
         * @return The item that will be swapped to the main hand.
         */
        public ItemStack getItemSwappedToMainHand()
        {
            return toMainHand;
        }

        /**
         * Gets the item that will be swapped to the offhand of the entity.
         * @return The item that will be swapped to the offhand.
         */
        public ItemStack getItemSwappedToOffHand()
        {
            return toOffHand;
        }

        /**
         * Sets the item that will be swapped to the main hand of the entity.
         * @param item The item to swap to the main hand.
         */
        public void setItemSwappedToMainHand(ItemStack item)
        {
            this.toMainHand = item;
        }

        /**
         * Sets the item that will be swapped to the offhand of the entity.
         * @param item The item to swap to the offhand.
         */
        public void setItemSwappedToOffHand(ItemStack item)
        {
            this.toOffHand = item;
        }
    }
}
