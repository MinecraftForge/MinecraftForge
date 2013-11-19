package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.NetServerHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet53BlockChange;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandomItem;
import net.minecraft.world.EnumGameType;
import net.minecraft.world.World;
import net.minecraftforge.event.Event;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.PlayerOpenContainerEvent;
import net.minecraftforge.event.world.BlockEvent;

public class ForgeHooks
{
    static class GrassEntry extends WeightedRandomItem
    {
        public final Block block;
        public final int metadata;
        public GrassEntry(Block block, int meta, int weight)
        {
            super(weight);
            this.block = block;
            this.metadata = meta;
        }
    }

    static class SeedEntry extends WeightedRandomItem
    {
        public final ItemStack seed;
        public SeedEntry(ItemStack seed, int weight)
        {
            super(weight);
            this.seed = seed;
        }
    }
    static final List<GrassEntry> grassList = new ArrayList<GrassEntry>();
    static final List<SeedEntry> seedList = new ArrayList<SeedEntry>();

    public static void plantGrass(World world, int x, int y, int z)
    {
        GrassEntry grass = (GrassEntry)WeightedRandom.getRandomItem(world.rand, grassList);
        if (grass == null || grass.block == null || !grass.block.canBlockStay(world, x, y, z))
        {
            return;
        }
        world.setBlock(x, y, z, grass.block.blockID, grass.metadata, 3);
    }

    public static ItemStack getGrassSeed(World world)
    {
        SeedEntry entry = (SeedEntry)WeightedRandom.getRandomItem(world.rand, seedList);
        if (entry == null || entry.seed == null)
        {
            return null;
        }
        return entry.seed.copy();
    }

    private static boolean toolInit = false;
    static HashMap<Item, List> toolClasses = new HashMap<Item, List>();
    static HashMap<List, Integer> toolHarvestLevels = new HashMap<List, Integer>();
    static HashSet<List> toolEffectiveness = new HashSet<List>();

    public static boolean canHarvestBlock(Block block, EntityPlayer player, int metadata)
    {
        if (block.blockMaterial.isToolNotRequired())
        {
            return true;
        }

        ItemStack stack = player.inventory.getCurrentItem();
        if (stack == null)
        {
            return player.canHarvestBlock(block);
        }

        List info = toolClasses.get(stack.getItem());
        if (info == null)
        {
            return player.canHarvestBlock(block);
        }

        Object[] tmp = info.toArray();
        String toolClass = (String)tmp[0];
        int harvestLevel = (Integer)tmp[1];

        Integer blockHarvestLevel = toolHarvestLevels.get(Arrays.asList(block, metadata, toolClass));
        if (blockHarvestLevel == null)
        {
            return player.canHarvestBlock(block);
        }

        if (blockHarvestLevel > harvestLevel)
        {
            return false;
        }
        return true;
    }

    public static boolean canToolHarvestBlock(Block block, int metadata, ItemStack stack)
    {
        if (stack == null) return false;
        List info = toolClasses.get(stack.getItem());
        if (info == null) return false;

        Object[] tmp = info.toArray();
        String toolClass = (String)tmp[0];
        int harvestLevel = (Integer)tmp[1];

        Integer blockHarvestLevel = toolHarvestLevels.get(Arrays.asList(block, metadata, toolClass));
        return !(blockHarvestLevel == null || blockHarvestLevel > harvestLevel);
    }

    public static float blockStrength(Block block, EntityPlayer player, World world, int x, int y, int z)
    {
        int metadata = world.getBlockMetadata(x, y, z);
        float hardness = block.getBlockHardness(world, x, y, z);
        if (hardness < 0.0F)
        {
            return 0.0F;
        }

        if (!canHarvestBlock(block, player, metadata))
        {
            float speed = ForgeEventFactory.getBreakSpeed(player, block, metadata, 1.0f);
            return (speed < 0 ? 0 : speed) / hardness / 100F;
        }
        else
        {
             return player.getCurrentPlayerStrVsBlock(block, false, metadata) / hardness / 30F;
        }
    }

    public static boolean isToolEffective(ItemStack stack, Block block, int metadata)
    {
        List toolClass = toolClasses.get(stack.getItem());
        return toolClass != null && toolEffectiveness.contains(Arrays.asList(block, metadata, toolClass.get(0)));
    }

