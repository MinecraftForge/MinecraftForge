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

package net.minecraftforge.event.world;

import com.google.common.base.Preconditions;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;

import javax.annotation.Nullable;

/**
 * This event is fired by mods, before they interact with blocks in non-standard way, so protection mods can cancel it.
 * ResourceLocation 'action' has to be your modid:action, e.g "gravity_altering_mod:pick_up_block"
 */
public class ModifyBlockEvent extends PlayerEvent
{
    private final EnumHand hand;
    private final BlockPos pos;
    private final EnumFacing face;
    private final ResourceLocation action;

    public ModifyBlockEvent(EntityPlayer player, EnumHand hand, BlockPos pos, EnumFacing face, ResourceLocation action)
    {
        super(Preconditions.checkNotNull(player, "Null player in ModifyBlockEvent!"));
        this.hand = Preconditions.checkNotNull(hand, "Null hand in ModifyBlockEvent!");
        this.pos = Preconditions.checkNotNull(pos, "Null position in ModifyBlockEvent!");
        this.face = face;
        this.action = Preconditions.checkNotNull(action, "Null action in ModifyBlockEvent!");
    }

    /**
     * @return The hand involved in this modification. Will never be null.
     */
    public EnumHand getHand()
    {
        return hand;
    }

    /**
     * @return The position of the block
     */
    public BlockPos getPos()
    {
        return pos;
    }

    /**
     * @return The face involved in this modification. This can return null.
     */
    @Nullable
    public EnumFacing getFace()
    {
        return face;
    }

    /**
     * @return Convenience method to get the world.
     */
    public World getWorld()
    {
        return getEntityPlayer().getEntityWorld();
    }

    /**
     * @return Action that this mod is currently doing, e.h
     */
    public ResourceLocation getAction()
    {
        return action;
    }

    /**
     * @return Convenience method to get the block state.
     */
    public IBlockState getBlockState()
    {
        return getWorld().getGroundAboveSeaLevel(getPos());
    }
}
