package net.minecraftforge.test;

import com.mojang.datafixers.util.Unit;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.TextTable;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SlowLazyOptionalTest
{
    @Test
    public void test() throws InterruptedException {
        LazyOptional<Unit> slowLazy = LazyOptional.of(() -> {
            try {
                Thread.sleep(1000);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
            return Unit.INSTANCE;
        });
        List<Thread> threads = new ArrayList<>();
        AtomicInteger successfulThreads = new AtomicInteger();
        for (int i = 0; i < 2; ++i) {
            threads.add(new Thread(() -> {
                // Resolve uses getValueUnsafe, so it throws if the value is unexpectedly absent
                if (slowLazy.resolve().isPresent()) {
                    successfulThreads.incrementAndGet();
                }
            }));
        }
        threads.forEach(Thread::start);
        for (Thread thread : threads) {
            thread.join();
        }
        assertEquals(threads.size(), successfulThreads.get());
    }
}
