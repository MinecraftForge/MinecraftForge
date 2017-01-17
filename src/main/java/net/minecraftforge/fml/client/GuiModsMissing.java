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

public class GuiModsMissing extends GuiErrorBase
{
    private File minecraftDir = new File(Loader.instance().getConfigDir().getParent());
    private File clientLog = new File(minecraftDir, "logs/fml-client-latest.log");
    private MissingModsException modsMissing;

    public GuiModsMissing(MissingModsException modsMissing)
    {
        super();
        this.modsMissing = modsMissing;
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
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
