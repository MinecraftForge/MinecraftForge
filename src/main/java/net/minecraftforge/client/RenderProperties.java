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

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;

public class RenderProperties
{
    public static EffectRenderer getEffectRenderer(EffectInstance effectInstance)
    {
        return getEffectRenderer(effectInstance.getEffect());
    }

    public static EffectRenderer getEffectRenderer(Effect effect)
    {
        return ((IEffectRendererAccessor)effect).getEffectRenderer();
    }

    public static IItemRenderProperties get(ItemStack stack)
    {
        return get(stack.getItem());
    }

    public static IItemRenderProperties get(Item item)
    {
        return ((IItemRenderPropertiesAccessor)item).getRenderProperties();
    }

    public static IBlockRenderProperties get(BlockState state)
    {
        return get(state.getBlock());
    }

    public static IBlockRenderProperties get(Block item)
    {
        return ((IBlockRenderPropertiesAccessor)item).getRenderProperties();
    }

    public interface IItemRenderPropertiesAccessor
    {
        /**
         * Use {@link #get(Item)} or {@link #get(ItemStack)} instead of calling this directly!
         */
        IItemRenderProperties getRenderProperties();
    }

    public interface IBlockRenderPropertiesAccessor
    {
        /**
         * Use {@link #get(Block)} or {@link #get(BlockState)} instead of calling this directly!
         */
        IBlockRenderProperties getRenderProperties();
    }

    public interface IEffectRendererAccessor
    {
        /**
         * Use {@link #getEffectRenderer(Effect)} or {@link #getEffectRenderer(EffectInstance)} instead of calling this directly!
         */
        EffectRenderer getEffectRenderer();
    }

    private RenderProperties()
    {
        // Not instantiable.
    }
}
