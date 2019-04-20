package net.minecraftforge.fml.fixers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.util.Pair;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraftforge.common.util.Constants;

public class ModFixes extends DataFixerBuilder
{
    private static Map<String, ModFixes> builders = Maps.newHashMap();

    /**
     * Create and return a builder instance to add fixes to. This will only return an instance ONCE, so store the returned builder!
     * @param modid - The modid of the mod.
     * @param version - The data version of the mod. Any {@link com.mojang.datafixers.schemas.Schema} added with a lower version will execute. 
     * This is based off vanilla versions, which are multiplied by ten. The same happens for Schemas you add.
     * @return The builder for the mod.
     */
    public static ModFixes init(String modid, int version)
    {
        if (builders.containsKey(modid))
        {
            return null;
        }
        ModFixes builder = new ModFixes(modid, version * 10);
        builders.put(modid, builder);
        return builder;
    }
    
    /*
     * Modders: Do not use this! For forge use only.
     */
    public static ImmutableMap<String, ModFixes> getBuilders()
    {
        return ImmutableMap.copyOf(builders);
    }
    
    /**
     * Returns a list of built datafixers with their dataversions, including vanilla.
     * @param nbttagcompound - Compound to retrieve DataVersions from
     * @param defaultMC - If no key is found for MC, use this number as the dataversion.
     * @param defaultForge - If no key is found for a mod, use this number as the dataversion.
     * @param useLatestInstead - If this is true, it will override defaultForge and instead use that mod's current dataversion.
     * @return A Map of built datafixers to a pair of savedOrDefaultVersion -> currentVersion
     */
    public static Map<DataFixer, Pair<Integer, Integer>> getFixVersions(NBTTagCompound nbttagcompound, int defaultMC, int defaultForge, boolean useLatestInstead)
    {
        Map<DataFixer, Pair<Integer, Integer>> versions = getFixVersionsNoVanilla(nbttagcompound, defaultForge, useLatestInstead);
        int i = nbttagcompound.contains("DataVersion", Constants.NBT.TAG_ANY_NUMERIC) ? nbttagcompound.getInt("DataVersion") : defaultMC;
        versions.put(DataFixesManager.getDataFixer(), Pair.of(i, DataFixesManager.FIXER_VERSION));
        return versions;
    }
    
    /**
     * Returns a list of built datafixers with their dataversions, excluding vanilla.
     * @param nbttagcompound - Compound to retrieve DataVersions from
     * @param defaultForge - If no key is found for a mod, use this number as the dataversion.
     * @param useLatestInstead - If this is true, it will override defaultForge and instead use that mod's current dataversion.
     * @return A Map of built datafixers to a pair of savedOrDefaultVersion -> currentVersion
     */
    public static Map<DataFixer, Pair<Integer, Integer>> getFixVersionsNoVanilla(NBTTagCompound nbttagcompound, int defaultForge, boolean useLatestInstead)
    {
        Map<DataFixer, Pair<Integer, Integer>> versions = new HashMap<DataFixer, Pair<Integer, Integer>>();
        if (nbttagcompound.contains("ForgeDataVersions", Constants.NBT.TAG_COMPOUND))
        {
            NBTTagCompound vers = nbttagcompound.getCompound("ForgeDataVersion");
            for (String modid : ModFixes.getBuilders().keySet())
            {
                ModFixes fixes = builders.get(modid);
                int newVersion = fixes.dataVersion;
                int version = useLatestInstead ? newVersion : defaultForge;
                if (vers.contains(modid, Constants.NBT.TAG_ANY_NUMERIC))
                {
                    version = vers.getInt(modid);
                }
                DataFixer fixer = fixes.getBuiltFixer();
                versions.put(fixer, Pair.of(version, newVersion));
            }
        }
        return versions;
    }
    
    /**
     * Returns a list of built datafixers with their dataversions, including vanilla.
     * @param nbttagcompound - Compound to retrieve DataVersions from
     * @param useLatestOtherwiseN1 - If this is true, it will use the latest version for a DataFixer. If false, it will use -1.
     * @return A Map of built datafixers to a pair of savedOrDefaultVersion -> currentVersion
     */
    public static Map<DataFixer, Pair<Integer, Integer>> getFixVersions(NBTTagCompound nbttagcompound, boolean useLatestOtherwiseN1)
    {
        return getFixVersions(nbttagcompound, (useLatestOtherwiseN1 ? DataFixesManager.FIXER_VERSION : -1), -1, useLatestOtherwiseN1);
    }
    
    /**
     * Returns a list of built datafixers with their dataversions, excluding vanilla.
     * @param nbttagcompound - Compound to retrieve DataVersions from
     * @param useLatestOtherwiseN1 - If this is true, it will use the latest version for a DataFixer. If false, it will use -1.
     * @return A Map of built datafixers to a pair of savedOrDefaultVersion -> currentVersion
     */
    public static Map<DataFixer, Pair<Integer, Integer>> getFixVersionsNoVanilla(NBTTagCompound nbttagcompound, boolean useLatestOtherwiseN1)
    {
        return getFixVersionsNoVanilla(nbttagcompound, -1, useLatestOtherwiseN1);
    }

    /*
     * ######################################### Members #########################################
     */

    public final String mod;
    public final int dataVersion;
    private DataFixer fixer;

    private ModFixes(String modid, int dataVersion)
    {
        super(dataVersion);
        this.dataVersion = dataVersion;
        this.mod = modid;
    }

    public String getModId()
    {
        return mod;
    }
    
    /**
     * Use this instead of {@link DataFixerBuilder#build(java.util.concurrent.Executor)}
     * This will only build on this first run, as mod fixers cannot be rebuilt.
     */
    public DataFixer getBuiltFixer()
    {
        if (fixer != null)
        {
            return fixer;
        }
        return (fixer = this.build(ForkJoinPool.commonPool()));
    }

}
