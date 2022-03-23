package net.minecraftforge.debug;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.async.OnWorldRegistriesLoadedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 * Declares a test for the {@link OnWorldRegistriesLoadedEvent} event.
 * In particular this test verifies its ability to post process task in an async fashion.
 *
 * During world load (or creation) the following four lines need to appear in the log:
 * - Async world registry loaded event without result processing, first task!
 * - Async world registry loaded event with result processing!
 * - Async world registry loaded event without result processing, second task!
 * - Sync world registry loaded event with result processing, result: From Async
 *
 * Important here is that the order of the first three lines does not matter, but that the sync line always happens last.
 */
@Mod(OnWorldRegistriesLoadedEventTest.MODID)
public class OnWorldRegistriesLoadedEventTest
{
    public static final String MODID = "world_registries_loaded_event";
    public static final boolean ENABLED = true;

    private static final Logger LOGGER = LogUtils.getLogger();

    public OnWorldRegistriesLoadedEventTest() {
        if (!ENABLED)
            return;

        MinecraftForge.EVENT_BUS.addListener(this::onWorldRegistriesLoaded);
    }

    private void onWorldRegistriesLoaded(final OnWorldRegistriesLoadedEvent worldRegistriesLoadedEvent)
    {
        worldRegistriesLoadedEvent.withAsyncTask(asyncConfigurator -> asyncConfigurator.async(() -> LOGGER.warn("Async world registry loaded event without result processing, first task!")));
        worldRegistriesLoadedEvent.withAsyncTask(asyncConfigurator -> asyncConfigurator.async(() -> {
            LOGGER.warn("Async world registry loaded event with result processing!");
            return "From Async";
        })
        .sync(result -> LOGGER.warn("Sync world registry loaded event with result processing, result: {}", result)));
        worldRegistriesLoadedEvent.withAsyncTask(asyncConfigurator -> asyncConfigurator.async(() -> LOGGER.warn("Async world registry loaded event without result processing, second task!")));
    }
}
