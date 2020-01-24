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

import net.minecraft.client.gui.widget.Widget;
import net.minecraftforge.common.ForgeConfigSpec.Range;
import net.minecraftforge.fml.client.config.element.ConfigElement;
import net.minecraftforge.fml.client.config.element.IConfigElementContainer;
import net.minecraftforge.fml.client.config.entry.ElementConfigListEntry;
import net.minecraftforge.fml.client.config.entry.widget.IConfigListEntryWidget;
import net.minecraftforge.fml.client.config.entry.widget.ObjectTextField;
import net.minecraftforge.fml.client.config.entry.widget.number.NumberSlider;
import net.minecraftforge.fml.client.config.screen.ConfigScreen;
import net.minecraftforge.fml.client.gui.widget.Slider;

/**
 * ConfigElement for a {@link Number}
 *
 * @author Cadiboo
 */
public abstract class NumberConfigElement<T extends Number & Comparable<? super T>> extends ConfigElement<T> {

	public NumberConfigElement(final IConfigElementContainer<T> configElementContainer) {
		super(configElementContainer);
	}

	@Override
	public ElementConfigListEntry<T> makeConfigListEntry(final ConfigScreen configScreen) {
		final IConfigListEntryWidget.Callback<T> callback = new IConfigListEntryWidget.Callback<>(this::get, this::set, this::getDefault, this::isDefault, this::resetToDefault, this::isChanged, this::undoChanges, this::isValid, this::save);
		final Widget widget;
		if (this.hasSlidingControl())
			widget = this.makeSlider(this.getLabel(), callback, this.getConfigElementContainer().getRange());
		else
			widget = this.makeTextField(this.getLabel(), callback);
		return new ElementConfigListEntry<>(configScreen, cast(widget), this);
	}

	protected NumberSlider<T> makeSlider(final String label, final IConfigListEntryWidget.Callback<T> callback, final Range<T> range) {
		return new NumberSlider<T>(label, callback, range) {
			@Override
			public T getValue(final Slider slider) {
				return fromSlider(slider);
			}
		};
	}

	protected ObjectTextField<T> makeTextField(final String label, final IConfigListEntryWidget.Callback<T> callback) {
		return new ObjectTextField<T>(label, callback) {

			@Override
			public String toText(final T value) {
				return stringify(value);
			}

			/**
			 * @throws NumberFormatException if the string does not contain a parsable Number.
			 */
			@Override
			public T fromText(final String text) throws NumberFormatException {
				return parse(text);
			}
		};
	}

	private <W extends Widget & IConfigListEntryWidget<T>> W cast(final Widget widget) {
		// Generate ClassCastException if it's not actually assignable.
		return (W) IConfigListEntryWidget.class.cast(widget);
	}

	public String stringify(final T value) {
		return value.toString();
	}

	/**
	 * @throws NumberFormatException if the string does not contain a parsable Number.
	 */
	public abstract T parse(final String text) throws NumberFormatException;

	public abstract T fromSlider(final Slider slider);

}
