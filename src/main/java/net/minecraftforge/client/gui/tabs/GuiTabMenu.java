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
package net.minecraftforge.client.gui.tabs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiUtils;

/**
 * Enclosing class to keep GuiContainer clean. This handles all the survival tab drawing, and the addition of two buttons.
 *
 * @author Subaraki, Modified by Ash Indigo
 */
public class GuiTabMenu
{
    /**
     * Dictates how many tabs per page are drawn
     */
    public static final int MAX_TABS_PER_PAGE = 12;
    private static final ResourceLocation TEXTURE_TABS = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");

    private int currentPage;
    /**
     * Returns how many pages, a.k.a max button clicks to the right, can be done.
     */
    private int maxPages;
    private GuiContainer guiContainer;
    /**
     * Cache used for comparison
     */
    private Class<? extends GuiContainer> guiContainerClass;
    private List<GuiTab> tabs;
    private int start;
    private int end;
    private GuiButtonExt leftPageButton;
    private GuiButtonExt rightPageButton;
    private int posNormalXLeft;
    private int posNormalXRight;
    private int mouseX;
    private int mouseY;
    private float partialTicks;

    public GuiTabMenu(GuiContainer guiContainer)
    {
        tabs = new ArrayList<>(); // get a copy of the stored list so it can be modified without consequence
        List<GuiTab> tabsForGui;
        this.setGuiContainer(guiContainer);
        tabsForGui = GuiTab.getTabsForGui(this.guiContainerClass);
        if (tabsForGui.size() > 1)
        {
            tabs.addAll(tabsForGui);
        }
        maxPages = setMaxPages();
        for (GuiTab tab : getTabs())// Set the first page
        {
            if (tab.isActiveTab(guiContainerClass))
            {
                currentPage = tab.getPage();
                break;
            }
        }
    }

    private int setMaxPages()
    {
        return (getTabsSize() + MAX_TABS_PER_PAGE - 1) / MAX_TABS_PER_PAGE;
    }

    /**
     * Draws all tabs that are not selected behind the texture
     */
    public void drawTabs(boolean isFront, int mouseX, int mouseY, float partialTicks, int guiLeft, int guiTop, int xSize, int ySize, RenderItem itemRender,
            FontRenderer fontRenderer)
    {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.partialTicks = partialTicks;
        this.setStart();
        this.setEnd();

        for (GuiTab tab : getTabs())
        {
            int index = tab.getIndex();
            if (isInRange(index, start, end) && (tab.isActiveTab(guiContainerClass) || !isFront))
            {
                Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE_TABS);
                tab.draw(guiContainer, isFront, guiLeft, guiTop, xSize, ySize, itemRender, fontRenderer);
            }
        }

        if (getTabsSize() > MAX_TABS_PER_PAGE)
        {
            String page = String.format("%d / %d", this.currentPage + 1, maxPages + 1);
            int width = fontRenderer.getStringWidth(page); // 44 Originally
            GlStateManager.disableLighting();
            fontRenderer.drawString(page, guiLeft + (xSize / 2) - (width / 2), guiTop - 43, -1);
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableLighting();
    }

    public void drawTabHoveringText(int mouseX, int mouseY, int guiLeft, int guiTop, int xSize, int ySize, FontRenderer fontRenderer)
    {
        this.setStart();
        this.setEnd();

        for (GuiTab tab : getTabs())
        {
            int index = tab.getIndex();
            if (isInRange(index, start, end) && this.isMouseOverTab(tab, mouseX - guiLeft, mouseY - guiTop, xSize, ySize))
            {
                renderHoveringText(tab, mouseX, mouseY, fontRenderer);
                break;
            }
        }
    }

    private boolean renderHoveringText(GuiTab tab, int mouseX, int mouseY, FontRenderer fontRenderer)
    {
        RenderHelper.enableGUIStandardItemLighting();
        GuiUtils.drawHoveringText(Collections.singletonList(I18n.format(tab.getUnlocalizedName())), mouseX,
                mouseY, guiContainer.width, guiContainer.height, -1, fontRenderer);
        return true;
    }

