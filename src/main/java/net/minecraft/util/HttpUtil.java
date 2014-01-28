package net.minecraft.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpUtil
{
    private static final AtomicInteger field_151228_a = new AtomicInteger(0);
    private static final Logger field_151227_b = LogManager.getLogger();
    private static final String __OBFID = "CL_00001485";

    // JAVADOC METHOD $$ func_76179_a
    public static String buildPostString(Map par0Map)
    {
        StringBuilder stringbuilder = new StringBuilder();
        Iterator iterator = par0Map.entrySet().iterator();

        while (iterator.hasNext())
        {
            Entry entry = (Entry)iterator.next();

            if (stringbuilder.length() > 0)
            {
                stringbuilder.append('&');
            }

            try
            {
                stringbuilder.append(URLEncoder.encode((String)entry.getKey(), "UTF-8"));
            }
            catch (UnsupportedEncodingException unsupportedencodingexception1)
            {
                unsupportedencodingexception1.printStackTrace();
            }

            if (entry.getValue() != null)
            {
                stringbuilder.append('=');

                try
                {
                    stringbuilder.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
                }
                catch (UnsupportedEncodingException unsupportedencodingexception)
                {
                    unsupportedencodingexception.printStackTrace();
                }
            }
        }

        return stringbuilder.toString();
    }

    public static String func_151226_a(URL p_151226_0_, Map p_151226_1_, boolean p_151226_2_)
    {
        return func_151225_a(p_151226_0_, buildPostString(p_151226_1_), p_151226_2_);
    }

    private static String func_151225_a(URL p_151225_0_, String p_151225_1_, boolean p_151225_2_)
    {
        try
        {
            Proxy proxy = MinecraftServer.getServer() == null ? null : MinecraftServer.getServer().getServerProxy();

            if (proxy == null)
            {
                proxy = Proxy.NO_PROXY;
            }

            HttpURLConnection httpurlconnection = (HttpURLConnection)p_151225_0_.openConnection(proxy);
            httpurlconnection.setRequestMethod("POST");
            httpurlconnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpurlconnection.setRequestProperty("Content-Length", "" + p_151225_1_.getBytes().length);
            httpurlconnection.setRequestProperty("Content-Language", "en-US");
            httpurlconnection.setUseCaches(false);
            httpurlconnection.setDoInput(true);
            httpurlconnection.setDoOutput(true);
            DataOutputStream dataoutputstream = new DataOutputStream(httpurlconnection.getOutputStream());
            dataoutputstream.writeBytes(p_151225_1_);
            dataoutputstream.flush();
            dataoutputstream.close();
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(httpurlconnection.getInputStream()));
            StringBuffer stringbuffer = new StringBuffer();
            String s1;

            while ((s1 = bufferedreader.readLine()) != null)
            {
                stringbuffer.append(s1);
                stringbuffer.append('\r');
            }

            bufferedreader.close();
            return stringbuffer.toString();
        }
        catch (Exception exception)
        {
            if (!p_151225_2_)
            {
                field_151227_b.error("Could not post to " + p_151225_0_, exception);
            }

            return "";
        }
    }

    @SideOnly(Side.CLIENT)
    public static void func_151223_a(final File p_151223_0_, final String p_151223_1_, final HttpUtil.DownloadListener p_151223_2_, final Map p_151223_3_, final int p_151223_4_, final IProgressUpdate p_151223_5_, final Proxy p_151223_6_)
    {
        Thread thread = new Thread(new Runnable()
        {
            private static final String __OBFID = "CL_00001486";
            public void run()
            {
                URLConnection urlconnection = null;
                InputStream inputstream = null;
                DataOutputStream dataoutputstream = null;

                if (p_151223_5_ != null)
                {
                    p_151223_5_.resetProgressAndMessage("Downloading Texture Pack");
                    p_151223_5_.resetProgresAndWorkingMessage("Making Request...");
                }

                try
                {
                    try
                    {
                        byte[] abyte = new byte[4096];
                        URL url = new URL(p_151223_1_);
                        urlconnection = url.openConnection(p_151223_6_);
                        float f = 0.0F;
                        float f1 = (float)p_151223_3_.entrySet().size();
                        Iterator iterator = p_151223_3_.entrySet().iterator();

                        while (iterator.hasNext())
                        {
                            Entry entry = (Entry)iterator.next();
                            urlconnection.setRequestProperty((String)entry.getKey(), (String)entry.getValue());

                            if (p_151223_5_ != null)
                            {
                                p_151223_5_.setLoadingProgress((int)(++f / f1 * 100.0F));
                            }
                        }

                        inputstream = urlconnection.getInputStream();
                        f1 = (float)urlconnection.getContentLength();
                        int i = urlconnection.getContentLength();

                        if (p_151223_5_ != null)
                        {
                            p_151223_5_.resetProgresAndWorkingMessage(String.format("Downloading file (%.2f MB)...", new Object[] {Float.valueOf(f1 / 1000.0F / 1000.0F)}));
                        }

                        if (p_151223_0_.exists())
                        {
                            long j = p_151223_0_.length();

                            if (j == (long)i)
                            {
                                p_151223_2_.func_148522_a(p_151223_0_);

                                if (p_151223_5_ != null)
                                {
                                    p_151223_5_.func_146586_a();
                                }

                                return;
                            }

                            HttpUtil.field_151227_b.warn("Deleting " + p_151223_0_ + " as it does not match what we currently have (" + i + " vs our " + j + ").");
                            p_151223_0_.delete();
                        }
                        else if (p_151223_0_.getParentFile() != null)
                        {
                            p_151223_0_.getParentFile().mkdirs();
                        }

                        dataoutputstream = new DataOutputStream(new FileOutputStream(p_151223_0_));

                        if (p_151223_4_ > 0 && f1 > (float)p_151223_4_)
                        {
                            if (p_151223_5_ != null)
                            {
                                p_151223_5_.func_146586_a();
                            }

                            throw new IOException("Filesize is bigger than maximum allowed (file is " + f + ", limit is " + p_151223_4_ + ")");
                        }

                        boolean flag = false;
                        int k;

                        while ((k = inputstream.read(abyte)) >= 0)
                        {
                            f += (float)k;

                            if (p_151223_5_ != null)
                            {
                                p_151223_5_.setLoadingProgress((int)(f / f1 * 100.0F));
                            }

                            if (p_151223_4_ > 0 && f > (float)p_151223_4_)
                            {
                                if (p_151223_5_ != null)
                                {
                                    p_151223_5_.func_146586_a();
                                }

                                throw new IOException("Filesize was bigger than maximum allowed (got >= " + f + ", limit was " + p_151223_4_ + ")");
                            }

                            dataoutputstream.write(abyte, 0, k);
                        }

                        p_151223_2_.func_148522_a(p_151223_0_);

                        if (p_151223_5_ != null)
                        {
                            p_151223_5_.func_146586_a();
                            return;
                        }
                    }
                    catch (Throwable throwable)
                    {
                        throwable.printStackTrace();
                    }
                }
                finally
                {
                    try
                    {
                        if (inputstream != null)
                        {
                            inputstream.close();
                        }
                    }
                    catch (IOException ioexception1)
                    {
                        ;
                    }

                    try
                    {
                        if (dataoutputstream != null)
                        {
                            dataoutputstream.close();
                        }
                    }
                    catch (IOException ioexception)
                    {
                        ;
                    }
                }
            }
        }, "File Downloader #" + field_151228_a.incrementAndGet());
        thread.setDaemon(true);
        thread.start();
    }

    @SideOnly(Side.CLIENT)
    public static int func_76181_a() throws IOException
    {
        ServerSocket serversocket = null;
        boolean flag = true;
        int i;

        try
        {
            serversocket = new ServerSocket(0);
            i = serversocket.getLocalPort();
        }
        finally
        {
            try
            {
                if (serversocket != null)
                {
                    serversocket.close();
                }
            }
            catch (IOException ioexception)
            {
                ;
            }
        }

        return i;
    }

    @SideOnly(Side.CLIENT)
    public interface DownloadListener
    {
        void func_148522_a(File var1);
    }
}