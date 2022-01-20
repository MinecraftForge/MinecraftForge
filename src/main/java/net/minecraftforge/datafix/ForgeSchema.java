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

package net.minecraftforge.datafix;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.families.RecursiveTypeFamily;
import com.mojang.datafixers.types.families.TypeFamily;
import com.mojang.datafixers.types.templates.RecursivePoint;
import com.mojang.datafixers.types.templates.TypeTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * A partial carbon copy of the {@link Schema} class.
 * But this allows us to delay the setup of the type hierarchy until we need it.
 */
class ForgeSchema extends Schema
{
    //Can not be final, needs to be initialized on the fly!
    //Constructor invocation order and the what not!
    private Map<String, Supplier<TypeTemplate>> TYPE_TEMPLATES = Maps.newConcurrentMap();
    private Map<String, Type<?>>                TYPES          = Maps.newConcurrentMap();
    private Map<String, Integer>                RECURSIVE_TYPES     = Maps.newConcurrentMap();

    private final Map<String, Supplier<TypeTemplate>> MODDED_ENTITY_TYPES = Maps.newConcurrentMap();
    private final Map<String, Supplier<TypeTemplate>> MODDED_BLOCK_ENTITY_TYPES = Maps.newConcurrentMap();

    //Store some data we can not access later anymore.
    private final String name;
    private final Schema wrapped;

    ForgeSchema(final int versionKey, final Schema parent, final Schema wrapped)
    {
        super(versionKey, parent);
        this.wrapped = wrapped;

        final int subVersion = DataFixUtils.getSubVersion(versionKey);
        name = "V" + DataFixUtils.getVersion(versionKey) + (subVersion == 0 ? "" : "." + subVersion);

        //Now with the vanilla data in the schema, lets build our first version again.
        //Yes this executes the type resolve multiple times per schema (but it's not a big deal)
        resetSchema();
        rebuildSchema(
          Collections.emptyMap(),
          Collections.emptyMap()
        );
    }

    /**
     * Rebuilds the types, with support for the case that no types exists.
     * (Which should never happen, but you never know!)
     *
     * @return The type map.
     */
    protected Map<String, Type<?>> buildTypes()
    {
        //This is potentially invoked from the super constructor, so our field init code has not run yet.
        //Set it to a none null value, so the super type can do its initial construction.
        if (RECURSIVE_TYPES == null)
        {
            RECURSIVE_TYPES = Maps.newHashMap();
        }

        //This is potentially invoked from the super constructor, so our field init code has not run yet.
        //Set it to a none null value, so the super type can do its initial construction.
        if (TYPE_TEMPLATES == null)
        {
            TYPE_TEMPLATES = Maps.newHashMap();
        }

        //Result map.
        final Map<String, Type<?>> types = Maps.newConcurrentMap();

        //All the core templates into which recursion of data is possible.
        final List<TypeTemplate> templates = Lists.newArrayList();

        //Check all recursion types
        for (final Map.Entry<String, Integer> entry : RECURSIVE_TYPES.entrySet())
        {
            //Generate a template for the type.
            templates.add(DSL.check(entry.getKey(), entry.getValue(), getTemplate(entry.getKey())));
        }

        //Merge them all together into one root recursive type.
        final Optional<TypeTemplate> availableTypeTemplate = templates.stream().reduce(DSL::or);
        if (availableTypeTemplate.isEmpty())
        {
            //Check for its existence or nuke.
            return Maps.newConcurrentMap();
        }
        //Build a type family for our recursive types.
        final TypeTemplate choice = availableTypeTemplate.get();
        final TypeFamily family = new RecursiveTypeFamily(name, choice);

        //Now process all other known types, taking care of recursion.
        for (final String name : TYPE_TEMPLATES.keySet())
        {
            final Type<?> type;
            final int recurseId = RECURSIVE_TYPES.getOrDefault(name, -1);
            if (recurseId != -1)
            {
                type = family.apply(recurseId);
            }
            else
            {
                type = getTemplate(name).apply(family).apply(-1);
            }
            types.put(name, type);
        }
        return types;
    }

    @Override
    public Set<String> types()
    {
        //This is potentially invoked from the super constructor, so our field init code has not run yet.
        //Set it to a none null value, so the super type can do its initial construction.
        if (RECURSIVE_TYPES == null)
        {
            RECURSIVE_TYPES = Maps.newHashMap();
        }

        //This is potentially invoked from the super constructor, so our field init code has not run yet.
        //Set it to a none null value, so the super type can do its initial construction.
        if (TYPE_TEMPLATES == null)
        {
            TYPE_TEMPLATES = Maps.newHashMap();
        }

        return TYPES.keySet();
    }

