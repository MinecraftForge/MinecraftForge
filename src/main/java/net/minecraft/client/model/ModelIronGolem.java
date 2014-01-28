package net.minecraft.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;

@SideOnly(Side.CLIENT)
public class ModelIronGolem extends ModelBase
{
    // JAVADOC FIELD $$ field_78178_a
    public ModelRenderer ironGolemHead;
    // JAVADOC FIELD $$ field_78176_b
    public ModelRenderer ironGolemBody;
    // JAVADOC FIELD $$ field_78177_c
    public ModelRenderer ironGolemRightArm;
    // JAVADOC FIELD $$ field_78174_d
    public ModelRenderer ironGolemLeftArm;
    // JAVADOC FIELD $$ field_78175_e
    public ModelRenderer ironGolemLeftLeg;
    // JAVADOC FIELD $$ field_78173_f
    public ModelRenderer ironGolemRightLeg;
    private static final String __OBFID = "CL_00000863";

    public ModelIronGolem()
    {
        this(0.0F);
    }

    public ModelIronGolem(float par1)
    {
        this(par1, -7.0F);
    }

    public ModelIronGolem(float par1, float par2)
    {
        short short1 = 128;
        short short2 = 128;
        this.ironGolemHead = (new ModelRenderer(this)).setTextureSize(short1, short2);
        this.ironGolemHead.setRotationPoint(0.0F, 0.0F + par2, -2.0F);
        this.ironGolemHead.setTextureOffset(0, 0).addBox(-4.0F, -12.0F, -5.5F, 8, 10, 8, par1);
        this.ironGolemHead.setTextureOffset(24, 0).addBox(-1.0F, -5.0F, -7.5F, 2, 4, 2, par1);
        this.ironGolemBody = (new ModelRenderer(this)).setTextureSize(short1, short2);
        this.ironGolemBody.setRotationPoint(0.0F, 0.0F + par2, 0.0F);
        this.ironGolemBody.setTextureOffset(0, 40).addBox(-9.0F, -2.0F, -6.0F, 18, 12, 11, par1);
        this.ironGolemBody.setTextureOffset(0, 70).addBox(-4.5F, 10.0F, -3.0F, 9, 5, 6, par1 + 0.5F);
        this.ironGolemRightArm = (new ModelRenderer(this)).setTextureSize(short1, short2);
        this.ironGolemRightArm.setRotationPoint(0.0F, -7.0F, 0.0F);
        this.ironGolemRightArm.setTextureOffset(60, 21).addBox(-13.0F, -2.5F, -3.0F, 4, 30, 6, par1);
        this.ironGolemLeftArm = (new ModelRenderer(this)).setTextureSize(short1, short2);
        this.ironGolemLeftArm.setRotationPoint(0.0F, -7.0F, 0.0F);
        this.ironGolemLeftArm.setTextureOffset(60, 58).addBox(9.0F, -2.5F, -3.0F, 4, 30, 6, par1);
        this.ironGolemLeftLeg = (new ModelRenderer(this, 0, 22)).setTextureSize(short1, short2);
        this.ironGolemLeftLeg.setRotationPoint(-4.0F, 18.0F + par2, 0.0F);
        this.ironGolemLeftLeg.setTextureOffset(37, 0).addBox(-3.5F, -3.0F, -3.0F, 6, 16, 5, par1);
        this.ironGolemRightLeg = (new ModelRenderer(this, 0, 22)).setTextureSize(short1, short2);
        this.ironGolemRightLeg.mirror = true;
        this.ironGolemRightLeg.setTextureOffset(60, 0).setRotationPoint(5.0F, 18.0F + par2, 0.0F);
        this.ironGolemRightLeg.addBox(-3.5F, -3.0F, -3.0F, 6, 16, 5, par1);
    }

    // JAVADOC METHOD $$ func_78088_a
    public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7, par1Entity);
        this.ironGolemHead.render(par7);
        this.ironGolemBody.render(par7);
        this.ironGolemLeftLeg.render(par7);
        this.ironGolemRightLeg.render(par7);
        this.ironGolemRightArm.render(par7);
        this.ironGolemLeftArm.render(par7);
    }

    // JAVADOC METHOD $$ func_78087_a
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity)
    {
        this.ironGolemHead.rotateAngleY = par4 / (180F / (float)Math.PI);
        this.ironGolemHead.rotateAngleX = par5 / (180F / (float)Math.PI);
        this.ironGolemLeftLeg.rotateAngleX = -1.5F * this.func_78172_a(par1, 13.0F) * par2;
        this.ironGolemRightLeg.rotateAngleX = 1.5F * this.func_78172_a(par1, 13.0F) * par2;
        this.ironGolemLeftLeg.rotateAngleY = 0.0F;
        this.ironGolemRightLeg.rotateAngleY = 0.0F;
    }

    // JAVADOC METHOD $$ func_78086_a
    public void setLivingAnimations(EntityLivingBase par1EntityLivingBase, float par2, float par3, float par4)
    {
        EntityIronGolem entityirongolem = (EntityIronGolem)par1EntityLivingBase;
        int i = entityirongolem.getAttackTimer();

        if (i > 0)
        {
            this.ironGolemRightArm.rotateAngleX = -2.0F + 1.5F * this.func_78172_a((float)i - par4, 10.0F);
            this.ironGolemLeftArm.rotateAngleX = -2.0F + 1.5F * this.func_78172_a((float)i - par4, 10.0F);
        }
        else
        {
            int j = entityirongolem.getHoldRoseTick();

            if (j > 0)
            {
                this.ironGolemRightArm.rotateAngleX = -0.8F + 0.025F * this.func_78172_a((float)j, 70.0F);
                this.ironGolemLeftArm.rotateAngleX = 0.0F;
            }
            else
            {
                this.ironGolemRightArm.rotateAngleX = (-0.2F + 1.5F * this.func_78172_a(par2, 13.0F)) * par3;
                this.ironGolemLeftArm.rotateAngleX = (-0.2F - 1.5F * this.func_78172_a(par2, 13.0F)) * par3;
            }
        }
    }

    private float func_78172_a(float par1, float par2)
    {
        return (Math.abs(par1 % par2 - par2 * 0.5F) - par2 * 0.25F) / (par2 * 0.25F);
    }
}