/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 *
 * Fired when the player's tutorial step changes
 *
 */
public class TutorialStepChangeEvent extends Event
{

    protected final TutorialSteps previous;

    protected TutorialSteps next;

    public TutorialStepChangeEvent(TutorialSteps previous, TutorialSteps next)
    {
        this.previous = previous;
        this.next = next;
    }

    /**
     *
     * Gets the tutorial step the player was on previously
     *
     * @return The previous tutorial step
     */
    public TutorialSteps getPrevious()
    {
        return this.previous;
    }

    /**
     *
     * Gets the tutorial step the player will be moving on to
     *
     * @return The next tutorial step
     */
    public TutorialSteps getNext()
    {
        return this.next;
    }

    /**
     *
     * Fired before the step changes
     * Cancelling will keep the player on the current step
     *
     */
    @Cancelable
    public static class Pre extends TutorialStepChangeEvent
    {

        public Pre(TutorialSteps previous, TutorialSteps next)
        {
            super(previous, next);
        }

        /**
         *
         * Set the step that the client will progress to
         *
         * @param next The next tutorial step
         */
        public void setNext(TutorialSteps next)
        {
            this.next = next;
        }
    }

    /**
     *
     * Fired after the step changes
     *
     */
    public static class Post extends TutorialStepChangeEvent
    {

        public Post(TutorialSteps previous, TutorialSteps next)
        {
            super(previous, next);
        }
    }
}
