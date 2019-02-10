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

import net.minecraftforge.fml.SidedProvider;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;

public class StartupProgressManager
{
    private static final Function<String, String> stripChars = SidedProvider.STRIPCHARS.get();

    public static void start(String title, int steps, Consumer<ProgressBar> task)
    {
        start(title, steps, false, task);
    }

    public static void start(String title, int steps, boolean timeEachStep, Consumer<ProgressBar> task)
    {
        try (ProgressBar bar = start(title, steps, timeEachStep))
        {
            task.accept(bar);
        }
    }

    public static ProgressBar start(String title, int steps)
    {
        return start(title, steps, false);
    }

    public static ProgressBar start(String title, int steps, boolean timeEachStep)
    {
        StartupProgressBarTracker tracker = new StartupProgressBarTracker(timeEachStep, steps);
        return new ProgressBar(title, steps, stripChars, tracker);
    }

    /**
     * Internal use only.
     */
    public static Iterator<ProgressBar> barIterator()
    {
        return StartupProgressBarTracker.barIterator();
    }
}
