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

package net.minecraftforge.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraftforge.client.IBlockRenderProperties;
import net.minecraftforge.client.RenderProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockRenderPropertiesMixin implements RenderProperties.IBlockRenderPropertiesAccessor
{
    private IBlockRenderProperties properties = IBlockRenderProperties.DUMMY;

    @Override
    public IBlockRenderProperties getRenderProperties()
    {
        return properties;
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(target=@Desc(value="<init>", args = {AbstractBlock.Properties.class}), at=@At("RETURN"))
    public void constructorInject(AbstractBlock.Properties blockProperties, CallbackInfo cb)
    {
        Block block = (Block) (Object) this;
        block.initializeClient(properties -> {
            if (properties == block)
                throw new IllegalStateException("Don't extend IBlockRenderProperties in your block, use an anonymous class instead.");
            this.properties = properties;
        });
    }
}
