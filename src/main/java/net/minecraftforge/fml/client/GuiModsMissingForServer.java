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
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.MissingModsException;
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
        this.buttonList.add(new GuiButton(1, this.width / 2 - 75, this.height - 38, I18n.format("gui.done")));
    }

    @Override
    protected void actionPerformed(GuiButton p_73875_1_)
    {
        if (p_73875_1_.enabled && p_73875_1_.id == 1)
        {
            FMLClientHandler.instance().showGuiScreen(null);
        }
    }
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        int offset = Math.max(85 - modsMissing.missingMods.size() * 10, 10);
        this.drawCenteredString(this.fontRenderer, "Forge Mod Loader could not connect to this server", this.width / 2, offset, 0xFFFFFF);
        offset += 10;
        this.drawCenteredString(this.fontRenderer, "The mods and versions listed below could not be found", this.width / 2, offset, 0xFFFFFF);
        offset += 10;
        this.drawCenteredString(this.fontRenderer, "They are required to play on this server", this.width / 2, offset, 0xFFFFFF);
        offset += 5;
        for (ArtifactVersion v : modsMissing.missingMods)
        {
            offset += 10;
            this.drawCenteredString(this.fontRenderer, String.format("%s : %s", v.getLabel(), v.getRangeString()), this.width / 2, offset, 0xEEEEEE);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
