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

package net.minecraftforge.fml.common.progress;

import com.google.common.base.Stopwatch;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.client.SplashProgress;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.MessageFormatMessage;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static net.minecraftforge.fml.Logging.SPLASH;

class StartupProgressBarTracker implements IProgressBarTracker
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final List<ProgressBar> bars = new CopyOnWriteArrayList<>();

    private final boolean timeEachStep;
    private final Stopwatch stopwatch;

    StartupProgressBarTracker(boolean timeEachStep)
    {
        this.timeEachStep = timeEachStep;
        this.stopwatch = Stopwatch.createUnstarted();
    }

    @Override
    public void onBarStart(ProgressBar bar)
    {
        bars.add(bar);
        DistExecutor.runWhenOn(Dist.CLIENT, ()-> SplashProgress::processMessages);
        LOGGER.debug(SPLASH, () -> new MessageFormatMessage("Bar Starting: {0}", bar.getTitle()));
        stopwatch.start();
    }

    @Override
    public void onStepStarted(ProgressBar bar, int step, String message)
    {
        if (timeEachStep)
        {
            LOGGER.debug(SPLASH, () -> new MessageFormatMessage("Bar Step: {0} - {1} starting", bar.getTitle(), message));
            stopwatch.reset();
            stopwatch.start();
        }
    }

    @Override
    public void onStepFinished(ProgressBar bar, int step, String message)
    {
        if (timeEachStep)
        {
            stopwatch.stop();
            LOGGER.debug(SPLASH, () -> new MessageFormatMessage("Bar Step: {0} - {1} took {2}", bar.getTitle(), message, stopwatch));
        }
    }

    @Override
    public void onBarFinished(ProgressBar bar, int step, String message)
    {
        stopwatch.stop();
        bars.remove(bar);
        if (timeEachStep)
        {
            LOGGER.debug(SPLASH, () -> new MessageFormatMessage("Bar Finished: {0}", bar.getTitle()));
        }
        else
        {
            LOGGER.debug(SPLASH, () -> new MessageFormatMessage("Bar Finished: {0} took {1}", bar.getTitle(), stopwatch));
        }
        DistExecutor.runWhenOn(Dist.CLIENT, ()-> SplashProgress::processMessages);
    }

    /**
     * Internal use only.
     */
    public static Iterator<ProgressBar> barIterator()
    {
        return bars.iterator();
    }
}
