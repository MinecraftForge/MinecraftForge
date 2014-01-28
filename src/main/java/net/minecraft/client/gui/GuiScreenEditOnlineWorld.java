package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.UnsupportedEncodingException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.mco.GuiScreenResetWorld;
import net.minecraft.client.mco.ExceptionMcoService;
import net.minecraft.client.mco.McoClient;
import net.minecraft.client.mco.McoServer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class GuiScreenEditOnlineWorld extends GuiScreen
{
    private static final Logger field_146862_a = LogManager.getLogger();
    private GuiScreen field_146855_f;
    private GuiScreen field_146857_g;
    private GuiTextField field_146863_h;
    private GuiTextField field_146864_i;
    private McoServer field_146861_r;
    private GuiButton field_146860_s;
    private int field_146859_t;
    private int field_146858_u;
    private int field_146856_v;
    private GuiScreenOnlineServersSubscreen field_146854_w;
    private static final String __OBFID = "CL_00000779";

    public GuiScreenEditOnlineWorld(GuiScreen par1GuiScreen, GuiScreen par2GuiScreen, McoServer par3McoServer)
    {
        this.field_146855_f = par1GuiScreen;
        this.field_146857_g = par2GuiScreen;
        this.field_146861_r = par3McoServer;
    }

    // JAVADOC METHOD $$ func_73876_c
    public void updateScreen()
    {
        this.field_146864_i.func_146178_a();
        this.field_146863_h.func_146178_a();
    }

    // JAVADOC METHOD $$ func_73866_w_
    public void initGui()
    {
        this.field_146859_t = this.field_146294_l / 4;
        this.field_146858_u = this.field_146294_l / 4 - 2;
        this.field_146856_v = this.field_146294_l / 2 + 4;
        Keyboard.enableRepeatEvents(true);
        this.field_146292_n.clear();
        this.field_146292_n.add(this.field_146860_s = new GuiButton(0, this.field_146859_t, this.field_146295_m / 4 + 120 + 22, this.field_146858_u, 20, I18n.getStringParams("mco.configure.world.buttons.done", new Object[0])));
        this.field_146292_n.add(new GuiButton(1, this.field_146856_v, this.field_146295_m / 4 + 120 + 22, this.field_146858_u, 20, I18n.getStringParams("gui.cancel", new Object[0])));
        this.field_146864_i = new GuiTextField(this.field_146289_q, this.field_146859_t, 56, 212, 20);
        this.field_146864_i.func_146195_b(true);
        this.field_146864_i.func_146203_f(32);
        this.field_146864_i.func_146180_a(this.field_146861_r.func_148801_b());
        this.field_146863_h = new GuiTextField(this.field_146289_q, this.field_146859_t, 96, 212, 20);
        this.field_146863_h.func_146203_f(32);
        this.field_146863_h.func_146180_a(this.field_146861_r.func_148800_a());
        this.field_146854_w = new GuiScreenOnlineServersSubscreen(this.field_146294_l, this.field_146295_m, this.field_146859_t, 122, this.field_146861_r.field_148820_i, this.field_146861_r.field_148817_j);
        this.field_146292_n.addAll(this.field_146854_w.field_148405_a);
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
                this.field_146297_k.func_147108_a(this.field_146855_f);
            }
            else if (p_146284_1_.field_146127_k == 0)
            {
                this.func_146853_g();
            }
            else if (p_146284_1_.field_146127_k == 2)
            {
                this.field_146297_k.func_147108_a(new GuiScreenResetWorld(this, this.field_146861_r));
            }
            else
            {
                this.field_146854_w.func_148397_a(p_146284_1_);
            }
        }
    }

    private void func_146853_g()
    {
        Session session = this.field_146297_k.getSession();
        McoClient mcoclient = new McoClient(session.getSessionID(), session.getUsername(), "1.7.2", Minecraft.getMinecraft().getProxy());

        try
        {
            String s = this.field_146863_h.func_146179_b() != null && !this.field_146863_h.func_146179_b().trim().equals("") ? this.field_146863_h.func_146179_b() : null;
            mcoclient.func_148689_a(this.field_146861_r.field_148812_a, this.field_146864_i.func_146179_b(), s, this.field_146854_w.field_148402_e, this.field_146854_w.field_148399_f);
            this.field_146861_r.func_148803_a(this.field_146864_i.func_146179_b());
            this.field_146861_r.func_148804_b(this.field_146863_h.func_146179_b());
            this.field_146861_r.field_148820_i = this.field_146854_w.field_148402_e;
            this.field_146861_r.field_148817_j = this.field_146854_w.field_148399_f;
            this.field_146297_k.func_147108_a(new GuiScreenConfigureWorld(this.field_146857_g, this.field_146861_r));
        }
        catch (ExceptionMcoService exceptionmcoservice)
        {
            field_146862_a.error("Couldn\'t edit world");
        }
        catch (UnsupportedEncodingException unsupportedencodingexception)
        {
            field_146862_a.error("Couldn\'t edit world");
        }
    }

    // JAVADOC METHOD $$ func_73869_a
    protected void keyTyped(char par1, int par2)
    {
        this.field_146864_i.func_146201_a(par1, par2);
        this.field_146863_h.func_146201_a(par1, par2);

        if (par2 == 15)
        {
            this.field_146864_i.func_146195_b(!this.field_146864_i.func_146206_l());
            this.field_146863_h.func_146195_b(!this.field_146863_h.func_146206_l());
        }

        if (par2 == 28 || par2 == 156)
        {
            this.func_146853_g();
        }

        this.field_146860_s.field_146124_l = this.field_146864_i.func_146179_b() != null && !this.field_146864_i.func_146179_b().trim().equals("");
    }

    // JAVADOC METHOD $$ func_73864_a
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
        this.field_146863_h.func_146192_a(par1, par2, par3);
        this.field_146864_i.func_146192_a(par1, par2, par3);
    }

    // JAVADOC METHOD $$ func_73863_a
    public void drawScreen(int par1, int par2, float par3)
    {
        this.func_146276_q_();
        this.drawCenteredString(this.field_146289_q, I18n.getStringParams("mco.configure.world.edit.title", new Object[0]), this.field_146294_l / 2, 17, 16777215);
        this.drawString(this.field_146289_q, I18n.getStringParams("mco.configure.world.name", new Object[0]), this.field_146859_t, 43, 10526880);
        this.drawString(this.field_146289_q, I18n.getStringParams("mco.configure.world.description", new Object[0]), this.field_146859_t, 84, 10526880);
        this.field_146864_i.func_146194_f();
        this.field_146863_h.func_146194_f();
        this.field_146854_w.func_148394_a(this, this.field_146289_q);
        super.drawScreen(par1, par2, par3);
    }
}