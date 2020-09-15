/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

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
    private final World world;
    @Nullable
    private final RayTraceResult target;

    private ItemStack result;

    public FillBucketEvent(PlayerEntity player, @Nonnull ItemStack current, World world, @Nullable RayTraceResult target)
    {
        super(player);
        this.current = current;
        this.world = world;
        this.target = target;
    }

    @Nonnull
    public ItemStack getEmptyBucket() { return this.current; }
    public World getWorld(){ return this.world; }
    @Nullable
    public RayTraceResult getTarget() { return this.target; }
    @Nonnull
    public ItemStack getFilledBucket() { return this.result; }
    public void setFilledBucket(@Nonnull ItemStack bucket) { this.result = bucket; }
}
