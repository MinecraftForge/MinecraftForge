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

package net.minecraftforge.fml.common.progress;

import com.google.common.base.Stopwatch;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.client.SplashProgress;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.MessageFormatMessage;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static net.minecraftforge.fml.Logging.SPLASH;

class StartupProgressBarTracker implements IProgressBarTracker
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final List<ProgressBar> bars = new CopyOnWriteArrayList<>();

    private final boolean timeEachStep;
    private final int steps;
    private final Stopwatch stopwatch;
    @Nullable
    private Stopwatch stepStopwatch;
    private String logPrefix = "";

    StartupProgressBarTracker(boolean timeEachStep, int steps)
    {
        this.timeEachStep = timeEachStep;
        this.steps = steps;
        this.stopwatch = Stopwatch.createUnstarted();
    }

    @Override
    public void onBarCreated(ProgressBar bar)
    {
        int depth = bars.size();
        logPrefix = StringUtils.repeat("        ", depth);
        bars.add(bar);
        DistExecutor.runWhenOn(Dist.CLIENT, ()-> SplashProgress::processMessages);
    }

    @Override
    public void onStepStarted(ProgressBar bar, int step, String message)
    {
        if (step == 1)
        {
            if (steps > 1)
            {
                LOGGER.debug(SPLASH, () -> new MessageFormatMessage("{0}Bar Starting: {1}", logPrefix, bar.getTitle()));
                if (timeEachStep)
                {
                    stepStopwatch = Stopwatch.createStarted();
                }
            }
            else
            {
                LOGGER.debug(SPLASH, () -> new MessageFormatMessage("{0}Bar Starting: {1} - {2}", logPrefix, bar.getTitle(), message));
            }
            stopwatch.start();
        }
        if (stepStopwatch != null)
        {
            LOGGER.debug(SPLASH, () -> new MessageFormatMessage("{0}    Bar Step: {1} - {2} - starting", logPrefix, bar.getTitle(), message));
            stepStopwatch.reset();
            stepStopwatch.start();
        }
    }

    @Override
    public void onStepFinished(ProgressBar bar, int step, String message)
    {
        if (stepStopwatch != null)
        {
            stepStopwatch.stop();
            LOGGER.debug(SPLASH, () -> new MessageFormatMessage("{0}    Bar Step: {1} - {2} - took {3}", logPrefix, bar.getTitle(), message, stepStopwatch));
        }
    }

    @Override
    public void onBarFinished(ProgressBar bar, int step, String message)
    {
        if (steps > 0)
        {
            stopwatch.stop();
            bars.remove(bar);
            if (steps > 1)
            {
                LOGGER.debug(SPLASH, () -> new MessageFormatMessage("{0}Bar Finished: {1} - took {2}", logPrefix, bar.getTitle(), stopwatch));
            }
            else
            {
                LOGGER.debug(SPLASH, () -> new MessageFormatMessage("{0}Bar Finished: {1} - {2} - took {3}", logPrefix, bar.getTitle(), message, stopwatch));
            }
            DistExecutor.runWhenOn(Dist.CLIENT, () -> SplashProgress::processMessages);
        }
    }

    /**
     * Internal use only.
     */
    public static Iterator<ProgressBar> barIterator()
    {
        return bars.iterator();
    }
}
