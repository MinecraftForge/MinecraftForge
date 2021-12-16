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

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public abstract class EntityAnimation<E extends LivingEntity> implements Comparable<EntityAnimation<E>>
{
    private final Mode mode;
    private final Priority priority;

    public EntityAnimation(Mode mode, Priority priority)
    {
        this.mode = mode;
        this.priority = priority;
    }

    public EntityAnimation(Mode mode)
    {
        this(mode, Priority.DEFAULT);
    }

    /**
     * Gets the mode of the animation
     */
    public final Mode getMode()
    {
        return mode;
    }

    /**
     * Gets the priority of the animation
     */
    public final Priority getPriority()
    {
        return priority;
    }

    /**
     * The condition to test if the animation can start. This is where a test like holding or using
     * a particular item, riding an entity, etc can be used. If this animation's mode is active,
     * returning true here will cancel remaining active animations from executing.
     *
     * @param entity the entity the animation is being applied to
     * @return the result of the test
     */
    public abstract boolean canStart(E entity);

    /**
     * Executes and applies the animation to the specified model. The model will need to be cast to
     * the entity specific model in order to access additional fields and methods.
     *
     * <p>
     * For players this would look like:
     * <p>
     * <code>if (model instanceof PlayerModel<?> playerModel) {</code>
     * <p>
     * <code>// your animation code</code>
     * <p>
     * <code>}</code>
     * <p>
     * The context parameter contains additional data for calculating animations should it be used.
     * It should be noted that another animation may have already been applied before this animation
     * is executed. See {@link EntityAnimation#compareTo(EntityAnimation)} for an explanation on
     * order animations are applied.
     *
     * @param entity the entity currently being rendered
     * @param root the root model component contain
     * @param context additional data used for calculating animations
     */
    public abstract void apply(E entity, ModelComponent root, AnimationData data, float partialTick);

    /**
     * Determines how animations are executed. The order is based on the mode and priority
     * of the animation. Below is a visual representation of the ordering.
     * <ul>
     * <li>PASSIVE - FIRST</li>
     * <li>PASSIVE - DEFAULT</li>
     * <li>PASSIVE - LAST</li>
     * <li>ACTIVE - FIRST</li>
     * <li>ACTIVE - DEFAULT (Cancelled if an ACTIVE - FIRST is executed)</li>
     * <li>ACTIVE - LAST (Cancelled if an ACTIVE - FIRST/DEFAULT is executed)</li>
     * </ul>
     * @param o an entity animation
     * @return See {@link Comparable} documentation
     */
    @Override
    public final int compareTo(@NotNull EntityAnimation<E> o)
    {
        if (this.getMode() == o.getMode())
        {
            return this.getPriority().ordinal() - o.getPriority().ordinal();
        }
        else if (o.getMode() == Mode.ACTIVE)
        {
            return -1;
        }
        return 1;
    }

    /**
     * The animation mode. Read individual enums for an explanation on which one to use.
     */
    public enum Mode
    {
        /**
         * A passive animation is considered a background animation like walking or sitting. It is
         * executed first before active animations. It can override or be overridden by other
         * passive animations.
         */
        PASSIVE,

        /**
         * An active animation is considered an important animation. It will override passive
         * animations. The big difference is that once an active animation is executed, all remaining
         * active animations will not be executed.
         */
        ACTIVE
    }

    /**
     * The priority of the animation. Read individual enums for an explanation on which one to use.
     */
    public enum Priority
    {
        /**
         * Marks the animation to execute first. If the animation mode is {@link Mode#PASSIVE}, it
         * may be overridden by animations with the same or later priority. Otherwise if the mode is
         * {@link Mode#ACTIVE} and the animation is executed, active animations with the same or
         * later priority will not be executed.
         */
        FIRST,

        /**
         * The default priority of animations. A passive animation executed with this priority can
         * override any passive animations executed in an earlier priority. It is not guaranteed that
         * an active animation will be executed at this priority if one was executed in an early
         * priority.
         */
        DEFAULT,

        /**
         * Marks the animation to run last. A passive animation executed with this priority can
         * override any passive animations executed in an earlier priority. It is not guaranteed that
         * an active animation will be executed at this priority if one was executed in an early
         * priority.
         */
        LAST
    }
}
