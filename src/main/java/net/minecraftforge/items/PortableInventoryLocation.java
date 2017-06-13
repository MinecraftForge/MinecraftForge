/*
 * Minecraft Forge
 * Copyright (c) 2016.
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
package net.minecraftforge.items;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PortableInventoryLocation
{
    @Nullable
    private IInventory inv;
    @Nullable
    private IItemHandler handler;

    public PortableInventoryLocation(@Nullable IInventory inv, @Nullable IItemHandler handler)
    {
        this.inv = inv;
        this.handler = handler;
    }

    @Nullable
    public IInventory getInventory()
    {
        return inv;
    }

    @Nullable
    public IItemHandler getItemHandler()
    {
        return handler;
    }

    public static class ItemStackInventory extends PortableInventoryLocation
    {
        /**
         * The stack which contains this inventory. This does not necessarily have the {@link IItemHandler} capability.
         */
        @Nonnull
        private ItemStack stack;

        public ItemStackInventory(@Nonnull ItemStack stack, @Nullable IInventory inv, @Nullable IItemHandler handler)
        {
            super(inv, handler);
            this.stack = stack;
        }

        @Nonnull
        public ItemStack getContainerStack()
        {
            return stack;
        }
    }

    public static class BlockInventory extends PortableInventoryLocation
    {
        @Nonnull
        private BlockPos pos;
        @Nonnull
        private IBlockAccess world;
        public BlockInventory(@Nonnull BlockPos pos, @Nonnull IBlockAccess world, @Nullable IInventory inv, @Nullable IItemHandler handler)
        {
            super(inv, handler);
            this.pos = pos;
            this.world = world;
        }

        public BlockInventory(@Nonnull TileEntity tile, @Nullable IInventory inv, @Nullable IItemHandler handler)
        {
            this(tile.getPos(), tile.getWorld(), inv, handler);
        }

        @Nonnull
        public BlockPos getPosition()
        {
            return pos;
        }

        @Nonnull
        public IBlockAccess getWorld()
        {
            return world;
        }
    }
}