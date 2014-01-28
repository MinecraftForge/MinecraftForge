package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.I18n;

@SideOnly(Side.CLIENT)
public class GuiScreenConfirmation extends GuiScreen
{
    private final GuiScreenConfirmation.ConfirmationType field_146937_i;
    private final String field_146934_r;
    private final String field_146933_s;
    protected final GuiScreen field_146935_a;
    protected final String field_146931_f;
    protected final String field_146932_g;
    protected final int field_146936_h;
    private static final String __OBFID = "CL_00000781";

    public GuiScreenConfirmation(GuiScreen par1GuiScreen, GuiScreenConfirmation.ConfirmationType par2GuiScreenConfirmationType, String par3Str, String par4Str, int par5)
    {
        this.field_146935_a = par1GuiScreen;
        this.field_146936_h = par5;
        this.field_146937_i = par2GuiScreenConfirmationType;
        this.field_146934_r = par3Str;
        this.field_146933_s = par4Str;
        this.field_146931_f = I18n.getStringParams("gui.yes", new Object[0]);
        this.field_146932_g = I18n.getStringParams("gui.no", new Object[0]);
    }

    // JAVADOC METHOD $$ func_73866_w_
    public void initGui()
    {
        this.field_146292_n.add(new GuiOptionButton(0, this.field_146294_l / 2 - 155, this.field_146295_m / 6 + 112, this.field_146931_f));
        this.field_146292_n.add(new GuiOptionButton(1, this.field_146294_l / 2 - 155 + 160, this.field_146295_m / 6 + 112, this.field_146932_g));
    }

    protected void func_146284_a(GuiButton p_146284_1_)
    {
        this.field_146935_a.confirmClicked(p_146284_1_.field_146127_k == 0, this.field_146936_h);
    }

    // JAVADOC METHOD $$ func_73863_a
    public void drawScreen(int par1, int par2, float par3)
    {
        this.func_146276_q_();
        this.drawCenteredString(this.field_146289_q, this.field_146937_i.field_148515_d, this.field_146294_l / 2, 70, this.field_146937_i.field_148518_c);
        this.drawCenteredString(this.field_146289_q, this.field_146934_r, this.field_146294_l / 2, 90, 16777215);
        this.drawCenteredString(this.field_146289_q, this.field_146933_s, this.field_146294_l / 2, 110, 16777215);
        super.drawScreen(par1, par2, par3);
    }

    @SideOnly(Side.CLIENT)
    public static enum ConfirmationType
    {
        Warning("Warning!", 16711680),
        Info("Info!", 8226750);
        public final int field_148518_c;
        public final String field_148515_d;

        private static final String __OBFID = "CL_00000782";

        private ConfirmationType(String par3Str, int par4)
        {
            this.field_148515_d = par3Str;
            this.field_148518_c = par4;
        }
    }
}