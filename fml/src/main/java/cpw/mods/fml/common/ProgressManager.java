package cpw.mods.fml.common;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.common.base.Joiner;

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
        return push(title, steps, false);
    }
    /**
     * @deprecated not a stable API, will break, don't use this yet
     */
    @Deprecated
    public static ProgressBar push(String title, int steps, boolean timeEachStep)
    {
        ProgressBar bar = new ProgressBar(title, steps);
        bars.add(bar);
        if (timeEachStep)
        {
            bar.timeEachStep();
        }
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
        if (bar.getSteps() != 0)
        {
            long newTime = System.nanoTime();
            if (bar.timeEachStep)
                FMLLog.fine("Bar Step: %s - %s took %.3fs", bar.getTitle(), bar.getMessage(), ((float)(newTime - bar.lastTime) / 1000000 / 1000));
            if (bar.getSteps() == 1)
                FMLLog.fine("Bar Finished: %s - %s took %.3fs", bar.getTitle(), bar.getMessage(), ((float)(newTime - bar.startTime) / 1000000 / 1000));
            else
                FMLLog.fine("Bar Finished: %s took %.3fs", bar.getTitle(), ((float)(newTime - bar.startTime) / 1000000 / 1000));
        }
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
        private boolean timeEachStep = false;
        private long startTime = System.nanoTime();
        private long lastTime = startTime;

        private ProgressBar(String title, int steps)
        {
            this.title = title;
            this.steps = steps;
        }

        public void step(Class<?> classToName, String... extra)
        {
            step(ClassNameUtils.shortName(classToName)+Joiner.on(' ').join(extra));
        }

        public void step(String message)
        {
            if(step >= steps) throw new IllegalStateException("too much steps for ProgressBar " + title);
            if (timeEachStep && step != 0)
            {
                long newTime = System.nanoTime();
                FMLLog.fine("Bar Step: %s - %s took %.3fs", getTitle(), getMessage(), ((float)(newTime - lastTime) / 1000000 / 1000));
                lastTime = newTime;
            }
            step++;
            this.message = FMLCommonHandler.instance().stripSpecialChars(message);
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

        public void timeEachStep()
        {
            this.timeEachStep = true;
        }
    }
}