package net.minecraftforge.fml.client;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.*;

import java.util.List;

public class GuiMultipleModsException extends GuiErrorBase
{
    private final List<RuntimeException> exceptions;
    private int pageIndex;
    private GuiErrorBase subGui;

    public GuiMultipleModsException(MultipleModsException exception)
    {
        super();
        this.exceptions = exception.exceptions;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        pageIndex = 0;
        RuntimeException exception = exceptions.get(pageIndex);
        if (exception instanceof WrongMinecraftVersionException)
        {
            subGui = (new GuiWrongMinecraft((WrongMinecraftVersionException) exception));
        }
        else if (exception instanceof MissingModsException)
        {
            subGui = (new GuiModsMissing((MissingModsException) exception));
        }
        if(subGui!=null)
        {
            subGui.initGui();
            subGui.clearButtons();
        }
        this.buttonList.add(new GuiButton(3,50, this.height -20, this.width/2 -55 , 20,   "<"));
        this.buttonList.add(new GuiButton(4, this.width/2 +5, this.height -20, this.width/2 -55, 20, ">"));
    }

    @Override
    public void updateScreen()
    {
        RuntimeException exception = exceptions.get(pageIndex);
        subGui = null;
        if (exception instanceof WrongMinecraftVersionException)
        {
            subGui = (new GuiWrongMinecraft((WrongMinecraftVersionException) exception));
        }
        else if (exception instanceof MissingModsException)
        {
            subGui = (new GuiModsMissing((MissingModsException) exception));
        }
        buttonList.get(2).enabled = pageIndex != 0;
        buttonList.get(3).enabled = pageIndex<exceptions.size()-1;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        ScaledResolution resolution = new ScaledResolution(mc);
        subGui.setWorldAndResolution(mc, resolution.getScaledWidth(), resolution.getScaledHeight());
        subGui.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRendererObj, I18n.format("fml.messages.mod.missing.multiple", exceptions.size()), this.width / 2, 1, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void actionPerformed(GuiButton button)
    {
        if(button.id==3)
        {
            pageIndex--;
        }
        else if(button.id==4)
        {
            pageIndex++;
        }
        else
        {
            super.actionPerformed(button);
        }
    }
}