/*
 * Minecraft Forge
 * Copyright (c) 2016-2022.
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

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.UseAnim;

/**
 * An ArmPose that can be defined by the user. Register one to a {@link net.minecraft.world.item.UseAnim}
 * using {@link net.minecraftforge.client.ClientRegistry#registerUseAnimation(UseAnim, IForgeArmPose)}.
 */
public interface IForgeArmPose
{

    /**
     * @return Whether this pose should disable off-hand animations
     */
    boolean isPoseTwoHanded();

    /**
     * This method should be used to apply all wanted transformations to the player if when the UseAnim is active.
     * You can use {@link LivingEntity#getTicksUsingItem()} and {@link LivingEntity#getUseItemRemainingTicks()} for moving animations.
     *
     * @param model  The humanoid model
     * @param entity The humanoid entity
     * @param <T>    The exact entity type. Since HumanoidModel is generic, this must be generic as well.
     */
    <T extends LivingEntity> void applyTransform(HumanoidModel<T> model, T entity);

}
