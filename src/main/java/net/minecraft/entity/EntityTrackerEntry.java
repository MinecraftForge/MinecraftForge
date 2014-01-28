package net.minecraft.entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.attributes.ServersideAttributeMap;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S10PacketSpawnPainting;
import net.minecraft.network.play.server.S11PacketSpawnExperienceOrb;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.storage.MapData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.network.internal.FMLNetworkHandler;

public class EntityTrackerEntry
{
    private static final Logger field_151262_p = LogManager.getLogger();
    public Entity myEntity;
    public int blocksDistanceThreshold;
    // JAVADOC FIELD $$ field_73131_c
    public int updateFrequency;
    public int lastScaledXPosition;
    public int lastScaledYPosition;
    public int lastScaledZPosition;
    public int lastYaw;
    public int lastPitch;
    public int lastHeadMotion;
    public double motionX;
    public double motionY;
    public double motionZ;
    public int ticks;
    private double posX;
    private double posY;
    private double posZ;
    // JAVADOC FIELD $$ field_73144_s
    private boolean isDataInitialized;
    private boolean sendVelocityUpdates;
    // JAVADOC FIELD $$ field_73142_u
    private int ticksSinceLastForcedTeleport;
    private Entity field_85178_v;
    private boolean ridingEntity;
    public boolean playerEntitiesUpdated;
    // JAVADOC FIELD $$ field_73134_o
    public Set trackingPlayers = new HashSet();
    private static final String __OBFID = "CL_00001443";

    public EntityTrackerEntry(Entity par1Entity, int par2, int par3, boolean par4)
    {
        this.myEntity = par1Entity;
        this.blocksDistanceThreshold = par2;
        this.updateFrequency = par3;
        this.sendVelocityUpdates = par4;
        this.lastScaledXPosition = MathHelper.floor_double(par1Entity.posX * 32.0D);
        this.lastScaledYPosition = MathHelper.floor_double(par1Entity.posY * 32.0D);
        this.lastScaledZPosition = MathHelper.floor_double(par1Entity.posZ * 32.0D);
        this.lastYaw = MathHelper.floor_float(par1Entity.rotationYaw * 256.0F / 360.0F);
        this.lastPitch = MathHelper.floor_float(par1Entity.rotationPitch * 256.0F / 360.0F);
        this.lastHeadMotion = MathHelper.floor_float(par1Entity.getRotationYawHead() * 256.0F / 360.0F);
    }

    public boolean equals(Object par1Obj)
    {
        return par1Obj instanceof EntityTrackerEntry ? ((EntityTrackerEntry)par1Obj).myEntity.func_145782_y() == this.myEntity.func_145782_y() : false;
    }

    public int hashCode()
    {
        return this.myEntity.func_145782_y();
    }

