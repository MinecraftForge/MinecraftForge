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

package net.minecraftforge.fml.client.gui;

import net.minecraft.client.gui.GuiButton;

public class GuiButtonClickConsumer extends GuiButton {
    public interface DoubleBiConsumer {
        void applyAsDouble(double x, double y);
    }
    private final DoubleBiConsumer onClickAction;

    public GuiButtonClickConsumer(final int buttonId, final int x, final int y, final String buttonText, DoubleBiConsumer onClick) {
        super(buttonId, x, y, buttonText);
        this.onClickAction = onClick;
    }
    public GuiButtonClickConsumer(final int buttonId, final int x, final int y, final int widthIn, final int heightIn, final String buttonText, DoubleBiConsumer onClick) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        this.onClickAction = onClick;
    }

    @Override
    public void onClick(final double mouseX, final double mouseY) {
        onClickAction.applyAsDouble(mouseX, mouseY);
    }
}