    @Override
    public Type<?> getTypeRaw(final DSL.TypeReference type)
    {
        //This is potentially invoked from the super constructor, so our field init code has not run yet.
        //Set it to a none null value, so the super type can do its initial construction.
        if (RECURSIVE_TYPES == null)
        {
            RECURSIVE_TYPES = Maps.newHashMap();
        }

        //This is potentially invoked from the super constructor, so our field init code has not run yet.
        //Set it to a none null value, so the super type can do its initial construction.
        if (TYPE_TEMPLATES == null)
        {
            TYPE_TEMPLATES = Maps.newHashMap();
        }

        final String name = type.typeName();
        return TYPES.computeIfAbsent(name, key -> {
            throw new IllegalArgumentException("Unknown type: " + name);
        });
    }

    @Override
    public Type<?> getType(final DSL.TypeReference type)
    {
        //This is potentially invoked from the super constructor, so our field init code has not run yet.
        //Set it to a none null value, so the super type can do its initial construction.
        if (RECURSIVE_TYPES == null)
        {
            RECURSIVE_TYPES = Maps.newHashMap();
        }

        //This is potentially invoked from the super constructor, so our field init code has not run yet.
        //Set it to a none null value, so the super type can do its initial construction.
        if (TYPE_TEMPLATES == null)
        {
            TYPE_TEMPLATES = Maps.newHashMap();
        }

        final String name = type.typeName();
        final Type<?> type1 = TYPES.computeIfAbsent(name, key -> {
            throw new IllegalArgumentException("Unknown type: " + name);
        });
        if (type1 instanceof RecursivePoint.RecursivePointType<?>)
        {
            return type1.findCheckedType(-1).orElseThrow(() -> new IllegalStateException("Could not find choice type in the recursive type"));
        }
        return type1;
    }

    @Override
    public TypeTemplate resolveTemplate(final String name)
    {
        //This is potentially invoked from the super constructor, so our field init code has not run yet.
        //Set it to a none null value, so the super type can do its initial construction.
        if (RECURSIVE_TYPES == null)
        {
            RECURSIVE_TYPES = Maps.newHashMap();
        }

        //This is potentially invoked from the super constructor, so our field init code has not run yet.
        //Set it to a none null value, so the super type can do its initial construction.
        if (TYPE_TEMPLATES == null)
        {
            TYPE_TEMPLATES = Maps.newHashMap();
        }

        return TYPE_TEMPLATES.getOrDefault(name, () -> {
            throw new IllegalArgumentException("Unknown type: " + name);
        }).get();
    }

    @Override
    public TypeTemplate id(final String name)
    {
        //This is potentially invoked from the super constructor, so our field init code has not run yet.
        //Set it to a none null value, so the super type can do its initial construction.
        if (RECURSIVE_TYPES == null)
        {
            RECURSIVE_TYPES = Maps.newHashMap();
        }

        //This is potentially invoked from the super constructor, so our field init code has not run yet.
        //Set it to a none null value, so the super type can do its initial construction.
        if (TYPE_TEMPLATES == null)
        {
            TYPE_TEMPLATES = Maps.newHashMap();
        }

        final int id = RECURSIVE_TYPES.getOrDefault(name, -1);
        if (id != -1)
        {
            return DSL.id(id);
        }
        return getTemplate(name);
    }

