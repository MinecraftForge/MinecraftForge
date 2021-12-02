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
import it.unimi.dsi.fastutil.objects.Object2IntMap;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A partial carbon copy of the {@link Schema} class.
 * But this allows us to delay the setup of the type hierarchy until we need it.
 */
class ForgeSchema extends Schema
{
    //Can not be final, needs to be initialized on the fly!
    private Map<String, Supplier<TypeTemplate>> TYPE_TEMPLATES = Maps.newHashMap();
    private Map<String, Type<?>> TYPES = Maps.newHashMap();

    private final String name;
    private final Schema wrapped;

    ForgeSchema(final int versionKey, final Schema parent, final Schema wrapped)
    {
        super(versionKey, parent);
        this.wrapped = wrapped;

        final int subVersion = DataFixUtils.getSubVersion(versionKey);
        name = "V" + DataFixUtils.getVersion(versionKey) + (subVersion == 0 ? "" : "." + subVersion);

        resetSchema();
        rebuildSchema(
          Collections.emptyMap(),
          Collections.emptyMap()
        );
    }

    protected Map<String, Type<?>> buildTypes() {
        final Map<String, Type<?>> types = Maps.newHashMap();

        final List<TypeTemplate> templates = Lists.newArrayList();

        for (final Object2IntMap.Entry<String> entry : RECURSIVE_TYPES.object2IntEntrySet()) {
            templates.add(DSL.check(entry.getKey(), entry.getIntValue(), getTemplate(entry.getKey())));
        }

        final Optional<TypeTemplate> availableTypeTemplate = templates.stream().reduce(DSL::or);
        if (availableTypeTemplate.isEmpty()) {
            return Maps.newHashMap();
        }
        final TypeTemplate choice = availableTypeTemplate.get();
        final TypeFamily family = new RecursiveTypeFamily(name, choice);

        for (final String name : TYPE_TEMPLATES.keySet()) {
            final Type<?> type;
            final int recurseId = RECURSIVE_TYPES.getOrDefault(name, -1);
            if (recurseId != -1) {
                type = family.apply(recurseId);
            } else {
                type = getTemplate(name).apply(family).apply(-1);
            }
            types.put(name, type);
        }
        return types;
    }

    public Set<String> types() {
        return TYPES.keySet();
    }

    public Type<?> getTypeRaw(final DSL.TypeReference type) {
        final String name = type.typeName();
        return TYPES.computeIfAbsent(name, key -> {
            throw new IllegalArgumentException("Unknown type: " + name);
        });
    }

    public Type<?> getType(final DSL.TypeReference type) {
        final String name = type.typeName();
        final Type<?> type1 = TYPES.computeIfAbsent(name, key -> {
            throw new IllegalArgumentException("Unknown type: " + name);
        });
        if (type1 instanceof RecursivePoint.RecursivePointType<?>) {
            return type1.findCheckedType(-1).orElseThrow(() -> new IllegalStateException("Could not find choice type in the recursive type"));
        }
        return type1;
    }

    public TypeTemplate resolveTemplate(final String name) {
        return TYPE_TEMPLATES.getOrDefault(name, () -> {
            throw new IllegalArgumentException("Unknown type: " + name);
        }).get();
    }

    public TypeTemplate id(final String name) {
        final int id = RECURSIVE_TYPES.getOrDefault(name, -1);
        if (id != -1) {
            return DSL.id(id);
        }
        return getTemplate(name);
    }

    protected TypeTemplate getTemplate(final String name) {
        return DSL.named(name, resolveTemplate(name));
    }

    public Type<?> getChoiceType(final DSL.TypeReference type, final String choiceName) {
        final TaggedChoice.TaggedChoiceType<?> choiceType = findChoiceType(type);
        if (!choiceType.types().containsKey(choiceName)) {
            throw new IllegalArgumentException("Data fixer not registered for: " + choiceName + " in " + type.typeName());
        }
        return choiceType.types().get(choiceName);
    }

    public TaggedChoice.TaggedChoiceType<?> findChoiceType(final DSL.TypeReference type) {
        return getType(type).findChoiceType("id", -1).orElseThrow(() -> new IllegalArgumentException("Not a choice type"));
    }

    public void registerTypes(final Schema schema, final Map<String, Supplier<TypeTemplate>> entityTypes, final Map<String, Supplier<TypeTemplate>> blockEntityTypes) {
        if (wrapped != null)
            wrapped.registerTypes(schema, entityTypes, blockEntityTypes);
        else if (parent != null)
            parent.registerTypes(schema, entityTypes, blockEntityTypes);
    }

    public Map<String, Supplier<TypeTemplate>> registerEntities(final Schema schema) {
        if (wrapped != null)
            return wrapped.registerEntities(schema);
        if (parent != null)
            return parent.registerEntities(schema);

        return Maps.newHashMap();
    }

    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(final Schema schema) {
        if (wrapped != null)
            return wrapped.registerBlockEntities(schema);
        if (parent != null)
            return parent.registerBlockEntities(schema);

        return Maps.newHashMap();
    }

    public void registerSimple(final Map<String, Supplier<TypeTemplate>> map, final String name) {
        register(map, name, DSL::remainder);
    }

    public void register(final Map<String, Supplier<TypeTemplate>> map, final String name, final Function<String, TypeTemplate> template) {
        register(map, name, () -> template.apply(name));
    }

    public void register(final Map<String, Supplier<TypeTemplate>> map, final String name, final Supplier<TypeTemplate> template) {
        map.put(name, template);
    }

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

    public void resetSchema() {
        this.TYPE_TEMPLATES.clear();
        this.TYPES.clear();
        this.RECURSIVE_TYPES.clear();
    }

    public void rebuildSchema(
      final Map<String, Supplier<TypeTemplate>> modEntityTypes,
      final Map<String, Supplier<TypeTemplate>> modBlockEntityTypes
    ) {
        this.TYPE_TEMPLATES.clear();
        this.TYPES.clear();
        this.RECURSIVE_TYPES.clear();

        final Map<String, Supplier<TypeTemplate>> entityTypes = registerEntities(this);
        final Map<String, Supplier<TypeTemplate>> blockEntityTypes = registerBlockEntities(this);

        entityTypes.putAll(modEntityTypes);
        blockEntityTypes.putAll(modBlockEntityTypes);

        registerTypes(this, entityTypes, blockEntityTypes);

        TYPES = buildTypes();
    }
}
