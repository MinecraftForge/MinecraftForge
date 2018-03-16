/*
 * Minecraft Forge
 * Copyright (c) 2018.
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

package net.minecraftforge.client.event;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;

/**
 * This event is called whenever {@link net.minecraft.item.Item#shouldRenderAdditionalOverlay(ItemStack)} returns true
 */
public class RenderItemOverlayEvent extends Event
{
    private final FontRenderer fr;
    private final ItemStack item;
    private final int xPosition;
    private final int yPosition;

    public RenderItemOverlayEvent(FontRenderer fr, ItemStack item, int xPosition, int yPosition) {
        this.fr = fr;
        this.item = item;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }

    @Nonnull
    public ItemStack getItem() {
        return item;
    }

    public int getxPosition() {
        return xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public FontRenderer getFontRenderer() {
        return fr;
    }
}
