package net.minecraftforge.test;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = "entityupdateblockedtest", name = "Entity Update Blocked Test", version = "1.0.0")
public class EntityUpdateBlockedTest
{
    @Mod.EventHandler
    public void init(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new BlockEntityUpdateCommand());
    }

    private class BlockEntityUpdateCommand extends CommandBase
    {

        @Override
        public String getName()
        {
            return "blockEntityUpdate";
        }

        @Override
        public String getUsage(ICommandSender sender)
        {
            return "blockEntityUpdate <value>";
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
        {
            if (args.length != 1)
                return;
            boolean value = Boolean.parseBoolean(args[0]);
            for (Entity ent : sender.getEntityWorld().loadedEntityList)
            {
                if (!(ent instanceof EntityPlayer))
                    ent.updateBlocked = value;
            }
        }
    }
}
