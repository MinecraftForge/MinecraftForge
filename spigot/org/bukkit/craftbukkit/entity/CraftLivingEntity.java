package org.bukkit.craftbukkit.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;


import org.apache.commons.lang.Validate;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.inventory.CraftEntityEquipment;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

public class CraftLivingEntity extends CraftEntity implements LivingEntity {
    private CraftEntityEquipment equipment;

    public CraftLivingEntity(final CraftServer server, final net.minecraft.entity.EntityLivingBase entity) {
        super(server, entity);

        if (entity instanceof net.minecraft.entity.EntityLiving) {
            equipment = new CraftEntityEquipment(this);
        }
    }

    public double getHealth() {
        return Math.min(Math.max(0, getHandle().getHealth()), getMaxHealth());
    }

    public void setHealth(double health) {
        if ((health < 0) || (health > getMaxHealth())) {
            throw new IllegalArgumentException("Health must be between 0 and " + getMaxHealth());
        }

        if (entity instanceof net.minecraft.entity.player.EntityPlayerMP && health == 0) {
            ((net.minecraft.entity.player.EntityPlayerMP) entity).onDeath(net.minecraft.util.DamageSource.generic);
        }

        getHandle().setHealth((float) health);
    }

    public double getMaxHealth() {
        return getHandle().getMaxHealth();
    }

    public void setMaxHealth(double amount) {
        Validate.isTrue(amount > 0, "Max health must be greater than 0");

        getHandle().getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.maxHealth).setAttribute(amount);

