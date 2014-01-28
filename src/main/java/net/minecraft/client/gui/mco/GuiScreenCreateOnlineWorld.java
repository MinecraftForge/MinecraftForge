package net.minecraft.client.gui.mco;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenLongRunningTask;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.TaskLongRunning;
import net.minecraft.client.mco.ExceptionMcoService;
import net.minecraft.client.mco.McoClient;
import net.minecraft.client.mco.WorldTemplate;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class GuiScreenCreateOnlineWorld extends ScreenWithCallback
{
    private static final Logger field_146765_a = LogManager.getLogger();
    private GuiScreen field_146758_f;
    private GuiTextField field_146760_g;
    private String field_146766_h;
    private static int field_146767_i = 0;
    private static int field_146764_r = 1;
    private static int field_146763_s = 2;
    private boolean field_146762_t;
    private String field_146761_u = "You must enter a name!";
    private WorldTemplate field_146759_v;
    private static final String __OBFID = "CL_00000776";

    public GuiScreenCreateOnlineWorld(GuiScreen par1GuiScreen)
    {
        super.field_146292_n = Collections.synchronizedList(new ArrayList());
        this.field_146758_f = par1GuiScreen;
    }

    // JAVADOC METHOD $$ func_73876_c
    public void updateScreen()
    {
        this.field_146760_g.func_146178_a();
        this.field_146766_h = this.field_146760_g.func_146179_b();
    }

    // JAVADOC METHOD $$ func_73866_w_
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.field_146292_n.clear();
        this.field_146292_n.add(new GuiButton(field_146767_i, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 120 + 17, 97, 20, I18n.getStringParams("mco.create.world", new Object[0])));
        this.field_146292_n.add(new GuiButton(field_146764_r, this.field_146294_l / 2 + 5, this.field_146295_m / 4 + 120 + 17, 95, 20, I18n.getStringParams("gui.cancel", new Object[0])));
        this.field_146760_g = new GuiTextField(this.field_146289_q, this.field_146294_l / 2 - 100, 65, 200, 20);
        this.field_146760_g.func_146195_b(true);

        if (this.field_146766_h != null)
        {
            this.field_146760_g.func_146180_a(this.field_146766_h);
        }

        if (this.field_146759_v == null)
        {
            this.field_146292_n.add(new GuiButton(field_146763_s, this.field_146294_l / 2 - 100, 107, 200, 20, I18n.getStringParams("mco.template.default.name", new Object[0])));
        }
        else
        {
            this.field_146292_n.add(new GuiButton(field_146763_s, this.field_146294_l / 2 - 100, 107, 200, 20, I18n.getStringParams("mco.template.name", new Object[0]) + ": " + this.field_146759_v.field_148785_b));
        }
    }

    public void func_146281_b()
    {
        Keyboard.enableRepeatEvents(false);
    }

    protected void func_146284_a(GuiButton p_146284_1_)
    {
        if (p_146284_1_.field_146124_l)
        {
            if (p_146284_1_.field_146127_k == field_146764_r)
            {
                this.field_146297_k.func_147108_a(this.field_146758_f);
            }
            else if (p_146284_1_.field_146127_k == field_146767_i)
            {
                this.func_146757_h();
            }
            else if (p_146284_1_.field_146127_k == field_146763_s)
            {
                this.field_146297_k.func_147108_a(new GuiScreenMcoWorldTemplate(this, this.field_146759_v));
            }
        }
    }

    private void func_146757_h()
    {
        if (this.func_146753_i())
        {
            GuiScreenCreateOnlineWorld.TaskWorldCreation taskworldcreation = new GuiScreenCreateOnlineWorld.TaskWorldCreation(this.field_146760_g.func_146179_b(), this.field_146759_v);
            GuiScreenLongRunningTask guiscreenlongrunningtask = new GuiScreenLongRunningTask(this.field_146297_k, this.field_146758_f, taskworldcreation);
            guiscreenlongrunningtask.func_146902_g();
            this.field_146297_k.func_147108_a(guiscreenlongrunningtask);
        }
    }

    private boolean func_146753_i()
    {
        this.field_146762_t = this.field_146760_g.func_146179_b() == null || this.field_146760_g.func_146179_b().trim().equals("");
        return !this.field_146762_t;
    }

    // JAVADOC METHOD $$ func_73869_a
    protected void keyTyped(char par1, int par2)
    {
        this.field_146760_g.func_146201_a(par1, par2);

        if (par2 == 15)
        {
            this.field_146760_g.func_146195_b(!this.field_146760_g.func_146206_l());
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
        this.field_146760_g.func_146192_a(par1, par2, par3);
    }

    // JAVADOC METHOD $$ func_73863_a
    public void drawScreen(int par1, int par2, float par3)
    {
        this.func_146276_q_();
        this.drawCenteredString(this.field_146289_q, I18n.getStringParams("mco.selectServer.create", new Object[0]), this.field_146294_l / 2, 11, 16777215);
        this.drawString(this.field_146289_q, I18n.getStringParams("mco.configure.world.name", new Object[0]), this.field_146294_l / 2 - 100, 52, 10526880);

        if (this.field_146762_t)
        {
            this.drawCenteredString(this.field_146289_q, this.field_146761_u, this.field_146294_l / 2, 167, 16711680);
        }

        this.field_146760_g.func_146194_f();
        super.drawScreen(par1, par2, par3);
    }

    public void func_146735_a(WorldTemplate p_146756_1_)
    {
        this.field_146759_v = p_146756_1_;
    }

    public void func_146735_a(Object p_146735_1_)
    {
        this.func_146735_a((WorldTemplate)p_146735_1_);
    }

    @SideOnly(Side.CLIENT)
    class TaskWorldCreation extends TaskLongRunning
    {
        private final String field_148427_c;
        private final WorldTemplate field_148426_d;
        private static final String __OBFID = "CL_00000777";

        public TaskWorldCreation(String p_i45036_2_, WorldTemplate p_i45036_3_)
        {
            this.field_148427_c = p_i45036_2_;
            this.field_148426_d = p_i45036_3_;
        }

        public void run()
        {
            String s = I18n.getStringParams("mco.create.world.wait", new Object[0]);
            this.func_148417_b(s);
            Session session = GuiScreenCreateOnlineWorld.this.field_146297_k.getSession();
            McoClient mcoclient = new McoClient(session.getSessionID(), session.getUsername(), "1.7.2", Minecraft.getMinecraft().getProxy());

            try
            {
                if (this.field_148426_d != null)
                {
                    mcoclient.func_148707_a(this.field_148427_c, this.field_148426_d.field_148787_a);
                }
                else
                {
                    mcoclient.func_148707_a(this.field_148427_c, "-1");
                }

                GuiScreenCreateOnlineWorld.this.field_146297_k.func_147108_a(GuiScreenCreateOnlineWorld.this.field_146758_f);
            }
            catch (ExceptionMcoService exceptionmcoservice)
            {
                GuiScreenCreateOnlineWorld.field_146765_a.error("Couldn\'t create world");
                this.func_148416_a(exceptionmcoservice.toString());
            }
            catch (UnsupportedEncodingException unsupportedencodingexception)
            {
                GuiScreenCreateOnlineWorld.field_146765_a.error("Couldn\'t create world");
                this.func_148416_a(unsupportedencodingexception.getLocalizedMessage());
            }
            catch (@SuppressWarnings("hiding") IOException ioexception)
            {
                GuiScreenCreateOnlineWorld.field_146765_a.error("Could not parse response creating world");
                this.func_148416_a(ioexception.getLocalizedMessage());
            }
            catch (Exception exception)
            {
                GuiScreenCreateOnlineWorld.field_146765_a.error("Could not create world");
                this.func_148416_a(exception.getLocalizedMessage());
            }
        }
    }
}