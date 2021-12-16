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
    protected final ModelComponent root;
    protected final List<EntityAnimation<T>> animations = new ArrayList<>();
    protected final DefaultPoseHolder defaultPoseHolder;

    public EntityAnimator(EntityModel<T> model)
    {
        this.root = new ModelComponent(model);
        this.defaultPoseHolder = new DefaultPoseHolder(this.root);
    }

    /**
     * Adds an animation to this animator and sorts based on mode and priority.
     * See {@link EntityAnimation#compareTo(EntityAnimation)} for an explanation on the ordering.
     *
     * @param animation an animation instance for the specific entity
     */
    public void addAnimation(EntityAnimation<T> animation)
    {
        this.animations.add(animation);
        Collections.sort(this.animations);
    }

    /**
     * Executes this animator and applies the registered animations to given entity/model. Only the
     * animations that can run will be applied. If the animation is active, it will be applied then
     * all remaining active animations will be ignored. The execution order of animations is based
     * on mode and priority. See {@link EntityAnimation#compareTo(EntityAnimation)} for an explanation.
     *
     * @param entity          the entity currently being rendered
     * @param animateTicks    the current movement ticks
     * @param animateSpeed    the speed of the movement ticks
     * @param bobAnimateTicks the ticks of the bob animation
     * @param headYaw         the yaw of the entity
     * @param headPitch       the pitch of the entity
     * @param partialTicks    the current partial ticks
     */
    public void execute(T entity, float animateTicks, float animateSpeed, float bobAnimateTicks, float headYaw, float headPitch, float partialTicks)
    {
        if (this.animations.isEmpty()) return;
        EntityAnimation.Context context = new EntityAnimation.Context(animateTicks, animateSpeed, bobAnimateTicks, headYaw, headPitch, partialTicks);
        for (EntityAnimation<T> animation : this.animations)
        {
            if (animation.canStart(entity))
            {
                animation.apply(entity, this.root, context);
                if (animation.getMode() == EntityAnimation.Mode.ACTIVE)
                {
                    break;
                }
            }
        }
    }

    /**
     * Restores the default pose of this entity
     */
    public void restoreDefaultPose()
    {
        this.defaultPoseHolder.restoreDefaultPose();
    }
}
