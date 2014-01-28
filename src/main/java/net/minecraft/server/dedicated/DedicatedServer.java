package net.minecraft.server.dedicated;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.ServerCommand;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.rcon.IServer;
import net.minecraft.network.rcon.RConThreadMain;
import net.minecraft.network.rcon.RConThreadQuery;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.gui.MinecraftServerGui;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.CryptManager;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.SERVER)
public class DedicatedServer extends MinecraftServer implements IServer
{
    private static final Logger field_155771_h = LogManager.getLogger();
    private final List pendingCommandList = Collections.synchronizedList(new ArrayList());
    private RConThreadQuery theRConThreadQuery;
    private RConThreadMain theRConThreadMain;
    private PropertyManager settings;
    private boolean canSpawnStructures;
    private WorldSettings.GameType gameType;
    private boolean guiIsEnabled;
    private static final String __OBFID = "CL_00001784";

    public DedicatedServer(File par1File)
    {
        super(par1File, Proxy.NO_PROXY);
        Thread thread = new Thread("Server Infinisleeper")
        {
            private static final String __OBFID = "CL_00001787";
            {
                this.setDaemon(true);
                this.start();
            }
            public void run()
            {
                while (true)
                {
                    try
                    {
                        while (true)
                        {
                            Thread.sleep(2147483647L);
                        }
                    }
                    catch (InterruptedException interruptedexception)
                    {
                        ;
                    }
                }
            }
        };
    }

