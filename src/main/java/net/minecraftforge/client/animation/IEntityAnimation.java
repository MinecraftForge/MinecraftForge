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

import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public interface IEntityAnimation<E extends LivingEntity> extends Comparable<IEntityAnimation<E>>
{
    boolean canRun(E entity);

    void apply(E entity, EntityModel<E> model, Context context);

    default Mode getMode()
    {
        return Mode.ACTIVE;
    }

    default Priority getPriority()
    {
        return Priority.DEFAULT;
    }

    @Override
    default int compareTo(@NotNull IEntityAnimation<E> o)
    {
        if(this.getMode() == o.getMode())
        {
            return this.getPriority().ordinal() - o.getPriority().ordinal();
        }
        else if(o.getMode() == Mode.ACTIVE)
        {
            return -1;
        }
        return 1;
    }

    enum Mode
    {
        PASSIVE, ACTIVE
    }

    enum Priority
    {
        FIRST, DEFAULT, LAST
    }

    record Context(float animateTicks, float animateSpeed, float bobAnimateTicks, float headYaw, float headPitch, float partialTicks) {}
}
