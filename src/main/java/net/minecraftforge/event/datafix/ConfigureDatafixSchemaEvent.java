package net.minecraftforge.event.datafix;

import com.google.common.collect.Maps;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.Map;
import java.util.function.Supplier;

public class ConfigureDatafixSchemaEvent extends Event implements IModBusEvent
{

    private final Schema schema;

    private final Map<String, Supplier<TypeTemplate>> entityTypes = Maps.newConcurrentMap();
    private final Map<String, Supplier<TypeTemplate>> blockEntityTypes = Maps.newConcurrentMap();

    public ConfigureDatafixSchemaEvent(final Schema schema)
    {
        this.schema = schema;
    }

    public Schema getSchema()
    {
        return schema;
    }

    public Map<String, Supplier<TypeTemplate>> getEntityTypes()
    {
        return entityTypes;
    }

    public Map<String, Supplier<TypeTemplate>> getBlockEntityTypes()
    {
        return blockEntityTypes;
    }
}
