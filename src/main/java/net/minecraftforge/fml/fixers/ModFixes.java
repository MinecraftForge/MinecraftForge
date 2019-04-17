package net.minecraftforge.fml.fixers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraftforge.common.util.Constants;

public class ModFixes extends DataFixerBuilder
{
    private static Map<String, ModFixes> builders = Maps.newHashMap();

    /**
     * Create and return a builder instance to add fixes to.
     * 
     * @param modid - The modid of the mod.
     * @param version - The data version of the mod. Any {@link com.mojang.datafixers.schemas.Schema} added with a lower version will execute.
     * @return The builder for the mod.
     */
    public static ModFixes init(String modid, int version)
    {
        if (builders.containsKey(modid))
        {
            return builders.get(modid);
        }
        ModFixes builder = new ModFixes(modid, version * 10);
        builders.put(modid, builder);
        return builder;
    }

    public static ImmutableMap<String, ModFixes> getBuilders()
    {
        return ImmutableMap.copyOf(builders);
    }

    public static Map<DataFixer, Pair<Integer, Integer>> getFixVersions(NBTTagCompound nbttagcompound, int defaultMC, int defaultForge)
    {
        Map<DataFixer, Pair<Integer, Integer>> versions = getFixVersionsNoVanilla(nbttagcompound, defaultForge);
        int i = nbttagcompound.contains("DataVersion", Constants.NBT.TAG_ANY_NUMERIC) ? nbttagcompound.getInt("DataVersion") : defaultMC;
        versions.put(DataFixesManager.getDataFixer(), Pair.of(i, DataFixesManager.FIXER_VERSION));
        return versions;
    }

    public static Map<DataFixer, Pair<Integer, Integer>> getFixVersionsNoVanilla(NBTTagCompound nbttagcompound, int defaultForge)
    {
        Map<DataFixer, Pair<Integer, Integer>> versions = new HashMap<DataFixer, Pair<Integer, Integer>>();
        if (nbttagcompound.contains("ForgeDataVersions", Constants.NBT.TAG_COMPOUND))
        {
            NBTTagCompound vers = nbttagcompound.getCompound("ForgeDataVersions");
            for (String modid : ModFixes.getBuilders().keySet())
            {
                ModFixes fixes = builders.get(modid);
                int newVersion = fixes.dataVersion;
                int version = defaultForge == -2492 ? newVersion : defaultForge;
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

    public static Map<DataFixer, Pair<Integer, Integer>> getFixVersions(NBTTagCompound nbttagcompound, boolean useLatestOtherwiseN1)
    {
        return getFixVersions(nbttagcompound, (useLatestOtherwiseN1 ? DataFixesManager.FIXER_VERSION : -1), (useLatestOtherwiseN1 ? -2492 : -1));
    }

    public static Map<DataFixer, Pair<Integer, Integer>> getFixVersionsNoVanilla(NBTTagCompound nbttagcompound, boolean useLatestOtherwiseN1)
    {
        return getFixVersionsNoVanilla(nbttagcompound, (useLatestOtherwiseN1 ? -2492 : -1));
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
    
    public void addSchema(final Schema schema) {
        super.addSchema(schema);
        fixer = null;
    }

    public void addFixer(final DataFix fix) {
        super.addFixer(fix);
        fixer = null;
    }

    public DataFixer getBuiltFixer()
    {
        if (fixer != null)
        {
            return fixer;
        }
        return (fixer = this.build(ForkJoinPool.commonPool()));
    }

}
