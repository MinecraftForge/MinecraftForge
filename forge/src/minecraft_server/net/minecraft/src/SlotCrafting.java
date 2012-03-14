package net.minecraft.src;

import net.minecraft.src.forge.ForgeHooks;

public class SlotCrafting extends Slot
{
    /** The craft matrix inventory linked to this result slot. */
    private final IInventory craftMatrix;
    private EntityPlayer thePlayer;
    private int field_48418_g;

    public SlotCrafting(EntityPlayer par1EntityPlayer, IInventory par2IInventory, IInventory par3IInventory, int par4, int par5, int par6)
    {
        super(par3IInventory, par4, par5, par6);
        this.thePlayer = par1EntityPlayer;
        this.craftMatrix = par2IInventory;
    }

    /**
     * Check if the stack is a valid item for this slot.
     */
    public boolean isItemValid(ItemStack par1ItemStack)
    {
        return false;
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
     * stack.
     */
    public ItemStack decrStackSize(int par1)
    {
        if (this.getHasStack())
        {
            this.field_48418_g += Math.min(par1, this.getStack().stackSize);
        }

        return super.decrStackSize(par1);
    }

    protected void func_48415_a(ItemStack par1ItemStack, int par2)
    {
        this.field_48418_g += par2;
        this.func_48416_b(par1ItemStack);
    }

    protected void func_48416_b(ItemStack par1ItemStack)
    {
        par1ItemStack.func_48584_a(this.thePlayer.worldObj, this.thePlayer, this.field_48418_g);
        this.field_48418_g = 0;

        if (par1ItemStack.itemID == Block.workbench.blockID)
        {
            this.thePlayer.addStat(AchievementList.buildWorkBench, 1);
        }
        else if (par1ItemStack.itemID == Item.pickaxeWood.shiftedIndex)
        {
            this.thePlayer.addStat(AchievementList.buildPickaxe, 1);
        }
        else if (par1ItemStack.itemID == Block.stoneOvenIdle.blockID)
        {
            this.thePlayer.addStat(AchievementList.buildFurnace, 1);
        }
        else if (par1ItemStack.itemID == Item.hoeWood.shiftedIndex)
        {
            this.thePlayer.addStat(AchievementList.buildHoe, 1);
        }
        else if (par1ItemStack.itemID == Item.bread.shiftedIndex)
        {
            this.thePlayer.addStat(AchievementList.makeBread, 1);
        }
        else if (par1ItemStack.itemID == Item.cake.shiftedIndex)
        {
            this.thePlayer.addStat(AchievementList.bakeCake, 1);
        }
        else if (par1ItemStack.itemID == Item.pickaxeStone.shiftedIndex)
        {
            this.thePlayer.addStat(AchievementList.buildBetterPickaxe, 1);
        }
        else if (par1ItemStack.itemID == Item.swordWood.shiftedIndex)
        {
            this.thePlayer.addStat(AchievementList.buildSword, 1);
        }
        else if (par1ItemStack.itemID == Block.enchantmentTable.blockID)
        {
            this.thePlayer.addStat(AchievementList.enchantments, 1);
        }
        else if (par1ItemStack.itemID == Block.bookShelf.blockID)
        {
            this.thePlayer.addStat(AchievementList.bookcase, 1);
        }

        ModLoader.takenFromCrafting(this.thePlayer, par1ItemStack, this.craftMatrix);
        ForgeHooks.onTakenFromCrafting(thePlayer, par1ItemStack, craftMatrix);
    }

    /**
     * Called when the player picks up an item from an inventory slot
     */
    public void onPickupFromSlot(ItemStack par1ItemStack)
    {
        this.func_48416_b(par1ItemStack);

        for (int var2 = 0; var2 < this.craftMatrix.getSizeInventory(); ++var2)
        {
            ItemStack var3 = this.craftMatrix.getStackInSlot(var2);

            if (var3 != null)
            {
                this.craftMatrix.decrStackSize(var2, 1);

                if (var3.getItem().hasContainerItem())
                {
                    ItemStack var4 = new ItemStack(var3.getItem().getContainerItem());

                    if (!var3.getItem().doesContainerItemLeaveCraftingGrid(var3) || !this.thePlayer.inventory.addItemStackToInventory(var4))
                    {
                        if (this.craftMatrix.getStackInSlot(var2) == null)
                        {
                            this.craftMatrix.setInventorySlotContents(var2, var4);
                        }
                        else
                        {
                            this.thePlayer.func_48348_b(var4);
                        }
                    }
                }
            }
        }
    }
}
