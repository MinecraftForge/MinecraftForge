/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.datafix;

import com.google.common.collect.Lists;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.logging.LogUtils;
import net.minecraft.Util;
import net.minecraft.util.datafix.DataFixers;
import net.minecraftforge.common.ForgeSerialModStateTransition;
import net.minecraftforge.event.datafix.ConfigureDataFixSchemaEvent;
import net.minecraftforge.fml.IModStateTransition;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

/**
 * Event handler utility class for managing the events related to data fix schemas.
 * This particular class is used to handle firing of the parallel mod loading events
 * for the DFU schema setup.
 *
 * These events are fired immediately after mod construction and before the rest of the game
 * loads, even before registries are fired.
 *
 * THIS CLASS IS FOR INTERNAL USE ONLY!
 */
public class ForgeDataFixerSchemaConfigurationModStateTransition
{
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Builds a new mod state transition which can be used to fire the appropriate schema configuration events on the mod bus.
     *
     * @return The mod state transition used to configure DFU schemas.
     */
    public static IModStateTransition get()
    {
        return new ForgeSerialModStateTransition<>(
          ForgeDataFixerSchemaConfigurationModStateTransition::generateConfigureSchemaEvents,
          ForgeDataFixerSchemaConfigurationModStateTransition::preDispatchSchemaEvents,
          ForgeDataFixerSchemaConfigurationModStateTransition::postDispatchSchemaEvents,
          ForgeDataFixerSchemaConfigurationModStateTransition::rebuildFixerAfterSchemaEvents
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
        if (DataFixers.getDataFixer() instanceof ForgeDataFixerDelegateHandler forgeDataFixerDelegateHandler)
        {
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
     * The generator will always return the same multi-thread capable event reference regardless
     * of the passed in mod name.
     *
     * @return The stream of event generators used to fire one event per mod per schema.
     */
    private static Stream<IModStateTransition.EventGenerator<?>> generateConfigureSchemaEvents()
    {
        //Grab the schemas
        List<ForgeSchema> schemas = Lists.newArrayList(getAllSchemas());
        //Sort them based on their version key.
        schemas.sort(Comparator.comparingInt(Schema::getVersionKey));

        //Make one event per schema, and return it reference based from the event generator,
        //regardless of which mod the generator is invoked for.
        return schemas.stream()
                 .map(ConfigureDataFixSchemaEvent::new)
                 .map(event -> IModStateTransition.EventGenerator.fromFunction(modContainer -> event));
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
    private static CompletableFuture<List<Throwable>> preDispatchSchemaEvents(
      final Executor executor, final IModStateTransition.EventGenerator<ConfigureDataFixSchemaEvent> eventGenerator
    )
    {
        return CompletableFuture.runAsync(() -> {
            //Grab the event, with a null container (is reference equals anyways for all containers).
            final ConfigureDataFixSchemaEvent event = eventGenerator.apply(null);

            //Log that we are processing that particular event.
            LOGGER.debug("Registering DFU Schema updates: {}", event.getSchema().getVersionKey());

            //Check if we are a compatible schema.
            if (event.getSchema() instanceof ForgeSchema forgeSchema)
            {
                //And reset if needed.
                forgeSchema.resetSchema();
            }
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
    private static CompletableFuture<List<Throwable>> postDispatchSchemaEvents(
      final Executor executor, final IModStateTransition.EventGenerator<ConfigureDataFixSchemaEvent> eventGenerator
    )
    {
        return CompletableFuture.runAsync(() -> {
            //Grab the event again, with a null container (is reference equals anyways for all containers).
            final ConfigureDataFixSchemaEvent event = eventGenerator.apply(null);

            //Notify about the completion of the event firing.
            LOGGER.debug("Registered DFU Schema updates: {}", event.getSchema().getVersionKey());

            //Check for a compatible schema.
            if (event.getSchema() instanceof ForgeSchema forgeSchema)
            {
                //Rebuild the schema with the mod data.
                forgeSchema.rebuildSchema(
                  event.getEntityTypes(),
                  event.getBlockEntityTypes(),
                  event.getEntityTypesToRemove(),
                  event.getBlockEntityTypesToRemove()
                );
            }
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
    private static CompletableFuture<List<Throwable>> rebuildFixerAfterSchemaEvents(
      final Executor executor, final CompletableFuture<List<Throwable>> listCompletableFuture
    )
    {
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
                    //We don't yet recompute the type rewrite rules, since we still need to collect the additional fixers for that.
                    forgeDataFixerDelegateHandler.rebuildFixer(Util.bootstrapExecutor(), false);
                }
            }
        }, executor);
    }
}
