package org.bukkit.craftbukkit.command;

import org.bukkit.command.RemoteConsoleCommandSender;

public class CraftRemoteConsoleCommandSender extends ServerCommandSender implements RemoteConsoleCommandSender {
    public CraftRemoteConsoleCommandSender() {
        super();
    }

    public void sendMessage(String message) {
        net.minecraft.network.rcon.RConConsoleSource.consoleBuffer.sendChatToPlayer(net.minecraft.util.ChatMessageComponent.createFromText(message + "\n")); // Send a newline after each message, to preserve formatting.
    }

    public void sendMessage(String[] messages) {
        for (String message : messages) {
            sendMessage(message);
        }
    }

    public String getName() {
        return "Rcon";
    }

    public boolean isOp() {
        return true;
    }

    public void setOp(boolean value) {
        throw new UnsupportedOperationException("Cannot change operator status of remote controller.");
    }
}
