/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.common;

import net.minecraft.DetectedVersion;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.item.Items;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import net.minecraftsingularity.api.distmarker.Dist;
import net.minecraftsingularity.client.singularityAtlasProvider;
import net.minecraftsingularity.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftsingularity.common.data.ExistingFileHelper;
import net.minecraftsingularity.common.data.singularityBiomeTagsProvider;
import net.minecraftsingularity.common.data.singularityBlockTagsProvider;
import net.minecraftsingularity.common.data.singularityEnchantmentTagsProvider;
import net.minecraftsingularity.common.data.singularityEntityTypeTagsProvider;
import net.minecraftsingularity.common.data.singularityFluidTagsProvider;
import net.minecraftsingularity.common.data.singularityItemTagsProvider;
import net.minecraftsingularity.common.data.singularityLootTableProvider;
import net.minecraftsingularity.common.data.singularityRecipeProvider;
import net.minecraftsingularity.common.data.singularitySpriteSourceProvider;
import net.minecraftsingularity.common.data.singularityStructureTagsProvider;
import net.minecraftsingularity.common.data.VanillaSoundDefinitionsProvider;
import net.minecraftsingularity.common.loot.CanToolPerformAction;
import net.minecraftsingularity.common.loot.LootTableIdCondition;
import net.minecraftsingularity.common.world.BiomeModifier;
import net.minecraftsingularity.common.world.singularityBiomeModifiers.AddFeaturesBiomeModifier;
import net.minecraftsingularity.common.world.singularityBiomeModifiers.AddSpawnsBiomeModifier;
import net.minecraftsingularity.common.world.singularityBiomeModifiers.RemoveFeaturesBiomeModifier;
import net.minecraftsingularity.common.world.singularityBiomeModifiers.RemoveSpawnsBiomeModifier;
import net.minecraftsingularity.common.world.NoneBiomeModifier;
import net.minecraftsingularity.common.world.NoneStructureModifier;
import net.minecraftsingularity.common.world.StructureModifier;
import net.minecraftsingularity.event.network.GatherLoginConfigurationTasksEvent;
import net.minecraftsingularity.eventbus.api.bus.BusGroup;
import net.minecraftsingularity.fluids.FluidType;
import net.minecraftsingularity.fluids.singularityFlowingFluid;
import net.minecraftsingularity.fml.*;
import net.minecraftsingularity.fml.config.ModConfig;
import net.minecraftsingularity.fml.event.config.ModConfigEvent;
import net.minecraftsingularity.fml.event.lifecycle.*;
import net.minecraftsingularity.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftsingularity.fml.loading.FMLLoader;
import net.minecraftsingularity.registries.*;
import net.minecraftsingularity.registries.holdersets.AndHolderSet;
import net.minecraftsingularity.registries.holdersets.AnyHolderSet;
import net.minecraftsingularity.registries.holdersets.HolderSetType;
import net.minecraftsingularity.registries.holdersets.NotHolderSet;
import net.minecraftsingularity.registries.holdersets.OrHolderSet;
import net.minecraftsingularity.network.NetworkInitialization;
import net.minecraftsingularity.network.tasks.singularityNetworkConfigurationHandler;
import net.minecraftsingularity.event.entity.EntityAttributeModificationEvent;
import net.minecraftsingularity.data.event.GatherDataEvent;
import net.minecraftsingularity.server.command.EnumArgument;
import net.minecraftsingularity.server.command.ModIdArgument;
import net.minecraftsingularity.server.permission.nodes.PermissionNode;
import net.minecraftsingularity.server.permission.nodes.PermissionTypes;
import net.minecraftsingularity.unsafe.UnsafeHacks;
import net.minecraftsingularity.versions.singularity.singularityVersion;
import net.minecraftsingularity.versions.mcp.MCPVersion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraftsingularity.common.crafting.ConditionalRecipe;
import net.minecraftsingularity.common.crafting.conditions.AndCondition;
import net.minecraftsingularity.common.crafting.conditions.FalseCondition;
import net.minecraftsingularity.common.crafting.conditions.ICondition;
import net.minecraftsingularity.common.crafting.conditions.ItemExistsCondition;
import net.minecraftsingularity.common.crafting.conditions.ModLoadedCondition;
import net.minecraftsingularity.common.crafting.conditions.NotCondition;
import net.minecraftsingularity.common.crafting.conditions.OrCondition;
import net.minecraftsingularity.common.crafting.conditions.TagEmptyCondition;
import net.minecraftsingularity.common.crafting.conditions.TrueCondition;
import net.minecraftsingularity.common.crafting.ingredients.CompoundIngredient;
import net.minecraftsingularity.common.crafting.ingredients.DifferenceIngredient;
import net.minecraftsingularity.common.crafting.ingredients.IIngredientSerializer;
import net.minecraftsingularity.common.crafting.ingredients.IntersectionIngredient;
import net.minecraftsingularity.common.crafting.ingredients.NBTIngredient;
import net.minecraftsingularity.fml.common.Mod;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.Nullable;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Mod("singularity")
public class singularityMod {
    public static final String VERSION_CHECK_CAT = "version_checking";
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker singularityMOD = MarkerManager.getMarker("singularityMOD");

