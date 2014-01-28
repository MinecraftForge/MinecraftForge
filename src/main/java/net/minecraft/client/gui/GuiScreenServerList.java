package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class GuiScreenServerList extends GuiScreen
{
    private final GuiScreen field_146303_a;
    private final ServerData field_146301_f;
    private GuiTextField field_146302_g;
    private static final String __OBFID = "CL_00000692";

    public GuiScreenServerList(GuiScreen par1GuiScreen, ServerData par2ServerData)
    {
        this.field_146303_a = par1GuiScreen;
        this.field_146301_f = par2ServerData;
    }

    // JAVADOC METHOD $$ func_73876_c
    public void updateScreen()
    {
        this.field_146302_g.func_146178_a();
    }

    // JAVADOC METHOD $$ func_73866_w_
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.field_146292_n.clear();
        this.field_146292_n.add(new GuiButton(0, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 96 + 12, I18n.getStringParams("selectServer.select", new Object[0])));
        this.field_146292_n.add(new GuiButton(1, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 120 + 12, I18n.getStringParams("gui.cancel", new Object[0])));
        this.field_146302_g = new GuiTextField(this.field_146289_q, this.field_146294_l / 2 - 100, 116, 200, 20);
        this.field_146302_g.func_146203_f(128);
        this.field_146302_g.func_146195_b(true);
        this.field_146302_g.func_146180_a(this.field_146297_k.gameSettings.lastServer);
        ((GuiButton)this.field_146292_n.get(0)).field_146124_l = this.field_146302_g.func_146179_b().length() > 0 && this.field_146302_g.func_146179_b().split(":").length > 0;
    }

    public void func_146281_b()
    {
        Keyboard.enableRepeatEvents(false);
        this.field_146297_k.gameSettings.lastServer = this.field_146302_g.func_146179_b();
        this.field_146297_k.gameSettings.saveOptions();
    }

    protected void func_146284_a(GuiButton p_146284_1_)
    {
        if (p_146284_1_.field_146124_l)
        {
            if (p_146284_1_.field_146127_k == 1)
            {
                this.field_146303_a.confirmClicked(false, 0);
            }
            else if (p_146284_1_.field_146127_k == 0)
            {
                this.field_146301_f.serverIP = this.field_146302_g.func_146179_b();
                this.field_146303_a.confirmClicked(true, 0);
            }
        }
    }

    // JAVADOC METHOD $$ func_73869_a
    protected void keyTyped(char par1, int par2)
    {
        if (this.field_146302_g.func_146201_a(par1, par2))
        {
            ((GuiButton)this.field_146292_n.get(0)).field_146124_l = this.field_146302_g.func_146179_b().length() > 0 && this.field_146302_g.func_146179_b().split(":").length > 0;
        }
        else if (par2 == 28 || par2 == 156)
        {
            this.func_146284_a((GuiButton)this.field_146292_n.get(0));
        }
    }

    // JAVADOC METHOD $$ func_73864_a
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
        this.field_146302_g.func_146192_a(par1, par2, par3);
    }

    // JAVADOC METHOD $$ func_73863_a
    public void drawScreen(int par1, int par2, float par3)
    {
        this.func_146276_q_();
        this.drawCenteredString(this.field_146289_q, I18n.getStringParams("selectServer.direct", new Object[0]), this.field_146294_l / 2, 20, 16777215);
        this.drawString(this.field_146289_q, I18n.getStringParams("addServer.enterIp", new Object[0]), this.field_146294_l / 2 - 100, 100, 10526880);
        this.field_146302_g.func_146194_f();
        super.drawScreen(par1, par2, par3);
    }
}