    // JAVADOC METHOD $$ func_73122_a
    public void sendLocationToAllClients(List par1List)
    {
        this.playerEntitiesUpdated = false;

        if (!this.isDataInitialized || this.myEntity.getDistanceSq(this.posX, this.posY, this.posZ) > 16.0D)
        {
            this.posX = this.myEntity.posX;
            this.posY = this.myEntity.posY;
            this.posZ = this.myEntity.posZ;
            this.isDataInitialized = true;
            this.playerEntitiesUpdated = true;
            this.sendEventsToPlayers(par1List);
        }

        if (this.field_85178_v != this.myEntity.ridingEntity || this.myEntity.ridingEntity != null && this.ticks % 60 == 0)
        {
            this.field_85178_v = this.myEntity.ridingEntity;
            this.func_151259_a(new S1BPacketEntityAttach(0, this.myEntity, this.myEntity.ridingEntity));
        }

        if (this.myEntity instanceof EntityItemFrame && this.ticks % 10 == 0)
        {
            EntityItemFrame entityitemframe = (EntityItemFrame)this.myEntity;
            ItemStack itemstack = entityitemframe.getDisplayedItem();

            if (itemstack != null && itemstack.getItem() instanceof ItemMap)
            {
                MapData mapdata = Items.filled_map.getMapData(itemstack, this.myEntity.worldObj);
                Iterator iterator = par1List.iterator();

                while (iterator.hasNext())
                {
                    EntityPlayer entityplayer = (EntityPlayer)iterator.next();
                    EntityPlayerMP entityplayermp = (EntityPlayerMP)entityplayer;
                    mapdata.updateVisiblePlayers(entityplayermp, itemstack);
                    Packet packet = Items.filled_map.func_150911_c(itemstack, this.myEntity.worldObj, entityplayermp);

                    if (packet != null)
                    {
                        entityplayermp.playerNetServerHandler.func_147359_a(packet);
                    }
                }
            }

            this.func_111190_b();
        }
        else if (this.ticks % this.updateFrequency == 0 || this.myEntity.isAirBorne || this.myEntity.getDataWatcher().hasChanges())
        {
            int i;
            int j;

            if (this.myEntity.ridingEntity == null)
            {
                ++this.ticksSinceLastForcedTeleport;
                i = this.myEntity.myEntitySize.multiplyBy32AndRound(this.myEntity.posX);
                j = MathHelper.floor_double(this.myEntity.posY * 32.0D);
                int k = this.myEntity.myEntitySize.multiplyBy32AndRound(this.myEntity.posZ);
                int l = MathHelper.floor_float(this.myEntity.rotationYaw * 256.0F / 360.0F);
                int i1 = MathHelper.floor_float(this.myEntity.rotationPitch * 256.0F / 360.0F);
                int j1 = i - this.lastScaledXPosition;
                int k1 = j - this.lastScaledYPosition;
                int l1 = k - this.lastScaledZPosition;
                Object object = null;
                boolean flag = Math.abs(j1) >= 4 || Math.abs(k1) >= 4 || Math.abs(l1) >= 4 || this.ticks % 60 == 0;
                boolean flag1 = Math.abs(l - this.lastYaw) >= 4 || Math.abs(i1 - this.lastPitch) >= 4;

                if (this.ticks > 0 || this.myEntity instanceof EntityArrow)
                {
                    if (j1 >= -128 && j1 < 128 && k1 >= -128 && k1 < 128 && l1 >= -128 && l1 < 128 && this.ticksSinceLastForcedTeleport <= 400 && !this.ridingEntity)
                    {
                        if (flag && flag1)
                        {
                            object = new S14PacketEntity.S17PacketEntityLookMove(this.myEntity.func_145782_y(), (byte)j1, (byte)k1, (byte)l1, (byte)l, (byte)i1);
                        }
                        else if (flag)
                        {
                            object = new S14PacketEntity.S15PacketEntityRelMove(this.myEntity.func_145782_y(), (byte)j1, (byte)k1, (byte)l1);
                        }
                        else if (flag1)
                        {
                            object = new S14PacketEntity.S16PacketEntityLook(this.myEntity.func_145782_y(), (byte)l, (byte)i1);
                        }
                    }
                    else
                    {
                        this.ticksSinceLastForcedTeleport = 0;
                        object = new S18PacketEntityTeleport(this.myEntity.func_145782_y(), i, j, k, (byte)l, (byte)i1);
                    }
                }

                if (this.sendVelocityUpdates)
                {
                    double d0 = this.myEntity.motionX - this.motionX;
                    double d1 = this.myEntity.motionY - this.motionY;
                    double d2 = this.myEntity.motionZ - this.motionZ;
                    double d3 = 0.02D;
                    double d4 = d0 * d0 + d1 * d1 + d2 * d2;

                    if (d4 > d3 * d3 || d4 > 0.0D && this.myEntity.motionX == 0.0D && this.myEntity.motionY == 0.0D && this.myEntity.motionZ == 0.0D)
                    {
                        this.motionX = this.myEntity.motionX;
                        this.motionY = this.myEntity.motionY;
                        this.motionZ = this.myEntity.motionZ;
                        this.func_151259_a(new S12PacketEntityVelocity(this.myEntity.func_145782_y(), this.motionX, this.motionY, this.motionZ));
                    }
                }

                if (object != null)
                {
                    this.func_151259_a((Packet)object);
                }

                this.func_111190_b();

                if (flag)
                {
                    this.lastScaledXPosition = i;
                    this.lastScaledYPosition = j;
                    this.lastScaledZPosition = k;
                }

                if (flag1)
                {
                    this.lastYaw = l;
                    this.lastPitch = i1;
                }

                this.ridingEntity = false;
            }
            else
            {
                i = MathHelper.floor_float(this.myEntity.rotationYaw * 256.0F / 360.0F);
                j = MathHelper.floor_float(this.myEntity.rotationPitch * 256.0F / 360.0F);
                boolean flag2 = Math.abs(i - this.lastYaw) >= 4 || Math.abs(j - this.lastPitch) >= 4;

                if (flag2)
                {
                    this.func_151259_a(new S14PacketEntity.S16PacketEntityLook(this.myEntity.func_145782_y(), (byte)i, (byte)j));
                    this.lastYaw = i;
                    this.lastPitch = j;
                }

                this.lastScaledXPosition = this.myEntity.myEntitySize.multiplyBy32AndRound(this.myEntity.posX);
                this.lastScaledYPosition = MathHelper.floor_double(this.myEntity.posY * 32.0D);
                this.lastScaledZPosition = this.myEntity.myEntitySize.multiplyBy32AndRound(this.myEntity.posZ);
                this.func_111190_b();
                this.ridingEntity = true;
            }

            i = MathHelper.floor_float(this.myEntity.getRotationYawHead() * 256.0F / 360.0F);

            if (Math.abs(i - this.lastHeadMotion) >= 4)
            {
                this.func_151259_a(new S19PacketEntityHeadLook(this.myEntity, (byte)i));
                this.lastHeadMotion = i;
            }

            this.myEntity.isAirBorne = false;
        }

        ++this.ticks;

        if (this.myEntity.velocityChanged)
        {
            this.func_151261_b(new S12PacketEntityVelocity(this.myEntity));
            this.myEntity.velocityChanged = false;
        }
    }

