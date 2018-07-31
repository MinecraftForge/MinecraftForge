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

package net.minecraftforge.event.world;

public class FoundChunksForSpawningEventCreatureTypeData
{
    private final int k4;
    private final int l4;

    public FoundChunksForSpawningEventCreatureTypeData()
    {
        k4 = -1;
        l4 = -1;
    }

    public FoundChunksForSpawningEventCreatureTypeData(int k4In, int l4In)
    {
        k4 = k4In;
        l4 = l4In;
    }

    /**
     * The number of creatures of the given type that currently reside in the eligible spawn chunks. A value of -1 indicates that no attempt was made to calculate this number.
     */
    public int getk4()
    {
        return k4;
    }

    /**
     * The maximum number of creatures of the given type that can reside in the eligible spawn chunks before the game stops attempting to spawn more. A value of -1 indicates that
     * no attempt was made to calculate this number.
     */
    public int getl4()
    {
        return l4;
    }
}
