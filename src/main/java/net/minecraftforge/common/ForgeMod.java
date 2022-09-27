/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.item.Items;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.crafting.PartialNBTIngredient;
import net.minecraftforge.common.crafting.DifferenceIngredient;
import net.minecraftforge.common.crafting.IntersectionIngredient;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeBiomeTagsProvider;
import net.minecraftforge.common.data.ForgeFluidTagsProvider;
import net.minecraftforge.common.extensions.IForgeEntity;
import net.minecraftforge.common.extensions.IForgePlayer;
import net.minecraftforge.common.loot.CanToolPerformAction;
import net.minecraftforge.common.loot.LootTableIdCondition;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers.AddFeaturesBiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers.AddSpawnsBiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers.RemoveFeaturesBiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers.RemoveSpawnsBiomeModifier;
import net.minecraftforge.common.world.NoneBiomeModifier;
import net.minecraftforge.common.world.NoneStructureModifier;
import net.minecraftforge.common.world.StructureModifier;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.registries.*;
import net.minecraftforge.registries.holdersets.AndHolderSet;
import net.minecraftforge.registries.holdersets.AnyHolderSet;
import net.minecraftforge.registries.holdersets.HolderSetType;
import net.minecraftforge.registries.holdersets.NotHolderSet;
import net.minecraftforge.registries.holdersets.OrHolderSet;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.network.filters.VanillaPacketSplitter;
import net.minecraftforge.server.command.EnumArgument;
import net.minecraftforge.server.command.ModIdArgument;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import net.minecraftforge.server.permission.nodes.PermissionTypes;
import net.minecraftforge.versions.forge.ForgeVersion;
import net.minecraftforge.versions.mcp.MCPVersion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import net.minecraftforge.common.crafting.VanillaIngredientSerializer;
import net.minecraftforge.common.crafting.conditions.AndCondition;
import net.minecraftforge.common.crafting.conditions.FalseCondition;
import net.minecraftforge.common.crafting.conditions.ItemExistsCondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.OrCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;
import net.minecraftforge.common.crafting.conditions.TrueCondition;
import net.minecraftforge.common.data.ForgeBlockTagsProvider;
import net.minecraftforge.common.data.ForgeEntityTypeTagsProvider;
import net.minecraftforge.common.data.ForgeItemTagsProvider;
import net.minecraftforge.common.data.ForgeLootTableProvider;
import net.minecraftforge.common.data.ForgeRecipeProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

@Mod("forge")
public class ForgeMod
{
    public static final String VERSION_CHECK_CAT = "version_checking";
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker FORGEMOD = MarkerManager.getMarker("FORGEMOD");

