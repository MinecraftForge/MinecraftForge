package net.minecraft.src;

import net.minecraft.client.Minecraft;

public class EntityPlayerSP extends EntityPlayer
{
    public MovementInput movementInput;
    protected Minecraft mc;

    /**
     * Used to tell if the player pressed forward twice. If this is at 0 and it's pressed (And they are allowed to
     * sprint, aka enough food on the ground etc) it sets this to 7. If it's pressed and it's greater than 0 enable
     * sprinting.
     */
    protected int sprintToggleTimer = 0;

    /** Ticks left before sprinting is disabled. */
    public int sprintingTicksLeft = 0;
    public float renderArmYaw;
    public float renderArmPitch;
    public float prevRenderArmYaw;
    public float prevRenderArmPitch;
    private MouseFilter field_21903_bJ = new MouseFilter();
    private MouseFilter field_21904_bK = new MouseFilter();
    private MouseFilter field_21902_bL = new MouseFilter();

    public EntityPlayerSP(Minecraft par1Minecraft, World par2World, Session par3Session, int par4)
    {
        super(par2World);
        this.mc = par1Minecraft;
        this.dimension = par4;

        if (par3Session != null && par3Session.username != null && par3Session.username.length() > 0)
        {
            this.skinUrl = "http://s3.amazonaws.com/MinecraftSkins/" + par3Session.username + ".png";
        }

        this.username = par3Session.username;
    }

    /**
     * Tries to moves the entity by the passed in displacement. Args: x, y, z
     */
    public void moveEntity(double par1, double par3, double par5)
    {
        super.moveEntity(par1, par3, par5);
    }

    public void updateEntityActionState()
    {
        super.updateEntityActionState();
        this.moveStrafing = this.movementInput.moveStrafe;
        this.moveForward = this.movementInput.moveForward;
        this.isJumping = this.movementInput.jump;
        this.prevRenderArmYaw = this.renderArmYaw;
        this.prevRenderArmPitch = this.renderArmPitch;
        this.renderArmPitch = (float)((double)this.renderArmPitch + (double)(this.rotationPitch - this.renderArmPitch) * 0.5D);
        this.renderArmYaw = (float)((double)this.renderArmYaw + (double)(this.rotationYaw - this.renderArmYaw) * 0.5D);
    }

