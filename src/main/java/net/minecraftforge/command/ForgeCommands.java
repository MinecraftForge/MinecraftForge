package net.minecraftforge.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;

import java.util.ArrayList;
import java.util.List;

public class ForgeCommands {

	private static List<ModCommand> COMMANDS = new ArrayList<>();

	public static void addCommand(ModCommand command) {
		COMMANDS.add(command);
	}

	public static void register(CommandDispatcher<CommandSource> dispatcher, boolean isDedicatedServer) {
		// TODO register commands here
		// 	ideas:
		// 		- look through all classes annotated with @ModCommand and execute the register method
		//  	- create a list of ModCommands and have the user add their command to it

		System.err.println("Registering commands");
		for (ModCommand command : COMMANDS) {
			command.register(dispatcher, isDedicatedServer);
		}
	}
}
