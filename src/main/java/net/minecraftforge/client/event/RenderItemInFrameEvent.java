/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

import net.minecraft.client.renderer.entity.RenderItemFrame;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;

/**
 * This event is called when an item is rendered in an item frame.
 *
 * You can set canceled to do no further vanilla processing.
 */
@Cancelable
public class RenderItemInFrameEvent extends Event
{
    private final ItemStack item;
    private final EntityItemFrame entityItemFrame;
    private final RenderItemFrame renderer;

    public RenderItemInFrameEvent(EntityItemFrame itemFrame, RenderItemFrame renderItemFrame)
    {
        item = itemFrame.getDisplayedItem();
        entityItemFrame = itemFrame;
        renderer = renderItemFrame;
    }

    @Nonnull
    public ItemStack getItem()
    {
        return item;
    }

    public EntityItemFrame getEntityItemFrame()
    {
        return entityItemFrame;
    }

    public RenderItemFrame getRenderer()
    {
        return renderer;
    }
}
