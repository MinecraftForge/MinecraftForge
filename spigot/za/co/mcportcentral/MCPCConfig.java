package za.co.mcportcentral;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import net.minecraft.server.MinecraftServer;

import org.apache.commons.lang.BooleanUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.base.Throwables;

public class MCPCConfig
{

    private static final File CONFIG_FILE = new File("mcpc.yml");
    private static final String HEADER = "This is the main configuration file for MCPC+.\n"
            + "\n"
            + "If you need help with the configuration or have any questions related to MCPC,\n"
            + "join us at the IRC or drop by our forums and leave a post.\n"
            + "\n"
            + "IRC: #mcportcentral @ irc.esper.net ( http://webchat.esper.net/?channel=mcportcentral )\n"
            + "Forums: http://http://www.mcportcentral.co.za/\n";
    
    /* ======================================================================== */
    
    static {
        settings = new HashMap<String, Setting>();
        Setting setting = Setting.disableWarnings;
    }

    static YamlConfiguration config;
    static int version;
    static Map<String, Command> commands;

    public static final Map<String, Setting> settings;

    public abstract static class Setting<T>
    {
        // Logging options
        public static final BoolSetting dumpMaterials = new BoolSetting("settings.dump-materials", false, "Dumps all materials with their corresponding id's");
        public static final BoolSetting disableWarnings = new BoolSetting("logging.disabled-warnings", false, "Disable warning messages to server admins");
        public static final BoolSetting worldLeakDebug = new BoolSetting("logging.world-leak-debug", false, "Log worlds that appear to be leaking (buggy)");
        public static final BoolSetting connectionLogging = new BoolSetting("logging.connection", false, "Log connections");
        public static final BoolSetting tileEntityPlaceLogging = new BoolSetting("logging.warn-place-no-tileentity", true, "Warn when a mod requests tile entity from a block that doesn't support one");
        public static final BoolSetting tickIntervalLogging = new BoolSetting("logging.tick-intervals", false, "Log when skip interval handlers are ticked");
        public static final BoolSetting chunkLoadLogging = new BoolSetting("logging.chunk-load", false, "Log when chunks are loaded (dev)");
        public static final BoolSetting chunkUnloadLogging = new BoolSetting("logging.chunk-unload", false, "Log when chunks are unloaded (dev)");
        public static final BoolSetting entitySpawnLogging = new BoolSetting("logging.entity-spawn", false, "Log when living entities are spawned (dev)");
        public static final BoolSetting entityDespawnLogging = new BoolSetting("logging.entity-despawn", false, "Log when living entities are despawned (dev)");
        public static final BoolSetting entityDeathLogging = new BoolSetting("logging.entity-death", false, "Log when an entity is destroyed (dev)");
        public static final BoolSetting logWithStackTraces = new BoolSetting("logging.detailed-logging", false, "Add stack traces to dev logging");
        public static final BoolSetting dumpChunksOnDeadlock = new BoolSetting("logging.dump-chunks-on-deadlock", false, "Dump chunks in the event of a deadlock (helps to debug the deadlock)");
        public static final BoolSetting dumpHeapOnDeadlock = new BoolSetting("logging.dump-heap-on-deadlock", false, "Dump the heap in the event of a deadlock (helps to debug the deadlock)");
        public static final BoolSetting dumpThreadsOnWarn = new BoolSetting("logging.dump-threads-on-warn", false, "Dump the the server thread on deadlock warning (delps to debug the deadlock)");
        public static final BoolSetting logEntityCollisionChecks = new BoolSetting("logging.entity-collision-checks", false, "Whether to log entity collision/count checks");
        public static final BoolSetting logEntitySpeedRemoval = new BoolSetting("logging.entity-speed-removal", true, "Whether to log entity removals due to speed");
        public static final IntSetting largeCollisionLogSize = new IntSetting("logging.collision-warn-size", 200, "Number of colliding entities in one spot before logging a warning. Set to 0 to disable");
        public static final IntSetting largeEntityCountLogSize = new IntSetting("logging.entity-count-warn-size", 0, "Number of entities in one dimension logging a warning. Set to 0 to disable");

        // General settings
        public static final BoolSetting loadChunkOnRequest = new BoolSetting("settings.load-chunk-on-request", true, "Forces Chunk Loading on 'Provide' requests (speedup for mods that don't check if a chunk is loaded");
        public static final BoolSetting loadChunkOnForgeTick = new BoolSetting("settings.load-chunk-on-forge-tick", false, "Forces Chunk Loading during Forge Server Tick events");
        public static final BoolSetting overrideTileTicks = new BoolSetting("settings.override-tile-ticks", false, "Global setting to override tile entity tick intervals");
        public static final IntSetting chunkGCGracePeriod = new IntSetting("settings.chunk-gc-grace-period", 700, "Grace period on a loaded chunk before we try to unload it");
        public static final IntSetting largeBoundingBoxLogSize = new IntSetting("settings.entity-bounding-box-max-size", 1000, "Max size of an entity's bounding box before removing it (either being too large or bugged and 'moving' too fast)");
        public static final IntSetting entityMaxSpeed = new IntSetting("settings.entity-max-speed", 100, "Square of the max speed of an entity before removing it");

