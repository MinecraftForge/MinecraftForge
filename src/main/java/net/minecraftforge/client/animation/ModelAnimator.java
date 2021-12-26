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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModelAnimator<T>
{
    protected final ModelTree root;
    protected final List<ModelAnimation<T>> animations = new ArrayList<>();
    protected final ModelPoseHolder defaultPose;
    protected final AnimationData data;

    public ModelAnimator(IAnimatedModel model)
    {
        this.root = new ModelTree(model);
        this.defaultPose = new ModelPoseHolder(this.root);
        this.data = new AnimationData();
    }

    /**
     * Adds an animation to this animator and sorts based on mode and priority.
     * See {@link ModelAnimation#compareTo(ModelAnimation)} for an explanation on the ordering.
     *
     * @param animation an animation instance
     */
    public void addAnimation(ModelAnimation<T> animation)
    {
        this.animations.add(animation);
        Collections.sort(this.animations);
    }

    /**
     * Pushes custom data into the animator for the given key. The data is then stored in a {@link AnimationData}
     * instance which is passed to {@link ModelAnimation#apply(Object, ModelTree, AnimationData, float)}.
     * The data can then be retrieved with the same key using {@link AnimationData#get(AnimationKey)}.
     * Data should be pushed before calling {@link #execute} otherwise it won't be available for
     * animations. All data is cleared after running {@link #execute}.
     * See {@link AnimationKey} for creating custom keys.
     *
     * @param key   the animation key
     * @param value a value matching the same type from the key
     * @param <V>   the animation key type
     */
    public <V> void pushData(AnimationKey<V> key, V value)
    {
        this.data.push(key, value);
    }

    /**
     * Executes this animator and applies the registered animations to given model. Only the
     * animations that can run will be applied. If the animation is active, it will be applied then
     * all remaining active animations will be ignored. The execution order of animations is based
     * on mode and priority. See {@link ModelAnimation#compareTo(ModelAnimation)}} for an explanation.
     *
     * @param t           an instance of T
     * @param partialTick the current partial ticks
     */
    public void execute(T t, float partialTick)
    {
        if (this.animations.isEmpty()) return;
        for (ModelAnimation<T> animation : this.animations)
        {
            if (animation.canStart(t, this.data))
            {
                animation.apply(t, this.root, this.data, partialTick);
                if (animation.getMode() == ModelAnimation.Mode.ACTIVE)
                {
                    break;
                }
            }
        }
    }

    /**
     * Returns if this animator has any registered animations
     */
    public boolean hasAnimations()
    {
        return !this.animations.isEmpty();
    }

    /**
     * Restores the default pose of the model
     */
    public void restoreDefaultPose()
    {
        this.defaultPose.restoreDefaultPose();
    }

    public interface Getter<T>
    {
        @Nullable
        default ModelAnimator<T> getModelAnimator()
        {
            return null;
        }
    }
}
