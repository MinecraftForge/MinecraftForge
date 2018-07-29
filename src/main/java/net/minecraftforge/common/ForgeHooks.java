/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTippedArrow;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketRecipeBook;
import net.minecraft.network.play.server.SPacketRecipeBook.State;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.DifficultyChangeEvent;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher.ConnectionType;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

public class ForgeHooks
{
    //TODO: Loot tables?
    static class SeedEntry extends WeightedRandom.Item
    {
        @Nonnull
        public final ItemStack seed;
        public SeedEntry(@Nonnull ItemStack seed, int weight)
        {
            super(weight);
            this.seed = seed;
        }
        @Nonnull
        public ItemStack getStack(Random rand, int fortune)
        {
            return seed.copy();
        }
    }
    static final List<SeedEntry> seedList = new ArrayList<SeedEntry>();

    @Nonnull
    public static ItemStack getGrassSeed(Random rand, int fortune)
    {
        if (seedList.size() == 0)
        {
            return ItemStack.EMPTY; //Some bad mods hack in and empty our list, so lets not hard crash -.-
        }
        SeedEntry entry = WeightedRandom.getRandomItem(rand, seedList);
        if (entry == null || entry.seed.isEmpty())
        {
            return ItemStack.EMPTY;
        }
        return entry.getStack(rand, fortune);
    }

    private static boolean toolInit = false;
    //static HashSet<List> toolEffectiveness = new HashSet<List>();

    public static boolean canHarvestBlock(@Nonnull Block block, @Nonnull EntityPlayer player, @Nonnull IBlockAccess world, @Nonnull BlockPos pos)
    {
        IBlockState state = world.getBlockState(pos);
        state = state.getBlock().getActualState(state, world, pos);
        if (state.getMaterial().isToolNotRequired())
        {
            return true;
        }

        ItemStack stack = player.getHeldItemMainhand();
        String tool = block.getHarvestTool(state);
        if (stack.isEmpty() || tool == null)
        {
            return player.canHarvestBlock(state);
        }

        int toolLevel = stack.getItem().getHarvestLevel(stack, tool, player, state);
        if (toolLevel < 0)
        {
            return player.canHarvestBlock(state);
        }

        return toolLevel >= block.getHarvestLevel(state);
    }

    public static boolean canToolHarvestBlock(IBlockAccess world, BlockPos pos, @Nonnull ItemStack stack)
    {
        IBlockState state = world.getBlockState(pos);
        state = state.getBlock().getActualState(state, world, pos);
        String tool = state.getBlock().getHarvestTool(state);
        if (stack.isEmpty() || tool == null) return false;
        return stack.getItem().getHarvestLevel(stack, tool, null, null) >= state.getBlock().getHarvestLevel(state);
    }

    public static float blockStrength(@Nonnull IBlockState state, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos)
    {
        float hardness = state.getBlockHardness(world, pos);
        if (hardness < 0.0F)
        {
            return 0.0F;
        }

        if (!canHarvestBlock(state.getBlock(), player, world, pos))
        {
            return player.getDigSpeed(state, pos) / hardness / 100F;
        }
        else
        {
            return player.getDigSpeed(state, pos) / hardness / 30F;
        }
    }

