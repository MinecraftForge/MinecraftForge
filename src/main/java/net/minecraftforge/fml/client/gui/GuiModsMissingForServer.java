/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.fml.client.gui;

import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;

public class GuiModsMissingForServer extends GuiScreen
{
    private MissingModsException modsMissing;

    public GuiModsMissingForServer(MissingModsException modsMissing)
    {
        this.modsMissing = modsMissing;
    }

    @Override
    public void initGui()
    {
        this.buttonList.add(new GuiButton(1, this.width / 2 - 75, this.height - 38, I18n.format("gui.done"))
        {
            public void func_194829_a(double p_194829_1_, double p_194829_3_)
            {
                GuiModsMissingForServer.this.mc.displayGuiScreen(null);
            }
        });
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        List<MissingModsException.MissingModInfo> missingModsVersions = modsMissing.getMissingModInfos();
        int offset = Math.max(85 - missingModsVersions.size() * 10, 10);
        this.drawCenteredString(this.fontRenderer, "Forge Mod Loader could not connect to this server", this.width / 2, offset, 0xFFFFFF);
        offset += 10;
        this.drawCenteredString(this.fontRenderer, "The mods and versions listed below could not be found", this.width / 2, offset, 0xFFFFFF);
        offset += 10;
        this.drawCenteredString(this.fontRenderer, "They are required to play on this server", this.width / 2, offset, 0xFFFFFF);
        offset += 5;
        for (MissingModsException.MissingModInfo info : missingModsVersions)
        {
            ArtifactVersion v = info.getAcceptedVersion();
            offset += 10;
            this.drawCenteredString(this.fontRenderer, String.format("%s : %s", v.getLabel(), v.getRangeString()), this.width / 2, offset, 0xEEEEEE);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
