package net.minecraftforge.server.command;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.server.ForgeTimeTracker;

public class ForgeCommand extends CommandBase {

    private static final DecimalFormat timeFormatter = new DecimalFormat("########0.000");
    private WeakReference<MinecraftServer> server;

    public ForgeCommand(MinecraftServer server)
    {
        this.server = new WeakReference<MinecraftServer>(server);
    }

    @Override
    public String getCommandName()
    {
        return "forge";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender)
    {
        return "commands.forge.usage";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }
    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length == 0)
        {
            throw new WrongUsageException("commands.forge.usage");
        }
        else if ("help".equals(args[0]))
        {
            throw new WrongUsageException("commands.forge.usage");
        }
        else if ("tps".equals(args[0]))
        {
            displayTPS(sender,args);
        }
        else if ("tpslog".equals(args[0]))
        {
            doTPSLog(sender,args);
        }
        else if ("track".equals(args[0]))
        {
            handleTracking(sender, args);
        }
        else
        {
            throw new WrongUsageException("commands.forge.usage");
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, "tps", "track");
        }
        else if (args.length == 2)
        {
            if ("tps".equals(args[0])) {
                return getListOfStringsMatchingLastWord(args, getServer().worldTickTimes.keySet());
            }
            else if ("track".equals(args[0]))
            {
                return getListOfStringsMatchingLastWord(args, "te");
            }
        }
        return null;
    }

    private void handleTracking(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length != 3)
        {
            throw new WrongUsageException("commands.forge.usage.tracking");
        }
        String type = args[1];
        int duration = parseInt(args[2], 1, 60);

        if ("te".equals(type))
        {
            doTurnOnTileEntityTracking(sender, duration);
        }
        else
        {
            throw new WrongUsageException("commands.forge.usage.tracking");
        }
    }

    private void doTurnOnTileEntityTracking(ICommandSender sender, int duration)
    {
        ForgeTimeTracker.tileEntityTrackingDuration = duration;
        ForgeTimeTracker.tileEntityTracking = true;
        sender.addChatMessage(new ChatComponentTranslation("commands.forge.tracking.te.enabled", duration));
    }

    private void doTPSLog(ICommandSender sender, String[] args)
    {

    }

    private void displayTPS(ICommandSender sender, String[] args) throws CommandException
    {
        int dim = 0;
        boolean summary = true;
        if (args.length > 1)
        {
            dim = parseInt(args[1]);
            summary = false;
        }
        if (summary)
        {
            for (Integer dimId : DimensionManager.getIDs())
            {
                double worldTickTime = ForgeCommand.mean(this.getServer().worldTickTimes.get(dimId)) * 1.0E-6D;
                double worldTPS = Math.min(1000.0/worldTickTime, 20);
                sender.addChatMessage(new ChatComponentTranslation("commands.forge.tps.summary",String.format("Dim %d", dimId), timeFormatter.format(worldTickTime), timeFormatter.format(worldTPS)));
            }
            double meanTickTime = ForgeCommand.mean(this.getServer().tickTimeArray) * 1.0E-6D;
            double meanTPS = Math.min(1000.0/meanTickTime, 20);
            sender.addChatMessage(new ChatComponentTranslation("commands.forge.tps.summary","Overall", timeFormatter.format(meanTickTime), timeFormatter.format(meanTPS)));
        }
        else
        {
            double worldTickTime = ForgeCommand.mean(this.getServer().worldTickTimes.get(dim)) * 1.0E-6D;
            double worldTPS = Math.min(1000.0/worldTickTime, 20);
            sender.addChatMessage(new ChatComponentTranslation("commands.forge.tps.summary",String.format("Dim %d", dim), timeFormatter.format(worldTickTime), timeFormatter.format(worldTPS)));
        }
    }

    private static long mean(long[] values)
    {
        long sum = 0l;
        for (long v : values)
        {
            sum+=v;
        }

        return sum / values.length;
    }

    private MinecraftServer getServer()
    {
        return this.server.get();
    }
}
