package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiHopper extends GuiContainer
{
    private static final ResourceLocation field_147085_u = new ResourceLocation("textures/gui/container/hopper.png");
    private IInventory field_147084_v;
    private IInventory field_147083_w;
    private static final String __OBFID = "CL_00000759";

    public GuiHopper(InventoryPlayer par1InventoryPlayer, IInventory par2IInventory)
    {
        super(new ContainerHopper(par1InventoryPlayer, par2IInventory));
        this.field_147084_v = par1InventoryPlayer;
        this.field_147083_w = par2IInventory;
        this.field_146291_p = false;
        this.field_147000_g = 133;
    }

    protected void func_146979_b(int p_146979_1_, int p_146979_2_)
    {
        this.field_146289_q.drawString(this.field_147083_w.func_145818_k_() ? this.field_147083_w.func_145825_b() : I18n.getStringParams(this.field_147083_w.func_145825_b(), new Object[0]), 8, 6, 4210752);
        this.field_146289_q.drawString(this.field_147084_v.func_145818_k_() ? this.field_147084_v.func_145825_b() : I18n.getStringParams(this.field_147084_v.func_145825_b(), new Object[0]), 8, this.field_147000_g - 96 + 2, 4210752);
    }

    protected void func_146976_a(float p_146976_1_, int p_146976_2_, int p_146976_3_)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.field_146297_k.getTextureManager().bindTexture(field_147085_u);
        int k = (this.field_146294_l - this.field_146999_f) / 2;
        int l = (this.field_146295_m - this.field_147000_g) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.field_146999_f, this.field_147000_g);
    }
}