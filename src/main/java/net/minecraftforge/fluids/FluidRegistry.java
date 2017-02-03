/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.fluids;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraftforge.fml.common.LoaderState;
import org.apache.logging.log4j.Level;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.MinecraftForge;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IRegistryDelegate;

import javax.annotation.Nullable;

/**
 * Handles Fluid registrations. Fluids MUST be registered in order to function.
 */
public abstract class FluidRegistry
{
    private static boolean init = false;

    static boolean universalBucketEnabled = false;
    static Set<Fluid> bucketFluids = Sets.newHashSet();

    public static final Fluid WATER = new Fluid(new ResourceLocation("blocks/water_still"), new ResourceLocation("blocks/water_flow")) {
        @Override
        public String getLocalizedName(FluidStack fs) {
            return I18n.translateToLocal("tile.water.name");
        }
    }.setBlock(Blocks.WATER).setUnlocalizedName(Blocks.WATER.getUnlocalizedName()).setRegistryName("water");

    public static final Fluid LAVA = new Fluid(new ResourceLocation("blocks/lava_still"), new ResourceLocation("blocks/lava_flow")) {
        @Override
        public String getLocalizedName(FluidStack fs) {
            return I18n.translateToLocal("tile.lava.name");
        }
    }.setBlock(Blocks.LAVA).setLuminosity(15).setDensity(3000).setViscosity(6000).setTemperature(1300).setUnlocalizedName(Blocks.LAVA.getUnlocalizedName()).setRegistryName("lava");

    private FluidRegistry(){}

    /**
     * Enables the universal bucket in forge.
     * Has to be called before pre-initialization.
     * Actually just call it statically in your mod class.
     */
    public static void enableUniversalBucket()
    {
        if (Loader.instance().hasReachedState(LoaderState.PREINITIALIZATION))
        {
            ModContainer modContainer = Loader.instance().activeModContainer();
            String modContainerName = modContainer == null ? null : modContainer.getName();
            FMLLog.log.error("Trying to activate the universal filled bucket too late. Call it statically in your Mods class. Mod: {}", modContainerName);
        }
        else
        {
            universalBucketEnabled = true;
        }
    }

    public static boolean isUniversalBucketEnabled()
    {
        return universalBucketEnabled;
    }

    /**
     * Registers a fluid with the universal bucket.
     * This only has an effect if the universal bucket is enabled.
     * @param fluid    The fluid that the bucket shall be able to hold
     * @return True if the fluid was added successfully, false if it already was registered or couldn't be registered with the bucket.
     */
    public static boolean addBucketForFluid(Fluid fluid)
    {
        Preconditions.checkNotNull(fluid, "Fluid cannot be null");
        Preconditions.checkArgument(ForgeRegistries.FLUIDS.containsValue(fluid), "Cannot register bucket for unregistered Fluid %s", fluid);
        return bucketFluids.add(fluid);
    }

    /**
     * All fluids registered with the universal bucket
     * @return An immutable set containing the fluids
     */
    public static Set<Fluid> getBucketFluids()
    {
        return ImmutableSet.copyOf(bucketFluids);
    }

<<<<<<< HEAD

    public static Fluid lookupFluidForBlock(Block block)
    {
        if (fluidBlocks == null)
        {
            BiMap<Block, Fluid> tmp = HashBiMap.create();
            for (Fluid fluid : fluids.values())
            {
                if (fluid.canBePlacedInWorld() && fluid.getBlock() != null)
                {
                    tmp.put(fluid.getBlock(), fluid);
                }
            }
            fluidBlocks = tmp;
        }
        return fluidBlocks.get(block);
    }

    public static class FluidRegisterEvent extends Event
    {
        private final String fluidName;
        private final int fluidID;

        public FluidRegisterEvent(String fluidName, int fluidID)
        {
            this.fluidName = fluidName;
            this.fluidID = fluidID;
        }

        public String getFluidName()
        {
            return fluidName;
        }

        public int getFluidID()
        {
            return fluidID;
        }
    }

    public static int getMaxID()
    {
        return maxID;
    }

    public static String getDefaultFluidName(Fluid key)
    {
        String name = masterFluidReference.inverse().get(key);
        if (Strings.isNullOrEmpty(name)) {
            FMLLog.log.error("The fluid registry is corrupted. A fluid {} {} is not properly registered. The mod that registered this is broken", key.getClass().getName(), key.getName());
            throw new IllegalStateException("The fluid registry is corrupted");
        }
        return name;
    }

    public static void loadFluidDefaults(NBTTagCompound tag)
    {
        Set<String> defaults = Sets.newHashSet();
        if (tag.hasKey("DefaultFluidList",9))
        {
            FMLLog.log.debug("Loading persistent fluid defaults from world");
            NBTTagList tl = tag.getTagList("DefaultFluidList", 8);
            for (int i = 0; i < tl.tagCount(); i++)
            {
                defaults.add(tl.getStringTagAt(i));
            }
        }
        else
        {
            FMLLog.log.debug("World is missing persistent fluid defaults - using local defaults");
        }
        loadFluidDefaults(HashBiMap.create(fluidIDs), defaults);
    }

    public static void writeDefaultFluidList(NBTTagCompound forgeData)
    {
        NBTTagList tagList = new NBTTagList();

        for (Entry<String, Fluid> def : fluids.entrySet())
        {
            tagList.appendTag(new NBTTagString(getDefaultFluidName(def.getValue())));
        }

        forgeData.setTag("DefaultFluidList", tagList);
    }

    public static void validateFluidRegistry()
    {
        Set<Fluid> illegalFluids = Sets.newHashSet();
        for (Fluid f : fluids.values())
        {
            if (!masterFluidReference.containsValue(f))
            {
                illegalFluids.add(f);
            }
        }

        if (!illegalFluids.isEmpty())
        {
            FMLLog.log.fatal("The fluid registry is corrupted. Something has inserted a fluid without registering it");
            FMLLog.log.fatal("There is {} unregistered fluids", illegalFluids.size());
            for (Fluid f: illegalFluids)
            {
                FMLLog.log.fatal("  Fluid name : {}, type: {}", f.getName(), f.getClass().getName());
            }
            FMLLog.log.fatal("The mods that own these fluids need to register them properly");
            throw new IllegalStateException("The fluid map contains fluids unknown to the master fluid registry");
        }
    }

    static IRegistryDelegate<Fluid> makeDelegate(Fluid fl)
    {
        return delegates.get(fl);
    }


    private static class FluidDelegate implements IRegistryDelegate<Fluid>
    {
        private String name;
        private Fluid fluid;

        FluidDelegate(Fluid fluid, String name)
        {
            this.fluid = fluid;
            this.name = name;
        }

        @Override
        public Fluid get()
        {
            return fluid;
        }

        @Override
        public ResourceLocation name() {
            return new ResourceLocation(name);
        }

        @Override
        public Class<Fluid> type()
        {
            return Fluid.class;
        }

        void rebind()
=======
    public static void registerFluids()
    {
        if (!init)
>>>>>>> Completely rewrite how fluids are registered
        {
            GameData.getFluidRegistry().registerAll(WATER, LAVA);
            FluidDictionary.registerFluid(WATER, "water");
            FluidDictionary.registerFluid(LAVA, "lava");
            init = true;
        }
    }
}
