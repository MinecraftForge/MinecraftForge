/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

import com.google.common.collect.Multimap;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.item.Items;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.Registry;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeFluidTagsProvider;
import net.minecraftforge.common.loot.CanToolPerformAction;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootTableIdCondition;
import net.minecraftforge.common.util.MavenVersionStringHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.registries.*;
import net.minecraftforge.internal.WorldPersistenceHooks;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.network.filters.VanillaPacketSplitter;
import net.minecraftforge.server.command.EnumArgument;
import net.minecraftforge.server.command.ModIdArgument;
import net.minecraftforge.versions.forge.ForgeVersion;
import net.minecraftforge.versions.mcp.MCPVersion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.*;

@Mod("forge")
public class ForgeMod implements WorldPersistenceHooks.WorldPersistenceHook
{
    public static final String VERSION_CHECK_CAT = "version_checking";
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker FORGEMOD = MarkerManager.getMarker("FORGEMOD");

    private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Attribute.class, "forge");

    public static final RegistryObject<Attribute> SWIM_SPEED = ATTRIBUTES.register("swim_speed", () -> new RangedAttribute("forge.swimSpeed", 1.0D, 0.0D, 1024.0D).setSyncable(true));
    public static final RegistryObject<Attribute> NAMETAG_DISTANCE = ATTRIBUTES.register("nametag_distance", () -> new RangedAttribute("forge.nameTagDistance", 64.0D, 0.0D, 64.0).setSyncable(true));
    public static final RegistryObject<Attribute> ENTITY_GRAVITY = ATTRIBUTES.register("entity_gravity", () -> new RangedAttribute("forge.entity_gravity", 0.08D, -8.0D, 8.0D).setSyncable(true));

    public static final RegistryObject<Attribute> REACH_DISTANCE = ATTRIBUTES.register("reach_distance", () -> new RangedAttribute("generic.reachDistance", 5.0D, 0.0D, 1024.0D).setSyncable(true));

    private static boolean enableMilkFluid = false;
    public static final RegistryObject<Fluid> MILK = RegistryObject.of(new ResourceLocation("milk"), ForgeRegistries.FLUIDS);
    public static final RegistryObject<Fluid> FLOWING_MILK = RegistryObject.of(new ResourceLocation("flowing_milk"), ForgeRegistries.FLUIDS);

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

        WorldPersistenceHooks.addHook(this);
        WorldPersistenceHooks.addHook(new FMLWorldPersistenceHook());
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::preInit);
        modEventBus.addListener(this::gatherData);
        modEventBus.addListener(this::loadComplete);
        modEventBus.addGenericListener(Fluid.class, this::registerFluids);
        modEventBus.register(this);
        ATTRIBUTES.register(modEventBus);
        MinecraftForge.EVENT_BUS.addListener(this::serverStopping);
        MinecraftForge.EVENT_BUS.addGenericListener(SoundEvent.class, this::missingSoundMapping);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ForgeConfig.clientSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ForgeConfig.serverSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ForgeConfig.commonSpec);
        modEventBus.register(ForgeConfig.class);
        // Forge does not display problems when the remote is not matching.
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, ()->new IExtensionPoint.DisplayTest(()->"ANY", (remote, isServer)-> true));
        StartupMessageManager.addModMessage("Forge version "+ForgeVersion.getVersion());

        MinecraftForge.EVENT_BUS.addListener(VillagerTradingManager::loadTrades);
        MinecraftForge.EVENT_BUS.register(MinecraftForge.INTERNAL_HANDLER);
        BiomeDictionary.init();
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

        registerArgumentTypes();
        VanillaPacketSplitter.register();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void registerArgumentTypes()
    {
        ArgumentTypes.register("forge:enum", EnumArgument.class, (ArgumentSerializer) new EnumArgument.Serializer());
        ArgumentTypes.register("forge:modid", ModIdArgument.class, new EmptyArgumentSerializer<>(ModIdArgument::modIdArgument));
    }

    public void loadComplete(FMLLoadCompleteEvent event)
    {
        if (FMLEnvironment.dist == Dist.CLIENT)
            ForgeHooksClient.registerForgeWorldPresetScreens();
    }

    public void serverStopping(ServerStoppingEvent evt)
    {
        WorldWorkerManager.clear();
    }

    @Override
    public CompoundTag getDataForWriting(LevelStorageSource.LevelStorageAccess levelSave, WorldData serverInfo)
    {
        CompoundTag forgeData = new CompoundTag();
        CompoundTag dims = new CompoundTag();
        //TODO Dimensions
//        DimensionManager.writeRegistry(dims);
        if (!dims.isEmpty())
            forgeData.put("dims", dims);
        return forgeData;
    }

    @Override
    public void readData(LevelStorageSource.LevelStorageAccess levelSave, WorldData serverInfo, CompoundTag tag)
    {
        //TODO Dimensions
//        if (tag.contains("dims", 10))
//            DimensionManager.readRegistry(tag.getCompound("dims"));
//        DimensionManager.processScheduledDeletions(levelSave);
    }

    public void mappingChanged(RegistryEvent.IdMappingEvent evt)
    {
        Ingredient.invalidateAll();
    }

    @Override
    public String getModId()
    {
        return ForgeVersion.MOD_ID;
    }

    public void gatherData(GatherDataEvent event)
    {
        DataGenerator gen = event.getGenerator();

        if (event.includeServer())
        {
            ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
            ForgeBlockTagsProvider blockTags = new ForgeBlockTagsProvider(gen, existingFileHelper);
            gen.addProvider(blockTags);
            gen.addProvider(new ForgeItemTagsProvider(gen, blockTags, existingFileHelper));
            gen.addProvider(new ForgeFluidTagsProvider(gen, existingFileHelper));
            gen.addProvider(new ForgeRecipeProvider(gen));
            gen.addProvider(new ForgeLootTableProvider(gen));
        }
    }

    public void missingSoundMapping(RegistryEvent.MissingMappings<SoundEvent> event)
    {
        //Removed in 1.15, see https://minecraft.gamepedia.com/Parrot#History
        List<String> removedSounds = Arrays.asList("entity.parrot.imitate.panda", "entity.parrot.imitate.zombie_pigman", "entity.parrot.imitate.enderman", "entity.parrot.imitate.polar_bear", "entity.parrot.imitate.wolf");
        for (RegistryEvent.MissingMappings.Mapping<SoundEvent> mapping : event.getAllMappings())
        {
            ResourceLocation regName = mapping.key;
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
    public void registerFluids(RegistryEvent.Register<Fluid> event)
    {
        if (enableMilkFluid)
        {
            // set up attributes
            FluidAttributes.Builder attributesBuilder = FluidAttributes.builder(new ResourceLocation("forge", "block/milk_still"), new ResourceLocation("forge", "block/milk_flowing")).density(1024).viscosity(1024);
            ForgeFlowingFluid.Properties properties = new ForgeFlowingFluid.Properties(MILK, FLOWING_MILK, attributesBuilder).bucket(() -> Items.MILK_BUCKET);
            // register fluids
            event.getRegistry().register(new ForgeFlowingFluid.Source(properties).setRegistryName(MILK.getId()));
            event.getRegistry().register(new ForgeFlowingFluid.Flowing(properties).setRegistryName(FLOWING_MILK.getId()));
        }
    }

    @SubscribeEvent //ModBus, can't use addListener due to nested genetics.
    public void registerRecipeSerialziers(RegistryEvent.Register<RecipeSerializer<?>> event)
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
        CraftingHelper.register(new ResourceLocation("minecraft", "item"), VanillaIngredientSerializer.INSTANCE);

        event.getRegistry().register(new ConditionalRecipe.Serializer<Recipe<?>>().setRegistryName(new ResourceLocation("forge", "conditional")));

    }

    @SubscribeEvent //ModBus
    @SuppressWarnings("unused") // automatically called by Forge
    public void registerLootData(RegistryEvent.Register<GlobalLootModifierSerializer<?>> event)
    {
        // Ignore the event itself: this is done only not to statically initialize our custom LootConditionType
        Registry.register(Registry.LOOT_CONDITION_TYPE, new ResourceLocation("forge:loot_table_id"), LootTableIdCondition.LOOT_TABLE_ID);
        Registry.register(Registry.LOOT_CONDITION_TYPE, new ResourceLocation("forge:can_tool_perform_action"), CanToolPerformAction.LOOT_CONDITION_TYPE);
    }

    private static final class FMLWorldPersistenceHook implements WorldPersistenceHooks.WorldPersistenceHook
    {

        private static final Logger LOGGER = LogManager.getLogger();
        private static final Marker WORLDPERSISTENCE = MarkerManager.getMarker("WP");

        @Override
        public String getModId()
        {
            return "fml";
        }

        @Override
        public CompoundTag getDataForWriting(LevelStorageSource.LevelStorageAccess levelSave, WorldData serverInfo)
        {
            CompoundTag fmlData = new CompoundTag();
            ListTag modList = new ListTag();
            ModList.get().getMods().forEach(mi->
            {
                final CompoundTag mod = new CompoundTag();
                mod.putString("ModId", mi.getModId());
                mod.putString("ModVersion", MavenVersionStringHelper.artifactVersionToString(mi.getVersion()));
                modList.add(mod);
            });
            fmlData.put("LoadingModList", modList);

            CompoundTag registries = new CompoundTag();
            fmlData.put("Registries", registries);
            LOGGER.debug(WORLDPERSISTENCE,"Gathering id map for writing to world save {}", serverInfo.getLevelName());

            for (Map.Entry<ResourceLocation, ForgeRegistry.Snapshot> e : RegistryManager.ACTIVE.takeSnapshot(true).entrySet())
            {
                registries.put(e.getKey().toString(), e.getValue().write());
            }
            LOGGER.debug(WORLDPERSISTENCE,"ID Map collection complete {}", serverInfo.getLevelName());
            return fmlData;
        }

        @Override
        public void readData(LevelStorageSource.LevelStorageAccess levelSave, WorldData serverInfo, CompoundTag tag)
        {
            if (tag.contains("LoadingModList"))
            {
                ListTag modList = tag.getList("LoadingModList", (byte)10);
                for (int i = 0; i < modList.size(); i++)
                {
                    CompoundTag mod = modList.getCompound(i);
                    String modId = mod.getString("ModId");
                    if (Objects.equals("minecraft",  modId)) {
                        continue;
                    }
                    String modVersion = mod.getString("ModVersion");
                    Optional<? extends ModContainer> container = ModList.get().getModContainerById(modId);
                    if (!container.isPresent())
                    {
                        LOGGER.error(WORLDPERSISTENCE,"This world was saved with mod {} which appears to be missing, things may not work well", modId);
                        continue;
                    }
                    if (!Objects.equals(modVersion, MavenVersionStringHelper.artifactVersionToString(container.get().getModInfo().getVersion())))
                    {
                        LOGGER.warn(WORLDPERSISTENCE,"This world was saved with mod {} version {} and it is now at version {}, things may not work well", modId, modVersion, MavenVersionStringHelper.artifactVersionToString(container.get().getModInfo().getVersion()));
                    }
                }
            }

            Multimap<ResourceLocation, ResourceLocation> failedElements = null;

            if (tag.contains("Registries")) // 1.8, genericed out the 'registries' list
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
            }
        }
    }
}
