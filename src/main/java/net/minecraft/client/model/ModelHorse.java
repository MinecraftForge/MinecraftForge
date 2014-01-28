package net.minecraft.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class ModelHorse extends ModelBase
{
    private ModelRenderer head;
    private ModelRenderer mouthTop;
    private ModelRenderer mouthBottom;
    private ModelRenderer horseLeftEar;
    private ModelRenderer horseRightEar;
    private ModelRenderer field_110703_f;
    private ModelRenderer field_110704_g;
    private ModelRenderer neck;
    private ModelRenderer field_110717_i;
    private ModelRenderer mane;
    private ModelRenderer body;
    private ModelRenderer tailBase;
    private ModelRenderer tailMiddle;
    private ModelRenderer tailTip;
    private ModelRenderer backLeftLeg;
    private ModelRenderer backLeftShin;
    private ModelRenderer backLeftHoof;
    private ModelRenderer backRightLeg;
    private ModelRenderer backRightShin;
    private ModelRenderer backRightHoof;
    private ModelRenderer frontRightLeg;
    private ModelRenderer frontLeftShin;
    private ModelRenderer frontLeftHoof;
    private ModelRenderer field_110684_D;
    private ModelRenderer frontRightShin;
    private ModelRenderer frontRightHoof;
    private ModelRenderer field_110687_G;
    private ModelRenderer field_110695_H;
    private ModelRenderer field_110696_I;
    private ModelRenderer field_110697_J;
    private ModelRenderer field_110698_K;
    private ModelRenderer field_110691_L;
    private ModelRenderer field_110692_M;
    private ModelRenderer field_110693_N;
    private ModelRenderer field_110694_O;
    private ModelRenderer field_110700_P;
    private ModelRenderer field_110699_Q;
    private ModelRenderer field_110702_R;
    private ModelRenderer field_110701_S;
    private static final String __OBFID = "CL_00000846";

    public ModelHorse()
    {
        this.textureWidth = 128;
        this.textureHeight = 128;
        this.body = new ModelRenderer(this, 0, 34);
        this.body.addBox(-5.0F, -8.0F, -19.0F, 10, 10, 24);
        this.body.setRotationPoint(0.0F, 11.0F, 9.0F);
        this.tailBase = new ModelRenderer(this, 44, 0);
        this.tailBase.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 3);
        this.tailBase.setRotationPoint(0.0F, 3.0F, 14.0F);
        this.func_110682_a(this.tailBase, -1.134464F, 0.0F, 0.0F);
        this.tailMiddle = new ModelRenderer(this, 38, 7);
        this.tailMiddle.addBox(-1.5F, -2.0F, 3.0F, 3, 4, 7);
        this.tailMiddle.setRotationPoint(0.0F, 3.0F, 14.0F);
        this.func_110682_a(this.tailMiddle, -1.134464F, 0.0F, 0.0F);
        this.tailTip = new ModelRenderer(this, 24, 3);
        this.tailTip.addBox(-1.5F, -4.5F, 9.0F, 3, 4, 7);
        this.tailTip.setRotationPoint(0.0F, 3.0F, 14.0F);
        this.func_110682_a(this.tailTip, -1.40215F, 0.0F, 0.0F);
        this.backLeftLeg = new ModelRenderer(this, 78, 29);
        this.backLeftLeg.addBox(-2.5F, -2.0F, -2.5F, 4, 9, 5);
        this.backLeftLeg.setRotationPoint(4.0F, 9.0F, 11.0F);
        this.backLeftShin = new ModelRenderer(this, 78, 43);
        this.backLeftShin.addBox(-2.0F, 0.0F, -1.5F, 3, 5, 3);
        this.backLeftShin.setRotationPoint(4.0F, 16.0F, 11.0F);
        this.backLeftHoof = new ModelRenderer(this, 78, 51);
        this.backLeftHoof.addBox(-2.5F, 5.1F, -2.0F, 4, 3, 4);
        this.backLeftHoof.setRotationPoint(4.0F, 16.0F, 11.0F);
        this.backRightLeg = new ModelRenderer(this, 96, 29);
        this.backRightLeg.addBox(-1.5F, -2.0F, -2.5F, 4, 9, 5);
        this.backRightLeg.setRotationPoint(-4.0F, 9.0F, 11.0F);
        this.backRightShin = new ModelRenderer(this, 96, 43);
        this.backRightShin.addBox(-1.0F, 0.0F, -1.5F, 3, 5, 3);
        this.backRightShin.setRotationPoint(-4.0F, 16.0F, 11.0F);
        this.backRightHoof = new ModelRenderer(this, 96, 51);
        this.backRightHoof.addBox(-1.5F, 5.1F, -2.0F, 4, 3, 4);
        this.backRightHoof.setRotationPoint(-4.0F, 16.0F, 11.0F);
        this.frontRightLeg = new ModelRenderer(this, 44, 29);
        this.frontRightLeg.addBox(-1.9F, -1.0F, -2.1F, 3, 8, 4);
        this.frontRightLeg.setRotationPoint(4.0F, 9.0F, -8.0F);
        this.frontLeftShin = new ModelRenderer(this, 44, 41);
        this.frontLeftShin.addBox(-1.9F, 0.0F, -1.6F, 3, 5, 3);
        this.frontLeftShin.setRotationPoint(4.0F, 16.0F, -8.0F);
        this.frontLeftHoof = new ModelRenderer(this, 44, 51);
        this.frontLeftHoof.addBox(-2.4F, 5.1F, -2.1F, 4, 3, 4);
        this.frontLeftHoof.setRotationPoint(4.0F, 16.0F, -8.0F);
        this.field_110684_D = new ModelRenderer(this, 60, 29);
        this.field_110684_D.addBox(-1.1F, -1.0F, -2.1F, 3, 8, 4);
        this.field_110684_D.setRotationPoint(-4.0F, 9.0F, -8.0F);
        this.frontRightShin = new ModelRenderer(this, 60, 41);
        this.frontRightShin.addBox(-1.1F, 0.0F, -1.6F, 3, 5, 3);
        this.frontRightShin.setRotationPoint(-4.0F, 16.0F, -8.0F);
        this.frontRightHoof = new ModelRenderer(this, 60, 51);
        this.frontRightHoof.addBox(-1.6F, 5.1F, -2.1F, 4, 3, 4);
        this.frontRightHoof.setRotationPoint(-4.0F, 16.0F, -8.0F);
        this.head = new ModelRenderer(this, 0, 0);
        this.head.addBox(-2.5F, -10.0F, -1.5F, 5, 5, 7);
        this.head.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.func_110682_a(this.head, 0.5235988F, 0.0F, 0.0F);
        this.mouthTop = new ModelRenderer(this, 24, 18);
        this.mouthTop.addBox(-2.0F, -10.0F, -7.0F, 4, 3, 6);
        this.mouthTop.setRotationPoint(0.0F, 3.95F, -10.0F);
        this.func_110682_a(this.mouthTop, 0.5235988F, 0.0F, 0.0F);
        this.mouthBottom = new ModelRenderer(this, 24, 27);
        this.mouthBottom.addBox(-2.0F, -7.0F, -6.5F, 4, 2, 5);
        this.mouthBottom.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.func_110682_a(this.mouthBottom, 0.5235988F, 0.0F, 0.0F);
        this.head.addChild(this.mouthTop);
        this.head.addChild(this.mouthBottom);
        this.horseLeftEar = new ModelRenderer(this, 0, 0);
        this.horseLeftEar.addBox(0.45F, -12.0F, 4.0F, 2, 3, 1);
        this.horseLeftEar.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.func_110682_a(this.horseLeftEar, 0.5235988F, 0.0F, 0.0F);
        this.horseRightEar = new ModelRenderer(this, 0, 0);
        this.horseRightEar.addBox(-2.45F, -12.0F, 4.0F, 2, 3, 1);
        this.horseRightEar.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.func_110682_a(this.horseRightEar, 0.5235988F, 0.0F, 0.0F);
        this.field_110703_f = new ModelRenderer(this, 0, 12);
        this.field_110703_f.addBox(-2.0F, -16.0F, 4.0F, 2, 7, 1);
        this.field_110703_f.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.func_110682_a(this.field_110703_f, 0.5235988F, 0.0F, 0.2617994F);
        this.field_110704_g = new ModelRenderer(this, 0, 12);
        this.field_110704_g.addBox(0.0F, -16.0F, 4.0F, 2, 7, 1);
        this.field_110704_g.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.func_110682_a(this.field_110704_g, 0.5235988F, 0.0F, -0.2617994F);
        this.neck = new ModelRenderer(this, 0, 12);
        this.neck.addBox(-2.05F, -9.8F, -2.0F, 4, 14, 8);
        this.neck.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.func_110682_a(this.neck, 0.5235988F, 0.0F, 0.0F);
        this.field_110687_G = new ModelRenderer(this, 0, 34);
        this.field_110687_G.addBox(-3.0F, 0.0F, 0.0F, 8, 8, 3);
        this.field_110687_G.setRotationPoint(-7.5F, 3.0F, 10.0F);
        this.func_110682_a(this.field_110687_G, 0.0F, ((float)Math.PI / 2F), 0.0F);
        this.field_110695_H = new ModelRenderer(this, 0, 47);
        this.field_110695_H.addBox(-3.0F, 0.0F, 0.0F, 8, 8, 3);
        this.field_110695_H.setRotationPoint(4.5F, 3.0F, 10.0F);
        this.func_110682_a(this.field_110695_H, 0.0F, ((float)Math.PI / 2F), 0.0F);
        this.field_110696_I = new ModelRenderer(this, 80, 0);
        this.field_110696_I.addBox(-5.0F, 0.0F, -3.0F, 10, 1, 8);
        this.field_110696_I.setRotationPoint(0.0F, 2.0F, 2.0F);
        this.field_110697_J = new ModelRenderer(this, 106, 9);
        this.field_110697_J.addBox(-1.5F, -1.0F, -3.0F, 3, 1, 2);
        this.field_110697_J.setRotationPoint(0.0F, 2.0F, 2.0F);
        this.field_110698_K = new ModelRenderer(this, 80, 9);
        this.field_110698_K.addBox(-4.0F, -1.0F, 3.0F, 8, 1, 2);
        this.field_110698_K.setRotationPoint(0.0F, 2.0F, 2.0F);
        this.field_110692_M = new ModelRenderer(this, 74, 0);
        this.field_110692_M.addBox(-0.5F, 6.0F, -1.0F, 1, 2, 2);
        this.field_110692_M.setRotationPoint(5.0F, 3.0F, 2.0F);
        this.field_110691_L = new ModelRenderer(this, 70, 0);
        this.field_110691_L.addBox(-0.5F, 0.0F, -0.5F, 1, 6, 1);
        this.field_110691_L.setRotationPoint(5.0F, 3.0F, 2.0F);
        this.field_110694_O = new ModelRenderer(this, 74, 4);
        this.field_110694_O.addBox(-0.5F, 6.0F, -1.0F, 1, 2, 2);
        this.field_110694_O.setRotationPoint(-5.0F, 3.0F, 2.0F);
        this.field_110693_N = new ModelRenderer(this, 80, 0);
        this.field_110693_N.addBox(-0.5F, 0.0F, -0.5F, 1, 6, 1);
        this.field_110693_N.setRotationPoint(-5.0F, 3.0F, 2.0F);
        this.field_110700_P = new ModelRenderer(this, 74, 13);
        this.field_110700_P.addBox(1.5F, -8.0F, -4.0F, 1, 2, 2);
        this.field_110700_P.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.func_110682_a(this.field_110700_P, 0.5235988F, 0.0F, 0.0F);
        this.field_110699_Q = new ModelRenderer(this, 74, 13);
        this.field_110699_Q.addBox(-2.5F, -8.0F, -4.0F, 1, 2, 2);
        this.field_110699_Q.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.func_110682_a(this.field_110699_Q, 0.5235988F, 0.0F, 0.0F);
        this.field_110702_R = new ModelRenderer(this, 44, 10);
        this.field_110702_R.addBox(2.6F, -6.0F, -6.0F, 0, 3, 16);
        this.field_110702_R.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.field_110701_S = new ModelRenderer(this, 44, 5);
        this.field_110701_S.addBox(-2.6F, -6.0F, -6.0F, 0, 3, 16);
        this.field_110701_S.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.mane = new ModelRenderer(this, 58, 0);
        this.mane.addBox(-1.0F, -11.5F, 5.0F, 2, 16, 4);
        this.mane.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.func_110682_a(this.mane, 0.5235988F, 0.0F, 0.0F);
        this.field_110717_i = new ModelRenderer(this, 80, 12);
        this.field_110717_i.addBox(-2.5F, -10.1F, -7.0F, 5, 5, 12, 0.2F);
        this.field_110717_i.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.func_110682_a(this.field_110717_i, 0.5235988F, 0.0F, 0.0F);
    }

    // JAVADOC METHOD $$ func_78088_a
    public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        EntityHorse entityhorse = (EntityHorse)par1Entity;
        int i = entityhorse.getHorseType();
        float f6 = entityhorse.getGrassEatingAmount(0.0F);
        boolean flag = entityhorse.isAdultHorse();
        boolean flag1 = flag && entityhorse.isHorseSaddled();
        boolean flag2 = flag && entityhorse.isChested();
        boolean flag3 = i == 1 || i == 2;
        float f7 = entityhorse.getHorseSize();
        boolean flag4 = entityhorse.riddenByEntity != null;

        if (flag1)
        {
            this.field_110717_i.render(par7);
            this.field_110696_I.render(par7);
            this.field_110697_J.render(par7);
            this.field_110698_K.render(par7);
            this.field_110691_L.render(par7);
            this.field_110692_M.render(par7);
            this.field_110693_N.render(par7);
            this.field_110694_O.render(par7);
            this.field_110700_P.render(par7);
            this.field_110699_Q.render(par7);

            if (flag4)
            {
                this.field_110702_R.render(par7);
                this.field_110701_S.render(par7);
            }
        }

        if (!flag)
        {
            GL11.glPushMatrix();
            GL11.glScalef(f7, 0.5F + f7 * 0.5F, f7);
            GL11.glTranslatef(0.0F, 0.95F * (1.0F - f7), 0.0F);
        }

        this.backLeftLeg.render(par7);
        this.backLeftShin.render(par7);
        this.backLeftHoof.render(par7);
        this.backRightLeg.render(par7);
        this.backRightShin.render(par7);
        this.backRightHoof.render(par7);
        this.frontRightLeg.render(par7);
        this.frontLeftShin.render(par7);
        this.frontLeftHoof.render(par7);
        this.field_110684_D.render(par7);
        this.frontRightShin.render(par7);
        this.frontRightHoof.render(par7);

        if (!flag)
        {
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(f7, f7, f7);
            GL11.glTranslatef(0.0F, 1.35F * (1.0F - f7), 0.0F);
        }

        this.body.render(par7);
        this.tailBase.render(par7);
        this.tailMiddle.render(par7);
        this.tailTip.render(par7);
        this.neck.render(par7);
        this.mane.render(par7);

        if (!flag)
        {
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            float f8 = 0.5F + f7 * f7 * 0.5F;
            GL11.glScalef(f8, f8, f8);

            if (f6 <= 0.0F)
            {
                GL11.glTranslatef(0.0F, 1.35F * (1.0F - f7), 0.0F);
            }
            else
            {
                GL11.glTranslatef(0.0F, 0.9F * (1.0F - f7) * f6 + 1.35F * (1.0F - f7) * (1.0F - f6), 0.15F * (1.0F - f7) * f6);
            }
        }

        if (flag3)
        {
            this.field_110703_f.render(par7);
            this.field_110704_g.render(par7);
        }
        else
        {
            this.horseLeftEar.render(par7);
            this.horseRightEar.render(par7);
        }

        this.head.render(par7);

        if (!flag)
        {
            GL11.glPopMatrix();
        }

        if (flag2)
        {
            this.field_110687_G.render(par7);
            this.field_110695_H.render(par7);
        }
    }

    private void func_110682_a(ModelRenderer par1ModelRenderer, float par2, float par3, float par4)
    {
        par1ModelRenderer.rotateAngleX = par2;
        par1ModelRenderer.rotateAngleY = par3;
        par1ModelRenderer.rotateAngleZ = par4;
    }

    private float func_110683_a(float par1, float par2, float par3)
    {
        float f3;

        for (f3 = par2 - par1; f3 < -180.0F; f3 += 360.0F)
        {
            ;
        }

        while (f3 >= 180.0F)
        {
            f3 -= 360.0F;
        }

        return par1 + par3 * f3;
    }

    // JAVADOC METHOD $$ func_78086_a
    public void setLivingAnimations(EntityLivingBase par1EntityLivingBase, float par2, float par3, float par4)
    {
        super.setLivingAnimations(par1EntityLivingBase, par2, par3, par4);
        float f3 = this.func_110683_a(par1EntityLivingBase.prevRenderYawOffset, par1EntityLivingBase.renderYawOffset, par4);
        float f4 = this.func_110683_a(par1EntityLivingBase.prevRotationYawHead, par1EntityLivingBase.rotationYawHead, par4);
        float f5 = par1EntityLivingBase.prevRotationPitch + (par1EntityLivingBase.rotationPitch - par1EntityLivingBase.prevRotationPitch) * par4;
        float f6 = f4 - f3;
        float f7 = f5 / (180F / (float)Math.PI);

        if (f6 > 20.0F)
        {
            f6 = 20.0F;
        }

        if (f6 < -20.0F)
        {
            f6 = -20.0F;
        }

        if (par3 > 0.2F)
        {
            f7 += MathHelper.cos(par2 * 0.4F) * 0.15F * par3;
        }

        EntityHorse entityhorse = (EntityHorse)par1EntityLivingBase;
        float f8 = entityhorse.getGrassEatingAmount(par4);
        float f9 = entityhorse.getRearingAmount(par4);
        float f10 = 1.0F - f9;
        float f11 = entityhorse.func_110201_q(par4);
        boolean flag = entityhorse.field_110278_bp != 0;
        boolean flag1 = entityhorse.isHorseSaddled();
        boolean flag2 = entityhorse.riddenByEntity != null;
        float f12 = (float)par1EntityLivingBase.ticksExisted + par4;
        float f13 = MathHelper.cos(par2 * 0.6662F + (float)Math.PI);
        float f14 = f13 * 0.8F * par3;
        this.head.rotationPointY = 4.0F;
        this.head.rotationPointZ = -10.0F;
        this.tailBase.rotationPointY = 3.0F;
        this.tailMiddle.rotationPointZ = 14.0F;
        this.field_110695_H.rotationPointY = 3.0F;
        this.field_110695_H.rotationPointZ = 10.0F;
        this.body.rotateAngleX = 0.0F;
        this.head.rotateAngleX = 0.5235988F + f7;
        this.head.rotateAngleY = f6 / (180F / (float)Math.PI);
        this.head.rotateAngleX = f9 * (0.2617994F + f7) + f8 * 2.18166F + (1.0F - Math.max(f9, f8)) * this.head.rotateAngleX;
        this.head.rotateAngleY = f9 * (f6 / (180F / (float)Math.PI)) + (1.0F - Math.max(f9, f8)) * this.head.rotateAngleY;
        this.head.rotationPointY = f9 * -6.0F + f8 * 11.0F + (1.0F - Math.max(f9, f8)) * this.head.rotationPointY;
        this.head.rotationPointZ = f9 * -1.0F + f8 * -10.0F + (1.0F - Math.max(f9, f8)) * this.head.rotationPointZ;
        this.tailBase.rotationPointY = f9 * 9.0F + f10 * this.tailBase.rotationPointY;
        this.tailMiddle.rotationPointZ = f9 * 18.0F + f10 * this.tailMiddle.rotationPointZ;
        this.field_110695_H.rotationPointY = f9 * 5.5F + f10 * this.field_110695_H.rotationPointY;
        this.field_110695_H.rotationPointZ = f9 * 15.0F + f10 * this.field_110695_H.rotationPointZ;
        this.body.rotateAngleX = f9 * -((float)Math.PI / 4F) + f10 * this.body.rotateAngleX;
        this.horseLeftEar.rotationPointY = this.head.rotationPointY;
        this.horseRightEar.rotationPointY = this.head.rotationPointY;
        this.field_110703_f.rotationPointY = this.head.rotationPointY;
        this.field_110704_g.rotationPointY = this.head.rotationPointY;
        this.neck.rotationPointY = this.head.rotationPointY;
        this.mouthTop.rotationPointY = 0.02F;
        this.mouthBottom.rotationPointY = 0.0F;
        this.mane.rotationPointY = this.head.rotationPointY;
        this.horseLeftEar.rotationPointZ = this.head.rotationPointZ;
        this.horseRightEar.rotationPointZ = this.head.rotationPointZ;
        this.field_110703_f.rotationPointZ = this.head.rotationPointZ;
        this.field_110704_g.rotationPointZ = this.head.rotationPointZ;
        this.neck.rotationPointZ = this.head.rotationPointZ;
        this.mouthTop.rotationPointZ = 0.02F - f11 * 1.0F;
        this.mouthBottom.rotationPointZ = 0.0F + f11 * 1.0F;
        this.mane.rotationPointZ = this.head.rotationPointZ;
        this.horseLeftEar.rotateAngleX = this.head.rotateAngleX;
        this.horseRightEar.rotateAngleX = this.head.rotateAngleX;
        this.field_110703_f.rotateAngleX = this.head.rotateAngleX;
        this.field_110704_g.rotateAngleX = this.head.rotateAngleX;
        this.neck.rotateAngleX = this.head.rotateAngleX;
        this.mouthTop.rotateAngleX = 0.0F - 0.09424778F * f11;
        this.mouthBottom.rotateAngleX = 0.0F + 0.15707964F * f11;
        this.mane.rotateAngleX = this.head.rotateAngleX;
        this.horseLeftEar.rotateAngleY = this.head.rotateAngleY;
        this.horseRightEar.rotateAngleY = this.head.rotateAngleY;
        this.field_110703_f.rotateAngleY = this.head.rotateAngleY;
        this.field_110704_g.rotateAngleY = this.head.rotateAngleY;
        this.neck.rotateAngleY = this.head.rotateAngleY;
        this.mouthTop.rotateAngleY = 0.0F;
        this.mouthBottom.rotateAngleY = 0.0F;
        this.mane.rotateAngleY = this.head.rotateAngleY;
        this.field_110687_G.rotateAngleX = f14 / 5.0F;
        this.field_110695_H.rotateAngleX = -f14 / 5.0F;
        float f15 = ((float)Math.PI / 2F);
        float f16 = ((float)Math.PI * 3F / 2F);
        float f17 = -1.0471976F;
        float f18 = 0.2617994F * f9;
        float f19 = MathHelper.cos(f12 * 0.6F + (float)Math.PI);
        this.frontRightLeg.rotationPointY = -2.0F * f9 + 9.0F * f10;
        this.frontRightLeg.rotationPointZ = -2.0F * f9 + -8.0F * f10;
        this.field_110684_D.rotationPointY = this.frontRightLeg.rotationPointY;
        this.field_110684_D.rotationPointZ = this.frontRightLeg.rotationPointZ;
        this.backLeftShin.rotationPointY = this.backLeftLeg.rotationPointY + MathHelper.sin(((float)Math.PI / 2F) + f18 + f10 * -f13 * 0.5F * par3) * 7.0F;
        this.backLeftShin.rotationPointZ = this.backLeftLeg.rotationPointZ + MathHelper.cos(((float)Math.PI * 3F / 2F) + f18 + f10 * -f13 * 0.5F * par3) * 7.0F;
        this.backRightShin.rotationPointY = this.backRightLeg.rotationPointY + MathHelper.sin(((float)Math.PI / 2F) + f18 + f10 * f13 * 0.5F * par3) * 7.0F;
        this.backRightShin.rotationPointZ = this.backRightLeg.rotationPointZ + MathHelper.cos(((float)Math.PI * 3F / 2F) + f18 + f10 * f13 * 0.5F * par3) * 7.0F;
        float f20 = (-1.0471976F + f19) * f9 + f14 * f10;
        float f21 = (-1.0471976F + -f19) * f9 + -f14 * f10;
        this.frontLeftShin.rotationPointY = this.frontRightLeg.rotationPointY + MathHelper.sin(((float)Math.PI / 2F) + f20) * 7.0F;
        this.frontLeftShin.rotationPointZ = this.frontRightLeg.rotationPointZ + MathHelper.cos(((float)Math.PI * 3F / 2F) + f20) * 7.0F;
        this.frontRightShin.rotationPointY = this.field_110684_D.rotationPointY + MathHelper.sin(((float)Math.PI / 2F) + f21) * 7.0F;
        this.frontRightShin.rotationPointZ = this.field_110684_D.rotationPointZ + MathHelper.cos(((float)Math.PI * 3F / 2F) + f21) * 7.0F;
        this.backLeftLeg.rotateAngleX = f18 + -f13 * 0.5F * par3 * f10;
        this.backLeftShin.rotateAngleX = -0.08726646F * f9 + (-f13 * 0.5F * par3 - Math.max(0.0F, f13 * 0.5F * par3)) * f10;
        this.backLeftHoof.rotateAngleX = this.backLeftShin.rotateAngleX;
        this.backRightLeg.rotateAngleX = f18 + f13 * 0.5F * par3 * f10;
        this.backRightShin.rotateAngleX = -0.08726646F * f9 + (f13 * 0.5F * par3 - Math.max(0.0F, -f13 * 0.5F * par3)) * f10;
        this.backRightHoof.rotateAngleX = this.backRightShin.rotateAngleX;
        this.frontRightLeg.rotateAngleX = f20;
        this.frontLeftShin.rotateAngleX = (this.frontRightLeg.rotateAngleX + (float)Math.PI * Math.max(0.0F, 0.2F + f19 * 0.2F)) * f9 + (f14 + Math.max(0.0F, f13 * 0.5F * par3)) * f10;
        this.frontLeftHoof.rotateAngleX = this.frontLeftShin.rotateAngleX;
        this.field_110684_D.rotateAngleX = f21;
        this.frontRightShin.rotateAngleX = (this.field_110684_D.rotateAngleX + (float)Math.PI * Math.max(0.0F, 0.2F - f19 * 0.2F)) * f9 + (-f14 + Math.max(0.0F, -f13 * 0.5F * par3)) * f10;
        this.frontRightHoof.rotateAngleX = this.frontRightShin.rotateAngleX;
        this.backLeftHoof.rotationPointY = this.backLeftShin.rotationPointY;
        this.backLeftHoof.rotationPointZ = this.backLeftShin.rotationPointZ;
        this.backRightHoof.rotationPointY = this.backRightShin.rotationPointY;
        this.backRightHoof.rotationPointZ = this.backRightShin.rotationPointZ;
        this.frontLeftHoof.rotationPointY = this.frontLeftShin.rotationPointY;
        this.frontLeftHoof.rotationPointZ = this.frontLeftShin.rotationPointZ;
        this.frontRightHoof.rotationPointY = this.frontRightShin.rotationPointY;
        this.frontRightHoof.rotationPointZ = this.frontRightShin.rotationPointZ;

        if (flag1)
        {
            this.field_110696_I.rotationPointY = f9 * 0.5F + f10 * 2.0F;
            this.field_110696_I.rotationPointZ = f9 * 11.0F + f10 * 2.0F;
            this.field_110697_J.rotationPointY = this.field_110696_I.rotationPointY;
            this.field_110698_K.rotationPointY = this.field_110696_I.rotationPointY;
            this.field_110691_L.rotationPointY = this.field_110696_I.rotationPointY;
            this.field_110693_N.rotationPointY = this.field_110696_I.rotationPointY;
            this.field_110692_M.rotationPointY = this.field_110696_I.rotationPointY;
            this.field_110694_O.rotationPointY = this.field_110696_I.rotationPointY;
            this.field_110687_G.rotationPointY = this.field_110695_H.rotationPointY;
            this.field_110697_J.rotationPointZ = this.field_110696_I.rotationPointZ;
            this.field_110698_K.rotationPointZ = this.field_110696_I.rotationPointZ;
            this.field_110691_L.rotationPointZ = this.field_110696_I.rotationPointZ;
            this.field_110693_N.rotationPointZ = this.field_110696_I.rotationPointZ;
            this.field_110692_M.rotationPointZ = this.field_110696_I.rotationPointZ;
            this.field_110694_O.rotationPointZ = this.field_110696_I.rotationPointZ;
            this.field_110687_G.rotationPointZ = this.field_110695_H.rotationPointZ;
            this.field_110696_I.rotateAngleX = this.body.rotateAngleX;
            this.field_110697_J.rotateAngleX = this.body.rotateAngleX;
            this.field_110698_K.rotateAngleX = this.body.rotateAngleX;
            this.field_110702_R.rotationPointY = this.head.rotationPointY;
            this.field_110701_S.rotationPointY = this.head.rotationPointY;
            this.field_110717_i.rotationPointY = this.head.rotationPointY;
            this.field_110700_P.rotationPointY = this.head.rotationPointY;
            this.field_110699_Q.rotationPointY = this.head.rotationPointY;
            this.field_110702_R.rotationPointZ = this.head.rotationPointZ;
            this.field_110701_S.rotationPointZ = this.head.rotationPointZ;
            this.field_110717_i.rotationPointZ = this.head.rotationPointZ;
            this.field_110700_P.rotationPointZ = this.head.rotationPointZ;
            this.field_110699_Q.rotationPointZ = this.head.rotationPointZ;
            this.field_110702_R.rotateAngleX = f7;
            this.field_110701_S.rotateAngleX = f7;
            this.field_110717_i.rotateAngleX = this.head.rotateAngleX;
            this.field_110700_P.rotateAngleX = this.head.rotateAngleX;
            this.field_110699_Q.rotateAngleX = this.head.rotateAngleX;
            this.field_110717_i.rotateAngleY = this.head.rotateAngleY;
            this.field_110700_P.rotateAngleY = this.head.rotateAngleY;
            this.field_110702_R.rotateAngleY = this.head.rotateAngleY;
            this.field_110699_Q.rotateAngleY = this.head.rotateAngleY;
            this.field_110701_S.rotateAngleY = this.head.rotateAngleY;

            if (flag2)
            {
                this.field_110691_L.rotateAngleX = -1.0471976F;
                this.field_110692_M.rotateAngleX = -1.0471976F;
                this.field_110693_N.rotateAngleX = -1.0471976F;
                this.field_110694_O.rotateAngleX = -1.0471976F;
                this.field_110691_L.rotateAngleZ = 0.0F;
                this.field_110692_M.rotateAngleZ = 0.0F;
                this.field_110693_N.rotateAngleZ = 0.0F;
                this.field_110694_O.rotateAngleZ = 0.0F;
            }
            else
            {
                this.field_110691_L.rotateAngleX = f14 / 3.0F;
                this.field_110692_M.rotateAngleX = f14 / 3.0F;
                this.field_110693_N.rotateAngleX = f14 / 3.0F;
                this.field_110694_O.rotateAngleX = f14 / 3.0F;
                this.field_110691_L.rotateAngleZ = f14 / 5.0F;
                this.field_110692_M.rotateAngleZ = f14 / 5.0F;
                this.field_110693_N.rotateAngleZ = -f14 / 5.0F;
                this.field_110694_O.rotateAngleZ = -f14 / 5.0F;
            }
        }

        f15 = -1.3089F + par3 * 1.5F;

        if (f15 > 0.0F)
        {
            f15 = 0.0F;
        }

        if (flag)
        {
            this.tailBase.rotateAngleY = MathHelper.cos(f12 * 0.7F);
            f15 = 0.0F;
        }
        else
        {
            this.tailBase.rotateAngleY = 0.0F;
        }

        this.tailMiddle.rotateAngleY = this.tailBase.rotateAngleY;
        this.tailTip.rotateAngleY = this.tailBase.rotateAngleY;
        this.tailMiddle.rotationPointY = this.tailBase.rotationPointY;
        this.tailTip.rotationPointY = this.tailBase.rotationPointY;
        this.tailMiddle.rotationPointZ = this.tailBase.rotationPointZ;
        this.tailTip.rotationPointZ = this.tailBase.rotationPointZ;
        this.tailBase.rotateAngleX = f15;
        this.tailMiddle.rotateAngleX = f15;
        this.tailTip.rotateAngleX = -0.2618F + f15;
    }
}