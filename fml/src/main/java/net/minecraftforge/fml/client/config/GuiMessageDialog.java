package net.minecraftforge.fml.client.config;

import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IChatComponent;

public class GuiMessageDialog extends GuiDisconnected
{
    protected String buttonText;

    public GuiMessageDialog(GuiScreen nextScreen, String title, IChatComponent message, String buttonText)
    {
        super(nextScreen, title, message);
        this.buttonText = buttonText;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        ((GuiButton) buttonList.get(0)).displayString = I18n.format(buttonText);
    }
}