    private static final List<DeferredRegister<?>> registries = new ArrayList<>();
    private static <T> DeferredRegister<T> deferred(ResourceKey<Registry<T>> key) {
        return deferred(key, "singularity");
    }
    private static <T> DeferredRegister<T> deferred(ResourceKey<Registry<T>> key, String modid) {
        var ret = DeferredRegister.create(key, modid);
        registries.add(ret);
        return ret;
    }

    private static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = deferred(Registries.COMMAND_ARGUMENT_TYPE);
    static {
        @SuppressWarnings({ "unchecked", "rawtypes", "unused" })
        var v = COMMAND_ARGUMENT_TYPES.register("enum", () -> ArgumentTypeInfos.registerByClass(EnumArgument.class, new EnumArgument.Info()));
        COMMAND_ARGUMENT_TYPES.register("modid", () -> ArgumentTypeInfos.registerByClass(ModIdArgument.class, SingletonArgumentInfo.contextFree(ModIdArgument::modIdArgument)));
    }

    private static final DeferredRegister<Attribute> ATTRIBUTES = deferred(singularityRegistries.Keys.ATTRIBUTES);
    public static final RegistryObject<Attribute> SWIM_SPEED = ATTRIBUTES.register("swim_speed", () -> new RangedAttribute("singularity.swim_speed", 1.0D, 0.0D, 1024.0D).setSyncable(true));
    public static final RegistryObject<Attribute> NAMETAG_DISTANCE = ATTRIBUTES.register("nametag_distance", () -> new RangedAttribute("singularity.name_tag_distance", 64.0D, 0.0D, 64.0).setSyncable(true));

    private static final DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS = deferred(singularityRegistries.Keys.BIOME_MODIFIER_SERIALIZERS);
    static {
        BIOME_MODIFIER_SERIALIZERS.register("none", () -> NoneBiomeModifier.CODEC);
        BIOME_MODIFIER_SERIALIZERS.register("add_features", () -> AddFeaturesBiomeModifier.CODEC);
        BIOME_MODIFIER_SERIALIZERS.register("remove_features", () -> RemoveFeaturesBiomeModifier.CODEC);
        BIOME_MODIFIER_SERIALIZERS.register("add_spawns", () -> AddSpawnsBiomeModifier.CODEC);
        BIOME_MODIFIER_SERIALIZERS.register("remove_spawns", () -> RemoveSpawnsBiomeModifier.CODEC);
    }

    private static final DeferredRegister<MapCodec<? extends StructureModifier>> STRUCTURE_MODIFIER_SERIALIZERS = deferred(singularityRegistries.Keys.STRUCTURE_MODIFIER_SERIALIZERS);
    static {
        STRUCTURE_MODIFIER_SERIALIZERS.register("none", () -> NoneStructureModifier.CODEC);
    }

