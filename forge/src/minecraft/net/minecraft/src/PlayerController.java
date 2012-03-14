package net.minecraft.src;

import net.minecraft.client.Minecraft;
import net.minecraft.src.forge.ForgeHooks;

public abstract class PlayerController
{
    /** A reference to the Minecraft object. */
    protected final Minecraft mc;
    public boolean isInTestMode = false;

    public PlayerController(Minecraft par1Minecraft)
    {
        this.mc = par1Minecraft;
    }

    /**
     * Called on world change with the new World as the only parameter.
     */
    public void onWorldChange(World par1World) {}

    /**
     * Called by Minecraft class when the player is hitting a block with an item. Args: x, y, z, side
     */
    public abstract void clickBlock(int var1, int var2, int var3, int var4);

    /**
     * Called when a player completes the destruction of a block
     */
    public boolean onPlayerDestroyBlock(int par1, int par2, int par3, int par4)
    {
        World var5 = this.mc.theWorld;
        Block var6 = Block.blocksList[var5.getBlockId(par1, par2, par3)];

        if (var6 == null)
        {
            return false;
        }
        else
        {
            var5.playAuxSFX(2001, par1, par2, par3, var6.blockID + (var5.getBlockMetadata(par1, par2, par3) << 12));
            int var7 = var5.getBlockMetadata(par1, par2, par3);
            boolean var8 = var6.removeBlockByPlayer(var5, mc.thePlayer, par1, par2, par3);

            if (var6 != null && var8)
            {
                var6.onBlockDestroyedByPlayer(var5, par1, par2, par3, var7);
            }

            return var8;
        }
    }

    /**
     * Called when a player damages a block and updates damage counters
     */
    public abstract void onPlayerDamageBlock(int var1, int var2, int var3, int var4);

    /**
     * Resets current block damage and isHittingBlock
     */
    public abstract void resetBlockRemoving();

    public void setPartialTime(float par1) {}

    /**
     * player reach distance = 4F
     */
    public abstract float getBlockReachDistance();

    /**
     * Notifies the server of things like consuming food, etc...
     */
    public boolean sendUseItem(EntityPlayer par1EntityPlayer, World par2World, ItemStack par3ItemStack)
    {
        int var4 = par3ItemStack.stackSize;
        ItemStack var5 = par3ItemStack.useItemRightClick(par2World, par1EntityPlayer);

        if (var5 == par3ItemStack && (var5 == null || var5.stackSize == var4))
        {
            return false;
        }
        else
        {
            par1EntityPlayer.inventory.mainInventory[par1EntityPlayer.inventory.currentItem] = var5;

            if (var5.stackSize == 0)
            {
                par1EntityPlayer.inventory.mainInventory[par1EntityPlayer.inventory.currentItem] = null;
                ForgeHooks.onDestroyCurrentItem(par1EntityPlayer, var5);
            }

            return true;
        }
    }

    /**
     * Flips the player around. Args: player
     */
    public void flipPlayer(EntityPlayer par1EntityPlayer) {}

    public void updateController() {}

    public abstract boolean shouldDrawHUD();

    public void func_6473_b(EntityPlayer par1EntityPlayer)
    {
        PlayerControllerCreative.disableAbilities(par1EntityPlayer);
    }

    /**
     * Handles a players right click
     */
    public abstract boolean onPlayerRightClick(EntityPlayer var1, World var2, ItemStack var3, int var4, int var5, int var6, int var7);

    public EntityPlayer createPlayer(World par1World)
    {
        return new EntityPlayerSP(this.mc, par1World, this.mc.session, par1World.worldProvider.worldType);
    }

    /**
     * Interacts with an entity
     */
    public void interactWithEntity(EntityPlayer par1EntityPlayer, Entity par2Entity)
    {
        par1EntityPlayer.useCurrentItemOnEntity(par2Entity);
    }

    /**
     * Attacks an entity
     */
    public void attackEntity(EntityPlayer par1EntityPlayer, Entity par2Entity)
    {
        par1EntityPlayer.attackTargetEntityWithCurrentItem(par2Entity);
    }

    public ItemStack windowClick(int par1, int par2, int par3, boolean par4, EntityPlayer par5EntityPlayer)
    {
        return par5EntityPlayer.craftingInventory.slotClick(par2, par3, par4, par5EntityPlayer);
    }

    public void func_20086_a(int par1, EntityPlayer par2EntityPlayer)
    {
        par2EntityPlayer.craftingInventory.onCraftGuiClosed(par2EntityPlayer);
        par2EntityPlayer.craftingInventory = par2EntityPlayer.inventorySlots;
    }

    public void func_40593_a(int par1, int par2) {}

    public boolean func_35643_e()
    {
        return false;
    }

    public void onStoppedUsingItem(EntityPlayer par1EntityPlayer)
    {
        par1EntityPlayer.stopUsingItem();
    }

    public boolean func_35642_f()
    {
        return false;
    }

    /**
     * Checks if the player is not creative, used for checking if it should break a block instantly
     */
    public boolean isNotCreative()
    {
        return true;
    }

    /**
     * returns true if player is in creative mode
     */
    public boolean isInCreativeMode()
    {
        return false;
    }

    /**
     * true for hitting entities far away.
     */
    public boolean extendedReach()
    {
        return false;
    }

    /**
     * Used in PlayerControllerMP to update the server with an ItemStack in a slot.
     */
    public void sendSlotPacket(ItemStack par1ItemStack, int par2) {}

    public void func_35639_a(ItemStack par1ItemStack) {}
}
