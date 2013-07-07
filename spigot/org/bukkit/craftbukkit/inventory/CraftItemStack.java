package org.bukkit.craftbukkit.inventory;

import static org.bukkit.craftbukkit.inventory.CraftMetaItem.ENCHANTMENTS;
import static org.bukkit.craftbukkit.inventory.CraftMetaItem.ENCHANTMENTS_ID;
import static org.bukkit.craftbukkit.inventory.CraftMetaItem.ENCHANTMENTS_LVL;

import java.util.Map;


import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.ImmutableMap;

@DelegateDeserialization(ItemStack.class)
public final class CraftItemStack extends ItemStack {

    public static net.minecraft.item.ItemStack asNMSCopy(ItemStack original) {
        if (original instanceof CraftItemStack) {
            CraftItemStack stack = (CraftItemStack) original;
            return stack.handle == null ? null : stack.handle.copy();
        }
        if (original == null || original.getTypeId() <= 0) {
            return null;
        }
        net.minecraft.item.ItemStack stack = new net.minecraft.item.ItemStack(original.getTypeId(), original.getAmount(), original.getDurability());
        if (original.hasItemMeta()) {
            setItemMeta(stack, original.getItemMeta());
        }
        return stack;
    }

    public static net.minecraft.item.ItemStack copyNMSStack(net.minecraft.item.ItemStack original, int amount) {
        net.minecraft.item.ItemStack stack = original.copy();
        stack.stackSize = amount;
        return stack;
    }

    /**
     * Copies the NMS stack to return as a strictly-Bukkit stack
     */
    public static ItemStack asBukkitCopy(net.minecraft.item.ItemStack original) {
        if (original == null) {
            return new ItemStack(Material.AIR);
        }

        // MCPC+ start - return non-strictly-Bukkit stack in order to preserve full NBT tags
        return asCraftMirror(copyNMSStack(original, original.stackSize));

        /*
        ItemStack stack = new ItemStack(original.itemID, original.stackSize, (short) original.getItemDamage());
        if (hasItemMeta(original)) {
            stack.setItemMeta(getItemMeta(original)); // MCPC+ - TODO: wrap arbitrary mod NBT in ItemMeta
        }
        return stack;
        */
        // MCPC+ end
    }

    public static CraftItemStack asCraftMirror(net.minecraft.item.ItemStack original) {
        return new CraftItemStack(original);
    }

    public static CraftItemStack asCraftCopy(ItemStack original) {
        if (original instanceof CraftItemStack) {
            CraftItemStack stack = (CraftItemStack) original;
            return new CraftItemStack(stack.handle == null ? null : stack.handle.copy());
        }
        return new CraftItemStack(original);
    }

    public static CraftItemStack asNewCraftStack(net.minecraft.item.Item item) {
        return asNewCraftStack(item, 1);
    }

    public static CraftItemStack asNewCraftStack(net.minecraft.item.Item item, int amount) {
        return new CraftItemStack(item.itemID, amount, (short) 0, null);
    }

    net.minecraft.item.ItemStack handle;

    /**
     * Mirror
     */
    private CraftItemStack(net.minecraft.item.ItemStack item) {
        this.handle = item;
    }

    private CraftItemStack(ItemStack item) {
        this(item.getTypeId(), item.getAmount(), item.getDurability(), item.hasItemMeta() ? item.getItemMeta() : null);
    }

    private CraftItemStack(int typeId, int amount, short durability, ItemMeta itemMeta) {
        setTypeId(typeId);
        setAmount(amount);
        setDurability(durability);
        setItemMeta(itemMeta);
    }

    @Override
    public int getTypeId() {
        return handle != null ? handle.itemID : 0;
    }

    @Override
    public void setTypeId(int type) {
        if (getTypeId() == type) {
            return;
        } else if (type == 0) {
            handle = null;
        } else if (handle == null) {
            handle = new net.minecraft.item.ItemStack(type, 1, 0);
        } else {
            handle.itemID = type;
            if (hasItemMeta()) {
                // This will create the appropriate item meta, which will contain all the data we intend to keep
                setItemMeta(handle, getItemMeta(handle));
            }
        }
        setData(null);
    }

    @Override
    public int getAmount() {
        return handle != null ? handle.stackSize : 0;
    }

