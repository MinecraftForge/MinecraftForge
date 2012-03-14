package net.minecraft.src;

public class ModelSnowMan extends ModelBase
{
    public ModelRenderer field_40306_a;
    public ModelRenderer field_40304_b;
    public ModelRenderer field_40305_c;
    public ModelRenderer field_40302_d;
    public ModelRenderer field_40303_e;

    public ModelSnowMan()
    {
        float var1 = 4.0F;
        float var2 = 0.0F;
        this.field_40305_c = (new ModelRenderer(this, 0, 0)).setTextureSize(64, 64);
        this.field_40305_c.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, var2 - 0.5F);
        this.field_40305_c.setRotationPoint(0.0F, 0.0F + var1, 0.0F);
        this.field_40302_d = (new ModelRenderer(this, 32, 0)).setTextureSize(64, 64);
        this.field_40302_d.addBox(-1.0F, 0.0F, -1.0F, 12, 2, 2, var2 - 0.5F);
        this.field_40302_d.setRotationPoint(0.0F, 0.0F + var1 + 9.0F - 7.0F, 0.0F);
        this.field_40303_e = (new ModelRenderer(this, 32, 0)).setTextureSize(64, 64);
        this.field_40303_e.addBox(-1.0F, 0.0F, -1.0F, 12, 2, 2, var2 - 0.5F);
        this.field_40303_e.setRotationPoint(0.0F, 0.0F + var1 + 9.0F - 7.0F, 0.0F);
        this.field_40306_a = (new ModelRenderer(this, 0, 16)).setTextureSize(64, 64);
        this.field_40306_a.addBox(-5.0F, -10.0F, -5.0F, 10, 10, 10, var2 - 0.5F);
        this.field_40306_a.setRotationPoint(0.0F, 0.0F + var1 + 9.0F, 0.0F);
        this.field_40304_b = (new ModelRenderer(this, 0, 36)).setTextureSize(64, 64);
        this.field_40304_b.addBox(-6.0F, -12.0F, -6.0F, 12, 12, 12, var2 - 0.5F);
        this.field_40304_b.setRotationPoint(0.0F, 0.0F + var1 + 20.0F, 0.0F);
    }

    /**
     * Sets the models various rotation angles.
     */
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6)
    {
        super.setRotationAngles(par1, par2, par3, par4, par5, par6);
        this.field_40305_c.rotateAngleY = par4 / (180F / (float)Math.PI);
        this.field_40305_c.rotateAngleX = par5 / (180F / (float)Math.PI);
        this.field_40306_a.rotateAngleY = par4 / (180F / (float)Math.PI) * 0.25F;
        float var7 = MathHelper.sin(this.field_40306_a.rotateAngleY);
        float var8 = MathHelper.cos(this.field_40306_a.rotateAngleY);
        this.field_40302_d.rotateAngleZ = 1.0F;
        this.field_40303_e.rotateAngleZ = -1.0F;
        this.field_40302_d.rotateAngleY = 0.0F + this.field_40306_a.rotateAngleY;
        this.field_40303_e.rotateAngleY = (float)Math.PI + this.field_40306_a.rotateAngleY;
        this.field_40302_d.rotationPointX = var8 * 5.0F;
        this.field_40302_d.rotationPointZ = -var7 * 5.0F;
        this.field_40303_e.rotationPointX = -var8 * 5.0F;
        this.field_40303_e.rotationPointZ = var7 * 5.0F;
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7);
        this.field_40306_a.render(par7);
        this.field_40304_b.render(par7);
        this.field_40305_c.render(par7);
        this.field_40302_d.render(par7);
        this.field_40303_e.render(par7);
    }
}
