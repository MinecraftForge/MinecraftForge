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

import net.minecraft.client.gui.widget.TextFieldWidget;

import java.time.DateTimeException;
import java.time.LocalDate;

/**
 * {@link TextFieldWidget} for a {@link LocalDate}
 *
 * @author Cadiboo
 */
public class LocalDateTextField extends ObjectTextField<LocalDate> {

	public LocalDateTextField(final Callback<LocalDate> callback) {
		this("LocalDate", callback);
	}

	public LocalDateTextField(final String message, final Callback<LocalDate> callback) {
		super(message, callback);
	}

	@Override
	public String toText(final LocalDate value) {
		return value.toString();
	}

	/**
	 * Tries to parse the text to a LocalDate.
	 * TOML Local Dates are in RFC 3339 format.
	 * <p>
	 * Examples:
	 * 1979-05-27
	 *
	 * @throws DateTimeException if the string does not contain a parsable LocalDate.
	 * @see "https://github.com/toml-lang/toml#local-date"
	 */
	@Override
	public LocalDate fromText(final String text) throws DateTimeException {
		final String[] split = text.split("-");
		final int year = Integer.parseInt(split[0]);
		final int month = Integer.parseInt(split[1]);
		final int day = Integer.parseInt(split[2]);
		return LocalDate.of(year, month, day);
	}

}
