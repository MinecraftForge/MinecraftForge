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

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.DyeColor;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import org.lwjgl.glfw.GLFW;

/**
 * {@link Button} for a {@link Enum}
 *
 * @author Cadiboo
 */
public class EnumButton<T extends Enum<?>> extends ExtendedButton implements IConfigListEntryWidget<T> {

	private final Callback<T> callback;
	/**
	 * Used to determine if the next value should be found in reverse order.
	 */
	private boolean isRightClick;

	public EnumButton(final Callback<T> callback) {
		this("Enum", callback);
	}

	public EnumButton(final String message, final Callback<T> callback) {
		super(0, 0, 0, 0, message, button -> nextValue(callback, button, 0, ((EnumButton<T>) button).isRightClick));
		this.callback = callback;
		updateTextAndColor(this, callback.get());
	}

	/**
	 * Helper method to cycle the value to the next valid value.
	 * Useful for limited value enums.
	 */
	private static <T extends Enum<?>> void nextValue(final Callback<T> callback, final Button button, final int iteration, final boolean reverse) {
		final T currentValue = callback.get();
		final T[] enumConstants = (T[]) currentValue.getClass().getEnumConstants();
		if (iteration >= enumConstants.length)
			return;
		final int increment = reverse ? enumConstants.length - 1 : +1;
		final T newValue = enumConstants[(currentValue.ordinal() + increment) % enumConstants.length];
		callback.set(newValue);
		if (!callback.isValid())
			nextValue(callback, button, iteration + 1, reverse);
		else
			updateTextAndColor(button, callback.get());
	}

	public static void updateTextAndColor(final Button button, final Enum<?> newValue) {
		button.setMessage(newValue.toString());
		button.setFGColor(getColor(newValue));
	}

	public static int getColor(final Enum<?> anEnum) {
		if (anEnum instanceof DyeColor)
			return ((DyeColor) anEnum).getColorValue();
		else if (anEnum instanceof IDisplayColorableEnum)
			return ((IDisplayColorableEnum) anEnum).getDisplayColor();
		else
			return 0xFF_FF_FF; // White
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
		updateTextAndColor(this, getCallback().get());
	}

	@Override
	protected boolean isValidClickButton(int mouseButton) {
		this.isRightClick = mouseButton == GLFW.GLFW_MOUSE_BUTTON_RIGHT;
		return mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT || this.isRightClick;
	}

}
