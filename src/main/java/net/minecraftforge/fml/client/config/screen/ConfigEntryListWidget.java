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

package net.minecraftforge.fml.client.config.screen;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraftforge.fml.client.config.entry.ConfigListEntry;
import net.minecraftforge.fml.client.gui.widget.Slider;
import org.lwjgl.opengl.GL11;

/**
 * This class implements the scrolling list functionality of the ConfigScreen.
 *
 * @author bspkrs
 * @author Cadiboo
 */
public class ConfigEntryListWidget extends ExtendedList<ConfigListEntry<?>> {

	/**
	 * The empty space between the entries and both sides of the screen.
	 */
	public static final int PADDING_X = 10;
	/**
	 * The longest size a label of a config element can be before it is trimmed with an ellipsis.
	 * If it is trimmed, the new text (including the ellipsis) will always be equal to this value.
	 */
	public static final int MAX_LABEL_WIDTH = 300;

	public final ConfigScreen owningScreen;
	/**
	 * The size of the largest label of all the config elements on this screen.
	 * Always smaller than or equal to {@link #MAX_LABEL_WIDTH};
	 * Used for rendering all widgets at the same x position
	 */
	private int longestLabelWidth;
	/**
	 * Exists to make the hacks used to stop the dirt background rendering work.
	 */
	private boolean doHackery = false;

	public ConfigEntryListWidget(final ConfigScreen owningScreen) {
		super(Minecraft.getInstance(), owningScreen.width, owningScreen.height, owningScreen.getHeaderSize(), owningScreen.height - owningScreen.getFooterSize(), 20);
		this.owningScreen = owningScreen;
	}

	public int getLongestLabelWidth() {
		this.children().forEach(configListEntry -> {
			final int labelWidth = configListEntry.getLabelWidth();
			if (longestLabelWidth < labelWidth)
				longestLabelWidth = labelWidth;
		});
		longestLabelWidth = Math.min(longestLabelWidth, MAX_LABEL_WIDTH);
		return longestLabelWidth;
	}

	/**
	 * First mimics the logic from {@link Screen#init(Minecraft, int, int)} (Set bounds, clear children, clear focused)
	 * then mimics the logic from {@link Screen#i¬nit()} (Add Widgets)
	 */
	public void init() {
		// Screen#init(Minecraft, int, int)
		setBounds();

		this.children().clear();
		this.setFocused(null);

		// Screen#init()
		// No logic here, all widgets are added by the owningScreen
	}

	/**
	 * Updates the bounds of the entry list.
	 * Would be inlined into {@link #init()} but is needed to make the hacks used to stop the dirt background rendering work.
	 */
	public void setBounds() {
		// top
		this.y0 = owningScreen.getHeaderSize();
		// bottom
		this.y1 = owningScreen.height - owningScreen.getFooterSize();
		// left
		this.x0 = 0;
		// right
		this.x1 = owningScreen.width;

		this.width = this.x1 - this.x0;
		this.height = this.y1 - this.y0;
	}

	/**
	 * @return The width minus the padding (from both sides)
	 */
	public int getRowWidth() {
		return getWidth() - PADDING_X * 2;
	}

	/**
	 * Exists to make the hacks used to stop the dirt background rendering work.
	 * Renders a translucent gray background instead of the dirt background
	 */
	@Override
	protected void renderBackground() {
		this.fillGradient(this.getLeft(), this.getTop(), this.getRight(), this.getBottom(), 0x70_00_00_00, 0x70_00_00_00);
	}

	@Override
	public void render(final int mouseX, final int mouseY, final float partialTicks) {
		// GLScissors to hide overflow from entryList
		GL11.glEnable(GL11.GL_SCISSOR_TEST);

		final MainWindow mainWindow = minecraft.func_228018_at_();
		double scale = mainWindow.getGuiScaleFactor();
		// Scissors coords are relative to the bottom left of the screen
		int scissorsX = (int) (this.getLeft() * scale);
		int scissorsY = (int) (mainWindow.getFramebufferHeight() - (this.getBottom() * scale));
		int scissorsWidth = (int) (this.getWidth() * scale);
		int scissorsHeight = (int) (this.getHeight() * scale);

		GL11.glScissor(scissorsX, scissorsY, scissorsWidth, scissorsHeight);

		// Dirty hacks are used to stop the dirt texture from being rendered.
		// see getRowLeft and getScrollbarPosition.
		doHackery = true;
		super.render(mouseX, mouseY, partialTicks);

		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}

	/**
	 * Override exists to make the hacks used to stop the dirt background rendering work.
	 */
	@Override
	protected int getScrollbarPosition() {
		final int right = getRight();
		if (doHackery) {
			// Dirty hack to stop the dirt background from being visible.
			// Makes all sizes 0 so when the dirt background is rendered nothing is visible.
			x0 = x1 = y0 = y1 = 0;
			doHackery = false;
		}
		return right - 6; // 6 = scrollbar width
	}

