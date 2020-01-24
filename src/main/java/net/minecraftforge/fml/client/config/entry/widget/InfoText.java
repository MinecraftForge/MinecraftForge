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
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.resources.I18n;

/**
 * A dummy Widget & IConfigListEntryWidget that just displays text (Usually error text) and does nothing else.
 *
 * @author Cadiboo
 */
public class InfoText<T> extends Widget implements IConfigListEntryWidget<T> {

	public InfoText(final String translationKey, String... formatArgs) {
		super(0, 0, 0, 0, I18n.format(translationKey, (Object[]) formatArgs));
		this.active = false;
		this.visible = false;
	}

	@Override
	public Callback<T> getCallback() {
		return null;
	}

	@Override
	public void renderToolTip(final int mouseX, final int mouseY, final float partialTicks) {
	}

	@Override
	public T getDefault() {
		return null;
	}

	@Override
	public boolean isDefault() {
		return true;
	}

	@Override
	public void resetToDefault() {
	}

	@Override
	public boolean isChanged() {
		return false;
	}

	@Override
	public void undoChanges() {
	}

	@Override
	public void save() {
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public boolean isWidgetValueValid() {
		return true;
	}

	@Override
	public void updateWidgetValue() {
	}

	@Override
	public void render(final int mouseX, final int mouseY, final float partialTicks) {
		Minecraft minecraft = Minecraft.getInstance();
		FontRenderer fontrenderer = minecraft.fontRenderer;
		this.drawCenteredString(fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, 0xFF_FF_00_00);
	}

}
