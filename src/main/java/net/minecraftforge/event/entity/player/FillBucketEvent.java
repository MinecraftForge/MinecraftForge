/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.player;

import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This event is fired when a player attempts to use a Empty bucket, it
 * can be canceled to completely prevent any further processing.
 *
 * If you set the result to 'ALLOW', it means that you have processed
 * the event and wants the basic functionality of adding the new
 * ItemStack to your inventory and reducing the stack size to process.
 * setResult(ALLOW) is the same as the old setHandled();
 */
@Cancelable
@Event.HasResult
public class FillBucketEvent extends PlayerEvent
{

    private final ItemStack current;
    private final Level level;
    @Nullable
    private final HitResult target;

    private ItemStack result;

    public FillBucketEvent(Player player, @NotNull ItemStack current, Level level, @Nullable HitResult target)
    {
        super(player);
        this.current = current;
        this.level = level;
        this.target = target;
    }

    @NotNull
    public ItemStack getEmptyBucket() { return this.current; }
    public Level getLevel(){ return this.level; }
    @Nullable
    public HitResult getTarget() { return this.target; }
    @NotNull
    public ItemStack getFilledBucket() { return this.result; }
    public void setFilledBucket(@NotNull ItemStack bucket) { this.result = bucket; }
}
