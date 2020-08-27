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

    //Body Parts
	protected final ModelRenderer body;
    protected final ModelRenderer tail;
    protected final ModelRenderer saddle;

	//Neck and Head Parts
    protected final ModelRenderer neck;
    protected final ModelRenderer head;
    protected final ModelRenderer mane;
    protected final ModelRenderer snout;
    protected final ModelRenderer leftEar;
    protected final ModelRenderer rightEar;
    protected final ModelRenderer leftBit;
    protected final ModelRenderer rightBit;
    protected final ModelRenderer leftRein;
    protected final ModelRenderer rightRein;
    protected final ModelRenderer headstall;
    protected final ModelRenderer noseband;
    
    //Leg Parts
    protected final ModelRenderer rightBackLeg;
    protected final ModelRenderer leftBackLeg;
    protected final ModelRenderer rightFrontLeg;
    protected final ModelRenderer leftFrontLeg;
    protected final ModelRenderer rightBackChildLeg;
    protected final ModelRenderer leftBackChildLeg;
    protected final ModelRenderer rightFrontChildLeg;
    protected final ModelRenderer leftFrontChildLeg;

    //Subsets
    private final ModelRenderer[] tack;
    private final ModelRenderer[] reins;

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
        this.neck = new ModelRenderer(this, 0, 35);
        this.neck.addBox(-2.05F, -6.0F, -2.0F, 4.0F, 12.0F, 7.0F);
        this.neck.rotateAngleX = ((float)Math.PI / 6F);
        this.head = new ModelRenderer(this, 0, 13);
        this.head.addBox(-3.0F, -11.0F, -2.0F, 6.0F, 5.0F, 7.0F, modelSizeIn);
        this.mane = new ModelRenderer(this, 56, 36);
        this.mane.addBox(-1.0F, -11.0F, 5.01F, 2.0F, 16.0F, 2.0F, modelSizeIn);
        this.snout = new ModelRenderer(this, 0, 25);
        this.snout.addBox(-2.0F, -11.0F, -7.0F, 4.0F, 5.0F, 5.0F, modelSizeIn);
        this.neck.addChild(this.head);
        this.neck.addChild(this.mane);
        this.neck.addChild(this.snout);
        this.leftEar = new ModelRenderer(this, 19, 16);
        this.leftEar.addBox(0.55F, -13.0F, 4.0F, 2.0F, 3.0F, 1.0F, -0.001F);
        this.rightEar = new ModelRenderer(this, 19, 16);
        this.rightEar.addBox(-2.55F, -13.0F, 4.0F, 2.0F, 3.0F, 1.0F, -0.001F);
        this.neck.addChild(this.leftEar);
        this.neck.addChild(this.rightEar);
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
        this.saddle = new ModelRenderer(this, 26, 0);
        this.saddle.addBox(-5.0F, -8.0F, -9.0F, 10.0F, 9.0F, 9.0F, 0.5F);
        this.body.addChild(this.saddle);
        this.leftBit = new ModelRenderer(this, 29, 5);
        this.leftBit.addBox(2.0F, -9.0F, -6.0F, 1.0F, 2.0F, 2.0F, modelSizeIn);
        this.neck.addChild(this.leftBit);
        this.rightBit = new ModelRenderer(this, 29, 5);
        this.rightBit.addBox(-3.0F, -9.0F, -6.0F, 1.0F, 2.0F, 2.0F, modelSizeIn);
        this.neck.addChild(this.rightBit);
        this.leftRein = new ModelRenderer(this, 32, 2);
        this.leftRein.addBox(3.1F, -6.0F, -8.0F, 0.0F, 3.0F, 16.0F, modelSizeIn);
        this.leftRein.rotateAngleX = (-(float)Math.PI / 6F);
        this.neck.addChild(this.leftRein);
        this.rightRein = new ModelRenderer(this, 32, 2);
        this.rightRein.addBox(-3.1F, -6.0F, -8.0F, 0.0F, 3.0F, 16.0F, modelSizeIn);
        this.rightRein.rotateAngleX = (-(float)Math.PI / 6F);
        this.neck.addChild(this.rightRein);
        this.headstall = new ModelRenderer(this, 1, 1);
        this.headstall.addBox(-3.0F, -11.0F, -1.9F, 6.0F, 5.0F, 6.0F, 0.2F);
        this.neck.addChild(this.headstall);
        this.noseband = new ModelRenderer(this, 19, 0);
        this.noseband.addBox(-2.0F, -11.0F, -4.0F, 4.0F, 5.0F, 2.0F, 0.2F);
        this.neck.addChild(this.noseband);
        this.tack = new ModelRenderer[] {this.saddle, this.leftBit, this.rightBit, this.headstall, this.noseband};
        this.reins = new ModelRenderer[] {this.leftRein, this.rightRein};
    }

    @Override
    protected Iterable<ModelRenderer> getHeadParts()
    {
        return ImmutableList.of(this.neck);
    }

    @Override
    protected Iterable<ModelRenderer> getBodyParts()
    {
    	return ImmutableList.of(this.body, this.rightBackLeg, this.leftBackLeg, this.rightFrontLeg, this.leftFrontLeg, this.rightBackChildLeg, this.leftBackChildLeg, this.rightFrontChildLeg, this.leftFrontChildLeg);
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netneckYaw, float neckPitch)
    {
        boolean saddled = entityIn.isHorseSaddled();
        boolean ridden = entityIn.isBeingRidden();

        for (ModelRenderer saddlePart : this.tack) saddlePart.showModel = saddled;
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
        this.neck.rotationPointY = 4.0F;
        this.neck.rotationPointZ = -12.0F;
        this.body.rotateAngleX = 0.0F;
        this.neck.rotateAngleX = ((float)Math.PI / 6F) + f4;
        this.neck.rotateAngleY = f3 * ((float)Math.PI / 180F);
        float f10 = entityIn.isInWater() ? 0.2F : 1.0F;
        float f11 = MathHelper.cos(f10 * limbSwing * 0.6662F + (float)Math.PI);
        float f12 = f11 * 0.8F * limbSwingAmount;
        float f13 = (1.0F - Math.max(f6, f5)) * (((float)Math.PI / 6F) + f4 + f8 * MathHelper.sin(f9) * 0.05F);
        this.neck.rotateAngleX = f6 * (0.2617994F + f4) + f5 * (2.1816616F + MathHelper.sin(f9) * 0.05F) + f13;
        this.neck.rotateAngleY = f6 * f3 * ((float)Math.PI / 180F) + (1.0F - Math.max(f6, f5)) * this.neck.rotateAngleY;
        this.neck.rotationPointY = f6 * -4.0F + f5 * 11.0F + (1.0F - Math.max(f6, f5)) * this.neck.rotationPointY;
        this.neck.rotationPointZ = f6 * -4.0F + f5 * -12.0F + (1.0F - Math.max(f6, f5)) * this.neck.rotationPointZ;
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
