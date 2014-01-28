package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.util.concurrent.GenericFutureListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.mco.ExceptionMcoService;
import net.minecraft.client.mco.ExceptionRetryCall;
import net.minecraft.client.mco.McoClient;
import net.minecraft.client.mco.McoServer;
import net.minecraft.client.mco.McoServerAddress;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.CLIENT)
public class TaskOnlineConnect extends TaskLongRunning
{
    private static final AtomicInteger field_148439_a = new AtomicInteger(0);
    private static final Logger field_148438_c = LogManager.getLogger();
    private NetworkManager field_148436_d;
    private final McoServer field_148437_e;
    private final GuiScreen field_148435_f;
    private static final String __OBFID = "CL_00000790";

    public TaskOnlineConnect(GuiScreen par1GuiScreen, McoServer par2McoServer)
    {
        this.field_148435_f = par1GuiScreen;
        this.field_148437_e = par2McoServer;
    }

    public void run()
    {
        this.func_148417_b(I18n.getStringParams("mco.connect.connecting", new Object[0]));
        Session session = this.func_148413_b().getSession();
        McoClient mcoclient = new McoClient(session.getSessionID(), session.getUsername(), "1.7.2", Minecraft.getMinecraft().getProxy());
        boolean flag = false;
        boolean flag1 = false;
        int i = 5;
        McoServerAddress mcoserveraddress = null;
        boolean flag2 = false;

        for (int j = 0; j < 10 && !this.func_148418_c(); ++j)
        {
            try
            {
                mcoserveraddress = mcoclient.func_148688_b(this.field_148437_e.field_148812_a);
                flag = true;
            }
            catch (ExceptionRetryCall exceptionretrycall)
            {
                i = exceptionretrycall.field_148832_d;
            }
            catch (ExceptionMcoService exceptionmcoservice)
            {
                if (exceptionmcoservice.field_148830_c == 6002)
                {
                    flag2 = true;
                }
                else
                {
                    flag1 = true;
                    this.func_148416_a(exceptionmcoservice.toString());
                    field_148438_c.error("Couldn\'t connect to world", exceptionmcoservice);
                }

                break;
            }
            catch (IOException ioexception)
            {
                field_148438_c.error("Couldn\'t parse response connecting to world", ioexception);
            }
            catch (Exception exception)
            {
                flag1 = true;
                field_148438_c.error("Couldn\'t connect to world", exception);
                this.func_148416_a(exception.getLocalizedMessage());
            }

            if (flag)
            {
                break;
            }

            this.func_148429_a(i);
        }

        if (flag2)
        {
            this.func_148413_b().func_147108_a(new GuiScreenReamlsTOS(this.field_148435_f, this.field_148437_e));
        }
        else if (!this.func_148418_c() && !flag1)
        {
            if (flag)
            {
                ServerAddress serveraddress = ServerAddress.func_78860_a(mcoserveraddress.field_148770_a);
                this.func_148432_a(serveraddress.getIP(), serveraddress.getPort());
            }
            else
            {
                this.func_148413_b().func_147108_a(this.field_148435_f);
            }
        }
    }

    private void func_148429_a(int p_148429_1_)
    {
        try
        {
            Thread.sleep((long)(p_148429_1_ * 1000));
        }
        catch (InterruptedException interruptedexception)
        {
            field_148438_c.warn(interruptedexception.getLocalizedMessage());
        }
    }

    private void func_148432_a(final String p_148432_1_, final int p_148432_2_)
    {
        (new Thread("MCO Connector #" + field_148439_a.incrementAndGet())
        {
            private static final String __OBFID = "CL_00000791";
            public void run()
            {
                try
                {
                    if (TaskOnlineConnect.this.func_148418_c())
                    {
                        return;
                    }

                    TaskOnlineConnect.this.field_148436_d = NetworkManager.func_150726_a(InetAddress.getByName(p_148432_1_), p_148432_2_);

                    if (TaskOnlineConnect.this.func_148418_c())
                    {
                        return;
                    }

                    TaskOnlineConnect.this.field_148436_d.func_150719_a(new NetHandlerLoginClient(TaskOnlineConnect.this.field_148436_d, TaskOnlineConnect.this.func_148413_b(), TaskOnlineConnect.this.field_148435_f));

                    if (TaskOnlineConnect.this.func_148418_c())
                    {
                        return;
                    }

                    TaskOnlineConnect.this.field_148436_d.func_150725_a(new C00Handshake(4, p_148432_1_, p_148432_2_, EnumConnectionState.LOGIN), new GenericFutureListener[0]);

                    if (TaskOnlineConnect.this.func_148418_c())
                    {
                        return;
                    }

                    TaskOnlineConnect.this.field_148436_d.func_150725_a(new C00PacketLoginStart(TaskOnlineConnect.this.func_148413_b().getSession().func_148256_e()), new GenericFutureListener[0]);
                    TaskOnlineConnect.this.func_148417_b(I18n.getStringParams("mco.connect.authorizing", new Object[0]));
                }
                catch (UnknownHostException unknownhostexception)
                {
                    if (TaskOnlineConnect.this.func_148418_c())
                    {
                        return;
                    }

                    TaskOnlineConnect.field_148438_c.error("Couldn\'t connect to world", unknownhostexception);
                    TaskOnlineConnect.this.func_148413_b().func_147108_a(new GuiScreenDisconnectedOnline(TaskOnlineConnect.this.field_148435_f, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", new Object[] {"Unknown host \'" + p_148432_1_ + "\'"})));
                }
                catch (Exception exception)
                {
                    if (TaskOnlineConnect.this.func_148418_c())
                    {
                        return;
                    }

                    TaskOnlineConnect.field_148438_c.error("Couldn\'t connect to world", exception);
                    TaskOnlineConnect.this.func_148413_b().func_147108_a(new GuiScreenDisconnectedOnline(TaskOnlineConnect.this.field_148435_f, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", new Object[] {exception.toString()})));
                }
            }
        }).start();
    }

    public void func_148414_a()
    {
        if (this.field_148436_d != null)
        {
            this.field_148436_d.processReadPackets();
        }
    }
}