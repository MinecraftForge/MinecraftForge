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
 * ConfigElement for a {@link Long}
 *
 * @author Cadiboo
 */
public class LongConfigElement extends NumberConfigElement<Long> {

	public LongConfigElement(final IConfigElementContainer<Long> configElementContainer) {
		super(configElementContainer);
	}

	/**
	 * @throws NumberFormatException if the string does not contain a parsable Long.
	 */
	@Override
	public Long parse(final String text) throws NumberFormatException {
		return Long.parseLong(text);
	}

	@Override
	public Long fromSlider(final GuiSlider slider) {
		return (long) slider.getValue();
	}

}
