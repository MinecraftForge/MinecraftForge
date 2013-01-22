package net.minecraftforge.liquids;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * ItemStack substitute for liquids
 * @author SirSengir
 */
public class LiquidStack
{
    public int itemID;
    public int amount;
    public int itemMeta;

    private LiquidStack(){}

    public LiquidStack(int itemID,  int amount) { this(itemID,        amount, 0); }
    public LiquidStack(Item item,   int amount) { this(item.itemID,   amount, 0); }
    public LiquidStack(Block block, int amount) { this(block.blockID, amount, 0); }

    public LiquidStack(int itemID, int amount, int itemDamage)
    {
        this.itemID = itemID;
        this.amount = amount;
        this.itemMeta = itemDamage;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setShort("Id", (short)itemID);
        nbt.setInteger("Amount", amount);
        nbt.setShort("Meta", (short)itemMeta);
        return nbt;
    }

    public void readFromNBT(NBTTagCompound nbt)
    {
        itemID = nbt.getShort("Id");
        amount = nbt.getInteger("Amount");
        itemMeta = nbt.getShort("Meta");
    }

    /**
     * @return A copy of this LiquidStack
     */
    public LiquidStack copy()
    {
        return new LiquidStack(itemID, amount, itemMeta);
    }

    /**
     * @param other
     * @return true if this LiquidStack contains the same liquid as the one passed in.
     */
    public boolean isLiquidEqual(LiquidStack other)
    {
        return other != null && itemID == other.itemID && itemMeta == other.itemMeta;
    }

    /**
     * @param other
     * @return true if this LiquidStack contains the other liquid (liquids are equal and amount >= other.amount).
     */
    public boolean containsLiquid(LiquidStack other)
    {
        return isLiquidEqual(other) && amount >= other.amount;
    }

    /**
     * @param other ItemStack containing liquids.
     * @return true if this LiquidStack contains the same liquid as the one passed in.
     */
    public boolean isLiquidEqual(ItemStack other)
    {
        return other != null && itemID == other.itemID && itemMeta == other.getItemDamage();
    }

    /**
     * @return ItemStack representation of this LiquidStack
     */
    public ItemStack asItemStack()
    {
        return new ItemStack(itemID, 1, itemMeta);
    }

    /**
     * Reads a liquid stack from the passed nbttagcompound and returns it.
     *
     * @param nbt
     * @return the liquid stack
     */
    public static LiquidStack loadLiquidStackFromNBT(NBTTagCompound nbt)
    {
        LiquidStack liquidstack = new LiquidStack();
        liquidstack.readFromNBT(nbt);
        return liquidstack.itemID == 0 ? null : liquidstack;
    }
}
