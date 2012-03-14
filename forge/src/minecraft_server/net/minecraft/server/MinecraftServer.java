package net.minecraft.server;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.src.AnvilSaveConverter;
import net.minecraft.src.AnvilSaveHandler;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.ConsoleCommandHandler;
import net.minecraft.src.ConsoleLogManager;
import net.minecraft.src.ConvertProgressUpdater;
import net.minecraft.src.EntityTracker;
import net.minecraft.src.ICommandListener;
import net.minecraft.src.IProgressUpdate;
import net.minecraft.src.ISaveFormat;
import net.minecraft.src.IServer;
import net.minecraft.src.IUpdatePlayerListBox;
import net.minecraft.src.MathHelper;
import net.minecraft.src.ModLoader;
import net.minecraft.src.NetworkListenThread;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet4UpdateTime;
import net.minecraft.src.PropertyManager;
import net.minecraft.src.RConConsoleSource;
import net.minecraft.src.RConThreadMain;
import net.minecraft.src.RConThreadQuery;
import net.minecraft.src.ServerCommand;
import net.minecraft.src.ServerConfigurationManager;
import net.minecraft.src.ServerGUI;
import net.minecraft.src.StatList;
import net.minecraft.src.ThreadCommandReader;
import net.minecraft.src.ThreadServerApplication;
import net.minecraft.src.ThreadServerSleep;
import net.minecraft.src.Vec3D;
import net.minecraft.src.World;
import net.minecraft.src.WorldManager;
import net.minecraft.src.WorldServer;
import net.minecraft.src.WorldServerMulti;
import net.minecraft.src.WorldSettings;
import net.minecraft.src.WorldType;
import net.minecraft.src.forge.DimensionManager;

public class MinecraftServer implements Runnable, ICommandListener, IServer
{
    /** The logging system. */
    public static Logger logger = Logger.getLogger("Minecraft");
    public static HashMap field_6037_b = new HashMap();

    /** The server's hostname */
    private String hostname;

    /** The server's port */
    private int serverPort;

    /** listening server socket and client accept thread */
    public NetworkListenThread networkServer;

    /** Reference to the PropertyManager object. */
    public PropertyManager propertyManagerObj;

    /** The server world manager. */
    //public WorldServer[] worldMngr;
    public long[] field_40027_f = new long[100];
    //public long[][] field_40028_g;
    public Hashtable<Integer, long[]> worldTickTimes = new Hashtable<Integer, long[]>();

    /** the server config manager for this server */
    public ServerConfigurationManager configManager;
    private ConsoleCommandHandler commandHandler;

    /**
     * Indicates whether the server is running or not. Set to false to initiate a shutdown.
     */
    private boolean serverRunning = true;

    /** Indicates to other classes that the server is safely stopped. */
    public boolean serverStopped = false;
    int deathTime = 0;

    /**
     * the task the server is currently working on(and will output on ouputPercentRemaining)
     */
    public String currentTask;

    /** the percentage of the current task finished so far */
    public int percentDone;

    /** List of names of players who are online. */
    private List playersOnline = new ArrayList();

    /** A list containing all the commands entered. */
    private List commands = Collections.synchronizedList(new ArrayList());
    //public EntityTracker[] entityTracker = new EntityTracker[3];

    /** True if the server is in online mode. */
    public boolean onlineMode;

    /** True if server has animals turned on */
    public boolean spawnPeacefulMobs;
    public boolean field_44002_p;

    /** Indicates whether PvP is active on the server or not. */
    public boolean pvpOn;

    /** Determines if flight is Allowed or not */
    public boolean allowFlight;

    /** The server MOTD string. */
    public String motd;

    /** Maximum build height */
    public int buildLimit;
    private long field_48074_E;
    private long field_48075_F;
    private long field_48076_G;
    private long field_48077_H;
    public long[] field_48080_u = new long[100];
    public long[] field_48079_v = new long[100];
    public long[] field_48078_w = new long[100];
    public long[] field_48082_x = new long[100];
    private RConThreadQuery rconQueryThread;
    private RConThreadMain rconMainThread;

