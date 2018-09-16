/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.event.terraingen;

import net.minecraftforge.eventbus.api.Event;

public class InitMapGenEvent extends Event
{
    /** Use CUSTOM to filter custom event types
     */
    public static enum EventType { CAVE, MINESHAFT, NETHER_BRIDGE, NETHER_CAVE, RAVINE, SCATTERED_FEATURE, STRONGHOLD, VILLAGE, OCEAN_MONUMENT, WOODLAND_MANSION, END_CITY, CUSTOM }

    /* TODO: Re-Evaluate
    private final EventType type;
    private final MapGenBase originalGen;
    private MapGenBase newGen;

    InitMapGenEvent(EventType type, MapGenBase original)
    {
        this.type = type;
        this.originalGen = original;
        this.setNewGen(original);
    }
    public EventType getType() { return type; }
    public MapGenBase getOriginalGen() { return originalGen; }
    public MapGenBase getNewGen() { return newGen; }
    public void setNewGen(MapGenBase newGen) { this.newGen = newGen; }
    */
}