    /**
     * Returns whether the entity is in a local (client) world
     */
    protected boolean isClientWorld()
    {
        return true;
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        if (this.sprintingTicksLeft > 0)
        {
            --this.sprintingTicksLeft;

            if (this.sprintingTicksLeft == 0)
            {
                this.setSprinting(false);
            }
        }

        if (this.sprintToggleTimer > 0)
        {
            --this.sprintToggleTimer;
        }

        if (this.mc.playerController.func_35643_e())
        {
            this.posX = this.posZ = 0.5D;
            this.posX = 0.0D;
            this.posZ = 0.0D;
            this.rotationYaw = (float)this.ticksExisted / 12.0F;
            this.rotationPitch = 10.0F;
            this.posY = 68.5D;
        }
        else
        {
            if (!this.mc.statFileWriter.hasAchievementUnlocked(AchievementList.openInventory))
            {
                this.mc.guiAchievement.queueAchievementInformation(AchievementList.openInventory);
            }

            this.prevTimeInPortal = this.timeInPortal;
            boolean var1;

            if (this.inPortal)
            {
                if (!this.worldObj.isRemote && this.ridingEntity != null)
                {
                    this.mountEntity((Entity)null);
                }

                if (this.mc.currentScreen != null)
                {
                    this.mc.displayGuiScreen((GuiScreen)null);
                }

                if (this.timeInPortal == 0.0F)
                {
                    this.mc.sndManager.playSoundFX("portal.trigger", 1.0F, this.rand.nextFloat() * 0.4F + 0.8F);
                }

                this.timeInPortal += 0.0125F;

                if (this.timeInPortal >= 1.0F)
                {
                    this.timeInPortal = 1.0F;

                    if (!this.worldObj.isRemote)
                    {
                        this.timeUntilPortal = 10;
                        this.mc.sndManager.playSoundFX("portal.travel", 1.0F, this.rand.nextFloat() * 0.4F + 0.8F);
                        var1 = false;
                        byte var5;

                        if (this.dimension == -1)
                        {
                            var5 = 0;
                        }
                        else
                        {
                            var5 = -1;
                        }

                        this.mc.usePortal(var5);
                        this.triggerAchievement(AchievementList.portal);
                    }
                }

                this.inPortal = false;
            }
            else if (this.isPotionActive(Potion.confusion) && this.getActivePotionEffect(Potion.confusion).getDuration() > 60)
            {
                this.timeInPortal += 0.006666667F;

                if (this.timeInPortal > 1.0F)
                {
                    this.timeInPortal = 1.0F;
                }
            }
            else
            {
                if (this.timeInPortal > 0.0F)
                {
                    this.timeInPortal -= 0.05F;
                }

                if (this.timeInPortal < 0.0F)
                {
                    this.timeInPortal = 0.0F;
                }
            }

            if (this.timeUntilPortal > 0)
            {
                --this.timeUntilPortal;
            }

            var1 = this.movementInput.jump;
            float var2 = 0.8F;
            boolean var3 = this.movementInput.moveForward >= var2;
            this.movementInput.updatePlayerMoveState(this);

            if (this.isUsingItem())
            {
                this.movementInput.moveStrafe *= 0.2F;
                this.movementInput.moveForward *= 0.2F;
                this.sprintToggleTimer = 0;
            }

            if (this.movementInput.sneak && this.ySize < 0.2F)
            {
                this.ySize = 0.2F;
            }

            this.pushOutOfBlocks(this.posX - (double)this.width * 0.35D, this.boundingBox.minY + 0.5D, this.posZ + (double)this.width * 0.35D);
            this.pushOutOfBlocks(this.posX - (double)this.width * 0.35D, this.boundingBox.minY + 0.5D, this.posZ - (double)this.width * 0.35D);
            this.pushOutOfBlocks(this.posX + (double)this.width * 0.35D, this.boundingBox.minY + 0.5D, this.posZ - (double)this.width * 0.35D);
            this.pushOutOfBlocks(this.posX + (double)this.width * 0.35D, this.boundingBox.minY + 0.5D, this.posZ + (double)this.width * 0.35D);
            boolean var4 = (float)this.getFoodStats().getFoodLevel() > 6.0F;

            if (this.onGround && !var3 && this.movementInput.moveForward >= var2 && !this.isSprinting() && var4 && !this.isUsingItem() && !this.isPotionActive(Potion.blindness))
            {
                if (this.sprintToggleTimer == 0)
                {
                    this.sprintToggleTimer = 7;
                }
                else
                {
                    this.setSprinting(true);
                    this.sprintToggleTimer = 0;
                }
            }

            if (this.isSneaking())
            {
                this.sprintToggleTimer = 0;
            }

            if (this.isSprinting() && (this.movementInput.moveForward < var2 || this.isCollidedHorizontally || !var4))
            {
                this.setSprinting(false);
            }

            if (this.capabilities.allowFlying && !var1 && this.movementInput.jump)
            {
                if (this.flyToggleTimer == 0)
                {
                    this.flyToggleTimer = 7;
                }
                else
                {
                    this.capabilities.isFlying = !this.capabilities.isFlying;
                    this.flyToggleTimer = 0;
                }
            }

            if (this.capabilities.isFlying)
            {
                if (this.movementInput.sneak)
                {
                    this.motionY -= 0.15D;
                }

                if (this.movementInput.jump)
                {
                    this.motionY += 0.15D;
                }
            }

            super.onLivingUpdate();

            if (this.onGround && this.capabilities.isFlying)
            {
                this.capabilities.isFlying = false;
            }
        }
    }

    public void travelToTheEnd(int par1)
    {
        if (!this.worldObj.isRemote)
        {
            if (this.dimension == 1 && par1 == 1)
            {
                this.triggerAchievement(AchievementList.theEnd2);
                this.mc.displayGuiScreen(new GuiWinGame());
            }
            else
            {
                this.triggerAchievement(AchievementList.theEnd);
                this.mc.sndManager.playSoundFX("portal.travel", 1.0F, this.rand.nextFloat() * 0.4F + 0.8F);
                this.mc.usePortal(1);
            }
        }
    }

