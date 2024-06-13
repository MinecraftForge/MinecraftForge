/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import com.mojang.serialization.MapCodec;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.entity.ai.sensing.SensorType;
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
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.ingredients.IIngredientSerializer;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister.RegistryHolder;
import net.minecraftforge.registries.holdersets.HolderSetType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraftforge.common.world.StructureModifier;

/**
 * A class that exposes static references to all vanilla and Forge registries.
 * Created to have a central place to access the registries directly if modders need.
 * It is still advised that if you are registering things to use {@link RegisterEvent} or {@link DeferredRegister}, but queries and iterations can use this.
 */
public class ForgeRegistries {
    static { init(); } // This must be above the fields so we guarantee it's run before getRegistry is called. Yay static initializers
    private static <T> IForgeRegistry<T> active(ResourceKey<Registry<T>> key) {
        return RegistryManager.ACTIVE.getRegistry(key);
    }
    static final List<DeferredRegister<?>> registries = new ArrayList<>();
    private static final <T> RegistryHolder<T> registry(ResourceKey<Registry<T>> key, Supplier<RegistryBuilder<T>> factory) {
        var dr = DeferredRegister.create(key, key.location().getNamespace());
        registries.add(dr);
        return dr.makeRegistry(factory);
    }

    // Game objects
    public static final IForgeRegistry<Block> BLOCKS = active(Keys.BLOCKS);
    public static final IForgeRegistry<Fluid> FLUIDS = active(Keys.FLUIDS);
    public static final IForgeRegistry<Item> ITEMS = active(Keys.ITEMS);
    public static final IForgeRegistry<MobEffect> MOB_EFFECTS = active(Keys.MOB_EFFECTS);
    public static final IForgeRegistry<SoundEvent> SOUND_EVENTS = active(Keys.SOUND_EVENTS);
    public static final IForgeRegistry<Potion> POTIONS = active(Keys.POTIONS);
    public static final IForgeRegistry<EntityType<?>> ENTITY_TYPES = active(Keys.ENTITY_TYPES);
    public static final IForgeRegistry<BlockEntityType<?>> BLOCK_ENTITY_TYPES = active(Keys.BLOCK_ENTITY_TYPES);
    public static final IForgeRegistry<ParticleType<?>> PARTICLE_TYPES = active(Keys.PARTICLE_TYPES);
    public static final IForgeRegistry<MenuType<?>> MENU_TYPES = active(Keys.MENU_TYPES);
    public static final IForgeRegistry<PaintingVariant> PAINTING_VARIANTS = active(Keys.PAINTING_VARIANTS);
    public static final IForgeRegistry<RecipeType<?>> RECIPE_TYPES = active(Keys.RECIPE_TYPES);
    public static final IForgeRegistry<RecipeSerializer<?>> RECIPE_SERIALIZERS = active(Keys.RECIPE_SERIALIZERS);
    public static final IForgeRegistry<Attribute> ATTRIBUTES = active(Keys.ATTRIBUTES);
    public static final IForgeRegistry<StatType<?>> STAT_TYPES = active(Keys.STAT_TYPES);
    public static final IForgeRegistry<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = active(Keys.COMMAND_ARGUMENT_TYPES);

    // Villages
    public static final IForgeRegistry<VillagerProfession> VILLAGER_PROFESSIONS = active(Keys.VILLAGER_PROFESSIONS);
    public static final IForgeRegistry<PoiType> POI_TYPES = active(Keys.POI_TYPES);
    public static final IForgeRegistry<MemoryModuleType<?>> MEMORY_MODULE_TYPES = active(Keys.MEMORY_MODULE_TYPES);
    public static final IForgeRegistry<SensorType<?>> SENSOR_TYPES = active(Keys.SENSOR_TYPES);
    public static final IForgeRegistry<Schedule> SCHEDULES = active(Keys.SCHEDULES);
    public static final IForgeRegistry<Activity> ACTIVITIES = active(Keys.ACTIVITIES);

    // Worldgen
    public static final IForgeRegistry<WorldCarver<?>> WORLD_CARVERS = active(Keys.WORLD_CARVERS);
    public static final IForgeRegistry<Feature<?>> FEATURES = active(Keys.FEATURES);
    public static final IForgeRegistry<ChunkStatus> CHUNK_STATUS = active(Keys.CHUNK_STATUS);
    public static final IForgeRegistry<BlockStateProviderType<?>> BLOCK_STATE_PROVIDER_TYPES = active(Keys.BLOCK_STATE_PROVIDER_TYPES);
    public static final IForgeRegistry<FoliagePlacerType<?>> FOLIAGE_PLACER_TYPES = active(Keys.FOLIAGE_PLACER_TYPES);
    public static final IForgeRegistry<TreeDecoratorType<?>> TREE_DECORATOR_TYPES = active(Keys.TREE_DECORATOR_TYPES);

    // Dynamic/Data driven.
    public static final IForgeRegistry<Biome> BIOMES = active(Keys.BIOMES);

