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

package net.minecraftforge.client.animation;

import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;

public class AnimationData
{
    private final Reference2ObjectMap<AnimationKey<?>, Object> dataMap = new Reference2ObjectOpenHashMap<>();

    <V> void push(AnimationKey<V> key, V value)
    {
        this.dataMap.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <V> V get(AnimationKey<V> key)
    {
        return (V) this.dataMap.getOrDefault(key, key.defaultValue());
    }

    public static void fillFromLivingEntity(EntityAnimator<?> animator, float animateTicks, float animateSpeed, float bobAnimateTicks, float headYaw, float headPitch, float deltaBodyYaw)
    {
        if (!animator.hasAnimations()) return; // No point filling if no animations exist
        animator.pushData(AnimationKey.MOVEMENT_TICKS, animateTicks);
        animator.pushData(AnimationKey.MOVEMENT_SPEED, animateSpeed);
        animator.pushData(AnimationKey.BOB_TICKS, bobAnimateTicks);
        animator.pushData(AnimationKey.YAW, headYaw);
        animator.pushData(AnimationKey.PITCH, headPitch);
        animator.pushData(AnimationKey.DELTA_BODY_YAW, deltaBodyYaw);
    }
}
