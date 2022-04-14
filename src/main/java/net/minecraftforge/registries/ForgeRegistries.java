/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.decoration.Motive;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.server.Bootstrap;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.stats.StatType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.world.ForgeWorldPreset;

import java.util.function.Supplier;

/**
 * A class that exposes static references to all vanilla and Forge registries.
 * Created to have a central place to access the registries directly if modders need.
 * It is still advised that if you are registering things to use {@link net.minecraftforge.event.RegistryEvent.Register} or {@link net.minecraftforge.registries.DeferredRegister}, but queries and iterations can use this.
 */
public class ForgeRegistries
{
    static { init(); } // This must be above the fields so we guarantee it's run before getRegistry is called. Yay static inializers

    // Game objects
    public static final IForgeRegistry<Block> BLOCKS = RegistryManager.ACTIVE.getRegistry(Keys.BLOCKS);
    public static final IForgeRegistry<Fluid> FLUIDS = RegistryManager.ACTIVE.getRegistry(Keys.FLUIDS);
    public static final IForgeRegistry<Item> ITEMS = RegistryManager.ACTIVE.getRegistry(Keys.ITEMS);
    public static final IForgeRegistry<MobEffect> MOB_EFFECTS = RegistryManager.ACTIVE.getRegistry(Keys.MOB_EFFECTS);
    public static final IForgeRegistry<SoundEvent> SOUND_EVENTS = RegistryManager.ACTIVE.getRegistry(Keys.SOUND_EVENTS);
    public static final IForgeRegistry<Potion> POTIONS = RegistryManager.ACTIVE.getRegistry(Keys.POTIONS);
    public static final IForgeRegistry<Enchantment> ENCHANTMENTS = RegistryManager.ACTIVE.getRegistry(Keys.ENCHANTMENTS);
    public static final IForgeRegistry<EntityType<?>> ENTITIES = RegistryManager.ACTIVE.getRegistry(Keys.ENTITY_TYPES);
    public static final IForgeRegistry<BlockEntityType<?>> BLOCK_ENTITIES = RegistryManager.ACTIVE.getRegistry(Keys.BLOCK_ENTITY_TYPES);
    public static final IForgeRegistry<ParticleType<?>> PARTICLE_TYPES = RegistryManager.ACTIVE.getRegistry(Keys.PARTICLE_TYPES);
    public static final IForgeRegistry<MenuType<?>> CONTAINERS = RegistryManager.ACTIVE.getRegistry(Keys.CONTAINER_TYPES);
    public static final IForgeRegistry<Motive> PAINTING_TYPES = RegistryManager.ACTIVE.getRegistry(Keys.PAINTING_TYPES);
    public static final IForgeRegistry<RecipeSerializer<?>> RECIPE_SERIALIZERS = RegistryManager.ACTIVE.getRegistry(Keys.RECIPE_SERIALIZERS);
    public static final IForgeRegistry<Attribute> ATTRIBUTES = RegistryManager.ACTIVE.getRegistry(Keys.ATTRIBUTES);
    public static final IForgeRegistry<StatType<?>> STAT_TYPES = RegistryManager.ACTIVE.getRegistry(Keys.STAT_TYPES);

    // Villages
    public static final IForgeRegistry<VillagerProfession> PROFESSIONS = RegistryManager.ACTIVE.getRegistry(Keys.VILLAGER_PROFESSIONS);
    public static final IForgeRegistry<PoiType> POI_TYPES = RegistryManager.ACTIVE.getRegistry(Keys.POI_TYPES);
    public static final IForgeRegistry<MemoryModuleType<?>> MEMORY_MODULE_TYPES = RegistryManager.ACTIVE.getRegistry(Keys.MEMORY_MODULE_TYPES);
    public static final IForgeRegistry<SensorType<?>> SENSOR_TYPES = RegistryManager.ACTIVE.getRegistry(Keys.SENSOR_TYPES);
    public static final IForgeRegistry<Schedule> SCHEDULES = RegistryManager.ACTIVE.getRegistry(Keys.SCHEDULES);
    public static final IForgeRegistry<Activity> ACTIVITIES = RegistryManager.ACTIVE.getRegistry(Keys.ACTIVITIES);

    // Worldgen
    public static final IForgeRegistry<WorldCarver<?>> WORLD_CARVERS = RegistryManager.ACTIVE.getRegistry(Keys.WORLD_CARVERS);
    public static final IForgeRegistry<Feature<?>> FEATURES = RegistryManager.ACTIVE.getRegistry(Keys.FEATURES);
    public static final IForgeRegistry<ChunkStatus> CHUNK_STATUS = RegistryManager.ACTIVE.getRegistry(Keys.CHUNK_STATUS);
    public static final IForgeRegistry<StructureFeature<?>> STRUCTURE_FEATURES = RegistryManager.ACTIVE.getRegistry(Keys.STRUCTURE_FEATURES);
    public static final IForgeRegistry<BlockStateProviderType<?>> BLOCK_STATE_PROVIDER_TYPES = RegistryManager.ACTIVE.getRegistry(Keys.BLOCK_STATE_PROVIDER_TYPES);
    public static final IForgeRegistry<FoliagePlacerType<?>> FOLIAGE_PLACER_TYPES = RegistryManager.ACTIVE.getRegistry(Keys.FOLIAGE_PLACER_TYPES);
    public static final IForgeRegistry<TreeDecoratorType<?>> TREE_DECORATOR_TYPES = RegistryManager.ACTIVE.getRegistry(Keys.TREE_DECORATOR_TYPES);

    // Dynamic/Data driven.
    public static final IForgeRegistry<Biome> BIOMES = RegistryManager.ACTIVE.getRegistry(Keys.BIOMES);

