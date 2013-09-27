package org.spigotmc;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

public class SpigotWorldConfig
{

    private final String worldName;
    private final YamlConfiguration config;
    private boolean verbose;

    public SpigotWorldConfig(String worldName)
    {
        this.worldName = worldName;
        this.config = SpigotConfig.config;
        init();
    }

    public void init()
    {
        this.verbose = getBoolean( "verbose", true );

        log( "-------- World Settings For [" + worldName + "] --------" );
        SpigotConfig.readConfig( SpigotWorldConfig.class, this );
    }

    private void log(String s)
    {
        if ( verbose )
        {
            Bukkit.getLogger().info( s );
        }
    }

    private void set(String path, Object val)
    {
        config.set( "world-settings.default." + path, val );
    }

    private boolean getBoolean(String path, boolean def)
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

    public int chunksPerTick;
    private void chunksPerTick()
    {
        chunksPerTick = getInt( "chunks-per-tick", 650 );
        log( "Chunks to Grow per Tick: " + chunksPerTick );
    }

    // Crop growth rates
    public int cactusModifier;
    public int caneModifier;
    public int melonModifier;
    public int mushroomModifier;
    public int pumpkinModifier;
    public int saplingModifier;
    public int wheatModifier;
    private void growthModifiers()
    {
        cactusModifier = getInt( "growth.cactus-modifier", 100 );
        log( "Cactus Growth Modifier: " + cactusModifier + "%" );

        caneModifier = getInt( "growth.cane-modifier", 100 );
        log( "Cane Growth Modifier: " + caneModifier + "%" );

        melonModifier = getInt( "growth.melon-modifier", 100 );
        log( "Melon Growth Modifier: " + melonModifier + "%" );

        mushroomModifier = getInt( "growth.mushroom-modifier", 100 );
        log( "Mushroom Growth Modifier: " + mushroomModifier + "%" );

        pumpkinModifier = getInt( "growth.pumpkin-modifier", 100 );
        log( "Pumpkin Growth Modifier: " + pumpkinModifier + "%" );

        saplingModifier = getInt( "growth.sapling-modifier", 100 );
        log( "Sapling Growth Modifier: " + saplingModifier + "%" );

        wheatModifier = getInt( "growth.wheat-modifier", 100 );
        log( "Wheat Growth Modifier: " + wheatModifier + "%" );
    }

    public double itemMerge;
    private void itemMerge()
    {
        itemMerge = getDouble("merge-radius.item", 2.5 );
        log( "Item Merge Radius: " + itemMerge );
    }

    public double expMerge;
    private void expMerge()
    {
        expMerge = getDouble("merge-radius.exp", 3.0 );
        log( "Experience Merge Radius: " + expMerge );
    }

    public int viewDistance;
    private void viewDistance()
    {
        viewDistance = getInt( "view-distance", Bukkit.getViewDistance() );
        log( "View Distance: " + viewDistance );
    }

    public boolean antiXray = true;
    public int engineMode = 1;
    public List<Integer> blocks = Arrays.asList( new Integer[]
    {
        1, 5, 14, 15, 16, 21, 48, 49, 54, 56, 73, 74, 82, 129, 130
    } );
    public AntiXray antiXrayInstance;
    private void antiXray()
    {
        antiXray = getBoolean( "anti-xray.enabled", antiXray );
        log( "Anti X-Ray: " + antiXray );

        engineMode = getInt( "anti-xray.engine-mode", engineMode );
        log( "\tEngine Mode: " + engineMode );

        if ( SpigotConfig.version < 3 )
        {
            set( "anti-xray.blocks", blocks );
        }
        blocks = getList( "anti-xray.blocks", blocks );
        log( "\tBlocks: " + blocks );

        antiXrayInstance = new AntiXray( this );
    }

    public byte mobSpawnRange;
    private void mobSpawnRange()
    {
        mobSpawnRange = (byte) getInt( "mob-spawn-range", 4 );
        log( "Mob Spawn Range: " + mobSpawnRange );
    }

    public int animalActivationRange = 32;
    public int monsterActivationRange = 32;
    public int miscActivationRange = 16;
    private void activationRange()
    {
        animalActivationRange = getInt( "entity-activation-range.animals", animalActivationRange );
        monsterActivationRange = getInt( "entity-activation-range.monsters", monsterActivationRange );
        miscActivationRange = getInt( "entity-activation-range.misc", miscActivationRange );
        log( "Entity Activation Range: An " + animalActivationRange + " / Mo " + monsterActivationRange + " / Mi " + miscActivationRange );
    }

    public int playerTrackingRange = 48;
    public int animalTrackingRange = 48;
    public int monsterTrackingRange = 48;
    public int miscTrackingRange = 32;
    public int maxTrackingRange = 64;
    private void trackingRange()
    {
        playerTrackingRange = getInt( "entity-tracking-range.players", playerTrackingRange );
        animalTrackingRange = getInt( "entity-tracking-range.animals", animalTrackingRange );
        monsterTrackingRange = getInt( "entity-tracking-range.monsters", monsterTrackingRange );
        miscTrackingRange = getInt( "entity-tracking-range.misc", miscTrackingRange );
        maxTrackingRange = getInt( "entity-tracking-range.other", maxTrackingRange );
        log( "Entity Tracking Range: Pl " + playerTrackingRange + " / An " + animalTrackingRange + " / Mo " + monsterTrackingRange + " / Mi " + miscTrackingRange + " / Other " + maxTrackingRange );
    }

    public int hopperTransfer = 8;
    public int hopperCheck = 8;
    private void hoppers()
    {
        // Set the tick delay between hopper item movements
        hopperTransfer = getInt( "ticks-per.hopper-transfer", hopperTransfer );
        // Set the tick delay between checking for items after the associated
        // container is empty. Default to the hopperTransfer value to prevent
        // hopper sorting machines from becoming out of sync.
        hopperCheck = getInt( "ticks-per.hopper-check", hopperTransfer );
        log( "Hopper Transfer: " + hopperTransfer + " Hopper Check: " + hopperCheck );
    }

    public boolean randomLightUpdates;
    private void lightUpdates()
    {
        randomLightUpdates = getBoolean( "random-light-updates", false );
        log( "Random Lighting Updates: " + randomLightUpdates );
    }
}
