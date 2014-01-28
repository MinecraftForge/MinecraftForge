package net.minecraft.entity.player;

import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.inventory.ContainerHorseInventory;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryMerchant;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemMapBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1EPacketRemoveEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.network.play.server.S31PacketWindowProperty;
import net.minecraft.network.play.server.S36PacketSignEditorOpen;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsFile;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.JsonSerializableSet;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.item.EntityItem;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;

public class EntityPlayerMP extends EntityPlayer implements ICrafting
{
    private static final Logger field_147102_bM = LogManager.getLogger();
    private String translator = "en_US";
    // JAVADOC FIELD $$ field_71135_a
    public NetHandlerPlayServer playerNetServerHandler;
    // JAVADOC FIELD $$ field_71133_b
    public final MinecraftServer mcServer;
    // JAVADOC FIELD $$ field_71134_c
    public final ItemInWorldManager theItemInWorldManager;
    // JAVADOC FIELD $$ field_71131_d
    public double managedPosX;
    // JAVADOC FIELD $$ field_71132_e
    public double managedPosZ;
    // JAVADOC FIELD $$ field_71129_f
    public final List loadedChunks = new LinkedList();
    // JAVADOC FIELD $$ field_71130_g
    public final List destroyedItemsNetCache = new LinkedList();
    private final StatisticsFile field_147103_bO;
    private float field_130068_bO = Float.MIN_VALUE;
    // JAVADOC FIELD $$ field_71149_ch
    private float lastHealth = -1.0E8F;
    // JAVADOC FIELD $$ field_71146_ci
    private int lastFoodLevel = -99999999;
    // JAVADOC FIELD $$ field_71147_cj
    private boolean wasHungry = true;
    // JAVADOC FIELD $$ field_71144_ck
    private int lastExperience = -99999999;
    private int field_147101_bU = 60;
    // JAVADOC FIELD $$ field_71142_cm
    private int renderDistance;
    private EntityPlayer.EnumChatVisibility chatVisibility;
    private boolean chatColours = true;
    private long field_143005_bX = 0L;
    // JAVADOC FIELD $$ field_71139_cq
    public int currentWindowId;
    // JAVADOC FIELD $$ field_71137_h
    public boolean playerInventoryBeingManipulated;
    public int ping;
    // JAVADOC FIELD $$ field_71136_j
    public boolean playerConqueredTheEnd;
    private static final String __OBFID = "CL_00001440";

    public EntityPlayerMP(MinecraftServer p_i45285_1_, WorldServer p_i45285_2_, GameProfile p_i45285_3_, ItemInWorldManager p_i45285_4_)
    {
        super(p_i45285_2_, p_i45285_3_);
        p_i45285_4_.thisPlayerMP = this;
        this.theItemInWorldManager = p_i45285_4_;
        this.renderDistance = p_i45285_1_.getConfigurationManager().getViewDistance();
        ChunkCoordinates chunkcoordinates = p_i45285_2_.provider.getRandomizedSpawnPoint();
        int i = chunkcoordinates.posX;
        int j = chunkcoordinates.posZ;
        int k = chunkcoordinates.posY;

        this.mcServer = p_i45285_1_;
        this.field_147103_bO = p_i45285_1_.getConfigurationManager().func_148538_i(this.getCommandSenderName());
        this.stepHeight = 0.0F;
        this.yOffset = 0.0F;
        this.setLocationAndAngles((double)i + 0.5D, (double)k, (double)j + 0.5D, 0.0F, 0.0F);

        while (!p_i45285_2_.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty())
        {
            this.setPosition(this.posX, this.posY + 1.0D, this.posZ);
        }
    }

