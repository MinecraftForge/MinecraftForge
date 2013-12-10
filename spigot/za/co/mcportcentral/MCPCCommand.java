package za.co.mcportcentral;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import com.google.common.collect.ImmutableList;

public class MCPCCommand extends Command {
    private static final List<String> COMMANDS = ImmutableList.of("get", "set", "tick-interval", "save", "reload", "chunks", "heap"); // TODO:, "chunks");
    private static final List<String> CHUNK_COMMANDS = ImmutableList.of("print", "dump");
    
    public MCPCCommand()
    {
        super("mcpc");
        this.description = "Toggle certain MCPC options";
        
        this.usageMessage = "/mcpc [" + StringUtils.join(COMMANDS, '|') + "] <option> [value]";
        this.setPermission("mcpc.command.mcpc");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args)
    {
        if (!testPermission(sender))
        {
            return true;
        }
        if (args.length >0 && "heap".equalsIgnoreCase(args[0]))
        {
            processHeap(sender, args);
            return true;
        }
        if (args.length >0 && "chunks".equalsIgnoreCase(args[0]))
        {
            processChunks(sender, args);
            return true;
        }
        if (args.length == 1 && "save".equalsIgnoreCase(args[0]))
        {
            MCPCConfig.save();
            sender.sendMessage(ChatColor.GREEN + "Config file saved");
            return true;
        }
        if (args.length == 1 && "reload".equalsIgnoreCase(args[0]))
        {
            MCPCConfig.load();
            sender.sendMessage(ChatColor.GREEN + "Config file reloaded");
            return true;
        }
        if (args.length < 2)
        {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }
        
        if ("tick-interval".equalsIgnoreCase(args[0]))
        {
            return intervalSet(sender, args);
        }
        if ("get".equalsIgnoreCase(args[0]))
        {
            return getToggle(sender, args);
        }
        else if ("set".equalsIgnoreCase(args[0]))
        {
            return setToggle(sender, args);
        }
        else
        {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
        }
        
        return false;
    }
    
    private void processHeap(CommandSender sender, String[] args) {
        File file = new File(new File(new File("."), "dumps"), "heap-dump-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-server.bin");
        sender.sendMessage("Writing chunk info to: " + file);
        MCPCHooks.HeapDump.dumpHeap(file, true);
        sender.sendMessage("Chunk info complete");
    }
    
    private void processChunks(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.GOLD + "Dimension stats: ");
        for(net.minecraft.world.WorldServer world : MinecraftServer.getServer().worlds)
        {
            sender.sendMessage(ChatColor.GOLD + "Dimension: " + ChatColor.GRAY + world.provider.dimensionId + 
                    ChatColor.GOLD + " Loaded Chunks: " + ChatColor.GRAY + world.theChunkProviderServer.loadedChunkHashMap.size() +
                    ChatColor.GOLD + " Active Chunks: " + ChatColor.GRAY + world.activeChunkSet.size() +
                    ChatColor.GOLD + " Entities: " + ChatColor.GRAY + world.loadedEntityList.size() +
                    ChatColor.GOLD + " Tile Entities: " + ChatColor.GRAY + world.loadedTileEntityList.size()
                    );
            sender.sendMessage(ChatColor.GOLD + " Entities Last Tick: " + ChatColor.GRAY + world.entitiesTicked +
                    ChatColor.GOLD + " Tiles Last Tick: " + ChatColor.GRAY + world.tilesTicked +
                    ChatColor.GOLD + " Removed Entities: " + ChatColor.GRAY + world.unloadedEntityList.size() +
                    ChatColor.GOLD + " Removed Tile Entities: " + ChatColor.GRAY + world.entityRemoval.size()
                    );
        }
        
        if (args.length < 2 || !"dump".equalsIgnoreCase(args[1])) return;
        boolean dumpAll = (args.length > 2 && "all".equalsIgnoreCase(args[2]));
        
        File file = new File(new File(new File("."), "chunk-dumps"), "chunk-info-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-server.txt");
        sender.sendMessage("Writing chunk info to: " + file);
        MCPCHooks.writeChunks(file, dumpAll);
        sender.sendMessage("Chunk info complete");
    }
    
    private boolean getToggle(CommandSender sender, String[] args)
    {
        try {
            MCPCConfig.Setting toggle = MCPCConfig.settings.get(args[1]);
            if (toggle == null)
            {
                sender.sendMessage(ChatColor.RED + "Could not find option: " + args[1]);
                return false;
            }
            Object value = toggle.getValue();
            String option = (Boolean.TRUE.equals(value) ? ChatColor.GREEN : ChatColor.RED) + " " + value;
            sender.sendMessage(ChatColor.GOLD + args[1] + " " + option);
        }
        catch (Exception ex) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            ex.printStackTrace();
        }
        return true;
    }
    
    private boolean intervalSet(CommandSender sender, String[] args)
    {
        try {
            int setting = NumberUtils.toInt(args[2], 1);
            MCPCConfig.set(args[1], setting);
        } catch (Exception ex) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }
        return true;
    }
    
    private boolean setToggle(CommandSender sender, String[] args)
    {
        try {
            MCPCConfig.Setting toggle = MCPCConfig.settings.get(args[1]);
            if (toggle == null)
            {
                sender.sendMessage(ChatColor.RED + "Could not find option: " + args[1]);
                return false;
            }
            if (args.length == 2)
            {
                sender.sendMessage(ChatColor.RED + "Usage: " + args[0] + " " + args[1] + " [value]");
                return false;
            }
            toggle.setValue(args[2]);
            Object value = toggle.getValue();
            String option = (Boolean.TRUE.equals(value) ? ChatColor.GREEN : ChatColor.RED) + " " + value;
            sender.sendMessage(ChatColor.GOLD + args[1] + " " + option);
            // Special case for load-on-request
            if (toggle == MCPCConfig.Setting.loadChunkOnRequest)
            {
                for (net.minecraft.world.WorldServer world : MinecraftServer.getServer().worlds)
                {
                    world.theChunkProviderServer.loadChunkOnProvideRequest = za.co.mcportcentral.MCPCConfig.Setting.loadChunkOnRequest.getValue();
                }
            }
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
            return StringUtil.copyPartialMatches(args[1], MCPCConfig.settings.keySet(), new ArrayList<String>(MCPCConfig.settings.size()));
        }
        else if (args.length == 2 && "chunks".equalsIgnoreCase(args[0]))
        {
            return StringUtil.copyPartialMatches(args[1], CHUNK_COMMANDS, new ArrayList<String>(CHUNK_COMMANDS.size()));
        }
        else if (args.length == 2 && "tick-interval".equalsIgnoreCase(args[0]))
        {
            ArrayList<String> handlers = new ArrayList<String>();
            for(SkipIntervalHandler h : SkipIntervalHandler.handlers) handlers.add(h.configString);
            return StringUtil.copyPartialMatches(args[1], handlers, new ArrayList<String>(CHUNK_COMMANDS.size()));
        }

        return ImmutableList.of();
    }

}
