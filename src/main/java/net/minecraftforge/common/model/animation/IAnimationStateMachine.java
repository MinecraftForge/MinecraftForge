/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.model.animation;

import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraftforge.common.animation.Event;

import org.apache.commons.lang3.tuple.Pair;

/**
 * State machine representing the model animation.
 */
public interface IAnimationStateMachine
{
    /**
     * Sample the state and events at the current time.
     * Event iterable will contain all events that happened from the last invocation of this method, from most to least recent.
     * Event offset is relative to the previous event, and for the first event it's relative to the current time. 
     */
    Pair<IModelTransform, Iterable<Event>> apply(float time);

    /**
     * Transition to a new state.
     */
    void transition(String newState);

    /**
     * Get current state name.
     */
    String currentState();

    /**
     * Set to true if the machine should handle special events that come from the clips (they start with '!').
     * Right now only implemented event is "!transition:<state_name>".
     * Default value is true.
     */
    void shouldHandleSpecialEvents(boolean value);
}
