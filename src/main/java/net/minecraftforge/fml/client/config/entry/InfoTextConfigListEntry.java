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

package net.minecraftforge.fml.client.config.entry;

import net.minecraftforge.fml.client.config.ConfigScreen;
import net.minecraftforge.fml.client.config.entry.widget.InfoText;

/**
 * A dummy ConfigListEntry that just displays text (Usually error text) and does nothing else.
 *
 * @author Cadiboo
 */
public class InfoTextConfigListEntry<T> extends ConfigListEntry<T> {

	public InfoTextConfigListEntry(final ConfigScreen configScreen, final String label) {
		super(configScreen, new InfoText<>(label));
	}

	@Override
	public void renderToolTip(final int mouseX, final int mouseY, final float partialTicks) {
	}

	@Override
	public int preRenderWidgets(final int startY, final int startX, final int width, final int height, final int buttonSize) {
		return width;
	}

	@Override
	public boolean shouldRenderLabel() {
		return false;
	}

	@Override
	public boolean displayDefaultValue() {
		return false;
	}

}