	@Override
	public boolean mouseClicked(final double mouseX, final double mouseY, final int mouseEvent) {
		// Unfocus all TextFieldWidgets otherwise their cursor still appears. Vanilla bug that they don't do it themselves?
		this.children().forEach(configListEntry -> {
			final Widget widget = configListEntry.getWidget();
			if (widget instanceof TextFieldWidget)
				((TextFieldWidget) widget).setFocused2(false);
			else if (widget instanceof Slider) // Stop sliders moving around when you've clicked somewhere else
				((Slider) widget).dragging = false;
		});
		return super.mouseClicked(mouseX, mouseY, mouseEvent);
	}

	/**
	 * Override exists to make the hacks used to stop the dirt background rendering work.
	 */
	@Override
	protected int getRowLeft() {
		this.setBounds(); // Revert dirty hack to stop the dirt background from being visible.
		return super.getRowLeft();
	}

	/**
	 * Override exists to make the hacks used to stop the dirt background rendering work.
	 */
	@Override
	protected void renderHoleBackground(final int p_renderHoleBackground_1_, final int p_renderHoleBackground_2_, final int p_renderHoleBackground_3_, final int p_renderHoleBackground_4_) {
		// No-op We use GLScissors instead of hiding overflow afterwards by rendering over the top
	}

	/**
	 * This method is a pass-through for IConfigEntry objects that contain {@link TextFieldWidget} elements.
	 * Called from the parent ConfigScreen.
	 */
	public void tick() {
		for (ConfigListEntry<?> entry : this.getListEntries())
			entry.tick();
	}

	/**
	 * This method is a pass-through for IConfigEntry objects that need to perform actions
	 * when the containing GUI is closed.
	 * Called from the parent ConfigScreen.
	 */
	public void onClose() {
		for (ConfigListEntry<?> entry : this.getListEntries())
			entry.onGuiClosed();
	}

	/**
	 * Saves all entries.
	 */
	public void save() {
		for (ConfigListEntry<?> entry : this.getListEntries())
			if (entry.isChanged())
				entry.save();
	}

	/**
	 * Checks if all IConfigEntry objects on this screen are set to default.
	 *
	 * @param applyToSubcategories If sub-category objects should be checked as well.
	 * @return If all IConfigEntry objects on this screen are set to default.
	 */
	public boolean areAllEntriesDefault(boolean applyToSubcategories) {
		for (ConfigListEntry<?> entry : this.getListEntries())
			if (applyToSubcategories || !entry.isCategory())
				if (!entry.isDefault())
					return false;
		return true;
	}

	/**
	 * Resets all IConfigEntry objects on this screen to their default values.
	 *
	 * @param applyToSubcategories If sub-category objects should be reset as well.
	 */
	public void resetAllToDefault(boolean applyToSubcategories) {
		for (ConfigListEntry<?> entry : this.getListEntries())
			if (applyToSubcategories || !entry.isCategory())
				entry.resetToDefault();
	}

	/**
	 * Checks if any IConfigEntry objects on this screen are changed.
	 *
	 * @param applyToSubcategories If sub-category objects should be checked as well.
	 * @return If any IConfigEntry objects on this screen are changed.
	 */
	public boolean areAnyEntriesChanged(boolean applyToSubcategories) {
		for (ConfigListEntry<?> entry : this.getListEntries())
			if (applyToSubcategories || !entry.isCategory())
				if (entry.isChanged())
					return true;
		return false;
	}

	/**
	 * Reverts changes to all IConfigEntry objects on this screen.
	 *
	 * @param applyToSubcategories If sub-category objects should be reverted as well.
	 */
	public void undoAllChanges(boolean applyToSubcategories) {
		for (ConfigListEntry<?> entry : this.getListEntries())
			if (applyToSubcategories || !entry.isCategory())
				entry.undoChanges();
	}

	/**
	 * @param mouseX       The x coordinate of the mouse pointer on the screen
	 * @param mouseY       The y coordinate of the mouse pointer on the screen
	 * @param partialTicks The partial render ticks elapsed
	 */
	public void postRender(final int mouseX, final int mouseY, final float partialTicks) {
		for (int item = 0; item < this.getItemCount(); item++) {
			int itemTop = this.getRowTop(item);
//			int itemBottom = this.getRowBottom(item); // Private, logic copied below
			int itemBottom = this.getRowTop(item) + this.itemHeight;
			if (itemTop <= this.y1 && itemBottom >= this.y0) {
				ConfigListEntry<?> configListEntry = this.getEntry(item);
				configListEntry.renderToolTip(mouseX, mouseY, partialTicks);
			}
		}
	}

	public Iterable<? extends ConfigListEntry<?>> getListEntries() {
		return children();
	}

}
