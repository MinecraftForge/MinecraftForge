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

package net.minecraftforge.advancements.critereon;


import com.google.gson.JsonObject;
import net.minecraft.util.JsonUtils;
import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * An {@link ItemPredicate} that matches oredicts.
 */
public class OredictItemPredicate extends ItemPredicate
{
    private final String ore;

    public OredictItemPredicate(String ore)
    {
        this.ore = ore;
    }

    public OredictItemPredicate(JsonObject jsonObject) { this(JsonUtils.getString(jsonObject, "ore")); }

    @Override
    public boolean test(ItemStack stack)
    {
        return !stack.isEmpty() && ArrayUtils.contains(OreDictionary.getOreIDs(stack), OreDictionary.getOreID(ore));
    }
}
