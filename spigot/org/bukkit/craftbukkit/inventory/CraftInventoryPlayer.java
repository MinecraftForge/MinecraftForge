package org.bukkit.craftbukkit.inventory;


import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class CraftInventoryPlayer extends CraftInventory implements org.bukkit.inventory.PlayerInventory, EntityEquipment {
    public CraftInventoryPlayer(net.minecraft.entity.player.InventoryPlayer inventory) {
        super(inventory);
    }

    @Override
    public net.minecraft.entity.player.InventoryPlayer getInventory() {
        return (net.minecraft.entity.player.InventoryPlayer) inventory;
    }

    @Override
    public int getSize() {
        return getInventory().mainInventory.length; // MCPC+ - Galacticraft and Aether extend equipped item slots so we need to check the main inventory array directly
    }

    public ItemStack getItemInHand() {
        return CraftItemStack.asCraftMirror(getInventory().getCurrentItem());
    }

    public void setItemInHand(ItemStack stack) {
        setItem(getHeldItemSlot(), stack);
    }

    public int getHeldItemSlot() {
        return getInventory().currentItem;
    }

    public void setHeldItemSlot(int slot) {
        Validate.isTrue(slot >= 0 && slot < net.minecraft.entity.player.InventoryPlayer.getHotbarSize(), "Slot is not between 0 and 8 inclusive");
        this.getInventory().currentItem = slot;
        ((CraftPlayer) this.getHolder()).getHandle().playerNetServerHandler.sendPacketToPlayer(new net.minecraft.network.packet.Packet16BlockItemSwitch(slot));
    }

    public ItemStack getHelmet() {
        return getItem(getSize() + 3);
    }

    public ItemStack getChestplate() {
        return getItem(getSize() + 2);
    }

    public ItemStack getLeggings() {
        return getItem(getSize() + 1);
    }

    public ItemStack getBoots() {
        return getItem(getSize() + 0);
    }

    public void setHelmet(ItemStack helmet) {
        setItem(getSize() + 3, helmet);
    }

    public void setChestplate(ItemStack chestplate) {
        setItem(getSize() + 2, chestplate);
    }

    public void setLeggings(ItemStack leggings) {
        setItem(getSize() + 1, leggings);
    }

    public void setBoots(ItemStack boots) {
        setItem(getSize() + 0, boots);
    }

    public ItemStack[] getArmorContents() {
        net.minecraft.item.ItemStack[] mcItems = getInventory().getArmorContents();
        ItemStack[] ret = new ItemStack[mcItems.length];

        for (int i = 0; i < mcItems.length; i++) {
            ret[i] = CraftItemStack.asCraftMirror(mcItems[i]);
        }
        return ret;
    }

    public void setArmorContents(ItemStack[] items) {
        int cnt = getSize();

        if (items == null) {
            items = new ItemStack[4];
        }
        for (ItemStack item : items) {
            if (item == null || item.getTypeId() == 0) {
                clear(cnt++);
            } else {
                setItem(cnt++, item);
            }
        }
    }

    public int clear(int id, int data) {
        int count = 0;
        ItemStack[] items = getContents();
        ItemStack[] armor = getArmorContents();
        int armorSlot = getSize();

        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            if (item == null) continue;
            if (id > -1 && item.getTypeId() != id) continue;
            if (data > -1 && item.getData().getData() != data) continue;

            count += item.getAmount();
            setItem(i, null);
        }

        for (ItemStack item : armor) {
            if (item == null) continue;
            if (id > -1 && item.getTypeId() != id) continue;
            if (data > -1 && item.getData().getData() != data) continue;

            count += item.getAmount();
            setItem(armorSlot++, null);
        }
        return count;
    }

    @Override
    public HumanEntity getHolder() {
        return (HumanEntity) inventory.getOwner();
    }

    public float getItemInHandDropChance() {
        return 1;
    }

    public void setItemInHandDropChance(float chance) {
        throw new UnsupportedOperationException();
    }

    public float getHelmetDropChance() {
        return 1;
    }

    public void setHelmetDropChance(float chance) {
        throw new UnsupportedOperationException();
    }

    public float getChestplateDropChance() {
        return 1;
    }

    public void setChestplateDropChance(float chance) {
        throw new UnsupportedOperationException();
    }

    public float getLeggingsDropChance() {
        return 1;
    }

    public void setLeggingsDropChance(float chance) {
        throw new UnsupportedOperationException();
    }

    public float getBootsDropChance() {
        return 1;
    }

    public void setBootsDropChance(float chance) {
        throw new UnsupportedOperationException();
    }
}
