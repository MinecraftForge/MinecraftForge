package org.bukkit.craftbukkit.entity;

import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.inventory.CraftInventoryHorse;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.inventory.HorseInventory;

public class CraftHorse extends CraftAnimals implements Horse {

    public CraftHorse(CraftServer server, net.minecraft.entity.passive.EntityHorse entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.passive.EntityHorse getHandle() {
        return (net.minecraft.entity.passive.EntityHorse) entity;
    }

    public Variant getVariant() {
        return Variant.values()[getHandle().getHorseType()];
    }

    public void setVariant(Variant variant) {
        Validate.notNull(variant, "Variant cannot be null");
        getHandle().setHorseType(variant.ordinal());
    }

    public Color getColor() {
        return Color.values()[getHandle().getHorseVariant() & 0xFF];
    }

    public void setColor(Color color) {
        Validate.notNull(color, "Color cannot be null");
        getHandle().setHorseVariant(color.ordinal() & 0xFF | getStyle().ordinal() << 8);
    }

    public Style getStyle() {
        return Style.values()[getHandle().getHorseVariant() >>> 8];
    }

    public void setStyle(Style style) {
        Validate.notNull(style, "Style cannot be null");
        getHandle().setHorseVariant(getColor().ordinal() & 0xFF | style.ordinal() << 8);
    }

    public boolean isCarryingChest() {
        return getHandle().isChested();
    }

    public void setCarryingChest(boolean chest) {
        if (chest == isCarryingChest()) return;
        getHandle().setChested(chest);
        getHandle().func_110226_cD(); // Should be loadChest
    }

    public int getDomestication() {
        return getHandle().getTemper();
    }

    public void setDomestication(int value) {
        Validate.isTrue(value >= 0, "Domestication cannot be less than zero");
        Validate.isTrue(value <= getMaxDomestication(), "Domestication cannot be greater than the max domestication");
        getHandle().setTemper(value);
    }

    public int getMaxDomestication() {
        return getHandle().getMaxTemper(); // Should be getMaxDomestication
    }

    public void setMaxDomestication(int value) {
        Validate.isTrue(value > 0, "Max domestication cannot be zero or less");
        getHandle().maxDomestication = value;
    }

    public double getJumpStrength() {
        return getHandle().getHorseJumpStrength();
    }

    public void setJumpStrength(double strength) {
        Validate.isTrue(strength >= 0, "Jump strength cannot be less than zero");
        getHandle().getEntityAttribute(net.minecraft.entity.passive.EntityHorse.horseJumpStrength).setAttribute(strength);
    }

    @Override
    public boolean isTamed() {
        return getHandle().isTame();
    }

    @Override
    public void setTamed(boolean tamed) {
        getHandle().setHorseTamed(tamed);
    }

    @Override
    public AnimalTamer getOwner() {
        if (getOwnerName() == null || "".equals(getOwnerName())) return null;
        return getServer().getOfflinePlayer(getOwnerName());
    }

    @Override
    public void setOwner(AnimalTamer owner) {
        if (owner != null && !"".equals(owner.getName())) {
            setTamed(true);
            getHandle().setPathToEntity(null);
            setOwnerName(owner.getName());
        } else {
            setTamed(false);
            setOwnerName("");
        }
    }

    public String getOwnerName() {
        return getHandle().getOwnerName();
    }

    public void setOwnerName(String name) {
        getHandle().setOwnerName(name);
    }

    public HorseInventory getInventory() {
        return new CraftInventoryHorse(getHandle().horseChest);
    }

    @Override
    public String toString() {
        return "CraftHorse{variant=" + getVariant() + ", owner=" + getOwner() + '}';
    }

    public EntityType getType() {
        return EntityType.HORSE;
    }
}
