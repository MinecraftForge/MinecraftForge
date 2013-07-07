package org.bukkit.craftbukkit.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class TicksPerSecondCommand extends Command {

    public TicksPerSecondCommand(String name) {
        super(name);
        this.description = "Gets the current ticks per second for the server";
        this.usageMessage = "/tps";
        this.setPermission("bukkit.command.tps");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;

        double tps = Math.min(20,  Math.round(net.minecraft.server.MinecraftServer.currentTPS * 10) / 10.0);
        ChatColor color;
        if (tps > 19.2D) {
            color = ChatColor.GREEN;
        } else if (tps > 17.4D) {
            color = ChatColor.YELLOW;
        } else {
            color = ChatColor.RED;
        }

        sender.sendMessage(ChatColor.GOLD + "[TPS] " + color + tps);

        return true;
    }
}
