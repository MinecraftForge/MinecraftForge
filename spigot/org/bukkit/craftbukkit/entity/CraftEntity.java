package org.bukkit.craftbukkit.entity;

import java.util.List;
import java.util.UUID;


import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

// MCPC+ start
import java.util.Map;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.EnumHelper;
import za.co.mcportcentral.entity.*;
// MCPC+ end

public abstract class CraftEntity implements org.bukkit.entity.Entity {
    protected final CraftServer server;
    protected net.minecraft.entity.Entity entity;
    private EntityDamageEvent lastDamageEvent;

    public CraftEntity(final CraftServer server, final net.minecraft.entity.Entity entity) {
        this.server = server;
        this.entity = entity;
    }

    public static CraftEntity getEntity(CraftServer server, net.minecraft.entity.Entity entity) {
        /**
         * Order is *EXTREMELY* important -- keep it right! =D
         */
        if (entity instanceof net.minecraft.entity.EntityLivingBase) {
            // Players
            if (entity instanceof net.minecraft.entity.player.EntityPlayer) {
                if (entity instanceof net.minecraft.entity.player.EntityPlayerMP) { return new CraftPlayer(server, (net.minecraft.entity.player.EntityPlayerMP) entity); }
                // MCPC+ start - support fake player classes from mods
                // This case is never hit in vanilla
                //else { return new CraftHumanEntity(server, (net.minecraft.entity.player.EntityPlayer) entity); }
                else {
                    return new CraftFakePlayer(server, CraftFakePlayer.get(entity.worldObj, (net.minecraft.entity.player.EntityPlayer)entity));
                }
                // MCPC+ end
            }
            else if (entity instanceof net.minecraft.entity.EntityCreature) {
                // Animals
                if (entity instanceof net.minecraft.entity.passive.EntityAnimal) {
                    if (entity instanceof net.minecraft.entity.passive.EntityChicken) { return new CraftChicken(server, (net.minecraft.entity.passive.EntityChicken) entity); }
                    else if (entity instanceof net.minecraft.entity.passive.EntityCow) {
                        if (entity instanceof net.minecraft.entity.passive.EntityMooshroom) { return new CraftMushroomCow(server, (net.minecraft.entity.passive.EntityMooshroom) entity); }
                        else { return new CraftCow(server, (net.minecraft.entity.passive.EntityCow) entity); }
                    }
                    else if (entity instanceof net.minecraft.entity.passive.EntityPig) { return new CraftPig(server, (net.minecraft.entity.passive.EntityPig) entity); }
                    else if (entity instanceof net.minecraft.entity.passive.EntityTameable) {
                        if (entity instanceof net.minecraft.entity.passive.EntityWolf) { return new CraftWolf(server, (net.minecraft.entity.passive.EntityWolf) entity); }
                        else if (entity instanceof net.minecraft.entity.passive.EntityOcelot) { return new CraftOcelot(server, (net.minecraft.entity.passive.EntityOcelot) entity); } // MCPC+
                        else { return new CraftCustomTameableAnimal(server, (net.minecraft.entity.passive.EntityTameable) entity); } // MCPC+
                    }
                    else if (entity instanceof net.minecraft.entity.passive.EntitySheep) { return new CraftSheep(server, (net.minecraft.entity.passive.EntitySheep) entity); }
                    else if (entity instanceof net.minecraft.entity.passive.EntityHorse) { return new CraftHorse(server, (net.minecraft.entity.passive.EntityHorse) entity); }
                    else { return new CraftCustomAnimal(server, (net.minecraft.entity.passive.EntityAnimal) entity); } // MCPC+
                }
                // Monsters
                else if (entity instanceof net.minecraft.entity.monster.EntityMob) {
                    if (entity instanceof net.minecraft.entity.monster.EntityZombie) {
                        if (entity instanceof net.minecraft.entity.monster.EntityPigZombie) { return new CraftPigZombie(server, (net.minecraft.entity.monster.EntityPigZombie) entity); }
                        else { return new CraftZombie(server, (net.minecraft.entity.monster.EntityZombie) entity); }
                    }
                    else if (entity instanceof net.minecraft.entity.monster.EntityCreeper) { return new CraftCreeper(server, (net.minecraft.entity.monster.EntityCreeper) entity); }
                    else if (entity instanceof net.minecraft.entity.monster.EntityEnderman) { return new CraftEnderman(server, (net.minecraft.entity.monster.EntityEnderman) entity); }
                    else if (entity instanceof net.minecraft.entity.monster.EntitySilverfish) { return new CraftSilverfish(server, (net.minecraft.entity.monster.EntitySilverfish) entity); }
                    else if (entity instanceof net.minecraft.entity.monster.EntityGiantZombie) { return new CraftGiant(server, (net.minecraft.entity.monster.EntityGiantZombie) entity); }
                    else if (entity instanceof net.minecraft.entity.monster.EntitySkeleton) { return new CraftSkeleton(server, (net.minecraft.entity.monster.EntitySkeleton) entity); }
                    else if (entity instanceof net.minecraft.entity.monster.EntityBlaze) { return new CraftBlaze(server, (net.minecraft.entity.monster.EntityBlaze) entity); }
                    else if (entity instanceof net.minecraft.entity.monster.EntityWitch) { return new CraftWitch(server, (net.minecraft.entity.monster.EntityWitch) entity); }
                    else if (entity instanceof net.minecraft.entity.boss.EntityWither) { return new CraftWither(server, (net.minecraft.entity.boss.EntityWither) entity); }
                    else if (entity instanceof net.minecraft.entity.monster.EntitySpider) {
                        if (entity instanceof net.minecraft.entity.monster.EntityCaveSpider) { return new CraftCaveSpider(server, (net.minecraft.entity.monster.EntityCaveSpider) entity); }
                        else { return new CraftSpider(server, (net.minecraft.entity.monster.EntitySpider) entity); }
                    }

                    else  { return new CraftCustomMonster(server, (net.minecraft.entity.monster.EntityMob) entity); } // MCPC+
                }
                // Water Animals
                else if (entity instanceof net.minecraft.entity.passive.EntityWaterMob) {
                    if (entity instanceof net.minecraft.entity.passive.EntitySquid) { return new CraftSquid(server, (net.minecraft.entity.passive.EntitySquid) entity); }
                    else { return new CraftCustomWaterMob(server, (net.minecraft.entity.passive.EntityWaterMob) entity); } // MCPC+
                }
                else if (entity instanceof net.minecraft.entity.monster.EntityGolem) {
                    if (entity instanceof net.minecraft.entity.monster.EntitySnowman) { return new CraftSnowman(server, (net.minecraft.entity.monster.EntitySnowman) entity); }
                    else if (entity instanceof net.minecraft.entity.monster.EntityIronGolem) { return new CraftIronGolem(server, (net.minecraft.entity.monster.EntityIronGolem) entity); }
                    else { return new CraftCustomLivingEntity(server, (net.minecraft.entity.EntityLiving) entity); } // MCPC+
                }
                else if (entity instanceof net.minecraft.entity.passive.EntityVillager) { return new CraftVillager(server, (net.minecraft.entity.passive.EntityVillager) entity); }
                else { return new CraftCustomCreature(server, (net.minecraft.entity.EntityCreature) entity); } // MCPC+
            }
            // Slimes are a special (and broken) case
            else if (entity instanceof net.minecraft.entity.monster.EntitySlime) {
                if (entity instanceof net.minecraft.entity.monster.EntityMagmaCube) { return new CraftMagmaCube(server, (net.minecraft.entity.monster.EntityMagmaCube) entity); }
                else { return new CraftSlime(server, (net.minecraft.entity.monster.EntitySlime) entity); }
            }
            // Flying
            else if (entity instanceof net.minecraft.entity.EntityFlying) {
                if (entity instanceof net.minecraft.entity.monster.EntityGhast) { return new CraftGhast(server, (net.minecraft.entity.monster.EntityGhast) entity); }
                else { return new CraftFlying(server, (net.minecraft.entity.EntityFlying) entity); }
            }
            else if (entity instanceof net.minecraft.entity.boss.EntityDragon) {
                return new CraftEnderDragon(server, (net.minecraft.entity.boss.EntityDragon) entity);
            }
            // Ambient
            else if (entity instanceof net.minecraft.entity.passive.EntityAmbientCreature) {
                if (entity instanceof net.minecraft.entity.passive.EntityBat) { return new CraftBat(server, (net.minecraft.entity.passive.EntityBat) entity); }
                else { return new CraftCustomAmbient(server, (net.minecraft.entity.passive.EntityAmbientCreature) entity); } // MCPC+
            }
            else  { return new CraftCustomLivingEntity(server, (net.minecraft.entity.EntityLiving) entity); } // MCPC+
        }
        else if (entity instanceof net.minecraft.entity.boss.EntityDragonPart) {
            net.minecraft.entity.boss.EntityDragonPart part = (net.minecraft.entity.boss.EntityDragonPart) entity;
            if (part.entityDragonObj instanceof net.minecraft.entity.boss.EntityDragon) { return new CraftEnderDragonPart(server, (net.minecraft.entity.boss.EntityDragonPart) entity); }
            else { return new CraftComplexPart(server, (net.minecraft.entity.boss.EntityDragonPart) entity); }
        }
        else if (entity instanceof net.minecraft.entity.item.EntityXPOrb) { return new CraftExperienceOrb(server, (net.minecraft.entity.item.EntityXPOrb) entity); }
        else if (entity instanceof net.minecraft.entity.projectile.EntityArrow) { return new CraftArrow(server, (net.minecraft.entity.projectile.EntityArrow) entity); }
        else if (entity instanceof net.minecraft.entity.item.EntityBoat) { return new CraftBoat(server, (net.minecraft.entity.item.EntityBoat) entity); }
        else if (entity instanceof net.minecraft.entity.projectile.EntityThrowable) {
            if (entity instanceof net.minecraft.entity.projectile.EntityEgg) { return new CraftEgg(server, (net.minecraft.entity.projectile.EntityEgg) entity); }
            else if (entity instanceof net.minecraft.entity.projectile.EntitySnowball) { return new CraftSnowball(server, (net.minecraft.entity.projectile.EntitySnowball) entity); }
            else if (entity instanceof net.minecraft.entity.projectile.EntityPotion) { return new CraftThrownPotion(server, (net.minecraft.entity.projectile.EntityPotion) entity); }
            else if (entity instanceof net.minecraft.entity.item.EntityEnderPearl) { return new CraftEnderPearl(server, (net.minecraft.entity.item.EntityEnderPearl) entity); }
            else if (entity instanceof net.minecraft.entity.item.EntityExpBottle) { return new CraftThrownExpBottle(server, (net.minecraft.entity.item.EntityExpBottle) entity); }
            else { return new CraftProjectile(server, (net.minecraft.entity.projectile.EntityThrowable) entity); } // MCPC
        }
        else if (entity instanceof net.minecraft.entity.item.EntityFallingSand) { return new CraftFallingSand(server, (net.minecraft.entity.item.EntityFallingSand) entity); }
        else if (entity instanceof net.minecraft.entity.projectile.EntityFireball) {
            if (entity instanceof net.minecraft.entity.projectile.EntitySmallFireball) { return new CraftSmallFireball(server, (net.minecraft.entity.projectile.EntitySmallFireball) entity); }
            else if (entity instanceof net.minecraft.entity.projectile.EntityLargeFireball) { return new CraftLargeFireball(server, (net.minecraft.entity.projectile.EntityLargeFireball) entity); }
            else if (entity instanceof net.minecraft.entity.projectile.EntityWitherSkull) { return new CraftWitherSkull(server, (net.minecraft.entity.projectile.EntityWitherSkull) entity); }
            else { return new CraftFireball(server, (net.minecraft.entity.projectile.EntityFireball) entity); }
        }
        else if (entity instanceof net.minecraft.entity.item.EntityEnderEye) { return new CraftEnderSignal(server, (net.minecraft.entity.item.EntityEnderEye) entity); }
        else if (entity instanceof net.minecraft.entity.item.EntityEnderCrystal) { return new CraftEnderCrystal(server, (net.minecraft.entity.item.EntityEnderCrystal) entity); }
        else if (entity instanceof net.minecraft.entity.projectile.EntityFishHook) { return new CraftFish(server, (net.minecraft.entity.projectile.EntityFishHook) entity); }
        else if (entity instanceof net.minecraft.entity.item.EntityItem) { return new CraftItem(server, (net.minecraft.entity.item.EntityItem) entity); }
        else if (entity instanceof net.minecraft.entity.effect.EntityWeatherEffect) {
            if (entity instanceof net.minecraft.entity.effect.EntityLightningBolt) { return new CraftLightningStrike(server, (net.minecraft.entity.effect.EntityLightningBolt) entity); }
            else { return new CraftWeather(server, (net.minecraft.entity.effect.EntityWeatherEffect) entity); }
        }
        else if (entity instanceof net.minecraft.entity.item.EntityMinecart) {
            if (entity instanceof net.minecraft.entity.item.EntityMinecartFurnace) { return new CraftMinecartFurnace(server, (net.minecraft.entity.item.EntityMinecartFurnace) entity); }
            else if (entity instanceof net.minecraft.entity.item.EntityMinecartChest) { return new CraftMinecartChest(server, (net.minecraft.entity.item.EntityMinecartChest) entity); }
            else if (entity instanceof net.minecraft.entity.item.EntityMinecartTNT) { return new CraftMinecartTNT(server, (net.minecraft.entity.item.EntityMinecartTNT) entity); }
            else if (entity instanceof net.minecraft.entity.item.EntityMinecartHopper) { return new CraftMinecartHopper(server, (net.minecraft.entity.item.EntityMinecartHopper) entity); }
            else if (entity instanceof net.minecraft.entity.ai.EntityMinecartMobSpawner) { return new CraftMinecartMobSpawner(server, (net.minecraft.entity.ai.EntityMinecartMobSpawner) entity); }
            else if (entity instanceof net.minecraft.entity.item.EntityMinecartEmpty) { return new CraftMinecartRideable(server, (net.minecraft.entity.item.EntityMinecartEmpty) entity); }
            else { return new CraftMinecart(server, (net.minecraft.entity.item.EntityMinecart) entity); } // MCPC+ - other minecarts (Steve's Carts)
        } else if (entity instanceof net.minecraft.entity.EntityHanging) {
            if (entity instanceof net.minecraft.entity.item.EntityPainting) { return new CraftPainting(server, (net.minecraft.entity.item.EntityPainting) entity); }
            else if (entity instanceof net.minecraft.entity.item.EntityItemFrame) { return new CraftItemFrame(server, (net.minecraft.entity.item.EntityItemFrame) entity); }
            else if (entity instanceof net.minecraft.entity.EntityLeashKnot) { return new CraftLeash(server, (net.minecraft.entity.EntityLeashKnot) entity); }
            else { return new CraftHanging(server, (net.minecraft.entity.EntityHanging) entity); }
        }
        else if (entity instanceof net.minecraft.entity.item.EntityTNTPrimed) { return new CraftTNTPrimed(server, (net.minecraft.entity.item.EntityTNTPrimed) entity); }
        else if (entity instanceof net.minecraft.entity.item.EntityFireworkRocket) { return new CraftFirework(server, (net.minecraft.entity.item.EntityFireworkRocket) entity); }
        // MCPC+ - used for custom entities that extend Entity directly
        else if (entity instanceof net.minecraft.entity.Entity) { return new CraftCustomEntity(server, (net.minecraft.entity.Entity) entity); }

        throw new AssertionError("Unknown entity " + entity == null ? null : entity.getClass() + ": " + entity); // MCPC - show the entity that caused exception
    }

