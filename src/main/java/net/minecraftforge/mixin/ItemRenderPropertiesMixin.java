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

import net.minecraft.item.Item;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.client.RenderProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.class)
public class ItemRenderPropertiesMixin implements RenderProperties.IItemRenderPropertiesAccessor
{
    private IItemRenderProperties properties = IItemRenderProperties.DUMMY;

    @Override
    public IItemRenderProperties getRenderProperties()
    {
        return properties;
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(target=@Desc(value="<init>", args = {Item.Properties.class}),  at=@At("RETURN"))
    public void constructorInject(Item.Properties itemProperties, CallbackInfo cb)
    {
        Item item = (Item) (Object) this;
        item.initializeClient(properties -> {
            if (properties == item)
                throw new IllegalStateException("Don't extend IItemRenderProperties in your item, use an anonymous class instead.");
            this.properties = properties;
        });
    }
}
