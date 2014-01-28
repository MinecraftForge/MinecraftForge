package net.minecraft.client.gui;

import cpw.mods.fml.client.GuiModList;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.mco.ExceptionMcoService;
import net.minecraft.client.mco.ExceptionRetryCall;
import net.minecraft.client.mco.GuiScreenClientOutdated;
import net.minecraft.client.mco.McoClient;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.client.ForgeHooksClient;
import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

@SideOnly(Side.CLIENT)
public class GuiMainMenu extends GuiScreen
{
    private static final AtomicInteger field_146973_f = new AtomicInteger(0);
    private static final Logger field_146974_g = LogManager.getLogger();
    // JAVADOC FIELD $$ field_73976_a
    private static final Random rand = new Random();
    // JAVADOC FIELD $$ field_73974_b
    private float updateCounter;
    // JAVADOC FIELD $$ field_73975_c
    private String splashText;
    private GuiButton buttonResetDemo;
    // JAVADOC FIELD $$ field_73979_m
    private int panoramaTimer;
    // JAVADOC FIELD $$ field_73977_n
    private DynamicTexture viewportTexture;
    private boolean field_96141_q = true;
    private static boolean field_96140_r;
    private static boolean field_96139_s;
    private final Object field_104025_t = new Object();
    private String field_92025_p;
    private String field_146972_A;
    private String field_104024_v;
    private static final ResourceLocation splashTexts = new ResourceLocation("texts/splashes.txt");
    private static final ResourceLocation minecraftTitleTextures = new ResourceLocation("textures/gui/title/minecraft.png");
    // JAVADOC FIELD $$ field_73978_o
    private static final ResourceLocation[] titlePanoramaPaths = new ResourceLocation[] {new ResourceLocation("textures/gui/title/background/panorama_0.png"), new ResourceLocation("textures/gui/title/background/panorama_1.png"), new ResourceLocation("textures/gui/title/background/panorama_2.png"), new ResourceLocation("textures/gui/title/background/panorama_3.png"), new ResourceLocation("textures/gui/title/background/panorama_4.png"), new ResourceLocation("textures/gui/title/background/panorama_5.png")};
    public static final String field_96138_a = "Please click " + EnumChatFormatting.UNDERLINE + "here" + EnumChatFormatting.RESET + " for more information.";
    private int field_92024_r;
    private int field_92023_s;
    private int field_92022_t;
    private int field_92021_u;
    private int field_92020_v;
    private int field_92019_w;
    private ResourceLocation field_110351_G;
    private GuiButton minecraftRealmsButton;
    private static final String __OBFID = "CL_00001154";

    private GuiButton fmlModButton = null;

    public GuiMainMenu()
    {
        this.field_146972_A = field_96138_a;
        this.splashText = "missingno";
        BufferedReader bufferedreader = null;

        try
        {
            ArrayList arraylist = new ArrayList();
            bufferedreader = new BufferedReader(new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(splashTexts).getInputStream(), Charsets.UTF_8));
            String s;

            while ((s = bufferedreader.readLine()) != null)
            {
                s = s.trim();

                if (!s.isEmpty())
                {
                    arraylist.add(s);
                }
            }

            if (!arraylist.isEmpty())
            {
                do
                {
                    this.splashText = (String)arraylist.get(rand.nextInt(arraylist.size()));
                }
                while (this.splashText.hashCode() == 125780783);
            }
        }
        catch (IOException ioexception1)
        {
            ;
        }
        finally
        {
            if (bufferedreader != null)
            {
                try
                {
                    bufferedreader.close();
                }
                catch (IOException ioexception)
                {
                    ;
                }
            }
        }

        this.updateCounter = rand.nextFloat();
        this.field_92025_p = "";

