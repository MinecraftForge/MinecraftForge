package net.minecraftforge.datafix;

import com.google.common.collect.Lists;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.Util;
import net.minecraft.util.datafix.DataFixers;
import net.minecraftforge.event.datafix.ConfigureDatafixSchemaEvent;
import net.minecraftforge.fml.IModStateTransition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Stream;

public class ForgeDataFixerEventHandler
{
    private static final Logger LOGGER = LogManager.getLogger();

    private static List<ForgeSchema> getAllSchemas() {
        if (DataFixers.getDataFixer() instanceof ForgeDataFixerDelegateHandler forgeDataFixerDelegateHandler) {
            return forgeDataFixerDelegateHandler.getAllSchemas();
        }

        return Collections.emptyList();
    }

    public static Stream<IModStateTransition.EventGenerator<?>> generateConfigureEvents() {
        List<ForgeSchema> schemas = Lists.newArrayList(getAllSchemas());
        schemas.sort(Comparator.comparingInt(Schema::getVersionKey));

        return schemas.stream()
                 .map(ConfigureDatafixSchemaEvent::new)
                 .map(event -> IModStateTransition.EventGenerator.fromFunction(modContainer -> event));
    }

    public static CompletableFuture<List<Throwable>> preDispatch(final Executor executor, final IModStateTransition.EventGenerator<ConfigureDatafixSchemaEvent> eventGenerator) {
        return CompletableFuture.runAsync(()-> {
            final ConfigureDatafixSchemaEvent event = eventGenerator.apply(null);
            LOGGER.info("Registering DFU Schema updates: {}", event.getSchema().getVersionKey());
            if (event.getSchema() instanceof ForgeSchema forgeSchema) {
                forgeSchema.resetSchema();
            }
        }, executor).thenApply(v-> Collections.emptyList());
    }

    public static CompletableFuture<List<Throwable>> postDispatch(final Executor executor, final IModStateTransition.EventGenerator<ConfigureDatafixSchemaEvent> eventGenerator) {
        return CompletableFuture.runAsync(()-> {
            final ConfigureDatafixSchemaEvent event = eventGenerator.apply(null);
            LOGGER.info("Registered DFU Schema updates: {}", event.getSchema().getVersionKey());
            if (event.getSchema() instanceof ForgeSchema forgeSchema) {
                forgeSchema.rebuildSchema(
                  event.getEntityTypes(),
                  event.getBlockEntityTypes()
                );
            }
        }, executor).handle((v, t)->t != null ? Collections.singletonList(t): Collections.emptyList());
    }

    public static CompletableFuture<List<Throwable>> rebuildFixer(final Executor executor, final CompletableFuture<List<Throwable>> listCompletableFuture) {
        return listCompletableFuture.whenCompleteAsync((errors, except) -> {
            if (except != null) {
                LOGGER.fatal("Detected errors during dfu setup. DFU might not be available.", except);
            } else {
                if (DataFixers.getDataFixer() instanceof ForgeDataFixerDelegateHandler forgeDataFixerDelegateHandler) {
                    //Do not do this on the mod executor!
                    //This will crash the game.
                    forgeDataFixerDelegateHandler.rebuildFixer(Util.bootstrapExecutor());
                }
            }
        }, executor);
    }
}
