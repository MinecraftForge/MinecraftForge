package net.minecraftforge.permissions;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

public interface IPermissionHandler {

    /**
     * Check if a permission is allowed
     * <p>
     * <b>The EntityPlayer instance may be a
     * {@link net.minecraftforge.common.util.FakePlayer FakePlayer}</b>
     * 
     * @param perm
     *            the perm node
     * @param player
     *            the EntityPlayer
     * @return if the permission is allowed
     */
    public boolean checkPerm(String perm, EntityPlayer player);

    /**
     * Register a permission with its default value
     * 
     * @param perm
     *            the perm node
     * @param defaultValue
     *            the default value
     */
    public void regiserPerm(String perm, DefaultValue defaultValue);

    /**
     * Check if a command is allowed
     * <p>
     * Commands are handled here instead of
     * {@link #checkPerm(String, EntityPlayer)}
     * 
     * @param command
     *            the command
     * @param sender
     *            the command sender
     * @return if the command is allowed
     */
    public boolean checkCommand(ICommand command, ICommandSender sender);

    /**
     * Callback for when a command is registered
     * 
     * @param command
     *            the command
     */
    public void registerCommand(ICommand command);
}
