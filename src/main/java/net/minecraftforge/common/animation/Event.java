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
