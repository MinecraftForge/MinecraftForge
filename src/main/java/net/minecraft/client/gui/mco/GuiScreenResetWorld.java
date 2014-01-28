package net.minecraft.client.gui.mco;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenConfirmation;
import net.minecraft.client.gui.GuiScreenLongRunningTask;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.TaskLongRunning;
import net.minecraft.client.mco.ExceptionMcoService;
import net.minecraft.client.mco.McoClient;
import net.minecraft.client.mco.McoServer;
import net.minecraft.client.mco.WorldTemplate;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class GuiScreenResetWorld extends ScreenWithCallback
{
    private static final Logger field_146748_a = LogManager.getLogger();
    private GuiScreen field_146742_f;
    private McoServer field_146743_g;
    private GuiTextField field_146749_h;
    private final int field_146750_i = 1;
    private final int field_146747_r = 2;
    private static int field_146746_s = 3;
    private WorldTemplate field_146745_t;
    private GuiButton field_146744_u;
    private static final String __OBFID = "CL_00000810";

    public GuiScreenResetWorld(GuiScreen par1GuiScreen, McoServer par2McoServer)
    {
        this.field_146742_f = par1GuiScreen;
        this.field_146743_g = par2McoServer;
    }

    // JAVADOC METHOD $$ func_73876_c
    public void updateScreen()
    {
        this.field_146749_h.func_146178_a();
    }

    // JAVADOC METHOD $$ func_73866_w_
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.field_146292_n.clear();
        this.field_146292_n.add(this.field_146744_u = new GuiButton(1, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 120 + 12, 97, 20, I18n.getStringParams("mco.configure.world.buttons.reset", new Object[0])));
        this.field_146292_n.add(new GuiButton(2, this.field_146294_l / 2 + 5, this.field_146295_m / 4 + 120 + 12, 97, 20, I18n.getStringParams("gui.cancel", new Object[0])));
        this.field_146749_h = new GuiTextField(this.field_146289_q, this.field_146294_l / 2 - 100, 99, 200, 20);
        this.field_146749_h.func_146195_b(true);
        this.field_146749_h.func_146203_f(32);
        this.field_146749_h.func_146180_a("");

        if (this.field_146745_t == null)
        {
            this.field_146292_n.add(new GuiButton(field_146746_s, this.field_146294_l / 2 - 100, 125, 200, 20, I18n.getStringParams("mco.template.default.name", new Object[0])));
        }
        else
        {
            this.field_146749_h.func_146180_a("");
            this.field_146749_h.func_146184_c(false);
            this.field_146749_h.func_146195_b(false);
            this.field_146292_n.add(new GuiButton(field_146746_s, this.field_146294_l / 2 - 100, 125, 200, 20, I18n.getStringParams("mco.template.name", new Object[0]) + ": " + this.field_146745_t.field_148785_b));
        }
    }

    public void func_146281_b()
    {
        Keyboard.enableRepeatEvents(false);
    }

    // JAVADOC METHOD $$ func_73869_a
    protected void keyTyped(char par1, int par2)
    {
        this.field_146749_h.func_146201_a(par1, par2);

        if (par2 == 28 || par2 == 156)
        {
            this.func_146284_a(this.field_146744_u);
        }
    }

    protected void func_146284_a(GuiButton p_146284_1_)
    {
        if (p_146284_1_.field_146124_l)
        {
            if (p_146284_1_.field_146127_k == 2)
            {
                this.field_146297_k.func_147108_a(this.field_146742_f);
            }
            else if (p_146284_1_.field_146127_k == 1)
            {
                String s = I18n.getStringParams("mco.configure.world.reset.question.line1", new Object[0]);
                String s1 = I18n.getStringParams("mco.configure.world.reset.question.line2", new Object[0]);
                this.field_146297_k.func_147108_a(new GuiScreenConfirmation(this, GuiScreenConfirmation.ConfirmationType.Warning, s, s1, 1));
            }
            else if (p_146284_1_.field_146127_k == field_146746_s)
            {
                this.field_146297_k.func_147108_a(new GuiScreenMcoWorldTemplate(this, this.field_146745_t));
            }
        }
    }

    public void confirmClicked(boolean par1, int par2)
    {
        if (par1 && par2 == 1)
        {
            this.func_146741_h();
        }
        else
        {
            this.field_146297_k.func_147108_a(this);
        }
    }

    private void func_146741_h()
    {
        GuiScreenResetWorld.ResetWorldTask resetworldtask = new GuiScreenResetWorld.ResetWorldTask(this.field_146743_g.field_148812_a, this.field_146749_h.func_146179_b(), this.field_146745_t);
        GuiScreenLongRunningTask guiscreenlongrunningtask = new GuiScreenLongRunningTask(this.field_146297_k, this.field_146742_f, resetworldtask);
        guiscreenlongrunningtask.func_146902_g();
        this.field_146297_k.func_147108_a(guiscreenlongrunningtask);
    }

    // JAVADOC METHOD $$ func_73864_a
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
        this.field_146749_h.func_146192_a(par1, par2, par3);
    }

    // JAVADOC METHOD $$ func_73863_a
    public void drawScreen(int par1, int par2, float par3)
    {
        this.func_146276_q_();
        this.drawCenteredString(this.field_146289_q, I18n.getStringParams("mco.reset.world.title", new Object[0]), this.field_146294_l / 2, 17, 16777215);
        this.drawCenteredString(this.field_146289_q, I18n.getStringParams("mco.reset.world.warning", new Object[0]), this.field_146294_l / 2, 56, 16711680);
        this.drawString(this.field_146289_q, I18n.getStringParams("mco.reset.world.seed", new Object[0]), this.field_146294_l / 2 - 100, 86, 10526880);
        this.field_146749_h.func_146194_f();
        super.drawScreen(par1, par2, par3);
    }

    void func_146740_a(WorldTemplate p_146740_1_)
    {
        this.field_146745_t = p_146740_1_;
    }

    void func_146735_a(Object p_146735_1_)
    {
        this.func_146740_a((WorldTemplate)p_146735_1_);
    }

    @SideOnly(Side.CLIENT)
    class ResetWorldTask extends TaskLongRunning
    {
        private final long field_148422_c;
        private final String field_148420_d;
        private final WorldTemplate field_148421_e;
        private static final String __OBFID = "CL_00000811";

        public ResetWorldTask(long par2, String par4Str, WorldTemplate par5WorldTemplate)
        {
            this.field_148422_c = par2;
            this.field_148420_d = par4Str;
            this.field_148421_e = par5WorldTemplate;
        }

        public void run()
        {
            Session session = GuiScreenResetWorld.this.field_146297_k.getSession();
            McoClient mcoclient = new McoClient(session.getSessionID(), session.getUsername(), "1.7.2", Minecraft.getMinecraft().getProxy());
            String s = I18n.getStringParams("mco.reset.world.resetting.screen.title", new Object[0]);
            this.func_148417_b(s);

            try
            {
                if (this.func_148418_c())
                {
                    return;
                }

                if (this.field_148421_e != null)
                {
                    mcoclient.func_148696_e(this.field_148422_c, this.field_148421_e.field_148787_a);
                }
                else
                {
                    mcoclient.func_148699_d(this.field_148422_c, this.field_148420_d);
                }

                if (this.func_148418_c())
                {
                    return;
                }

                GuiScreenResetWorld.this.field_146297_k.func_147108_a(GuiScreenResetWorld.this.field_146742_f);
            }
            catch (ExceptionMcoService exceptionmcoservice)
            {
                if (this.func_148418_c())
                {
                    return;
                }

                GuiScreenResetWorld.field_146748_a.error("Couldn\'t reset world");
                this.func_148416_a(exceptionmcoservice.toString());
            }
            catch (Exception exception)
            {
                if (this.func_148418_c())
                {
                    return;
                }

                GuiScreenResetWorld.field_146748_a.error("Couldn\'t reset world");
                this.func_148416_a(exception.toString());
            }
        }
    }
}