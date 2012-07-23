package cpw.mods.fml.common.registry;

import java.util.List;
import java.util.PriorityQueue;

import com.google.common.collect.Queues;

import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.SingleIntervalHandler;

public class TickRegistry
{

    /**
     * We register our delegate here
     * @param handler
     */
    
    public static class TickQueueElement implements Comparable<TickQueueElement>
    {
        public static long tickCounter = 0;
        public TickQueueElement(IScheduledTickHandler ticker)
        {
            this.ticker = ticker;
            update();
        }
        @Override
        public int compareTo(TickQueueElement o)
        {
            return (int)(next - o.next);
        }
    
        public void update()
        {
            next = tickCounter + Math.max(ticker.nextTickSpacing(),1);
        }
    
        private long next;
        public IScheduledTickHandler ticker;
    
        public boolean scheduledNow()
        {
            return tickCounter >= next;
        }
    }

    private static PriorityQueue<TickQueueElement> tickHandlers = Queues.newPriorityQueue();
    
    public static void registerScheduledTickHandler(IScheduledTickHandler handler)
    {
        tickHandlers.add(new TickQueueElement(handler));
    }

    public static void registerTickHandler(ITickHandler handler)
    {
        registerScheduledTickHandler(new SingleIntervalHandler(handler));
    }

    public static void updateTickQueue(List<IScheduledTickHandler> ticks)
    {
        ticks.clear();
        TickQueueElement.tickCounter++;
        while (true)
        {
            if (tickHandlers.size()==0 || !tickHandlers.peek().scheduledNow())
            {
                break;
            }
            TickRegistry.TickQueueElement tickQueueElement  = tickHandlers.poll();
            tickQueueElement.update();
            tickHandlers.offer(tickQueueElement);
            ticks.add(tickQueueElement.ticker);
        }
    }

}
