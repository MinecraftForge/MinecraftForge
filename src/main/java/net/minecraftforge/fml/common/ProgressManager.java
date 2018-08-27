/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.fml.common;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.minecraftforge.fml.SidedProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.MessageFormatMessage;

import static net.minecraftforge.fml.Logging.SPLASH;

/**
 * Not a fully fleshed out API, may change in future MC versions.
 * However feel free to use and suggest additions.
 */
public class ProgressManager
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final List<ProgressBar> bars = new CopyOnWriteArrayList<ProgressBar>();

    /**
     * Not a fully fleshed out API, may change in future MC versions.
     * However feel free to use and suggest additions.
     */
    public static ProgressBar push(String title, int steps)
    {
        return push(title, steps, false);
    }
    /**
     * Not a fully fleshed out API, may change in future MC versions.
     * However feel free to use and suggest additions.
     */
    public static ProgressBar push(String title, int steps, boolean timeEachStep)
    {
        ProgressBar bar = new ProgressBar(title, steps);
        bars.add(bar);
        if (timeEachStep)
        {
            bar.timeEachStep();
        }
//        DistExecutor.runWhenOn(Dist.CLIENT, ()->SplashProgress::processMessages);
        return bar;
    }

    /**
     * Not a fully fleshed out API, may change in future MC versions.
     * However feel free to use and suggest additions.
     */
    public static void pop(ProgressBar bar)
    {
        if(bar.getSteps() != bar.getStep()) throw new IllegalStateException("can't pop unfinished ProgressBar " + bar.getTitle());
        bars.remove(bar);
        if (bar.getSteps() != 0)
        {
            long newTime = System.nanoTime();
            if (bar.timeEachStep)
                LOGGER.debug(SPLASH, () -> new MessageFormatMessage("Bar Step: {0} - {1} took {2,number,0.000}ms", bar.getTitle(), bar.getMessage(), (newTime - bar.lastTime) / 1.0e6));
            if (bar.getSteps() == 1)
                LOGGER.debug(SPLASH, () -> new MessageFormatMessage("Bar Finished: {0} - {1} took {2,number,0.000}ms", bar.getTitle(), bar.getMessage(), (newTime - bar.lastTime) / 1.0e6));
            else
                LOGGER.debug(SPLASH, () -> new MessageFormatMessage("Bar Finished: {0} took {1,number,0.000}ms", bar.getTitle(), (newTime - bar.lastTime) / 1.0e6));
        }
//        DistExecutor.runWhenOn(Dist.CLIENT, ()->SplashProgress::processMessages);
    }

    /*
     * Internal use only.
     */
    public static Iterator<ProgressBar> barIterator()
    {
        return bars.iterator();
    }


    /**
     * Not a fully fleshed out API, may change in future MC versions.
     * However feel free to use and suggest additions.
     */
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
            step(ClassNameUtils.shortName(classToName)+ String.join(" ", extra));
        }

        public void step(String message)
        {
            if(step >= steps) throw new IllegalStateException("too much steps for ProgressBar " + title);
            if (timeEachStep && step != 0)
            {
                long newTime = System.nanoTime();
                LOGGER.debug(SPLASH,new MessageFormatMessage("Bar Step: {0} - {1} took {2,number,0.000}ms", getTitle(), getMessage(), (newTime - lastTime) / 1.0e6));
                lastTime = newTime;
            }
            step += 1;
            this.message = SidedProvider.STRIPCHARS.<Function<String, String>>get().apply(message);
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