/*
 * Minecraft Forge
 * Copyright (c) 2018.
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

package net.minecraftforge.fml;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class DeferredWorkQueue
{
    public static ConcurrentLinkedDeque<FutureWorkTask<?>> deferredWorkQueue = new ConcurrentLinkedDeque<>();

    public static <T> Future<T> enqueueWork(Callable<T> workToEnqueue) {
        final FutureWorkTask<T> workTask = new FutureWorkTask<>(workToEnqueue);
        DeferredWorkQueue.deferredWorkQueue.add(workTask);
        return workTask;
    }

    public static class FutureWorkTask<T> extends FutureTask<T>
    {
        FutureWorkTask(Callable<T> callable)
        {
            super(callable);
        }
    }
}
