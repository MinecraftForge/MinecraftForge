package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.net.URI;
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
public class GuiScreenReamlsTOS extends GuiScreen
{
    private static final Logger field_146773_a = LogManager.getLogger();
    private final GuiScreen field_146770_f;
    private final McoServer field_146771_g;
    private GuiButton field_146774_h;
    private boolean field_146775_i = false;
    private String field_146772_r = "https://minecraft.net/realms/terms";
    private static final String __OBFID = "CL_00000809";

    public GuiScreenReamlsTOS(GuiScreen p_i45045_1_, McoServer p_i45045_2_)
    {
        this.field_146770_f = p_i45045_1_;
        this.field_146771_g = p_i45045_2_;
    }

    // JAVADOC METHOD $$ func_73876_c
    public void updateScreen() {}

    // JAVADOC METHOD $$ func_73866_w_
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.field_146292_n.clear();
        int i = this.field_146294_l / 4;
        int j = this.field_146294_l / 4 - 2;
        int k = this.field_146294_l / 2 + 4;
        this.field_146292_n.add(this.field_146774_h = new GuiButton(1, i, this.field_146295_m / 5 + 96 + 22, j, 20, I18n.getStringParams("mco.terms.buttons.agree", new Object[0])));
        this.field_146292_n.add(new GuiButton(2, k, this.field_146295_m / 5 + 96 + 22, j, 20, I18n.getStringParams("mco.terms.buttons.disagree", new Object[0])));
    }

    public void func_146281_b()
    {
        Keyboard.enableRepeatEvents(false);
    }

    protected void func_146284_a(GuiButton p_146284_1_)
    {
        if (p_146284_1_.field_146124_l)
        {
            if (p_146284_1_.field_146127_k == 2)
            {
                this.field_146297_k.func_147108_a(this.field_146770_f);
            }
            else if (p_146284_1_.field_146127_k == 1)
            {
                this.func_146768_g();
            }
        }
    }

    private void func_146768_g()
    {
        Session session = this.field_146297_k.getSession();
        McoClient mcoclient = new McoClient(session.getSessionID(), session.getUsername(), "1.7.2", Minecraft.getMinecraft().getProxy());

        try
        {
            mcoclient.func_148714_h();
            GuiScreenLongRunningTask guiscreenlongrunningtask = new GuiScreenLongRunningTask(this.field_146297_k, this, new TaskOnlineConnect(this, this.field_146771_g));
            guiscreenlongrunningtask.func_146902_g();
            this.field_146297_k.func_147108_a(guiscreenlongrunningtask);
        }
        catch (ExceptionMcoService exceptionmcoservice)
        {
            field_146773_a.error("Couldn\'t agree to TOS");
        }
    }

    // JAVADOC METHOD $$ func_73864_a
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);

        if (this.field_146775_i)
        {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(this.field_146772_r), (ClipboardOwner)null);
            this.func_146769_a(this.field_146772_r);
        }
    }

    private void func_146769_a(String p_146769_1_)
    {
        try
        {
            URI uri = new URI(p_146769_1_);
            Class oclass = Class.forName("java.awt.Desktop");
            Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
            oclass.getMethod("browse", new Class[] {URI.class}).invoke(object, new Object[] {uri});
        }
        catch (Throwable throwable)
        {
            field_146773_a.error("Couldn\'t open link");
        }
    }

    // JAVADOC METHOD $$ func_73863_a
    public void drawScreen(int par1, int par2, float par3)
    {
        this.func_146276_q_();
        this.drawCenteredString(this.field_146289_q, I18n.getStringParams("mco.terms.title", new Object[0]), this.field_146294_l / 2, 17, 16777215);
        this.drawString(this.field_146289_q, I18n.getStringParams("mco.terms.sentence.1", new Object[0]), this.field_146294_l / 2 - 120, 87, 16777215);
        int k = this.field_146289_q.getStringWidth(I18n.getStringParams("mco.terms.sentence.1", new Object[0]));
        int l = 3368635;
        int i1 = 7107012;
        int j1 = this.field_146294_l / 2 - 121 + k;
        byte b0 = 86;
        int k1 = j1 + this.field_146289_q.getStringWidth("mco.terms.sentence.2") + 1;
        int l1 = 87 + this.field_146289_q.FONT_HEIGHT;

        if (j1 <= par1 && par1 <= k1 && b0 <= par2 && par2 <= l1)
        {
            this.field_146775_i = true;
            this.drawString(this.field_146289_q, " " + I18n.getStringParams("mco.terms.sentence.2", new Object[0]), this.field_146294_l / 2 - 120 + k, 87, i1);
        }
        else
        {
            this.field_146775_i = false;
            this.drawString(this.field_146289_q, " " + I18n.getStringParams("mco.terms.sentence.2", new Object[0]), this.field_146294_l / 2 - 120 + k, 87, l);
        }

        super.drawScreen(par1, par2, par3);
    }
}