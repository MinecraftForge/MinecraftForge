package net.minecraft.src;

import net.minecraft.src.forge.ForgeHooks;

public class ItemInWorldManager
{
    /** Forge reach distance hook */
    private double blockReachDistance = 5.0d;
    /** The world object that this object is connected to. */
    public World thisWorld;

    /** The player that this object references. */
    public EntityPlayer thisPlayer;

    /** The game mode, 1 for creative, 0 for survival. */
    private int gameType = -1;
    private float field_672_d = 0.0F;
    private int initialDamage;
    private int curBlockX;
    private int curBlockY;
    private int curBlockZ;
    private int curblockDamage;
    private boolean field_22050_k;
    private int field_22049_l;
    private int field_22048_m;
    private int field_22047_n;
    private int field_22046_o;

    public ItemInWorldManager(World par1World)
    {
        this.thisWorld = par1World;
    }

    public void toggleGameType(int par1)
    {
        this.gameType = par1;

        if (par1 == 0)
        {
            this.thisPlayer.capabilities.allowFlying = false;
            this.thisPlayer.capabilities.isFlying = false;
            this.thisPlayer.capabilities.depleteBuckets = false;
            this.thisPlayer.capabilities.disableDamage = false;
        }
        else
        {
            this.thisPlayer.capabilities.allowFlying = true;
            this.thisPlayer.capabilities.depleteBuckets = true;
            this.thisPlayer.capabilities.disableDamage = true;
        }
    }

    public int getGameType()
    {
        return this.gameType;
    }

    /**
     * Get if we are in creative game mode.
     */
    public boolean isCreative()
    {
        return this.gameType == 1;
    }

    public void func_35695_b(int par1)
    {
        if (this.gameType == -1)
        {
            this.gameType = par1;
        }

        this.toggleGameType(this.gameType);
    }

    public void updateBlockRemoving()
    {
        ++this.curblockDamage;

        if (this.field_22050_k)
        {
            int var1 = this.curblockDamage - this.field_22046_o;
            int var2 = this.thisWorld.getBlockId(this.field_22049_l, this.field_22048_m, this.field_22047_n);

            if (var2 != 0)
            {
                Block var3 = Block.blocksList[var2];
                float var4 = var3.blockStrength(thisWorld, this.thisPlayer, field_22049_l, field_22048_m, field_22047_n) * (float)(var1 + 1);

                if (var4 >= 1.0F)
                {
                    this.field_22050_k = false;
                    this.blockHarvessted(this.field_22049_l, this.field_22048_m, this.field_22047_n);
                }
            }
            else
            {
                this.field_22050_k = false;
            }
        }
    }

    public void blockClicked(int par1, int par2, int par3, int par4)
    {
        if (this.isCreative())
        {
            if (!this.thisWorld.func_48093_a((EntityPlayer)null, par1, par2, par3, par4))
            {
                this.blockHarvessted(par1, par2, par3);
            }
        }
        else
        {
            this.thisWorld.func_48093_a((EntityPlayer)null, par1, par2, par3, par4);
            this.initialDamage = this.curblockDamage;
            int var5 = this.thisWorld.getBlockId(par1, par2, par3);

            if (var5 > 0)
            {
                Block.blocksList[var5].onBlockClicked(this.thisWorld, par1, par2, par3, this.thisPlayer);
            }

            if (var5 > 0 && Block.blocksList[var5].blockStrength(thisWorld, this.thisPlayer, par1, par2, par3) >= 1.0F)
            {
                this.blockHarvessted(par1, par2, par3);
            }
            else
            {
                this.curBlockX = par1;
                this.curBlockY = par2;
                this.curBlockZ = par3;
            }
        }
    }

    public void blockRemoving(int par1, int par2, int par3)
    {
        if (par1 == this.curBlockX && par2 == this.curBlockY && par3 == this.curBlockZ)
        {
            int var4 = this.curblockDamage - this.initialDamage;
            int var5 = this.thisWorld.getBlockId(par1, par2, par3);

            if (var5 != 0)
            {
                Block var6 = Block.blocksList[var5];
                float var7 = var6.blockStrength(thisWorld, this.thisPlayer, par1, par2, par3) * (float)(var4 + 1);

                if (var7 >= 0.7F)
                {
                    this.blockHarvessted(par1, par2, par3);
                }
                else if (!this.field_22050_k)
                {
                    this.field_22050_k = true;
                    this.field_22049_l = par1;
                    this.field_22048_m = par2;
                    this.field_22047_n = par3;
                    this.field_22046_o = this.initialDamage;
                }
            }
        }

        this.field_672_d = 0.0F;
    }

