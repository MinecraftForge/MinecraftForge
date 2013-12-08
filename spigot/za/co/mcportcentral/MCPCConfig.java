package za.co.mcportcentral;


import com.google.common.base.Throwables;

import gnu.trove.set.hash.THashSet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import net.minecraft.server.MinecraftServer;

import org.apache.commons.lang.BooleanUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.YamlConfiguration;

public class MCPCConfig
{

    private static final File CONFIG_FILE = new File( "mcpc.yml" );
    private static final String HEADER = "This is the main configuration file for MCPC+.\n"
            + "\n"
            + "If you need help with the configuration or have any questions related to MCPC,\n"
            + "join us at the IRC or drop by our forums and leave a post.\n"
            + "\n"
            + "IRC: #mcportcentral @ irc.esper.net ( http://webchat.esper.net/?channel=mcportcentral )\n"
            + "Forums: http://http://www.mcportcentral.co.za/\n";
    /*========================================================================*/
    static YamlConfiguration config;
    static int version;
    static Map<String, Command> commands;
    // Some mods such as Twilight Forest listen for specific events as their WorldProvider loads to hotload its dimension. This prevents this from happening so MV can create worlds using the same provider without issue.
    public static boolean craftWorldLoading = false;

    public static final Map<String, Setting> settings = new HashMap<String, Setting>();
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
        public static final BoolSetting dumpChunksOnDeadlock = new BoolSetting("logging.dump-chunks-on-deadlock", true, "Dump chunks in the event of a deadlock (helps to debug the deadlock)");
        public static final BoolSetting dumpHeapOnDeadlock = new BoolSetting("logging.dump-heap-on-deadlock", false, "Dump the heap in the event of a deadlock (helps to debug the deadlock)");
        public static final IntSetting largeCollisionLogSize = new IntSetting("logging.collision-warn-size", 200, "Number of colliding entities in one spot before logging a warning. Set to 0 to disable");
        
        // Chunk loading options
        public static final BoolSetting loadChunkOnRequest = new BoolSetting("settings.load-chunk-on-request", false, "Forces Chunk Loading on 'Provide' requests (speedup for mods that don't check if a chunk is loaded");
        public static final BoolSetting loadChunkOnForgeTick = new BoolSetting("settings.load-chunk-on-forge-tick", false, "Forces Chunk Loading during Forge Server Tick events");
        
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
            private IntSetting(String path, Integer def, String description)
            {
                super(path, def, description);
            }
            
            public Integer getValue()
            {
                config.addDefault(path, def);
                return config.getInt(path);
            }
            
            public void setValue(String value)
            {
                config.set(path, org.apache.commons.lang.math.NumberUtils.toInt(value, def));
            }
        }
        
        public static class BoolSetting extends Setting<Boolean>
        {
            private BoolSetting(String path, Boolean def, String description)
            {
                super(path, def, description);
            }
            
            public Boolean getValue()
            {
                config.addDefault(path, def);
                return config.getBoolean(path);
            }
            public void setValue(String value)
            {
                Boolean val = BooleanUtils.toBooleanObject(value);
                val = val == null ? def : val;
                config.set(path, val);
            }
        }
    }

    /*========================================================================*/

    
    public static void init()
    {
        if (config == null)
        {
            commands = new HashMap<String, Command>();
            commands.put( "mcpc", new MCPCCommand());
            
            config = YamlConfiguration.loadConfiguration( CONFIG_FILE );
            String header = HEADER + "\n";
            for(Setting toggle : settings.values())
            {
                header += "Setting: " + toggle.path + " Default: " + toggle.def + "   # " + toggle.description + "\n";
                config.addDefault(toggle.path, toggle.def);
            }
            config.options().header( header );
            config.options().copyDefaults( true );

            version = getInt( "config-version", 1 );
            set( "config-version", 1 );
            
            readConfig( MCPCConfig.class, null );
        }
    }
    
    public static void registerCommands()
    {
        for ( Map.Entry<String, Command> entry : commands.entrySet() )
        {
            MinecraftServer.getServer().server.getCommandMap().register( entry.getKey(), "mcpc", entry.getValue() );
        }
    }

    public static void save()
    {
        try
        {
            config.save( CONFIG_FILE );
        } catch ( IOException ex )
        {
            Bukkit.getLogger().log( Level.SEVERE, "Could not save " + CONFIG_FILE, ex );
        }
    }

    static void readConfig(Class<?> clazz, Object instance)
    {
        init();
        for ( Method method : clazz.getDeclaredMethods() )
        {
            if ( Modifier.isPrivate( method.getModifiers() ) )
            {
                if ( method.getParameterTypes().length == 0 && method.getReturnType() == Void.TYPE )
                {
                    try
                    {
                        method.setAccessible( true );
                        method.invoke( instance );
                    } catch ( InvocationTargetException ex )
                    {
                        Throwables.propagate( ex.getCause() );
                    } catch ( Exception ex )
                    {
                        Bukkit.getLogger().log( Level.SEVERE, "Error invoking " + method, ex );
                    }
                }
            }
        }

        try
        {
            config.save( CONFIG_FILE );
        } catch ( IOException ex )
        {
            Bukkit.getLogger().log( Level.SEVERE, "Could not save " + CONFIG_FILE, ex );
        }
    }

    public static void set(String path, Object val)
    {
        init();
        config.set( path, val );
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
            config.addDefault( path, def );
        }
        return config.getBoolean( path, def );
    }
    
    public static int getInt(String path, int def)
    {
        return getInt(path, def, true);
    }

    public static int getInt(String path, int def, boolean addDefault)
    {
        init();
        config.addDefault( path, def );
        return config.getInt( path, config.getInt( path ) );
    }

    private static <T> List getList(String path, T def)
    {
        init();
        config.addDefault( path, def );
        return config.getList( path, config.getList( path ) );
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
            config.addDefault( path, def );
        }
        return config.getString( path, def );
    }

    public static String getFakePlayer(String className, String defaultName)
    {
    	return getString("fake-players." + className + ".username", defaultName);
    }
}
