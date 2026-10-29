/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.ticket;

import net.minecraft.world.level.ChunkPos;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

/// @deprecated This class is scheduled for removal in 26.4 due to a lack of adoption by mods, in order to ease porting.
///             Please reach out to the Forge team if you are relying on this and would like to see it remain in Forge.
///             We may keep it or add it back in a future version if there is enough demand.
@Deprecated(forRemoval = true, since = "26.4")
public class ChunkTicketManager<T> implements ITicketGetter<T>
{
    private final Set<SimpleTicket<T>> tickets = Collections.newSetFromMap(new WeakHashMap<>());
    public final ChunkPos pos;

    public ChunkTicketManager(ChunkPos pos)
    {
        this.pos = pos;
    }

    @Override
    public void add(SimpleTicket<T> ticket)
    {
        this.tickets.add(ticket);
    }

    @Override
    public void remove(SimpleTicket<T> ticket)
    {
        this.tickets.remove(ticket);
    }

    @Override
    public Collection<SimpleTicket<T>> getTickets()
    {
        return tickets;
    }
}
