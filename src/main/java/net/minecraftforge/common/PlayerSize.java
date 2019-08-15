/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.common;


import com.google.common.base.Preconditions;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;

import java.util.HashMap;
import java.util.Map;

public class PlayerSize
{
    private final static Map<Pose, EntitySize> SIZE_BY_POSE = new HashMap<>();

    /**
     * Specify the entity size a player has in the given pose.
     * Does not affect vanilla poses
     */
    public static void specifySize(Pose pose, EntitySize size){
        Preconditions.checkNotNull(pose);
        Preconditions.checkNotNull(size);
        SIZE_BY_POSE.put(pose,size);
    }

    public static EntitySize getOrDefault(Pose pose, EntitySize defaultSize){
        return SIZE_BY_POSE.getOrDefault(pose,defaultSize);
    }
}
