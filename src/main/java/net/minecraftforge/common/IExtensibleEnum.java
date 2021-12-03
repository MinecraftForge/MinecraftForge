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

package net.minecraftforge.common;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;

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
     * Use this instead of {@link StringRepresentable#fromEnum(Supplier, Function)} for extensible enums because this not cache the enum values on construction
     */
    static <E extends Enum<E> & StringRepresentable> Codec<E> createCodecForExtensibleEnum(Supplier<E[]> valuesSupplier, Function<? super String, ? extends E> enumValueFromNameFunction) {
        return Codec.either(Codec.STRING, Codec.INT).comapFlatMap(
                either -> either.map(
                        str -> {
                            var val = enumValueFromNameFunction.apply(str);
                            return val != null ? DataResult.success(val) : DataResult.error("Unknown enum value name: " + str);
                        },
                        num -> {
                            var values = valuesSupplier.get();
                            return num >= 0 && num < values.length ? DataResult.success(values[num]) : DataResult.error("Unknown enum id: " + num);
                        }
                ),
                value -> Either.left(value.getSerializedName())
        );
    }
}
