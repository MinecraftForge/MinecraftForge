package net.minecraftforge.fml.client;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.MissingModsException;
import net.minecraftforge.fml.common.MultipleModsException;
import net.minecraftforge.fml.common.WrongMinecraftVersionException;

import java.util.List;

public class GuiMultipleModsException extends GuiErrorScreen
{
    private final List<RuntimeException> exceptions;
    private int pageIndex;
    private GuiErrorScreen subGui;

    public GuiMultipleModsException(MultipleModsException exception)
    {
        super(null ,null);
        this.exceptions = exception.exceptions;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        pageIndex = 0;
        RuntimeException exception = exceptions.get(pageIndex);
        subGui = null;
        buttonList.clear();
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
        }
        this.buttonList.add(new GuiButton(1,50, this.height -20, this.width/2 -55 , 20,   "<"));
        this.buttonList.add(new GuiButton(2, this.width/2 +5, this.height -20, this.width/2 -55, 20, ">"));
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
        buttonList.get(0).enabled = pageIndex != 0;
        buttonList.get(1).enabled = pageIndex<exceptions.size()-1;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        ScaledResolution resolution = new ScaledResolution(mc);
        subGui.setWorldAndResolution(mc, resolution.getScaledWidth(), resolution.getScaledHeight());
        subGui.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRendererObj, I18n.format("fml.messages.mod.missing.multiple", exceptions.size()), this.width / 2, 1, 0xFFFFFF);
        for(GuiButton button : buttonList)
        {
            button.drawButton(this.mc, mouseX, mouseY);
        }
    }

    @Override
    public void actionPerformed(GuiButton button)
    {
        if(button.id==1)
        {
            pageIndex--;
        }
        else if(button.id==2)
        {
            pageIndex++;
        }
    }
}