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

import com.google.common.collect.Sets;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class GuiResourceError extends GuiErrorScreen
{
    private final Set<String> textureOffenders;
    private final Set<String> modelOffenders;

    public GuiResourceError(Set<String> textureOffenders, Set<String> modelOffenders)
    {
        super(null,null);
        this.textureOffenders = Sets.newHashSet();
        this.modelOffenders = Sets.newHashSet();

        final Map<String,ModContainer> containers = Loader.instance().getIndexedModList();
        textureOffenders.forEach(name ->
        {
            ModContainer mc = containers.get(name);
            this.textureOffenders.add(mc != null ? mc.getName() : name);
        });
        modelOffenders.forEach(name ->
        {
            ModContainer mc = containers.get(name);
            this.modelOffenders.add(mc != null ? mc.getName() : name);
        });
    }

    @Override
    public void initGui()
    {
        this.buttonList.clear();
        int size = this.width / 2 - 50;
        this.buttonList.add(new GuiButton(1, this.width / 2 - size / 2 , this.height - 38, size, 20, I18n.format("fml.button.continue")));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 1)
            FMLClientHandler.instance().showGuiScreen(null);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        int offset = 20;
        this.drawCenteredString(this.fontRenderer, I18n.format("fml.messages.resources.warning", TextFormatting.RED, TextFormatting.BOLD), this.width / 2, offset, 0xFFFFFF);
        offset+=10;
        this.drawCenteredString(this.fontRenderer, I18n.format("fml.messages.log"), this.width / 2, offset, 0xFFFFFF);
        offset += 25;
        if (!textureOffenders.isEmpty())
        {
            this.drawCenteredString(this.fontRenderer, I18n.format("fml.messages.resources.textures", TextFormatting.RED,TextFormatting.BOLD, TextFormatting.RESET), this.width / 2, offset, 0xFFFFFF);
            for (String mod : textureOffenders)
            {
                offset += 10;
                this.drawCenteredString(this.fontRenderer, mod, this.width / 2, offset, 0xEEEEEE);
            }
            offset += 25;
        }
        if (!modelOffenders.isEmpty())
        {
            this.drawCenteredString(this.fontRenderer, I18n.format("fml.messages.resources.models", TextFormatting.RED,TextFormatting.BOLD, TextFormatting.RESET), this.width / 2, offset, 0xFFFFFF);
            for (String mod : modelOffenders)
            {
                offset += 10;
                this.drawCenteredString(this.fontRenderer, mod, this.width / 2, offset, 0xEEEEEE);
            }
        }
        this.buttonList.forEach(button -> button.drawButton(this.mc, mouseX, mouseY, partialTicks));
    }
}
