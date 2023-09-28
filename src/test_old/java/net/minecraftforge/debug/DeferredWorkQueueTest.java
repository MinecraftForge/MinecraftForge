/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug;

import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

/**
 * Tests that the {@link net.minecraftforge.fml.DeferredWorkQueue} properly executes enqueued tasks and
 * forwards any exceptions thrown by those tasks
 */
@Mod(DeferredWorkQueueTest.MOD_ID)
@Mod.EventBusSubscriber(modid = DeferredWorkQueueTest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DeferredWorkQueueTest
{
    public static final String MOD_ID = "deferred_work_queue_test";
    private static final boolean ENABLE = false;
    private static final Logger LOGGER = LogUtils.getLogger();

    public DeferredWorkQueueTest() { }

    @SubscribeEvent
    public static void onCommonSetup(final FMLCommonSetupEvent event)
    {
        if (!ENABLE)
        {
            return;
        }

        event.enqueueWork(() -> LOGGER.info("Enqueued runnable ran"));
        event.enqueueWork(() -> "Enqueued supplier ran").thenAccept(LOGGER::info);

        event.enqueueWork(DeferredWorkQueueTest::executeThrowingRunnable);
        event.enqueueWork(DeferredWorkQueueTest::executeThrowingSupplier).thenAccept(s ->
                LOGGER.error("This must not be printed")
        );
    }

    private static void executeThrowingRunnable()
    {
        LOGGER.info("Enqueued throwing runnable ran");
        throw new IllegalStateException("Throwing runnable ran");
    }

    private static String executeThrowingSupplier()
    {
        LOGGER.info("Enqueued throwing supplier ran");
        throw new IllegalStateException("Throwing supplier ran");
    }
}
