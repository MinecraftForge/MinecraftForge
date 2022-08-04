package net.minecraftforge.datafix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.datafixers.types.templates.TypeTemplate;
import net.minecraftforge.datafix.event.DataFixEvent;

import java.util.Map;
import java.util.function.Supplier;

public class ForgeDSL
{

    private ForgeDSL()
    {
        throw new IllegalStateException("Can not instantiate an instance of: ForgeDSL. This is a utility class");
    }

    public static <K> TaggedChoice<K> taggedChoiceLazy(final Class<? extends Schema> schemaClass, final String name, final Type<K> keyType, final Map<K, Supplier<TypeTemplate>> templates) {
        final Map<K, Supplier<TypeTemplate>> finalValues = DataFixEvent.EventFactory.fireTaggedChoiceFor(schemaClass, name, keyType, templates);
        return DSL.taggedChoiceLazy(name, keyType, finalValues);
    }
}
