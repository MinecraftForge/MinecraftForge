package net.minecraft.src;

public class ChestItemRenderHelper
{
    /** The static instance of ChestItemRenderHelper. */
    public static ChestItemRenderHelper instance = new ChestItemRenderHelper();
    private TileEntityChest field_35610_b = new TileEntityChest();

    public void func_35609_a(Block par1Block, int par2, float par3)
    {
        TileEntityRenderer.instance.renderTileEntityAt(this.field_35610_b, 0.0D, 0.0D, 0.0D, 0.0F);
    }
}
