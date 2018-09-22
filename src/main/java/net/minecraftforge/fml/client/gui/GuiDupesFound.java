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

import java.io.File;
import java.util.Map.Entry;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.DuplicateModsFoundException;

@OnlyIn(Dist.CLIENT)
public class GuiDupesFound extends GuiErrorBase
{
    private DuplicateModsFoundException dupes;

    public GuiDupesFound(DuplicateModsFoundException dupes)
    {
        this.dupes = dupes;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        int offset = Math.max(85 - dupes.dupes.size() * 10, 10);
        this.drawCenteredString(this.fontRenderer, "Forge Mod Loader has found a problem with your minecraft installation", this.width / 2, offset, 0xFFFFFF);
        offset += 10;
        this.drawCenteredString(this.fontRenderer, "You have mod sources that are duplicate within your system", this.width / 2, offset, 0xFFFFFF);
        offset += 10;
        this.drawCenteredString(this.fontRenderer, "Mod Id : File name", this.width / 2, offset, 0xFFFFFF);
        offset += 5;
        for (Entry<ModContainer, File> mc : dupes.dupes.entries())
        {
            offset += 10;
            this.drawCenteredString(this.fontRenderer, String.format("%s : %s", mc.getKey().getModId(), mc.getValue().getName()), this.width / 2, offset, 0xEEEEEE);
        }
        super.render(mouseX, mouseY, partialTicks);
    }
}
