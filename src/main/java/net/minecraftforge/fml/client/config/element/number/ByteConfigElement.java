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

package net.minecraftforge.fml.client.config.element.number;

import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.client.config.element.IConfigElementContainer;

/**
 * ConfigElement for a {@link Byte}
 *
 * @author Cadiboo
 */
public class ByteConfigElement extends NumberConfigElement<Byte> {

	public ByteConfigElement(final IConfigElementContainer<Byte> configElementContainer) {
		super(configElementContainer);
	}

	/**
	 * @throws NumberFormatException if the string does not contain a parsable Byte.
	 */
	@Override
	public Byte parse(final String text) throws NumberFormatException {
		return Byte.parseByte(text);
	}

	@Override
	public Byte fromSlider(final GuiSlider slider) {
		return (byte) slider.getValueInt();
	}

}
