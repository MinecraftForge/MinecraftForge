package net.minecraft.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class ModelOcelot extends ModelBase
{
    // JAVADOC FIELD $$ field_78161_a
    ModelRenderer ocelotBackLeftLeg;
    // JAVADOC FIELD $$ field_78159_b
    ModelRenderer ocelotBackRightLeg;
    // JAVADOC FIELD $$ field_78160_c
    ModelRenderer ocelotFrontLeftLeg;
    // JAVADOC FIELD $$ field_78157_d
    ModelRenderer ocelotFrontRightLeg;
    // JAVADOC FIELD $$ field_78158_e
    ModelRenderer ocelotTail;
    // JAVADOC FIELD $$ field_78155_f
    ModelRenderer ocelotTail2;
    // JAVADOC FIELD $$ field_78156_g
    ModelRenderer ocelotHead;
    // JAVADOC FIELD $$ field_78162_h
    ModelRenderer ocelotBody;
    int field_78163_i = 1;
    private static final String __OBFID = "CL_00000848";

    public ModelOcelot()
    {
        this.setTextureOffset("head.main", 0, 0);
        this.setTextureOffset("head.nose", 0, 24);
        this.setTextureOffset("head.ear1", 0, 10);
        this.setTextureOffset("head.ear2", 6, 10);
        this.ocelotHead = new ModelRenderer(this, "head");
        this.ocelotHead.addBox("main", -2.5F, -2.0F, -3.0F, 5, 4, 5);
        this.ocelotHead.addBox("nose", -1.5F, 0.0F, -4.0F, 3, 2, 2);
        this.ocelotHead.addBox("ear1", -2.0F, -3.0F, 0.0F, 1, 1, 2);
        this.ocelotHead.addBox("ear2", 1.0F, -3.0F, 0.0F, 1, 1, 2);
        this.ocelotHead.setRotationPoint(0.0F, 15.0F, -9.0F);
        this.ocelotBody = new ModelRenderer(this, 20, 0);
        this.ocelotBody.addBox(-2.0F, 3.0F, -8.0F, 4, 16, 6, 0.0F);
        this.ocelotBody.setRotationPoint(0.0F, 12.0F, -10.0F);
        this.ocelotTail = new ModelRenderer(this, 0, 15);
        this.ocelotTail.addBox(-0.5F, 0.0F, 0.0F, 1, 8, 1);
        this.ocelotTail.rotateAngleX = 0.9F;
        this.ocelotTail.setRotationPoint(0.0F, 15.0F, 8.0F);
        this.ocelotTail2 = new ModelRenderer(this, 4, 15);
        this.ocelotTail2.addBox(-0.5F, 0.0F, 0.0F, 1, 8, 1);
        this.ocelotTail2.setRotationPoint(0.0F, 20.0F, 14.0F);
        this.ocelotBackLeftLeg = new ModelRenderer(this, 8, 13);
        this.ocelotBackLeftLeg.addBox(-1.0F, 0.0F, 1.0F, 2, 6, 2);
        this.ocelotBackLeftLeg.setRotationPoint(1.1F, 18.0F, 5.0F);
        this.ocelotBackRightLeg = new ModelRenderer(this, 8, 13);
        this.ocelotBackRightLeg.addBox(-1.0F, 0.0F, 1.0F, 2, 6, 2);
        this.ocelotBackRightLeg.setRotationPoint(-1.1F, 18.0F, 5.0F);
        this.ocelotFrontLeftLeg = new ModelRenderer(this, 40, 0);
        this.ocelotFrontLeftLeg.addBox(-1.0F, 0.0F, 0.0F, 2, 10, 2);
        this.ocelotFrontLeftLeg.setRotationPoint(1.2F, 13.8F, -5.0F);
        this.ocelotFrontRightLeg = new ModelRenderer(this, 40, 0);
        this.ocelotFrontRightLeg.addBox(-1.0F, 0.0F, 0.0F, 2, 10, 2);
        this.ocelotFrontRightLeg.setRotationPoint(-1.2F, 13.8F, -5.0F);
    }

    // JAVADOC METHOD $$ func_78088_a
    public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7, par1Entity);

        if (this.isChild)
        {
            float f6 = 2.0F;
            GL11.glPushMatrix();
            GL11.glScalef(1.5F / f6, 1.5F / f6, 1.5F / f6);
            GL11.glTranslatef(0.0F, 10.0F * par7, 4.0F * par7);
            this.ocelotHead.render(par7);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
            GL11.glTranslatef(0.0F, 24.0F * par7, 0.0F);
            this.ocelotBody.render(par7);
            this.ocelotBackLeftLeg.render(par7);
            this.ocelotBackRightLeg.render(par7);
            this.ocelotFrontLeftLeg.render(par7);
            this.ocelotFrontRightLeg.render(par7);
            this.ocelotTail.render(par7);
            this.ocelotTail2.render(par7);
            GL11.glPopMatrix();
        }
        else
        {
            this.ocelotHead.render(par7);
            this.ocelotBody.render(par7);
            this.ocelotTail.render(par7);
            this.ocelotTail2.render(par7);
            this.ocelotBackLeftLeg.render(par7);
            this.ocelotBackRightLeg.render(par7);
            this.ocelotFrontLeftLeg.render(par7);
            this.ocelotFrontRightLeg.render(par7);
        }
    }

    // JAVADOC METHOD $$ func_78087_a
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity)
    {
        this.ocelotHead.rotateAngleX = par5 / (180F / (float)Math.PI);
        this.ocelotHead.rotateAngleY = par4 / (180F / (float)Math.PI);

        if (this.field_78163_i != 3)
        {
            this.ocelotBody.rotateAngleX = ((float)Math.PI / 2F);

            if (this.field_78163_i == 2)
            {
                this.ocelotBackLeftLeg.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.0F * par2;
                this.ocelotBackRightLeg.rotateAngleX = MathHelper.cos(par1 * 0.6662F + 0.3F) * 1.0F * par2;
                this.ocelotFrontLeftLeg.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float)Math.PI + 0.3F) * 1.0F * par2;
                this.ocelotFrontRightLeg.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float)Math.PI) * 1.0F * par2;
                this.ocelotTail2.rotateAngleX = 1.7278761F + ((float)Math.PI / 10F) * MathHelper.cos(par1) * par2;
            }
            else
            {
                this.ocelotBackLeftLeg.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.0F * par2;
                this.ocelotBackRightLeg.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float)Math.PI) * 1.0F * par2;
                this.ocelotFrontLeftLeg.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float)Math.PI) * 1.0F * par2;
                this.ocelotFrontRightLeg.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.0F * par2;

                if (this.field_78163_i == 1)
                {
                    this.ocelotTail2.rotateAngleX = 1.7278761F + ((float)Math.PI / 4F) * MathHelper.cos(par1) * par2;
                }
                else
                {
                    this.ocelotTail2.rotateAngleX = 1.7278761F + 0.47123894F * MathHelper.cos(par1) * par2;
                }
            }
        }
    }

    // JAVADOC METHOD $$ func_78086_a
    public void setLivingAnimations(EntityLivingBase par1EntityLivingBase, float par2, float par3, float par4)
    {
        EntityOcelot entityocelot = (EntityOcelot)par1EntityLivingBase;
        this.ocelotBody.rotationPointY = 12.0F;
        this.ocelotBody.rotationPointZ = -10.0F;
        this.ocelotHead.rotationPointY = 15.0F;
        this.ocelotHead.rotationPointZ = -9.0F;
        this.ocelotTail.rotationPointY = 15.0F;
        this.ocelotTail.rotationPointZ = 8.0F;
        this.ocelotTail2.rotationPointY = 20.0F;
        this.ocelotTail2.rotationPointZ = 14.0F;
        this.ocelotFrontLeftLeg.rotationPointY = this.ocelotFrontRightLeg.rotationPointY = 13.8F;
        this.ocelotFrontLeftLeg.rotationPointZ = this.ocelotFrontRightLeg.rotationPointZ = -5.0F;
        this.ocelotBackLeftLeg.rotationPointY = this.ocelotBackRightLeg.rotationPointY = 18.0F;
        this.ocelotBackLeftLeg.rotationPointZ = this.ocelotBackRightLeg.rotationPointZ = 5.0F;
        this.ocelotTail.rotateAngleX = 0.9F;

        if (entityocelot.isSneaking())
        {
            ++this.ocelotBody.rotationPointY;
            this.ocelotHead.rotationPointY += 2.0F;
            ++this.ocelotTail.rotationPointY;
            this.ocelotTail2.rotationPointY += -4.0F;
            this.ocelotTail2.rotationPointZ += 2.0F;
            this.ocelotTail.rotateAngleX = ((float)Math.PI / 2F);
            this.ocelotTail2.rotateAngleX = ((float)Math.PI / 2F);
            this.field_78163_i = 0;
        }
        else if (entityocelot.isSprinting())
        {
            this.ocelotTail2.rotationPointY = this.ocelotTail.rotationPointY;
            this.ocelotTail2.rotationPointZ += 2.0F;
            this.ocelotTail.rotateAngleX = ((float)Math.PI / 2F);
            this.ocelotTail2.rotateAngleX = ((float)Math.PI / 2F);
            this.field_78163_i = 2;
        }
        else if (entityocelot.isSitting())
        {
            this.ocelotBody.rotateAngleX = ((float)Math.PI / 4F);
            this.ocelotBody.rotationPointY += -4.0F;
            this.ocelotBody.rotationPointZ += 5.0F;
            this.ocelotHead.rotationPointY += -3.3F;
            ++this.ocelotHead.rotationPointZ;
            this.ocelotTail.rotationPointY += 8.0F;
            this.ocelotTail.rotationPointZ += -2.0F;
            this.ocelotTail2.rotationPointY += 2.0F;
            this.ocelotTail2.rotationPointZ += -0.8F;
            this.ocelotTail.rotateAngleX = 1.7278761F;
            this.ocelotTail2.rotateAngleX = 2.670354F;
            this.ocelotFrontLeftLeg.rotateAngleX = this.ocelotFrontRightLeg.rotateAngleX = -0.15707964F;
            this.ocelotFrontLeftLeg.rotationPointY = this.ocelotFrontRightLeg.rotationPointY = 15.8F;
            this.ocelotFrontLeftLeg.rotationPointZ = this.ocelotFrontRightLeg.rotationPointZ = -7.0F;
            this.ocelotBackLeftLeg.rotateAngleX = this.ocelotBackRightLeg.rotateAngleX = -((float)Math.PI / 2F);
            this.ocelotBackLeftLeg.rotationPointY = this.ocelotBackRightLeg.rotationPointY = 21.0F;
            this.ocelotBackLeftLeg.rotationPointZ = this.ocelotBackRightLeg.rotationPointZ = 1.0F;
            this.field_78163_i = 3;
        }
        else
        {
            this.field_78163_i = 1;
        }
    }
}