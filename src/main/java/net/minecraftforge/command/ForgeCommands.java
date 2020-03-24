package net.minecraftforge.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class ForgeCommands {

	private static final Logger LOGGER = LogManager.getLogger();

	private static List<ModCommand> COMMANDS = new ArrayList<>();

	public static void addCommand(ModCommand command) {
		COMMANDS.add(command);
	}

	public static void register(CommandDispatcher<CommandSource> dispatcher, boolean isDedicatedServer) {
		// TODO register commands here
		// 	ideas:
		// 		- look through all classes annotated with @ModCommand and execute the register method
		//  	- create a list of ModCommands and have the user add their command to it

		LOGGER.info("Registering commands");
		for (ModCommand command : COMMANDS) {
			command.register(dispatcher, isDedicatedServer);
			LOGGER.info("Registered command " + command.getClass());
		}
	}
}
