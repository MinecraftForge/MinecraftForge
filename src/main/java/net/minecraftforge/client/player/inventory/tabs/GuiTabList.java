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

import java.util.ArrayList;
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
 * 
 * @author Subaraki, Modified by Ash Indigo
 */
public class GuiTabList
{
    /** Dictates how many tabs per page are drawn */
    private int maxTabsPerPage = 12;
    private int page;
    /** Returns how many pages, a.k.a max button clicks to the right, can be done. */
    private int maxPages;
    private GuiContainer guiContainer;
    /** Cache used for comparison */
    private Class<? extends GuiContainer> guiContainerClass;
    private List<GuiTab> tabs;
    private static final ResourceLocation TEXTURE_TABS = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");

    public GuiTabList(GuiContainer guiContainer)
    {
        this.guiContainer = guiContainer;
        this.guiContainerClass = guiContainer.getClass();

        tabs = new ArrayList<GuiTab>(GuiTab.getTabsForGui(this.guiContainer.getClass())); // get a copy of the stored list so it can be modified without consequence
        maxPages = setMaxPages();
        getTabs();
        for (GuiTab tab : getTabs())// Set the first page
            if (tab.isActiveTab(guiContainerClass))
            {
                page = tab.getTabPage();
                break;
            }
    }

    private int setMaxPages()
    {
        if (getTabs().size() % 12 > 0)
        {
            return new Integer(((getTabs().size() - maxTabsPerPage) / maxTabsPerPage) + 1);
        }
        else if (getTabs().size() > 12)
        {
            return new Integer(((getTabs().size() - maxTabsPerPage) / maxTabsPerPage));
        }
        return 0;
    }

    /**
     * Draws all tabs that are not selected behind the texture
     */
    public void drawTabs(boolean isFront, int mouseX, int mouseY, float partialTicks, int guiLeft, int guiTop, int xSize, int ySize, RenderItem itemRender,
            FontRenderer fontRendererObj)
    {

        if (getTabs().size() == 1 && guiContainer instanceof GuiInventory) // Don't draw the base tab if it's the only one
            return;

        int start = page * maxTabsPerPage;
        int end = Math.min(getTabs().size(), (page + 1) * maxTabsPerPage);

        for (GuiTab tab : getTabs())
        {
            if (tab.getTabIndex() >= start && tab.getTabIndex() < end && (tab.isActiveTab(guiContainerClass) || !isFront))
            {
                Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE_TABS);
                tab.draw(guiContainer, isFront, guiLeft, guiTop, xSize, ySize, itemRender, fontRendererObj);
            }
        }
    }

    public void drawTabHoveringText(int mouseX, int mouseY, int guiLeft, int guiTop, int xSize, int ySize, FontRenderer fontRendererObj)
    {
        if (getTabs().size() == 1 && guiContainer instanceof GuiInventory) // Don't draw the base tab if it's the only one
            return;
        int start = page * maxTabsPerPage;
        int end = Math.min(getTabs().size(), (page + 1) * maxTabsPerPage);

        for (GuiTab tab : getTabs())
        {
            if (tab.getTabIndex() >= start && tab.getTabIndex() < end && this.isMouseOverTab(tab, mouseX - guiLeft, mouseY - guiTop, xSize, ySize))
            {
                renderHoveringText(tab, mouseX, mouseY, fontRendererObj);
                break;
            }
        }
    }

    private boolean renderHoveringText(GuiTab tab, int mouseX, int mouseY, FontRenderer fontRendererObj)
    {
        GuiUtils.drawHoveringText(Arrays.<String> asList(new String[] { I18n.format(tab.getLocalizedName()) }), mouseX,
                mouseY, guiContainer.width, guiContainer.height, -1, fontRendererObj);
        return true;
    }

    private boolean isMouseOverTab(GuiTab tab, int mouseX, int mouseY, int xSize, int ySize)
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
        if (getTabs().size() == 1 && guiContainer instanceof GuiInventory) // don't draw the base tab if it's the only one
            return;
        if (state == 0)
        {
            int x = mouseX - guiLeft;
            int y = mouseY - guiTop;

            int start = page * maxTabsPerPage;
            int end = Math.min(getTabs().size(), (page + 1) * maxTabsPerPage);

            for (GuiTab tab : getTabs())
            {
                int index = tab.getTabIndex();
                if (index >= start && index < end && this.isMouseOverTab(tab, x, y, xSize, ySize))
                {
                    tab.onTabClicked();
                    //tab.onTabClicked(Minecraft.getMinecraft().thePlayer);
                    return;
                }
            }
        }
    }

    public void addButtons(List<GuiButton> buttonList, int guiLeft, int guiTop, int xSize, int ySize)
    {
        if (getTabs().size() > maxTabsPerPage)
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

    public List<GuiTab> getTabs()
    {
        return tabs;
    }
}