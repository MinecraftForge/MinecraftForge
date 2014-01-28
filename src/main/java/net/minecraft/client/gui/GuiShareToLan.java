package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldSettings;

@SideOnly(Side.CLIENT)
public class GuiShareToLan extends GuiScreen
{
    private final GuiScreen field_146598_a;
    private GuiButton field_146596_f;
    private GuiButton field_146597_g;
    private String field_146599_h = "survival";
    private boolean field_146600_i;
    private static final String __OBFID = "CL_00000713";

    public GuiShareToLan(GuiScreen par1GuiScreen)
    {
        this.field_146598_a = par1GuiScreen;
    }

    // JAVADOC METHOD $$ func_73866_w_
    public void initGui()
    {
        this.field_146292_n.clear();
        this.field_146292_n.add(new GuiButton(101, this.field_146294_l / 2 - 155, this.field_146295_m - 28, 150, 20, I18n.getStringParams("lanServer.start", new Object[0])));
        this.field_146292_n.add(new GuiButton(102, this.field_146294_l / 2 + 5, this.field_146295_m - 28, 150, 20, I18n.getStringParams("gui.cancel", new Object[0])));
        this.field_146292_n.add(this.field_146597_g = new GuiButton(104, this.field_146294_l / 2 - 155, 100, 150, 20, I18n.getStringParams("selectWorld.gameMode", new Object[0])));
        this.field_146292_n.add(this.field_146596_f = new GuiButton(103, this.field_146294_l / 2 + 5, 100, 150, 20, I18n.getStringParams("selectWorld.allowCommands", new Object[0])));
        this.func_146595_g();
    }

    private void func_146595_g()
    {
        this.field_146597_g.field_146126_j = I18n.getStringParams("selectWorld.gameMode", new Object[0]) + " " + I18n.getStringParams("selectWorld.gameMode." + this.field_146599_h, new Object[0]);
        this.field_146596_f.field_146126_j = I18n.getStringParams("selectWorld.allowCommands", new Object[0]) + " ";

        if (this.field_146600_i)
        {
            this.field_146596_f.field_146126_j = this.field_146596_f.field_146126_j + I18n.getStringParams("options.on", new Object[0]);
        }
        else
        {
            this.field_146596_f.field_146126_j = this.field_146596_f.field_146126_j + I18n.getStringParams("options.off", new Object[0]);
        }
    }

    protected void func_146284_a(GuiButton p_146284_1_)
    {
        if (p_146284_1_.field_146127_k == 102)
        {
            this.field_146297_k.func_147108_a(this.field_146598_a);
        }
        else if (p_146284_1_.field_146127_k == 104)
        {
            if (this.field_146599_h.equals("survival"))
            {
                this.field_146599_h = "creative";
            }
            else if (this.field_146599_h.equals("creative"))
            {
                this.field_146599_h = "adventure";
            }
            else
            {
                this.field_146599_h = "survival";
            }

            this.func_146595_g();
        }
        else if (p_146284_1_.field_146127_k == 103)
        {
            this.field_146600_i = !this.field_146600_i;
            this.func_146595_g();
        }
        else if (p_146284_1_.field_146127_k == 101)
        {
            this.field_146297_k.func_147108_a((GuiScreen)null);
            String s = this.field_146297_k.getIntegratedServer().shareToLAN(WorldSettings.GameType.getByName(this.field_146599_h), this.field_146600_i);
            Object object;

            if (s != null)
            {
                object = new ChatComponentTranslation("commands.publish.started", new Object[] {s});
            }
            else
            {
                object = new ChatComponentText("commands.publish.failed");
            }

            this.field_146297_k.ingameGUI.func_146158_b().func_146227_a((IChatComponent)object);
        }
    }

    // JAVADOC METHOD $$ func_73863_a
    public void drawScreen(int par1, int par2, float par3)
    {
        this.func_146276_q_();
        this.drawCenteredString(this.field_146289_q, I18n.getStringParams("lanServer.title", new Object[0]), this.field_146294_l / 2, 50, 16777215);
        this.drawCenteredString(this.field_146289_q, I18n.getStringParams("lanServer.otherPlayers", new Object[0]), this.field_146294_l / 2, 82, 16777215);
        super.drawScreen(par1, par2, par3);
    }
}