    private static final DeferredRegister<HolderSetType> HOLDER_SET_TYPES = deferred(singularityRegistries.Keys.HOLDER_SET_TYPES);

    /**
     * Stock holder set type that represents any/all values in a registry. Can be used in a holderset object with {@code { "type": "singularity:any" }}
     */
    public static final RegistryObject<HolderSetType> ANY_HOLDER_SET = HOLDER_SET_TYPES.register("any", () -> AnyHolderSet::codec);

    /**
     * Stock holder set type that represents an intersection of other holdersets. Can be used in a holderset object with {@code { "type": "singularity:and", "values": [list of holdersets] }}
     */
    public static final RegistryObject<HolderSetType> AND_HOLDER_SET = HOLDER_SET_TYPES.register("and", () -> AndHolderSet::codec);

    /**
     * Stock holder set type that represents a union of other holdersets. Can be used in a holderset object with {@code { "type": "singularity:or", "values": [list of holdersets] }}
     */
    public static final RegistryObject<HolderSetType> OR_HOLDER_SET = HOLDER_SET_TYPES.register("or", () -> OrHolderSet::codec);

    /**
     * <p>Stock holder set type that represents all values in a registry except those in another given set.
     * Can be used in a holderset object with {@code { "type": "singularity:not", "value": holderset }}</p>
     */
    public static final RegistryObject<HolderSetType> NOT_HOLDER_SET = HOLDER_SET_TYPES.register("not", () -> NotHolderSet::codec);

