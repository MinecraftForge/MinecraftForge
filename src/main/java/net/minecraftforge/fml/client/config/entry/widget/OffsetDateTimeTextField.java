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
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * {@link TextFieldWidget} for a {@link OffsetDateTime}
 *
 * @author Cadiboo
 */
public class OffsetDateTimeTextField extends ObjectTextField<OffsetDateTime> {

	public OffsetDateTimeTextField(final Callback<OffsetDateTime> callback) {
		this("OffsetDateTime", callback);
	}

	public OffsetDateTimeTextField(final String message, final Callback<OffsetDateTime> callback) {
		super(message, callback);
	}

	@Override
	public String toText(final OffsetDateTime value) {
		return value.toString();
	}

	/**
	 * Tries to parse the text to a LocalDate.
	 * TOML Offset Date Times are in RFC 3339 format.
	 * <p>
	 * Examples:
	 * 1979-05-27T07:32:00Z
	 * 1979-05-27T00:32:00-07:00
	 * 1979-05-27T00:32:00.999999-07:00
	 *
	 * @throws DateTimeException if the string does not contain a parsable OffsetDateTime.
	 * @see "https://github.com/toml-lang/toml#offset-date-time"
	 */
	@Override
	public OffsetDateTime fromText(String text) throws DateTimeException {

		int zoneStart = text.length() - 1;
		while (true) {
			final char c = text.charAt(zoneStart);
			if (c == 'Z' || c == '+' || c == '-') // See com.electronwill.nightconfig.toml.TemporalParser.OFFSET_INDICATORS
				break;
			--zoneStart;
		}

		final ZoneOffset offset = ZoneOffset.of(text.substring(zoneStart));

		text = text.substring(0, zoneStart);

		final String[] split = text.split("[Tt ]"); // See com.electronwill.nightconfig.toml.TemporalParser.ALLOWED_DT_SEPARATORS

		final String[] ymd = split[0].split("-");
		final int year = Integer.parseInt(ymd[0]);
		final int month = Integer.parseInt(ymd[1]);
		final int day = Integer.parseInt(ymd[2]);

		final String[] hms = split[1].split(":");

		final int hour = Integer.parseInt(hms[0]);
		final int minute = Integer.parseInt(hms[1]);
		final int second;
		final int nano;
		if (hms.length > 2) {
			final String[] sS = hms[2].split("\\.");
			second = Integer.parseInt(sS[0]);
			if (sS.length > 1) {
				final String S = sS[1];
				final int mul = (int) Math.pow(10, "SSSSSSSSS".length() - S.length());
				nano = Integer.parseInt(S) * mul;
			} else
				nano = 0;
		} else
			second = nano = 0;
		return OffsetDateTime.of(year, month, day, hour, minute, second, nano, offset);

	}

}
