package net.minecraftforge.common.ticket;

import java.util.Collection;

public interface ITicketManager<T>
{
    void add(SimpleTicket<T> ticket);

    void remove(SimpleTicket<T> ticket);
}
