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

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.MissingModsException;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;
import org.apache.commons.lang3.tuple.Pair;

public class GuiModsMissing extends GuiErrorBase
{
    private MissingModsException modsMissing;

    public GuiModsMissing(MissingModsException modsMissing)
    {
        this.modsMissing = modsMissing;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        List<Pair<ArtifactVersion, ArtifactVersion>> missingModsVersions = modsMissing.getMissingModsVersions();
        int offset = Math.max(85 - missingModsVersions.size() * 10, 10);
        String modMissingDependenciesText = I18n.format("fml.messages.mod.missing.dependencies", TextFormatting.BOLD + modsMissing.getModName() + TextFormatting.RESET);
        this.drawCenteredString(this.fontRenderer, modMissingDependenciesText, this.width / 2, offset, 0xFFFFFF);
        offset+=10;
        String fixMissingDependenciesText = I18n.format("fml.messages.mod.missing.dependencies.fix", modsMissing.getModName());
        this.drawCenteredString(this.fontRenderer, fixMissingDependenciesText, this.width / 2, offset, 0xFFFFFF);
        offset+=5;
        for (Pair<ArtifactVersion, ArtifactVersion> versionInfo : missingModsVersions)
        {
            ArtifactVersion neededVersion = versionInfo.getKey();
            String neededMod = neededVersion.getLabel();
            ArtifactVersion haveVersion = versionInfo.getValue();
            String missingReason;
            if (haveVersion == null)
            {
                missingReason = I18n.format("fml.messages.mod.missing.dependencies.missing");
            }
            else
            {
                missingReason = I18n.format("fml.messages.mod.missing.dependencies.you.have", neededVersion.getVersionString());
            }
            String neededModVersionString = neededVersion.getRangeString();
            if (neededVersion instanceof DefaultArtifactVersion)
            {
                DefaultArtifactVersion dav = (DefaultArtifactVersion)neededVersion;
                if (dav.getRange() != null)
                {
                    neededModVersionString = dav.getRange().toStringFriendly();
                }
            }
            String message = String.format(TextFormatting.BOLD + "%s " + TextFormatting.RESET + "%s (%s)", neededMod, neededModVersionString, missingReason);
            offset += 10;
            this.drawCenteredString(this.fontRenderer, message, this.width / 2, offset, 0xEEEEEE);
        }
        offset+=20;
        String seeLogText = I18n.format("fml.messages.mod.missing.dependencies.see.log", GuiErrorBase.clientLog.getName());
        this.drawCenteredString(this.fontRenderer, seeLogText, this.width / 2, offset, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
