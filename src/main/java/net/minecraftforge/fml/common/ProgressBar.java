package net.minecraftforge.fml.common;

import com.google.common.base.Stopwatch;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.SidedProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.MessageFormatMessage;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static net.minecraftforge.fml.Logging.SPLASH;

/**
 * Not a fully fleshed out API, may change in future MC versions.
 * However feel free to use and suggest additions.
 */
public class ProgressBar
{
    private static final Logger LOGGER = LogManager.getLogger();

    private final String title;
    private final int totalSteps;
    private AtomicInteger step = new AtomicInteger(0);
    private volatile String message = "";
    private final boolean timeEachStep;
    private final Function<String, String> stripChars;
    private final Stopwatch stopwatch;

    ProgressBar(String title, int totalSteps, boolean timeEachStep)
    {
        this.title = title;
        this.totalSteps = totalSteps;
        this.timeEachStep = timeEachStep;
        this.stripChars = SidedProvider.STRIPCHARS.get();
        this.stopwatch = Stopwatch.createStarted();
    }

    public void step(Class<?> classToName, String... extra)
    {
        step(ClassNameUtils.shortName(classToName) + String.join(" ", extra));
    }

    public void step(String newMessage)
    {
        int stepValue = step.incrementAndGet();
        if (timeEachStep && stepValue > 1)
        {
            LOGGER.debug(SPLASH, () -> new MessageFormatMessage("Bar Step: {0} - {1} took {2}", title, message, stopwatch));
            stopwatch.reset();
            stopwatch.start();
        }

        if (stepValue > totalSteps) {
            LOGGER.error(SPLASH, "ProgressBar stepped past the total: {0}.\nSteps: {1}/{2}", title, stepValue, totalSteps);
        }
        this.message = stripChars.apply(newMessage);
    }

    /**
     * Get the current step.
     * Called from the render thread.
     */
    public int getStep()
    {
        return step.get();
    }

    /**
     * Get the total number of steps.
     * Called from the render thread.
     */
    public int getTotalSteps()
    {
        return totalSteps;
    }

    /**
     * Get the current title.
     * Called from the render thread.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Get the current message.
     * Called from the render thread.
     */
    public String getMessage()
    {
        return message;
    }

    void finish()
    {
        stopwatch.stop();

        if (totalSteps == 0)
        {
            return;
        }

        int stepValue = step.get();
        if (totalSteps > stepValue)
        {
            LOGGER.error(SPLASH, "ProgressBar didn't finish: {0}.\nCompleted Steps: {1}/{2}" + title, stepValue, totalSteps);
            return;
        }

        if (timeEachStep)
        {
            LOGGER.debug(SPLASH, () -> new MessageFormatMessage("Bar Step: {0} - {1} took {2}", title, message, stopwatch));
        }

        if (totalSteps == 1)
        {
            LOGGER.debug(SPLASH, () -> new MessageFormatMessage("Bar Finished: {0} - {1} took {2}", title, message, stopwatch));
        }
        else
        {
            LOGGER.debug(SPLASH, () -> new MessageFormatMessage("Bar Finished: {0} took {1}", title, stopwatch));
        }
    }
}