    private boolean isMouseOverTab(GuiTab tab, int mouseX, int mouseY, int xSize, int ySize)
    {
        int column = tab.getColumn();
        int xOffset = 28 * column;
        int yOffset = 0;

        if (column == 5)
        {
            xOffset = xSize - 28 + 2;
        }
        else if (column > 0)
        {
            xOffset += column;
        }

        yOffset = tab.isTopRow() ? yOffset - 26 : yOffset + ySize;

        return mouseX >= xOffset && mouseX <= xOffset + 26 && mouseY >= yOffset && mouseY <= yOffset + 28;
    }

    public void onMouseRelease(int guiLeft, int guiTop, int xSize, int ySize, int state, int mouseX, int mouseY)
    {
        if (state == 0)
        {
            int x = mouseX - guiLeft;
            int y = mouseY - guiTop;

            this.setStart();
            this.setEnd();

            for (GuiTab tab : getTabs())
            {
                int index = tab.getIndex();
                if (isInRange(index, start, end) && this.isMouseOverTab(tab, x, y, xSize, ySize))
                {
                    tab.onTabClicked(guiContainer);
                    guiContainer.mc.player.getRecipeBook().setGuiOpen(false);
                    correctButtonAlignment();
                    return;
                }
            }
        }
    }

    public void addButtons(List<GuiButton> buttonList, int guiLeft, int guiTop, int xSize, int ySize)
    {
        if (getTabsSize() > MAX_TABS_PER_PAGE)
        {
            posNormalXLeft = guiLeft;
            posNormalXRight = guiLeft + xSize - 20;
            int posY = guiTop - 49;
            this.setLeftPageButton(new GuiButtonExt(201, posNormalXLeft, posY, 20, 20, "<"));
            this.setRightPageButton(new GuiButtonExt(202, posNormalXRight, posY, 20, 20, ">"));
            buttonList.add(leftPageButton);
            buttonList.add(rightPageButton);
            this.correctButtonAlignment();
            maxPages = (int) Math.ceil((getTabsSize() - 12) / 10D);
        }
    }

    public void actionPerformed(GuiButton button)
    {
        if (button.id == 201)
        {
            this.setCurrentPage(Math.max(currentPage - 1, 0));
        }
        else if (button.id == 202)
        {
            this.setCurrentPage(Math.min(currentPage + 1, maxPages));
        }
        else if (button.id == 10)
        {
            if (button instanceof GuiButtonImage)
            {
                this.setButtonAlignment();
            }
        }
    }

    public void setCurrentPage(int currentPage)
    {
        this.currentPage = currentPage;
    }

    public List<GuiTab> getTabs()
    {
        return tabs;
    }

    private boolean isInRange(int index, int start, int end)
    {
        return index >= start && index < end;
    }

    public void setStart()
    {
        this.start = currentPage * MAX_TABS_PER_PAGE;
    }

    public void setEnd()
    {
        this.end = Math.min(getTabsSize(), (currentPage + 1) * MAX_TABS_PER_PAGE);
    }

    public int getTabsSize()
    {
        return getTabs() == null ? 0 : getTabs().size();
    }

    public void setLeftPageButton(GuiButtonExt leftPageButton)
    {
        this.leftPageButton = leftPageButton;
    }

    public void setRightPageButton(GuiButtonExt rightPageButton)
    {
        this.rightPageButton = rightPageButton;
    }

    private void correctButtonAlignment()
    {
        if (guiContainer instanceof GuiCrafting)
        {
            if (((GuiCrafting) guiContainer).mc.player.getRecipeBook().isGuiOpen())
            {
                this.setButtonAlignment();
            }
        }
        if (guiContainer instanceof GuiInventory)
        {
            if (((GuiInventory) guiContainer).mc.player.getRecipeBook().isGuiOpen())
            {
                this.setButtonAlignment();
            }
        }
    }

    private void setButtonAlignment()
    {
        if (leftPageButton != null)
        {
            leftPageButton.x = leftPageButton.x == posNormalXLeft ? posNormalXLeft + 77 : posNormalXLeft;
        }
        if (rightPageButton != null)
        {
            rightPageButton.x = rightPageButton.x == posNormalXRight ? posNormalXRight + 77 : posNormalXRight;
        }
    }

    private void setGuiContainer(GuiContainer guiContainer)
    {
        this.guiContainer = guiContainer;
        this.guiContainerClass = guiContainer.getClass();
    }
}