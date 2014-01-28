package net.minecraft.client.multiplayer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.CLIENT)
public class GuiConnecting extends GuiScreen
{
    private static final AtomicInteger field_146372_a = new AtomicInteger(0);
    private static final Logger field_146370_f = LogManager.getLogger();
    private NetworkManager field_146371_g;
    private boolean field_146373_h;
    private final GuiScreen field_146374_i;
    private static final String __OBFID = "CL_00000685";

    public GuiConnecting(GuiScreen par1GuiScreen, Minecraft par2Minecraft, ServerData par3ServerData)
    {
        this.field_146297_k = par2Minecraft;
        this.field_146374_i = par1GuiScreen;
        ServerAddress serveraddress = ServerAddress.func_78860_a(par3ServerData.serverIP);
        par2Minecraft.loadWorld((WorldClient)null);
        par2Minecraft.setServerData(par3ServerData);
        this.func_146367_a(serveraddress.getIP(), serveraddress.getPort());
    }

    public GuiConnecting(GuiScreen par1GuiScreen, Minecraft par2Minecraft, String par3Str, int par4)
    {
        this.field_146297_k = par2Minecraft;
        this.field_146374_i = par1GuiScreen;
        par2Minecraft.loadWorld((WorldClient)null);
        this.func_146367_a(par3Str, par4);
    }

    private void func_146367_a(final String p_146367_1_, final int p_146367_2_)
    {
        field_146370_f.info("Connecting to " + p_146367_1_ + ", " + p_146367_2_);
        (new Thread("Server Connector #" + field_146372_a.incrementAndGet())
        {
            private static final String __OBFID = "CL_00000686";
            public void run()
            {
                try
                {
                    if (GuiConnecting.this.field_146373_h)
                    {
                        return;
                    }

                    GuiConnecting.this.field_146371_g = NetworkManager.func_150726_a(InetAddress.getByName(p_146367_1_), p_146367_2_);
                    GuiConnecting.this.field_146371_g.func_150719_a(new NetHandlerLoginClient(GuiConnecting.this.field_146371_g, GuiConnecting.this.field_146297_k, GuiConnecting.this.field_146374_i));
                    GuiConnecting.this.field_146371_g.func_150725_a(new C00Handshake(4, p_146367_1_, p_146367_2_, EnumConnectionState.LOGIN), new GenericFutureListener[0]);
                    GuiConnecting.this.field_146371_g.func_150725_a(new C00PacketLoginStart(GuiConnecting.this.field_146297_k.getSession().func_148256_e()), new GenericFutureListener[0]);
                }
                catch (UnknownHostException unknownhostexception)
                {
                    if (GuiConnecting.this.field_146373_h)
                    {
                        return;
                    }

                    GuiConnecting.field_146370_f.error("Couldn\'t connect to server", unknownhostexception);
                    GuiConnecting.this.field_146297_k.func_147108_a(new GuiDisconnected(GuiConnecting.this.field_146374_i, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", new Object[] {"Unknown host \'" + p_146367_1_ + "\'"})));
                }
                catch (Exception exception)
                {
                    if (GuiConnecting.this.field_146373_h)
                    {
                        return;
                    }

                    GuiConnecting.field_146370_f.error("Couldn\'t connect to server", exception);
                    GuiConnecting.this.field_146297_k.func_147108_a(new GuiDisconnected(GuiConnecting.this.field_146374_i, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", new Object[] {exception.toString()})));
                }
            }
        }).start();
    }

    // JAVADOC METHOD $$ func_73876_c
    public void updateScreen()
    {
        if (this.field_146371_g != null)
        {
            if (this.field_146371_g.func_150724_d())
            {
                this.field_146371_g.processReadPackets();
            }
            else if (this.field_146371_g.func_150730_f() != null)
            {
                this.field_146371_g.func_150729_e().func_147231_a(this.field_146371_g.func_150730_f());
            }
        }
    }

    // JAVADOC METHOD $$ func_73869_a
    protected void keyTyped(char par1, int par2) {}

    // JAVADOC METHOD $$ func_73866_w_
    public void initGui()
    {
        this.field_146292_n.clear();
        this.field_146292_n.add(new GuiButton(0, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 120 + 12, I18n.getStringParams("gui.cancel", new Object[0])));
    }

    protected void func_146284_a(GuiButton p_146284_1_)
    {
        if (p_146284_1_.field_146127_k == 0)
        {
            this.field_146373_h = true;

            if (this.field_146371_g != null)
            {
                this.field_146371_g.func_150718_a(new ChatComponentText("Aborted"));
            }

            this.field_146297_k.func_147108_a(this.field_146374_i);
        }
    }

    // JAVADOC METHOD $$ func_73863_a
    public void drawScreen(int par1, int par2, float par3)
    {
        this.func_146276_q_();

        if (this.field_146371_g == null)
        {
            this.drawCenteredString(this.field_146289_q, I18n.getStringParams("connect.connecting", new Object[0]), this.field_146294_l / 2, this.field_146295_m / 2 - 50, 16777215);
        }
        else
        {
            this.drawCenteredString(this.field_146289_q, I18n.getStringParams("connect.authorizing", new Object[0]), this.field_146294_l / 2, this.field_146295_m / 2 - 50, 16777215);
        }

        super.drawScreen(par1, par2, par3);
    }
}