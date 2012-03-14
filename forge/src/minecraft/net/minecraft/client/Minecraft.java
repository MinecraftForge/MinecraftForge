package net.minecraft.client;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import net.minecraft.src.AchievementList;
import net.minecraft.src.AnvilSaveConverter;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.ChunkProviderLoadOrGenerate;
import net.minecraft.src.ColorizerFoliage;
import net.minecraft.src.ColorizerGrass;
import net.minecraft.src.ColorizerWater;
import net.minecraft.src.EffectRenderer;
import net.minecraft.src.EntityClientPlayerMP;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerSP;
import net.minecraft.src.EntityRenderer;
import net.minecraft.src.EnumMovingObjectType;
import net.minecraft.src.EnumOS2;
import net.minecraft.src.EnumOSMappingHelper;
import net.minecraft.src.EnumOptions;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.GLAllocation;
import net.minecraft.src.GameSettings;
import net.minecraft.src.GameWindowListener;
import net.minecraft.src.GuiAchievement;
import net.minecraft.src.GuiChat;
import net.minecraft.src.GuiConflictWarning;
import net.minecraft.src.GuiConnecting;
import net.minecraft.src.GuiErrorScreen;
import net.minecraft.src.GuiGameOver;
import net.minecraft.src.GuiIngame;
import net.minecraft.src.GuiIngameMenu;
import net.minecraft.src.GuiInventory;
import net.minecraft.src.GuiMainMenu;
import net.minecraft.src.GuiMemoryErrorScreen;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiSleepMP;
import net.minecraft.src.IChunkProvider;
import net.minecraft.src.ISaveFormat;
import net.minecraft.src.ISaveHandler;
import net.minecraft.src.Item;
import net.minecraft.src.ItemRenderer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.KeyBinding;
import net.minecraft.src.LoadingScreenRenderer;
import net.minecraft.src.MathHelper;
import net.minecraft.src.MinecraftError;
import net.minecraft.src.MinecraftException;
import net.minecraft.src.MinecraftImpl;
import net.minecraft.src.ModelBiped;
import net.minecraft.src.MouseHelper;
import net.minecraft.src.MovementInputFromOptions;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.NetClientHandler;
import net.minecraft.src.OpenGlCapsChecker;
import net.minecraft.src.OpenGlHelper;
import net.minecraft.src.PlayerController;
import net.minecraft.src.PlayerControllerCreative;
import net.minecraft.src.PlayerControllerDemo;
import net.minecraft.src.PlayerControllerMP;
import net.minecraft.src.PlayerControllerSP;
import net.minecraft.src.PlayerUsageSnooper;
import net.minecraft.src.Profiler;
import net.minecraft.src.ProfilerResult;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.RenderEngine;
import net.minecraft.src.RenderGlobal;
import net.minecraft.src.RenderManager;
import net.minecraft.src.ScaledResolution;
import net.minecraft.src.ScreenShotHelper;
import net.minecraft.src.Session;
import net.minecraft.src.SoundManager;
import net.minecraft.src.StatCollector;
import net.minecraft.src.StatFileWriter;
import net.minecraft.src.StatList;
import net.minecraft.src.StatStringFormatKeyInv;
import net.minecraft.src.StringTranslate;
import net.minecraft.src.Teleporter;
import net.minecraft.src.Tessellator;
import net.minecraft.src.TextureCompassFX;
import net.minecraft.src.TextureFlamesFX;
import net.minecraft.src.TextureLavaFX;
import net.minecraft.src.TextureLavaFlowFX;
import net.minecraft.src.TexturePackList;
import net.minecraft.src.TexturePortalFX;
import net.minecraft.src.TextureWatchFX;
import net.minecraft.src.TextureWaterFX;
import net.minecraft.src.TextureWaterFlowFX;
import net.minecraft.src.ThreadCheckHasPaid;
import net.minecraft.src.ThreadClientSleep;
import net.minecraft.src.ThreadDownloadResources;
import net.minecraft.src.Timer;
import net.minecraft.src.UnexpectedThrowable;
import net.minecraft.src.Vec3D;
import net.minecraft.src.World;
import net.minecraft.src.WorldProvider;
import net.minecraft.src.WorldRenderer;
import net.minecraft.src.WorldSettings;
import net.minecraft.src.WorldType;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.AWTGLCanvas;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

public abstract class Minecraft implements Runnable
{
    public static byte[] field_28006_b = new byte[10485760];

    /**
     * Set to 'this' in Minecraft constructor; used by some settings get methods
     */
    private static Minecraft theMinecraft;
    public PlayerController playerController;
    private boolean fullscreen = false;
    private boolean hasCrashed = false;
    public int displayWidth;
    public int displayHeight;

    /** Checks OpenGL capabilities (as of 1.2.3_04 effectively unused). */
    private OpenGlCapsChecker glCapabilities;
    private Timer timer = new Timer(20.0F);

    /** The World instance that Minecraft uses. */
    public World theWorld;
    public RenderGlobal renderGlobal;

    /** The player who's actually in control of this game. */
    public EntityPlayerSP thePlayer;

    /**
     * The Entity from which the renderer determines the render viewpoint. Currently is always the parent Minecraft
     * class's 'thePlayer' instance. Modification of its location, rotation, or other settings at render time will
     * modify the camera likewise, with the caveat of triggering chunk rebuilds as it moves, making it unsuitable for
     * changing the viewpoint mid-render.
     */
    public EntityLiving renderViewEntity;
    public EffectRenderer effectRenderer;
    public Session session = null;
    public String minecraftUri;
    public Canvas mcCanvas;

    /** a boolean to hide a Quit button from the main menu */
    public boolean hideQuitButton = false;
    public volatile boolean isGamePaused = false;

    /** The RenderEngine instance used by Minecraft */
    public RenderEngine renderEngine;

    /** The font renderer used for displaying and measuring text. */
    public FontRenderer fontRenderer;
    public FontRenderer standardGalacticFontRenderer;

    /** The GuiScreen that's being displayed at the moment. */
    public GuiScreen currentScreen = null;
    public LoadingScreenRenderer loadingScreen;
    public EntityRenderer entityRenderer;

    /** Reference to the download resources thread. */
    private ThreadDownloadResources downloadResourcesThread;

    /** Number of ticks ran since the program was started. */
    private int ticksRan = 0;

    /** Mouse left click counter */
    private int leftClickCounter = 0;

    /** Display width */
    private int tempDisplayWidth;

    /** Display height */
    private int tempDisplayHeight;

    /** Gui achievement */
    public GuiAchievement guiAchievement = new GuiAchievement(this);
    public GuiIngame ingameGUI;

    /** Skip render world */
    public boolean skipRenderWorld = false;

    /** The ModelBiped of the player */
    public ModelBiped playerModelBiped = new ModelBiped(0.0F);

    /** The ray trace hit that the mouse is over. */
    public MovingObjectPosition objectMouseOver = null;

    /** The game settings that currently hold effect. */
    public GameSettings gameSettings;
    protected MinecraftApplet mcApplet;
    public SoundManager sndManager = new SoundManager();

    /** Mouse helper instance. */
    public MouseHelper mouseHelper;

    /** The TexturePackLister used by this instance of Minecraft... */
    public TexturePackList texturePackList;
    public File mcDataDir;
    private ISaveFormat saveLoader;
    public static long[] frameTimes = new long[512];
    public static long[] tickTimes = new long[512];
    public static int numRecordedFrameTimes = 0;

    /**
     * time in milliseconds when TheadCheckHasPaid determined you have not paid. 0 if you have paid. Used in
     * GuiAchievement whether to display the nag text.
     */
    public static long hasPaidCheckTime = 0L;

    /**
     * When you place a block, it's set to 6, decremented once per tick, when it's 0, you can place another block.
     */
    private int rightClickDelayTimer = 0;

    /** Stat file writer */
    public StatFileWriter statFileWriter;
    private String serverName;
    private int serverPort;
    private TextureWaterFX textureWaterFX = new TextureWaterFX();
    private TextureLavaFX textureLavaFX = new TextureLavaFX();

    /** The working dir (OS specific) for minecraft */
    private static File minecraftDir = null;

    /**
     * Set to true to keep the game loop running. Set to false by shutdown() to allow the game loop to exit cleanly.
     */
    public volatile boolean running = true;

    /** String that shows the debug information */
    public String debug = "";

    /** Approximate time (in ms) of last update to debug string */
    long debugUpdateTime = System.currentTimeMillis();

    /** holds the current fps */
    int fpsCounter = 0;

    /**
     * Makes sure it doesn't keep taking screenshots when both buttons are down.
     */
    boolean isTakingScreenshot = false;
    long prevFrameTime = -1L;

    /** Profiler currently displayed in the debug screen pie chart */
    private String debugProfilerName = "root";

    /**
     * Does the actual gameplay have focus. If so then mouse and keys will effect the player instead of menus.
     */
    public boolean inGameHasFocus = false;
    public boolean isRaining = false;
    long systemTime = System.currentTimeMillis();

    /** Join player counter */
    private int joinPlayerCounter = 0;

