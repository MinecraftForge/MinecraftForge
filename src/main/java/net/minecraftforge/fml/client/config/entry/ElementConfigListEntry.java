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

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeConfigSpec.Range;
import net.minecraftforge.fml.client.config.ConfigEntryListWidget;
import net.minecraftforge.fml.client.config.ConfigScreen;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.client.config.HoverChecker;
import net.minecraftforge.fml.client.config.element.IConfigElement;
import net.minecraftforge.fml.client.config.entry.widget.IConfigListEntryWidget;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.minecraftforge.fml.client.config.GuiUtils.RESET_CHAR;
import static net.minecraftforge.fml.client.config.GuiUtils.UNDO_CHAR;

/**
 * A ConfigListEntry for an {@link IConfigElement}
 * Has the undo changes and reset to default buttons on the right.
 * Also handles rendering the label (only if renderLabel == true).
 *
 * @param <T> The type of the config object (e.g. Boolean/Float).
 * @author bspkrs
 * @author Cadiboo
 */
public class ElementConfigListEntry<T> extends ConfigListEntry<T> {

	protected final GuiButtonExt undoChangesButton, resetToDefaultButton;
	protected final List<String> undoToolTip, defaultToolTip;
	protected final HoverChecker undoChangesButtonHoverChecker, resetToDefaultButtonHoverChecker;
	private final IConfigElement<T> configElement;
	protected List<String> toolTip;
	protected HoverChecker tooltipHoverChecker;

	public <W extends Widget & IConfigListEntryWidget<T>> ElementConfigListEntry(@Nonnull final ConfigScreen owningScreen, @Nonnull final W widget, final IConfigElement<T> configElement) {
		super(owningScreen, widget);
		this.configElement = configElement;

		this.children().add(widget);
		this.children().add(this.undoChangesButton = new GuiButtonExt(0, 0, 0, 0, UNDO_CHAR, b -> this.undoChanges()));
		this.children().add(this.resetToDefaultButton = new GuiButtonExt(0, 0, 0, 0, RESET_CHAR, b -> this.resetToDefault()));

		this.undoToolTip = Collections.singletonList(I18n.format("fml.configgui.undoChanges.tooltip.single"));
		this.defaultToolTip = Collections.singletonList(I18n.format("fml.configgui.resetToDefault.tooltip.single"));

		this.undoChangesButtonHoverChecker = new HoverChecker(this.undoChangesButton, 500);
		this.resetToDefaultButtonHoverChecker = new HoverChecker(this.resetToDefaultButton, 500);
	}

	public IConfigElement<T> getConfigElement() {
		return configElement;
	}

	@Override
	public void tick() {
		super.tick();
		resetToDefaultButton.active = !isDefault();
		undoChangesButton.active = isChanged();
	}

	@Override
	public void renderToolTip(final int mouseX, final int mouseY, final float partialTicks) {
		List<String> toolTip = getToolTip();

		if (!getWidget().isValid()) {
			List<String> invalid = Lists.newArrayList(I18n.format("fml.configgui.tooltip.entryValueInvalid").replace("\\n", "\n"));
			for (int i = 0; i < invalid.size(); i++)
				invalid.set(i, "" + TextFormatting.RED + TextFormatting.BOLD + TextFormatting.UNDERLINE + invalid.get(i));
			toolTip = Lists.newArrayList(toolTip);
			toolTip.addAll(invalid);
		}

		if (!toolTip.isEmpty() && this.tooltipHoverChecker != null)
			if (this.tooltipHoverChecker.checkHover(mouseX, mouseY, true))
				this.owningScreen.drawToolTip(toolTip, mouseX, mouseY);
		if (this.undoChangesButtonHoverChecker.checkHover(mouseX, mouseY, true))
			this.owningScreen.drawToolTip(undoToolTip, mouseX, mouseY);
		if (this.resetToDefaultButtonHoverChecker.checkHover(mouseX, mouseY, true))
			this.owningScreen.drawToolTip(defaultToolTip, mouseX, mouseY);
		super.renderToolTip(mouseX, mouseY, partialTicks);
	}

