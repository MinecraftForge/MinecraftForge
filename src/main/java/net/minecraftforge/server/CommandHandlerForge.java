package net.minecraftforge.server;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import static net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue.*;
 
/**
 * Helper class for Forge permissions and command handling.
 *
 */
public class CommandHandlerForge {
    
    private static Map<String, String> permMap = new HashMap<String, String>();
    
    /**
     * This is a permissions-aware version of
     * {@link cpw.mods.fml.common.event.FMLServerStartingEvent#registerServerCommand(ICommand)}
     * . You should be using this, and NOT FML's, if you wish to specify a
     * custom permission node
     * 
     * @param your
     *            ICommand
     * @param node
     *            a permission node
     * @param permLevel
     *            the default permission level ({@link RegisteredPermValue})
     */
    public static void registerCommand(ICommand command, String node, RegisteredPermValue permLevel)
    {
        if (permMap.containsKey(command.getCommandName()))
        {
            FMLLog.warning("Command %s registered a duplicate permission!", command);
        }
        permMap.put(command.getCommandName(), node);
        PermissionsManager.registerPermission(node, permLevel);
        CommandHandler ch = (CommandHandler) MinecraftServer.getServer().getCommandManager();
        ch.registerCommand(command);
    }
    
    /**
     * INTERNAL USE ONLY - used by CommandHandler for auto-registration of command permissions
     * Mods should be using registerCommand(ICommand, String, RegisteredPermValue)
     * @param cmd
     */
    public static void doPermissionReg(ICommand cmd)
    {
        if (permMap.containsKey(cmd.getCommandName()))return;
        
        String node = cmd.getClass().getName().startsWith("net.minecraft.command") ? "mc." : "cmd." + cmd.getCommandName();

        RegisteredPermValue value;
        if (cmd instanceof CommandBase){
            int level = ((CommandBase) cmd).getRequiredPermissionLevel();
            
            switch (level)
            {
            case 0: 
                value = TRUE;
                break;
            case 1:
            case 2:
            case 3:
            case 4:
                value = OP;
                break;
            default:
                value = FALSE;
            }
        }
        else
            value = OP;
        permMap.put(cmd.getCommandName(), "mc." + cmd.getCommandName());
        PermissionsManager.registerPermission("mc." + cmd.getCommandName(), value);
    }
    
    /**
     * INTERNAL USE ONLY - used by CommandHandler to check usage permissions
     * If you need to check permissions for non-command objects, or additional permissions for commands,
     * call PermissionsManager directly.
     */
    public static boolean canUse(ICommand command, ICommandSender sender)
    {
        if (sender instanceof EntityPlayerMP && permMap.get(command.getCommandName()) != null)
        {
            return PermissionsManager.checkPermission((EntityPlayerMP) sender, permMap.get(command.getCommandName()));
        }
        else return command.canCommandSenderUseCommand(sender);
    }

}