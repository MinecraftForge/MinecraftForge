package net.minecraft.client.renderer.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public abstract class TileEntitySpecialRenderer
{
    protected TileEntityRendererDispatcher field_147501_a;
    private static final String __OBFID = "CL_00000964";

    public abstract void func_147500_a(TileEntity var1, double var2, double var4, double var6, float var8);

    protected void func_147499_a(ResourceLocation p_147499_1_)
    {
        TextureManager texturemanager = this.field_147501_a.field_147553_e;

        if (texturemanager != null)
        {
            texturemanager.bindTexture(p_147499_1_);
        }
    }

    public void func_147497_a(TileEntityRendererDispatcher p_147497_1_)
    {
        this.field_147501_a = p_147497_1_;
    }

    public void func_147496_a(World p_147496_1_) {}

    public FontRenderer func_147498_b()
    {
        return this.field_147501_a.func_147548_a();
    }
}