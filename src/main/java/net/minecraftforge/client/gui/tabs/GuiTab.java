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

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.base.Predicates;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * Class used to add a new GuiTab
 */
@SideOnly(Side.CLIENT)
public class GuiTab
{
    private static ListMultimap<Class<? extends GuiContainer>, GuiTab> tabRegistry = ArrayListMultimap.create();

    private ItemStack iconStack = ItemStack.EMPTY;
    @Nullable
    private ResourceLocation iconResLoc = null;
    private String name;
    protected Class<? extends GuiContainer> parentContainer;
    private Class<? extends GuiContainer> targetGui;
    protected static Minecraft mc = Minecraft.getMinecraft();

    static
    {
        new DefaultVanillaGuiTabs();
    }

    /**
     * Internal use for the vanilla default tabs
     *
     * @param name The name of the tab
     */
    GuiTab(String name)
    {
        this.name = name;
    }

    /**
     * ItemStack version of tabs
     *
     * @param name The name of the tab
     * @param icon The icon represented by an ItemStack
     */
    public GuiTab(String name, ItemStack icon)
    {
        this(name);
        this.iconStack = icon;
    }

    /**
     * ItemStack version of tabs with a set target Gui
     *
     * @param name  The name of the tab
     * @param icon  The icon represented by an ItemStack
     * @param clazz The gui class that is going to be displayed
     */
    public GuiTab(String name, ItemStack icon, Class<? extends GuiContainer> clazz)
    {
        this(name, icon);
        this.setTargetGui(clazz);
    }

    /**
     * ResourceLocation version of tabs
     *
     * @param name The name of the tab
     * @param icon The icon of the tab
     */
    public GuiTab(String name, ResourceLocation icon)
    {
        this(name);
        this.iconResLoc = icon;
    }

    /**
     * ResourceLocation version of tabs with a set target Gui
     *
     * @param name  The name of the tab
     * @param icon  The icon of the tab
     * @param clazz The gui class that is going to be displayed
     */
    public GuiTab(String name, ResourceLocation icon, Class<? extends GuiContainer> clazz)
    {
        this(name, icon);
        this.setTargetGui(clazz);
    }

    public void draw(GuiContainer guiContainer, boolean isSelectedtab, int guiLeft, int guiTop, int xSize, int ySize, RenderItem itemRender,
            FontRenderer fontRenderer)
    {

        boolean isFirstRow = isTopRow();
        int column = getColumn();
        int texX = column * 28;
        int texY = 0;
        int x = guiLeft + 28 * column;
        int y = guiTop;

        if (isSelectedtab)
        {
            texY += 32;
        }

        if (isAlignedRight())
        {
            x = guiLeft + xSize - 28 * (6 - column);
        }
        else if (column > 0)
        {
            x += column;
        }

        if (isFirstRow)
        {
            y -= 28;
        }
        else
        {
            texY += 64;
            y += (ySize - 5);
        }
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.color(1F, 1F, 1F); // Reset color in case Items change it.
        GlStateManager.enableBlend(); // Make sure blend is enabled else tabs show a white border.
        guiContainer.drawTexturedModalRect(x, y, texX, texY, 28, 32);
        x += 6;
        y += 8 + (isFirstRow ? 1 : -1);
        GlStateManager.enableLighting();
        if (!getIconStack().isEmpty())
        {
            ItemStack itemstack = getIconStack();
            itemRender.renderItemAndEffectIntoGUI(itemstack, x, y);
            itemRender.renderItemOverlays(fontRenderer, itemstack, x, y);
        }
        else if (getIconResLoc() != null)
        {
            Minecraft.getMinecraft().getTextureManager().bindTexture(getIconResLoc());
            Gui.drawModalRectWithCustomSizedTexture(x, y, 0f, 0f, 16, 16, 16f, 16f);
        }
        GlStateManager.disableLighting();
    }

    public boolean isAlignedRight()
    {
        return this.getColumn() == 5;
    }

    /**
     * Returns the index relative to the gui this tab was registered too
     */
    public int getIndex()
    {
        return tabRegistry.get(parentContainer).indexOf(this);
    }

    /**
     * Whether this tab is on the top row or not
     */
    public boolean isTopRow()
    {
        return (getIndex() > 11 ? (getIndex() - 12) % 12 : this.getIndex()) < 6;
    }

    /**
     * Returns index % 6 to determine column
     */
    public int getColumn()
    {
        return (getIndex() > 11 ? (getIndex() - 12) % 12 : this.getIndex()) % 6;
    }

    /**
     * @return Whether this tab's parent GUI is the current GUI
     */
    public boolean isActiveTab(Class<? extends GuiContainer> guiContainer)
    {
        return guiContainer == getTargetGui();
    }

    /**
     * @return The tab index / 12
     */
    public int getPage()
    {
        return getIndex() / 12;
    }

    /**
     * @return The unlocalized name for this tab
     */
    public String getUnlocalizedName()
    {
        return "guiGroup." + name;
    }

    public ResourceLocation getIconResLoc()
    {
        return iconResLoc;
    }

    public ItemStack getIconStack()
    {
        return iconStack;
    }

    /**
     * @return A list of the appended tabs for the given GuiContainer
     */
    public static List<GuiTab> getTabsForGui(Class<? extends GuiContainer> guiContainer)
    {
        return tabRegistry.get(guiContainer);
    }

    /**
     * Adds the GuiTab to GuiContainer
     *
     * @param parentContainer The GuiContainer to add a tab too
     * @return The GuiTab
     */
    public GuiTab addTo(Class<? extends GuiContainer> parentContainer)
    {
        if (parentContainer == GuiContainerCreative.class)
        {
            throw new IllegalArgumentException("Cannot add tabs to creative inventory. Use CreativeTabs instead");
        }
        else if (parentContainer == InventoryEffectRenderer.class)
        {
            throw new IllegalArgumentException(name + " tab has a parent that is ambiguous with the creative inventory. cannot proceed with registry.");
        }

        this.parentContainer = parentContainer;
        tabRegistry.put(parentContainer, this);
        return this;
    }

    /**
     * Adds the GuiTab to GuiContainers
     *
     * @param parentContainers The GuiContainers to add a tab too
     * @return The GuiTab
     */
    public GuiTab addTo(Class<? extends GuiContainer>... parentContainers)
    {
        Arrays.stream(parentContainers).forEach(this::addTo);
        return this;
    }

    /**
     * Sets the GuiContainer this tab should show on as the main tab.
     *
     * @param targetGui The target gui to shown on
     * @return The GuiTab instance
     */
    public GuiTab setTargetGui(Class<? extends GuiContainer> targetGui)
    {
        this.targetGui = targetGui;
        return this;
    }

    /**
     * Process interaction when the tab is clicked. should be used to send a packet to the server and open a GUI
     *
     * @param guiContainer The parent gui
     */
    public void onTabClicked(GuiContainer guiContainer)
    {
        List<GuiTab> targetTabs = getTabsForGui(targetGui);
        List<GuiTab> tabs = getTabsForGui(guiContainer.getClass());
        tabs.stream().filter(Predicates.not(targetTabs::contains)).forEach(t -> t.addTo(targetGui));
    }

    /**
     * Returns the GuiContainer this tab should be linked too. not affiliated with opening the GUI. can be null if no GUI should lead to this tab. mostly used to know when a tab is
     * on an affiliated GUI, and draw the tab texture in of the others.
     */
    public Class<? extends GuiContainer> getTargetGui()
    {
        return this.targetGui;
    }
}