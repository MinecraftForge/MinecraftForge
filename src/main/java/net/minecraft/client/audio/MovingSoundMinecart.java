package net.minecraft.client.audio;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class MovingSoundMinecart extends MovingSound
{
    private final EntityMinecart field_147670_k;
    private float field_147669_l = 0.0F;
    private static final String __OBFID = "CL_00001118";

    public MovingSoundMinecart(EntityMinecart p_i45105_1_)
    {
        super(new ResourceLocation("minecraft:minecart.base"));
        this.field_147670_k = p_i45105_1_;
        this.field_147659_g = true;
        this.field_147665_h = 0;
    }

    // JAVADOC METHOD $$ func_73660_a
    public void update()
    {
        if (this.field_147670_k.isDead)
        {
            this.field_147668_j = true;
        }
        else
        {
            this.field_147660_d = (float)this.field_147670_k.posX;
            this.field_147661_e = (float)this.field_147670_k.posY;
            this.field_147658_f = (float)this.field_147670_k.posZ;
            float f = MathHelper.sqrt_double(this.field_147670_k.motionX * this.field_147670_k.motionX + this.field_147670_k.motionZ * this.field_147670_k.motionZ);

            if ((double)f >= 0.01D)
            {
                this.field_147669_l = MathHelper.clamp_float(this.field_147669_l + 0.0025F, 0.0F, 1.0F);
                this.field_147662_b = 0.0F + MathHelper.clamp_float(f, 0.0F, 0.5F) * 0.7F;
            }
            else
            {
                this.field_147669_l = 0.0F;
                this.field_147662_b = 0.0F;
            }
        }
    }
}