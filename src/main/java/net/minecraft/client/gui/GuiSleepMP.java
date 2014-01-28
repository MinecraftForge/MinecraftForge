package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.C0BPacketEntityAction;

@SideOnly(Side.CLIENT)
public class GuiSleepMP extends GuiChat
{
    private static final String __OBFID = "CL_00000697";

    // JAVADOC METHOD $$ func_73866_w_
    public void initGui()
    {
        super.initGui();
        this.field_146292_n.add(new GuiButton(1, this.field_146294_l / 2 - 100, this.field_146295_m - 40, I18n.getStringParams("multiplayer.stopSleeping", new Object[0])));
    }

    // JAVADOC METHOD $$ func_73869_a
    protected void keyTyped(char par1, int par2)
    {
        if (par2 == 1)
        {
            this.func_146418_g();
        }
        else if (par2 != 28 && par2 != 156)
        {
            super.keyTyped(par1, par2);
        }
        else
        {
            String s = this.field_146415_a.func_146179_b().trim();

            if (!s.isEmpty())
            {
                this.field_146297_k.thePlayer.sendChatMessage(s);
            }

            this.field_146415_a.func_146180_a("");
            this.field_146297_k.ingameGUI.func_146158_b().func_146240_d();
        }
    }

    protected void func_146284_a(GuiButton p_146284_1_)
    {
        if (p_146284_1_.field_146127_k == 1)
        {
            this.func_146418_g();
        }
        else
        {
            super.func_146284_a(p_146284_1_);
        }
    }

    private void func_146418_g()
    {
        NetHandlerPlayClient nethandlerplayclient = this.field_146297_k.thePlayer.sendQueue;
        nethandlerplayclient.func_147297_a(new C0BPacketEntityAction(this.field_146297_k.thePlayer, 3));
    }
}