    // JAVADOC METHOD $$ func_71197_b
    protected boolean startServer() throws IOException
    {
        Thread thread = new Thread("Server console handler")
        {
            private static final String __OBFID = "CL_00001786";
            public void run()
            {
                BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(System.in));
                String s4;

                try
                {
                    while (!DedicatedServer.this.isServerStopped() && DedicatedServer.this.isServerRunning() && (s4 = bufferedreader.readLine()) != null)
                    {
                        DedicatedServer.this.addPendingCommand(s4, DedicatedServer.this);
                    }
                }
                catch (IOException ioexception1)
                {
                    DedicatedServer.field_155771_h.error("Exception handling console input", ioexception1);
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
        field_155771_h.info("Starting minecraft server version 1.7.2");

        if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L)
        {
            field_155771_h.warn("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
        }

        FMLCommonHandler.instance().onServerStart(this);

        field_155771_h.info("Loading properties");
        this.settings = new PropertyManager(new File("server.properties"));

        if (this.isSinglePlayer())
        {
            this.setHostname("127.0.0.1");
        }
        else
        {
            this.setOnlineMode(this.settings.getBooleanProperty("online-mode", true));
            this.setHostname(this.settings.getProperty("server-ip", ""));
        }

        this.setCanSpawnAnimals(this.settings.getBooleanProperty("spawn-animals", true));
        this.setCanSpawnNPCs(this.settings.getBooleanProperty("spawn-npcs", true));
        this.setAllowPvp(this.settings.getBooleanProperty("pvp", true));
        this.setAllowFlight(this.settings.getBooleanProperty("allow-flight", false));
        this.func_155759_m(this.settings.getProperty("resource-pack", ""));
        this.setMOTD(this.settings.getProperty("motd", "A Minecraft Server"));
        this.setForceGamemode(this.settings.getBooleanProperty("force-gamemode", false));
        this.func_143006_e(this.settings.getIntProperty("player-idle-timeout", 0));

        if (this.settings.getIntProperty("difficulty", 1) < 0)
        {
            this.settings.setProperty("difficulty", Integer.valueOf(0));
        }
        else if (this.settings.getIntProperty("difficulty", 1) > 3)
        {
            this.settings.setProperty("difficulty", Integer.valueOf(3));
        }

        this.canSpawnStructures = this.settings.getBooleanProperty("generate-structures", true);
        int i = this.settings.getIntProperty("gamemode", WorldSettings.GameType.SURVIVAL.getID());
        this.gameType = WorldSettings.getGameTypeById(i);
        field_155771_h.info("Default game type: " + this.gameType);
        InetAddress inetaddress = null;

        if (this.getServerHostname().length() > 0)
        {
            inetaddress = InetAddress.getByName(this.getServerHostname());
        }

        if (this.getServerPort() < 0)
        {
            this.setServerPort(this.settings.getIntProperty("server-port", 25565));
        }

        field_155771_h.info("Generating keypair");
        this.setKeyPair(CryptManager.createNewKeyPair());
        field_155771_h.info("Starting Minecraft server on " + (this.getServerHostname().length() == 0 ? "*" : this.getServerHostname()) + ":" + this.getServerPort());

        try
        {
            this.func_147137_ag().func_151265_a(inetaddress, this.getServerPort());
        }
        catch (IOException ioexception)
        {
            field_155771_h.warn("**** FAILED TO BIND TO PORT!");
            field_155771_h.warn("The exception was: {}", new Object[] {ioexception.toString()});
            field_155771_h.warn("Perhaps a server is already running on that port?");
            return false;
        }

        if (!this.isServerInOnlineMode())
        {
            field_155771_h.warn("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
            field_155771_h.warn("The server will make no attempt to authenticate usernames. Beware.");
            field_155771_h.warn("While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose.");
            field_155771_h.warn("To change this, set \"online-mode\" to \"true\" in the server.properties file.");
        }

        FMLCommonHandler.instance().onServerStarted();

        this.setConfigurationManager(new DedicatedPlayerList(this));
        long j = System.nanoTime();

        if (this.getFolderName() == null)
        {
            this.setFolderName(this.settings.getProperty("level-name", "world"));
        }

        String s = this.settings.getProperty("level-seed", "");
        String s1 = this.settings.getProperty("level-type", "DEFAULT");
        String s2 = this.settings.getProperty("generator-settings", "");
        long k = (new Random()).nextLong();

        if (s.length() > 0)
        {
            try
            {
                long l = Long.parseLong(s);

                if (l != 0L)
                {
                    k = l;
                }
            }
            catch (NumberFormatException numberformatexception)
            {
                k = (long)s.hashCode();
            }
        }

        WorldType worldtype = WorldType.parseWorldType(s1);

        if (worldtype == null)
        {
            worldtype = WorldType.DEFAULT;
        }

        this.func_155757_ar();
        this.isCommandBlockEnabled();
        this.func_110455_j();
        this.isSnooperEnabled();
        this.setBuildLimit(this.settings.getIntProperty("max-build-height", 256));
        this.setBuildLimit((this.getBuildLimit() + 8) / 16 * 16);
        this.setBuildLimit(MathHelper.clamp_int(this.getBuildLimit(), 64, 256));
        this.settings.setProperty("max-build-height", Integer.valueOf(this.getBuildLimit()));
        if (!FMLCommonHandler.instance().handleServerAboutToStart(this)) { return false; }
        field_155771_h.info("Preparing level \"" + this.getFolderName() + "\"");
        this.loadAllWorlds(this.getFolderName(), this.getFolderName(), k, worldtype, s2);
        long i1 = System.nanoTime() - j;
        String s3 = String.format("%.3fs", new Object[] {Double.valueOf((double)i1 / 1.0E9D)});
        field_155771_h.info("Done (" + s3 + ")! For help, type \"help\" or \"?\"");

        if (this.settings.getBooleanProperty("enable-query", false))
        {
            field_155771_h.info("Starting GS4 status listener");
            this.theRConThreadQuery = new RConThreadQuery(this);
            this.theRConThreadQuery.startThread();
        }

        if (this.settings.getBooleanProperty("enable-rcon", false))
        {
            field_155771_h.info("Starting remote control listener");
            this.theRConThreadMain = new RConThreadMain(this);
            this.theRConThreadMain.startThread();
        }

        return FMLCommonHandler.instance().handleServerStarting(this);
    }

    public boolean canStructuresSpawn()
    {
        return this.canSpawnStructures;
    }

    public WorldSettings.GameType getGameType()
    {
        return this.gameType;
    }

    public EnumDifficulty func_147135_j()
    {
        return EnumDifficulty.func_151523_a(this.settings.getIntProperty("difficulty", 1));
    }

    // JAVADOC METHOD $$ func_71199_h
    public boolean isHardcore()
    {
        return this.settings.getBooleanProperty("hardcore", false);
    }

    // JAVADOC METHOD $$ func_71228_a
    protected void finalTick(CrashReport par1CrashReport)
    {
        while (this.isServerRunning())
        {
            this.executePendingCommands();

            try
            {
                Thread.sleep(10L);
            }
            catch (InterruptedException interruptedexception)
            {
                ;
            }
        }
    }

    // JAVADOC METHOD $$ func_71230_b
    public CrashReport addServerInfoToCrashReport(CrashReport par1CrashReport)
    {
        par1CrashReport = super.addServerInfoToCrashReport(par1CrashReport);
        par1CrashReport.getCategory().addCrashSectionCallable("Is Modded", new Callable()
        {
            private static final String __OBFID = "CL_00001785";
            public String call()
            {
                String s = DedicatedServer.this.getServerModName();
                return !s.equals("vanilla") ? "Definitely; Server brand changed to \'" + s + "\'" : "Unknown (can\'t tell)";
            }
        });
        par1CrashReport.getCategory().addCrashSectionCallable("Type", new Callable()
        {
            private static final String __OBFID = "CL_00001788";
            public String call()
            {
                return "Dedicated Server (map_server.txt)";
            }
        });
        return par1CrashReport;
    }

    // JAVADOC METHOD $$ func_71240_o
    protected void systemExitNow()
    {
        System.exit(0);
    }

    public void updateTimeLightAndEntities()
    {
        super.updateTimeLightAndEntities();
        this.executePendingCommands();
    }

    public boolean getAllowNether()
    {
        return this.settings.getBooleanProperty("allow-nether", true);
    }

    public boolean allowSpawnMonsters()
    {
        return this.settings.getBooleanProperty("spawn-monsters", true);
    }

    public void addServerStatsToSnooper(PlayerUsageSnooper par1PlayerUsageSnooper)
    {
        par1PlayerUsageSnooper.addData("whitelist_enabled", Boolean.valueOf(this.getConfigurationManager().isWhiteListEnabled()));
        par1PlayerUsageSnooper.addData("whitelist_count", Integer.valueOf(this.getConfigurationManager().getWhiteListedPlayers().size()));
        super.addServerStatsToSnooper(par1PlayerUsageSnooper);
    }

    // JAVADOC METHOD $$ func_70002_Q
    public boolean isSnooperEnabled()
    {
        return this.settings.getBooleanProperty("snooper-enabled", true);
    }

    public void addPendingCommand(String par1Str, ICommandSender par2ICommandSender)
    {
        this.pendingCommandList.add(new ServerCommand(par1Str, par2ICommandSender));
    }

    public void executePendingCommands()
    {
        while (!this.pendingCommandList.isEmpty())
        {
            ServerCommand servercommand = (ServerCommand)this.pendingCommandList.remove(0);
            this.getCommandManager().executeCommand(servercommand.sender, servercommand.command);
        }
    }

    public boolean isDedicatedServer()
    {
        return true;
    }

    public DedicatedPlayerList getConfigurationManager()
    {
        return (DedicatedPlayerList)super.getConfigurationManager();
    }

    // JAVADOC METHOD $$ func_71327_a
    public int getIntProperty(String par1Str, int par2)
    {
        return this.settings.getIntProperty(par1Str, par2);
    }

    // JAVADOC METHOD $$ func_71330_a
    public String getStringProperty(String par1Str, String par2Str)
    {
        return this.settings.getProperty(par1Str, par2Str);
    }

    // JAVADOC METHOD $$ func_71332_a
    public boolean getBooleanProperty(String par1Str, boolean par2)
    {
        return this.settings.getBooleanProperty(par1Str, par2);
    }

    // JAVADOC METHOD $$ func_71328_a
    public void setProperty(String par1Str, Object par2Obj)
    {
        this.settings.setProperty(par1Str, par2Obj);
    }

    // JAVADOC METHOD $$ func_71326_a
    public void saveProperties()
    {
        this.settings.saveProperties();
    }

    // JAVADOC METHOD $$ func_71329_c
    public String getSettingsFilename()
    {
        File file1 = this.settings.getPropertiesFile();
        return file1 != null ? file1.getAbsolutePath() : "No settings file";
    }

    public void func_120011_ar()
    {
        MinecraftServerGui.func_120016_a(this);
        this.guiIsEnabled = true;
    }

    public boolean getGuiEnabled()
    {
        return this.guiIsEnabled;
    }

    // JAVADOC METHOD $$ func_71206_a
    public String shareToLAN(WorldSettings.GameType par1EnumGameType, boolean par2)
    {
        return "";
    }

    // JAVADOC METHOD $$ func_82356_Z
    public boolean isCommandBlockEnabled()
    {
        return this.settings.getBooleanProperty("enable-command-block", false);
    }

    // JAVADOC METHOD $$ func_82357_ak
    public int getSpawnProtectionSize()
    {
        return this.settings.getIntProperty("spawn-protection", super.getSpawnProtectionSize());
    }

    // JAVADOC METHOD $$ func_96290_a
    public boolean isBlockProtected(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer)
    {
        if (par1World.provider.dimensionId != 0)
        {
            return false;
        }
        else if (this.getConfigurationManager().getOps().isEmpty())
        {
            return false;
        }
        else if (this.getConfigurationManager().isPlayerOpped(par5EntityPlayer.getCommandSenderName()))
        {
            return false;
        }
        else if (this.getSpawnProtectionSize() <= 0)
        {
            return false;
        }
        else
        {
            ChunkCoordinates chunkcoordinates = par1World.getSpawnPoint();
            int l = MathHelper.abs_int(par2 - chunkcoordinates.posX);
            int i1 = MathHelper.abs_int(par4 - chunkcoordinates.posZ);
            int j1 = Math.max(l, i1);
            return j1 <= this.getSpawnProtectionSize();
        }
    }

    public int func_110455_j()
    {
        return this.settings.getIntProperty("op-permission-level", 4);
    }

    public void func_143006_e(int par1)
    {
        super.func_143006_e(par1);
        this.settings.setProperty("player-idle-timeout", Integer.valueOf(par1));
        this.saveProperties();
    }

    public boolean func_155757_ar()
    {
        return this.settings.getBooleanProperty("announce-player-achievements", true);
    }
}