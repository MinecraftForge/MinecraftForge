package net.minecraftforge.datafix;

import com.google.common.collect.Lists;
import com.mojang.datafixers.*;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * A delegate handler which can rebuild a data fixer on the fly.
 */
public class ForgeDataFixerDelegateHandler extends DataFixerUpper
{
    private static final Logger     LOGGER = LogManager.getLogger();

    private final     int    dataVersion;
    private final     List<ForgeSchema> allSchemas;
    private final     Int2ObjectSortedMap<ForgeSchema> schemas       ;
    private final     List<DataFix>               globalList  ;
    private final     IntSortedSet                fixerVersions;

    private              InnerFixer activeFixer;



    protected ForgeDataFixerDelegateHandler(
      final int dataVersion,
      final Int2ObjectSortedMap<ForgeSchema> schemas,
      final List<ForgeSchema> allSchemas,
      final List<DataFix> globalList,
      final Executor executor,
      final IntSortedSet fixerVersions)
    {
        super(new Int2ObjectAVLTreeMap<>(), new ArrayList<>(), new IntAVLTreeSet());

        this.dataVersion = dataVersion;
        this.allSchemas = allSchemas;
        this.schemas = schemas;
        this.globalList = globalList;
        this.fixerVersions = fixerVersions;

        rebuildFixer(executor);
    }

    public void rebuildFixer(
      final Executor executor
    )
    {
        this.allSchemas.forEach(ForgeSchema::resetSchema);

        this.activeFixer = new InnerFixer(new Int2ObjectAVLTreeMap<Schema>(schemas), new ArrayList<>(globalList), new IntAVLTreeSet(fixerVersions));

        final IntBidirectionalIterator iterator = activeFixer.fixerVersions().iterator();
        while (iterator.hasNext())
        {
            final int versionKey = iterator.nextInt();
            final Schema schema = schemas.get(versionKey);
            for (final String typeName : schema.types())
            {
                CompletableFuture.runAsync(() -> {
                    final Type<?> dataType = schema.getType(() -> typeName);
                    final TypeRewriteRule rule = activeFixer.getRule(DataFixUtils.getVersion(versionKey), dataVersion);
                    dataType.rewrite(rule, DataFixerUpper.OPTIMIZATION_RULE);
                }, executor).exceptionally(e -> {
                    LOGGER.error("Unable to build datafixers", e);
                    Runtime.getRuntime().exit(1);
                    return null;
                });
            }
        }
    }

    @Override
    public <T> Dynamic<T> update(final DSL.TypeReference type, final Dynamic<T> input, final int version, final int newVersion)
    {
        return activeFixer.update(
          type, input, version, newVersion
        );
    }

    @Override
    public Schema getSchema(final int key)
    {
        return activeFixer.getSchema(key);
    }

    private static final class InnerFixer extends DataFixerUpper
    {
        private InnerFixer(final Int2ObjectSortedMap<Schema> schemas, final List<DataFix> globalList, final IntSortedSet fixerVersions)
        {
            super(schemas, globalList, fixerVersions);
        }

        @Override
        public <T> Dynamic<T> update(final DSL.TypeReference type, final Dynamic<T> input, final int version, final int newVersion)
        {
            return super.update(type, input, version, newVersion);
        }

        @Override
        public Schema getSchema(final int key)
        {
            return super.getSchema(key);
        }

        @Override
        public Type<?> getType(final DSL.TypeReference type, final int version)
        {
            return super.getType(type, version);
        }

        @Override
        public TypeRewriteRule getRule(final int version, final int dataVersion)
        {
            return super.getRule(version, dataVersion);
        }

        @Override
        public IntSortedSet fixerVersions()
        {
            return super.fixerVersions();
        }
    }
}
