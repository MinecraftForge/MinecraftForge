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

package net.minecraftforge.fml.client.config.entry;

import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.entry.widget.BooleanButton;
import net.minecraftforge.fml.client.config.entry.widget.IConfigListEntryWidget;
import net.minecraftforge.fml.client.config.screen.ConfigScreen;
import net.minecraftforge.fml.client.gui.HoverChecker;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static net.minecraftforge.fml.client.gui.GuiUtils.UNDO_CHAR;

/**
 * A ConfigListEntry for an element in a {@link List}
 * Has the undo changes, add entry below and remove entry buttons on the right.
 *
 * @param <T> The type of the config object (e.g. Boolean/Float).
 * @author bspkrs
 * @author Cadiboo
 */
public abstract class ListConfigListEntry<T> extends ConfigListEntry<T> {

	@Nullable
	protected final ExtendedButton undoChangesButton, addEntryBelowButton, removeEntryButton;
	@Nullable
	protected final List<String> undoChangesButtonTooltip, addEntryBelowTooltip, removeEntryTooltip;
	@Nullable
	protected final HoverChecker undoChangesButtonHoverChecker, addEntryBelowButtonHoverChecker, removeEntryButtonHoverChecker;

	public <W extends Widget & IConfigListEntryWidget<T>> ListConfigListEntry(final ConfigScreen owningScreen, final W widget, final boolean enableUndoChangesButton, final boolean enableAddNewEntryBelowButton, final boolean enableRemoveEntryButton) {
		super(owningScreen, widget);
		if (enableUndoChangesButton) {
			this.children().add(this.undoChangesButton = new ExtendedButton(0, 0, 0, 0, UNDO_CHAR, b -> this.undoChanges()));
			this.undoChangesButtonTooltip = Collections.singletonList(I18n.format("fml.configgui.undoChanges.tooltip.single"));
			this.undoChangesButtonHoverChecker = new HoverChecker(this.undoChangesButton, ConfigScreen.HOVER_THRESHOLD);
		} else {
			this.undoChangesButton = null;
			this.undoChangesButtonTooltip = null;
			this.undoChangesButtonHoverChecker = null;
		}
		if (enableAddNewEntryBelowButton) {
			this.children().add(this.addEntryBelowButton = new ExtendedButton(0, 0, 0, 0, "+", b -> this.addEntryBelow()));
			this.addEntryBelowTooltip = Collections.singletonList(I18n.format("fml.configgui.tooltip.addNewEntryBelow"));
			this.addEntryBelowButtonHoverChecker = new HoverChecker(this.addEntryBelowButton, ConfigScreen.HOVER_THRESHOLD);
			this.addEntryBelowButton.setFGColor(BooleanButton.getColor(true));
		} else {
			this.addEntryBelowButton = null;
			this.addEntryBelowTooltip = null;
			this.addEntryBelowButtonHoverChecker = null;
		}
		if (enableRemoveEntryButton) {
			this.children().add(this.removeEntryButton = new ExtendedButton(0, 0, 0, 0, "-", b -> this.removeEntry()));
			this.removeEntryTooltip = Collections.singletonList(I18n.format("fml.configgui.tooltip.removeEntry"));
			this.removeEntryButtonHoverChecker = new HoverChecker(this.removeEntryButton, ConfigScreen.HOVER_THRESHOLD);
			this.removeEntryButton.setFGColor(BooleanButton.getColor(false));
		} else {
			this.removeEntryButton = null;
			this.removeEntryTooltip = null;
			this.removeEntryButtonHoverChecker = null;
		}
	}

	public abstract void removeEntry();

	public abstract void addEntryBelow();

	@Override
	public void tick() {
		super.tick();
		if (undoChangesButton != null)
			undoChangesButton.active = isChanged();
	}

	@Override
	public void renderToolTip(final int mouseX, final int mouseY, final float partialTicks) {
		if (this.undoChangesButtonHoverChecker != null && this.undoChangesButtonHoverChecker.checkHover(mouseX, mouseY, true))
			this.owningScreen.drawToolTip(undoChangesButtonTooltip, mouseX, mouseY);
		if (this.addEntryBelowButtonHoverChecker != null && this.addEntryBelowButtonHoverChecker.checkHover(mouseX, mouseY, true))
			this.owningScreen.drawToolTip(addEntryBelowTooltip, mouseX, mouseY);
		if (this.removeEntryButtonHoverChecker != null && this.removeEntryButtonHoverChecker.checkHover(mouseX, mouseY, true))
			this.owningScreen.drawToolTip(removeEntryTooltip, mouseX, mouseY);
		super.renderToolTip(mouseX, mouseY, partialTicks);
	}

	public int preRenderWidgets(final int startY, final int startX, final int width, final int height, final int buttonSize) {
		// Changing x coordinate.
		// After use it is the largest x coordinate before the buttons.
		// After use it is comparable to "startX + width - buttonsWidth"
		int posX = startX + width;
		if (undoChangesButton != null) {
			posX -= BUTTON_SPACER + buttonSize;
			preRenderWidget(undoChangesButton, posX, startY, buttonSize, buttonSize);
		}
		if (addEntryBelowButton != null) {
			posX -= BUTTON_SPACER + buttonSize;
			preRenderWidget(addEntryBelowButton, posX, startY, buttonSize, buttonSize);
		}
		if (removeEntryButton != null) {
			posX -= buttonSize + BUTTON_SPACER;
			preRenderWidget(removeEntryButton, posX, startY, buttonSize, buttonSize);
		}
		return posX;
	}

	@Override
	public boolean shouldRenderLabel() {
		return false;
	}

}
