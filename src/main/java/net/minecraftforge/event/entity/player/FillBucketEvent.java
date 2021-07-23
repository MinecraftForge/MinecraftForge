/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.event.entity.player;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

    public FillBucketEvent(Player player, @Nonnull ItemStack current, Level level, @Nullable HitResult target)
    {
        super(player);
        this.current = current;
        this.level = level;
        this.target = target;
    }

    @Nonnull
    public ItemStack getEmptyBucket()
    {
        return this.current;
    }

    public Level getLevel()
    {
        return this.level;
    }

    @Nullable
    public HitResult getTarget()
    {
        return this.target;
    }

    @Nonnull
    public ItemStack getFilledBucket()
    {
        return this.result;
    }

    public void setFilledBucket(@Nonnull ItemStack bucket)
    {
        this.result = bucket;
    }
}
