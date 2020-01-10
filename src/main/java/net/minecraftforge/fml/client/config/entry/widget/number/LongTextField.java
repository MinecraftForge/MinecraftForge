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

package net.minecraftforge.fml.client.config.entry.widget.number;

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraftforge.fml.client.config.entry.widget.ObjectTextField;

/**
 * {@link TextFieldWidget} for a {@link Long}
 *
 * @author Cadiboo
 */
public class LongTextField extends ObjectTextField<Long> {

	public LongTextField(final Callback<Long> callback) {
		this("Long", callback);
	}

	public LongTextField(final String message, final Callback<Long> callback) {
		super(message, callback);
	}

	@Override
	public String toText(final Long value) {
		return value.toString();
	}

	/**
	 * @throws NumberFormatException if the string does not contain a parsable Long.
	 */
	@Override
	public Long fromText(final String text) throws NumberFormatException {
		return Long.parseLong(text);
	}

}