    // Custom forge registries
    public static final RegistryHolder<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = registry(Keys.ENTITY_DATA_SERIALIZERS, GameData::getDataSerializersRegistryBuilder);
    public static final RegistryHolder<MapCodec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIER_SERIALIZERS = registry(Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, GameData::getGLMSerializersRegistryBuilder);
    public static final RegistryHolder<MapCodec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS = registry(Keys.BIOME_MODIFIER_SERIALIZERS, GameData::makeUnsavedAndUnsynced);
    public static final RegistryHolder<MapCodec<? extends StructureModifier>> STRUCTURE_MODIFIER_SERIALIZERS = registry(Keys.STRUCTURE_MODIFIER_SERIALIZERS, GameData::makeUnsavedAndUnsynced);
    public static final RegistryHolder<FluidType> FLUID_TYPES = registry(Keys.FLUID_TYPES, GameData::getFluidTypeRegistryBuilder);
    public static final RegistryHolder<HolderSetType> HOLDER_SET_TYPES = registry(Keys.HOLDER_SET_TYPES, GameData::makeUnsavedAndUnsynced);
    public static final RegistryHolder<ItemDisplayContext> DISPLAY_CONTEXTS = registry(Keys.DISPLAY_CONTEXTS, GameData::getItemDisplayContextRegistryBuilder);
    public static final RegistryHolder<MapCodec<? extends ICondition>> CONDITION_SERIALIZERS = registry(Keys.CONDITION_SERIALIZERS, GameData::makeUnsavedAndUnsynced);
    public static final RegistryHolder<IIngredientSerializer<?>> INGREDIENT_SERIALIZERS = registry(Keys.INGREDIENT_SERIALIZERS, GameData::makeUnsavedAndUnsynced);

    public static final class Keys {
        //Vanilla
        public static final ResourceKey<Registry<Block>>  BLOCKS  = key("block");
        public static final ResourceKey<Registry<Fluid>>  FLUIDS  = key("fluid");
        public static final ResourceKey<Registry<Item>>   ITEMS   = key("item");
        public static final ResourceKey<Registry<MobEffect>> MOB_EFFECTS = key("mob_effect");
        public static final ResourceKey<Registry<Potion>> POTIONS = key("potion");
        public static final ResourceKey<Registry<Attribute>> ATTRIBUTES = key("attribute");
        public static final ResourceKey<Registry<StatType<?>>> STAT_TYPES = key("stat_type");
        public static final ResourceKey<Registry<ArgumentTypeInfo<?, ?>>> COMMAND_ARGUMENT_TYPES = key("command_argument_type");
        public static final ResourceKey<Registry<SoundEvent>> SOUND_EVENTS = key("sound_event");
        public static final ResourceKey<Registry<EntityType<?>>> ENTITY_TYPES = key("entity_type");
        public static final ResourceKey<Registry<PaintingVariant>> PAINTING_VARIANTS = key("painting_variant");
        public static final ResourceKey<Registry<ParticleType<?>>> PARTICLE_TYPES = key("particle_type");
        public static final ResourceKey<Registry<MenuType<?>>> MENU_TYPES = key("menu");
        public static final ResourceKey<Registry<BlockEntityType<?>>> BLOCK_ENTITY_TYPES = key("block_entity_type");
        public static final ResourceKey<Registry<RecipeType<?>>> RECIPE_TYPES = key("recipe_type");
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
        public static final ResourceKey<Registry<BlockStateProviderType<?>>> BLOCK_STATE_PROVIDER_TYPES = key("worldgen/block_state_provider_type");
        public static final ResourceKey<Registry<FoliagePlacerType<?>>> FOLIAGE_PLACER_TYPES = key("worldgen/foliage_placer_type");
        public static final ResourceKey<Registry<TreeDecoratorType<?>>> TREE_DECORATOR_TYPES = key("worldgen/tree_decorator_type");

        // Vanilla Dynamic
        public static final ResourceKey<Registry<Biome>> BIOMES = key("worldgen/biome");

        // Forge
        public static final ResourceKey<Registry<EntityDataSerializer<?>>> ENTITY_DATA_SERIALIZERS = key("forge:entity_data_serializers");
        public static final ResourceKey<Registry<MapCodec<? extends IGlobalLootModifier>>> GLOBAL_LOOT_MODIFIER_SERIALIZERS = key("forge:global_loot_modifier_serializers");
        public static final ResourceKey<Registry<MapCodec<? extends BiomeModifier>>> BIOME_MODIFIER_SERIALIZERS = key("forge:biome_modifier_serializers");
        public static final ResourceKey<Registry<MapCodec<? extends StructureModifier>>> STRUCTURE_MODIFIER_SERIALIZERS = key("forge:structure_modifier_serializers");
        public static final ResourceKey<Registry<FluidType>> FLUID_TYPES = key("forge:fluid_type");
        public static final ResourceKey<Registry<HolderSetType>> HOLDER_SET_TYPES = key("forge:holder_set_type");
        public static final ResourceKey<Registry<ItemDisplayContext>> DISPLAY_CONTEXTS = key("forge:display_contexts");
        public static final ResourceKey<Registry<MapCodec<? extends ICondition>>> CONDITION_SERIALIZERS = key("forge:condition_codecs");
        public static final ResourceKey<Registry<IIngredientSerializer<?>>> INGREDIENT_SERIALIZERS = key("forge:ingredient_serializers");

        // Forge Dynamic
        public static final ResourceKey<Registry<BiomeModifier>> BIOME_MODIFIERS = key("forge:biome_modifier");
        public static final ResourceKey<Registry<StructureModifier>> STRUCTURE_MODIFIERS = key("forge:structure_modifier");

        private static <T> ResourceKey<Registry<T>> key(String name) {
            return ResourceKey.createRegistryKey(ResourceLocation.parse(name));
        }

        private static void init() {}
    }

    /**
     * This function is just to make sure static initializers in other classes have run and setup their registries before we query them.
     */
    private static void init() {
        Keys.init();
        GameData.init();
        Bootstrap.bootStrap();
        Tags.init();
    }
}