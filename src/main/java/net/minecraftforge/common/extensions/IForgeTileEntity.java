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

package net.minecraftforge.common.extensions;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public interface IForgeTileEntity extends ICapabilitySerializable<CompoundNBT>
{
    default TileEntity getTileEntity() { return (TileEntity) this; }

    @Override
    default void deserializeNBT(CompoundNBT nbt)
    {
        //TODO re-evaluate
        deserializeNBT(null, nbt);
    }

    //    @Override TODO  re-evaluate
    default void deserializeNBT(BlockState state, CompoundNBT nbt)
    {
        getTileEntity().read(state, nbt);
    }

    @Override
    default CompoundNBT serializeNBT()
    {
        CompoundNBT ret = new CompoundNBT();
        getTileEntity().write(ret);
        return ret;
    }

    /**
     * Called when you receive a TileEntityData packet for the location this
     * TileEntity is currently in. On the client, the NetworkManager will always
     * be the remote server. On the server, it will be whomever is responsible for
     * sending the packet.
     *
     * @param net The NetworkManager the packet originated from
     * @param pkt The data packet
     */
    default void onDataPacket(net.minecraft.network.NetworkManager net, net.minecraft.network.play.server.SUpdateTileEntityPacket pkt){ }

    /**
     * Called when the chunk's TE update tag, gotten from {@link #getUpdateTag()}, is received on the client.
     * <p>
     * Used to handle this tag in a special way. By default this simply calls {@link #readFromNBT(NBTTagCompound)}.
     *
     * @param tag The {@link NBTTagCompound} sent from {@link #getUpdateTag()}
     */
     default void handleUpdateTag(BlockState state, CompoundNBT tag)
     {
         getTileEntity().read(state, tag);
     }

    /**
     * Gets a {@link NBTTagCompound} that can be used to store custom data for this tile entity.
     * It will be written, and read from disc, so it persists over world saves.
     *
     * @return A compound tag for custom data
     */
     CompoundNBT getTileData();

     default void onChunkUnloaded(){}

    /**
     * Called when this is first added to the world (by {@link World#addTileEntity(TileEntity)}).
     * Override instead of adding {@code if (firstTick)} stuff in update.
     */
     default void onLoad()
     {
         requestModelDataUpdate();
     }

     /**
      * Sometimes default render bounding box: infinite in scope. Used to control rendering on {@link TileEntitySpecialRenderer}.
      */
     public static final AxisAlignedBB INFINITE_EXTENT_AABB = new net.minecraft.util.math.AxisAlignedBB(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

     /**
      * Return an {@link AxisAlignedBB} that controls the visible scope of a {@link TileEntitySpecialRenderer} associated with this {@link TileEntity}
      * Defaults to the collision bounding box {@link Block#getCollisionBoundingBoxFromPool(World, int, int, int)} associated with the block
      * at this location.
      *
      * @return an appropriately size {@link AxisAlignedBB} for the {@link TileEntity}
      */
     @OnlyIn(Dist.CLIENT)
     default AxisAlignedBB getRenderBoundingBox()
     {
         AxisAlignedBB bb = INFINITE_EXTENT_AABB;
         BlockState state = getTileEntity().getBlockState();
         Block block = state.getBlock();
         BlockPos pos = getTileEntity().getPos();
         if (block == Blocks.ENCHANTING_TABLE)
         {
             bb = new AxisAlignedBB(pos, pos.add(1, 1, 1));
         }
         else if (block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST)
         {
             bb = new AxisAlignedBB(pos.add(-1, 0, -1), pos.add(2, 2, 2));
         }
         else if (block == Blocks.STRUCTURE_BLOCK)
         {
             bb = INFINITE_EXTENT_AABB;
         }
         else if (block != null && block != Blocks.BEACON)
         {
             AxisAlignedBB cbb = null;
             try
             {
                 VoxelShape collisionShape = state.getCollisionShape(getTileEntity().getWorld(), pos);
                 if (!collisionShape.isEmpty())
                 {
                     cbb = collisionShape.getBoundingBox().offset(pos);
                 }
             }
             catch (Exception e)
             {
                 // We have to capture any exceptions that may occur here because BUKKIT servers like to send
                 // the tile entity data BEFORE the chunk data, you know, the OPPOSITE of what vanilla does!
                 // So we can not GARENTEE that the world state is the real state for the block...
                 // So, once again in the long line of US having to accommodate BUKKIT breaking things,
                 // here it is, assume that the TE is only 1 cubic block. Problem with this is that it may
                 // cause the TileEntity renderer to error further down the line! But alas, nothing we can do.
                 cbb = new net.minecraft.util.math.AxisAlignedBB(pos.add(-1, 0, -1), pos.add(1, 1, 1));
             }
             if (cbb != null) bb = cbb;
         }
         return bb;
     }

    /**
     * Requests a refresh for the model data of your TE
     * Call this every time your {@link #getModelData()} changes
     */
     default void requestModelDataUpdate()
     {
         TileEntity te = getTileEntity();
         World world = te.getWorld();
         if (world != null && world.isRemote)
         {
             ModelDataManager.requestModelDataRefresh(te);
         }
     }

    /**
     * Allows you to return additional model data.
     * This data can be used to provide additional functionality in your {@link net.minecraft.client.renderer.model.IBakedModel}
     * You need to schedule a refresh of you model data via {@link #requestModelDataUpdate()} if the result of this function changes.
     * <b>Note that this method may be called on a chunk render thread instead of the main client thread</b>
     * @return Your model data
     */
     default @Nonnull IModelData getModelData()
     {
         return EmptyModelData.INSTANCE;
     }
}
