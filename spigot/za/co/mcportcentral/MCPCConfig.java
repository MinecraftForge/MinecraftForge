package za.co.mcportcentral;


import com.google.common.base.Throwables;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.command.TicksPerSecondCommand;
import org.spigotmc.Metrics;

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

    public enum Toggle
    {
        // Logging options
        dumpMaterials("settings.dump-materials", false, "Dumps all materials with their corresponding id's"),
        disableWarnings("logging.disabled-warnings", false, "Disable warning messages to server admins"),
        worldLeakDebug("logging.world-leak-debug", false, "Log worlds that appear to be leaking (buggy)"),
        connectionLogging("logging.connection", false, "Log connections"),
        tileEntityPlaceLogging("logging.warn-place-no-tileentity", true, "Warn when a mod requests tile entity from a block that doesn't support one"),
        chunkLoadLogging("logging.chunk-load", false, "Log when chunks are loaded (dev)"),
        chunkUnloadLogging("logging.chunk-unload", false, "Log when chunks are unloaded (dev)"),
        entitySpawnLogging("logging.entity-spawn", false, "Log when living entities are spawned (dev)"),
        entityDespawnLogging("logging.entity-despawn", false, "Log when living entities are despawned (dev)"),
        entityDeathLogging("logging.entity-death", false, "Log when an entity is destroyed (dev)"),
        logWithStackTraces("logging.detailed-logging", false, "Add stack traces to dev logging"),
        
        // Chunk loading options
        loadChunkOnRequest("settings.load-chunk-on-request", false, "Forces Chunk Loading on 'Provide' requests (speedup for mods that don't check if a chunk is loaded"),
        loadChunkOnForgeTick("settings.load-chunk-on-forge-tick", false, "Forces Chunk Loading during Forge Server Tick events"),
        
        // Server options
        infiniteWaterSource("world-settings.default.infinite-water-source", true, "Vanilla water source behavior - is infinite"),
        flowingLavaDecay("world-settings.default.flowing-lava-decay", false, "Lava behaves like vanilla water when source block is removed"),
        fakePlayerLogin("fake-players.do-login", false, "Raise login events for fake players"),
        
        // Plug-in options
        remapPluginFile("plugin-settings.default.remap-plugin-file", false, "Remap the plugin file (dev)");
        
        public final String path;
        public final boolean def;
        public final String description;
        private Toggle(String path, boolean def, String description)
        {
            this.path = path;
            this.def = def;
            this.description = description;
        }
        
        public boolean value()
        {
            return getBoolean(path, def);
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
            for(Toggle toggle : Toggle.class.getEnumConstants())
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

    private static void set(String path, Object val)
    {
        config.set( path, val );
    }

    public static boolean getToggle(Toggle toggle)
    {
        return getBoolean(toggle.path, toggle.def);
    }
    
    public static void setToggle(Toggle toggle, boolean value)
    {
        config.set(toggle.path, value);
    }
    
    public static boolean getBoolean(String path, boolean def)
    {
        return getBoolean(path, def, true);
    }

    public static boolean getBoolean(String path, boolean def, boolean addDefault)
    {
        if (addDefault)
            config.addDefault( path, def );
        return config.getBoolean( path, def );
    }

    private static int getInt(String path, int def)
    {
        config.addDefault( path, def );
        return config.getInt( path, config.getInt( path ) );
    }

    private static <T> List getList(String path, T def)
    {
        config.addDefault( path, def );
        return (List<T>) config.getList( path, config.getList( path ) );
    }

    public static String getString(String path, String def)
    {
        return getString(path, def, true);
    }

    public static String getString(String path, String def, boolean addDefault)
    {
        if (addDefault)
            config.addDefault( path, def );
        return config.getString( path, def );
    }

    public static String getFakePlayer(String className, String defaultName)
    {
    	return getString("fake-players." + className + ".username", defaultName);
    }
}