    static void initTools()
    {
        if (toolInit)
        {
            return;
        }
        toolInit = true;

        MinecraftForge.setToolClass(Item.pickaxeWood,    "pickaxe", 0);
        MinecraftForge.setToolClass(Item.pickaxeStone,   "pickaxe", 1);
        MinecraftForge.setToolClass(Item.pickaxeIron,   "pickaxe", 2);
        MinecraftForge.setToolClass(Item.pickaxeGold,    "pickaxe", 0);
        MinecraftForge.setToolClass(Item.pickaxeDiamond, "pickaxe", 3);

        MinecraftForge.setToolClass(Item.axeWood,    "axe", 0);
        MinecraftForge.setToolClass(Item.axeStone,   "axe", 1);
        MinecraftForge.setToolClass(Item.axeIron,   "axe", 2);
        MinecraftForge.setToolClass(Item.axeGold,    "axe", 0);
        MinecraftForge.setToolClass(Item.axeDiamond, "axe", 3);

        MinecraftForge.setToolClass(Item.shovelWood,    "shovel", 0);
        MinecraftForge.setToolClass(Item.shovelStone,   "shovel", 1);
        MinecraftForge.setToolClass(Item.shovelIron,   "shovel", 2);
        MinecraftForge.setToolClass(Item.shovelGold,    "shovel", 0);
        MinecraftForge.setToolClass(Item.shovelDiamond, "shovel", 3);

        for (Block block : ItemPickaxe.blocksEffectiveAgainst)
        {
            MinecraftForge.setBlockHarvestLevel(block, "pickaxe", 0);
        }

        for (Block block : ItemSpade.blocksEffectiveAgainst)
        {
            MinecraftForge.setBlockHarvestLevel(block, "shovel", 0);
        }

        for (Block block : ItemAxe.blocksEffectiveAgainst)
        {
            MinecraftForge.setBlockHarvestLevel(block, "axe", 0);
        }

        MinecraftForge.setBlockHarvestLevel(Block.obsidian,     "pickaxe", 3);
        MinecraftForge.setBlockHarvestLevel(Block.oreEmerald,   "pickaxe", 2);
        MinecraftForge.setBlockHarvestLevel(Block.oreDiamond,   "pickaxe", 2);
        MinecraftForge.setBlockHarvestLevel(Block.blockDiamond, "pickaxe", 2);
        MinecraftForge.setBlockHarvestLevel(Block.oreGold,      "pickaxe", 2);
        MinecraftForge.setBlockHarvestLevel(Block.blockGold,    "pickaxe", 2);
        MinecraftForge.setBlockHarvestLevel(Block.oreIron,      "pickaxe", 1);
        MinecraftForge.setBlockHarvestLevel(Block.blockIron,   "pickaxe", 1);
        MinecraftForge.setBlockHarvestLevel(Block.oreLapis,     "pickaxe", 1);
        MinecraftForge.setBlockHarvestLevel(Block.blockLapis,   "pickaxe", 1);
        MinecraftForge.setBlockHarvestLevel(Block.oreRedstone,  "pickaxe", 2);
        MinecraftForge.setBlockHarvestLevel(Block.oreRedstoneGlowing, "pickaxe", 2);
        MinecraftForge.removeBlockEffectiveness(Block.oreRedstone, "pickaxe");
        MinecraftForge.removeBlockEffectiveness(Block.obsidian,    "pickaxe");
        MinecraftForge.removeBlockEffectiveness(Block.oreRedstoneGlowing, "pickaxe");
    }

    public static int getTotalArmorValue(EntityPlayer player)
    {
        int ret = 0;
        for (int x = 0; x < player.inventory.armorInventory.length; x++)
        {
            ItemStack stack = player.inventory.armorInventory[x];
            if (stack != null && stack.getItem() instanceof ISpecialArmor)
            {
                ret += ((ISpecialArmor)stack.getItem()).getArmorDisplay(player, stack, x);
            }
            else if (stack != null && stack.getItem() instanceof ItemArmor)
            {
                ret += ((ItemArmor)stack.getItem()).damageReduceAmount;
            }
        }
        return ret;
    }

