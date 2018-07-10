/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.common.util;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IDataWalker;
import net.minecraft.util.datafix.IFixType;
import net.minecraft.util.datafix.IFixableData;

public class CompoundDataFixer extends DataFixer
{
    private final ModFixs vanilla;
    private final Map<String, ModFixs> modFixers = Maps.newHashMap();
    private final Map<IFixType, List<IDataWalker>> walkers = Maps.newHashMap();

    public CompoundDataFixer(DataFixer vanilla)
    {
        super(0);
        this.vanilla = init("minecraft", vanilla.version);
    }

    @Override
    public NBTTagCompound process(IFixType type, NBTTagCompound nbt)
    {
        final Map<String, Integer>versions = getVersions(nbt);
        final int mcversion = versions.get("minecraft") == null ? -1 : versions.get("minecraft");
        final IDataFixerData holder = new IDataFixerData()
        {
            @Override
            public NBTTagCompound process(IFixType type, NBTTagCompound nbt, int version)
            {
                for (Entry<String, ModFixs> e : modFixers.entrySet())
                {
                    // This is a potential performance hot spot. As it walks all the data for all
                    // of the fixers... But with the vanilla api there isn't a way to pass down
                    // the mod specific version numbers, so redundant.. but not hacky...
                    //Actually, this wont work as the data walkers take versions into account...
                    ModFixs fixer = e.getValue();
                    int ver = getVersion(e.getKey());
                    if (ver < fixer.version)
                    {
                       for (IFixableData fix : fixer.getFixes(type))
                       {
                           if (fix.getFixVersion() > ver)
                               nbt = fix.fixTagCompound(nbt);
                       }

                       for (IDataWalker walker : getWalkers(type))
                           nbt = walker.process(this, nbt, version); //We pass in the holder, in case a walker wants to know a mod version
                    }
                }
                return nbt;
            }

            @Override
            public int getVersion(String mod)
            {
                Integer ret = versions.get(mod);
                return ret == null ? -1 : ret;
            }
        };
        return holder.process(type, nbt, mcversion);
    }

    @Override
    @Deprecated //MODDERS DO NOT CALL DIRECTLY! Only use from DataWalker!
    public NBTTagCompound process(IFixType type, NBTTagCompound nbt, int mcversion)
    {
        if (type != FixTypes.OPTIONS) //Options are vanilla only
            throw new IllegalStateException("Do not call recursive process directly on DataFixer!");

        for (IFixableData fix : vanilla.getFixes(type))
        {
            if (fix.getFixVersion() > mcversion)
                nbt = fix.fixTagCompound(nbt);
        }
        //Options is a hack, and doesn't have any nested components
        //for (IDataWalker walker : getWalkers(type))
        //    nbt = walker.process(this, nbt, version);
        return nbt;
    }

    private List<IDataWalker> getWalkers(IFixType type)
    {
        return walkers.computeIfAbsent(type, k -> Lists.newArrayList());
    }

    @Override
    @Deprecated //Modders do not use this, this will register you as vanilla. Use the ModID version below.
    public void registerFix(IFixType type, IFixableData fixable)
    {
        vanilla.registerFix(type, fixable);
    }


    @Override
    @Deprecated //Modders do not use this, use add below, To better allow custom fix types.
    public void registerWalker(FixTypes type, IDataWalker walker)
    {
        registerVanillaWalker(type, walker);
    }

    @Override
    public void registerVanillaWalker(IFixType type, IDataWalker walker)
    {
        getWalkers(type).add(walker);
    }

    private void validateModId(String mod)
    {
        //String current = Loader.instance().activeModContainer() == null ? "minecraft" : Loader.instance().activeModContainer().getModId();
        //Test active modid?
        if (!mod.equals(mod.toLowerCase(Locale.ENGLISH)))
            throw new IllegalArgumentException("Mod ID is not lower case: " + mod);
        if (mod.length() > 64)
            throw new IllegalArgumentException("Mod ID is to long: " + mod);
    }

    /**
     * Initialize your mod specific data fixer.
     *
     * @param modid You mod id, must be lower case.
     * @param version The current data version of your mod
     */
    public ModFixs init(String modid, int version)
    {
        validateModId(modid);
        if (modFixers.containsKey(modid))
            throw new IllegalStateException("Attempted to initalize DataFixer for " + modid + " twice");
        ModFixs ret = new ModFixs(modid, version);
        modFixers.put(modid, ret);
        return ret;
    }

    private Map<String, Integer> getVersions(NBTTagCompound nbt)
    {
        Map<String, Integer> ret = Maps.newHashMap();
        ret.put("minecraft", nbt.hasKey("DataVersion", 99) ? nbt.getInteger("DataVersion") : -1);
        if (nbt.hasKey("ForgeDataVersion", 10))
        {
            NBTTagCompound sub = nbt.getCompoundTag("ForgeDataVersion");
            for (String key : sub.getKeySet())
            {
                ret.put(key, sub.hasKey(key, 99) ? sub.getInteger(key) : -1);
            }
        }
        return ret;
    }

    public void writeVersionData(NBTTagCompound nbt)
    {
        //nbt.setInteger("DataVersion", vanilla.version);
        NBTTagCompound sub = new NBTTagCompound();
        nbt.setTag("ForgeDataVersion", sub);
        for (ModFixs mod : modFixers.values())
            sub.setInteger(mod.mod, mod.version);
    }
}
