/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.datafix;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Func;
import com.mojang.datafixers.types.templates.TypeTemplate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.fixes.References;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Event fired in parallel on the mod event bus to allow for registration of new fixers to a given schema.
 */
public class RegisterFixesEvent extends Event implements IModBusEvent
{
    private final Schema schema;
    private final ModContainer                      container;
    private final List<Function<Schema, DataFix>> fixerFactories;

    public RegisterFixesEvent(
      final Schema schema,
      final ModContainer container
    ) {
        this.schema = schema;
        this.container = container;
        this.fixerFactories = Lists.newArrayList();
    }

    /**
     * The schema for which you can add fixes.
     *
     * @return The schema
     */
    public Schema getSchema()
    {
        return schema;
    }

    /**
     * The mod container for whom fixes are being added.
     *
     * @return The mod container.
     */
    public ModContainer getContainer()
    {
        return container;
    }

    /**
     * The version of the schema for which you are adding fixers.
     *
     * @return The version.
     */
    public int getVersion()
    {
        return getSchema().getVersionKey();
    }

    /**
     * A stream of already registered factories for fixers.
     *
     * @return The fixer factories.
     */
    public Stream<Function<Schema, DataFix>> getFixerFactories()
    {
        return fixerFactories.stream();
    }

    /**
     * A chainable registration method which can be used to add fixers to the current schema.
     *
     * @param fixerFactory The fixer factory to add.
     * @param others The other fixer factories to add. (Optional)
     * @return The event itself, with the fixer factories added.
     */
    @SafeVarargs
    public final RegisterFixesEvent addFixer(final Function<Schema, DataFix> fixerFactory, final Function<Schema, DataFix>... others) {
        this.fixerFactories.add(fixerFactory);
        this.fixerFactories.addAll(List.of(others));

        return this;
    };
}
