package org.spigotmc;

import java.net.InetAddress;
import net.minecraft.network.NetLoginHandler;

public class SpamHaus
{

    private SpamHaus()
    {
    }

    public static boolean filterIp(NetLoginHandler con)
    {
        if ( SpigotConfig.preventProxies )
        {
            try
            {
                InetAddress address = con.getSocket().getInetAddress();
                String ip = address.getHostAddress();

                if ( !address.isLoopbackAddress() )
                {
                    String[] split = ip.split( "\\." );
                    StringBuilder lookup = new StringBuilder();
                    for ( int i = split.length - 1; i >= 0; i-- )
                    {
                        lookup.append( split[i] );
                        lookup.append( "." );
                    }
                    lookup.append( "xbl.spamhaus.org." );
                    if ( InetAddress.getByName( lookup.toString() ) != null )
                    {
                        con.raiseErrorAndDisconnect( "Your IP address (" + ip + ") is flagged as unsafe by spamhaus.org/xbl" );
                        return true;
                    }
                }
            } catch ( Exception ex )
            {
            }
        }
        return false;
    }
}
