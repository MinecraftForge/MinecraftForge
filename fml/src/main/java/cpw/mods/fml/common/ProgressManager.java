package cpw.mods.fml.common;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @deprecated not a stable API, will break, don't use this yet
 */
@Deprecated
public class ProgressManager
{
    private static final List<ProgressBar> bars = new CopyOnWriteArrayList<ProgressBar>();

    /**
     * @deprecated not a stable API, will break, don't use this yet
     */
    @Deprecated
    public static ProgressBar push(String title, int steps)
    {
        ProgressBar bar = new ProgressBar(title, steps);
        bars.add(bar);
        FMLCommonHandler.instance().processWindowMessages();
        return bar;
    }

    /**
     * @deprecated not a stable API, will break, don't use this yet
     */
    @Deprecated
    public static void pop(ProgressBar bar)
    {
        if(bar.getSteps() != bar.getStep()) throw new IllegalStateException("can't pop unfinished ProgressBar " + bar.getTitle());
        bars.remove(bar);
        FMLCommonHandler.instance().processWindowMessages();
    }

    /*
     * Internal use only.
     */
    public static Iterator<ProgressBar> barIterator()
    {
        return bars.iterator();
    }

    /**
     * @deprecated not a stable API, will break, don't use this yet
     */
    @Deprecated
    public static class ProgressBar
    {
        private final String title;
        private final int steps;
        private volatile int step = 0;
        private volatile String message = "";

        private ProgressBar(String title, int steps)
        {
            this.title = title;
            this.steps = steps;
        }

        public void step(String message)
        {
            if(step >= steps) throw new IllegalStateException("too much steps for ProgressBar " + title);
            step++;
            this.message = message;
            FMLCommonHandler.instance().processWindowMessages();
        }

        public String getTitle()
        {
            return title;
        }

        public int getSteps()
        {
            return steps;
        }

        public int getStep()
        {
            return step;
        }

        public String getMessage()
        {
            return message;
        }
    }
}
