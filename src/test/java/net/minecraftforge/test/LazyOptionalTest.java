/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.test;

import com.mojang.datafixers.util.Unit;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LazyOptionalTest
{
    @Test
    public void testConcurrentResolve() throws InterruptedException {
        AtomicInteger supplierCalls = new AtomicInteger();
        LazyOptional<Unit> slowLazy = LazyOptional.of(() -> {
            supplierCalls.incrementAndGet();
            try
            {
                Thread.sleep(1000);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
            return Unit.INSTANCE;
        });
        List<Thread> threads = new ArrayList<>();
        AtomicInteger successfulThreads = new AtomicInteger();
        for (int i = 0; i < 2; ++i)
        {
            threads.add(new Thread(() -> {
                // Resolve uses getValueUnsafe, so it throws if the value is unexpectedly absent
                if (slowLazy.resolve().isPresent()) {
                    successfulThreads.incrementAndGet();
                }
            }));
        }
        threads.forEach(Thread::start);
        for (Thread thread : threads)
        {
            thread.join();
        }
        assertEquals(threads.size(), successfulThreads.get());
        assertEquals(1, supplierCalls.get());
    }

    @Test
    public void testInvalidSupplier() {
        MutableInt supplierCalls = new MutableInt();
        LazyOptional<Unit> badLazy = LazyOptional.of(() -> {
            supplierCalls.increment();
            return null;
        });
        badLazy.ifPresent(u -> {});
        badLazy.ifPresent(u -> {});
        assertEquals(1, supplierCalls.intValue());
    }
}
