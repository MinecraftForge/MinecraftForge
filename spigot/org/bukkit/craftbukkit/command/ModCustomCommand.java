package org.bukkit.craftbukkit.command;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

public class ModCustomCommand extends Command {

    public ModCustomCommand(String name)
    {
        super(name);
    }

    public ModCustomCommand(String name, String description, String usageMessage, List<String> aliases)
    {
        super(name, description, usageMessage, aliases);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        // Dummy method
        return false;
    }
}