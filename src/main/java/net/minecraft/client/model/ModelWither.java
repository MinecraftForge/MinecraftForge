package net.minecraft.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.util.MathHelper;

@SideOnly(Side.CLIENT)
public class ModelWither extends ModelBase
{
    private ModelRenderer[] field_82905_a;
    private ModelRenderer[] field_82904_b;
    private static final String __OBFID = "CL_00000867";

    public ModelWither()
    {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.field_82905_a = new ModelRenderer[3];
        this.field_82905_a[0] = new ModelRenderer(this, 0, 16);
        this.field_82905_a[0].addBox(-10.0F, 3.9F, -0.5F, 20, 3, 3);
        this.field_82905_a[1] = (new ModelRenderer(this)).setTextureSize(this.textureWidth, this.textureHeight);
        this.field_82905_a[1].setRotationPoint(-2.0F, 6.9F, -0.5F);
        this.field_82905_a[1].setTextureOffset(0, 22).addBox(0.0F, 0.0F, 0.0F, 3, 10, 3);
        this.field_82905_a[1].setTextureOffset(24, 22).addBox(-4.0F, 1.5F, 0.5F, 11, 2, 2);
        this.field_82905_a[1].setTextureOffset(24, 22).addBox(-4.0F, 4.0F, 0.5F, 11, 2, 2);
        this.field_82905_a[1].setTextureOffset(24, 22).addBox(-4.0F, 6.5F, 0.5F, 11, 2, 2);
        this.field_82905_a[2] = new ModelRenderer(this, 12, 22);
        this.field_82905_a[2].addBox(0.0F, 0.0F, 0.0F, 3, 6, 3);
        this.field_82904_b = new ModelRenderer[3];
        this.field_82904_b[0] = new ModelRenderer(this, 0, 0);
        this.field_82904_b[0].addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);
        this.field_82904_b[1] = new ModelRenderer(this, 32, 0);
        this.field_82904_b[1].addBox(-4.0F, -4.0F, -4.0F, 6, 6, 6);
        this.field_82904_b[1].rotationPointX = -8.0F;
        this.field_82904_b[1].rotationPointY = 4.0F;
        this.field_82904_b[2] = new ModelRenderer(this, 32, 0);
        this.field_82904_b[2].addBox(-4.0F, -4.0F, -4.0F, 6, 6, 6);
        this.field_82904_b[2].rotationPointX = 10.0F;
        this.field_82904_b[2].rotationPointY = 4.0F;
    }

    public int func_82903_a()
    {
        return 32;
    }

    // JAVADOC METHOD $$ func_78088_a
    public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7, par1Entity);
        ModelRenderer[] amodelrenderer = this.field_82904_b;
        int i = amodelrenderer.length;
        int j;
        ModelRenderer modelrenderer;

        for (j = 0; j < i; ++j)
        {
            modelrenderer = amodelrenderer[j];
            modelrenderer.render(par7);
        }

        amodelrenderer = this.field_82905_a;
        i = amodelrenderer.length;

        for (j = 0; j < i; ++j)
        {
            modelrenderer = amodelrenderer[j];
            modelrenderer.render(par7);
        }
    }

    // JAVADOC METHOD $$ func_78087_a
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity)
    {
        float f6 = MathHelper.cos(par3 * 0.1F);
        this.field_82905_a[1].rotateAngleX = (0.065F + 0.05F * f6) * (float)Math.PI;
        this.field_82905_a[2].setRotationPoint(-2.0F, 6.9F + MathHelper.cos(this.field_82905_a[1].rotateAngleX) * 10.0F, -0.5F + MathHelper.sin(this.field_82905_a[1].rotateAngleX) * 10.0F);
        this.field_82905_a[2].rotateAngleX = (0.265F + 0.1F * f6) * (float)Math.PI;
        this.field_82904_b[0].rotateAngleY = par4 / (180F / (float)Math.PI);
        this.field_82904_b[0].rotateAngleX = par5 / (180F / (float)Math.PI);
    }

    // JAVADOC METHOD $$ func_78086_a
    public void setLivingAnimations(EntityLivingBase par1EntityLivingBase, float par2, float par3, float par4)
    {
        EntityWither entitywither = (EntityWither)par1EntityLivingBase;

        for (int i = 1; i < 3; ++i)
        {
            this.field_82904_b[i].rotateAngleY = (entitywither.func_82207_a(i - 1) - par1EntityLivingBase.renderYawOffset) / (180F / (float)Math.PI);
            this.field_82904_b[i].rotateAngleX = entitywither.func_82210_r(i - 1) / (180F / (float)Math.PI);
        }
    }
}