package net.minecraft.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class ModelDragon extends ModelBase
{
    // JAVADOC FIELD $$ field_78221_a
    private ModelRenderer head;
    // JAVADOC FIELD $$ field_78219_b
    private ModelRenderer spine;
    // JAVADOC FIELD $$ field_78220_c
    private ModelRenderer jaw;
    // JAVADOC FIELD $$ field_78217_d
    private ModelRenderer body;
    // JAVADOC FIELD $$ field_78218_e
    private ModelRenderer rearLeg;
    // JAVADOC FIELD $$ field_78215_f
    private ModelRenderer frontLeg;
    // JAVADOC FIELD $$ field_78216_g
    private ModelRenderer rearLegTip;
    // JAVADOC FIELD $$ field_78226_h
    private ModelRenderer frontLegTip;
    // JAVADOC FIELD $$ field_78227_i
    private ModelRenderer rearFoot;
    // JAVADOC FIELD $$ field_78224_j
    private ModelRenderer frontFoot;
    // JAVADOC FIELD $$ field_78225_k
    private ModelRenderer wing;
    // JAVADOC FIELD $$ field_78222_l
    private ModelRenderer wingTip;
    private float partialTicks;
    private static final String __OBFID = "CL_00000870";

    public ModelDragon(float par1)
    {
        this.textureWidth = 256;
        this.textureHeight = 256;
        this.setTextureOffset("body.body", 0, 0);
        this.setTextureOffset("wing.skin", -56, 88);
        this.setTextureOffset("wingtip.skin", -56, 144);
        this.setTextureOffset("rearleg.main", 0, 0);
        this.setTextureOffset("rearfoot.main", 112, 0);
        this.setTextureOffset("rearlegtip.main", 196, 0);
        this.setTextureOffset("head.upperhead", 112, 30);
        this.setTextureOffset("wing.bone", 112, 88);
        this.setTextureOffset("head.upperlip", 176, 44);
        this.setTextureOffset("jaw.jaw", 176, 65);
        this.setTextureOffset("frontleg.main", 112, 104);
        this.setTextureOffset("wingtip.bone", 112, 136);
        this.setTextureOffset("frontfoot.main", 144, 104);
        this.setTextureOffset("neck.box", 192, 104);
        this.setTextureOffset("frontlegtip.main", 226, 138);
        this.setTextureOffset("body.scale", 220, 53);
        this.setTextureOffset("head.scale", 0, 0);
        this.setTextureOffset("neck.scale", 48, 0);
        this.setTextureOffset("head.nostril", 112, 0);
        float f1 = -16.0F;
        this.head = new ModelRenderer(this, "head");
        this.head.addBox("upperlip", -6.0F, -1.0F, -8.0F + f1, 12, 5, 16);
        this.head.addBox("upperhead", -8.0F, -8.0F, 6.0F + f1, 16, 16, 16);
        this.head.mirror = true;
        this.head.addBox("scale", -5.0F, -12.0F, 12.0F + f1, 2, 4, 6);
        this.head.addBox("nostril", -5.0F, -3.0F, -6.0F + f1, 2, 2, 4);
        this.head.mirror = false;
        this.head.addBox("scale", 3.0F, -12.0F, 12.0F + f1, 2, 4, 6);
        this.head.addBox("nostril", 3.0F, -3.0F, -6.0F + f1, 2, 2, 4);
        this.jaw = new ModelRenderer(this, "jaw");
        this.jaw.setRotationPoint(0.0F, 4.0F, 8.0F + f1);
        this.jaw.addBox("jaw", -6.0F, 0.0F, -16.0F, 12, 4, 16);
        this.head.addChild(this.jaw);
        this.spine = new ModelRenderer(this, "neck");
        this.spine.addBox("box", -5.0F, -5.0F, -5.0F, 10, 10, 10);
        this.spine.addBox("scale", -1.0F, -9.0F, -3.0F, 2, 4, 6);
        this.body = new ModelRenderer(this, "body");
        this.body.setRotationPoint(0.0F, 4.0F, 8.0F);
        this.body.addBox("body", -12.0F, 0.0F, -16.0F, 24, 24, 64);
        this.body.addBox("scale", -1.0F, -6.0F, -10.0F, 2, 6, 12);
        this.body.addBox("scale", -1.0F, -6.0F, 10.0F, 2, 6, 12);
        this.body.addBox("scale", -1.0F, -6.0F, 30.0F, 2, 6, 12);
        this.wing = new ModelRenderer(this, "wing");
        this.wing.setRotationPoint(-12.0F, 5.0F, 2.0F);
        this.wing.addBox("bone", -56.0F, -4.0F, -4.0F, 56, 8, 8);
        this.wing.addBox("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56);
        this.wingTip = new ModelRenderer(this, "wingtip");
        this.wingTip.setRotationPoint(-56.0F, 0.0F, 0.0F);
        this.wingTip.addBox("bone", -56.0F, -2.0F, -2.0F, 56, 4, 4);
        this.wingTip.addBox("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56);
        this.wing.addChild(this.wingTip);
        this.frontLeg = new ModelRenderer(this, "frontleg");
        this.frontLeg.setRotationPoint(-12.0F, 20.0F, 2.0F);
        this.frontLeg.addBox("main", -4.0F, -4.0F, -4.0F, 8, 24, 8);
        this.frontLegTip = new ModelRenderer(this, "frontlegtip");
        this.frontLegTip.setRotationPoint(0.0F, 20.0F, -1.0F);
        this.frontLegTip.addBox("main", -3.0F, -1.0F, -3.0F, 6, 24, 6);
        this.frontLeg.addChild(this.frontLegTip);
        this.frontFoot = new ModelRenderer(this, "frontfoot");
        this.frontFoot.setRotationPoint(0.0F, 23.0F, 0.0F);
        this.frontFoot.addBox("main", -4.0F, 0.0F, -12.0F, 8, 4, 16);
        this.frontLegTip.addChild(this.frontFoot);
        this.rearLeg = new ModelRenderer(this, "rearleg");
        this.rearLeg.setRotationPoint(-16.0F, 16.0F, 42.0F);
        this.rearLeg.addBox("main", -8.0F, -4.0F, -8.0F, 16, 32, 16);
        this.rearLegTip = new ModelRenderer(this, "rearlegtip");
        this.rearLegTip.setRotationPoint(0.0F, 32.0F, -4.0F);
        this.rearLegTip.addBox("main", -6.0F, -2.0F, 0.0F, 12, 32, 12);
        this.rearLeg.addChild(this.rearLegTip);
        this.rearFoot = new ModelRenderer(this, "rearfoot");
        this.rearFoot.setRotationPoint(0.0F, 31.0F, 4.0F);
        this.rearFoot.addBox("main", -9.0F, 0.0F, -20.0F, 18, 6, 24);
        this.rearLegTip.addChild(this.rearFoot);
    }

    // JAVADOC METHOD $$ func_78086_a
    public void setLivingAnimations(EntityLivingBase par1EntityLivingBase, float par2, float par3, float par4)
    {
        this.partialTicks = par4;
    }

    // JAVADOC METHOD $$ func_78088_a
    public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        GL11.glPushMatrix();
        EntityDragon entitydragon = (EntityDragon)par1Entity;
        float f6 = entitydragon.prevAnimTime + (entitydragon.animTime - entitydragon.prevAnimTime) * this.partialTicks;
        this.jaw.rotateAngleX = (float)(Math.sin((double)(f6 * (float)Math.PI * 2.0F)) + 1.0D) * 0.2F;
        float f7 = (float)(Math.sin((double)(f6 * (float)Math.PI * 2.0F - 1.0F)) + 1.0D);
        f7 = (f7 * f7 * 1.0F + f7 * 2.0F) * 0.05F;
        GL11.glTranslatef(0.0F, f7 - 2.0F, -3.0F);
        GL11.glRotatef(f7 * 2.0F, 1.0F, 0.0F, 0.0F);
        float f8 = -30.0F;
        float f10 = 0.0F;
        float f11 = 1.5F;
        double[] adouble = entitydragon.getMovementOffsets(6, this.partialTicks);
        float f12 = this.updateRotations(entitydragon.getMovementOffsets(5, this.partialTicks)[0] - entitydragon.getMovementOffsets(10, this.partialTicks)[0]);
        float f13 = this.updateRotations(entitydragon.getMovementOffsets(5, this.partialTicks)[0] + (double)(f12 / 2.0F));
        f8 += 2.0F;
        float f14 = f6 * (float)Math.PI * 2.0F;
        f8 = 20.0F;
        float f9 = -12.0F;
        float f15;

        for (int i = 0; i < 5; ++i)
        {
            double[] adouble1 = entitydragon.getMovementOffsets(5 - i, this.partialTicks);
            f15 = (float)Math.cos((double)((float)i * 0.45F + f14)) * 0.15F;
            this.spine.rotateAngleY = this.updateRotations(adouble1[0] - adouble[0]) * (float)Math.PI / 180.0F * f11;
            this.spine.rotateAngleX = f15 + (float)(adouble1[1] - adouble[1]) * (float)Math.PI / 180.0F * f11 * 5.0F;
            this.spine.rotateAngleZ = -this.updateRotations(adouble1[0] - (double)f13) * (float)Math.PI / 180.0F * f11;
            this.spine.rotationPointY = f8;
            this.spine.rotationPointZ = f9;
            this.spine.rotationPointX = f10;
            f8 = (float)((double)f8 + Math.sin((double)this.spine.rotateAngleX) * 10.0D);
            f9 = (float)((double)f9 - Math.cos((double)this.spine.rotateAngleY) * Math.cos((double)this.spine.rotateAngleX) * 10.0D);
            f10 = (float)((double)f10 - Math.sin((double)this.spine.rotateAngleY) * Math.cos((double)this.spine.rotateAngleX) * 10.0D);
            this.spine.render(par7);
        }

        this.head.rotationPointY = f8;
        this.head.rotationPointZ = f9;
        this.head.rotationPointX = f10;
        double[] adouble2 = entitydragon.getMovementOffsets(0, this.partialTicks);
        this.head.rotateAngleY = this.updateRotations(adouble2[0] - adouble[0]) * (float)Math.PI / 180.0F * 1.0F;
        this.head.rotateAngleZ = -this.updateRotations(adouble2[0] - (double)f13) * (float)Math.PI / 180.0F * 1.0F;
        this.head.render(par7);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-f12 * f11 * 1.0F, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef(0.0F, -1.0F, 0.0F);
        this.body.rotateAngleZ = 0.0F;
        this.body.render(par7);

        for (int j = 0; j < 2; ++j)
        {
            GL11.glEnable(GL11.GL_CULL_FACE);
            f15 = f6 * (float)Math.PI * 2.0F;
            this.wing.rotateAngleX = 0.125F - (float)Math.cos((double)f15) * 0.2F;
            this.wing.rotateAngleY = 0.25F;
            this.wing.rotateAngleZ = (float)(Math.sin((double)f15) + 0.125D) * 0.8F;
            this.wingTip.rotateAngleZ = -((float)(Math.sin((double)(f15 + 2.0F)) + 0.5D)) * 0.75F;
            this.rearLeg.rotateAngleX = 1.0F + f7 * 0.1F;
            this.rearLegTip.rotateAngleX = 0.5F + f7 * 0.1F;
            this.rearFoot.rotateAngleX = 0.75F + f7 * 0.1F;
            this.frontLeg.rotateAngleX = 1.3F + f7 * 0.1F;
            this.frontLegTip.rotateAngleX = -0.5F - f7 * 0.1F;
            this.frontFoot.rotateAngleX = 0.75F + f7 * 0.1F;
            this.wing.render(par7);
            this.frontLeg.render(par7);
            this.rearLeg.render(par7);
            GL11.glScalef(-1.0F, 1.0F, 1.0F);

            if (j == 0)
            {
                GL11.glCullFace(GL11.GL_FRONT);
            }
        }

        GL11.glPopMatrix();
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glDisable(GL11.GL_CULL_FACE);
        float f16 = -((float)Math.sin((double)(f6 * (float)Math.PI * 2.0F))) * 0.0F;
        f14 = f6 * (float)Math.PI * 2.0F;
        f8 = 10.0F;
        f9 = 60.0F;
        f10 = 0.0F;
        adouble = entitydragon.getMovementOffsets(11, this.partialTicks);

        for (int k = 0; k < 12; ++k)
        {
            adouble2 = entitydragon.getMovementOffsets(12 + k, this.partialTicks);
            f16 = (float)((double)f16 + Math.sin((double)((float)k * 0.45F + f14)) * 0.05000000074505806D);
            this.spine.rotateAngleY = (this.updateRotations(adouble2[0] - adouble[0]) * f11 + 180.0F) * (float)Math.PI / 180.0F;
            this.spine.rotateAngleX = f16 + (float)(adouble2[1] - adouble[1]) * (float)Math.PI / 180.0F * f11 * 5.0F;
            this.spine.rotateAngleZ = this.updateRotations(adouble2[0] - (double)f13) * (float)Math.PI / 180.0F * f11;
            this.spine.rotationPointY = f8;
            this.spine.rotationPointZ = f9;
            this.spine.rotationPointX = f10;
            f8 = (float)((double)f8 + Math.sin((double)this.spine.rotateAngleX) * 10.0D);
            f9 = (float)((double)f9 - Math.cos((double)this.spine.rotateAngleY) * Math.cos((double)this.spine.rotateAngleX) * 10.0D);
            f10 = (float)((double)f10 - Math.sin((double)this.spine.rotateAngleY) * Math.cos((double)this.spine.rotateAngleX) * 10.0D);
            this.spine.render(par7);
        }

        GL11.glPopMatrix();
    }

    // JAVADOC METHOD $$ func_78214_a
    private float updateRotations(double par1)
    {
        while (par1 >= 180.0D)
        {
            par1 -= 360.0D;
        }

        while (par1 < -180.0D)
        {
            par1 += 360.0D;
        }

        return (float)par1;
    }
}