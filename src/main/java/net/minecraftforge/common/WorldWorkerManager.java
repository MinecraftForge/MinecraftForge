/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.List;

public class WorldWorkerManager
{
    private static List<IWorker> workers = new ArrayList<IWorker>();
    private static long startTime = -1;
    private static int index = 0;

    public static void tick(boolean start)
    {
        if (start)
        {
            startTime = System.currentTimeMillis();
            return;
        }

        index = 0;
        IWorker task = getNext();
        if (task == null)
            return;

        long time = 50 - (System.currentTimeMillis() - startTime);
        if (time < 10)
            time = 10; //If ticks are lagging, give us at least 10ms to do something.
        time += System.currentTimeMillis();

        while (System.currentTimeMillis() < time && task != null)
        {
            boolean again = task.doWork();

            if (!task.hasWork())
            {
                remove(task);
                task = getNext();
            }
            else if (!again)
            {
                task = getNext();
            }
        }
    }

    public static synchronized void addWorker(IWorker worker)
    {
        workers.add(worker);
    }

    private static synchronized IWorker getNext()
    {
        return workers.size() > index ? workers.get(index++) : null;
    }

    private static synchronized void remove(IWorker worker)
    {
        workers.remove(worker);
        index--;
    }

    //Internal only, used to clear everything when the server shuts down.
    public static synchronized void clear()
    {
        workers.clear();
    }

    public static interface IWorker
    {
        boolean hasWork();

        /**
         * Perform a task, returning true from this will have the manager call this function again this tick if there is time left.
         * Returning false will skip calling this worker until next tick.
         */
        boolean doWork();
    }
}
