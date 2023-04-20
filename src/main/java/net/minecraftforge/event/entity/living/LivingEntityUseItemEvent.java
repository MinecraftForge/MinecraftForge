/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;

public class LivingEntityUseItemEvent extends LivingEvent
{
    private final ItemStack item;
    private int duration;

    private LivingEntityUseItemEvent(LivingEntity entity, @NotNull ItemStack item, int duration)
    {
        super(entity);
        this.item = item;
        this.setDuration(duration);
    }

    @NotNull
    public ItemStack getItem()
    {
        return item;
    }

    public int getDuration()
    {
        return duration;
    }

    public void setDuration(int duration)
    {
        this.duration = duration;
    }

    /**
     * Fired when a player starts 'using' an item, typically when they hold right mouse.
     * Examples:
     *   Drawing a bow
     *   Eating Food
     *   Drinking Potions/Milk
     *   Guarding with a sword
     *
     * Cancel the event, or set the duration or {@literal <=} 0 to prevent it from processing.
     *
     */
    @Cancelable
    public static class Start extends LivingEntityUseItemEvent
    {
        public Start(LivingEntity entity, @NotNull ItemStack item, int duration)
        {
            super(entity, item, duration);
        }
    }

    /**
     * Fired every tick that a player is 'using' an item, see {@link Start} for info.
     *
     * Cancel the event, or set the duration to {@literal <=} 0 to cause the player to stop using the item.
     *
     */
    @Cancelable
    public static class Tick extends LivingEntityUseItemEvent
    {
        public Tick(LivingEntity entity, @NotNull ItemStack item, int duration)
        {
            super(entity, item, duration);
        }
    }

    /**
     * Fired when a player stops using an item without the use duration timing out.
     * Example:
     *   Stop eating 1/2 way through
     *   Stop defending with sword
     *   Stop drawing bow. This case would fire the arrow
     *
     * Duration on this event is how long the item had left in it's count down before 'finishing'
     *
     * Canceling this event will prevent the Item from being notified that it has stopped being used,
     * The only vanilla item this would effect are bows, and it would cause them NOT to fire there arrow.
     */
    @Cancelable
    public static class Stop extends LivingEntityUseItemEvent
    {
        public Stop(LivingEntity entity, @NotNull ItemStack item, int duration)
        {
            super(entity, item, duration);
        }
    }

    /**
     * Fired after an item has fully finished being used.
     * The item has been notified that it was used, and the item/result stacks reflect after that state.
     * This means that when this is fired for a Potion, the potion effect has already been applied.
     *
     * {@link LivingEntityUseItemEvent#item} is a copy of the item BEFORE it was used.
     *
     * If you wish to cancel those effects, you should cancel one of the above events.
     *
     * The result item stack is the stack that is placed in the player's inventory in replacement of the stack that is currently being used.
     *
     */
    public static class Finish extends LivingEntityUseItemEvent
    {
        private ItemStack result;
        public Finish(LivingEntity entity, @NotNull ItemStack item, int duration, @NotNull ItemStack result)
        {
            super(entity, item, duration);
            this.setResultStack(result);
        }

        @NotNull
        public ItemStack getResultStack()
        {
            return result;
        }

        public void setResultStack(@NotNull ItemStack result)
        {
            this.result = result;
        }
    }
}
