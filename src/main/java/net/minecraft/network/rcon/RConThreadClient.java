package net.minecraft.network.rcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.SERVER)
public class RConThreadClient extends RConThreadBase
{
    private static final Logger field_164005_h = LogManager.getLogger();
    // JAVADOC FIELD $$ field_72657_g
    private boolean loggedIn;
    // JAVADOC FIELD $$ field_72659_h
    private Socket clientSocket;
    // JAVADOC FIELD $$ field_72660_i
    private byte[] buffer = new byte[1460];
    // JAVADOC FIELD $$ field_72658_j
    private String rconPassword;
    private static final String __OBFID = "CL_00001804";

    RConThreadClient(IServer par1IServer, Socket par2Socket)
    {
        super(par1IServer, "RCON Client");
        this.clientSocket = par2Socket;

        try
        {
            this.clientSocket.setSoTimeout(0);
        }
        catch (Exception exception)
        {
            this.running = false;
        }

        this.rconPassword = par1IServer.getStringProperty("rcon.password", "");
        this.logInfo("Rcon connection from: " + par2Socket.getInetAddress());
    }

    public void run()
    {
        while (true)
        {
            try
            {
                if (!this.running)
                {
                    break;
                }

                BufferedInputStream bufferedinputstream = new BufferedInputStream(this.clientSocket.getInputStream());
                int i = bufferedinputstream.read(this.buffer, 0, 1460);

                if (10 > i)
                {
                    return;
                }

                byte b0 = 0;
                int j = RConUtils.getBytesAsLEInt(this.buffer, 0, i);

                if (j == i - 4)
                {
                    int i1 = b0 + 4;
                    int k = RConUtils.getBytesAsLEInt(this.buffer, i1, i);
                    i1 += 4;
                    int l = RConUtils.getRemainingBytesAsLEInt(this.buffer, i1);
                    i1 += 4;

                    switch (l)
                    {
                        case 2:
                            if (this.loggedIn)
                            {
                                String s1 = RConUtils.getBytesAsString(this.buffer, i1, i);

                                try
                                {
                                    this.sendMultipacketResponse(k, this.server.executeCommand(s1));
                                }
                                catch (Exception exception)
                                {
                                    this.sendMultipacketResponse(k, "Error executing: " + s1 + " (" + exception.getMessage() + ")");
                                }

                                continue;
                            }

                            this.sendLoginFailedResponse();
                            continue;
                        case 3:
                            String s = RConUtils.getBytesAsString(this.buffer, i1, i);
                            int j1 = i1 + s.length();

                            if (0 != s.length() && s.equals(this.rconPassword))
                            {
                                this.loggedIn = true;
                                this.sendResponse(k, 2, "");
                                continue;
                            }

                            this.loggedIn = false;
                            this.sendLoginFailedResponse();
                            continue;
                        default:
                            this.sendMultipacketResponse(k, String.format("Unknown request %s", new Object[] {Integer.toHexString(l)}));
                            continue;
                    }
                }
            }
            catch (SocketTimeoutException sockettimeoutexception)
            {
                break;
            }
            catch (IOException ioexception)
            {
                break;
            }
            catch (Exception exception1)
            {
                field_164005_h.error("Exception whilst parsing RCON input", exception1);
                break;
            }
            finally
            {
                this.closeSocket();
            }

            return;
        }
    }

    // JAVADOC METHOD $$ func_72654_a
    private void sendResponse(int par1, int par2, String par3Str) throws IOException
    {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream(1248);
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        byte[] abyte = par3Str.getBytes("UTF-8");
        dataoutputstream.writeInt(Integer.reverseBytes(abyte.length + 10));
        dataoutputstream.writeInt(Integer.reverseBytes(par1));
        dataoutputstream.writeInt(Integer.reverseBytes(par2));
        dataoutputstream.write(abyte);
        dataoutputstream.write(0);
        dataoutputstream.write(0);
        this.clientSocket.getOutputStream().write(bytearrayoutputstream.toByteArray());
    }

    // JAVADOC METHOD $$ func_72656_f
    private void sendLoginFailedResponse() throws IOException
    {
        this.sendResponse(-1, 2, "");
    }

    // JAVADOC METHOD $$ func_72655_a
    private void sendMultipacketResponse(int par1, String par2Str) throws IOException
    {
        int j = par2Str.length();

        do
        {
            int k = 4096 <= j ? 4096 : j;
            this.sendResponse(par1, 0, par2Str.substring(0, k));
            par2Str = par2Str.substring(k);
            j = par2Str.length();
        }
        while (0 != j);
    }

    // JAVADOC METHOD $$ func_72653_g
    private void closeSocket()
    {
        if (null != this.clientSocket)
        {
            try
            {
                this.clientSocket.close();
            }
            catch (IOException ioexception)
            {
                this.logWarning("IO: " + ioexception.getMessage());
            }

            this.clientSocket = null;
        }
    }
}