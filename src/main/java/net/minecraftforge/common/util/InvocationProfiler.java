package net.minecraftforge.common.util;

import org.apache.commons.lang3.time.StopWatch;

import java.util.concurrent.TimeUnit;

public class InvocationProfiler
{

    private final StopWatch watch = new StopWatch();
    private final String name;
    private int invocations = 0;
    private long totalTime = 0;

    public InvocationProfiler(String name)
    {
        this.name = name;
    }

    public void enter()
    {
        watch.start();
    }

    public void leave()
    {
        watch.stop();
        long localTime = watch.getTime(TimeUnit.MICROSECONDS);
        watch.reset();
        invocations++;
        totalTime += localTime;
        if (invocations % 100 == 0)
        {
            System.out.printf("%s: Total %s invocations %s mean %s (ms)\n", name, totalTime / 1000, invocations, ((totalTime / 1000F) / invocations));
        }
    }
}