    private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.Keys.ATTRIBUTES, "forge");
    private static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister.create(Registry.COMMAND_ARGUMENT_TYPE_REGISTRY, "forge");
    private static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, "forge");
    private static final DeferredRegister<Codec<? extends StructureModifier>> STRUCTURE_MODIFIER_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.STRUCTURE_MODIFIER_SERIALIZERS, "forge");
    private static final DeferredRegister<HolderSetType> HOLDER_SET_TYPES = DeferredRegister.create(ForgeRegistries.Keys.HOLDER_SET_TYPES, "forge");

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static final RegistryObject<EnumArgument.Info> ENUM_COMMAND_ARGUMENT_TYPE = COMMAND_ARGUMENT_TYPES.register("enum", () ->
            ArgumentTypeInfos.registerByClass(EnumArgument.class, new EnumArgument.Info()));
    private static final RegistryObject<SingletonArgumentInfo<ModIdArgument>> MODID_COMMAND_ARGUMENT_TYPE = COMMAND_ARGUMENT_TYPES.register("modid", () ->
            ArgumentTypeInfos.registerByClass(ModIdArgument.class,
                    SingletonArgumentInfo.contextFree(ModIdArgument::modIdArgument)));

    public static final RegistryObject<Attribute> SWIM_SPEED = ATTRIBUTES.register("swim_speed", () -> new RangedAttribute("forge.swimSpeed", 1.0D, 0.0D, 1024.0D).setSyncable(true));
    public static final RegistryObject<Attribute> NAMETAG_DISTANCE = ATTRIBUTES.register("nametag_distance", () -> new RangedAttribute("forge.nameTagDistance", 64.0D, 0.0D, 64.0).setSyncable(true));
    public static final RegistryObject<Attribute> ENTITY_GRAVITY = ATTRIBUTES.register("entity_gravity", () -> new RangedAttribute("forge.entity_gravity", 0.08D, -8.0D, 8.0D).setSyncable(true));

    /**
     * Reach Distance represents the distance at which a player may interact with the world.  The default is 4.5 blocks.  Players in creative mode have an additional 0.5 blocks of reach distance.
     * @see IForgePlayer#getReachDistance()
     * @see IForgePlayer#canInteractWith(BlockPos, double)
     * @see IForgePlayer#canInteractWith(Entity, double)
     */
    public static final RegistryObject<Attribute> REACH_DISTANCE = ATTRIBUTES.register("reach_distance", () -> new RangedAttribute("generic.reachDistance", 4.5D, 0.0D, 1024.0D).setSyncable(true));

    /**
     * Attack Range represents the distance at which a player may attack an entity.  The default is 3 blocks.  Players in creative mode have an additional 3 blocks of attack reach.
     * @see IForgePlayer#getAttackRange()
     * @see IForgePlayer#canHit(Entity, double)
     */
    public static final RegistryObject<Attribute> ATTACK_RANGE = ATTRIBUTES.register("attack_range", () -> new RangedAttribute("generic.attack_range", 3.0D, 0.0D, 1024.0D).setSyncable(true));

    /**
     * Step Height Addition modifies the amount of blocks an entity may walk up without jumping.
     * @see IForgeEntity#getStepHeight()
     */
    public static final RegistryObject<Attribute> STEP_HEIGHT_ADDITION = ATTRIBUTES.register("step_height_addition", () -> new RangedAttribute("forge.stepHeight", 0.0D, -512.0D, 512.0D).setSyncable(true));

    /**
     * Noop biome modifier. Can be used in a biome modifier json with "type": "forge:none".
     */
    public static final RegistryObject<Codec<NoneBiomeModifier>> NONE_BIOME_MODIFIER_TYPE = BIOME_MODIFIER_SERIALIZERS.register("none", () -> Codec.unit(NoneBiomeModifier.INSTANCE));

    /**
     * Stock biome modifier for adding features to biomes.
     */
    public static final RegistryObject<Codec<AddFeaturesBiomeModifier>> ADD_FEATURES_BIOME_MODIFIER_TYPE = BIOME_MODIFIER_SERIALIZERS.register("add_features", () ->
        RecordCodecBuilder.create(builder -> builder.group(
                Biome.LIST_CODEC.fieldOf("biomes").forGetter(AddFeaturesBiomeModifier::biomes),
                PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(AddFeaturesBiomeModifier::features),
                Decoration.CODEC.fieldOf("step").forGetter(AddFeaturesBiomeModifier::step)
            ).apply(builder, AddFeaturesBiomeModifier::new))
        );

    /**
     * Stock biome modifier for removing features from biomes.
     */
    public static final RegistryObject<Codec<RemoveFeaturesBiomeModifier>> REMOVE_FEATURES_BIOME_MODIFIER_TYPE = BIOME_MODIFIER_SERIALIZERS.register("remove_features", () ->
        RecordCodecBuilder.create(builder -> builder.group(
                Biome.LIST_CODEC.fieldOf("biomes").forGetter(RemoveFeaturesBiomeModifier::biomes),
                PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(RemoveFeaturesBiomeModifier::features),
                new ExtraCodecs.EitherCodec<List<Decoration>, Decoration>(Decoration.CODEC.listOf(), Decoration.CODEC).<Set<Decoration>>xmap(
                        either -> either.map(Set::copyOf, Set::of), // convert list/singleton to set when decoding
                        set -> set.size() == 1 ? Either.right(set.toArray(Decoration[]::new)[0]) : Either.left(List.copyOf(set))
                    ).optionalFieldOf("steps", EnumSet.allOf(Decoration.class)).forGetter(RemoveFeaturesBiomeModifier::steps)
            ).apply(builder, RemoveFeaturesBiomeModifier::new))
        );

    /**
     * Stock biome modifier for adding mob spawns to biomes.
     */
    public static final RegistryObject<Codec<AddSpawnsBiomeModifier>> ADD_SPAWNS_BIOME_MODIFIER_TYPE = BIOME_MODIFIER_SERIALIZERS.register("add_spawns", () ->
        RecordCodecBuilder.create(builder -> builder.group(
                Biome.LIST_CODEC.fieldOf("biomes").forGetter(AddSpawnsBiomeModifier::biomes),
                // Allow either a list or single spawner, attempting to decode the list format first.
                // Uses the better EitherCodec that logs both errors if both formats fail to parse.
                new ExtraCodecs.EitherCodec<>(SpawnerData.CODEC.listOf(), SpawnerData.CODEC).xmap(
                        either -> either.map(Function.identity(), List::of), // convert list/singleton to list when decoding
                        list -> list.size() == 1 ? Either.right(list.get(0)) : Either.left(list) // convert list to singleton/list when encoding
                    ).fieldOf("spawners").forGetter(AddSpawnsBiomeModifier::spawners)
            ).apply(builder, AddSpawnsBiomeModifier::new))
        );

    /**
     * Stock biome modifier for removing mob spawns from biomes.
     */
    public static final RegistryObject<Codec<RemoveSpawnsBiomeModifier>> REMOVE_SPAWNS_BIOME_MODIFIER_TYPE = BIOME_MODIFIER_SERIALIZERS.register("remove_spawns", () ->
        RecordCodecBuilder.create(builder -> builder.group(
                Biome.LIST_CODEC.fieldOf("biomes").forGetter(RemoveSpawnsBiomeModifier::biomes),
                RegistryCodecs.homogeneousList(ForgeRegistries.Keys.ENTITY_TYPES).fieldOf("entity_types").forGetter(RemoveSpawnsBiomeModifier::entityTypes)
            ).apply(builder, RemoveSpawnsBiomeModifier::new))
        );
    /**
     * Noop structure modifier. Can be used in a structure modifier json with "type": "forge:none".
     */
    public static final RegistryObject<Codec<NoneStructureModifier>> NONE_STRUCTURE_MODIFIER_TYPE = STRUCTURE_MODIFIER_SERIALIZERS.register("none", () -> Codec.unit(NoneStructureModifier.INSTANCE));

    /**
     * Stock holder set type that represents any/all values in a registry. Can be used in a holderset object with {@code { "type": "forge:any" }}
     */
    public static final RegistryObject<HolderSetType> ANY_HOLDER_SET = HOLDER_SET_TYPES.register("any", () -> AnyHolderSet::codec);

    /**
     * Stock holder set type that represents an intersection of other holdersets. Can be used in a holderset object with {@code { "type": "forge:and", "values": [list of holdersets] }}
     */
    public static final RegistryObject<HolderSetType> AND_HOLDER_SET = HOLDER_SET_TYPES.register("and", () -> AndHolderSet::codec);

    /**
     * Stock holder set type that represents a union of other holdersets. Can be used in a holderset object with {@code { "type": "forge:or", "values": [list of holdersets] }}
     */
    public static final RegistryObject<HolderSetType> OR_HOLDER_SET = HOLDER_SET_TYPES.register("or", () -> OrHolderSet::codec);

    /**
     * <p>Stock holder set type that represents all values in a registry except those in another given set.
     * Can be used in a holderset object with {@code { "type": "forge:not", "value": holderset }}</p>
     */
    public static final RegistryObject<HolderSetType> NOT_HOLDER_SET = HOLDER_SET_TYPES.register("not", () -> NotHolderSet::codec);

    private static final DeferredRegister<FluidType> VANILLA_FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, "minecraft");

    public static final RegistryObject<FluidType> EMPTY_TYPE = VANILLA_FLUID_TYPES.register("empty", () ->
            new FluidType(FluidType.Properties.create()
                    .descriptionId("block.minecraft.air")
                    .motionScale(1D)
                    .canPushEntity(false)
                    .canSwim(false)
                    .canDrown(false)
                    .fallDistanceModifier(1F)
                    .pathType(null)
                    .adjacentPathType(null)
                    .density(0)
                    .temperature(0)
                    .viscosity(0))
            {
                @Override
                public void setItemMovement(ItemEntity entity)
                {
                    if (!entity.isNoGravity()) entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
                }
            });
    public static final RegistryObject<FluidType> WATER_TYPE = VANILLA_FLUID_TYPES.register("water", () ->
            new FluidType(FluidType.Properties.create()
                    .descriptionId("block.minecraft.water")
                    .fallDistanceModifier(0F)
                    .canExtinguish(true)
                    .canConvertToSource(true)
                    .supportsBoating(true)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                    .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
                    .canHydrate(true))
            {
                @Override
                public @Nullable BlockPathTypes getBlockPathType(FluidState state, BlockGetter level, BlockPos pos, @Nullable Mob mob, boolean canFluidLog)
                {
                    return canFluidLog ? super.getBlockPathType(state, level, pos, mob, true) : null;
                }

                @Override
                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer)
                {
                    consumer.accept(new IClientFluidTypeExtensions()
                    {
                        private static final ResourceLocation UNDERWATER_LOCATION = new ResourceLocation("textures/misc/underwater.png"),
                                WATER_STILL = new ResourceLocation("block/water_still"),
                                WATER_FLOW = new ResourceLocation("block/water_flow"),
                                WATER_OVERLAY = new ResourceLocation("block/water_overlay");

                        @Override
                        public ResourceLocation getStillTexture()
                        {
                            return WATER_STILL;
                        }

                        @Override
                        public ResourceLocation getFlowingTexture()
                        {
                            return WATER_FLOW;
                        }

                        @Nullable
                        @Override
                        public ResourceLocation getOverlayTexture()
                        {
                            return WATER_OVERLAY;
                        }

                        @Override
                        public ResourceLocation getRenderOverlayTexture(Minecraft mc)
                        {
                            return UNDERWATER_LOCATION;
                        }

                        @Override
                        public int getTintColor()
                        {
                            return 0xFF3F76E4;
                        }

                        @Override
                        public int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos)
                        {
                            return BiomeColors.getAverageWaterColor(getter, pos) | 0xFF000000;
                        }
                    });
                }
            });
    public static final RegistryObject<FluidType> LAVA_TYPE = VANILLA_FLUID_TYPES.register("lava", () ->
            new FluidType(FluidType.Properties.create()
                    .descriptionId("block.minecraft.lava")
                    .canSwim(false)
                    .canDrown(false)
                    .pathType(BlockPathTypes.LAVA)
                    .adjacentPathType(null)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                    .lightLevel(15)
                    .density(3000)
                    .viscosity(6000)
                    .temperature(1300))
            {
                @Override
                public double motionScale(Entity entity)
                {
                    return entity.level.dimensionType().ultraWarm() ? 0.007D : 0.0023333333333333335D;
                }

                @Override
                public void setItemMovement(ItemEntity entity)
                {
                    Vec3 vec3 = entity.getDeltaMovement();
                    entity.setDeltaMovement(vec3.x * (double)0.95F, vec3.y + (double)(vec3.y < (double)0.06F ? 5.0E-4F : 0.0F), vec3.z * (double)0.95F);
                }

                @Override
                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer)
                {
                    consumer.accept(new IClientFluidTypeExtensions()
                    {
                        private static final ResourceLocation LAVA_STILL = new ResourceLocation("block/lava_still"),
                                LAVA_FLOW = new ResourceLocation("block/lava_flow");

                        @Override
                        public ResourceLocation getStillTexture()
                        {
                            return LAVA_STILL;
                        }

                        @Override
                        public ResourceLocation getFlowingTexture()
                        {
                            return LAVA_FLOW;
                        }
                    });
                }
            });

    private static boolean enableMilkFluid = false;
    private static boolean serverChatPreviewEnabled = false;
    public static final RegistryObject<FluidType> MILK_TYPE = RegistryObject.createOptional(new ResourceLocation("milk"), ForgeRegistries.Keys.FLUID_TYPES.location(), "minecraft");
    public static final RegistryObject<Fluid> MILK = RegistryObject.create(new ResourceLocation("milk"), ForgeRegistries.FLUIDS);
    public static final RegistryObject<Fluid> FLOWING_MILK = RegistryObject.create(new ResourceLocation("flowing_milk"), ForgeRegistries.FLUIDS);

    private static ForgeMod INSTANCE;
    public static ForgeMod getInstance()
    {
        return INSTANCE;
    }

    /**
     * Run this method during mod constructor to enable milk and add it to the Minecraft milk bucket
     */
    public static void enableMilkFluid()
    {
        enableMilkFluid = true;
    }

    /**
     * Run this method during mod constructor to enable chat previews for the server.
     * Recommended for mods which modify the chat message in {@link ServerChatEvent}.
     *
     * @see ServerChatEvent
     */
    public static void enableServerChatPreview()
    {
        serverChatPreviewEnabled = true;
    }

    /**
     * @return {@code true} if server chat previews are enabled
     */
    public static boolean isServerChatPreviewEnabled()
    {
        return serverChatPreviewEnabled;
    }

    public ForgeMod()
    {
        LOGGER.info(FORGEMOD,"Forge mod loading, version {}, for MC {} with MCP {}", ForgeVersion.getVersion(), MCPVersion.getMCVersion(), MCPVersion.getMCPVersion());
        INSTANCE = this;
        MinecraftForge.initialize();
        CrashReportCallables.registerCrashCallable("Crash Report UUID", ()-> {
            final UUID uuid = UUID.randomUUID();
            LOGGER.fatal("Preparing crash report with UUID {}", uuid);
            return uuid.toString();
        });

        LOGGER.debug(FORGEMOD, "Loading Network data for FML net version: {}", NetworkConstants.init());
        CrashReportCallables.registerCrashCallable("FML", ForgeVersion::getSpec);
        CrashReportCallables.registerCrashCallable("Forge", ()->ForgeVersion.getGroup()+":"+ForgeVersion.getVersion());

        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::preInit);
        modEventBus.addListener(this::gatherData);
        modEventBus.addListener(this::loadComplete);
        modEventBus.addListener(this::registerFluids);
        modEventBus.addListener(this::registerRecipeSerializers);
        modEventBus.addListener(this::registerLootData);
        modEventBus.register(this);
        ATTRIBUTES.register(modEventBus);
        COMMAND_ARGUMENT_TYPES.register(modEventBus);
        BIOME_MODIFIER_SERIALIZERS.register(modEventBus);
        STRUCTURE_MODIFIER_SERIALIZERS.register(modEventBus);
        HOLDER_SET_TYPES.register(modEventBus);
        VANILLA_FLUID_TYPES.register(modEventBus);
        MinecraftForge.EVENT_BUS.addListener(this::serverStopping);
        MinecraftForge.EVENT_BUS.addListener(this::missingSoundMapping);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ForgeConfig.clientSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ForgeConfig.serverSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ForgeConfig.commonSpec);
        modEventBus.register(ForgeConfig.class);
        ForgeDeferredRegistriesSetup.setup(modEventBus);
        // Forge does not display problems when the remote is not matching.
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, ()->new IExtensionPoint.DisplayTest(()->"ANY", (remote, isServer)-> true));
        StartupMessageManager.addModMessage("Forge version "+ForgeVersion.getVersion());

        MinecraftForge.EVENT_BUS.addListener(VillagerTradingManager::loadTrades);
        MinecraftForge.EVENT_BUS.register(MinecraftForge.INTERNAL_HANDLER);
        MinecraftForge.EVENT_BUS.addListener(this::mappingChanged);
        MinecraftForge.EVENT_BUS.addListener(this::registerPermissionNodes);

        ForgeRegistries.ITEMS.tags().addOptionalTagDefaults(Tags.Items.ENCHANTING_FUELS, Set.of(ForgeRegistries.ITEMS.getDelegateOrThrow(Items.LAPIS_LAZULI)));
    }

    public void preInit(FMLCommonSetupEvent evt)
    {
        VersionChecker.startVersionCheck();
        VanillaPacketSplitter.register();
    }

    public void loadComplete(FMLLoadCompleteEvent event)
    {
    }

    public void serverStopping(ServerStoppingEvent evt)
    {
        WorldWorkerManager.clear();
    }

    public void mappingChanged(IdMappingEvent evt)
    {
        Ingredient.invalidateAll();
    }

    public void gatherData(GatherDataEvent event)
    {
        DataGenerator gen = event.getGenerator();

        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        ForgeBlockTagsProvider blockTags = new ForgeBlockTagsProvider(gen, existingFileHelper);
        gen.addProvider(event.includeServer(), blockTags);
        gen.addProvider(event.includeServer(), new ForgeItemTagsProvider(gen, blockTags, existingFileHelper));
        gen.addProvider(event.includeServer(), new ForgeEntityTypeTagsProvider(gen, existingFileHelper));
        gen.addProvider(event.includeServer(), new ForgeFluidTagsProvider(gen, existingFileHelper));
        gen.addProvider(event.includeServer(), new ForgeRecipeProvider(gen));
        gen.addProvider(event.includeServer(), new ForgeLootTableProvider(gen));
        gen.addProvider(event.includeServer(), new ForgeBiomeTagsProvider(gen, existingFileHelper));
    }

    public void missingSoundMapping(MissingMappingsEvent event)
    {
        if (event.getKey() != ForgeRegistries.Keys.SOUND_EVENTS)
            return;

        //Removed in 1.15, see https://minecraft.gamepedia.com/Parrot#History
        List<String> removedSounds = Arrays.asList("entity.parrot.imitate.panda", "entity.parrot.imitate.zombie_pigman", "entity.parrot.imitate.enderman", "entity.parrot.imitate.polar_bear", "entity.parrot.imitate.wolf");
        for (MissingMappingsEvent.Mapping<SoundEvent> mapping : event.getAllMappings(ForgeRegistries.Keys.SOUND_EVENTS))
        {
            ResourceLocation regName = mapping.getKey();
            if (regName != null && regName.getNamespace().equals("minecraft"))
            {
                String path = regName.getPath();
                if (removedSounds.stream().anyMatch(s -> s.equals(path)))
                {
                    LOGGER.info("Ignoring removed minecraft sound {}", regName);
                    mapping.ignore();
                }
            }
        }
    }

    // done in an event instead of deferred to only enable if a mod requests it
    public void registerFluids(RegisterEvent event)
    {
        if (enableMilkFluid)
        {
            // register fluid type
            event.register(ForgeRegistries.Keys.FLUID_TYPES, helper -> helper.register(MILK_TYPE.getId(), new FluidType(FluidType.Properties.create().density(1024).viscosity(1024))
            {
                @Override
                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer)
                {
                    consumer.accept(new IClientFluidTypeExtensions()
                    {
                        private static final ResourceLocation MILK_STILL = new ResourceLocation("forge", "block/milk_still"),
                                MILK_FLOW = new ResourceLocation("forge", "block/milk_flowing");

                        @Override
                        public ResourceLocation getStillTexture()
                        {
                            return MILK_STILL;
                        }

                        @Override
                        public ResourceLocation getFlowingTexture()
                        {
                            return MILK_FLOW;
                        }
                    });
                }
            }));

            // register fluids
            event.register(ForgeRegistries.Keys.FLUIDS, helper -> {
                // set up properties
                ForgeFlowingFluid.Properties properties = new ForgeFlowingFluid.Properties(MILK_TYPE, MILK, FLOWING_MILK).bucket(() -> Items.MILK_BUCKET);

                helper.register(MILK.getId(), new ForgeFlowingFluid.Source(properties));
                helper.register(FLOWING_MILK.getId(), new ForgeFlowingFluid.Flowing(properties));
            });
        }
    }

    public void registerRecipeSerializers(RegisterEvent event)
    {
        if (event.getRegistryKey().equals(ForgeRegistries.Keys.RECIPE_SERIALIZERS))
        {
            CraftingHelper.register(AndCondition.Serializer.INSTANCE);
            CraftingHelper.register(FalseCondition.Serializer.INSTANCE);
            CraftingHelper.register(ItemExistsCondition.Serializer.INSTANCE);
            CraftingHelper.register(ModLoadedCondition.Serializer.INSTANCE);
            CraftingHelper.register(NotCondition.Serializer.INSTANCE);
            CraftingHelper.register(OrCondition.Serializer.INSTANCE);
            CraftingHelper.register(TrueCondition.Serializer.INSTANCE);
            CraftingHelper.register(TagEmptyCondition.Serializer.INSTANCE);

            CraftingHelper.register(new ResourceLocation("forge", "compound"), CompoundIngredient.Serializer.INSTANCE);
            CraftingHelper.register(new ResourceLocation("forge", "nbt"), StrictNBTIngredient.Serializer.INSTANCE);
            CraftingHelper.register(new ResourceLocation("forge", "partial_nbt"), PartialNBTIngredient.Serializer.INSTANCE);
            CraftingHelper.register(new ResourceLocation("forge", "difference"), DifferenceIngredient.Serializer.INSTANCE);
            CraftingHelper.register(new ResourceLocation("forge", "intersection"), IntersectionIngredient.Serializer.INSTANCE);
            CraftingHelper.register(new ResourceLocation("minecraft", "item"), VanillaIngredientSerializer.INSTANCE);

            event.register(ForgeRegistries.Keys.RECIPE_SERIALIZERS, new ResourceLocation("forge", "conditional"), ConditionalRecipe.Serializer::new);
        }
    }

    public void registerLootData(RegisterEvent event)
    {
        if (!event.getRegistryKey().equals(Registry.LOOT_ITEM_REGISTRY))
            return;

        event.register(Registry.LOOT_ITEM_REGISTRY, new ResourceLocation("forge:loot_table_id"), () -> LootTableIdCondition.LOOT_TABLE_ID);
        event.register(Registry.LOOT_ITEM_REGISTRY, new ResourceLocation("forge:can_tool_perform_action"), () -> CanToolPerformAction.LOOT_CONDITION_TYPE);
    }

    public static final PermissionNode<Boolean> USE_SELECTORS_PERMISSION = new PermissionNode<>("forge", "use_entity_selectors",
            PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));

    public void registerPermissionNodes(PermissionGatherEvent.Nodes event)
    {
        event.addNodes(USE_SELECTORS_PERMISSION);
    }
}