    @Override
    public void registerTypes(
      final Schema schema, final Map<String, Supplier<TypeTemplate>> entityTypes, final Map<String, Supplier<TypeTemplate>> blockEntityTypes
    )
    {
        //This is a bit wonky, in vanilla it suffices to register the types to one single version
        //And all newer versions will have this type.
        //Due to the way we set up the schemas with events later, that is not possible anymore.
        //Modders will need to register their types on all versions that they should exist in,
        //not just in the oldest version.

        if (wrapped != null)
        {
            wrapped.registerTypes(schema, entityTypes, blockEntityTypes);
        }
        else if (parent != null)
        {
            parent.registerTypes(schema, entityTypes, blockEntityTypes);
        }
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerEntities(final Schema schema)
    {
        //This is a bit wonky, in vanilla it suffices to register the types to one single version
        //And all newer versions will have this type.
        //Due to the way we set up the schemas with events later, that is not possible anymore.
        //Modders will need to register their types on all versions that they should exist in,
        //not just in the oldest version.

        if (wrapped != null)
        {
            return wrapped.registerEntities(schema);
        }
        if (parent != null)
        {
            return parent.registerEntities(schema);
        }

        return Maps.newHashMap();
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(final Schema schema)
    {
        //This is a bit wonky, in vanilla it suffices to register the types to one single version
        //And all newer versions will have this type.
        //Due to the way we set up the schemas with events later, that is not possible anymore.
        //Modders will need to register their types on all versions that they should exist in,
        //not just in the oldest version.

        if (wrapped != null)
        {
            return wrapped.registerBlockEntities(schema);
        }
        if (parent != null)
        {
            return parent.registerBlockEntities(schema);
        }

        return Maps.newHashMap();
    }

    /**
     * Registers a new type to the Schema with its type template.
     *
     * @param recursive Indicates if the type is potentially recursive.
     * @param type The type in question.
     * @param template Its template.
     */
    @Override
    public void registerType(final boolean recursive, final DSL.TypeReference type, final Supplier<TypeTemplate> template)
    {
        //This is invoked from the super constructor, so our field init code has not run yet.
        //Set it to a none null value, so the super type can do its initial construction.
        if (TYPE_TEMPLATES == null)
        {
            TYPE_TEMPLATES = Maps.newHashMap();
        }

        //This is invoked from the super constructor, so our field init code has not run yet.
        //Set it to a none null value, so the super type can do its initial construction.
        if (RECURSIVE_TYPES == null)
        {
            RECURSIVE_TYPES = Maps.newHashMap();
        }

        TYPE_TEMPLATES.put(type.typeName(), template);
        if (recursive && !RECURSIVE_TYPES.containsKey(type.typeName()))
        {
            RECURSIVE_TYPES.put(type.typeName(), RECURSIVE_TYPES.size());
        }
    }

    /**
     * Resets the schema so that the type map can be properly rebuild.
     */
    void resetSchema()
    {
        this.TYPE_TEMPLATES.clear();
        this.TYPES.clear();
        this.RECURSIVE_TYPES.clear();
        this.MODDED_ENTITY_TYPES.clear();
        this.MODDED_BLOCK_ENTITY_TYPES.clear();
    }

    /**
     * Rebuilds the schema's type map.
     * This also injects custom types for entities and block entities.
     *
     * @param modEntityTypes The mod entity types to inject.
     * @param modBlockEntityTypes The block entity types to inject.
     */
    void rebuildSchema(
      final Map<String, Supplier<TypeTemplate>> modEntityTypes,
      final Map<String, Supplier<TypeTemplate>> modBlockEntityTypes
    )
    {
        //First grab the parent's additional modded types, if the parent is compatible.
        if (this.parent instanceof ForgeSchema parentForgeSchema)
        {
            //Compatible parent found, add its modded types.
            this.MODDED_ENTITY_TYPES.putAll(parentForgeSchema.MODDED_ENTITY_TYPES);
            this.MODDED_BLOCK_ENTITY_TYPES.putAll(parentForgeSchema.MODDED_BLOCK_ENTITY_TYPES);
        }

        //Now add our own modded types, and potentially override the data.
        this.MODDED_ENTITY_TYPES.putAll(modEntityTypes);
        this.MODDED_BLOCK_ENTITY_TYPES.putAll(modBlockEntityTypes);

        //Remove those types from the map, which the event marked for removal.
        this.MODDED_ENTITY_TYPES.values().removeIf(Objects::isNull);
        this.MODDED_BLOCK_ENTITY_TYPES.values().removeIf(Objects::isNull);

        //Grab the vanilla entities.
        final Map<String, Supplier<TypeTemplate>> entityTypes = registerEntities(this);
        final Map<String, Supplier<TypeTemplate>> blockEntityTypes = registerBlockEntities(this);

        //Add all modded entities and block entities.
        entityTypes.putAll(this.MODDED_ENTITY_TYPES);
        blockEntityTypes.putAll(this.MODDED_BLOCK_ENTITY_TYPES);

        //Re-register them.
        registerTypes(this, entityTypes, blockEntityTypes);

        //Build the type map again.
        TYPES = buildTypes();
    }
}
