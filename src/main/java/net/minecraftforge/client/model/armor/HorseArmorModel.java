package net.minecraftforge.client.model.armor;

import java.util.function.Function;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.entity.model.HorseModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

/**
 * An almost carbon copy of {@link HorseModel} 
 * to allow custom models to be applied for 
 * horse armor.
 * */
public class HorseArmorModel<T extends AbstractHorseEntity> extends AgeableModel<T> {

    protected final ModelRenderer body;
    protected final ModelRenderer head;
    protected final ModelRenderer rightBackLeg;
    protected final ModelRenderer leftBackLeg;
    protected final ModelRenderer rightFrontLeg;
    protected final ModelRenderer leftFrontLeg;
    protected final ModelRenderer rightBackChildLeg;
    protected final ModelRenderer leftBackChildLeg;
    protected final ModelRenderer rightFrontChildLeg;
    protected final ModelRenderer leftFrontChildLeg;
    protected final ModelRenderer tail;
    protected final ImmutableList<ModelRenderer> saddle;
    protected final ImmutableList<ModelRenderer> reins;

    public HorseArmorModel(float modelSize)
    {
        this(RenderType::getEntityCutoutNoCull, modelSize, 64, 64);
    }

    public HorseArmorModel(float modelSize, int textureWidth, int textureHeight)
    {
        this(RenderType::getEntityCutoutNoCull, modelSize, textureWidth, textureHeight);
    }

