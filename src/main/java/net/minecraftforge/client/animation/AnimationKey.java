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

/**
 * Record used for creating animation keys. Keys are used for storing and retrieving data in {@link AnimationData}
 * @param <T> any type
 */
public record AnimationKey<T>(String name, T defaultValue)
{
    public static final AnimationKey<Float> YAW = new AnimationKey<>("yaw", 0F);
    public static final AnimationKey<Float> PITCH = new AnimationKey<>("pitch", 0F);
    public static final AnimationKey<Float> DELTA_BODY_YAW = new AnimationKey<>("delta_body_yaw", 0F);
    public static final AnimationKey<Float> MOVEMENT_TICKS = new AnimationKey<>("movement_ticks", 0F);
    public static final AnimationKey<Float> MOVEMENT_SPEED = new AnimationKey<>("movement_speed", 0F);
    public static final AnimationKey<Float> BOB_TICKS = new AnimationKey<>("bob_ticks", 0F);

    // Should there be more common keys?
}
