package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings.GameType;
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
import static net.minecraft.init.Blocks.*;

public class ForgeHooks
{
    static class SeedEntry extends WeightedRandom.Item
    {
        public final ItemStack seed;
        public SeedEntry(ItemStack seed, int weight)
        {
            super(weight);
            this.seed = seed;
        }
    }
    static final List<SeedEntry> seedList = new ArrayList<SeedEntry>();

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
    //static HashSet<List> toolEffectiveness = new HashSet<List>();

    public static boolean canHarvestBlock(Block block, EntityPlayer player, int metadata)
    {
        if (block.getMaterial().isToolNotRequired())
        {
            return true;
        }

        ItemStack stack = player.inventory.getCurrentItem();
        String tool = block.getHarvestTool(metadata);
        if (stack == null || tool == null)
        {
            return player.canHarvestBlock(block);
        }

        int toolLevel = stack.getItem().getHarvestLevel(stack, tool);
        if (toolLevel < 0)
        {
            return player.canHarvestBlock(block);
        }

        return toolLevel >= block.getHarvestLevel(metadata);
    }

    public static boolean canToolHarvestBlock(Block block, int metadata, ItemStack stack)
    {
        String tool = block.getHarvestTool(metadata);
        if (stack == null || tool == null) return false;
        return stack.getItem().getHarvestLevel(stack, tool) >= block.getHarvestLevel(metadata);
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
            return player.getBreakSpeed(block, true, metadata) / hardness / 100F;
        }
        else
        {
            return player.getBreakSpeed(block, false, metadata) / hardness / 30F;
        }
    }

    public static boolean isToolEffective(ItemStack stack, Block block, int metadata)
    {
        for (String type : stack.getItem().getToolClasses(stack))
        {
            if (block.isToolEffective(type, metadata))
                return true;
        }
        return false;
    }

    static void initTools()
    {
        if (toolInit)
        {
            return;
        }
        toolInit = true;

        Set<Block> blocks = ReflectionHelper.getPrivateValue(ItemPickaxe.class, null, 0);
        for (Block block : blocks)
        {
            block.setHarvestLevel("pickaxe", 0);
        }

        blocks = ReflectionHelper.getPrivateValue(ItemSpade.class, null, 0);
        for (Block block : blocks)
        {
            block.setHarvestLevel("shovel", 0);
        }

        blocks = ReflectionHelper.getPrivateValue(ItemAxe.class, null, 0);
        for (Block block : blocks)
        {
            block.setHarvestLevel("axe", 0);
        }

        Blocks.obsidian.setHarvestLevel("pickaxe", 3);
        for (Block block : new Block[]{emerald_ore, emerald_block, diamond_ore, diamond_block, gold_ore, gold_block, redstone_ore, lit_redstone_ore})
        {
            block.setHarvestLevel("pickaxe", 2);
        }
        Blocks.iron_ore.setHarvestLevel("pickaxe", 1);
        Blocks.iron_block.setHarvestLevel("pickaxe", 1);
        Blocks.lapis_ore.setHarvestLevel("pickaxe", 1);
        Blocks.lapis_block.setHarvestLevel("pickaxe", 1);
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
        seedList.add(new SeedEntry(new ItemStack(Items.wheat_seeds), 10));
        initTools();
    }

    /**
     * Called when a player uses 'pick block', calls new Entity and Block hooks.
     */
    public static boolean onPickBlock(MovingObjectPosition target, EntityPlayer player, World world)
    {
        ItemStack result = null;
        boolean isCreative = player.capabilities.isCreativeMode;

        if (target.typeOfHit == MovingObjectType.BLOCK)
        {
            int x = target.blockX;
            int y = target.blockY;
            int z = target.blockZ;
            Block block = world.getBlock(x, y, z);

            if (block.isAir(world, x, y, z))
            {
                return false;
            }

            result = block.getPickBlock(target, world, x, y, z);
        }
        else
        {
            if (target.typeOfHit != MovingObjectType.ENTITY || target.entityHit == null || !isCreative)
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
        if (!ForgeModContainer.fullBoundingBoxLadders)
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
                        block = world.getBlock(x2, y2, z2);
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

    public static EntityItem onPlayerTossEvent(EntityPlayer player, ItemStack item, boolean includeName)
    {
        player.captureDrops = true;
        EntityItem ret = player.func_146097_a(item, false, includeName);
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
        return world.getBlock(x, y, z).getEnchantPowerBonus(world, x, y, z);
    }

    public static ChatComponentTranslation onServerChatEvent(NetHandlerPlayServer net, String raw, ChatComponentTranslation comp)
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

    public static BlockEvent.BreakEvent onBlockBreakEvent(World world, GameType gameType, EntityPlayerMP entityPlayer, int x, int y, int z)
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
        if (world.getTileEntity(x, y, z) == null)
        {
            S23PacketBlockChange packet = new S23PacketBlockChange(x, y, z, world);
            packet.field_148883_d = Blocks.air;
            packet.field_148884_e = 0;
            entityPlayer.playerNetServerHandler.sendPacket(packet);
        }

        // Post the block break event
        Block block = world.getBlock(x, y, z);
        int blockMetadata = world.getBlockMetadata(x, y, z);
        BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(x, y, z, world, block, blockMetadata, entityPlayer);
        event.setCanceled(preCancelEvent);
        MinecraftForge.EVENT_BUS.post(event);

        // Handle if the event is canceled
        if (event.isCanceled())
        {
            // Let the client know the block still exists
            entityPlayer.playerNetServerHandler.sendPacket(new S23PacketBlockChange(x, y, z, world));

            // Update any tile entity data for this block
            TileEntity tileentity = world.getTileEntity(x, y, z);
            if (tileentity != null)
            {
                Packet pkt = tileentity.getDescriptionPacket();
                if (pkt != null)
                {
                    entityPlayer.playerNetServerHandler.sendPacket(pkt);
                }
            }
        }
        return event;
    }
}
