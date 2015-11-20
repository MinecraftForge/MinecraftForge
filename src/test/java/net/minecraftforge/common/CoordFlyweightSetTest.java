package net.minecraftforge.common;

import java.io.IOException;
import java.util.Random;
import java.util.Set;

import net.minecraft.world.ChunkCoordIntPair;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.google.common.collect.Sets;

import scala.actors.threadpool.Arrays;

// HOWTO: run with -Xms -Xmx equal and what the budget of world chunk caching should be (+16M for JVM)
// spoiler: anything less than 2GB will choke the default impl, 32M will OOM
public class CoordFlyweightSetTest {
    @Test
    @Ignore("this should be replaced by a Caliper benchmark in the integration test suite")
    public void testTrivialMicrobenchmark() throws InterruptedException, IOException
    {
        // HOWTO: try data_size * iterations = 1000M for best results

        // data size roughly describes number of possible active chunks
        int DATA_SIZE = 1000000;
        int WARMUP_SIZE = Math.max(100, (int) Math.sqrt(DATA_SIZE));

        // set is cleared after each iteration, increase iterations to show more
        // contention, similar to world tick
        int ITERATIONS = 100;

        Random rand = new Random();
        Runtime javart = Runtime.getRuntime();

        // HOWTO: uncomment this pause if you want to profile
        // System.out.println("press key to begin!");
        // System.in.read();

        for (int size : new int[] { WARMUP_SIZE, DATA_SIZE })
        {
            int[] data = new int[size];

            long totalMemory = javart.totalMemory();
            long usedMemory = totalMemory - javart.freeMemory();
            long temp, time = 0;

            // fill
            for (int i = 0; i < size; i++)
            {
                data[i] = rand.nextInt();
            }

            System.out.println("Test size=" + size + " iterations=" + ITERATIONS + " startMx=" + totalMemory + " startHeap=" + usedMemory);

            // less favorable timing setup for the new implementation to go
            // first, and shows us memory gains
            for (Set set : new Set[] { ForgeChunkManager.packedChunkCoordSet(), Sets.newHashSet() })
            {
                time = System.nanoTime();

                // main benchmark - iterate over data pushing it into the set
                for (int pass = ITERATIONS - 1; pass >= 0; pass--)
                {
                    for (int position = size - 2; position >= 0; position -= 2)
                    {
                        // HOWTO: try case 1, case 2 in separate runs
                        // case 1 use reflection and extract raw coords to
                        // packed coord set, ~3x speedup with 4GB ram
                        // with 256M ~4x speedup
                        ForgeChunkManager.addActiveChunk(set, data[position], data[position + 1]);

                        // case 2 allocate temp object, extract coords and shove
                        // it into TLongHashSet, ~2x speedup with 4GB ram
                        // with 256M ~3x speedup
                        // set.add(new ChunkCoordIntPair(data[position],
                        // data[position+1]));
                    }

                    // take worst case memory statistics
                    temp = javart.totalMemory();
                    totalMemory = Math.max(totalMemory, temp);
                    usedMemory = Math.max(usedMemory, temp - javart.freeMemory());

                    set.clear(); // possibly simulates world tick behavior
                }

                time = System.nanoTime() - time;

                System.out.println(set.getClass() + "\tt=" + time + "\tMx=" + totalMemory + "\theap=" + usedMemory);

                set = null;

                // personal voodoo, timing of sleep/GC/yield to release memory
                // is not consistent across platforms
                Thread.currentThread().sleep(50);
                System.gc();
                Thread.yield();
                System.gc();
                Thread.yield();

                // should be in a clean state for the next test candidate
            }
        }
    }
}
