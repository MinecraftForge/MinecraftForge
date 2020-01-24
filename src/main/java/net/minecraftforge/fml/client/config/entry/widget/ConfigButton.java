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

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.screen.ConfigConfigScreen;
import net.minecraftforge.fml.client.config.screen.ConfigScreen;

/**
 * {@link Button} for a {@link Config}
 * Opens a {@link ConfigConfigScreen}
 *
 * @author Cadiboo
 */
public class ConfigButton extends ScreenButton<UnmodifiableConfig> {

	private final ConfigConfigScreen screen;

	public ConfigButton(final ConfigScreen configScreen, final Callback<UnmodifiableConfig> callback) {
		this("Config", configScreen, callback);
	}

	public ConfigButton(final String message, final ConfigScreen configScreen, final Callback<UnmodifiableConfig> callback) {
		super(message, callback, b -> Minecraft.getInstance().displayGuiScreen(((ConfigButton) b).screen));
		this.screen = makeScreen(configScreen, callback, message);
	}

	public static ConfigConfigScreen makeScreen(final ConfigScreen owningScreen, final Callback<UnmodifiableConfig> callback, final String label) {
		final ConfigConfigScreen configScreen = new ConfigConfigScreen(owningScreen, callback);
		final ITextComponent subtitle;
		if (owningScreen.getSubtitle() == null)
			subtitle = new StringTextComponent(label);
		else
			subtitle = owningScreen.getSubtitle().deepCopy().appendSibling(new StringTextComponent(ConfigScreen.CATEGORY_DIVIDER + label));
		if (configScreen.isUnmodifiable())
			subtitle.appendText(" ").appendSibling(new StringTextComponent("(" + I18n.format("fml.configgui.config.unmodifiable") + ")").applyTextStyle(TextFormatting.RED));
		configScreen.setSubtitle(subtitle);
		return configScreen;
	}

	/**
	 * Configs are mutable so care must be taken not to modify the original config so that
	 * resetToDefault and undoChanges work properly.
	 */
	@Override
	public void resetToDefault() {
		super.resetToDefault();
		// Need to clone so that we don't actually modify the default value.
		getCallback().set(screen.cloneConfig(getCallback().get()));
		updateWidgetValue();
	}

	/**
	 * Configs are mutable so care must be taken not to modify the original config so that
	 * resetToDefault and undoChanges work properly.
	 */
	@Override
	public void undoChanges() {
		super.undoChanges();
		// Need to clone so that we don't actually modify the original value.
		getCallback().set(screen.cloneConfig(getCallback().get()));
		updateWidgetValue();
	}

}