    private void func_111190_b()
    {
        DataWatcher datawatcher = this.myEntity.getDataWatcher();

        if (datawatcher.hasChanges())
        {
            this.func_151261_b(new S1CPacketEntityMetadata(this.myEntity.func_145782_y(), datawatcher, false));
        }

        if (this.myEntity instanceof EntityLivingBase)
        {
            ServersideAttributeMap serversideattributemap = (ServersideAttributeMap)((EntityLivingBase)this.myEntity).getAttributeMap();
            Set set = serversideattributemap.func_111161_b();

            if (!set.isEmpty())
            {
                this.func_151261_b(new S20PacketEntityProperties(this.myEntity.func_145782_y(), set));
            }

            set.clear();
        }
    }

    public void func_151259_a(Packet p_151259_1_)
    {
        Iterator iterator = this.trackingPlayers.iterator();

        while (iterator.hasNext())
        {
            EntityPlayerMP entityplayermp = (EntityPlayerMP)iterator.next();
            entityplayermp.playerNetServerHandler.func_147359_a(p_151259_1_);
        }
    }

    public void func_151261_b(Packet p_151261_1_)
    {
        this.func_151259_a(p_151261_1_);

        if (this.myEntity instanceof EntityPlayerMP)
        {
            ((EntityPlayerMP)this.myEntity).playerNetServerHandler.func_147359_a(p_151261_1_);
        }
    }

    public void informAllAssociatedPlayersOfItemDestruction()
    {
        Iterator iterator = this.trackingPlayers.iterator();

        while (iterator.hasNext())
        {
            EntityPlayerMP entityplayermp = (EntityPlayerMP)iterator.next();
            entityplayermp.destroyedItemsNetCache.add(Integer.valueOf(this.myEntity.func_145782_y()));
        }
    }

    public void removeFromWatchingList(EntityPlayerMP par1EntityPlayerMP)
    {
        if (this.trackingPlayers.contains(par1EntityPlayerMP))
        {
            par1EntityPlayerMP.destroyedItemsNetCache.add(Integer.valueOf(this.myEntity.func_145782_y()));
            this.trackingPlayers.remove(par1EntityPlayerMP);
        }
    }

