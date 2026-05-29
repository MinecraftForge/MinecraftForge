/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.entity.living;

import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftsingularity.eventbus.api.bus.CancellableEventBus;
import net.minecraftsingularity.eventbus.api.event.MutableEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.Cancellable;
import org.jetbrains.annotations.ApiStatus;

public sealed abstract class LivingSwapItemsEvent extends MutableEvent implements LivingEvent {
    private final LivingEntity entity;

    @ApiStatus.Internal
    protected LivingSwapItemsEvent(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public LivingEntity getEntity() {
        return entity;
    }

    /**
     * This event is fired when a living entity is about to swap the items in their main and offhand.
     * This event is executed in {@link ServerGamePacketListenerImpl#handlePlayerAction}
     *
     * <p>This event is {@linkplain Cancellable cancellable}.
     */
    public static final class Hands extends LivingSwapItemsEvent implements Cancellable {
        public static final CancellableEventBus<Hands> BUS = CancellableEventBus.create(Hands.class);

        private ItemStack toMainHand;
        private ItemStack toOffHand;

        @ApiStatus.Internal
        public Hands(LivingEntity entity) {
            super(entity);
            this.toMainHand = entity.getOffhandItem(); //the main hand will be swapped with the offhand
            this.toOffHand = entity.getMainHandItem(); //the offhand will be swapped with the main hand
        }

        /**
         * Gets the item that will be swapped to the main hand of the entity.
         * @return The item that will be swapped to the main hand.
         */
        public ItemStack getItemSwappedToMainHand() {
            return toMainHand;
        }

        /**
         * Gets the item that will be swapped to the offhand of the entity.
         * @return The item that will be swapped to the offhand.
         */
        public ItemStack getItemSwappedToOffHand() {
            return toOffHand;
        }

        /**
         * Sets the item that will be swapped to the main hand of the entity.
         * @param item The item to swap to the main hand.
         */
        public void setItemSwappedToMainHand(ItemStack item) {
            this.toMainHand = item;
        }

        /**
         * Sets the item that will be swapped to the offhand of the entity.
         * @param item The item to swap to the offhand.
         */
        public void setItemSwappedToOffHand(ItemStack item) {
            this.toOffHand = item;
        }
    }
}
