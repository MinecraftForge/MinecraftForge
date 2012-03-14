package net.minecraft.src;

public class ModelSpider extends ModelBase
{
    /** The spider's head box */
    public ModelRenderer spiderHead;

    /** The spider's neck box */
    public ModelRenderer spiderNeck;

    /** The spider's body box */
    public ModelRenderer spiderBody;

    /** Spider's first leg */
    public ModelRenderer spiderLeg1;

    /** Spider's second leg */
    public ModelRenderer spiderLeg2;

    /** Spider's third leg */
    public ModelRenderer spiderLeg3;

    /** Spider's fourth leg */
    public ModelRenderer spiderLeg4;

    /** Spider's fifth leg */
    public ModelRenderer spiderLeg5;

    /** Spider's sixth leg */
    public ModelRenderer spiderLeg6;

    /** Spider's seventh leg */
    public ModelRenderer spiderLeg7;

    /** Spider's eight leg */
    public ModelRenderer spiderLeg8;

    public ModelSpider()
    {
        float var1 = 0.0F;
        byte var2 = 15;
        this.spiderHead = new ModelRenderer(this, 32, 4);
        this.spiderHead.addBox(-4.0F, -4.0F, -8.0F, 8, 8, 8, var1);
        this.spiderHead.setRotationPoint(0.0F, (float)(0 + var2), -3.0F);
        this.spiderNeck = new ModelRenderer(this, 0, 0);
        this.spiderNeck.addBox(-3.0F, -3.0F, -3.0F, 6, 6, 6, var1);
        this.spiderNeck.setRotationPoint(0.0F, (float)var2, 0.0F);
        this.spiderBody = new ModelRenderer(this, 0, 12);
        this.spiderBody.addBox(-5.0F, -4.0F, -6.0F, 10, 8, 12, var1);
        this.spiderBody.setRotationPoint(0.0F, (float)(0 + var2), 9.0F);
        this.spiderLeg1 = new ModelRenderer(this, 18, 0);
        this.spiderLeg1.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, var1);
        this.spiderLeg1.setRotationPoint(-4.0F, (float)(0 + var2), 2.0F);
        this.spiderLeg2 = new ModelRenderer(this, 18, 0);
        this.spiderLeg2.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, var1);
        this.spiderLeg2.setRotationPoint(4.0F, (float)(0 + var2), 2.0F);
        this.spiderLeg3 = new ModelRenderer(this, 18, 0);
        this.spiderLeg3.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, var1);
        this.spiderLeg3.setRotationPoint(-4.0F, (float)(0 + var2), 1.0F);
        this.spiderLeg4 = new ModelRenderer(this, 18, 0);
        this.spiderLeg4.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, var1);
        this.spiderLeg4.setRotationPoint(4.0F, (float)(0 + var2), 1.0F);
        this.spiderLeg5 = new ModelRenderer(this, 18, 0);
        this.spiderLeg5.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, var1);
        this.spiderLeg5.setRotationPoint(-4.0F, (float)(0 + var2), 0.0F);
        this.spiderLeg6 = new ModelRenderer(this, 18, 0);
        this.spiderLeg6.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, var1);
        this.spiderLeg6.setRotationPoint(4.0F, (float)(0 + var2), 0.0F);
        this.spiderLeg7 = new ModelRenderer(this, 18, 0);
        this.spiderLeg7.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, var1);
        this.spiderLeg7.setRotationPoint(-4.0F, (float)(0 + var2), -1.0F);
        this.spiderLeg8 = new ModelRenderer(this, 18, 0);
        this.spiderLeg8.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, var1);
        this.spiderLeg8.setRotationPoint(4.0F, (float)(0 + var2), -1.0F);
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7);
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

    /**
     * Sets the models various rotation angles.
     */
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6)
    {
        this.spiderHead.rotateAngleY = par4 / (180F / (float)Math.PI);
        this.spiderHead.rotateAngleX = par5 / (180F / (float)Math.PI);
        float var7 = ((float)Math.PI / 4F);
        this.spiderLeg1.rotateAngleZ = -var7;
        this.spiderLeg2.rotateAngleZ = var7;
        this.spiderLeg3.rotateAngleZ = -var7 * 0.74F;
        this.spiderLeg4.rotateAngleZ = var7 * 0.74F;
        this.spiderLeg5.rotateAngleZ = -var7 * 0.74F;
        this.spiderLeg6.rotateAngleZ = var7 * 0.74F;
        this.spiderLeg7.rotateAngleZ = -var7;
        this.spiderLeg8.rotateAngleZ = var7;
        float var8 = -0.0F;
        float var9 = 0.3926991F;
        this.spiderLeg1.rotateAngleY = var9 * 2.0F + var8;
        this.spiderLeg2.rotateAngleY = -var9 * 2.0F - var8;
        this.spiderLeg3.rotateAngleY = var9 * 1.0F + var8;
        this.spiderLeg4.rotateAngleY = -var9 * 1.0F - var8;
        this.spiderLeg5.rotateAngleY = -var9 * 1.0F + var8;
        this.spiderLeg6.rotateAngleY = var9 * 1.0F - var8;
        this.spiderLeg7.rotateAngleY = -var9 * 2.0F + var8;
        this.spiderLeg8.rotateAngleY = var9 * 2.0F - var8;
        float var10 = -(MathHelper.cos(par1 * 0.6662F * 2.0F + 0.0F) * 0.4F) * par2;
        float var11 = -(MathHelper.cos(par1 * 0.6662F * 2.0F + (float)Math.PI) * 0.4F) * par2;
        float var12 = -(MathHelper.cos(par1 * 0.6662F * 2.0F + ((float)Math.PI / 2F)) * 0.4F) * par2;
        float var13 = -(MathHelper.cos(par1 * 0.6662F * 2.0F + ((float)Math.PI * 3F / 2F)) * 0.4F) * par2;
        float var14 = Math.abs(MathHelper.sin(par1 * 0.6662F + 0.0F) * 0.4F) * par2;
        float var15 = Math.abs(MathHelper.sin(par1 * 0.6662F + (float)Math.PI) * 0.4F) * par2;
        float var16 = Math.abs(MathHelper.sin(par1 * 0.6662F + ((float)Math.PI / 2F)) * 0.4F) * par2;
        float var17 = Math.abs(MathHelper.sin(par1 * 0.6662F + ((float)Math.PI * 3F / 2F)) * 0.4F) * par2;
        this.spiderLeg1.rotateAngleY += var10;
        this.spiderLeg2.rotateAngleY += -var10;
        this.spiderLeg3.rotateAngleY += var11;
        this.spiderLeg4.rotateAngleY += -var11;
        this.spiderLeg5.rotateAngleY += var12;
        this.spiderLeg6.rotateAngleY += -var12;
        this.spiderLeg7.rotateAngleY += var13;
        this.spiderLeg8.rotateAngleY += -var13;
        this.spiderLeg1.rotateAngleZ += var14;
        this.spiderLeg2.rotateAngleZ += -var14;
        this.spiderLeg3.rotateAngleZ += var15;
        this.spiderLeg4.rotateAngleZ += -var15;
        this.spiderLeg5.rotateAngleZ += var16;
        this.spiderLeg6.rotateAngleZ += -var16;
        this.spiderLeg7.rotateAngleZ += var17;
        this.spiderLeg8.rotateAngleZ += -var17;
    }
}
