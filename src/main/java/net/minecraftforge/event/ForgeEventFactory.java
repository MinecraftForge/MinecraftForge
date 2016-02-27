package net.minecraftforge.event;

import java.io.File;
import java.util.EnumSet;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.EnumStatus;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.SaveHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent.OverlayType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.brewing.PotionBrewEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingPackSizeEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.AllowDespawn;
import net.minecraftforge.event.entity.living.ZombieEvent.SummonAidEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.SleepingLocationCheckEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.event.world.BlockEvent.MultiPlaceEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class ForgeEventFactory
{

    public static MultiPlaceEvent onPlayerMultiBlockPlace(EntityPlayer player, List<BlockSnapshot> blockSnapshots, EnumFacing direction)
    {
        BlockSnapshot snap = blockSnapshots.get(0);
        IBlockState placedAgainst = snap.world.getBlockState(snap.pos.offset(direction.getOpposite()));
        MultiPlaceEvent event = new MultiPlaceEvent(blockSnapshots, placedAgainst, player);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static PlaceEvent onPlayerBlockPlace(EntityPlayer player, BlockSnapshot blockSnapshot, EnumFacing direction)
    {
        IBlockState placedAgainst = blockSnapshot.world.getBlockState(blockSnapshot.pos.offset(direction.getOpposite()));
        PlaceEvent event = new PlaceEvent(blockSnapshot, placedAgainst, player);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static NeighborNotifyEvent onNeighborNotify(World world, BlockPos pos, IBlockState state, EnumSet<EnumFacing> notifiedSides)
    {
        NeighborNotifyEvent event = new NeighborNotifyEvent(world, pos, state, notifiedSides);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static boolean doPlayerHarvestCheck(EntityPlayer player, Block block, boolean success)
    {
        PlayerEvent.HarvestCheck event = new PlayerEvent.HarvestCheck(player, block, success);
        MinecraftForge.EVENT_BUS.post(event);
        return event.success;
    }

    public static float getBreakSpeed(EntityPlayer player, IBlockState state, float original, BlockPos pos)
    {
        PlayerEvent.BreakSpeed event = new PlayerEvent.BreakSpeed(player, state, original, pos);
        return (MinecraftForge.EVENT_BUS.post(event) ? -1 : event.newSpeed);
    }

    @Deprecated
    public static PlayerInteractEvent onPlayerInteract(EntityPlayer player, Action action, World world, BlockPos pos, EnumFacing face)
    {
        return onPlayerInteract(player, action, world, pos, face, null);
    }

    public static PlayerInteractEvent onPlayerInteract(EntityPlayer player, Action action, World world, BlockPos pos, EnumFacing face, Vec3 localPos)
    {
        PlayerInteractEvent event = new PlayerInteractEvent(player, action, pos, face, world, localPos);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static void onPlayerDestroyItem(EntityPlayer player, ItemStack stack)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(player, stack));
    }

    public static Result canEntitySpawn(EntityLiving entity, World world, float x, float y, float z)
    {
        LivingSpawnEvent.CheckSpawn event = new LivingSpawnEvent.CheckSpawn(entity, world, x, y, z);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult();
    }

    public static boolean doSpecialSpawn(EntityLiving entity, World world, float x, float y, float z)
    {
        return MinecraftForge.EVENT_BUS.post(new LivingSpawnEvent.SpecialSpawn(entity, world, x, y, z));
    }

    public static Result canEntityDespawn(EntityLiving entity)
    {
        AllowDespawn event = new AllowDespawn(entity);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult();
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

    public static List<BiomeGenBase.SpawnListEntry> getPotentialSpawns(WorldServer world, EnumCreatureType type, BlockPos pos, List<BiomeGenBase.SpawnListEntry> oldList)
    {
        WorldEvent.PotentialSpawns event = new WorldEvent.PotentialSpawns(world, type, pos, oldList);
        if (MinecraftForge.EVENT_BUS.post(event))
        {
            return null;
        }
        return event.list;
    }

    public static int getMaxSpawnPackSize(EntityLiving entity)
    {
        LivingPackSizeEvent maxCanSpawnEvent = new LivingPackSizeEvent(entity);
        MinecraftForge.EVENT_BUS.post(maxCanSpawnEvent);
        return maxCanSpawnEvent.getResult() == Result.ALLOW ? maxCanSpawnEvent.maxPackSize : entity.getMaxSpawnedInChunk();
    }

    public static String getPlayerDisplayName(EntityPlayer player, String username)
    {
        PlayerEvent.NameFormat event = new PlayerEvent.NameFormat(player, username);
        MinecraftForge.EVENT_BUS.post(event);
        return event.displayname;
    }

    public static float fireBlockHarvesting(List<ItemStack> drops, World world, BlockPos pos, IBlockState state, int fortune, float dropChance, boolean silkTouch, EntityPlayer player)
    {
        BlockEvent.HarvestDropsEvent event = new BlockEvent.HarvestDropsEvent(world, pos, state, fortune, dropChance, drops, player, silkTouch);
        MinecraftForge.EVENT_BUS.post(event);
        return event.dropChance;
    }

    public static ItemTooltipEvent onItemTooltip(ItemStack itemStack, EntityPlayer entityPlayer, List<String> toolTip, boolean showAdvancedItemTooltips)
    {
        ItemTooltipEvent event = new ItemTooltipEvent(itemStack, entityPlayer, toolTip, showAdvancedItemTooltips);
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

    public static int onItemUseStart(EntityPlayer player, ItemStack item, int duration)
    {
        PlayerUseItemEvent event = new PlayerUseItemEvent.Start(player, item, duration);
        return MinecraftForge.EVENT_BUS.post(event) ? -1 : event.duration;
    }

    public static int onItemUseTick(EntityPlayer player, ItemStack item, int duration)
    {
        PlayerUseItemEvent event = new PlayerUseItemEvent.Tick(player, item, duration);
        return MinecraftForge.EVENT_BUS.post(event) ? -1 : event.duration;
    }

    public static boolean onUseItemStop(EntityPlayer player, ItemStack item, int duration)
    {
        return MinecraftForge.EVENT_BUS.post(new PlayerUseItemEvent.Stop(player, item, duration));
    }

    public static ItemStack onItemUseFinish(EntityPlayer player, ItemStack item, int duration, ItemStack result)
    {
        PlayerUseItemEvent.Finish event = new PlayerUseItemEvent.Finish(player, item, duration, result);
        MinecraftForge.EVENT_BUS.post(event);
        return event.result;
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
        SaveHandler sh = (SaveHandler) playerFileData;
        File dir = ObfuscationReflectionHelper.getPrivateValue(SaveHandler.class, sh, "playersDirectory", "field_"+"75771_c");
        MinecraftForge.EVENT_BUS.post(new PlayerEvent.LoadFromFile(player, dir, uuidString));
    }

    public static IChatComponent onClientChat(byte type, IChatComponent message)
    {
        ClientChatReceivedEvent event = new ClientChatReceivedEvent(type, message);
        return MinecraftForge.EVENT_BUS.post(event) ? null : event.message;
    }

    public static int onHoeUse(ItemStack stack, EntityPlayer player, World worldIn, BlockPos pos)
    {
        UseHoeEvent event = new UseHoeEvent(player, stack, worldIn, pos);
        if (MinecraftForge.EVENT_BUS.post(event)) return -1;
        if (event.getResult() == Result.ALLOW)
        {
            stack.damageItem(1, player);
            return 1;
        }
        return 0;
    }

    public static int onApplyBonemeal(EntityPlayer player, World world, BlockPos pos, IBlockState state, ItemStack stack)
    {
        BonemealEvent event = new BonemealEvent(player, world, pos, state);
        if (MinecraftForge.EVENT_BUS.post(event)) return -1;
        if (event.getResult() == Result.ALLOW)
        {
            if (!world.isRemote)
                stack.stackSize--;
            return 1;
        }
        return 0;
    }

    public static ItemStack onBucketUse(EntityPlayer player, World world, ItemStack stack, MovingObjectPosition target)
    {
        FillBucketEvent event = new FillBucketEvent(player, stack, world, target);
        if (MinecraftForge.EVENT_BUS.post(event)) return stack;

        if (event.getResult() == Result.ALLOW)
        {
            if (player.capabilities.isCreativeMode)
                return stack;

            if (--stack.stackSize <= 0)
                return event.result;

            if (!player.inventory.addItemStackToInventory(event.result))
                player.dropPlayerItemWithRandomChoice(event.result, false);

            return stack;
        }
        return null;
    }

    public static boolean canEntityUpdate(Entity entity)
    {
        EntityEvent.CanUpdate event = new EntityEvent.CanUpdate(entity);
        MinecraftForge.EVENT_BUS.post(event);
        return event.canUpdate;
    }

    /**
     * Use {@link #onPlaySoundAtEntity(Entity,String,float,float)}
     */
    @Deprecated
    public static String onPlaySoundAt(Entity entity, String name, float volume, float pitch)
    {
        PlaySoundAtEntityEvent event = new PlaySoundAtEntityEvent(entity, name, volume, pitch);
        MinecraftForge.EVENT_BUS.post(event);
        return event.name;
    }

    public static PlaySoundAtEntityEvent onPlaySoundAtEntity(Entity entity, String name, float volume, float pitch)
    {
        PlaySoundAtEntityEvent event = new PlaySoundAtEntityEvent(entity, name, volume, pitch);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static int onItemExpire(EntityItem entity, ItemStack item)
    {
        if (item == null) return -1;
        ItemExpireEvent event = new ItemExpireEvent(entity, (item.getItem() == null ? 6000 : item.getItem().getEntityLifespan(item, entity.worldObj)));
        if (!MinecraftForge.EVENT_BUS.post(event)) return -1;
        return event.extraLife;
    }

    public static int onItemPickup(EntityItem entityItem, EntityPlayer entityIn, ItemStack itemstack)
    {
        Event event = new EntityItemPickupEvent(entityIn, entityItem);
        if (MinecraftForge.EVENT_BUS.post(event)) return -1;
        return event.getResult() == Result.ALLOW ? 1 : 0;
    }

    public static void onPlayerDrops(EntityPlayer player, DamageSource cause, List<EntityItem> capturedDrops, boolean recentlyHit)
    {
        PlayerDropsEvent event = new PlayerDropsEvent(player, cause, capturedDrops, recentlyHit);
        if (!MinecraftForge.EVENT_BUS.post(event))
        {
            for (EntityItem item : capturedDrops)
            {
                player.joinEntityItemWithWorld(item);
            }
        }
    }

    public static boolean canInteractWith(EntityPlayer player, Entity entity)
    {
        return !MinecraftForge.EVENT_BUS.post(new EntityInteractEvent(player, entity));
    }

    public static boolean canMountEntity(Entity entityMounting, Entity entityBeingMounted, boolean isMounting)
    {
        boolean isCanceled = MinecraftForge.EVENT_BUS.post(new EntityMountEvent(entityMounting, entityBeingMounted, entityMounting.worldObj, isMounting));

        if(isCanceled)
        {
            entityMounting.setPositionAndRotation(entityMounting.posX, entityMounting.posY, entityMounting.posZ, entityMounting.prevRotationYaw, entityMounting.prevRotationPitch);
            return false;
        }
        else
            return true;
    }

    public static EnumStatus onPlayerSleepInBed(EntityPlayer player, BlockPos pos)
    {
        PlayerSleepInBedEvent event = new PlayerSleepInBedEvent(player, pos);
        MinecraftForge.EVENT_BUS.post(event);
        return event.result;
    }

    public static void onPlayerWakeup(EntityPlayer player, boolean wakeImmediatly, boolean updateWorldFlag, boolean setSpawn)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerWakeUpEvent(player, wakeImmediatly, updateWorldFlag, setSpawn));
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
            if (e.func_180427_aV() || dist > 1.0F) itr.remove();
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
        return (MinecraftForge.EVENT_BUS.post(event) ? 0 : event.amount);
    }

    public static boolean onPotionAttemptBreaw(ItemStack[] stacks)
    {
        ItemStack[] tmp = new ItemStack[stacks.length];
        for (int x = 0; x < tmp.length; x++)
            tmp[x] = ItemStack.copyItemStack(stacks[x]);

        PotionBrewEvent.Pre event = new PotionBrewEvent.Pre(tmp);
        if (MinecraftForge.EVENT_BUS.post(event))
        {
            boolean changed = false;
            for (int x = 0; x < stacks.length; x++)
            {
                changed |= ItemStack.areItemStacksEqual(tmp[x], stacks[x]);
                stacks[x] = event.getItem(x);
            }
            if (changed)
                onPotionBrewed(stacks);
            return true;
        }
        return false;
    }

    public static void onPotionBrewed(ItemStack[] brewingItemStacks)
    {
        MinecraftForge.EVENT_BUS.post(new PotionBrewEvent.Post(brewingItemStacks));
    }

    public static boolean renderFireOverlay(EntityPlayer player, float renderPartialTicks)
    {
        return renderBlockOverlay(player, renderPartialTicks, OverlayType.FIRE, Blocks.fire.getDefaultState(), new BlockPos(player));
    }

    public static boolean renderWaterOverlay(EntityPlayer player, float renderPartialTicks)
    {
        return renderBlockOverlay(player, renderPartialTicks, OverlayType.WATER, Blocks.water.getDefaultState(), new BlockPos(player));
    }

    public static boolean renderBlockOverlay(EntityPlayer player, float renderPartialTicks, OverlayType type, IBlockState block, BlockPos pos)
    {
        return MinecraftForge.EVENT_BUS.post(new RenderBlockOverlayEvent(player, renderPartialTicks, type, block, pos));
    }

    public static CapabilityDispatcher gatherCapabilities(TileEntity tileEntity)
    {
        return gatherCapabilities(new AttachCapabilitiesEvent.TileEntity(tileEntity), null);
    }

    public static CapabilityDispatcher gatherCapabilities(Entity entity)
    {
        return gatherCapabilities(new AttachCapabilitiesEvent.Entity(entity), null);
    }

    public static CapabilityDispatcher gatherCapabilities(Item item, ItemStack stack, ICapabilityProvider parent)
    {
        return gatherCapabilities(new AttachCapabilitiesEvent.Item(item, stack), parent);
    }

    private static CapabilityDispatcher gatherCapabilities(AttachCapabilitiesEvent event, ICapabilityProvider parent)
    {
        MinecraftForge.EVENT_BUS.post(event);
        return event.getCapabilities().size() > 0 || parent != null ? new CapabilityDispatcher(event.getCapabilities(), parent) : null;
    }

    public static boolean fireSleepingLocationCheck(EntityPlayer player, BlockPos sleepingLocation)
    {
        SleepingLocationCheckEvent evt = new SleepingLocationCheckEvent(player, sleepingLocation);
        MinecraftForge.EVENT_BUS.post(evt);

        Result canContinueSleep = evt.getResult();
        if (canContinueSleep == Result.DEFAULT)
            return player.worldObj.getBlockState(player.playerLocation).getBlock().isBed(player.worldObj, player.playerLocation, player);
        else
            return canContinueSleep == Result.ALLOW;
    }

}
