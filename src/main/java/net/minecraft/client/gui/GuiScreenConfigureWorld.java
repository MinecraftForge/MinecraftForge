package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.mco.GuiScreenBackup;
import net.minecraft.client.gui.mco.GuiScreenResetWorld;
import net.minecraft.client.mco.ExceptionMcoService;
import net.minecraft.client.mco.McoClient;
import net.minecraft.client.mco.McoServer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class GuiScreenConfigureWorld extends GuiScreen
{
    private static final Logger field_146889_a = LogManager.getLogger();
    private final GuiScreen field_146884_f;
    private McoServer field_146885_g;
    private GuiScreenConfigureWorld.SelectionListInvited field_146890_h;
    private int field_146891_i;
    private int field_146897_r;
    private int field_146896_s;
    private int field_146895_t = -1;
    private String field_146894_u;
    private GuiButton field_146893_v;
    private GuiButton field_146892_w;
    private GuiButton field_146900_x;
    private GuiButton field_146899_y;
    private GuiButton field_146898_z;
    private GuiButton field_146886_A;
    private GuiButton field_146887_B;
    private GuiButton field_146888_C;
    private boolean field_146883_D;
    private static final String __OBFID = "CL_00000773";

    public GuiScreenConfigureWorld(GuiScreen par1GuiScreen, McoServer par2McoServer)
    {
        this.field_146884_f = par1GuiScreen;
        this.field_146885_g = par2McoServer;
    }

    // JAVADOC METHOD $$ func_73876_c
    public void updateScreen() {}

    // JAVADOC METHOD $$ func_73866_w_
    public void initGui()
    {
        this.field_146891_i = this.field_146294_l / 2 - 200;
        this.field_146897_r = 180;
        this.field_146896_s = this.field_146294_l / 2;
        Keyboard.enableRepeatEvents(true);
        this.field_146292_n.clear();

        if (this.field_146885_g.field_148808_d.equals("CLOSED"))
        {
            this.field_146292_n.add(this.field_146893_v = new GuiButton(0, this.field_146891_i, this.func_146873_a(12), this.field_146897_r / 2 - 2, 20, I18n.getStringParams("mco.configure.world.buttons.open", new Object[0])));
            this.field_146893_v.field_146124_l = !this.field_146885_g.field_148819_h;
        }
        else
        {
            this.field_146292_n.add(this.field_146892_w = new GuiButton(1, this.field_146891_i, this.func_146873_a(12), this.field_146897_r / 2 - 2, 20, I18n.getStringParams("mco.configure.world.buttons.close", new Object[0])));
            this.field_146892_w.field_146124_l = !this.field_146885_g.field_148819_h;
        }

        this.field_146292_n.add(this.field_146887_B = new GuiButton(7, this.field_146891_i + this.field_146897_r / 2 + 2, this.func_146873_a(12), this.field_146897_r / 2 - 2, 20, I18n.getStringParams("mco.configure.world.buttons.subscription", new Object[0])));
        this.field_146292_n.add(this.field_146900_x = new GuiButton(5, this.field_146891_i, this.func_146873_a(10), this.field_146897_r / 2 - 2, 20, I18n.getStringParams("mco.configure.world.buttons.edit", new Object[0])));
        this.field_146292_n.add(this.field_146899_y = new GuiButton(6, this.field_146891_i + this.field_146897_r / 2 + 2, this.func_146873_a(10), this.field_146897_r / 2 - 2, 20, I18n.getStringParams("mco.configure.world.buttons.reset", new Object[0])));
        this.field_146292_n.add(this.field_146898_z = new GuiButton(4, this.field_146896_s, this.func_146873_a(10), this.field_146897_r / 2 - 2, 20, I18n.getStringParams("mco.configure.world.buttons.invite", new Object[0])));
        this.field_146292_n.add(this.field_146886_A = new GuiButton(3, this.field_146896_s + this.field_146897_r / 2 + 2, this.func_146873_a(10), this.field_146897_r / 2 - 2, 20, I18n.getStringParams("mco.configure.world.buttons.uninvite", new Object[0])));
        this.field_146292_n.add(this.field_146888_C = new GuiButton(8, this.field_146896_s, this.func_146873_a(12), this.field_146897_r / 2 - 2, 20, I18n.getStringParams("mco.configure.world.buttons.backup", new Object[0])));
        this.field_146292_n.add(new GuiButton(10, this.field_146896_s + this.field_146897_r / 2 + 2, this.func_146873_a(12), this.field_146897_r / 2 - 2, 20, I18n.getStringParams("gui.back", new Object[0])));
        this.field_146890_h = new GuiScreenConfigureWorld.SelectionListInvited();
        this.field_146900_x.field_146124_l = !this.field_146885_g.field_148819_h;
        this.field_146899_y.field_146124_l = !this.field_146885_g.field_148819_h;
        this.field_146898_z.field_146124_l = !this.field_146885_g.field_148819_h;
        this.field_146886_A.field_146124_l = !this.field_146885_g.field_148819_h;
        this.field_146888_C.field_146124_l = !this.field_146885_g.field_148819_h;
    }

    private int func_146873_a(int p_146873_1_)
    {
        return 40 + p_146873_1_ * 13;
    }

    public void func_146281_b()
    {
        Keyboard.enableRepeatEvents(false);
    }

    protected void func_146284_a(GuiButton p_146284_1_)
    {
        if (p_146284_1_.field_146124_l)
        {
            if (p_146284_1_.field_146127_k == 10)
            {
                if (this.field_146883_D)
                {
                    ((GuiScreenOnlineServers)this.field_146884_f).func_146670_h();
                }

                this.field_146297_k.func_147108_a(this.field_146884_f);
            }
            else if (p_146284_1_.field_146127_k == 5)
            {
                this.field_146297_k.func_147108_a(new GuiScreenEditOnlineWorld(this, this.field_146884_f, this.field_146885_g));
            }
            else if (p_146284_1_.field_146127_k == 1)
            {
                String s = I18n.getStringParams("mco.configure.world.close.question.line1", new Object[0]);
                String s1 = I18n.getStringParams("mco.configure.world.close.question.line2", new Object[0]);
                this.field_146297_k.func_147108_a(new GuiScreenConfirmation(this, GuiScreenConfirmation.ConfirmationType.Info, s, s1, 1));
            }
            else if (p_146284_1_.field_146127_k == 0)
            {
                this.func_146876_g();
            }
            else if (p_146284_1_.field_146127_k == 4)
            {
                this.field_146297_k.func_147108_a(new GuiScreenInvite(this.field_146884_f, this, this.field_146885_g));
            }
            else if (p_146284_1_.field_146127_k == 3)
            {
                this.func_146877_i();
            }
            else if (p_146284_1_.field_146127_k == 6)
            {
                this.field_146297_k.func_147108_a(new GuiScreenResetWorld(this, this.field_146885_g));
            }
            else if (p_146284_1_.field_146127_k == 7)
            {
                this.field_146297_k.func_147108_a(new GuiScreenSubscription(this, this.field_146885_g));
            }
            else if (p_146284_1_.field_146127_k == 8)
            {
                this.field_146297_k.func_147108_a(new GuiScreenBackup(this, this.field_146885_g.field_148812_a));
            }
        }
    }

    private void func_146876_g()
    {
        Session session = this.field_146297_k.getSession();
        McoClient mcoclient = new McoClient(session.getSessionID(), session.getUsername(), "1.7.2", Minecraft.getMinecraft().getProxy());

        try
        {
            Boolean obool = mcoclient.func_148692_e(this.field_146885_g.field_148812_a);

            if (obool.booleanValue())
            {
                this.field_146883_D = true;
                this.field_146885_g.field_148808_d = "OPEN";
                this.initGui();
            }
        }
        catch (ExceptionMcoService exceptionmcoservice)
        {
            field_146889_a.error("Couldn\'t open world");
        }
        catch (IOException ioexception)
        {
            field_146889_a.error("Could not parse response opening world");
        }
    }

    private void func_146882_h()
    {
        Session session = this.field_146297_k.getSession();
        McoClient mcoclient = new McoClient(session.getSessionID(), session.getUsername(), "1.7.2", Minecraft.getMinecraft().getProxy());

        try
        {
            boolean flag = mcoclient.func_148700_f(this.field_146885_g.field_148812_a).booleanValue();

            if (flag)
            {
                this.field_146883_D = true;
                this.field_146885_g.field_148808_d = "CLOSED";
                this.initGui();
            }
        }
        catch (ExceptionMcoService exceptionmcoservice)
        {
            field_146889_a.error("Couldn\'t close world");
        }
        catch (IOException ioexception)
        {
            field_146889_a.error("Could not parse response closing world");
        }
    }

    private void func_146877_i()
    {
        if (this.field_146895_t >= 0 && this.field_146895_t < this.field_146885_g.field_148806_f.size())
        {
            this.field_146894_u = (String)this.field_146885_g.field_148806_f.get(this.field_146895_t);
            GuiYesNo guiyesno = new GuiYesNo(this, "Question", I18n.getStringParams("mco.configure.world.uninvite.question", new Object[0]) + " \'" + this.field_146894_u + "\'", 3);
            this.field_146297_k.func_147108_a(guiyesno);
        }
    }

    public void confirmClicked(boolean par1, int par2)
    {
        if (par2 == 3)
        {
            if (par1)
            {
                Session session = this.field_146297_k.getSession();
                McoClient mcoclient = new McoClient(session.getSessionID(), session.getUsername(), "1.7.2", Minecraft.getMinecraft().getProxy());

                try
                {
                    mcoclient.func_148694_a(this.field_146885_g.field_148812_a, this.field_146894_u);
                }
                catch (ExceptionMcoService exceptionmcoservice)
                {
                    field_146889_a.error("Couldn\'t uninvite user");
                }

                this.func_146875_d(this.field_146895_t);
            }

            this.field_146297_k.func_147108_a(new GuiScreenConfigureWorld(this.field_146884_f, this.field_146885_g));
        }

        if (par2 == 1)
        {
            if (par1)
            {
                this.func_146882_h();
            }

            this.field_146297_k.func_147108_a(this);
        }
    }

    private void func_146875_d(int p_146875_1_)
    {
        this.field_146885_g.field_148806_f.remove(p_146875_1_);
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
        this.field_146890_h.func_148446_a(par1, par2, par3);
        this.drawCenteredString(this.field_146289_q, I18n.getStringParams("mco.configure.world.title", new Object[0]), this.field_146294_l / 2, 17, 16777215);
        this.drawString(this.field_146289_q, I18n.getStringParams("mco.configure.world.name", new Object[0]), this.field_146891_i, this.func_146873_a(1), 10526880);
        this.drawString(this.field_146289_q, this.field_146885_g.func_148801_b(), this.field_146891_i, this.func_146873_a(2), 16777215);
        this.drawString(this.field_146289_q, I18n.getStringParams("mco.configure.world.description", new Object[0]), this.field_146891_i, this.func_146873_a(4), 10526880);
        this.drawString(this.field_146289_q, this.field_146885_g.func_148800_a(), this.field_146891_i, this.func_146873_a(5), 16777215);
        this.drawString(this.field_146289_q, I18n.getStringParams("mco.configure.world.status", new Object[0]), this.field_146891_i, this.func_146873_a(7), 10526880);
        this.drawString(this.field_146289_q, this.func_146870_p(), this.field_146891_i, this.func_146873_a(8), 16777215);
        this.drawString(this.field_146289_q, I18n.getStringParams("mco.configure.world.invited", new Object[0]), this.field_146896_s, this.func_146873_a(1), 10526880);
        super.drawScreen(par1, par2, par3);
    }

    private String func_146870_p()
    {
        if (this.field_146885_g.field_148819_h)
        {
            return "Expired";
        }
        else
        {
            String s = this.field_146885_g.field_148808_d.toLowerCase();
            return Character.toUpperCase(s.charAt(0)) + s.substring(1);
        }
    }

    @SideOnly(Side.CLIENT)
    class SelectionListInvited extends SelectionListBase
    {
        private static final String __OBFID = "CL_00000775";

        public SelectionListInvited()
        {
            super(GuiScreenConfigureWorld.this.field_146297_k, GuiScreenConfigureWorld.this.field_146896_s, GuiScreenConfigureWorld.this.func_146873_a(2), GuiScreenConfigureWorld.this.field_146897_r, GuiScreenConfigureWorld.this.func_146873_a(9) - GuiScreenConfigureWorld.this.func_146873_a(2), 12);
        }

        protected int func_148443_a()
        {
            return GuiScreenConfigureWorld.this.field_146885_g.field_148806_f.size() + 1;
        }

        protected void func_148449_a(int p_148449_1_, boolean p_148449_2_)
        {
            if (p_148449_1_ < GuiScreenConfigureWorld.this.field_146885_g.field_148806_f.size())
            {
                GuiScreenConfigureWorld.this.field_146895_t = p_148449_1_;
            }
        }

        protected boolean func_148444_a(int p_148444_1_)
        {
            return p_148444_1_ == GuiScreenConfigureWorld.this.field_146895_t;
        }

        protected int func_148447_b()
        {
            return this.func_148443_a() * 12;
        }

        protected void func_148445_c() {}

        protected void func_148442_a(int p_148442_1_, int p_148442_2_, int p_148442_3_, int p_148442_4_, Tessellator p_148442_5_)
        {
            if (p_148442_1_ < GuiScreenConfigureWorld.this.field_146885_g.field_148806_f.size())
            {
                this.func_148463_b(p_148442_1_, p_148442_2_, p_148442_3_, p_148442_4_, p_148442_5_);
            }
        }

        private void func_148463_b(int p_148463_1_, int p_148463_2_, int p_148463_3_, int p_148463_4_, Tessellator p_148463_5_)
        {
            String s = (String)GuiScreenConfigureWorld.this.field_146885_g.field_148806_f.get(p_148463_1_);
            GuiScreenConfigureWorld.this.drawString(GuiScreenConfigureWorld.this.field_146289_q, s, p_148463_2_ + 2, p_148463_3_ + 1, 16777215);
        }
    }
}