    // Custom forge registries
    static final DeferredRegister<DataSerializerEntry> DEFERRED_DATA_SERIALIZERS = DeferredRegister.create(Keys.DATA_SERIALIZERS, Keys.DATA_SERIALIZERS.location().getNamespace());
    public static final Supplier<IForgeRegistry<DataSerializerEntry>> DATA_SERIALIZERS = DEFERRED_DATA_SERIALIZERS.makeRegistry(DataSerializerEntry.class, GameData::getDataSerializersRegistryBuilder);
    static final DeferredRegister<GlobalLootModifierSerializer<?>> DEFERRED_LOOT_MODIFIER_SERIALIZERS = DeferredRegister.create(Keys.LOOT_MODIFIER_SERIALIZERS, Keys.LOOT_MODIFIER_SERIALIZERS.location().getNamespace());
    public static final Supplier<IForgeRegistry<GlobalLootModifierSerializer<?>>> LOOT_MODIFIER_SERIALIZERS = DEFERRED_LOOT_MODIFIER_SERIALIZERS.makeRegistry(GameData.c(GlobalLootModifierSerializer.class), GameData::getGLMSerializersRegistryBuilder);
    static final DeferredRegister<ForgeWorldPreset> DEFERRED_WORLD_TYPES = DeferredRegister.create(Keys.WORLD_TYPES, Keys.WORLD_TYPES.location().getNamespace());
    public static final Supplier<IForgeRegistry<ForgeWorldPreset>> WORLD_TYPES = DEFERRED_WORLD_TYPES.makeRegistry(ForgeWorldPreset.class, GameData::getWorldTypesRegistryBuilder);

    public static final class Keys {
        //Vanilla
        public static final ResourceKey<Registry<Block>>  BLOCKS  = key("block");
        public static final ResourceKey<Registry<Fluid>>  FLUIDS  = key("fluid");
        public static final ResourceKey<Registry<Item>>   ITEMS   = key("item");
        public static final ResourceKey<Registry<MobEffect>> MOB_EFFECTS = key("mob_effect");
        public static final ResourceKey<Registry<Potion>> POTIONS = key("potion");
        public static final ResourceKey<Registry<Attribute>> ATTRIBUTES = key("attribute");
        public static final ResourceKey<Registry<StatType<?>>> STAT_TYPES = key("stat_type");
        public static final ResourceKey<Registry<SoundEvent>> SOUND_EVENTS = key("sound_event");
        public static final ResourceKey<Registry<Enchantment>> ENCHANTMENTS = key("enchantment");
        public static final ResourceKey<Registry<EntityType<?>>> ENTITY_TYPES = key("entity_type");
        public static final ResourceKey<Registry<Motive>> PAINTING_TYPES = key("motive");
        public static final ResourceKey<Registry<ParticleType<?>>> PARTICLE_TYPES = key("particle_type");
        public static final ResourceKey<Registry<MenuType<?>>> CONTAINER_TYPES = key("menu");
        public static final ResourceKey<Registry<BlockEntityType<?>>> BLOCK_ENTITY_TYPES = key("block_entity_type");
        public static final ResourceKey<Registry<RecipeSerializer<?>>> RECIPE_SERIALIZERS = key("recipe_serializer");
        public static final ResourceKey<Registry<VillagerProfession>> VILLAGER_PROFESSIONS = key("villager_profession");
        public static final ResourceKey<Registry<PoiType>> POI_TYPES = key("point_of_interest_type");
        public static final ResourceKey<Registry<MemoryModuleType<?>>> MEMORY_MODULE_TYPES = key("memory_module_type");
        public static final ResourceKey<Registry<SensorType<?>>> SENSOR_TYPES = key("sensor_type");
        public static final ResourceKey<Registry<Schedule>> SCHEDULES = key("schedule");
        public static final ResourceKey<Registry<Activity>> ACTIVITIES = key("activity");
        public static final ResourceKey<Registry<WorldCarver<?>>> WORLD_CARVERS = key("worldgen/carver");
        public static final ResourceKey<Registry<Feature<?>>> FEATURES = key("worldgen/feature");
        public static final ResourceKey<Registry<ChunkStatus>> CHUNK_STATUS = key("chunk_status");
        public static final ResourceKey<Registry<StructureFeature<?>>> STRUCTURE_FEATURES = key("worldgen/structure_feature");
        public static final ResourceKey<Registry<BlockStateProviderType<?>>> BLOCK_STATE_PROVIDER_TYPES = key("worldgen/block_state_provider_type");
        public static final ResourceKey<Registry<FoliagePlacerType<?>>> FOLIAGE_PLACER_TYPES = key("worldgen/foliage_placer_type");
        public static final ResourceKey<Registry<TreeDecoratorType<?>>> TREE_DECORATOR_TYPES = key("worldgen/tree_decorator_type");

        // Vanilla Dynamic
        public static final ResourceKey<Registry<Biome>> BIOMES = key("worldgen/biome");

        // Forge
        public static final ResourceKey<Registry<DataSerializerEntry>> DATA_SERIALIZERS = key("data_serializers");
        public static final ResourceKey<Registry<GlobalLootModifierSerializer<?>>> LOOT_MODIFIER_SERIALIZERS = key("forge:loot_modifier_serializers");
        public static final ResourceKey<Registry<ForgeWorldPreset>> WORLD_TYPES = key("forge:world_types");

        private static <T> ResourceKey<Registry<T>> key(String name)
        {
            return ResourceKey.createRegistryKey(new ResourceLocation(name));
        }
        private static void init() {}
    }

    /**
     * This function is just to make sure static initializers in other classes have run and setup their registries before we query them.
     */
    private static void init()
    {
        Keys.init();
        GameData.init();
        Bootstrap.bootStrap();
        Tags.init();
    }
}
