package net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderEnchantmentTable extends TileEntitySpecialRenderer
{
    private static final ResourceLocation field_147540_b = new ResourceLocation("textures/entity/enchanting_table_book.png");
    private ModelBook field_147541_c = new ModelBook();
    private static final String __OBFID = "CL_00000966";

    public void func_147500_a(TileEntityEnchantmentTable p_147539_1_, double p_147539_2_, double p_147539_4_, double p_147539_6_, float p_147539_8_)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)p_147539_2_ + 0.5F, (float)p_147539_4_ + 0.75F, (float)p_147539_6_ + 0.5F);
        float f1 = (float)p_147539_1_.field_145926_a + p_147539_8_;
        GL11.glTranslatef(0.0F, 0.1F + MathHelper.sin(f1 * 0.1F) * 0.01F, 0.0F);
        float f2;

        for (f2 = p_147539_1_.field_145928_o - p_147539_1_.field_145925_p; f2 >= (float)Math.PI; f2 -= ((float)Math.PI * 2F))
        {
            ;
        }

        while (f2 < -(float)Math.PI)
        {
            f2 += ((float)Math.PI * 2F);
        }

        float f3 = p_147539_1_.field_145925_p + f2 * p_147539_8_;
        GL11.glRotatef(-f3 * 180.0F / (float)Math.PI, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(80.0F, 0.0F, 0.0F, 1.0F);
        this.func_147499_a(field_147540_b);
        float f4 = p_147539_1_.field_145931_j + (p_147539_1_.field_145933_i - p_147539_1_.field_145931_j) * p_147539_8_ + 0.25F;
        float f5 = p_147539_1_.field_145931_j + (p_147539_1_.field_145933_i - p_147539_1_.field_145931_j) * p_147539_8_ + 0.75F;
        f4 = (f4 - (float)MathHelper.truncateDoubleToInt((double)f4)) * 1.6F - 0.3F;
        f5 = (f5 - (float)MathHelper.truncateDoubleToInt((double)f5)) * 1.6F - 0.3F;

        if (f4 < 0.0F)
        {
            f4 = 0.0F;
        }

        if (f5 < 0.0F)
        {
            f5 = 0.0F;
        }

        if (f4 > 1.0F)
        {
            f4 = 1.0F;
        }

        if (f5 > 1.0F)
        {
            f5 = 1.0F;
        }

        float f6 = p_147539_1_.field_145927_n + (p_147539_1_.field_145930_m - p_147539_1_.field_145927_n) * p_147539_8_;
        GL11.glEnable(GL11.GL_CULL_FACE);
        this.field_147541_c.render((Entity)null, f1, f4, f5, f6, 0.0F, 0.0625F);
        GL11.glPopMatrix();
    }

    public void func_147500_a(TileEntity p_147500_1_, double p_147500_2_, double p_147500_4_, double p_147500_6_, float p_147500_8_)
    {
        this.func_147500_a((TileEntityEnchantmentTable)p_147500_1_, p_147500_2_, p_147500_4_, p_147500_6_, p_147500_8_);
    }
}