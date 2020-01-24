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
import net.minecraftforge.fml.client.config.screen.ConfigScreen;
import net.minecraftforge.fml.client.config.element.ConfigConfigElement;
import net.minecraftforge.fml.client.config.element.IConfigElement;
import net.minecraftforge.fml.client.config.element.ListConfigElement;
import net.minecraftforge.fml.client.config.element.category.ConfigCategoryElement;
import net.minecraftforge.fml.client.config.element.category.ModConfigCategoryElement;
import net.minecraftforge.fml.client.config.entry.widget.IConfigListEntryWidget;

/**
 * A ConfigListEntry for an {@link ElementConfigListEntry} that displays a screen.
 * Used by Configs (ConfigValue), Lists (ConfigValue) and Categories (ModConfig, Config)
 *
 * @param <T> The type of the config object (e.g. Boolean/Float).
 * @author Cadiboo
 * @see ConfigConfigElement
 * @see ListConfigElement
 * @see ConfigCategoryElement
 * @see ModConfigCategoryElement
 */
public class ScreenElementConfigListEntry<T> extends ElementConfigListEntry<T> {

	public <W extends Widget & IConfigListEntryWidget<T>> ScreenElementConfigListEntry(final ConfigScreen configScreen, final W widget, final IConfigElement<T> configElement) {
		super(configScreen, widget, configElement);
	}

	@Override
	public boolean shouldRenderLabel() {
		return false;
	}

	@Override
	public boolean displayDefaultValue() {
		return false;
	}

}
