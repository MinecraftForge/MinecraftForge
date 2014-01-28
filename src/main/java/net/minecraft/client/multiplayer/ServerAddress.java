package net.minecraft.client.multiplayer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Hashtable;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;

@SideOnly(Side.CLIENT)
public class ServerAddress
{
    private final String ipAddress;
    private final int serverPort;
    private static final String __OBFID = "CL_00000889";

    private ServerAddress(String par1Str, int par2)
    {
        this.ipAddress = par1Str;
        this.serverPort = par2;
    }

    public String getIP()
    {
        return this.ipAddress;
    }

    public int getPort()
    {
        return this.serverPort;
    }

    public static ServerAddress func_78860_a(String par0Str)
    {
        if (par0Str == null)
        {
            return null;
        }
        else
        {
            String[] astring = par0Str.split(":");

            if (par0Str.startsWith("["))
            {
                int i = par0Str.indexOf("]");

                if (i > 0)
                {
                    String s1 = par0Str.substring(1, i);
                    String s2 = par0Str.substring(i + 1).trim();

                    if (s2.startsWith(":") && s2.length() > 0)
                    {
                        s2 = s2.substring(1);
                        astring = new String[] {s1, s2};
                    }
                    else
                    {
                        astring = new String[] {s1};
                    }
                }
            }

            if (astring.length > 2)
            {
                astring = new String[] {par0Str};
            }

            String s3 = astring[0];
            int j = astring.length > 1 ? parseIntWithDefault(astring[1], 25565) : 25565;

            if (j == 25565)
            {
                String[] astring1 = getServerAddress(s3);
                s3 = astring1[0];
                j = parseIntWithDefault(astring1[1], 25565);
            }

            return new ServerAddress(s3, j);
        }
    }

    // JAVADOC METHOD $$ func_78863_b
    private static String[] getServerAddress(String par0Str)
    {
        try
        {
            String s1 = "com.sun.jndi.dns.DnsContextFactory";
            Class.forName("com.sun.jndi.dns.DnsContextFactory");
            Hashtable hashtable = new Hashtable();
            hashtable.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            hashtable.put("java.naming.provider.url", "dns:");
            hashtable.put("com.sun.jndi.dns.timeout.retries", "1");
            InitialDirContext initialdircontext = new InitialDirContext(hashtable);
            Attributes attributes = initialdircontext.getAttributes("_minecraft._tcp." + par0Str, new String[] {"SRV"});
            String[] astring = attributes.get("srv").get().toString().split(" ", 4);
            return new String[] {astring[3], astring[2]};
        }
        catch (Throwable throwable)
        {
            return new String[] {par0Str, Integer.toString(25565)};
        }
    }

    private static int parseIntWithDefault(String par0Str, int par1)
    {
        try
        {
            return Integer.parseInt(par0Str.trim());
        }
        catch (Exception exception)
        {
            return par1;
        }
    }
}