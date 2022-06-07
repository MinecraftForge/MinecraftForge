/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.item.Items;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.model.ModelLoaderRegistry;
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
import net.minecraftforge.common.world.NoneBiomeModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.*;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.network.filters.VanillaPacketSplitter;
import net.minecraftforge.server.command.EnumArgument;
import net.minecraftforge.server.command.ModIdArgument;
import net.minecraftforge.versions.forge.ForgeVersion;
import net.minecraftforge.versions.mcp.MCPVersion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.NBTIngredient;
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
import net.minecraftforge.common.data.ForgeItemTagsProvider;
import net.minecraftforge.common.data.ForgeLootTableProvider;
import net.minecraftforge.common.data.ForgeRecipeProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.mojang.serialization.Codec;

import java.util.*;

@Mod("forge")
public class ForgeMod
{
    public static final String VERSION_CHECK_CAT = "version_checking";
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker FORGEMOD = MarkerManager.getMarker("FORGEMOD");

    private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.Keys.ATTRIBUTES, "forge");
    private static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister.create(Registry.COMMAND_ARGUMENT_TYPE_REGISTRY, "forge");
    private static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, "forge");

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


    private static boolean enableMilkFluid = false;
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
        modEventBus.addListener(this::registerCapabilities);
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

        ForgeRegistries.ITEMS.tags().addOptionalTagDefaults(Tags.Items.ENCHANTING_FUELS, Set.of(ForgeRegistries.ITEMS.getDelegateOrThrow(Items.LAPIS_LAZULI)));

        if (FMLEnvironment.dist == Dist.CLIENT)
        {
            ModelLoaderRegistry.init();
        }
    }

    public void registerCapabilities(RegisterCapabilitiesEvent event)
    {
        CapabilityItemHandler.register(event);
        CapabilityFluidHandler.register(event);
        CapabilityEnergy.register(event);
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
        if (enableMilkFluid && event.getRegistryKey().equals(ForgeRegistries.Keys.FLUIDS))
        {
            // set up attributes
            FluidAttributes.Builder attributesBuilder = FluidAttributes.builder(new ResourceLocation("forge", "block/milk_still"), new ResourceLocation("forge", "block/milk_flowing")).density(1024).viscosity(1024);
            ForgeFlowingFluid.Properties properties = new ForgeFlowingFluid.Properties(MILK, FLOWING_MILK, attributesBuilder).bucket(() -> Items.MILK_BUCKET);
            // register fluids
            event.register(ForgeRegistries.Keys.FLUIDS, MILK.getId(), () -> new ForgeFlowingFluid.Source(properties));
            event.register(ForgeRegistries.Keys.FLUIDS, FLOWING_MILK.getId(), () -> new ForgeFlowingFluid.Flowing(properties));
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
            CraftingHelper.register(new ResourceLocation("forge", "nbt"), NBTIngredient.Serializer.INSTANCE);
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
}
