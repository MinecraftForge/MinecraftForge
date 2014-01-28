package net.minecraft.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityJumpHelper;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntitySenses;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;
import cpw.mods.fml.common.eventhandler.Event.Result;
import net.minecraftforge.event.ForgeEventFactory;

public abstract class EntityLiving extends EntityLivingBase
{
    // JAVADOC FIELD $$ field_70757_a
    public int livingSoundTime;
    // JAVADOC FIELD $$ field_70728_aV
    protected int experienceValue;
    private EntityLookHelper lookHelper;
    private EntityMoveHelper moveHelper;
    // JAVADOC FIELD $$ field_70767_i
    private EntityJumpHelper jumpHelper;
    private EntityBodyHelper bodyHelper;
    private PathNavigate navigator;
    public final EntityAITasks tasks;
    public final EntityAITasks targetTasks;
    // JAVADOC FIELD $$ field_70696_bz
    private EntityLivingBase attackTarget;
    private EntitySenses senses;
    // JAVADOC FIELD $$ field_82182_bS
    private ItemStack[] equipment = new ItemStack[5];
    // JAVADOC FIELD $$ field_82174_bp
    protected float[] equipmentDropChances = new float[5];
    // JAVADOC FIELD $$ field_82172_bs
    private boolean canPickUpLoot;
    // JAVADOC FIELD $$ field_82179_bU
    private boolean persistenceRequired;
    protected float defaultPitch;
    // JAVADOC FIELD $$ field_70776_bF
    private Entity currentTarget;
    // JAVADOC FIELD $$ field_70700_bx
    protected int numTicksToChaseTarget;
    private boolean isLeashed;
    private Entity leashedToEntity;
    private NBTTagCompound field_110170_bx;
    private static final String __OBFID = "CL_00001550";

    public EntityLiving(World par1World)
    {
        super(par1World);
        this.tasks = new EntityAITasks(par1World != null && par1World.theProfiler != null ? par1World.theProfiler : null);
        this.targetTasks = new EntityAITasks(par1World != null && par1World.theProfiler != null ? par1World.theProfiler : null);
        this.lookHelper = new EntityLookHelper(this);
        this.moveHelper = new EntityMoveHelper(this);
        this.jumpHelper = new EntityJumpHelper(this);
        this.bodyHelper = new EntityBodyHelper(this);
        this.navigator = new PathNavigate(this, par1World);
        this.senses = new EntitySenses(this);

        for (int i = 0; i < this.equipmentDropChances.length; ++i)
        {
            this.equipmentDropChances[i] = 0.085F;
        }
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getAttributeMap().func_111150_b(SharedMonsterAttributes.followRange).setAttribute(16.0D);
    }

    public EntityLookHelper getLookHelper()
    {
        return this.lookHelper;
    }

    public EntityMoveHelper getMoveHelper()
    {
        return this.moveHelper;
    }

    public EntityJumpHelper getJumpHelper()
    {
        return this.jumpHelper;
    }

    public PathNavigate getNavigator()
    {
        return this.navigator;
    }

    // JAVADOC METHOD $$ func_70635_at
    public EntitySenses getEntitySenses()
    {
        return this.senses;
    }

    // JAVADOC METHOD $$ func_70638_az
    public EntityLivingBase getAttackTarget()
    {
        return this.attackTarget;
    }

    // JAVADOC METHOD $$ func_70624_b
    public void setAttackTarget(EntityLivingBase par1EntityLivingBase)
    {
        this.attackTarget = par1EntityLivingBase;
        ForgeHooks.onLivingSetAttackTarget(this, par1EntityLivingBase);
    }

    // JAVADOC METHOD $$ func_70686_a
    public boolean canAttackClass(Class par1Class)
    {
        return EntityCreeper.class != par1Class && EntityGhast.class != par1Class;
    }

    // JAVADOC METHOD $$ func_70615_aA
    public void eatGrassBonus() {}

    protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(11, Byte.valueOf((byte)0));
        this.dataWatcher.addObject(10, "");
    }

    // JAVADOC METHOD $$ func_70627_aG
    public int getTalkInterval()
    {
        return 80;
    }

    // JAVADOC METHOD $$ func_70642_aH
    public void playLivingSound()
    {
        String s = this.getLivingSound();

        if (s != null)
        {
            this.playSound(s, this.getSoundVolume(), this.getSoundPitch());
        }
    }

    // JAVADOC METHOD $$ func_70030_z
    public void onEntityUpdate()
    {
        super.onEntityUpdate();
        this.worldObj.theProfiler.startSection("mobBaseTick");

        if (this.isEntityAlive() && this.rand.nextInt(1000) < this.livingSoundTime++)
        {
            this.livingSoundTime = -this.getTalkInterval();
            this.playLivingSound();
        }

        this.worldObj.theProfiler.endSection();
    }

    // JAVADOC METHOD $$ func_70693_a
    protected int getExperiencePoints(EntityPlayer par1EntityPlayer)
    {
        if (this.experienceValue > 0)
        {
            int i = this.experienceValue;
            ItemStack[] aitemstack = this.getLastActiveItems();

            for (int j = 0; j < aitemstack.length; ++j)
            {
                if (aitemstack[j] != null && this.equipmentDropChances[j] <= 1.0F)
                {
                    i += 1 + this.rand.nextInt(3);
                }
            }

            return i;
        }
        else
        {
            return this.experienceValue;
        }
    }

    // JAVADOC METHOD $$ func_70656_aK
    public void spawnExplosionParticle()
    {
        for (int i = 0; i < 20; ++i)
        {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            double d3 = 10.0D;
            this.worldObj.spawnParticle("explode", this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width - d0 * d3, this.posY + (double)(this.rand.nextFloat() * this.height) - d1 * d3, this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width - d2 * d3, d0, d1, d2);
        }
    }

    // JAVADOC METHOD $$ func_70071_h_
    public void onUpdate()
    {
        super.onUpdate();

        if (!this.worldObj.isRemote)
        {
            this.func_110159_bB();
        }
    }

    protected float func_110146_f(float par1, float par2)
    {
        if (this.isAIEnabled())
        {
            this.bodyHelper.func_75664_a();
            return par2;
        }
        else
        {
            return super.func_110146_f(par1, par2);
        }
    }

    // JAVADOC METHOD $$ func_70639_aQ
    protected String getLivingSound()
    {
        return null;
    }

    protected Item func_146068_u()
    {
        return Item.func_150899_d(0);
    }

    // JAVADOC METHOD $$ func_70628_a
    protected void dropFewItems(boolean par1, int par2)
    {
        Item item = this.func_146068_u();

        if (item != null)
        {
            int j = this.rand.nextInt(3);

            if (par2 > 0)
            {
                j += this.rand.nextInt(par2 + 1);
            }

            for (int k = 0; k < j; ++k)
            {
                this.func_145779_a(item, 1);
            }
        }
    }

    // JAVADOC METHOD $$ func_70014_b
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setBoolean("CanPickUpLoot", this.canPickUpLoot());
        par1NBTTagCompound.setBoolean("PersistenceRequired", this.persistenceRequired);
        NBTTagList nbttaglist = new NBTTagList();
        NBTTagCompound nbttagcompound1;

        for (int i = 0; i < this.equipment.length; ++i)
        {
            nbttagcompound1 = new NBTTagCompound();

            if (this.equipment[i] != null)
            {
                this.equipment[i].writeToNBT(nbttagcompound1);
            }

            nbttaglist.appendTag(nbttagcompound1);
        }

        par1NBTTagCompound.setTag("Equipment", nbttaglist);
        NBTTagList nbttaglist1 = new NBTTagList();

        for (int j = 0; j < this.equipmentDropChances.length; ++j)
        {
            nbttaglist1.appendTag(new NBTTagFloat(this.equipmentDropChances[j]));
        }

        par1NBTTagCompound.setTag("DropChances", nbttaglist1);
        par1NBTTagCompound.setString("CustomName", this.getCustomNameTag());
        par1NBTTagCompound.setBoolean("CustomNameVisible", this.getAlwaysRenderNameTag());
        par1NBTTagCompound.setBoolean("Leashed", this.isLeashed);

        if (this.leashedToEntity != null)
        {
            nbttagcompound1 = new NBTTagCompound();

            if (this.leashedToEntity instanceof EntityLivingBase)
            {
                nbttagcompound1.setLong("UUIDMost", this.leashedToEntity.getUniqueID().getMostSignificantBits());
                nbttagcompound1.setLong("UUIDLeast", this.leashedToEntity.getUniqueID().getLeastSignificantBits());
            }
            else if (this.leashedToEntity instanceof EntityHanging)
            {
                EntityHanging entityhanging = (EntityHanging)this.leashedToEntity;
                nbttagcompound1.setInteger("X", entityhanging.field_146063_b);
                nbttagcompound1.setInteger("Y", entityhanging.field_146064_c);
                nbttagcompound1.setInteger("Z", entityhanging.field_146062_d);
            }

            par1NBTTagCompound.setTag("Leash", nbttagcompound1);
        }
    }

    // JAVADOC METHOD $$ func_70037_a
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        this.setCanPickUpLoot(par1NBTTagCompound.getBoolean("CanPickUpLoot"));
        this.persistenceRequired = par1NBTTagCompound.getBoolean("PersistenceRequired");

        if (par1NBTTagCompound.func_150297_b("CustomName", 8) && par1NBTTagCompound.getString("CustomName").length() > 0)
        {
            this.setCustomNameTag(par1NBTTagCompound.getString("CustomName"));
        }

        this.setAlwaysRenderNameTag(par1NBTTagCompound.getBoolean("CustomNameVisible"));
        NBTTagList nbttaglist;
        int i;

        if (par1NBTTagCompound.func_150297_b("Equipment", 9))
        {
            nbttaglist = par1NBTTagCompound.func_150295_c("Equipment", 10);

            for (i = 0; i < this.equipment.length; ++i)
            {
                this.equipment[i] = ItemStack.loadItemStackFromNBT(nbttaglist.func_150305_b(i));
            }
        }

        if (par1NBTTagCompound.func_150297_b("DropChances", 9))
        {
            nbttaglist = par1NBTTagCompound.func_150295_c("DropChances", 5);

            for (i = 0; i < nbttaglist.tagCount(); ++i)
            {
                this.equipmentDropChances[i] = nbttaglist.func_150308_e(i);
            }
        }

        this.isLeashed = par1NBTTagCompound.getBoolean("Leashed");

        if (this.isLeashed && par1NBTTagCompound.func_150297_b("Leash", 10))
        {
            this.field_110170_bx = par1NBTTagCompound.getCompoundTag("Leash");
        }
    }

    public void setMoveForward(float par1)
    {
        this.moveForward = par1;
    }

    // JAVADOC METHOD $$ func_70659_e
    public void setAIMoveSpeed(float par1)
    {
        super.setAIMoveSpeed(par1);
        this.setMoveForward(par1);
    }

    // JAVADOC METHOD $$ func_70636_d
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        this.worldObj.theProfiler.startSection("looting");

        if (!this.worldObj.isRemote && this.canPickUpLoot() && !this.dead && this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"))
        {
            List list = this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.boundingBox.expand(1.0D, 0.0D, 1.0D));
            Iterator iterator = list.iterator();

            while (iterator.hasNext())
            {
                EntityItem entityitem = (EntityItem)iterator.next();

                if (!entityitem.isDead && entityitem.getEntityItem() != null)
                {
                    ItemStack itemstack = entityitem.getEntityItem();
                    int i = getArmorPosition(itemstack);

                    if (i > -1)
                    {
                        boolean flag = true;
                        ItemStack itemstack1 = this.getCurrentItemOrArmor(i);

                        if (itemstack1 != null)
                        {
                            if (i == 0)
                            {
                                if (itemstack.getItem() instanceof ItemSword && !(itemstack1.getItem() instanceof ItemSword))
                                {
                                    flag = true;
                                }
                                else if (itemstack.getItem() instanceof ItemSword && itemstack1.getItem() instanceof ItemSword)
                                {
                                    ItemSword itemsword = (ItemSword)itemstack.getItem();
                                    ItemSword itemsword1 = (ItemSword)itemstack1.getItem();

                                    if (itemsword.func_150931_i() == itemsword1.func_150931_i())
                                    {
                                        flag = itemstack.getItemDamage() > itemstack1.getItemDamage() || itemstack.hasTagCompound() && !itemstack1.hasTagCompound();
                                    }
                                    else
                                    {
                                        flag = itemsword.func_150931_i() > itemsword1.func_150931_i();
                                    }
                                }
                                else
                                {
                                    flag = false;
                                }
                            }
                            else if (itemstack.getItem() instanceof ItemArmor && !(itemstack1.getItem() instanceof ItemArmor))
                            {
                                flag = true;
                            }
                            else if (itemstack.getItem() instanceof ItemArmor && itemstack1.getItem() instanceof ItemArmor)
                            {
                                ItemArmor itemarmor = (ItemArmor)itemstack.getItem();
                                ItemArmor itemarmor1 = (ItemArmor)itemstack1.getItem();

                                if (itemarmor.damageReduceAmount == itemarmor1.damageReduceAmount)
                                {
                                    flag = itemstack.getItemDamage() > itemstack1.getItemDamage() || itemstack.hasTagCompound() && !itemstack1.hasTagCompound();
                                }
                                else
                                {
                                    flag = itemarmor.damageReduceAmount > itemarmor1.damageReduceAmount;
                                }
                            }
                            else
                            {
                                flag = false;
                            }
                        }

                        if (flag)
                        {
                            if (itemstack1 != null && this.rand.nextFloat() - 0.1F < this.equipmentDropChances[i])
                            {
                                this.entityDropItem(itemstack1, 0.0F);
                            }

                            if (itemstack.getItem() == Items.diamond && entityitem.func_145800_j() != null)
                            {
                                EntityPlayer entityplayer = this.worldObj.getPlayerEntityByName(entityitem.func_145800_j());

                                if (entityplayer != null)
                                {
                                    entityplayer.triggerAchievement(AchievementList.field_150966_x);
                                }
                            }

                            this.setCurrentItemOrArmor(i, itemstack);
                            this.equipmentDropChances[i] = 2.0F;
                            this.persistenceRequired = true;
                            this.onItemPickup(entityitem, 1);
                            entityitem.setDead();
                        }
                    }
                }
            }
        }

        this.worldObj.theProfiler.endSection();
    }

    // JAVADOC METHOD $$ func_70650_aV
    protected boolean isAIEnabled()
    {
        return false;
    }

    // JAVADOC METHOD $$ func_70692_ba
    protected boolean canDespawn()
    {
        return true;
    }

    // JAVADOC METHOD $$ func_70623_bb
    protected void despawnEntity()
    {
        Result result = null;
        if (this.persistenceRequired)
        {
            this.entityAge = 0;
        }
        else if ((this.entityAge & 0x1F) == 0x1F && (result = ForgeEventFactory.canEntityDespawn(this)) != Result.DEFAULT)
        {
            if (result == Result.DENY)
            {
                this.entityAge = 0;
            }
            else
            {
                this.setDead();
            }
        }
        else
        {
            EntityPlayer entityplayer = this.worldObj.getClosestPlayerToEntity(this, -1.0D);

            if (entityplayer != null)
            {
                double d0 = entityplayer.posX - this.posX;
                double d1 = entityplayer.posY - this.posY;
                double d2 = entityplayer.posZ - this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;

                if (this.canDespawn() && d3 > 16384.0D)
                {
                    this.setDead();
                }

                if (this.entityAge > 600 && this.rand.nextInt(800) == 0 && d3 > 1024.0D && this.canDespawn())
                {
                    this.setDead();
                }
                else if (d3 < 1024.0D)
                {
                    this.entityAge = 0;
                }
            }
        }
    }

    protected void updateAITasks()
    {
        ++this.entityAge;
        this.worldObj.theProfiler.startSection("checkDespawn");
        this.despawnEntity();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("sensing");
        this.senses.clearSensingCache();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("targetSelector");
        this.targetTasks.onUpdateTasks();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("goalSelector");
        this.tasks.onUpdateTasks();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("navigation");
        this.navigator.onUpdateNavigation();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("mob tick");
        this.updateAITick();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("controls");
        this.worldObj.theProfiler.startSection("move");
        this.moveHelper.onUpdateMoveHelper();
        this.worldObj.theProfiler.endStartSection("look");
        this.lookHelper.onUpdateLook();
        this.worldObj.theProfiler.endStartSection("jump");
        this.jumpHelper.doJump();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.endSection();
    }

    protected void updateEntityActionState()
    {
        super.updateEntityActionState();
        this.moveStrafing = 0.0F;
        this.moveForward = 0.0F;
        this.despawnEntity();
        float f = 8.0F;

        if (this.rand.nextFloat() < 0.02F)
        {
            EntityPlayer entityplayer = this.worldObj.getClosestPlayerToEntity(this, (double)f);

            if (entityplayer != null)
            {
                this.currentTarget = entityplayer;
                this.numTicksToChaseTarget = 10 + this.rand.nextInt(20);
            }
            else
            {
                this.randomYawVelocity = (this.rand.nextFloat() - 0.5F) * 20.0F;
            }
        }

        if (this.currentTarget != null)
        {
            this.faceEntity(this.currentTarget, 10.0F, (float)this.getVerticalFaceSpeed());

            if (this.numTicksToChaseTarget-- <= 0 || this.currentTarget.isDead || this.currentTarget.getDistanceSqToEntity(this) > (double)(f * f))
            {
                this.currentTarget = null;
            }
        }
        else
        {
            if (this.rand.nextFloat() < 0.05F)
            {
                this.randomYawVelocity = (this.rand.nextFloat() - 0.5F) * 20.0F;
            }

            this.rotationYaw += this.randomYawVelocity;
            this.rotationPitch = this.defaultPitch;
        }

        boolean flag1 = this.isInWater();
        boolean flag = this.handleLavaMovement();

        if (flag1 || flag)
        {
            this.isJumping = this.rand.nextFloat() < 0.8F;
        }
    }

    // JAVADOC METHOD $$ func_70646_bf
    public int getVerticalFaceSpeed()
    {
        return 40;
    }

    // JAVADOC METHOD $$ func_70625_a
    public void faceEntity(Entity par1Entity, float par2, float par3)
    {
        double d0 = par1Entity.posX - this.posX;
        double d2 = par1Entity.posZ - this.posZ;
        double d1;

        if (par1Entity instanceof EntityLivingBase)
        {
            EntityLivingBase entitylivingbase = (EntityLivingBase)par1Entity;
            d1 = entitylivingbase.posY + (double)entitylivingbase.getEyeHeight() - (this.posY + (double)this.getEyeHeight());
        }
        else
        {
            d1 = (par1Entity.boundingBox.minY + par1Entity.boundingBox.maxY) / 2.0D - (this.posY + (double)this.getEyeHeight());
        }

        double d3 = (double)MathHelper.sqrt_double(d0 * d0 + d2 * d2);
        float f2 = (float)(Math.atan2(d2, d0) * 180.0D / Math.PI) - 90.0F;
        float f3 = (float)(-(Math.atan2(d1, d3) * 180.0D / Math.PI));
        this.rotationPitch = this.updateRotation(this.rotationPitch, f3, par3);
        this.rotationYaw = this.updateRotation(this.rotationYaw, f2, par2);
    }

    // JAVADOC METHOD $$ func_70663_b
    private float updateRotation(float par1, float par2, float par3)
    {
        float f3 = MathHelper.wrapAngleTo180_float(par2 - par1);

        if (f3 > par3)
        {
            f3 = par3;
        }

        if (f3 < -par3)
        {
            f3 = -par3;
        }

        return par1 + f3;
    }

    // JAVADOC METHOD $$ func_70601_bi
    public boolean getCanSpawnHere()
    {
        return this.worldObj.checkNoEntityCollision(this.boundingBox) && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty() && !this.worldObj.isAnyLiquid(this.boundingBox);
    }

    // JAVADOC METHOD $$ func_70603_bj
    public float getRenderSizeModifier()
    {
        return 1.0F;
    }

    // JAVADOC METHOD $$ func_70641_bl
    public int getMaxSpawnedInChunk()
    {
        return 4;
    }

    // JAVADOC METHOD $$ func_82143_as
    public int getMaxSafePointTries()
    {
        if (this.getAttackTarget() == null)
        {
            return 3;
        }
        else
        {
            int i = (int)(this.getHealth() - this.getMaxHealth() * 0.33F);
            i -= (3 - this.worldObj.difficultySetting.func_151525_a()) * 4;

            if (i < 0)
            {
                i = 0;
            }

            return i + 3;
        }
    }

    // JAVADOC METHOD $$ func_70694_bm
    public ItemStack getHeldItem()
    {
        return this.equipment[0];
    }

    // JAVADOC METHOD $$ func_71124_b
    public ItemStack getCurrentItemOrArmor(int par1)
    {
        return this.equipment[par1];
    }

    public ItemStack func_130225_q(int par1)
    {
        return this.equipment[par1 + 1];
    }

    // JAVADOC METHOD $$ func_70062_b
    public void setCurrentItemOrArmor(int par1, ItemStack par2ItemStack)
    {
        this.equipment[par1] = par2ItemStack;
    }

    public ItemStack[] getLastActiveItems()
    {
        return this.equipment;
    }

    // JAVADOC METHOD $$ func_82160_b
    protected void dropEquipment(boolean par1, int par2)
    {
        for (int j = 0; j < this.getLastActiveItems().length; ++j)
        {
            ItemStack itemstack = this.getCurrentItemOrArmor(j);
            boolean flag1 = this.equipmentDropChances[j] > 1.0F;

            if (itemstack != null && (par1 || flag1) && this.rand.nextFloat() - (float)par2 * 0.01F < this.equipmentDropChances[j])
            {
                if (!flag1 && itemstack.isItemStackDamageable())
                {
                    int k = Math.max(itemstack.getMaxDamage() - 25, 1);
                    int l = itemstack.getMaxDamage() - this.rand.nextInt(this.rand.nextInt(k) + 1);

                    if (l > k)
                    {
                        l = k;
                    }

                    if (l < 1)
                    {
                        l = 1;
                    }

                    itemstack.setItemDamage(l);
                }

                this.entityDropItem(itemstack, 0.0F);
            }
        }
    }

    // JAVADOC METHOD $$ func_82164_bB
    protected void addRandomArmor()
    {
        if (this.rand.nextFloat() < 0.15F * this.worldObj.func_147462_b(this.posX, this.posY, this.posZ))
        {
            int i = this.rand.nextInt(2);
            float f = this.worldObj.difficultySetting == EnumDifficulty.HARD ? 0.1F : 0.25F;

            if (this.rand.nextFloat() < 0.095F)
            {
                ++i;
            }

            if (this.rand.nextFloat() < 0.095F)
            {
                ++i;
            }

            if (this.rand.nextFloat() < 0.095F)
            {
                ++i;
            }

            for (int j = 3; j >= 0; --j)
            {
                ItemStack itemstack = this.func_130225_q(j);

                if (j < 3 && this.rand.nextFloat() < f)
                {
                    break;
                }

                if (itemstack == null)
                {
                    Item item = getArmorItemForSlot(j + 1, i);

                    if (item != null)
                    {
                        this.setCurrentItemOrArmor(j + 1, new ItemStack(item));
                    }
                }
            }
        }
    }

    public static int getArmorPosition(ItemStack par0ItemStack)
    {
        if (par0ItemStack.getItem() != Item.func_150898_a(Blocks.pumpkin) && par0ItemStack.getItem() != Items.skull)
        {
            if (par0ItemStack.getItem() instanceof ItemArmor)
            {
                switch (((ItemArmor)par0ItemStack.getItem()).armorType)
                {
                    case 0:
                        return 4;
                    case 1:
                        return 3;
                    case 2:
                        return 2;
                    case 3:
                        return 1;
                }
            }

            return 0;
        }
        else
        {
            return 4;
        }
    }

    // JAVADOC METHOD $$ func_82161_a
    public static Item getArmorItemForSlot(int par0, int par1)
    {
        switch (par0)
        {
            case 4:
                if (par1 == 0)
                {
                    return Items.leather_helmet;
                }
                else if (par1 == 1)
                {
                    return Items.golden_helmet;
                }
                else if (par1 == 2)
                {
                    return Items.chainmail_helmet;
                }
                else if (par1 == 3)
                {
                    return Items.iron_helmet;
                }
                else if (par1 == 4)
                {
                    return Items.diamond_helmet;
                }
            case 3:
                if (par1 == 0)
                {
                    return Items.leather_chestplate;
                }
                else if (par1 == 1)
                {
                    return Items.golden_chestplate;
                }
                else if (par1 == 2)
                {
                    return Items.chainmail_chestplate;
                }
                else if (par1 == 3)
                {
                    return Items.iron_chestplate;
                }
                else if (par1 == 4)
                {
                    return Items.diamond_chestplate;
                }
            case 2:
                if (par1 == 0)
                {
                    return Items.leather_leggings;
                }
                else if (par1 == 1)
                {
                    return Items.golden_leggings;
                }
                else if (par1 == 2)
                {
                    return Items.chainmail_leggings;
                }
                else if (par1 == 3)
                {
                    return Items.iron_leggings;
                }
                else if (par1 == 4)
                {
                    return Items.diamond_leggings;
                }
            case 1:
                if (par1 == 0)
                {
                    return Items.leather_boots;
                }
                else if (par1 == 1)
                {
                    return Items.golden_boots;
                }
                else if (par1 == 2)
                {
                    return Items.chainmail_boots;
                }
                else if (par1 == 3)
                {
                    return Items.iron_boots;
                }
                else if (par1 == 4)
                {
                    return Items.diamond_boots;
                }
            default:
                return null;
        }
    }

    // JAVADOC METHOD $$ func_82162_bC
    protected void enchantEquipment()
    {
        float f = this.worldObj.func_147462_b(this.posX, this.posY, this.posZ);

        if (this.getHeldItem() != null && this.rand.nextFloat() < 0.25F * f)
        {
            EnchantmentHelper.addRandomEnchantment(this.rand, this.getHeldItem(), (int)(5.0F + f * (float)this.rand.nextInt(18)));
        }

        for (int i = 0; i < 4; ++i)
        {
            ItemStack itemstack = this.func_130225_q(i);

            if (itemstack != null && this.rand.nextFloat() < 0.5F * f)
            {
                EnchantmentHelper.addRandomEnchantment(this.rand, itemstack, (int)(5.0F + f * (float)this.rand.nextInt(18)));
            }
        }
    }

    public IEntityLivingData onSpawnWithEgg(IEntityLivingData par1EntityLivingData)
    {
        this.getEntityAttribute(SharedMonsterAttributes.followRange).applyModifier(new AttributeModifier("Random spawn bonus", this.rand.nextGaussian() * 0.05D, 1));
        return par1EntityLivingData;
    }

    // JAVADOC METHOD $$ func_82171_bF
    public boolean canBeSteered()
    {
        return false;
    }

    // JAVADOC METHOD $$ func_70005_c_
    public String getCommandSenderName()
    {
        return this.hasCustomNameTag() ? this.getCustomNameTag() : super.getCommandSenderName();
    }

    public void func_110163_bv()
    {
        this.persistenceRequired = true;
    }

    public void setCustomNameTag(String par1Str)
    {
        this.dataWatcher.updateObject(10, par1Str);
    }

    public String getCustomNameTag()
    {
        return this.dataWatcher.getWatchableObjectString(10);
    }

    public boolean hasCustomNameTag()
    {
        return this.dataWatcher.getWatchableObjectString(10).length() > 0;
    }

    public void setAlwaysRenderNameTag(boolean par1)
    {
        this.dataWatcher.updateObject(11, Byte.valueOf((byte)(par1 ? 1 : 0)));
    }

    public boolean getAlwaysRenderNameTag()
    {
        return this.dataWatcher.getWatchableObjectByte(11) == 1;
    }

    @SideOnly(Side.CLIENT)
    public boolean getAlwaysRenderNameTagForRender()
    {
        return this.getAlwaysRenderNameTag();
    }

    public void setEquipmentDropChance(int par1, float par2)
    {
        this.equipmentDropChances[par1] = par2;
    }

    public boolean canPickUpLoot()
    {
        return this.canPickUpLoot;
    }

    public void setCanPickUpLoot(boolean par1)
    {
        this.canPickUpLoot = par1;
    }

    public boolean isNoDespawnRequired()
    {
        return this.persistenceRequired;
    }

    // JAVADOC METHOD $$ func_130002_c
    public final boolean interactFirst(EntityPlayer par1EntityPlayer)
    {
        if (this.getLeashed() && this.getLeashedToEntity() == par1EntityPlayer)
        {
            this.clearLeashed(true, !par1EntityPlayer.capabilities.isCreativeMode);
            return true;
        }
        else
        {
            ItemStack itemstack = par1EntityPlayer.inventory.getCurrentItem();

            if (itemstack != null && itemstack.getItem() == Items.lead && this.allowLeashing())
            {
                if (!(this instanceof EntityTameable) || !((EntityTameable)this).isTamed())
                {
                    this.setLeashedToEntity(par1EntityPlayer, true);
                    --itemstack.stackSize;
                    return true;
                }

                if (par1EntityPlayer.getCommandSenderName().equalsIgnoreCase(((EntityTameable)this).getOwnerName()))
                {
                    this.setLeashedToEntity(par1EntityPlayer, true);
                    --itemstack.stackSize;
                    return true;
                }
            }

            return this.interact(par1EntityPlayer) ? true : super.interactFirst(par1EntityPlayer);
        }
    }

    // JAVADOC METHOD $$ func_70085_c
    protected boolean interact(EntityPlayer par1EntityPlayer)
    {
        return false;
    }

    protected void func_110159_bB()
    {
        if (this.field_110170_bx != null)
        {
            this.recreateLeash();
        }

        if (this.isLeashed)
        {
            if (this.leashedToEntity == null || this.leashedToEntity.isDead)
            {
                this.clearLeashed(true, true);
            }
        }
    }

    // JAVADOC METHOD $$ func_110160_i
    public void clearLeashed(boolean par1, boolean par2)
    {
        if (this.isLeashed)
        {
            this.isLeashed = false;
            this.leashedToEntity = null;

            if (!this.worldObj.isRemote && par2)
            {
                this.func_145779_a(Items.lead, 1);
            }

            if (!this.worldObj.isRemote && par1 && this.worldObj instanceof WorldServer)
            {
                ((WorldServer)this.worldObj).getEntityTracker().func_151247_a(this, new S1BPacketEntityAttach(1, this, (Entity)null));
            }
        }
    }

    public boolean allowLeashing()
    {
        return !this.getLeashed() && !(this instanceof IMob);
    }

    public boolean getLeashed()
    {
        return this.isLeashed;
    }

    public Entity getLeashedToEntity()
    {
        return this.leashedToEntity;
    }

    // JAVADOC METHOD $$ func_110162_b
    public void setLeashedToEntity(Entity par1Entity, boolean par2)
    {
        this.isLeashed = true;
        this.leashedToEntity = par1Entity;

        if (!this.worldObj.isRemote && par2 && this.worldObj instanceof WorldServer)
        {
            ((WorldServer)this.worldObj).getEntityTracker().func_151247_a(this, new S1BPacketEntityAttach(1, this, this.leashedToEntity));
        }
    }

    private void recreateLeash()
    {
        if (this.isLeashed && this.field_110170_bx != null)
        {
            if (this.field_110170_bx.func_150297_b("UUIDMost", 4) && this.field_110170_bx.func_150297_b("UUIDLeast", 4))
            {
                UUID uuid = new UUID(this.field_110170_bx.getLong("UUIDMost"), this.field_110170_bx.getLong("UUIDLeast"));
                List list = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.boundingBox.expand(10.0D, 10.0D, 10.0D));
                Iterator iterator = list.iterator();

                while (iterator.hasNext())
                {
                    EntityLivingBase entitylivingbase = (EntityLivingBase)iterator.next();

                    if (entitylivingbase.getUniqueID().equals(uuid))
                    {
                        this.leashedToEntity = entitylivingbase;
                        break;
                    }
                }
            }
            else if (this.field_110170_bx.func_150297_b("X", 99) && this.field_110170_bx.func_150297_b("Y", 99) && this.field_110170_bx.func_150297_b("Z", 99))
            {
                int i = this.field_110170_bx.getInteger("X");
                int j = this.field_110170_bx.getInteger("Y");
                int k = this.field_110170_bx.getInteger("Z");
                EntityLeashKnot entityleashknot = EntityLeashKnot.getKnotForBlock(this.worldObj, i, j, k);

                if (entityleashknot == null)
                {
                    entityleashknot = EntityLeashKnot.func_110129_a(this.worldObj, i, j, k);
                }

                this.leashedToEntity = entityleashknot;
            }
            else
            {
                this.clearLeashed(false, true);
            }
        }

        this.field_110170_bx = null;
    }
}