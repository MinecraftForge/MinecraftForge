package net.minecraft.src;

import org.lwjgl.opengl.GL11;

public class ModelOcelot extends ModelBase
{
    ModelRenderer field_48225_a;
    ModelRenderer field_48223_b;
    ModelRenderer field_48224_c;
    ModelRenderer field_48221_d;
    ModelRenderer field_48222_e;
    ModelRenderer field_48219_f;
    ModelRenderer field_48220_g;
    ModelRenderer field_48226_n;
    int field_48227_o = 1;

    public ModelOcelot()
    {
        this.setTextureOffset("head.main", 0, 0);
        this.setTextureOffset("head.nose", 0, 24);
        this.setTextureOffset("head.ear1", 0, 10);
        this.setTextureOffset("head.ear2", 6, 10);
        this.field_48220_g = new ModelRenderer(this, "head");
        this.field_48220_g.addBox("main", -2.5F, -2.0F, -3.0F, 5, 4, 5);
        this.field_48220_g.addBox("nose", -1.5F, 0.0F, -4.0F, 3, 2, 2);
        this.field_48220_g.addBox("ear1", -2.0F, -3.0F, 0.0F, 1, 1, 2);
        this.field_48220_g.addBox("ear2", 1.0F, -3.0F, 0.0F, 1, 1, 2);
        this.field_48220_g.setRotationPoint(0.0F, 15.0F, -9.0F);
        this.field_48226_n = new ModelRenderer(this, 20, 0);
        this.field_48226_n.addBox(-2.0F, 3.0F, -8.0F, 4, 16, 6, 0.0F);
        this.field_48226_n.setRotationPoint(0.0F, 12.0F, -10.0F);
        this.field_48222_e = new ModelRenderer(this, 0, 15);
        this.field_48222_e.addBox(-0.5F, 0.0F, 0.0F, 1, 8, 1);
        this.field_48222_e.rotateAngleX = 0.9F;
        this.field_48222_e.setRotationPoint(0.0F, 15.0F, 8.0F);
        this.field_48219_f = new ModelRenderer(this, 4, 15);
        this.field_48219_f.addBox(-0.5F, 0.0F, 0.0F, 1, 8, 1);
        this.field_48219_f.setRotationPoint(0.0F, 20.0F, 14.0F);
        this.field_48225_a = new ModelRenderer(this, 8, 13);
        this.field_48225_a.addBox(-1.0F, 0.0F, 1.0F, 2, 6, 2);
        this.field_48225_a.setRotationPoint(1.1F, 18.0F, 5.0F);
        this.field_48223_b = new ModelRenderer(this, 8, 13);
        this.field_48223_b.addBox(-1.0F, 0.0F, 1.0F, 2, 6, 2);
        this.field_48223_b.setRotationPoint(-1.1F, 18.0F, 5.0F);
        this.field_48224_c = new ModelRenderer(this, 40, 0);
        this.field_48224_c.addBox(-1.0F, 0.0F, 0.0F, 2, 10, 2);
        this.field_48224_c.setRotationPoint(1.2F, 13.8F, -5.0F);
        this.field_48221_d = new ModelRenderer(this, 40, 0);
        this.field_48221_d.addBox(-1.0F, 0.0F, 0.0F, 2, 10, 2);
        this.field_48221_d.setRotationPoint(-1.2F, 13.8F, -5.0F);
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7);

