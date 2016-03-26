package net.minecraftforge.common;

import static net.minecraft.init.Blocks.diamond_block;
import static net.minecraft.init.Blocks.diamond_ore;
import static net.minecraft.init.Blocks.emerald_block;
import static net.minecraft.init.Blocks.emerald_ore;
import static net.minecraft.init.Blocks.gold_block;
import static net.minecraft.init.Blocks.gold_ore;
import static net.minecraft.init.Blocks.lit_redstone_ore;
import static net.minecraft.init.Blocks.redstone_ore;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.gen.feature.WorldGeneratorBonusChest;
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
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerOpenContainerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

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

    public static ItemStack getGrassSeed(Random rand)
    {
        SeedEntry entry = (SeedEntry)WeightedRandom.getRandomItem(rand, seedList);
        if (entry == null || entry.seed == null)
        {
            return null;
        }
        return entry.seed.copy();
    }

    private static boolean toolInit = false;
    //static HashSet<List> toolEffectiveness = new HashSet<List>();

    public static boolean canHarvestBlock(Block block, EntityPlayer player, IBlockAccess world, BlockPos pos)
    {
        if (block.getMaterial().isToolNotRequired())
        {
            return true;
        }

        ItemStack stack = player.inventory.getCurrentItem();
        IBlockState state = world.getBlockState(pos);
        state = state.getBlock().getActualState(state, world, pos);
        String tool = block.getHarvestTool(state);
        if (stack == null || tool == null)
        {
            return player.canHarvestBlock(block);
        }

        int toolLevel = stack.getItem().getHarvestLevel(stack, tool);
        if (toolLevel < 0)
        {
            return player.canHarvestBlock(block);
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
        float hardness = state.getBlock().getBlockHardness(world, pos);
        if (hardness < 0.0F)
        {
            return 0.0F;
        }

        if (!canHarvestBlock(state.getBlock(), player, world, pos))
        {
            return player.getBreakSpeed(state, pos) / hardness / 100F;
        }
        else
        {
            return player.getBreakSpeed(state, pos) / hardness / 30F;
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
        for (Block block : new Block[]{emerald_ore, emerald_block, diamond_ore, diamond_block, gold_ore, gold_block, redstone_ore, lit_redstone_ore})
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
        seedList.add(new SeedEntry(new ItemStack(Items.wheat_seeds), 10));
        initTools();
    }

    /**
     * Called when a player uses 'pick block', calls new Entity and Block hooks.
     */
    public static boolean onPickBlock(MovingObjectPosition target, EntityPlayer player, World world)
    {
        /*
            int i = 0;
            boolean flag1 = false;
            TileEntity tileentity = null;
            Item item;

            if (this.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
            {
                BlockPos blockpos = this.objectMouseOver.getBlockPos();
                Block block = this.theWorld.getBlockState(blockpos).getBlock();

                if (block.getMaterial() == Material.air)
                {
                    return;
                }

                item = block.getItem(this.theWorld, blockpos);

                if (item == null)
                {
                    return;
                }

                if (flag && GuiScreen.isCtrlKeyDown())
                {
                    tileentity = this.theWorld.getTileEntity(blockpos);
                }

                Block block1 = item instanceof ItemBlock && !block.isFlowerPot() ? Block.getBlockFromItem(item) : block;
                i = block1.getDamageValue(this.theWorld, blockpos);
                flag1 = item.getHasSubtypes();
            }
            else
            {
                if (this.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY || this.objectMouseOver.entityHit == null || !flag)
                {
                    return;
                }

                if (this.objectMouseOver.entityHit instanceof EntityPainting)
                {
                    item = Items.painting;
                }
                else if (this.objectMouseOver.entityHit instanceof EntityLeashKnot)
                {
                    item = Items.lead;
                }
                else if (this.objectMouseOver.entityHit instanceof EntityItemFrame)
                {
                    EntityItemFrame entityitemframe = (EntityItemFrame)this.objectMouseOver.entityHit;
                    ItemStack itemstack = entityitemframe.getDisplayedItem();

                    if (itemstack == null)
                    {
                        item = Items.item_frame;
                    }
                    else
                    {
                        item = itemstack.getItem();
                        i = itemstack.getMetadata();
                        flag1 = true;
                    }
                }
                else if (this.objectMouseOver.entityHit instanceof EntityMinecart)
                {
                    EntityMinecart entityminecart = (EntityMinecart)this.objectMouseOver.entityHit;

                    switch (entityminecart.getMinecartType())
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
                }
                else if (this.objectMouseOver.entityHit instanceof EntityBoat)
                {
                    item = Items.boat;
                }
                else if (this.objectMouseOver.entityHit instanceof EntityArmorStand)
                {
                    item = Items.armor_stand;
                }
                else
                {
                    item = Items.spawn_egg;
                    i = EntityList.getEntityID(this.objectMouseOver.entityHit);
                    flag1 = true;

                    if (!EntityList.entityEggs.containsKey(Integer.valueOf(i)))
                    {
                        return;
                    }
                }
            }

            InventoryPlayer inventoryplayer = this.thePlayer.inventory;

            if (tileentity == null)
            {
                inventoryplayer.setCurrentItem(item, i, flag1, flag);
            }
            else
            {
                ItemStack itemstack1 = this.func_181036_a(item, i, tileentity);
                inventoryplayer.setInventorySlotContents(inventoryplayer.currentItem, itemstack1);
            }
         */
        ItemStack result = null;
        boolean isCreative = player.capabilities.isCreativeMode;
        TileEntity te = null;

        if (target.typeOfHit == MovingObjectType.BLOCK)
        {
            IBlockState state = world.getBlockState(target.getBlockPos());

            if (state.getBlock().isAir(world, target.getBlockPos()))
            {
                return false;
            }

            if (isCreative && GuiScreen.isCtrlKeyDown())
                te = world.getTileEntity(target.getBlockPos());

            result = state.getBlock().getPickBlock(target, world, target.getBlockPos(), player);
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

        if (te != null)
        {
            NBTTagCompound nbt = new NBTTagCompound();
            te.writeToNBT(nbt);
            result.setTagInfo("BlockEntityTag", nbt);

            NBTTagCompound display = new NBTTagCompound();
            result.setTagInfo("display", display);

            NBTTagList lore = new NBTTagList();
            display.setTag("Lore", lore);
            lore.appendTag(new NBTTagString("(+NBT)"));
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
        return !MinecraftForge.EVENT_BUS.post(new LivingAttackEvent(entity, src, amount));
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

    public static boolean onLivingDrops(EntityLivingBase entity, DamageSource source, ArrayList<EntityItem> drops, int lootingLevel, boolean recentlyHit)
    {
        return MinecraftForge.EVENT_BUS.post(new LivingDropsEvent(entity, source, drops, lootingLevel, recentlyHit));
    }

    public static float[] onLivingFall(EntityLivingBase entity, float distance, float damageMultiplier)
    {
        LivingFallEvent event = new LivingFallEvent(entity, distance, damageMultiplier);
        return (MinecraftForge.EVENT_BUS.post(event) ? null : new float[]{event.distance, event.damageMultiplier});
    }

    public static boolean isLivingOnLadder(Block block, World world, BlockPos pos, EntityLivingBase entity)
    {
        boolean isSpectator = (entity instanceof EntityPlayer && ((EntityPlayer)entity).isSpectator());
        if (isSpectator) return false;
        if (!ForgeModContainer.fullBoundingBoxLadders)
        {
            return block != null && block.isLadder(world, pos, entity);
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
                        if (world.getBlockState(tmp).getBlock().isLadder(world, tmp, entity))
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

        player.joinEntityItemWithWorld(event.entityItem);
        return event.entityItem;
    }

    public static float getEnchantPower(World world, BlockPos pos)
    {
        return world.getBlockState(pos).getBlock().getEnchantPowerBonus(world, pos);
    }

    @SuppressWarnings("deprecation")
    public static ChatComponentTranslation onServerChatEvent(NetHandlerPlayServer net, String raw, ChatComponentTranslation comp)
    {
        ServerChatEvent event = new ServerChatEvent(net.playerEntity, raw, comp);
        if (MinecraftForge.EVENT_BUS.post(event))
        {
            return null;
        }
        return event.component;
    }


    static final Pattern URL_PATTERN = Pattern.compile(
            //         schema                          ipv4            OR           namespace                 port     path         ends
            //   |-----------------|        |-------------------------|  |----------------------------|    |---------| |--|   |---------------|
            "((?:[a-z0-9]{2,}:\\/\\/)?(?:(?:[0-9]{1,3}\\.){3}[0-9]{1,3}|(?:[-\\w_\\.]{1,}\\.[a-z]{2,}?))(?::[0-9]{1,5})?.*?(?=[!\"\u00A7 \n]|$))",
            Pattern.CASE_INSENSITIVE);

    public static IChatComponent newChatWithLinks(String string){ return newChatWithLinks(string, true); }
    public static IChatComponent newChatWithLinks(String string, boolean allowMissingHeader)
    {
        // Includes ipv4 and domain pattern
        // Matches an ip (xx.xxx.xx.xxx) or a domain (something.com) with or
        // without a protocol or path.
        IChatComponent ichat = null;
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
                    ichat = new ChatComponentText(part);
                else
                    ichat.appendText(part);
            }
            lastEnd = end;
            String url = string.substring(start, end);
            IChatComponent link = new ChatComponentText(url);

            try
            {
                // Add schema so client doesn't crash.
                if ((new URI(url)).getScheme() == null)
                {
                    if (!allowMissingHeader)
                    {
                        if (ichat == null)
                            ichat = new ChatComponentText(url);
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
                if (ichat == null) ichat = new ChatComponentText(url);
                else ichat.appendText(url);
                continue;
            }

            // Set the click event and append the link.
            ClickEvent click = new ClickEvent(ClickEvent.Action.OPEN_URL, url);
            link.getChatStyle().setChatClickEvent(click);
            link.getChatStyle().setUnderlined(true);
            link.getChatStyle().setColor(EnumChatFormatting.BLUE);
            if (ichat == null)
                ichat = link;
            else
                ichat.appendSibling(link);
        }

        // Append the rest of the message.
        String end = string.substring(lastEnd);
        if (ichat == null)
            ichat = new ChatComponentText(end);
        else if (end.length() > 0)
            ichat.appendText(string.substring(lastEnd));
        return ichat;
    }

    public static boolean canInteractWith(EntityPlayer player, Container openContainer)
    {
        PlayerOpenContainerEvent event = new PlayerOpenContainerEvent(player, openContainer);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult() == Event.Result.DEFAULT ? event.canInteractWith : event.getResult() == Event.Result.ALLOW ? true : false;
    }

    public static int onBlockBreakEvent(World world, GameType gameType, EntityPlayerMP entityPlayer, BlockPos pos)
    {
        // Logic from tryHarvestBlock for pre-canceling the event
        boolean preCancelEvent = false;
        if (gameType.isCreative() && entityPlayer.getHeldItem() != null && entityPlayer.getHeldItem().getItem() instanceof ItemSword)
            preCancelEvent = true;

        if (gameType.isAdventure())
        {
            if (gameType == WorldSettings.GameType.SPECTATOR)
                preCancelEvent = true;

            if (!entityPlayer.isAllowEdit())
            {
                ItemStack itemstack = entityPlayer.getCurrentEquippedItem();
                if (itemstack == null || !itemstack.canDestroy(world.getBlockState(pos).getBlock()))
                    preCancelEvent = true;
            }
        }

        // Tell client the block is gone immediately then process events
        if (world.getTileEntity(pos) == null)
        {
            S23PacketBlockChange packet = new S23PacketBlockChange(world, pos);
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
            entityPlayer.playerNetServerHandler.sendPacket(new S23PacketBlockChange(world, pos));

            // Update any tile entity data for this block
            TileEntity tileentity = world.getTileEntity(pos);
            if (tileentity != null)
            {
                Packet<?> pkt = tileentity.getDescriptionPacket();
                if (pkt != null)
                {
                    entityPlayer.playerNetServerHandler.sendPacket(pkt);
                }
            }
        }
        return event.isCanceled() ? -1 : event.getExpToDrop();
    }

    public static boolean onPlaceItemIntoWorld(ItemStack itemstack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
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

        boolean flag = itemstack.getItem().onItemUse(itemstack, player, world, pos, side, hitX, hitY, hitZ);
        world.captureBlockSnapshots = false;

        if (flag)
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
                flag = false; // cancel placement
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
                    int updateFlag = snap.flag;
                    IBlockState oldBlock = snap.replacedBlock;
                    IBlockState newBlock = world.getBlockState(snap.pos);
                    if (newBlock != null && !(newBlock.getBlock().hasTileEntity(newBlock))) // Containers get placed automatically
                    {
                        newBlock.getBlock().onBlockAdded(world, snap.pos, newBlock);
                    }

                    world.markAndNotifyBlock(snap.pos, null, oldBlock, newBlock, updateFlag);
                }
                player.addStat(StatList.objectUseStats[Item.getIdFromItem(itemstack.getItem())], 1);
            }
        }
        world.capturedBlockSnapshots.clear();

        return flag;
    }

    public static boolean onAnvilChange(ContainerRepair container, ItemStack left, ItemStack right, IInventory outputSlot, String name, int baseCost)
    {
        AnvilUpdateEvent e = new AnvilUpdateEvent(left, right, name, baseCost);
        if (MinecraftForge.EVENT_BUS.post(e)) return false;
        if (e.output == null) return true;

        outputSlot.setInventorySlotContents(0, e.output);
        container.maximumCost = e.cost;
        container.materialCost = e.materialCost;
        return false;
    }

    public static float onAnvilRepair(EntityPlayer player, ItemStack output, ItemStack left, ItemStack right)
    {
        AnvilRepairEvent e = new AnvilRepairEvent(player, left, right, output);
        MinecraftForge.EVENT_BUS.post(e);
        return e.breakChance;
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
                MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(craftingPlayer.get(), stack));
                return null;
            }
            return stack;
        }
        return null;
    }

    public static WorldGeneratorBonusChest getBonusChest(Random rand)
    {
        return new WorldGeneratorBonusChest(ChestGenHooks.getItems(ChestGenHooks.BONUS_CHEST, rand), ChestGenHooks.getCount(ChestGenHooks.BONUS_CHEST, rand));
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
            return eyes > (double)(pos.getY() + 1 + (1 - filled));
        }
        else
        {
            return eyes < (double)(pos.getY() + 1 + filled);
        }
    }

    public static boolean onPlayerAttackTarget(EntityPlayer player, Entity target)
    {
        if (MinecraftForge.EVENT_BUS.post(new AttackEntityEvent(player, target))) return false;
        ItemStack stack = player.getCurrentEquippedItem();
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

    public static MovingObjectPosition rayTraceEyes(EntityLivingBase entity, double length)
    {
        Vec3 startPos = new Vec3(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
        Vec3 endPos = startPos.add(new Vec3(entity.getLookVec().xCoord * length, entity.getLookVec().yCoord * length, entity.getLookVec().zCoord * length));
        return entity.worldObj.rayTraceBlocks(startPos, endPos);
    }

    public static Vec3 rayTraceEyeHitVec(EntityLivingBase entity, double length)
    {
        MovingObjectPosition movingObjectPosition = rayTraceEyes(entity, length);
        return movingObjectPosition == null ? null : movingObjectPosition.hitVec;
    }
}