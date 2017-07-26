package net.minecraftforge.debug;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.DimensionType;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = "forge.serveronlydimensiontest", serverSideOnly = true, acceptableRemoteVersions = "*")
public class ServerOnlyDimensionTest
{

    private static final boolean ENABLED = Boolean.TRUE;
    private static final int DIMENSION_ID = 100;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (!ENABLED)
        {
            return;
        }

        DimensionType dimensionType = DimensionType.register("forge_serveronlytest", "_serveronlytest", DIMENSION_ID, ServerOnlyOverworldClone.class, false);
        DimensionManager.registerDimension(DIMENSION_ID, dimensionType, 0);
    }

    @Mod.EventHandler
    public void serverStart(FMLServerStartingEvent event)
    {
        if (!ENABLED)
        {
            return;
        }

        event.registerServerCommand(new CommandBase()
        {
            @Override
            public String getName()
            {
                return "serveronlytestdim";
            }

            @Override
            public boolean checkPermission(MinecraftServer server, ICommandSender sender)
            {
                return true;
            }

            @Override
            public String getUsage(ICommandSender sender)
            {
                return "";
            }

            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
            {
                if (sender instanceof EntityPlayerMP)
                {
                    EntityPlayerMP player = (EntityPlayerMP)sender;
                    Teleporter teleporter = new Teleporter(((WorldServer)player.world))
                    {
                        @Override
                        public void placeInPortal(Entity entityIn, float rotationYaw)
                        {
                            if (entityIn instanceof EntityPlayerMP)
                            {
                                ((EntityPlayerMP)entityIn).connection.setPlayerLocation(0, 70, 0, entityIn.rotationYaw, entityIn.rotationPitch);
                            }
                            else
                            {
                                entityIn.attackEntityFrom(DamageSource.OUT_OF_WORLD, 10000);
                            }
                        }
                    };
                    server.getPlayerList().transferPlayerToDimension(player, DIMENSION_ID, teleporter);
                }
            }
        });

        event.registerServerCommand(new CommandBase()
        {
            @Override
            public String getName()
            {
                return "getmydimension";
            }

            @Override
            public boolean checkPermission(MinecraftServer server, ICommandSender sender)
            {
                return true;
            }

            @Override
            public String getUsage(ICommandSender sender)
            {
                return "";
            }

            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
            {
                if (sender instanceof EntityPlayerMP)
                {
                    sender.sendMessage(new TextComponentString(String.valueOf(((EntityPlayerMP)sender).dimension)));
                }
            }
        });
    }

    public static class ServerOnlyOverworldClone extends WorldProviderSurface
    {

        @Override
        public boolean canRespawnHere()
        {
            return false;
        }

        @Override
        public int getRespawnDimension(EntityPlayerMP player)
        {
            return 0;
        }
    }

}
