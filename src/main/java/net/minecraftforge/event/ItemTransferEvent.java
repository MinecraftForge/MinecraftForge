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

package net.minecraftforge.event;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemTransferEvent extends Event
{
    @Nonnull
    private final IItemHandler targetHandler;
    @Nonnull
    private final TileEntity targetTileEntity;
    @Nullable
    private final EnumFacing facing;
    @Nullable
    private final TileEntity fromTileEntity;
    @Nonnull
    private final ItemTransferEvent.flow flow;

    public ItemTransferEvent(@Nonnull IItemHandler targetHandler, @Nonnull TileEntity targetTileEntity, @Nullable EnumFacing facing, @Nullable TileEntity fromTileEntity, @Nonnull ItemTransferEvent.flow flow)
    {
        this.targetHandler = targetHandler;
        this.targetTileEntity = targetTileEntity;
        this.facing = facing;
        this.fromTileEntity = fromTileEntity;
        this.flow = flow;
    }

    /**
     * WARNING this can be a different IItemHandler than that is provided by the targetTileEntity
     *
     * @return the IItemHandler that we are interacting with
     */
    @Nonnull
    public IItemHandler getTargetHandler()
    {
        return targetHandler;
    }

    /**
     * @return the TileEntity where it got the IItemHandler from
     */
    @Nonnull
    public TileEntity getTargetTileEntity()
    {
        return targetTileEntity;
    }

    /**
     * @return the face where it got the IItemHandler from
     */
    @Nullable
    public EnumFacing getFacing()
    {
        return facing;
    }

    /**
     * @return the tileEntity that interacted with the giving IItemHandler, null if it was not a tileEntity
     */
    @Nullable
    public TileEntity getFromTileEntity()
    {
        return fromTileEntity;
    }

    @Nonnull
    public ItemTransferEvent.flow getFlow()
    {
        return flow;
    }

    /**
     * gets fired before
     */
    @Cancelable
    public static class PRE extends ItemTransferEvent
    {
        public PRE(@Nonnull IItemHandler targetHandler, @Nonnull TileEntity targetTileEntity, @Nullable EnumFacing facing, @Nullable TileEntity fromTileEntity, @Nonnull ItemTransferEvent.flow flow)
        {
            super(targetHandler, targetTileEntity, facing, fromTileEntity, flow);
        }
    }

    public static class POST extends ItemTransferEvent
    {
        @Nonnull
        private final ItemStack transferredStack;

        public POST(@Nonnull IItemHandler targetHandler, @Nonnull TileEntity targetTileEntity, @Nullable EnumFacing facing, @Nullable TileEntity fromTileEntity, @Nonnull ItemTransferEvent.flow flow, @Nonnull ItemStack transferredStack)
        {
            super(targetHandler, targetTileEntity, facing, fromTileEntity, flow);
            this.transferredStack = transferredStack;
        }

        @Nonnull
        public ItemStack getTransferredStack()
        {
            return transferredStack;
        }
    }

    public enum flow
    {
        EXTRACT,
        INSERT
    }
}
