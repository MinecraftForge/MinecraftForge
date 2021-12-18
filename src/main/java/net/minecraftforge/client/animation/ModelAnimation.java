package net.minecraftforge.client.animation;

import javax.annotation.Nonnull;

public abstract class ModelAnimation<T> implements Comparable<ModelAnimation<T>>
{
    private final Mode mode;
    private final Priority priority;

    public ModelAnimation(Mode mode, Priority priority)
    {
        this.mode = mode;
        this.priority = priority;
    }

    public ModelAnimation(Mode mode)
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
     * The condition to test if the animation can start. This is where a test using {@link T} can be
     * applied. For example, if this animation is for an entity, this could check for a particular
     * held item, riding an entity, etc. If this animation's mode is active, returning true here
     * will cancel remaining active animations from executing.
     *
     * @param t an instance of T
     * @return the result of the test
     */
    public abstract boolean canStart(T t, AnimationData data);

    /**
     * Executes and applies the animation. This is where modifications to the model are to be
     * applied. Model Parts from the model have been broken down into ModelComponent; an intermediate
     * layer that allows model parts to be retrieved using a string path. See {@link ModelTree#get}
     * for details. TODO The structure of the model can be dumped using {@link ModelTree#dump()}
     * <p>
     * The {@link AnimationData} parameter is used for retrieving custom data that has been passed
     * to the {@link ModelAnimator} implementation that handles the rendering of {@link T}
     * TODO The data can be dumped using {@link AnimationData#dump()}
     * <p>
     * It should be noted that another animation may have already been applied before this animation
     * is executed. See {@link ModelAnimation#compareTo(ModelAnimation)} for an explanation on the
     * order animations are applied.
     *
     * @param t an instance of T
     * @param root the root model component contain
     * @param data additional data passed by the animator
     * @param partialTick the current partial ticks
     */
    public abstract void apply(T t, ModelTree root, AnimationData data, float partialTick);

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
     * @param o a model animation
     * @return See {@link Comparable} documentation
     */
    @Override
    public final int compareTo(@Nonnull ModelAnimation<T> o)
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
         * Marks the animation to execute first. If the animation mode is {@link ModelAnimation.Mode#PASSIVE}, it
         * may be overridden by animations with the same or later priority. Otherwise if the mode is
         * {@link ModelAnimation.Mode#ACTIVE} and the animation is executed, active animations with the same or
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
