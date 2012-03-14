package net.minecraft.src;

public class ModelVillager extends ModelBase
{
    public ModelRenderer field_40340_a;
    public ModelRenderer field_40338_b;
    public ModelRenderer field_40339_c;
    public ModelRenderer field_40336_d;
    public ModelRenderer field_40337_e;
    public int field_40334_f;
    public int field_40335_g;
    public boolean field_40341_n;
    public boolean field_40342_o;

    public ModelVillager()
    {
        this(0.0F);
    }

    public ModelVillager(float par1)
    {
        this(par1, 0.0F);
    }

    public ModelVillager(float par1, float par2)
    {
        this.field_40334_f = 0;
        this.field_40335_g = 0;
        this.field_40341_n = false;
        this.field_40342_o = false;
        byte var3 = 64;
        byte var4 = 64;
        this.field_40340_a = (new ModelRenderer(this)).setTextureSize(var3, var4);
        this.field_40340_a.setRotationPoint(0.0F, 0.0F + par2, 0.0F);
        this.field_40340_a.setTextureOffset(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8, 10, 8, par1);
        this.field_40340_a.setTextureOffset(24, 0).addBox(-1.0F, -3.0F, -6.0F, 2, 4, 2, par1);
        this.field_40338_b = (new ModelRenderer(this)).setTextureSize(var3, var4);
        this.field_40338_b.setRotationPoint(0.0F, 0.0F + par2, 0.0F);
        this.field_40338_b.setTextureOffset(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8, 12, 6, par1);
        this.field_40338_b.setTextureOffset(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8, 18, 6, par1 + 0.5F);
        this.field_40339_c = (new ModelRenderer(this)).setTextureSize(var3, var4);
        this.field_40339_c.setRotationPoint(0.0F, 0.0F + par2 + 2.0F, 0.0F);
        this.field_40339_c.setTextureOffset(44, 22).addBox(-8.0F, -2.0F, -2.0F, 4, 8, 4, par1);
        this.field_40339_c.setTextureOffset(44, 22).addBox(4.0F, -2.0F, -2.0F, 4, 8, 4, par1);
        this.field_40339_c.setTextureOffset(40, 38).addBox(-4.0F, 2.0F, -2.0F, 8, 4, 4, par1);
        this.field_40336_d = (new ModelRenderer(this, 0, 22)).setTextureSize(var3, var4);
        this.field_40336_d.setRotationPoint(-2.0F, 12.0F + par2, 0.0F);
        this.field_40336_d.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, par1);
        this.field_40337_e = (new ModelRenderer(this, 0, 22)).setTextureSize(var3, var4);
        this.field_40337_e.mirror = true;
        this.field_40337_e.setRotationPoint(2.0F, 12.0F + par2, 0.0F);
        this.field_40337_e.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, par1);
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7);
        this.field_40340_a.render(par7);
        this.field_40338_b.render(par7);
        this.field_40336_d.render(par7);
        this.field_40337_e.render(par7);
        this.field_40339_c.render(par7);
    }

    /**
     * Sets the models various rotation angles.
     */
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6)
    {
        this.field_40340_a.rotateAngleY = par4 / (180F / (float)Math.PI);
        this.field_40340_a.rotateAngleX = par5 / (180F / (float)Math.PI);
        this.field_40339_c.rotationPointY = 3.0F;
        this.field_40339_c.rotationPointZ = -1.0F;
        this.field_40339_c.rotateAngleX = -0.75F;
        this.field_40336_d.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.4F * par2 * 0.5F;
        this.field_40337_e.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float)Math.PI) * 1.4F * par2 * 0.5F;
        this.field_40336_d.rotateAngleY = 0.0F;
        this.field_40337_e.rotateAngleY = 0.0F;
    }
}
