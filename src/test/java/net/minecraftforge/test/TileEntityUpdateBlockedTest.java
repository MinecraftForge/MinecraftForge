package net.minecraftforge.test;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = "tileentityupdateblockedtest", name = "Tile Entity Update Blocked Test", version = "1.0.0")
public class TileEntityUpdateBlockedTest
{
    @Mod.EventHandler
    public void init(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new BlockTileEntityUpdateCommand());
    }

    private class BlockTileEntityUpdateCommand extends CommandBase
    {

        @Override
        public String getName()
        {
            return "blockTileEntityUpdate";
        }

        @Override
        public String getUsage(ICommandSender sender)
        {
            return "blockTileEntityUpdate <value>";
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
        {
            if (args.length != 1)
                return;
            boolean value = Boolean.parseBoolean(args[0]);
            for (TileEntity ent : sender.getEntityWorld().tickableTileEntities)
            {
                    ent.updateBlocked = value;
            }
        }
    }
}
