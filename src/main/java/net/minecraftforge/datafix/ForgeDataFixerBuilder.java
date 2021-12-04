package net.minecraftforge.datafix;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.DataFixerUpper;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.schemas.Schema;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;

/**
 * The purpose of this custom data fixer builder is to gain full control over the datafixer that is in play at runtime.
 * <p>
 * This means that schemas become adaptable and that the ruleset can be dynamically rebaked to adapt to the changes mod authors make to the schemas.
 * <p>
 * Some methods are carbon copies of the DFU counterpart, but are copied so that we can control the data.
 */
public class ForgeDataFixerBuilder extends DataFixerBuilder
{
    private static final Logger LOGGER = LogManager.getLogger();

    private final int                              dataVersion;
    private final List<ForgeSchema>                allSchemas    = new ArrayList<>();
    private final Int2ObjectSortedMap<ForgeSchema> schemas       = new Int2ObjectAVLTreeMap<>();
    private final List<ForgeDataFixDelegate>       globalList    = Lists.newArrayList();
    private final IntSortedSet                     fixerVersions = new IntAVLTreeSet();

    public ForgeDataFixerBuilder(final int dataVersion)
    {
        super(dataVersion);
        this.dataVersion = dataVersion;
    }

    @Override
    public Schema addSchema(final int version, final int subVersion, final BiFunction<Integer, Schema, Schema> factory)
    {
        final int key = DataFixUtils.makeKey(version, subVersion);
        final Schema parent = schemas.isEmpty() ? null : schemas.get(getLowestSchemaSameVersion(schemas, key - 1));
        return addWrappedSchema(factory.apply(key, parent), parent);
    }

    @Override
    public void addSchema(final Schema schema)
    {
        final Schema parent = schemas.isEmpty() ? null : schemas.get(getLowestSchemaSameVersion(schemas, schema.getVersionKey() - 1));
        addWrappedSchema(schema, parent);
    }

    private ForgeSchema addWrappedSchema(final Schema schema, final Schema parent)
    {
        final ForgeSchema wrapped = new ForgeSchema(schema.getVersionKey(), parent, schema);
        schemas.put(wrapped.getVersionKey(), wrapped);
        allSchemas.add(wrapped);
        return wrapped;
    }

    @Override
    public DataFixer build(final Executor executor)
    {
        return new ForgeDataFixerDelegateHandler(dataVersion, schemas, allSchemas, globalList, executor, fixerVersions);
    }

    /**
     * CC of the original method in {@link DataFixerUpper}. Is protected so not accessible for us.
     *
     * @param schemas    The schemas to search in.
     * @param versionKey The version to find the lowest matching schema for.
     * @return The version for the lowest matching schema.
     */
    protected static int getLowestSchemaSameVersion(final Int2ObjectSortedMap<ForgeSchema> schemas, final int versionKey)
    {
        if (versionKey < schemas.firstIntKey())
        {
            return schemas.firstIntKey();
        }
        return schemas.subMap(0, versionKey + 1).lastIntKey();
    }

    @Override
    public void addFixer(final DataFix fix)
    {
        final int version = DataFixUtils.getVersion(fix.getVersionKey());

        if (version > dataVersion)
        {
            LOGGER.warn("Ignored fix registered for version: {} as the DataVersion of the game is: {}", version, dataVersion);
            return;
        }

        globalList.add(new ForgeDataFixDelegate(fix));
        fixerVersions.add(fix.getVersionKey());
    }
}
