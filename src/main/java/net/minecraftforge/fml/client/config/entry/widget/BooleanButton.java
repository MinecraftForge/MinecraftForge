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
import net.minecraftforge.fml.client.config.GuiButtonExt;
import org.lwjgl.glfw.GLFW;

/**
 * {@link Button} for a {@link Boolean}
 *
 * @author Cadiboo
 */
public class BooleanButton extends GuiButtonExt implements IConfigListEntryWidget<Boolean> {

	private final Callback<Boolean> callback;

	public BooleanButton(final Callback<Boolean> callback) {
		this("Boolean", callback);
	}

	public BooleanButton(final String message, final Callback<Boolean> callback) {
		super(0, 0, 0, 0, message, button -> {
			callback.set(!callback.get());
			if (!callback.isValid())
				callback.set(!callback.get());
			updateTextAndColor(button, callback.get());
		});
		this.callback = callback;
		updateWidgetValue();
	}

	public static void updateTextAndColor(final Button button, final Boolean newValue) {
		button.setMessage(newValue.toString());
		button.setFGColor(getColor(newValue));
	}

	/**
	 * @return Green if true, Red if false
	 */
	public static int getColor(final boolean b) {
		return b ? 0x55FF55 : 0xFF5555;
	}

	public Callback<Boolean> getCallback() {
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
		return mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT || mouseButton == GLFW.GLFW_MOUSE_BUTTON_RIGHT;
	}

}
