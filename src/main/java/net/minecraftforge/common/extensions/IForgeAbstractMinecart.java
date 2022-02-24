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

import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

public interface IForgeAbstractMinecart
{
    public static float DEFAULT_MAX_SPEED_AIR_LATERAL = 0.4f;
    public static float DEFAULT_MAX_SPEED_AIR_VERTICAL = -1.0f;
    public static double DEFAULT_AIR_DRAG = 0.95f;

    private AbstractMinecart self() {
        return (AbstractMinecart)this;
    }

    /**
     * Internal, returns the current spot to look for the attached rail.
     */
    default BlockPos getCurrentRailPosition()
    {
        int x = Mth.floor(self().getX());
        int y = Mth.floor(self().getY());
        int z = Mth.floor(self().getZ());
        BlockPos pos = new BlockPos(x, y, z);
        if (self().level.getBlockState(pos.below()).is(BlockTags.RAILS)) pos = pos.below();
        return pos;
    }

    double getMaxSpeedWithRail();

    /**
     * Moved to allow overrides.
     * This code handles minecart movement and speed capping when on a rail.
     */
    void moveMinecartOnRail(BlockPos pos);

    /**
     * This function returns an ItemStack that represents this cart.
     * This should be an ItemStack that can be used by the player to place the cart,
     * but is not necessary the item the cart drops when destroyed.
     * @return An ItemStack that can be used to place the cart.
     */
    default ItemStack getCartItem()
    {
        return new ItemStack(Items.MINECART);
    }

    /**
     * Returns true if this cart can currently use rails.
     * This function is mainly used to gracefully detach a minecart from a rail.
     * @return True if the minecart can use rails.
     */
    boolean canUseRail();

    /**
     * Set whether the minecart can use rails.
     * This function is mainly used to gracefully detach a minecart from a rail.
     * @param use Whether the minecart can currently use rails.
     */
    void setCanUseRail(boolean use);

    /**
     * Return false if this cart should not call onMinecartPass() and should ignore Powered Rails.
     * @return True if this cart should call onMinecartPass().
     */
    default boolean shouldDoRailFunctions() {
        return true;
    }

    /**
     * Returns true if this cart is self propelled.
     * @return True if powered.
     */
    default boolean isPoweredCart() {
        return self().getMinecartType() == AbstractMinecart.Type.FURNACE;
    }

    /**
     * Returns true if this cart can be ridden by an Entity.
     * @return True if this cart can be ridden.
     */
    default boolean canBeRidden() {
        return self().getMinecartType() == AbstractMinecart.Type.RIDEABLE;
    }

    /**
     * Returns the carts max speed when traveling on rails. Carts going faster
     * than 1.1 cause issues with chunk loading. Carts cant traverse slopes or
     * corners at greater than 0.5 - 0.6. This value is compared with the rails
     * max speed and the carts current speed cap to determine the carts current
     * max speed. A normal rail's max speed is 0.4.
     *
     * @return Carts max speed.
     */
    default float getMaxCartSpeedOnRail() {
        return 1.2f;
    }

    /**
     * Returns the current speed cap for the cart when traveling on rails. This
     * functions differs from getMaxCartSpeedOnRail() in that it controls
     * current movement and cannot be overridden. The value however can never be
     * higher than getMaxCartSpeedOnRail().
     *
     * @return
     */
    float getCurrentCartSpeedCapOnRail();
    void setCurrentCartSpeedCapOnRail(float value);
    float getMaxSpeedAirLateral();
    void setMaxSpeedAirLateral(float value);
    float getMaxSpeedAirVertical();
    void setMaxSpeedAirVertical(float value);
    double getDragAir();
    void setDragAir(double value);

    default double getSlopeAdjustment() {
        return 0.0078125D;
    }

    /**
     * Called from Detector Rails to retrieve a redstone power level for comparators.
     */
    default int getComparatorLevel() {
        return -1;
    }
}
