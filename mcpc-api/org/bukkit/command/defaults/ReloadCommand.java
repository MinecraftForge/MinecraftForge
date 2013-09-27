package org.bukkit.command.defaults;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends BukkitCommand {
    public ReloadCommand(String name) {
        super(name);
        this.description = "Reloads the server configuration and plugins";
        this.usageMessage = "/reload";
        this.setPermission("bukkit.command.reload");
        this.setAliases(Arrays.asList("rl"));
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        // MCPC+ - disable
        /*
        if (!testPermission(sender)) return true;

        org.spigotmc.CustomTimingsHandler.reload(); // Spigot: TODO: Why is this here?
        Bukkit.reload();
        Command.broadcastCommandMessage(sender, ChatColor.GREEN + "Reload complete.");
        */
        sender.sendMessage(ChatColor.RED + "Reload not allowed on an MCPC+ server.");
        // MCPC+ end

        return true;
    }
}
