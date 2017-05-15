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
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * class named after CreativeTabs, but for Survival
 */
public class GuiTab
{
	private static ListMultimap<Class<? extends GuiContainer>, GuiTab> tabRegistry = ArrayListMultimap.create();

	private ItemStack iconStack = null;
	private ResourceLocation iconResLoc = null;
	private String name = null;
	private Class<? extends GuiContainer> parentContainer;
	private Class<? extends GuiContainer> targetGui;
	private final Logger LOGGER = LogManager.getLogger("GUITABS");
	
	public static final GuiTab VANILLA_INVENTORY_TAB = new GuiTab("inventory", new ItemStack(Blocks.CHEST))
	{
		@Override
		public void onTabClicked(EntityPlayerSP player)
		{
			Minecraft.getMinecraft().displayGuiScreen(new GuiInventory(player));
		}

	}.setTargetGui(GuiInventory.class).addTo(GuiInventory.class);

	public GuiTab(String name, ItemStack icon)
	{
		this.name = name;
		this.iconStack = icon;
	}

	public GuiTab(String name, ResourceLocation icon)
	{
		this.name = name;
		this.iconResLoc = icon;
	}

	public void draw(GuiContainer guiContainer, boolean isSelectedtab, int guiLeft, int guiTop, int xSize, int ySize, RenderItem itemRender,
			FontRenderer fontRendererObj)
	{

		boolean isFirstRow = isTopRow();
		int column = getColumn();
		int texX = column * 28;
		int texY = 0;
		int x = guiLeft + 28 * column;
		int y = guiTop;

		if (isSelectedtab)
			texY += 32;

		if (column == 5) // set the last tab to the extreme right of the gui
			x = guiLeft + xSize - 28;

		else if (column > 0)
			x += column;

		if (isFirstRow)
			y = y - 28;
		else
		{
			texY += 64;
			y = y + (ySize - 4);
		}
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.color(1F, 1F, 1F); // Forge: Reset color in case Items change it.
		GlStateManager.enableBlend(); // Forge: Make sure blend is enabled else tabs show a white border.
		guiContainer.drawTexturedModalRect(x, y, texX, texY, 28, 32);
		x = x + 6;
		y = y + 8 + (isFirstRow ? 1 : -1);
		GlStateManager.enableLighting();
		if (getIconStack() != null)
		{
			ItemStack itemstack = getIconStack();
			itemRender.renderItemAndEffectIntoGUI(itemstack, x, y);
			itemRender.renderItemOverlays(fontRendererObj, itemstack, x, y);
		}
		else if(getIconResLoc() != null){
			Minecraft.getMinecraft().getTextureManager().bindTexture(getIconResLoc());
			Gui.drawModalRectWithCustomSizedTexture(x, y, 0f, 0f, 16, 16, 16f, 16f);
		}
		GlStateManager.disableLighting();
	}

	/** returns the index relative to the gui this tab was registered too */
	public int getTabIndex()
	{
		return tabRegistry.get(parentContainer).indexOf(this);
	}

	/** wether this tab is on the top row or not */
	public boolean isTopRow()
	{
		return getTabIndex() > 11 ? ((getTabIndex() - 12) % 12) < 6 : getTabIndex() < 6;
	}

	/** returns index % 6 to determine column */
	public int getColumn()
	{
		return getTabIndex() > 11 ? ((getTabIndex() - 12) % 12) % 6 : getTabIndex() % 6;
	}

	/**
	 * @return wether this tab's parent gui is the current gui
	 */
	public boolean isActiveTab(Class<? extends GuiContainer> guiContainer)
	{
		return guiContainer != null && guiContainer.equals(getTargetGui());
	}

	/**
	 * @return the tabindex / 12
	 */
	public int getTabPage()
	{
		return getTabIndex() / 12;
	}

	/**
	 * @return the localized name for this tab
	 */
	public String getLocalizedName()
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
	 * @return a list of the appended tabs for the given guicontainer
	 */
	public static List<GuiTab> getTabsForGui(Class<? extends GuiContainer> guiContainer)
	{
		return tabRegistry.containsKey(guiContainer) ? tabRegistry.get(guiContainer) : new ArrayList<GuiTab>();
	}

	public GuiTab addTo(Class<? extends GuiContainer> parentContainer){
		if(parentContainer.equals(GuiContainerCreative.class)){
			LOGGER.warn("Cannot add tabs to creative inventory. Use CreativeTabs instead");
			return this;
		}
		else if(parentContainer.equals(InventoryEffectRenderer.class)){
			LOGGER.warn(name + " tab has a parent that is ambigious with the creative inventory. cannot proceed with registry.");
			return this;
		}
		
		this.parentContainer = parentContainer;
		tabRegistry.put(parentContainer, this);
		return this;
	}

	/**
	 * sets the GuiContainer this tab should show on as the main tab.
	 */
	public GuiTab setTargetGui(Class<? extends GuiContainer> targetGui){
		this.targetGui = targetGui;
		return this;
	}

	/**
	 * process interaction when the tab is clicked. should be used to send a packet to the server and open a gui
	 */
	public void onTabClicked(EntityPlayerSP player){}

	/**
	 * returns the GuiContainer this tab should be linked too. not affiliated with opening the gui. can be null if no gui should lead to this tab. mostly used to know when a tab is
	 * on an affiliated gui, and draw the tab texture in of the others.
	 */
	public Class<? extends GuiContainer> getTargetGui(){
		return this.targetGui;
	}
}