    /**
     * Gets the player's field of view multiplier. (ex. when flying)
     */
    public float getFOVMultiplier()
    {
        float var1 = 1.0F;

        if (this.capabilities.isFlying)
        {
            var1 *= 1.1F;
        }

        var1 *= (this.landMovementFactor * this.getSpeedModifier() / this.speedOnGround + 1.0F) / 2.0F;

        if (this.isUsingItem() && this.getItemInUse().itemID == Item.bow.shiftedIndex)
        {
            int var2 = this.getItemInUseDuration();
            float var3 = (float)var2 / 20.0F;

            if (var3 > 1.0F)
            {
                var3 = 1.0F;
            }
            else
            {
                var3 *= var3;
            }

            var1 *= 1.0F - var3 * 0.15F;
        }

        return var1;
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("Score", this.score);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        this.score = par1NBTTagCompound.getInteger("Score");
    }

    /**
     * sets current screen to null (used on escape buttons of GUIs)
     */
    public void closeScreen()
    {
        super.closeScreen();
        this.mc.displayGuiScreen((GuiScreen)null);
    }

    /**
     * Displays the GUI for editing a sign. Args: tileEntitySign
     */
    public void displayGUIEditSign(TileEntitySign par1TileEntitySign)
    {
        this.mc.displayGuiScreen(new GuiEditSign(par1TileEntitySign));
    }

    /**
     * Displays the GUI for interacting with a chest inventory. Args: chestInventory
     */
    public void displayGUIChest(IInventory par1IInventory)
    {
        this.mc.displayGuiScreen(new GuiChest(this.inventory, par1IInventory));
    }

    /**
     * Displays the crafting GUI for a workbench.
     */
    public void displayWorkbenchGUI(int par1, int par2, int par3)
    {
        this.mc.displayGuiScreen(new GuiCrafting(this.inventory, this.worldObj, par1, par2, par3));
    }

    public void displayGUIEnchantment(int par1, int par2, int par3)
    {
        this.mc.displayGuiScreen(new GuiEnchantment(this.inventory, this.worldObj, par1, par2, par3));
    }

    /**
     * Displays the furnace GUI for the passed in furnace entity. Args: tileEntityFurnace
     */
    public void displayGUIFurnace(TileEntityFurnace par1TileEntityFurnace)
    {
        this.mc.displayGuiScreen(new GuiFurnace(this.inventory, par1TileEntityFurnace));
    }

    /**
     * Displays the GUI for interacting with a brewing stand.
     */
    public void displayGUIBrewingStand(TileEntityBrewingStand par1TileEntityBrewingStand)
    {
        this.mc.displayGuiScreen(new GuiBrewingStand(this.inventory, par1TileEntityBrewingStand));
    }

    /**
     * Displays the dipsenser GUI for the passed in dispenser entity. Args: TileEntityDispenser
     */
    public void displayGUIDispenser(TileEntityDispenser par1TileEntityDispenser)
    {
        this.mc.displayGuiScreen(new GuiDispenser(this.inventory, par1TileEntityDispenser));
    }

    /**
     * is called when the player performs a critical hit on the Entity. Args: entity that was hit critically
     */
    public void onCriticalHit(Entity par1Entity)
    {
        this.mc.effectRenderer.addEffect(new EntityCrit2FX(this.mc.theWorld, par1Entity));
    }

    public void onEnchantmentCritical(Entity par1Entity)
    {
        EntityCrit2FX var2 = new EntityCrit2FX(this.mc.theWorld, par1Entity, "magicCrit");
        this.mc.effectRenderer.addEffect(var2);
    }

    /**
     * Called whenever an item is picked up from walking over it. Args: pickedUpEntity, stackSize
     */
    public void onItemPickup(Entity par1Entity, int par2)
    {
        this.mc.effectRenderer.addEffect(new EntityPickupFX(this.mc.theWorld, par1Entity, this, -0.5F));
    }

    /**
     * Sends a chat message from the player. Args: chatMessage
     */
    public void sendChatMessage(String par1Str) {}

    /**
     * Returns if this entity is sneaking.
     */
    public boolean isSneaking()
    {
        return this.movementInput.sneak && !this.sleeping;
    }