    @Override
    public void setAmount(int amount) {
        if (handle == null) {
            return;
        }
        if (amount == 0) {
            handle = null;
        } else {
            handle.stackSize = amount;
        }
    }

    @Override
    public void setDurability(final short durability) {
        // Ignore damage if item is null
        if (handle != null) {
            handle.setItemDamage(durability);
        }
    }

    @Override
    public short getDurability() {
        if (handle != null) {
            return (short) handle.getItemDamage();
        } else {
            return -1;
        }
    }

    @Override
    public int getMaxStackSize() {
        return (handle == null) ? Material.AIR.getMaxStackSize() : handle.getItem().getItemStackLimit();
    }

    @Override
    public void addUnsafeEnchantment(Enchantment ench, int level) {
        Validate.notNull(ench, "Cannot add null enchantment");

        if (!makeTag(handle)) {
            return;
        }
        net.minecraft.nbt.NBTTagList list = getEnchantmentList(handle);
        if (list == null) {
            list = new net.minecraft.nbt.NBTTagList(ENCHANTMENTS.NBT);
            handle.stackTagCompound.setTag(ENCHANTMENTS.NBT, list);
        }
        int size = list.tagCount();

        for (int i = 0; i < size; i++) {
            net.minecraft.nbt.NBTTagCompound tag = (net.minecraft.nbt.NBTTagCompound) list.tagAt(i);
            short id = tag.getShort(ENCHANTMENTS_ID.NBT);
            if (id == ench.getId()) {
                tag.setShort(ENCHANTMENTS_LVL.NBT, (short) level);
                return;
            }
        }
        net.minecraft.nbt.NBTTagCompound tag = new net.minecraft.nbt.NBTTagCompound();
        tag.setShort(ENCHANTMENTS_ID.NBT, (short) ench.getId());
        tag.setShort(ENCHANTMENTS_LVL.NBT, (short) level);
        list.appendTag(tag);
    }

    static boolean makeTag(net.minecraft.item.ItemStack item) {
        if (item == null) {
            return false;
        }
        if (item.stackTagCompound != null) {
            return true;
        }
        item.stackTagCompound = new net.minecraft.nbt.NBTTagCompound();
        return true;
    }

    @Override
    public boolean containsEnchantment(Enchantment ench) {
        return getEnchantmentLevel(ench) > 0;
    }

    @Override
    public int getEnchantmentLevel(Enchantment ench) {
        Validate.notNull(ench, "Cannot find null enchantment");
        if (handle == null) {
            return 0;
        }
        return net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(ench.getId(), handle);
    }

    @Override
    public int removeEnchantment(Enchantment ench) {
        Validate.notNull(ench, "Cannot remove null enchantment");

        net.minecraft.nbt.NBTTagList list = getEnchantmentList(handle), listCopy;
        if (list == null) {
            return 0;
        }
        int index = Integer.MIN_VALUE;
        int level = Integer.MIN_VALUE;
        int size = list.tagCount();

        for (int i = 0; i < size; i++) {
            net.minecraft.nbt.NBTTagCompound enchantment = (net.minecraft.nbt.NBTTagCompound) list.tagAt(i);
            int id = 0xffff & enchantment.getShort(ENCHANTMENTS_ID.NBT);
            if (id == ench.getId()) {
                index = i;
                level = 0xffff & enchantment.getShort(ENCHANTMENTS_LVL.NBT);
                break;
            }
        }

        if (index == Integer.MIN_VALUE) {
            return 0;
        }
        if (size == 1) {
            handle.stackTagCompound.removeTag(ENCHANTMENTS.NBT);
            if (handle.stackTagCompound.hasNoTags()) {
                handle.stackTagCompound = null;
            }
            return level;
        }

        // This is workaround for not having an index removal
        listCopy = new net.minecraft.nbt.NBTTagList(ENCHANTMENTS.NBT);
        for (int i = 0; i < size; i++) {
            if (i != index) {
                listCopy.appendTag(list.tagAt(i));
            }
        }
        handle.stackTagCompound.setTag(ENCHANTMENTS.NBT, listCopy);

        return level;
    }

    @Override
    public Map<Enchantment, Integer> getEnchantments() {
        return getEnchantments(handle);
    }

