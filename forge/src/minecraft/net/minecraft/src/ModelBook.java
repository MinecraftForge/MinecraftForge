package net.minecraft.src;

public class ModelBook extends ModelBase
{
    /** Right cover renderer (when facing the book) */
    public ModelRenderer coverRight = (new ModelRenderer(this)).setTextureOffset(0, 0).addBox(-6.0F, -5.0F, 0.0F, 6, 10, 0);

    /** Left cover renderer (when facing the book) */
    public ModelRenderer coverLeft = (new ModelRenderer(this)).setTextureOffset(16, 0).addBox(0.0F, -5.0F, 0.0F, 6, 10, 0);

    /** The right pages renderer (when facing the book) */
    public ModelRenderer pagesRight = (new ModelRenderer(this)).setTextureOffset(0, 10).addBox(0.0F, -4.0F, -0.99F, 5, 8, 1);

    /** The left pages renderer (when facing the book) */
    public ModelRenderer pagesLeft = (new ModelRenderer(this)).setTextureOffset(12, 10).addBox(0.0F, -4.0F, -0.01F, 5, 8, 1);

    /** Right cover renderer (when facing the book) */
    public ModelRenderer flippingPageRight = (new ModelRenderer(this)).setTextureOffset(24, 10).addBox(0.0F, -4.0F, 0.0F, 5, 8, 0);

    /** Right cover renderer (when facing the book) */
    public ModelRenderer flippingPageLeft = (new ModelRenderer(this)).setTextureOffset(24, 10).addBox(0.0F, -4.0F, 0.0F, 5, 8, 0);

    /** The renderer of spine of the book */
    public ModelRenderer bookSpine = (new ModelRenderer(this)).setTextureOffset(12, 0).addBox(-1.0F, -5.0F, 0.0F, 2, 10, 0);

    public ModelBook()
    {
        this.coverRight.setRotationPoint(0.0F, 0.0F, -1.0F);
        this.coverLeft.setRotationPoint(0.0F, 0.0F, 1.0F);
        this.bookSpine.rotateAngleY = ((float)Math.PI / 2F);
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7);
        this.coverRight.render(par7);
        this.coverLeft.render(par7);
        this.bookSpine.render(par7);
        this.pagesRight.render(par7);
        this.pagesLeft.render(par7);
        this.flippingPageRight.render(par7);
        this.flippingPageLeft.render(par7);
    }

    /**
     * Sets the models various rotation angles.
     */
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6)
    {
        float var7 = (MathHelper.sin(par1 * 0.02F) * 0.1F + 1.25F) * par4;
        this.coverRight.rotateAngleY = (float)Math.PI + var7;
        this.coverLeft.rotateAngleY = -var7;
        this.pagesRight.rotateAngleY = var7;
        this.pagesLeft.rotateAngleY = -var7;
        this.flippingPageRight.rotateAngleY = var7 - var7 * 2.0F * par2;
        this.flippingPageLeft.rotateAngleY = var7 - var7 * 2.0F * par3;
        this.pagesRight.rotationPointX = MathHelper.sin(var7);
        this.pagesLeft.rotationPointX = MathHelper.sin(var7);
        this.flippingPageRight.rotationPointX = MathHelper.sin(var7);
        this.flippingPageLeft.rotationPointX = MathHelper.sin(var7);
    }
}