    // MCPC+ start - copy of getEntity() but operates on classes instead of instances, for EntityRegistry registerBukkitType
    public static Class<? extends org.bukkit.entity.Entity> getEntityClass(Class<? extends net.minecraft.entity.Entity> nmsClass) {
        /**
         * Order is *EXTREMELY* important -- keep it right! =D
         */
        // pbpaste|perl -pe's/entity instanceof ([\w.]+)/$1.class.isAssignableFrom(nmsClass)/g'|perl -pe's/return new (\w+)([^;\n]+)/return $1.class/g'
        if (net.minecraft.entity.EntityLiving.class.isAssignableFrom(nmsClass)) {
            // Players
            if (net.minecraft.entity.player.EntityPlayer.class.isAssignableFrom(nmsClass)) {
                if (net.minecraft.entity.player.EntityPlayerMP.class.isAssignableFrom(nmsClass)) { return CraftPlayer.class; }
                // MCPC+ start - support fake player classes from mods
                // This case is never hit in vanilla
                //else { return CraftHumanEntity.class; }
                else {
                    return CraftFakePlayer.class;
                }
                // MCPC+ end
            }
            else if (net.minecraft.entity.EntityCreature.class.isAssignableFrom(nmsClass)) {
                // Animals
                if (net.minecraft.entity.passive.EntityAnimal.class.isAssignableFrom(nmsClass)) {
                    if (net.minecraft.entity.passive.EntityChicken.class.isAssignableFrom(nmsClass)) { return CraftChicken.class; }
                    else if (net.minecraft.entity.passive.EntityCow.class.isAssignableFrom(nmsClass)) {
                        if (net.minecraft.entity.passive.EntityMooshroom.class.isAssignableFrom(nmsClass)) { return CraftMushroomCow.class; }
                        else { return CraftCow.class; }
                    }
                    else if (net.minecraft.entity.passive.EntityPig.class.isAssignableFrom(nmsClass)) { return CraftPig.class; }
                    else if (net.minecraft.entity.passive.EntityTameable.class.isAssignableFrom(nmsClass)) {
                        if (net.minecraft.entity.passive.EntityWolf.class.isAssignableFrom(nmsClass)) { return CraftWolf.class; }
                        else if (net.minecraft.entity.passive.EntityOcelot.class.isAssignableFrom(nmsClass)) { return CraftOcelot.class; } // MCPC+
                        else { return CraftCustomTameableAnimal.class; } // MCPC+
                    }
                    else if (net.minecraft.entity.passive.EntitySheep.class.isAssignableFrom(nmsClass)) { return CraftSheep.class; }
                    else if (net.minecraft.entity.passive.EntityHorse.class.isAssignableFrom(nmsClass)) { return CraftHorse.class; }
                    else { return CraftCustomAnimal.class; } // MCPC+
                }
                // Monsters
                else if (net.minecraft.entity.monster.EntityMob.class.isAssignableFrom(nmsClass)) {
                    if (net.minecraft.entity.monster.EntityZombie.class.isAssignableFrom(nmsClass)) {
                        if (net.minecraft.entity.monster.EntityPigZombie.class.isAssignableFrom(nmsClass)) { return CraftPigZombie.class; }
                        else { return CraftZombie.class; }
                    }
                    else if (net.minecraft.entity.monster.EntityCreeper.class.isAssignableFrom(nmsClass)) { return CraftCreeper.class; }
                    else if (net.minecraft.entity.monster.EntityEnderman.class.isAssignableFrom(nmsClass)) { return CraftEnderman.class; }
                    else if (net.minecraft.entity.monster.EntitySilverfish.class.isAssignableFrom(nmsClass)) { return CraftSilverfish.class; }
                    else if (net.minecraft.entity.monster.EntityGiantZombie.class.isAssignableFrom(nmsClass)) { return CraftGiant.class; }
                    else if (net.minecraft.entity.monster.EntitySkeleton.class.isAssignableFrom(nmsClass)) { return CraftSkeleton.class; }
                    else if (net.minecraft.entity.monster.EntityBlaze.class.isAssignableFrom(nmsClass)) { return CraftBlaze.class; }
                    else if (net.minecraft.entity.monster.EntityWitch.class.isAssignableFrom(nmsClass)) { return CraftWitch.class; }
                    else if (net.minecraft.entity.boss.EntityWither.class.isAssignableFrom(nmsClass)) { return CraftWither.class; }
                    else if (net.minecraft.entity.monster.EntitySpider.class.isAssignableFrom(nmsClass)) {
                        if (net.minecraft.entity.monster.EntityCaveSpider.class.isAssignableFrom(nmsClass)) { return CraftCaveSpider.class; }
                        else { return CraftSpider.class; }
                    }

                    else  { return CraftCustomMonster.class; } // MCPC+
                }
                // Water Animals
                else if (net.minecraft.entity.passive.EntityWaterMob.class.isAssignableFrom(nmsClass)) {
                    if (net.minecraft.entity.passive.EntitySquid.class.isAssignableFrom(nmsClass)) { return CraftSquid.class; }
                    else { return CraftCustomWaterMob.class; } // MCPC+
                }
                else if (net.minecraft.entity.monster.EntityGolem.class.isAssignableFrom(nmsClass)) {
                    if (net.minecraft.entity.monster.EntitySnowman.class.isAssignableFrom(nmsClass)) { return CraftSnowman.class; }
                    else if (net.minecraft.entity.monster.EntityIronGolem.class.isAssignableFrom(nmsClass)) { return CraftIronGolem.class; }
                    else { return CraftCustomLivingEntity.class; } // MCPC+
                }
                else if (net.minecraft.entity.passive.EntityVillager.class.isAssignableFrom(nmsClass)) { return CraftVillager.class; }
                else { return CraftCustomCreature.class; } // MCPC+
            }
            // Slimes are a special (and broken) case
            else if (net.minecraft.entity.monster.EntitySlime.class.isAssignableFrom(nmsClass)) {
                if (net.minecraft.entity.monster.EntityMagmaCube.class.isAssignableFrom(nmsClass)) { return CraftMagmaCube.class; }
                else { return CraftSlime.class; }
            }
            // Flying
            else if (net.minecraft.entity.EntityFlying.class.isAssignableFrom(nmsClass)) {
                if (net.minecraft.entity.monster.EntityGhast.class.isAssignableFrom(nmsClass)) { return CraftGhast.class; }
                else { return CraftFlying.class; }
            }
            else if (net.minecraft.entity.boss.EntityDragon.class.isAssignableFrom(nmsClass)) {
                return CraftEnderDragon.class;
            }
            // Ambient
            else if (net.minecraft.entity.passive.EntityAmbientCreature.class.isAssignableFrom(nmsClass)) {
                if (net.minecraft.entity.passive.EntityBat.class.isAssignableFrom(nmsClass)) { return CraftBat.class; }
                else { return CraftCustomAmbient.class; } // MCPC+
            }
            else  { return CraftCustomLivingEntity.class; } // MCPC+
        }
        else if (net.minecraft.entity.boss.EntityDragonPart.class.isAssignableFrom(nmsClass)) {
            /* MCPC+ - no instance, best we can say is this is a CraftComplexPart
            net.minecraft.entity.boss.EntityDragonPart part = (net.minecraft.entity.boss.EntityDragonPart) entity;
            if (part.entityDragonObj instanceof net.minecraft.entity.boss.EntityDragon) { return CraftEnderDragonPart.class; }
            else { return CraftComplexPart.class; }
            */
            return CraftComplexPart.class;
        }
        else if (net.minecraft.entity.item.EntityXPOrb.class.isAssignableFrom(nmsClass)) { return CraftExperienceOrb.class; }
        else if (net.minecraft.entity.projectile.EntityArrow.class.isAssignableFrom(nmsClass)) { return CraftArrow.class; }
        else if (net.minecraft.entity.item.EntityBoat.class.isAssignableFrom(nmsClass)) { return CraftBoat.class; }
        else if (net.minecraft.entity.projectile.EntityThrowable.class.isAssignableFrom(nmsClass)) {
            if (net.minecraft.entity.projectile.EntityEgg.class.isAssignableFrom(nmsClass)) { return CraftEgg.class; }
            else if (net.minecraft.entity.projectile.EntitySnowball.class.isAssignableFrom(nmsClass)) { return CraftSnowball.class; }
            else if (net.minecraft.entity.projectile.EntityPotion.class.isAssignableFrom(nmsClass)) { return CraftThrownPotion.class; }
            else if (net.minecraft.entity.item.EntityEnderPearl.class.isAssignableFrom(nmsClass)) { return CraftEnderPearl.class; }
            else if (net.minecraft.entity.item.EntityExpBottle.class.isAssignableFrom(nmsClass)) { return CraftThrownExpBottle.class; }
            else { return CraftProjectile.class; } // MCPC
        }
        else if (net.minecraft.entity.item.EntityFallingSand.class.isAssignableFrom(nmsClass)) { return CraftFallingSand.class; }
        else if (net.minecraft.entity.projectile.EntityFireball.class.isAssignableFrom(nmsClass)) {
            if (net.minecraft.entity.projectile.EntitySmallFireball.class.isAssignableFrom(nmsClass)) { return CraftSmallFireball.class; }
            else if (net.minecraft.entity.projectile.EntityLargeFireball.class.isAssignableFrom(nmsClass)) { return CraftLargeFireball.class; }
            else if (net.minecraft.entity.projectile.EntityWitherSkull.class.isAssignableFrom(nmsClass)) { return CraftWitherSkull.class; }
            else { return CraftFireball.class; }
        }
        else if (net.minecraft.entity.item.EntityEnderEye.class.isAssignableFrom(nmsClass)) { return CraftEnderSignal.class; }
        else if (net.minecraft.entity.item.EntityEnderCrystal.class.isAssignableFrom(nmsClass)) { return CraftEnderCrystal.class; }
        else if (net.minecraft.entity.projectile.EntityFishHook.class.isAssignableFrom(nmsClass)) { return CraftFish.class; }
        else if (net.minecraft.entity.item.EntityItem.class.isAssignableFrom(nmsClass)) { return CraftItem.class; }
        else if (net.minecraft.entity.effect.EntityWeatherEffect.class.isAssignableFrom(nmsClass)) {
            if (net.minecraft.entity.effect.EntityLightningBolt.class.isAssignableFrom(nmsClass)) { return CraftLightningStrike.class; }
            else { return CraftWeather.class; }
        }
        else if (net.minecraft.entity.item.EntityMinecart.class.isAssignableFrom(nmsClass)) {
            if (net.minecraft.entity.item.EntityMinecartFurnace.class.isAssignableFrom(nmsClass)) { return CraftMinecartFurnace.class; }
            else if (net.minecraft.entity.item.EntityMinecartChest.class.isAssignableFrom(nmsClass)) { return CraftMinecartChest.class; }
            else if (net.minecraft.entity.item.EntityMinecartTNT.class.isAssignableFrom(nmsClass)) { return CraftMinecartTNT.class; }
            else if (net.minecraft.entity.item.EntityMinecartHopper.class.isAssignableFrom(nmsClass)) { return CraftMinecartHopper.class; }
            else if (net.minecraft.entity.ai.EntityMinecartMobSpawner.class.isAssignableFrom(nmsClass)) { return CraftMinecartMobSpawner.class; }
            else if (net.minecraft.entity.item.EntityMinecartEmpty.class.isAssignableFrom(nmsClass)) { return CraftMinecartRideable.class; }
            else { return CraftMinecart.class; } // MCPC+ - other minecarts (Steve's Carts)
        } else if (net.minecraft.entity.EntityHanging.class.isAssignableFrom(nmsClass)) {
            if (net.minecraft.entity.item.EntityPainting.class.isAssignableFrom(nmsClass)) { return CraftPainting.class; }
            else if (net.minecraft.entity.item.EntityItemFrame.class.isAssignableFrom(nmsClass)) { return CraftItemFrame.class; }
            else if (net.minecraft.entity.EntityLeashKnot.class.isAssignableFrom(nmsClass)) { return CraftLeash.class; }
            else { return CraftHanging.class; }
        }
        else if (net.minecraft.entity.item.EntityTNTPrimed.class.isAssignableFrom(nmsClass)) { return CraftTNTPrimed.class; }
        else if (net.minecraft.entity.item.EntityFireworkRocket.class.isAssignableFrom(nmsClass)) { return CraftFirework.class; }
        // MCPC+ - used for custom entities that extend Entity directly
        else if (net.minecraft.entity.Entity.class.isAssignableFrom(nmsClass)) { return CraftCustomEntity.class; }

        throw new AssertionError("Unknown entity class " + nmsClass == null ? null : nmsClass); // MCPC - show the entity that caused exception
    }

