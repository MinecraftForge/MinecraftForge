/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.storage.IServerConfiguration;
import net.minecraft.world.storage.IWorldInfo;
import net.minecraft.world.storage.SaveFormat;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLModIdMappingEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.progress.StartupMessageManager;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.command.ConfigCommand;
import net.minecraftforge.server.command.ForgeCommand;
import net.minecraftforge.versions.forge.ForgeVersion;
import net.minecraftforge.versions.mcp.MCPVersion;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.data.DataGenerator;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
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
import net.minecraftforge.common.model.animation.CapabilityAnimation;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Arrays;
import java.util.List;

@Mod("forge")
public class ForgeMod implements WorldPersistenceHooks.WorldPersistenceHook
{
    public static final String VERSION_CHECK_CAT = "version_checking";
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker FORGEMOD = MarkerManager.getMarker("FORGEMOD");

    private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Attribute.class, "forge");

    public static final RegistryObject<Attribute> SWIM_SPEED = ATTRIBUTES.register("swim_speed", () -> new RangedAttribute("forge.swimSpeed", 1.0D, 0.0D, 1024.0D).func_233753_a_(true));
    public static final RegistryObject<Attribute> NAMETAG_DISTANCE = ATTRIBUTES.register("nametag_distance", () -> new RangedAttribute("forge.nameTagDistance", 64.0D, 0.0D, Float.MAX_VALUE).func_233753_a_(true));
    public static final RegistryObject<Attribute> ENTITY_GRAVITY = ATTRIBUTES.register("entity_gravity", () -> new RangedAttribute("forge.entity_gravity", 0.08D, -8.0D, 8.0D).func_233753_a_(true));

    public static final RegistryObject<Attribute> REACH_DISTANCE = ATTRIBUTES.register("reach_distance", () -> new RangedAttribute( "generic.reachDistance", 5.0D, 0.0D, 1024.0D).func_233753_a_(true));

    private static ForgeMod INSTANCE;
    public static ForgeMod getInstance()
    {
        return INSTANCE;
    }

    public ForgeMod()
    {
        LOGGER.info(FORGEMOD,"Forge mod loading, version {}, for MC {} with MCP {}", ForgeVersion.getVersion(), MCPVersion.getMCVersion(), MCPVersion.getMCPVersion());
        INSTANCE = this;
        MinecraftForge.initialize();
        WorldPersistenceHooks.addHook(this);
        WorldPersistenceHooks.addHook(new FMLWorldPersistenceHook());
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::preInit);
        modEventBus.addListener(this::gatherData);
        modEventBus.register(this);
        ATTRIBUTES.register(modEventBus);
        MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
        MinecraftForge.EVENT_BUS.addListener(this::serverStopping);
        MinecraftForge.EVENT_BUS.addGenericListener(SoundEvent.class, this::missingSoundMapping);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ForgeConfig.clientSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ForgeConfig.serverSpec);
        modEventBus.register(ForgeConfig.class);
        // Forge does not display problems when the remote is not matching.
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, ()-> Pair.of(()->"ANY", (remote, isServer)-> true));
        StartupMessageManager.addModMessage("Forge version "+ForgeVersion.getVersion());

        MinecraftForge.EVENT_BUS.addListener(VillagerTradingManager::loadTrades);
        MinecraftForge.EVENT_BUS.register(MinecraftForge.INTERNAL_HANDLER);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void preInit(FMLCommonSetupEvent evt)
    {
        CapabilityItemHandler.register();
        CapabilityFluidHandler.register();
        CapabilityAnimation.register();
        CapabilityEnergy.register();

        VersionChecker.startVersionCheck();

        /*
         * We can't actually add any of these, because vanilla clients will choke on unknown argument types
         * So our custom arguments will not validate client-side, but they do still work
        ArgumentTypes.register("forge:enum", EnumArgument.class, new EnumArgument.Serializer());
        ArgumentTypes.register("forge:modid", ModIdArgument.class, new ArgumentSerializer<>(ModIdArgument::modIdArgument));
        ArgumentTypes.register("forge:structure_type", StructureArgument.class, new ArgumentSerializer<>(StructureArgument::structure));
        */
    }

    public void serverStarting(FMLServerStartingEvent evt)
    {
        new ForgeCommand(evt.getCommandDispatcher());
        ConfigCommand.register(evt.getCommandDispatcher());
    }

    public void serverStopping(FMLServerStoppingEvent evt)
    {
        WorldWorkerManager.clear();
    }

    @Override
    public CompoundNBT getDataForWriting(SaveFormat.LevelSave levelSave, IServerConfiguration serverInfo)
    {
        CompoundNBT forgeData = new CompoundNBT();
        CompoundNBT dims = new CompoundNBT();
        //TODO Dimensions
//        DimensionManager.writeRegistry(dims);
        if (!dims.isEmpty())
            forgeData.put("dims", dims);
        return forgeData;
    }

    @Override
    public void readData(SaveFormat.LevelSave levelSave, IServerConfiguration serverInfo, CompoundNBT tag)
    {
        //TODO Dimensions
//        if (tag.contains("dims", 10))
//            DimensionManager.readRegistry(tag.getCompound("dims"));
//        DimensionManager.processScheduledDeletions(levelSave);
    }

    public void mappingChanged(FMLModIdMappingEvent evt)
    {
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
            ForgeBlockTagsProvider blockTags = new ForgeBlockTagsProvider(gen);
            gen.addProvider(new ForgeBlockTagsProvider(gen));
            gen.addProvider(new ForgeItemTagsProvider(gen, blockTags));
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

    @SubscribeEvent //ModBus, can't use addListener due to nested genetics.
    public void registerRecipeSerialziers(RegistryEvent.Register<IRecipeSerializer<?>> event)
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

        event.getRegistry().register(new ConditionalRecipe.Serializer<IRecipe<?>>().setRegistryName(new ResourceLocation("forge", "conditional")));

    }
}
