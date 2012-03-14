package net.minecraft.src;

public class EntityLookHelper
{
    private EntityLiving entity;
    private float field_46066_b;
    private float field_46067_c;
    private boolean field_46064_d = false;
    private double posX;
    private double posY;
    private double posZ;

    public EntityLookHelper(EntityLiving par1EntityLiving)
    {
        this.entity = par1EntityLiving;
    }

    public void setLookPositionWithEntity(Entity par1Entity, float par2, float par3)
    {
        this.posX = par1Entity.posX;

        if (par1Entity instanceof EntityLiving)
        {
            this.posY = par1Entity.posY + (double)((EntityLiving)par1Entity).getEyeHeight();
        }
        else
        {
            this.posY = (par1Entity.boundingBox.minY + par1Entity.boundingBox.maxY) / 2.0D;
        }

        this.posZ = par1Entity.posZ;
        this.field_46066_b = par2;
        this.field_46067_c = par3;
        this.field_46064_d = true;
    }

    public void setLookPosition(double par1, double par3, double par5, float par7, float par8)
    {
        this.posX = par1;
        this.posY = par3;
        this.posZ = par5;
        this.field_46066_b = par7;
        this.field_46067_c = par8;
        this.field_46064_d = true;
    }

    public void onUpdateLook()
    {
        this.entity.rotationPitch = 0.0F;

        if (this.field_46064_d)
        {
            this.field_46064_d = false;
            double var1 = this.posX - this.entity.posX;
            double var3 = this.posY - (this.entity.posY + (double)this.entity.getEyeHeight());
            double var5 = this.posZ - this.entity.posZ;
            double var7 = (double)MathHelper.sqrt_double(var1 * var1 + var5 * var5);
            float var9 = (float)(Math.atan2(var5, var1) * 180.0D / Math.PI) - 90.0F;
            float var10 = (float)(-(Math.atan2(var3, var7) * 180.0D / Math.PI));
            this.entity.rotationPitch = this.updateRotation(this.entity.rotationPitch, var10, this.field_46067_c);
            this.entity.prevRotationYaw2 = this.updateRotation(this.entity.prevRotationYaw2, var9, this.field_46066_b);
        }
        else
        {
            this.entity.prevRotationYaw2 = this.updateRotation(this.entity.prevRotationYaw2, this.entity.renderYawOffset, 10.0F);
        }

        float var11;

        for (var11 = this.entity.prevRotationYaw2 - this.entity.renderYawOffset; var11 < -180.0F; var11 += 360.0F)
        {
            ;
        }

        while (var11 >= 180.0F)
        {
            var11 -= 360.0F;
        }

        if (!this.entity.getNavigator().noPath())
        {
            if (var11 < -75.0F)
            {
                this.entity.prevRotationYaw2 = this.entity.renderYawOffset - 75.0F;
            }

            if (var11 > 75.0F)
            {
                this.entity.prevRotationYaw2 = this.entity.renderYawOffset + 75.0F;
            }
        }
    }

    private float updateRotation(float par1, float par2, float par3)
    {
        float var4;

        for (var4 = par2 - par1; var4 < -180.0F; var4 += 360.0F)
        {
            ;
        }

        while (var4 >= 180.0F)
        {
            var4 -= 360.0F;
        }

        if (var4 > par3)
        {
            var4 = par3;
        }

        if (var4 < -par3)
        {
            var4 = -par3;
        }

        return par1 + var4;
    }
}
