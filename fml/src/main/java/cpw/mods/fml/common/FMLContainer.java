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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.Level;
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
import cpw.mods.fml.common.registry.GameRegistryException;
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
            mod.func_74778_a("ModId", mc.getModId());
            mod.func_74778_a("ModVersion", mc.getVersion());
            list.func_74742_a(mod);
        }
        fmlData.func_74782_a("ModList", list);
        NBTTagList dataList = new NBTTagList();
        FMLLog.fine("Gathering id map for writing to world save %s", info.func_76065_j());
        Map<String,Integer> itemList = GameData.buildItemDataList();
        for (Entry<String, Integer> item : itemList.entrySet())
        {
            NBTTagCompound tag = new NBTTagCompound();
            tag.func_74778_a("K",item.getKey());
            tag.func_74768_a("V",item.getValue());
            dataList.func_74742_a(tag);
        }
        fmlData.func_74782_a("ItemData", dataList);
        return fmlData;
    }

    @Override
    public void readData(SaveHandler handler, WorldInfo info, Map<String, NBTBase> propertyMap, NBTTagCompound tag)
    {
        if (tag.func_74764_b("ModList"))
        {
            NBTTagList modList = tag.func_150295_c("ModList", (byte)10);
            for (int i = 0; i < modList.func_74745_c(); i++)
            {
                NBTTagCompound mod = modList.func_150305_b(i);
                String modId = mod.func_74779_i("ModId");
                String modVersion = mod.func_74779_i("ModVersion");
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
        if (tag.func_74764_b("ModItemData"))
        {
            FMLLog.info("Attempting to convert old world data to new system. This may be trouble!");
            NBTTagList modList = tag.func_150295_c("ModItemData", (byte)10);
            Map<String,Integer> dataList = Maps.newLinkedHashMap();
            for (int i = 0; i < modList.func_74745_c(); i++)
            {
                NBTTagCompound itemTag = modList.func_150305_b(i);
                String modId = itemTag.func_74779_i("ModId");
                String itemType = itemTag.func_74779_i("ItemType");
                int itemId = itemTag.func_74762_e("ItemId");
                int ordinal = itemTag.func_74762_e("ordinal");
                String forcedModId = itemTag.func_74764_b("ForcedModId") ? itemTag.func_74779_i("ForcedModId") : null;
                String forcedName = itemTag.func_74764_b("ForcedName") ? itemTag.func_74779_i("ForcedName") : null;
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
            List<String> failedElements = GameData.injectWorldIDMap(dataList, true, true);
            if (!failedElements.isEmpty())
            {
                throw new GameRegistryException("Failed to load the world - there are fatal block and item id issues", failedElements);
            }
        }
        else if (tag.func_74764_b("ItemData"))
        {
            NBTTagList list = tag.func_150295_c("ItemData", (byte)10);
            Map<String,Integer> dataList = Maps.newLinkedHashMap();
            for (int i = 0; i < list.func_74745_c(); i++)
            {
                NBTTagCompound dataTag = list.func_150305_b(i);
                dataList.put(dataTag.func_74779_i("K"), dataTag.func_74762_e("V"));
            }
            List<String> failedElements = GameData.injectWorldIDMap(dataList, true, true);
            if (!failedElements.isEmpty())
            {
                throw new GameRegistryException("Failed to load the world - there are fatal block and item id issues", failedElements);
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

    @Override
    public Object getMod()
    {
        return this;
    }
}