    public Minecraft(Component par1Component, Canvas par2Canvas, MinecraftApplet par3MinecraftApplet, int par4, int par5, boolean par6)
    {
        StatList.func_27360_a();
        this.tempDisplayHeight = par5;
        this.fullscreen = par6;
        this.mcApplet = par3MinecraftApplet;
        new ThreadClientSleep(this, "Timer hack thread");
        this.mcCanvas = par2Canvas;
        this.displayWidth = par4;
        this.displayHeight = par5;
        this.fullscreen = par6;

        if (par3MinecraftApplet == null || "true".equals(par3MinecraftApplet.getParameter("stand-alone")))
        {
            this.hideQuitButton = false;
        }

        theMinecraft = this;
    }

    public void onMinecraftCrash(UnexpectedThrowable par1UnexpectedThrowable)
    {
        this.hasCrashed = true;
        this.displayUnexpectedThrowable(par1UnexpectedThrowable);
    }

    /**
     * Displays an unexpected error that has come up during the game.
     */
    public abstract void displayUnexpectedThrowable(UnexpectedThrowable var1);

    public void setServer(String par1Str, int par2)
    {
        this.serverName = par1Str;
        this.serverPort = par2;
    }

    /**
     * Starts the game: initializes the canvas, the title, the settings, etcetera.
     */
    public void startGame() throws LWJGLException
    {
        if (this.mcCanvas != null)
        {
            Graphics var1 = this.mcCanvas.getGraphics();

            if (var1 != null)
            {
                var1.setColor(Color.BLACK);
                var1.fillRect(0, 0, this.displayWidth, this.displayHeight);
                var1.dispose();
            }

            Display.setParent(this.mcCanvas);
        }
        else if (this.fullscreen)
        {
            Display.setFullscreen(true);
            this.displayWidth = Display.getDisplayMode().getWidth();
            this.displayHeight = Display.getDisplayMode().getHeight();

            if (this.displayWidth <= 0)
            {
                this.displayWidth = 1;
            }

            if (this.displayHeight <= 0)
            {
                this.displayHeight = 1;
            }
        }
        else
        {
            Display.setDisplayMode(new DisplayMode(this.displayWidth, this.displayHeight));
        }

        Display.setTitle("Minecraft Minecraft 1.2.3");

        try
        {
            PixelFormat var7 = new PixelFormat();
            var7 = var7.withDepthBits(24);
            Display.create(var7);
        }
        catch (LWJGLException var6)
        {
            var6.printStackTrace();

            try
            {
                Thread.sleep(1000L);
            }
            catch (InterruptedException var5)
            {
                ;
            }

            Display.create();
        }

        OpenGlHelper.initializeTextures();
        this.mcDataDir = getMinecraftDir();
        this.saveLoader = new AnvilSaveConverter(new File(this.mcDataDir, "saves"));
        this.gameSettings = new GameSettings(this, this.mcDataDir);
        this.texturePackList = new TexturePackList(this, this.mcDataDir);
        this.renderEngine = new RenderEngine(this.texturePackList, this.gameSettings);
        this.loadScreen();
        this.fontRenderer = new FontRenderer(this.gameSettings, "/font/default.png", this.renderEngine, false);
        this.standardGalacticFontRenderer = new FontRenderer(this.gameSettings, "/font/alternate.png", this.renderEngine, false);

        if (this.gameSettings.language != null)
        {
            StringTranslate.getInstance().setLanguage(this.gameSettings.language);
            this.fontRenderer.setUnicodeFlag(StringTranslate.getInstance().isUnicode());
            this.fontRenderer.setBidiFlag(StringTranslate.isBidrectional(this.gameSettings.language));
        }

        ColorizerWater.setWaterBiomeColorizer(this.renderEngine.getTextureContents("/misc/watercolor.png"));
        ColorizerGrass.setGrassBiomeColorizer(this.renderEngine.getTextureContents("/misc/grasscolor.png"));
        ColorizerFoliage.getFoilageBiomeColorizer(this.renderEngine.getTextureContents("/misc/foliagecolor.png"));
        this.entityRenderer = new EntityRenderer(this);
        RenderManager.instance.itemRenderer = new ItemRenderer(this);
        this.statFileWriter = new StatFileWriter(this.session, this.mcDataDir);
        AchievementList.openInventory.setStatStringFormatter(new StatStringFormatKeyInv(this));
        this.loadScreen();
        Mouse.create();
        this.mouseHelper = new MouseHelper(this.mcCanvas);

        try
        {
            Controllers.create();
        }
        catch (Exception var4)
        {
            var4.printStackTrace();
        }

        this.checkGLError("Pre startup");
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glClearDepth(1.0D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        this.checkGLError("Startup");
        this.glCapabilities = new OpenGlCapsChecker();
        this.sndManager.loadSoundSettings(this.gameSettings);
        this.renderEngine.registerTextureFX(this.textureLavaFX);
        this.renderEngine.registerTextureFX(this.textureWaterFX);
        this.renderEngine.registerTextureFX(new TexturePortalFX());
        this.renderEngine.registerTextureFX(new TextureCompassFX(this));
        this.renderEngine.registerTextureFX(new TextureWatchFX(this));
        this.renderEngine.registerTextureFX(new TextureWaterFlowFX());
        this.renderEngine.registerTextureFX(new TextureLavaFlowFX());
        this.renderEngine.registerTextureFX(new TextureFlamesFX(0));
        this.renderEngine.registerTextureFX(new TextureFlamesFX(1));
        this.renderGlobal = new RenderGlobal(this, this.renderEngine);
        GL11.glViewport(0, 0, this.displayWidth, this.displayHeight);
        this.effectRenderer = new EffectRenderer(this.theWorld, this.renderEngine);

        try
        {
            this.downloadResourcesThread = new ThreadDownloadResources(this.mcDataDir, this);
            this.downloadResourcesThread.start();
        }
        catch (Exception var3)
        {
            ;
        }

        this.checkGLError("Post startup");
        this.ingameGUI = new GuiIngame(this);

        if (this.serverName != null)
        {
            this.displayGuiScreen(new GuiConnecting(this, this.serverName, this.serverPort));
        }
        else
        {
            this.displayGuiScreen(new GuiMainMenu());
        }

        this.loadingScreen = new LoadingScreenRenderer(this);
    }

    /**
     * Displays a new screen.
     */
    private void loadScreen() throws LWJGLException
    {
        ScaledResolution var1 = new ScaledResolution(this.gameSettings, this.displayWidth, this.displayHeight);
        GL11.glClear(16640);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, var1.scaledWidthD, var1.scaledHeightD, 0.0D, 1000.0D, 3000.0D);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
        GL11.glViewport(0, 0, this.displayWidth, this.displayHeight);
        GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        Tessellator var2 = Tessellator.instance;
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_FOG);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.renderEngine.getTexture("/title/mojang.png"));
        var2.startDrawingQuads();
        var2.setColorOpaque_I(16777215);
        var2.addVertexWithUV(0.0D, (double)this.displayHeight, 0.0D, 0.0D, 0.0D);
        var2.addVertexWithUV((double)this.displayWidth, (double)this.displayHeight, 0.0D, 0.0D, 0.0D);
        var2.addVertexWithUV((double)this.displayWidth, 0.0D, 0.0D, 0.0D, 0.0D);
        var2.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
        var2.draw();
        short var3 = 256;
        short var4 = 256;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        var2.setColorOpaque_I(16777215);
        this.scaledTessellator((var1.getScaledWidth() - var3) / 2, (var1.getScaledHeight() - var4) / 2, 0, 0, var3, var4);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        Display.swapBuffers();
    }

    /**
     * Loads Tessellator with a scaled resolution
     */
    public void scaledTessellator(int par1, int par2, int par3, int par4, int par5, int par6)
    {
        float var7 = 0.00390625F;
        float var8 = 0.00390625F;
        Tessellator var9 = Tessellator.instance;
        var9.startDrawingQuads();
        var9.addVertexWithUV((double)(par1 + 0), (double)(par2 + par6), 0.0D, (double)((float)(par3 + 0) * var7), (double)((float)(par4 + par6) * var8));
        var9.addVertexWithUV((double)(par1 + par5), (double)(par2 + par6), 0.0D, (double)((float)(par3 + par5) * var7), (double)((float)(par4 + par6) * var8));
        var9.addVertexWithUV((double)(par1 + par5), (double)(par2 + 0), 0.0D, (double)((float)(par3 + par5) * var7), (double)((float)(par4 + 0) * var8));
        var9.addVertexWithUV((double)(par1 + 0), (double)(par2 + 0), 0.0D, (double)((float)(par3 + 0) * var7), (double)((float)(par4 + 0) * var8));
        var9.draw();
    }

    /**
     * gets the working dir (OS specific) for minecraft
     */
    public static File getMinecraftDir()
    {
        if (minecraftDir == null)
        {
            minecraftDir = getAppDir("minecraft");
        }

        return minecraftDir;
    }

    /**
     * gets the working dir (OS specific) for the specific application (which is always minecraft)
     */
    public static File getAppDir(String par0Str)
    {
        String var1 = System.getProperty("user.home", ".");
        File var2;

        switch (EnumOSMappingHelper.enumOSMappingArray[getOs().ordinal()])
        {
            case 1:
            case 2:
                var2 = new File(var1, '.' + par0Str + '/');
                break;

            case 3:
                String var3 = System.getenv("APPDATA");

                if (var3 != null)
                {
                    var2 = new File(var3, "." + par0Str + '/');
                }
                else
                {
                    var2 = new File(var1, '.' + par0Str + '/');
                }

                break;

            case 4:
                var2 = new File(var1, "Library/Application Support/" + par0Str);
                break;

            default:
                var2 = new File(var1, par0Str + '/');
        }

        if (!var2.exists() && !var2.mkdirs())
        {
            throw new RuntimeException("The working directory could not be created: " + var2);
        }
        else
        {
            return var2;
        }
    }

    private static EnumOS2 getOs()
    {
        String var0 = System.getProperty("os.name").toLowerCase();
        return var0.contains("win") ? EnumOS2.windows : (var0.contains("mac") ? EnumOS2.macos : (var0.contains("solaris") ? EnumOS2.solaris : (var0.contains("sunos") ? EnumOS2.solaris : (var0.contains("linux") ? EnumOS2.linux : (var0.contains("unix") ? EnumOS2.linux : EnumOS2.unknown)))));
    }

    /**
     * Returns the save loader that is currently being used
     */
    public ISaveFormat getSaveLoader()
    {
        return this.saveLoader;
    }

    /**
     * Sets the argument GuiScreen as the main (topmost visible) screen.
     */
    public void displayGuiScreen(GuiScreen par1GuiScreen)
    {
        if (!(this.currentScreen instanceof GuiErrorScreen))
        {
            if (this.currentScreen != null)
            {
                this.currentScreen.onGuiClosed();
            }

            if (par1GuiScreen instanceof GuiMainMenu)
            {
                this.statFileWriter.func_27175_b();
            }

            this.statFileWriter.syncStats();

            if (par1GuiScreen == null && this.theWorld == null)
            {
                par1GuiScreen = new GuiMainMenu();
            }
            else if (par1GuiScreen == null && this.thePlayer.getEntityHealth() <= 0)
            {
                par1GuiScreen = new GuiGameOver();
            }

            if (par1GuiScreen instanceof GuiMainMenu)
            {
                this.gameSettings.showDebugInfo = false;
                this.ingameGUI.clearChatMessages();
            }

            this.currentScreen = (GuiScreen)par1GuiScreen;

            if (par1GuiScreen != null)
            {
                this.setIngameNotInFocus();
                ScaledResolution var2 = new ScaledResolution(this.gameSettings, this.displayWidth, this.displayHeight);
                int var3 = var2.getScaledWidth();
                int var4 = var2.getScaledHeight();
                ((GuiScreen)par1GuiScreen).setWorldAndResolution(this, var3, var4);
                this.skipRenderWorld = false;
            }
            else
            {
                this.setIngameFocus();
            }
        }
    }

    /**
     * Checks for an OpenGL error. If there is one, prints the error ID and error string.
     */
    private void checkGLError(String par1Str)
    {
        int var2 = GL11.glGetError();

        if (var2 != 0)
        {
            String var3 = GLU.gluErrorString(var2);
            System.out.println("########## GL ERROR ##########");
            System.out.println("@ " + par1Str);
            System.out.println(var2 + ": " + var3);
        }
    }

    /**
     * Shuts down the minecraft applet by stopping the resource downloads, and clearing up GL stuff; called when the
     * application (or web page) is exited.
     */
    public void shutdownMinecraftApplet()
    {
        try
        {
            this.statFileWriter.func_27175_b();
            this.statFileWriter.syncStats();

            if (this.mcApplet != null)
            {
                this.mcApplet.clearApplet();
            }

            try
            {
                if (this.downloadResourcesThread != null)
                {
                    this.downloadResourcesThread.closeMinecraft();
                }
            }
            catch (Exception var9)
            {
                ;
            }

            System.out.println("Stopping!");

            try
            {
                this.changeWorld1((World)null);
            }
            catch (Throwable var8)
            {
                ;
            }

            try
            {
                GLAllocation.deleteTexturesAndDisplayLists();
            }
            catch (Throwable var7)
            {
                ;
            }

            this.sndManager.closeMinecraft();
            Mouse.destroy();
            Keyboard.destroy();
        }
        finally
        {
            Display.destroy();

            if (!this.hasCrashed)
            {
                System.exit(0);
            }
        }

        System.gc();
    }

    public void run()
    {
        this.running = true;

        try
        {
            this.startGame();
        }
        catch (Exception var11)
        {
            var11.printStackTrace();
            this.onMinecraftCrash(new UnexpectedThrowable("Failed to start game", var11));
            return;
        }

        try
        {
            while (this.running)
            {
                try
                {
                    this.runGameLoop();
                }
                catch (MinecraftException var9)
                {
                    this.theWorld = null;
                    this.changeWorld1((World)null);
                    this.displayGuiScreen(new GuiConflictWarning());
                }
                catch (OutOfMemoryError var10)
                {
                    this.freeMemory();
                    this.displayGuiScreen(new GuiMemoryErrorScreen());
                    System.gc();
                }
            }
        }
        catch (MinecraftError var12)
        {
            ;
        }
        catch (Throwable var13)
        {
            this.freeMemory();
            var13.printStackTrace();
            this.onMinecraftCrash(new UnexpectedThrowable("Unexpected error", var13));
        }
        finally
        {
            this.shutdownMinecraftApplet();
        }
    }

    /**
     * Called repeatedly from run()
     */
    private void runGameLoop()
    {
        if (this.mcApplet != null && !this.mcApplet.isActive())
        {
            this.running = false;
        }
        else
        {
            AxisAlignedBB.clearBoundingBoxPool();
            Vec3D.initialize();
            PlayerUsageSnooper.field_48478_a.func_48472_a();

            if (this.playerController == null)
            {
                PlayerUsageSnooper.field_48478_a.func_48474_a("mode", "title");
            }
            else if (this.playerController instanceof PlayerControllerCreative)
            {
                PlayerUsageSnooper.field_48478_a.func_48474_a("mode", "single_creative");
            }
            else if (this.playerController instanceof PlayerControllerDemo)
            {
                PlayerUsageSnooper.field_48478_a.func_48474_a("mode", "single_demo");
            }
            else if (this.playerController instanceof PlayerControllerSP)
            {
                PlayerUsageSnooper.field_48478_a.func_48474_a("mode", "single_survival");
            }
            else if (this.playerController instanceof PlayerControllerMP)
            {
                PlayerUsageSnooper.field_48478_a.func_48474_a("mode", "multiplayer");
            }

            Profiler.startSection("root");

            if (this.mcCanvas == null && Display.isCloseRequested())
            {
                this.shutdown();
            }

            if (this.isGamePaused && this.theWorld != null)
            {
                float var1 = this.timer.renderPartialTicks;
                this.timer.updateTimer();
                this.timer.renderPartialTicks = var1;
            }
            else
            {
                this.timer.updateTimer();
            }

            long var6 = System.nanoTime();
            Profiler.startSection("tick");

            for (int var3 = 0; var3 < this.timer.elapsedTicks; ++var3)
            {
                ++this.ticksRan;

                try
                {
                    this.runTick();
                }
                catch (MinecraftException var5)
                {
                    this.theWorld = null;
                    this.changeWorld1((World)null);
                    this.displayGuiScreen(new GuiConflictWarning());
                }
            }

            Profiler.endSection();
            long var7 = System.nanoTime() - var6;
            this.checkGLError("Pre render");
            RenderBlocks.fancyGrass = this.gameSettings.fancyGraphics;
            Profiler.startSection("sound");
            this.sndManager.setListener(this.thePlayer, this.timer.renderPartialTicks);
            Profiler.endStartSection("updatelights");

            if (this.theWorld != null)
            {
                this.theWorld.updatingLighting();
            }

            Profiler.endSection();
            Profiler.startSection("render");
            Profiler.startSection("display");
            GL11.glEnable(GL11.GL_TEXTURE_2D);

            if (!Keyboard.isKeyDown(65))
            {
                Display.update();
            }

            if (this.thePlayer != null && this.thePlayer.isEntityInsideOpaqueBlock())
            {
                this.gameSettings.thirdPersonView = 0;
            }

            Profiler.endSection();

            if (!this.skipRenderWorld)
            {
                Profiler.startSection("gameMode");

                if (this.playerController != null)
                {
                    this.playerController.setPartialTime(this.timer.renderPartialTicks);
                }

                Profiler.endStartSection("gameRenderer");
                this.entityRenderer.updateCameraAndRender(this.timer.renderPartialTicks);
                Profiler.endSection();
            }

            GL11.glFlush();
            Profiler.endSection();

            if (!Display.isActive() && this.fullscreen)
            {
                this.toggleFullscreen();
            }

            Profiler.endSection();

            if (this.gameSettings.showDebugInfo)
            {
                if (!Profiler.profilingEnabled)
                {
                    Profiler.clearProfiling();
                }

                Profiler.profilingEnabled = true;
                this.displayDebugInfo(var7);
            }
            else
            {
                Profiler.profilingEnabled = false;
                this.prevFrameTime = System.nanoTime();
            }

            this.guiAchievement.updateAchievementWindow();
            Profiler.startSection("root");
            Thread.yield();

            if (Keyboard.isKeyDown(65))
            {
                Display.update();
            }

            this.screenshotListener();

            if (this.mcCanvas != null && !this.fullscreen && (this.mcCanvas.getWidth() != this.displayWidth || this.mcCanvas.getHeight() != this.displayHeight))
            {
                this.displayWidth = this.mcCanvas.getWidth();
                this.displayHeight = this.mcCanvas.getHeight();

                if (this.displayWidth <= 0)
                {
                    this.displayWidth = 1;
                }

                if (this.displayHeight <= 0)
                {
                    this.displayHeight = 1;
                }

                this.resize(this.displayWidth, this.displayHeight);
            }

            this.checkGLError("Post render");
            ++this.fpsCounter;

            for (this.isGamePaused = !this.isMultiplayerWorld() && this.currentScreen != null && this.currentScreen.doesGuiPauseGame(); System.currentTimeMillis() >= this.debugUpdateTime + 1000L; this.fpsCounter = 0)
            {
                this.debug = this.fpsCounter + " fps, " + WorldRenderer.chunksUpdated + " chunk updates";
                WorldRenderer.chunksUpdated = 0;
                this.debugUpdateTime += 1000L;
            }

            Profiler.endSection();
        }
    }

    public void freeMemory()
    {
        try
        {
            field_28006_b = new byte[0];
            this.renderGlobal.func_28137_f();
        }
        catch (Throwable var4)
        {
            ;
        }

        try
        {
            System.gc();
            AxisAlignedBB.clearBoundingBoxes();
            Vec3D.clearVectorList();
        }
        catch (Throwable var3)
        {
            ;
        }

        try
        {
            System.gc();
            this.changeWorld1((World)null);
        }
        catch (Throwable var2)
        {
            ;
        }

        System.gc();
    }

    /**
     * checks if keys are down
     */
    private void screenshotListener()
    {
        if (Keyboard.isKeyDown(60))
        {
            if (!this.isTakingScreenshot)
            {
                this.isTakingScreenshot = true;
                this.ingameGUI.addChatMessage(ScreenShotHelper.saveScreenshot(minecraftDir, this.displayWidth, this.displayHeight));
            }
        }
        else
        {
            this.isTakingScreenshot = false;
        }
    }

    /**
     * Update debugProfilerName in response to number keys in debug screen
     */
    private void updateDebugProfilerName(int par1)
    {
        List var2 = Profiler.getProfilingData(this.debugProfilerName);

        if (var2 != null && var2.size() != 0)
        {
            ProfilerResult var3 = (ProfilerResult)var2.remove(0);

            if (par1 == 0)
            {
                if (var3.name.length() > 0)
                {
                    int var4 = this.debugProfilerName.lastIndexOf(".");

                    if (var4 >= 0)
                    {
                        this.debugProfilerName = this.debugProfilerName.substring(0, var4);
                    }
                }
            }
            else
            {
                --par1;

                if (par1 < var2.size() && !((ProfilerResult)var2.get(par1)).name.equals("unspecified"))
                {
                    if (this.debugProfilerName.length() > 0)
                    {
                        this.debugProfilerName = this.debugProfilerName + ".";
                    }

                    this.debugProfilerName = this.debugProfilerName + ((ProfilerResult)var2.get(par1)).name;
                }
            }
        }
    }

    private void displayDebugInfo(long par1)
    {
        List var3 = Profiler.getProfilingData(this.debugProfilerName);
        ProfilerResult var4 = (ProfilerResult)var3.remove(0);
        long var5 = 16666666L;

        if (this.prevFrameTime == -1L)
        {
            this.prevFrameTime = System.nanoTime();
        }

        long var7 = System.nanoTime();
        tickTimes[numRecordedFrameTimes & frameTimes.length - 1] = par1;
        frameTimes[numRecordedFrameTimes++ & frameTimes.length - 1] = var7 - this.prevFrameTime;
        this.prevFrameTime = var7;
        GL11.glClear(256);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, (double)this.displayWidth, (double)this.displayHeight, 0.0D, 1000.0D, 3000.0D);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
        GL11.glLineWidth(1.0F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        Tessellator var9 = Tessellator.instance;
        var9.startDrawing(7);
        int var10 = (int)(var5 / 200000L);
        var9.setColorOpaque_I(536870912);
        var9.addVertex(0.0D, (double)(this.displayHeight - var10), 0.0D);
        var9.addVertex(0.0D, (double)this.displayHeight, 0.0D);
        var9.addVertex((double)frameTimes.length, (double)this.displayHeight, 0.0D);
        var9.addVertex((double)frameTimes.length, (double)(this.displayHeight - var10), 0.0D);
        var9.setColorOpaque_I(538968064);
        var9.addVertex(0.0D, (double)(this.displayHeight - var10 * 2), 0.0D);
        var9.addVertex(0.0D, (double)(this.displayHeight - var10), 0.0D);
        var9.addVertex((double)frameTimes.length, (double)(this.displayHeight - var10), 0.0D);
        var9.addVertex((double)frameTimes.length, (double)(this.displayHeight - var10 * 2), 0.0D);
        var9.draw();
        long var11 = 0L;
        int var13;

        for (var13 = 0; var13 < frameTimes.length; ++var13)
        {
            var11 += frameTimes[var13];
        }

        var13 = (int)(var11 / 200000L / (long)frameTimes.length);
        var9.startDrawing(7);
        var9.setColorOpaque_I(541065216);
        var9.addVertex(0.0D, (double)(this.displayHeight - var13), 0.0D);
        var9.addVertex(0.0D, (double)this.displayHeight, 0.0D);
        var9.addVertex((double)frameTimes.length, (double)this.displayHeight, 0.0D);
        var9.addVertex((double)frameTimes.length, (double)(this.displayHeight - var13), 0.0D);
        var9.draw();
        var9.startDrawing(1);
        int var15;
        int var16;

        for (int var14 = 0; var14 < frameTimes.length; ++var14)
        {
            var15 = (var14 - numRecordedFrameTimes & frameTimes.length - 1) * 255 / frameTimes.length;
            var16 = var15 * var15 / 255;
            var16 = var16 * var16 / 255;
            int var17 = var16 * var16 / 255;
            var17 = var17 * var17 / 255;

            if (frameTimes[var14] > var5)
            {
                var9.setColorOpaque_I(-16777216 + var16 * 65536);
            }
            else
            {
                var9.setColorOpaque_I(-16777216 + var16 * 256);
            }

            long var18 = frameTimes[var14] / 200000L;
            long var20 = tickTimes[var14] / 200000L;
            var9.addVertex((double)((float)var14 + 0.5F), (double)((float)((long)this.displayHeight - var18) + 0.5F), 0.0D);
            var9.addVertex((double)((float)var14 + 0.5F), (double)((float)this.displayHeight + 0.5F), 0.0D);
            var9.setColorOpaque_I(-16777216 + var16 * 65536 + var16 * 256 + var16 * 1);
            var9.addVertex((double)((float)var14 + 0.5F), (double)((float)((long)this.displayHeight - var18) + 0.5F), 0.0D);
            var9.addVertex((double)((float)var14 + 0.5F), (double)((float)((long)this.displayHeight - (var18 - var20)) + 0.5F), 0.0D);
        }

        var9.draw();
        short var26 = 160;
        var15 = this.displayWidth - var26 - 10;
        var16 = this.displayHeight - var26 * 2;
        GL11.glEnable(GL11.GL_BLEND);
        var9.startDrawingQuads();
        var9.setColorRGBA_I(0, 200);
        var9.addVertex((double)((float)var15 - (float)var26 * 1.1F), (double)((float)var16 - (float)var26 * 0.6F - 16.0F), 0.0D);
        var9.addVertex((double)((float)var15 - (float)var26 * 1.1F), (double)(var16 + var26 * 2), 0.0D);
        var9.addVertex((double)((float)var15 + (float)var26 * 1.1F), (double)(var16 + var26 * 2), 0.0D);
        var9.addVertex((double)((float)var15 + (float)var26 * 1.1F), (double)((float)var16 - (float)var26 * 0.6F - 16.0F), 0.0D);
        var9.draw();
        GL11.glDisable(GL11.GL_BLEND);
        double var27 = 0.0D;
        int var21;

        for (int var19 = 0; var19 < var3.size(); ++var19)
        {
            ProfilerResult var29 = (ProfilerResult)var3.get(var19);
            var21 = MathHelper.floor_double(var29.sectionPercentage / 4.0D) + 1;
            var9.startDrawing(6);
            var9.setColorOpaque_I(var29.getDisplayColor());
            var9.addVertex((double)var15, (double)var16, 0.0D);
            float var23;
            int var22;
            float var25;
            float var24;

            for (var22 = var21; var22 >= 0; --var22)
            {
                var23 = (float)((var27 + var29.sectionPercentage * (double)var22 / (double)var21) * Math.PI * 2.0D / 100.0D);
                var24 = MathHelper.sin(var23) * (float)var26;
                var25 = MathHelper.cos(var23) * (float)var26 * 0.5F;
                var9.addVertex((double)((float)var15 + var24), (double)((float)var16 - var25), 0.0D);
            }

            var9.draw();
            var9.startDrawing(5);
            var9.setColorOpaque_I((var29.getDisplayColor() & 16711422) >> 1);

            for (var22 = var21; var22 >= 0; --var22)
            {
                var23 = (float)((var27 + var29.sectionPercentage * (double)var22 / (double)var21) * Math.PI * 2.0D / 100.0D);
                var24 = MathHelper.sin(var23) * (float)var26;
                var25 = MathHelper.cos(var23) * (float)var26 * 0.5F;
                var9.addVertex((double)((float)var15 + var24), (double)((float)var16 - var25), 0.0D);
                var9.addVertex((double)((float)var15 + var24), (double)((float)var16 - var25 + 10.0F), 0.0D);
            }

            var9.draw();
            var27 += var29.sectionPercentage;
        }

        DecimalFormat var30 = new DecimalFormat("##0.00");
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        String var28 = "";

        if (!var4.name.equals("unspecified"))
        {
            var28 = var28 + "[0] ";
        }

        if (var4.name.length() == 0)
        {
            var28 = var28 + "ROOT ";
        }
        else
        {
            var28 = var28 + var4.name + " ";
        }

        var21 = 16777215;
        this.fontRenderer.drawStringWithShadow(var28, var15 - var26, var16 - var26 / 2 - 16, var21);
        this.fontRenderer.drawStringWithShadow(var28 = var30.format(var4.globalPercentage) + "%", var15 + var26 - this.fontRenderer.getStringWidth(var28), var16 - var26 / 2 - 16, var21);

        for (int var31 = 0; var31 < var3.size(); ++var31)
        {
            ProfilerResult var33 = (ProfilerResult)var3.get(var31);
            String var32 = "";

            if (!var33.name.equals("unspecified"))
            {
                var32 = var32 + "[" + (var31 + 1) + "] ";
            }
            else
            {
                var32 = var32 + "[?] ";
            }

            var32 = var32 + var33.name;
            this.fontRenderer.drawStringWithShadow(var32, var15 - var26, var16 + var26 / 2 + var31 * 8 + 20, var33.getDisplayColor());
            this.fontRenderer.drawStringWithShadow(var32 = var30.format(var33.sectionPercentage) + "%", var15 + var26 - 50 - this.fontRenderer.getStringWidth(var32), var16 + var26 / 2 + var31 * 8 + 20, var33.getDisplayColor());
            this.fontRenderer.drawStringWithShadow(var32 = var30.format(var33.globalPercentage) + "%", var15 + var26 - this.fontRenderer.getStringWidth(var32), var16 + var26 / 2 + var31 * 8 + 20, var33.getDisplayColor());
        }
    }

    /**
     * Called when the window is closing. Sets 'running' to false which allows the game loop to exit cleanly.
     */
    public void shutdown()
    {
        this.running = false;
    }

    /**
     * Will set the focus to ingame if the Minecraft window is the active with focus. Also clears any GUI screen
     * currently displayed
     */
    public void setIngameFocus()
    {
        if (Display.isActive())
        {
            if (!this.inGameHasFocus)
            {
                this.inGameHasFocus = true;
                this.mouseHelper.grabMouseCursor();
                this.displayGuiScreen((GuiScreen)null);
                this.leftClickCounter = 10000;
            }
        }
    }

    /**
     * Resets the player keystate, disables the ingame focus, and ungrabs the mouse cursor.
     */
    public void setIngameNotInFocus()
    {
        if (this.inGameHasFocus)
        {
            KeyBinding.unPressAllKeys();
            this.inGameHasFocus = false;
            this.mouseHelper.ungrabMouseCursor();
        }
    }

    /**
     * Displays the ingame menu
     */
    public void displayInGameMenu()
    {
        if (this.currentScreen == null)
        {
            this.displayGuiScreen(new GuiIngameMenu());
        }
    }

    private void sendClickBlockToController(int par1, boolean par2)
    {
        if (!par2)
        {
            this.leftClickCounter = 0;
        }

        if (par1 != 0 || this.leftClickCounter <= 0)
        {
            if (par2 && this.objectMouseOver != null && this.objectMouseOver.typeOfHit == EnumMovingObjectType.TILE && par1 == 0)
            {
                int var3 = this.objectMouseOver.blockX;
                int var4 = this.objectMouseOver.blockY;
                int var5 = this.objectMouseOver.blockZ;
                this.playerController.onPlayerDamageBlock(var3, var4, var5, this.objectMouseOver.sideHit);

                if (this.thePlayer.canPlayerEdit(var3, var4, var5))
                {
                    this.effectRenderer.addBlockHitEffects(var3, var4, var5, this.objectMouseOver.sideHit);
                    this.thePlayer.swingItem();
                }
            }
            else
            {
                this.playerController.resetBlockRemoving();
            }
        }
    }

    /**
     * Called whenever the mouse is clicked. Button clicked is 0 for left clicking and 1 for right clicking. Args:
     * buttonClicked
     */
    private void clickMouse(int par1)
    {
        if (par1 != 0 || this.leftClickCounter <= 0)
        {
            if (par1 == 0)
            {
                this.thePlayer.swingItem();
            }

            if (par1 == 1)
            {
                this.rightClickDelayTimer = 4;
            }

            boolean var2 = true;
            ItemStack var3 = this.thePlayer.inventory.getCurrentItem();

            if (this.objectMouseOver == null)
            {
                if (par1 == 0 && this.playerController.isNotCreative())
                {
                    this.leftClickCounter = 10;
                }
            }
            else if (this.objectMouseOver.typeOfHit == EnumMovingObjectType.ENTITY)
            {
                if (par1 == 0)
                {
                    this.playerController.attackEntity(this.thePlayer, this.objectMouseOver.entityHit);
                }

                if (par1 == 1)
                {
                    this.playerController.interactWithEntity(this.thePlayer, this.objectMouseOver.entityHit);
                }
            }
            else if (this.objectMouseOver.typeOfHit == EnumMovingObjectType.TILE)
            {
                int var4 = this.objectMouseOver.blockX;
                int var5 = this.objectMouseOver.blockY;
                int var6 = this.objectMouseOver.blockZ;
                int var7 = this.objectMouseOver.sideHit;

                if (par1 == 0)
                {
                    this.playerController.clickBlock(var4, var5, var6, this.objectMouseOver.sideHit);
                }
                else
                {
                    int var9 = var3 != null ? var3.stackSize : 0;

                    if (this.playerController.onPlayerRightClick(this.thePlayer, this.theWorld, var3, var4, var5, var6, var7))
                    {
                        var2 = false;
                        this.thePlayer.swingItem();
                    }

                    if (var3 == null)
                    {
                        return;
                    }

                    if (var3.stackSize == 0)
                    {
                        this.thePlayer.inventory.mainInventory[this.thePlayer.inventory.currentItem] = null;
                    }
                    else if (var3.stackSize != var9 || this.playerController.isInCreativeMode())
                    {
                        this.entityRenderer.itemRenderer.func_9449_b();
                    }
                }
            }

            if (var2 && par1 == 1)
            {
                ItemStack var10 = this.thePlayer.inventory.getCurrentItem();

                if (var10 != null && this.playerController.sendUseItem(this.thePlayer, this.theWorld, var10))
                {
                    this.entityRenderer.itemRenderer.func_9450_c();
                }
            }
        }
    }

    /**
     * Toggles fullscreen mode.
     */
    public void toggleFullscreen()
    {
        try
        {
            this.fullscreen = !this.fullscreen;

            if (this.fullscreen)
            {
                Display.setDisplayMode(Display.getDesktopDisplayMode());
                this.displayWidth = Display.getDisplayMode().getWidth();
                this.displayHeight = Display.getDisplayMode().getHeight();

                if (this.displayWidth <= 0)
                {
                    this.displayWidth = 1;
                }

                if (this.displayHeight <= 0)
                {
                    this.displayHeight = 1;
                }
            }
            else
            {
                if (this.mcCanvas != null)
                {
                    this.displayWidth = this.mcCanvas.getWidth();
                    this.displayHeight = this.mcCanvas.getHeight();
                }
                else
                {
                    this.displayWidth = this.tempDisplayWidth;
                    this.displayHeight = this.tempDisplayHeight;
                }

                if (this.displayWidth <= 0)
                {
                    this.displayWidth = 1;
                }

                if (this.displayHeight <= 0)
                {
                    this.displayHeight = 1;
                }
            }

            if (this.currentScreen != null)
            {
                this.resize(this.displayWidth, this.displayHeight);
            }

            Display.setFullscreen(this.fullscreen);
            Display.update();
        }
        catch (Exception var2)
        {
            var2.printStackTrace();
        }
    }

    /**
     * Called to resize the current screen.
     */
    private void resize(int par1, int par2)
    {
        if (par1 <= 0)
        {
            par1 = 1;
        }

        if (par2 <= 0)
        {
            par2 = 1;
        }

        this.displayWidth = par1;
        this.displayHeight = par2;

        if (this.currentScreen != null)
        {
            ScaledResolution var3 = new ScaledResolution(this.gameSettings, par1, par2);
            int var4 = var3.getScaledWidth();
            int var5 = var3.getScaledHeight();
            this.currentScreen.setWorldAndResolution(this, var4, var5);
        }
    }

    private void startThreadCheckHasPaid()
    {
        (new ThreadCheckHasPaid(this)).start();
    }

    /**
     * Runs the current tick.
     */
    public void runTick()
    {
        if (this.rightClickDelayTimer > 0)
        {
            --this.rightClickDelayTimer;
        }

        if (this.ticksRan == 6000)
        {
            this.startThreadCheckHasPaid();
        }

        Profiler.startSection("stats");
        this.statFileWriter.func_27178_d();
        Profiler.endStartSection("gui");

        if (!this.isGamePaused)
        {
            this.ingameGUI.updateTick();
        }

        Profiler.endStartSection("pick");
        this.entityRenderer.getMouseOver(1.0F);
        Profiler.endStartSection("centerChunkSource");
        int var3;

        if (this.thePlayer != null)
        {
            IChunkProvider var1 = this.theWorld.getChunkProvider();

            if (var1 instanceof ChunkProviderLoadOrGenerate)
            {
                ChunkProviderLoadOrGenerate var2 = (ChunkProviderLoadOrGenerate)var1;
                var3 = MathHelper.floor_float((float)((int)this.thePlayer.posX)) >> 4;
                int var4 = MathHelper.floor_float((float)((int)this.thePlayer.posZ)) >> 4;
                var2.setCurrentChunkOver(var3, var4);
            }
        }

        Profiler.endStartSection("gameMode");

        if (!this.isGamePaused && this.theWorld != null)
        {
            this.playerController.updateController();
        }

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.renderEngine.getTexture("/terrain.png"));
        Profiler.endStartSection("textures");

        if (!this.isGamePaused)
        {
            this.renderEngine.updateDynamicTextures();
        }

        if (this.currentScreen == null && this.thePlayer != null)
        {
            if (this.thePlayer.getEntityHealth() <= 0)
            {
                this.displayGuiScreen((GuiScreen)null);
            }
            else if (this.thePlayer.isPlayerSleeping() && this.theWorld != null && this.theWorld.isRemote)
            {
                this.displayGuiScreen(new GuiSleepMP());
            }
        }
        else if (this.currentScreen != null && this.currentScreen instanceof GuiSleepMP && !this.thePlayer.isPlayerSleeping())
        {
            this.displayGuiScreen((GuiScreen)null);
        }

        if (this.currentScreen != null)
        {
            this.leftClickCounter = 10000;
        }

        if (this.currentScreen != null)
        {
            this.currentScreen.handleInput();

            if (this.currentScreen != null)
            {
                this.currentScreen.guiParticles.update();
                this.currentScreen.updateScreen();
            }
        }

        if (this.currentScreen == null || this.currentScreen.allowUserInput)
        {
            Profiler.endStartSection("mouse");

            while (Mouse.next())
            {
                KeyBinding.setKeyBindState(Mouse.getEventButton() - 100, Mouse.getEventButtonState());

                if (Mouse.getEventButtonState())
                {
                    KeyBinding.onTick(Mouse.getEventButton() - 100);
                }

                long var5 = System.currentTimeMillis() - this.systemTime;

                if (var5 <= 200L)
                {
                    var3 = Mouse.getEventDWheel();

                    if (var3 != 0)
                    {
                        this.thePlayer.inventory.changeCurrentItem(var3);

                        if (this.gameSettings.noclip)
                        {
                            if (var3 > 0)
                            {
                                var3 = 1;
                            }

                            if (var3 < 0)
                            {
                                var3 = -1;
                            }

                            this.gameSettings.noclipRate += (float)var3 * 0.25F;
                        }
                    }

                    if (this.currentScreen == null)
                    {
                        if (!this.inGameHasFocus && Mouse.getEventButtonState())
                        {
                            this.setIngameFocus();
                        }
                    }
                    else if (this.currentScreen != null)
                    {
                        this.currentScreen.handleMouseInput();
                    }
                }
            }

            if (this.leftClickCounter > 0)
            {
                --this.leftClickCounter;
            }

            Profiler.endStartSection("keyboard");

            while (Keyboard.next())
            {
                KeyBinding.setKeyBindState(Keyboard.getEventKey(), Keyboard.getEventKeyState());

                if (Keyboard.getEventKeyState())
                {
                    KeyBinding.onTick(Keyboard.getEventKey());
                }

                if (Keyboard.getEventKeyState())
                {
                    if (Keyboard.getEventKey() == 87)
                    {
                        this.toggleFullscreen();
                    }
                    else
                    {
                        if (this.currentScreen != null)
                        {
                            this.currentScreen.handleKeyboardInput();
                        }
                        else
                        {
                            if (Keyboard.getEventKey() == 1)
                            {
                                this.displayInGameMenu();
                            }

                            if (Keyboard.getEventKey() == 31 && Keyboard.isKeyDown(61))
                            {
                                this.forceReload();
                            }

                            if (Keyboard.getEventKey() == 20 && Keyboard.isKeyDown(61))
                            {
                                this.renderEngine.refreshTextures();
                            }

                            if (Keyboard.getEventKey() == 33 && Keyboard.isKeyDown(61))
                            {
                                boolean var6 = Keyboard.isKeyDown(42) | Keyboard.isKeyDown(54);
                                this.gameSettings.setOptionValue(EnumOptions.RENDER_DISTANCE, var6 ? -1 : 1);
                            }

                            if (Keyboard.getEventKey() == 30 && Keyboard.isKeyDown(61))
                            {
                                this.renderGlobal.loadRenderers();
                            }

                            if (Keyboard.getEventKey() == 59)
                            {
                                this.gameSettings.hideGUI = !this.gameSettings.hideGUI;
                            }

                            if (Keyboard.getEventKey() == 61)
                            {
                                this.gameSettings.showDebugInfo = !this.gameSettings.showDebugInfo;
                            }

                            if (Keyboard.getEventKey() == 63)
                            {
                                ++this.gameSettings.thirdPersonView;

                                if (this.gameSettings.thirdPersonView > 2)
                                {
                                    this.gameSettings.thirdPersonView = 0;
                                }
                            }

                            if (Keyboard.getEventKey() == 66)
                            {
                                this.gameSettings.smoothCamera = !this.gameSettings.smoothCamera;
                            }
                        }

                        int var7;

                        for (var7 = 0; var7 < 9; ++var7)
                        {
                            if (Keyboard.getEventKey() == 2 + var7)
                            {
                                this.thePlayer.inventory.currentItem = var7;
                            }
                        }

                        if (this.gameSettings.showDebugInfo)
                        {
                            if (Keyboard.getEventKey() == 11)
                            {
                                this.updateDebugProfilerName(0);
                            }

                            for (var7 = 0; var7 < 9; ++var7)
                            {
                                if (Keyboard.getEventKey() == 2 + var7)
                                {
                                    this.updateDebugProfilerName(var7 + 1);
                                }
                            }
                        }
                    }
                }
            }

            while (this.gameSettings.keyBindInventory.isPressed())
            {
                this.displayGuiScreen(new GuiInventory(this.thePlayer));
            }

            while (this.gameSettings.keyBindDrop.isPressed())
            {
                this.thePlayer.func_48152_as();
            }

            while (this.isMultiplayerWorld() && this.gameSettings.keyBindChat.isPressed())
            {
                this.displayGuiScreen(new GuiChat());
            }

            if (this.thePlayer.isUsingItem())
            {
                if (!this.gameSettings.keyBindUseItem.pressed)
                {
                    this.playerController.onStoppedUsingItem(this.thePlayer);
                }

                label309:

                while (true)
                {
                    if (!this.gameSettings.keyBindAttack.isPressed())
                    {
                        while (this.gameSettings.keyBindUseItem.isPressed())
                        {
                            ;
                        }

                        while (true)
                        {
                            if (this.gameSettings.keyBindPickBlock.isPressed())
                            {
                                continue;
                            }

                            break label309;
                        }
                    }
                }
            }
            else
            {
                while (this.gameSettings.keyBindAttack.isPressed())
                {
                    this.clickMouse(0);
                }

                while (this.gameSettings.keyBindUseItem.isPressed())
                {
                    this.clickMouse(1);
                }

                while (this.gameSettings.keyBindPickBlock.isPressed())
                {
                    this.clickMiddleMouseButton();
                }
            }

            if (this.gameSettings.keyBindUseItem.pressed && this.rightClickDelayTimer == 0 && !this.thePlayer.isUsingItem())
            {
                this.clickMouse(1);
            }

            this.sendClickBlockToController(0, this.currentScreen == null && this.gameSettings.keyBindAttack.pressed && this.inGameHasFocus);
        }

        if (this.theWorld != null)
        {
            if (this.thePlayer != null)
            {
                ++this.joinPlayerCounter;

                if (this.joinPlayerCounter == 30)
                {
                    this.joinPlayerCounter = 0;
                    this.theWorld.joinEntityInSurroundings(this.thePlayer);
                }
            }

            if (this.theWorld.getWorldInfo().isHardcoreModeEnabled())
            {
                this.theWorld.difficultySetting = 3;
            }
            else
            {
                this.theWorld.difficultySetting = this.gameSettings.difficulty;
            }

            if (this.theWorld.isRemote)
            {
                this.theWorld.difficultySetting = 1;
            }

            Profiler.endStartSection("gameRenderer");

            if (!this.isGamePaused)
            {
                this.entityRenderer.updateRenderer();
            }

            Profiler.endStartSection("levelRenderer");

            if (!this.isGamePaused)
            {
                this.renderGlobal.updateClouds();
            }

            Profiler.endStartSection("level");

            if (!this.isGamePaused)
            {
                if (this.theWorld.lightningFlash > 0)
                {
                    --this.theWorld.lightningFlash;
                }

                this.theWorld.updateEntities();
            }

            if (!this.isGamePaused || this.isMultiplayerWorld())
            {
                this.theWorld.setAllowedSpawnTypes(this.theWorld.difficultySetting > 0, true);
                this.theWorld.tick();
            }

            Profiler.endStartSection("animateTick");

            if (!this.isGamePaused && this.theWorld != null)
            {
                this.theWorld.randomDisplayUpdates(MathHelper.floor_double(this.thePlayer.posX), MathHelper.floor_double(this.thePlayer.posY), MathHelper.floor_double(this.thePlayer.posZ));
            }

            Profiler.endStartSection("particles");

            if (!this.isGamePaused)
            {
                this.effectRenderer.updateEffects();
            }
        }

        Profiler.endSection();
        this.systemTime = System.currentTimeMillis();
    }

    /**
     * Forces a reload of the sound manager and all the resources. Called in game by holding 'F3' and pressing 'S'.
     */
    private void forceReload()
    {
        System.out.println("FORCING RELOAD!");
        this.sndManager = new SoundManager();
        this.sndManager.loadSoundSettings(this.gameSettings);
        this.downloadResourcesThread.reloadResources();
    }

    /**
     * Checks if the current world is a multiplayer world, returns true if it is, false otherwise.
     */
    public boolean isMultiplayerWorld()
    {
        return this.theWorld != null && this.theWorld.isRemote;
    }

    /**
     * creates a new world or loads an existing one
     */
    public void startWorld(String par1Str, String par2Str, WorldSettings par3WorldSettings)
    {
        this.changeWorld1((World)null);
        System.gc();

        if (this.saveLoader.isOldMapFormat(par1Str))
        {
            this.convertMapFormat(par1Str, par2Str);
        }
        else
        {
            if (this.loadingScreen != null)
            {
                this.loadingScreen.printText(StatCollector.translateToLocal("menu.switchingLevel"));
                this.loadingScreen.displayLoadingString("");
            }

            ISaveHandler var4 = this.saveLoader.getSaveLoader(par1Str, false);
            World var5 = null;
            var5 = new World(var4, par2Str, par3WorldSettings);

            if (var5.isNewWorld)
            {
                this.statFileWriter.readStat(StatList.createWorldStat, 1);
                this.statFileWriter.readStat(StatList.startGameStat, 1);
                this.changeWorld2(var5, StatCollector.translateToLocal("menu.generatingLevel"));
            }
            else
            {
                this.statFileWriter.readStat(StatList.loadWorldStat, 1);
                this.statFileWriter.readStat(StatList.startGameStat, 1);
                this.changeWorld2(var5, StatCollector.translateToLocal("menu.loadingLevel"));
            }
        }
    }

    /**
     * Will use a portal teleport switching the dimension the player is in.
     */
    public void usePortal(int par1)
    {
    	usePortal(par1, new Teleporter());
    }
    
    public void usePortal(int par1, Teleporter teleporter)
    {
        int var2 = this.thePlayer.dimension;
        this.thePlayer.dimension = par1;
        this.theWorld.setEntityDead(this.thePlayer);
        this.thePlayer.isDead = false;
        WorldProvider pNew = WorldProvider.getProviderForDimension(par1);
        WorldProvider pOld = WorldProvider.getProviderForDimension(var2);
        double var3 = this.thePlayer.posX;
        double var5 = this.thePlayer.posZ;
        double var7 = pOld.getMovementFactor() / pNew.getMovementFactor();
        var3 *= var7;
        var5 *= var7;
        World var9 = new World(theWorld, pNew);

        if (this.thePlayer.isEntityAlive())
        {
            this.theWorld.updateEntityWithOptionalForce(this.thePlayer, false);
        }
        
        if (thePlayer.dimension == 1)
        {
            ChunkCoordinates var10 = var9.getEntrancePortalLocation();
            var3 = (double)var10.posX;
            this.thePlayer.posY = (double)var10.posY;
            var5 = (double)var10.posZ;
        }
        
        if (thePlayer.dimension == 0)
        {
        	changeWorld(var9, pOld.getDepartMessage(), thePlayer);
        }
        else
        {
        	changeWorld(var9, pOld.getWelcomeMessage(), thePlayer);
        }

        this.thePlayer.worldObj = this.theWorld;
        System.out.println("Teleported to " + this.theWorld.worldProvider.worldType);

        if (this.thePlayer.isEntityAlive())
        {
            this.thePlayer.setLocationAndAngles(var3, this.thePlayer.posY, var5, this.thePlayer.rotationYaw, this.thePlayer.rotationPitch);
            this.theWorld.updateEntityWithOptionalForce(this.thePlayer, false);
            teleporter.placeInPortal(this.theWorld, this.thePlayer);
        }
    }

    /**
     * Unloads the current world, and displays a String while waiting
     */
    public void exitToMainMenu(String par1Str)
    {
        this.theWorld = null;
        this.changeWorld2((World)null, par1Str);
    }

    /**
     * Changes the world, no message, no player.
     */
    public void changeWorld1(World par1World)
    {
        this.changeWorld2(par1World, "");
    }

    /**
     * Changes the world with given message, no player.
     */
    public void changeWorld2(World par1World, String par2Str)
    {
        this.changeWorld(par1World, par2Str, (EntityPlayer)null);
    }

    /**
     * first argument is the world to change to, second one is a loading message and the third the player itself
     */
    public void changeWorld(World par1World, String par2Str, EntityPlayer par3EntityPlayer)
    {
        this.statFileWriter.func_27175_b();
        this.statFileWriter.syncStats();
        this.renderViewEntity = null;

        if (this.loadingScreen != null)
        {
            this.loadingScreen.printText(par2Str);
            this.loadingScreen.displayLoadingString("");
        }

        this.sndManager.playStreaming((String)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);

        if (this.theWorld != null)
        {
            this.theWorld.saveWorldIndirectly(this.loadingScreen);
        }

        this.theWorld = par1World;

        if (par1World != null)
        {
            if (this.playerController != null)
            {
                this.playerController.onWorldChange(par1World);
            }

            if (!this.isMultiplayerWorld())
            {
                if (par3EntityPlayer == null)
                {
                    this.thePlayer = (EntityPlayerSP)par1World.func_4085_a(EntityPlayerSP.class);
                }
            }
            else if (this.thePlayer != null)
            {
                this.thePlayer.preparePlayerToSpawn();

                if (par1World != null)
                {
                    par1World.spawnEntityInWorld(this.thePlayer);
                }
            }

            if (!par1World.isRemote)
            {
                this.preloadWorld(par2Str);
            }

            if (this.thePlayer == null)
            {
                this.thePlayer = (EntityPlayerSP)this.playerController.createPlayer(par1World);
                this.thePlayer.preparePlayerToSpawn();
                this.playerController.flipPlayer(this.thePlayer);
            }

            this.thePlayer.movementInput = new MovementInputFromOptions(this.gameSettings);

            if (this.renderGlobal != null)
            {
                this.renderGlobal.changeWorld(par1World);
            }

            if (this.effectRenderer != null)
            {
                this.effectRenderer.clearEffects(par1World);
            }

            if (par3EntityPlayer != null)
            {
                par1World.func_6464_c();
            }

            IChunkProvider var4 = par1World.getChunkProvider();

            if (var4 instanceof ChunkProviderLoadOrGenerate)
            {
                ChunkProviderLoadOrGenerate var5 = (ChunkProviderLoadOrGenerate)var4;
                int var6 = MathHelper.floor_float((float)((int)this.thePlayer.posX)) >> 4;
                int var7 = MathHelper.floor_float((float)((int)this.thePlayer.posZ)) >> 4;
                var5.setCurrentChunkOver(var6, var7);
            }

            par1World.spawnPlayerWithLoadedChunks(this.thePlayer);
            this.playerController.func_6473_b(this.thePlayer);

            if (par1World.isNewWorld)
            {
                par1World.saveWorldIndirectly(this.loadingScreen);
            }

            this.renderViewEntity = this.thePlayer;
        }
        else
        {
            this.saveLoader.flushCache();
            this.thePlayer = null;
        }

        System.gc();
        this.systemTime = 0L;
    }

    /**
     * Converts from old map format to new map format
     */
    private void convertMapFormat(String par1Str, String par2Str)
    {
        this.loadingScreen.printText("Converting World to " + this.saveLoader.getFormatName());
        this.loadingScreen.displayLoadingString("This may take a while :)");
        this.saveLoader.convertMapFormat(par1Str, this.loadingScreen);
        this.startWorld(par1Str, par2Str, new WorldSettings(0L, 0, true, false, WorldType.field_48635_b));
    }

    /**
     * Display the preload world loading screen then load SP World.
     */
    private void preloadWorld(String par1Str)
    {
        if (this.loadingScreen != null)
        {
            this.loadingScreen.printText(par1Str);
            this.loadingScreen.displayLoadingString(StatCollector.translateToLocal("menu.generatingTerrain"));
        }

        short var2 = 128;

        if (this.playerController.func_35643_e())
        {
            var2 = 64;
        }

        int var3 = 0;
        int var4 = var2 * 2 / 16 + 1;
        var4 *= var4;
        IChunkProvider var5 = this.theWorld.getChunkProvider();
        ChunkCoordinates var6 = this.theWorld.getSpawnPoint();

        if (this.thePlayer != null)
        {
            var6.posX = (int)this.thePlayer.posX;
            var6.posZ = (int)this.thePlayer.posZ;
        }

        if (var5 instanceof ChunkProviderLoadOrGenerate)
        {
            ChunkProviderLoadOrGenerate var7 = (ChunkProviderLoadOrGenerate)var5;
            var7.setCurrentChunkOver(var6.posX >> 4, var6.posZ >> 4);
        }

        for (int var10 = -var2; var10 <= var2; var10 += 16)
        {
            for (int var8 = -var2; var8 <= var2; var8 += 16)
            {
                if (this.loadingScreen != null)
                {
                    this.loadingScreen.setLoadingProgress(var3++ * 100 / var4);
                }

                this.theWorld.getBlockId(var6.posX + var10, 64, var6.posZ + var8);

                if (!this.playerController.func_35643_e())
                {
                }
            }
        }

        if (!this.playerController.func_35643_e())
        {
            if (this.loadingScreen != null)
            {
                this.loadingScreen.displayLoadingString(StatCollector.translateToLocal("menu.simulating"));
            }

            boolean var9 = true;
            this.theWorld.dropOldChunks();
        }
    }

    /**
     * Installs a resource. Currently only sounds are download so this method just adds them to the SoundManager.
     */
    public void installResource(String par1Str, File par2File)
    {
        int var3 = par1Str.indexOf("/");
        String var4 = par1Str.substring(0, var3);
        par1Str = par1Str.substring(var3 + 1);

        if (var4.equalsIgnoreCase("sound"))
        {
            this.sndManager.addSound(par1Str, par2File);
        }
        else if (var4.equalsIgnoreCase("newsound"))
        {
            this.sndManager.addSound(par1Str, par2File);
        }
        else if (var4.equalsIgnoreCase("streaming"))
        {
            this.sndManager.addStreaming(par1Str, par2File);
        }
        else if (var4.equalsIgnoreCase("music"))
        {
            this.sndManager.addMusic(par1Str, par2File);
        }
        else if (var4.equalsIgnoreCase("newmusic"))
        {
            this.sndManager.addMusic(par1Str, par2File);
        }
    }

    /**
     * A String of renderGlobal.getDebugInfoRenders
     */
    public String debugInfoRenders()
    {
        return this.renderGlobal.getDebugInfoRenders();
    }

    /**
     * Gets the information in the F3 menu about how many entities are infront/around you
     */
    public String getEntityDebug()
    {
        return this.renderGlobal.getDebugInfoEntities();
    }

    /**
     * Gets the name of the world's current chunk provider
     */
    public String getWorldProviderName()
    {
        return this.theWorld.getProviderName();
    }

    /**
     * A String of how many entities are in the world
     */
    public String debugInfoEntities()
    {
        return "P: " + this.effectRenderer.getStatistics() + ". T: " + this.theWorld.getDebugLoadedEntities();
    }

    /**
     * Called when the respawn button is pressed after the player dies.
     */
    public void respawn(boolean par1, int par2, boolean par3)
    {
        if (!this.theWorld.isRemote && !this.theWorld.worldProvider.canRespawnHere())
        {
            this.usePortal(0);
        }

        ChunkCoordinates var4 = null;
        ChunkCoordinates var5 = null;
        boolean var6 = true;

        if (this.thePlayer != null && !par1)
        {
            var4 = this.thePlayer.getSpawnChunk();

            if (var4 != null)
            {
                var5 = EntityPlayer.verifyRespawnCoordinates(this.theWorld, var4);

                if (var5 == null)
                {
                    this.thePlayer.addChatMessage("tile.bed.notValid");
                }
            }
        }

        if (var5 == null)
        {
            var5 = this.theWorld.getSpawnPoint();
            var6 = false;
        }

        IChunkProvider var7 = this.theWorld.getChunkProvider();

        if (var7 instanceof ChunkProviderLoadOrGenerate)
        {
            ChunkProviderLoadOrGenerate var8 = (ChunkProviderLoadOrGenerate)var7;
            var8.setCurrentChunkOver(var5.posX >> 4, var5.posZ >> 4);
        }

        this.theWorld.setSpawnLocation();
        this.theWorld.updateEntityList();
        int var10 = 0;

        if (this.thePlayer != null)
        {
            var10 = this.thePlayer.entityId;
            this.theWorld.setEntityDead(this.thePlayer);
        }

        EntityPlayerSP var9 = this.thePlayer;
        this.renderViewEntity = null;
        this.thePlayer = (EntityPlayerSP)this.playerController.createPlayer(this.theWorld);

        if (par3)
        {
            this.thePlayer.copyPlayer(var9);
        }

        this.thePlayer.dimension = par2;
        this.renderViewEntity = this.thePlayer;
        this.thePlayer.preparePlayerToSpawn();

        if (var6)
        {
            this.thePlayer.setSpawnChunk(var4);
            this.thePlayer.setLocationAndAngles((double)((float)var5.posX + 0.5F), (double)((float)var5.posY + 0.1F), (double)((float)var5.posZ + 0.5F), 0.0F, 0.0F);
        }

        this.playerController.flipPlayer(this.thePlayer);
        this.theWorld.spawnPlayerWithLoadedChunks(this.thePlayer);
        this.thePlayer.movementInput = new MovementInputFromOptions(this.gameSettings);
        this.thePlayer.entityId = var10;
        this.thePlayer.func_6420_o();
        this.playerController.func_6473_b(this.thePlayer);
        this.preloadWorld(StatCollector.translateToLocal("menu.respawning"));

        if (this.currentScreen instanceof GuiGameOver)
        {
            this.displayGuiScreen((GuiScreen)null);
        }
    }

    public static void startMainThread1(String par0Str, String par1Str)
    {
        try
        {
            startMainThread(par0Str, par1Str, (String)null);
        }
        catch (LWJGLException e)
        {
        }
    }

    public static void startMainThread(String par0Str, String par1Str, String par2Str) throws LWJGLException
    {
        boolean var3 = false;
        Frame var5 = new Frame("Minecraft");
        Canvas var6 = new Canvas();
        var5.setLayout(new BorderLayout());
        var5.add(var6, "Center");
        var6.setPreferredSize(new Dimension(854, 480));
        var5.pack();
        var5.setLocationRelativeTo((Component)null);
        MinecraftImpl var7 = new MinecraftImpl(var5, var6, (MinecraftApplet)null, 854, 480, var3, var5);
        Thread var8 = new Thread(var7, "Minecraft main thread");
        var8.setPriority(10);
        var7.minecraftUri = "www.minecraft.net";

        if (par0Str != null && par1Str != null)
        {
            var7.session = new Session(par0Str, par1Str);
        }
        else
        {
            var7.session = new Session("Player" + System.currentTimeMillis() % 1000L, "");
        }

        if (par2Str != null)
        {
            String[] var9 = par2Str.split(":");
            var7.setServer(var9[0], Integer.parseInt(var9[1]));
        }

        var5.setVisible(true);
        var5.addWindowListener(new GameWindowListener(var7, var8));
        System.out.println("LWJGL Version: " + Sys.getVersion());
        var8.start();
    }

    /**
     * get the client packet send queue
     */
    public NetClientHandler getSendQueue()
    {
        return this.thePlayer instanceof EntityClientPlayerMP ? ((EntityClientPlayerMP)this.thePlayer).sendQueue : null;
    }

    public static void main(String[] par0ArrayOfStr)
    {
        String var1 = null;
        String var2 = null;
        var1 = "Player" + System.currentTimeMillis() % 1000L;

        if (par0ArrayOfStr.length > 0)
        {
            var1 = par0ArrayOfStr[0];
        }

        var2 = "-";

        if (par0ArrayOfStr.length > 1)
        {
            var2 = par0ArrayOfStr[1];
        }

        startMainThread1(var1, var2);
    }

    public static boolean isGuiEnabled()
    {
        return theMinecraft == null || !theMinecraft.gameSettings.hideGUI;
    }

    public static boolean isFancyGraphicsEnabled()
    {
        return theMinecraft != null && theMinecraft.gameSettings.fancyGraphics;
    }

    /**
     * Returns if ambient occlusion is enabled
     */
    public static boolean isAmbientOcclusionEnabled()
    {
        return theMinecraft != null && theMinecraft.gameSettings.ambientOcclusion;
    }

    public static boolean isDebugInfoEnabled()
    {
        return theMinecraft != null && theMinecraft.gameSettings.showDebugInfo;
    }

    /**
     * Returns true if string begins with '/'
     */
    public boolean lineIsCommand(String par1Str)
    {
        if (par1Str.startsWith("/"))
        {
            ;
        }

        return false;
    }

    /**
     * Called when the middle mouse button gets clicked
     */
    private void clickMiddleMouseButton()
    {
        if (this.objectMouseOver != null)
        {
            int var1 = this.theWorld.getBlockId(this.objectMouseOver.blockX, this.objectMouseOver.blockY, this.objectMouseOver.blockZ);

            if (var1 == Block.grass.blockID)
            {
                var1 = Block.dirt.blockID;
            }

            if (var1 == Block.stairDouble.blockID)
            {
                var1 = Block.stairSingle.blockID;
            }

            if (var1 == Block.bedrock.blockID)
            {
                var1 = Block.stone.blockID;
            }

            int var2 = 0;
            boolean var3 = false;

            if (Item.itemsList[var1] != null && Item.itemsList[var1].getHasSubtypes())
            {
                var2 = this.theWorld.getBlockMetadata(this.objectMouseOver.blockX, this.objectMouseOver.blockY, this.objectMouseOver.blockZ);
                var3 = true;
            }

            this.thePlayer.inventory.setCurrentItem(var1, var2, var3, this.playerController instanceof PlayerControllerCreative);
        }
    }
}
