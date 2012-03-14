package net.minecraft.src;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.src.forge.ForgeHooks;

public class EntityTrackerEntry
{
    /** The entity that this EntityTrackerEntry tracks. */
    public Entity trackedEntity;
    public int trackingDistanceThreshold;
    public int field_9234_e;

    /** The encoded entity X position. */
    public int encodedPosX;

    /** The encoded entity Y position. */
    public int encodedPosY;

    /** The encoded entity Z position. */
    public int encodedPosZ;

    /** The encoded entity yaw rotation. */
    public int encodedRotationYaw;

    /** The encoded entity pitch rotation. */
    public int encodedRotationPitch;
    public int field_48617_i;
    public double lastTrackedEntityMotionX;
    public double lastTrackedEntityMotionY;
    public double lastTrackedEntityMotionZ;
    public int updateCounter = 0;
    private double lastTrackedEntityPosX;
    private double lastTrackedEntityPosY;
    private double lastTrackedEntityPosZ;
    private boolean firstUpdateDone = false;
    private boolean shouldSendMotionUpdates;
    private int field_28165_t = 0;
    public boolean playerEntitiesUpdated = false;
    public Set trackedPlayers = new HashSet();

    public EntityTrackerEntry(Entity par1Entity, int par2, int par3, boolean par4)
    {
        this.trackedEntity = par1Entity;
        this.trackingDistanceThreshold = par2;
        this.field_9234_e = par3;
        this.shouldSendMotionUpdates = par4;
        this.encodedPosX = MathHelper.floor_double(par1Entity.posX * 32.0D);
        this.encodedPosY = MathHelper.floor_double(par1Entity.posY * 32.0D);
        this.encodedPosZ = MathHelper.floor_double(par1Entity.posZ * 32.0D);
        this.encodedRotationYaw = MathHelper.floor_float(par1Entity.rotationYaw * 256.0F / 360.0F);
        this.encodedRotationPitch = MathHelper.floor_float(par1Entity.rotationPitch * 256.0F / 360.0F);
        this.field_48617_i = MathHelper.floor_float(par1Entity.func_48314_aq() * 256.0F / 360.0F);
    }

    public boolean equals(Object par1Obj)
    {
        return par1Obj instanceof EntityTrackerEntry ? ((EntityTrackerEntry)par1Obj).trackedEntity.entityId == this.trackedEntity.entityId : false;
    }

    public int hashCode()
    {
        return this.trackedEntity.entityId;
    }

