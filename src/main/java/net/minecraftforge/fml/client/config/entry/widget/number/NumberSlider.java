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

import net.minecraftforge.common.ForgeConfigSpec.Range;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.client.config.entry.widget.IConfigListEntryWidget;

/**
 * {@link GuiSlider} for a {@link Number}
 *
 * @author Cadiboo
 */
public abstract class NumberSlider<T extends Number & Comparable<? super T>> extends GuiSlider implements IConfigListEntryWidget<T> {

	private final Callback<T> callback;

	public NumberSlider(final String message, final IConfigListEntryWidget.Callback<T> callback, final Range<T> range) {
		// public GuiSlider(int xPos, int yPos, int width, int height, String prefix, String suf, double minVal, double maxVal, double currentVal, boolean showDec, boolean drawStr, IPressable handler)
		super(
				0, 0, 0, 0,
				"", "",
				getMin(range), getMax(range), callback.get().doubleValue(),
				shouldShowDecimal(callback),
				true,
				b -> {
				}
		);
		this.callback = callback;
		this.parent = s -> callback.set(getValue(s));
	}

	public static boolean shouldShowDecimal(final Callback<? extends Number> callback) {
		final Number number = callback.get();
		return number instanceof Float || callback.get() instanceof Double;
	}

	private static double getMax(final Range<? extends Number> range) {
		if (range == null) // Should never happen
			return Double.MAX_VALUE; // Leads to weird results but DOESN'T crash
		return range.getMax().doubleValue();
	}

	private static double getMin(final Range<? extends Number> range) {
		if (range == null) // Should never happen
			return Double.MIN_VALUE; // Leads to weird results but DOESN'T crash
		return range.getMin().doubleValue();
	}

	@Override
	public Callback<T> getCallback() {
		return callback;
	}

	@Override
	public boolean isWidgetValueValid() {
		return true;
	}

	@Override
	public void updateWidgetValue() {
		this.setValue(callback.get().doubleValue());
		this.updateSlider();
	}

	public abstract T getValue(GuiSlider slider);

}
