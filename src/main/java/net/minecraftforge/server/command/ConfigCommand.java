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
                    requires(cs->cs.hasPermission(0)).
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
                context.getSource().sendSuccess(new TranslationTextComponent("commands.config.getwithtype",
                        modId, type,
                        new StringTextComponent(f.getName()).withStyle(TextFormatting.UNDERLINE).
                        withStyle((style) -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, f.getAbsolutePath())))
                ), true);
            } else {
                context.getSource().sendSuccess(new TranslationTextComponent("commands.config.noconfig", modId, type),
                        true);
            }
            return 0;
        }
    }
}
