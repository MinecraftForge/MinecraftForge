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

package net.minecraftforge.items;

import net.minecraft.client.gui.Font;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ItemDecorator;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ItemDecoratorHandler implements IItemDecoratorHandler
{
    private Map<String, ItemDecorator> ItemDecoratorCache = new HashMap();
    
    @Override
    public void addDecorator(ItemDecorator decorator)
    {
        ItemDecoratorCache.put(decorator.getKey().toString(), decorator);
    }

    @Override
    public void removeDecorator(ItemDecorator decorator)
    {
        ItemDecoratorCache.remove(decorator.getKey().toString());
    }

    @Override
    public void render(Font font, ItemStack stack, int xOffset, int yOffset, @Nullable String itemCountLabel)
    {
        for (ItemDecorator itemDecorator: ItemDecoratorCache.values()) {
            itemDecorator.render(font, stack, xOffset, yOffset, itemCountLabel);
        }
    }
}
