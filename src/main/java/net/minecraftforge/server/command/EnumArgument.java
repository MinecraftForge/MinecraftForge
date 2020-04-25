/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.server.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.ISuggestionProvider;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumArgument<T extends Enum<T>> implements ArgumentType<T> {
    private final Class<T> enumClass;

    public static <R extends Enum<R>> EnumArgument<R> enumArgument(Class<R> enumClass) {
        return new EnumArgument<>(enumClass);
    }
    private EnumArgument(final Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public T parse(final StringReader reader) throws CommandSyntaxException {
        return Enum.valueOf(enumClass, reader.readUnquotedString());
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return ISuggestionProvider.suggest(Stream.of(enumClass.getEnumConstants()).map(Object::toString), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return Stream.of(enumClass.getEnumConstants()).map(Object::toString).collect(Collectors.toList());
    }

    /* JAVAC HATES RAW TYPES!
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static class Serialzier implements IArgumentSerializer<EnumArgument> {
        @Override
        public void write(EnumArgument argument, PacketBuffer buffer) {
            buffer.writeString(argument.enumClass.getName());
        }

        @Override
        public EnumArgument<?> read(PacketBuffer buffer) {
            try {
                String name = buffer.readString();
                return new EnumArgument(Class.forName(name));
            } catch (ClassNotFoundException e) {
                return null;
            }
        }

        @Override
        public void write(EnumArgument argument, JsonObject json) {
            json.addProperty("enum", argument.enumClass.getName());
        }
    }
    */
}