        // Server options
        public static final BoolSetting infiniteWaterSource = new BoolSetting("world-settings.default.infinite-water-source", true, "Vanilla water source behavior - is infinite");
        public static final BoolSetting flowingLavaDecay = new BoolSetting("world-settings.default.flowing-lava-decay", false, "Lava behaves like vanilla water when source block is removed");
        public static final BoolSetting fakePlayerLogin = new BoolSetting("fake-players.do-login", false, "Raise login events for fake players");

        // Plug-in options
        public static final BoolSetting remapPluginFile = new BoolSetting("plugin-settings.default.remap-plugin-file", false, "Remap the plugin file (dev)");

        public final String path;
        public final T def;
        public final String description;

        private Setting(String path, T def, String description)
        {
            this.path = path;
            this.def = def;
            this.description = description;
            settings.put(path, this);
        }

        public abstract T getValue();

        public abstract void setValue(String value);

        public static class IntSetting extends Setting<Integer>
        {
            private Integer value;
            
            private IntSetting(String path, Integer def, String description)
            {
                super(path, def, description);
                this.value = def;
            }

            @Override
            public Integer getValue()
            {
                if (value != null) return value;
                config.addDefault(path, def);
                this.value = config.getInt(path);
                return value;
            }

            @Override
            public void setValue(String value)
            {
                this.value = org.apache.commons.lang.math.NumberUtils.toInt(value, def);
                config.set(path, value);
            }
        }

        public static class BoolSetting extends Setting<Boolean>
        {
            Boolean value;
            private BoolSetting(String path, Boolean def, String description)
            {
                super(path, def, description);
                this.value = def;
            }

            @Override
            public Boolean getValue()
            {
                if (value != null) return value;
                config.addDefault(path, def);
                this.value = config.getBoolean(path);
                return value;
            }

            @Override
            public void setValue(String value)
            {
                this.value = BooleanUtils.toBooleanObject(value);
                this.value = this.value == null ? def : this.value;
                config.set(path, this.value);
            }
        }
    }

    /* ======================================================================== */

    public static void init()
    {
        if (config == null)
        {
            commands = new HashMap<String, Command>();
            commands.put("mcpc", new MCPCCommand());

            load();
        }
    }

    public static void registerCommands()
    {
        for (Map.Entry<String, Command> entry : commands.entrySet())
        {
            MinecraftServer.getServer().server.getCommandMap().register(entry.getKey(), "mcpc", entry.getValue());
        }
    }

    public static void save()
    {
        try
        {
            config.save(CONFIG_FILE);
        }
        catch (IOException ex)
        {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save " + CONFIG_FILE, ex);
        }
    }

    public static void load()
    {
        try
        {
            config = YamlConfiguration.loadConfiguration(CONFIG_FILE);
            String header = HEADER + "\n";
            for (Setting toggle : settings.values())
            {
                header += "Setting: " + toggle.path + " Default: " + toggle.def + "   # " + toggle.description + "\n";
                config.addDefault(toggle.path, toggle.def);
            }
            config.options().header(header);
            config.options().copyDefaults(true);

            version = getInt("config-version", 1);
            set("config-version", 1);

            MCPCHooks.overrideTileTicks = Setting.overrideTileTicks.getValue();
            readConfig(MCPCConfig.class, null);
        }
        catch (Exception ex)
        {
            Bukkit.getLogger().log(Level.SEVERE, "Could not load " + CONFIG_FILE, ex);
        }
    }

    static void readConfig(Class<?> clazz, Object instance)
    {
        init();
        for (Method method : clazz.getDeclaredMethods())
        {
            if (Modifier.isPrivate(method.getModifiers()))
            {
                if ((method.getParameterTypes().length == 0) && (method.getReturnType() == Void.TYPE))
                {
                    try
                    {
                        method.setAccessible(true);
                        method.invoke(instance);
                    }
                    catch (InvocationTargetException ex)
                    {
                        Throwables.propagate(ex.getCause());
                    }
                    catch (Exception ex)
                    {
                        Bukkit.getLogger().log(Level.SEVERE, "Error invoking " + method, ex);
                    }
                }
            }
        }

        try
        {
            config.save(CONFIG_FILE);
        }
        catch (IOException ex)
        {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save " + CONFIG_FILE, ex);
        }
    }

    public static void set(String path, Object val)
    {
        init();
        config.set(path, val);
    }

    public static boolean isSet(String path)
    {
        return config.isSet(path);
    }

    public static boolean getBoolean(String path, boolean def)
    {
        return getBoolean(path, def, true);
    }

    public static boolean getBoolean(String path, boolean def, boolean addDefault)
    {
        init();
        if (addDefault)
        {
            config.addDefault(path, def);
        }
        return config.getBoolean(path, def);
    }

    public static int getInt(String path, int def)
    {
        return getInt(path, def, true);
    }

    public static int getInt(String path, int def, boolean addDefault)
    {
        init();
        config.addDefault(path, def);
        return config.getInt(path, config.getInt(path));
    }

    private static <T> List getList(String path, T def)
    {
        init();
        config.addDefault(path, def);
        return config.getList(path, config.getList(path));
    }

    public static String getString(String path, String def)
    {
        return getString(path, def, true);
    }

    public static String getString(String path, String def, boolean addDefault)
    {
        init();
        if (addDefault)
        {
            config.addDefault(path, def);
        }
        return config.getString(path, def);
    }

    public static String getFakePlayer(String className, String defaultName)
    {
        return getString("fake-players." + className + ".username", defaultName);
    }
}
