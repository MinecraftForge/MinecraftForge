package net.minecraft.client.gui.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiChest extends GuiContainer
{
    private static final ResourceLocation field_147017_u = new ResourceLocation("textures/gui/container/generic_54.png");
    private IInventory field_147016_v;
    private IInventory field_147015_w;
    private int field_147018_x;
    private static final String __OBFID = "CL_00000749";

    public GuiChest(IInventory par1IInventory, IInventory par2IInventory)
    {
        super(new ContainerChest(par1IInventory, par2IInventory));
        this.field_147016_v = par1IInventory;
        this.field_147015_w = par2IInventory;
        this.field_146291_p = false;
        short short1 = 222;
        int i = short1 - 108;
        this.field_147018_x = par2IInventory.getSizeInventory() / 9;
        this.field_147000_g = i + this.field_147018_x * 18;
    }

    protected void func_146979_b(int p_146979_1_, int p_146979_2_)
    {
        this.field_146289_q.drawString(this.field_147015_w.func_145818_k_() ? this.field_147015_w.func_145825_b() : I18n.getStringParams(this.field_147015_w.func_145825_b(), new Object[0]), 8, 6, 4210752);
        this.field_146289_q.drawString(this.field_147016_v.func_145818_k_() ? this.field_147016_v.func_145825_b() : I18n.getStringParams(this.field_147016_v.func_145825_b(), new Object[0]), 8, this.field_147000_g - 96 + 2, 4210752);
    }

    protected void func_146976_a(float p_146976_1_, int p_146976_2_, int p_146976_3_)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.field_146297_k.getTextureManager().bindTexture(field_147017_u);
        int k = (this.field_146294_l - this.field_146999_f) / 2;
        int l = (this.field_146295_m - this.field_147000_g) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.field_146999_f, this.field_147018_x * 18 + 17);
        this.drawTexturedModalRect(k, l + this.field_147018_x * 18 + 17, 0, 126, this.field_146999_f, 96);
    }
}