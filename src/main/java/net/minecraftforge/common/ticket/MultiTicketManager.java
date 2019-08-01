/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

import java.util.Arrays;
import java.util.function.Supplier;

public class MultiTicketManager<T> implements ITicketManager<T>
{
    private final Supplier<? extends ITicketGetter<T>>[] ticketManagersSuppliers;
    private ITicketGetter<T>[] cachedManagers;

    @SuppressWarnings("unchecked")
    @SafeVarargs
    public MultiTicketManager(Supplier<? extends ITicketGetter<T>>... ticketManagers)
    {
        this.ticketManagersSuppliers = ticketManagers;
        this.cachedManagers = Arrays.stream(this.ticketManagersSuppliers).map(Supplier::get).toArray(ITicketGetter[]::new);
    }

    @Override
    public void add(SimpleTicket<T> ticket)
    {
        for (int i = 0; i < cachedManagers.length; i++)
            getManager(i).add(ticket);
    }

    private ITicketGetter<T> getManager(int index)
    {
        ITicketGetter<T> manager = cachedManagers[index];
        if (manager.isDestroyed())
        {
            manager = ticketManagersSuppliers[index].get();
            cachedManagers[index] = manager;
        }
        return manager;
    }

    @Override
    public void remove(SimpleTicket<T> ticket)
    {
        for (int i = 0; i < cachedManagers.length; i++)
            getManager(i).remove(ticket);
    }

    @Override
    public boolean isDestroyed()
    {
        return Arrays.stream(cachedManagers).allMatch(ITicketManager::isDestroyed);
    }
}
