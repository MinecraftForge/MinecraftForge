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
/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */
package net.minecraftforge.client.player.inventory.tabs;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;

/**
 * Enclosing class to keep GuiContainer clean. This handles all the survival tab drawing, and the addition of two buttons.
 */
public class SurvivalTabList
{

    private int page;
    private int maxPages;
    private GuiContainer container;
    private List<SurvivalTabs> tabs;
    private static final ResourceLocation SURVIVAL_INVENTORY_TABS = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");

    public SurvivalTabList(GuiContainer container)
    {
        this.container = container;
        tabs = SurvivalTabs.getTabsForContainer(container.getClass());
        maxPages = ((tabs.size() - 12) / 12) + 1;

        for (SurvivalTabs tab : tabs)//set the first page
            if (container.getClass().equals(tab.getTabContainer()))
            {
                page = tab.getTabPage();
                break;
            }
    }

    /**
     * draws all tabs that are not selected behind the texture
     */
    public void drawTabs(boolean isFront, int mouseX, int mouseY, float partialTicks, int guiLeft, int guiTop, int xSize, int ySize, RenderItem itemRender,
            FontRenderer fontRendererObj)
    {

        if (tabs.size() == 1 && container.getClass().equals(GuiInventory.class)) // don't draw the base tab if it's the only one
            return;

        int start = page * 12;
        int end = Math.min(tabs.size(), (page + 1) * 12);

        for (SurvivalTabs tab : tabs)
        {
            if (tab.getTabIndex() >= start && tab.getTabIndex() < end)
            {

                if (!tab.isActiveTab(container.getClass()) && !isFront)
                {
                    Minecraft.getMinecraft().getTextureManager().bindTexture(SURVIVAL_INVENTORY_TABS);
                    tab.draw(container, isFront, guiLeft, guiTop, xSize, ySize, itemRender, fontRendererObj);
                }

                if (tab.isActiveTab(container.getClass()) && isFront)
                {
                    Minecraft.getMinecraft().getTextureManager().bindTexture(SURVIVAL_INVENTORY_TABS);
                    tab.draw(container, isFront, guiLeft, guiTop, xSize, ySize, itemRender, fontRendererObj);
                }
            }
        }
    }

    public void drawTabHoveringText(GuiContainer screen, int mouseX, int mouseY, int guiLeft, int guiTop, int xSize, int ySize, FontRenderer fontRendererObj)
    {
        if (tabs.size() == 1 && container.getClass().equals(GuiInventory.class)) // don't draw the base tab if it's the only one
            return;
        int start = page * 12;
        int end = Math.min(tabs.size(), (page + 1) * 12);

        for (SurvivalTabs tab : tabs)
        {
            if (tab.getTabIndex() >= start && tab.getTabIndex() < end)
            {
                if (tab != null && this.isMouseOverTab(tab, mouseX - guiLeft, mouseY - guiTop, xSize, ySize))
                {
                    renderSurvivalInventoryHoveringText(tab, screen, mouseX, mouseY, fontRendererObj);
                    break;
                }
            }
        }
    }

    private boolean renderSurvivalInventoryHoveringText(SurvivalTabs tab, GuiContainer screen, int mouseX, int mouseY, FontRenderer fontRendererObj)
    {
        GuiUtils.drawHoveringText(Arrays.<String> asList(new String[] { I18n.format(tab.getLocalizedName()) }), mouseX,
                mouseY, screen.width, screen.height, -1, fontRendererObj);
        return true;
    }

    private boolean isMouseOverTab(SurvivalTabs tab, int mouseX, int mouseY, int xSize, int ySize)
    {
        int column = tab.getColumn();
        int xOffset = 28 * column;
        int yOffset = 0;

        if (column == 5)
            xOffset = xSize - 28 + 2;
        else if (column > 0)
            xOffset += column;

        if (tab.isTopRow())
            yOffset = yOffset - 26;
        else
            yOffset = yOffset + ySize;

        return mouseX >= xOffset && mouseX <= xOffset + 26 && mouseY >= yOffset && mouseY <= yOffset + 28;
    }

    public void onMouseRelease(int guiLeft, int guiTop, int xSize, int ySize, int state, int mouseX, int mouseY)
    {
        if (tabs.size() == 1 && container.getClass().equals(GuiInventory.class)) // don't traw the base tab if it's the only one
            return;
        if (state == 0)
        {
            int x = mouseX - guiLeft;
            int y = mouseY - guiTop;

            int start = page * 12;
            int end = Math.min(tabs.size(), (page + 1) * 12);

            for (SurvivalTabs tab : tabs)
            {
                int index = tab.getTabIndex();
                if (index >= start && index < end)
                {
                    if (tab != null && this.isMouseOverTab(tab, x, y, xSize, ySize))
                    {
                        tab.onTabClicked(Minecraft.getMinecraft().thePlayer);
                        return;
                    }
                }
            }
        }
    }

    public void addButtons(List<GuiButton> buttonList, int guiLeft, int guiTop, int xSize, int ySize)
    {
        if (tabs.size() > 12)
        {
            buttonList.add(new GuiButton(101, guiLeft - 24, guiTop - 22, 20, 20, "<"));
            buttonList.add(new GuiButton(102, guiLeft + xSize + 4, guiTop - 22, 20, 20, ">"));
        }
    }

    public void actionPerformed(GuiButton button)
    {
        if (button.id == 101)
        {
            page = Math.max(page - 1, 0);
        }
        else if (button.id == 102)
        {
            page = Math.min(page + 1, maxPages);
        }
    }
}
