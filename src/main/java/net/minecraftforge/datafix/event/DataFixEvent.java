package net.minecraftforge.datafix.event;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TypeTemplate;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.Collections;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class DataFixEvent extends Event implements IModBusEvent
{
    public static final class EventFactory {

        public static <K> Map<K, Supplier<TypeTemplate>> fireTaggedChoiceFor(final Class<? extends Schema> schemaClass, final String name, final Type<K> keyType, final Map<K, Supplier<TypeTemplate>> templates) {
            final ConfigureTaggedChoice<K> event = new ConfigureTaggedChoice<>(schemaClass, name, keyType, templates);

            ModLoader.get().postEventWithWrapInModOrder(
                    event,
                    (mc, e) -> ModLoadingContext.get().setActiveContainer(mc),
                    (mc, e) -> ModLoadingContext.get().setActiveContainer(null)
            );

            return event.build();
        }
    }

    public static class SchemaConstruction extends DataFixEvent {
        private final Class<? extends Schema> schemaClass;

        public SchemaConstruction(final Class<? extends Schema> schemaClass) {this.schemaClass = schemaClass;}

        public Class<? extends Schema> getSchemaClass()
        {
            return schemaClass;
        }
    }

    public static class ConfigureTaggedChoice<K> extends SchemaConstruction {
        private final String name;
        private final Type<K> keyType;
        private final Map<K, Supplier<TypeTemplate>> vanillaChoices;
        private final Map<K, Supplier<TypeTemplate>> modChoices = Maps.newConcurrentMap();
        private final Map<K, Supplier<TypeTemplate>> modChoicesView = Collections.unmodifiableMap(modChoices);

        public ConfigureTaggedChoice(final Class<? extends Schema> schemaClass, final String name, final Type<K> keyType , final Map<K, Supplier<TypeTemplate>> vanillaChoices)
        {
            super(schemaClass);
            this.name = name;
            this.keyType = keyType;
            this.vanillaChoices = Collections.unmodifiableMap(vanillaChoices);
        }

        public String getName()
        {
            return name;
        }

        public Type<K> getKeyType()
        {
            return keyType;
        }

        public Map<K, Supplier<TypeTemplate>> getVanillaChoices()
        {
            return vanillaChoices;
        }

        public Map<K, Supplier<TypeTemplate>> getModChoices()
        {
            return modChoicesView;
        }

        public ConfigureTaggedChoice<K> register(final K name, final Supplier<TypeTemplate> builder) {
            this.modChoices.put(name, builder);
            return this;
        }

        private Map<K, Supplier<TypeTemplate>> build() {
            final ImmutableMap.Builder<K, Supplier<TypeTemplate>> builder = ImmutableMap.builder();
            builder.putAll(vanillaChoices);
            builder.putAll(modChoices);
            return builder.build();
        }
    }
}
