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

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("config").
                        then(ShowFiles.register())
        );
    }

    public static class ShowFiles {
        static ArgumentBuilder<CommandSource, ?> register() {
            return Commands.literal("showfile").
                    requires(cs->cs.hasPermissionLevel(0)).
                    then(Commands.argument("mod", ModIdArgument.modIdArgument()).
                        then(Commands.argument("type", EnumArgument.enumArgument(ModConfig.Type.class)).
                            executes(ShowFiles::showFiles)
                        )
                    );
        }

        private static int showFiles(final CommandContext<CommandSource> context) {
            final String modId = context.getArgument("mod", String.class);
            final ModConfig.Type type = context.getArgument("type", ModConfig.Type.class);
            final List<String> configFileNames = ConfigTracker.INSTANCE.getConfigFileNames(modId, type);
            ITextComponent fileNames = configFileNames.stream()
                .map(File::new)
                .map(file -> new StringTextComponent(file.getName()).func_240700_a_((style) -> style.func_240715_a_(new ClickEvent(ClickEvent.Action.OPEN_FILE, file.getAbsolutePath()))))
                .map(component -> component.func_240701_a_(TextFormatting.UNDERLINE))
                .reduce(IFormattableTextComponent::func_230529_a_)
                .orElse(null);
            if (fileNames != null) {
                ITextComponent message = new TranslationTextComponent("commands.config.getwithtype", modId, type, fileNames);
                context.getSource().sendFeedback(message, true);
            } else {
                context.getSource().sendFeedback(new TranslationTextComponent("commands.config.noconfig", modId, type),
                        true);
            }
            return 0;
        }
    }
}