        if (!OpenGlHelper.field_148827_a)
        {
            this.field_92025_p = "Old graphics card detected; this may prevent you from";
            this.field_146972_A = "playing in the far future as OpenGL 2.1 will be required.";
            this.field_104024_v = "https://help.mojang.com/customer/portal/articles/325948?ref=game";
        }
    }

    // JAVADOC METHOD $$ func_73876_c
    public void updateScreen()
    {
        ++this.panoramaTimer;
    }

    // JAVADOC METHOD $$ func_73868_f
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    // JAVADOC METHOD $$ func_73869_a
    protected void keyTyped(char par1, int par2) {}

    // JAVADOC METHOD $$ func_73866_w_
    public void initGui()
    {
        this.viewportTexture = new DynamicTexture(256, 256);
        this.field_110351_G = this.field_146297_k.getTextureManager().getDynamicTextureLocation("background", this.viewportTexture);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        if (calendar.get(2) + 1 == 11 && calendar.get(5) == 9)
        {
            this.splashText = "Happy birthday, ez!";
        }
        else if (calendar.get(2) + 1 == 6 && calendar.get(5) == 1)
        {
            this.splashText = "Happy birthday, Notch!";
        }
        else if (calendar.get(2) + 1 == 12 && calendar.get(5) == 24)
        {
            this.splashText = "Merry X-mas!";
        }
        else if (calendar.get(2) + 1 == 1 && calendar.get(5) == 1)
        {
            this.splashText = "Happy new year!";
        }
        else if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31)
        {
            this.splashText = "OOoooOOOoooo! Spooky!";
        }

        boolean flag = true;
        int i = this.field_146295_m / 4 + 48;

        if (this.field_146297_k.isDemo())
        {
            this.addDemoButtons(i, 24);
        }
        else
        {
            this.addSingleplayerMultiplayerButtons(i, 24);
        }

        this.func_130020_g();
        this.field_146292_n.add(new GuiButton(0, this.field_146294_l / 2 - 100, i + 72 + 12, 98, 20, I18n.getStringParams("menu.options", new Object[0])));
        this.field_146292_n.add(new GuiButton(4, this.field_146294_l / 2 + 2, i + 72 + 12, 98, 20, I18n.getStringParams("menu.quit", new Object[0])));
        this.field_146292_n.add(new GuiButtonLanguage(5, this.field_146294_l / 2 - 124, i + 72 + 12));
        Object object = this.field_104025_t;

        synchronized (this.field_104025_t)
        {
            this.field_92023_s = this.field_146289_q.getStringWidth(this.field_92025_p);
            this.field_92024_r = this.field_146289_q.getStringWidth(this.field_146972_A);
            int j = Math.max(this.field_92023_s, this.field_92024_r);
            this.field_92022_t = (this.field_146294_l - j) / 2;
            this.field_92021_u = ((GuiButton)this.field_146292_n.get(0)).field_146129_i - 24;
            this.field_92020_v = this.field_92022_t + j;
            this.field_92019_w = this.field_92021_u + 24;
        }
    }

    private void func_130020_g()
    {
        if (this.field_96141_q)
        {
            if (!field_96140_r)
            {
                field_96140_r = true;
                (new Thread("MCO Availability Checker #" + field_146973_f.incrementAndGet())
                {
                    private static final String __OBFID = "CL_00001155";
                    public void run()
                    {
                        Session session = GuiMainMenu.this.field_146297_k.getSession();
                        McoClient mcoclient = new McoClient(session.getSessionID(), session.getUsername(), "1.7.2", Minecraft.getMinecraft().getProxy());
                        boolean flag = false;

                        for (int i = 0; i < 3; ++i)
                        {
                            try
                            {
                                Boolean obool = mcoclient.func_148687_b();

                                if (obool.booleanValue())
                                {
                                    GuiMainMenu.this.func_130022_h();
                                }

                                GuiMainMenu.field_96139_s = obool.booleanValue();
                            }
                            catch (ExceptionRetryCall exceptionretrycall)
                            {
                                flag = true;
                            }
                            catch (ExceptionMcoService exceptionmcoservice)
                            {
                                GuiMainMenu.field_146974_g.error("Couldn\'t connect to Realms");
                            }
                            catch (IOException ioexception)
                            {
                                GuiMainMenu.field_146974_g.error("Couldn\'t parse response connecting to Realms");
                            }

                            if (!flag)
                            {
                                break;
                            }

                            try
                            {
                                Thread.sleep(10000L);
                            }
                            catch (InterruptedException interruptedexception)
                            {
                                Thread.currentThread().interrupt();
                            }
                        }
                    }
                }).start();
            }
            else if (field_96139_s)
            {
                this.func_130022_h();
            }
        }
    }

    private void func_130022_h()
    {
        this.minecraftRealmsButton.field_146125_m = true;
        fmlModButton.field_146120_f = 98;
        fmlModButton.field_146128_h = this.field_146294_l / 2 + 2;
    }

    // JAVADOC METHOD $$ func_73969_a
    private void addSingleplayerMultiplayerButtons(int par1, int par2)
    {
        this.field_146292_n.add(new GuiButton(1, this.field_146294_l / 2 - 100, par1, I18n.getStringParams("menu.singleplayer", new Object[0])));
        this.field_146292_n.add(new GuiButton(2, this.field_146294_l / 2 - 100, par1 + par2 * 1, I18n.getStringParams("menu.multiplayer", new Object[0])));
        //If Minecraft Realms is enabled, halve the size of both buttons and set them next to eachother.
        fmlModButton = new GuiButton(6, this.field_146294_l / 2 - 100, par1 + par2 * 2, "Mods");
        this.field_146292_n.add(fmlModButton);

        minecraftRealmsButton = new GuiButton(14, this.field_146294_l / 2 - 100, par1 + par2 * 2, I18n.getStringParams("menu.online"));
        minecraftRealmsButton.field_146120_f = 98;
        minecraftRealmsButton.field_146128_h = this.field_146294_l / 2 - 100;
        this.field_146292_n.add(minecraftRealmsButton);
        this.minecraftRealmsButton.field_146125_m = false;
    }

    // JAVADOC METHOD $$ func_73972_b
    private void addDemoButtons(int par1, int par2)
    {
        this.field_146292_n.add(new GuiButton(11, this.field_146294_l / 2 - 100, par1, I18n.getStringParams("menu.playdemo", new Object[0])));
        this.field_146292_n.add(this.buttonResetDemo = new GuiButton(12, this.field_146294_l / 2 - 100, par1 + par2 * 1, I18n.getStringParams("menu.resetdemo", new Object[0])));
        ISaveFormat isaveformat = this.field_146297_k.getSaveLoader();
        WorldInfo worldinfo = isaveformat.getWorldInfo("Demo_World");

        if (worldinfo == null)
        {
            this.buttonResetDemo.field_146124_l = false;
        }
    }

    protected void func_146284_a(GuiButton p_146284_1_)
    {
        if (p_146284_1_.field_146127_k == 0)
        {
            this.field_146297_k.func_147108_a(new GuiOptions(this, this.field_146297_k.gameSettings));
        }

        if (p_146284_1_.field_146127_k == 5)
        {
            this.field_146297_k.func_147108_a(new GuiLanguage(this, this.field_146297_k.gameSettings, this.field_146297_k.getLanguageManager()));
        }

        if (p_146284_1_.field_146127_k == 1)
        {
            this.field_146297_k.func_147108_a(new GuiSelectWorld(this));
        }

        if (p_146284_1_.field_146127_k == 2)
        {
            this.field_146297_k.func_147108_a(new GuiMultiplayer(this));
        }

        if (p_146284_1_.field_146127_k == 14 && this.minecraftRealmsButton.field_146125_m)
        {
            this.func_140005_i();
        }

        if (p_146284_1_.field_146127_k == 4)
        {
            this.field_146297_k.shutdown();
        }

        if (p_146284_1_.field_146127_k == 6)
        {
            this.field_146297_k.func_147108_a(new GuiModList(this));
        }

        if (p_146284_1_.field_146127_k == 11)
        {
            this.field_146297_k.launchIntegratedServer("Demo_World", "Demo_World", DemoWorldServer.demoWorldSettings);
        }

        if (p_146284_1_.field_146127_k == 12)
        {
            ISaveFormat isaveformat = this.field_146297_k.getSaveLoader();
            WorldInfo worldinfo = isaveformat.getWorldInfo("Demo_World");

            if (worldinfo != null)
            {
                GuiYesNo guiyesno = GuiSelectWorld.func_146623_a(this, worldinfo.getWorldName(), 12);
                this.field_146297_k.func_147108_a(guiyesno);
            }
        }
    }

    private void func_140005_i()
    {
        Session session = this.field_146297_k.getSession();
        McoClient mcoclient = new McoClient(session.getSessionID(), session.getUsername(), "1.7.2", Minecraft.getMinecraft().getProxy());

        try
        {
            if (mcoclient.func_148695_c().booleanValue())
            {
                this.field_146297_k.func_147108_a(new GuiScreenClientOutdated(this));
            }
            else
            {
                this.field_146297_k.func_147108_a(new GuiScreenOnlineServers(this));
            }
        }
        catch (ExceptionMcoService exceptionmcoservice)
        {
            field_146974_g.error("Couldn\'t connect to realms");
        }
        catch (IOException ioexception)
        {
            field_146974_g.error("Couldn\'t connect to realms");
        }
    }

    public void confirmClicked(boolean par1, int par2)
    {
        if (par1 && par2 == 12)
        {
            ISaveFormat isaveformat = this.field_146297_k.getSaveLoader();
            isaveformat.flushCache();
            isaveformat.deleteWorldDirectory("Demo_World");
            this.field_146297_k.func_147108_a(this);
        }
        else if (par2 == 13)
        {
            if (par1)
            {
                try
                {
                    Class oclass = Class.forName("java.awt.Desktop");
                    Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
                    oclass.getMethod("browse", new Class[] {URI.class}).invoke(object, new Object[] {new URI(this.field_104024_v)});
                }
                catch (Throwable throwable)
                {
                    field_146974_g.error("Couldn\'t open link", throwable);
                }
            }

            this.field_146297_k.func_147108_a(this);
        }
    }

    // JAVADOC METHOD $$ func_73970_b
    private void drawPanorama(int par1, int par2, float par3)
    {
        Tessellator tessellator = Tessellator.instance;
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDepthMask(false);
        OpenGlHelper.func_148821_a(770, 771, 1, 0);
        byte b0 = 8;

        for (int k = 0; k < b0 * b0; ++k)
        {
            GL11.glPushMatrix();
            float f1 = ((float)(k % b0) / (float)b0 - 0.5F) / 64.0F;
            float f2 = ((float)(k / b0) / (float)b0 - 0.5F) / 64.0F;
            float f3 = 0.0F;
            GL11.glTranslatef(f1, f2, f3);
            GL11.glRotatef(MathHelper.sin(((float)this.panoramaTimer + par3) / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(-((float)this.panoramaTimer + par3) * 0.1F, 0.0F, 1.0F, 0.0F);

            for (int l = 0; l < 6; ++l)
            {
                GL11.glPushMatrix();

                if (l == 1)
                {
                    GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                }

                if (l == 2)
                {
                    GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                }

                if (l == 3)
                {
                    GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                }

                if (l == 4)
                {
                    GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                }

                if (l == 5)
                {
                    GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                }

                this.field_146297_k.getTextureManager().bindTexture(titlePanoramaPaths[l]);
                tessellator.startDrawingQuads();
                tessellator.setColorRGBA_I(16777215, 255 / (k + 1));
                float f4 = 0.0F;
                tessellator.addVertexWithUV(-1.0D, -1.0D, 1.0D, (double)(0.0F + f4), (double)(0.0F + f4));
                tessellator.addVertexWithUV(1.0D, -1.0D, 1.0D, (double)(1.0F - f4), (double)(0.0F + f4));
                tessellator.addVertexWithUV(1.0D, 1.0D, 1.0D, (double)(1.0F - f4), (double)(1.0F - f4));
                tessellator.addVertexWithUV(-1.0D, 1.0D, 1.0D, (double)(0.0F + f4), (double)(1.0F - f4));
                tessellator.draw();
                GL11.glPopMatrix();
            }

            GL11.glPopMatrix();
            GL11.glColorMask(true, true, true, false);
        }

        tessellator.setTranslation(0.0D, 0.0D, 0.0D);
        GL11.glColorMask(true, true, true, true);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    // JAVADOC METHOD $$ func_73968_a
    private void rotateAndBlurSkybox(float par1)
    {
        this.field_146297_k.getTextureManager().bindTexture(this.field_110351_G);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, 256, 256);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.func_148821_a(770, 771, 1, 0);
        GL11.glColorMask(true, true, true, false);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        byte b0 = 3;

        for (int i = 0; i < b0; ++i)
        {
            tessellator.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F / (float)(i + 1));
            int j = this.field_146294_l;
            int k = this.field_146295_m;
            float f1 = (float)(i - b0 / 2) / 256.0F;
            tessellator.addVertexWithUV((double)j, (double)k, (double)this.zLevel, (double)(0.0F + f1), 1.0D);
            tessellator.addVertexWithUV((double)j, 0.0D, (double)this.zLevel, (double)(1.0F + f1), 1.0D);
            tessellator.addVertexWithUV(0.0D, 0.0D, (double)this.zLevel, (double)(1.0F + f1), 0.0D);
            tessellator.addVertexWithUV(0.0D, (double)k, (double)this.zLevel, (double)(0.0F + f1), 0.0D);
        }

        tessellator.draw();
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glColorMask(true, true, true, true);
    }

    // JAVADOC METHOD $$ func_73971_c
    private void renderSkybox(int par1, int par2, float par3)
    {
        this.field_146297_k.func_147110_a().func_147609_e();
        GL11.glViewport(0, 0, 256, 256);
        this.drawPanorama(par1, par2, par3);
        this.rotateAndBlurSkybox(par3);
        this.rotateAndBlurSkybox(par3);
        this.rotateAndBlurSkybox(par3);
        this.rotateAndBlurSkybox(par3);
        this.rotateAndBlurSkybox(par3);
        this.rotateAndBlurSkybox(par3);
        this.rotateAndBlurSkybox(par3);
        this.field_146297_k.func_147110_a().func_147610_a(true);
        GL11.glViewport(0, 0, this.field_146297_k.displayWidth, this.field_146297_k.displayHeight);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        float f1 = this.field_146294_l > this.field_146295_m ? 120.0F / (float)this.field_146294_l : 120.0F / (float)this.field_146295_m;
        float f2 = (float)this.field_146295_m * f1 / 256.0F;
        float f3 = (float)this.field_146294_l * f1 / 256.0F;
        tessellator.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
        int k = this.field_146294_l;
        int l = this.field_146295_m;
        tessellator.addVertexWithUV(0.0D, (double)l, (double)this.zLevel, (double)(0.5F - f2), (double)(0.5F + f3));
        tessellator.addVertexWithUV((double)k, (double)l, (double)this.zLevel, (double)(0.5F - f2), (double)(0.5F - f3));
        tessellator.addVertexWithUV((double)k, 0.0D, (double)this.zLevel, (double)(0.5F + f2), (double)(0.5F - f3));
        tessellator.addVertexWithUV(0.0D, 0.0D, (double)this.zLevel, (double)(0.5F + f2), (double)(0.5F + f3));
        tessellator.draw();
    }

    // JAVADOC METHOD $$ func_73863_a
    public void drawScreen(int par1, int par2, float par3)
    {
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        this.renderSkybox(par1, par2, par3);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        Tessellator tessellator = Tessellator.instance;
        short short1 = 274;
        int k = this.field_146294_l / 2 - short1 / 2;
        byte b0 = 30;
        this.drawGradientRect(0, 0, this.field_146294_l, this.field_146295_m, -2130706433, 16777215);
        this.drawGradientRect(0, 0, this.field_146294_l, this.field_146295_m, 0, Integer.MIN_VALUE);
        this.field_146297_k.getTextureManager().bindTexture(minecraftTitleTextures);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        if ((double)this.updateCounter < 1.0E-4D)
        {
            this.drawTexturedModalRect(k + 0, b0 + 0, 0, 0, 99, 44);
            this.drawTexturedModalRect(k + 99, b0 + 0, 129, 0, 27, 44);
            this.drawTexturedModalRect(k + 99 + 26, b0 + 0, 126, 0, 3, 44);
            this.drawTexturedModalRect(k + 99 + 26 + 3, b0 + 0, 99, 0, 26, 44);
            this.drawTexturedModalRect(k + 155, b0 + 0, 0, 45, 155, 44);
        }
        else
        {
            this.drawTexturedModalRect(k + 0, b0 + 0, 0, 0, 155, 44);
            this.drawTexturedModalRect(k + 155, b0 + 0, 0, 45, 155, 44);
        }

        tessellator.setColorOpaque_I(-1);
        GL11.glPushMatrix();
        GL11.glTranslatef((float)(this.field_146294_l / 2 + 90), 70.0F, 0.0F);
        GL11.glRotatef(-20.0F, 0.0F, 0.0F, 1.0F);
        float f1 = 1.8F - MathHelper.abs(MathHelper.sin((float)(Minecraft.getSystemTime() % 1000L) / 1000.0F * (float)Math.PI * 2.0F) * 0.1F);
        f1 = f1 * 100.0F / (float)(this.field_146289_q.getStringWidth(this.splashText) + 32);
        GL11.glScalef(f1, f1, f1);
        this.drawCenteredString(this.field_146289_q, this.splashText, 0, -8, -256);
        GL11.glPopMatrix();
        String s = "Minecraft 1.7.2";

        if (this.field_146297_k.isDemo())
        {
            s = s + " Demo";
        }

        List<String> brandings = Lists.reverse(FMLCommonHandler.instance().getBrandings(true));
        for (int i = 0; i < brandings.size(); i++)
        {
            String brd = brandings.get(i);
            if (!Strings.isNullOrEmpty(brd))
            {
                this.drawString(this.field_146289_q, brd, 2, this.field_146295_m - ( 10 + i * (this.field_146289_q.FONT_HEIGHT + 1)), 16777215);
            }
        }
        ForgeHooksClient.renderMainMenu(this, field_146289_q, field_146294_l, field_146295_m);
        String s1 = "Copyright Mojang AB. Do not distribute!";
        this.drawString(this.field_146289_q, s1, this.field_146294_l - this.field_146289_q.getStringWidth(s1) - 2, this.field_146295_m - 10, -1);

        if (this.field_92025_p != null && this.field_92025_p.length() > 0)
        {
            drawRect(this.field_92022_t - 2, this.field_92021_u - 2, this.field_92020_v + 2, this.field_92019_w - 1, 1428160512);
            this.drawString(this.field_146289_q, this.field_92025_p, this.field_92022_t, this.field_92021_u, -1);
            this.drawString(this.field_146289_q, this.field_146972_A, (this.field_146294_l - this.field_92024_r) / 2, ((GuiButton)this.field_146292_n.get(0)).field_146129_i - 12, -1);
        }

        super.drawScreen(par1, par2, par3);
    }

    // JAVADOC METHOD $$ func_73864_a
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
        Object object = this.field_104025_t;

        synchronized (this.field_104025_t)
        {
            if (this.field_92025_p.length() > 0 && par1 >= this.field_92022_t && par1 <= this.field_92020_v && par2 >= this.field_92021_u && par2 <= this.field_92019_w)
            {
                GuiConfirmOpenLink guiconfirmopenlink = new GuiConfirmOpenLink(this, this.field_104024_v, 13, true);
                guiconfirmopenlink.func_146358_g();
                this.field_146297_k.func_147108_a(guiconfirmopenlink);
            }
        }
    }
}