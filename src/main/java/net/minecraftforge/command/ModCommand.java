package net.minecraftforge.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;

public interface ModCommand {
	void register(CommandDispatcher<CommandSource> dispatcher, boolean isDedicatedServer);
}
