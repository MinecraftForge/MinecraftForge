package net.minecraftforge.debug;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.command.ForgeCommands;
import net.minecraftforge.command.ModCommand;
import net.minecraftforge.fml.common.Mod;

@Mod("command_test")
@Mod.EventBusSubscriber
public class CommandTest implements ModCommand {

	public CommandTest() {
		ForgeCommands.addCommand(this);
	}

	@Override
	public void register(CommandDispatcher<CommandSource> dispatcher, boolean isDedicatedServer) {
		dispatcher.register(
				Commands.literal("test_command")
						.executes((context) -> {
							System.err.println("Hello World");
							return 0;
						}));
	}
}