    // JAVADOC METHOD $$ func_70037_a
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);

        if (par1NBTTagCompound.func_150297_b("playerGameType", 99))
        {
            if (MinecraftServer.getServer().getForceGamemode())
            {
                this.theItemInWorldManager.setGameType(MinecraftServer.getServer().getGameType());
            }
            else
            {
                this.theItemInWorldManager.setGameType(WorldSettings.GameType.getByID(par1NBTTagCompound.getInteger("playerGameType")));
            }
        }
    }

    // JAVADOC METHOD $$ func_70014_b
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("playerGameType", this.theItemInWorldManager.getGameType().getID());
    }

    // JAVADOC METHOD $$ func_82242_a
    public void addExperienceLevel(int par1)
    {
        super.addExperienceLevel(par1);
        this.lastExperience = -1;
    }

    public void addSelfToInternalCraftingInventory()
    {
        this.openContainer.addCraftingToCrafters(this);
    }

    // JAVADOC METHOD $$ func_71061_d_
    protected void resetHeight()
    {
        this.yOffset = 0.0F;
    }

    public float getEyeHeight()
    {
        return super.getEyeHeight();
    }

    // JAVADOC METHOD $$ func_70071_h_
    public void onUpdate()
    {
        this.theItemInWorldManager.updateBlockRemoving();
        --this.field_147101_bU;

        if (this.hurtResistantTime > 0)
        {
            --this.hurtResistantTime;
        }

        this.openContainer.detectAndSendChanges();

        if (!this.worldObj.isRemote && !ForgeHooks.canInteractWith(this, this.openContainer))
        {
            this.closeScreen();
            this.openContainer = this.inventoryContainer;
        }

        while (!this.destroyedItemsNetCache.isEmpty())
        {
            int i = Math.min(this.destroyedItemsNetCache.size(), 127);
            int[] aint = new int[i];
            Iterator iterator = this.destroyedItemsNetCache.iterator();
            int j = 0;

            while (iterator.hasNext() && j < i)
            {
                aint[j++] = ((Integer)iterator.next()).intValue();
                iterator.remove();
            }

            this.playerNetServerHandler.func_147359_a(new S13PacketDestroyEntities(aint));
        }

        if (!this.loadedChunks.isEmpty())
        {
            ArrayList arraylist = new ArrayList();
            Iterator iterator1 = this.loadedChunks.iterator();
            ArrayList arraylist1 = new ArrayList();
            Chunk chunk;

            while (iterator1.hasNext() && arraylist.size() < S26PacketMapChunkBulk.func_149258_c())
            {
                ChunkCoordIntPair chunkcoordintpair = (ChunkCoordIntPair)iterator1.next();

                if (chunkcoordintpair != null)
                {
                    if (this.worldObj.blockExists(chunkcoordintpair.chunkXPos << 4, 0, chunkcoordintpair.chunkZPos << 4))
                    {
                        chunk = this.worldObj.getChunkFromChunkCoords(chunkcoordintpair.chunkXPos, chunkcoordintpair.chunkZPos);

                        if (chunk.func_150802_k())
                        {
                            arraylist.add(chunk);                            
                            arraylist1.addAll(((WorldServer)this.worldObj).func_147486_a(chunkcoordintpair.chunkXPos * 16, 0, chunkcoordintpair.chunkZPos * 16, chunkcoordintpair.chunkXPos * 16 + 15, 256, chunkcoordintpair.chunkZPos * 16 + 16));
                            //BugFix: 16 makes it load an extra chunk, which isn't associated with a player, which makes it not unload unless a player walks near it.
                            iterator1.remove();
                        }
                    }
                }
                else
                {
                    iterator1.remove();
                }
            }

            if (!arraylist.isEmpty())
            {
                this.playerNetServerHandler.func_147359_a(new S26PacketMapChunkBulk(arraylist));
                Iterator iterator2 = arraylist1.iterator();

                while (iterator2.hasNext())
                {
                    TileEntity tileentity = (TileEntity)iterator2.next();
                    this.func_147097_b(tileentity);
                }

                iterator2 = arraylist.iterator();

                while (iterator2.hasNext())
                {
                    chunk = (Chunk)iterator2.next();
                    this.getServerForPlayer().getEntityTracker().func_85172_a(this, chunk);
                    MinecraftForge.EVENT_BUS.post(new ChunkWatchEvent.Watch(chunk.getChunkCoordIntPair(), this));
                }
            }
        }

        if (this.field_143005_bX > 0L && this.mcServer.func_143007_ar() > 0 && MinecraftServer.getSystemTimeMillis() - this.field_143005_bX > (long)(this.mcServer.func_143007_ar() * 1000 * 60))
        {
            this.playerNetServerHandler.func_147360_c("You have been idle for too long!");
        }
    }

    public void onUpdateEntity()
    {
        try
        {
            super.onUpdate();

            for (int i = 0; i < this.inventory.getSizeInventory(); ++i)
            {
                ItemStack itemstack = this.inventory.getStackInSlot(i);

                if (itemstack != null && itemstack.getItem().isMap())
                {
                    Packet packet = ((ItemMapBase)itemstack.getItem()).func_150911_c(itemstack, this.worldObj, this);

                    if (packet != null)
                    {
                        this.playerNetServerHandler.func_147359_a(packet);
                    }
                }
            }

            if (this.getHealth() != this.lastHealth || this.lastFoodLevel != this.foodStats.getFoodLevel() || this.foodStats.getSaturationLevel() == 0.0F != this.wasHungry)
            {
                this.playerNetServerHandler.func_147359_a(new S06PacketUpdateHealth(this.getHealth(), this.foodStats.getFoodLevel(), this.foodStats.getSaturationLevel()));
                this.lastHealth = this.getHealth();
                this.lastFoodLevel = this.foodStats.getFoodLevel();
                this.wasHungry = this.foodStats.getSaturationLevel() == 0.0F;
            }

            if (this.getHealth() + this.getAbsorptionAmount() != this.field_130068_bO)
            {
                this.field_130068_bO = this.getHealth() + this.getAbsorptionAmount();
                Collection collection = this.getWorldScoreboard().func_96520_a(IScoreObjectiveCriteria.health);
                Iterator iterator = collection.iterator();

                while (iterator.hasNext())
                {
                    ScoreObjective scoreobjective = (ScoreObjective)iterator.next();
                    this.getWorldScoreboard().func_96529_a(this.getCommandSenderName(), scoreobjective).func_96651_a(Arrays.asList(new EntityPlayer[] {this}));
                }
            }

            if (this.experienceTotal != this.lastExperience)
            {
                this.lastExperience = this.experienceTotal;
                this.playerNetServerHandler.func_147359_a(new S1FPacketSetExperience(this.experience, this.experienceTotal, this.experienceLevel));
            }

            if (this.ticksExisted % 20 * 5 == 0 && !this.func_147099_x().hasAchievementUnlocked(AchievementList.field_150961_L))
            {
                this.func_147098_j();
            }
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Ticking player");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Player being ticked");
            this.addEntityCrashInfo(crashreportcategory);
            throw new ReportedException(crashreport);
        }
    }

    protected void func_147098_j()
    {
        BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posZ));

        if (biomegenbase != null)
        {
            String s = biomegenbase.biomeName;
            JsonSerializableSet jsonserializableset = (JsonSerializableSet)this.func_147099_x().func_150870_b(AchievementList.field_150961_L);

            if (jsonserializableset == null)
            {
                jsonserializableset = (JsonSerializableSet)this.func_147099_x().func_150872_a(AchievementList.field_150961_L, new JsonSerializableSet());
            }

            jsonserializableset.add(s);

            if (this.func_147099_x().canUnlockAchievement(AchievementList.field_150961_L) && jsonserializableset.size() == BiomeGenBase.field_150597_n.size())
            {
                HashSet hashset = Sets.newHashSet(BiomeGenBase.field_150597_n);
                Iterator iterator = jsonserializableset.iterator();

                while (iterator.hasNext())
                {
                    String s1 = (String)iterator.next();
                    Iterator iterator1 = hashset.iterator();

                    while (iterator1.hasNext())
                    {
                        BiomeGenBase biomegenbase1 = (BiomeGenBase)iterator1.next();

                        if (biomegenbase1.biomeName.equals(s1))
                        {
                            iterator1.remove();
                        }
                    }

                    if (hashset.isEmpty())
                    {
                        break;
                    }
                }

                if (hashset.isEmpty())
                {
                    this.triggerAchievement(AchievementList.field_150961_L);
                }
            }
        }
    }

    // JAVADOC METHOD $$ func_70645_a
    public void onDeath(DamageSource par1DamageSource)
    {
        if (ForgeHooks.onLivingDeath(this, par1DamageSource)) return;
        this.mcServer.getConfigurationManager().func_148539_a(this.func_110142_aN().func_151521_b());

        if (!this.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory"))
        {
            captureDrops = true;
            capturedDrops.clear();

            this.inventory.dropAllItems();

            captureDrops = false;
            PlayerDropsEvent event = new PlayerDropsEvent(this, par1DamageSource, capturedDrops, recentlyHit > 0);
            if (!MinecraftForge.EVENT_BUS.post(event))
            {
                for (EntityItem item : capturedDrops)
                {
                    joinEntityItemWithWorld(item);
                }
            }
        }

        Collection collection = this.worldObj.getScoreboard().func_96520_a(IScoreObjectiveCriteria.deathCount);
        Iterator iterator = collection.iterator();

        while (iterator.hasNext())
        {
            ScoreObjective scoreobjective = (ScoreObjective)iterator.next();
            Score score = this.getWorldScoreboard().func_96529_a(this.getCommandSenderName(), scoreobjective);
            score.func_96648_a();
        }

        EntityLivingBase entitylivingbase = this.func_94060_bK();

        if (entitylivingbase != null)
        {
            int i = EntityList.getEntityID(entitylivingbase);
            EntityList.EntityEggInfo entityegginfo = (EntityList.EntityEggInfo)EntityList.entityEggs.get(Integer.valueOf(i));

            if (entityegginfo != null)
            {
                this.addStat(entityegginfo.field_151513_e, 1);
            }

            entitylivingbase.addToPlayerScore(this, this.scoreValue);
        }

        this.addStat(StatList.deathsStat, 1);
    }

    // JAVADOC METHOD $$ func_70097_a
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2)
    {
        if (this.isEntityInvulnerable())
        {
            return false;
        }
        else
        {
            boolean flag = this.mcServer.isDedicatedServer() && this.mcServer.isPVPEnabled() && "fall".equals(par1DamageSource.damageType);

            if (!flag && this.field_147101_bU > 0 && par1DamageSource != DamageSource.outOfWorld)
            {
                return false;
            }
            else
            {
                if (par1DamageSource instanceof EntityDamageSource)
                {
                    Entity entity = par1DamageSource.getEntity();

                    if (entity instanceof EntityPlayer && !this.canAttackPlayer((EntityPlayer)entity))
                    {
                        return false;
                    }

                    if (entity instanceof EntityArrow)
                    {
                        EntityArrow entityarrow = (EntityArrow)entity;

                        if (entityarrow.shootingEntity instanceof EntityPlayer && !this.canAttackPlayer((EntityPlayer)entityarrow.shootingEntity))
                        {
                            return false;
                        }
                    }
                }

                return super.attackEntityFrom(par1DamageSource, par2);
            }
        }
    }

    public boolean canAttackPlayer(EntityPlayer par1EntityPlayer)
    {
        return !this.mcServer.isPVPEnabled() ? false : super.canAttackPlayer(par1EntityPlayer);
    }

    // JAVADOC METHOD $$ func_71027_c
    public void travelToDimension(int par1)
    {
        if (this.dimension == 1 && par1 == 1)
        {
            this.triggerAchievement(AchievementList.theEnd2);
            this.worldObj.removeEntity(this);
            this.playerConqueredTheEnd = true;
            this.playerNetServerHandler.func_147359_a(new S2BPacketChangeGameState(4, 0.0F));
        }
        else
        {
            if (this.dimension == 0 && par1 == 1)
            {
                this.triggerAchievement(AchievementList.theEnd);
                ChunkCoordinates chunkcoordinates = this.mcServer.worldServerForDimension(par1).getEntrancePortalLocation();

                if (chunkcoordinates != null)
                {
                    this.playerNetServerHandler.func_147364_a((double)chunkcoordinates.posX, (double)chunkcoordinates.posY, (double)chunkcoordinates.posZ, 0.0F, 0.0F);
                }

                par1 = 1;
            }
            else
            {
                this.triggerAchievement(AchievementList.portal);
            }

            this.mcServer.getConfigurationManager().transferPlayerToDimension(this, par1);
            this.lastExperience = -1;
            this.lastHealth = -1.0F;
            this.lastFoodLevel = -1;
        }
    }

    private void func_147097_b(TileEntity p_147097_1_)
    {
        if (p_147097_1_ != null)
        {
            Packet packet = p_147097_1_.func_145844_m();

            if (packet != null)
            {
                this.playerNetServerHandler.func_147359_a(packet);
            }
        }
    }

    // JAVADOC METHOD $$ func_71001_a
    public void onItemPickup(Entity par1Entity, int par2)
    {
        super.onItemPickup(par1Entity, par2);
        this.openContainer.detectAndSendChanges();
    }

    // JAVADOC METHOD $$ func_71018_a
    public EntityPlayer.EnumStatus sleepInBedAt(int par1, int par2, int par3)
    {
        EntityPlayer.EnumStatus enumstatus = super.sleepInBedAt(par1, par2, par3);

        if (enumstatus == EntityPlayer.EnumStatus.OK)
        {
            S0APacketUseBed s0apacketusebed = new S0APacketUseBed(this, par1, par2, par3);
            this.getServerForPlayer().getEntityTracker().func_151247_a(this, s0apacketusebed);
            this.playerNetServerHandler.func_147364_a(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            this.playerNetServerHandler.func_147359_a(s0apacketusebed);
        }

        return enumstatus;
    }

    // JAVADOC METHOD $$ func_70999_a
    public void wakeUpPlayer(boolean par1, boolean par2, boolean par3)
    {
        if (this.isPlayerSleeping())
        {
            this.getServerForPlayer().getEntityTracker().func_151248_b(this, new S0BPacketAnimation(this, 2));
        }

        super.wakeUpPlayer(par1, par2, par3);

        if (this.playerNetServerHandler != null)
        {
            this.playerNetServerHandler.func_147364_a(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
        }
    }

    // JAVADOC METHOD $$ func_70078_a
    public void mountEntity(Entity par1Entity)
    {
        super.mountEntity(par1Entity);
        this.playerNetServerHandler.func_147359_a(new S1BPacketEntityAttach(0, this, this.ridingEntity));
        this.playerNetServerHandler.func_147364_a(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
    }

    // JAVADOC METHOD $$ func_70064_a
    protected void updateFallState(double par1, boolean par3) {}

    // JAVADOC METHOD $$ func_71122_b
    public void updateFlyingState(double par1, boolean par3)
    {
        super.updateFallState(par1, par3);
    }

    public void func_146100_a(TileEntity p_146100_1_)
    {
        if (p_146100_1_ instanceof TileEntitySign)
        {
            ((TileEntitySign)p_146100_1_).func_145912_a(this);
            this.playerNetServerHandler.func_147359_a(new S36PacketSignEditorOpen(p_146100_1_.field_145851_c, p_146100_1_.field_145848_d, p_146100_1_.field_145849_e));
        }
    }

    public void incrementWindowID()
    {
        this.currentWindowId = this.currentWindowId % 100 + 1;
    }

    // JAVADOC METHOD $$ func_71058_b
    public void displayGUIWorkbench(int par1, int par2, int par3)
    {
        this.incrementWindowID();
        this.playerNetServerHandler.func_147359_a(new S2DPacketOpenWindow(this.currentWindowId, 1, "Crafting", 9, true));
        this.openContainer = new ContainerWorkbench(this.inventory, this.worldObj, par1, par2, par3);
        this.openContainer.windowId = this.currentWindowId;
        this.openContainer.addCraftingToCrafters(this);
    }

    public void displayGUIEnchantment(int par1, int par2, int par3, String par4Str)
    {
        this.incrementWindowID();
        this.playerNetServerHandler.func_147359_a(new S2DPacketOpenWindow(this.currentWindowId, 4, par4Str == null ? "" : par4Str, 9, par4Str != null));
        this.openContainer = new ContainerEnchantment(this.inventory, this.worldObj, par1, par2, par3);
        this.openContainer.windowId = this.currentWindowId;
        this.openContainer.addCraftingToCrafters(this);
    }

    // JAVADOC METHOD $$ func_82244_d
    public void displayGUIAnvil(int par1, int par2, int par3)
    {
        this.incrementWindowID();
        this.playerNetServerHandler.func_147359_a(new S2DPacketOpenWindow(this.currentWindowId, 8, "Repairing", 9, true));
        this.openContainer = new ContainerRepair(this.inventory, this.worldObj, par1, par2, par3, this);
        this.openContainer.windowId = this.currentWindowId;
        this.openContainer.addCraftingToCrafters(this);
    }

    // JAVADOC METHOD $$ func_71007_a
    public void displayGUIChest(IInventory par1IInventory)
    {
        if (this.openContainer != this.inventoryContainer)
        {
            this.closeScreen();
        }

        this.incrementWindowID();
        this.playerNetServerHandler.func_147359_a(new S2DPacketOpenWindow(this.currentWindowId, 0, par1IInventory.func_145825_b(), par1IInventory.getSizeInventory(), par1IInventory.func_145818_k_()));
        this.openContainer = new ContainerChest(this.inventory, par1IInventory);
        this.openContainer.windowId = this.currentWindowId;
        this.openContainer.addCraftingToCrafters(this);
    }

    public void func_146093_a(TileEntityHopper p_146093_1_)
    {
        this.incrementWindowID();
        this.playerNetServerHandler.func_147359_a(new S2DPacketOpenWindow(this.currentWindowId, 9, p_146093_1_.func_145825_b(), p_146093_1_.getSizeInventory(), p_146093_1_.func_145818_k_()));
        this.openContainer = new ContainerHopper(this.inventory, p_146093_1_);
        this.openContainer.windowId = this.currentWindowId;
        this.openContainer.addCraftingToCrafters(this);
    }

    public void displayGUIHopperMinecart(EntityMinecartHopper par1EntityMinecartHopper)
    {
        this.incrementWindowID();
        this.playerNetServerHandler.func_147359_a(new S2DPacketOpenWindow(this.currentWindowId, 9, par1EntityMinecartHopper.func_145825_b(), par1EntityMinecartHopper.getSizeInventory(), par1EntityMinecartHopper.func_145818_k_()));
        this.openContainer = new ContainerHopper(this.inventory, par1EntityMinecartHopper);
        this.openContainer.windowId = this.currentWindowId;
        this.openContainer.addCraftingToCrafters(this);
    }

    public void func_146101_a(TileEntityFurnace p_146101_1_)
    {
        this.incrementWindowID();
        this.playerNetServerHandler.func_147359_a(new S2DPacketOpenWindow(this.currentWindowId, 2, p_146101_1_.func_145825_b(), p_146101_1_.getSizeInventory(), p_146101_1_.func_145818_k_()));
        this.openContainer = new ContainerFurnace(this.inventory, p_146101_1_);
        this.openContainer.windowId = this.currentWindowId;
        this.openContainer.addCraftingToCrafters(this);
    }

    public void func_146102_a(TileEntityDispenser p_146102_1_)
    {
        this.incrementWindowID();
        this.playerNetServerHandler.func_147359_a(new S2DPacketOpenWindow(this.currentWindowId, p_146102_1_ instanceof TileEntityDropper ? 10 : 3, p_146102_1_.func_145825_b(), p_146102_1_.getSizeInventory(), p_146102_1_.func_145818_k_()));
        this.openContainer = new ContainerDispenser(this.inventory, p_146102_1_);
        this.openContainer.windowId = this.currentWindowId;
        this.openContainer.addCraftingToCrafters(this);
    }

    public void func_146098_a(TileEntityBrewingStand p_146098_1_)
    {
        this.incrementWindowID();
        this.playerNetServerHandler.func_147359_a(new S2DPacketOpenWindow(this.currentWindowId, 5, p_146098_1_.func_145825_b(), p_146098_1_.getSizeInventory(), p_146098_1_.func_145818_k_()));
        this.openContainer = new ContainerBrewingStand(this.inventory, p_146098_1_);
        this.openContainer.windowId = this.currentWindowId;
        this.openContainer.addCraftingToCrafters(this);
    }

    public void func_146104_a(TileEntityBeacon p_146104_1_)
    {
        this.incrementWindowID();
        this.playerNetServerHandler.func_147359_a(new S2DPacketOpenWindow(this.currentWindowId, 7, p_146104_1_.func_145825_b(), p_146104_1_.getSizeInventory(), p_146104_1_.func_145818_k_()));
        this.openContainer = new ContainerBeacon(this.inventory, p_146104_1_);
        this.openContainer.windowId = this.currentWindowId;
        this.openContainer.addCraftingToCrafters(this);
    }

    public void displayGUIMerchant(IMerchant par1IMerchant, String par2Str)
    {
        this.incrementWindowID();
        this.openContainer = new ContainerMerchant(this.inventory, par1IMerchant, this.worldObj);
        this.openContainer.windowId = this.currentWindowId;
        this.openContainer.addCraftingToCrafters(this);
        InventoryMerchant inventorymerchant = ((ContainerMerchant)this.openContainer).getMerchantInventory();
        this.playerNetServerHandler.func_147359_a(new S2DPacketOpenWindow(this.currentWindowId, 6, par2Str == null ? "" : par2Str, inventorymerchant.getSizeInventory(), par2Str != null));
        MerchantRecipeList merchantrecipelist = par1IMerchant.getRecipes(this);

        if (merchantrecipelist != null)
        {
            try
            {
                PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());
                packetbuffer.writeInt(this.currentWindowId);
                merchantrecipelist.func_151391_a(packetbuffer);
                this.playerNetServerHandler.func_147359_a(new S3FPacketCustomPayload("MC|TrList", packetbuffer));
            }
            catch (IOException ioexception)
            {
                field_147102_bM.error("Couldn\'t send trade list", ioexception);
            }
        }
    }

    public void displayGUIHorse(EntityHorse par1EntityHorse, IInventory par2IInventory)
    {
        if (this.openContainer != this.inventoryContainer)
        {
            this.closeScreen();
        }

        this.incrementWindowID();
        this.playerNetServerHandler.func_147359_a(new S2DPacketOpenWindow(this.currentWindowId, 11, par2IInventory.func_145825_b(), par2IInventory.getSizeInventory(), par2IInventory.func_145818_k_(), par1EntityHorse.func_145782_y()));
        this.openContainer = new ContainerHorseInventory(this.inventory, par2IInventory, par1EntityHorse);
        this.openContainer.windowId = this.currentWindowId;
        this.openContainer.addCraftingToCrafters(this);
    }

    // JAVADOC METHOD $$ func_71111_a
    public void sendSlotContents(Container par1Container, int par2, ItemStack par3ItemStack)
    {
        if (!(par1Container.getSlot(par2) instanceof SlotCrafting))
        {
            if (!this.playerInventoryBeingManipulated)
            {
                this.playerNetServerHandler.func_147359_a(new S2FPacketSetSlot(par1Container.windowId, par2, par3ItemStack));
            }
        }
    }

    public void sendContainerToPlayer(Container par1Container)
    {
        this.sendContainerAndContentsToPlayer(par1Container, par1Container.getInventory());
    }

    public void sendContainerAndContentsToPlayer(Container par1Container, List par2List)
    {
        this.playerNetServerHandler.func_147359_a(new S30PacketWindowItems(par1Container.windowId, par2List));
        this.playerNetServerHandler.func_147359_a(new S2FPacketSetSlot(-1, -1, this.inventory.getItemStack()));
    }

    // JAVADOC METHOD $$ func_71112_a
    public void sendProgressBarUpdate(Container par1Container, int par2, int par3)
    {
        this.playerNetServerHandler.func_147359_a(new S31PacketWindowProperty(par1Container.windowId, par2, par3));
    }

    // JAVADOC METHOD $$ func_71053_j
    public void closeScreen()
    {
        this.playerNetServerHandler.func_147359_a(new S2EPacketCloseWindow(this.openContainer.windowId));
        this.closeContainer();
    }

    // JAVADOC METHOD $$ func_71113_k
    public void updateHeldItem()
    {
        if (!this.playerInventoryBeingManipulated)
        {
            this.playerNetServerHandler.func_147359_a(new S2FPacketSetSlot(-1, -1, this.inventory.getItemStack()));
        }
    }

    // JAVADOC METHOD $$ func_71128_l
    public void closeContainer()
    {
        this.openContainer.onContainerClosed(this);
        this.openContainer = this.inventoryContainer;
    }

    public void setEntityActionState(float par1, float par2, boolean par3, boolean par4)
    {
        if (this.ridingEntity != null)
        {
            if (par1 >= -1.0F && par1 <= 1.0F)
            {
                this.moveStrafing = par1;
            }

            if (par2 >= -1.0F && par2 <= 1.0F)
            {
                this.moveForward = par2;
            }

            this.isJumping = par3;
            this.setSneaking(par4);
        }
    }

    // JAVADOC METHOD $$ func_71064_a
    public void addStat(StatBase par1StatBase, int par2)
    {
        if (par1StatBase != null)
        {
            this.field_147103_bO.func_150871_b(this, par1StatBase, par2);
            Iterator iterator = this.getWorldScoreboard().func_96520_a(par1StatBase.func_150952_k()).iterator();

            while (iterator.hasNext())
            {
                ScoreObjective scoreobjective = (ScoreObjective)iterator.next();
                this.getWorldScoreboard().func_96529_a(this.getCommandSenderName(), scoreobjective).func_96648_a();
            }

            if (this.field_147103_bO.func_150879_e())
            {
                this.field_147103_bO.func_150876_a(this);
            }
        }
    }

    public void mountEntityAndWakeUp()
    {
        if (this.riddenByEntity != null)
        {
            this.riddenByEntity.mountEntity(this);
        }

        if (this.sleeping)
        {
            this.wakeUpPlayer(true, false, false);
        }
    }

    // JAVADOC METHOD $$ func_71118_n
    public void setPlayerHealthUpdated()
    {
        this.lastHealth = -1.0E8F;
    }

    public void func_146105_b(IChatComponent p_146105_1_)
    {
        this.playerNetServerHandler.func_147359_a(new S02PacketChat(p_146105_1_));
    }

    // JAVADOC METHOD $$ func_71036_o
    protected void onItemUseFinish()
    {
        this.playerNetServerHandler.func_147359_a(new S19PacketEntityStatus(this, (byte)9));
        super.onItemUseFinish();
    }

    // JAVADOC METHOD $$ func_71008_a
    public void setItemInUse(ItemStack par1ItemStack, int par2)
    {
        super.setItemInUse(par1ItemStack, par2);

        if (par1ItemStack != null && par1ItemStack.getItem() != null && par1ItemStack.getItem().getItemUseAction(par1ItemStack) == EnumAction.eat)
        {
            this.getServerForPlayer().getEntityTracker().func_151248_b(this, new S0BPacketAnimation(this, 3));
        }
    }

    // JAVADOC METHOD $$ func_71049_a
    public void clonePlayer(EntityPlayer par1EntityPlayer, boolean par2)
    {
        super.clonePlayer(par1EntityPlayer, par2);
        this.lastExperience = -1;
        this.lastHealth = -1.0F;
        this.lastFoodLevel = -1;
        this.destroyedItemsNetCache.addAll(((EntityPlayerMP)par1EntityPlayer).destroyedItemsNetCache);
    }

    protected void onNewPotionEffect(PotionEffect par1PotionEffect)
    {
        super.onNewPotionEffect(par1PotionEffect);
        this.playerNetServerHandler.func_147359_a(new S1DPacketEntityEffect(this.func_145782_y(), par1PotionEffect));
    }

    protected void onChangedPotionEffect(PotionEffect par1PotionEffect, boolean par2)
    {
        super.onChangedPotionEffect(par1PotionEffect, par2);
        this.playerNetServerHandler.func_147359_a(new S1DPacketEntityEffect(this.func_145782_y(), par1PotionEffect));
    }

    protected void onFinishedPotionEffect(PotionEffect par1PotionEffect)
    {
        super.onFinishedPotionEffect(par1PotionEffect);
        this.playerNetServerHandler.func_147359_a(new S1EPacketRemoveEntityEffect(this.func_145782_y(), par1PotionEffect));
    }

    // JAVADOC METHOD $$ func_70634_a
    public void setPositionAndUpdate(double par1, double par3, double par5)
    {
        this.playerNetServerHandler.func_147364_a(par1, par3, par5, this.rotationYaw, this.rotationPitch);
    }

    // JAVADOC METHOD $$ func_71009_b
    public void onCriticalHit(Entity par1Entity)
    {
        this.getServerForPlayer().getEntityTracker().func_151248_b(this, new S0BPacketAnimation(par1Entity, 4));
    }

    public void onEnchantmentCritical(Entity par1Entity)
    {
        this.getServerForPlayer().getEntityTracker().func_151248_b(this, new S0BPacketAnimation(par1Entity, 5));
    }

    // JAVADOC METHOD $$ func_71016_p
    public void sendPlayerAbilities()
    {
        if (this.playerNetServerHandler != null)
        {
            this.playerNetServerHandler.func_147359_a(new S39PacketPlayerAbilities(this.capabilities));
        }
    }

    public WorldServer getServerForPlayer()
    {
        return (WorldServer)this.worldObj;
    }

    // JAVADOC METHOD $$ func_71033_a
    public void setGameType(WorldSettings.GameType par1EnumGameType)
    {
        this.theItemInWorldManager.setGameType(par1EnumGameType);
        this.playerNetServerHandler.func_147359_a(new S2BPacketChangeGameState(3, (float)par1EnumGameType.getID()));
    }

    public void func_145747_a(IChatComponent p_145747_1_)
    {
        this.playerNetServerHandler.func_147359_a(new S02PacketChat(p_145747_1_));
    }

    // JAVADOC METHOD $$ func_70003_b
    public boolean canCommandSenderUseCommand(int par1, String par2Str)
    {
        return "seed".equals(par2Str) && !this.mcServer.isDedicatedServer() ? true : (!"tell".equals(par2Str) && !"help".equals(par2Str) && !"me".equals(par2Str) ? (this.mcServer.getConfigurationManager().isPlayerOpped(this.getCommandSenderName()) ? this.mcServer.func_110455_j() >= par1 : false) : true);
    }

    // JAVADOC METHOD $$ func_71114_r
    public String getPlayerIP()
    {
        String s = this.playerNetServerHandler.field_147371_a.getSocketAddress().toString();
        s = s.substring(s.indexOf("/") + 1);
        s = s.substring(0, s.indexOf(":"));
        return s;
    }

    public void func_147100_a(C15PacketClientSettings p_147100_1_)
    {
        this.translator = p_147100_1_.func_149524_c();
        int i = 256 >> p_147100_1_.func_149521_d();

        if (i > 3 && i < 15)
        {
            this.renderDistance = i;
        }

        this.chatVisibility = p_147100_1_.func_149523_e();
        this.chatColours = p_147100_1_.func_149520_f();

        if (this.mcServer.isSinglePlayer() && this.mcServer.getServerOwner().equals(this.getCommandSenderName()))
        {
            this.mcServer.func_147139_a(p_147100_1_.func_149518_g());
        }

        this.setHideCape(1, !p_147100_1_.func_149519_h());
    }

    public EntityPlayer.EnumChatVisibility func_147096_v()
    {
        return this.chatVisibility;
    }

    public void func_147095_a(String p_147095_1_)
    {
        this.playerNetServerHandler.func_147359_a(new S3FPacketCustomPayload("MC|RPack", p_147095_1_.getBytes(Charsets.UTF_8)));
    }

    // JAVADOC METHOD $$ func_82114_b
    public ChunkCoordinates getPlayerCoordinates()
    {
        return new ChunkCoordinates(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY + 0.5D), MathHelper.floor_double(this.posZ));
    }

    public void func_143004_u()
    {
        this.field_143005_bX = MinecraftServer.getSystemTimeMillis();
    }

    public StatisticsFile func_147099_x()
    {
        return this.field_147103_bO;
    }

    /* ===================================== FORGE START =====================================*/
    /**
     * Returns the default eye height of the player
     * @return player default eye height
     */
    @Override
    public float getDefaultEyeHeight()
    {
        return 1.62F;
    }
}