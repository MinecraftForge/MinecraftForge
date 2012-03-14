package net.minecraft.src;

public class EntityBodyHelper
{
    private EntityLiving field_48435_a;
    private int field_48433_b = 0;
    private float field_48434_c = 0.0F;

    public EntityBodyHelper(EntityLiving par1EntityLiving)
    {
        this.field_48435_a = par1EntityLiving;
    }

    public void func_48431_a()
    {
        double var1 = this.field_48435_a.posX - this.field_48435_a.prevPosX;
        double var3 = this.field_48435_a.posZ - this.field_48435_a.prevPosZ;

        if (var1 * var1 + var3 * var3 > 2.500000277905201E-7D)
        {
            this.field_48435_a.renderYawOffset = this.field_48435_a.rotationYaw;
            this.field_48435_a.prevRotationYaw2 = this.func_48432_a(this.field_48435_a.renderYawOffset, this.field_48435_a.prevRotationYaw2, 75.0F);
            this.field_48434_c = this.field_48435_a.prevRotationYaw2;
            this.field_48433_b = 0;
        }
        else
        {
            float var5 = 75.0F;

            if (Math.abs(this.field_48435_a.prevRotationYaw2 - this.field_48434_c) > 15.0F)
            {
                this.field_48433_b = 0;
                this.field_48434_c = this.field_48435_a.prevRotationYaw2;
            }
            else
            {
                ++this.field_48433_b;

                if (this.field_48433_b > 10)
                {
                    var5 = Math.max(1.0F - (float)(this.field_48433_b - 10) / 10.0F, 0.0F) * 75.0F;
                }
            }

            this.field_48435_a.renderYawOffset = this.func_48432_a(this.field_48435_a.prevRotationYaw2, this.field_48435_a.renderYawOffset, var5);
        }
    }

    private float func_48432_a(float par1, float par2, float par3)
    {
        float var4;

        for (var4 = par1 - par2; var4 < -180.0F; var4 += 360.0F)
        {
            ;
        }

        while (var4 >= 180.0F)
        {
            var4 -= 360.0F;
        }

        if (var4 < -par3)
        {
            var4 = -par3;
        }

        if (var4 >= par3)
        {
            var4 = par3;
        }

        return par1 - var4;
    }
}
