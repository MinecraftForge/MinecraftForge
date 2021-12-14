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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EntityAnimator<T extends LivingEntity>
{
    protected final List<IEntityAnimation<T>> animations = new ArrayList<>();
    protected final DefaultPoseHolder<T> defaultPoseHolder;

    public EntityAnimator(EntityModel<T> model)
    {
        this.defaultPoseHolder = new DefaultPoseHolder<>(model);
    }

    public void addAnimation(IEntityAnimation<T> animation)
    {
        this.animations.add(animation);
        Collections.sort(this.animations);
    }

    public void execute(T entity, EntityModel<T> model, float animateTicks, float animateSpeed, float bobAnimateTicks, float headYaw, float headPitch, float partialTicks)
    {
        if (this.animations.isEmpty()) return;
        IEntityAnimation.Context context = new IEntityAnimation.Context(animateTicks, animateSpeed, bobAnimateTicks, headYaw, headPitch, partialTicks);
        for (IEntityAnimation<T> animation : this.animations)
        {
            if (animation.canRun(entity))
            {
                animation.apply(entity, model, context);
                if (animation.getMode() == IEntityAnimation.Mode.ACTIVE)
                {
                    break;
                }
            }
        }
    }

    public void restoreDefaultPose()
    {
        this.defaultPoseHolder.restoreDefaultPose();
    }
}
