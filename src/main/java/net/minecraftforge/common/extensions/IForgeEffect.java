/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IForgeEffect {

    default Effect getEffect() {
        return (Effect)this;
    }

    /**
     * If the Potion effect should be displayed in the players inventory
     * @param effect the active PotionEffect
     * @return true to display it (default), false to hide it.
     */
    default boolean shouldRender(EffectInstance effect) { return true; }

    /**
     * If the standard PotionEffect text (name and duration) should be drawn when this potion is active.
     * @param effect the active PotionEffect
     * @return true to draw the standard text
     */
    default boolean shouldRenderInvText(EffectInstance effect) { return true; }

    /**
     * If the Potion effect should be displayed in the player's ingame HUD
     * @param effect the active PotionEffect
     * @return true to display it (default), false to hide it.
     */
    default boolean shouldRenderHUD(EffectInstance effect) { return true; }

    /**
     * Called to draw the this Potion onto the player's inventory when it's active.
     * This can be used to e.g. render Potion icons from your own texture.
     *
     * @param effect the active PotionEffect
     * @param gui the gui instance
     * @param mStack The MatrixStack
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z level
     */
    @OnlyIn(Dist.CLIENT)
    default void renderInventoryEffect(EffectInstance effect, DisplayEffectsScreen<?> gui, MatrixStack mStack, int x, int y, float z) { }

    /**
     * Called to draw the this Potion onto the player's ingame HUD when it's active.
     * This can be used to e.g. render Potion icons from your own texture.
     * @param effect the active PotionEffect
     * @param gui the gui instance
     * @param mStack The MatrixStack
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z level
     * @param alpha the alpha value, blinks when the potion is about to run out
     */
    @OnlyIn(Dist.CLIENT)
    default void renderHUDEffect(EffectInstance effect, AbstractGui gui, MatrixStack mStack, int x, int y, float z, float alpha) { }

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
    default int getGuiSortColor(EffectInstance potionEffect) {
       return getEffect().getLiquidColor();
    }
}
