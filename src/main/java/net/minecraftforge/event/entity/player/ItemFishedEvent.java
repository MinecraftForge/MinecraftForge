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

package net.minecraftforge.event.entity.player;

import com.google.common.base.Preconditions;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnegative;
import java.util.List;

/**
 * This event is called when a player fishes an item.
 * You can change the items the player will get using {@link #stacks}
 * You can also change the damage the rod will take using {@link #setRodDamage(int)}
 *
 * <b>Do not change the BlockPos {@link #pos}.</b> It should only be used to determine the position of the rod
 *
 * This event is not {@link net.minecraftforge.fml.common.eventhandler.Cancelable}
 */
public class ItemFishedEvent extends PlayerEvent {
    /**
     * Use this to set the items the player will get
     */
    public final List<ItemStack> stacks = NonNullList.create();
    /**
     * Use this to determine the position of the rod
     */
    public final BlockPos pos;
    private int rodDamage;

    public ItemFishedEvent (EntityPlayer player, List<ItemStack> stacks, int rodDamage, double posX, double posY, double posZ)
    {
        super(player);
        this.stacks.addAll(stacks);
        this.rodDamage = rodDamage;
        pos = new BlockPos(posX, posY, posZ);
    }

    /**
     * Get the damage the rod will take
     * @return The damage the rod will take
     */
    public int getRodDamage()
    {
        return rodDamage;
    }

    /**
     * Set the damage the fishing rod will take
     * @param rodDamage The damage the rod will take. Must be greater or equals zero
     */
    public void setRodDamage (@Nonnegative int rodDamage)
    {
        Preconditions.checkArgument(rodDamage >= 0);
        this.rodDamage = rodDamage;
    }
}
