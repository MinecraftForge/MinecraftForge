package net.minecraft.src;

import org.lwjgl.opengl.GL11;

public class RenderEnchantmentTable extends TileEntitySpecialRenderer
{
    private ModelBook field_40450_a = new ModelBook();

    public void func_40449_a(TileEntityEnchantmentTable par1TileEntityEnchantmentTable, double par2, double par4, double par6, float par8)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)par2 + 0.5F, (float)par4 + 0.75F, (float)par6 + 0.5F);
        float var9 = (float)par1TileEntityEnchantmentTable.tickCount + par8;
        GL11.glTranslatef(0.0F, 0.1F + MathHelper.sin(var9 * 0.1F) * 0.01F, 0.0F);
        float var10;

        for (var10 = par1TileEntityEnchantmentTable.bookRotation2 - par1TileEntityEnchantmentTable.bookRotationPrev; var10 >= (float)Math.PI; var10 -= ((float)Math.PI * 2F))
        {
            ;
        }

        while (var10 < -(float)Math.PI)
        {
            var10 += ((float)Math.PI * 2F);
        }

        float var11 = par1TileEntityEnchantmentTable.bookRotationPrev + var10 * par8;
        GL11.glRotatef(-var11 * 180.0F / (float)Math.PI, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(80.0F, 0.0F, 0.0F, 1.0F);
        this.bindTextureByName("/item/book.png");
        float var12 = par1TileEntityEnchantmentTable.pageFlipPrev + (par1TileEntityEnchantmentTable.pageFlip - par1TileEntityEnchantmentTable.pageFlipPrev) * par8 + 0.25F;
        float var13 = par1TileEntityEnchantmentTable.pageFlipPrev + (par1TileEntityEnchantmentTable.pageFlip - par1TileEntityEnchantmentTable.pageFlipPrev) * par8 + 0.75F;
        var12 = (var12 - (float)MathHelper.func_40346_b((double)var12)) * 1.6F - 0.3F;
        var13 = (var13 - (float)MathHelper.func_40346_b((double)var13)) * 1.6F - 0.3F;

        if (var12 < 0.0F)
        {
            var12 = 0.0F;
        }

        if (var13 < 0.0F)
        {
            var13 = 0.0F;
        }

        if (var12 > 1.0F)
        {
            var12 = 1.0F;
        }

        if (var13 > 1.0F)
        {
            var13 = 1.0F;
        }

        float var14 = par1TileEntityEnchantmentTable.bookSpreadPrev + (par1TileEntityEnchantmentTable.bookSpread - par1TileEntityEnchantmentTable.bookSpreadPrev) * par8;
        this.field_40450_a.render((Entity)null, var9, var12, var13, var14, 0.0F, 0.0625F);
        GL11.glPopMatrix();
    }

    public void renderTileEntityAt(TileEntity par1TileEntity, double par2, double par4, double par6, float par8)
    {
        this.func_40449_a((TileEntityEnchantmentTable)par1TileEntity, par2, par4, par6, par8);
    }
}