        if (this.isChild)
        {
            float var8 = 2.0F;
            GL11.glPushMatrix();
            GL11.glScalef(1.5F / var8, 1.5F / var8, 1.5F / var8);
            GL11.glTranslatef(0.0F, 10.0F * par7, 4.0F * par7);
            this.field_48220_g.render(par7);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.0F / var8, 1.0F / var8, 1.0F / var8);
            GL11.glTranslatef(0.0F, 24.0F * par7, 0.0F);
            this.field_48226_n.render(par7);
            this.field_48225_a.render(par7);
            this.field_48223_b.render(par7);
            this.field_48224_c.render(par7);
            this.field_48221_d.render(par7);
            this.field_48222_e.render(par7);
            this.field_48219_f.render(par7);
            GL11.glPopMatrix();
        }
        else
        {
            this.field_48220_g.render(par7);
            this.field_48226_n.render(par7);
            this.field_48222_e.render(par7);
            this.field_48219_f.render(par7);
            this.field_48225_a.render(par7);
            this.field_48223_b.render(par7);
            this.field_48224_c.render(par7);
            this.field_48221_d.render(par7);
        }
    }

    /**
     * Sets the models various rotation angles.
     */
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6)
    {
        this.field_48220_g.rotateAngleX = par5 / (180F / (float)Math.PI);
        this.field_48220_g.rotateAngleY = par4 / (180F / (float)Math.PI);

        if (this.field_48227_o != 3)
        {
            this.field_48226_n.rotateAngleX = ((float)Math.PI / 2F);

            if (this.field_48227_o == 2)
            {
                this.field_48225_a.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.0F * par2;
                this.field_48223_b.rotateAngleX = MathHelper.cos(par1 * 0.6662F + 0.3F) * 1.0F * par2;
                this.field_48224_c.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float)Math.PI + 0.3F) * 1.0F * par2;
                this.field_48221_d.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float)Math.PI) * 1.0F * par2;
                this.field_48219_f.rotateAngleX = 1.7278761F + ((float)Math.PI / 10F) * MathHelper.cos(par1) * par2;
            }
            else
            {
                this.field_48225_a.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.0F * par2;
                this.field_48223_b.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float)Math.PI) * 1.0F * par2;
                this.field_48224_c.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float)Math.PI) * 1.0F * par2;
                this.field_48221_d.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.0F * par2;

                if (this.field_48227_o == 1)
                {
                    this.field_48219_f.rotateAngleX = 1.7278761F + ((float)Math.PI / 4F) * MathHelper.cos(par1) * par2;
                }
                else
                {
                    this.field_48219_f.rotateAngleX = 1.7278761F + 0.47123894F * MathHelper.cos(par1) * par2;
                }
            }
        }
    }

    /**
     * Used for easily adding entity-dependent animations. The second and third float params here are the same second
     * and third as in the setRotationAngles method.
     */
    public void setLivingAnimations(EntityLiving par1EntityLiving, float par2, float par3, float par4)
    {
        EntityOcelot var5 = (EntityOcelot)par1EntityLiving;
        this.field_48226_n.rotationPointY = 12.0F;
        this.field_48226_n.rotationPointZ = -10.0F;
        this.field_48220_g.rotationPointY = 15.0F;
        this.field_48220_g.rotationPointZ = -9.0F;
        this.field_48222_e.rotationPointY = 15.0F;
        this.field_48222_e.rotationPointZ = 8.0F;
        this.field_48219_f.rotationPointY = 20.0F;
        this.field_48219_f.rotationPointZ = 14.0F;
        this.field_48224_c.rotationPointY = this.field_48221_d.rotationPointY = 13.8F;
        this.field_48224_c.rotationPointZ = this.field_48221_d.rotationPointZ = -5.0F;
        this.field_48225_a.rotationPointY = this.field_48223_b.rotationPointY = 18.0F;
        this.field_48225_a.rotationPointZ = this.field_48223_b.rotationPointZ = 5.0F;
        this.field_48222_e.rotateAngleX = 0.9F;

        if (var5.isSneaking())
        {
            ++this.field_48226_n.rotationPointY;
            this.field_48220_g.rotationPointY += 2.0F;
            ++this.field_48222_e.rotationPointY;
            this.field_48219_f.rotationPointY += -4.0F;
            this.field_48219_f.rotationPointZ += 2.0F;
            this.field_48222_e.rotateAngleX = ((float)Math.PI / 2F);
            this.field_48219_f.rotateAngleX = ((float)Math.PI / 2F);
            this.field_48227_o = 0;
        }
        else if (var5.isSprinting())
        {
            this.field_48219_f.rotationPointY = this.field_48222_e.rotationPointY;
            this.field_48219_f.rotationPointZ += 2.0F;
            this.field_48222_e.rotateAngleX = ((float)Math.PI / 2F);
            this.field_48219_f.rotateAngleX = ((float)Math.PI / 2F);
            this.field_48227_o = 2;
        }
        else if (var5.func_48141_af())
        {
            this.field_48226_n.rotateAngleX = ((float)Math.PI / 4F);
            this.field_48226_n.rotationPointY += -4.0F;
            this.field_48226_n.rotationPointZ += 5.0F;
            this.field_48220_g.rotationPointY += -3.3F;
            ++this.field_48220_g.rotationPointZ;
            this.field_48222_e.rotationPointY += 8.0F;
            this.field_48222_e.rotationPointZ += -2.0F;
            this.field_48219_f.rotationPointY += 2.0F;
            this.field_48219_f.rotationPointZ += -0.8F;
            this.field_48222_e.rotateAngleX = 1.7278761F;
            this.field_48219_f.rotateAngleX = 2.670354F;
            this.field_48224_c.rotateAngleX = this.field_48221_d.rotateAngleX = -0.15707964F;
            this.field_48224_c.rotationPointY = this.field_48221_d.rotationPointY = 15.8F;
            this.field_48224_c.rotationPointZ = this.field_48221_d.rotationPointZ = -7.0F;
            this.field_48225_a.rotateAngleX = this.field_48223_b.rotateAngleX = -((float)Math.PI / 2F);
            this.field_48225_a.rotationPointY = this.field_48223_b.rotationPointY = 21.0F;
            this.field_48225_a.rotationPointZ = this.field_48223_b.rotationPointZ = 1.0F;
            this.field_48227_o = 3;
        }
        else
        {
            this.field_48227_o = 1;
        }
    }
}
