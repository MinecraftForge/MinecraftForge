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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
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
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.relauncher.Side;

import org.apache.logging.log4j.Level;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
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
        meta.updateUrl="https://github.com/MinecraftForge/FML/wiki";
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
        GameData.GameDataSnapshot dataSnapshot = GameData.takeSnapshot();

        for (Map.Entry<String, GameData.GameDataSnapshot.Entry> e : dataSnapshot.entries.entrySet())
        {
            NBTTagCompound data = new NBTTagCompound();
            registries.setTag(e.getKey(), data);

            NBTTagList ids = new NBTTagList();
            for (Entry<String, Integer> item : e.getValue().ids.entrySet())
            {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString("K", item.getKey());
                tag.setInteger("V", item.getValue());
                ids.appendTag(tag);
            }
            data.setTag("ids", ids);

            NBTTagList aliases = new NBTTagList();
            for (Entry<String, String> entry : e.getValue().aliases.entrySet())
            {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString("K", entry.getKey());
                tag.setString("V", entry.getValue());
                aliases.appendTag(tag);
            }
            data.setTag("aliases", aliases);

            NBTTagList subs = new NBTTagList();
            for (String entry : e.getValue().substitutions)
            {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString("K", entry);
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
            GameData.GameDataSnapshot snapshot = new GameData.GameDataSnapshot();
            GameData.GameDataSnapshot.Entry items = new GameData.GameDataSnapshot.Entry();
            snapshot.entries.put("fml:blocks", new GameData.GameDataSnapshot.Entry());
            snapshot.entries.put("fml:items", items);

            FMLLog.info("Attempting to convert old world data to new system. This may be trouble!");
            NBTTagList modList = tag.getTagList("ModItemData", (byte)10);
            for (int i = 0; i < modList.tagCount(); i++)
            {
                NBTTagCompound data = modList.getCompoundTagAt(i);
                String forcedModId = data.hasKey("ForcedModId") ? data.getString("ForcedModId") : null;
                String forcedName = data.hasKey("ForcedName") ? data.getString("ForcedName") : null;
                if (forcedName == null)
                {
                    FMLLog.warning("Found unlabelled item in world save, this may cause problems. The item type %s:%d will not be present", data.getString("ItemType"), data.getInteger("ordinal"));
                }
                else
                {
                    // all entries are Items, blocks were only saved through their ItemBlock
                    String itemLabel = String.format("%s:%s", forcedModId != null ? forcedModId : data.getString("ModId"), forcedName);
                    items.ids.put(itemLabel, data.getInteger("ItemId"));
                }
            }
            failedElements = GameData.injectSnapshot(snapshot, true, true);

        }
        else if (tag.hasKey("ItemData")) // 1.7
        {
            GameData.GameDataSnapshot snapshot = new GameData.GameDataSnapshot();
            GameData.GameDataSnapshot.Entry blocks = new GameData.GameDataSnapshot.Entry();
            GameData.GameDataSnapshot.Entry items = new GameData.GameDataSnapshot.Entry();
            snapshot.entries.put("fml:blocks", blocks);
            snapshot.entries.put("fml:items", items);

            NBTTagList list = tag.getTagList("ItemData", 10);
            for (int i = 0; i < list.tagCount(); i++)
            {
                NBTTagCompound e = list.getCompoundTagAt(i);
                String name = e.getString("K");

                if (name.charAt(0) == '\u0001')
                    blocks.ids.put(name.substring(1), e.getInteger("V"));
                else if (name.charAt(0) == '\u0002')
                    items.ids.put(name.substring(1), e.getInteger("V"));
            }

            Set<Integer> blockedIds = new HashSet<Integer>();
            if (!tag.hasKey("BlockedItemIds")) // no blocked id info -> old 1.7 save
            {
                // old early 1.7 save potentially affected by the registry mapping bug
                // fix the ids the best we can...
                GameData.fixBrokenIds(blocks, items, blockedIds);
            }
            else
            {
                for (int id : tag.getIntArray("BlockedItemIds"))
                {
                    blockedIds.add(id);
                }
            }
            blocks.blocked.addAll(blockedIds);
            items.blocked.addAll(blockedIds);

            list = tag.getTagList("BlockAliases", 10);
            for (int i = 0; i < list.tagCount(); i++)
            {
                NBTTagCompound dataTag = list.getCompoundTagAt(i);
                blocks.aliases.put(dataTag.getString("K"), dataTag.getString("V"));
            }

            if (tag.hasKey("BlockSubstitutions", 9))
            {
                list = tag.getTagList("BlockSubstitutions", 10);
                for (int i = 0; i < list.tagCount(); i++)
                {
                    NBTTagCompound dataTag = list.getCompoundTagAt(i);
                    blocks.substitutions.add(dataTag.getString("K"));
                }
            }

            list = tag.getTagList("ItemAliases", 10);
            for (int i = 0; i < list.tagCount(); i++)
            {
                NBTTagCompound dataTag = list.getCompoundTagAt(i);
                items.aliases.put(dataTag.getString("K"), dataTag.getString("V"));
            }

            if (tag.hasKey("ItemSubstitutions", 9))
            {
                list = tag.getTagList("ItemSubstitutions", 10);
                for (int i = 0; i < list.tagCount(); i++)
                {
                    NBTTagCompound dataTag = list.getCompoundTagAt(i);
                    items.substitutions.add(dataTag.getString("K"));
                }
            }
            failedElements = GameData.injectSnapshot(snapshot, true, true);
        }
        else if (tag.hasKey("Registries")) // 1.8, genericed out the 'registries' list
        {
            GameData.GameDataSnapshot snapshot = new GameData.GameDataSnapshot();
            NBTTagCompound regs = tag.getCompoundTag("Registries");
            for (String key : (Set<String>)regs.getKeySet())
            {
                GameData.GameDataSnapshot.Entry entry = new GameData.GameDataSnapshot.Entry();
                snapshot.entries.put(key, entry);

                NBTTagList list = regs.getCompoundTag(key).getTagList("ids", 10);
                for (int x = 0; x < list.tagCount(); x++)
                {
                    NBTTagCompound e = list.getCompoundTagAt(x);
                    entry.ids.put(e.getString("K"), e.getInteger("V"));
                }

                list = regs.getCompoundTag(key).getTagList("aliases", 10);
                for (int x = 0; x < list.tagCount(); x++)
                {
                    NBTTagCompound e = list.getCompoundTagAt(x);
                    entry.aliases.put(e.getString("K"), e.getString("V"));
                }

                list = regs.getCompoundTag(key).getTagList("substitutions", 10);
                for (int x = 0; x < list.tagCount(); x++)
                {
                    NBTTagCompound e = list.getCompoundTagAt(x);
                    entry.substitutions.add(e.getString("K"));
                }

                int[] blocked = regs.getCompoundTag(key).getIntArray("blocked");
                for (int i : blocked)
                {
                    entry.blocked.add(i);
                }
            }
            failedElements = GameData.injectSnapshot(snapshot, true, true);
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
