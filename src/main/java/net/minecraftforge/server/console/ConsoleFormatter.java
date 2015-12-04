package net.minecraftforge.server.console;

import java.util.Map;
import java.util.regex.Pattern;

import org.fusesource.jansi.Ansi;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;

import net.minecraft.util.EnumChatFormatting;

public final class ConsoleFormatter implements Function<String, String>
{

    public ConsoleFormatter()
    {
    }

    private static final String RESET = Ansi.ansi().reset().toString();

    private static final ImmutableMap<Pattern, String> REPLACEMENTS = ImmutableMap.<Pattern, String> builder()
            .put(compile(EnumChatFormatting.BLACK), Ansi.ansi().reset().fg(Ansi.Color.BLACK).boldOff().toString())
            .put(compile(EnumChatFormatting.DARK_BLUE), Ansi.ansi().reset().fg(Ansi.Color.BLUE).boldOff().toString())
            .put(compile(EnumChatFormatting.DARK_GREEN), Ansi.ansi().reset().fg(Ansi.Color.GREEN).boldOff().toString())
            .put(compile(EnumChatFormatting.DARK_AQUA), Ansi.ansi().reset().fg(Ansi.Color.CYAN).boldOff().toString())
            .put(compile(EnumChatFormatting.DARK_RED), Ansi.ansi().reset().fg(Ansi.Color.RED).boldOff().toString())
            .put(compile(EnumChatFormatting.DARK_PURPLE), Ansi.ansi().reset().fg(Ansi.Color.MAGENTA).boldOff().toString())
            .put(compile(EnumChatFormatting.GOLD), Ansi.ansi().reset().fg(Ansi.Color.YELLOW).boldOff().toString())
            .put(compile(EnumChatFormatting.GRAY), Ansi.ansi().reset().fg(Ansi.Color.WHITE).boldOff().toString())
            .put(compile(EnumChatFormatting.DARK_GRAY), Ansi.ansi().reset().fg(Ansi.Color.BLACK).bold().toString())
            .put(compile(EnumChatFormatting.BLUE), Ansi.ansi().reset().fg(Ansi.Color.BLUE).bold().toString())
            .put(compile(EnumChatFormatting.GREEN), Ansi.ansi().reset().fg(Ansi.Color.GREEN).bold().toString())
            .put(compile(EnumChatFormatting.AQUA), Ansi.ansi().reset().fg(Ansi.Color.CYAN).bold().toString())
            .put(compile(EnumChatFormatting.RED), Ansi.ansi().reset().fg(Ansi.Color.RED).bold().toString())
            .put(compile(EnumChatFormatting.LIGHT_PURPLE), Ansi.ansi().reset().fg(Ansi.Color.MAGENTA).bold().toString())
            .put(compile(EnumChatFormatting.YELLOW), Ansi.ansi().reset().fg(Ansi.Color.YELLOW).bold().toString())
            .put(compile(EnumChatFormatting.WHITE), Ansi.ansi().reset().fg(Ansi.Color.WHITE).bold().toString())
            .put(compile(EnumChatFormatting.OBFUSCATED), Ansi.ansi().a(Ansi.Attribute.BLINK_SLOW).toString())
            .put(compile(EnumChatFormatting.BOLD), Ansi.ansi().a(Ansi.Attribute.UNDERLINE_DOUBLE).toString())
            .put(compile(EnumChatFormatting.STRIKETHROUGH), Ansi.ansi().a(Ansi.Attribute.STRIKETHROUGH_ON).toString())
            .put(compile(EnumChatFormatting.UNDERLINE), Ansi.ansi().a(Ansi.Attribute.UNDERLINE).toString())
            .put(compile(EnumChatFormatting.ITALIC), Ansi.ansi().a(Ansi.Attribute.ITALIC).toString())
            .put(compile(EnumChatFormatting.RESET), RESET)
            .build();

    private static Pattern compile(EnumChatFormatting formatting)
    {
        return Pattern.compile(formatting.toString(), Pattern.LITERAL | Pattern.CASE_INSENSITIVE);
    }

    @Override
    public String apply(String text)
    {
        for (Map.Entry<Pattern, String> entry : REPLACEMENTS.entrySet())
        {
            text = entry.getKey().matcher(text).replaceAll(entry.getValue());
        }

        return text + RESET;
    }

}
