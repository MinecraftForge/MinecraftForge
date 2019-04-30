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

package net.minecraftforge.fml.client;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class GuiResourceError extends GuiErrorScreen
{
    private final SetMultimap<String, String> modnameByType;

    private List guiList;
    private int textSize;

    /**
     * @param modidByType ResourceType 1<->N ModIDs
     */
    public GuiResourceError(SetMultimap<String, String> modidByType)
    {
        super("GuiResourceError", "Errored Resources Found");
        this.modnameByType = HashMultimap.create();

        final Map<String, ModContainer> containerLookup = Loader.instance().getIndexedModList();
        modidByType.forEach((resourceType, modId) ->
        {
            ModContainer container = containerLookup.get(modId);
            this.modnameByType.put(resourceType, container != null ? container.getName() : modId);
        });

        textSize = 35 + modnameByType.keys().size() * 10 + modnameByType.keySet().size() * 20;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.buttonList.clear();
        int buttonWidth = Math.min(this.width / 2 - 50, 397);
        this.buttonList.add(new GuiButton(1, this.width / 2 - buttonWidth / 2, this.height - 25, buttonWidth, 20, I18n.format("fml.button.continue")));

        guiList = new List(mc, width - 20, height, 10, height - 30, textSize);
        guiList.setSlotXBoundsFromLeft(10);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 1)
            FMLClientHandler.instance().showGuiScreen(null);
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        this.guiList.handleMouseInput();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.guiList.drawScreen(mouseX, mouseY, partialTicks);
        this.buttonList.forEach(button -> button.drawButton(this.mc, mouseX, mouseY, partialTicks));
    }

    private class List extends GuiSlot
    {

        public List(Minecraft mc, int width, int height, int topIn, int bottomIn, int slotHeightIn)
        {
            super(mc, width, height, topIn, bottomIn, slotHeightIn);
        }

        @Override
        protected int getSize()
        {
            return 1;
        }

        @Override
        protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY)
        {
        }

        @Override
        protected int getScrollBarX()
        {
            return right - 6;
        }

        @Override
        protected boolean isSelected(int slotIndex)
        {
            return false;
        }

        @Override
        protected void drawBackground()
        {
        }

        @Override
        protected void drawSlot(int slotIndex, int xPos, int yPos, int heightIn, int mouseXIn, int mouseYIn, float partialTicks)
        {
            int offset = yPos + 5;
            drawCenteredString(fontRenderer, I18n.format("fml.messages.resources.warning", TextFormatting.RED, TextFormatting.BOLD), this.width / 2, offset, 0xFFFFFF);
            offset += 10;
            drawCenteredString(fontRenderer, I18n.format("fml.messages.log"), this.width / 2, offset, 0xFFFFFF);
            offset += 20;

            for (Map.Entry<String, Set<String>> type : Multimaps.asMap(modnameByType).entrySet())
            {
                drawCenteredString(fontRenderer, I18n.format("fml.messages.resources." + type.getKey(), TextFormatting.RED, TextFormatting.BOLD, TextFormatting.RESET), this.width / 2, offset, 0xFFFFFF);
                for (String mod : type.getValue())
                {
                    offset += 10;
                    drawCenteredString(fontRenderer, mod, this.width / 2, offset, 0xEEEEEE);
                }
                offset += 20;
            }
        }
    }
}
