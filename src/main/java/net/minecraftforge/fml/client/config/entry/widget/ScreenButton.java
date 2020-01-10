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
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.client.config.GuiButtonExt;

/**
 * Opens a screen when pressed.
 * Used by Lists ({@link ConfigValue}), Configs ({@link ConfigValue}) and Categories (ModConfigs and Configs).
 *
 * @author Cadiboo
 */
public class ScreenButton<T> extends GuiButtonExt implements IConfigListEntryWidget<T> {

	private final Callback<T> callback;

	public ScreenButton(final String message, final Callback<T> callback, final Screen screen) {
		this(message, callback, button -> Minecraft.getInstance().displayGuiScreen(screen));
	}

	public ScreenButton(final String message, final Callback<T> callback, final IPressable onPress) {
		super(0, 0, 0, 0, message, onPress);
		this.callback = callback;
	}

	public Callback<T> getCallback() {
		return callback;
	}

	@Override
	public void tick() {
		if (isValid())
			this.setFGColor(0);
		else
			this.setFGColor(BooleanButton.getColor(false));
	}

	@Override
	public boolean isWidgetValueValid() {
		return true;
	}

	@Override
	public void updateWidgetValue() {
	}

}
