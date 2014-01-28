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

package cpw.mods.fml.common;

import java.io.File;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.logging.log4j.Level;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;

import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.client.FMLFileResourcePack;
import cpw.mods.fml.client.FMLFolderResourcePack;
import cpw.mods.fml.common.asm.FMLSanityChecker;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;

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
        meta.authorList=Arrays.asList("cpw", "LexManos");
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

    @NetworkCheckHandler
    public boolean checkModLists(Map<String,String> modList, Side side)
    {
        return Loader.instance().checkRemoteModList(modList,side);
    }
    @Override
    public NBTTagCompound getDataForWriting(SaveHandler handler, WorldInfo info)
    {
        NBTTagCompound fmlData = new NBTTagCompound();
        NBTTagList list = new NBTTagList();
        for (ModContainer mc : Loader.instance().getActiveModList())
        {
            NBTTagCompound mod = new NBTTagCompound();
            mod.setString("ModId", mc.getModId());
            mod.setString("ModVersion", mc.getVersion());
            list.appendTag(mod);
        }
        fmlData.setTag("ModList", list);
        NBTTagList dataList = new NBTTagList();
        FMLLog.fine("Gathering id map for writing to world save %s", info.getWorldName());
        Map<String,Integer> itemList = GameData.buildItemDataList();
        for (Entry<String, Integer> item : itemList.entrySet())
        {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("K",item.getKey());
            tag.setInteger("V",item.getValue());
            dataList.appendTag(tag);
        }
        fmlData.setTag("ItemData", dataList);
        return fmlData;
    }

    @Override
    public void readData(SaveHandler handler, WorldInfo info, Map<String, NBTBase> propertyMap, NBTTagCompound tag)
    {
        if (tag.hasKey("ModList"))
        {
            NBTTagList modList = tag.func_150295_c("ModList", (byte)10);
            for (int i = 0; i < modList.tagCount(); i++)
            {
                NBTTagCompound mod = modList.func_150305_b(i);
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
        if (tag.hasKey("ModItemData"))
        {
            FMLLog.info("Attempting to convert old world data to new system. This may be trouble!");
            NBTTagList modList = tag.func_150295_c("ModItemData", (byte)10);
            Map<String,Integer> dataList = Maps.newLinkedHashMap();
            for (int i = 0; i < modList.tagCount(); i++)
            {
                NBTTagCompound itemTag = modList.func_150305_b(i);
                String modId = itemTag.getString("ModId");
                String itemType = itemTag.getString("ItemType");
                int itemId = itemTag.getInteger("ItemId");
                int ordinal = itemTag.getInteger("ordinal");
                String forcedModId = itemTag.hasKey("ForcedModId") ? itemTag.getString("ForcedModId") : null;
                String forcedName = itemTag.hasKey("ForcedName") ? itemTag.getString("ForcedName") : null;
                if (forcedName == null)
                {
                    FMLLog.warning("Found unlabelled item in world save, this may cause problems. The item type %s:%d will not be present", itemType, ordinal);
                }
                else
                {
                    boolean isItem;
                    try {
                        Class<?> clazz = Class.forName(itemType);
                        clazz.asSubclass(Item.class);
                        isItem = true;
                    }
                    catch (ClassNotFoundException cnfs)
                    {
                        FMLLog.warning("The old item %s is not present in this game, it's type cannot be inferred - it will be skipped", itemType);
                        // MISSING, skip
                        continue;
                    }
                    catch (ClassCastException ccs)
                    {
                        isItem = false;
                    }
                    String itemLabel = String.format("%c%s:%s", isItem ? '\u0002' : '\u0001', forcedModId != null ? forcedModId : modId, forcedName);
                    dataList.put(itemLabel, itemId);
                }
            }
            boolean successfullyInjected = GameData.injectWorldIDMap(dataList, true);
            if (!successfullyInjected)
            {
                throw new RuntimeException("Failed to load the world - there are fatal block and item id issues");
            }
        }
        else if (tag.hasKey("ItemData"))
        {
            NBTTagList list = tag.func_150295_c("ItemData", (byte)10);
            Map<String,Integer> dataList = Maps.newLinkedHashMap();
            for (int i = 0; i < list.tagCount(); i++)
            {
                NBTTagCompound dataTag = list.func_150305_b(i);
                dataList.put(dataTag.getString("K"), dataTag.getInteger("V"));
            }
            boolean successfullyInjected = GameData.injectWorldIDMap(dataList, true);
            if (!successfullyInjected)
            {
                throw new RuntimeException("Failed to load the world - there are fatal block and item id issues");
            }
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
        return "cpw.mods.fml.client.FMLConfigGuiFactory";
    }
}