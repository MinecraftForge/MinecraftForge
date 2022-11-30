/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Strings;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.ResourceLocationException;
import net.minecraft.advancements.Advancement;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.*;
import net.minecraft.network.chat.*;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.util.datafix.fixes.StructuresBecomeConfiguredFix;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.util.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.Container;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.stats.Stats;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifierManager;
import net.minecraftforge.common.loot.LootTableIdCondition;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.MavenVersionStringHelper;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.DifficultyChangeEvent;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.GrindstoneEvent;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.ItemStackedOnOtherEvent;
import net.minecraftforge.event.ModMismatchEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.RegisterStructureConversionsEvent;
import net.minecraftforge.event.VanillaGameEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.EnderManAngerEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent.ILivingTargetType;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.LivingUseTotemEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.living.LivingGetProjectileEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.NoteBlockEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.resource.ResourcePackLoader;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.RegistryManager;
import net.minecraftforge.server.permission.PermissionAPI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import net.minecraft.ChatFormatting;
import net.minecraft.resources.RegistryResourceAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.RegistryResourceAccess.EntryThunk;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.material.Fluid;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ForgeHooks
{
    private static final Logger LOGGER = LogManager.getLogger();
    @SuppressWarnings("unused")
    private static final Marker FORGEHOOKS = MarkerManager.getMarker("FORGEHOOKS");
    private static final Marker WORLDPERSISTENCE = MarkerManager.getMarker("WP");

    public static boolean canContinueUsing(@NotNull ItemStack from, @NotNull ItemStack to)
    {
        if (!from.isEmpty() && !to.isEmpty())
        {
            return from.getItem().canContinueUsing(from, to);
        }
        return false;
    }

    public static boolean isCorrectToolForDrops(@NotNull BlockState state, @NotNull Player player)
    {
        if (!state.requiresCorrectToolForDrops())
            return ForgeEventFactory.doPlayerHarvestCheck(player, state, true);

        return player.hasCorrectToolForDrops(state);
    }

    /**
     * Called when a player uses 'pick block', calls new Entity and Block hooks.
     */
    @SuppressWarnings("resource")
    @Deprecated(forRemoval = true, since = "1.19")
    public static boolean onPickBlock(HitResult target, Player player, Level level)
    {
        ItemStack result = ItemStack.EMPTY;
        boolean isCreative = player.getAbilities().instabuild;
        BlockEntity te = null;

        if (target.getType() == HitResult.Type.BLOCK)
        {
            BlockPos pos = ((BlockHitResult)target).getBlockPos();
            BlockState state = level.getBlockState(pos);

            if (state.isAir())
                return false;

            if (isCreative && Screen.hasControlDown() && state.hasBlockEntity())
                te = level.getBlockEntity(pos);

            result = state.getCloneItemStack(target, level, pos, player);

            if (result.isEmpty())
                LOGGER.warn("Picking on: [{}] {} gave null item", target.getType(), ForgeRegistries.BLOCKS.getKey(state.getBlock()));
        }
        else if (target.getType() == HitResult.Type.ENTITY)
        {
            Entity entity = ((EntityHitResult)target).getEntity();
            result = entity.getPickedResult(target);

            if (result.isEmpty())
                LOGGER.warn("Picking on: [{}] {} gave null item", target.getType(), ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()));
        }

        if (result.isEmpty())
            return false;

        if (te != null)
            Minecraft.getInstance().addCustomNbtData(result, te);

        if (isCreative)
        {
            player.getInventory().setPickedItem(result);
            Minecraft.getInstance().gameMode.handleCreativeModeItemAdd(player.getItemInHand(InteractionHand.MAIN_HAND), 36 + player.getInventory().selected);
            return true;
        }
        int slot = player.getInventory().findSlotMatchingItem(result);
        if (slot != -1)
        {
            if (Inventory.isHotbarSlot(slot))
                player.getInventory().selected = slot;
            else
                Minecraft.getInstance().gameMode.handlePickItem(slot);
            return true;
        }
        return false;
    }

    public static boolean onItemStackedOn(ItemStack carriedItem, ItemStack stackedOnItem, Slot slot, ClickAction action, Player player, SlotAccess carriedSlotAccess)
    {
        return MinecraftForge.EVENT_BUS.post(new ItemStackedOnOtherEvent(carriedItem, stackedOnItem, slot, action, player, carriedSlotAccess));
    }

    public static void onDifficultyChange(Difficulty difficulty, Difficulty oldDifficulty)
    {
        MinecraftForge.EVENT_BUS.post(new DifficultyChangeEvent(difficulty, oldDifficulty));
    }

    //Optifine Helper Functions u.u, these are here specifically for Optifine
    //Note: When using Optifine, these methods are invoked using reflection, which
    //incurs a major performance penalty.
    // TODO: Remove in 1.20
    @Deprecated(since = "1.19.2", forRemoval = true)
    public static void onLivingSetAttackTarget(LivingEntity entity, LivingEntity target)
    {
        MinecraftForge.EVENT_BUS.post(new LivingSetAttackTargetEvent(entity, target));
    }

    // TODO: Remove in 1.20
    @Deprecated(since = "1.19.2", forRemoval = true)
    public static void onLivingSetAttackTarget(LivingEntity entity, LivingEntity target, ILivingTargetType targetType)
    {
        MinecraftForge.EVENT_BUS.post(new LivingSetAttackTargetEvent(entity, target, targetType));
    }
    
    public static LivingChangeTargetEvent onLivingChangeTarget(LivingEntity entity, LivingEntity originalTarget, ILivingTargetType targetType)
    {
        LivingChangeTargetEvent event = new LivingChangeTargetEvent(entity, originalTarget, targetType);
        MinecraftForge.EVENT_BUS.post(event);
        
        return event;
    }

    public static boolean onLivingTick(LivingEntity entity)
    {
        return MinecraftForge.EVENT_BUS.post(new LivingTickEvent(entity));
    }

    public static boolean onLivingAttack(LivingEntity entity, DamageSource src, float amount)
    {
        return entity instanceof Player || !MinecraftForge.EVENT_BUS.post(new LivingAttackEvent(entity, src, amount));
    }

    public static boolean onPlayerAttack(LivingEntity entity, DamageSource src, float amount)
    {
        return !MinecraftForge.EVENT_BUS.post(new LivingAttackEvent(entity, src, amount));
    }

    public static LivingKnockBackEvent onLivingKnockBack(LivingEntity target, float strength, double ratioX, double ratioZ)
    {
        LivingKnockBackEvent event = new LivingKnockBackEvent(target, strength, ratioX, ratioZ);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static boolean onLivingUseTotem(LivingEntity entity, DamageSource damageSource, ItemStack totem, InteractionHand hand)
    {
        return !MinecraftForge.EVENT_BUS.post(new LivingUseTotemEvent(entity, damageSource, totem, hand));
    }

    public static float onLivingHurt(LivingEntity entity, DamageSource src, float amount)
    {
        LivingHurtEvent event = new LivingHurtEvent(entity, src, amount);
        return (MinecraftForge.EVENT_BUS.post(event) ? 0 : event.getAmount());
    }

    public static float onLivingDamage(LivingEntity entity, DamageSource src, float amount)
    {
        LivingDamageEvent event = new LivingDamageEvent(entity, src, amount);
        return (MinecraftForge.EVENT_BUS.post(event) ? 0 : event.getAmount());
    }

    public static boolean onLivingDeath(LivingEntity entity, DamageSource src)
    {
        return MinecraftForge.EVENT_BUS.post(new LivingDeathEvent(entity, src));
    }

    public static boolean onLivingDrops(LivingEntity entity, DamageSource source, Collection<ItemEntity> drops, int lootingLevel, boolean recentlyHit)
    {
        return MinecraftForge.EVENT_BUS.post(new LivingDropsEvent(entity, source, drops, lootingLevel, recentlyHit));
    }

    @Nullable
    public static float[] onLivingFall(LivingEntity entity, float distance, float damageMultiplier)
    {
        LivingFallEvent event = new LivingFallEvent(entity, distance, damageMultiplier);
        return (MinecraftForge.EVENT_BUS.post(event) ? null : new float[]{event.getDistance(), event.getDamageMultiplier()});
    }

    public static int getLootingLevel(Entity target, @Nullable Entity killer, @Nullable DamageSource cause)
    {
        int looting = 0;
        if (killer instanceof LivingEntity)
            looting = EnchantmentHelper.getMobLooting((LivingEntity)killer);
        if (target instanceof LivingEntity)
            looting = getLootingLevel((LivingEntity)target, cause, looting);
        return looting;
    }

    public static int getLootingLevel(LivingEntity target, @Nullable DamageSource cause, int level)
    {
        LootingLevelEvent event = new LootingLevelEvent(target, cause, level);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getLootingLevel();
    }

    public static double getEntityVisibilityMultiplier(LivingEntity entity, Entity lookingEntity, double originalMultiplier){
        LivingEvent.LivingVisibilityEvent event = new LivingEvent.LivingVisibilityEvent(entity, lookingEntity, originalMultiplier);
        MinecraftForge.EVENT_BUS.post(event);
        return Math.max(0,event.getVisibilityModifier());
    }

    public static Optional<BlockPos> isLivingOnLadder(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull LivingEntity entity)
    {
        boolean isSpectator = (entity instanceof Player && entity.isSpectator());
        if (isSpectator) return Optional.empty();
        if (!ForgeConfig.SERVER.fullBoundingBoxLadders.get())
        {
            return state.isLadder(level, pos, entity) ? Optional.of(pos) : Optional.empty();
        }
        else
        {
            AABB bb = entity.getBoundingBox();
            int mX = Mth.floor(bb.minX);
            int mY = Mth.floor(bb.minY);
            int mZ = Mth.floor(bb.minZ);
            for (int y2 = mY; y2 < bb.maxY; y2++)
            {
                for (int x2 = mX; x2 < bb.maxX; x2++)
                {
                    for (int z2 = mZ; z2 < bb.maxZ; z2++)
                    {
                        BlockPos tmp = new BlockPos(x2, y2, z2);
                        state = level.getBlockState(tmp);
                        if (state.isLadder(level, tmp, entity))
                        {
                            return Optional.of(tmp);
                        }
                    }
                }
            }
            return Optional.empty();
        }
    }

    public static void onLivingJump(LivingEntity entity)
    {
        MinecraftForge.EVENT_BUS.post(new LivingJumpEvent(entity));
    }

    @Nullable
    public static ItemEntity onPlayerTossEvent(@NotNull Player player, @NotNull ItemStack item, boolean includeName)
    {
        player.captureDrops(Lists.newArrayList());
        ItemEntity ret = player.drop(item, false, includeName);
        player.captureDrops(null);

        if (ret == null)
            return null;

        ItemTossEvent event = new ItemTossEvent(ret, player);
        if (MinecraftForge.EVENT_BUS.post(event))
            return null;

        if (!player.level.isClientSide)
            player.getCommandSenderWorld().addFreshEntity(event.getEntity());
        return event.getEntity();
    }

    public static boolean onVanillaGameEvent(Level level, GameEvent vanillaEvent, Vec3 pos, GameEvent.Context context)
    {
        return !MinecraftForge.EVENT_BUS.post(new VanillaGameEvent(level, vanillaEvent, pos, context));
    }

    private static String getRawText(Component message)
    {
        return message.getContents() instanceof LiteralContents literalContents ? literalContents.text() : "";
    }

    @Nullable
    public static Component onServerChatSubmittedEvent(ServerPlayer player, String plain, Component decorated, boolean canChangeMessage)
    {
        ServerChatEvent.Submitted event = new ServerChatEvent.Submitted(player, plain, decorated, canChangeMessage);
        return MinecraftForge.EVENT_BUS.post(event) ? null : event.getMessage();
    }

    private static final ChatDecorator SERVER_CHAT_SUBMITTED_DECORATOR = new ChatDecorator()
    {
        @NotNull
        @Override
        public CompletableFuture<Component> decorate(@Nullable ServerPlayer sender, Component message)
        {
            return CompletableFuture.supplyAsync(() -> {
                if (sender == null)
                    return message; // Vanilla should never get here with the patches we use, but let's be safe with dumb mods

                return onServerChatSubmittedEvent(sender, getRawText(message), message, true);
            });
        }

        @NotNull
        @Override
        public CompletableFuture<PlayerChatMessage> decorate(@Nullable ServerPlayer sender, PlayerChatMessage message)
        {
            if (message.signedContent().isDecorated())
            {
                return CompletableFuture.supplyAsync(() -> {
                    if (sender != null)
                        return onServerChatSubmittedEvent(sender, message.signedContent().plain(), message.signedContent().decorated(), false) == null;

                    return false;
                }).thenApply(canceled -> canceled == Boolean.TRUE ? null : message);
            }

            return this.decorate(sender, message.serverContent()).thenApply(component -> component == null ? null : message.withUnsignedContent(component));
        }
    };

    @NotNull
    public static ChatDecorator getServerChatSubmittedDecorator()
    {
        return SERVER_CHAT_SUBMITTED_DECORATOR;
    }

    @Nullable
    public static Component onServerChatPreviewEvent(@NotNull ServerPlayer player, @NotNull Component message)
    {
        ServerChatEvent.Preview event = new ServerChatEvent.Preview(player, getRawText(message), message);
        return MinecraftForge.EVENT_BUS.post(event) ? null : event.getMessage();
    }

    @NotNull
    public static ChatDecorator getServerChatPreviewDecorator()
    {
        return (player, message) -> CompletableFuture.supplyAsync(() -> {
            if (player == null)
                return message; // Vanilla should never get here with the patches we use, but let's be safe with dumb mods

            Component preview = onServerChatPreviewEvent(player, message);
            // Send the input message back to the client if the event was cancelled
            return preview == null ? message : preview;
        });
    }


    static final Pattern URL_PATTERN = Pattern.compile(
            //         schema                          ipv4            OR        namespace                 port     path         ends
            //   |-----------------|        |-------------------------|  |-------------------------|    |---------| |--|   |---------------|
            "((?:[a-z0-9]{2,}:\\/\\/)?(?:(?:[0-9]{1,3}\\.){3}[0-9]{1,3}|(?:[-\\w_]{1,}\\.[a-z]{2,}?))(?::[0-9]{1,5})?.*?(?=[!\"\u00A7 \n]|$))",
            Pattern.CASE_INSENSITIVE);

    public static Component newChatWithLinks(String string){ return newChatWithLinks(string, true); }
    public static Component newChatWithLinks(String string, boolean allowMissingHeader)
    {
        // Includes ipv4 and domain pattern
        // Matches an ip (xx.xxx.xx.xxx) or a domain (something.com) with or
        // without a protocol or path.
        MutableComponent ichat = null;
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
                    ichat = Component.literal(part);
                else
                    ichat.append(part);
            }
            lastEnd = end;
            String url = string.substring(start, end);
            MutableComponent link = Component.literal(url);

            try
            {
                // Add schema so client doesn't crash.
                if ((new URI(url)).getScheme() == null)
                {
                    if (!allowMissingHeader)
                    {
                        if (ichat == null)
                            ichat = Component.literal(url);
                        else
                            ichat.append(url);
                        continue;
                    }
                    url = "http://" + url;
                }
            }
            catch (URISyntaxException e)
            {
                // Bad syntax bail out!
                if (ichat == null) ichat = Component.literal(url);
                else ichat.append(url);
                continue;
            }

            // Set the click event and append the link.
            ClickEvent click = new ClickEvent(ClickEvent.Action.OPEN_URL, url);
            link.setStyle(link.getStyle().withClickEvent(click).withUnderlined(true).withColor(TextColor.fromLegacyFormat(ChatFormatting.BLUE)));
            if (ichat == null)
                ichat = Component.literal("");
            ichat.append(link);
        }

        // Append the rest of the message.
        String end = string.substring(lastEnd);
        if (ichat == null)
            ichat = Component.literal(end);
        else if (end.length() > 0)
            ichat.append(Component.literal(string.substring(lastEnd)));
        return ichat;
    }

    public static int onBlockBreakEvent(Level level, GameType gameType, ServerPlayer entityPlayer, BlockPos pos)
    {
        // Logic from tryHarvestBlock for pre-canceling the event
        boolean preCancelEvent = false;
        ItemStack itemstack = entityPlayer.getMainHandItem();
        if (!itemstack.isEmpty() && !itemstack.getItem().canAttackBlock(level.getBlockState(pos), level, pos, entityPlayer))
        {
            preCancelEvent = true;
        }

        if (gameType.isBlockPlacingRestricted())
        {
            if (gameType == GameType.SPECTATOR)
                preCancelEvent = true;

            if (!entityPlayer.mayBuild())
            {
                if (itemstack.isEmpty() || !itemstack.hasAdventureModeBreakTagForBlock(level.registryAccess().registryOrThrow(Registry.BLOCK_REGISTRY), new BlockInWorld(level, pos, false)))
                    preCancelEvent = true;
            }
        }

        // Tell client the block is gone immediately then process events
        if (level.getBlockEntity(pos) == null)
        {
            entityPlayer.connection.send(new ClientboundBlockUpdatePacket(pos, level.getFluidState(pos).createLegacyBlock()));
        }

        // Post the block break event
        BlockState state = level.getBlockState(pos);
        BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(level, pos, state, entityPlayer);
        event.setCanceled(preCancelEvent);
        MinecraftForge.EVENT_BUS.post(event);

        // Handle if the event is canceled
        if (event.isCanceled())
        {
            // Let the client know the block still exists
            entityPlayer.connection.send(new ClientboundBlockUpdatePacket(level, pos));

            // Update any tile entity data for this block
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity != null)
            {
                Packet<?> pkt = blockEntity.getUpdatePacket();
                if (pkt != null)
                {
                    entityPlayer.connection.send(pkt);
                }
            }
        }
        return event.isCanceled() ? -1 : event.getExpToDrop();
    }

    public static InteractionResult onPlaceItemIntoWorld(@NotNull UseOnContext context)
    {
        ItemStack itemstack = context.getItemInHand();
        Level level = context.getLevel();

        Player player = context.getPlayer();
        if (player != null && !player.getAbilities().mayBuild && !itemstack.hasAdventureModePlaceTagForBlock(level.registryAccess().registryOrThrow(Registry.BLOCK_REGISTRY), new BlockInWorld(level, context.getClickedPos(), false)))
            return InteractionResult.PASS;

        // handle all placement events here
        Item item = itemstack.getItem();
        int size = itemstack.getCount();
        CompoundTag nbt = null;
        if (itemstack.getTag() != null)
            nbt = itemstack.getTag().copy();

        if (!(itemstack.getItem() instanceof BucketItem)) // if not bucket
            level.captureBlockSnapshots = true;

        ItemStack copy = itemstack.copy();
        InteractionResult ret = itemstack.getItem().useOn(context);
        if (itemstack.isEmpty())
            ForgeEventFactory.onPlayerDestroyItem(player, copy, context.getHand());

        level.captureBlockSnapshots = false;

        if (ret.consumesAction())
        {
            // save new item data
            int newSize = itemstack.getCount();
            CompoundTag newNBT = null;
            if (itemstack.getTag() != null)
            {
                newNBT = itemstack.getTag().copy();
            }
            @SuppressWarnings("unchecked")
            List<BlockSnapshot> blockSnapshots = (List<BlockSnapshot>)level.capturedBlockSnapshots.clone();
            level.capturedBlockSnapshots.clear();

            // make sure to set pre-placement item data for event
            itemstack.setCount(size);
            itemstack.setTag(nbt);

            Direction side = context.getClickedFace();

            boolean eventResult = false;
            if (blockSnapshots.size() > 1)
            {
                eventResult = ForgeEventFactory.onMultiBlockPlace(player, blockSnapshots, side);
            }
            else if (blockSnapshots.size() == 1)
            {
                eventResult = ForgeEventFactory.onBlockPlace(player, blockSnapshots.get(0), side);
            }

            if (eventResult)
            {
                ret = InteractionResult.FAIL; // cancel placement
                // revert back all captured blocks
                for (BlockSnapshot blocksnapshot : Lists.reverse(blockSnapshots))
                {
                    level.restoringBlockSnapshots = true;
                    blocksnapshot.restore(true, false);
                    level.restoringBlockSnapshots = false;
                }
            }
            else
            {
                // Change the stack to its new content
                itemstack.setCount(newSize);
                itemstack.setTag(newNBT);

                for (BlockSnapshot snap : blockSnapshots)
                {
                    int updateFlag = snap.getFlag();
                    BlockState oldBlock = snap.getReplacedBlock();
                    BlockState newBlock = level.getBlockState(snap.getPos());
                    newBlock.onPlace(level, snap.getPos(), oldBlock, false);

                    level.markAndNotifyBlock(snap.getPos(), level.getChunkAt(snap.getPos()), oldBlock, newBlock, updateFlag, 512);
                }
                if (player != null)
                    player.awardStat(Stats.ITEM_USED.get(item));
            }
        }
        level.capturedBlockSnapshots.clear();

        return ret;
    }

    public static boolean onAnvilChange(AnvilMenu container, @NotNull ItemStack left, @NotNull ItemStack right, Container outputSlot, String name, int baseCost, Player player)
    {
        AnvilUpdateEvent e = new AnvilUpdateEvent(left, right, name, baseCost, player);
        if (MinecraftForge.EVENT_BUS.post(e)) return false;
        if (e.getOutput().isEmpty()) return true;

        outputSlot.setItem(0, e.getOutput());
        container.setMaximumCost(e.getCost());
        container.repairItemCountCost = e.getMaterialCost();
        return false;
    }

    public static float onAnvilRepair(Player player, @NotNull ItemStack output, @NotNull ItemStack left, @NotNull ItemStack right)
    {
        AnvilRepairEvent e = new AnvilRepairEvent(player, left, right, output);
        MinecraftForge.EVENT_BUS.post(e);
        return e.getBreakChance();
    }

    public static int onGrindstoneChange(@NotNull ItemStack top, @NotNull ItemStack bottom, Container outputSlot, int xp)
    {
        GrindstoneEvent.OnplaceItem e = new GrindstoneEvent.OnplaceItem(top, bottom, xp);
        if (MinecraftForge.EVENT_BUS.post(e))
        {
            outputSlot.setItem(0, ItemStack.EMPTY);
            return -1;
        }
        if (e.getOutput().isEmpty()) return Integer.MIN_VALUE;

        outputSlot.setItem(0, e.getOutput());
        return e.getXp();
    }

    public static boolean onGrindstoneTake(Container inputSlots, ContainerLevelAccess access, Function<Level, Integer> xpFunction)
    {
        access.execute((l,p) -> {
            int xp = xpFunction.apply(l);
            GrindstoneEvent.OnTakeItem e = new GrindstoneEvent.OnTakeItem(inputSlots.getItem(0), inputSlots.getItem(1), xp);
            if (MinecraftForge.EVENT_BUS.post(e))
            {
                return;
            }
            if (l instanceof ServerLevel)
            {
                ExperienceOrb.award((ServerLevel)l, Vec3.atCenterOf(p), e.getXp());
            }
            l.levelEvent(1042, p, 0);
            inputSlots.setItem(0, e.getNewTopItem());
            inputSlots.setItem(1, e.getNewBottomItem());
            inputSlots.setChanged();
        });
        return true;
    }

    private static ThreadLocal<Player> craftingPlayer = new ThreadLocal<Player>();
    public static void setCraftingPlayer(Player player)
    {
        craftingPlayer.set(player);
    }
    public static Player getCraftingPlayer()
    {
        return craftingPlayer.get();
    }
    @NotNull
    public static ItemStack getCraftingRemainingItem(@NotNull ItemStack stack)
    {
        if (stack.getItem().hasCraftingRemainingItem(stack))
        {
            stack = stack.getItem().getCraftingRemainingItem(stack);
            if (!stack.isEmpty() && stack.isDamageableItem() && stack.getDamageValue() > stack.getMaxDamage())
            {
                ForgeEventFactory.onPlayerDestroyItem(craftingPlayer.get(), stack, null);
                return ItemStack.EMPTY;
            }
            return stack;
        }
        return ItemStack.EMPTY;
    }

    public static boolean onPlayerAttackTarget(Player player, Entity target)
    {
        if (MinecraftForge.EVENT_BUS.post(new AttackEntityEvent(player, target))) return false;
        ItemStack stack = player.getMainHandItem();
        return stack.isEmpty() || !stack.getItem().onLeftClickEntity(stack, player, target);
    }

    public static boolean onTravelToDimension(Entity entity, ResourceKey<Level> dimension)
    {
        EntityTravelToDimensionEvent event = new EntityTravelToDimensionEvent(entity, dimension);
        MinecraftForge.EVENT_BUS.post(event);
        return !event.isCanceled();
    }

    public static InteractionResult onInteractEntityAt(Player player, Entity entity, HitResult ray, InteractionHand hand)
    {
        Vec3 vec3d = ray.getLocation().subtract(entity.position());
        return onInteractEntityAt(player, entity, vec3d, hand);
    }

    public static InteractionResult onInteractEntityAt(Player player, Entity entity, Vec3 vec3d, InteractionHand hand)
    {
        PlayerInteractEvent.EntityInteractSpecific evt = new PlayerInteractEvent.EntityInteractSpecific(player, hand, entity, vec3d);
        MinecraftForge.EVENT_BUS.post(evt);
        return evt.isCanceled() ? evt.getCancellationResult() : null;
    }

    public static InteractionResult onInteractEntity(Player player, Entity entity, InteractionHand hand)
    {
        PlayerInteractEvent.EntityInteract evt = new PlayerInteractEvent.EntityInteract(player, hand, entity);
        MinecraftForge.EVENT_BUS.post(evt);
        return evt.isCanceled() ? evt.getCancellationResult() : null;
    }

    public static InteractionResult onItemRightClick(Player player, InteractionHand hand)
    {
        PlayerInteractEvent.RightClickItem evt = new PlayerInteractEvent.RightClickItem(player, hand);
        MinecraftForge.EVENT_BUS.post(evt);
        return evt.isCanceled() ? evt.getCancellationResult() : null;
    }

    public static PlayerInteractEvent.LeftClickBlock onLeftClickBlock(Player player, BlockPos pos, Direction face)
    {
        PlayerInteractEvent.LeftClickBlock evt = new PlayerInteractEvent.LeftClickBlock(player, pos, face);
        MinecraftForge.EVENT_BUS.post(evt);
        return evt;
    }

    public static PlayerInteractEvent.RightClickBlock onRightClickBlock(Player player, InteractionHand hand, BlockPos pos, BlockHitResult hitVec)
    {
        PlayerInteractEvent.RightClickBlock evt = new PlayerInteractEvent.RightClickBlock(player, hand, pos, hitVec);
        MinecraftForge.EVENT_BUS.post(evt);
        return evt;
    }

    public static void onEmptyClick(Player player, InteractionHand hand)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerInteractEvent.RightClickEmpty(player, hand));
    }

    public static void onEmptyLeftClick(Player player)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerInteractEvent.LeftClickEmpty(player));
    }

    /**
     * @return null if game type should not be changed, desired new GameType otherwise
     */
    @Nullable
    public static GameType onChangeGameType(Player player, GameType currentGameType, GameType newGameType)
    {
        if (currentGameType != newGameType)
        {
            PlayerEvent.PlayerChangeGameModeEvent evt = new PlayerEvent.PlayerChangeGameModeEvent(player, currentGameType, newGameType);
            MinecraftForge.EVENT_BUS.post(evt);
            return evt.isCanceled() ? null : evt.getNewGameMode();
        }
        return newGameType;
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
    public static LootTable loadLootTable(Gson gson, ResourceLocation name, JsonElement data, boolean custom, LootTables lootTableManager)
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
            ret.setLootTableId(name);
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

    /**
     * Returns a vanilla fluid type for the given fluid.
     *
     * @param fluid the fluid looking for its type
     * @return the type of the fluid if vanilla
     * @throws RuntimeException if the fluid is not a vanilla one
     */
    public static FluidType getVanillaFluidType(Fluid fluid)
    {
        if (fluid == Fluids.EMPTY)
            return ForgeMod.EMPTY_TYPE.get();
        if (fluid == Fluids.WATER || fluid == Fluids.FLOWING_WATER)
            return ForgeMod.WATER_TYPE.get();
        if (fluid == Fluids.LAVA || fluid == Fluids.FLOWING_LAVA)
            return ForgeMod.LAVA_TYPE.get();
        if (ForgeMod.MILK.filter(milk -> milk == fluid).isPresent() || ForgeMod.FLOWING_MILK.filter(milk -> milk == fluid).isPresent())
            return ForgeMod.MILK_TYPE.get();
        throw new RuntimeException("Mod fluids must override getFluidType.");
    }

    public static TagKey<Block> getTagFromVanillaTier(Tiers tier)
    {
        return switch(tier)
                {
                    case WOOD -> Tags.Blocks.NEEDS_WOOD_TOOL;
                    case GOLD -> Tags.Blocks.NEEDS_GOLD_TOOL;
                    case STONE -> BlockTags.NEEDS_STONE_TOOL;
                    case IRON -> BlockTags.NEEDS_IRON_TOOL;
                    case DIAMOND -> BlockTags.NEEDS_DIAMOND_TOOL;
                    case NETHERITE -> Tags.Blocks.NEEDS_NETHERITE_TOOL;
                };
    }

    @FunctionalInterface
    public interface BiomeCallbackFunction
    {
        Biome apply(final Biome.ClimateSettings climate, final BiomeSpecialEffects effects, final BiomeGenerationSettings gen, final MobSpawnSettings spawns);
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
            this.vanilla = "minecraft".equals(this.name.getNamespace());
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
            return GsonHelper.getAsString(json, "name");

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
            return ctx.validateEntryName(GsonHelper.getAsString(json, "entryName"));

        if (ctx.custom)
            return "custom#" + json.hashCode(); //We don't care about custom ones modders shouldn't be editing them!

        String name = null;
        if ("item".equals(type))
            name = GsonHelper.getAsString(json, "name");
        else if ("loot_table".equals(type))
            name = GsonHelper.getAsString(json, "name");
        else if ("empty".equals(type))
            name = "empty";

        return ctx.validateEntryName(name);
    }

    public static boolean onCropsGrowPre(Level level, BlockPos pos, BlockState state, boolean def)
    {
        BlockEvent ev = new BlockEvent.CropGrowEvent.Pre(level,pos,state);
        MinecraftForge.EVENT_BUS.post(ev);
        return (ev.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW || (ev.getResult() == net.minecraftforge.eventbus.api.Event.Result.DEFAULT && def));
    }

    public static void onCropsGrowPost(Level level, BlockPos pos, BlockState state)
    {
        MinecraftForge.EVENT_BUS.post(new BlockEvent.CropGrowEvent.Post(level, pos, state, level.getBlockState(pos)));
    }

    @Nullable
    public static CriticalHitEvent getCriticalHit(Player player, Entity target, boolean vanillaCritical, float damageModifier)
    {
        CriticalHitEvent hitResult = new CriticalHitEvent(player, target, damageModifier, vanillaCritical);
        MinecraftForge.EVENT_BUS.post(hitResult);
        if (hitResult.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW || (vanillaCritical && hitResult.getResult() == net.minecraftforge.eventbus.api.Event.Result.DEFAULT))
        {
            return hitResult;
        }
        return null;
    }

    /**
     * @deprecated See {@link ForgeEventFactory#onAdvancementEarnedEvent} and {@link ForgeEventFactory#onAdvancementProgressedEvent}
     */
    @Deprecated(forRemoval = true, since = "1.19.2")
    public static void onAdvancement(ServerPlayer player, Advancement advancement)
    {
        MinecraftForge.EVENT_BUS.post(new AdvancementEvent(player, advancement));
    }

    /**
     * Hook to fire {@link ItemAttributeModifierEvent}. Modders should use {@link ItemStack#getAttributeModifiers(EquipmentSlot)} instead.
     */
    public static Multimap<Attribute,AttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot equipmentSlot, Multimap<Attribute,AttributeModifier> attributes)
    {
        ItemAttributeModifierEvent event = new ItemAttributeModifierEvent(stack, equipmentSlot, attributes);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getModifiers();
    }

    /**
     * Hook to fire {@link LivingGetProjectileEvent}. Returns the ammo to be used.
     */
    public static ItemStack getProjectile(LivingEntity entity, ItemStack projectileWeaponItem, ItemStack projectile)
    {
        LivingGetProjectileEvent event = new LivingGetProjectileEvent(entity, projectileWeaponItem, projectile);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getProjectileItemStack();
    }

    /**
     * Used as the default implementation of {@link Item#getCreatorModId}. Call that method instead.
     */
    @Nullable
    public static String getDefaultCreatorModId(@NotNull ItemStack itemStack)
    {
        Item item = itemStack.getItem();
        ResourceLocation registryName = ForgeRegistries.ITEMS.getKey(item);
        String modId = registryName == null ? null : registryName.getNamespace();
        if ("minecraft".equals(modId))
        {
            if (item instanceof EnchantedBookItem)
            {
                ListTag enchantmentsNbt = EnchantedBookItem.getEnchantments(itemStack);
                if (enchantmentsNbt.size() == 1)
                {
                    CompoundTag nbttagcompound = enchantmentsNbt.getCompound(0);
                    ResourceLocation resourceLocation = ResourceLocation.tryParse(nbttagcompound.getString("id"));
                    if (resourceLocation != null && ForgeRegistries.ENCHANTMENTS.containsKey(resourceLocation))
                    {
                        return resourceLocation.getNamespace();
                    }
                }
            }
            else if (item instanceof PotionItem || item instanceof TippedArrowItem)
            {
                Potion potionType = PotionUtils.getPotion(itemStack);
                ResourceLocation resourceLocation = ForgeRegistries.POTIONS.getKey(potionType);
                if (resourceLocation != null)
                {
                    return resourceLocation.getNamespace();
                }
            }
            else if (item instanceof SpawnEggItem)
            {
                ResourceLocation resourceLocation = ForgeRegistries.ENTITY_TYPES.getKey(((SpawnEggItem) item).getType(null));
                if (resourceLocation != null)
                {
                    return resourceLocation.getNamespace();
                }
            }
        }
        return modId;
    }

    public static boolean onFarmlandTrample(Level level, BlockPos pos, BlockState state, float fallDistance, Entity entity)
    {
        if (entity.canTrample(state, pos, fallDistance))
        {
            BlockEvent.FarmlandTrampleEvent event = new BlockEvent.FarmlandTrampleEvent(level, pos, state, fallDistance, entity);
            MinecraftForge.EVENT_BUS.post(event);
            return !event.isCanceled();
        }
        return false;
    }

    public static int onNoteChange(Level level, BlockPos pos, BlockState state, int old, int _new) {
        NoteBlockEvent.Change event = new NoteBlockEvent.Change(level, pos, state, old, _new);
        if (MinecraftForge.EVENT_BUS.post(event))
            return -1;
        return event.getVanillaNoteId();
    }

    public static int canEntitySpawn(Mob entity, LevelAccessor world, double x, double y, double z, BaseSpawner spawner, MobSpawnType spawnReason) {
        Result res = ForgeEventFactory.canEntitySpawn(entity, world, x, y, z, null, spawnReason);
        return res == Result.DEFAULT ? 0 : res == Result.DENY ? -1 : 1;
    }

    public static boolean hasNoElements(Ingredient ingredient)
    {
        ItemStack[] items = ingredient.getItems();
        if (items.length == 0) return true;
        if (items.length == 1)
        {
            //If we potentially added a barrier due to the ingredient being an empty tag, try and check if it is the stack we added
            ItemStack item = items[0];
            return item.getItem() == Items.BARRIER && item.getHoverName() instanceof MutableComponent hoverName && hoverName.getString().startsWith("Empty Tag: ");
        }
        return false;
    }

    public static <T> void deserializeTagAdditions(List<TagEntry> list, JsonObject json, List<TagEntry> allList)
    {
        if (json.has("remove"))
        {
            for (JsonElement entry : GsonHelper.getAsJsonArray(json, "remove"))
            {
                String s = GsonHelper.convertToString(entry, "value");
                TagEntry dummy;
                if (!s.startsWith("#"))
                    dummy = TagEntry.optionalElement(new ResourceLocation(s));
                else
                    dummy = TagEntry.tag(new ResourceLocation(s.substring(1)));
                allList.removeIf(e -> e.equals(dummy));
            }
        }
    }

    @Nullable
    public static EntityDataSerializer<?> getSerializer(int id, CrudeIncrementalIntIdentityHashBiMap<EntityDataSerializer<?>> vanilla)
    {
        EntityDataSerializer<?> serializer = vanilla.byId(id);
        if (serializer == null)
        {
            // ForgeRegistries.DATA_SERIALIZERS is a deferred register now, so if this method is called too early, the registry will be null
            ForgeRegistry<EntityDataSerializer<?>> registry = (ForgeRegistry<EntityDataSerializer<?>>) ForgeRegistries.ENTITY_DATA_SERIALIZERS.get();
            if (registry != null)
                serializer = registry.getValue(id);
        }
        return serializer;
    }

    public static int getSerializerId(EntityDataSerializer<?> serializer, CrudeIncrementalIntIdentityHashBiMap<EntityDataSerializer<?>> vanilla)
    {
        int id = vanilla.getId(serializer);
        if (id < 0)
        {
            // ForgeRegistries.DATA_SERIALIZERS is a deferred register now, so if this method is called too early, the registry will be null
            ForgeRegistry<EntityDataSerializer<?>> registry = (ForgeRegistry<EntityDataSerializer<?>>) ForgeRegistries.ENTITY_DATA_SERIALIZERS.get();
            if (registry != null)
                id = registry.getID(serializer);
        }
        return id;
    }

    public static boolean canEntityDestroy(Level level, BlockPos pos, LivingEntity entity)
    {
        if (!level.isLoaded(pos))
            return false;
        BlockState state = level.getBlockState(pos);
        return ForgeEventFactory.getMobGriefingEvent(level, entity) && state.canEntityDestroy(level, pos, entity) && ForgeEventFactory.onEntityDestroyBlock(entity, pos, state);
    }

    private static final Map<Holder.Reference<Item>, Integer> VANILLA_BURNS = new HashMap<>();

    /**
     * Gets the burn time of this itemstack.
     */
    public static int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType)
    {
        if (stack.isEmpty())
        {
            return 0;
        }
        else
        {
            Item item = stack.getItem();
            int ret = stack.getBurnTime(recipeType);
            return ForgeEventFactory.getItemBurnTime(stack, ret == -1 ? VANILLA_BURNS.getOrDefault(ForgeRegistries.ITEMS.getDelegateOrThrow(item), 0) : ret, recipeType);
        }
    }

    @SuppressWarnings("deprecation")
    public static synchronized void updateBurns()
    {
        VANILLA_BURNS.clear();
        FurnaceBlockEntity.getFuel().entrySet().forEach(e -> VANILLA_BURNS.put(ForgeRegistries.ITEMS.getDelegateOrThrow(e.getKey()), e.getValue()));
    }

    /**
     * All loot table drops should be passed to this function so that mod added effects
     * (e.g. smelting enchantments) can be processed.
     *
     * @param list The loot generated
     * @param context The loot context that generated that loot
     * @return The modified list
     *
     * @deprecated Use {@link #modifyLoot(ResourceLocation, ObjectArrayList, LootContext)} instead.
     *
     * @implNote This method will use the {@linkplain LootTableIdCondition#UNKNOWN_LOOT_TABLE
     *           unknown loot table marker} when redirecting.
     */
    @Deprecated
    public static List<ItemStack> modifyLoot(List<ItemStack> list, LootContext context) {
        return modifyLoot(LootTableIdCondition.UNKNOWN_LOOT_TABLE, ObjectArrayList.wrap((ItemStack[]) list.toArray()), context);
    }

    /**
     * Handles the modification of loot table drops via the registered Global Loot Modifiers,
     * so that custom effects can be processed.
     *
     * <p>All loot-table generated loot should be passed to this function.</p>
     *
     * @param lootTableId The ID of the loot table currently being queried
     * @param generatedLoot The loot generated by the loot table
     * @param context The loot context that generated the loot, unmodified
     * @return The modified list of drops
     *
     * @apiNote The given context will be modified by this method to also store the ID of the
     *          loot table being queried.
     */
    public static ObjectArrayList<ItemStack> modifyLoot(ResourceLocation lootTableId, ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        context.setQueriedLootTableId(lootTableId); // In case the ID was set via copy constructor, this will be ignored: intended
        LootModifierManager man = ForgeInternalHandler.getLootModifierManager();
        for (IGlobalLootModifier mod : man.getAllLootMods()) {
            generatedLoot = mod.apply(generatedLoot, context);
        }
        return generatedLoot;
    }

    public static List<String> getModPacks()
    {
        List<String> modpacks = ResourcePackLoader.getPackNames();
        if(modpacks.isEmpty())
            throw new IllegalStateException("Attempted to retrieve mod packs before they were loaded in!");
        return modpacks;
    }

    public static List<String> getModPacksWithVanilla()
    {
        List<String> modpacks = getModPacks();
        modpacks.add("vanilla");
        return modpacks;
    }

    private static final Set<String> VANILLA_DIMS = Sets.newHashSet("minecraft:overworld", "minecraft:the_nether", "minecraft:the_end");
    private static final String DIMENSIONS_KEY = "dimensions";
    private static final String SEED_KEY = "seed";
    //No to static init!
    private static final Supplier<Codec<Registry<LevelStem>>> CODEC = Suppliers.memoize(() -> RegistryCodecs.dataPackAwareCodec(Registry.LEVEL_STEM_REGISTRY, Lifecycle.stable(), LevelStem.CODEC).xmap(LevelStem::sortMap, Function.identity()));

    private static final Map<EntityType<? extends LivingEntity>, AttributeSupplier> FORGE_ATTRIBUTES = new HashMap<>();
    /**  FOR INTERNAL USE ONLY, DO NOT CALL DIRECTLY */
    @Deprecated
    public static Map<EntityType<? extends LivingEntity>, AttributeSupplier> getAttributesView()
    {
        return Collections.unmodifiableMap(FORGE_ATTRIBUTES);
    }

    /**  FOR INTERNAL USE ONLY, DO NOT CALL DIRECTLY */
    @Deprecated
    public static void modifyAttributes()
    {
        ModLoader.get().postEvent(new EntityAttributeCreationEvent(FORGE_ATTRIBUTES));
        Map<EntityType<? extends LivingEntity>, AttributeSupplier.Builder> finalMap = new HashMap<>();
        ModLoader.get().postEvent(new EntityAttributeModificationEvent(finalMap));

        finalMap.forEach((k, v) ->
        {
            AttributeSupplier supplier = DefaultAttributes.getSupplier(k);
            AttributeSupplier.Builder newBuilder = supplier != null ? new AttributeSupplier.Builder(supplier) : new AttributeSupplier.Builder();
            newBuilder.combine(v);
            FORGE_ATTRIBUTES.put(k, newBuilder.build());
        });
    }

    public static void onEntityEnterSection(Entity entity, long packedOldPos, long packedNewPos)
    {
        MinecraftForge.EVENT_BUS.post(new EntityEvent.EnteringSection(entity, packedOldPos, packedNewPos));
    }

    public static ShieldBlockEvent onShieldBlock(LivingEntity blocker, DamageSource source, float blocked)
    {
        ShieldBlockEvent e = new ShieldBlockEvent(blocker, source, blocked);
        MinecraftForge.EVENT_BUS.post(e);
        return e;
    }

    public static void writeAdditionalLevelSaveData(WorldData worldData, CompoundTag levelTag)
    {
        CompoundTag fmlData = new CompoundTag();
        ListTag modList = new ListTag();
        ModList.get().getMods().forEach(mi ->
        {
            final CompoundTag mod = new CompoundTag();
            mod.putString("ModId", mi.getModId());
            mod.putString("ModVersion", MavenVersionStringHelper.artifactVersionToString(mi.getVersion()));
            modList.add(mod);
        });
        fmlData.put("LoadingModList", modList);

        CompoundTag registries = new CompoundTag();
        fmlData.put("Registries", registries);
        LOGGER.debug(WORLDPERSISTENCE, "Gathering id map for writing to world save {}", worldData.getLevelName());

        for (Map.Entry<ResourceLocation, ForgeRegistry.Snapshot> e : RegistryManager.ACTIVE.takeSnapshot(true).entrySet())
        {
            registries.put(e.getKey().toString(), e.getValue().write());
        }
        LOGGER.debug(WORLDPERSISTENCE, "ID Map collection complete {}", worldData.getLevelName());
        levelTag.put("fml", fmlData);
    }

    /**
     * @deprecated To be removed in 1.20.
     * Use {@link #readAdditionalLevelSaveData(CompoundTag, LevelStorageSource.LevelDirectory)} instead.
     */
    @Deprecated(forRemoval = true, since = "1.19.2")
    public static void readAdditionalLevelSaveData(CompoundTag rootTag) {
        readAdditionalLevelSaveData(rootTag, null);
    }

    /**
     * @param rootTag Level data file contents.
     * @param levelDirectory Level currently being loaded. TODO 1.20 - Remove nullable annotation
     */
    @ApiStatus.Internal
    public static void readAdditionalLevelSaveData(CompoundTag rootTag, @Nullable LevelStorageSource.LevelDirectory levelDirectory)
    {
        CompoundTag tag = rootTag.getCompound("fml");
        if (tag.contains("LoadingModList"))
        {
            ListTag modList = tag.getList("LoadingModList", net.minecraft.nbt.Tag.TAG_COMPOUND);
            Map<String, ArtifactVersion> mismatchedVersions = new HashMap<>(modList.size());
            Map<String, ArtifactVersion> missingVersions = new HashMap<>(modList.size());
            for (int i = 0; i < modList.size(); i++)
            {
                CompoundTag mod = modList.getCompound(i);
                String modId = mod.getString("ModId");
                if (Objects.equals("minecraft",  modId))
                {
                    continue;
                }

                String modVersion = mod.getString("ModVersion");
                final var previousVersion = new DefaultArtifactVersion(modVersion);
                ModList.get().getModContainerById(modId).ifPresentOrElse(container ->
                {
                    final var loadingVersion = container.getModInfo().getVersion();
                    if (!loadingVersion.equals(previousVersion))
                    {
                        // Enqueue mismatched versions for bulk event
                        mismatchedVersions.put(modId, previousVersion);
                    }
                }, () -> missingVersions.put(modId, previousVersion));
            }

            final var mismatchEvent = new ModMismatchEvent(levelDirectory, mismatchedVersions, missingVersions);
            ModLoader.get().postEvent(mismatchEvent);

            StringBuilder resolved = new StringBuilder("The following mods have version differences that were marked resolved:");
            StringBuilder unresolved = new StringBuilder("The following mods have version differences that were not resolved:");

            // For mods that were marked resolved, log the version resolution and the mod that resolved the mismatch
            mismatchEvent.getResolved().forEachOrdered((res) ->
            {
                final var modid = res.modid();
                final var diff = res.versionDifference();
                if (res.wasSelfResolved())
                {
                    resolved.append(System.lineSeparator())
                            .append(diff.isMissing()
                                    ? "%s (version %s -> MISSING, self-resolved)".formatted(modid, diff.oldVersion())
                                    : "%s (version %s -> %s, self-resolved)".formatted(modid, diff.oldVersion(), diff.newVersion())
                            );
                }
                else
                {
                    final var resolver = res.resolver().getModId();
                    resolved.append(System.lineSeparator())
                            .append(diff.isMissing()
                                    ? "%s (version %s -> MISSING, resolved by %s)".formatted(modid, diff.oldVersion(), resolver)
                                    : "%s (version %s -> %s, resolved by %s)".formatted(modid, diff.oldVersion(), diff.newVersion(), resolver)
                            );
                }
            });

            // For mods that did not specify handling, show a warning to users that errors may occur
            mismatchEvent.getUnresolved().forEachOrdered((unres) ->
            {
                final var modid = unres.modid();
                final var diff = unres.versionDifference();
                unresolved.append(System.lineSeparator())
                        .append(diff.isMissing()
                                ? "%s (version %s -> MISSING)".formatted(modid, diff.oldVersion())
                                : "%s (version %s -> %s)".formatted(modid, diff.oldVersion(), diff.newVersion())
                        );
            });

            if (mismatchEvent.anyResolved())
            {
                resolved.append(System.lineSeparator()).append("Things may not work well.");
                LOGGER.debug(WORLDPERSISTENCE, resolved.toString());
            }

            if (mismatchEvent.anyUnresolved())
            {
                unresolved.append(System.lineSeparator()).append("Things may not work well.");
                LOGGER.warn(WORLDPERSISTENCE, unresolved.toString());
            }
        }

        Multimap<ResourceLocation, ResourceLocation> failedElements = null;

        if (tag.contains("Registries"))
        {
            Map<ResourceLocation, ForgeRegistry.Snapshot> snapshot = new HashMap<>();
            CompoundTag regs = tag.getCompound("Registries");
            for (String key : regs.getAllKeys())
            {
                snapshot.put(new ResourceLocation(key), ForgeRegistry.Snapshot.read(regs.getCompound(key)));
            }
            failedElements = GameData.injectSnapshot(snapshot, true, true);
        }

        if (failedElements != null && !failedElements.isEmpty())
        {
            StringBuilder buf = new StringBuilder();
            buf.append("Forge Mod Loader could not load this save.\n\n")
                .append("There are ").append(failedElements.size()).append(" unassigned registry entries in this save.\n")
                .append("You will not be able to load until they are present again.\n\n");

            failedElements.asMap().forEach((name, entries) ->
            {
                buf.append("Missing ").append(name).append(":\n");
                entries.forEach(rl -> buf.append("    ").append(rl).append("\n"));
            });
            LOGGER.error(WORLDPERSISTENCE, buf.toString());
        }
    }

    public static String encodeLifecycle(Lifecycle lifecycle)
    {
        if (lifecycle == Lifecycle.stable())
            return "stable";
        if (lifecycle == Lifecycle.experimental())
            return "experimental";
        if (lifecycle instanceof Lifecycle.Deprecated dep)
            return "deprecated=" + dep.since();
        throw new IllegalArgumentException("Unknown lifecycle.");
    }

    public static Lifecycle parseLifecycle(String lifecycle)
    {
        if (lifecycle.equals("stable"))
            return Lifecycle.stable();
        if (lifecycle.equals("experimental"))
            return Lifecycle.experimental();
        if (lifecycle.startsWith("deprecated="))
            return Lifecycle.deprecated(Integer.parseInt(lifecycle.substring(lifecycle.indexOf('=') + 1)));
        throw new IllegalArgumentException("Unknown lifecycle.");
    }

    public static void saveMobEffect(CompoundTag nbt, String key, MobEffect effect)
    {
        var registryName = ForgeRegistries.MOB_EFFECTS.getKey(effect);
        if (registryName != null)
        {
            nbt.putString(key, registryName.toString());
        }
    }

    @Nullable
    public static MobEffect loadMobEffect(CompoundTag nbt, String key, @Nullable MobEffect fallback)
    {
        var registryName = nbt.getString(key);
        if (Strings.isNullOrEmpty(registryName))
        {
            return fallback;
        }
        try
        {
            return ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(registryName));
        }
        catch (ResourceLocationException e)
        {
            return fallback;
        }
    }

    public static boolean shouldSuppressEnderManAnger(EnderMan enderMan, Player player, ItemStack mask)
    {
        return mask.isEnderMask(player, enderMan) || MinecraftForge.EVENT_BUS.post(new EnderManAngerEvent(enderMan, player));
    }

    private static final Lazy<Map<String, StructuresBecomeConfiguredFix.Conversion>> FORGE_CONVERSION_MAP = Lazy.concurrentOf(() -> {
        Map<String, StructuresBecomeConfiguredFix.Conversion> map = new HashMap<>();
        MinecraftForge.EVENT_BUS.post(new RegisterStructureConversionsEvent(map));
        return ImmutableMap.copyOf(map);
    });

    // DO NOT CALL from within RegisterStructureConversionsEvent, otherwise you'll get a deadlock
    /**
     * @hidden For internal use only.
     */
    @Nullable
    public static StructuresBecomeConfiguredFix.Conversion getStructureConversion(String originalBiome)
    {
        return FORGE_CONVERSION_MAP.get().get(originalBiome);
    }

    /**
     * @hidden For internal use only.
     */
    public static boolean checkStructureNamespace(String biome)
    {
        @Nullable ResourceLocation biomeLocation = ResourceLocation.tryParse(biome);
        return biomeLocation != null && !biomeLocation.getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE);
    }

    public static Map<PackType, Integer> readTypedPackFormats(JsonObject json)
    {
        ImmutableMap.Builder<PackType, Integer> map = ImmutableMap.builder();

        for (PackType packType : PackType.values())
        {
            String key = "forge:" + packType.bridgeType.name().toLowerCase(Locale.ROOT) + "_pack_format";
            if (json.has(key))
            {
                map.put(packType, GsonHelper.getAsInt(json, key));
            }
        }

        return map.buildOrThrow();
    }

    /**
     * <p>
     *    This method is used to prefix the path, where elements of the associated registry are stored, with their namespace, if it is not minecraft
     * </p>
     * <p>
     *    This rules conflicts with equal paths out. If for example the mod {@code fancy_cheese} adds a registry named {@code cheeses},
     *    but the mod {@code awesome_cheese} also adds a registry called {@code cheeses},
     *    they are going to have the same path {@code cheeses}, just with different namespaces.
     *    If {@code additional_cheese} wants to add additional cheese to {@code awesome_cheese}, but not {@code fancy_cheese},
     *    it can not differentiate both. Both paths will look like {@code data/additional_cheese/cheeses}.
     * </p>
     * <p>
     *    The fix, which is applied here prefixes the path of the registry with the namespace,
     *    so {@code fancy_cheese}'s registry stores its elements in {@code data/<namespace>/fancy_cheese/cheeses}
     *    and {@code awesome_cheese}'s registry stores its elements in {@code data/namespace/awesome_cheese/cheeses}
     * </p>
     *
     * @param registryKey key of the registry
     * @return path of the registry key. Prefixed with the namespace if it is not "minecraft"
     */
    public static String prefixNamespace(ResourceLocation registryKey)
    {
        return registryKey.getNamespace().equals("minecraft") ? registryKey.getPath() : registryKey.getNamespace() +  "/"  + registryKey.getPath();
    }

    public static boolean canUseEntitySelectors(SharedSuggestionProvider provider)
    {
        if (provider.hasPermission(Commands.LEVEL_GAMEMASTERS))
        {
            return true;
        }
        else if (provider instanceof CommandSourceStack source && source.source instanceof ServerPlayer player)
        {
            return PermissionAPI.getPermission(player, ForgeMod.USE_SELECTORS_PERMISSION);
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static <E> Collection<Entry<ResourceKey<E>, EntryThunk<E>>> filterThunks(Map<ResourceKey<E>, RegistryResourceAccess.EntryThunk<E>> map)
    {
        return map.entrySet().stream().filter(e -> 
        ((EntryThunk<Boolean>)e.getValue()).parseElement(JsonOps.INSTANCE, ICondition.DECODER)
        .get().left().get().value()).toList(); // Validity of this .get() call is enforced by the above Decoder object, which only returns DataResult.success.
    }
}