    static
    {
        grassList.add(new GrassEntry(Block.plantYellow, 0, 20));
        grassList.add(new GrassEntry(Block.plantRed,    0, 10));
        seedList.add(new SeedEntry(new ItemStack(Item.seeds), 10));
        initTools();
    }

    /**
     * Called when a player uses 'pick block', calls new Entity and Block hooks.
     */
    public static boolean onPickBlock(MovingObjectPosition target, EntityPlayer player, World world)
    {
        ItemStack result = null;
        boolean isCreative = player.capabilities.isCreativeMode;

        if (target.typeOfHit == EnumMovingObjectType.TILE)
        {
            int x = target.blockX;
            int y = target.blockY;
            int z = target.blockZ;
            Block var8 = Block.blocksList[world.getBlockId(x, y, z)];

            if (var8 == null)
            {
                return false;
            }

            result = var8.getPickBlock(target, world, x, y, z);
        }
        else
        {
            if (target.typeOfHit != EnumMovingObjectType.ENTITY || target.entityHit == null || !isCreative)
            {
                return false;
            }

            result = target.entityHit.getPickedResult(target);
        }

        if (result == null)
        {
            return false;
        }

        for (int x = 0; x < 9; x++)
        {
            ItemStack stack = player.inventory.getStackInSlot(x);
            if (stack != null && stack.isItemEqual(result) && ItemStack.areItemStackTagsEqual(stack, result))
            {
                player.inventory.currentItem = x;
                return true;
            }
        }

        if (!isCreative)
        {
            return false;
        }

        int slot = player.inventory.getFirstEmptyStack();
        if (slot < 0 || slot >= 9)
        {
            slot = player.inventory.currentItem;
        }

        player.inventory.setInventorySlotContents(slot, result);
        player.inventory.currentItem = slot;
        return true;
    }

    //Optifine Helper Functions u.u, these are here specifically for Optifine
    //Note: When using Optfine, these methods are invoked using reflection, which
    //incurs a major performance penalty.
    public static void onLivingSetAttackTarget(EntityLivingBase entity, EntityLivingBase target)
    {
        MinecraftForge.EVENT_BUS.post(new LivingSetAttackTargetEvent(entity, target));
    }

    public static boolean onLivingUpdate(EntityLivingBase entity)
    {
        return MinecraftForge.EVENT_BUS.post(new LivingUpdateEvent(entity));
    }

    public static boolean onLivingAttack(EntityLivingBase entity, DamageSource src, float amount)
    {
        return MinecraftForge.EVENT_BUS.post(new LivingAttackEvent(entity, src, amount));
    }

    public static float onLivingHurt(EntityLivingBase entity, DamageSource src, float amount)
    {
        LivingHurtEvent event = new LivingHurtEvent(entity, src, amount);
        return (MinecraftForge.EVENT_BUS.post(event) ? 0 : event.ammount);
    }

    public static boolean onLivingDeath(EntityLivingBase entity, DamageSource src)
    {
        return MinecraftForge.EVENT_BUS.post(new LivingDeathEvent(entity, src));
    }

    public static boolean onLivingDrops(EntityLivingBase entity, DamageSource source, ArrayList<EntityItem> drops, int lootingLevel, boolean recentlyHit, int specialDropValue)
    {
        return MinecraftForge.EVENT_BUS.post(new LivingDropsEvent(entity, source, drops, lootingLevel, recentlyHit, specialDropValue));
    }

    public static float onLivingFall(EntityLivingBase entity, float distance)
    {
        LivingFallEvent event = new LivingFallEvent(entity, distance);
        return (MinecraftForge.EVENT_BUS.post(event) ? 0.0f : event.distance);
    }

