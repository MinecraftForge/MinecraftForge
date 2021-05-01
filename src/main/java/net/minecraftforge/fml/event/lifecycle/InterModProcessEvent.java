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

package net.minecraftforge.fml.event.lifecycle;

import net.minecraftforge.fml.ModContainer;

import java.util.function.Predicate;

/**
 * This is the fourth of four commonly called events during mod lifecycle startup.
 *
 * Called after {@link InterModEnqueueEvent}
 *
 * Retrieve {@link net.minecraftforge.fml.InterModComms} {@link net.minecraftforge.fml.InterModComms.IMCMessage} suppliers
 * and process them as you wish with this event.
 *
 * This is a parallel dispatch event.
 *
 * @see #getIMCStream()
 * @see #getIMCStream(Predicate)
 */
public class InterModProcessEvent extends ParallelDispatchEvent
{
    public InterModProcessEvent(final ModContainer container)
    {
        super(container);
    }
}