    public void updatePlayerList(List par1List)
    {
        this.playerEntitiesUpdated = false;

        if (!this.firstUpdateDone || this.trackedEntity.getDistanceSq(this.lastTrackedEntityPosX, this.lastTrackedEntityPosY, this.lastTrackedEntityPosZ) > 16.0D)
        {
            this.lastTrackedEntityPosX = this.trackedEntity.posX;
            this.lastTrackedEntityPosY = this.trackedEntity.posY;
            this.lastTrackedEntityPosZ = this.trackedEntity.posZ;
            this.firstUpdateDone = true;
            this.playerEntitiesUpdated = true;
            this.updatePlayerEntities(par1List);
        }

        ++this.field_28165_t;

        if (this.updateCounter++ % this.field_9234_e == 0 || this.trackedEntity.isAirBorne)
        {
            int var2 = MathHelper.floor_double(this.trackedEntity.posX * 32.0D);
            int var3 = MathHelper.floor_double(this.trackedEntity.posY * 32.0D);
            int var4 = MathHelper.floor_double(this.trackedEntity.posZ * 32.0D);
            int var5 = MathHelper.floor_float(this.trackedEntity.rotationYaw * 256.0F / 360.0F);
            int var6 = MathHelper.floor_float(this.trackedEntity.rotationPitch * 256.0F / 360.0F);
            int var7 = var2 - this.encodedPosX;
            int var8 = var3 - this.encodedPosY;
            int var9 = var4 - this.encodedPosZ;
            Object var10 = null;
            boolean var11 = Math.abs(var7) >= 4 || Math.abs(var8) >= 4 || Math.abs(var9) >= 4;
            boolean var12 = Math.abs(var5 - this.encodedRotationYaw) >= 4 || Math.abs(var6 - this.encodedRotationPitch) >= 4;

            if (var7 >= -128 && var7 < 128 && var8 >= -128 && var8 < 128 && var9 >= -128 && var9 < 128 && this.field_28165_t <= 400)
            {
                if (var11 && var12)
                {
                    var10 = new Packet33RelEntityMoveLook(this.trackedEntity.entityId, (byte)var7, (byte)var8, (byte)var9, (byte)var5, (byte)var6);
                }
                else if (var11)
                {
                    var10 = new Packet31RelEntityMove(this.trackedEntity.entityId, (byte)var7, (byte)var8, (byte)var9);
                }
                else if (var12)
                {
                    var10 = new Packet32EntityLook(this.trackedEntity.entityId, (byte)var5, (byte)var6);
                }
            }
            else
            {
                this.field_28165_t = 0;
                this.trackedEntity.posX = (double)var2 / 32.0D;
                this.trackedEntity.posY = (double)var3 / 32.0D;
                this.trackedEntity.posZ = (double)var4 / 32.0D;
                var10 = new Packet34EntityTeleport(this.trackedEntity.entityId, var2, var3, var4, (byte)var5, (byte)var6);
            }

            if (this.shouldSendMotionUpdates)
            {
                double var13 = this.trackedEntity.motionX - this.lastTrackedEntityMotionX;
                double var15 = this.trackedEntity.motionY - this.lastTrackedEntityMotionY;
                double var17 = this.trackedEntity.motionZ - this.lastTrackedEntityMotionZ;
                double var19 = 0.02D;
                double var21 = var13 * var13 + var15 * var15 + var17 * var17;

                if (var21 > var19 * var19 || var21 > 0.0D && this.trackedEntity.motionX == 0.0D && this.trackedEntity.motionY == 0.0D && this.trackedEntity.motionZ == 0.0D)
                {
                    this.lastTrackedEntityMotionX = this.trackedEntity.motionX;
                    this.lastTrackedEntityMotionY = this.trackedEntity.motionY;
                    this.lastTrackedEntityMotionZ = this.trackedEntity.motionZ;
                    this.sendPacketToTrackedPlayers(new Packet28EntityVelocity(this.trackedEntity.entityId, this.lastTrackedEntityMotionX, this.lastTrackedEntityMotionY, this.lastTrackedEntityMotionZ));
                }
            }

            if (var10 != null)
            {
                this.sendPacketToTrackedPlayers((Packet)((Packet)var10));
            }

            DataWatcher var23 = this.trackedEntity.getDataWatcher();

            if (var23.hasObjectChanged())
            {
                this.sendPacketToTrackedPlayersAndTrackedEntity(new Packet40EntityMetadata(this.trackedEntity.entityId, var23));
            }

            int var14 = MathHelper.floor_float(this.trackedEntity.func_48314_aq() * 256.0F / 360.0F);

            if (Math.abs(var14 - this.field_48617_i) >= 4)
            {
                this.sendPacketToTrackedPlayers(new Packet35EntityHeadRotation(this.trackedEntity.entityId, (byte)var14));
                this.field_48617_i = var14;
            }

            if (var11)
            {
                this.encodedPosX = var2;
                this.encodedPosY = var3;
                this.encodedPosZ = var4;
            }

            if (var12)
            {
                this.encodedRotationYaw = var5;
                this.encodedRotationPitch = var6;
            }
        }

        this.trackedEntity.isAirBorne = false;

        if (this.trackedEntity.velocityChanged)
        {
            this.sendPacketToTrackedPlayersAndTrackedEntity(new Packet28EntityVelocity(this.trackedEntity));
            this.trackedEntity.velocityChanged = false;
        }
    }

    public void sendPacketToTrackedPlayers(Packet par1Packet)
    {
        Iterator var3 = this.trackedPlayers.iterator();

        while (var3.hasNext())
        {
            EntityPlayerMP var2 = (EntityPlayerMP)var3.next();
            var2.playerNetServerHandler.sendPacket(par1Packet);
        }
    }

