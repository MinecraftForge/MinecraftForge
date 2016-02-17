/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
 */

package net.minecraftforge.fml.common;

import java.io.File;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.PersistentRegistryManager;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.relauncher.Side;

import org.apache.logging.log4j.Level;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

/**
 * @author cpw
 *
 */
public class FMLContainer extends DummyModContainer implements WorldAccessContainer
{
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
        // Initialize the villager registry
        VillagerRegistry.instance();
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
        FMLLog.fine("Gathering id map for writing to world save %s", info.getWorldName());
        PersistentRegistryManager.GameDataSnapshot dataSnapshot = PersistentRegistryManager.takeSnapshot();

        for (Map.Entry<ResourceLocation, PersistentRegistryManager.GameDataSnapshot.Entry> e : dataSnapshot.entries.entrySet())
        {
            NBTTagCompound data = new NBTTagCompound();
            registries.setTag(e.getKey().toString(), data);

            NBTTagList ids = new NBTTagList();
            for (Entry<ResourceLocation, Integer> item : e.getValue().ids.entrySet())
            {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString("K", item.getKey().toString());
                tag.setInteger("V", item.getValue());
                ids.appendTag(tag);
            }
            data.setTag("ids", ids);

            NBTTagList aliases = new NBTTagList();
            for (Entry<ResourceLocation, ResourceLocation> entry : e.getValue().aliases.entrySet())
            {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString("K", entry.getKey().toString());
                tag.setString("V", entry.getValue().toString());
                aliases.appendTag(tag);
            }
            data.setTag("aliases", aliases);

            NBTTagList subs = new NBTTagList();
            for (ResourceLocation entry : e.getValue().substitutions)
            {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString("K", entry.toString());
                subs.appendTag(tag);
            }
            data.setTag("substitutions", subs);

            int[] blocked = new int[e.getValue().blocked.size()];
            int idx = 0;
            for (Integer i : e.getValue().blocked)
            {
                blocked[idx++] = i;
            }
            data.setIntArray("blocked", blocked);
            NBTTagList dummied = new NBTTagList();
            for (ResourceLocation entry : e.getValue().dummied)
            {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString("K", entry.toString());
                dummied.appendTag(tag);
            }
            data.setTag("dummied", dummied);
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
                    FMLLog.log("fml.ModTracker", Level.ERROR, "This world was saved with mod %s which appears to be missing, things may not work well", modId);
                    continue;
                }
                if (!modVersion.equals(container.getVersion()))
                {
                    FMLLog.log("fml.ModTracker", Level.INFO, "This world was saved with mod %s version %s and it is now at version %s, things may not work well", modId, modVersion, container.getVersion());
                }
            }
        }

        List<String> failedElements = null;

        if (tag.hasKey("ModItemData")) // Pre 1.7
        {
            StartupQuery.notify("This save predates 1.7.10, it can no longer be loaded here. Please load in 1.7.10 or 1.8 first");
            StartupQuery.abort();
        }
        else if (tag.hasKey("ItemData")) // 1.7
        {
            if (!tag.hasKey("BlockedItemIds")) // no blocked id info -> old 1.7 save
            {
                StartupQuery.notify("This save predates 1.7.10, it can no longer be loaded here. Please load in 1.7.10 or 1.8 first");
                StartupQuery.abort();
            }
            PersistentRegistryManager.GameDataSnapshot snapshot = new PersistentRegistryManager.GameDataSnapshot();
            PersistentRegistryManager.GameDataSnapshot.Entry blocks = new PersistentRegistryManager.GameDataSnapshot.Entry();
            PersistentRegistryManager.GameDataSnapshot.Entry items = new PersistentRegistryManager.GameDataSnapshot.Entry();
            snapshot.entries.put(PersistentRegistryManager.BLOCKS, blocks);
            snapshot.entries.put(PersistentRegistryManager.ITEMS, items);

            NBTTagList list = tag.getTagList("ItemData", 10);
            for (int i = 0; i < list.tagCount(); i++)
            {
                NBTTagCompound e = list.getCompoundTagAt(i);
                String name = e.getString("K");

                if (name.charAt(0) == '\u0001')
                    blocks.ids.put(new ResourceLocation(name.substring(1)), e.getInteger("V"));
                else if (name.charAt(0) == '\u0002')
                    items.ids.put(new ResourceLocation(name.substring(1)), e.getInteger("V"));
            }

            Set<Integer> blockedIds = new HashSet<Integer>();
            for (int id : tag.getIntArray("BlockedItemIds"))
            {
                blockedIds.add(id);
            }
            blocks.blocked.addAll(blockedIds);
            items.blocked.addAll(blockedIds);

            list = tag.getTagList("BlockAliases", 10);
            for (int i = 0; i < list.tagCount(); i++)
            {
                NBTTagCompound dataTag = list.getCompoundTagAt(i);
                blocks.aliases.put(new ResourceLocation(dataTag.getString("K")), new ResourceLocation(dataTag.getString("V")));
            }

            if (tag.hasKey("BlockSubstitutions", 9))
            {
                list = tag.getTagList("BlockSubstitutions", 10);
                for (int i = 0; i < list.tagCount(); i++)
                {
                    NBTTagCompound dataTag = list.getCompoundTagAt(i);
                    blocks.substitutions.add(new ResourceLocation(dataTag.getString("K")));
                }
            }

            list = tag.getTagList("ItemAliases", 10);
            for (int i = 0; i < list.tagCount(); i++)
            {
                NBTTagCompound dataTag = list.getCompoundTagAt(i);
                items.aliases.put(new ResourceLocation(dataTag.getString("K")), new ResourceLocation(dataTag.getString("V")));
            }

            if (tag.hasKey("ItemSubstitutions", 9))
            {
                list = tag.getTagList("ItemSubstitutions", 10);
                for (int i = 0; i < list.tagCount(); i++)
                {
                    NBTTagCompound dataTag = list.getCompoundTagAt(i);
                    items.substitutions.add(new ResourceLocation(dataTag.getString("K")));
                }
            }
            failedElements = PersistentRegistryManager.injectSnapshot(snapshot, true, true);
        }
        else if (tag.hasKey("Registries")) // 1.8, genericed out the 'registries' list
        {
            PersistentRegistryManager.GameDataSnapshot snapshot = new PersistentRegistryManager.GameDataSnapshot();
            NBTTagCompound regs = tag.getCompoundTag("Registries");
            for (String key : regs.getKeySet())
            {
                PersistentRegistryManager.GameDataSnapshot.Entry entry = new PersistentRegistryManager.GameDataSnapshot.Entry();
                ResourceLocation entryLoc;
                if ("fml:blocks".equals(key)) entryLoc = PersistentRegistryManager.BLOCKS;
                else if ("fml:items".equals(key)) entryLoc = PersistentRegistryManager.ITEMS;
                else if ("fmlgr:villagerprofessions".equals(key)) entryLoc = VillagerRegistry.PROFESSIONS;
                else entryLoc = new ResourceLocation(key);
                snapshot.entries.put(entryLoc, entry);

                NBTTagList list = regs.getCompoundTag(key).getTagList("ids", 10);
                for (int x = 0; x < list.tagCount(); x++)
                {
                    NBTTagCompound e = list.getCompoundTagAt(x);
                    entry.ids.put(new ResourceLocation(e.getString("K")), e.getInteger("V"));
                }

                list = regs.getCompoundTag(key).getTagList("aliases", 10);
                for (int x = 0; x < list.tagCount(); x++)
                {
                    NBTTagCompound e = list.getCompoundTagAt(x);
                    entry.aliases.put(new ResourceLocation(e.getString("K")), new ResourceLocation(e.getString("V")));
                }

                list = regs.getCompoundTag(key).getTagList("substitutions", 10);
                for (int x = 0; x < list.tagCount(); x++)
                {
                    NBTTagCompound e = list.getCompoundTagAt(x);
                    entry.substitutions.add(new ResourceLocation(e.getString("K")));
                }

                int[] blocked = regs.getCompoundTag(key).getIntArray("blocked");
                for (int i : blocked)
                {
                    entry.blocked.add(i);
                }

                if (regs.getCompoundTag(key).hasKey("dummied")) // Added in 1.8.9 dev, some worlds may not have it.
                {
                    list = regs.getCompoundTag(key).getTagList("dummied",10);
                    for (int x = 0; x < list.tagCount(); x++)
                    {
                        NBTTagCompound e = list.getCompoundTagAt(x);
                        entry.dummied.add(new ResourceLocation(e.getString("K")));
                    }
                }
            }
            failedElements = PersistentRegistryManager.injectSnapshot(snapshot, true, true);
        }

        if (failedElements != null && !failedElements.isEmpty())
        {
            String text = "Forge Mod Loader could not load this save.\n\n" +
            "There are "+failedElements.size()+" unassigned blocks and items in this save.\n" +
                    "You will not be able to load until they are present again.\n\n" +
                    "Missing Blocks/Items:\n";

            for (String s : failedElements) text += s + "\n";

            StartupQuery.notify(text);
            StartupQuery.abort();
        }
    }


    @Override
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
