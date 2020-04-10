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

package net.minecraftforge.common.ticket;

/**
 * @deprecated No longer needed, {@link SimpleTicket} now accepts dummy managers
 */
@Deprecated
public class MultiTicketManager<T> implements ITicketManager<T>
{
    private final ITicketGetter<T>[] ticketManagers;

    @SafeVarargs
    public MultiTicketManager(ITicketGetter<T>... ticketManagers)
    {
        this.ticketManagers = ticketManagers;
    }

    @Override
    public void add(SimpleTicket<T> ticket)
    {
        for (ITicketGetter<T> manager : ticketManagers)
            manager.add(ticket);
    }

    @Override
    public void remove(SimpleTicket<T> ticket)
    {
        for (ITicketGetter<T> manager : ticketManagers)
            manager.remove(ticket);
    }
}
