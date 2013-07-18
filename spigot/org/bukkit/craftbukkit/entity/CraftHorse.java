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
        return Variant.values()[getHandle().func_110265_bP()];
    }

    public void setVariant(Variant variant) {
        Validate.notNull(variant, "Variant cannot be null");
        getHandle().func_110214_p(variant.ordinal());
    }

    public Color getColor() {
        return Color.values()[getHandle().func_110202_bQ() & 0xFF];
    }

    public void setColor(Color color) {
        Validate.notNull(color, "Color cannot be null");
        getHandle().func_110235_q(color.ordinal() & 0xFF | getStyle().ordinal() << 8);
    }

    public Style getStyle() {
        return Style.values()[getHandle().func_110202_bQ() >>> 8];
    }

    public void setStyle(Style style) {
        Validate.notNull(style, "Style cannot be null");
        getHandle().func_110235_q(getColor().ordinal() & 0xFF | style.ordinal() << 8);
    }

    public boolean isCarryingChest() {
        return getHandle().func_110261_ca();
    }

    public void setCarryingChest(boolean chest) {
        if (chest == isCarryingChest()) return;
        getHandle().func_110207_m(chest);
        getHandle().func_110226_cD(); // Should be loadChest
    }

    public int getDomestication() {
        return getHandle().func_110252_cg();
    }

    public void setDomestication(int value) {
        Validate.isTrue(value >= 0, "Domestication cannot be less than zero");
        Validate.isTrue(value <= getMaxDomestication(), "Domestication cannot be greater than the max domestication");
        getHandle().func_110238_s(value);
    }

    public int getMaxDomestication() {
        return getHandle().func_110218_cm(); // Should be getMaxDomestication
    }

    public void setMaxDomestication(int value) {
        Validate.isTrue(value > 0, "Max domestication cannot be zero or less");
        getHandle().maxDomestication = value;
    }

    public double getJumpStrength() {
        return getHandle().func_110215_cj();
    }

    public void setJumpStrength(double strength) {
        Validate.isTrue(strength >= 0, "Jump strength cannot be less than zero");
        getHandle().func_110148_a(net.minecraft.entity.passive.EntityHorse.field_110271_bv).func_111128_a(strength);
    }

    @Override
    public boolean isTamed() {
        return getHandle().func_110248_bS();
    }

    @Override
    public void setTamed(boolean tamed) {
        getHandle().func_110234_j(tamed);
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
        return getHandle().func_142019_cb();
    }

    public void setOwnerName(String name) {
        getHandle().func_110213_b(name);
    }

    public HorseInventory getInventory() {
        return new CraftInventoryHorse(getHandle().field_110296_bG);
    }

    @Override
    public String toString() {
        return "CraftHorse{variant=" + getVariant() + ", owner=" + getOwner() + '}';
    }

    public EntityType getType() {
        return EntityType.HORSE;
    }
}