    public void sendPacketToTrackedPlayersAndTrackedEntity(Packet par1Packet)
    {
        this.sendPacketToTrackedPlayers(par1Packet);

        if (this.trackedEntity instanceof EntityPlayerMP)
        {
            ((EntityPlayerMP)this.trackedEntity).playerNetServerHandler.sendPacket(par1Packet);
        }
    }

    public void sendDestroyEntityPacketToTrackedPlayers()
    {
        this.sendPacketToTrackedPlayers(new Packet29DestroyEntity(this.trackedEntity.entityId));
    }

    public void removeFromTrackedPlayers(EntityPlayerMP par1EntityPlayerMP)
    {
        if (this.trackedPlayers.contains(par1EntityPlayerMP))
        {
            this.trackedPlayers.remove(par1EntityPlayerMP);
        }
    }

    public void updatePlayerEntity(EntityPlayerMP par1EntityPlayerMP)
    {
        if (par1EntityPlayerMP != this.trackedEntity)
        {
            double var2 = par1EntityPlayerMP.posX - (double)(this.encodedPosX / 32);
            double var4 = par1EntityPlayerMP.posZ - (double)(this.encodedPosZ / 32);

            if (var2 >= (double)(-this.trackingDistanceThreshold) && var2 <= (double)this.trackingDistanceThreshold && var4 >= (double)(-this.trackingDistanceThreshold) && var4 <= (double)this.trackingDistanceThreshold)
            {
                if (!this.trackedPlayers.contains(par1EntityPlayerMP))
                {
                    this.trackedPlayers.add(par1EntityPlayerMP);
                    par1EntityPlayerMP.playerNetServerHandler.sendPacket(this.getSpawnPacket());

                    if (this.shouldSendMotionUpdates)
                    {
                        par1EntityPlayerMP.playerNetServerHandler.sendPacket(new Packet28EntityVelocity(this.trackedEntity.entityId, this.trackedEntity.motionX, this.trackedEntity.motionY, this.trackedEntity.motionZ));
                    }

                    ItemStack[] var6 = this.trackedEntity.getInventory();

                    if (var6 != null)
                    {
                        for (int var7 = 0; var7 < var6.length; ++var7)
                        {
                            par1EntityPlayerMP.playerNetServerHandler.sendPacket(new Packet5PlayerInventory(this.trackedEntity.entityId, var7, var6[var7]));
                        }
                    }

                    if (this.trackedEntity instanceof EntityPlayer)
                    {
                        EntityPlayer var11 = (EntityPlayer)this.trackedEntity;

                        if (var11.isPlayerSleeping())
                        {
                            par1EntityPlayerMP.playerNetServerHandler.sendPacket(new Packet17Sleep(this.trackedEntity, 0, MathHelper.floor_double(this.trackedEntity.posX), MathHelper.floor_double(this.trackedEntity.posY), MathHelper.floor_double(this.trackedEntity.posZ)));
                        }
                    }

                    if (this.trackedEntity instanceof EntityLiving)
                    {
                        EntityLiving var10 = (EntityLiving)this.trackedEntity;
                        Iterator var9 = var10.getActivePotionEffects().iterator();

                        while (var9.hasNext())
                        {
                            PotionEffect var8 = (PotionEffect)var9.next();
                            par1EntityPlayerMP.playerNetServerHandler.sendPacket(new Packet41EntityEffect(this.trackedEntity.entityId, var8));
                        }
                    }
                }
            }
            else if (this.trackedPlayers.contains(par1EntityPlayerMP))
            {
                this.trackedPlayers.remove(par1EntityPlayerMP);
                par1EntityPlayerMP.playerNetServerHandler.sendPacket(new Packet29DestroyEntity(this.trackedEntity.entityId));
            }
        }
    }

    public void updatePlayerEntities(List par1List)
    {
        for (int var2 = 0; var2 < par1List.size(); ++var2)
        {
            this.updatePlayerEntity((EntityPlayerMP)par1List.get(var2));
        }
    }