    static Map<Enchantment, Integer> getEnchantments(net.minecraft.item.ItemStack item) {
        ImmutableMap.Builder<Enchantment, Integer> result = ImmutableMap.builder();
        net.minecraft.nbt.NBTTagList list = (item == null) ? null : item.getEnchantmentTagList();

        if (list == null) {
            return result.build();
        }

        for (int i = 0; i < list.tagCount(); i++) {
            int id = 0xffff & ((net.minecraft.nbt.NBTTagCompound) list.tagAt(i)).getShort(ENCHANTMENTS_ID.NBT);
            int level = 0xffff & ((net.minecraft.nbt.NBTTagCompound) list.tagAt(i)).getShort(ENCHANTMENTS_LVL.NBT);

            result.put(Enchantment.getById(id), level);
        }

        return result.build();
    }

    static net.minecraft.nbt.NBTTagList getEnchantmentList(net.minecraft.item.ItemStack item) {
        return item == null ? null : item.getEnchantmentTagList();
    }

    @Override
    public CraftItemStack clone() {
        CraftItemStack itemStack = (CraftItemStack) super.clone();
        if (this.handle != null) {
            itemStack.handle = this.handle.copy();
        }
        return itemStack;
    }

    @Override
    public ItemMeta getItemMeta() {
        return getItemMeta(handle);
    }

    public static ItemMeta getItemMeta(net.minecraft.item.ItemStack item) {
        if (!hasItemMeta(item)) {
            return CraftItemFactory.instance().getItemMeta(getType(item));
        }
        switch (getType(item)) {
            case WRITTEN_BOOK:
            case BOOK_AND_QUILL:
                return new CraftMetaBook(item.stackTagCompound);
            case SKULL_ITEM:
                return new CraftMetaSkull(item.stackTagCompound);
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
                return new CraftMetaLeatherArmor(item.stackTagCompound);
            case POTION:
                return new CraftMetaPotion(item.stackTagCompound);
            case MAP:
                return new CraftMetaMap(item.stackTagCompound);
            case FIREWORK:
                return new CraftMetaFirework(item.stackTagCompound);
            case FIREWORK_CHARGE:
                return new CraftMetaCharge(item.stackTagCompound);
            case ENCHANTED_BOOK:
                return new CraftMetaEnchantedBook(item.stackTagCompound);
            default:
                return new CraftMetaItem(item.stackTagCompound);
        }
    }

    static Material getType(net.minecraft.item.ItemStack item) {
        Material material = Material.getMaterial(item == null ? 0 : item.itemID);
        return material == null ? Material.AIR : material;
    }

    @Override
    public boolean setItemMeta(ItemMeta itemMeta) {
        return setItemMeta(handle, itemMeta);
    }

    public static boolean setItemMeta(net.minecraft.item.ItemStack item, ItemMeta itemMeta) {
        if (item == null) {
            return false;
        }
        if (itemMeta == null) {
            item.stackTagCompound = null;
            return true;
        }
        if (!CraftItemFactory.instance().isApplicable(itemMeta, getType(item))) {
            return false;
        }

        net.minecraft.nbt.NBTTagCompound tag = new net.minecraft.nbt.NBTTagCompound();
        item.setTagCompound(tag);

        ((CraftMetaItem) itemMeta).applyToItem(tag);
        return true;
    }

    @Override
    public boolean isSimilar(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        if (stack == this) {
            return true;
        }
        if (!(stack instanceof CraftItemStack)) {
            return stack.getClass() == ItemStack.class && stack.isSimilar(this);
        }

        CraftItemStack that = (CraftItemStack) stack;
        if (handle == that.handle) {
            return true;
        }
        if (handle == null || that.handle == null) {
            return false;
        }
        if (!(that.getTypeId() == getTypeId() && getDurability() == that.getDurability())) {
            return false;
        }
        return hasItemMeta() ? that.hasItemMeta() && handle.stackTagCompound.equals(that.handle.stackTagCompound) : !that.hasItemMeta();
    }

    @Override
    public boolean hasItemMeta() {
        return hasItemMeta(handle);
    }

    static boolean hasItemMeta(net.minecraft.item.ItemStack item) {
        return !(item == null || item.stackTagCompound == null || item.stackTagCompound.hasNoTags());
    }
}
