package net.minecraftforge.common.loot;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.ModContainer;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class LootRegistry {

    /**
     * This is called by LootTableManager after it looks for loot tables in the world folder
     * and before it looks for loot tables in vanilla.
     * By default, loot tables are loaded from the resource path (jar) of the mod whose modid matches the resource domain of the given path.
     * If there exists an override handler, then that is used instead.
     * @param path The path of the loot table to load.
     * @param deserializer The gson that can handle Loot Tables.
     * @return A LootTable object, or null indicating that the LootTableManager should continue looking elsewhere.
     */
    public static LootTable loadModLootTable(ResourceLocation path, Gson deserializer)
    {
        String modid = path.getResourceDomain();
        InputStream input = null;

        if (LOADING_OVERRIDES.containsKey(modid))
        {
            input = LOADING_OVERRIDES.get(modid).apply(path);
        }
        else
        {
            ModContainer mod = Loader.instance().getIndexedModList().get(modid);
            if (mod != null)
            {
                input = mod.getMod().getClass().getResourceAsStream("/assets/" + path.getResourceDomain() + "/loot_tables/" + path.getResourcePath() + ".json");
            }
        }


        if (input == null)
        {
            return null;
        }

        try
        {
            return deserializer.fromJson(new InputStreamReader(new BufferedInputStream(input)), LootTable.class);
        }
        catch (JsonSyntaxException ex)
        {
            FMLLog.warning("Failed to load loot table from path %s: Malformed json", path);
            ex.printStackTrace();
            return null;
        }
        catch (JsonIOException ex)
        {
            FMLLog.warning("Failed to load loot table from path %s: IOException", path);
            ex.printStackTrace();
            return null;
        }
    }

    private static final Map<String, Function<ResourceLocation, InputStream>> LOADING_OVERRIDES = Maps.newHashMap();

    /**
     * By default, loot tables are only loaded from the mod whose mod id matches the resource domain of the path.
     * By registering an override here, a mod can handle the ResourceLocation -> InputStream process on its own.
     * This allows a mod to, for example, allow addons to register modified loot tables.
     * Call this anytime during startup.
     */
    public static void registerOverride(Function<ResourceLocation, InputStream> override)
    {
        Preconditions.checkState(!Loader.instance().hasReachedState(LoaderState.AVAILABLE),
                "Mod %s calling LootRegistry.registerOverride too late!", Loader.instance().activeModContainer().getModId());
        LOADING_OVERRIDES.put(Loader.instance().activeModContainer().getModId(), override);
    }

}
