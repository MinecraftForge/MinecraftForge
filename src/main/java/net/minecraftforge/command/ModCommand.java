package net.minecraftforge.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;

@FunctionalInterface
public interface ModCommand {
	void register(CommandDispatcher<CommandSource> dispatcher, boolean isDedicatedServer);
}
