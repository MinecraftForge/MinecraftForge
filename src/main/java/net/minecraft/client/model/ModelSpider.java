package net.minecraft.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

@SideOnly(Side.CLIENT)
public class ModelSpider extends ModelBase
{
    // JAVADOC FIELD $$ field_78209_a
    public ModelRenderer spiderHead;
    // JAVADOC FIELD $$ field_78207_b
    public ModelRenderer spiderNeck;
    // JAVADOC FIELD $$ field_78208_c
    public ModelRenderer spiderBody;
    // JAVADOC FIELD $$ field_78205_d
    public ModelRenderer spiderLeg1;
    // JAVADOC FIELD $$ field_78206_e
    public ModelRenderer spiderLeg2;
    // JAVADOC FIELD $$ field_78203_f
    public ModelRenderer spiderLeg3;
    // JAVADOC FIELD $$ field_78204_g
    public ModelRenderer spiderLeg4;
    // JAVADOC FIELD $$ field_78212_h
    public ModelRenderer spiderLeg5;
    // JAVADOC FIELD $$ field_78213_i
    public ModelRenderer spiderLeg6;
    // JAVADOC FIELD $$ field_78210_j
    public ModelRenderer spiderLeg7;
    // JAVADOC FIELD $$ field_78211_k
    public ModelRenderer spiderLeg8;
    private static final String __OBFID = "CL_00000860";

