package net.minecraft.src;

public class ModelSilverfish extends ModelBase
{
    /** The body parts of the silverfish's model. */
    private ModelRenderer[] silverfishBodyParts = new ModelRenderer[7];

    /** The wings (dust-looking sprites) on the silverfish's model. */
    private ModelRenderer[] silverfishWings;
    private float[] field_35399_c = new float[7];

    /** The widths, heights, and lengths for the silverfish model boxes. */
    private static final int[][] silverfishBoxLength = new int[][] {{3, 2, 2}, {4, 3, 2}, {6, 4, 3}, {3, 3, 3}, {2, 2, 3}, {2, 1, 2}, {1, 1, 2}};

    /** The texture positions for the silverfish's model's boxes. */
    private static final int[][] silverfishTexturePositions = new int[][] {{0, 0}, {0, 4}, {0, 9}, {0, 16}, {0, 22}, {11, 0}, {13, 4}};

    public ModelSilverfish()
    {
        float var1 = -3.5F;

        for (int var2 = 0; var2 < this.silverfishBodyParts.length; ++var2)
        {
            this.silverfishBodyParts[var2] = new ModelRenderer(this, silverfishTexturePositions[var2][0], silverfishTexturePositions[var2][1]);
            this.silverfishBodyParts[var2].addBox((float)silverfishBoxLength[var2][0] * -0.5F, 0.0F, (float)silverfishBoxLength[var2][2] * -0.5F, silverfishBoxLength[var2][0], silverfishBoxLength[var2][1], silverfishBoxLength[var2][2]);
            this.silverfishBodyParts[var2].setRotationPoint(0.0F, (float)(24 - silverfishBoxLength[var2][1]), var1);
            this.field_35399_c[var2] = var1;

            if (var2 < this.silverfishBodyParts.length - 1)
            {
                var1 += (float)(silverfishBoxLength[var2][2] + silverfishBoxLength[var2 + 1][2]) * 0.5F;
            }
        }

        this.silverfishWings = new ModelRenderer[3];
        this.silverfishWings[0] = new ModelRenderer(this, 20, 0);
        this.silverfishWings[0].addBox(-5.0F, 0.0F, (float)silverfishBoxLength[2][2] * -0.5F, 10, 8, silverfishBoxLength[2][2]);
        this.silverfishWings[0].setRotationPoint(0.0F, 16.0F, this.field_35399_c[2]);
        this.silverfishWings[1] = new ModelRenderer(this, 20, 11);
        this.silverfishWings[1].addBox(-3.0F, 0.0F, (float)silverfishBoxLength[4][2] * -0.5F, 6, 4, silverfishBoxLength[4][2]);
        this.silverfishWings[1].setRotationPoint(0.0F, 20.0F, this.field_35399_c[4]);
        this.silverfishWings[2] = new ModelRenderer(this, 20, 18);
        this.silverfishWings[2].addBox(-3.0F, 0.0F, (float)silverfishBoxLength[4][2] * -0.5F, 6, 5, silverfishBoxLength[1][2]);
        this.silverfishWings[2].setRotationPoint(0.0F, 19.0F, this.field_35399_c[1]);
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7);
        int var8;

        for (var8 = 0; var8 < this.silverfishBodyParts.length; ++var8)
        {
            this.silverfishBodyParts[var8].render(par7);
        }

        for (var8 = 0; var8 < this.silverfishWings.length; ++var8)
        {
            this.silverfishWings[var8].render(par7);
        }
    }

    /**
     * Sets the models various rotation angles.
     */
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6)
    {
        for (int var7 = 0; var7 < this.silverfishBodyParts.length; ++var7)
        {
            this.silverfishBodyParts[var7].rotateAngleY = MathHelper.cos(par3 * 0.9F + (float)var7 * 0.15F * (float)Math.PI) * (float)Math.PI * 0.05F * (float)(1 + Math.abs(var7 - 2));
            this.silverfishBodyParts[var7].rotationPointX = MathHelper.sin(par3 * 0.9F + (float)var7 * 0.15F * (float)Math.PI) * (float)Math.PI * 0.2F * (float)Math.abs(var7 - 2);
        }

        this.silverfishWings[0].rotateAngleY = this.silverfishBodyParts[2].rotateAngleY;
        this.silverfishWings[1].rotateAngleY = this.silverfishBodyParts[4].rotateAngleY;
        this.silverfishWings[1].rotationPointX = this.silverfishBodyParts[4].rotationPointX;
        this.silverfishWings[2].rotateAngleY = this.silverfishBodyParts[1].rotateAngleY;
        this.silverfishWings[2].rotationPointX = this.silverfishBodyParts[1].rotationPointX;
    }
}
