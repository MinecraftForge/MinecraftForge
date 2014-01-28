package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.C00PacketKeepAlive;

@SideOnly(Side.CLIENT)
public class GuiDownloadTerrain extends GuiScreen
{
    private NetHandlerPlayClient field_146594_a;
    private int field_146593_f;
    private static final String __OBFID = "CL_00000708";

    public GuiDownloadTerrain(NetHandlerPlayClient p_i45023_1_)
    {
        this.field_146594_a = p_i45023_1_;
    }

    // JAVADOC METHOD $$ func_73869_a
    protected void keyTyped(char par1, int par2) {}

    // JAVADOC METHOD $$ func_73866_w_
    public void initGui()
    {
        this.field_146292_n.clear();
    }

    // JAVADOC METHOD $$ func_73876_c
    public void updateScreen()
    {
        ++this.field_146593_f;

        if (this.field_146593_f % 20 == 0)
        {
            this.field_146594_a.func_147297_a(new C00PacketKeepAlive());
        }

        if (this.field_146594_a != null)
        {
            this.field_146594_a.func_147233_a();
        }
    }

    // JAVADOC METHOD $$ func_73863_a
    public void drawScreen(int par1, int par2, float par3)
    {
        this.func_146278_c(0);
        this.drawCenteredString(this.field_146289_q, I18n.getStringParams("multiplayer.downloadingTerrain", new Object[0]), this.field_146294_l / 2, this.field_146295_m / 2 - 50, 16777215);
        super.drawScreen(par1, par2, par3);
    }

    // JAVADOC METHOD $$ func_73868_f
    public boolean doesGuiPauseGame()
    {
        return false;
    }
}