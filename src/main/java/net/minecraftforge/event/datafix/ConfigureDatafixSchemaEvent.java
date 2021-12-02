package net.minecraftforge.event.datafix;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.fixes.References;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class ConfigureDatafixSchemaEvent extends Event implements IModBusEvent
{

    private final Schema schema;

    private final Map<String, Supplier<TypeTemplate>> entityTypes = Maps.newConcurrentMap();
    private final Map<String, Supplier<TypeTemplate>> blockEntityTypes = Maps.newConcurrentMap();

    private final Map<String, Supplier<TypeTemplate>> entityTypesView = Collections.unmodifiableMap(entityTypes);
    private final Map<String, Supplier<TypeTemplate>> blockEntityTypesView = Collections.unmodifiableMap(blockEntityTypes);

    public ConfigureDatafixSchemaEvent(final Schema schema)
    {
        this.schema = schema;
    }

    public Schema getSchema()
    {
        return schema;
    }

    public int getVersion() {
        return getSchema().getVersionKey();
    }

    public void registerSimpleBlockEntity(final ResourceLocation registryName) {
        registerBlockEntity(registryName, DSL::remainder);
    }

    public void registerSimpleItemInventory(final ResourceLocation registryName, final String nbtKey) {
        registerBlockEntity(registryName, () -> DSL.optionalFields(nbtKey, DSL.list(References.ITEM_STACK.in(getSchema()))));
    }

    public void registerBlockEntity(final ResourceLocation registryName, final Function<String, TypeTemplate> template) {
        registerBlockEntity(registryName, () -> template.apply(registryName.toString()));
    }

    public void registerBlockEntity(final ResourceLocation registryName, final Supplier<TypeTemplate> template) {
        blockEntityTypes.put(registryName.toString(), template);
    }

    public void registerSimpleEntity(final ResourceLocation registryName) {
        registerEntity(registryName, DSL::remainder);
    }

    public void registerEntity(final ResourceLocation registryName, final Function<String, TypeTemplate> template) {
        registerEntity(registryName, () -> template.apply(registryName.toString()));
    }

    public void registerEntity(final ResourceLocation registryName, final Supplier<TypeTemplate> template) {
        entityTypes.put(registryName.toString(), template);
    }

    public Map<String, Supplier<TypeTemplate>> getEntityTypes()
    {
        return entityTypesView;
    }

    public Map<String, Supplier<TypeTemplate>> getBlockEntityTypes()
    {
        return blockEntityTypesView;
    }
}