    private Packet getSpawnPacket()
    {
        if (this.trackedEntity.isDead)
        {
            System.out.println("Fetching addPacket for removed entity");
        }
        Packet pkt = ForgeHooks.getEntitySpawnPacket(trackedEntity);
        if (pkt != null)
        {
            return pkt;
        }

        EntityTrackerEntry2 var1 = ModLoaderMp.handleEntityTrackerEntries(this.trackedEntity);

        if (var1 != null)
        {
            try
            {
                if (this.trackedEntity instanceof ISpawnable)
                {
                    Packet230ModLoader var11 = ((ISpawnable)this.trackedEntity).getSpawnPacket();
                    var11.modId = "Spawn".hashCode();

                    if (var1.entityId > 127)
                    {
                        var11.packetType = var1.entityId - 256;
                    }
                    else
                    {
                        var11.packetType = var1.entityId;
                    }

                    return var11;
                }
                else if (!var1.entityHasOwner)
                {
                    return new Packet23VehicleSpawn(this.trackedEntity, var1.entityId);
                }
                else
                {
                    Field var10 = this.trackedEntity.getClass().getField("owner");

                    if (Entity.class.isAssignableFrom(var10.getType()))
                    {
                        Entity var12 = (Entity)var10.get(this.trackedEntity);
                        return new Packet23VehicleSpawn(this.trackedEntity, var1.entityId, var12 != null ? var12.entityId : this.trackedEntity.entityId);
                    }
                    else
                    {
                        throw new Exception(String.format("Entity\'s owner field must be of type Entity, but it is of type %s.", new Object[] {var10.getType()}));
                    }
                }
            }
            catch (Exception var4)
            {
                ModLoader.getLogger().throwing("EntityTrackerEntry", "getSpawnPacket", var4);
                ModLoader.throwException(String.format("Error sending spawn packet for entity of type %s.", new Object[] {this.trackedEntity.getClass()}), var4);
                return null;
            }
        }
        else if (this.trackedEntity instanceof EntityItem)
        {
            EntityItem var9 = (EntityItem)this.trackedEntity;
            Packet21PickupSpawn var13 = new Packet21PickupSpawn(var9);
            var9.posX = (double)var13.xPosition / 32.0D;
            var9.posY = (double)var13.yPosition / 32.0D;
            var9.posZ = (double)var13.zPosition / 32.0D;
            return var13;
        }
        else if (this.trackedEntity instanceof EntityPlayerMP)
        {
            return new Packet20NamedEntitySpawn((EntityPlayer)this.trackedEntity);
        }
        else
        {
            if (this.trackedEntity instanceof EntityMinecart)
            {
                EntityMinecart var2 = (EntityMinecart)this.trackedEntity;

                if (var2.minecartType == 0)
                {
                    return new Packet23VehicleSpawn(this.trackedEntity, 10);
                }

                if (var2.minecartType == 1)
                {
                    return new Packet23VehicleSpawn(this.trackedEntity, 11);
                }

                if (var2.minecartType == 2)
                {
                    return new Packet23VehicleSpawn(this.trackedEntity, 12);
                }
            }

            if (this.trackedEntity instanceof EntityBoat)
            {
                return new Packet23VehicleSpawn(this.trackedEntity, 1);
            }
            else if (this.trackedEntity instanceof IAnimals)
            {
                return new Packet24MobSpawn((EntityLiving)this.trackedEntity);
            }
            else if (this.trackedEntity instanceof EntityDragon)
            {
                return new Packet24MobSpawn((EntityLiving)this.trackedEntity);
            }
            else if (this.trackedEntity instanceof EntityFishHook)
            {
                return new Packet23VehicleSpawn(this.trackedEntity, 90);
            }
            else if (this.trackedEntity instanceof EntityArrow)
            {
                Entity var8 = ((EntityArrow)this.trackedEntity).shootingEntity;
                return new Packet23VehicleSpawn(this.trackedEntity, 60, var8 == null ? this.trackedEntity.entityId : var8.entityId);
            }
            else if (this.trackedEntity instanceof EntitySnowball)
            {
                return new Packet23VehicleSpawn(this.trackedEntity, 61);
            }
            else if (this.trackedEntity instanceof EntityPotion)
            {
                return new Packet23VehicleSpawn(this.trackedEntity, 73, ((EntityPotion)this.trackedEntity).getPotionDamage());
            }
            else if (this.trackedEntity instanceof EntityExpBottle)
            {
                return new Packet23VehicleSpawn(this.trackedEntity, 75);
            }
            else if (this.trackedEntity instanceof EntityEnderPearl)
            {
                return new Packet23VehicleSpawn(this.trackedEntity, 65);
            }
            else if (this.trackedEntity instanceof EntityEnderEye)
            {
                return new Packet23VehicleSpawn(this.trackedEntity, 72);
            }
            else
            {
                Packet23VehicleSpawn var3;

                if (this.trackedEntity instanceof EntitySmallFireball)
                {
                    EntitySmallFireball var7 = (EntitySmallFireball)this.trackedEntity;
                    var3 = null;

                    if (var7.shootingEntity != null)
                    {
                        var3 = new Packet23VehicleSpawn(this.trackedEntity, 64, var7.shootingEntity.entityId);
                    }
                    else
                    {
                        var3 = new Packet23VehicleSpawn(this.trackedEntity, 64, 0);
                    }

                    var3.speedX = (int)(var7.accelerationX * 8000.0D);
                    var3.speedY = (int)(var7.accelerationY * 8000.0D);
                    var3.speedZ = (int)(var7.accelerationZ * 8000.0D);
                    return var3;
                }
                else if (this.trackedEntity instanceof EntityFireball)
                {
                    EntityFireball var6 = (EntityFireball)this.trackedEntity;
                    var3 = null;

                    if (var6.shootingEntity != null)
                    {
                        var3 = new Packet23VehicleSpawn(this.trackedEntity, 63, ((EntityFireball)this.trackedEntity).shootingEntity.entityId);
                    }
                    else
                    {
                        var3 = new Packet23VehicleSpawn(this.trackedEntity, 63, 0);
                    }

                    var3.speedX = (int)(var6.accelerationX * 8000.0D);
                    var3.speedY = (int)(var6.accelerationY * 8000.0D);
                    var3.speedZ = (int)(var6.accelerationZ * 8000.0D);
                    return var3;
                }
                else if (this.trackedEntity instanceof EntityEgg)
                {
                    return new Packet23VehicleSpawn(this.trackedEntity, 62);
                }
                else if (this.trackedEntity instanceof EntityTNTPrimed)
                {
                    return new Packet23VehicleSpawn(this.trackedEntity, 50);
                }
                else if (this.trackedEntity instanceof EntityEnderCrystal)
                {
                    return new Packet23VehicleSpawn(this.trackedEntity, 51);
                }
                else
                {
                    if (this.trackedEntity instanceof EntityFallingSand)
                    {
                        EntityFallingSand var5 = (EntityFallingSand)this.trackedEntity;

                        if (var5.blockID == Block.sand.blockID)
                        {
                            return new Packet23VehicleSpawn(this.trackedEntity, 70);
                        }

                        if (var5.blockID == Block.gravel.blockID)
                        {
                            return new Packet23VehicleSpawn(this.trackedEntity, 71);
                        }

                        if (var5.blockID == Block.dragonEgg.blockID)
                        {
                            return new Packet23VehicleSpawn(this.trackedEntity, 74);
                        }
                    }

                    if (this.trackedEntity instanceof EntityPainting)
                    {
                        return new Packet25EntityPainting((EntityPainting)this.trackedEntity);
                    }
                    else if (this.trackedEntity instanceof EntityXPOrb)
                    {
                        return new Packet26EntityExpOrb((EntityXPOrb)this.trackedEntity);
                    }
                    else
                    {
                        throw new IllegalArgumentException("Don\'t know how to add " + this.trackedEntity.getClass() + "!");
                    }
                }
            }
        }
    }

    /**
     * Remove a tracked player from our list and tell the tracked player to destroy us from their world.
     */
    public void removeTrackedPlayerSymmetric(EntityPlayerMP par1EntityPlayerMP)
    {
        if (this.trackedPlayers.contains(par1EntityPlayerMP))
        {
            this.trackedPlayers.remove(par1EntityPlayerMP);
            par1EntityPlayerMP.playerNetServerHandler.sendPacket(new Packet29DestroyEntity(this.trackedEntity.entityId));
        }
    }
}