    /**
     * Updates health locally.
     */
    public void setHealth(int par1)
    {
        int var2 = this.getEntityHealth() - par1;

        if (var2 <= 0)
        {
            this.setEntityHealth(par1);

            if (var2 < 0)
            {
                this.heartsLife = this.heartsHalvesLife / 2;
            }
        }
        else
        {
            this.naturalArmorRating = var2;
            this.setEntityHealth(this.getEntityHealth());
            this.heartsLife = this.heartsHalvesLife;
            this.damageEntity(DamageSource.generic, var2);
            this.hurtTime = this.maxHurtTime = 10;
        }
    }

    public void respawnPlayer()
    {
        this.mc.respawn(false, 0, false);
    }

    public void func_6420_o() {}

    /**
     * Add a chat message to the player
     */
    public void addChatMessage(String par1Str)
    {
        this.mc.ingameGUI.addChatMessageTranslate(par1Str);
    }

    /**
     * Adds a value to a statistic field.
     */
    public void addStat(StatBase par1StatBase, int par2)
    {
        if (par1StatBase != null)
        {
            if (par1StatBase.isAchievement())
            {
                Achievement var3 = (Achievement)par1StatBase;

                if (var3.parentAchievement == null || this.mc.statFileWriter.hasAchievementUnlocked(var3.parentAchievement))
                {
                    if (!this.mc.statFileWriter.hasAchievementUnlocked(var3))
                    {
                        this.mc.guiAchievement.queueTakenAchievement(var3);
                    }

                    this.mc.statFileWriter.readStat(par1StatBase, par2);
                }
            }
            else
            {
                this.mc.statFileWriter.readStat(par1StatBase, par2);
            }
        }
    }

    private boolean isBlockTranslucent(int par1, int par2, int par3)
    {
        return this.worldObj.isBlockNormalCube(par1, par2, par3);
    }

    /**
     * Adds velocity to push the entity out of blocks at the specified x, y, z position Args: x, y, z
     */
    protected boolean pushOutOfBlocks(double par1, double par3, double par5)
    {
        int var7 = MathHelper.floor_double(par1);
        int var8 = MathHelper.floor_double(par3);
        int var9 = MathHelper.floor_double(par5);
        double var10 = par1 - (double)var7;
        double var12 = par5 - (double)var9;

        if (this.isBlockTranslucent(var7, var8, var9) || this.isBlockTranslucent(var7, var8 + 1, var9))
        {
            boolean var14 = !this.isBlockTranslucent(var7 - 1, var8, var9) && !this.isBlockTranslucent(var7 - 1, var8 + 1, var9);
            boolean var15 = !this.isBlockTranslucent(var7 + 1, var8, var9) && !this.isBlockTranslucent(var7 + 1, var8 + 1, var9);
            boolean var16 = !this.isBlockTranslucent(var7, var8, var9 - 1) && !this.isBlockTranslucent(var7, var8 + 1, var9 - 1);
            boolean var17 = !this.isBlockTranslucent(var7, var8, var9 + 1) && !this.isBlockTranslucent(var7, var8 + 1, var9 + 1);
            byte var18 = -1;
            double var19 = 9999.0D;

            if (var14 && var10 < var19)
            {
                var19 = var10;
                var18 = 0;
            }

            if (var15 && 1.0D - var10 < var19)
            {
                var19 = 1.0D - var10;
                var18 = 1;
            }

            if (var16 && var12 < var19)
            {
                var19 = var12;
                var18 = 4;
            }

            if (var17 && 1.0D - var12 < var19)
            {
                var19 = 1.0D - var12;
                var18 = 5;
            }

            float var21 = 0.1F;

            if (var18 == 0)
            {
                this.motionX = (double)(-var21);
            }

            if (var18 == 1)
            {
                this.motionX = (double)var21;
            }

            if (var18 == 4)
            {
                this.motionZ = (double)(-var21);
            }

            if (var18 == 5)
            {
                this.motionZ = (double)var21;
            }
        }

        return false;
    }

    /**
     * Sets the player's sprinting state.
     */
    public void setSprinting(boolean par1)
    {
        super.setSprinting(par1);

        if (!par1)
        {
            this.sprintingTicksLeft = 0;
        }
        else
        {
            this.sprintingTicksLeft = 600;
        }
    }

    /**
     * Sets the current XP, total XP, and level number.
     */
    public void setXPStats(float par1, int par2, int par3)
    {
        this.experience = par1;
        this.experienceTotal = par2;
        this.experienceLevel = par3;
    }
}
