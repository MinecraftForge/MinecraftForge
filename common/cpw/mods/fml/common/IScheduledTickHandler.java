package cpw.mods.fml.common;

public interface IScheduledTickHandler extends ITickHandler
{
    /**
     * Return the number of actual ticks that will pass
     * before your next tick will fire. This will be called
     * just after your last tick fired to compute the next delay.
     * @param tick
     * @return
     */
    public int nextTickSpacing();
}
