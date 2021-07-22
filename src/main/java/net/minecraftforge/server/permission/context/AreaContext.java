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

package net.minecraftforge.server.permission.context;

import com.google.common.base.Preconditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;

public class AreaContext extends PlayerContext
{
    private final AABB area;

    public AreaContext(Player ep, AABB aabb)
    {
        super(ep);
        area = Preconditions.checkNotNull(aabb, "AxisAlignedBB can't be null in AreaContext!");
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nullable
    public <T> T get(ContextKey<T> key)
    {
        return key.equals(ContextKeys.AREA) ? (T) area : super.get(key);
    }

    @Override
    protected boolean covers(ContextKey<?> key)
    {
        return key.equals(ContextKeys.AREA);
    }
}
