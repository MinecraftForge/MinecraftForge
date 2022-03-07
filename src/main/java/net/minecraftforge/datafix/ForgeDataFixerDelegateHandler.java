/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.datafix;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DataFixerUpper;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMaps;
import it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import it.unimi.dsi.fastutil.ints.IntSortedSets;
import it.unimi.dsi.fastutil.ints.IntBidirectionalIterator;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * A delegate handler which can rebuild a data fixer on the fly.
 */
class ForgeDataFixerDelegateHandler extends DataFixerUpper
{
    private static final boolean DISABLE_OFF_THREAD_RULE_PRE_COMPUTE = Boolean.getBoolean("forge.datafixer.disablePreCompute");
    private static final Logger LOGGER = LogUtils.getLogger();

    private final int                              dataVersion;
    private final List<ForgeSchema>                allSchemas;
    private final Int2ObjectSortedMap<ForgeSchema> schemas;
    private final List<ForgeDataFixDelegate> vanillaFixers;
    private final IntSortedSet               vanillaFixerVersions;

    private InnerFixer activeFixer;

    ForgeDataFixerDelegateHandler(
      final int dataVersion,
      final Int2ObjectSortedMap<ForgeSchema> schemas,
      final List<ForgeSchema> allSchemas,
      final List<ForgeDataFixDelegate> vanillaFixers,
      final Executor executor,
      final IntSortedSet vanillaFixerVersions
    )
    {
        //Supply dummy data to the super constructor
        //We never invoke and method on the super and so this data does not matter.
        super(Int2ObjectSortedMaps.emptyMap(), Collections.emptyList(), IntSortedSets.EMPTY_SET);

        //Store the builders input so we can rebuild it later if needed.
        this.dataVersion = dataVersion;
        this.allSchemas = Collections.unmodifiableList(allSchemas);
        this.schemas = schemas;
        this.vanillaFixers = vanillaFixers;
        this.vanillaFixerVersions = vanillaFixerVersions;

        //Rebuild the setup immediately, don't trigger the lazy rebuild, it causes NPE's.
        rebuildFixer(executor, false);
    }

    public void rebuildFixer(final Executor executor, final boolean triggerOffThreadCompute)
    {
        //Rebuild the total environment.
        //Collect all fixes.
        final List<ForgeDataFixDelegate> globalList = new ArrayList<>(this.vanillaFixers);

        //Collect all schema version keys with known fixers.
        final IntSortedSet fixerVersions = new IntAVLTreeSet(this.vanillaFixerVersions);

        //And now collect all custom fixers again that we know of and update.
        getAllSchemas().forEach(schema -> {
            schema.getCustomFixers().forEach(fix -> {
                final int version = DataFixUtils.getVersion(fix.getVersionKey());

                if (version > dataVersion)
                {
                    LOGGER.warn("Ignored fix registered for version: {} as the DataVersion of the game is: {}", version, dataVersion);
                    return;
                }

                final ForgeDataFixDelegate delegate = new ForgeDataFixDelegate(fix);

                globalList.add(delegate);
                fixerVersions.add(fix.getVersionKey());
            });
          });

        //Reset our rules, all rules keep their schemas cached.
        //We can not use that and need to forcefully recompute them.
        this.vanillaFixers.forEach(ForgeDataFixDelegate::resetRule);

        //All schemas are already reset via the mod event bus as this time.
        //Rebuild of those is not needed.
        this.activeFixer = new InnerFixer(new Int2ObjectAVLTreeMap<Schema>(schemas), new ArrayList<>(globalList), new IntAVLTreeSet(fixerVersions));

        //This section was added to make it easier to debug the rule set at runtime, since it is no longer pre-computed off thread,
        //But only on the main thread, at the moment it is needed.
        //This basically disables the REWRITE_CACHE prefill and fills it on-demand.
        //Although it is mainly used for debugging, it also provides a good way of reducing DFU's memory footprint, when it is not needed.
        //Since the following code will compute an update rule from every version to the current for every known type in the schema,
        //relating to this version. By default, however this is disabled to preserve vanilla compatibility, since it does do the pre-compute
        //to fill up the cache.
        //To enable it start the JVM with the system property set to true, -Dforge.datafixer.disablePreCompute=true needs to be added as
        //a JVM Launch argument.
        if (DISABLE_OFF_THREAD_RULE_PRE_COMPUTE)
        {
            LOGGER.warn("The pre-compute of the data fixer rules is disabled, this is not recommended for normal use, but can be useful for debugging or in low-memory situations.");
            return;
        }

        //We only rebuild once we are told to do so.
        if (!triggerOffThreadCompute)
        {
            return;
        }

        //Get an iterator for all known versions.
        final IntBidirectionalIterator iterator = activeFixer.fixerVersions().iterator();
        while (iterator.hasNext())
        {
            //Grab the key.
            final int versionKey = iterator.nextInt();

            //Grab its schema.
            final Schema schema = schemas.get(versionKey);

            //For each type in the schema, compute its rewrite rule for the current version.
            for (final String typeName : schema.types())
            {
                //Schedule the recompute off-thread
                CompletableFuture.runAsync(() -> {
                    //Grab the type.
                    final Type<?> dataType = schema.getType(() -> typeName);

                    //Grab its normal rewrite rule for the given version and our current target version.
                    final TypeRewriteRule rule = activeFixer.getRule(DataFixUtils.getVersion(versionKey), dataVersion);

                    //Optimize the rule to only apply to the given type.
                    dataType.rewrite(rule, DataFixerUpper.OPTIMIZATION_RULE);
                }, executor).exceptionally(e -> {
                    //Fails.
                    LOGGER.error("Unable to build datafixers", e);

                    //Just kill it, if this fails we have a problem anyway!
                    //This is literally what vanilla does hence the 1 to 1 copy.
                    Runtime.getRuntime().exit(1);
                    return null;
                });
            }
        }
    }

    @Override
    public <T> Dynamic<T> update(
      final DSL.TypeReference type, final Dynamic<T> input, final int version, final int newVersion
    )
    {
        //Invoke the internal DFU engine.
        return activeFixer.update(
          type, input, version, newVersion
        );
    }

    @Override
    public Schema getSchema(final int key)
    {
        //Grab it from the internal DFU engine.
        return activeFixer.getSchema(key);
    }

    /**
     * Gives access to all known schemas at the point of building of this wrapper.
     *
     * @return All schemas known to DFU.
     */
    public List<ForgeSchema> getAllSchemas()
    {
        return allSchemas;
    }

    /**
     * Represents a DFU that gives access to the protected sub methods so data can be properly extracted.
     * Delegates all of its method invocations to the super DFU instance.
     */
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
