/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.fml.common.eventhandler;

public interface IEventExceptionHandler
{
    /**
     * Fired when a EventListener throws an exception for the specified event on the event bus.
     * After this function returns, the original Throwable will be propagated upwards.
     *
     * @param bus The bus the event is being fired on
     * @param event The event that is being fired
     * @param listeners All listeners that are listening for this event, in order
     * @param index Index for the current listener being fired.
     * @param throwable The throwable being thrown
     */
    void handleException(EventBus bus, Event event, IEventListener[] listeners, int index, Throwable throwable);
}
