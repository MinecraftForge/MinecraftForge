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

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class RenderProperties
{
    public static EffectRenderer getEffectRenderer(MobEffectInstance effectInstance)
    {
        return getEffectRenderer(effectInstance.getEffect());
    }

    public static EffectRenderer getEffectRenderer(MobEffect effect)
    {
        return effect.getEffectRendererInternal() instanceof EffectRenderer r ? r : EffectRenderer.DUMMY;
    }

    public static IItemRenderProperties get(ItemStack stack)
    {
        return get(stack.getItem());
    }

    public static IItemRenderProperties get(Item item)
    {
        return item.getRenderPropertiesInternal() instanceof IItemRenderProperties props ? props : IItemRenderProperties.DUMMY;
    }

    public static IBlockRenderProperties get(BlockState state)
    {
        return get(state.getBlock());
    }

    public static IBlockRenderProperties get(Block block)
    {
        return block.getRenderPropertiesInternal() instanceof IBlockRenderProperties props ? props : IBlockRenderProperties.DUMMY;
    }

    private RenderProperties()
    {
        // Not instantiable.
    }
}