    public HorseArmorModel(Function<ResourceLocation, RenderType> renderTypeIn, float modelSizeIn, int textureWidthIn, int textureHeightIn)
    {
        super(renderTypeIn, true, 16.2F, 1.36F, 2.7272F, 2.0F, 20.0F);
        this.textureWidth = textureWidthIn;
        this.textureHeight = textureHeightIn;
        this.body = new ModelRenderer(this, 0, 32);
        this.body.addBox(-5.0F, -8.0F, -17.0F, 10.0F, 10.0F, 22.0F, 0.05F);
        this.body.setRotationPoint(0.0F, 11.0F, 5.0F);
        this.head = new ModelRenderer(this, 0, 35);
        this.head.addBox(-2.05F, -6.0F, -2.0F, 4.0F, 12.0F, 7.0F);
        this.head.rotateAngleX = ((float)Math.PI / 6F);
        ModelRenderer modelrenderer = new ModelRenderer(this, 0, 13);
        modelrenderer.addBox(-3.0F, -11.0F, -2.0F, 6.0F, 5.0F, 7.0F, modelSizeIn);
        ModelRenderer modelrenderer1 = new ModelRenderer(this, 56, 36);
        modelrenderer1.addBox(-1.0F, -11.0F, 5.01F, 2.0F, 16.0F, 2.0F, modelSizeIn);
        ModelRenderer modelrenderer2 = new ModelRenderer(this, 0, 25);
        modelrenderer2.addBox(-2.0F, -11.0F, -7.0F, 4.0F, 5.0F, 5.0F, modelSizeIn);
        this.head.addChild(modelrenderer);
        this.head.addChild(modelrenderer1);
        this.head.addChild(modelrenderer2);
        ModelRenderer modelrenderer10 = new ModelRenderer(this, 19, 16);
        modelrenderer10.addBox(0.55F, -13.0F, 4.0F, 2.0F, 3.0F, 1.0F, -0.001F);
        ModelRenderer modelrenderer11 = new ModelRenderer(this, 19, 16);
        modelrenderer11.addBox(-2.55F, -13.0F, 4.0F, 2.0F, 3.0F, 1.0F, -0.001F);
        this.head.addChild(modelrenderer10);
        this.head.addChild(modelrenderer11);
        this.rightBackLeg = new ModelRenderer(this, 48, 21);
        this.rightBackLeg.mirror = true;
        this.rightBackLeg.addBox(-3.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, modelSizeIn);
        this.rightBackLeg.setRotationPoint(4.0F, 14.0F, 7.0F);
        this.leftBackLeg = new ModelRenderer(this, 48, 21);
        this.leftBackLeg.addBox(-1.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, modelSizeIn);
        this.leftBackLeg.setRotationPoint(-4.0F, 14.0F, 7.0F);
        this.rightFrontLeg = new ModelRenderer(this, 48, 21);
        this.rightFrontLeg.mirror = true;
        this.rightFrontLeg.addBox(-3.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, modelSizeIn);
        this.rightFrontLeg.setRotationPoint(4.0F, 6.0F, -12.0F);
        this.leftFrontLeg = new ModelRenderer(this, 48, 21);
        this.leftFrontLeg.addBox(-1.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, modelSizeIn);
        this.leftFrontLeg.setRotationPoint(-4.0F, 6.0F, -12.0F);
        this.rightBackChildLeg = new ModelRenderer(this, 48, 21);
        this.rightBackChildLeg.mirror = true;
        this.rightBackChildLeg.addBox(-3.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, modelSizeIn, modelSizeIn + 5.5F, modelSizeIn);
        this.rightBackChildLeg.setRotationPoint(4.0F, 14.0F, 7.0F);
        this.leftBackChildLeg = new ModelRenderer(this, 48, 21);
        this.leftBackChildLeg.addBox(-1.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, modelSizeIn, modelSizeIn + 5.5F, modelSizeIn);
        this.leftBackChildLeg.setRotationPoint(-4.0F, 14.0F, 7.0F);
        this.rightFrontChildLeg = new ModelRenderer(this, 48, 21);
        this.rightFrontChildLeg.mirror = true;
        this.rightFrontChildLeg.addBox(-3.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, modelSizeIn, modelSizeIn + 5.5F, modelSizeIn);
        this.rightFrontChildLeg.setRotationPoint(4.0F, 6.0F, -12.0F);
        this.leftFrontChildLeg = new ModelRenderer(this, 48, 21);
        this.leftFrontChildLeg.addBox(-1.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, modelSizeIn, modelSizeIn + 5.5F, modelSizeIn);
        this.leftFrontChildLeg.setRotationPoint(-4.0F, 6.0F, -12.0F);
        this.tail = new ModelRenderer(this, 42, 36);
        this.tail.addBox(-1.5F, 0.0F, 0.0F, 3.0F, 14.0F, 4.0F, modelSizeIn);
        this.tail.setRotationPoint(0.0F, -5.0F, 2.0F);
        this.tail.rotateAngleX = ((float)Math.PI / 6F);
        this.body.addChild(this.tail);
        ModelRenderer modelrenderer3 = new ModelRenderer(this, 26, 0);
        modelrenderer3.addBox(-5.0F, -8.0F, -9.0F, 10.0F, 9.0F, 9.0F, 0.5F);
        this.body.addChild(modelrenderer3);
        ModelRenderer modelrenderer4 = new ModelRenderer(this, 29, 5);
        modelrenderer4.addBox(2.0F, -9.0F, -6.0F, 1.0F, 2.0F, 2.0F, modelSizeIn);
        this.head.addChild(modelrenderer4);
        ModelRenderer modelrenderer5 = new ModelRenderer(this, 29, 5);
        modelrenderer5.addBox(-3.0F, -9.0F, -6.0F, 1.0F, 2.0F, 2.0F, modelSizeIn);
        this.head.addChild(modelrenderer5);
        ModelRenderer modelrenderer6 = new ModelRenderer(this, 32, 2);
        modelrenderer6.addBox(3.1F, -6.0F, -8.0F, 0.0F, 3.0F, 16.0F, modelSizeIn);
        modelrenderer6.rotateAngleX = (-(float)Math.PI / 6F);
        this.head.addChild(modelrenderer6);
        ModelRenderer modelrenderer7 = new ModelRenderer(this, 32, 2);
        modelrenderer7.addBox(-3.1F, -6.0F, -8.0F, 0.0F, 3.0F, 16.0F, modelSizeIn);
        modelrenderer7.rotateAngleX = (-(float)Math.PI / 6F);
        this.head.addChild(modelrenderer7);
        ModelRenderer modelrenderer8 = new ModelRenderer(this, 1, 1);
        modelrenderer8.addBox(-3.0F, -11.0F, -1.9F, 6.0F, 5.0F, 6.0F, 0.2F);
        this.head.addChild(modelrenderer8);
        ModelRenderer modelrenderer9 = new ModelRenderer(this, 19, 0);
        modelrenderer9.addBox(-2.0F, -11.0F, -4.0F, 4.0F, 5.0F, 2.0F, 0.2F);
        this.head.addChild(modelrenderer9);
        this.saddle = ImmutableList.of(modelrenderer3, modelrenderer4, modelrenderer5, modelrenderer8, modelrenderer9);
        this.reins = ImmutableList.of(modelrenderer6, modelrenderer7);
    }

