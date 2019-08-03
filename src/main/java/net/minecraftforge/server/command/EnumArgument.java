package net.minecraftforge.server.command;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.IArgumentSerializer;
import net.minecraft.network.PacketBuffer;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumArgument<T extends Enum<T>>  implements ArgumentType<T> {
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

    public static class Serializer<T extends Enum<T>> implements IArgumentSerializer<EnumArgument<T>> {

        @Override
        public void write(EnumArgument argument, PacketBuffer buffer) {
            buffer.writeString(argument.enumClass.getName());
        }

        @Override
        public EnumArgument read(PacketBuffer buffer) {
            String className = buffer.readString();
            try {
                return enumArgument((Class<T>) Class.forName(className));
            } catch (Exception e) {
                throw new RuntimeException("Failed to read EnumArgument for enum class " + className);
            }
        }

        @Override
        public void write(EnumArgument argument, JsonObject jsonObject) {
            jsonObject.addProperty("enumClass", argument.enumClass.getName());
        }

    }
}
