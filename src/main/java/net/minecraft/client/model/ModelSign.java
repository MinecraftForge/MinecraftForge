package net.minecraft.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelSign extends ModelBase
{
    // JAVADOC FIELD $$ field_78166_a
    public ModelRenderer signBoard = new ModelRenderer(this, 0, 0);
    // JAVADOC FIELD $$ field_78165_b
    public ModelRenderer signStick;
    private static final String __OBFID = "CL_00000854";

    public ModelSign()
    {
        this.signBoard.addBox(-12.0F, -14.0F, -1.0F, 24, 12, 2, 0.0F);
        this.signStick = new ModelRenderer(this, 0, 14);
        this.signStick.addBox(-1.0F, -2.0F, -1.0F, 2, 14, 2, 0.0F);
    }

    // JAVADOC METHOD $$ func_78164_a
    public void renderSign()
    {
        this.signBoard.render(0.0625F);
        this.signStick.render(0.0625F);
    }
}