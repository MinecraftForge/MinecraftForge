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

import net.minecraftforge.fml.client.config.ConfigScreen;
import net.minecraftforge.fml.client.config.entry.ElementConfigListEntry;
import net.minecraftforge.fml.client.config.entry.widget.IConfigListEntryWidget;
import net.minecraftforge.fml.client.config.entry.widget.LocalTimeTextField;

import java.time.LocalTime;

/**
 * ConfigElement for a {@link LocalTime}
 *
 * @author Cadiboo
 */
public class LocalTimeConfigElement extends ConfigElement<LocalTime> {

	public LocalTimeConfigElement(final IConfigElementContainer<LocalTime> elementContainer) {
		super(elementContainer);
	}

	@Override
	public ElementConfigListEntry<LocalTime> makeConfigListEntry(final ConfigScreen configScreen) {
		final IConfigListEntryWidget.Callback<LocalTime> callback = new IConfigListEntryWidget.Callback<>(this::get, this::set, this::getDefault, this::isDefault, this::resetToDefault, this::isChanged, this::undoChanges, this::isValid, this::save);
		final LocalTimeTextField widget = new LocalTimeTextField(callback);
		return new ElementConfigListEntry<>(configScreen, widget, this);
	}

}
