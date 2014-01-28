package net.minecraft.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

@SideOnly(Side.CLIENT)
public class ModelBook extends ModelBase
{
    // JAVADOC FIELD $$ field_78102_a
    public ModelRenderer coverRight = (new ModelRenderer(this)).setTextureOffset(0, 0).addBox(-6.0F, -5.0F, 0.0F, 6, 10, 0);
    // JAVADOC FIELD $$ field_78100_b
    public ModelRenderer coverLeft = (new ModelRenderer(this)).setTextureOffset(16, 0).addBox(0.0F, -5.0F, 0.0F, 6, 10, 0);
    // JAVADOC FIELD $$ field_78101_c
    public ModelRenderer pagesRight = (new ModelRenderer(this)).setTextureOffset(0, 10).addBox(0.0F, -4.0F, -0.99F, 5, 8, 1);
    // JAVADOC FIELD $$ field_78098_d
    public ModelRenderer pagesLeft = (new ModelRenderer(this)).setTextureOffset(12, 10).addBox(0.0F, -4.0F, -0.01F, 5, 8, 1);
    // JAVADOC FIELD $$ field_78099_e
    public ModelRenderer flippingPageRight = (new ModelRenderer(this)).setTextureOffset(24, 10).addBox(0.0F, -4.0F, 0.0F, 5, 8, 0);
    // JAVADOC FIELD $$ field_78096_f
    public ModelRenderer flippingPageLeft = (new ModelRenderer(this)).setTextureOffset(24, 10).addBox(0.0F, -4.0F, 0.0F, 5, 8, 0);
    // JAVADOC FIELD $$ field_78097_g
    public ModelRenderer bookSpine = (new ModelRenderer(this)).setTextureOffset(12, 0).addBox(-1.0F, -5.0F, 0.0F, 2, 10, 0);
    private static final String __OBFID = "CL_00000833";

    public ModelBook()
    {
        this.coverRight.setRotationPoint(0.0F, 0.0F, -1.0F);
        this.coverLeft.setRotationPoint(0.0F, 0.0F, 1.0F);
        this.bookSpine.rotateAngleY = ((float)Math.PI / 2F);
    }

    // JAVADOC METHOD $$ func_78088_a
    public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7, par1Entity);
        this.coverRight.render(par7);
        this.coverLeft.render(par7);
        this.bookSpine.render(par7);
        this.pagesRight.render(par7);
        this.pagesLeft.render(par7);
        this.flippingPageRight.render(par7);
        this.flippingPageLeft.render(par7);
    }

    // JAVADOC METHOD $$ func_78087_a
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity)
    {
        float f6 = (MathHelper.sin(par1 * 0.02F) * 0.1F + 1.25F) * par4;
        this.coverRight.rotateAngleY = (float)Math.PI + f6;
        this.coverLeft.rotateAngleY = -f6;
        this.pagesRight.rotateAngleY = f6;
        this.pagesLeft.rotateAngleY = -f6;
        this.flippingPageRight.rotateAngleY = f6 - f6 * 2.0F * par2;
        this.flippingPageLeft.rotateAngleY = f6 - f6 * 2.0F * par3;
        this.pagesRight.rotationPointX = MathHelper.sin(f6);
        this.pagesLeft.rotationPointX = MathHelper.sin(f6);
        this.flippingPageRight.rotationPointX = MathHelper.sin(f6);
        this.flippingPageLeft.rotationPointX = MathHelper.sin(f6);
    }
}