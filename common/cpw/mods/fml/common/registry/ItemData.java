package cpw.mods.fml.common.registry;

import java.util.Map;

import com.google.common.base.Objects;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;

import cpw.mods.fml.common.ModContainer;

import net.minecraft.src.Item;
import net.minecraft.src.NBTTagCompound;

public class ItemData {

    private static Map<String, Multiset<String>> modOrdinals = Maps.newHashMap();
    public final String modId;
    public final String itemType;
    public final int itemId;
    public final int ordinal;

    public ItemData(Item item, ModContainer mc)
    {
        this.itemId = item.field_77779_bT;
        this.itemType = item.getClass().getName();
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
    }

    public NBTTagCompound toNBT()
    {
        NBTTagCompound tag = new NBTTagCompound();
        tag.func_74778_a("ModId", modId);
        tag.func_74778_a("ItemType", itemType);
        tag.func_74768_a("ItemId", itemId);
        tag.func_74768_a("ordinal", ordinal);
        return tag;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(modId, itemType, itemId, ordinal);
    }

    @Override
    public boolean equals(Object obj)
    {
        try
        {
            ItemData other = (ItemData) obj;
            return Objects.equal(modId, other.modId) && Objects.equal(itemType, other.itemType) && Objects.equal(itemId, other.itemId) && Objects.equal(ordinal, other.ordinal);
        }
        catch (ClassCastException cce)
        {
            return false;
        }
    }
}