    // add Bukkit wrappers
    public static void initMappings() {
        for (Map.Entry<Class<? extends Entity>, String> entry : cpw.mods.fml.common.registry.EntityRegistry.entityTypeMap.entrySet()) {
            Class<? extends Entity> entityClass = entry.getKey();
            String entityName = entry.getValue();
            int entityId = getEntityTypeIDfromClass(entityClass);

            Class<? extends org.bukkit.entity.Entity> bukkitEntityClass = CraftEntity.getEntityClass(entityClass);
            EnumHelper.addBukkitEntityType(entityName, bukkitEntityClass, entityId, false);
        }
    }

    // Lookup entity id from NMS entity class
    private static int getEntityTypeIDfromClass(Class entityClass) {
        // check both maps, since mods can add to either

        Map<Class, Integer> classToIDMapping = cpw.mods.fml.relauncher.ReflectionHelper.getPrivateValue(net.minecraft.entity.EntityList.class, null, "field_75624_e", "classToIDMapping");
        if (classToIDMapping.containsKey(entityClass)) {
            return classToIDMapping.get(entityClass);
        }

        Map<Integer, Class> IDtoClassMapping = cpw.mods.fml.relauncher.ReflectionHelper.getPrivateValue(net.minecraft.entity.EntityList.class, null, "field_75623_d", "IDtoClassMapping");
        for (Map.Entry<Integer, Class> entry : IDtoClassMapping.entrySet()) {
            int entityId = entry.getKey();
            Class thisEntityClass = entry.getValue();

            if (thisEntityClass.getName().equals(entityClass.getName())) {
                return entityId;
            }
        }

        // if there is no entity ID, choose a negative integer based on the class name
        return -Math.abs(entityClass.getName().hashCode()^(entityClass.getName().hashCode()>>>16));
    }
    // MCPC+ end

