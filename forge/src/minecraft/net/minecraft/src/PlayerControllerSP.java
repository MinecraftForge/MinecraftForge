package net.minecraft.src;

import net.minecraft.client.Minecraft;
import net.minecraft.src.forge.ForgeHooks;

public class PlayerControllerSP extends PlayerController
{
    private int curBlockX = -1;
    private int curBlockY = -1;
    private int curBlockZ = -1;
    private float curBlockDamage = 0.0F;
    private float prevBlockDamage = 0.0F;
    private float blockDestroySoundCounter = 0.0F;
    private int blockHitWait = 0;

    public PlayerControllerSP(Minecraft par1Minecraft)
    {
        super(par1Minecraft);
    }

    /**
     * Flips the player around. Args: player
     */
    public void flipPlayer(EntityPlayer par1EntityPlayer)
    {
        par1EntityPlayer.rotationYaw = -180.0F;
    }

    public boolean shouldDrawHUD()
    {
        return true;
    }

    /**
     * Called when a player completes the destruction of a block
     */
    public boolean onPlayerDestroyBlock(int par1, int par2, int par3, int par4)
    {
        ItemStack stack = mc.thePlayer.getCurrentEquippedItem();
        if (stack != null && stack.getItem().onBlockStartBreak(stack, par1, par2, par3, mc.thePlayer))
        {
            return false;
        }
        
        int var5 = this.mc.theWorld.getBlockId(par1, par2, par3);
        int var6 = this.mc.theWorld.getBlockMetadata(par1, par2, par3);
        boolean var7 = super.onPlayerDestroyBlock(par1, par2, par3, par4);
        ItemStack var8 = this.mc.thePlayer.getCurrentEquippedItem();
        boolean var9 = Block.blocksList[var5].canHarvestBlock(mc.thePlayer, var6);

        if (var8 != null)
        {
            var8.onDestroyBlock(var5, par1, par2, par3, this.mc.thePlayer);

            if (var8.stackSize == 0)
            {
                var8.onItemDestroyedByUse(this.mc.thePlayer);
                this.mc.thePlayer.destroyCurrentEquippedItem();
            }
        }

        if (var7 && var9)
        {
            Block.blocksList[var5].harvestBlock(this.mc.theWorld, this.mc.thePlayer, par1, par2, par3, var6);
        }

        return var7;
    }

    /**
     * Called by Minecraft class when the player is hitting a block with an item. Args: x, y, z, side
     */
    public void clickBlock(int par1, int par2, int par3, int par4)
    {
        if (this.mc.thePlayer.canPlayerEdit(par1, par2, par3))
        {
            this.mc.theWorld.func_48457_a(this.mc.thePlayer, par1, par2, par3, par4);
            int var5 = this.mc.theWorld.getBlockId(par1, par2, par3);

            if (var5 > 0 && this.curBlockDamage == 0.0F)
            {
                Block.blocksList[var5].onBlockClicked(this.mc.theWorld, par1, par2, par3, this.mc.thePlayer);
            }

            if (var5 > 0 && Block.blocksList[var5].blockStrength(mc.theWorld, mc.thePlayer, par1, par2, par3) >= 1.0F)
            {
                this.onPlayerDestroyBlock(par1, par2, par3, par4);
            }
        }
    }

    /**
     * Resets current block damage and isHittingBlock
     */
    public void resetBlockRemoving()
    {
        this.curBlockDamage = 0.0F;
        this.blockHitWait = 0;
    }

    /**
     * Called when a player damages a block and updates damage counters
     */
    public void onPlayerDamageBlock(int par1, int par2, int par3, int par4)
    {
        if (this.blockHitWait > 0)
        {
            --this.blockHitWait;
        }
        else
        {
            if (par1 == this.curBlockX && par2 == this.curBlockY && par3 == this.curBlockZ)
            {
                int var5 = this.mc.theWorld.getBlockId(par1, par2, par3);

                if (!this.mc.thePlayer.canPlayerEdit(par1, par2, par3))
                {
                    return;
                }

                if (var5 == 0)
                {
                    return;
                }

                Block var6 = Block.blocksList[var5];
                this.curBlockDamage += var6.blockStrength(this.mc.thePlayer);

                if (this.blockDestroySoundCounter % 4.0F == 0.0F && var6 != null)
                {
                    this.mc.sndManager.playSound(var6.stepSound.getStepSound(), (float)par1 + 0.5F, (float)par2 + 0.5F, (float)par3 + 0.5F, (var6.stepSound.getVolume() + 1.0F) / 8.0F, var6.stepSound.getPitch() * 0.5F);
                }

                ++this.blockDestroySoundCounter;

                if (this.curBlockDamage >= 1.0F)
                {
                    this.onPlayerDestroyBlock(par1, par2, par3, par4);
                    this.curBlockDamage = 0.0F;
                    this.prevBlockDamage = 0.0F;
                    this.blockDestroySoundCounter = 0.0F;
                    this.blockHitWait = 5;
                }
            }
            else
            {
                this.curBlockDamage = 0.0F;
                this.prevBlockDamage = 0.0F;
                this.blockDestroySoundCounter = 0.0F;
                this.curBlockX = par1;
                this.curBlockY = par2;
                this.curBlockZ = par3;
            }
        }
    }

    public void setPartialTime(float par1)
    {
        if (this.curBlockDamage <= 0.0F)
        {
            this.mc.ingameGUI.damageGuiPartialTime = 0.0F;
            this.mc.renderGlobal.damagePartialTime = 0.0F;
        }
        else
        {
            float var2 = this.prevBlockDamage + (this.curBlockDamage - this.prevBlockDamage) * par1;
            this.mc.ingameGUI.damageGuiPartialTime = var2;
            this.mc.renderGlobal.damagePartialTime = var2;
        }
    }

    /**
     * player reach distance = 4F
     */
    public float getBlockReachDistance()
    {
        return 4.0F;
    }

    /**
     * Called on world change with the new World as the only parameter.
     */
    public void onWorldChange(World par1World)
    {
        super.onWorldChange(par1World);
    }

    public EntityPlayer createPlayer(World par1World)
    {
        EntityPlayer var2 = super.createPlayer(par1World);
        return var2;
    }

    public void updateController()
    {
        this.prevBlockDamage = this.curBlockDamage;
        this.mc.sndManager.playRandomMusicIfReady();
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
        
        if (par3ItemStack == null)
        {
            return false;
        }
        
        if (!par3ItemStack.useItem(par1EntityPlayer, par2World, par4, par5, par6, par7))
        {
            return false;
        }
        
        if (par3ItemStack.stackSize <= 0)
        {
            ForgeHooks.onDestroyCurrentItem(par1EntityPlayer, par3ItemStack);
        }
        return true;
    }

    public boolean func_35642_f()
    {
        return true;
    }
}
