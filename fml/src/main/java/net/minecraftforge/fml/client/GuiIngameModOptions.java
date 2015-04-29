package net.minecraftforge.fml.client;

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

    @SuppressWarnings("unchecked")
    @Override
    public void initGui()
    {
        this.optionList=new GuiModOptionList(this);
        this.optionList.registerScrollButtons(this.buttonList, 7, 8);
        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.format("gui.done", new Object[0])));
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.enabled)
        {
            if (button.id == 200)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(this.parentScreen);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        // force a non-transparent background
        this.drawDefaultBackground();
        this.optionList.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 15, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    FontRenderer getFontRenderer() {
        return fontRendererObj;
    }

}
