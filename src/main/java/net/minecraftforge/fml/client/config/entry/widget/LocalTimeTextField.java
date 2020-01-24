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
import java.time.LocalTime;

/**
 * {@link TextFieldWidget} for a {@link LocalTime}
 *
 * @author Cadiboo
 */
public class LocalTimeTextField extends ObjectTextField<LocalTime> {

	public LocalTimeTextField(final Callback<LocalTime> callback) {
		this("LocalTime", callback);
	}

	public LocalTimeTextField(final String message, final Callback<LocalTime> callback) {
		super(message, callback);
	}

	@Override
	public String toText(final LocalTime value) {
		return value.toString();
	}

	/**
	 * Tries to parse the text to a LocalDate.
	 * TOML Local Times are in RFC 3339 format.
	 * <p>
	 * Examples:
	 * 07:32:00
	 * 00:32:00.999999
	 *
	 * @throws DateTimeException if the string does not contain a parsable LocalTime.
	 * @see "https://github.com/toml-lang/toml#local-time"
	 */
	@Override
	public LocalTime fromText(final String text) throws DateTimeException {
		final String[] hms = text.split(":");

		final int hour = Integer.parseInt(hms[0]);
		final int minute = Integer.parseInt(hms[1]);
		final int second;
		final int nano;
		if (hms.length > 2) {
			final String[] sS = hms[2].split("\\.");
			second = Integer.parseInt(sS[0]);
			if (sS.length > 1)
				nano = Integer.parseInt(sS[1]);
			else
				nano = 0;
		} else
			second = nano = 0;
		return LocalTime.of(hour, minute, second, nano);
	}

}
