package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class GuiScreenAddServer extends GuiScreen
{
    private GuiScreen field_146310_a;
    private GuiTextField field_146308_f;
    private GuiTextField field_146309_g;
    private ServerData field_146311_h;
    private static final String __OBFID = "CL_00000695";

    public GuiScreenAddServer(GuiScreen par1GuiScreen, ServerData par2ServerData)
    {
        this.field_146310_a = par1GuiScreen;
        this.field_146311_h = par2ServerData;
    }

    // JAVADOC METHOD $$ func_73876_c
    public void updateScreen()
    {
        this.field_146309_g.func_146178_a();
        this.field_146308_f.func_146178_a();
    }

    // JAVADOC METHOD $$ func_73866_w_
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.field_146292_n.clear();
        this.field_146292_n.add(new GuiButton(0, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 96 + 12, I18n.getStringParams("addServer.add", new Object[0])));
        this.field_146292_n.add(new GuiButton(1, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 120 + 12, I18n.getStringParams("gui.cancel", new Object[0])));
        this.field_146309_g = new GuiTextField(this.field_146289_q, this.field_146294_l / 2 - 100, 66, 200, 20);
        this.field_146309_g.func_146195_b(true);
        this.field_146309_g.func_146180_a(this.field_146311_h.serverName);
        this.field_146308_f = new GuiTextField(this.field_146289_q, this.field_146294_l / 2 - 100, 106, 200, 20);
        this.field_146308_f.func_146203_f(128);
        this.field_146308_f.func_146180_a(this.field_146311_h.serverIP);
        ((GuiButton)this.field_146292_n.get(0)).field_146124_l = this.field_146308_f.func_146179_b().length() > 0 && this.field_146308_f.func_146179_b().split(":").length > 0 && this.field_146309_g.func_146179_b().length() > 0;
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
                this.field_146310_a.confirmClicked(false, 0);
            }
            else if (p_146284_1_.field_146127_k == 0)
            {
                this.field_146311_h.serverName = this.field_146309_g.func_146179_b();
                this.field_146311_h.serverIP = this.field_146308_f.func_146179_b();
                this.field_146310_a.confirmClicked(true, 0);
            }
        }
    }

    // JAVADOC METHOD $$ func_73869_a
    protected void keyTyped(char par1, int par2)
    {
        this.field_146309_g.func_146201_a(par1, par2);
        this.field_146308_f.func_146201_a(par1, par2);

        if (par2 == 15)
        {
            this.field_146309_g.func_146195_b(!this.field_146309_g.func_146206_l());
            this.field_146308_f.func_146195_b(!this.field_146308_f.func_146206_l());
        }

        if (par2 == 28 || par2 == 156)
        {
            this.func_146284_a((GuiButton)this.field_146292_n.get(0));
        }

        ((GuiButton)this.field_146292_n.get(0)).field_146124_l = this.field_146308_f.func_146179_b().length() > 0 && this.field_146308_f.func_146179_b().split(":").length > 0 && this.field_146309_g.func_146179_b().length() > 0;
    }

    // JAVADOC METHOD $$ func_73864_a
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
        this.field_146308_f.func_146192_a(par1, par2, par3);
        this.field_146309_g.func_146192_a(par1, par2, par3);
    }

    // JAVADOC METHOD $$ func_73863_a
    public void drawScreen(int par1, int par2, float par3)
    {
        this.func_146276_q_();
        this.drawCenteredString(this.field_146289_q, I18n.getStringParams("addServer.title", new Object[0]), this.field_146294_l / 2, 17, 16777215);
        this.drawString(this.field_146289_q, I18n.getStringParams("addServer.enterName", new Object[0]), this.field_146294_l / 2 - 100, 53, 10526880);
        this.drawString(this.field_146289_q, I18n.getStringParams("addServer.enterIp", new Object[0]), this.field_146294_l / 2 - 100, 94, 10526880);
        this.field_146309_g.func_146194_f();
        this.field_146308_f.func_146194_f();
        super.drawScreen(par1, par2, par3);
    }
}