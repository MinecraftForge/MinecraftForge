package net.minecraft.client.gui;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.LanServerDetector;
import net.minecraft.client.network.OldServerPinger;
import net.minecraft.client.resources.I18n;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class GuiMultiplayer extends GuiScreen
{
    private static final Logger field_146802_a = LogManager.getLogger();
    private final OldServerPinger field_146797_f = new OldServerPinger();
    private GuiScreen field_146798_g;
    private ServerSelectionList field_146803_h;
    private ServerList field_146804_i;
    private GuiButton field_146810_r;
    private GuiButton field_146809_s;
    private GuiButton field_146808_t;
    private boolean field_146807_u;
    private boolean field_146806_v;
    private boolean field_146805_w;
    private boolean field_146813_x;
    private String field_146812_y;
    private ServerData field_146811_z;
    private LanServerDetector.LanServerList field_146799_A;
    private LanServerDetector.ThreadLanServerFind field_146800_B;
    private boolean field_146801_C;
    private static final String __OBFID = "CL_00000814";

    public GuiMultiplayer(GuiScreen par1GuiScreen)
    {
        this.field_146798_g = par1GuiScreen;
        FMLClientHandler.instance().setupServerList();
    }

    // JAVADOC METHOD $$ func_73866_w_
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.field_146292_n.clear();

        if (!this.field_146801_C)
        {
            this.field_146801_C = true;
            this.field_146804_i = new ServerList(this.field_146297_k);
            this.field_146804_i.loadServerList();
            this.field_146799_A = new LanServerDetector.LanServerList();

            try
            {
                this.field_146800_B = new LanServerDetector.ThreadLanServerFind(this.field_146799_A);
                this.field_146800_B.start();
            }
            catch (Exception exception)
            {
                field_146802_a.warn("Unable to start LAN server detection: " + exception.getMessage());
            }

            this.field_146803_h = new ServerSelectionList(this, this.field_146297_k, this.field_146294_l, this.field_146295_m, 32, this.field_146295_m - 64, 36);
            this.field_146803_h.func_148195_a(this.field_146804_i);
        }
        else
        {
            this.field_146803_h.func_148122_a(this.field_146294_l, this.field_146295_m, 32, this.field_146295_m - 64);
        }

        this.func_146794_g();
    }

    public void func_146794_g()
    {
        this.field_146292_n.add(this.field_146810_r = new GuiButton(7, this.field_146294_l / 2 - 154, this.field_146295_m - 28, 70, 20, I18n.getStringParams("selectServer.edit", new Object[0])));
        this.field_146292_n.add(this.field_146808_t = new GuiButton(2, this.field_146294_l / 2 - 74, this.field_146295_m - 28, 70, 20, I18n.getStringParams("selectServer.delete", new Object[0])));
        this.field_146292_n.add(this.field_146809_s = new GuiButton(1, this.field_146294_l / 2 - 154, this.field_146295_m - 52, 100, 20, I18n.getStringParams("selectServer.select", new Object[0])));
        this.field_146292_n.add(new GuiButton(4, this.field_146294_l / 2 - 50, this.field_146295_m - 52, 100, 20, I18n.getStringParams("selectServer.direct", new Object[0])));
        this.field_146292_n.add(new GuiButton(3, this.field_146294_l / 2 + 4 + 50, this.field_146295_m - 52, 100, 20, I18n.getStringParams("selectServer.add", new Object[0])));
        this.field_146292_n.add(new GuiButton(8, this.field_146294_l / 2 + 4, this.field_146295_m - 28, 70, 20, I18n.getStringParams("selectServer.refresh", new Object[0])));
        this.field_146292_n.add(new GuiButton(0, this.field_146294_l / 2 + 4 + 76, this.field_146295_m - 28, 75, 20, I18n.getStringParams("gui.cancel", new Object[0])));
        this.func_146790_a(this.field_146803_h.func_148193_k());
    }

    // JAVADOC METHOD $$ func_73876_c
    public void updateScreen()
    {
        super.updateScreen();

        if (this.field_146799_A.getWasUpdated())
        {
            List list = this.field_146799_A.getLanServers();
            this.field_146799_A.setWasNotUpdated();
            this.field_146803_h.func_148194_a(list);
        }

        this.field_146797_f.func_147223_a();
    }

    public void func_146281_b()
    {
        Keyboard.enableRepeatEvents(false);

        if (this.field_146800_B != null)
        {
            this.field_146800_B.interrupt();
            this.field_146800_B = null;
        }

        this.field_146797_f.func_147226_b();
    }

    protected void func_146284_a(GuiButton p_146284_1_)
    {
        if (p_146284_1_.field_146124_l)
        {
            GuiListExtended.IGuiListEntry iguilistentry = this.field_146803_h.func_148193_k() < 0 ? null : this.field_146803_h.func_148180_b(this.field_146803_h.func_148193_k());

            if (p_146284_1_.field_146127_k == 2 && iguilistentry instanceof ServerListEntryNormal)
            {
                String s4 = ((ServerListEntryNormal)iguilistentry).func_148296_a().serverName;

                if (s4 != null)
                {
                    this.field_146807_u = true;
                    String s = I18n.getStringParams("selectServer.deleteQuestion", new Object[0]);
                    String s1 = "\'" + s4 + "\' " + I18n.getStringParams("selectServer.deleteWarning", new Object[0]);
                    String s2 = I18n.getStringParams("selectServer.deleteButton", new Object[0]);
                    String s3 = I18n.getStringParams("gui.cancel", new Object[0]);
                    GuiYesNo guiyesno = new GuiYesNo(this, s, s1, s2, s3, this.field_146803_h.func_148193_k());
                    this.field_146297_k.func_147108_a(guiyesno);
                }
            }
            else if (p_146284_1_.field_146127_k == 1)
            {
                this.func_146796_h();
            }
            else if (p_146284_1_.field_146127_k == 4)
            {
                this.field_146813_x = true;
                this.field_146297_k.func_147108_a(new GuiScreenServerList(this, this.field_146811_z = new ServerData(I18n.getStringParams("selectServer.defaultName", new Object[0]), "")));
            }
            else if (p_146284_1_.field_146127_k == 3)
            {
                this.field_146806_v = true;
                this.field_146297_k.func_147108_a(new GuiScreenAddServer(this, this.field_146811_z = new ServerData(I18n.getStringParams("selectServer.defaultName", new Object[0]), "")));
            }
            else if (p_146284_1_.field_146127_k == 7 && iguilistentry instanceof ServerListEntryNormal)
            {
                this.field_146805_w = true;
                ServerData serverdata = ((ServerListEntryNormal)iguilistentry).func_148296_a();
                this.field_146811_z = new ServerData(serverdata.serverName, serverdata.serverIP);
                this.field_146811_z.setHideAddress(serverdata.isHidingAddress());
                this.field_146297_k.func_147108_a(new GuiScreenAddServer(this, this.field_146811_z));
            }
            else if (p_146284_1_.field_146127_k == 0)
            {
                this.field_146297_k.func_147108_a(this.field_146798_g);
            }
            else if (p_146284_1_.field_146127_k == 8)
            {
                this.func_146792_q();
            }
        }
    }

    private void func_146792_q()
    {
        this.field_146297_k.func_147108_a(new GuiMultiplayer(this.field_146798_g));
    }

    public void confirmClicked(boolean par1, int par2)
    {
        GuiListExtended.IGuiListEntry iguilistentry = this.field_146803_h.func_148193_k() < 0 ? null : this.field_146803_h.func_148180_b(this.field_146803_h.func_148193_k());

        if (this.field_146807_u)
        {
            this.field_146807_u = false;

            if (par1 && iguilistentry instanceof ServerListEntryNormal)
            {
                this.field_146804_i.removeServerData(this.field_146803_h.func_148193_k());
                this.field_146804_i.saveServerList();
                this.field_146803_h.func_148192_c(-1);
                this.field_146803_h.func_148195_a(this.field_146804_i);
            }

            this.field_146297_k.func_147108_a(this);
        }
        else if (this.field_146813_x)
        {
            this.field_146813_x = false;

            if (par1)
            {
                this.func_146791_a(this.field_146811_z);
            }
            else
            {
                this.field_146297_k.func_147108_a(this);
            }
        }
        else if (this.field_146806_v)
        {
            this.field_146806_v = false;

            if (par1)
            {
                this.field_146804_i.addServerData(this.field_146811_z);
                this.field_146804_i.saveServerList();
                this.field_146803_h.func_148192_c(-1);
                this.field_146803_h.func_148195_a(this.field_146804_i);
            }

            this.field_146297_k.func_147108_a(this);
        }
        else if (this.field_146805_w)
        {
            this.field_146805_w = false;

            if (par1 && iguilistentry instanceof ServerListEntryNormal)
            {
                ServerData serverdata = ((ServerListEntryNormal)iguilistentry).func_148296_a();
                serverdata.serverName = this.field_146811_z.serverName;
                serverdata.serverIP = this.field_146811_z.serverIP;
                serverdata.setHideAddress(this.field_146811_z.isHidingAddress());
                this.field_146804_i.saveServerList();
                this.field_146803_h.func_148195_a(this.field_146804_i);
            }

            this.field_146297_k.func_147108_a(this);
        }
    }

    // JAVADOC METHOD $$ func_73869_a
    protected void keyTyped(char par1, int par2)
    {
        int j = this.field_146803_h.func_148193_k();
        GuiListExtended.IGuiListEntry iguilistentry = j < 0 ? null : this.field_146803_h.func_148180_b(j);

        if (par2 == 63)
        {
            this.func_146792_q();
        }
        else
        {
            if (j >= 0)
            {
                if (par2 == 200)
                {
                    if (func_146272_n())
                    {
                        if (j > 0 && iguilistentry instanceof ServerListEntryNormal)
                        {
                            this.field_146804_i.swapServers(j, j - 1);
                            this.func_146790_a(this.field_146803_h.func_148193_k() - 1);
                            this.field_146803_h.func_148145_f(-this.field_146803_h.func_148146_j());
                            this.field_146803_h.func_148195_a(this.field_146804_i);
                        }
                    }
                    else if (j > 0)
                    {
                        this.func_146790_a(this.field_146803_h.func_148193_k() - 1);
                        this.field_146803_h.func_148145_f(-this.field_146803_h.func_148146_j());

                        if (this.field_146803_h.func_148180_b(this.field_146803_h.func_148193_k()) instanceof ServerListEntryLanScan)
                        {
                            if (this.field_146803_h.func_148193_k() > 0)
                            {
                                this.func_146790_a(this.field_146803_h.func_148127_b() - 1);
                                this.field_146803_h.func_148145_f(-this.field_146803_h.func_148146_j());
                            }
                            else
                            {
                                this.func_146790_a(-1);
                            }
                        }
                    }
                    else
                    {
                        this.func_146790_a(-1);
                    }
                }
                else if (par2 == 208)
                {
                    if (func_146272_n())
                    {
                        if (j < this.field_146804_i.countServers() - 1)
                        {
                            this.field_146804_i.swapServers(j, j + 1);
                            this.func_146790_a(j + 1);
                            this.field_146803_h.func_148145_f(this.field_146803_h.func_148146_j());
                            this.field_146803_h.func_148195_a(this.field_146804_i);
                        }
                    }
                    else if (j < this.field_146803_h.func_148127_b())
                    {
                        this.func_146790_a(this.field_146803_h.func_148193_k() + 1);
                        this.field_146803_h.func_148145_f(this.field_146803_h.func_148146_j());

                        if (this.field_146803_h.func_148180_b(this.field_146803_h.func_148193_k()) instanceof ServerListEntryLanScan)
                        {
                            if (this.field_146803_h.func_148193_k() < this.field_146803_h.func_148127_b() - 1)
                            {
                                this.func_146790_a(this.field_146803_h.func_148127_b() + 1);
                                this.field_146803_h.func_148145_f(this.field_146803_h.func_148146_j());
                            }
                            else
                            {
                                this.func_146790_a(-1);
                            }
                        }
                    }
                    else
                    {
                        this.func_146790_a(-1);
                    }
                }
                else if (par2 != 28 && par2 != 156)
                {
                    super.keyTyped(par1, par2);
                }
                else
                {
                    this.func_146284_a((GuiButton)this.field_146292_n.get(2));
                }
            }
            else
            {
                super.keyTyped(par1, par2);
            }
        }
    }

    // JAVADOC METHOD $$ func_73863_a
    public void drawScreen(int par1, int par2, float par3)
    {
        this.field_146812_y = null;
        this.func_146276_q_();
        this.field_146803_h.func_148128_a(par1, par2, par3);
        this.drawCenteredString(this.field_146289_q, I18n.getStringParams("multiplayer.title", new Object[0]), this.field_146294_l / 2, 20, 16777215);
        super.drawScreen(par1, par2, par3);

        if (this.field_146812_y != null)
        {
            this.func_146283_a(Lists.newArrayList(Splitter.on("\n").split(this.field_146812_y)), par1, par2);
        }
    }

    public void func_146796_h()
    {
        GuiListExtended.IGuiListEntry iguilistentry = this.field_146803_h.func_148193_k() < 0 ? null : this.field_146803_h.func_148180_b(this.field_146803_h.func_148193_k());

        if (iguilistentry instanceof ServerListEntryNormal)
        {
            this.func_146791_a(((ServerListEntryNormal)iguilistentry).func_148296_a());
        }
        else if (iguilistentry instanceof ServerListEntryLanDetected)
        {
            LanServerDetector.LanServer lanserver = ((ServerListEntryLanDetected)iguilistentry).func_148289_a();
            this.func_146791_a(new ServerData(lanserver.getServerMotd(), lanserver.getServerIpPort()));
        }
    }

    private void func_146791_a(ServerData p_146791_1_)
    {
        FMLClientHandler.instance().connectToServer(this, p_146791_1_);
    }

    public void func_146790_a(int p_146790_1_)
    {
        this.field_146803_h.func_148192_c(p_146790_1_);
        GuiListExtended.IGuiListEntry iguilistentry = p_146790_1_ < 0 ? null : this.field_146803_h.func_148180_b(p_146790_1_);
        this.field_146809_s.field_146124_l = false;
        this.field_146810_r.field_146124_l = false;
        this.field_146808_t.field_146124_l = false;

        if (iguilistentry != null && !(iguilistentry instanceof ServerListEntryLanScan))
        {
            this.field_146809_s.field_146124_l = true;

            if (iguilistentry instanceof ServerListEntryNormal)
            {
                this.field_146810_r.field_146124_l = true;
                this.field_146808_t.field_146124_l = true;
            }
        }
    }

    public OldServerPinger func_146789_i()
    {
        return this.field_146797_f;
    }

    public void func_146793_a(String p_146793_1_)
    {
        this.field_146812_y = p_146793_1_;
    }

    // JAVADOC METHOD $$ func_73864_a
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
        this.field_146803_h.func_148179_a(par1, par2, par3);
    }

    protected void func_146286_b(int p_146286_1_, int p_146286_2_, int p_146286_3_)
    {
        super.func_146286_b(p_146286_1_, p_146286_2_, p_146286_3_);
        this.field_146803_h.func_148181_b(p_146286_1_, p_146286_2_, p_146286_3_);
    }

    public ServerList func_146795_p()
    {
        return this.field_146804_i;
    }
}