	@Override
	public void render(final int index, final int startY, final int startX, final int width, final int height, final int mouseX, final int mouseY, final boolean isHovered, final float partialTicks) {
		super.render(index, startY, startX, width, height, mouseX, mouseY, isHovered, partialTicks);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

		if (shouldRenderLabel()) {
			final boolean isValidValue = getWidget().isValid();
			String formatting = "" + TextFormatting.GRAY;
			final boolean changed = isChanged();
			if (changed)
				formatting += TextFormatting.WHITE;
			if (!isValidValue)
				formatting += "" + TextFormatting.RED + TextFormatting.BOLD;
			if (changed)
				formatting += TextFormatting.ITALIC; // Italic MUST be the last formatting style
			final FontRenderer font = this.minecraft.fontRenderer;
			final String trimmedLabel = GuiUtils.trimStringToSize(font, getLabel(), ConfigEntryListWidget.MAX_LABEL_WIDTH);
			font.drawString(
					formatting + trimmedLabel,
					startX,
					startY + height / 2F - font.FONT_HEIGHT / 2F,
					0xFFFFFF
			);
		}
		if (tooltipHoverChecker == null)
			tooltipHoverChecker = new HoverChecker(startY, startY + height, startX, buttonsStartPosX, 500);
		else
			tooltipHoverChecker.updateBounds(startY, startY + height, startX, buttonsStartPosX);
	}

	@Override
	public int preRenderWidgets(final int startY, final int startX, final int width, final int height, final int buttonSize) {
		// Changing x coordinate.
		// After use it is the largest x coordinate before the buttons.
		// After use it is comparable to "startX + width - buttonsWidth"
		int posX = startX + width;

		posX -= BUTTON_SPACER + buttonSize;
		preRenderWidget(undoChangesButton, posX, startY, buttonSize, buttonSize);

		posX -= buttonSize + BUTTON_SPACER;
		preRenderWidget(resetToDefaultButton, posX, startY, buttonSize, buttonSize);

		return posX;
	}

	@Override
	public boolean shouldRenderLabel() {
		return true;
	}

	@Override
	public int getLabelWidth() {
		if (!shouldRenderLabel())
			return 0;
		final String label = this.getLabel();
		if (label == null)
			return 0;
		return this.minecraft.fontRenderer.getStringWidth(label);
	}

	/**
	 * If we have a translation key use it.
	 * If we don't try to use the comment.
	 * If we don't have a comment use "No tooltip defined".
	 */
	public void makeTooltip() {
		this.toolTip = new ArrayList<>();

		final String label = getLabel();
		if (label == null)
			return; //

		toolTip.add(TextFormatting.GREEN + label);

		// Try for translation, with comment as backup

		boolean commentDone = false;

		final String translationKey = getTranslationKey();
		if (!Strings.isNullOrEmpty(translationKey)) {
			final String tooltipTranslationKey = translationKey + ".tooltip";
			final String translated = I18n.format(tooltipTranslationKey);
			if (!translated.equals(tooltipTranslationKey)) {
				for (final String s : translated.replace("\\n", "\n").split("\n"))
					toolTip.add(TextFormatting.YELLOW + s);
				commentDone = true;
			}
		}
		if (!commentDone) {
			final String comment = getComment();
			if (!Strings.isNullOrEmpty(comment))
				for (final String s : comment.split("\n"))
					toolTip.add(TextFormatting.YELLOW + s);
			else
				for (final String s : I18n.format("fml.configgui.tooltip.noTooltip").replace("\\n", "\n").split("\n"))
					toolTip.add(TextFormatting.RED + s);
		}

		if (this.displayDefaultValue()) {
			final T defaultValue = this.getDefault();
			final Range<?> range = this.getRange();
			if (range != null) {
				for (final String s : I18n.format("fml.configgui.tooltip.rangeWithDefault", range.getMin(), range.getMax(), defaultValue).replace("\\n", "\n").split("\n"))
					toolTip.add(TextFormatting.AQUA + s);
			} else
				for (final String s : I18n.format("fml.configgui.tooltip.default", defaultValue).replace("\\n", "\n").split("\n"))
					toolTip.add(TextFormatting.AQUA + s);
		}

		if (getConfigElement().requiresGameRestart())
			toolTip.add(TextFormatting.RED + "[" + I18n.format("fml.configgui.gameRestartTitle") + "]");
		if (getConfigElement().requiresWorldRestart())
			toolTip.add(TextFormatting.RED + "[" + I18n.format("fml.configgui.worldRestartTitle") + "]");
	}

	/**
	 * @return The result of formatting the translation key.
	 */
	public String getLabel() {
		return getConfigElement().getLabel();
	}

	@Nullable
	public String getTranslationKey() {
		return getConfigElement().getTranslationKey();
	}

	@Nullable
	public String getComment() {
		return getConfigElement().getComment();
	}

	@Nullable
	public Range<?> getRange() {
		return getConfigElement().getRange();
	}

	@Override
	public boolean isCategory() {
		return getConfigElement().isCategory();
	}

	@Nonnull
	public List<String> getToolTip() {
		if (toolTip == null)
			makeTooltip();
		return toolTip;
	}

}