    private static final DeferredRegister<FluidType> VANILLA_FLUID_TYPES = deferred(singularityRegistries.Keys.FLUID_TYPES, "minecraft");

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
                public void setItemMovement(ItemEntity entity) {
                    if (!entity.isNoGravity())
                        entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
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
                public @Nullable PathType getBlockPathType(FluidState state, BlockGetter level, BlockPos pos, @Nullable Mob mob, boolean canFluidLog) {
                    return canFluidLog ? super.getBlockPathType(state, level, pos, mob, true) : null;
                }

                @Override
                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                    consumer.accept(new IClientFluidTypeExtensions() {
                        private static final Identifier UNDERWATER_LOCATION = Identifier.withDefaultNamespace("textures/misc/underwater.png");
                        private static final Identifier WATER_STILL = Identifier.withDefaultNamespace("block/water_still");
                        private static final Identifier WATER_FLOW = Identifier.withDefaultNamespace("block/water_flow");
                        private static final Identifier WATER_OVERLAY = Identifier.withDefaultNamespace("block/water_overlay");

                        @Override
                        public Identifier getStillTexture() {
                            return WATER_STILL;
                        }

                        @Override
                        public Identifier getFlowingTexture() {
                            return WATER_FLOW;
                        }

                        @Nullable
                        @Override
                        public Identifier getOverlayTexture() {
                            return WATER_OVERLAY;
                        }

                        @Override
                        public Identifier getRenderOverlayTexture(Minecraft mc) {
                            return UNDERWATER_LOCATION;
                        }

                        @Override
                        public int getTintColor() {
                            return 0xFF3F76E4;
                        }
                    });
                }
            });

    public static final RegistryObject<FluidType> LAVA_TYPE = VANILLA_FLUID_TYPES.register("lava", () ->
            new FluidType(FluidType.Properties.create()
                    .descriptionId("block.minecraft.lava")
                    .canSwim(false)
                    .canDrown(false)
                    .pathType(PathType.LAVA)
                    .adjacentPathType(null)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                    .lightLevel(15)
                    .density(3000)
                    .viscosity(6000)
                    .temperature(1300))
            {
                @Override
                public double motionScale(Entity entity) {
                    return entity.level().environmentAttributes().getDimensionValue(EnvironmentAttributes.WATER_EVAPORATES) ? 0.007D : 0.0023333333333333335D;
                }

                @Override
                public void setItemMovement(ItemEntity entity) {
                    Vec3 vec3 = entity.getDeltaMovement();
                    entity.setDeltaMovement(vec3.x * (double)0.95F, vec3.y + (double)(vec3.y < (double)0.06F ? 5.0E-4F : 0.0F), vec3.z * (double)0.95F);
                }

                @Override
                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                    consumer.accept(new IClientFluidTypeExtensions() {
                        private static final Identifier LAVA_STILL = Identifier.withDefaultNamespace("block/lava_still");
                        private static final Identifier LAVA_FLOW = Identifier.withDefaultNamespace("block/lava_flow");

                        @Override
                        public Identifier getStillTexture() {
                            return LAVA_STILL;
                        }

                        @Override
                        public Identifier getFlowingTexture() {
                            return LAVA_FLOW;
                        }
                    });
                }
            });

    private static final DeferredRegister<MapCodec<? extends LootItemCondition>> LOOT_CONDITION_TYPES = deferred(Registries.LOOT_CONDITION_TYPE);
    static {
        LOOT_CONDITION_TYPES.register("loot_table_id", () -> LootTableIdCondition.CODEC);
        LOOT_CONDITION_TYPES.register("can_tool_perform_action", () -> CanToolPerformAction.CODEC);
    }

    private static final DeferredRegister<MapCodec<? extends ICondition>> CONDITION_SERIALIZERS = deferred(singularityRegistries.Keys.CONDITION_SERIALIZERS);
    static {
        CONDITION_SERIALIZERS.register("and", () -> AndCondition.CODEC);
        CONDITION_SERIALIZERS.register("false", () -> FalseCondition.CODEC);
        CONDITION_SERIALIZERS.register("item_exists", () -> ItemExistsCondition.CODEC);
        CONDITION_SERIALIZERS.register("mod_loaded", () -> ModLoadedCondition.CODEC);
        CONDITION_SERIALIZERS.register("not", () -> NotCondition.CODEC);
        CONDITION_SERIALIZERS.register("or", () -> OrCondition.CODEC);
        CONDITION_SERIALIZERS.register("true", () -> TrueCondition.CODEC);
        CONDITION_SERIALIZERS.register("tag_empty", () -> TagEmptyCondition.CODEC);
    }

    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = deferred(singularityRegistries.Keys.RECIPE_SERIALIZERS);
    static {
        RECIPE_SERIALIZERS.register("conditional", () -> ConditionalRecipe.SERIALZIER);
    }

    private static final DeferredRegister<IIngredientSerializer<?>> INGREDIENT_SERIALIZERS = deferred(singularityRegistries.Keys.INGREDIENT_SERIALIZERS);
    static {
        INGREDIENT_SERIALIZERS.register("compound", () -> CompoundIngredient.SERIALIZER);
        INGREDIENT_SERIALIZERS.register("nbt", () -> NBTIngredient.SERIALIZER);
        INGREDIENT_SERIALIZERS.register("difference", () -> DifferenceIngredient.SERIALIZER);
        INGREDIENT_SERIALIZERS.register("intersection", () -> IntersectionIngredient.SERIALIZER);
    }


    private static boolean enableMilkFluid = false;
    public static final RegistryObject<SoundEvent> BUCKET_EMPTY_MILK = RegistryObject.create(Identifier.withDefaultNamespace("item.bucket.empty_milk"), singularityRegistries.SOUND_EVENTS);
    public static final RegistryObject<SoundEvent> BUCKET_FILL_MILK = RegistryObject.create(Identifier.withDefaultNamespace("item.bucket.fill_milk"), singularityRegistries.SOUND_EVENTS);
    public static final RegistryObject<FluidType> MILK_TYPE = RegistryObject.createOptional(Identifier.withDefaultNamespace("milk"), singularityRegistries.Keys.FLUID_TYPES.identifier(), "minecraft");
    public static final RegistryObject<Fluid> MILK = RegistryObject.create(Identifier.withDefaultNamespace("milk"), singularityRegistries.FLUIDS);
    public static final RegistryObject<Fluid> FLOWING_MILK = RegistryObject.create(Identifier.withDefaultNamespace("flowing_milk"), singularityRegistries.FLUIDS);

    private static singularityMod INSTANCE;
    public static singularityMod getInstance() {
        return INSTANCE;
    }

    /**
     * Run this method during mod constructor to enable milk and add it to the Minecraft milk bucket
     */
    public static void enableMilkFluid() {
        enableMilkFluid = true;
    }

    public singularityMod(FMLJavaModLoadingContext context) {
        LOGGER.info(singularityMOD,"singularity mod loading, version {}, for MC {} with MCP {}", singularityVersion.getVersion(), MCPVersion.getMCVersion(), MCPVersion.getMCPVersion());
        INSTANCE = this;
        MinecraftForge.initialize();
        CrashReportCallables.registerCrashCallable("Crash Report UUID", ()-> {
            final UUID uuid = UUID.randomUUID();
            LOGGER.fatal("Preparing crash report with UUID {}", uuid);
            return uuid.toString();
        });

        hackDNSResolver();

        NetworkInitialization.init();

        CrashReportCallables.registerCrashCallable("FML", singularityVersion::getSpec);
        CrashReportCallables.registerCrashCallable("singularity", ()->singularityVersion.getGroup()+":"+singularityVersion.getVersion());

        BusGroup modBusGroup = context.getModBusGroup();
        // singularity-provided datapack registries
        DataPackRegistryEvent.NewRegistry.BUS.addListener(event -> {
            event.dataPackRegistry(singularityRegistries.Keys.BIOME_MODIFIERS, BiomeModifier.DIRECT_CODEC);
            event.dataPackRegistry(singularityRegistries.Keys.STRUCTURE_MODIFIERS, StructureModifier.DIRECT_CODEC);
        });
        FMLCommonSetupEvent.getBus(modBusGroup).addListener(singularityMod::preInit);
        GatherDataEvent.getBus(modBusGroup).addListener(singularityMod::gatherData);
        var registerEventBus = RegisterEvent.getBus(modBusGroup);
        registerEventBus.addListener(singularityMod::registerFluids);
        registerEventBus.addListener(singularityMod::registerVanillaDisplayContexts);
        EntityAttributeModificationEvent.BUS.addListener(singularityMod::onRegisterAttributes);
        singularityDeferredRegistriesSetup.setup(modBusGroup);
        for (var reg : registries)
            reg.register(modBusGroup);

        context.registerConfig(ModConfig.Type.CLIENT, singularityConfig.clientSpec);
        context.registerConfig(ModConfig.Type.SERVER, singularityConfig.serverSpec);
        context.registerConfig(ModConfig.Type.COMMON, singularityConfig.commonSpec);
        if (singularityConfig.LOGGER.isDebugEnabled()) {
            ModConfigEvent.Loading.getBus(modBusGroup).addListener(singularityConfig::onLoad);
            ModConfigEvent.Reloading.getBus(modBusGroup).addListener(singularityConfig::onFileChange);
        }

        // singularity does not display problems when the remote is not matching.
        context.registerDisplayTest(IExtensionPoint.DisplayTest.IGNORE_ALL_VERSION);
        StartupMessageManager.addModMessage("singularity version "+singularityVersion.getVersion());

        singularityInternalHandler.register();
        GatherLoginConfigurationTasksEvent.BUS.addListener(singularityNetworkConfigurationHandler::gatherInit);

        singularityRegistries.ITEMS.tags().addOptionalTagDefaults(Tags.Items.ENCHANTING_FUELS, Set.of(singularityRegistries.ITEMS.getDelegateOrThrow(Items.LAPIS_LAZULI)));

        // TODO: Remove when addAlias becomes proper API, as this should be done in the DR's above.
        addAlias(singularityRegistries.ATTRIBUTES, Identifier.fromNamespaceAndPath("singularity", "reach_distance"), Identifier.fromNamespaceAndPath("singularity", "block_reach"));
        addAlias(singularityRegistries.ATTRIBUTES, Identifier.fromNamespaceAndPath("singularity", "attack_range"), Identifier.fromNamespaceAndPath("singularity", "entity_reach"));
    }

    private static void preInit(FMLCommonSetupEvent evt) {
        VersionChecker.startVersionCheck();
        //VanillaPacketSplitter.register();
    }

    private static void onRegisterAttributes(EntityAttributeModificationEvent event) {
        for (var type : event.getTypes()) {
            event.add(type, singularityMod.SWIM_SPEED.getHolder().get());
            event.add(type, singularityMod.NAMETAG_DISTANCE.getHolder().get());
        }
    }

    private static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput packOutput = gen.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        gen.addProvider(true, new PackMetadataGenerator(packOutput)
            .add(PackMetadataSection.SERVER_TYPE, new PackMetadataSection(
                Component.translatable("pack.singularity.description"),
                DetectedVersion.BUILT_IN.packVersion(PackType.SERVER_DATA).minorRange()
            ))
        );
        singularityBlockTagsProvider blockTags = new singularityBlockTagsProvider(packOutput, lookupProvider, existingFileHelper);
        gen.addProvider(event.includeServer(), blockTags);
        gen.addProvider(event.includeServer(), new singularityItemTagsProvider(packOutput, lookupProvider, existingFileHelper));
        gen.addProvider(event.includeServer(), new singularityEntityTypeTagsProvider(packOutput, lookupProvider, existingFileHelper));
        gen.addProvider(event.includeServer(), new singularityFluidTagsProvider(packOutput, lookupProvider, existingFileHelper));
        gen.addProvider(event.includeServer(), new singularityEnchantmentTagsProvider(packOutput, lookupProvider, existingFileHelper));
        gen.addProvider(event.includeServer(), new singularityRecipeProvider.Runner(packOutput, lookupProvider));
        gen.addProvider(event.includeServer(), new singularityLootTableProvider(packOutput, lookupProvider));
        gen.addProvider(event.includeServer(), new singularityBiomeTagsProvider(packOutput, lookupProvider, existingFileHelper));
        gen.addProvider(event.includeServer(), new singularityStructureTagsProvider(packOutput, lookupProvider, existingFileHelper));

        gen.addProvider(event.includeClient(), new VanillaSoundDefinitionsProvider(packOutput, existingFileHelper));
        // The provider uses Client only classes, so put a side guard on it.
        if (FMLLoader.getDist() == Dist.CLIENT) {
            gen.addProvider(event.includeClient(), new singularityAtlasProvider(packOutput));
        }
    }

    // done in an event instead of deferred to only enable if a mod requests it
    private static void registerFluids(RegisterEvent event) {
        if (enableMilkFluid) {
            // register milk fill, empty sounds (delegates to water fill, empty sounds)
            event.register(singularityRegistries.Keys.SOUND_EVENTS, helper -> {
                helper.register(BUCKET_EMPTY_MILK.getId(), SoundEvent.createVariableRangeEvent(BUCKET_EMPTY_MILK.getId()));
                helper.register(BUCKET_FILL_MILK.getId(), SoundEvent.createVariableRangeEvent(BUCKET_FILL_MILK.getId()));
            });

            // register fluid type
            event.register(singularityRegistries.Keys.FLUID_TYPES, helper -> helper.register(MILK_TYPE.getId(), new FluidType(
                FluidType.Properties.create().density(1024).viscosity(1024)
                    .sound(SoundActions.BUCKET_FILL, BUCKET_FILL_MILK.get())
                    .sound(SoundActions.BUCKET_EMPTY, BUCKET_EMPTY_MILK.get())
            ) {
                @Override
                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                    consumer.accept(new IClientFluidTypeExtensions() {
                        private static final Identifier MILK_STILL = Identifier.fromNamespaceAndPath("singularity", "block/milk_still");
                        private static final Identifier MILK_FLOW = Identifier.fromNamespaceAndPath("singularity", "block/milk_flowing");

                        @Override
                        public Identifier getStillTexture() {
                            return MILK_STILL;
                        }

                        @Override
                        public Identifier getFlowingTexture() {
                            return MILK_FLOW;
                        }
                    });
                }
            }));

            // register fluids
            event.register(singularityRegistries.Keys.FLUIDS, helper -> {
                // set up properties
                singularityFlowingFluid.Properties properties = new singularityFlowingFluid.Properties(MILK_TYPE, MILK, FLOWING_MILK).bucket(() -> Items.MILK_BUCKET);

                helper.register(MILK.getId(), new singularityFlowingFluid.Source(properties));
                helper.register(FLOWING_MILK.getId(), new singularityFlowingFluid.Flowing(properties));
            });
        }
    }

    private static void registerVanillaDisplayContexts(RegisterEvent event) {
        if (event.getRegistryKey().equals(singularityRegistries.Keys.DISPLAY_CONTEXTS)) {
            IForgeRegistryInternal<ItemDisplayContext> singularityRegistry = (IForgeRegistryInternal<ItemDisplayContext>) event.<ItemDisplayContext>getForgeRegistry();
            if (singularityRegistry == null)
                throw new IllegalStateException("Item display context was not a singularity registry, wtf???");

            Arrays.stream(ItemDisplayContext.values())
                .filter(Predicate.not(ItemDisplayContext::isModded))
                .forEach(ctx -> singularityRegistry.register(ctx.getId(), Identifier.fromNamespaceAndPath("minecraft", ctx.getSerializedName()), ctx));
        }
    }

    public static final PermissionNode<Boolean> USE_SELECTORS_PERMISSION = new PermissionNode<>("singularity", "use_entity_selectors",
            PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && Commands.LEVEL_GAMEMASTERS.check(player.permissions()));

    /**
     * TODO: Remove when {@link singularityRegistry#addAlias(Identifier, Identifier)} is elevated to {@link IForgeRegistry}.
     */
    //@Deprecated(forRemoval = true, since = "1.20")
    private static <T> void addAlias(IForgeRegistry<T> registry, Identifier from, Identifier to) {
        singularityRegistry<T> fReg = (singularityRegistry<T>) registry;
        fReg.addAlias(from, to);
    }

    // net.minecraft.client.multiplayer.resolver.ServerRedirectHandler.createDnsSrvRedirectHandler uses DNSContextFactory
    // to resolve DNS records, and that is initialized reflectively by NamingManager. Which in module land isn't allowed.
    // So hack it so it is. this is equivalent to doing --add-exports jdk.naming.dns/com.sun.jndi.dns=java.naming
    private static void hackDNSResolver() {
        try {
            var target = Class.forName("com.sun.jndi.dns.DnsContextFactory");
            var reader = Class.forName("javax.naming.spi.NamingManager");
            addOpen(target, reader);
        } catch (Exception e) {
            LOGGER.error(singularityMOD, "Failed to hack DnsContextFactory, some servers might not work", e);
        }
    }

    private static Method implAddExportsOrOpens;
    private static void addOpen(Class<?> target, Class<?> reader) throws Exception {
        if (implAddExportsOrOpens == null) {
            implAddExportsOrOpens = Module.class.getDeclaredMethod("implAddExportsOrOpens", String.class, Module.class, boolean.class, boolean.class);
            UnsafeHacks.setAccessible(implAddExportsOrOpens);
        }
        LOGGER.info(singularityMOD, "Opening {}/{} to {}", target.getModule().getName(), target.getPackageName(), reader.getModule().getName());
        implAddExportsOrOpens.invoke(target.getModule(), target.getPackageName(), reader.getModule(), /*open*/true, /*syncVM*/true);
    }
}
