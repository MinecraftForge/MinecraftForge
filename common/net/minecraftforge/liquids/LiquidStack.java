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
 * Things of note: they are equal if their items are equal. Amount does NOT matter for java equals() testing
 * <br/>
 * The canonical liquidstack is probably the only one that has a lot of the rendering data on it. Use {@link #canonical()}
 * to get it.
 *
 * @author SirSengir
 */
@Deprecated //See new net.minecraftforge.fluids
public class LiquidStack
{
    public final int itemID;
    public int amount;
    public final int itemMeta;
    public NBTTagCompound extra;

    public LiquidStack(int itemID,  int amount) { this(itemID,        amount, 0); }
    public LiquidStack(Item item,   int amount) { this(item.itemID,   amount, 0); }
    public LiquidStack(Block block, int amount) { this(block.blockID, amount, 0); }

    public LiquidStack(int itemID, int amount, int itemDamage)
    {
        this.itemID = itemID;
        this.amount = amount;
        this.itemMeta = itemDamage;
    }

    public LiquidStack(int itemID, int amount, int itemDamage, NBTTagCompound nbt)
    {
        this(itemID, amount, itemDamage);
        if (nbt != null)
        {
            extra = (NBTTagCompound)nbt.copy();
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("Amount", amount);
        nbt.setShort("Id", (short)itemID);
        nbt.setShort("Meta", (short)itemMeta);
        String name = LiquidDictionary.findLiquidName(this);
        if(name != null)
        {
            nbt.setString("LiquidName", name);
        }
        if (extra != null)
        {
            nbt.setTag("extra", extra);
        }
        return nbt;
    }

    /**
     * @return A copy of this LiquidStack
     */
    public LiquidStack copy()
    {
        return new LiquidStack(itemID, amount, itemMeta, extra);
    }

    /**
     * @param other
     * @return true if this LiquidStack contains the same liquid as the one passed in.
     */
    public boolean isLiquidEqual(LiquidStack other)
    {
        return other != null && itemID == other.itemID && itemMeta == other.itemMeta && (extra == null ? other.extra == null : extra.equals(other.extra));
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
        ItemStack stack = new ItemStack(itemID, 1, itemMeta);
        if (extra != null)
        {
            stack.stackTagCompound = (NBTTagCompound)extra.copy();
        }
        return stack;
    }

    /**
     * Reads a liquid stack from the passed nbttagcompound and returns it.
     *
     * @param nbt
     * @return the liquid stack
     */
    public static LiquidStack loadLiquidStackFromNBT(NBTTagCompound nbt)
    {
        if (nbt == null)
        {
            return null;
        }
        String liquidName = nbt.getString("LiquidName");
        int itemID = nbt.getShort("Id");
        int itemMeta = nbt.getShort("Meta");
        LiquidStack liquid = LiquidDictionary.getCanonicalLiquid(liquidName);
        if(liquid != null) {
            itemID = liquid.itemID;
            itemMeta = liquid.itemMeta;
        }
        // if the item is not existent, and no liquid dictionary is found, null returns
        else if (Item.itemsList[itemID] == null)
        {
            return null;
        }
        int amount = nbt.getInteger("Amount");
        LiquidStack liquidstack = new LiquidStack(itemID, amount, itemMeta);
        if (nbt.hasKey("extra"))
        {
            liquidstack.extra = nbt.getCompoundTag("extra");
        }
        return liquidstack.itemID == 0 ? null : liquidstack;
    }

    private String textureSheet = "/terrain.png";

    /**
     * Return the textureSheet used for this liquid stack's texture Icon
     * Defaults to '/terrain.png'
     *
     * See {@link #getRenderingIcon()} for the actual icon
     *
     * @return The texture sheet
     */
    public String getTextureSheet()
    {
        return textureSheet;
    }

    /**
     * Set the texture sheet for this icon (usually /terrain.png or /gui/items.png)
     *
     * See also the {@link #setRenderingIcon(Icon)} for the icon itself
     *
     * @param textureSheet
     * @return the liquid stack
     */
    public LiquidStack setTextureSheet(String textureSheet)
    {
        this.textureSheet = textureSheet;
        return this;
    }
    @SideOnly(CLIENT)
    private Icon renderingIcon;

    /**
     * Get the rendering icon for this liquid stack, for presentation in the world or in GUIs.
     * Defaults to handling water and lava, and returns the set rendering icon otherwise.
     *
     * See {@link #getTextureSheet()} to get the texture sheet this icon is associated with
     *
     * @return The icon for rendering this liquid
     */
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

    /**
     * Set the icon for rendering this liquid
     * It should be refreshed whenever textures are refreshed.
     *
     * See also {@link #setTextureSheet(String)} for setting the sheet this icon is associated with
     *
     * @param icon The icon to render
     * @return The liquid stack
     */
    @SideOnly(CLIENT)
    public LiquidStack setRenderingIcon(Icon icon)
    {
        this.renderingIcon = icon;
        return this;
    }

    @Override
    public final int hashCode()
    {
        return 31 * itemMeta + itemID;
    }

    @Override
    public final boolean equals(Object ob)
    {
        if (ob instanceof LiquidStack)
        {
            LiquidStack ls = (LiquidStack)ob;
            return ls.itemID == itemID && ls.itemMeta == itemMeta && (extra == null ? ls.extra == null : extra.equals(ls.extra));
        }
        return false;
    }


    /**
     * Get the canonical version of this liquid stack (will contain things like icons and texturesheets)
     * @return The canonical liquidstack
     */
    public LiquidStack canonical()
    {
        return LiquidDictionary.getCanonicalLiquid(this);
    }
}
