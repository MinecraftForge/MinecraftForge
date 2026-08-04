/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.animation;

import com.google.common.base.MoreObjects;


/**
 * Event stored in the clip
 */
public final class Event implements Comparable<Event>
{
    private final String event;
    private final float offset;

    public Event(String event, float offset)
    {
        this.event = event;
        this.offset = offset;
    }

    /**
     * @return the name of the event.
     */
    public String event()
    {
        return event;
    }

    /**
     * @return how long ago the event happened, relative to the next event / first query time
     */
    public float offset()
    {
        return offset;
    }

    @Override
    public int compareTo(Event event)
    {
        return new Float(offset).compareTo(event.offset);
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(getClass()).add("event", event).add("offset", offset).toString();
    }
}
