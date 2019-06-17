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
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;

public class GuiBackupFailed extends Screen
{
    private Screen parent;
    private File zipName;
    public GuiBackupFailed(Screen parent, File zipName)
    {
        super(new TranslationTextComponent("fml.menu.backupfailed.title"));
        this.parent = parent;
        this.zipName = zipName;
    }

    @Override
    public void init()
    {
        this.buttons.add(new Button(this.width / 2 - 75, this.height - 38, 200, 20, I18n.format("gui.done"), b -> GuiBackupFailed.this.minecraft.displayGuiScreen(parent)));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground();
        String[] lines = I18n.format("fml.menu.backupfailed.message", zipName.getName()).split("\n");
        int offset = Math.max(85 - lines.length * 10, 10);
        for (String line : lines) {
            this.drawCenteredString(this.font, line, this.width / 2, offset, 0xFFFFFF);
            offset += 10;
        }
        super.render(mouseX, mouseY, partialTicks);
    }
}
