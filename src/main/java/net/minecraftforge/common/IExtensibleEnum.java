/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common;

import com.mojang.serialization.Codec;
import net.minecraft.util.IStringSerializable;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * To be implemented on vanilla enums that should be enhanced with ASM to be
 * extensible. If this is implemented on a class, the class must define a static
 * method called "create" which takes a String (enum name), and the rest of the
 * parameters matching a constructor.
 * <p>
 * For example, an enum with the constructor {@code MyEnum(Object foo)} would
 * require the method:
 * 
 * <pre>
 * public static MyEnum create(String name, Object foo)
 * {
 *     throw new IllegalStateException("Enum not extended");
 * }
 * </pre>
 * 
 * The method contents will be replaced with ASM at runtime. Multiple
 * {@code create} methods <strong>can</strong> be defined as long as each
 * matches a constructor.
 */
public interface IExtensibleEnum
{
    /**
     * Called by generated factory code to do any post-constructor setup required by
     * the enum. Should not be called manually.
     */
    @Deprecated
    default void init() {}

    /**
     * Use this instead of {@link IStringSerializable#createEnumCodec(Supplier, Function)} for extensible enums because this not cache the enum values on construction
     */
    static <E extends Enum<E> & IStringSerializable> Codec<E> createCodecForExtensibleEnum(Supplier<E[]> valuesSupplier, Function<? super String, ? extends E> enumValueFromNameFunction) {
        return IStringSerializable.fromStringResolver(Enum::ordinal, (id) -> valuesSupplier.get()[id], enumValueFromNameFunction);
    }
}
