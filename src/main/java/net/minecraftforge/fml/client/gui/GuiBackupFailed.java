/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

import java.io.File;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiBackupFailed extends GuiScreen
{
    private GuiScreen parent;
    private File zipName;
    public GuiBackupFailed(GuiScreen parent, File zipName)
    {
        this.parent = parent;
        this.zipName = zipName;
    }

    @Override
    public void initGui()
    {
        this.buttons.add(new GuiButton(1, this.width / 2 - 75, this.height - 38, I18n.format("gui.done"))
        {
            public void onClick(double mouseX, double mouseY)
            {
                GuiBackupFailed.this.mc.displayGuiScreen(parent);
            }
        });
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        int offset = Math.max(85 - 2 * 10, 10);
        this.drawCenteredString(this.fontRenderer, String.format("There was an error saving the archive %s", zipName.getName()), this.width / 2, offset, 0xFFFFFF);
        offset += 10;
        this.drawCenteredString(this.fontRenderer, String.format("Please fix the problem and try again"), this.width / 2, offset, 0xFFFFFF);
        super.render(mouseX, mouseY, partialTicks);
    }
}
