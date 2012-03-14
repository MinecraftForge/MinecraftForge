package net.minecraft.src;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class RConThreadClient extends RConThreadBase
{
    /**
     * True if the client has succefssfully logged into the RCon, otherwise false
     */
    private boolean loggedIn = false;

    /** The client's Socket connection */
    private Socket clientSocket;

    /** A buffer for incoming Socket data */
    private byte[] buffer = new byte[1460];

    /** The RCon password */
    private String rconPassword;

    RConThreadClient(IServer par1IServer, Socket par2Socket)
    {
        super(par1IServer);
        this.clientSocket = par2Socket;
        this.rconPassword = par1IServer.getStringProperty("rcon.password", "");
        this.log("Rcon connection from: " + par2Socket.getInetAddress());
    }

    public void run()
    {
        while (true)
        {
            try
            {
                if (!this.running)
                {
                    return;
                }

                try
                {
                    BufferedInputStream var1 = new BufferedInputStream(this.clientSocket.getInputStream());
                    int var2 = var1.read(this.buffer, 0, 1460);

                    if (10 <= var2)
                    {
                        byte var3 = 0;
                        int var4 = RConUtils.getBytesAsLEInt(this.buffer, 0, var2);

                        if (var4 != var2 - 4)
                        {
                            return;
                        }

                        int var21 = var3 + 4;
                        int var5 = RConUtils.getBytesAsLEInt(this.buffer, var21, var2);
                        var21 += 4;
                        int var6 = RConUtils.getRemainingBytesAsLEInt(this.buffer, var21);
                        var21 += 4;

                        switch (var6)
                        {
                            case 2:
                                if (this.loggedIn)
                                {
                                    String var8 = RConUtils.getBytesAsString(this.buffer, var21, var2);

                                    try
                                    {
                                        this.sendMultipacketResponse(var5, this.server.handleRConCommand(var8));
                                    }
                                    catch (Exception var16)
                                    {
                                        this.sendMultipacketResponse(var5, "Error executing: " + var8 + " (" + var16.getMessage() + ")");
                                    }

                                    continue;
                                }

                                this.sendLoginFailedResponse();
                                continue;

                            case 3:
                                String var7 = RConUtils.getBytesAsString(this.buffer, var21, var2);
                                int var10000 = var21 + var7.length();

                                if (0 != var7.length() && var7.equals(this.rconPassword))
                                {
                                    this.loggedIn = true;
                                    this.sendResponse(var5, 2, "");
                                    continue;
                                }

                                this.loggedIn = false;
                                this.sendLoginFailedResponse();
                                continue;

                            default:
                                this.sendMultipacketResponse(var5, String.format("Unknown request %s", new Object[] {Integer.toHexString(var6)}));
                                continue;
                        }
                    }
                }
                catch (SocketTimeoutException var17)
                {
                    continue;
                }
                catch (IOException var18)
                {
                    if (this.running)
                    {
                        this.log("IO: " + var18.getMessage());
                    }

                    continue;
                }
            }
            catch (Exception var19)
            {
                System.out.println(var19);
                return;
            }
            finally
            {
                this.closeSocket();
            }

            return;
        }
    }

    /**
     * Sends the given response message to the client
     */
    private void sendResponse(int par1, int par2, String par3Str) throws IOException
    {
        ByteArrayOutputStream var4 = new ByteArrayOutputStream(1248);
        DataOutputStream var5 = new DataOutputStream(var4);
        var5.writeInt(Integer.reverseBytes(par3Str.length() + 10));
        var5.writeInt(Integer.reverseBytes(par1));
        var5.writeInt(Integer.reverseBytes(par2));
        var5.writeBytes(par3Str);
        var5.write(0);
        var5.write(0);
        this.clientSocket.getOutputStream().write(var4.toByteArray());
    }

    /**
     * Sends the standard RCon 'authorization failed' response packet
     */
    private void sendLoginFailedResponse() throws IOException
    {
        this.sendResponse(-1, 2, "");
    }

    /**
     * Splits the response message into individual packets and sends each one
     */
    private void sendMultipacketResponse(int par1, String par2Str) throws IOException
    {
        int var3 = par2Str.length();

        do
        {
            int var4 = 4096 <= var3 ? 4096 : var3;
            this.sendResponse(par1, 0, par2Str.substring(0, var4));
            par2Str = par2Str.substring(var4);
            var3 = par2Str.length();
        }
        while (0 != var3);
    }

    /**
     * Closes the client socket
     */
    private void closeSocket()
    {
        if (null != this.clientSocket)
        {
            try
            {
                this.clientSocket.close();
            }
            catch (IOException var2)
            {
                this.logWarning("IO: " + var2.getMessage());
            }

            this.clientSocket = null;
        }
    }
}
