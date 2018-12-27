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

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Not a fully fleshed out API, may change in future MC versions.
 * However feel free to use and suggest additions.
 */
public class ProgressManager
{
    private static final List<ProgressBar> bars = new CopyOnWriteArrayList<>();

    /**
     * Not a fully fleshed out API, may change in future MC versions.
     * However feel free to use and suggest additions.
     */
    public static void run(String title, int steps, Consumer<ProgressBar> task)
    {
        run(title, steps, false, task);
    }

    /**
     * Not a fully fleshed out API, may change in future MC versions.
     * However feel free to use and suggest additions.
     */
    public static void run(String title, int steps, boolean timeEachStep, Consumer<ProgressBar> task)
    {
        ProgressBar bar = push(title, steps, timeEachStep);
        task.accept(bar);
        pop(bar);
    }

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
        ProgressBar bar = new ProgressBar(title, steps, timeEachStep);
        bars.add(bar);
//        DistExecutor.runWhenOn(Dist.CLIENT, ()->SplashProgress::processMessages);
        return bar;
    }

    /**
     * Not a fully fleshed out API, may change in future MC versions.
     * However feel free to use and suggest additions.
     */
    public static void pop(ProgressBar bar)
    {
        bars.remove(bar);
        bar.finish();
//        DistExecutor.runWhenOn(Dist.CLIENT, ()->SplashProgress::processMessages);
    }

    /*
     * Internal use only.
     */
    public static Iterator<ProgressBar> barIterator()
    {
        return bars.iterator();
    }

}