    public Location getLocation() {
        return new Location(getWorld(), entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
    }

    public Location getLocation(Location loc) {
        if (loc != null) {
            loc.setWorld(getWorld());
            loc.setX(entity.posX);
            loc.setY(entity.posY);
            loc.setZ(entity.posZ);
            loc.setYaw(entity.rotationYaw);
            loc.setPitch(entity.rotationPitch);
        }

        return loc;
    }

    public Vector getVelocity() {
        return new Vector(entity.motionX, entity.motionY, entity.motionZ);
    }

    public void setVelocity(Vector vel) {
        entity.motionX = vel.getX();
        entity.motionY = vel.getY();
        entity.motionZ = vel.getZ();
        entity.velocityChanged = true;
    }

    public boolean isOnGround() {
        if (entity instanceof net.minecraft.entity.projectile.EntityArrow) {
            return ((net.minecraft.entity.projectile.EntityArrow) entity).isInGround();
        }
        return entity.onGround;
    }

    public World getWorld() {
        return entity.worldObj.getWorld();
    }

    public boolean teleport(Location location) {
        return teleport(location, TeleportCause.PLUGIN);
    }

    public boolean teleport(Location location, TeleportCause cause) {
        if (entity.ridingEntity != null || entity.riddenByEntity != null || entity.isDead) {
            return false;
        }

        entity.worldObj = ((CraftWorld) location.getWorld()).getHandle();
        entity.setPositionAndRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        // entity.setLocation() throws no event, and so cannot be cancelled
        return true;
    }

    public boolean teleport(org.bukkit.entity.Entity destination) {
        return teleport(destination.getLocation());
    }

    public boolean teleport(org.bukkit.entity.Entity destination, TeleportCause cause) {
        return teleport(destination.getLocation(), cause);
    }

    public List<org.bukkit.entity.Entity> getNearbyEntities(double x, double y, double z) {
        @SuppressWarnings("unchecked")
        List<net.minecraft.entity.Entity> notchEntityList = entity.worldObj.getEntitiesWithinAABBExcludingEntity(entity, entity.boundingBox.expand(x, y, z));
        List<org.bukkit.entity.Entity> bukkitEntityList = new java.util.ArrayList<org.bukkit.entity.Entity>(notchEntityList.size());

        for (net.minecraft.entity.Entity e : notchEntityList) {
            bukkitEntityList.add(e.getBukkitEntity());
        }
        return bukkitEntityList;
    }

    public int getEntityId() {
        return entity.entityId;
    }

    public int getFireTicks() {
        return entity.fire;
    }

    public int getMaxFireTicks() {
        return entity.fireResistance;
    }

    public void setFireTicks(int ticks) {
        entity.fire = ticks;
    }

    public void remove() {
        entity.isDead = true;
    }

    public boolean isDead() {
        return !entity.isEntityAlive();
    }

    public boolean isValid() {
        return entity.isEntityAlive() && entity.valid;
    }

    public Server getServer() {
        return server;
    }

    public Vector getMomentum() {
        return getVelocity();
    }

    public void setMomentum(Vector value) {
        setVelocity(value);
    }

    public org.bukkit.entity.Entity getPassenger() {
        return isEmpty() ? null : getHandle().riddenByEntity.getBukkitEntity();
    }

    public boolean setPassenger(org.bukkit.entity.Entity passenger) {
        if (passenger instanceof CraftEntity) {
            ((CraftEntity) passenger).getHandle().setPassengerOf(getHandle());
            return true;
        } else {
            return false;
        }
    }

    public boolean isEmpty() {
        return getHandle().riddenByEntity == null;
    }

    public boolean eject() {
        if (getHandle().riddenByEntity == null) {
            return false;
        }

        getHandle().riddenByEntity.setPassengerOf(null);
        return true;
    }

    public float getFallDistance() {
        return getHandle().fallDistance;
    }

    public void setFallDistance(float distance) {
        getHandle().fallDistance = distance;
    }

    public void setLastDamageCause(EntityDamageEvent event) {
        lastDamageEvent = event;
    }

    public EntityDamageEvent getLastDamageCause() {
        return lastDamageEvent;
    }

    public UUID getUniqueId() {
        return getHandle().entityUniqueID;
    }

    public int getTicksLived() {
        return getHandle().ticksExisted;
    }

    public void setTicksLived(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Age must be at least 1 tick");
        }
        getHandle().ticksExisted = value;
    }