    // JAVADOC METHOD $$ func_73117_b
    public void tryStartWachingThis(EntityPlayerMP par1EntityPlayerMP)
    {
        if (par1EntityPlayerMP != this.myEntity)
        {
            double d0 = par1EntityPlayerMP.posX - (double)(this.lastScaledXPosition / 32);
            double d1 = par1EntityPlayerMP.posZ - (double)(this.lastScaledZPosition / 32);

            if (d0 >= (double)(-this.blocksDistanceThreshold) && d0 <= (double)this.blocksDistanceThreshold && d1 >= (double)(-this.blocksDistanceThreshold) && d1 <= (double)this.blocksDistanceThreshold)
            {
                if (!this.trackingPlayers.contains(par1EntityPlayerMP) && (this.isPlayerWatchingThisChunk(par1EntityPlayerMP) || this.myEntity.forceSpawn))
                {
                    this.trackingPlayers.add(par1EntityPlayerMP);
                    Packet packet = this.func_151260_c();
                    par1EntityPlayerMP.playerNetServerHandler.func_147359_a(packet);

                    if (!this.myEntity.getDataWatcher().getIsBlank())
                    {
                        par1EntityPlayerMP.playerNetServerHandler.func_147359_a(new S1CPacketEntityMetadata(this.myEntity.func_145782_y(), this.myEntity.getDataWatcher(), true));
                    }

                    if (this.myEntity instanceof EntityLivingBase)
                    {
                        ServersideAttributeMap serversideattributemap = (ServersideAttributeMap)((EntityLivingBase)this.myEntity).getAttributeMap();
                        Collection collection = serversideattributemap.func_111160_c();

                        if (!collection.isEmpty())
                        {
                            par1EntityPlayerMP.playerNetServerHandler.func_147359_a(new S20PacketEntityProperties(this.myEntity.func_145782_y(), collection));
                        }
                    }

                    this.motionX = this.myEntity.motionX;
                    this.motionY = this.myEntity.motionY;
                    this.motionZ = this.myEntity.motionZ;

                    int posX = MathHelper.floor_double(this.myEntity.posX * 32.0D);
                    int posY = MathHelper.floor_double(this.myEntity.posY * 32.0D);
                    int posZ = MathHelper.floor_double(this.myEntity.posZ * 32.0D);
                    if (posX != this.lastScaledXPosition || posY != this.lastScaledYPosition || posZ != this.lastScaledZPosition)
                    {
                        FMLNetworkHandler.makeEntitySpawnAdjustment(this.myEntity, par1EntityPlayerMP, this.lastScaledXPosition, this.lastScaledYPosition, this.lastScaledZPosition);
                    }

                    if (this.sendVelocityUpdates && !(packet instanceof S0FPacketSpawnMob))
                    {
                        par1EntityPlayerMP.playerNetServerHandler.func_147359_a(new S12PacketEntityVelocity(this.myEntity.func_145782_y(), this.myEntity.motionX, this.myEntity.motionY, this.myEntity.motionZ));
                    }

                    if (this.myEntity.ridingEntity != null)
                    {
                        par1EntityPlayerMP.playerNetServerHandler.func_147359_a(new S1BPacketEntityAttach(0, this.myEntity, this.myEntity.ridingEntity));
                    }

                    if (this.myEntity instanceof EntityLiving && ((EntityLiving)this.myEntity).getLeashedToEntity() != null)
                    {
                        par1EntityPlayerMP.playerNetServerHandler.func_147359_a(new S1BPacketEntityAttach(1, this.myEntity, ((EntityLiving)this.myEntity).getLeashedToEntity()));
                    }

                    if (this.myEntity instanceof EntityLivingBase)
                    {
                        for (int i = 0; i < 5; ++i)
                        {
                            ItemStack itemstack = ((EntityLivingBase)this.myEntity).getCurrentItemOrArmor(i);

                            if (itemstack != null)
                            {
                                par1EntityPlayerMP.playerNetServerHandler.func_147359_a(new S04PacketEntityEquipment(this.myEntity.func_145782_y(), i, itemstack));
                            }
                        }
                    }

                    if (this.myEntity instanceof EntityPlayer)
                    {
                        EntityPlayer entityplayer = (EntityPlayer)this.myEntity;

                        if (entityplayer.isPlayerSleeping())
                        {
                            par1EntityPlayerMP.playerNetServerHandler.func_147359_a(new S0APacketUseBed(entityplayer, MathHelper.floor_double(this.myEntity.posX), MathHelper.floor_double(this.myEntity.posY), MathHelper.floor_double(this.myEntity.posZ)));
                        }
                    }

                    if (this.myEntity instanceof EntityLivingBase)
                    {
                        EntityLivingBase entitylivingbase = (EntityLivingBase)this.myEntity;
                        Iterator iterator = entitylivingbase.getActivePotionEffects().iterator();

                        while (iterator.hasNext())
                        {
                            PotionEffect potioneffect = (PotionEffect)iterator.next();
                            par1EntityPlayerMP.playerNetServerHandler.func_147359_a(new S1DPacketEntityEffect(this.myEntity.func_145782_y(), potioneffect));
                        }
                    }
                }
            }
            else if (this.trackingPlayers.contains(par1EntityPlayerMP))
            {
                this.trackingPlayers.remove(par1EntityPlayerMP);
                par1EntityPlayerMP.destroyedItemsNetCache.add(Integer.valueOf(this.myEntity.func_145782_y()));
            }
        }
    }