    @Override
    protected Iterable<ModelRenderer> getHeadParts()
    {
        return ImmutableList.of(this.head);
    }

    @Override
    protected Iterable<ModelRenderer> getBodyParts()
    {
    	return ImmutableList.of(this.body, this.rightBackLeg, this.leftBackLeg, this.rightFrontLeg, this.leftFrontLeg, this.rightBackChildLeg, this.leftBackChildLeg, this.rightFrontChildLeg, this.leftFrontChildLeg);
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        boolean saddled = entityIn.isHorseSaddled();
        boolean ridden = entityIn.isBeingRidden();

        for (ModelRenderer saddlePart : this.saddle) saddlePart.showModel = saddled;
        for (ModelRenderer rein : this.reins) rein.showModel = saddled && ridden;

        this.body.rotationPointY = 11.0F;
    }

    @Override
    public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick)
    {
        super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
        float f = MathHelper.interpolateAngle(partialTick, entityIn.prevRenderYawOffset, entityIn.renderYawOffset);
        float f1 = MathHelper.interpolateAngle(partialTick, entityIn.prevRotationYawHead, entityIn.rotationYawHead);
        float f2 = MathHelper.lerp(partialTick, entityIn.prevRotationPitch, entityIn.rotationPitch);
        float f3 = f1 - f;
        float f4 = f2 * ((float)Math.PI / 180F);
        if (f3 > 20.0F) {
           f3 = 20.0F;
        }

        if (f3 < -20.0F) {
           f3 = -20.0F;
        }

        if (limbSwingAmount > 0.2F) {
           f4 += MathHelper.cos(limbSwing * 0.4F) * 0.15F * limbSwingAmount;
        }

        float f5 = entityIn.getGrassEatingAmount(partialTick);
        float f6 = entityIn.getRearingAmount(partialTick);
        float f7 = 1.0F - f6;
        float f8 = entityIn.getMouthOpennessAngle(partialTick);
        boolean flag = entityIn.tailCounter != 0;
        float f9 = (float)entityIn.ticksExisted + partialTick;
        this.head.rotationPointY = 4.0F;
        this.head.rotationPointZ = -12.0F;
        this.body.rotateAngleX = 0.0F;
        this.head.rotateAngleX = ((float)Math.PI / 6F) + f4;
        this.head.rotateAngleY = f3 * ((float)Math.PI / 180F);
        float f10 = entityIn.isInWater() ? 0.2F : 1.0F;
        float f11 = MathHelper.cos(f10 * limbSwing * 0.6662F + (float)Math.PI);
        float f12 = f11 * 0.8F * limbSwingAmount;
        float f13 = (1.0F - Math.max(f6, f5)) * (((float)Math.PI / 6F) + f4 + f8 * MathHelper.sin(f9) * 0.05F);
        this.head.rotateAngleX = f6 * (0.2617994F + f4) + f5 * (2.1816616F + MathHelper.sin(f9) * 0.05F) + f13;
        this.head.rotateAngleY = f6 * f3 * ((float)Math.PI / 180F) + (1.0F - Math.max(f6, f5)) * this.head.rotateAngleY;
        this.head.rotationPointY = f6 * -4.0F + f5 * 11.0F + (1.0F - Math.max(f6, f5)) * this.head.rotationPointY;
        this.head.rotationPointZ = f6 * -4.0F + f5 * -12.0F + (1.0F - Math.max(f6, f5)) * this.head.rotationPointZ;
        this.body.rotateAngleX = f6 * (-(float)Math.PI / 4F) + f7 * this.body.rotateAngleX;
        float f14 = 0.2617994F * f6;
        float f15 = MathHelper.cos(f9 * 0.6F + (float)Math.PI);
        this.rightFrontLeg.rotationPointY = 2.0F * f6 + 14.0F * f7;
        this.rightFrontLeg.rotationPointZ = -6.0F * f6 - 10.0F * f7;
        this.leftFrontLeg.rotationPointY = this.rightFrontLeg.rotationPointY;
        this.leftFrontLeg.rotationPointZ = this.rightFrontLeg.rotationPointZ;
        float f16 = ((-(float)Math.PI / 3F) + f15) * f6 + f12 * f7;
        float f17 = ((-(float)Math.PI / 3F) - f15) * f6 - f12 * f7;
        this.rightBackLeg.rotateAngleX = f14 - f11 * 0.5F * limbSwingAmount * f7;
        this.leftBackLeg.rotateAngleX = f14 + f11 * 0.5F * limbSwingAmount * f7;
        this.rightFrontLeg.rotateAngleX = f16;
        this.leftFrontLeg.rotateAngleX = f17;
        this.rightBackChildLeg.rotateAngleX = ((float)Math.PI / 6F) + limbSwingAmount * 0.75F;
        this.rightBackChildLeg.rotationPointY = -5.0F + limbSwingAmount;
        this.rightBackChildLeg.rotationPointZ = 2.0F + limbSwingAmount * 2.0F;
        if (flag) {
           this.rightBackChildLeg.rotateAngleY = MathHelper.cos(f9 * 0.7F);
        } else {
           this.rightBackChildLeg.rotateAngleY = 0.0F;
        }

        this.rightBackChildLeg.rotationPointY = this.rightBackLeg.rotationPointY;
        this.rightBackChildLeg.rotationPointZ = this.rightBackLeg.rotationPointZ;
        this.rightBackChildLeg.rotateAngleX = this.rightBackLeg.rotateAngleX;
        this.leftBackChildLeg.rotationPointY = this.leftBackLeg.rotationPointY;
        this.leftBackChildLeg.rotationPointZ = this.leftBackLeg.rotationPointZ;
        this.leftBackChildLeg.rotateAngleX = this.leftBackLeg.rotateAngleX;
        this.rightFrontChildLeg.rotationPointY = this.rightFrontLeg.rotationPointY;
        this.rightFrontChildLeg.rotationPointZ = this.rightFrontLeg.rotationPointZ;
        this.rightFrontChildLeg.rotateAngleX = this.rightFrontLeg.rotateAngleX;
        this.leftFrontChildLeg.rotationPointY = this.leftFrontLeg.rotationPointY;
        this.leftFrontChildLeg.rotationPointZ = this.leftFrontLeg.rotationPointZ;
        this.leftFrontChildLeg.rotateAngleX = this.leftFrontLeg.rotateAngleX;
        boolean flag1 = entityIn.isChild();
        this.rightBackLeg.showModel = !flag1;
        this.leftBackLeg.showModel = !flag1;
        this.rightFrontLeg.showModel = !flag1;
        this.leftFrontLeg.showModel = !flag1;
        this.rightBackChildLeg.showModel = flag1;
        this.leftBackChildLeg.showModel = flag1;
        this.rightFrontChildLeg.showModel = flag1;
        this.leftFrontChildLeg.showModel = flag1;
        this.body.rotationPointY = flag1 ? 10.8F : 0.0F;
    }
}
