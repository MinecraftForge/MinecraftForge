package net.minecraft.src;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

class ThreadLoginVerifier extends Thread
{
    /** The login packet to be verified. */
    final Packet1Login loginPacket;

    /** The login handler that spawned this thread. */
    final NetLoginHandler loginHandler;

    ThreadLoginVerifier(NetLoginHandler par1NetLoginHandler, Packet1Login par2Packet1Login)
    {
        this.loginHandler = par1NetLoginHandler;
        this.loginPacket = par2Packet1Login;
    }

    public void run()
    {
        try
        {
            String var1 = NetLoginHandler.getServerId(this.loginHandler);
            URL var2 = new URL("http://session.minecraft.net/game/checkserver.jsp?user=" + URLEncoder.encode(this.loginPacket.username, "UTF-8") + "&serverId=" + URLEncoder.encode(var1, "UTF-8"));
            BufferedReader var3 = new BufferedReader(new InputStreamReader(var2.openStream()));
            String var4 = var3.readLine();
            var3.close();

            if (var4.equals("YES"))
            {
                NetLoginHandler.setLoginPacket(this.loginHandler, this.loginPacket);
            }
            else
            {
                this.loginHandler.kickUser("Failed to verify username!");
            }
        }
        catch (Exception var5)
        {
            this.loginHandler.kickUser("Failed to verify username! [internal error " + var5 + "]");
            var5.printStackTrace();
        }
    }
}
