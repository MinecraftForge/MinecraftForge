package net.minecraft.src.forge;

import net.minecraft.src.EntityPlayer;

public interface IChatHandler 
{
    /**
     * Called when a player sends a normal chat message to the server,
     * you can manipulate the message parameter by returning a modified 
     * version from this function.
     * 
     * This is only called on the server side.
     * 
     * @param player The player issuing the message
     * @param message The message the player is sending
     * @return The message to be displayed, normal case is the 'message' parameter. Return null to cancel displaying this message.
     */
    public String onServerChat(EntityPlayer player, String message);
    
    /**
     * Called when a player sends a normal chat message to the server
     * that starts with a '/'.
     * 
     * This is only called on the server side.
     * 
     * Return true from this function to indicate that you have 
     * handled the command and no further processing is necessary.
     * 
     * @param player The player trying to issue the command
     * @param isOp True if the player is on the Op list 
     * @param command The command trying to be issued
     * @return True if no further processing is necessary, false to continue processing.
     */
    public boolean onChatCommand(EntityPlayer player, boolean isOp, String command);
    
    /**
     * Called when either the console or a player issues a / command that is not handled elsewhere.
     * 
     * This is only called on the server side.
     * 
     * Return true from this function to indicate that you have 
     * handled the command and no further processing is necessary.
     * 
     * The listener will always be a instance of ICommandListener, but because the client does 
     * not have this class it is defined as a Object to allow client compilation.
     * 
     * @param listener The source of the command, will always be a instance of ICommandListener
     * @param username The username of the person issuing the command, 'CONSOLE' if it's not a player.
     * @param command The command trying to be issued
     * @return True if no further processing is necessary, false to continue processing.
     */
    public boolean onServerCommand(Object listener, String username, String command);
    
    /**
     * 
     * Called when either the console or a player issues the /say command
     * 
     * This is only called on the server side.
     * 
     * @param listener The source of the command, will always be a instance of ICommandListener
     * @param username The username of the person issuing the command, 'CONSOLE' if it's not a player.
     * @param message The message trying to be sent, without the /say
     * @return The message to be displayed, normal case is the 'message' parameter. Return null to cancel displaying this message.
     */
    public String onServerCommandSay(Object listener, String username, String message);
    
    /**
     * Called when the client receives a Chat packet.
     * 
     * This is only called on the client side
     * 
     * @param message The chat message received
     * @return The message to be displayed, normal case is the 'message' parameter. Return null to cancel displaying this message.
     */
    public String onClientChatRecv(String message);
}
