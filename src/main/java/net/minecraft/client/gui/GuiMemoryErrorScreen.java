package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.I18n;

@SideOnly(Side.CLIENT)
public class GuiMemoryErrorScreen extends GuiScreen
{
    private static final String __OBFID = "CL_00000702";

    // JAVADOC METHOD $$ func_73866_w_
    public void initGui()
    {
        this.field_146292_n.clear();
        this.field_146292_n.add(new GuiOptionButton(0, this.field_146294_l / 2 - 155, this.field_146295_m / 4 + 120 + 12, I18n.getStringParams("gui.toMenu", new Object[0])));
        this.field_146292_n.add(new GuiOptionButton(1, this.field_146294_l / 2 - 155 + 160, this.field_146295_m / 4 + 120 + 12, I18n.getStringParams("menu.quit", new Object[0])));
    }

    protected void func_146284_a(GuiButton p_146284_1_)
    {
        if (p_146284_1_.field_146127_k == 0)
        {
            this.field_146297_k.func_147108_a(new GuiMainMenu());
        }
        else if (p_146284_1_.field_146127_k == 1)
        {
            this.field_146297_k.shutdown();
        }
    }

    // JAVADOC METHOD $$ func_73869_a
    protected void keyTyped(char par1, int par2) {}

    // JAVADOC METHOD $$ func_73863_a
    public void drawScreen(int par1, int par2, float par3)
    {
        this.func_146276_q_();
        this.drawCenteredString(this.field_146289_q, "Out of memory!", this.field_146294_l / 2, this.field_146295_m / 4 - 60 + 20, 16777215);
        this.drawString(this.field_146289_q, "Minecraft has run out of memory.", this.field_146294_l / 2 - 140, this.field_146295_m / 4 - 60 + 60 + 0, 10526880);
        this.drawString(this.field_146289_q, "This could be caused by a bug in the game or by the", this.field_146294_l / 2 - 140, this.field_146295_m / 4 - 60 + 60 + 18, 10526880);
        this.drawString(this.field_146289_q, "Java Virtual Machine not being allocated enough", this.field_146294_l / 2 - 140, this.field_146295_m / 4 - 60 + 60 + 27, 10526880);
        this.drawString(this.field_146289_q, "memory.", this.field_146294_l / 2 - 140, this.field_146295_m / 4 - 60 + 60 + 36, 10526880);
        this.drawString(this.field_146289_q, "To prevent level corruption, the current game has quit.", this.field_146294_l / 2 - 140, this.field_146295_m / 4 - 60 + 60 + 54, 10526880);
        this.drawString(this.field_146289_q, "We\'ve tried to free up enough memory to let you go back to", this.field_146294_l / 2 - 140, this.field_146295_m / 4 - 60 + 60 + 63, 10526880);
        this.drawString(this.field_146289_q, "the main menu and back to playing, but this may not have worked.", this.field_146294_l / 2 - 140, this.field_146295_m / 4 - 60 + 60 + 72, 10526880);
        this.drawString(this.field_146289_q, "Please restart the game if you see this message again.", this.field_146294_l / 2 - 140, this.field_146295_m / 4 - 60 + 60 + 81, 10526880);
        super.drawScreen(par1, par2, par3);
    }
}