    private boolean isPlayerWatchingThisChunk(EntityPlayerMP par1EntityPlayerMP)
    {
        return par1EntityPlayerMP.getServerForPlayer().getPlayerManager().isPlayerWatchingChunk(par1EntityPlayerMP, this.myEntity.chunkCoordX, this.myEntity.chunkCoordZ);
    }

    public void sendEventsToPlayers(List par1List)
    {
        for (int i = 0; i < par1List.size(); ++i)
        {
            this.tryStartWachingThis((EntityPlayerMP)par1List.get(i));
        }
    }

    private Packet func_151260_c()
    {
        if (this.myEntity.isDead)
        {
            field_151262_p.warn("Fetching addPacket for removed entity");
        }

        Packet pkt = FMLNetworkHandler.getEntitySpawningPacket(this.myEntity);

        if (pkt != null)
        {
            return pkt;
        }
        if (this.myEntity instanceof EntityItem)
        {
            return new S0EPacketSpawnObject(this.myEntity, 2, 1);
        }
        else if (this.myEntity instanceof EntityPlayerMP)
        {
            return new S0CPacketSpawnPlayer((EntityPlayer)this.myEntity);
        }
        else if (this.myEntity instanceof EntityMinecart)
        {
            EntityMinecart entityminecart = (EntityMinecart)this.myEntity;
            return new S0EPacketSpawnObject(this.myEntity, 10, entityminecart.getMinecartType());
        }
        else if (this.myEntity instanceof EntityBoat)
        {
            return new S0EPacketSpawnObject(this.myEntity, 1);
        }
        else if (!(this.myEntity instanceof IAnimals) && !(this.myEntity instanceof EntityDragon))
        {
            if (this.myEntity instanceof EntityFishHook)
            {
                EntityPlayer entityplayer = ((EntityFishHook)this.myEntity).field_146042_b;
                return new S0EPacketSpawnObject(this.myEntity, 90, entityplayer != null ? entityplayer.func_145782_y() : this.myEntity.func_145782_y());
            }
            else if (this.myEntity instanceof EntityArrow)
            {
                Entity entity = ((EntityArrow)this.myEntity).shootingEntity;
                return new S0EPacketSpawnObject(this.myEntity, 60, entity != null ? entity.func_145782_y() : this.myEntity.func_145782_y());
            }
            else if (this.myEntity instanceof EntitySnowball)
            {
                return new S0EPacketSpawnObject(this.myEntity, 61);
            }
            else if (this.myEntity instanceof EntityPotion)
            {
                return new S0EPacketSpawnObject(this.myEntity, 73, ((EntityPotion)this.myEntity).getPotionDamage());
            }
            else if (this.myEntity instanceof EntityExpBottle)
            {
                return new S0EPacketSpawnObject(this.myEntity, 75);
            }
            else if (this.myEntity instanceof EntityEnderPearl)
            {
                return new S0EPacketSpawnObject(this.myEntity, 65);
            }
            else if (this.myEntity instanceof EntityEnderEye)
            {
                return new S0EPacketSpawnObject(this.myEntity, 72);
            }
            else if (this.myEntity instanceof EntityFireworkRocket)
            {
                return new S0EPacketSpawnObject(this.myEntity, 76);
            }
            else
            {
                S0EPacketSpawnObject s0epacketspawnobject;

                if (this.myEntity instanceof EntityFireball)
                {
                    EntityFireball entityfireball = (EntityFireball)this.myEntity;
                    s0epacketspawnobject = null;
                    byte b0 = 63;

                    if (this.myEntity instanceof EntitySmallFireball)
                    {
                        b0 = 64;
                    }
                    else if (this.myEntity instanceof EntityWitherSkull)
                    {
                        b0 = 66;
                    }

                    if (entityfireball.shootingEntity != null)
                    {
                        s0epacketspawnobject = new S0EPacketSpawnObject(this.myEntity, b0, ((EntityFireball)this.myEntity).shootingEntity.func_145782_y());
                    }
                    else
                    {
                        s0epacketspawnobject = new S0EPacketSpawnObject(this.myEntity, b0, 0);
                    }

                    s0epacketspawnobject.func_149003_d((int)(entityfireball.accelerationX * 8000.0D));
                    s0epacketspawnobject.func_149000_e((int)(entityfireball.accelerationY * 8000.0D));
                    s0epacketspawnobject.func_149007_f((int)(entityfireball.accelerationZ * 8000.0D));
                    return s0epacketspawnobject;
                }
                else if (this.myEntity instanceof EntityEgg)
                {
                    return new S0EPacketSpawnObject(this.myEntity, 62);
                }
                else if (this.myEntity instanceof EntityTNTPrimed)
                {
                    return new S0EPacketSpawnObject(this.myEntity, 50);
                }
                else if (this.myEntity instanceof EntityEnderCrystal)
                {
                    return new S0EPacketSpawnObject(this.myEntity, 51);
                }
                else if (this.myEntity instanceof EntityFallingBlock)
                {
                    EntityFallingBlock entityfallingblock = (EntityFallingBlock)this.myEntity;
                    return new S0EPacketSpawnObject(this.myEntity, 70, Block.func_149682_b(entityfallingblock.func_145805_f()) | entityfallingblock.field_145814_a << 16);
                }
                else if (this.myEntity instanceof EntityPainting)
                {
                    return new S10PacketSpawnPainting((EntityPainting)this.myEntity);
                }
                else if (this.myEntity instanceof EntityItemFrame)
                {
                    EntityItemFrame entityitemframe = (EntityItemFrame)this.myEntity;
                    s0epacketspawnobject = new S0EPacketSpawnObject(this.myEntity, 71, entityitemframe.hangingDirection);
                    s0epacketspawnobject.func_148996_a(MathHelper.floor_float((float)(entityitemframe.field_146063_b * 32)));
                    s0epacketspawnobject.func_148995_b(MathHelper.floor_float((float)(entityitemframe.field_146064_c * 32)));
                    s0epacketspawnobject.func_149005_c(MathHelper.floor_float((float)(entityitemframe.field_146062_d * 32)));
                    return s0epacketspawnobject;
                }
                else if (this.myEntity instanceof EntityLeashKnot)
                {
                    EntityLeashKnot entityleashknot = (EntityLeashKnot)this.myEntity;
                    s0epacketspawnobject = new S0EPacketSpawnObject(this.myEntity, 77);
                    s0epacketspawnobject.func_148996_a(MathHelper.floor_float((float)(entityleashknot.field_146063_b * 32)));
                    s0epacketspawnobject.func_148995_b(MathHelper.floor_float((float)(entityleashknot.field_146064_c * 32)));
                    s0epacketspawnobject.func_149005_c(MathHelper.floor_float((float)(entityleashknot.field_146062_d * 32)));
                    return s0epacketspawnobject;
                }
                else if (this.myEntity instanceof EntityXPOrb)
                {
                    return new S11PacketSpawnExperienceOrb((EntityXPOrb)this.myEntity);
                }
                else
                {
                    throw new IllegalArgumentException("Don\'t know how to add " + this.myEntity.getClass() + "!");
                }
            }
        }
        else
        {
            this.lastHeadMotion = MathHelper.floor_float(this.myEntity.getRotationYawHead() * 256.0F / 360.0F);
            return new S0FPacketSpawnMob((EntityLivingBase)this.myEntity);
        }
    }

    public void removePlayerFromTracker(EntityPlayerMP par1EntityPlayerMP)
    {
        if (this.trackingPlayers.contains(par1EntityPlayerMP))
        {
            this.trackingPlayers.remove(par1EntityPlayerMP);
            par1EntityPlayerMP.destroyedItemsNetCache.add(Integer.valueOf(this.myEntity.func_145782_y()));
        }
    }
}