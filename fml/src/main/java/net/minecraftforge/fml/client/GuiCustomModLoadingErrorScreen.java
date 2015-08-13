/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
 */

package net.minecraftforge.fml.client;

import net.minecraft.client.gui.GuiErrorScreen;

public class GuiCustomModLoadingErrorScreen extends GuiErrorScreen
{
    private CustomModLoadingErrorDisplayException customException;
    public GuiCustomModLoadingErrorScreen(CustomModLoadingErrorDisplayException customException)
    {
        super(null,null);
        this.customException = customException;
    }
    @Override
    public void initGui()
    {
        super.initGui();
        this.buttonList.clear();
        this.customException.initGui(this, fontRendererObj);
    }
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.customException.drawScreen(this, fontRendererObj, mouseX, mouseY, partialTicks);
    }
}
