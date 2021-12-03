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
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.datafixers.types.templates.TypeTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
    private Map<String, Integer>                RECURSIVE_TYPES = Maps.newConcurrentMap();

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
        //Result map.
        final Map<String, Type<?>> types = Maps.newHashMap();

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
            return Maps.newHashMap();
        }
        //Build a type family for our recursive types.
        final TypeTemplate choice = availableTypeTemplate.get();
        final TypeFamily family = new RecursiveTypeFamily(name, choice);

        //Now process all other known types, taking care of recursion.
        for (final String name : TYPE_TEMPLATES.keySet()) {
            final Type<?> type;
            final int recurseId = RECURSIVE_TYPES.getOrDefault(name, -1);
            if (recurseId != -1)
            {
                type = family.apply(recurseId);
            } else {
                type = getTemplate(name).apply(family).apply(-1);
            }
            types.put(name, type);
        }
        return types;
    }

    public Set<String> types()
    {
        return TYPES.keySet();
    }

    public Type<?> getTypeRaw(final DSL.TypeReference type)
    {
        final String name = type.typeName();
        return TYPES.computeIfAbsent(name, key -> {
            throw new IllegalArgumentException("Unknown type: " + name);
        });
    }

    public Type<?> getType(final DSL.TypeReference type)
    {
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

    public TypeTemplate resolveTemplate(final String name)
    {
        return TYPE_TEMPLATES.getOrDefault(name, () -> {
            throw new IllegalArgumentException("Unknown type: " + name);
        }).get();
    }

    public TypeTemplate id(final String name)
    {
        final int id = RECURSIVE_TYPES.getOrDefault(name, -1);
        if (id != -1)
        {
            return DSL.id(id);
        }
        return getTemplate(name);
    }

    protected TypeTemplate getTemplate(final String name)
    {
        return DSL.named(name, resolveTemplate(name));
    }

    public Type<?> getChoiceType(final DSL.TypeReference type, final String choiceName)
    {
        final TaggedChoice.TaggedChoiceType<?> choiceType = findChoiceType(type);
        if (!choiceType.types().containsKey(choiceName))
        {
            throw new IllegalArgumentException("Data fixer not registered for: " + choiceName + " in " + type.typeName());
        }
        return choiceType.types().get(choiceName);
    }

    public TaggedChoice.TaggedChoiceType<?> findChoiceType(final DSL.TypeReference type) {
        return getType(type).findChoiceType("id", -1).orElseThrow(() -> new IllegalArgumentException("Not a choice type"));
    }

    public void registerTypes(final Schema schema, final Map<String, Supplier<TypeTemplate>> entityTypes, final Map<String, Supplier<TypeTemplate>> blockEntityTypes) {
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

    public Map<String, Supplier<TypeTemplate>> registerEntities(final Schema schema) {
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

    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(final Schema schema) {
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
    public void registerType(final boolean recursive, final DSL.TypeReference type, final Supplier<TypeTemplate> template)
    {
        if (TYPE_TEMPLATES == null)
            TYPE_TEMPLATES = Maps.newHashMap();

        TYPE_TEMPLATES.put(type.typeName(), template);
        if (recursive && !RECURSIVE_TYPES.containsKey(type.typeName()))
        {
            RECURSIVE_TYPES.put(type.typeName(), RECURSIVE_TYPES.size());
        }
    }

    /**
     * Resets the schema so that the type map can be properly rebuild.
     */
    public void resetSchema() {
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
    public void rebuildSchema(
      final Map<String, Supplier<TypeTemplate>> modEntityTypes,
      final Map<String, Supplier<TypeTemplate>> modBlockEntityTypes
    ) {
        //First grab the parent's additional modded types, if the parent is compatible.
        if (this.parent != null && this.parent instanceof ForgeSchema parentForgeSchema)
        {
            //Compatible parent found, add its modded types.
            this.MODDED_ENTITY_TYPES.putAll(parentForgeSchema.MODDED_ENTITY_TYPES);
            this.MODDED_BLOCK_ENTITY_TYPES.putAll(parentForgeSchema.MODDED_BLOCK_ENTITY_TYPES);
        }

        //Now add our own modded types, and potentially override the data.
        this.MODDED_ENTITY_TYPES.putAll(modEntityTypes);
        this.MODDED_BLOCK_ENTITY_TYPES.putAll(modBlockEntityTypes);

        //Find all types which the event marked as removed.
        final Set<String> removedEntityTypes = this.MODDED_ENTITY_TYPES.keySet().stream().filter(key -> this.MODDED_ENTITY_TYPES.get(key) == null).collect(Collectors.toSet());
        final Set<String> removedBlockEntityTypes = this.MODDED_BLOCK_ENTITY_TYPES.keySet().stream().filter(key -> this.MODDED_BLOCK_ENTITY_TYPES.get(key) == null).collect(Collectors.toSet());

        //And remove those from the type maps.
        removedEntityTypes.forEach(this.MODDED_ENTITY_TYPES::remove);
        removedBlockEntityTypes.forEach(this.MODDED_BLOCK_ENTITY_TYPES::remove);
        
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
