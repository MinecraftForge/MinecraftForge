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

package net.minecraftforge.common.model.animation;

import net.minecraft.client.resources.model.ModelState;
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
    Pair<ModelState, Iterable<Event>> apply(float time);

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
