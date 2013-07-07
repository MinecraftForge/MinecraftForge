package org.bukkit.craftbukkit.util;


// Util class to create custom DamageSources.
public final class CraftDamageSource extends net.minecraft.util.DamageSource {
    public static net.minecraft.util.DamageSource copyOf(final net.minecraft.util.DamageSource original) {
        CraftDamageSource newSource = new CraftDamageSource(original.damageType);

        // Check ignoresArmor
        if (original.isUnblockable()) {
            newSource.setDamageBypassesArmor();
        }

        // Check magic
        if (original.isMagicDamage()) {
            newSource.setMagicDamage();
        }

        // Check fire
        if (original.isExplosion()) {
            newSource.setFireDamage();
        }

        return newSource;
    }

    private CraftDamageSource(String identifier) {
        super(identifier);
    }
}
