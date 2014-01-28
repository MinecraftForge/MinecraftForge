package cpw.mods.fml.client;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiIngameModOptions extends GuiScreen
{
    private final GuiScreen parentScreen;
    protected String title = "Mod Options";
    private GuiModOptionList optionList;

    public GuiIngameModOptions(GuiScreen parentScreen)
    {
        this.parentScreen = parentScreen;
    }

    // JAVADOC METHOD $$ func_73866_w_
    @SuppressWarnings("unchecked")
    @Override
    public void initGui()
    {
        this.optionList=new GuiModOptionList(this);
        this.optionList.registerScrollButtons(this.field_146292_n, 7, 8);
        this.field_146292_n.add(new GuiButton(200, this.field_146294_l / 2 - 100, this.field_146295_m / 6 + 168, I18n.getStringParams("gui.done", new Object[0])));
    }

    @Override
    protected void func_146284_a(GuiButton p_146284_1_)
    {
        if (p_146284_1_.field_146124_l)
        {
            if (p_146284_1_.field_146127_k == 200)
            {
                this.field_146297_k.gameSettings.saveOptions();
                this.field_146297_k.func_147108_a(this.parentScreen);
            }
        }
    }

    // JAVADOC METHOD $$ func_73863_a
    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
        // force a non-transparent background
        this.func_146276_q_();
        this.optionList.drawScreen(par1, par2, par3);
        this.drawCenteredString(this.field_146289_q, this.title, this.field_146294_l / 2, 15, 0xFFFFFF);
        super.drawScreen(par1, par2, par3);
    }

    FontRenderer getFontRenderer() {
        return field_146289_q;
    }

}