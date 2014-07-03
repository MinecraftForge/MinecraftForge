package net.minecraftforge.server;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.api.PermissionsManager;
import net.minecraftforge.permissions.api.PermBuilderFactory.PermReg;
import net.minecraftforge.permissions.api.RegisteredPermValue;
 
public class CommandHandlerForge {
    
    private static Map<String, String> permMap = new HashMap<String, String>();
    private static List<PermReg> permRegList = new ArrayList<PermReg>();
    
    /**
     * Mods, use this!
     * @param command name of your command (ICommand.getCommandName)
     * @param node a permission node
     * @param permLevel
     */
    public static void doPermissionReg(String command, String node, RegisteredPermValue permLevel)
    {
        permRegList.add(new PermReg(node, permLevel, null));
        permMap.put(command, node);
    }
    
    // ******** INTERNAL USE ONLY ********
    
    public static void doPermissionReg(ICommand cmd)
    {
        if (cmd.getClass().getName().startsWith("net.minecraft.command") && cmd instanceof CommandBase)
        {
            doPermissionReg(cmd.getCommandName(), "mc." + cmd.getCommandName(), RegisteredPermValue.fromInt(((CommandBase) cmd).getRequiredPermissionLevel()));
        }
    }

    public static List getCommandPermRegList()
    {
        permRegList.add(new PermReg("mc.cmdblocks", RegisteredPermValue.OP2, null));
        return permRegList;
    }
    
    public static boolean canCommandSenderUseCommand(ICommand command, ICommandSender sender)
    {
        if (sender instanceof EntityPlayerMP && permMap.get(command.getCommandName()) != null)
        {
            return PermissionsManager.checkPerm((EntityPlayerMP) sender, permMap.get(command.getCommandName()));
        }
        else return command.canCommandSenderUseCommand(sender);
    }

 
}