    public net.minecraft.entity.Entity getHandle() {
        return entity;
    }

    public void playEffect(EntityEffect type) {
        this.getHandle().worldObj.setEntityState(getHandle(), type.getData());
    }

    public void setHandle(final net.minecraft.entity.Entity entity) {
        this.entity = entity;
    }

    @Override
    public String toString() {
        return "CraftEntity{" + "id=" + getEntityId() + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CraftEntity other = (CraftEntity) obj;
        return (this.getEntityId() == other.getEntityId());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.getEntityId();
        return hash;
    }

    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        server.getEntityMetadata().setMetadata(this, metadataKey, newMetadataValue);
    }

    public List<MetadataValue> getMetadata(String metadataKey) {
        return server.getEntityMetadata().getMetadata(this, metadataKey);
    }

    public boolean hasMetadata(String metadataKey) {
        return server.getEntityMetadata().hasMetadata(this, metadataKey);
    }

    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        server.getEntityMetadata().removeMetadata(this, metadataKey, owningPlugin);
    }

    public boolean isInsideVehicle() {
        return getHandle().ridingEntity != null;
    }

    public boolean leaveVehicle() {
        if (getHandle().ridingEntity == null) {
            return false;
        }

        getHandle().setPassengerOf(null);
        return true;
    }

    public org.bukkit.entity.Entity getVehicle() {
        if (getHandle().ridingEntity == null) {
            return null;
        }

        return getHandle().ridingEntity.getBukkitEntity();
    }
}
