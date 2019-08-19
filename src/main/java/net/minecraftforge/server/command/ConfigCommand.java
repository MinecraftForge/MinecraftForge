package net.minecraftforge.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;

import java.io.File;

public class ConfigCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("config").
                        then(ShowFile.register())
        );
    }

    public static class ShowFile {
        static ArgumentBuilder<CommandSource, ?> register() {
            return Commands.literal("showfile").
                    requires(cs->cs.hasPermissionLevel(0)).
                    then(Commands.argument("mod", ModIdArgument.modIdArgument()).
                        then(Commands.argument("type", EnumArgument.enumArgument(ModConfig.Type.class)).
                            executes(ShowFile::showFile)
                        )
                    );
        }

        private static int showFile(final CommandContext<CommandSource> context) {
            final String modId = context.getArgument("mod", String.class);
            final ModConfig.Type type = context.getArgument("type", ModConfig.Type.class);
            final String configFileName = ConfigTracker.INSTANCE.getConfigFileName(modId, type);
            if (configFileName != null) {
                File f = new File(configFileName);
                context.getSource().sendFeedback(new TranslationTextComponent("commands.config.getwithtype",
                        modId, type,
                        new StringTextComponent(f.getName()).applyTextStyle(TextFormatting.UNDERLINE).
                                applyTextStyle((style) -> style.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, f.getAbsolutePath())))
                ), true);
            } else {
                context.getSource().sendFeedback(new TranslationTextComponent("commands.config.noconfig", modId, type),
                        true);
            }
            return 0;
        }
    }
}
