/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.async;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.task.ForgeTaskFactories;
import net.minecraftforge.task.IAsyncTaskConfigurator;
import org.spongepowered.asm.mixin.Dynamic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ForgeAsyncEventFactories {

    public static void doFireWorldRegistriesLoadedEvent(RegistryAccess registryAccess,
                                                        DynamicOps<JsonElement> registryOps,
                                                        ResourceManager resourceManager,
                                                        Executor executor,
                                                        Consumer<BooleanSupplier> managedBlocker)
    {
        fireOnForgeBus(
                configuratorSupplier -> new OnWorldRegistriesLoadedEvent(configuratorSupplier, registryAccess, registryOps, resourceManager),
                executor,
                managedBlocker
        );
    }

    private static <T extends AbstractAsyncEvent> boolean fireOnForgeBus(
            final Function<Supplier<IAsyncTaskConfigurator>, T> eventBuilder,
            final Executor executor,
            final Consumer<BooleanSupplier> managedBlocker)
    {
        final List<IAsyncTaskConfigurator> configuratorList = Collections.synchronizedList(new ArrayList<>());
        final ForgeTaskFactories.BarrierContext barrierContext = ForgeTaskFactories.barrierFor(executor);
        final T event = eventBuilder.apply(() -> {
            final IAsyncTaskConfigurator configurator = ForgeTaskFactories.simpleBarrierAware(barrierContext);
            configuratorList.add(configurator);
            return configurator;
        });

        final boolean cancelled = MinecraftForge.EVENT_BUS.post(event);

        if (cancelled)
            return true;

        final CompletableFuture<?>[] allTasks = configuratorList.stream().map(ForgeTaskFactories::build)
                        .toArray(CompletableFuture[]::new);
        final CompletableFuture<Void> completionTask = CompletableFuture.allOf(allTasks);

        barrierContext.start();
        managedBlocker.accept(completionTask::isDone);

        return false;
    }
}
