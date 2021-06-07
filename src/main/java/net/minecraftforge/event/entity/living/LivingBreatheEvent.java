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

package net.minecraftforge.event.entity.living;

import net.minecraft.world.entity.LivingEntity;

public class LivingBreatheEvent extends LivingEvent
{
    private boolean canBreathe;
    public LivingBreatheEvent(LivingEntity entity, boolean canBreathe)
    {
        super(entity);
        this.canBreathe = canBreathe;
    }

    public boolean isCanBreathe()
    {
        return canBreathe;
    }

    public void setCanBreathe(boolean canBreathe)
    {
        this.canBreathe = canBreathe;
    }
}
