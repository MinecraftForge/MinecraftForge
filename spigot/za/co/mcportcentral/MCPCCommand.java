package za.co.mcportcentral;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.util.StringUtil;

import com.google.common.collect.ImmutableList;

public class MCPCCommand extends Command {
    private static final List<String> COMMANDS = ImmutableList.of("get", "set", "save", "collision"); // TODO:, "chunks");
    private static final List<String> CHUNK_COMMANDS = ImmutableList.of("print", "dump");
    private static final List<String> TOGGLE_CONFIG_OPTIONS = new ArrayList<String>();
    
    public MCPCCommand()
    {
        super("mcpc");
        this.description = "Toggle certain MCPC options";
        this.usageMessage = "/mcpc [get|set] <option> [value]";
        this.setPermission("mcpc.command.mcpc");
        
        for(MCPCConfig.Toggle toggle : MCPCConfig.Toggle.class.getEnumConstants())
        {
            TOGGLE_CONFIG_OPTIONS.add(toggle.name());
        }
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args)
    {
        if (!testPermission(sender)) return true;
        if (args.length > 0 && "collision".equalsIgnoreCase(args[0])) {
            processCollision(sender, args);
            return true;
        }
        if (args.length == 1 && "save".equalsIgnoreCase(args[0])) {
            MCPCConfig.save();
            sender.sendMessage(ChatColor.GREEN + "Config file saved");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }
        
        if ("get".equalsIgnoreCase(args[0])) {
            return getToggle(sender, args);
        }
        else if ("set".equalsIgnoreCase(args[0])) {
            return setToggle(sender, args);
        }
        else {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
        }
        
        return false;
    }
    
    private void processCollision(CommandSender sender, String[] args) {
        for(WorldServer world : DimensionManager.getWorlds()) {
            if (world.maxCollisionEntity == null) continue;
            if (args.length > 1 && "reset".equalsIgnoreCase(args[1])) {
                world.maxCollisionEntity = null;
                world.entityCollisionMaxSize = 0;
                continue;
            }
            
            int maxSize = world.entityCollisionMaxSize;
            String maxSizeMsg = (maxSize > 10 ? ChatColor.RED : ChatColor.GREEN) + " " + maxSize;
            sender.sendMessage(ChatColor.GOLD + " " + world.getWorld().getName() + ":" + maxSizeMsg);
            sender.sendMessage(ChatColor.GOLD + "" + world.maxCollisionEntity);

            if (args.length > 1 && "kill".equalsIgnoreCase(args[1])) {
                AxisAlignedBB box = world.maxCollisionEntity.boundingBox;
                if (box == null) continue;
                world.getCollidingBoundingBoxes(world.maxCollisionEntity, box);
                
                List kill = world.getEntitiesWithinAABBExcludingEntity(world.maxCollisionEntity, box.expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));
                sender.sendMessage(ChatColor.RED + "Killing " + kill.size()+1 + " entities");
                for(int i=0; i < kill.size(); i++) {
                    Entity entity = (Entity)kill.get(i);
                    entity.setDead();
                }
                world.maxCollisionEntity.setDead();
            }
            world.entityCollisionMaxSize = 0; // Reset the stats

        }
    }
    
    private boolean getToggle(CommandSender sender, String[] args)
    {
        try {
            MCPCConfig.Toggle toggle = Enum.valueOf(MCPCConfig.Toggle.class, args[1]);
            if (toggle == null)
            {
                sender.sendMessage(ChatColor.RED + "Could not find option: " + args[1]);
                return false;
            }
            boolean value = MCPCConfig.getToggle(toggle);
            String option = (value ? ChatColor.GREEN : ChatColor.RED) + " " + value;
            sender.sendMessage(ChatColor.GOLD + args[1] + " " + option);
        }
        catch (Exception ex) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            ex.printStackTrace();
        }
        return true;
    }
    
    private boolean setToggle(CommandSender sender, String[] args)
    {
        try {
            MCPCConfig.Toggle toggle = Enum.valueOf(MCPCConfig.Toggle.class, args[1]);
            if (toggle == null)
            {
                sender.sendMessage(ChatColor.RED + "Could not find option: " + args[1]);
                return false;
            }
            if (args.length == 2)
            {
                sender.sendMessage(ChatColor.RED + "Usage: " + args[0] + " " + args[1] + " [TRUE|FALSE]");
                return false;
            }
            Boolean value = BooleanUtils.toBooleanObject(args[2]);
            if (value == null)
            {
                sender.sendMessage(ChatColor.RED + "Usage: " + args[0] + " " + args[1] + " [TRUE|FALSE]");
                return false;
            }

            MCPCConfig.setToggle(toggle, value);
            String option = (value ? ChatColor.GREEN : ChatColor.RED) + " " + value;
            sender.sendMessage(ChatColor.GOLD + args[1] + " " + option);
        }
        catch (Exception ex) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            ex.printStackTrace();
        }
        return true;
    }
    
    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");

        if (args.length == 1)
        {
            return StringUtil.copyPartialMatches(args[0], COMMANDS, new ArrayList<String>(COMMANDS.size()));
        }
        if (args.length == 2 && "get".equalsIgnoreCase(args[0]) || "set".equalsIgnoreCase(args[0]))
        {
            return StringUtil.copyPartialMatches(args[1], TOGGLE_CONFIG_OPTIONS, new ArrayList<String>(TOGGLE_CONFIG_OPTIONS.size()));
        }
        else if (args.length == 2 && "chunks".equalsIgnoreCase(args[0]))
        {
            return StringUtil.copyPartialMatches(args[1], CHUNK_COMMANDS, new ArrayList<String>(CHUNK_COMMANDS.size()));
        }

        return ImmutableList.of();
    }

}
