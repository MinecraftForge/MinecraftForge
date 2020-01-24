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

package net.minecraftforge.fml.client.config.element;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.minecraftforge.fml.client.config.screen.ConfigScreen;
import net.minecraftforge.fml.client.config.element.category.CategoryElement;
import net.minecraftforge.fml.client.config.element.category.ConfigCategoryElement;
import net.minecraftforge.fml.client.config.entry.ScreenElementConfigListEntry;
import net.minecraftforge.fml.client.config.entry.widget.ConfigButton;
import net.minecraftforge.fml.client.config.entry.widget.IConfigListEntryWidget;
import net.minecraftforge.fml.client.config.entry.widget.ScreenButton;

/**
 * Config Element for a {@link Config}
 * This element represents a ConfigValue<Config> (as opposed to a SubCategory)
 *
 * @author Cadiboo
 * @see CategoryElement
 * @see ConfigCategoryElement
 */
public class ConfigConfigElement extends ConfigElement<UnmodifiableConfig> {

	public ConfigConfigElement(final IConfigElementContainer<UnmodifiableConfig> elementContainer) {
		super(elementContainer);
	}

	@Override
	public ScreenElementConfigListEntry<UnmodifiableConfig> makeConfigListEntry(final ConfigScreen configScreen) {
		final IConfigListEntryWidget.Callback<UnmodifiableConfig> callback = new IConfigListEntryWidget.Callback<>(this::get, this::set, this::getDefault, this::isDefault, this::resetToDefault, this::isChanged, this::undoChanges, this::isValid, this::save);
		final ScreenButton<UnmodifiableConfig> widget = new ConfigButton(getLabel(), configScreen, callback);
		return new ScreenElementConfigListEntry<>(configScreen, widget, this);
	}

}
