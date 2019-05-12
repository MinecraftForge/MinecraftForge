/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.event;

import java.io.File;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.advancements.Advancement;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.Explosion;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent.OverlayType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.brewing.PlayerBrewedPotionEvent;
import net.minecraftforge.event.brewing.PotionBrewEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.living.LivingPackSizeEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.AllowDespawn;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.living.ZombieEvent.SummonAidEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.SleepingLocationCheckEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.event.terraingen.ChunkGeneratorEvent;
import net.minecraftforge.event.village.MerchantTradeOffersEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.CreateFluidSourceEvent;
import net.minecraftforge.event.world.BlockEvent.EntityMultiPlaceEvent;
import net.minecraftforge.event.world.BlockEvent.EntityPlaceEvent;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.event.world.SaplingGrowTreeEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.Event.Result;

public class ForgeEventFactory
{

    public static boolean onMultiBlockPlace(@Nullable Entity entity, List<BlockSnapshot> blockSnapshots, EnumFacing direction)
    {
        BlockSnapshot snap = blockSnapshots.get(0);
        IBlockState placedAgainst = snap.getWorld().getBlockState(snap.getPos().offset(direction.getOpposite()));
        EntityMultiPlaceEvent event = new EntityMultiPlaceEvent(blockSnapshots, placedAgainst, entity);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onBlockPlace(@Nullable Entity entity, @Nonnull BlockSnapshot blockSnapshot, @Nonnull EnumFacing direction)
    {
        IBlockState placedAgainst = blockSnapshot.getWorld().getBlockState(blockSnapshot.getPos().offset(direction.getOpposite()));
        EntityPlaceEvent event = new BlockEvent.EntityPlaceEvent(blockSnapshot, placedAgainst, entity);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static NeighborNotifyEvent onNeighborNotify(World world, BlockPos pos, IBlockState state, EnumSet<EnumFacing> notifiedSides, boolean forceRedstoneUpdate)
    {
        NeighborNotifyEvent event = new NeighborNotifyEvent(world, pos, state, notifiedSides, forceRedstoneUpdate);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static boolean doPlayerHarvestCheck(EntityPlayer player, IBlockState state, boolean success)
    {
        PlayerEvent.HarvestCheck event = new PlayerEvent.HarvestCheck(player, state, success);
        MinecraftForge.EVENT_BUS.post(event);
        return event.canHarvest();
    }

    public static float getBreakSpeed(EntityPlayer player, IBlockState state, float original, BlockPos pos)
    {
        PlayerEvent.BreakSpeed event = new PlayerEvent.BreakSpeed(player, state, original, pos);
        return (MinecraftForge.EVENT_BUS.post(event) ? -1 : event.getNewSpeed());
    }

    public static void onPlayerDestroyItem(EntityPlayer player, @Nonnull ItemStack stack, @Nullable EnumHand hand)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(player, stack, hand));
    }

    public static Result canEntitySpawn(EntityLiving entity, IWorld world, double x, double y, double z, MobSpawnerBaseLogic spawner)
    {
        if (entity == null)
            return Result.DEFAULT;
        LivingSpawnEvent.CheckSpawn event = new LivingSpawnEvent.CheckSpawn(entity, world, x, y, z, spawner);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult();
    }

    public static boolean canEntitySpawnSpawner(EntityLiving entity, World world, float x, float y, float z, MobSpawnerBaseLogic spawner)
    {
        Result result = canEntitySpawn(entity, world, x, y, z, spawner);
        if (result == Result.DEFAULT)
            return entity.canSpawn(world, true) && entity.isNotColliding(); // vanilla logic
        else
            return result == Result.ALLOW;
    }

    public static boolean doSpecialSpawn(EntityLiving entity, World world, float x, float y, float z, MobSpawnerBaseLogic spawner)
    {
        return MinecraftForge.EVENT_BUS.post(new LivingSpawnEvent.SpecialSpawn(entity, world, x, y, z, spawner));
    }

    public static Result canEntityDespawn(EntityLiving entity)
    {
        AllowDespawn event = new AllowDespawn(entity);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult();
    }

    public static int getItemBurnTime(@Nonnull ItemStack itemStack, int burnTime)
    {
        FurnaceFuelBurnTimeEvent event = new FurnaceFuelBurnTimeEvent(itemStack, burnTime);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getBurnTime();
    }

    public static int getExperienceDrop(EntityLivingBase entity, EntityPlayer attackingPlayer, int originalExperience)
    {
       LivingExperienceDropEvent event = new LivingExperienceDropEvent(entity, attackingPlayer, originalExperience);
       if (MinecraftForge.EVENT_BUS.post(event))
       {
           return 0;
       }
       return event.getDroppedExperience();
    }

    @Nullable
    public static List<Biome.SpawnListEntry> getPotentialSpawns(WorldServer world, EnumCreatureType type, BlockPos pos, List<Biome.SpawnListEntry> oldList)
    {
        WorldEvent.PotentialSpawns event = new WorldEvent.PotentialSpawns(world, type, pos, oldList);
        if (MinecraftForge.EVENT_BUS.post(event))
        {
            return null;
        }
        return event.getList();
    }

    public static int getMaxSpawnPackSize(EntityLiving entity)
    {
        LivingPackSizeEvent maxCanSpawnEvent = new LivingPackSizeEvent(entity);
        MinecraftForge.EVENT_BUS.post(maxCanSpawnEvent);
        return maxCanSpawnEvent.getResult() == Result.ALLOW ? maxCanSpawnEvent.getMaxPackSize() : entity.getMaxSpawnedInChunk();
    }

    public static String getPlayerDisplayName(EntityPlayer player, String username)
    {
        PlayerEvent.NameFormat event = new PlayerEvent.NameFormat(player, username);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getDisplayname();
    }

    public static float fireBlockHarvesting(NonNullList<ItemStack> drops, World world, BlockPos pos, IBlockState state, int fortune, float dropChance, boolean silkTouch, EntityPlayer player)
    {
        BlockEvent.HarvestDropsEvent event = new BlockEvent.HarvestDropsEvent(world, pos, state, fortune, dropChance, drops, player, silkTouch);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getDropChance();
    }

    public static IBlockState fireFluidPlaceBlockEvent(World world, BlockPos pos, BlockPos liquidPos, IBlockState state)
    {
        BlockEvent.FluidPlaceBlockEvent event = new BlockEvent.FluidPlaceBlockEvent(world, pos, liquidPos, state);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getNewState();
    }

    public static ItemTooltipEvent onItemTooltip(ItemStack itemStack, @Nullable EntityPlayer entityPlayer, List<ITextComponent> list, ITooltipFlag flags)
    {
        ItemTooltipEvent event = new ItemTooltipEvent(itemStack, entityPlayer, list, flags);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static SummonAidEvent fireZombieSummonAid(EntityZombie zombie, World world, int x, int y, int z, EntityLivingBase attacker, double summonChance)
    {
        SummonAidEvent summonEvent = new SummonAidEvent(zombie, world, x, y, z, attacker, summonChance);
        MinecraftForge.EVENT_BUS.post(summonEvent);
        return summonEvent;
    }

    public static boolean onEntityStruckByLightning(Entity entity, EntityLightningBolt bolt)
    {
        return MinecraftForge.EVENT_BUS.post(new EntityStruckByLightningEvent(entity, bolt));
    }

    public static int onItemUseStart(EntityLivingBase entity, ItemStack item, int duration)
    {
        LivingEntityUseItemEvent event = new LivingEntityUseItemEvent.Start(entity, item, duration);
        return MinecraftForge.EVENT_BUS.post(event) ? -1 : event.getDuration();
    }

    public static int onItemUseTick(EntityLivingBase entity, ItemStack item, int duration)
    {
        LivingEntityUseItemEvent event = new LivingEntityUseItemEvent.Tick(entity, item, duration);
        return MinecraftForge.EVENT_BUS.post(event) ? -1 : event.getDuration();
    }

    public static boolean onUseItemStop(EntityLivingBase entity, ItemStack item, int duration)
    {
        return MinecraftForge.EVENT_BUS.post(new LivingEntityUseItemEvent.Stop(entity, item, duration));
    }

    public static ItemStack onItemUseFinish(EntityLivingBase entity, ItemStack item, int duration, ItemStack result)
    {
        LivingEntityUseItemEvent.Finish event = new LivingEntityUseItemEvent.Finish(entity, item, duration, result);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResultStack();
    }

    public static void onStartEntityTracking(Entity entity, EntityPlayer player)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerEvent.StartTracking(player, entity));
    }

