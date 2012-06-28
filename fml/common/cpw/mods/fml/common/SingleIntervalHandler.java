package cpw.mods.fml.common;

import java.util.EnumSet;

public class SingleIntervalHandler implements IScheduledTickHandler
{
    private ITickHandler wrapped;
    public SingleIntervalHandler(ITickHandler handler)
    {
        this.wrapped=handler;
    }

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData)
    {
        wrapped.tickStart(type, tickData);
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {
        wrapped.tickEnd(type, tickData);
    }

    @Override
    public EnumSet<TickType> ticks()
    {
        return wrapped.ticks();
    }

    @Override
    public String getLabel()
    {
        return wrapped.getLabel();
    }

    @Override
    public int nextTickSpacing()
    {
        return 1;
    }

}
