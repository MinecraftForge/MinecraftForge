package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class GuiRenameWorld extends GuiScreen
{
    private GuiScreen field_146585_a;
    private GuiTextField field_146583_f;
    private final String field_146584_g;
    private static final String __OBFID = "CL_00000709";

    public GuiRenameWorld(GuiScreen par1GuiScreen, String par2Str)
    {
        this.field_146585_a = par1GuiScreen;
        this.field_146584_g = par2Str;
    }

    // JAVADOC METHOD $$ func_73876_c
    public void updateScreen()
    {
        this.field_146583_f.func_146178_a();
    }

    // JAVADOC METHOD $$ func_73866_w_
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.field_146292_n.clear();
        this.field_146292_n.add(new GuiButton(0, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 96 + 12, I18n.getStringParams("selectWorld.renameButton", new Object[0])));
        this.field_146292_n.add(new GuiButton(1, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 120 + 12, I18n.getStringParams("gui.cancel", new Object[0])));
        ISaveFormat isaveformat = this.field_146297_k.getSaveLoader();
        WorldInfo worldinfo = isaveformat.getWorldInfo(this.field_146584_g);
        String s = worldinfo.getWorldName();
        this.field_146583_f = new GuiTextField(this.field_146289_q, this.field_146294_l / 2 - 100, 60, 200, 20);
        this.field_146583_f.func_146195_b(true);
        this.field_146583_f.func_146180_a(s);
    }

    public void func_146281_b()
    {
        Keyboard.enableRepeatEvents(false);
    }

    protected void func_146284_a(GuiButton p_146284_1_)
    {
        if (p_146284_1_.field_146124_l)
        {
            if (p_146284_1_.field_146127_k == 1)
            {
                this.field_146297_k.func_147108_a(this.field_146585_a);
            }
            else if (p_146284_1_.field_146127_k == 0)
            {
                ISaveFormat isaveformat = this.field_146297_k.getSaveLoader();
                isaveformat.renameWorld(this.field_146584_g, this.field_146583_f.func_146179_b().trim());
                this.field_146297_k.func_147108_a(this.field_146585_a);
            }
        }
    }

    // JAVADOC METHOD $$ func_73869_a
    protected void keyTyped(char par1, int par2)
    {
        this.field_146583_f.func_146201_a(par1, par2);
        ((GuiButton)this.field_146292_n.get(0)).field_146124_l = this.field_146583_f.func_146179_b().trim().length() > 0;

        if (par2 == 28 || par2 == 156)
        {
            this.func_146284_a((GuiButton)this.field_146292_n.get(0));
        }
    }

    // JAVADOC METHOD $$ func_73864_a
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
        this.field_146583_f.func_146192_a(par1, par2, par3);
    }

    // JAVADOC METHOD $$ func_73863_a
    public void drawScreen(int par1, int par2, float par3)
    {
        this.func_146276_q_();
        this.drawCenteredString(this.field_146289_q, I18n.getStringParams("selectWorld.renameTitle", new Object[0]), this.field_146294_l / 2, 20, 16777215);
        this.drawString(this.field_146289_q, I18n.getStringParams("selectWorld.enterName", new Object[0]), this.field_146294_l / 2 - 100, 47, 10526880);
        this.field_146583_f.func_146194_f();
        super.drawScreen(par1, par2, par3);
    }
}