    public MinecraftServer()
    {
        new ThreadServerSleep(this);
    }

    /**
     * Initialises the server and starts it.
     */
    private boolean startServer() throws UnknownHostException
    {
        this.commandHandler = new ConsoleCommandHandler(this);
        ThreadCommandReader var1 = new ThreadCommandReader(this);
        var1.setDaemon(true);
        var1.start();
        ConsoleLogManager.init();
        ModLoader.initialize(this);
        logger.info("Starting minecraft server version 1.2.3");

        if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L)
        {
            logger.warning("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
        }

        logger.info("Loading properties");
        this.propertyManagerObj = new PropertyManager(new File("server.properties"));
        this.hostname = this.propertyManagerObj.getStringProperty("server-ip", "");
        this.onlineMode = this.propertyManagerObj.getBooleanProperty("online-mode", true);
        this.spawnPeacefulMobs = this.propertyManagerObj.getBooleanProperty("spawn-animals", true);
        this.field_44002_p = this.propertyManagerObj.getBooleanProperty("spawn-npcs", true);
        this.pvpOn = this.propertyManagerObj.getBooleanProperty("pvp", true);
        this.allowFlight = this.propertyManagerObj.getBooleanProperty("allow-flight", false);
        this.motd = this.propertyManagerObj.getStringProperty("motd", "A Minecraft Server");
        this.motd.replace('\u00a7', '$');
        InetAddress var2 = null;

        if (this.hostname.length() > 0)
        {
            var2 = InetAddress.getByName(this.hostname);
        }

        this.serverPort = this.propertyManagerObj.getIntProperty("server-port", 25565);
        logger.info("Starting Minecraft server on " + (this.hostname.length() != 0 ? this.hostname : "*") + ":" + this.serverPort);

        try
        {
            this.networkServer = new NetworkListenThread(this, var2, this.serverPort);
        }
        catch (IOException var13)
        {
            logger.warning("**** FAILED TO BIND TO PORT!");
            logger.log(Level.WARNING, "The exception was: " + var13.toString());
            logger.warning("Perhaps a server is already running on that port?");
            return false;
        }

        if (!this.onlineMode)
        {
            logger.warning("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
            logger.warning("The server will make no attempt to authenticate usernames. Beware.");
            logger.warning("While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose.");
            logger.warning("To change this, set \"online-mode\" to \"true\" in the server.settings file.");
        }

        this.configManager = new ServerConfigurationManager(this);
        /*
        this.entityTracker[0] = new EntityTracker(this, 0);
        this.entityTracker[1] = new EntityTracker(this, -1);
        this.entityTracker[2] = new EntityTracker(this, 1);
        */
        long var3 = System.nanoTime();
        String var5 = this.propertyManagerObj.getStringProperty("level-name", "world");
        String var6 = this.propertyManagerObj.getStringProperty("level-seed", "");
        String var7 = this.propertyManagerObj.getStringProperty("level-type", "DEFAULT");
        long var8 = (new Random()).nextLong();

        if (var6.length() > 0)
        {
            try
            {
                long var10 = Long.parseLong(var6);

                if (var10 != 0L)
                {
                    var8 = var10;
                }
            }
            catch (NumberFormatException var12)
            {
                var8 = (long)var6.hashCode();
            }
        }

        WorldType var14 = WorldType.parseWorldType(var7);

        if (var14 == null)
        {
            var14 = WorldType.field_48457_b;
        }

        this.buildLimit = this.propertyManagerObj.getIntProperty("max-build-height", 256);
        this.buildLimit = (this.buildLimit + 8) / 16 * 16;
        this.buildLimit = MathHelper.clamp_int(this.buildLimit, 64, 256);
        this.propertyManagerObj.setProperty("max-build-height", Integer.valueOf(this.buildLimit));
        logger.info("Preparing level \"" + var5 + "\"");
        this.initWorld(new AnvilSaveConverter(new File(".")), var5, var8, var14);
        logger.info("Done (" + (System.nanoTime() - var3) + "ns)! For help, type \"help\" or \"?\"");

        if (this.propertyManagerObj.getBooleanProperty("enable-query", false))
        {
            logger.info("Starting GS4 status listener");
            this.rconQueryThread = new RConThreadQuery(this);
            this.rconQueryThread.startThread();
        }

        if (this.propertyManagerObj.getBooleanProperty("enable-rcon", false))
        {
            logger.info("Starting remote control listener");
            this.rconMainThread = new RConThreadMain(this);
            this.rconMainThread.startThread();
        }

        return true;
    }

    /**
     * Initialises the world object.
     */
    private void initWorld(ISaveFormat par1ISaveFormat, String par2Str, long par3, WorldType par5WorldType)
    {
        if (par1ISaveFormat.isOldMapFormat(par2Str))
        {
            logger.info("Converting map!");
            par1ISaveFormat.convertMapFormat(par2Str, new ConvertProgressUpdater(this));
        }
        /*
        this.worldMngr = new WorldServer[3];
        this.field_40028_g = new long[this.worldMngr.length][100];
        */
        int var6 = this.propertyManagerObj.getIntProperty("gamemode", 0);
        var6 = WorldSettings.validGameType(var6);
        logger.info("Default game type: " + var6);
        boolean var7 = this.propertyManagerObj.getBooleanProperty("generate-structures", true);
        WorldSettings var8 = new WorldSettings(par3, var6, var7, false, par5WorldType);
        AnvilSaveHandler var9 = new AnvilSaveHandler(new File("."), par2Str, true);
        
        WorldServer overWorld = new WorldServer(this, var9, par2Str, 0, var8);
        for (Integer id : DimensionManager.getIDs())
        {
            WorldServer world = (id == 0 ? overWorld : new WorldServerMulti(this, var9, par2Str, id, var8, overWorld));
            world.addWorldAccess(new WorldManager(this, overWorld));
            world.difficultySetting = propertyManagerObj.getIntProperty("difficulty", 1);
            world.setAllowedSpawnTypes(propertyManagerObj.getBooleanProperty("spawn-monsters", true), spawnPeacefulMobs);
            world.getWorldInfo().setGameType(var6);
            worldTickTimes.put(id, new long[100]);
        }
        configManager.setPlayerManager(new WorldServer[]{ overWorld });

        short var22 = 196;
        long var23 = System.currentTimeMillis();

        for (Integer id : DimensionManager.getIDs())
        {
            logger.info("Preparing start region for level " + id);
            WorldServer var14 = (WorldServer)DimensionManager.getWorld(id);
            ChunkCoordinates var15 = var14.getSpawnPoint();

            for (int var16 = -var22; var16 <= var22 && this.serverRunning; var16 += 16)
            {
                for (int var17 = -var22; var17 <= var22 && this.serverRunning; var17 += 16)
                {
                    long var18 = System.currentTimeMillis();

                    if (var18 < var23)
                    {
                        var23 = var18;
                    }

                    if (var18 > var23 + 1000L)
                    {
                        int var20 = (var22 * 2 + 1) * (var22 * 2 + 1);
                        int var21 = (var16 + var22) * (var22 * 2 + 1) + var17 + 1;
                        this.outputPercentRemaining("Preparing spawn area", var21 * 100 / var20);
                        var23 = var18;
                    }

                    var14.chunkProviderServer.loadChunk(var15.posX + var16 >> 4, var15.posZ + var17 >> 4);

                    while (var14.updatingLighting() && this.serverRunning)
                    {
                        ;
                    }
                }
            }
        }

        this.clearCurrentTask();
    }

    /**
     * used to display a percent remaining given text and the percentage
     */
    private void outputPercentRemaining(String par1Str, int par2)
    {
        this.currentTask = par1Str;
        this.percentDone = par2;
        logger.info(par1Str + ": " + par2 + "%");
    }

    /**
     * set current task to null and set its percentage to 0
     */
    private void clearCurrentTask()
    {
        this.currentTask = null;
        this.percentDone = 0;
    }

    /**
     * Saves the server's world, called by both save all and stop.
     */
    private void saveServerWorld()
    {
        logger.info("Saving chunks");

        for (World world : DimensionManager.getWorlds())
        {
            WorldServer var2 = (WorldServer)world;
            var2.saveWorld(true, (IProgressUpdate)null);
            var2.func_30006_w();
        }
    }

    /**
     * Saves all necessary data as preparation for stopping the server.
     */
    private void stopServer()
    {
        logger.info("Stopping server");

        if (this.configManager != null)
        {
            this.configManager.savePlayerStates();
        }

        for (World world : DimensionManager.getWorlds())
        {
            WorldServer var2 = (WorldServer)world;

            if (var2 != null)
            {
                this.saveServerWorld();
                break; //Added because saveServerWorld() loops through all worlds anyways.
            }
        }
    }

    /**
     * Sets the serverRunning variable to false, in order to get the server to shut down.
     */
    public void initiateShutdown()
    {
        this.serverRunning = false;
    }

    public void run()
    {
        try
        {
            if (this.startServer())
            {
                long var1 = System.currentTimeMillis();

                for (long var3 = 0L; this.serverRunning; Thread.sleep(1L))
                {
                    ModLoader.onTick(this);
                    long var5 = System.currentTimeMillis();
                    long var7 = var5 - var1;

                    if (var7 > 2000L)
                    {
                        logger.warning("Can\'t keep up! Did the system time change, or is the server overloaded?");
                        var7 = 2000L;
                    }

                    if (var7 < 0L)
                    {
                        logger.warning("Time ran backwards! Did the system time change?");
                        var7 = 0L;
                    }

                    var3 += var7;
                    var1 = var5;

                    if (((WorldServer)DimensionManager.getWorld(0)).isAllPlayersFullyAsleep())
                    {
                        this.doTick();
                        var3 = 0L;
                    }
                    else
                    {
                        while (var3 > 50L)
                        {
                            var3 -= 50L;
                            this.doTick();
                        }
                    }
                }
            }
            else
            {
                while (this.serverRunning)
                {
                    this.commandLineParser();

                    try
                    {
                        Thread.sleep(10L);
                    }
                    catch (InterruptedException var57)
                    {
                        var57.printStackTrace();
                    }
                }
            }
        }
        catch (Throwable var58)
        {
            var58.printStackTrace();
            logger.log(Level.SEVERE, "Unexpected exception", var58);

            while (this.serverRunning)
            {
                this.commandLineParser();

                try
                {
                    Thread.sleep(10L);
                }
                catch (InterruptedException var56)
                {
                    var56.printStackTrace();
                }
            }
        }
        finally
        {
            try
            {
                this.stopServer();
                this.serverStopped = true;
            }
            catch (Throwable var54)
            {
                var54.printStackTrace();
            }
            finally
            {
                System.exit(0);
            }
        }
    }

    private void doTick()
    {
        long var1 = System.nanoTime();
        ArrayList var3 = new ArrayList();
        Iterator var4 = field_6037_b.keySet().iterator();

        while (var4.hasNext())
        {
            String var5 = (String)var4.next();
            int var6 = ((Integer)field_6037_b.get(var5)).intValue();

            if (var6 > 0)
            {
                field_6037_b.put(var5, Integer.valueOf(var6 - 1));
            }
            else
            {
                var3.add(var5);
            }
        }

        int var9;

        for (var9 = 0; var9 < var3.size(); ++var9)
        {
            field_6037_b.remove(var3.get(var9));
        }

        AxisAlignedBB.clearBoundingBoxPool();
        Vec3D.initialize();
        ++this.deathTime;

        for (Integer id : DimensionManager.getIDs())
        {
            long var10 = System.nanoTime();

            if (id == 0 || this.propertyManagerObj.getBooleanProperty("allow-nether", true))
            {
                WorldServer var7 = (WorldServer)DimensionManager.getWorld(id);

                if (this.deathTime % 20 == 0)
                {
                    this.configManager.sendPacketToAllPlayersInDimension(new Packet4UpdateTime(var7.getWorldTime()), var7.worldProvider.worldType);
                }

                var7.tick();

                while (true)
                {
                    if (!var7.updatingLighting())
                    {
                        var7.updateEntities();
                        break;
                    }
                }
            }

            worldTickTimes.get(id)[this.deathTime % 100] = System.nanoTime() - var10;
        }

        this.networkServer.handleNetworkListenThread();
        this.configManager.onTick();

        for (World world : DimensionManager.getWorlds())
        {
            ((WorldServer)world).entityTracker.updateTrackedEntities();
        }

        for (var9 = 0; var9 < this.playersOnline.size(); ++var9)
        {
            ((IUpdatePlayerListBox)this.playersOnline.get(var9)).update();
        }

        try
        {
            this.commandLineParser();
        }
        catch (Exception var8)
        {
            logger.log(Level.WARNING, "Unexpected exception while parsing console command", var8);
        }

        this.field_40027_f[this.deathTime % 100] = System.nanoTime() - var1;
        this.field_48080_u[this.deathTime % 100] = Packet.field_48099_n - this.field_48074_E;
        this.field_48074_E = Packet.field_48099_n;
        this.field_48079_v[this.deathTime % 100] = Packet.field_48100_o - this.field_48075_F;
        this.field_48075_F = Packet.field_48100_o;
        this.field_48078_w[this.deathTime % 100] = Packet.field_48101_l - this.field_48076_G;
        this.field_48076_G = Packet.field_48101_l;
        this.field_48082_x[this.deathTime % 100] = Packet.field_48102_m - this.field_48077_H;
        this.field_48077_H = Packet.field_48102_m;
    }

    /**
     * Adds a command to the command list for processing.
     */
    public void addCommand(String par1Str, ICommandListener par2ICommandListener)
    {
        this.commands.add(new ServerCommand(par1Str, par2ICommandListener));
    }

    /**
     * Parse the command line and call the corresponding action.
     */
    public void commandLineParser()
    {
        while (this.commands.size() > 0)
        {
            ServerCommand var1 = (ServerCommand)this.commands.remove(0);
            this.commandHandler.handleCommand(var1);
        }
    }

    /**
     * Adds a player's name to the list of online players.
     */
    public void addToOnlinePlayerList(IUpdatePlayerListBox par1IUpdatePlayerListBox)
    {
        this.playersOnline.add(par1IUpdatePlayerListBox);
    }

    public static void main(String[] par0ArrayOfStr)
    {
        StatList.func_27092_a();

        try
        {
            MinecraftServer var1 = new MinecraftServer();

            if (!GraphicsEnvironment.isHeadless() && (par0ArrayOfStr.length <= 0 || !par0ArrayOfStr[0].equals("nogui")))
            {
                ServerGUI.initGui(var1);
            }

            (new ThreadServerApplication("Server thread", var1)).start();
        }
        catch (Exception var2)
        {
            logger.log(Level.SEVERE, "Failed to start the minecraft server", var2);
        }
    }

    /**
     * Returns a File object from the specified string.
     */
    public File getFile(String par1Str)
    {
        return new File(par1Str);
    }

    /**
     * Logs the message with a level of INFO.
     */
    public void log(String par1Str)
    {
        logger.info(par1Str);
    }

    /**
     * logs the warning same as: logger.warning(String);
     */
    public void logWarning(String par1Str)
    {
        logger.warning(par1Str);
    }

    /**
     * Gets the players username.
     */
    public String getUsername()
    {
        return "CONSOLE";
    }

    /**
     * gets the worldServer by the given dimension
     */
    public WorldServer getWorldManager(int par1)
    {
        WorldServer ret = (WorldServer)DimensionManager.getWorld(par1);
        return (ret != null ? ret : (WorldServer)DimensionManager.getWorld(0));
    }

    /**
     * gets the entityTracker by the given dimension
     */
    public EntityTracker getEntityTracker(int par1)
    {
        return getWorldManager(par1).entityTracker;
    }

    /**
     * Returns the specified property value as an int, or a default if the property doesn't exist
     */
    public int getIntProperty(String par1Str, int par2)
    {
        return this.propertyManagerObj.getIntProperty(par1Str, par2);
    }

    /**
     * Returns the specified property value as a String, or a default if the property doesn't exist
     */
    public String getStringProperty(String par1Str, String par2Str)
    {
        return this.propertyManagerObj.getStringProperty(par1Str, par2Str);
    }

    /**
     * Saves an Object with the given property name
     */
    public void setProperty(String par1Str, Object par2Obj)
    {
        this.propertyManagerObj.setProperty(par1Str, par2Obj);
    }

    /**
     * Saves all of the server properties to the properties file
     */
    public void saveProperties()
    {
        this.propertyManagerObj.saveProperties();
    }

    /**
     * Returns the filename where server properties are stored
     */
    public String getSettingsFilename()
    {
        File var1 = this.propertyManagerObj.getPropertiesFile();
        return var1 != null ? var1.getAbsolutePath() : "No settings file";
    }

    /**
     * Returns the server hostname
     */
    public String getHostname()
    {
        return this.hostname;
    }

    /**
     * Returns the server port
     */
    public int getPort()
    {
        return this.serverPort;
    }

    /**
     * Returns the server message of the day
     */
    public String getMotd()
    {
        return this.motd;
    }

    /**
     * Returns the server version string
     */
    public String getVersionString()
    {
        return "1.2.3";
    }

    /**
     * Returns the number of players on the server
     */
    public int playersOnline()
    {
        return this.configManager.playersOnline();
    }

    /**
     * Returns the maximum number of players allowed on the server
     */
    public int getMaxPlayers()
    {
        return this.configManager.getMaxPlayers();
    }

    /**
     * Returns a list of usernames of all connected players
     */
    public String[] getPlayerNamesAsList()
    {
        return this.configManager.getPlayerNamesAsList();
    }

    /**
     * Returns the name of the currently loaded world
     */
    public String getWorldName()
    {
        return this.propertyManagerObj.getStringProperty("level-name", "world");
    }

    public String getPlugin()
    {
        return "";
    }

    public void func_40010_o() {}

    /**
     * Handle a command received by an RCon instance
     */
    public String handleRConCommand(String par1Str)
    {
        RConConsoleSource.instance.resetLog();
        this.commandHandler.handleCommand(new ServerCommand(par1Str, RConConsoleSource.instance));
        return RConConsoleSource.instance.getLogContents();
    }

    /**
     * Returns true if debugging is enabled, false otherwise
     */
    public boolean isDebuggingEnabled()
    {
        return false;
    }

    /**
     * Log severe error message
     */
    public void logSevere(String par1Str)
    {
        logger.log(Level.SEVERE, par1Str);
    }

    public void logIn(String par1Str)
    {
        if (this.isDebuggingEnabled())
        {
            logger.log(Level.INFO, par1Str);
        }
    }

    /**
     * Returns the list of ban
     */
    public String[] getBannedIPsList()
    {
        return (String[])((String[])this.configManager.getBannedIPsList().toArray(new String[0]));
    }

    /**
     * Returns a list of banned player names
     */
    public String[] getBannedPlayersList()
    {
        return (String[])((String[])this.configManager.getBannedPlayersList().toArray(new String[0]));
    }

    /**
     * Returns the boolean serverRunning.
     */
    public static boolean isServerRunning(MinecraftServer par0MinecraftServer)
    {
        return par0MinecraftServer.serverRunning;
    }
}
