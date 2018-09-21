package net.minecraftforge.common.extensions;

import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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

}
