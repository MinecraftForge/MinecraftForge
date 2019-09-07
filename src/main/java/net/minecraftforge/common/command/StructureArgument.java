package net.minecraftforge.common.command;

import com.google.common.collect.Streams;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.registries.GameData;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

public class StructureArgument implements ArgumentType<ResourceLocation>
{
    private static final Collection<String> EXAMPLES = Arrays.asList("minecraft:village", "Mansion");
    public static final DynamicCommandExceptionType STRUCTURE_UNKNOWN_TYPE = new DynamicCommandExceptionType((p_211367_0_) ->
    {
        return new TranslationTextComponent("structure.notFound", p_211367_0_);
    });

    public static StructureArgument structure()
    {
        return new StructureArgument();
    }

    public static SuggestionProvider<CommandSource> suggestions()
    {
        return (ctx, sb) -> ISuggestionProvider.suggest(Streams.concat(
                GameData.getStructureMap().values().stream().map(Structure::getStructureName),
                GameData.getStructureFeatures().keySet().stream().map(ResourceLocation::toString)), sb);
    }

    public static ResourceLocation getStructureId(CommandContext<CommandSource> context, String name) throws CommandSyntaxException
    {
        return checkIfStructureExists(context.getArgument(name, ResourceLocation.class));
    }

    private static ResourceLocation checkIfStructureExists(ResourceLocation id) throws CommandSyntaxException
    {
        if (id.getNamespace().equals("minecraft"))
        {
            // Special case vanilla hardcoded names, for vanilla compat
            Structure<?> structure = GameData.getStructureMap().values().stream()
                    .filter(s -> s.getStructureName().equalsIgnoreCase(id.getPath()))
                    .findFirst()
                    .orElse(null);
            if (structure != null)
                return id;
        }
        GameData.getStructureFeatures().getValue(id).orElseThrow(() -> STRUCTURE_UNKNOWN_TYPE.create(id));
        return id;
    }
    
    private static final SimpleCommandExceptionType INVALID_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("argument.id.invalid"));

    public ResourceLocation parse(StringReader reader) throws CommandSyntaxException
    {
        // Logic taken from ResourceLocation.read, but made case-insensitive
        int cursor = reader.getCursor();
        try
        {
            while (reader.canRead() && ResourceLocation.isValidPathCharacter(Character.toLowerCase(reader.peek())))
            {
                reader.skip();
            }

            String s = reader.getString().substring(cursor, reader.getCursor());
            ResourceLocation rl = new ResourceLocation(s.toLowerCase(Locale.ROOT));
            return checkIfStructureExists(rl);
        }
        catch (ResourceLocationException e)
        {
            reader.setCursor(cursor);
            throw INVALID_EXCEPTION.createWithContext(reader);
        }
    }

    public Collection<String> getExamples()
    {
        return EXAMPLES;
    }
}
