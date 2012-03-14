package net.minecraft.src;

import java.util.Random;
import org.lwjgl.opengl.GL11;

public class ModelGhast extends ModelBase
{
    ModelRenderer body;
    ModelRenderer[] tentacles = new ModelRenderer[9];

    public ModelGhast()
    {
        byte var1 = -16;
        this.body = new ModelRenderer(this, 0, 0);
        this.body.addBox(-8.0F, -8.0F, -8.0F, 16, 16, 16);
        this.body.rotationPointY += (float)(24 + var1);
        Random var2 = new Random(1660L);

        for (int var3 = 0; var3 < this.tentacles.length; ++var3)
        {
            this.tentacles[var3] = new ModelRenderer(this, 0, 0);
            float var4 = (((float)(var3 % 3) - (float)(var3 / 3 % 2) * 0.5F + 0.25F) / 2.0F * 2.0F - 1.0F) * 5.0F;
            float var5 = ((float)(var3 / 3) / 2.0F * 2.0F - 1.0F) * 5.0F;
            int var6 = var2.nextInt(7) + 8;
            this.tentacles[var3].addBox(-1.0F, 0.0F, -1.0F, 2, var6, 2);
            this.tentacles[var3].rotationPointX = var4;
            this.tentacles[var3].rotationPointZ = var5;
            this.tentacles[var3].rotationPointY = (float)(31 + var1);
        }
    }

    /**
     * Sets the models various rotation angles.
     */
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6)
    {
        for (int var7 = 0; var7 < this.tentacles.length; ++var7)
        {
            this.tentacles[var7].rotateAngleX = 0.2F * MathHelper.sin(par3 * 0.3F + (float)var7) + 0.4F;
        }
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 0.6F, 0.0F);
        this.body.render(par7);

        for (int var8 = 0; var8 < this.tentacles.length; ++var8)
        {
            this.tentacles[var8].render(par7);
        }

        GL11.glPopMatrix();
    }
}
