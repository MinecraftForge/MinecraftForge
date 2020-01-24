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

import net.minecraftforge.fml.client.config.screen.ConfigScreen;
import net.minecraftforge.fml.client.config.entry.ElementConfigListEntry;
import net.minecraftforge.fml.client.config.entry.widget.IConfigListEntryWidget;
import net.minecraftforge.fml.client.config.entry.widget.OffsetDateTimeTextField;

import java.time.OffsetDateTime;

/**
 * ConfigElement for an {@link OffsetDateTime}
 *
 * @author Cadiboo
 */
public class OffsetDateTimeConfigElement extends ConfigElement<OffsetDateTime> {

	public OffsetDateTimeConfigElement(final IConfigElementContainer<OffsetDateTime> elementContainer) {
		super(elementContainer);
	}

	@Override
	public ElementConfigListEntry<OffsetDateTime> makeConfigListEntry(final ConfigScreen configScreen) {
		final IConfigListEntryWidget.Callback<OffsetDateTime> callback = new IConfigListEntryWidget.Callback<>(this::get, this::set, this::getDefault, this::isDefault, this::resetToDefault, this::isChanged, this::undoChanges, this::isValid, this::save);
		final OffsetDateTimeTextField widget = new OffsetDateTimeTextField(callback);
		return new ElementConfigListEntry<>(configScreen, widget, this);
	}

}