    public static boolean isToolEffective(IBlockAccess world, BlockPos pos, @Nonnull ItemStack stack)
    {
        IBlockState state = world.getBlockState(pos);
        state = state.getBlock().getActualState(state, world, pos);
        for (String type : stack.getItem().getToolClasses(stack))
        {
            if (state.getBlock().isToolEffective(type, state))
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

        Blocks.OBSIDIAN.setHarvestLevel("pickaxe", 3);
        Blocks.ENCHANTING_TABLE.setHarvestLevel("pickaxe", 0);
        Block[] oreBlocks = new Block[] {
                Blocks.EMERALD_ORE, Blocks.EMERALD_BLOCK, Blocks.DIAMOND_ORE, Blocks.DIAMOND_BLOCK,
                Blocks.GOLD_ORE, Blocks.GOLD_BLOCK, Blocks.REDSTONE_ORE, Blocks.LIT_REDSTONE_ORE
        };
        for (Block block : oreBlocks)
        {
            block.setHarvestLevel("pickaxe", 2);
        }
        Blocks.IRON_ORE.setHarvestLevel("pickaxe", 1);
        Blocks.IRON_BLOCK.setHarvestLevel("pickaxe", 1);
        Blocks.LAPIS_ORE.setHarvestLevel("pickaxe", 1);
        Blocks.LAPIS_BLOCK.setHarvestLevel("pickaxe", 1);
        Blocks.QUARTZ_ORE.setHarvestLevel("pickaxe", 0);
    }

    public static int getTotalArmorValue(EntityPlayer player)
    {
        int ret = player.getTotalArmorValue();
        for (int x = 0; x < player.inventory.armorInventory.size(); x++)
        {
            ItemStack stack = player.inventory.armorInventory.get(x);
            if (stack.getItem() instanceof ISpecialArmor)
            {
                ret += ((ISpecialArmor)stack.getItem()).getArmorDisplay(player, stack, x);
            }
        }
        return ret;
    }

    static
    {
        seedList.add(new SeedEntry(new ItemStack(Items.WHEAT_SEEDS), 10)
        {
            @Override
            @Nonnull
            public ItemStack getStack(Random rand, int fortune)
            {
                return new ItemStack(Items.WHEAT_SEEDS, 1 + rand.nextInt(fortune * 2 + 1));
            }
        });
        initTools();
    }

    /**
     * Called when a player uses 'pick block', calls new Entity and Block hooks.
     */
    public static boolean onPickBlock(RayTraceResult target, EntityPlayer player, World world)
    {
        /*
            boolean flag = this.player.capabilities.isCreativeMode;
            TileEntity tileentity = null;
            ItemStack itemstack;

            if (this.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK)
            {
                BlockPos blockpos = this.objectMouseOver.getBlockPos();
                IBlockState iblockstate = this.world.getBlockState(blockpos);
                Block block = iblockstate.getBlock();

                if (iblockstate.getMaterial() == Material.AIR)
                {
                    return;
                }

                itemstack = block.getItem(this.world, blockpos, iblockstate);

                if (itemstack.isEmpty())
                {
                    return;
                }

                if (flag && GuiScreen.isCtrlKeyDown() && block.hasTileEntity())
                {
                    tileentity = this.world.getTileEntity(blockpos);
                }
            }
            else
            {
                if (this.objectMouseOver.typeOfHit != RayTraceResult.Type.ENTITY || this.objectMouseOver.entityHit == null || !flag)
                {
                    return;
                }

                if (this.objectMouseOver.entityHit instanceof EntityPainting)
                {
                    itemstack = new ItemStack(Items.PAINTING);
                }
                else if (this.objectMouseOver.entityHit instanceof EntityLeashKnot)
                {
                    itemstack = new ItemStack(Items.LEAD);
                }
                else if (this.objectMouseOver.entityHit instanceof EntityItemFrame)
                {
                    EntityItemFrame entityitemframe = (EntityItemFrame)this.objectMouseOver.entityHit;
                    ItemStack itemstack1 = entityitemframe.getDisplayedItem();

                    if (itemstack1.isEmpty())
                    {
                        itemstack = new ItemStack(Items.ITEM_FRAME);
                    }
                    else
                    {
                        itemstack = itemstack1.copy();
                    }
                }
                else if (this.objectMouseOver.entityHit instanceof EntityMinecart)
                {
                    EntityMinecart entityminecart = (EntityMinecart)this.objectMouseOver.entityHit;
                    Item item;

                    switch (entityminecart.getType())
                    {
                        case FURNACE:
                            item = Items.FURNACE_MINECART;
                            break;
                        case CHEST:
                            item = Items.CHEST_MINECART;
                            break;
                        case TNT:
                            item = Items.TNT_MINECART;
                            break;
                        case HOPPER:
                            item = Items.HOPPER_MINECART;
                            break;
                        case COMMAND_BLOCK:
                            item = Items.COMMAND_BLOCK_MINECART;
                            break;
                        default:
                            item = Items.MINECART;
                    }

                    itemstack = new ItemStack(item);
                }
                else if (this.objectMouseOver.entityHit instanceof EntityBoat)
                {
                    itemstack = new ItemStack(((EntityBoat)this.objectMouseOver.entityHit).getItemBoat());
                }
                else if (this.objectMouseOver.entityHit instanceof EntityArmorStand)
                {
                    itemstack = new ItemStack(Items.ARMOR_STAND);
                }
                else if (this.objectMouseOver.entityHit instanceof EntityEnderCrystal)
                {
                    itemstack = new ItemStack(Items.END_CRYSTAL);
                }
                else
                {
                    ResourceLocation resourcelocation = EntityList.func_191301_a(this.objectMouseOver.entityHit);

                    if (resourcelocation == null || !EntityList.ENTITY_EGGS.containsKey(resourcelocation))
                    {
                        return;
                    }

                    itemstack = new ItemStack(Items.SPAWN_EGG);
                    ItemMonsterPlacer.applyEntityIdToItemStack(itemstack, resourcelocation);
                }
            }

            if (itemstack.isEmpty())
            {
                String s = "";

                if (this.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK)
                {
                    s = ((ResourceLocation)Block.REGISTRY.getNameForObject(this.world.getBlockState(this.objectMouseOver.getBlockPos()).getBlock())).toString();
                }
                else if (this.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY)
                {
                    s = EntityList.func_191301_a(this.objectMouseOver.entityHit).toString();
                }

                LOGGER.warn("Picking on: [{}] {} gave null item", new Object[] {this.objectMouseOver.typeOfHit, s});
            }
            else
            {
                InventoryPlayer inventoryplayer = this.player.inventory;

                if (tileentity != null)
                {
                    this.storeTEInStack(itemstack, tileentity);
                }

                int i = inventoryplayer.getSlotFor(itemstack);

                if (flag)
                {
                    inventoryplayer.setPickedItemStack(itemstack);
                    this.playerController.sendSlotPacket(this.player.getHeldItem(EnumHand.MAIN_HAND), 36 + inventoryplayer.currentItem);
                }
                else if (i != -1)
                {
                    if (InventoryPlayer.isHotbar(i))
                    {
                        inventoryplayer.currentItem = i;
                    }
                    else
                    {
                        this.playerController.pickItem(i);
                    }
                }
            }
         */
        ItemStack result;
        boolean isCreative = player.capabilities.isCreativeMode;
        TileEntity te = null;

        if (target.typeOfHit == RayTraceResult.Type.BLOCK)
        {
            IBlockState state = world.getBlockState(target.getBlockPos());

            if (state.getBlock().isAir(state, world, target.getBlockPos()))
            {
                return false;
            }

            if (isCreative && GuiScreen.isCtrlKeyDown() && state.getBlock().hasTileEntity(state))
                te = world.getTileEntity(target.getBlockPos());

            result = state.getBlock().getPickBlock(state, target, world, target.getBlockPos(), player);
        }
        else
        {
            if (target.typeOfHit != RayTraceResult.Type.ENTITY || target.entityHit == null || !isCreative)
            {
                return false;
            }

            result = target.entityHit.getPickedResult(target);
        }

        if (result.isEmpty())
        {
            return false;
        }

        if (te != null)
        {
            Minecraft.getMinecraft().storeTEInStack(result, te);
        }

        if (isCreative)
        {
            player.inventory.setPickedItemStack(result);
            Minecraft.getMinecraft().playerController.sendSlotPacket(player.getHeldItem(EnumHand.MAIN_HAND), 36 + player.inventory.currentItem);
            return true;
        }
        int slot = player.inventory.getSlotFor(result);
        if (slot != -1)
        {
            if (InventoryPlayer.isHotbar(slot))
                player.inventory.currentItem = slot;
            else
                Minecraft.getMinecraft().playerController.pickItem(slot);
            return true;
        }
        return false;
    }

    public static void onDifficultyChange(EnumDifficulty difficulty, EnumDifficulty oldDifficulty)
    {
        MinecraftForge.EVENT_BUS.post(new DifficultyChangeEvent(difficulty, oldDifficulty));
    }

    //Optifine Helper Functions u.u, these are here specifically for Optifine
    //Note: When using Optifine, these methods are invoked using reflection, which
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

    public static boolean onLivingDrops(EntityLivingBase entity, DamageSource source, ArrayList<EntityItem> drops, int lootingLevel, boolean recentlyHit)
    {
        return MinecraftForge.EVENT_BUS.post(new LivingDropsEvent(entity, source, drops, lootingLevel, recentlyHit));
    }

    @Nullable
    public static float[] onLivingFall(EntityLivingBase entity, float distance, float damageMultiplier)
    {
        LivingFallEvent event = new LivingFallEvent(entity, distance, damageMultiplier);
        return (MinecraftForge.EVENT_BUS.post(event) ? null : new float[]{event.getDistance(), event.getDamageMultiplier()});
    }

    public static int getLootingLevel(Entity target, @Nullable Entity killer, DamageSource cause)
    {
        int looting = 0;
        if (killer instanceof EntityLivingBase)
        {
            looting = EnchantmentHelper.getLootingModifier((EntityLivingBase)killer);
        }
        if (target instanceof EntityLivingBase)
        {
            looting = getLootingLevel((EntityLivingBase)target, cause, looting);
        }
        return looting;
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

    public static boolean isLivingOnLadder(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityLivingBase entity)
    {
        boolean isSpectator = (entity instanceof EntityPlayer && ((EntityPlayer)entity).isSpectator());
        if (isSpectator) return false;
        if (!ForgeModContainer.fullBoundingBoxLadders)
        {
            return state.getBlock().isLadder(state, world, pos, entity);
        }
        else
        {
            AxisAlignedBB bb = entity.getEntityBoundingBox();
            int mX = MathHelper.floor(bb.minX);
            int mY = MathHelper.floor(bb.minY);
            int mZ = MathHelper.floor(bb.minZ);
            for (int y2 = mY; y2 < bb.maxY; y2++)
            {
                for (int x2 = mX; x2 < bb.maxX; x2++)
                {
                    for (int z2 = mZ; z2 < bb.maxZ; z2++)
                    {
                        BlockPos tmp = new BlockPos(x2, y2, z2);
                        state = world.getBlockState(tmp);
                        if (state.getBlock().isLadder(state, world, tmp, entity))
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

    @Nullable
    public static EntityItem onPlayerTossEvent(@Nonnull EntityPlayer player, @Nonnull ItemStack item, boolean includeName)
    {
        player.captureDrops = true;
        EntityItem ret = player.dropItem(item, false, includeName);
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

        if (!player.world.isRemote)
        {
            player.getEntityWorld().spawnEntity(event.getEntityItem());
        }
        return event.getEntityItem();
    }

    public static float getEnchantPower(@Nonnull World world, @Nonnull BlockPos pos)
    {
        return world.getBlockState(pos).getBlock().getEnchantPowerBonus(world, pos);
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


    static final Pattern URL_PATTERN = Pattern.compile(
            //         schema                          ipv4            OR        namespace                 port     path         ends
            //   |-----------------|        |-------------------------|  |-------------------------|    |---------| |--|   |---------------|
            "((?:[a-z0-9]{2,}:\\/\\/)?(?:(?:[0-9]{1,3}\\.){3}[0-9]{1,3}|(?:[-\\w_]{1,}\\.[a-z]{2,}?))(?::[0-9]{1,5})?.*?(?=[!\"\u00A7 \n]|$))",
            Pattern.CASE_INSENSITIVE);

    public static ITextComponent newChatWithLinks(String string){ return newChatWithLinks(string, true); }
    public static ITextComponent newChatWithLinks(String string, boolean allowMissingHeader)
    {
        // Includes ipv4 and domain pattern
        // Matches an ip (xx.xxx.xx.xxx) or a domain (something.com) with or
        // without a protocol or path.
        ITextComponent ichat = null;
        Matcher matcher = URL_PATTERN.matcher(string);
        int lastEnd = 0;

        // Find all urls
        while (matcher.find())
        {
            int start = matcher.start();
            int end = matcher.end();

            // Append the previous left overs.
            String part = string.substring(lastEnd, start);
            if (part.length() > 0)
            {
                if (ichat == null)
                    ichat = new TextComponentString(part);
                else
                    ichat.appendText(part);
            }
            lastEnd = end;
            String url = string.substring(start, end);
            ITextComponent link = new TextComponentString(url);

            try
            {
                // Add schema so client doesn't crash.
                if ((new URI(url)).getScheme() == null)
                {
                    if (!allowMissingHeader)
                    {
                        if (ichat == null)
                            ichat = new TextComponentString(url);
                        else
                            ichat.appendText(url);
                        continue;
                    }
                    url = "http://" + url;
                }
            }
            catch (URISyntaxException e)
            {
                // Bad syntax bail out!
                if (ichat == null) ichat = new TextComponentString(url);
                else ichat.appendText(url);
                continue;
            }

            // Set the click event and append the link.
            ClickEvent click = new ClickEvent(ClickEvent.Action.OPEN_URL, url);
            link.getStyle().setClickEvent(click);
            link.getStyle().setUnderlined(true);
            link.getStyle().setColor(TextFormatting.BLUE);
            if (ichat == null)
                ichat = link;
            else
                ichat.appendSibling(link);
        }

        // Append the rest of the message.
        String end = string.substring(lastEnd);
        if (ichat == null)
            ichat = new TextComponentString(end);
        else if (end.length() > 0)
            ichat.appendText(string.substring(lastEnd));
        return ichat;
    }

    public static int onBlockBreakEvent(World world, GameType gameType, EntityPlayerMP entityPlayer, BlockPos pos)
    {
        // Logic from tryHarvestBlock for pre-canceling the event
        boolean preCancelEvent = false;
        ItemStack itemstack = entityPlayer.getHeldItemMainhand();
        if (gameType.isCreative() && !itemstack.isEmpty()
                && !itemstack.getItem().canDestroyBlockInCreative(world, pos, itemstack, entityPlayer))
            preCancelEvent = true;

        if (gameType.hasLimitedInteractions())
        {
            if (gameType == GameType.SPECTATOR)
                preCancelEvent = true;

            if (!entityPlayer.isAllowEdit())
            {
                if (itemstack.isEmpty() || !itemstack.canDestroy(world.getBlockState(pos).getBlock()))
                    preCancelEvent = true;
            }
        }

        // Tell client the block is gone immediately then process events
        if (world.getTileEntity(pos) == null)
        {
            SPacketBlockChange packet = new SPacketBlockChange(world, pos);
            packet.blockState = Blocks.AIR.getDefaultState();
            entityPlayer.connection.sendPacket(packet);
        }

        // Post the block break event
        IBlockState state = world.getBlockState(pos);
        BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, pos, state, entityPlayer);
        event.setCanceled(preCancelEvent);
        MinecraftForge.EVENT_BUS.post(event);

        // Handle if the event is canceled
        if (event.isCanceled())
        {
            // Let the client know the block still exists
            entityPlayer.connection.sendPacket(new SPacketBlockChange(world, pos));

            // Update any tile entity data for this block
            TileEntity tileentity = world.getTileEntity(pos);
            if (tileentity != null)
            {
                Packet<?> pkt = tileentity.getUpdatePacket();
                if (pkt != null)
                {
                    entityPlayer.connection.sendPacket(pkt);
                }
            }
        }
        return event.isCanceled() ? -1 : event.getExpToDrop();
    }

    public static EnumActionResult onPlaceItemIntoWorld(@Nonnull ItemStack itemstack, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing side, float hitX, float hitY, float hitZ, @Nonnull EnumHand hand)
    {
        // handle all placement events here
        int meta = itemstack.getItemDamage();
        int size = itemstack.getCount();
        NBTTagCompound nbt = null;
        if (itemstack.getTagCompound() != null)
        {
            nbt = itemstack.getTagCompound().copy();
        }

        if (!(itemstack.getItem() instanceof ItemBucket)) // if not bucket
        {
            world.captureBlockSnapshots = true;
        }

        EnumActionResult ret = itemstack.getItem().onItemUse(player, world, pos, hand, side, hitX, hitY, hitZ);
        world.captureBlockSnapshots = false;

        if (ret == EnumActionResult.SUCCESS)
        {
            // save new item data
            int newMeta = itemstack.getItemDamage();
            int newSize = itemstack.getCount();
            NBTTagCompound newNBT = null;
            if (itemstack.getTagCompound() != null)
            {
                newNBT = itemstack.getTagCompound().copy();
            }
            BlockEvent.PlaceEvent placeEvent = null;
            @SuppressWarnings("unchecked")
            List<BlockSnapshot> blockSnapshots = (List<BlockSnapshot>)world.capturedBlockSnapshots.clone();
            world.capturedBlockSnapshots.clear();

            // make sure to set pre-placement item data for event
            itemstack.setItemDamage(meta);
            itemstack.setCount(size);
            if (nbt != null)
            {
                itemstack.setTagCompound(nbt);
            }
            if (blockSnapshots.size() > 1)
            {
                placeEvent = ForgeEventFactory.onPlayerMultiBlockPlace(player, blockSnapshots, side, hand);
            }
            else if (blockSnapshots.size() == 1)
            {
                placeEvent = ForgeEventFactory.onPlayerBlockPlace(player, blockSnapshots.get(0), side, hand);
            }

            if (placeEvent != null && placeEvent.isCanceled())
            {
                ret = EnumActionResult.FAIL; // cancel placement
                // revert back all captured blocks
                for (BlockSnapshot blocksnapshot : Lists.reverse(blockSnapshots))
                {
                    world.restoringBlockSnapshots = true;
                    blocksnapshot.restore(true, false);
                    world.restoringBlockSnapshots = false;
                }
            }
            else
            {
                // Change the stack to its new content
                itemstack.setItemDamage(newMeta);
                itemstack.setCount(newSize);
                if (nbt != null)
                {
                    itemstack.setTagCompound(newNBT);
                }

                for (BlockSnapshot snap : blockSnapshots)
                {
                    int updateFlag = snap.getFlag();
                    IBlockState oldBlock = snap.getReplacedBlock();
                    IBlockState newBlock = world.getBlockState(snap.getPos());
                    if (!newBlock.getBlock().hasTileEntity(newBlock)) // Containers get placed automatically
                    {
                        newBlock.getBlock().onBlockAdded(world, snap.getPos(), newBlock);
                    }

                    world.markAndNotifyBlock(snap.getPos(), null, oldBlock, newBlock, updateFlag);
                }
                player.addStat(StatList.getObjectUseStats(itemstack.getItem()));
            }
        }
        world.capturedBlockSnapshots.clear();

        return ret;
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

    public static boolean onNoteChange(TileEntityNote te, byte old)
    {
        NoteBlockEvent.Change e = new NoteBlockEvent.Change(te.getWorld(), te.getPos(), te.getWorld().getBlockState(te.getPos()), old, te.note);
        if (MinecraftForge.EVENT_BUS.post(e))
        {
            te.note = old;
            return false;
        }
        te.note = (byte)e.getVanillaNoteId();
        return true;
    }

    /**
     * Default implementation of IRecipe.func_179532_b {getRemainingItems} because
     * this is just copy pasted over a lot of recipes.
     *
     * @param inv Crafting inventory
     * @return Crafting inventory contents after the recipe.
     */
    public static NonNullList<ItemStack> defaultRecipeGetRemainingItems(InventoryCrafting inv)
    {
        NonNullList<ItemStack> ret = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        for (int i = 0; i < ret.size(); i++)
        {
            ret.set(i, getContainerItem(inv.getStackInSlot(i)));
        }
        return ret;
    }

    private static ThreadLocal<EntityPlayer> craftingPlayer = new ThreadLocal<EntityPlayer>();
    public static void setCraftingPlayer(EntityPlayer player)
    {
        craftingPlayer.set(player);
    }
    public static EntityPlayer getCraftingPlayer()
    {
        return craftingPlayer.get();
    }
    @Nonnull
    public static ItemStack getContainerItem(@Nonnull ItemStack stack)
    {
        if (stack.getItem().hasContainerItem(stack))
        {
            stack = stack.getItem().getContainerItem(stack);
            if (!stack.isEmpty() && stack.isItemStackDamageable() && stack.getMetadata() > stack.getMaxDamage())
            {
                ForgeEventFactory.onPlayerDestroyItem(craftingPlayer.get(), stack, null);
                return ItemStack.EMPTY;
            }
            return stack;
        }
        return ItemStack.EMPTY;
    }

    public static boolean isInsideOfMaterial(Material material, Entity entity, BlockPos pos)
    {
        IBlockState state = entity.world.getBlockState(pos);
        Block block = state.getBlock();
        double eyes = entity.posY + (double)entity.getEyeHeight();

        double filled = 1.0f; //If it's not a liquid assume it's a solid block
        if (block instanceof IFluidBlock)
        {
            filled = ((IFluidBlock)block).getFilledPercentage(entity.world, pos);
        }
        else if (block instanceof BlockLiquid)
        {
            filled = 1.0 - (BlockLiquid.getLiquidHeightPercent(block.getMetaFromState(state)) - (1.0 / 9.0));
        }

        if (filled < 0)
        {
            return eyes > pos.getY() + (filled + 1);
        }
        else
        {
            return eyes < pos.getY() + filled;
        }
    }

    public static boolean onPlayerAttackTarget(EntityPlayer player, Entity target)
    {
        if (MinecraftForge.EVENT_BUS.post(new AttackEntityEvent(player, target))) return false;
        ItemStack stack = player.getHeldItemMainhand();
        return stack.isEmpty() || !stack.getItem().onLeftClickEntity(stack, player, target);
    }

    public static boolean onTravelToDimension(Entity entity, int dimension)
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

    @Nullable
    public static RayTraceResult rayTraceEyes(EntityLivingBase entity, double length)
    {
        Vec3d startPos = new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
        Vec3d endPos = startPos.add(new Vec3d(entity.getLookVec().x * length, entity.getLookVec().y * length, entity.getLookVec().z * length));
        return entity.world.rayTraceBlocks(startPos, endPos);
    }

    @Nullable
    public static Vec3d rayTraceEyeHitVec(EntityLivingBase entity, double length)
    {
        RayTraceResult git = rayTraceEyes(entity, length);
        return git == null ? null : git.hitVec;
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

    private static ThreadLocal<Deque<LootTableContext>> lootContext = new ThreadLocal<Deque<LootTableContext>>();
    private static LootTableContext getLootTableContext()
    {
        LootTableContext ctx = lootContext.get().peek();

        if (ctx == null)
            throw new JsonParseException("Invalid call stack, could not grab json context!"); // Should I throw this? Do we care about custom deserializers outside the manager?

        return ctx;
    }

    @Nullable
    public static LootTable loadLootTable(Gson gson, ResourceLocation name, String data, boolean custom, LootTableManager lootTableManager)
    {
        Deque<LootTableContext> que = lootContext.get();
        if (que == null)
        {
            que = Queues.newArrayDeque();
            lootContext.set(que);
        }

        LootTable ret = null;
        try
        {
            que.push(new LootTableContext(name, custom));
            ret = gson.fromJson(data, LootTable.class);
            que.pop();
        }
        catch (JsonParseException e)
        {
            que.pop();
            throw e;
        }

        if (!custom)
            ret = ForgeEventFactory.loadLootTable(name, ret, lootTableManager);

        if (ret != null)
            ret.freeze();

        return ret;
    }

    private static class LootTableContext
    {
        public final ResourceLocation name;
        private final boolean vanilla;
        public final boolean custom;
        public int poolCount = 0;
        public int entryCount = 0;
        private HashSet<String> entryNames = Sets.newHashSet();

        private LootTableContext(ResourceLocation name, boolean custom)
        {
            this.name = name;
            this.custom = custom;
            this.vanilla = "minecraft".equals(this.name.getResourceDomain());
        }

        private void resetPoolCtx()
        {
            this.entryCount = 0;
            this.entryNames.clear();
        }

        public String validateEntryName(@Nullable String name)
        {
            if (name != null && !this.entryNames.contains(name))
            {
                this.entryNames.add(name);
                return name;
            }

            if (!this.vanilla)
                throw new JsonParseException("Loot Table \"" + this.name.toString() + "\" Duplicate entry name \"" + name + "\" for pool #" + (this.poolCount - 1) + " entry #" + (this.entryCount-1));

            int x = 0;
            while (this.entryNames.contains(name + "#" + x))
                x++;

            name = name + "#" + x;
            this.entryNames.add(name);

            return name;
        }
    }

    public static String readPoolName(JsonObject json)
    {
        LootTableContext ctx = ForgeHooks.getLootTableContext();
        ctx.resetPoolCtx();

        if (json.has("name"))
            return JsonUtils.getString(json, "name");

        if (ctx.custom)
            return "custom#" + json.hashCode(); //We don't care about custom ones modders shouldn't be editing them!

        ctx.poolCount++;

        if (!ctx.vanilla)
            throw new JsonParseException("Loot Table \"" + ctx.name.toString() + "\" Missing `name` entry for pool #" + (ctx.poolCount - 1));

        return ctx.poolCount == 1 ? "main" : "pool" + (ctx.poolCount - 1);
    }

    public static String readLootEntryName(JsonObject json, String type)
    {
        LootTableContext ctx = ForgeHooks.getLootTableContext();
        ctx.entryCount++;

        if (json.has("entryName"))
            return ctx.validateEntryName(JsonUtils.getString(json, "entryName"));

        if (ctx.custom)
            return "custom#" + json.hashCode(); //We don't care about custom ones modders shouldn't be editing them!

        String name = null;
        if ("item".equals(type))
            name = JsonUtils.getString(json, "name");
        else if ("loot_table".equals(type))
            name = JsonUtils.getString(json, "name");
        else if ("empty".equals(type))
            name = "empty";

        return ctx.validateEntryName(name);
    }


    //TODO: Some registry to support custom LootEntry types?
    public static LootEntry deserializeJsonLootEntry(String type, JsonObject json, int weight, int quality, LootCondition[] conditions){ return null; }
    public static String getLootEntryType(LootEntry entry){ return null; } //Companion to above function

    /** @deprecated use {@link ForgeEventFactory#onProjectileImpact(EntityThrowable, RayTraceResult)} */
    @Deprecated // TODO: remove (1.13)
    public static boolean onThrowableImpact(EntityThrowable throwable, RayTraceResult ray)
    {
        return ForgeEventFactory.onProjectileImpact(throwable, ray);
    }

    public static boolean onCropsGrowPre(World worldIn, BlockPos pos, IBlockState state, boolean def)
    {
        BlockEvent ev = new BlockEvent.CropGrowEvent.Pre(worldIn,pos,state);
        MinecraftForge.EVENT_BUS.post(ev);
        return (ev.getResult() == Event.Result.ALLOW || (ev.getResult() == Event.Result.DEFAULT && def));
    }

    public static void onCropsGrowPost(World worldIn, BlockPos pos, IBlockState state, IBlockState blockState)
    {
        MinecraftForge.EVENT_BUS.post(new BlockEvent.CropGrowEvent.Post(worldIn, pos, state, worldIn.getBlockState(pos)));
    }

    private static final ClassValue<String> registryNames = new ClassValue<String>()
    {
        @Override
        @SuppressWarnings("unchecked")
        protected String computeValue(Class<?> type)
        {
            return String.valueOf(TileEntity.getKey((Class<? extends TileEntity>) type));
        }
    };

    public static String getRegistryName(Class<? extends TileEntity> type)
    {
        return registryNames.get(type);
    }

    public static boolean loadAdvancements(Map<ResourceLocation, Advancement.Builder> map)
    {
        boolean errored = false;
        setActiveModContainer(null);
        //Loader.instance().getActiveModList().forEach((mod) -> loadFactories(mod));
        for (ModContainer mod : Loader.instance().getActiveModList())
        {
            errored |= !loadAdvancements(map, mod);
        }
        setActiveModContainer(null);
        return errored;
    }

    @Nullable
    public static CriticalHitEvent getCriticalHit(EntityPlayer player, Entity target, boolean vanillaCritical, float damageModifier)
    {
        CriticalHitEvent hitResult = new CriticalHitEvent(player, target, damageModifier, vanillaCritical);
        MinecraftForge.EVENT_BUS.post(hitResult);
        if (hitResult.getResult() == Event.Result.ALLOW || (vanillaCritical && hitResult.getResult() == Event.Result.DEFAULT))
        {
            return hitResult;
        }
        return null;
    }

    private static void setActiveModContainer(ModContainer mod)
    {
        if (Loader.instance().getLoaderState() != LoaderState.NOINIT) //Unit Tests..
            Loader.instance().setActiveModContainer(mod);
    }

    private static boolean loadAdvancements(Map<ResourceLocation, Advancement.Builder> map, ModContainer mod)
    {
        return CraftingHelper.findFiles(mod, "assets/" + mod.getModId() + "/advancements", null,
            (root, file) ->
            {

                String relative = root.relativize(file).toString();
                if (!"json".equals(FilenameUtils.getExtension(file.toString())) || relative.startsWith("_"))
                    return true;

                String name = FilenameUtils.removeExtension(relative).replaceAll("\\\\", "/");
                ResourceLocation key = new ResourceLocation(mod.getModId(), name);

                if (!map.containsKey(key))
                {
                    BufferedReader reader = null;

                    try
                    {
                        reader = Files.newBufferedReader(file);
                        Advancement.Builder builder = JsonUtils.fromJson(AdvancementManager.GSON, reader, Advancement.Builder.class);
                        map.put(key, builder);
                    }
                    catch (JsonParseException jsonparseexception)
                    {
                        FMLLog.log.error("Parsing error loading built-in advancement " + key, (Throwable)jsonparseexception);
                        return false;
                    }
                    catch (IOException ioexception)
                    {
                        FMLLog.log.error("Couldn't read advancement " + key + " from " + file, (Throwable)ioexception);
                        return false;
                    }
                    finally
                    {
                        IOUtils.closeQuietly(reader);
                    }
                }

                return true;
            },
            true, true
        );
    }

    public static void sendRecipeBook(NetHandlerPlayServer connection, State state, List<IRecipe> recipes, List<IRecipe> display, boolean isGuiOpen, boolean isFilteringCraftable)
    {
        NetworkDispatcher disp = NetworkDispatcher.get(connection.getNetworkManager());
        //Not sure how it could ever be null, but screw it lets protect against it. Could Error the client but we dont care if they are asking for this stuff in the wrong state!
        ConnectionType type = disp == null || disp.getConnectionType() == null ? ConnectionType.MODDED : disp.getConnectionType();
        if (type == ConnectionType.VANILLA)
        {
            IForgeRegistry<IRecipe> vanilla = RegistryManager.VANILLA.getRegistry(IRecipe.class);
            if (recipes.size() > 0)
                recipes = recipes.stream().filter(e -> vanilla.containsValue(e)).collect(Collectors.toList());
            if (display.size() > 0)
                display = display.stream().filter(e -> vanilla.containsValue(e)).collect(Collectors.toList());
        }

        if (recipes.size() > 0 || display.size() > 0)
            connection.sendPacket(new SPacketRecipeBook(state, recipes, display, isGuiOpen, isFilteringCraftable));
    }

    public static void onAdvancement(EntityPlayerMP player, Advancement advancement)
    {
        MinecraftForge.EVENT_BUS.post(new AdvancementEvent(player, advancement));
    }

    /**
     * Used as the default implementation of {@link Item#getCreatorModId}. Call that method instead.
     */
    @Nullable
    public static String getDefaultCreatorModId(@Nonnull ItemStack itemStack)
    {
        Item item = itemStack.getItem();
        ResourceLocation registryName = item.getRegistryName();
        String modId = registryName == null ? null : registryName.getResourceDomain();
        if ("minecraft".equals(modId))
        {
            if (item instanceof ItemEnchantedBook)
            {
                NBTTagList enchantmentsNbt = ItemEnchantedBook.getEnchantments(itemStack);
                if (enchantmentsNbt.tagCount() == 1)
                {
                    NBTTagCompound nbttagcompound = enchantmentsNbt.getCompoundTagAt(0);
                    Enchantment enchantment = Enchantment.getEnchantmentByID(nbttagcompound.getShort("id"));
                    if (enchantment != null)
                    {
                        ResourceLocation resourceLocation = ForgeRegistries.ENCHANTMENTS.getKey(enchantment);
                        if (resourceLocation != null)
                        {
                            return resourceLocation.getResourceDomain();
                        }
                    }
                }
            }
            else if (item instanceof ItemPotion || item instanceof ItemTippedArrow)
            {
                PotionType potionType = PotionUtils.getPotionFromItem(itemStack);
                ResourceLocation resourceLocation = ForgeRegistries.POTION_TYPES.getKey(potionType);
                if (resourceLocation != null)
                {
                    return resourceLocation.getResourceDomain();
                }
            }
            else if (item instanceof ItemMonsterPlacer)
            {
                ResourceLocation resourceLocation = ItemMonsterPlacer.getNamedIdFrom(itemStack);
                if (resourceLocation != null)
                {
                    return resourceLocation.getResourceDomain();
                }
            }
        }
        return modId;
    }

    public static boolean onFarmlandTrample(World world, BlockPos pos, IBlockState state, float fallDistance, Entity entity)
    {

        if (entity.canTrample(world, state.getBlock(), pos, fallDistance))
        {
            BlockEvent.FarmlandTrampleEvent event = new BlockEvent.FarmlandTrampleEvent(world, pos, state, fallDistance, entity);
            MinecraftForge.EVENT_BUS.post(event);
            return !event.isCanceled();
        }
        return false;
    }

}
