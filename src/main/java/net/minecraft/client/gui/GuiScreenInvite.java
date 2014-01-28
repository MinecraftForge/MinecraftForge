package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.mco.ExceptionMcoService;
import net.minecraft.client.mco.McoClient;
import net.minecraft.client.mco.McoServer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class GuiScreenInvite extends GuiScreen
{
    private static final Logger field_146928_a = LogManager.getLogger();
    private GuiTextField field_146921_f;
    private McoServer field_146923_g;
    private final GuiScreen field_146929_h;
    private final GuiScreenConfigureWorld field_146930_i;
    private final int field_146927_r = 0;
    private final int field_146926_s = 1;
    private String field_146925_t = "Could not invite the provided name";
    private String field_146924_u;
    private boolean field_146922_v;
    private static final String __OBFID = "CL_00000780";

    public GuiScreenInvite(GuiScreen par1GuiScreen, GuiScreenConfigureWorld par2GuiScreenConfigureWorld, McoServer par3McoServer)
    {
        this.field_146929_h = par1GuiScreen;
        this.field_146930_i = par2GuiScreenConfigureWorld;
        this.field_146923_g = par3McoServer;
    }

    // JAVADOC METHOD $$ func_73876_c
    public void updateScreen()
    {
        this.field_146921_f.func_146178_a();
    }

    // JAVADOC METHOD $$ func_73866_w_
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.field_146292_n.clear();
        this.field_146292_n.add(new GuiButton(0, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 96 + 12, I18n.getStringParams("mco.configure.world.buttons.invite", new Object[0])));
        this.field_146292_n.add(new GuiButton(1, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 120 + 12, I18n.getStringParams("gui.cancel", new Object[0])));
        this.field_146921_f = new GuiTextField(this.field_146289_q, this.field_146294_l / 2 - 100, 66, 200, 20);
        this.field_146921_f.func_146195_b(true);
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
                this.field_146297_k.func_147108_a(this.field_146930_i);
            }
            else if (p_146284_1_.field_146127_k == 0)
            {
                Session session = this.field_146297_k.getSession();
                McoClient mcoclient = new McoClient(session.getSessionID(), session.getUsername(), "1.7.2", Minecraft.getMinecraft().getProxy());

                if (this.field_146921_f.func_146179_b() == null || this.field_146921_f.func_146179_b().isEmpty())
                {
                    return;
                }

                try
                {
                    McoServer mcoserver = mcoclient.func_148697_b(this.field_146923_g.field_148812_a, this.field_146921_f.func_146179_b());

                    if (mcoserver != null)
                    {
                        this.field_146923_g.field_148806_f = mcoserver.field_148806_f;
                        this.field_146297_k.func_147108_a(new GuiScreenConfigureWorld(this.field_146929_h, this.field_146923_g));
                    }
                    else
                    {
                        this.func_146920_a(this.field_146925_t);
                    }
                }
                catch (ExceptionMcoService exceptionmcoservice)
                {
                    field_146928_a.error("Couldn\'t invite user");
                    this.func_146920_a(exceptionmcoservice.field_148829_b);
                }
                catch (IOException ioexception)
                {
                    field_146928_a.error("Couldn\'t parse response inviting user", ioexception);
                    this.func_146920_a(this.field_146925_t);
                }
            }
        }
    }

    private void func_146920_a(String p_146920_1_)
    {
        this.field_146922_v = true;
        this.field_146924_u = p_146920_1_;
    }

    // JAVADOC METHOD $$ func_73869_a
    protected void keyTyped(char par1, int par2)
    {
        this.field_146921_f.func_146201_a(par1, par2);

        if (par2 == 15)
        {
            if (this.field_146921_f.func_146206_l())
            {
                this.field_146921_f.func_146195_b(false);
            }
            else
            {
                this.field_146921_f.func_146195_b(true);
            }
        }

        if (par2 == 28 || par2 == 156)
        {
            this.func_146284_a((GuiButton)this.field_146292_n.get(0));
        }
    }

    // JAVADOC METHOD $$ func_73864_a
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
        this.field_146921_f.func_146192_a(par1, par2, par3);
    }

    // JAVADOC METHOD $$ func_73863_a
    public void drawScreen(int par1, int par2, float par3)
    {
        this.func_146276_q_();
        this.drawString(this.field_146289_q, I18n.getStringParams("mco.configure.world.invite.profile.name", new Object[0]), this.field_146294_l / 2 - 100, 53, 10526880);

        if (this.field_146922_v)
        {
            this.drawCenteredString(this.field_146289_q, this.field_146924_u, this.field_146294_l / 2, 100, 16711680);
        }

        this.field_146921_f.func_146194_f();
        super.drawScreen(par1, par2, par3);
    }
}