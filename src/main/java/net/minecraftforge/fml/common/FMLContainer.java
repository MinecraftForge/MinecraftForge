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

package net.minecraftforge.fml.common;

import java.io.File;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Map;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.client.FMLFileResourcePack;
import net.minecraftforge.fml.client.FMLFolderResourcePack;
import net.minecraftforge.fml.common.asm.FMLSanityChecker;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.RegistryManager;

import org.apache.logging.log4j.LogManager;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

/**
 * @author cpw
 *
 */
public final class FMLContainer extends DummyModContainer implements WorldAccessContainer
{
    private static final Logger modTrackerLogger =  LogManager.getLogger("FML.ModTracker");

    public FMLContainer()
    {
        super(new ModMetadata());
        ModMetadata meta = getMetadata();
        meta.modId="FML";
        meta.name="Forge Mod Loader";
        meta.version=Loader.instance().getFMLVersionString();
        meta.credits="Made possible with help from many people";
        meta.authorList=Arrays.asList("cpw", "LexManos", "Player");
        meta.description="The Forge Mod Loader provides the ability for systems to load mods " +
                    "from the file system. It also provides key capabilities for mods to be able " +
                    "to cooperate and provide a good modding environment. ";
        meta.url="https://github.com/MinecraftForge/FML/wiki";
        meta.screenshots=new String[0];
        meta.logoFile="";
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller)
    {
        bus.register(this);
        return true;
    }

    @Subscribe
    public void modConstruction(FMLConstructionEvent evt)
    {
        NetworkRegistry.INSTANCE.register(this, this.getClass(), null, evt.getASMHarvestedData());
        FMLNetworkHandler.registerChannel(this, evt.getSide());
    }

    @Subscribe
    public void modPreinitialization(FMLPreInitializationEvent evt)
    {
        // Initialize all Forge/Vanilla registries {invoke the static init)
        if (ForgeRegistries.ITEMS == null)
            throw new RuntimeException("Something horrible went wrong in init, ForgeRegistres didn't create...");
    }

    @NetworkCheckHandler
    public boolean checkModLists(Map<String,String> modList, Side side)
    {
        return Loader.instance().checkRemoteModList(modList,side);
    }
    @Override
    public NBTTagCompound getDataForWriting(SaveHandler handler, WorldInfo info)
    {
        NBTTagCompound fmlData = new NBTTagCompound();
        NBTTagList modList = new NBTTagList();
        for (ModContainer mc : Loader.instance().getActiveModList())
        {
            NBTTagCompound mod = new NBTTagCompound();
            mod.setString("ModId", mc.getModId());
            mod.setString("ModVersion", mc.getVersion());
            modList.appendTag(mod);
        }
        fmlData.setTag("ModList", modList);

        NBTTagCompound registries = new NBTTagCompound();
        fmlData.setTag("Registries", registries);
        FMLLog.log.debug("Gathering id map for writing to world save {}", info.getWorldName());

        for (Map.Entry<ResourceLocation, ForgeRegistry.Snapshot> e : RegistryManager.ACTIVE.takeSnapshot(true).entrySet())
        {
            registries.setTag(e.getKey().toString(), e.getValue().write());
        }
        return fmlData;
    }

    @Override
    public void readData(SaveHandler handler, WorldInfo info, Map<String, NBTBase> propertyMap, NBTTagCompound tag)
    {
        if (tag.hasKey("ModList"))
        {
            NBTTagList modList = tag.getTagList("ModList", (byte)10);
            for (int i = 0; i < modList.tagCount(); i++)
            {
                NBTTagCompound mod = modList.getCompoundTagAt(i);
                String modId = mod.getString("ModId");
                String modVersion = mod.getString("ModVersion");
                ModContainer container = Loader.instance().getIndexedModList().get(modId);
                if (container == null)
                {
                    modTrackerLogger.error("This world was saved with mod {} which appears to be missing, things may not work well", modId);
                    continue;
                }
                if (!modVersion.equals(container.getVersion()))
                {
                    modTrackerLogger.info("This world was saved with mod {} version {} and it is now at version {}, things may not work well", modId, modVersion, container.getVersion());
                }
            }
        }

        Multimap<ResourceLocation, ResourceLocation> failedElements = null;

        if (tag.hasKey("ModItemData") || tag.hasKey("ItemData")) // Pre 1.7
        {
            StartupQuery.notify("This save predates 1.7.10, it can no longer be loaded here. Please load in 1.7.10 or 1.8 first");
            StartupQuery.abort();
        }
        else if (tag.hasKey("Registries")) // 1.8, genericed out the 'registries' list
        {
            Map<ResourceLocation, ForgeRegistry.Snapshot> snapshot = Maps.newHashMap();
            NBTTagCompound regs = tag.getCompoundTag("Registries");
            for (String key : regs.getKeySet())
            {
                snapshot.put(new ResourceLocation(key), ForgeRegistry.Snapshot.read(regs.getCompoundTag(key)));
            }
            failedElements = GameData.injectSnapshot(snapshot, true, true);
        }

        if (failedElements != null && !failedElements.isEmpty())
        {
            StringBuilder buf = new StringBuilder();
            buf.append("Forge Mod Loader could not load this save.\n\n")
               .append("There are ").append(failedElements.size()).append(" unassigned registry entries in this save.\n")
               .append("You will not be able to load until they are present again.\n\n");

            failedElements.asMap().forEach((name, entries) ->
            {
                buf.append("Missing ").append(name).append(":\n");
                entries.forEach(rl -> buf.append("    ").append(rl).append("\n"));
            });

            StartupQuery.notify(buf.toString());
            StartupQuery.abort();
        }
    }


    @Override
    @Nullable
    public Certificate getSigningCertificate()
    {
        Certificate[] certificates = getClass().getProtectionDomain().getCodeSource().getCertificates();
        return certificates != null ? certificates[0] : null;
    }

    @Override
    public File getSource()
    {
        return FMLSanityChecker.fmlLocation;
    }

    @Override
    public Class<?> getCustomResourcePackClass()
    {
        return getSource().isDirectory() ? FMLFolderResourcePack.class : FMLFileResourcePack.class;
    }

    @Override
    public String getGuiClassName()
    {
        return "net.minecraftforge.fml.client.FMLConfigGuiFactory";
    }

    @Override
    public Object getMod()
    {
        return this;
    }
}
