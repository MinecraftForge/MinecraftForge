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

package net.minecraftforge.fml.client.config.entry.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.screen.ConfigScreen;
import net.minecraftforge.fml.client.config.screen.ListConfigScreen;

import java.util.List;

/**
 * {@link Button} for a {@link List}
 * Opens a {@link ListConfigScreen}
 *
 * @author Cadiboo
 */
public class ListButton<T extends List<?>> extends ScreenButton<T> {

	private final ListConfigScreen<T> screen;

	public ListButton(final ConfigScreen configScreen, final Callback<T> callback) {
		this("List", configScreen, callback);
	}

	public ListButton(final String message, final ConfigScreen configScreen, final Callback<T> callback) {
		super(message, callback, b -> Minecraft.getInstance().displayGuiScreen(((ListButton<T>) b).screen));
		this.screen = makeScreen(configScreen, callback, message);
	}

	public static <T extends List<?>> ListConfigScreen<T> makeScreen(final ConfigScreen owningScreen, final Callback<T> callback, final String label) {
		final ListConfigScreen<T> configScreen = new ListConfigScreen<>(owningScreen, callback);
		final ITextComponent subtitle;
		if (owningScreen.getSubtitle() == null)
			subtitle = new StringTextComponent(label);
		else
			subtitle = owningScreen.getSubtitle().deepCopy().appendSibling(new StringTextComponent(ConfigScreen.CATEGORY_DIVIDER + label));
		if (configScreen.isFixedSize())
			subtitle.appendText(" ").appendSibling(new StringTextComponent("(" + I18n.format("fml.configgui.list.fixedSize") + ")").applyTextStyle(TextFormatting.RED));
		configScreen.setSubtitle(subtitle);

		return configScreen;
	}

	/**
	 * Lists are mutable so care must be taken not to modify the original list so that
	 * resetToDefault and undoChanges work properly.
	 */
	@Override
	public void resetToDefault() {
		super.resetToDefault();
		// Need to clone so that we don't actually modify the default value.
		getCallback().set(screen.cloneList(getCallback().get()));
		updateWidgetValue();
	}

	/**
	 * Lists are mutable so care must be taken not to modify the original list so that
	 * resetToDefault and undoChanges work properly.
	 */
	@Override
	public void undoChanges() {
		super.undoChanges();
		// Need to clone so that we don't actually modify the initial value.
		getCallback().set(screen.cloneList(getCallback().get()));
		updateWidgetValue();
	}

}
