package net.minecraft.src.forge;

public interface IChatHandler
{
	/**
	 * Process (by wordfiltering, etc.) a chat message from the specified user
	 * @param original The original chat message
	 * @param username The username sending the chat message
	 * @return A modified chat message or null to drop message
	 */
	public String processChat(String original, String username);
}
