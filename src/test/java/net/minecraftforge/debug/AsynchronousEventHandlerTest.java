package net.minecraftforge.debug;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static net.minecraftforge.fml.common.Mod.EventHandler;

@Mod(
        modid = AsynchronousEventHandlerTest.MOD_ID,
        name = "Asynchronous event handlers test mod",
        version = "1.0",
        acceptableRemoteVersions = "*"
)
public class AsynchronousEventHandlerTest
{

    public static final String MOD_ID = "Asynchronous_event_handlers_test";

    private static void doSomeStuffThatTakesALongTime()
    {
        try
        {
            // simulate workload
            Thread.sleep(100);
        }
        catch (InterruptedException e)
        {
            // we don't need to handle interruptions here
        }
    }

    /**
     * A synchronous event handler to test backwards compatibility
     * @param e unused but necessary for FML to know what kind of handler this is and when to call it
     */
    @EventHandler
    public void synchronousPreInitializationEventHandler(FMLPreInitializationEvent e)
    {
        // This will block the thread for a total of about one second.
        IntStream.range(1, 10)
                .forEachOrdered(i -> AsynchronousEventHandlerTest.doSomeStuffThatTakesALongTime());
    }

    /**
     * An asynchronous event handler to show how the support for them can be used
     * @param e unused but necessary for FML to know what kind of handler this is and when to call it
     * @return A future that will resolve as soon as all work inside this handler is done
     */
    @EventHandler
    public Future<Void> asynchronousPreInitializationEventHandler(FMLPreInitializationEvent e)
    {
        // This won't block the thread and will run in parallel to the other event handler
        CompletableFuture[] futures = IntStream.range(1, 10)
                .boxed()
                .map(i -> CompletableFuture.runAsync(AsynchronousEventHandlerTest::doSomeStuffThatTakesALongTime))
                .collect(Collectors.toList())
                .toArray(new CompletableFuture[10]);
        return CompletableFuture.allOf(futures);
    }

}
