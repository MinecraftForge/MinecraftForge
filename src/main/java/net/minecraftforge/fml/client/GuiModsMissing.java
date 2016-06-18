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

import java.awt.*;
import java.io.File;
import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.MissingModsException;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;
import org.apache.logging.log4j.Level;

public class GuiModsMissing extends GuiErrorScreen
{
    private File minecraftDir = new File(Loader.instance().getConfigDir().getParent());
    private File clientLog = new File(minecraftDir, "logs/fml-client-latest.log");
    private MissingModsException modsMissing;

    public GuiModsMissing(MissingModsException modsMissing)
    {
        super(null,null);
        this.modsMissing = modsMissing;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(1, 50, this.height - 38, this.width/2 -55, 20, I18n.format("fml.button.open.mods.folder")));
        String openFileText = I18n.format("fml.button.open.file", clientLog.getName());
        this.buttonList.add(new GuiButton(2, this.width / 2 + 5, this.height - 38, this.width / 2 - 55, 20, openFileText));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 1)
        {
            try
            {
                File modsDir = new File(minecraftDir, "mods");
                Desktop.getDesktop().open(modsDir);
            }
            catch (Exception e)
            {
                FMLLog.log(Level.ERROR, e, "Problem opening mods folder");
            }
        }
        else if (button.id == 2)
        {
            try
            {
                Desktop.getDesktop().open(clientLog);
            }
            catch (Exception e)
            {
                FMLLog.log(Level.ERROR, e, "Problem opening log file " + clientLog);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        int offset = Math.max(85 - modsMissing.missingMods.size() * 10, 10);
        String modMissingDependenciesText = I18n.format("fml.messages.mod.missing.dependencies", TextFormatting.BOLD + modsMissing.getModName() + TextFormatting.RESET);
        this.drawCenteredString(this.fontRendererObj, modMissingDependenciesText, this.width / 2, offset, 0xFFFFFF);
        offset+=10;
        String fixMissingDependenciesText = I18n.format("fml.messages.mod.missing.dependencies.fix", modsMissing.getModName());
        this.drawCenteredString(this.fontRendererObj, fixMissingDependenciesText, this.width / 2, offset, 0xFFFFFF);
        offset+=5;
        for (ArtifactVersion v : modsMissing.missingMods)
        {
            offset+=10;
            if (v instanceof DefaultArtifactVersion)
            {
                DefaultArtifactVersion dav =  (DefaultArtifactVersion)v;
                if (dav.getRange() != null)
                {
                    String message = String.format(TextFormatting.BOLD +  "%s " + TextFormatting.RESET + "%s", v.getLabel(), dav.getRange().toStringFriendly());
                    this.drawCenteredString(this.fontRendererObj, message, this.width / 2, offset, 0xEEEEEE);
                    continue;
                }
            }
            this.drawCenteredString(this.fontRendererObj, String.format("%s : %s", v.getLabel(), v.getRangeString()), this.width / 2, offset, 0xEEEEEE);
        }
        offset+=20;
        String seeLogText = I18n.format("fml.messages.mod.missing.dependencies.see.log", clientLog.getName());
        this.drawCenteredString(this.fontRendererObj, seeLogText, this.width / 2, offset, 0xFFFFFF);

        for (int i = 0; i < this.buttonList.size(); ++i)
        {
            this.buttonList.get(i).drawButton(this.mc, mouseX, mouseY);
        }
    }
}
