/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.event.datafix;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.fixes.References;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Event fired in parallel on the mod event bus to allow for the modification
 * of a DFU Schemas type signature.
 *
 * If used to register a new entity or block entity to a particular version,
 * then this also will need to be registered to all newer versions to persist properly!
 * As opposed to vanilla which automatically transfers the entities and block entities from older
 * schemas to newer ones, this event does not do that and the modder is on their own to implement this properly.
 *
 * We suggest instead of checking for version equality to check for a greater or equal version, and to unregister (or override with a newer version)
 * when the type changes again in a newer version.
 *
 * If a DFU touches a vanilla type (like a list of block entities in a chunk), then all the possible variations of that type need to be known,
 * in other words, when mojang changes the type signature of a vanilla type, all the mods that touch that type need also to register their types in that schema to persist.
 * En example of this is when Mojang moved the block entity list in the chunk during the upgrade from 1.17 to 1.18, any block entity type which is
 * not registered to the 1.18 schema will be lost, since the rule considers a not registered block entity type, as a type that got removed from the game.
 */
public class ConfigureDataFixSchemaEvent extends Event implements IModBusEvent
{

    private final Schema schema;

    private final Map<String, Supplier<TypeTemplate>> entityTypes = Maps.newConcurrentMap();
    private final Map<String, Supplier<TypeTemplate>> blockEntityTypes = Maps.newConcurrentMap();

    //Some unmodifiable views of the above maps
    private final Map<String, Supplier<TypeTemplate>> entityTypesView = Collections.unmodifiableMap(entityTypes);
    private final Map<String, Supplier<TypeTemplate>> blockEntityTypesView = Collections.unmodifiableMap(blockEntityTypes);

    public ConfigureDataFixSchemaEvent(final Schema schema)
    {
        this.schema = schema;
    }

    /**
     * The schema which you can configure.
     *
     * @return The schema
     */
    public Schema getSchema()
    {
        return schema;
    }

    /**
     * The version of the schema that is about to be configured.
     *
     * @return The version.
     */
    public int getVersion()
    {
        return getSchema().getVersionKey();
    }

    /**
     * Registers a new simple (catch-all-nbt) block entity to the current schema and all its descendent versions.
     *
     * @param registryName The registry name of the block entity.
     */
    public void registerSimpleBlockEntity(final ResourceLocation registryName)
    {
        registerBlockEntity(registryName, DSL::remainder);
    }

    /**
     * Registers a new simple (catch-all-nbt) block entity with an item inventory array on the nbt field with the given name to the current schema and all its descendent versions.
     *
     * @param registryName The registry name of the block entity.
     * @param nbtKey The name of the nbt field to where the item array is located.
     */
    public void registerSimpleItemInventory(final ResourceLocation registryName, final String nbtKey)
    {
        registerBlockEntity(registryName, () -> DSL.optionalFields(nbtKey, DSL.list(References.ITEM_STACK.in(getSchema()))));
    }

    /**
     * Allows for the custom definition of a new block entity type and registration of it to the current schema and all its descendent versions.
     * The function is given the registry name of the block entity type template to produce.
     *
     * @param registryName The registry name of the block entity type.
     * @param template The function to produce the block entity type template.
     */
    public void registerBlockEntity(final ResourceLocation registryName, final Function<String, TypeTemplate> template)
    {
        registerBlockEntity(registryName, () -> template.apply(registryName.toString()));
    }

    /**
     * Allows for the custom definition of a new block entity type and registration of it to the current schema and all its descendent versions.
     * The supplier is invoked to produce the block entity type template.
     *
     * @param registryName The registry name of the block entity type.
     * @param template The supplier of the type template.
     */
    public void registerBlockEntity(final ResourceLocation registryName, final Supplier<TypeTemplate> template)
    {
        blockEntityTypes.put(registryName.toString(), template);
    }

    /**
     * Removes an already registered block entity from the current schema and all its descendent versions.
     * This allows for the removal of a custom block entity type when it is registered in a parent version and is no longer needed.
     *
     * @param registryName The registry name of the block entity to remove.
     */
    public void removeBlockEntity(final ResourceLocation registryName)
    {
        blockEntityTypes.put(registryName.toString(), null);
    }

    /**
     * Registers a new simple (catch-all-nbt) entity to the current schema and all its descendent versions.
     *
     * @param registryName The registry name of the entity.
     */
    public void registerSimpleEntity(final ResourceLocation registryName)
    {
        registerEntity(registryName, DSL::remainder);
    }

    /**
     * Allows for the custom definition of a new entity type and registration of it to the current schema and all its descendent versions.
     * The function is given the registry name of the entity type template to produce.
     *
     * @param registryName The registry name of the entity type.
     * @param template The function to produce the entity type template.
     */
    public void registerEntity(final ResourceLocation registryName, final Function<String, TypeTemplate> template)
    {
        registerEntity(registryName, () -> template.apply(registryName.toString()));
    }

    /**
     * Allows for the custom definition of a new entity type and registration of it to the current schema and all its descendent versions.
     * The supplier is invoked to produce the entity type template.
     *
     * @param registryName The registry name of the entity type.
     * @param template The supplier of the type template.
     */
    public void registerEntity(final ResourceLocation registryName, final Supplier<TypeTemplate> template)
    {
        entityTypes.put(registryName.toString(), template);
    }

    /**
     * Removes an already registered entity from the current schema and all its descendent versions.
     * This allows for the removal of a custom entity type when it is registered in a parent version and is no longer needed.
     * 
     * @param registryName The registry name of the entity to remove.
     */
    public void removeEntity(final ResourceLocation registryName)
    {
        entityTypes.put(registryName.toString(), null);
    }

    /**
     * An unmodifiable map of all registered additional modded block entity types.
     * Keys may be mapped to a null value if they are supposed to be removed from the schema and its descendant versions, overriding the
     * type specification added in a parent version.
     *
     * @return The already registered additional modded block entity types.
     */
    public Map<String, Supplier<TypeTemplate>> getEntityTypes()
    {
        return entityTypesView;
    }

    /**
     * An unmodifiable map of all registered additional modded entity types.
     * Keys may be mapped to a null value if they are supposed to be removed from the schema and its descendant versions, overriding the
     * type specification added in a parent version.
     *
     * @return The already registered additional modded entity types.
     */
    public Map<String, Supplier<TypeTemplate>> getBlockEntityTypes()
    {
        return blockEntityTypesView;
    }
}
