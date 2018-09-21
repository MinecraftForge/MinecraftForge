/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

/*
 * Extension added to ItemStack that bounces to ItemSack sensitive Item methods. Typically this is just for convince.
 */
public interface IForgeItemStack extends ICapabilitySerializable<NBTTagCompound>
{
    // Helpers for accessing Item data
    default ItemStack getStack()
    {
        return (ItemStack)this;
    }

    /**
     * ItemStack sensitive version of getContainerItem. Returns a full ItemStack
     * instance of the result.
     *
     * @param itemStack The current ItemStack
     * @return The resulting ItemStack
     */
    default ItemStack getContainerItem()
    {
        return getStack().getItem().getContainerItem(getStack());
    }

    /**
     * ItemStack sensitive version of hasContainerItem
     *
     * @param stack The current item stack
     * @return True if this item has a 'container'
     */
    default boolean hasContainerItem()
    {
        return getStack().getItem().hasContainerItem(getStack());
    }

    /**
     * @return the fuel burn time for this itemStack in a furnace. Return 0 to make
     *         it not act as a fuel. Return -1 to let the default vanilla logic
     *         decide.
     */
    default int getBurnTime()
    {
        return getStack().getItem().getBurnTime(getStack());
    }

    /**
     * Queries the harvest level of this item stack for the specified tool class,
     * Returns -1 if this tool is not of the specified type
     *
     * @param stack      This item stack instance
     * @param toolClass  Tool Class
     * @param player     The player trying to harvest the given blockstate
     * @param state The block to harvest
     * @return Harvest level, or -1 if not the specified tool type.
     */
    default int getHarvestLevel(ToolType tool, @Nullable EntityPlayer player, @Nullable IBlockState state)
    {
        return getStack().getItem().getHarvestLevel(getStack(), tool, player, state);
    }

    default Set<ToolType> getToolTypes() {
        return getStack().getItem().getToolTypes(getStack());
    }

    default EnumActionResult onItemUseFirst(ItemUseContext context)
    {
       EntityPlayer entityplayer = context.func_195999_j();
       BlockPos blockpos = context.func_195995_a();
       BlockWorldState blockworldstate = new BlockWorldState(context.func_195991_k(), blockpos, false);
       if (entityplayer != null && !entityplayer.capabilities.allowEdit && !getStack().func_206847_b(context.func_195991_k().func_205772_D(), blockworldstate)) {
          return EnumActionResult.PASS;
       } else {
          Item item = getStack().getItem();
          EnumActionResult enumactionresult = item.onItemUseFirst(getStack(), context);
          if (entityplayer != null && enumactionresult == EnumActionResult.SUCCESS) {
             entityplayer.addStat(StatList.OBJECT_USE_STATS.func_199076_b(item));
          }

          return enumactionresult;
       }
    }

    default NBTTagCompound serializeNBT()
    {
        NBTTagCompound ret = new NBTTagCompound();
        getStack().writeToNBT(ret);
        return ret;
    }

}
