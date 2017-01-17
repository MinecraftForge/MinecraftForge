/*
 * Minecraft Forge
 * Copyright (c) 2016.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.minecraftforge.fml.client;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.*;

import java.util.List;

public class GuiMultipleModsErrored extends GuiErrorBase
{
    private final List<RuntimeException> exceptions;
    private int pageIndex;
    private GuiErrorBase subGui;

    public GuiMultipleModsErrored(MultipleModsErrored exception)
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
        subGui.initGui();
        subGui.clearButtons();
        this.buttonList.add(new GuiButton(3,50, this.height -20, this.width/2 -55 , 20,   "<"));
        this.buttonList.add(new GuiButton(4, this.width/2 +5, this.height -20, this.width/2 -55, 20, ">"));
    }

    @Override
    public void updateScreen()
    {
        RuntimeException exception = exceptions.get(pageIndex);
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