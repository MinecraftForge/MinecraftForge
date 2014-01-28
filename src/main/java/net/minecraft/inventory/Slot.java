package net.minecraft.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

public class Slot
{
    // JAVADOC FIELD $$ field_75225_a
    private final int slotIndex;
    // JAVADOC FIELD $$ field_75224_c
    public final IInventory inventory;
    // JAVADOC FIELD $$ field_75222_d
    public int slotNumber;
    // JAVADOC FIELD $$ field_75223_e
    public int xDisplayPosition;
    // JAVADOC FIELD $$ field_75221_f
    public int yDisplayPosition;
    private static final String __OBFID = "CL_00001762";

    /** Position within background texture file, normally -1 which causes no background to be drawn. */
    protected IIcon backgroundIcon = null;

    /** Background texture file assigned to this slot, if any. Vanilla "/gui/items.png" is used if this is null. */
    @SideOnly(Side.CLIENT)
    protected ResourceLocation texture;

    public Slot(IInventory par1IInventory, int par2, int par3, int par4)
    {
        this.inventory = par1IInventory;
        this.slotIndex = par2;
        this.xDisplayPosition = par3;
        this.yDisplayPosition = par4;
    }

    // JAVADOC METHOD $$ func_75220_a
    public void onSlotChange(ItemStack par1ItemStack, ItemStack par2ItemStack)
    {
        if (par1ItemStack != null && par2ItemStack != null)
        {
            if (par1ItemStack.getItem() == par2ItemStack.getItem())
            {
                int i = par2ItemStack.stackSize - par1ItemStack.stackSize;

                if (i > 0)
                {
                    this.onCrafting(par1ItemStack, i);
                }
            }
        }
    }

    // JAVADOC METHOD $$ func_75210_a
    protected void onCrafting(ItemStack par1ItemStack, int par2) {}

    // JAVADOC METHOD $$ func_75208_c
    protected void onCrafting(ItemStack par1ItemStack) {}

    public void onPickupFromSlot(EntityPlayer par1EntityPlayer, ItemStack par2ItemStack)
    {
        this.onSlotChanged();
    }

    // JAVADOC METHOD $$ func_75214_a
    public boolean isItemValid(ItemStack par1ItemStack)
    {
        return true;
    }

    // JAVADOC METHOD $$ func_75211_c
    public ItemStack getStack()
    {
        return this.inventory.getStackInSlot(this.slotIndex);
    }

    // JAVADOC METHOD $$ func_75216_d
    public boolean getHasStack()
    {
        return this.getStack() != null;
    }

    // JAVADOC METHOD $$ func_75215_d
    public void putStack(ItemStack par1ItemStack)
    {
        this.inventory.setInventorySlotContents(this.slotIndex, par1ItemStack);
        this.onSlotChanged();
    }

    // JAVADOC METHOD $$ func_75218_e
    public void onSlotChanged()
    {
        this.inventory.onInventoryChanged();
    }

    // JAVADOC METHOD $$ func_75219_a
    public int getSlotStackLimit()
    {
        return this.inventory.getInventoryStackLimit();
    }

    // JAVADOC METHOD $$ func_75209_a
    public ItemStack decrStackSize(int par1)
    {
        return this.inventory.decrStackSize(this.slotIndex, par1);
    }

    // JAVADOC METHOD $$ func_75217_a
    public boolean isSlotInInventory(IInventory par1IInventory, int par2)
    {
        return par1IInventory == this.inventory && par2 == this.slotIndex;
    }

    // JAVADOC METHOD $$ func_82869_a
    public boolean canTakeStack(EntityPlayer par1EntityPlayer)
    {
        return true;
    }

    // JAVADOC METHOD $$ func_75212_b
    @SideOnly(Side.CLIENT)
    public IIcon getBackgroundIconIndex()
    {
        return backgroundIcon;
    }

    @SideOnly(Side.CLIENT)
    public boolean func_111238_b()
    {
        return true;
    }

    /*========================================= FORGE START =====================================*/
    /**
     * Gets the path of the texture file to use for the background image of this slot when drawing the GUI.
     * @return String: The texture file that will be used in GuiContainer.drawSlotInventory for the slot background.
     */
    @SideOnly(Side.CLIENT)
    public ResourceLocation getBackgroundIconTexture()
    {
        return (texture == null ? TextureMap.locationItemsTexture : texture);
    }

    /**
     * Sets which icon index to use as the background image of the slot when it's empty.
     * @param icon The icon to use, null for none
     */
    public void setBackgroundIcon(IIcon icon)
    {
        backgroundIcon = icon;
    }

    /**
     * Sets the texture file to use for the background image of the slot when it's empty.
     * @param textureFilename String: Path of texture file to use, or null to use "/gui/items.png"
     */
    @SideOnly(Side.CLIENT)
    public void setBackgroundIconTexture(ResourceLocation texture)
    {
        this.texture = texture;
    }

    /**
     * Retrieves the index in the inventory for this slot, this value should typically not
     * be used, but can be useful for some occasions.
     *
     * @return Index in associated inventory for this slot.
     */
    public int getSlotIndex()
    {
        return slotIndex;
    }
    /*========================================= FORGE END =====================================*/
}