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

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IForgeEffectInstance {

    default EffectInstance getEffectInstance() {
        return (EffectInstance)this;
    }

    /**
     * If the Potion effect should be displayed in the players inventory
     * @return true to display it (default), false to hide it.
     */
    default boolean shouldRender() {
        return getEffectInstance().getPotion().shouldRender(getEffectInstance());
    }

    /**
     * If the standard PotionEffect text (name and duration) should be drawn when this potion is active.
     * @return true to draw the standard text
     */
    default boolean shouldRenderInvText() {
        return getEffectInstance().getPotion().shouldRenderInvText(getEffectInstance());
    }

    /**
     * If the Potion effect should be displayed in the player's ingame HUD
     * @return true to display it (default), false to hide it.
     */
    default boolean shouldRenderHUD() {
        return getEffectInstance().getPotion().shouldRenderHUD(getEffectInstance());
    }

    /**
     * Called to draw the this Potion onto the player's inventory when it's active.
     * This can be used to e.g. render Potion icons from your own texture.
     *
     * @param gui the gui instance
     * @param mStack The MatrixStack
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z level
     */
    @OnlyIn(Dist.CLIENT)
    default void renderInventoryEffect(DisplayEffectsScreen<?> gui, MatrixStack mStack, int x, int y, float z) {
        getEffectInstance().getPotion().renderInventoryEffect(getEffectInstance(), gui, mStack, x, y, z);
    }

    /**
     * Called to draw the this Potion onto the player's ingame HUD when it's active.
     * This can be used to e.g. render Potion icons from your own texture.
     *
     * @param gui the gui instance
     * @param mStack The MatrixStack
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z level
     * @param alpha the alpha value, blinks when the potion is about to run out
     */
    @OnlyIn(Dist.CLIENT)
    default void renderHUDEffect(AbstractGui gui, MatrixStack mStack, int x, int y, float z, float alpha) {
        getEffectInstance().getPotion().renderHUDEffect(getEffectInstance(), gui, mStack, x, y, z, alpha);
    }

    /***
     * Returns a list of curative items for the potion effect
     * By default, this list is initialized using {@link Potion#getCurativeItems}
     *
     * @return The list (ItemStack) of curative items for the potion effect
     */
    List<ItemStack> getCurativeItems();

    /***
     * Checks the given ItemStack to see if it is in the list of curative items for the potion effect
     * @param stack The ItemStack being checked against the list of curative items for this PotionEffect
     * @return true if the given ItemStack is in the list of curative items for this PotionEffect, false otherwise
     */
    default boolean isCurativeItem(ItemStack stack) {
       return this.getCurativeItems().stream().anyMatch(e -> e.isItemEqual(stack));
    }

    /***
     * Sets the list of curative items for this potion effect, overwriting any already present
     * @param curativeItems The list of ItemStacks being set to the potion effect
     */
    void setCurativeItems(List<ItemStack> curativeItems);

    /***
     * Adds the given stack to the list of curative items for this PotionEffect
     * @param stack The ItemStack being added to the curative item list
     */
    default void addCurativeItem(ItemStack stack) {
       if (!this.isCurativeItem(stack))
          this.getCurativeItems().add(stack);
    }

    default void writeCurativeItems(CompoundNBT nbt) {
       ListNBT list = new ListNBT();
       getCurativeItems().forEach(s -> list.add(s.write(new CompoundNBT())));
       nbt.put("CurativeItems", list);
    }
}
