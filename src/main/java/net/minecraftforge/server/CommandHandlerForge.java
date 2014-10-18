package net.minecraftforge.server;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;
 
/**
 * Helper class for Forge permissions.
 *
 */
public class CommandHandlerForge {
    
    private static Map<String, String> permMap = new HashMap<String, String>();
    
    /**
     * Mods, use this! Call this in your constructor.
     * For usage examples, see CommandHandlerForge.
     * @param command name of your command (ICommand.getCommandName)
     * @param node a permission node
     * @param permLevel
     */
    public static void doPermissionReg(String command, String node, RegisteredPermValue permLevel)
    {
        if (permMap.containsKey(command))
        {
            FMLLog.warning("Command %s tried to register a duplicate permission!", command);
            return;
        }
        permMap.put(command, node);
        PermissionsManager.registerPermission(node, permLevel);
    }
    
    /**
     * INTERNAL USE ONLY - used by CommandHandler for auto-registration of commands
     * Mods should be using doPermissionReg(String, String, RegisteredPermValue)
     * @param cmd
     */
    public static void doPermissionReg(ICommand cmd)
    {
        if (cmd.getClass().getName().startsWith("net.minecraft.command") && cmd instanceof CommandBase)
        {
            int level = ((CommandBase) cmd).getRequiredPermissionLevel();
            
            switch (level)
            {
            case 0: 
                doPermissionReg(cmd.getCommandName(), "mc." + cmd.getCommandName(), RegisteredPermValue.TRUE);
                break;
            
            case 1:
            case 2:
            case 3:
            case 4:
                doPermissionReg(cmd.getCommandName(), "mc." + cmd.getCommandName(), RegisteredPermValue.OP);
                break;
            default:
                doPermissionReg(cmd.getCommandName(), "mc." + cmd.getCommandName(), RegisteredPermValue.FALSE);
            }
        }
        else if (!permMap.containsKey(cmd.getCommandName())) 
            doPermissionReg(cmd.getCommandName(), "cmd." + cmd.getCommandName(), RegisteredPermValue.OP);
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
            return PermissionsManager.checkPerm((EntityPlayerMP) sender, permMap.get(command.getCommandName()));
        }
        else return command.canCommandSenderUseCommand(sender);
    }

 
}