package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import net.minecraft.client.Minecraft;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ThreadDownloadResources extends Thread
{
    /** The folder to store the resources in. */
    public File resourcesFolder;

    /** A reference to the Minecraft object. */
    private Minecraft mc;

    /** Set to true when Minecraft is closing down. */
    private boolean closing = false;

    public ThreadDownloadResources(File par1File, Minecraft par2Minecraft)
    {
        this.mc = par2Minecraft;
        this.setName("Resource download thread");
        this.setDaemon(true);
        this.resourcesFolder = new File(par1File, "resources/");

        if (!this.resourcesFolder.exists() && !this.resourcesFolder.mkdirs())
        {
            throw new RuntimeException("The working directory could not be created: " + this.resourcesFolder);
        }
    }

    public void run()
    {
        try
        {
            URL var1 = new URL("http://s3.amazonaws.com/MinecraftResources/");
            DocumentBuilderFactory var2 = DocumentBuilderFactory.newInstance();
            DocumentBuilder var3 = var2.newDocumentBuilder();
            Document var4 = var3.parse(var1.openStream());
            NodeList var5 = var4.getElementsByTagName("Contents");

            for (int var6 = 0; var6 < 2; ++var6)
            {
                for (int var7 = 0; var7 < var5.getLength(); ++var7)
                {
                    Node var8 = var5.item(var7);

                    if (var8.getNodeType() == 1)
                    {
                        Element var9 = (Element)var8;
                        String var10 = ((Element)var9.getElementsByTagName("Key").item(0)).getChildNodes().item(0).getNodeValue();
                        long var11 = Long.parseLong(((Element)var9.getElementsByTagName("Size").item(0)).getChildNodes().item(0).getNodeValue());

                        if (var11 > 0L)
                        {
                            this.downloadAndInstallResource(var1, var10, var11, var6);

                            if (this.closing)
                            {
                                return;
                            }
                        }
                    }
                }
            }
        }
        catch (Exception var13)
        {
            this.loadResource(this.resourcesFolder, "");
            var13.printStackTrace();
        }
    }

    /**
     * Reloads the resource folder and passes the resources to Minecraft to install.
     */
    public void reloadResources()
    {
        this.loadResource(this.resourcesFolder, "");
    }

    /**
     * Loads a resource and passes it to Minecraft to install.
     */
    private void loadResource(File par1File, String par2Str)
    {
        File[] var3 = par1File.listFiles();

        for (int var4 = 0; var4 < var3.length; ++var4)
        {
            if (var3[var4].isDirectory())
            {
                this.loadResource(var3[var4], par2Str + var3[var4].getName() + "/");
            }
            else
            {
                try
                {
                    this.mc.installResource(par2Str + var3[var4].getName(), var3[var4]);
                }
                catch (Exception var6)
                {
                    System.out.println("Failed to add " + par2Str + var3[var4].getName());
                }
            }
        }
    }

    /**
     * Downloads the resource and saves it to disk then installs it.
     */
    private void downloadAndInstallResource(URL par1URL, String par2Str, long par3, int par5)
    {
        try
        {
            int var6 = par2Str.indexOf("/");
            String var7 = par2Str.substring(0, var6);

            if (!var7.equals("sound") && !var7.equals("newsound"))
            {
                if (par5 != 1)
                {
                    return;
                }
            }
            else if (par5 != 0)
            {
                return;
            }

            File var8 = new File(this.resourcesFolder, par2Str);

            if (!var8.exists() || var8.length() != par3)
            {
                var8.getParentFile().mkdirs();
                String var9 = par2Str.replaceAll(" ", "%20");
                this.downloadResource(new URL(par1URL, var9), var8, par3);

                if (this.closing)
                {
                    return;
                }
            }

            this.mc.installResource(par2Str, var8);
        }
        catch (Exception var10)
        {
            var10.printStackTrace();
        }
    }

    /**
     * Downloads the resource and saves it to disk.
     */
    private void downloadResource(URL par1URL, File par2File, long par3) throws IOException
    {
        byte[] var5 = new byte[4096];
        DataInputStream var6 = new DataInputStream(par1URL.openStream());
        DataOutputStream var7 = new DataOutputStream(new FileOutputStream(par2File));
        boolean var8 = false;

        do
        {
            int var9;

            if ((var9 = var6.read(var5)) < 0)
            {
                var6.close();
                var7.close();
                return;
            }

            var7.write(var5, 0, var9);
        }
        while (!this.closing);
    }

    /**
     * Called when Minecraft is closing down.
     */
    public void closeMinecraft()
    {
        this.closing = true;
    }
}
