package org.spigotmc;

import java.io.File;
import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.packet.Packet255KickDisconnect;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class RestartCommand extends Command
{

    public RestartCommand(String name)
    {
        super( name );
        this.description = "Restarts the server";
        this.usageMessage = "/restart";
        this.setPermission( "bukkit.command.restart" );
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args)
    {
        if ( testPermission( sender ) )
        {
            restart();
        }
        return true;
    }

    public static void restart()
    {
        try
        {
            final File file = new File( SpigotConfig.restartScript );
            if ( file.isFile() )
            {
                System.out.println( "Attempting to restart with " + SpigotConfig.restartScript );

                // Kick all players
                for ( EntityPlayerMP p : (List< EntityPlayerMP>) MinecraftServer.getServer().getConfigurationManager().playerEntityList )
                {
                    p.playerNetServerHandler.netManager.addToSendQueue( new Packet255KickDisconnect( "Server is restarting" ) );
                    p.playerNetServerHandler.netManager.serverShutdown();
                }
                // Give the socket a chance to send the packets
                try
                {
                    Thread.sleep( 100 );
                } catch ( InterruptedException ex )
                {
                }
                // Close the socket so we can rebind with the new process
                MinecraftServer.getServer().getNetworkThread().stopListening();

                // Give time for it to kick in
                try
                {
                    Thread.sleep( 100 );
                } catch ( InterruptedException ex )
                {
                }

                // Actually shutdown
                try
                {
                    MinecraftServer.getServer().stopServer();
                } catch ( Throwable t )
                {
                }

                // This will be done AFTER the server has completely halted
                Thread shutdownHook = new Thread()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            String os = System.getProperty( "os.name" ).toLowerCase();
                            if ( os.contains( "win" ) )
                            {
                                Runtime.getRuntime().exec( "cmd /c start " + file.getPath() );
                            } else
                            {
                                Runtime.getRuntime().exec( new String[]
                                {
                                    "sh", file.getPath()
                                } );
                            }
                        } catch ( Exception e )
                        {
                            e.printStackTrace();
                        }
                    }
                };

                shutdownHook.setDaemon( true );
                Runtime.getRuntime().addShutdownHook( shutdownHook );
            } else
            {
                System.out.println( "Startup script '" + SpigotConfig.restartScript + "' does not exist! Stopping server." );
            }
            System.exit( 0 );
        } catch ( Exception ex )
        {
            ex.printStackTrace();
        }
    }
}