    public ModelSpider()
    {
        float f = 0.0F;
        byte b0 = 15;
        this.spiderHead = new ModelRenderer(this, 32, 4);
        this.spiderHead.addBox(-4.0F, -4.0F, -8.0F, 8, 8, 8, f);
        this.spiderHead.setRotationPoint(0.0F, (float)b0, -3.0F);
        this.spiderNeck = new ModelRenderer(this, 0, 0);
        this.spiderNeck.addBox(-3.0F, -3.0F, -3.0F, 6, 6, 6, f);
        this.spiderNeck.setRotationPoint(0.0F, (float)b0, 0.0F);
        this.spiderBody = new ModelRenderer(this, 0, 12);
        this.spiderBody.addBox(-5.0F, -4.0F, -6.0F, 10, 8, 12, f);
        this.spiderBody.setRotationPoint(0.0F, (float)b0, 9.0F);
        this.spiderLeg1 = new ModelRenderer(this, 18, 0);
        this.spiderLeg1.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, f);
        this.spiderLeg1.setRotationPoint(-4.0F, (float)b0, 2.0F);
        this.spiderLeg2 = new ModelRenderer(this, 18, 0);
        this.spiderLeg2.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, f);
        this.spiderLeg2.setRotationPoint(4.0F, (float)b0, 2.0F);
        this.spiderLeg3 = new ModelRenderer(this, 18, 0);
        this.spiderLeg3.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, f);
        this.spiderLeg3.setRotationPoint(-4.0F, (float)b0, 1.0F);
        this.spiderLeg4 = new ModelRenderer(this, 18, 0);
        this.spiderLeg4.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, f);
        this.spiderLeg4.setRotationPoint(4.0F, (float)b0, 1.0F);
        this.spiderLeg5 = new ModelRenderer(this, 18, 0);
        this.spiderLeg5.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, f);
        this.spiderLeg5.setRotationPoint(-4.0F, (float)b0, 0.0F);
        this.spiderLeg6 = new ModelRenderer(this, 18, 0);
        this.spiderLeg6.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, f);
        this.spiderLeg6.setRotationPoint(4.0F, (float)b0, 0.0F);
        this.spiderLeg7 = new ModelRenderer(this, 18, 0);
        this.spiderLeg7.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, f);
        this.spiderLeg7.setRotationPoint(-4.0F, (float)b0, -1.0F);
        this.spiderLeg8 = new ModelRenderer(this, 18, 0);
        this.spiderLeg8.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, f);
        this.spiderLeg8.setRotationPoint(4.0F, (float)b0, -1.0F);
    }

    // JAVADOC METHOD $$ func_78088_a
    public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7, par1Entity);
        this.spiderHead.render(par7);
        this.spiderNeck.render(par7);
        this.spiderBody.render(par7);
        this.spiderLeg1.render(par7);
        this.spiderLeg2.render(par7);
        this.spiderLeg3.render(par7);
        this.spiderLeg4.render(par7);
        this.spiderLeg5.render(par7);
        this.spiderLeg6.render(par7);
        this.spiderLeg7.render(par7);
        this.spiderLeg8.render(par7);
    }

    // JAVADOC METHOD $$ func_78087_a
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity)
    {
        this.spiderHead.rotateAngleY = par4 / (180F / (float)Math.PI);
        this.spiderHead.rotateAngleX = par5 / (180F / (float)Math.PI);
        float f6 = ((float)Math.PI / 4F);
        this.spiderLeg1.rotateAngleZ = -f6;
        this.spiderLeg2.rotateAngleZ = f6;
        this.spiderLeg3.rotateAngleZ = -f6 * 0.74F;
        this.spiderLeg4.rotateAngleZ = f6 * 0.74F;
        this.spiderLeg5.rotateAngleZ = -f6 * 0.74F;
        this.spiderLeg6.rotateAngleZ = f6 * 0.74F;
        this.spiderLeg7.rotateAngleZ = -f6;
        this.spiderLeg8.rotateAngleZ = f6;
        float f7 = -0.0F;
        float f8 = 0.3926991F;
        this.spiderLeg1.rotateAngleY = f8 * 2.0F + f7;
        this.spiderLeg2.rotateAngleY = -f8 * 2.0F - f7;
        this.spiderLeg3.rotateAngleY = f8 * 1.0F + f7;
        this.spiderLeg4.rotateAngleY = -f8 * 1.0F - f7;
        this.spiderLeg5.rotateAngleY = -f8 * 1.0F + f7;
        this.spiderLeg6.rotateAngleY = f8 * 1.0F - f7;
        this.spiderLeg7.rotateAngleY = -f8 * 2.0F + f7;
        this.spiderLeg8.rotateAngleY = f8 * 2.0F - f7;
        float f9 = -(MathHelper.cos(par1 * 0.6662F * 2.0F + 0.0F) * 0.4F) * par2;
        float f10 = -(MathHelper.cos(par1 * 0.6662F * 2.0F + (float)Math.PI) * 0.4F) * par2;
        float f11 = -(MathHelper.cos(par1 * 0.6662F * 2.0F + ((float)Math.PI / 2F)) * 0.4F) * par2;
        float f12 = -(MathHelper.cos(par1 * 0.6662F * 2.0F + ((float)Math.PI * 3F / 2F)) * 0.4F) * par2;
        float f13 = Math.abs(MathHelper.sin(par1 * 0.6662F + 0.0F) * 0.4F) * par2;
        float f14 = Math.abs(MathHelper.sin(par1 * 0.6662F + (float)Math.PI) * 0.4F) * par2;
        float f15 = Math.abs(MathHelper.sin(par1 * 0.6662F + ((float)Math.PI / 2F)) * 0.4F) * par2;
        float f16 = Math.abs(MathHelper.sin(par1 * 0.6662F + ((float)Math.PI * 3F / 2F)) * 0.4F) * par2;
        this.spiderLeg1.rotateAngleY += f9;
        this.spiderLeg2.rotateAngleY += -f9;
        this.spiderLeg3.rotateAngleY += f10;
        this.spiderLeg4.rotateAngleY += -f10;
        this.spiderLeg5.rotateAngleY += f11;
        this.spiderLeg6.rotateAngleY += -f11;
        this.spiderLeg7.rotateAngleY += f12;
        this.spiderLeg8.rotateAngleY += -f12;
        this.spiderLeg1.rotateAngleZ += f13;
        this.spiderLeg2.rotateAngleZ += -f13;
        this.spiderLeg3.rotateAngleZ += f14;
        this.spiderLeg4.rotateAngleZ += -f14;
        this.spiderLeg5.rotateAngleZ += f15;
        this.spiderLeg6.rotateAngleZ += -f15;
        this.spiderLeg7.rotateAngleZ += f16;
        this.spiderLeg8.rotateAngleZ += -f16;
    }
}