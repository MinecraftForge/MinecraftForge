package net.minecraft.src.flora;

import net.minecraft.src.*;

public class WoodWallModel extends ModelBase
{
    ModelRenderer wall1;
    ModelRenderer wall2;
    ModelRenderer wall3;
    ModelRenderer wall4;
    ModelRenderer wall5;
    ModelRenderer wall6;
    ModelRenderer wall7;
    ModelRenderer wall8;

    public WoodWallModel()
    {
        textureWidth = 64;
        textureHeight = 32;
        wall1 = new ModelRenderer(this, 0, 0);
        wall1.addBox(0.0F, 0.0F, 0.0F, 2, 16, 2);
        wall1.setRotationPoint(-8F, 0.0F, 0.0F);
        setRotation(wall1, 0.0F, 0.0F, 0.0F);
        wall2 = new ModelRenderer(this, 8, 0);
        wall2.addBox(0.0F, 0.0F, 0.0F, 2, 14, 2);
        wall2.setRotationPoint(-6F, 2.0F, 0.0F);
        setRotation(wall2, 0.0F, 0.0F, 0.0F);
        wall3 = new ModelRenderer(this, 16, 0);
        wall3.addBox(0.0F, 0.0F, 0.0F, 2, 16, 2);
        wall3.setRotationPoint(-4F, 0.0F, 0.0F);
        setRotation(wall3, 0.0F, 0.0F, 0.0F);
        wall4 = new ModelRenderer(this, 24, 0);
        wall4.addBox(0.0F, 0.0F, 0.0F, 2, 14, 2);
        wall4.setRotationPoint(-2F, 2.0F, 0.0F);
        setRotation(wall4, 0.0F, 0.0F, 0.0F);
        wall5 = new ModelRenderer(this, 32, 0);
        wall5.addBox(0.0F, 0.0F, 0.0F, 2, 16, 2);
        wall5.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotation(wall5, 0.0F, 0.0F, 0.0F);
        wall6 = new ModelRenderer(this, 40, 0);
        wall6.addBox(0.0F, 0.0F, 0.0F, 2, 14, 2);
        wall6.setRotationPoint(2.0F, 2.0F, 0.0F);
        setRotation(wall6, 0.0F, 0.0F, 0.0F);
        wall7 = new ModelRenderer(this, 48, 0);
        wall7.addBox(0.0F, 0.0F, 0.0F, 2, 16, 2);
        wall7.setRotationPoint(4F, 0.0F, 0.0F);
        setRotation(wall7, 0.0F, 0.0F, 0.0F);
        wall8 = new ModelRenderer(this, 56, 0);
        wall8.addBox(0.0F, 0.0F, 0.0F, 2, 14, 2);
        wall8.setRotationPoint(6F, 2.0F, 0.0F);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5);
        wall1.render(f5);
        wall2.render(f5);
        wall3.render(f5);
        wall4.render(f5);
        wall5.render(f5);
        wall6.render(f5);
        wall7.render(f5);
        wall8.render(f5);
    }

    private void setRotation(ModelRenderer modelrenderer, float f, float f1, float f2)
    {
        modelrenderer.rotateAngleX = f;
        modelrenderer.rotateAngleY = f1;
        modelrenderer.rotateAngleZ = f2;
    }

    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5)
    {
        super.setRotationAngles(f, f1, f2, f3, f4, f5);
    }
}