    public static void onStopEntityTracking(Entity entity, EntityPlayer player)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerEvent.StopTracking(player, entity));
    }

    public static void firePlayerLoadingEvent(EntityPlayer player, File playerDirectory, String uuidString)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerEvent.LoadFromFile(player, playerDirectory, uuidString));
    }

    public static void firePlayerSavingEvent(EntityPlayer player, File playerDirectory, String uuidString)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerEvent.SaveToFile(player, playerDirectory, uuidString));
    }

    public static void firePlayerLoadingEvent(EntityPlayer player, IPlayerFileData playerFileData, String uuidString)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerEvent.LoadFromFile(player, ((SaveHandler)playerFileData).playersDirectory, uuidString));
    }

    @Nullable
    public static ITextComponent onClientChat(ChatType type, ITextComponent message)
    {
        ClientChatReceivedEvent event = new ClientChatReceivedEvent(type, message);
        return MinecraftForge.EVENT_BUS.post(event) ? null : event.getMessage();
    }

    @Nonnull
    public static String onClientSendMessage(String message)
    {
        ClientChatEvent event = new ClientChatEvent(message);
        return MinecraftForge.EVENT_BUS.post(event) ? "" : event.getMessage();
    }

    public static int onHoeUse(ItemUseContext context)
    {
        UseHoeEvent event = new UseHoeEvent(context);
        if (MinecraftForge.EVENT_BUS.post(event)) return -1;
        if (event.getResult() == Result.ALLOW)
        {
            context.getItem().damageItem(1, context.getPlayer());
            return 1;
        }
        return 0;
    }


    public static int onApplyBonemeal(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull ItemStack stack)
    {
        BonemealEvent event = new BonemealEvent(player, world, pos, state, stack);
        if (MinecraftForge.EVENT_BUS.post(event)) return -1;
        if (event.getResult() == Result.ALLOW)
        {
            if (!world.isRemote)
                stack.shrink(1);
            return 1;
        }
        return 0;
    }

    @Nullable
    public static ActionResult<ItemStack> onBucketUse(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull ItemStack stack, @Nullable RayTraceResult target)
    {
        FillBucketEvent event = new FillBucketEvent(player, stack, world, target);
        if (MinecraftForge.EVENT_BUS.post(event)) return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);

        if (event.getResult() == Result.ALLOW)
        {
            if (player.abilities.isCreativeMode)
                return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);

            stack.shrink(1);
            if (stack.isEmpty())
                return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, event.getFilledBucket());

            if (!player.inventory.addItemStackToInventory(event.getFilledBucket()))
                player.dropItem(event.getFilledBucket(), false);

            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
        }
        return null;
    }

    public static boolean canEntityUpdate(Entity entity)
    {
        EntityEvent.CanUpdate event = new EntityEvent.CanUpdate(entity);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getCanUpdate();
    }

    public static PlaySoundAtEntityEvent onPlaySoundAtEntity(Entity entity, SoundEvent name, SoundCategory category, float volume, float pitch)
    {
        PlaySoundAtEntityEvent event = new PlaySoundAtEntityEvent(entity, name, category, volume, pitch);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static int onItemExpire(EntityItem entity, @Nonnull ItemStack item)
    {
        if (item.isEmpty()) return -1;
        ItemExpireEvent event = new ItemExpireEvent(entity, (item.isEmpty() ? 6000 : item.getItem().getEntityLifespan(item, entity.world)));
        if (!MinecraftForge.EVENT_BUS.post(event)) return -1;
        return event.getExtraLife();
    }

    public static int onItemPickup(EntityItem entityItem, EntityPlayer player)
    {
        Event event = new EntityItemPickupEvent(player, entityItem);
        if (MinecraftForge.EVENT_BUS.post(event)) return -1;
        return event.getResult() == Result.ALLOW ? 1 : 0;
    }

    public static void onPlayerDrops(EntityPlayer player, DamageSource cause, Collection<EntityItem> drops, boolean recentlyHit)
    {
        PlayerDropsEvent event = new PlayerDropsEvent(player, cause, drops, recentlyHit);
        if (!MinecraftForge.EVENT_BUS.post(event)) {
            drops.forEach(e -> player.dropItemAndGetStack(e));
        }
    }

    public static boolean canMountEntity(Entity entityMounting, Entity entityBeingMounted, boolean isMounting)
    {
        boolean isCanceled = MinecraftForge.EVENT_BUS.post(new EntityMountEvent(entityMounting, entityBeingMounted, entityMounting.world, isMounting));

        if(isCanceled)
        {
            entityMounting.setPositionAndRotation(entityMounting.posX, entityMounting.posY, entityMounting.posZ, entityMounting.prevRotationYaw, entityMounting.prevRotationPitch);
            return false;
        }
        else
            return true;
    }

    public static boolean onAnimalTame(EntityAnimal animal, EntityPlayer tamer)
    {
        return MinecraftForge.EVENT_BUS.post(new AnimalTameEvent(animal, tamer));
    }

    public static SleepResult onPlayerSleepInBed(EntityPlayer player, BlockPos pos)
    {
        PlayerSleepInBedEvent event = new PlayerSleepInBedEvent(player, pos);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResultStatus();
    }

    public static void onPlayerWakeup(EntityPlayer player, boolean wakeImmediately, boolean updateWorldFlag, boolean setSpawn)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerWakeUpEvent(player, wakeImmediately, updateWorldFlag, setSpawn));
    }

    public static void onPlayerFall(EntityPlayer player, float distance, float multiplier)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerFlyableFallEvent(player, distance, multiplier));
    }

    public static boolean onPlayerSpawnSet(EntityPlayer player, BlockPos pos, boolean forced) {
        return MinecraftForge.EVENT_BUS.post(new PlayerSetSpawnEvent(player, pos, forced));
    }

    public static void onPlayerClone(EntityPlayer player, EntityPlayer oldPlayer, boolean wasDeath)
    {
        MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerEvent.Clone(player, oldPlayer, wasDeath));
    }

    public static boolean onExplosionStart(World world, Explosion explosion)
    {
        return MinecraftForge.EVENT_BUS.post(new ExplosionEvent.Start(world, explosion));
    }

    public static void onExplosionDetonate(World world, Explosion explosion, List<Entity> list, double diameter)
    {
        //Filter entities to only those who are effected, to prevent modders from seeing more then will be hurt.
        /* Enable this if we get issues with modders looping to much.
        Iterator<Entity> itr = list.iterator();
        Vec3 p = explosion.getPosition();
        while (itr.hasNext())
        {
            Entity e = itr.next();
            double dist = e.getDistance(p.xCoord, p.yCoord, p.zCoord) / diameter;
            if (e.isImmuneToExplosions() || dist > 1.0F) itr.remove();
        }
        */
        MinecraftForge.EVENT_BUS.post(new ExplosionEvent.Detonate(world, explosion, list));
    }

    public static boolean onCreateWorldSpawn(World world, WorldSettings settings)
    {
        return MinecraftForge.EVENT_BUS.post(new WorldEvent.CreateSpawnPosition(world, settings));
    }

    public static float onLivingHeal(EntityLivingBase entity, float amount)
    {
        LivingHealEvent event = new LivingHealEvent(entity, amount);
        return (MinecraftForge.EVENT_BUS.post(event) ? 0 : event.getAmount());
    }

    public static boolean onPotionAttemptBrew(NonNullList<ItemStack> stacks)
    {
        NonNullList<ItemStack> tmp = NonNullList.withSize(stacks.size(), ItemStack.EMPTY);
        for (int x = 0; x < tmp.size(); x++)
            tmp.set(x, stacks.get(x).copy());

        PotionBrewEvent.Pre event = new PotionBrewEvent.Pre(tmp);
        if (MinecraftForge.EVENT_BUS.post(event))
        {
            boolean changed = false;
            for (int x = 0; x < stacks.size(); x++)
            {
                changed |= ItemStack.areItemStacksEqual(tmp.get(x), stacks.get(x));
                stacks.set(x, event.getItem(x));
            }
            if (changed)
                onPotionBrewed(stacks);
            return true;
        }
        return false;
    }

    public static void onPotionBrewed(NonNullList<ItemStack> brewingItemStacks)
    {
        MinecraftForge.EVENT_BUS.post(new PotionBrewEvent.Post(brewingItemStacks));
    }

    public static void onPlayerBrewedPotion(EntityPlayer player, ItemStack stack)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerBrewedPotionEvent(player, stack));
    }

    public static boolean renderFireOverlay(EntityPlayer player, float renderPartialTicks)
    {
        return renderBlockOverlay(player, renderPartialTicks, OverlayType.FIRE, Blocks.FIRE.getDefaultState(), new BlockPos(player));
    }

    public static boolean renderWaterOverlay(EntityPlayer player, float renderPartialTicks)
    {
        return renderBlockOverlay(player, renderPartialTicks, OverlayType.WATER, Blocks.WATER.getDefaultState(), new BlockPos(player));
    }

    public static boolean renderBlockOverlay(EntityPlayer player, float renderPartialTicks, OverlayType type, IBlockState block, BlockPos pos)
    {
        return MinecraftForge.EVENT_BUS.post(new RenderBlockOverlayEvent(player, renderPartialTicks, type, block, pos));
    }

    @Nullable
    public static <T extends ICapabilityProvider> CapabilityDispatcher gatherCapabilities(Class<? extends T> type, T provider)
    {
        return gatherCapabilities(type, provider, null);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T extends ICapabilityProvider> CapabilityDispatcher gatherCapabilities(Class<? extends T> type, T provider, @Nullable ICapabilityProvider parent)
    {
        return gatherCapabilities(new AttachCapabilitiesEvent<T>((Class<T>) type, provider), parent);
    }

    @Nullable
    private static CapabilityDispatcher gatherCapabilities(AttachCapabilitiesEvent<?> event, @Nullable ICapabilityProvider parent)
    {
        MinecraftForge.EVENT_BUS.post(event);
        return event.getCapabilities().size() > 0 || parent != null ? new CapabilityDispatcher(event.getCapabilities(), event.getListeners(), parent) : null;
    }

    public static boolean fireSleepingLocationCheck(EntityPlayer player, BlockPos sleepingLocation)
    {
        SleepingLocationCheckEvent evt = new SleepingLocationCheckEvent(player, sleepingLocation);
        MinecraftForge.EVENT_BUS.post(evt);

        Result canContinueSleep = evt.getResult();
        if (canContinueSleep == Result.DEFAULT)
        {
            IBlockState state = player.world.getBlockState(player.bedLocation);
            return state.getBlock().isBed(state, player.world, player.bedLocation, player);
        }
        else
            return canContinueSleep == Result.ALLOW;
    }

    public static boolean fireSleepingTimeCheck(EntityPlayer player, BlockPos sleepingLocation)
    {
        SleepingTimeCheckEvent evt = new SleepingTimeCheckEvent(player, sleepingLocation);
        MinecraftForge.EVENT_BUS.post(evt);

        Result canContinueSleep = evt.getResult();
        if (canContinueSleep == Result.DEFAULT)
            return !player.world.isDaytime();
        else
            return canContinueSleep == Result.ALLOW;
    }

    public static ActionResult<ItemStack> onArrowNock(ItemStack item, World world, EntityPlayer player, EnumHand hand, boolean hasAmmo)
    {
        ArrowNockEvent event = new ArrowNockEvent(player, item, hand, world, hasAmmo);
        if (MinecraftForge.EVENT_BUS.post(event))
            return new ActionResult<ItemStack>(EnumActionResult.FAIL, item);
        return event.getAction();
    }

    public static int onArrowLoose(ItemStack stack, World world, EntityPlayer player, int charge, boolean hasAmmo)
    {
        ArrowLooseEvent event = new ArrowLooseEvent(player, stack, world, charge, hasAmmo);
        if (MinecraftForge.EVENT_BUS.post(event))
            return -1;
        return event.getCharge();
    }

    public static boolean onProjectileImpact(Entity entity, RayTraceResult ray)
    {
        return MinecraftForge.EVENT_BUS.post(new ProjectileImpactEvent(entity, ray));
    }

    public static boolean onProjectileImpact(EntityArrow arrow, RayTraceResult ray)
    {
        return MinecraftForge.EVENT_BUS.post(new ProjectileImpactEvent.Arrow(arrow, ray));
    }

    public static boolean onProjectileImpact(EntityFireball fireball, RayTraceResult ray)
    {
        return MinecraftForge.EVENT_BUS.post(new ProjectileImpactEvent.Fireball(fireball, ray));
    }

    public static boolean onProjectileImpact(EntityThrowable throwable, RayTraceResult ray)
    {
        return MinecraftForge.EVENT_BUS.post(new ProjectileImpactEvent.Throwable(throwable, ray));
    }

    public static boolean onReplaceBiomeBlocks(IChunkGenerator<?> gen, IChunk chunk, IWorld world)
    {
        ChunkGeneratorEvent.ReplaceBiomeBlocks event = new ChunkGeneratorEvent.ReplaceBiomeBlocks(gen, chunk, world);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult() != Event.Result.DENY;
    }

    public static LootTable loadLootTable(ResourceLocation name, LootTable table, LootTableManager lootTableManager)
    {
        LootTableLoadEvent event = new LootTableLoadEvent(name, table, lootTableManager);
        if (MinecraftForge.EVENT_BUS.post(event))
            return LootTable.EMPTY_LOOT_TABLE;
        return event.getTable();
    }

    public static boolean canCreateFluidSource(World world, BlockPos pos, IBlockState state, boolean def)
    {
        CreateFluidSourceEvent evt = new CreateFluidSourceEvent(world, pos, state);
        MinecraftForge.EVENT_BUS.post(evt);

        Result result = evt.getResult();
        return result == Result.DEFAULT ? def : result == Result.ALLOW;
    }

    public static boolean onTrySpawnPortal(IWorld world, BlockPos pos, BlockPortal.Size size)
    {
        return MinecraftForge.EVENT_BUS.post(new BlockEvent.PortalSpawnEvent(world, pos, world.getBlockState(pos), size));
    }

    public static int onEnchantmentLevelSet(World world, BlockPos pos, int enchantRow, int power, ItemStack itemStack, int level)
    {
        net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent e = new net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent(world, pos, enchantRow, power, itemStack, level);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(e);
        return e.getLevel();
    }

    public static boolean onEntityDestroyBlock(EntityLivingBase entity, BlockPos pos, IBlockState state)
    {
        return !MinecraftForge.EVENT_BUS.post(new LivingDestroyBlockEvent(entity, pos, state));
    }

    public static boolean gatherCollisionBoxes(World world, Entity entity, AxisAlignedBB aabb, List<AxisAlignedBB> outList)
    {
        MinecraftForge.EVENT_BUS.post(new GetCollisionBoxesEvent(world, entity, aabb, outList));
        return outList.isEmpty();
    }

    public static boolean getMobGriefingEvent(World world, Entity entity)
    {
        EntityMobGriefingEvent event = new EntityMobGriefingEvent(entity);
        MinecraftForge.EVENT_BUS.post(event);

        Result result = event.getResult();
        return result == Result.DEFAULT ? world.getGameRules().getBoolean("mobGriefing") : result == Result.ALLOW;
    }

    public static boolean saplingGrowTree(IWorld world, Random rand, BlockPos pos)
    {
        SaplingGrowTreeEvent event = new SaplingGrowTreeEvent(world, rand, pos);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult() != Result.DENY;
    }
    
    public static MerchantRecipeList listTradeOffers(IMerchant merchant, EntityPlayer player, @Nullable MerchantRecipeList list)
    {
        MerchantRecipeList dupeList = null;
        if (list != null)
        {
            dupeList = new MerchantRecipeList();
            dupeList.addAll(list);
        }
        MerchantTradeOffersEvent event = new MerchantTradeOffersEvent(merchant, player, dupeList);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getList();
    }

    public static void onDifficultyChange(EnumDifficulty difficulty, EnumDifficulty oldDifficulty)
    {
        MinecraftForge.EVENT_BUS.post(new DifficultyChangeEvent(difficulty, oldDifficulty));
    }

    public static void onLivingSetAttackTarget(EntityLivingBase entity, EntityLivingBase target)
    {
        MinecraftForge.EVENT_BUS.post(new LivingSetAttackTargetEvent(entity, target));
    }

    public static boolean onLivingUpdate(EntityLivingBase entity)
    {
        return MinecraftForge.EVENT_BUS.post(new LivingEvent.LivingUpdateEvent(entity));
    }

    public static boolean onLivingAttack(EntityLivingBase entity, DamageSource src, float amount)
    {
        return entity instanceof EntityPlayer || !MinecraftForge.EVENT_BUS.post(new LivingAttackEvent(entity, src, amount));
    }

    public static boolean onPlayerAttack(EntityLivingBase entity, DamageSource src, float amount)
    {
        return !MinecraftForge.EVENT_BUS.post(new LivingAttackEvent(entity, src, amount));
    }

    public static LivingKnockBackEvent onLivingKnockBack(EntityLivingBase target, Entity attacker, float strength, double ratioX, double ratioZ)
    {
        LivingKnockBackEvent event = new LivingKnockBackEvent(target, attacker, strength, ratioX, ratioZ);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static float onLivingHurt(EntityLivingBase entity, DamageSource src, float amount)
    {
        LivingHurtEvent event = new LivingHurtEvent(entity, src, amount);
        return (MinecraftForge.EVENT_BUS.post(event) ? 0 : event.getAmount());
    }

    public static float onLivingDamage(EntityLivingBase entity, DamageSource src, float amount)
    {
        LivingDamageEvent event = new LivingDamageEvent(entity, src, amount);
        return (MinecraftForge.EVENT_BUS.post(event) ? 0 : event.getAmount());
    }

    public static boolean onLivingDeath(EntityLivingBase entity, DamageSource src)
    {
        return MinecraftForge.EVENT_BUS.post(new LivingDeathEvent(entity, src));
    }

    public static boolean onLivingDrops(EntityLivingBase entity, DamageSource source, Collection<EntityItem> drops, int lootingLevel, boolean recentlyHit)
    {
        return MinecraftForge.EVENT_BUS.post(new LivingDropsEvent(entity, source, drops, lootingLevel, recentlyHit));
    }

    @Nullable
    public static float[] onLivingFall(EntityLivingBase entity, float distance, float damageMultiplier)
    {
        LivingFallEvent event = new LivingFallEvent(entity, distance, damageMultiplier);
        return (MinecraftForge.EVENT_BUS.post(event) ? null : new float[]{event.getDistance(), event.getDamageMultiplier()});
    }

    public static int getLootingLevel(EntityLivingBase target, DamageSource cause, int level)
    {
        LootingLevelEvent event = new LootingLevelEvent(target, cause, level);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getLootingLevel();
    }

    public static double getPlayerVisibilityDistance(EntityPlayer player, double xzDistance, double maxXZDistance)
    {
        PlayerEvent.Visibility event = new PlayerEvent.Visibility(player);
        MinecraftForge.EVENT_BUS.post(event);
        double value = event.getVisibilityModifier() * xzDistance;
        return value >= maxXZDistance ? maxXZDistance : value;
    }

    public static void onLivingJump(EntityLivingBase entity)
    {
        MinecraftForge.EVENT_BUS.post(new LivingEvent.LivingJumpEvent(entity));
    }

    @Nullable
    public static EntityItem onPlayerTossEvent(@Nonnull EntityPlayer player, @Nonnull ItemStack item, boolean includeName)
    {
        player.captureDrops(Lists.newArrayList());
        EntityItem ret = player.dropItem(item, false, includeName);
        player.captureDrops(null);

        if (ret == null)
            return null;

        ItemTossEvent event = new ItemTossEvent(ret, player);
        if (MinecraftForge.EVENT_BUS.post(event))
            return null;

        if (!player.world.isRemote)
            player.getEntityWorld().spawnEntity(event.getEntityItem());
        return event.getEntityItem();
    }

    @Nullable
    public static ITextComponent onServerChatEvent(NetHandlerPlayServer net, String raw, ITextComponent comp)
    {
        ServerChatEvent event = new ServerChatEvent(net.player, raw, comp);
        if (MinecraftForge.EVENT_BUS.post(event))
        {
            return null;
        }
        return event.getComponent();
    }

    public static boolean onAnvilChange(ContainerRepair container, @Nonnull ItemStack left, @Nonnull ItemStack right, IInventory outputSlot, String name, int baseCost)
    {
        AnvilUpdateEvent e = new AnvilUpdateEvent(left, right, name, baseCost);
        if (MinecraftForge.EVENT_BUS.post(e)) return false;
        if (e.getOutput().isEmpty()) return true;

        outputSlot.setInventorySlotContents(0, e.getOutput());
        container.maximumCost = e.getCost();
        container.materialCost = e.getMaterialCost();
        return false;
    }

    public static float onAnvilRepair(EntityPlayer player, @Nonnull ItemStack output, @Nonnull ItemStack left, @Nonnull ItemStack right)
    {
        AnvilRepairEvent e = new AnvilRepairEvent(player, left, right, output);
        MinecraftForge.EVENT_BUS.post(e);
        return e.getBreakChance();
    }

    public static boolean onPlayerAttackTarget(EntityPlayer player, Entity target)
    {
        if (MinecraftForge.EVENT_BUS.post(new AttackEntityEvent(player, target))) return false;
        ItemStack stack = player.getHeldItemMainhand();
        return stack.isEmpty() || !stack.getItem().onLeftClickEntity(stack, player, target);
    }

    public static boolean onTravelToDimension(Entity entity, DimensionType dimension)
    {
        EntityTravelToDimensionEvent event = new EntityTravelToDimensionEvent(entity, dimension);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled())
        {
            // Revert variable back to true as it would have been set to false
            if (entity instanceof EntityMinecartContainer)
            {
                ((EntityMinecartContainer) entity).dropContentsWhenDead = true;
            }
        }
        return !event.isCanceled();
    }

    public static EnumActionResult onInteractEntityAt(EntityPlayer player, Entity entity, RayTraceResult ray, EnumHand hand)
    {
        Vec3d vec3d = new Vec3d(ray.hitVec.x - entity.posX, ray.hitVec.y - entity.posY, ray.hitVec.z - entity.posZ);
        return onInteractEntityAt(player, entity, vec3d, hand);
    }

    public static EnumActionResult onInteractEntityAt(EntityPlayer player, Entity entity, Vec3d vec3d, EnumHand hand)
    {
        PlayerInteractEvent.EntityInteractSpecific evt = new PlayerInteractEvent.EntityInteractSpecific(player, hand, entity, vec3d);
        MinecraftForge.EVENT_BUS.post(evt);
        return evt.isCanceled() ? evt.getCancellationResult() : null;
    }

    public static EnumActionResult onInteractEntity(EntityPlayer player, Entity entity, EnumHand hand)
    {
        PlayerInteractEvent.EntityInteract evt = new PlayerInteractEvent.EntityInteract(player, hand, entity);
        MinecraftForge.EVENT_BUS.post(evt);
        return evt.isCanceled() ? evt.getCancellationResult() : null;
    }

    public static EnumActionResult onItemRightClick(EntityPlayer player, EnumHand hand)
    {
        PlayerInteractEvent.RightClickItem evt = new PlayerInteractEvent.RightClickItem(player, hand);
        MinecraftForge.EVENT_BUS.post(evt);
        return evt.isCanceled() ? evt.getCancellationResult() : null;
    }

    public static PlayerInteractEvent.LeftClickBlock onLeftClickBlock(EntityPlayer player, BlockPos pos, EnumFacing face, Vec3d hitVec)
    {
        PlayerInteractEvent.LeftClickBlock evt = new PlayerInteractEvent.LeftClickBlock(player, pos, face, hitVec);
        MinecraftForge.EVENT_BUS.post(evt);
        return evt;
    }

    public static PlayerInteractEvent.RightClickBlock onRightClickBlock(EntityPlayer player, EnumHand hand, BlockPos pos, EnumFacing face, Vec3d hitVec)
    {
        PlayerInteractEvent.RightClickBlock evt = new PlayerInteractEvent.RightClickBlock(player, hand, pos, face, hitVec);
        MinecraftForge.EVENT_BUS.post(evt);
        return evt;
    }

    public static void onEmptyClick(EntityPlayer player, EnumHand hand)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerInteractEvent.RightClickEmpty(player, hand));
    }

    public static void onEmptyLeftClick(EntityPlayer player)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerInteractEvent.LeftClickEmpty(player));
    }

    public static boolean onCropsGrowPre(World worldIn, BlockPos pos, IBlockState state, boolean def)
    {
        BlockEvent ev = new BlockEvent.CropGrowEvent.Pre(worldIn,pos,state);
        MinecraftForge.EVENT_BUS.post(ev);
        return (ev.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW || (ev.getResult() == net.minecraftforge.eventbus.api.Event.Result.DEFAULT && def));
    }

    public static void onCropsGrowPost(World worldIn, BlockPos pos, IBlockState state)
    {
        MinecraftForge.EVENT_BUS.post(new BlockEvent.CropGrowEvent.Post(worldIn, pos, state, worldIn.getBlockState(pos)));
    }

    @Nullable
    public static CriticalHitEvent getCriticalHit(EntityPlayer player, Entity target, boolean vanillaCritical, float damageModifier)
    {
        CriticalHitEvent hitResult = new CriticalHitEvent(player, target, damageModifier, vanillaCritical);
        MinecraftForge.EVENT_BUS.post(hitResult);
        if (hitResult.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW || (vanillaCritical && hitResult.getResult() == net.minecraftforge.eventbus.api.Event.Result.DEFAULT))
        {
            return hitResult;
        }
        return null;
    }

    public static void onAdvancement(EntityPlayerMP player, Advancement advancement)
    {
        MinecraftForge.EVENT_BUS.post(new AdvancementEvent(player, advancement));
    }

    public static boolean onFarmlandTrample(World world, BlockPos pos, IBlockState state, float fallDistance, Entity entity)
    {
        if (entity.canTrample(state, pos, fallDistance))
        {
            BlockEvent.FarmlandTrampleEvent event = new BlockEvent.FarmlandTrampleEvent(world, pos, state, fallDistance, entity);
            MinecraftForge.EVENT_BUS.post(event);
            return !event.isCanceled();
        }
        return false;
    }

    public static int onNoteChange(World world, BlockPos pos, IBlockState state, int old, int _new)
    {
        NoteBlockEvent.Change event = new NoteBlockEvent.Change(world, pos, state, old, _new);
        if (MinecraftForge.EVENT_BUS.post(event))
            return -1;
        return event.getVanillaNoteId();
    }
}
