/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.datafix;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.logging.LogUtils;
import net.minecraft.Util;
import net.minecraft.util.datafix.DataFixers;
import net.minecraftforge.common.ForgeSerialModStateTransition;
import net.minecraftforge.event.datafix.RegisterFixesEvent;
import net.minecraftforge.fml.IModStateTransition;
import net.minecraftforge.fml.ModContainer;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Event handler utility class for managing the events related to custom DFU fixers.
 * This particular class is used to handle firing of the parallel mod loading events
 * for the DFU Fixer setup.
 *
 * These events are fired immediately after mod construction and before the rest of the game
 * loads, even before registries are fired.
 *
 * THIS CLASS IS FOR INTERNAL USE ONLY!
 */
public class ForgeDataFixerCustomFixersModStateTransition
{
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Builds a new mod state transition which can be used to fire the appropriate fixer registration events on the mod bus.
     *
     * @return The mod state transition used to add DFU fixers.
     */
    public static IModStateTransition get() {
        return new ForgeSerialModStateTransition<>(
          ForgeDataFixerCustomFixersModStateTransition::generateFixersEvents,
          ForgeDataFixerCustomFixersModStateTransition::preDispatchFixerEvents,
          ForgeDataFixerCustomFixersModStateTransition::postDispatchFixerEvents,
          ForgeDataFixerCustomFixersModStateTransition::rebuildRulesAfterSchemaEvents
        );
    }

    /**
     * Gives access to the list of schemas registered with the data fixers.
     * If we are currently running an incompatible version of DFU or somebody messed with it,
     * then an empty list is returned.
     *
     * @return The list of all known schemas to handle.
     */
    private static List<ForgeSchema> getAllSchemas()
    {
        //Check if we are running in a compatible environment.
        if (DataFixers.getDataFixer() instanceof ForgeDataFixerDelegateHandler forgeDataFixerDelegateHandler) {
            //Forge DFU wrapper is active, grab its configured schemas.
            return forgeDataFixerDelegateHandler.getAllSchemas();
        }

        //Incompatible environment, return an empty list.
        return Collections.emptyList();
    }

    /**
     * Builder function for creating the events that are fired on start up.
     * This returns one generator per schema, and keeps the schema in the order defined.
     *
     * The returned generator creates one event internally per schema and mod container, and then stores them for reuse.
     *
     * @return The stream of event generators used to fire one event per mod per schema.
     */
    private static Stream<IModStateTransition.EventGenerator<?>> generateFixersEvents()
    {
        //Grab the schemas
        List<ForgeSchema> schemas = Lists.newArrayList(getAllSchemas());
        //Sort them based on their version key.
        schemas.sort(Comparator.comparingInt(Schema::getVersionKey));

        //Create one generator per schema.
        return schemas.stream().map(InternalEventGenerator::new);
    }

    /**
     * Invoked by the event management system to indicate that we are about the fire an event for the given generator to all mods.
     * This is invoked exactly once, and only once, per generator (and as such per event, and schema).
     *
     * Clears out the current status of the schema by resetting its type definition maps.
     *
     * @param executor The executor to execute this task on.
     * @param eventGenerator The event generator that is about to be executed.
     * @return A future that indicates success or failure.
     */
    private static CompletableFuture<List<Throwable>> preDispatchFixerEvents(
      final Executor executor, final IModStateTransition.EventGenerator<RegisterFixesEvent> eventGenerator
    )
    {
        if (!(eventGenerator instanceof InternalEventGenerator internalEventGenerator)) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        return CompletableFuture.runAsync(() -> {
            //Notify of the update that is about to start.
            LOGGER.debug("Resetting and collecting custom fixes in DFU for: {}", internalEventGenerator.schema.getVersionKey());

            //And reset the stored fixes.
            internalEventGenerator.schema.resetCustomFixes();
        }, executor).thenApply(v -> Collections.emptyList());
    }

    /**
     * Invoked by the event management system to indicate that it has fired all the events for the given schema.
     * This allows us to collect the data from the events and trigger the given schema to rebuild its type map.
     *
     * @param executor The executor to execute the task on.
     * @param eventGenerator The event generator for the event (and thus the schema in question).
     * @return The future that indicates success or failure.
     */
    private static CompletableFuture<List<Throwable>> postDispatchFixerEvents(
      final Executor executor, final IModStateTransition.EventGenerator<RegisterFixesEvent> eventGenerator
    ) {
        if (!(eventGenerator instanceof InternalEventGenerator internalEventGenerator)) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        return CompletableFuture.runAsync(() -> {
            LOGGER.debug("Collected and registered custom fixes for: {}", internalEventGenerator.schema.getVersionKey());

            final List<Function<Schema, DataFix>> fixerFactories = internalEventGenerator.eventPerContainerMap.values()
              .stream()
              .flatMap(RegisterFixesEvent::getFixerFactories)
              .toList();

            internalEventGenerator.schema.registerCustomFixes(
              fixerFactories
            );
        }, executor).handle((v, t) -> t != null ? Collections.singletonList(t) : Collections.emptyList());
    }

    /**
     * Invoked by the event management system to indicate that it completely fired all events from the event stream.
     * This indicates that all the events for all the schemas have been fired and post processed.
     * As such we can now safely reconfigure the DFU wrapper so that the new schema configurations are picked
     * up by the system.
     *
     * @param executor The executor to execute the task on.
     * @param listCompletableFuture The list of previous failures.
     * @return The future which indicates success or failure.
     */
    private static CompletableFuture<List<Throwable>> rebuildRulesAfterSchemaEvents(
      final Executor executor, final CompletableFuture<List<Throwable>> listCompletableFuture
    ) {
        return listCompletableFuture.whenCompleteAsync((errors, except) -> {
            //Check for errors.
            if (except != null)
            {
                //Ouch that failed, log it and escape.
                LOGGER.error("Detected errors during dfu setup. DFU might not be available.", except);
            }
            else
            {
                //Success, check for a compatible DFU instance and then rebuild it.
                if (DataFixers.getDataFixer() instanceof ForgeDataFixerDelegateHandler forgeDataFixerDelegateHandler)
                {
                    //Do not do this on the mod executor!
                    //This will crash the game.
                    //Rebuild the DFU wrapper on the bootstrap executor
                    forgeDataFixerDelegateHandler.rebuildFixer(Util.bootstrapExecutor(), true);
                }
            }
        }, executor);
    }

    private static class InternalEventGenerator implements IModStateTransition.EventGenerator<RegisterFixesEvent> {

        private static final Logger LOGGER = LogUtils.getLogger();

        private final ForgeSchema                           schema;
        private final Map<ModContainer, RegisterFixesEvent> eventPerContainerMap = Maps.newConcurrentMap();

        private InternalEventGenerator(final ForgeSchema schema) {this.schema = schema;}

        @Override
        public RegisterFixesEvent apply(final ModContainer modContainer) {
            return eventPerContainerMap.computeIfAbsent(modContainer, this::createEvent);
        }

        private RegisterFixesEvent createEvent(final ModContainer modContainer) {
            LOGGER.debug("Creating register fixes event for mod: {} and schema: {}", modContainer.getModId(), schema.getVersionKey());

            return new RegisterFixesEvent(schema, modContainer);
        }
    }
}
