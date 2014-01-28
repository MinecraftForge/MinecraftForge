package net.minecraft.client.gui.mco;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenSelectLocation;
import net.minecraft.client.mco.ExceptionMcoService;
import net.minecraft.client.mco.McoClient;
import net.minecraft.client.mco.WorldTemplate;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class GuiScreenMcoWorldTemplate extends GuiScreen
{
    private static final AtomicInteger field_146958_a = new AtomicInteger(0);
    private static final Logger field_146953_f = LogManager.getLogger();
    private final ScreenWithCallback field_146954_g;
    private WorldTemplate field_146959_h;
    private List field_146960_i = Collections.emptyList();
    private GuiScreenMcoWorldTemplate.SelectionList field_146957_r;
    private int field_146956_s = -1;
    private GuiButton field_146955_t;
    private static final String __OBFID = "CL_00000786";

    public GuiScreenMcoWorldTemplate(ScreenWithCallback par1ScreenWithCallback, WorldTemplate par2WorldTemplate)
    {
        this.field_146954_g = par1ScreenWithCallback;
        this.field_146959_h = par2WorldTemplate;
    }

    // JAVADOC METHOD $$ func_73866_w_
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.field_146292_n.clear();
        this.field_146957_r = new GuiScreenMcoWorldTemplate.SelectionList();
        (new Thread("MCO World Creator #" + field_146958_a.incrementAndGet())
        {
            private static final String __OBFID = "CL_00000787";
            public void run()
            {
                Session session = GuiScreenMcoWorldTemplate.this.field_146297_k.getSession();
                McoClient mcoclient = new McoClient(session.getSessionID(), session.getUsername(), "1.7.2", Minecraft.getMinecraft().getProxy());

                try
                {
                    GuiScreenMcoWorldTemplate.this.field_146960_i = mcoclient.func_148693_e().field_148782_a;
                }
                catch (ExceptionMcoService exceptionmcoservice)
                {
                    GuiScreenMcoWorldTemplate.field_146953_f.error("Couldn\'t fetch templates");
                }
            }
        }).start();
        this.func_146952_h();
    }

    private void func_146952_h()
    {
        this.field_146292_n.add(new GuiButton(0, this.field_146294_l / 2 + 6, this.field_146295_m - 52, 153, 20, I18n.getStringParams("gui.cancel", new Object[0])));
        this.field_146292_n.add(this.field_146955_t = new GuiButton(1, this.field_146294_l / 2 - 154, this.field_146295_m - 52, 153, 20, I18n.getStringParams("mco.template.button.select", new Object[0])));
    }

    // JAVADOC METHOD $$ func_73876_c
    public void updateScreen()
    {
        super.updateScreen();
    }

    protected void func_146284_a(GuiButton p_146284_1_)
    {
        if (p_146284_1_.field_146124_l)
        {
            if (p_146284_1_.field_146127_k == 1)
            {
                this.func_146947_i();
            }
            else if (p_146284_1_.field_146127_k == 0)
            {
                this.field_146954_g.func_146735_a((Object)null);
                this.field_146297_k.func_147108_a(this.field_146954_g);
            }
            else
            {
                this.field_146957_r.func_148357_a(p_146284_1_);
            }
        }
    }

    private void func_146947_i()
    {
        if (this.field_146956_s >= 0 && this.field_146956_s < this.field_146960_i.size())
        {
            this.field_146954_g.func_146735_a(this.field_146960_i.get(this.field_146956_s));
            this.field_146297_k.func_147108_a(this.field_146954_g);
        }
    }

    // JAVADOC METHOD $$ func_73863_a
    public void drawScreen(int par1, int par2, float par3)
    {
        this.func_146276_q_();
        this.field_146957_r.func_148350_a(par1, par2, par3);
        this.drawCenteredString(this.field_146289_q, I18n.getStringParams("mco.template.title", new Object[0]), this.field_146294_l / 2, 20, 16777215);
        super.drawScreen(par1, par2, par3);
    }

    @SideOnly(Side.CLIENT)
    class SelectionList extends GuiScreenSelectLocation
    {
        private static final String __OBFID = "CL_00000788";

        public SelectionList()
        {
            super(GuiScreenMcoWorldTemplate.this.field_146297_k, GuiScreenMcoWorldTemplate.this.field_146294_l, GuiScreenMcoWorldTemplate.this.field_146295_m, 32, GuiScreenMcoWorldTemplate.this.field_146295_m - 64, 36);
        }

        protected int func_148355_a()
        {
            return GuiScreenMcoWorldTemplate.this.field_146960_i.size() + 1;
        }

        protected void func_148352_a(int p_148352_1_, boolean p_148352_2_)
        {
            if (p_148352_1_ < GuiScreenMcoWorldTemplate.this.field_146960_i.size())
            {
                GuiScreenMcoWorldTemplate.this.field_146956_s = p_148352_1_;
                GuiScreenMcoWorldTemplate.this.field_146959_h = null;
            }
        }

        protected boolean func_148356_a(int p_148356_1_)
        {
            return GuiScreenMcoWorldTemplate.this.field_146960_i.size() == 0 ? false : (p_148356_1_ >= GuiScreenMcoWorldTemplate.this.field_146960_i.size() ? false : (GuiScreenMcoWorldTemplate.this.field_146959_h != null ? GuiScreenMcoWorldTemplate.this.field_146959_h.field_148785_b.equals(((WorldTemplate)GuiScreenMcoWorldTemplate.this.field_146960_i.get(p_148356_1_)).field_148785_b) : p_148356_1_ == GuiScreenMcoWorldTemplate.this.field_146956_s));
        }

        protected boolean func_148349_b(int p_148349_1_)
        {
            return false;
        }

        protected int func_148351_b()
        {
            return this.func_148355_a() * 36;
        }

        protected void func_148358_c()
        {
            GuiScreenMcoWorldTemplate.this.func_146276_q_();
        }

        protected void func_148348_a(int p_148348_1_, int p_148348_2_, int p_148348_3_, int p_148348_4_, Tessellator p_148348_5_)
        {
            if (p_148348_1_ < GuiScreenMcoWorldTemplate.this.field_146960_i.size())
            {
                this.func_148387_b(p_148348_1_, p_148348_2_, p_148348_3_, p_148348_4_, p_148348_5_);
            }
        }

        private void func_148387_b(int p_148387_1_, int p_148387_2_, int p_148387_3_, int p_148387_4_, Tessellator p_148387_5_)
        {
            WorldTemplate worldtemplate = (WorldTemplate)GuiScreenMcoWorldTemplate.this.field_146960_i.get(p_148387_1_);
            GuiScreenMcoWorldTemplate.this.drawString(GuiScreenMcoWorldTemplate.this.field_146289_q, worldtemplate.field_148785_b, p_148387_2_ + 2, p_148387_3_ + 1, 16777215);
            GuiScreenMcoWorldTemplate.this.drawString(GuiScreenMcoWorldTemplate.this.field_146289_q, worldtemplate.field_148784_d, p_148387_2_ + 2, p_148387_3_ + 12, 7105644);
            GuiScreenMcoWorldTemplate.this.drawString(GuiScreenMcoWorldTemplate.this.field_146289_q, worldtemplate.field_148786_c, p_148387_2_ + 2 + 207 - GuiScreenMcoWorldTemplate.this.field_146289_q.getStringWidth(worldtemplate.field_148786_c), p_148387_3_ + 1, 5000268);
        }
    }
}