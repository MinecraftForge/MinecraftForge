/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * An Object that is used by the {@Link: net.minecraftforge.items.ItemDecoratorHandler} capability.
 */
public abstract class ItemDecorator{
    private final ResourceLocation key;
    public ItemDecorator(ResourceLocation key)
    {
        this.key = key;
    }

    /**
     * Is passed the parameters from {@link ItemRenderer#renderGuiItemDecorations(net.minecraft.client.gui.Font, net.minecraft.world.item.ItemStack, int, int, java.lang.String)}
     */
    public void render(Font font, ItemStack stack, int xOffset, int yOffset, @Nullable String stackSizeLabel)
    {}
    
    public ResourceLocation getKey() {
        return key;
    }
}
