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

import net.minecraftforge.fml.common.ClassNameUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.Function;

/**
 * Not a fully fleshed out API, may change in future MC versions.
 * However feel free to use and suggest additions.
 */
public final class ProgressBar implements AutoCloseable
{
    private final String title;
    private final int totalSteps;
    private final Function<String, String> stripChars;
    private final IProgressBarTracker tracker;
    private volatile int step = 0;
    private volatile String message = "";

    public ProgressBar(String title, int totalSteps, Function<String, String> stripChars, IProgressBarTracker tracker)
    {
        this.title = title;
        this.totalSteps = totalSteps;
        this.stripChars = stripChars;
        this.tracker = tracker;
        this.tracker.onBarCreated(this);
    }

    public void step(Class<?> classToName, String... extra)
    {
        step(ClassNameUtils.shortName(classToName) + String.join(" ", extra));
    }

    public synchronized void step(String newMessage)
    {
        if (step > 0)
        {
            tracker.onStepFinished(this, step, message);
        }
        step++;
        message = stripChars.apply(newMessage);
        tracker.onStepStarted(this, step, message);;
    }

    /**
     * Get the current step and message.
     */
    public synchronized Pair<Integer, String> getStepAndMessage()
    {
        return Pair.of(step, message);
    }

    /**
     * Get the total number of steps.
     */
    public int getTotalSteps()
    {
        return totalSteps;
    }

    /**
     * Get the current title.
     */
    public String getTitle()
    {
        return title;
    }

    @Override
    public synchronized void close()
    {
        if (step > 0)
        {
            tracker.onStepFinished(this, step, message);
        }
        tracker.onBarFinished(this, step, message);
    }
}