    /**
     * Removes a block and triggers the appropriate events
     */
    public boolean removeBlock(int par1, int par2, int par3)
    {
        Block var4 = Block.blocksList[this.thisWorld.getBlockId(par1, par2, par3)];
        int var5 = this.thisWorld.getBlockMetadata(par1, par2, par3);
        boolean var6 = (var4 != null && var4.removeBlockByPlayer(thisWorld, thisPlayer, par1, par2, par3));

        if (var4 != null && var6)
        {
            var4.onBlockDestroyedByPlayer(this.thisWorld, par1, par2, par3, var5);
        }

        return var6;
    }

    public boolean blockHarvessted(int par1, int par2, int par3)
    {
        ItemStack stack = thisPlayer.getCurrentEquippedItem();
        if (stack != null && stack.getItem().onBlockStartBreak(stack, par1, par2, par3, thisPlayer))
        {
            return false;
        }
        int var4 = this.thisWorld.getBlockId(par1, par2, par3);
        int var5 = this.thisWorld.getBlockMetadata(par1, par2, par3);
        this.thisWorld.playAuxSFXAtEntity(this.thisPlayer, 2001, par1, par2, par3, var4 + (this.thisWorld.getBlockMetadata(par1, par2, par3) << 12));
        boolean var6 = this.removeBlock(par1, par2, par3);

        if (this.isCreative())
        {
            ((EntityPlayerMP)this.thisPlayer).playerNetServerHandler.sendPacket(new Packet53BlockChange(par1, par2, par3, this.thisWorld));
        }
        else
        {
            ItemStack var7 = this.thisPlayer.getCurrentEquippedItem();
            boolean var8 = Block.blocksList[var4].canHarvestBlock(thisPlayer, var5);

            if (var7 != null)
            {
                var7.onDestroyBlock(var4, par1, par2, par3, this.thisPlayer);

                if (var7.stackSize == 0)
                {
                    var7.onItemDestroyedByUse(this.thisPlayer);
                    this.thisPlayer.destroyCurrentEquippedItem();
                    ForgeHooks.onDestroyCurrentItem(thisPlayer, var7);
                }
            }

            if (var6 && var8)
            {
                Block.blocksList[var4].harvestBlock(this.thisWorld, this.thisPlayer, par1, par2, par3, var5);
            }
        }

        return var6;
    }

    public boolean itemUsed(EntityPlayer par1EntityPlayer, World par2World, ItemStack par3ItemStack)
    {
        int var4 = par3ItemStack.stackSize;
        int var5 = par3ItemStack.getItemDamage();
        ItemStack var6 = par3ItemStack.useItemRightClick(par2World, par1EntityPlayer);

        if (var6 == par3ItemStack && (var6 == null || var6.stackSize == var4) && (var6 == null || var6.getMaxItemUseDuration() <= 0))
        {
            return false;
        }
        else
        {
            par1EntityPlayer.inventory.mainInventory[par1EntityPlayer.inventory.currentItem] = var6;

            if (this.isCreative())
            {
                var6.stackSize = var4;
                var6.setItemDamage(var5);
            }

            if (var6.stackSize == 0)
            {
                par1EntityPlayer.inventory.mainInventory[par1EntityPlayer.inventory.currentItem] = null;
            }

            return true;
        }
    }

    /**
     * Will either active a block (if there is one at the given location), otherwise will try to use the item being hold
     */
    public boolean activeBlockOrUseItem(EntityPlayer par1EntityPlayer, World par2World, ItemStack par3ItemStack, int par4, int par5, int par6, int par7)
    {
        if (par3ItemStack != null && par3ItemStack.getItem().onItemUseFirst(par3ItemStack, par1EntityPlayer, par2World, par4, par5, par6, par7))
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
        else if (this.isCreative())
        {
            int var9 = par3ItemStack.getItemDamage();
            int var10 = par3ItemStack.stackSize;
            boolean var11 = par3ItemStack.useItem(par1EntityPlayer, par2World, par4, par5, par6, par7);
            par3ItemStack.setItemDamage(var9);
            par3ItemStack.stackSize = var10;
            return var11;
        }
        else
        {
            if (!par3ItemStack.useItem(par1EntityPlayer, par2World, par4, par5, par6, par7))
            {
                return false;
            }
            if (par3ItemStack.stackSize == 0)
            {
                ForgeHooks.onDestroyCurrentItem(par1EntityPlayer, par3ItemStack);
            }
            return true;
        }
    }

    /**
     * Sets the world instance.
     */
    public void setWorld(WorldServer par1WorldServer)
    {
        this.thisWorld = par1WorldServer;
    }
    
    public double getBlockReachDistance()
    {
        return blockReachDistance;
    }
    public void setBlockReachDistance(double distance)
    {
        blockReachDistance = distance;
    }
}
