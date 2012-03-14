package net.minecraft.src;

public class ModelBoat extends ModelBase
{
    public ModelRenderer[] boatSides = new ModelRenderer[5];

    public ModelBoat()
    {
        this.boatSides[0] = new ModelRenderer(this, 0, 8);
        this.boatSides[1] = new ModelRenderer(this, 0, 0);
        this.boatSides[2] = new ModelRenderer(this, 0, 0);
        this.boatSides[3] = new ModelRenderer(this, 0, 0);
        this.boatSides[4] = new ModelRenderer(this, 0, 0);
        byte var1 = 24;
        byte var2 = 6;
        byte var3 = 20;
        byte var4 = 4;
        this.boatSides[0].addBox((float)(-var1 / 2), (float)(-var3 / 2 + 2), -3.0F, var1, var3 - 4, 4, 0.0F);
        this.boatSides[0].setRotationPoint(0.0F, (float)(0 + var4), 0.0F);
        this.boatSides[1].addBox((float)(-var1 / 2 + 2), (float)(-var2 - 1), -1.0F, var1 - 4, var2, 2, 0.0F);
        this.boatSides[1].setRotationPoint((float)(-var1 / 2 + 1), (float)(0 + var4), 0.0F);
        this.boatSides[2].addBox((float)(-var1 / 2 + 2), (float)(-var2 - 1), -1.0F, var1 - 4, var2, 2, 0.0F);
        this.boatSides[2].setRotationPoint((float)(var1 / 2 - 1), (float)(0 + var4), 0.0F);
        this.boatSides[3].addBox((float)(-var1 / 2 + 2), (float)(-var2 - 1), -1.0F, var1 - 4, var2, 2, 0.0F);
        this.boatSides[3].setRotationPoint(0.0F, (float)(0 + var4), (float)(-var3 / 2 + 1));
        this.boatSides[4].addBox((float)(-var1 / 2 + 2), (float)(-var2 - 1), -1.0F, var1 - 4, var2, 2, 0.0F);
        this.boatSides[4].setRotationPoint(0.0F, (float)(0 + var4), (float)(var3 / 2 - 1));
        this.boatSides[0].rotateAngleX = ((float)Math.PI / 2F);
        this.boatSides[1].rotateAngleY = ((float)Math.PI * 3F / 2F);
        this.boatSides[2].rotateAngleY = ((float)Math.PI / 2F);
        this.boatSides[3].rotateAngleY = (float)Math.PI;
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        for (int var8 = 0; var8 < 5; ++var8)
        {
            this.boatSides[var8].render(par7);
        }
    }

    /**
     * Sets the models various rotation angles.
     */
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6) {}
}
