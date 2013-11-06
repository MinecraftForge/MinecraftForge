package za.co.mcportcentral;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

public class MCPCWorldConfig
{
    private final String worldName;
    private final YamlConfiguration config;
    private boolean verbose;

    public MCPCWorldConfig(String worldName)
    {
        this.worldName = worldName.toLowerCase();
        this.config = MCPCConfig.config;
        if (worldName.toLowerCase().contains("dummy")) return;
        try {
            init();
        } catch (Throwable t) {
            log( "Something bad happened while trying init the mcpc config for [" + worldName + "]");
            t.printStackTrace();
        }
    }

    public void init()
    {
        MCPCConfig.readConfig( MCPCWorldConfig.class, this );
    }

    public void save()
    {
        MCPCConfig.save();
    }

    private void log(String s)
    {
        if ( verbose )
        {
            Bukkit.getLogger().info( s );
        }
    }

    public void set(String path, Object val)
    {
        config.set( path, val );
    }

    public boolean isBoolean(String path)
    {
        return config.isBoolean(path);
    }

    public boolean getBoolean(String path, boolean def)
    {
        config.addDefault( "world-settings.default." + path, def );
        return config.getBoolean( "world-settings." + worldName + "." + path, config.getBoolean( "world-settings.default." + path ) );
    }

    private double getDouble(String path, double def)
    {
        config.addDefault( "world-settings.default." + path, def );
        return config.getDouble( "world-settings." + worldName + "." + path, config.getDouble( "world-settings.default." + path ) );
    }

    private int getInt(String path, int def)
    {
        config.addDefault( "world-settings.default." + path, def );
        return config.getInt( "world-settings." + worldName + "." + path, config.getInt( "world-settings.default." + path ) );
    }

    private <T> List getList(String path, T def)
    {
        config.addDefault( "world-settings.default." + path, def );
        return (List<T>) config.getList( "world-settings." + worldName + "." + path, config.getList( "world-settings.default." + path ) );
    }

    private String getString(String path, String def)
    {
        config.addDefault( "world-settings.default." + path, def );
        return config.getString( "world-settings." + worldName + "." + path, config.getString( "world-settings.default." + path ) );
    }
}
