package net.minecraft.src;

import net.minecraft.client.Minecraft;

public class PlayerControllerCreative extends PlayerController
{
    private int field_35647_c;

    public PlayerControllerCreative(Minecraft par1Minecraft)
    {
        super(par1Minecraft);
        this.isInTestMode = true;
    }

    /**
     * Enables creative abilities to the player
     */
    public static void enableAbilities(EntityPlayer par0EntityPlayer)
    {
        par0EntityPlayer.capabilities.allowFlying = true;
        par0EntityPlayer.capabilities.depleteBuckets = true;
        par0EntityPlayer.capabilities.disableDamage = true;
    }

    /**
     * Disables creative abilities to the player.
     */
    public static void disableAbilities(EntityPlayer par0EntityPlayer)
    {
        par0EntityPlayer.capabilities.allowFlying = false;
        par0EntityPlayer.capabilities.isFlying = false;
        par0EntityPlayer.capabilities.depleteBuckets = false;
        par0EntityPlayer.capabilities.disableDamage = false;
    }

    public void func_6473_b(EntityPlayer par1EntityPlayer)
    {
        enableAbilities(par1EntityPlayer);

        for (int var2 = 0; var2 < 9; ++var2)
        {
            if (par1EntityPlayer.inventory.mainInventory[var2] == null)
            {
                par1EntityPlayer.inventory.mainInventory[var2] = new ItemStack((Block)Session.registeredBlocksList.get(var2));
            }
        }
    }

    /**
     * Called from a PlayerController when the player is hitting a block with an item in Creative mode. Args: Minecraft
     * instance, player controller, x, y, z, side
     */
    public static void clickBlockCreative(Minecraft par0Minecraft, PlayerController par1PlayerController, int par2, int par3, int par4, int par5)
    {
        ItemStack itemstack = par0Minecraft.thePlayer.getCurrentEquippedItem();
        if (itemstack != null && itemstack.getItem().onBlockStartBreak(itemstack, par2, par3, par4, par0Minecraft.thePlayer))
        {
            return;
        }
        if (!par0Minecraft.theWorld.func_48457_a(par0Minecraft.thePlayer, par2, par3, par4, par5))
        {
            par1PlayerController.onPlayerDestroyBlock(par2, par3, par4, par5);
        }
    }

    /**
     * Handles a players right click
     */
    public boolean onPlayerRightClick(EntityPlayer par1EntityPlayer, World par2World, ItemStack par3ItemStack, int par4, int par5, int par6, int par7)
    {
        if (par3ItemStack != null && 
            par3ItemStack.getItem() != null && 
            par3ItemStack.getItem().onItemUseFirst(par3ItemStack, par1EntityPlayer, par2World, par4, par5, par6, par7))
        {
                return true;
        }
        
        int var8 = par2World.getBlockId(par4, par5, par6);

        if (var8 > 0 && Block.blocksList[var8].blockActivated(par2World, par4, par5, par6, par1EntityPlayer))
        {
            return true;
        }
        else if (par3ItemStack == null)
        {
            return false;
        }
        else
        {
            int var9 = par3ItemStack.getItemDamage();
            int var10 = par3ItemStack.stackSize;
            boolean var11 = par3ItemStack.useItem(par1EntityPlayer, par2World, par4, par5, par6, par7);
            par3ItemStack.setItemDamage(var9);
            par3ItemStack.stackSize = var10;
            return var11;
        }
    }

    /**
     * Called by Minecraft class when the player is hitting a block with an item. Args: x, y, z, side
     */
    public void clickBlock(int par1, int par2, int par3, int par4)
    {
        clickBlockCreative(this.mc, this, par1, par2, par3, par4);
        this.field_35647_c = 5;
    }

    /**
     * Called when a player damages a block and updates damage counters
     */
    public void onPlayerDamageBlock(int par1, int par2, int par3, int par4)
    {
        --this.field_35647_c;

        if (this.field_35647_c <= 0)
        {
            this.field_35647_c = 5;
            clickBlockCreative(this.mc, this, par1, par2, par3, par4);
        }
    }

    /**
     * Resets current block damage and isHittingBlock
     */
    public void resetBlockRemoving() {}

    public boolean shouldDrawHUD()
    {
        return false;
    }

    /**
     * Called on world change with the new World as the only parameter.
     */
    public void onWorldChange(World par1World)
    {
        super.onWorldChange(par1World);
    }

    /**
     * player reach distance = 4F
     */
    public float getBlockReachDistance()
    {
        return 5.0F;
    }

    /**
     * Checks if the player is not creative, used for checking if it should break a block instantly
     */
    public boolean isNotCreative()
    {
        return false;
    }

    /**
     * returns true if player is in creative mode
     */
    public boolean isInCreativeMode()
    {
        return true;
    }

    /**
     * true for hitting entities far away.
     */
    public boolean extendedReach()
    {
        return true;
    }
}
