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

package net.minecraftforge.test;

import net.minecraftforge.fml.common.progress.IProgressBarTracker;
import net.minecraftforge.fml.common.progress.ProgressBar;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class TestProgressBar
{
    private static String getStepMessage(int step)
    {
        if (step == 0)
        {
            return "";
        }
        return "testStep" + step;
    }

    private static void checkBarState(int step, String message, int expectedStepValue, int totalSteps)
    {
        if (step != expectedStepValue)
        {
            throw new IllegalStateException("Got step " + step + " but expected " + expectedStepValue);
        }
        if (step > totalSteps)
        {
            throw new IllegalStateException("ProgressBar stepped past the total: " + step + "/" + totalSteps);
        }
        String expectedMessage = getStepMessage(expectedStepValue);
        if (!message.equals(expectedMessage))
        {
            throw new IllegalStateException("Got message '" + message + "' but expected '" + expectedMessage + "'");
        }
    }

    @Test
    public void testProgressBar()
    {
        String title = "testTitle";
        final int totalSteps = 20;

        IProgressBarTracker tracker = new TestProgressBarTracker(totalSteps, title);

        try (ProgressBar progressBar = new ProgressBar(title, totalSteps, s -> s, tracker))
        {
            String barTitle = progressBar.getTitle();
            if (!title.equals(barTitle))
            {
                throw new IllegalStateException("Got title " + barTitle + " but expected " + title);
            }
            for (int i = 1; i <= totalSteps; i++) {
                progressBar.step(getStepMessage(i));
            }
        }
    }

    @Test
    public void testThreadedProgressBar()
    {
        String title = "testTitle";
        final int totalSteps = 10;

        TestProgressBarTracker tracker = new TestProgressBarTracker(totalSteps, title);

        AtomicBoolean threadSawBar = new AtomicBoolean(false);
        Thread thread = new Thread(() ->
        {
            List<ProgressBar> bars = TestProgressBarTracker.getBars();
            while (bars.isEmpty())
            {
                Thread.yield();
                bars = TestProgressBarTracker.getBars();
            }
            while (!bars.isEmpty())
            {
                for (ProgressBar bar : bars)
                {
                    Pair<Integer, String> stepAndMessage = bar.getStepAndMessage();
                    int step = stepAndMessage.getLeft();
                    String message = stepAndMessage.getRight();
                    checkBarState(step, message, step, totalSteps);
                    threadSawBar.set(true);
                }
                Thread.yield();
                bars = TestProgressBarTracker.getBars();
            }
        });
        thread.start();

        try (ProgressBar progressBar = new ProgressBar(title, totalSteps, s -> s, tracker))
        {
            String barTitle = progressBar.getTitle();
            if (!title.equals(barTitle))
            {
                throw new IllegalStateException("Got title " + barTitle + " but expected " + title);
            }
            for (int i = 1; i <= totalSteps; i++) {
                progressBar.step(getStepMessage(i));
                try
                {
                    Thread.sleep(1);
                }
                catch (InterruptedException ignored)
                {

                }
            }
        }

        if (!threadSawBar.get())
        {
            throw new IllegalStateException("Thread never saw any progress bars");
        }
    }

    private static class TestProgressBarTracker implements IProgressBarTracker
    {
        private static final List<ProgressBar> bars = new CopyOnWriteArrayList<>();
        private final AtomicInteger stepped = new AtomicInteger(0);
        private final int totalSteps;
        private final String title;

        public TestProgressBarTracker(int totalSteps, String title)
        {
            this.totalSteps = totalSteps;
            this.title = title;
        }

        @Override
        public void onBarCreated(ProgressBar bar)
        {
            bars.add(bar);
            int expectedStepCount = 0;
            Pair<Integer, String> stepAndMessage = bar.getStepAndMessage();
            Integer step = stepAndMessage.getLeft();
            String message = stepAndMessage.getRight();
            checkBarState(step, message, expectedStepCount, totalSteps);
        }

        @Override
        public void onStepStarted(ProgressBar bar, int step, String message)
        {
            int expectedStepCount = stepped.incrementAndGet();
            checkBarState(step, message, expectedStepCount, totalSteps);
        }

        @Override
        public void onStepFinished(ProgressBar bar, int step, String message)
        {
            int expectedStepCount = stepped.get();
            checkBarState(step, message, expectedStepCount, totalSteps);
        }

        @Override
        public void onBarFinished(ProgressBar bar, int step, String message)
        {
            bars.remove(bar);
            int expectedStepCount = stepped.get();
            checkBarState(step, message, expectedStepCount, totalSteps);
            if (step != totalSteps)
            {
                throw new IllegalStateException("ProgressBar did not finish: " + title + ".\nSteps: " + step + "/" + totalSteps);
            }
        }

        public static List<ProgressBar> getBars()
        {
            return bars;
        }
    }
}
