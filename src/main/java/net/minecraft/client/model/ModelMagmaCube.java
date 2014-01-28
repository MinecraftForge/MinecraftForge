package net.minecraft.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMagmaCube;

@SideOnly(Side.CLIENT)
public class ModelMagmaCube extends ModelBase
{
    ModelRenderer[] field_78109_a = new ModelRenderer[8];
    ModelRenderer field_78108_b;
    private static final String __OBFID = "CL_00000842";

    public ModelMagmaCube()
    {
        for (int i = 0; i < this.field_78109_a.length; ++i)
        {
            byte b0 = 0;
            int j = i;

            if (i == 2)
            {
                b0 = 24;
                j = 10;
            }
            else if (i == 3)
            {
                b0 = 24;
                j = 19;
            }

            this.field_78109_a[i] = new ModelRenderer(this, b0, j);
            this.field_78109_a[i].addBox(-4.0F, (float)(16 + i), -4.0F, 8, 1, 8);
        }

        this.field_78108_b = new ModelRenderer(this, 0, 16);
        this.field_78108_b.addBox(-2.0F, 18.0F, -2.0F, 4, 4, 4);
    }

    // JAVADOC METHOD $$ func_78086_a
    public void setLivingAnimations(EntityLivingBase par1EntityLivingBase, float par2, float par3, float par4)
    {
        EntityMagmaCube entitymagmacube = (EntityMagmaCube)par1EntityLivingBase;
        float f3 = entitymagmacube.prevSquishFactor + (entitymagmacube.squishFactor - entitymagmacube.prevSquishFactor) * par4;

        if (f3 < 0.0F)
        {
            f3 = 0.0F;
        }

        for (int i = 0; i < this.field_78109_a.length; ++i)
        {
            this.field_78109_a[i].rotationPointY = (float)(-(4 - i)) * f3 * 1.7F;
        }
    }

    // JAVADOC METHOD $$ func_78088_a
    public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7, par1Entity);
        this.field_78108_b.render(par7);

        for (int i = 0; i < this.field_78109_a.length; ++i)
        {
            this.field_78109_a[i].render(par7);
        }
    }
}