    public static boolean isLivingOnLadder(Block block, World world, int x, int y, int z, EntityLivingBase entity)
    {
        if (!ForgeDummyContainer.fullBoundingBoxLadders)
        {
            return block != null && block.isLadder(world, x, y, z, entity);
        }
        else
        {
            AxisAlignedBB bb = entity.boundingBox;
            int mX = MathHelper.floor_double(bb.minX);
            int mY = MathHelper.floor_double(bb.minY);
            int mZ = MathHelper.floor_double(bb.minZ);
            for (int y2 = mY; y2 < bb.maxY; y2++)
            {
                for (int x2 = mX; x2 < bb.maxX; x2++)
                {
                    for (int z2 = mZ; z2 < bb.maxZ; z2++)
                    {
                        block = Block.blocksList[world.getBlockId(x2, y2, z2)];                        
                        if (block != null && block.isLadder(world, x2, y2, z2, entity))
                        {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }

    public static void onLivingJump(EntityLivingBase entity)
    {
        MinecraftForge.EVENT_BUS.post(new LivingJumpEvent(entity));
    }

    public static EntityItem onPlayerTossEvent(EntityPlayer player, ItemStack item)
    {
        player.captureDrops = true;
        EntityItem ret = player.dropPlayerItemWithRandomChoice(item, false);
        player.capturedDrops.clear();
        player.captureDrops = false;

        if (ret == null)
        {
            return null;
        }

        ItemTossEvent event = new ItemTossEvent(ret, player);
        if (MinecraftForge.EVENT_BUS.post(event))
        {
            return null;
        }

        player.joinEntityItemWithWorld(event.entityItem);
        return event.entityItem;
    }

    public static float getEnchantPower(World world, int x, int y, int z)
    {
        if (world.isAirBlock(x, y, z))
        {
            return 0;
        }

        Block block = Block.blocksList[world.getBlockId(x, y, z)];
        return (block == null ? 0 : block.getEnchantPowerBonus(world, x, y, z));
    }

    public static ChatMessageComponent onServerChatEvent(NetServerHandler net, String raw, ChatMessageComponent comp)
    {
        ServerChatEvent event = new ServerChatEvent(net.playerEntity, raw, comp);
        if (MinecraftForge.EVENT_BUS.post(event))
        {
            return null;
        }
        return event.component;
    }
    
    public static boolean canInteractWith(EntityPlayer player, Container openContainer)
    {
        PlayerOpenContainerEvent event = new PlayerOpenContainerEvent(player, openContainer);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult() == Event.Result.DEFAULT ? event.canInteractWith : event.getResult() == Event.Result.ALLOW ? true : false;
    }
    
    public static BlockEvent.BreakEvent onBlockBreakEvent(World world, EnumGameType gameType, EntityPlayerMP entityPlayer, int x, int y, int z)
    {
        // Logic from tryHarvestBlock for pre-canceling the event
        boolean preCancelEvent = false;
        if (gameType.isAdventure() && !entityPlayer.isCurrentToolAdventureModeExempt(x, y, z))
        {
            preCancelEvent = true;
        }
        else if (gameType.isCreative() && entityPlayer.getHeldItem() != null && entityPlayer.getHeldItem().getItem() instanceof ItemSword)
        {
            preCancelEvent = true;
        }

        // Tell client the block is gone immediately then process events
        if (world.getBlockTileEntity(x, y, z) == null)
        {
            Packet53BlockChange packet = new Packet53BlockChange(x, y, z, world);
            packet.type = 0;
            packet.metadata = 0;
            entityPlayer.playerNetServerHandler.sendPacketToPlayer(packet);
        }

        // Post the block break event
        Block block = Block.blocksList[world.getBlockId(x, y, z)];
        int blockMetadata = world.getBlockMetadata(x, y, z);
        BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(x, y, z, world, block, blockMetadata, entityPlayer);
        event.setCanceled(preCancelEvent);
        MinecraftForge.EVENT_BUS.post(event);

        // Handle if the event is canceled
        if (event.isCanceled())
        {
            // Let the client know the block still exists
            entityPlayer.playerNetServerHandler.sendPacketToPlayer(new Packet53BlockChange(x, y, z, world));
            
            // Update any tile entity data for this block
            TileEntity tileentity = world.getBlockTileEntity(x, y, z);
            if (tileentity != null)
            {
                Packet pkt = tileentity.getDescriptionPacket();
                if (pkt != null)
                {
                    entityPlayer.playerNetServerHandler.sendPacketToPlayer(pkt);
                }
            }
        }
        return event;
    }
}
