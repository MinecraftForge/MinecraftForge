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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;

/**
 * {@link TextFieldWidget} for an {@link Object}
 *
 * @author Cadiboo
 */
public abstract class ObjectTextField<T> extends TextFieldWidget implements IConfigListEntryWidget<T> {

	private final Callback<T> callback;
	private final String initialText;
	private boolean hasChanged = true;
	private boolean isValid = false;

	public ObjectTextField(final String message, final Callback<T> callback) {
		super(Minecraft.getInstance().fontRenderer, 0, 0, 0, 0, message);
		this.callback = callback;
		this.setMaxStringLength(Integer.MAX_VALUE);
		this.setText(toText(callback.get()));
		this.initialText = this.getText();
		this.setCursorPositionZero(); // Remove weird scroll bug
		this.func_212954_a(newText -> {
			hasChanged = true;
			if (!isWidgetValueValid())
				return;
			T t = null;
			try {
				t = fromText(newText);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (t != null) // Object wasn't initialised OR Config doesn't accept null values
				callback.set(t);
		});
		hasChanged = true;
		isWidgetValueValid(); // Build cache
	}

	public Callback<T> getCallback() {
		return callback;
	}

	@Override
	public boolean isChanged() {
		return !initialText.equals(getText()) || getCallback().isChanged();
	}

	@Override
	public boolean isWidgetValueValid() {
		// Calling fromText is likely to parse & making a new object.
		// Calling this each frame is pretty performance intensive.
		// Caching validity avoids this performance cost.
		if (!hasChanged)
			return isValid;
		boolean valid;
		try {
			fromText(this.getText());
			valid = true;
		} catch (Exception e) {
			valid = false;
		}
		hasChanged = false;
		isValid = valid;
		return valid;
	}

	@Override
	public void updateWidgetValue() {
		hasChanged = true;
		this.setText(toText(getCallback().get()));
	}

	public abstract String toText(final T value);

	/**
	 * @throws Exception if the string does not contain a parsable Object.
	 */
	public abstract T fromText(final String text) throws Exception;

}
