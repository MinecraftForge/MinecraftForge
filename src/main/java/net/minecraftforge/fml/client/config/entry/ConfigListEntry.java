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

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.list.AbstractOptionList;
import net.minecraftforge.fml.client.config.ConfigEntryListWidget;
import net.minecraftforge.fml.client.config.ConfigScreen;
import net.minecraftforge.fml.client.config.HoverChecker;
import net.minecraftforge.fml.client.config.entry.widget.IConfigListEntryWidget;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for entries of the {@link ConfigEntryListWidget}.
 *
 * @param <T> The type of the config object (e.g. Boolean/Float).
 * @author Cadiboo
 */
public abstract class ConfigListEntry<T> extends AbstractOptionList.Entry<ConfigListEntry<?>> implements IConfigListEntry<T> {

	/**
	 * The space between each button.
	 */
	public static final int BUTTON_SPACER = 1;
	protected final ConfigScreen owningScreen;
	protected final Minecraft minecraft;
	protected final List<Widget> children = new ArrayList<>();
	private final IConfigListEntryWidget<T> widget;
	private final HoverChecker widgetHoverChecker;
	/**
	 * @see #render(int, int, int, int, int, int, int, boolean, float)
	 * @see #preRenderWidgets(int, int, int, int, int)
	 */
	protected int buttonsStartPosX;

	/**
	 * @param <W> The type of the Widget
	 */
	public <W extends Widget & IConfigListEntryWidget<T>> ConfigListEntry(@Nonnull final ConfigScreen owningScreen, @Nonnull final W widget) {
		this.owningScreen = owningScreen;
		this.widget = widget;
		this.minecraft = Minecraft.getInstance();
		this.children().add(widget);
		this.widgetHoverChecker = new HoverChecker(widget, 500);
	}

	@Override
	public void tick() {
		this.getWidget().tick();
	}

	@Override
	public void renderToolTip(final int mouseX, final int mouseY, final float partialTicks) {
		if (widgetHoverChecker.checkHover(mouseX, mouseY))
			getWidget().renderToolTip(mouseX, mouseY, partialTicks);
	}

	@Nonnull
	public final <W extends Widget & IConfigListEntryWidget<T>> W getWidget() {
		return (W) widget;
	}

	@Override
	public void render(final int index, final int startY, final int startX, final int width, final int height, final int mouseX, final int mouseY, final boolean isHovered, final float partialTicks) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

		buttonsStartPosX = preRenderWidgets(startY, startX, width, height, height);

		buttonsStartPosX -= 2; // Add a tiny bit of space between the entry and the buttons

		final Widget widget = this.getWidget();
		int widgetX = startX;
		if (shouldRenderLabel())
			widgetX += this.owningScreen.getEntryList().getLongestLabelWidth() + 2; // Add a tiny bit of space between the label and the entry
		preRenderWidget(widget, widgetX, startY, buttonsStartPosX - widgetX, height);

		this.children().forEach(c -> c.render(mouseX, mouseY, partialTicks));
	}

	/**
	 * Call {@link #preRenderWidget(Widget, int, int, int, int)} for all widgets & return how much space they took up
	 *
	 * @return startX + width - buttonsWidth
	 */
	public abstract int preRenderWidgets(final int startY, final int startX, final int width, final int height, final int buttonSize);

	/**
	 * Helper method that is called for each widget before it is rendered to update it's bounds & active state.
	 */
	protected void preRenderWidget(final Widget widget, final int x, final int y, final int width, final int height) {
		// TextFieldWidget render larger than they should be (reeeee)
		if (widget instanceof TextFieldWidget) {
			widget.x = x + 2;
			widget.y = y + 2;
			widget.setWidth(width - 4);
			widget.setHeight(height - 4);
		} else {
			widget.x = x;
			widget.y = y;
			widget.setWidth(width);
			widget.setHeight(height);
		}
	}

	@Nonnull
	@Override
	public List<Widget> children() {
		return children;
	}

	/**
	 * @return If the label for this entry should be (theoretically) rendered (Doesn't take into account if this label is empty or null)
	 */
	public abstract boolean shouldRenderLabel();

	/**
	 * By default entries don't have a label so by default it returns 0.
	 *
	 * @return The width of this label or 0 if this does not have a label
	 */
	public int getLabelWidth() {
		return 0;
	}

}