        if (getHealth() > amount) {
            setHealth(amount);
        }
    }

    public void resetMaxHealth() {
        setMaxHealth(getHandle().getMaxHealth());
    }

    @Deprecated
    public Egg throwEgg() {
        return launchProjectile(Egg.class);
    }

    @Deprecated
    public Snowball throwSnowball() {
        return launchProjectile(Snowball.class);
    }

    public double getEyeHeight() {
        return getHandle().getEyeHeight();
    }

    public double getEyeHeight(boolean ignoreSneaking) {
        return getEyeHeight();
    }

    private List<Block> getLineOfSight(HashSet<Byte> transparent, int maxDistance, int maxLength) {
        if (maxDistance > 120) {
            maxDistance = 120;
        }
        ArrayList<Block> blocks = new ArrayList<Block>();
        Iterator<Block> itr = new BlockIterator(this, maxDistance);
        while (itr.hasNext()) {
            Block block = itr.next();
            blocks.add(block);
            if (maxLength != 0 && blocks.size() > maxLength) {
                blocks.remove(0);
            }
            int id = block.getTypeId();
            if (transparent == null) {
                if (id != 0) {
                    break;
                }
            } else {
                if (!transparent.contains((byte) id)) {
                    break;
                }
            }
        }
        return blocks;
    }

    public List<Block> getLineOfSight(HashSet<Byte> transparent, int maxDistance) {
        return getLineOfSight(transparent, maxDistance, 0);
    }

    public Block getTargetBlock(HashSet<Byte> transparent, int maxDistance) {
        List<Block> blocks = getLineOfSight(transparent, maxDistance, 1);
        return blocks.get(0);
    }

    public List<Block> getLastTwoTargetBlocks(HashSet<Byte> transparent, int maxDistance) {
        return getLineOfSight(transparent, maxDistance, 2);
    }

    @Deprecated
    public Arrow shootArrow() {
        return launchProjectile(Arrow.class);
    }

    public int getRemainingAir() {
        return getHandle().getAir();
    }

    public void setRemainingAir(int ticks) {
        getHandle().setAir(ticks);
    }

    public int getMaximumAir() {
        return getHandle().maxAirTicks;
    }

    public void setMaximumAir(int ticks) {
        getHandle().maxAirTicks = ticks;
    }

    public void damage(double amount) {
        damage(amount, null);
    }

    public void damage(double amount, org.bukkit.entity.Entity source) {
        net.minecraft.util.DamageSource reason = net.minecraft.util.DamageSource.generic;

        if (source instanceof HumanEntity) {
            reason = net.minecraft.util.DamageSource.causePlayerDamage(((CraftHumanEntity) source).getHandle());
        } else if (source instanceof LivingEntity) {
            reason = net.minecraft.util.DamageSource.causeMobDamage(((CraftLivingEntity) source).getHandle());
        }

        if (entity instanceof net.minecraft.entity.boss.EntityDragon) {
            ((net.minecraft.entity.boss.EntityDragon) entity).func_82195_e(reason, (float) amount);
        } else {
            entity.attackEntityFrom(reason, (float) amount);
        }
    }

    public Location getEyeLocation() {
        Location loc = getLocation();
        loc.setY(loc.getY() + getEyeHeight());
        return loc;
    }

    public int getMaximumNoDamageTicks() {
        return getHandle().maxHurtResistantTime;
    }

    public void setMaximumNoDamageTicks(int ticks) {
        getHandle().maxHurtResistantTime = ticks;
    }

    public double getLastDamage() {
        return getHandle().lastDamage;
    }

    public void setLastDamage(double damage) {
        getHandle().lastDamage = (float) damage;
    }

    public int getNoDamageTicks() {
        return getHandle().hurtResistantTime;
    }

    public void setNoDamageTicks(int ticks) {
        getHandle().hurtResistantTime = ticks;
    }

    @Override
    public net.minecraft.entity.EntityLivingBase getHandle() {
        return (net.minecraft.entity.EntityLivingBase) entity;
    }

    public void setHandle(final net.minecraft.entity.EntityLivingBase entity) {
        super.setHandle(entity);
    }

    @Override
    public String toString() {
        return "CraftLivingEntity{" + "id=" + getEntityId() + '}';
    }

    public Player getKiller() {
        return getHandle().attackingPlayer == null ? null : (Player) getHandle().attackingPlayer.getBukkitEntity();
    }

    public boolean addPotionEffect(PotionEffect effect) {
        return addPotionEffect(effect, false);
    }

    public boolean addPotionEffect(PotionEffect effect, boolean force) {
        if (hasPotionEffect(effect.getType())) {
            if (!force) {
                return false;
            }
            removePotionEffect(effect.getType());
        }
        getHandle().addPotionEffect(new net.minecraft.potion.PotionEffect(effect.getType().getId(), effect.getDuration(), effect.getAmplifier(), effect.isAmbient()));
        return true;
    }

    public boolean addPotionEffects(Collection<PotionEffect> effects) {
        boolean success = true;
        for (PotionEffect effect : effects) {
            success &= addPotionEffect(effect);
        }
        return success;
    }

    public boolean hasPotionEffect(PotionEffectType type) {
        return getHandle().isPotionActive(net.minecraft.potion.Potion.potionTypes[type.getId()]);
    }

    public void removePotionEffect(PotionEffectType type) {
        getHandle().removePotionEffect(type.getId()); // Should be removeEffect.
    }

    public Collection<PotionEffect> getActivePotionEffects() {
        List<PotionEffect> effects = new ArrayList<PotionEffect>();
        for (Object raw : getHandle().activePotionsMap.values()) {
            if (!(raw instanceof net.minecraft.potion.PotionEffect))
                continue;
            net.minecraft.potion.PotionEffect handle = (net.minecraft.potion.PotionEffect) raw;
            effects.add(new PotionEffect(PotionEffectType.getById(handle.getPotionID()), handle.getDuration(), handle.getAmplifier(), handle.getIsAmbient()));
        }
        return effects;
    }

    @SuppressWarnings("unchecked")
    public <T extends Projectile> T launchProjectile(Class<? extends T> projectile) {
        net.minecraft.world.World world = ((CraftWorld) getWorld()).getHandle();
        net.minecraft.entity.Entity launch = null;

        if (Snowball.class.isAssignableFrom(projectile)) {
            launch = new net.minecraft.entity.projectile.EntitySnowball(world, getHandle());
        } else if (Egg.class.isAssignableFrom(projectile)) {
            launch = new net.minecraft.entity.projectile.EntityEgg(world, getHandle());
        } else if (EnderPearl.class.isAssignableFrom(projectile)) {
            launch = new net.minecraft.entity.item.EntityEnderPearl(world, getHandle());
        } else if (Arrow.class.isAssignableFrom(projectile)) {
            launch = new net.minecraft.entity.projectile.EntityArrow(world, getHandle(), 1);
        } else if (ThrownPotion.class.isAssignableFrom(projectile)) {
            launch = new net.minecraft.entity.projectile.EntityPotion(world, getHandle(), CraftItemStack.asNMSCopy(new ItemStack(Material.POTION, 1)));
        } else if (Fireball.class.isAssignableFrom(projectile)) {
            Location location = getEyeLocation();
            Vector direction = location.getDirection().multiply(10);

            if (SmallFireball.class.isAssignableFrom(projectile)) {
                launch = new net.minecraft.entity.projectile.EntitySmallFireball(world, getHandle(), direction.getX(), direction.getY(), direction.getZ());
            } else if (WitherSkull.class.isAssignableFrom(projectile)) {
                launch = new net.minecraft.entity.projectile.EntityWitherSkull(world, getHandle(), direction.getX(), direction.getY(), direction.getZ());
            } else {
                launch = new net.minecraft.entity.projectile.EntityLargeFireball(world, getHandle(), direction.getX(), direction.getY(), direction.getZ());
            }

            launch.setLocationAndAngles(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        }

        Validate.notNull(launch, "Projectile not supported");

        world.spawnEntityInWorld(launch);
        return (T) launch.getBukkitEntity();
    }

    public EntityType getType() {
        return EntityType.UNKNOWN;
    }

    public boolean hasLineOfSight(Entity other) {
        return getHandle().canEntityBeSeen(((CraftEntity) other).getHandle());
    }

    public boolean getRemoveWhenFarAway() {
        return getHandle() instanceof net.minecraft.entity.EntityLiving && !((net.minecraft.entity.EntityLiving) getHandle()).persistenceRequired;
    }

    public void setRemoveWhenFarAway(boolean remove) {
        if (getHandle() instanceof net.minecraft.entity.EntityLiving) {
            ((net.minecraft.entity.EntityLiving) getHandle()).persistenceRequired = !remove;
        }
    }

    public EntityEquipment getEquipment() {
        return equipment;
    }

    public void setCanPickupItems(boolean pickup) {
        if (getHandle() instanceof net.minecraft.entity.EntityLiving) {
            ((net.minecraft.entity.EntityLiving) getHandle()).canPickUpLoot = pickup;
        }
    }

    public boolean getCanPickupItems() {
        return getHandle() instanceof net.minecraft.entity.EntityLiving && ((net.minecraft.entity.EntityLiving) getHandle()).canPickUpLoot;
    }

    @Override
    public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause cause) {
        if (getHealth() == 0) {
            return false;
        }

        return super.teleport(location, cause);
    }

    public void setCustomName(String name) {
        if (!(getHandle() instanceof net.minecraft.entity.EntityLiving)) {
            return;
        }

        if (name == null) {
            name = "";
        }

        // Names cannot be more than 64 characters due to DataWatcher limitations
        if (name.length() > 64) {
            name = name.substring(0, 64);
        }

        ((net.minecraft.entity.EntityLiving) getHandle()).setCustomNameTag(name);
    }

    public String getCustomName() {
        if (!(getHandle() instanceof net.minecraft.entity.EntityLiving)) {
            return null;
        }

        String name = ((net.minecraft.entity.EntityLiving) getHandle()).getCustomNameTag();

        if (name == null || name.length() == 0) {
            return null;
        }

        return name;
    }

    public void setCustomNameVisible(boolean flag) {
        if (getHandle() instanceof net.minecraft.entity.EntityLiving) {
            ((net.minecraft.entity.EntityLiving) getHandle()).setAlwaysRenderNameTag(flag);
        }
    }

    public boolean isCustomNameVisible() {
        return getHandle() instanceof net.minecraft.entity.EntityLiving && ((net.minecraft.entity.EntityLiving) getHandle()).getAlwaysRenderNameTag();
    }

    public boolean isLeashed() {
        if (!(getHandle() instanceof net.minecraft.entity.EntityLiving)) {
            return false;
        }
        return ((net.minecraft.entity.EntityLiving) getHandle()).getLeashedToEntity() != null;
    }

    public Entity getLeashHolder() throws IllegalStateException {
        if (!isLeashed()) {
            throw new IllegalStateException("Entity not leashed");
        }
        return ((net.minecraft.entity.EntityLiving) getHandle()).getLeashedToEntity().getBukkitEntity();
    }

    private boolean unleash() {
        if (!isLeashed()) {
            return false;
        }
        ((net.minecraft.entity.EntityLiving) getHandle()).clearLeashed(true, false);
        return true;
    }

    public boolean setLeashHolder(Entity holder) {
        if ((getHandle() instanceof net.minecraft.entity.boss.EntityWither) || !(getHandle() instanceof net.minecraft.entity.EntityLiving)) {
            return false;
        }

        if (holder == null) {
            return unleash();
        }

        if (holder.isDead()) {
            return false;
        }

        unleash();
        ((net.minecraft.entity.EntityLiving) getHandle()).setLeashedToEntity(((CraftEntity) holder).getHandle(), true);
        return true;
    }

    @Deprecated
    public int _INVALID_getLastDamage() {
        return NumberConversions.ceil(getLastDamage());
    }

    @Deprecated
    public void _INVALID_setLastDamage(int damage) {
        setLastDamage(damage);
    }

    @Deprecated
    public void _INVALID_damage(int amount) {
        damage(amount);
    }

    @Deprecated
    public void _INVALID_damage(int amount, Entity source) {
        damage(amount, source);
    }

    @Deprecated
    public int _INVALID_getHealth() {
        return NumberConversions.ceil(getHealth());
    }

    @Deprecated
    public void _INVALID_setHealth(int health) {
        setHealth(health);
    }

    @Deprecated
    public int _INVALID_getMaxHealth() {
        return NumberConversions.ceil(getMaxHealth());
    }

    @Deprecated
    public void _INVALID_setMaxHealth(int health) {
        setMaxHealth(health);
    }
}
