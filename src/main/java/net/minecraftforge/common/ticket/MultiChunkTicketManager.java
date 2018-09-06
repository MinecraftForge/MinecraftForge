package net.minecraftforge.common.ticket;

public class MultiChunkTicketManager<T> implements ITicketManager<T>
{
    private final ChunkTicketManager<T>[] ticketManagers;

    @SafeVarargs
    public MultiChunkTicketManager(ChunkTicketManager<T>... ticketManagers)
    {
        this.ticketManagers = ticketManagers;
    }

    @Override
    public void add(SimpleTicket<T> ticket)
    {
        for (ChunkTicketManager<T> manager : ticketManagers)
            manager.add(ticket);
    }

    @Override
    public void remove(SimpleTicket<T> ticket)
    {
        for (ChunkTicketManager<T> manager : ticketManagers)
            manager.remove(ticket);
    }
}
