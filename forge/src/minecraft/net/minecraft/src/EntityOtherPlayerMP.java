package net.minecraft.src;

public class EntityOtherPlayerMP extends EntityPlayer
{
    private boolean isItemInUse = false;
    private int otherPlayerMPPosRotationIncrements;
    private double otherPlayerMPX;
    private double otherPlayerMPY;
    private double otherPlayerMPZ;
    private double otherPlayerMPYaw;
    private double otherPlayerMPPitch;
    float field_20924_a = 0.0F;

    public EntityOtherPlayerMP(World par1World, String par2Str)
    {
        super(par1World);
        this.username = par2Str;
        this.yOffset = 0.0F;
        this.stepHeight = 0.0F;

        if (par2Str != null && par2Str.length() > 0)
        {
            this.skinUrl = "http://s3.amazonaws.com/MinecraftSkins/" + par2Str + ".png";
        }

        this.noClip = true;
        this.field_22062_y = 0.25F;
        this.renderDistanceWeight = 10.0D;
    }

    /**
     * sets the players height back to normal after doing things like sleeping and dieing
     */
    protected void resetHeight()
    {
        this.yOffset = 0.0F;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource par1DamageSource, int par2)
    {
        return true;
    }

    /**
     * Sets the position and rotation. Only difference from the other one is no bounding on the rotation. Args: posX,
     * posY, posZ, yaw, pitch
     */
    public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9)
    {
        this.otherPlayerMPX = par1;
        this.otherPlayerMPY = par3;
        this.otherPlayerMPZ = par5;
        this.otherPlayerMPYaw = (double)par7;
        this.otherPlayerMPPitch = (double)par8;
        this.otherPlayerMPPosRotationIncrements = par9;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        this.field_22062_y = 0.0F;
        super.onUpdate();
        this.field_705_Q = this.field_704_R;
        double var1 = this.posX - this.prevPosX;
        double var3 = this.posZ - this.prevPosZ;
        float var5 = MathHelper.sqrt_double(var1 * var1 + var3 * var3) * 4.0F;

        if (var5 > 1.0F)
        {
            var5 = 1.0F;
        }

        this.field_704_R += (var5 - this.field_704_R) * 0.4F;
        this.field_703_S += this.field_704_R;

        if (!this.isItemInUse && this.isEating() && this.inventory.mainInventory[this.inventory.currentItem] != null)
        {
            ItemStack var6 = this.inventory.mainInventory[this.inventory.currentItem];
            this.setItemInUse(this.inventory.mainInventory[this.inventory.currentItem], Item.itemsList[var6.itemID].getMaxItemUseDuration(var6));
            this.isItemInUse = true;
        }
        else if (this.isItemInUse && !this.isEating())
        {
            this.clearItemInUse();
            this.isItemInUse = false;
        }
    }

    public float getShadowSize()
    {
        return 0.0F;
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        super.updateEntityActionState();

        if (this.otherPlayerMPPosRotationIncrements > 0)
        {
            double var1 = this.posX + (this.otherPlayerMPX - this.posX) / (double)this.otherPlayerMPPosRotationIncrements;
            double var3 = this.posY + (this.otherPlayerMPY - this.posY) / (double)this.otherPlayerMPPosRotationIncrements;
            double var5 = this.posZ + (this.otherPlayerMPZ - this.posZ) / (double)this.otherPlayerMPPosRotationIncrements;
            double var7;

            for (var7 = this.otherPlayerMPYaw - (double)this.rotationYaw; var7 < -180.0D; var7 += 360.0D)
            {
                ;
            }

            while (var7 >= 180.0D)
            {
                var7 -= 360.0D;
            }

            this.rotationYaw = (float)((double)this.rotationYaw + var7 / (double)this.otherPlayerMPPosRotationIncrements);
            this.rotationPitch = (float)((double)this.rotationPitch + (this.otherPlayerMPPitch - (double)this.rotationPitch) / (double)this.otherPlayerMPPosRotationIncrements);
            --this.otherPlayerMPPosRotationIncrements;
            this.setPosition(var1, var3, var5);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        }

        this.prevCameraYaw = this.cameraYaw;
        float var9 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
        float var2 = (float)Math.atan(-this.motionY * 0.20000000298023224D) * 15.0F;

        if (var9 > 0.1F)
        {
            var9 = 0.1F;
        }

        if (!this.onGround || this.getEntityHealth() <= 0)
        {
            var9 = 0.0F;
        }

        if (this.onGround || this.getEntityHealth() <= 0)
        {
            var2 = 0.0F;
        }

        this.cameraYaw += (var9 - this.cameraYaw) * 0.4F;
        this.cameraPitch += (var2 - this.cameraPitch) * 0.8F;
    }

    /**
     * args: if j >= 0 a new item will be generated with j as itemid and; k as damage - ignored if j < 0; i 0 for held
     * item, 1-4 for armor slots 0-3, o;
     */
    public void outfitWithItem(int par1, int par2, int par3)
    {
        ItemStack var4 = null;

        if (par2 >= 0)
        {
            var4 = new ItemStack(par2, 1, par3);
        }

        if (par1 == 0)
        {
            this.inventory.mainInventory[this.inventory.currentItem] = var4;
        }
        else
        {
            this.inventory.armorInventory[par1 - 1] = var4;
        }
    }

    public void func_6420_o() {}

    public float getEyeHeight()
    {
        return 1.82F;
    }
}
