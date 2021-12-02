package net.minecraftforge.event.datafix;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.types.templates.TypeTemplate;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class ConfigureDatafixSchemaEvent extends Event implements IModBusEvent
{
    private final int version;

    private final Map<String, Supplier<TypeTemplate>> entityTypes = Maps.newConcurrentMap();
    private final Map<String, Supplier<TypeTemplate>> blockEntityTypes = Maps.newConcurrentMap();

    public ConfigureDatafixSchemaEvent(final int version)
    {
        this.version = version;
    }

    public int getVersion()
    {
        return version;
    }

    public Map<String, Supplier<TypeTemplate>> getEntityTypes()
    {
        return entityTypes;
    }

    public Map<String, Supplier<TypeTemplate>> getBlockEntityTypes()
    {
        return blockEntityTypes;
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
}
