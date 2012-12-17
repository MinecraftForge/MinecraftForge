package cpw.mods.fml.common.registry;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.nbt.NBTTagCompound;

import com.google.common.base.Objects;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderException;
import cpw.mods.fml.common.ModContainer;

public class ItemData {

    private static Map<String, Multiset<String>> modOrdinals = Maps.newHashMap();
    private final String modId;
    private final String itemType;
    private final int itemId;
    private final int ordinal;
    private String forcedModId;
    private String forcedName;

    public ItemData(Item item, ModContainer mc)
    {
        this.itemId = item.field_77779_bT;
        if (item.getClass().equals(ItemBlock.class))
        {
            this.itemType =  Block.field_71973_m[this.getItemId()].getClass().getName();
        }
        else
        {
            this.itemType = item.getClass().getName();
        }
        this.modId = mc.getModId();
        if (!modOrdinals.containsKey(mc.getModId()))
        {
            modOrdinals.put(mc.getModId(), HashMultiset.<String>create());
        }
        this.ordinal = modOrdinals.get(mc.getModId()).add(itemType, 1);
    }

    public ItemData(NBTTagCompound tag)
    {
        this.modId = tag.func_74779_i("ModId");
        this.itemType = tag.func_74779_i("ItemType");
        this.itemId = tag.func_74762_e("ItemId");
        this.ordinal = tag.func_74762_e("ordinal");
        this.forcedModId = tag.func_74764_b("ForcedModId") ? tag.func_74779_i("ForcedModId") : null;
        this.forcedName = tag.func_74764_b("ForcedName") ? tag.func_74779_i("ForcedName") : null;
    }

    public String getItemType()
    {
        return this.forcedName !=null ? forcedName : itemType;
    }

    public String getModId()
    {
        return this.forcedModId != null ? forcedModId : modId;
    }

    public int getOrdinal()
    {
        return ordinal;
    }

    public int getItemId()
    {
        return itemId;
    }

    public NBTTagCompound toNBT()
    {
        NBTTagCompound tag = new NBTTagCompound();
        tag.func_74778_a("ModId", modId);
        tag.func_74778_a("ItemType", itemType);
        tag.func_74768_a("ItemId", itemId);
        tag.func_74768_a("ordinal", ordinal);
        if (forcedModId != null)
        {
            tag.func_74778_a("ForcedModId", forcedModId);
        }
        if (forcedName != null)
        {
            tag.func_74778_a("ForcedName", forcedName);
        }
        return tag;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(itemId, ordinal);
    }

    @Override
    public boolean equals(Object obj)
    {
        try
        {
            ItemData other = (ItemData) obj;
            return Objects.equal(getModId(), other.getModId()) && Objects.equal(getItemType(), other.getItemType()) && Objects.equal(itemId, other.itemId) && ( isOveridden() || Objects.equal(ordinal, other.ordinal));
        }
        catch (ClassCastException cce)
        {
            return false;
        }
    }

    @Override
    public String toString()
    {
        return String.format("Item %d, Type %s, owned by %s, ordinal %d, name %s, claimedModId %s", itemId, itemType, modId, ordinal, forcedName, forcedModId);
    }

    public boolean mayDifferByOrdinal(ItemData rightValue)
    {
        return Objects.equal(getItemType(), rightValue.getItemType()) && Objects.equal(getModId(), rightValue.getModId());
    }

    public boolean isOveridden()
    {
        return forcedName != null;
    }

    public void setName(String name, String modId)
    {
        if (name == null)
        {
            this.forcedName = null;
            this.forcedModId = null;
            return;
        }
        String localModId = modId;
        if (modId == null)
        {
            localModId = Loader.instance().activeModContainer().getModId();
        }
        if (modOrdinals.get(localModId).count(name)>0)
        {
            FMLLog.severe("The mod %s is attempting to redefine the item at id %d with a non-unique name (%s.%s)", Loader.instance().activeModContainer(), itemId, localModId, name);
            throw new LoaderException();
        }
        modOrdinals.get(localModId).add(name);
        this.forcedModId = modId;
        this.forcedName = name;
    }
}
