package net.minecraft.client.gui.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiFurnace extends GuiContainer
{
    private static final ResourceLocation field_147087_u = new ResourceLocation("textures/gui/container/furnace.png");
    private TileEntityFurnace field_147086_v;
    private static final String __OBFID = "CL_00000758";

    public GuiFurnace(InventoryPlayer par1InventoryPlayer, TileEntityFurnace par2TileEntityFurnace)
    {
        super(new ContainerFurnace(par1InventoryPlayer, par2TileEntityFurnace));
        this.field_147086_v = par2TileEntityFurnace;
    }

    protected void func_146979_b(int p_146979_1_, int p_146979_2_)
    {
        String s = this.field_147086_v.func_145818_k_() ? this.field_147086_v.func_145825_b() : I18n.getStringParams(this.field_147086_v.func_145825_b(), new Object[0]);
        this.field_146289_q.drawString(s, this.field_146999_f / 2 - this.field_146289_q.getStringWidth(s) / 2, 6, 4210752);
        this.field_146289_q.drawString(I18n.getStringParams("container.inventory", new Object[0]), 8, this.field_147000_g - 96 + 2, 4210752);
    }

    protected void func_146976_a(float p_146976_1_, int p_146976_2_, int p_146976_3_)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.field_146297_k.getTextureManager().bindTexture(field_147087_u);
        int k = (this.field_146294_l - this.field_146999_f) / 2;
        int l = (this.field_146295_m - this.field_147000_g) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.field_146999_f, this.field_147000_g);
        int i1;

        if (this.field_147086_v.func_145950_i())
        {
            i1 = this.field_147086_v.func_145955_e(12);
            this.drawTexturedModalRect(k + 56, l + 36 + 12 - i1, 176, 12 - i1, 14, i1 + 2);
        }

        i1 = this.field_147086_v.func_145953_d(24);
        this.drawTexturedModalRect(k + 79, l + 34, 176, 14, i1 + 1, 16);
    }
}