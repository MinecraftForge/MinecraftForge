package net.minecraft.client.gui.mco;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.net.URI;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenConfigureWorld;
import net.minecraft.client.gui.GuiScreenConfirmation;
import net.minecraft.client.gui.GuiScreenLongRunningTask;
import net.minecraft.client.gui.GuiScreenSelectLocation;
import net.minecraft.client.gui.TaskLongRunning;
import net.minecraft.client.mco.Backup;
import net.minecraft.client.mco.ExceptionMcoService;
import net.minecraft.client.mco.McoClient;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class GuiScreenBackup extends GuiScreen
{
    private static final AtomicInteger field_146845_a = new AtomicInteger(0);
    private static final Logger field_146841_f = LogManager.getLogger();
    private final GuiScreenConfigureWorld field_146842_g;
    private final long field_146846_h;
    private List field_146847_i = Collections.emptyList();
    private GuiScreenBackup.SelectionList field_146844_r;
    private int field_146843_s = -1;
    private static final String __OBFID = "CL_00000766";

    public GuiScreenBackup(GuiScreenConfigureWorld par1GuiScreenConfigureWorld, long par2)
    {
        this.field_146842_g = par1GuiScreenConfigureWorld;
        this.field_146846_h = par2;
    }

    // JAVADOC METHOD $$ func_73866_w_
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.field_146292_n.clear();
        this.field_146844_r = new GuiScreenBackup.SelectionList();
        (new Thread("MCO Backup Requester #" + field_146845_a.incrementAndGet())
        {
            private static final String __OBFID = "CL_00000767";
            public void run()
            {
                Session session = GuiScreenBackup.this.field_146297_k.getSession();
                McoClient mcoclient = new McoClient(session.getSessionID(), session.getUsername(), "1.7.2", Minecraft.getMinecraft().getProxy());

                try
                {
                    GuiScreenBackup.this.field_146847_i = mcoclient.func_148704_d(GuiScreenBackup.this.field_146846_h).field_148797_a;
                }
                catch (ExceptionMcoService exceptionmcoservice)
                {
                    GuiScreenBackup.field_146841_f.error("Couldn\'t request backups", exceptionmcoservice);
                }
            }
        }).start();
        this.func_146840_h();
    }

    private void func_146840_h()
    {
        this.field_146292_n.add(new GuiButton(2, this.field_146294_l / 2 + 6, this.field_146295_m - 52, 153, 20, I18n.getStringParams("mco.backup.button.download", new Object[0])));
        this.field_146292_n.add(new GuiButton(1, this.field_146294_l / 2 - 154, this.field_146295_m - 52, 153, 20, I18n.getStringParams("mco.backup.button.restore", new Object[0])));
        this.field_146292_n.add(new GuiButton(0, this.field_146294_l / 2 - 74, this.field_146295_m - 52 + 25, 153, 20, I18n.getStringParams("gui.back", new Object[0])));
    }

    // JAVADOC METHOD $$ func_73876_c
    public void updateScreen()
    {
        super.updateScreen();
    }

    protected void func_146284_a(GuiButton p_146284_1_)
    {
        if (p_146284_1_.field_146124_l)
        {
            if (p_146284_1_.field_146127_k == 1)
            {
                this.func_146827_i();
            }
            else if (p_146284_1_.field_146127_k == 0)
            {
                this.field_146297_k.func_147108_a(this.field_146842_g);
            }
            else if (p_146284_1_.field_146127_k == 2)
            {
                this.func_146826_p();
            }
            else
            {
                this.field_146844_r.func_148357_a(p_146284_1_);
            }
        }
    }

    private void func_146827_i()
    {
        if (this.field_146843_s >= 0 && this.field_146843_s < this.field_146847_i.size())
        {
            Date date = ((Backup)this.field_146847_i.get(this.field_146843_s)).field_148778_b;
            String s = DateFormat.getDateTimeInstance(3, 3).format(date);
            String s1 = this.func_146829_a(Long.valueOf(System.currentTimeMillis() - date.getTime()));
            String s2 = I18n.getStringParams("mco.configure.world.restore.question.line1", new Object[0]) + " \'" + s + "\' (" + s1 + ")";
            String s3 = I18n.getStringParams("mco.configure.world.restore.question.line2", new Object[0]);
            this.field_146297_k.func_147108_a(new GuiScreenConfirmation(this, GuiScreenConfirmation.ConfirmationType.Warning, s2, s3, 1));
        }
    }

    private void func_146826_p()
    {
        String s = I18n.getStringParams("mco.configure.world.restore.download.question.line1", new Object[0]);
        String s1 = I18n.getStringParams("mco.configure.world.restore.download.question.line2", new Object[0]);
        this.field_146297_k.func_147108_a(new GuiScreenConfirmation(this, GuiScreenConfirmation.ConfirmationType.Info, s, s1, 2));
    }

    private void func_146821_q()
    {
        Session session = this.field_146297_k.getSession();
        McoClient mcoclient = new McoClient(session.getSessionID(), session.getUsername(), "1.7.2", Minecraft.getMinecraft().getProxy());

        try
        {
            String s = mcoclient.func_148708_h(this.field_146846_h);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(s), (ClipboardOwner)null);
            this.func_146823_a(s);
        }
        catch (ExceptionMcoService exceptionmcoservice)
        {
            field_146841_f.error("Couldn\'t download world data");
        }
    }

    private void func_146823_a(String p_146823_1_)
    {
        try
        {
            URI uri = new URI(p_146823_1_);
            Class oclass = Class.forName("java.awt.Desktop");
            Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
            oclass.getMethod("browse", new Class[] {URI.class}).invoke(object, new Object[] {uri});
        }
        catch (Throwable throwable)
        {
            field_146841_f.error("Couldn\'t open link");
        }
    }

    public void confirmClicked(boolean par1, int par2)
    {
        if (par1 && par2 == 1)
        {
            this.func_146839_r();
        }
        else if (par1 && par2 == 2)
        {
            this.func_146821_q();
        }
        else
        {
            this.field_146297_k.func_147108_a(this);
        }
    }

    private void func_146839_r()
    {
        Backup backup = (Backup)this.field_146847_i.get(this.field_146843_s);
        GuiScreenBackup.RestoreTask restoretask = new GuiScreenBackup.RestoreTask(backup, null);
        GuiScreenLongRunningTask guiscreenlongrunningtask = new GuiScreenLongRunningTask(this.field_146297_k, this.field_146842_g, restoretask);
        guiscreenlongrunningtask.func_146902_g();
        this.field_146297_k.func_147108_a(guiscreenlongrunningtask);
    }

    // JAVADOC METHOD $$ func_73863_a
    public void drawScreen(int par1, int par2, float par3)
    {
        this.func_146276_q_();
        this.field_146844_r.func_148350_a(par1, par2, par3);
        this.drawCenteredString(this.field_146289_q, I18n.getStringParams("mco.backup.title", new Object[0]), this.field_146294_l / 2, 20, 16777215);
        super.drawScreen(par1, par2, par3);
    }

    private String func_146829_a(Long p_146829_1_)
    {
        if (p_146829_1_.longValue() < 0L)
        {
            return "right now";
        }
        else
        {
            long i = p_146829_1_.longValue() / 1000L;

            if (i < 60L)
            {
                return (i == 1L ? "1 second" : i + " seconds") + " ago";
            }
            else
            {
                long j;

                if (i < 3600L)
                {
                    j = i / 60L;
                    return (j == 1L ? "1 minute" : j + " minutes") + " ago";
                }
                else if (i < 86400L)
                {
                    j = i / 3600L;
                    return (j == 1L ? "1 hour" : j + " hours") + " ago";
                }
                else
                {
                    j = i / 86400L;
                    return (j == 1L ? "1 day" : j + " days") + " ago";
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    class RestoreTask extends TaskLongRunning
    {
        private final Backup field_148424_c;
        private static final String __OBFID = "CL_00000769";

        private RestoreTask(Backup par2Backup)
        {
            this.field_148424_c = par2Backup;
        }

        public void run()
        {
            this.func_148417_b(I18n.getStringParams("mco.backup.restoring", new Object[0]));

            try
            {
                if (this.func_148418_c())
                {
                    return;
                }

                Session session = GuiScreenBackup.this.field_146297_k.getSession();
                McoClient mcoclient = new McoClient(session.getSessionID(), session.getUsername(), "1.7.2", Minecraft.getMinecraft().getProxy());
                mcoclient.func_148712_c(GuiScreenBackup.this.field_146846_h, this.field_148424_c.field_148780_a);

                try
                {
                    Thread.sleep(1000L);
                }
                catch (InterruptedException interruptedexception)
                {
                    Thread.currentThread().interrupt();
                }

                if (this.func_148418_c())
                {
                    return;
                }

                this.func_148413_b().func_147108_a(GuiScreenBackup.this.field_146842_g);
            }
            catch (ExceptionMcoService exceptionmcoservice)
            {
                if (this.func_148418_c())
                {
                    return;
                }

                GuiScreenBackup.field_146841_f.error("Couldn\'t restore backup");
                this.func_148416_a(exceptionmcoservice.toString());
            }
            catch (Exception exception)
            {
                if (this.func_148418_c())
                {
                    return;
                }

                GuiScreenBackup.field_146841_f.error("Couldn\'t restore backup");
                this.func_148416_a(exception.getLocalizedMessage());
            }
        }

        RestoreTask(Backup par2Backup, Object par3GuiScreenBackupDownloadThread)
        {
            this(par2Backup);
        }
    }

    @SideOnly(Side.CLIENT)
    class SelectionList extends GuiScreenSelectLocation
    {
        private static final String __OBFID = "CL_00000768";

        public SelectionList()
        {
            super(GuiScreenBackup.this.field_146297_k, GuiScreenBackup.this.field_146294_l, GuiScreenBackup.this.field_146295_m, 32, GuiScreenBackup.this.field_146295_m - 64, 36);
        }

        protected int func_148355_a()
        {
            return GuiScreenBackup.this.field_146847_i.size() + 1;
        }

        protected void func_148352_a(int p_148352_1_, boolean p_148352_2_)
        {
            if (p_148352_1_ < GuiScreenBackup.this.field_146847_i.size())
            {
                GuiScreenBackup.this.field_146843_s = p_148352_1_;
            }
        }

        protected boolean func_148356_a(int p_148356_1_)
        {
            return p_148356_1_ == GuiScreenBackup.this.field_146843_s;
        }

        protected boolean func_148349_b(int p_148349_1_)
        {
            return false;
        }

        protected int func_148351_b()
        {
            return this.func_148355_a() * 36;
        }

        protected void func_148358_c()
        {
            GuiScreenBackup.this.func_146276_q_();
        }

        protected void func_148348_a(int p_148348_1_, int p_148348_2_, int p_148348_3_, int p_148348_4_, Tessellator p_148348_5_)
        {
            if (p_148348_1_ < GuiScreenBackup.this.field_146847_i.size())
            {
                this.func_148385_b(p_148348_1_, p_148348_2_, p_148348_3_, p_148348_4_, p_148348_5_);
            }
        }

        private void func_148385_b(int p_148385_1_, int p_148385_2_, int p_148385_3_, int p_148385_4_, Tessellator p_148385_5_)
        {
            Backup backup = (Backup)GuiScreenBackup.this.field_146847_i.get(p_148385_1_);
            GuiScreenBackup.this.drawString(GuiScreenBackup.this.field_146289_q, "Backup (" + GuiScreenBackup.this.func_146829_a(Long.valueOf(MinecraftServer.getSystemTimeMillis() - backup.field_148778_b.getTime())) + ")", p_148385_2_ + 2, p_148385_3_ + 1, 16777215);
            GuiScreenBackup.this.drawString(GuiScreenBackup.this.field_146289_q, this.func_148384_a(backup.field_148778_b), p_148385_2_ + 2, p_148385_3_ + 12, 7105644);
        }

        private String func_148384_a(Date p_148384_1_)
        {
            return DateFormat.getDateTimeInstance(3, 3).format(p_148384_1_);
        }
    }
}