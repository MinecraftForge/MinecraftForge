package net.minecraftforge.liquids;

import static cpw.mods.fml.relauncher.Side.CLIENT;

import com.google.common.base.Objects;

import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFluid;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;

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
        if (other == null)
        {
            return false;
        }

        if (itemID == other.itemID && itemMeta == other.getItemDamage())
        {
            return true;
        }

        return isLiquidEqual(LiquidContainerRegistry.getLiquidForFilledItem(other));
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

    @SideOnly(CLIENT)
    private Icon renderingIcon;

    @SideOnly(CLIENT)
    public Icon getRenderingIcon()
    {
        if (itemID == Block.waterStill.blockID)
        {
            return BlockFluid.func_94424_b("water");
        }
        else if (itemID == Block.lavaStill.blockID)
        {
            return BlockFluid.func_94424_b("lava");
        }
        return renderingIcon;
    }

    @SideOnly(CLIENT)
    public void setRenderingIcon(Icon icon)
    {
        this.renderingIcon = icon;
    }

    @Override
    public final int hashCode()
    {
        return Objects.hashCode(itemID, itemMeta);
    }

    @Override
    public final boolean equals(Object ob)
    {
        return ob instanceof LiquidStack && Objects.equal(((LiquidStack)ob).itemID, itemID) && Objects.equal(((LiquidStack)ob).itemMeta, itemMeta);
    }
}
