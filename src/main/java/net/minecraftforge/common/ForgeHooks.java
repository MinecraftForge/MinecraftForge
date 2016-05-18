package net.minecraftforge.common;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Throwables;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.JsonUtils;
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
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerOpenContainerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class ForgeHooks
{
    //TODO: Loot tables?
    static class SeedEntry extends WeightedRandom.Item
    {
        public final ItemStack seed;
        public SeedEntry(ItemStack seed, int weight)
        {
            super(weight);
            this.seed = seed;
        }
        public ItemStack getStack(Random rand, int fortune)
        {
            return seed.copy();
        }
    }
    static final List<SeedEntry> seedList = new ArrayList<SeedEntry>();

    public static ItemStack getGrassSeed(Random rand, int fortune)
    {
        SeedEntry entry = WeightedRandom.getRandomItem(rand, seedList);
        if (entry == null || entry.seed == null)
        {
            return null;
        }
        return entry.getStack(rand, fortune);
    }

    private static boolean toolInit = false;
    //static HashSet<List> toolEffectiveness = new HashSet<List>();

    public static boolean canHarvestBlock(Block block, EntityPlayer player, IBlockAccess world, BlockPos pos)
    {
        IBlockState state = world.getBlockState(pos);
        state = state.getBlock().getActualState(state, world, pos);
        if (state.getMaterial().isToolNotRequired())
        {
            return true;
        }

        ItemStack stack = player.inventory.getCurrentItem();
        String tool = block.getHarvestTool(state);
        if (stack == null || tool == null)
        {
            return player.canHarvestBlock(state);
        }

        int toolLevel = stack.getItem().getHarvestLevel(stack, tool);
        if (toolLevel < 0)
        {
            return player.canHarvestBlock(state);
        }

        return toolLevel >= block.getHarvestLevel(state);
    }

    public static boolean canToolHarvestBlock(IBlockAccess world, BlockPos pos, ItemStack stack)
    {
        IBlockState state = world.getBlockState(pos);
        state = state.getBlock().getActualState(state, world, pos);
        String tool = state.getBlock().getHarvestTool(state);
        if (stack == null || tool == null) return false;
        return stack.getItem().getHarvestLevel(stack, tool) >= state.getBlock().getHarvestLevel(state);
    }

    public static float blockStrength(IBlockState state, EntityPlayer player, World world, BlockPos pos)
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

    public static boolean isToolEffective(IBlockAccess world, BlockPos pos, ItemStack stack)
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

        Blocks.obsidian.setHarvestLevel("pickaxe", 3);
        Block[] oreBlocks = new Block[] {
                Blocks.emerald_ore, Blocks.emerald_block, Blocks.diamond_ore, Blocks.diamond_block,
                Blocks.gold_ore, Blocks.gold_block, Blocks.redstone_ore, Blocks.lit_redstone_ore
        };
        for (Block block : oreBlocks)
        {
            block.setHarvestLevel("pickaxe", 2);
        }
        Blocks.iron_ore.setHarvestLevel("pickaxe", 1);
        Blocks.iron_block.setHarvestLevel("pickaxe", 1);
        Blocks.lapis_ore.setHarvestLevel("pickaxe", 1);
        Blocks.lapis_block.setHarvestLevel("pickaxe", 1);
        Blocks.quartz_ore.setHarvestLevel("pickaxe", 0);
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
        seedList.add(new SeedEntry(new ItemStack(Items.wheat_seeds), 10)
        {
            public ItemStack getStack(Random rand, int fortune)
            {
                return new ItemStack(Items.wheat_seeds, 1 + rand.nextInt(fortune * 2 + 1));
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
            TileEntity tileentity = null;
            ItemStack itemstack;

            if (this.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK)
            {
                BlockPos blockpos = this.objectMouseOver.getBlockPos();
                IBlockState iblockstate = this.theWorld.getBlockState(blockpos);
                Block block = iblockstate.getBlock();

                if (iblockstate.getMaterial() == Material.air)
                {
                    return;
                }

                itemstack = block.func_185473_a(this.theWorld, blockpos, iblockstate);

                if (itemstack == null)
                {
                    return;
                }

                if (flag && GuiScreen.isCtrlKeyDown() && block.hasTileEntity())
                {
                    tileentity = this.theWorld.getTileEntity(blockpos);
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
                    itemstack = new ItemStack(Items.painting);
                }
                else if (this.objectMouseOver.entityHit instanceof EntityLeashKnot)
                {
                    itemstack = new ItemStack(Items.lead);
                }
                else if (this.objectMouseOver.entityHit instanceof EntityItemFrame)
                {
                    EntityItemFrame entityitemframe = (EntityItemFrame)this.objectMouseOver.entityHit;
                    ItemStack itemstack1 = entityitemframe.getDisplayedItem();

                    if (itemstack1 == null)
                    {
                        itemstack = new ItemStack(Items.item_frame);
                    }
                    else
                    {
                        itemstack = ItemStack.copyItemStack(itemstack1);
                    }
                }
                else if (this.objectMouseOver.entityHit instanceof EntityMinecart)
                {
                    EntityMinecart entityminecart = (EntityMinecart)this.objectMouseOver.entityHit;
                    Item item;

                    switch (entityminecart.func_184264_v())
                    {
                        case FURNACE:
                            item = Items.furnace_minecart;
                            break;
                        case CHEST:
                            item = Items.chest_minecart;
                            break;
                        case TNT:
                            item = Items.tnt_minecart;
                            break;
                        case HOPPER:
                            item = Items.hopper_minecart;
                            break;
                        case COMMAND_BLOCK:
                            item = Items.command_block_minecart;
                            break;
                        default:
                            item = Items.minecart;
                    }

                    itemstack = new ItemStack(item);
                }
                else if (this.objectMouseOver.entityHit instanceof EntityBoat)
                {
                    itemstack = new ItemStack(((EntityBoat)this.objectMouseOver.entityHit).func_184455_j());
                }
                else if (this.objectMouseOver.entityHit instanceof EntityArmorStand)
                {
                    itemstack = new ItemStack(Items.armor_stand);
                }
                else if (this.objectMouseOver.entityHit instanceof EntityEnderCrystal)
                {
                    itemstack = new ItemStack(Items.field_185158_cP);
                }
                else
                {
                    String s = EntityList.getEntityString(this.objectMouseOver.entityHit);

                    if (!EntityList.entityEggs.containsKey(s))
                    {
                        return;
                    }

                    itemstack = new ItemStack(Items.spawn_egg);
                    ItemMonsterPlacer.func_185078_a(itemstack, s);
                }
            }
         */
        ItemStack result = null;
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

        if (result == null)
        {
            return false;
        }

        if (result.getItem() == null)
        {
            String s1 = "";

            if (target.typeOfHit == RayTraceResult.Type.BLOCK)
            {
                s1 = Block.blockRegistry.getNameForObject(world.getBlockState(target.getBlockPos()).getBlock()).toString();
            }
            else if (target.typeOfHit == RayTraceResult.Type.ENTITY)
            {
                s1 = EntityList.getEntityString(target.entityHit);
            }

            FMLLog.warning("Picking on: [%s] %s gave null item", target.typeOfHit, s1);
            return true;
        }

        if (te != null)
        {
            Minecraft.getMinecraft().storeTEInStack(result, te);
        }

        if (isCreative)
        {
            player.inventory.func_184434_a(result);
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
        return !MinecraftForge.EVENT_BUS.post(new LivingAttackEvent(entity, src, amount));
    }

    public static float onLivingHurt(EntityLivingBase entity, DamageSource src, float amount)
    {
        LivingHurtEvent event = new LivingHurtEvent(entity, src, amount);
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

    public static float[] onLivingFall(EntityLivingBase entity, float distance, float damageMultiplier)
    {
        LivingFallEvent event = new LivingFallEvent(entity, distance, damageMultiplier);
        return (MinecraftForge.EVENT_BUS.post(event) ? null : new float[]{event.getDistance(), event.getDamageMultiplier()});
    }

    public static boolean isLivingOnLadder(IBlockState state, World world, BlockPos pos, EntityLivingBase entity)
    {
        boolean isSpectator = (entity instanceof EntityPlayer && ((EntityPlayer)entity).isSpectator());
        if (isSpectator) return false;
        if (!ForgeModContainer.fullBoundingBoxLadders)
        {
            return state != null && state.getBlock().isLadder(state, world, pos, entity);
        }
        else
        {
            AxisAlignedBB bb = entity.getEntityBoundingBox();
            int mX = MathHelper.floor_double(bb.minX);
            int mY = MathHelper.floor_double(bb.minY);
            int mZ = MathHelper.floor_double(bb.minZ);
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

    public static EntityItem onPlayerTossEvent(EntityPlayer player, ItemStack item, boolean includeName)
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

        if (!player.worldObj.isRemote)
        {
            player.getEntityWorld().spawnEntityInWorld(event.getEntityItem());
        }
        return event.getEntityItem();
    }

    public static float getEnchantPower(World world, BlockPos pos)
    {
        return world.getBlockState(pos).getBlock().getEnchantPowerBonus(world, pos);
    }

    public static ITextComponent onServerChatEvent(NetHandlerPlayServer net, String raw, ITextComponent comp)
    {
        ServerChatEvent event = new ServerChatEvent(net.playerEntity, raw, comp);
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
            link.getChatStyle().setChatClickEvent(click);
            link.getChatStyle().setUnderlined(true);
            link.getChatStyle().setColor(TextFormatting.BLUE);
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

    public static boolean canInteractWith(EntityPlayer player, Container openContainer)
    {
        PlayerOpenContainerEvent event = new PlayerOpenContainerEvent(player, openContainer);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult() == Event.Result.DEFAULT ? event.isCanInteractWith() : event.getResult() == Event.Result.ALLOW ? true : false;
    }

    public static int onBlockBreakEvent(World world, GameType gameType, EntityPlayerMP entityPlayer, BlockPos pos)
    {
        // Logic from tryHarvestBlock for pre-canceling the event
        boolean preCancelEvent = false;
        if (gameType.isCreative() && entityPlayer.getHeldItemMainhand() != null && entityPlayer.getHeldItemMainhand().getItem() instanceof ItemSword)
            preCancelEvent = true;

        if (gameType.isAdventure())
        {
            if (gameType == WorldSettings.GameType.SPECTATOR)
                preCancelEvent = true;

            if (!entityPlayer.isAllowEdit())
            {
                ItemStack itemstack = entityPlayer.getHeldItemMainhand();
                if (itemstack == null || !itemstack.canDestroy(world.getBlockState(pos).getBlock()))
                    preCancelEvent = true;
            }
        }

        // Tell client the block is gone immediately then process events
        if (world.getTileEntity(pos) == null)
        {
            SPacketBlockChange packet = new SPacketBlockChange(world, pos);
            packet.blockState = Blocks.air.getDefaultState();
            entityPlayer.playerNetServerHandler.sendPacket(packet);
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
            entityPlayer.playerNetServerHandler.sendPacket(new SPacketBlockChange(world, pos));

            // Update any tile entity data for this block
            TileEntity tileentity = world.getTileEntity(pos);
            if (tileentity != null)
            {
                Packet<?> pkt = tileentity.func_189518_D_();
                if (pkt != null)
                {
                    entityPlayer.playerNetServerHandler.sendPacket(pkt);
                }
            }
        }
        return event.isCanceled() ? -1 : event.getExpToDrop();
    }

    public static EnumActionResult onPlaceItemIntoWorld(ItemStack itemstack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
    {
        // handle all placement events here
        int meta = itemstack.getItemDamage();
        int size = itemstack.stackSize;
        NBTTagCompound nbt = null;
        if (itemstack.getTagCompound() != null)
        {
            nbt = (NBTTagCompound)itemstack.getTagCompound().copy();
        }

        if (!(itemstack.getItem() instanceof ItemBucket)) // if not bucket
        {
            world.captureBlockSnapshots = true;
        }

        EnumActionResult ret = itemstack.getItem().onItemUse(itemstack, player, world, pos, hand, side, hitX, hitY, hitZ);
        world.captureBlockSnapshots = false;

        if (ret == EnumActionResult.SUCCESS)
        {
            // save new item data
            int newMeta = itemstack.getItemDamage();
            int newSize = itemstack.stackSize;
            NBTTagCompound newNBT = null;
            if (itemstack.getTagCompound() != null)
            {
                newNBT = (NBTTagCompound)itemstack.getTagCompound().copy();
            }
            net.minecraftforge.event.world.BlockEvent.PlaceEvent placeEvent = null;
            @SuppressWarnings("unchecked")
            List<net.minecraftforge.common.util.BlockSnapshot> blockSnapshots = (List<BlockSnapshot>)world.capturedBlockSnapshots.clone();
            world.capturedBlockSnapshots.clear();

            // make sure to set pre-placement item data for event
            itemstack.setItemDamage(meta);
            itemstack.stackSize = size;
            if (nbt != null)
            {
                itemstack.setTagCompound(nbt);
            }
            if (blockSnapshots.size() > 1)
            {
                placeEvent = ForgeEventFactory.onPlayerMultiBlockPlace(player, blockSnapshots, side);
            }
            else if (blockSnapshots.size() == 1)
            {
                placeEvent = ForgeEventFactory.onPlayerBlockPlace(player, blockSnapshots.get(0), side);
            }

            if (placeEvent != null && (placeEvent.isCanceled()))
            {
                ret = EnumActionResult.FAIL; // cancel placement
                // revert back all captured blocks
                for (net.minecraftforge.common.util.BlockSnapshot blocksnapshot : blockSnapshots)
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
                itemstack.stackSize = newSize;
                if (nbt != null)
                {
                    itemstack.setTagCompound(newNBT);
                }

                for (BlockSnapshot snap : blockSnapshots)
                {
                    int updateFlag = snap.getFlag();
                    IBlockState oldBlock = snap.getReplacedBlock();
                    IBlockState newBlock = world.getBlockState(snap.getPos());
                    if (newBlock != null && !(newBlock.getBlock().hasTileEntity(newBlock))) // Containers get placed automatically
                    {
                        newBlock.getBlock().onBlockAdded(world, snap.getPos(), newBlock);
                    }

                    world.markAndNotifyBlock(snap.getPos(), null, oldBlock, newBlock, updateFlag);
                }
                player.addStat(StatList.func_188060_a(itemstack.getItem()));
            }
        }
        world.capturedBlockSnapshots.clear();

        return ret;
    }

    public static boolean onAnvilChange(ContainerRepair container, ItemStack left, ItemStack right, IInventory outputSlot, String name, int baseCost)
    {
        AnvilUpdateEvent e = new AnvilUpdateEvent(left, right, name, baseCost);
        if (MinecraftForge.EVENT_BUS.post(e)) return false;
        if (e.getOutput() == null) return true;

        outputSlot.setInventorySlotContents(0, e.getOutput());
        container.maximumCost = e.getCost();
        container.materialCost = e.getMaterialCost();
        return false;
    }

    public static float onAnvilRepair(EntityPlayer player, ItemStack output, ItemStack left, ItemStack right)
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
     * Another use case for java 8 but sadly we can't use it!
     *
     * @param inv Crafting inventory
     * @return Crafting inventory contents after the recipe.
     */
    public static ItemStack[] defaultRecipeGetRemainingItems(InventoryCrafting inv)
    {
        ItemStack[] ret = new ItemStack[inv.getSizeInventory()];
        for (int i = 0; i < ret.length; i++)
        {
            ret[i] = getContainerItem(inv.getStackInSlot(i));
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
    public static ItemStack getContainerItem(ItemStack stack)
    {
        if (stack == null) return null;

        if (stack.getItem().hasContainerItem(stack))
        {
            stack = stack.getItem().getContainerItem(stack);
            if (stack != null && stack.isItemStackDamageable() && stack.getMetadata() > stack.getMaxDamage())
            {
                ForgeEventFactory.onPlayerDestroyItem(craftingPlayer.get(), stack, null);
                return null;
            }
            return stack;
        }
        return null;
    }

    public static boolean isInsideOfMaterial(Material material, Entity entity, BlockPos pos)
    {
        IBlockState state = entity.worldObj.getBlockState(pos);
        Block block = state.getBlock();
        double eyes = entity.posY + (double)entity.getEyeHeight();

        double filled = 1.0f; //If it's not a liquid assume it's a solid block
        if (block instanceof IFluidBlock)
        {
            filled = ((IFluidBlock)block).getFilledPercentage(entity.worldObj, pos);
        }
        else if (block instanceof BlockLiquid)
        {
            filled = BlockLiquid.getLiquidHeightPercent(block.getMetaFromState(state));
        }

        if (filled < 0)
        {
            filled *= -1;
            //filled -= 0.11111111F; //Why this is needed.. not sure...
            return eyes > pos.getY() + 1 + (1 - filled);
        }
        else
        {
            return eyes < pos.getY() + 1 + filled;
        }
    }

    public static boolean onPlayerAttackTarget(EntityPlayer player, Entity target)
    {
        if (MinecraftForge.EVENT_BUS.post(new AttackEntityEvent(player, target))) return false;
        ItemStack stack = player.getHeldItemMainhand();
        if (stack != null && stack.getItem().onLeftClickEntity(stack, player, target)) return false;
        return true;
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

    public static RayTraceResult rayTraceEyes(EntityLivingBase entity, double length)
    {
        Vec3d startPos = new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
        Vec3d endPos = startPos.add(new Vec3d(entity.getLookVec().xCoord * length, entity.getLookVec().yCoord * length, entity.getLookVec().zCoord * length));
        return entity.worldObj.rayTraceBlocks(startPos, endPos);
    }

    public static Vec3d rayTraceEyeHitVec(EntityLivingBase entity, double length)
    {
        RayTraceResult git = rayTraceEyes(entity, length);
        return git == null ? null : git.hitVec;
    }

    public static boolean onInteractEntityAt(EntityPlayer player, Entity entity, RayTraceResult ray, ItemStack stack, EnumHand hand)
    {
        Vec3d vec3d = new Vec3d(ray.hitVec.xCoord - entity.posX, ray.hitVec.yCoord - entity.posY, ray.hitVec.zCoord - entity.posZ);
        return onInteractEntityAt(player, entity, vec3d, stack, hand);
    }

    public static boolean onInteractEntityAt(EntityPlayer player, Entity entity, Vec3d vec3d, ItemStack stack, EnumHand hand)
    {
        return MinecraftForge.EVENT_BUS.post(new PlayerInteractEvent.EntityInteractSpecific(player, hand, stack, entity, vec3d));
    }

    public static boolean onInteractEntity(EntityPlayer player, Entity entity, ItemStack item, EnumHand hand)
    {
        return MinecraftForge.EVENT_BUS.post(new PlayerInteractEvent.EntityInteract(player, hand, item, entity));
    }

    public static boolean onItemRightClick(EntityPlayer player, EnumHand hand, ItemStack stack)
    {
        return MinecraftForge.EVENT_BUS.post(new PlayerInteractEvent.RightClickItem(player, hand, stack));
    }

    public static PlayerInteractEvent.LeftClickBlock onLeftClickBlock(EntityPlayer player, BlockPos pos, EnumFacing face, Vec3d hitVec)
    {
        PlayerInteractEvent.LeftClickBlock evt = new PlayerInteractEvent.LeftClickBlock(player, pos, face, hitVec);
        MinecraftForge.EVENT_BUS.post(evt);
        return evt;
    }

    public static PlayerInteractEvent.RightClickBlock onRightClickBlock(EntityPlayer player, EnumHand hand, ItemStack stack, BlockPos pos, EnumFacing face, Vec3d hitVec)
    {
        PlayerInteractEvent.RightClickBlock evt = new PlayerInteractEvent.RightClickBlock(player, hand, stack, pos, face, hitVec);
        MinecraftForge.EVENT_BUS.post(evt);
        return evt;
    }

    public static void onEmptyClick(EntityPlayer player, EnumHand hand)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerInteractEvent.RightClickEmpty(player, hand));
    }

    private static ThreadLocal<Deque<LootTableContext>> lootContext = new ThreadLocal<Deque<LootTableContext>>();
    private static LootTableContext getLootTableContext()
    {
        LootTableContext ctx = lootContext.get().peek();

        if (ctx == null)
            throw new JsonParseException("Invalid call stack, could to grab json context!"); // Show I throw this? Do we care about custom deserializers outside the manager?

        return ctx;
    }
    public static LootTable loadLootTable(Gson gson, ResourceLocation name, String data, boolean custom)
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
            ret = ForgeEventFactory.loadLootTable(name, ret);

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

        public String validateEntryName(String name)
        {
            if (!this.entryNames.contains(name))
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
}
