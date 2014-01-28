package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.mco.ExceptionMcoService;
import net.minecraft.client.mco.McoClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class GuiScreenBuyRealms extends GuiScreen
{
    private static final Logger field_146819_a = LogManager.getLogger();
    private GuiScreen field_146817_f;
    private static int field_146818_g = 111;
    private volatile String field_146820_h = "";
    private static final String __OBFID = "CL_00000770";

    public GuiScreenBuyRealms(GuiScreen p_i45035_1_)
    {
        this.field_146817_f = p_i45035_1_;
    }

    // JAVADOC METHOD $$ func_73876_c
    public void updateScreen() {}

    // JAVADOC METHOD $$ func_73866_w_
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.field_146292_n.clear();
        short short1 = 212;
        this.field_146292_n.add(new GuiButton(field_146818_g, this.field_146294_l / 2 - short1 / 2, 180, short1, 20, I18n.getStringParams("gui.back", new Object[0])));
        this.func_146816_h();
    }

    private void func_146816_h()
    {
        Session session = this.field_146297_k.getSession();
        final McoClient mcoclient = new McoClient(session.getSessionID(), session.getUsername(), "1.7.2", Minecraft.getMinecraft().getProxy());
        (new Thread()
        {
            private static final String __OBFID = "CL_00000771";
            public void run()
            {
                try
                {
                    GuiScreenBuyRealms.this.field_146820_h = mcoclient.func_148690_i();
                }
                catch (ExceptionMcoService exceptionmcoservice)
                {
                    GuiScreenBuyRealms.field_146819_a.error("Could not get stat");
                }
            }
        }).start();
    }

    public void func_146281_b()
    {
        Keyboard.enableRepeatEvents(false);
    }

    protected void func_146284_a(GuiButton p_146284_1_)
    {
        if (p_146284_1_.field_146124_l)
        {
            if (p_146284_1_.field_146127_k == field_146818_g)
            {
                this.field_146297_k.func_147108_a(this.field_146817_f);
            }
        }
    }

    // JAVADOC METHOD $$ func_73869_a
    protected void keyTyped(char par1, int par2) {}

    // JAVADOC METHOD $$ func_73864_a
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
    }

    // JAVADOC METHOD $$ func_73863_a
    public void drawScreen(int par1, int par2, float par3)
    {
        this.func_146276_q_();
        this.drawCenteredString(this.field_146289_q, I18n.getStringParams("mco.buy.realms.title", new Object[0]), this.field_146294_l / 2, 11, 16777215);
        String[] astring = this.field_146820_h.split("\n");
        int k = 52;
        String[] astring1 = astring;
        int l = astring.length;

        for (int i1 = 0; i1 < l; ++i1)
        {
            String s = astring1[i1];
            this.drawCenteredString(this.field_146289_q, s, this.field_146294_l / 2, k, 10526880);
            k += 18;
        }

        super.drawScreen(par1, par2, par3);
    }
}