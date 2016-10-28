/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;

public abstract class RenderItemLayerEvent extends Event
{
    private ItemStack stack;
    private int index;
    private TransformType transformType;
    
    public RenderItemLayerEvent(ItemStack stack, int index, TransformType transformType)
    {
        this.stack = stack;
        this.index = index;
        this.transformType = transformType;
    }
    
    public ItemStack getStack()
    {
        return stack;
    }
    
    public int getRenderLayer()
    {
        return index;
    }
    
    public TransformType getTransformType()
    {
        return transformType;
    }
    
    public static class Pre extends RenderItemLayerEvent
    {
        public Pre(ItemStack stack, int index, TransformType transformType)
        {
            super(stack, index, transformType);
        }
    }
    
    public static class Post extends RenderItemLayerEvent
    {
        public Post(ItemStack stack, int index, TransformType transformType)
        {
            super(stack, index, transformType);
        }
    }
}
