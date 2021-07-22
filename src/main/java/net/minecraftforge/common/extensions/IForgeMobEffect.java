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

package net.minecraftforge.common.extensions;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public interface IForgeMobEffect
{
    private MobEffect self() {
        return (MobEffect)this;
    }

    /**
     * Get a fresh list of items that can cure this Potion.
     * All new PotionEffects created from this Potion will call this to initialize the default curative items
     * @see PotionEffect#getCurativeItems
     * @return A list of items that can cure this Potion
     */
    default List<ItemStack> getCurativeItems() {
       ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
       ret.add(new ItemStack(Items.MILK_BUCKET));
       return ret;
    }

    /**
     * Used for determining {@code PotionEffect} sort order in GUIs.
     * Defaults to the {@code PotionEffect}'s liquid color.
     * @param potionEffect the {@code PotionEffect} instance containing the potion
     * @return a value used to sort {@code PotionEffect}s in GUIs
     */
    default int getGuiSortColor(MobEffectInstance potionEffect) {
       return self().getColor();
    }
}
