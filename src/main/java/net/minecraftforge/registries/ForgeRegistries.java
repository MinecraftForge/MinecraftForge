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

package net.minecraftforge.registries;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.schedule.Schedule;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.item.PaintingType;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.registry.Bootstrap;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.particles.ParticleType;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Potion;
import net.minecraft.stats.StatType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.blockplacer.BlockPlacerType;
import net.minecraft.world.gen.blockstateprovider.BlockStateProviderType;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.foliageplacer.FoliagePlacerType;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.world.ForgeWorldType;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * A class that exposes static references to all vanilla and Forge registries.
 * Created to have a central place to access the registries directly if modders need.
 * It is still advised that if you are registering things to go through {@link GameRegistry} register methods, but queries and iterations can use this.
 */
public class ForgeRegistries
{
    static { init(); } // This must be above the fields so we guarantee it's run before findRegistry is called. Yay static inializers

    // Game objects
    public static final IForgeRegistry<Block> BLOCKS = RegistryManager.ACTIVE.getRegistry(Block.class);
    public static final IForgeRegistry<Fluid> FLUIDS = RegistryManager.ACTIVE.getRegistry(Fluid.class);
    public static final IForgeRegistry<Item> ITEMS = RegistryManager.ACTIVE.getRegistry(Item.class);
    public static final IForgeRegistry<Effect> POTIONS = RegistryManager.ACTIVE.getRegistry(Effect.class);
    public static final IForgeRegistry<SoundEvent> SOUND_EVENTS = RegistryManager.ACTIVE.getRegistry(SoundEvent.class);
    public static final IForgeRegistry<Potion> POTION_TYPES = RegistryManager.ACTIVE.getRegistry(Potion.class);
    public static final IForgeRegistry<Enchantment> ENCHANTMENTS = RegistryManager.ACTIVE.getRegistry(Enchantment.class);
    public static final IForgeRegistry<EntityType<?>> ENTITIES = RegistryManager.ACTIVE.getRegistry(EntityType.class);
    public static final IForgeRegistry<TileEntityType<?>> TILE_ENTITIES = RegistryManager.ACTIVE.getRegistry(TileEntityType.class);
    public static final IForgeRegistry<ParticleType<?>> PARTICLE_TYPES = RegistryManager.ACTIVE.getRegistry(ParticleType.class);
    public static final IForgeRegistry<ContainerType<?>> CONTAINERS = RegistryManager.ACTIVE.getRegistry(ContainerType.class);
    public static final IForgeRegistry<PaintingType> PAINTING_TYPES = RegistryManager.ACTIVE.getRegistry(PaintingType.class);
    public static final IForgeRegistry<IRecipeSerializer<?>> RECIPE_SERIALIZERS = RegistryManager.ACTIVE.getRegistry(IRecipeSerializer.class);
    public static final IForgeRegistry<Attribute> ATTRIBUTES = RegistryManager.ACTIVE.getRegistry(Attribute.class);
    public static final IForgeRegistry<StatType<?>> STAT_TYPES = RegistryManager.ACTIVE.getRegistry(StatType.class);

    // Villages
    public static final IForgeRegistry<VillagerProfession> PROFESSIONS = RegistryManager.ACTIVE.getRegistry(VillagerProfession.class);
    public static final IForgeRegistry<PointOfInterestType> POI_TYPES = RegistryManager.ACTIVE.getRegistry(PointOfInterestType.class);
    public static final IForgeRegistry<MemoryModuleType<?>> MEMORY_MODULE_TYPES = RegistryManager.ACTIVE.getRegistry(MemoryModuleType.class);
    public static final IForgeRegistry<SensorType<?>> SENSOR_TYPES = RegistryManager.ACTIVE.getRegistry(SensorType.class);
    public static final IForgeRegistry<Schedule> SCHEDULES = RegistryManager.ACTIVE.getRegistry(Schedule.class);
    public static final IForgeRegistry<Activity> ACTIVITIES = RegistryManager.ACTIVE.getRegistry(Activity.class);

    // Worldgen
    public static final IForgeRegistry<WorldCarver<?>> WORLD_CARVERS = RegistryManager.ACTIVE.getRegistry(WorldCarver.class);
    public static final IForgeRegistry<SurfaceBuilder<?>> SURFACE_BUILDERS = RegistryManager.ACTIVE.getRegistry(SurfaceBuilder.class);
    public static final IForgeRegistry<Feature<?>> FEATURES = RegistryManager.ACTIVE.getRegistry(Feature.class);
    public static final IForgeRegistry<Placement<?>> DECORATORS = RegistryManager.ACTIVE.getRegistry(Placement.class);
    public static final IForgeRegistry<ChunkStatus> CHUNK_STATUS = RegistryManager.ACTIVE.getRegistry(ChunkStatus.class);
    public static final IForgeRegistry<Structure<?>> STRUCTURE_FEATURES = RegistryManager.ACTIVE.getRegistry(Structure.class);
    public static final IForgeRegistry<BlockStateProviderType<?>> BLOCK_STATE_PROVIDER_TYPES = RegistryManager.ACTIVE.getRegistry(BlockStateProviderType.class);
    public static final IForgeRegistry<BlockPlacerType<?>> BLOCK_PLACER_TYPES = RegistryManager.ACTIVE.getRegistry(BlockPlacerType.class);
    public static final IForgeRegistry<FoliagePlacerType<?>> FOLIAGE_PLACER_TYPES = RegistryManager.ACTIVE.getRegistry(FoliagePlacerType.class);
    public static final IForgeRegistry<TreeDecoratorType<?>> TREE_DECORATOR_TYPES = RegistryManager.ACTIVE.getRegistry(TreeDecoratorType.class);

    // Dynamic/Data driven.
    public static final IForgeRegistry<Biome> BIOMES = RegistryManager.ACTIVE.getRegistry(Keys.BIOMES);

    // Custom forge registries
    public static final IForgeRegistry<DataSerializerEntry> DATA_SERIALIZERS = RegistryManager.ACTIVE.getRegistry(DataSerializerEntry.class);
    public static final IForgeRegistry<GlobalLootModifierSerializer<?>> LOOT_MODIFIER_SERIALIZERS = RegistryManager.ACTIVE.getRegistry(GlobalLootModifierSerializer.class);
    public static final IForgeRegistry<ForgeWorldType> WORLD_TYPES = RegistryManager.ACTIVE.getRegistry(ForgeWorldType.class);

    public static final class Keys {
        //Vanilla
        public static final RegistryKey<Registry<Block>>  BLOCKS  = key("block");
        public static final RegistryKey<Registry<Fluid>>  FLUIDS  = key("fluid");
        public static final RegistryKey<Registry<Item>>   ITEMS   = key("item");
        public static final RegistryKey<Registry<Effect>> EFFECTS = key("mob_effect");
        public static final RegistryKey<Registry<Potion>> POTIONS = key("potion");
        public static final RegistryKey<Registry<Attribute>> ATTRIBUTES = key("attribute");
        public static final RegistryKey<Registry<StatType<?>>> STAT_TYPES = key("stat_type");
        public static final RegistryKey<Registry<SoundEvent>> SOUND_EVENTS = key("sound_event");
        public static final RegistryKey<Registry<Enchantment>> ENCHANTMENTS = key("enchantment");
        public static final RegistryKey<Registry<EntityType<?>>> ENTITY_TYPES = key("entity_type");
        public static final RegistryKey<Registry<PaintingType>> PAINTING_TYPES = key("motive");
        public static final RegistryKey<Registry<ParticleType<?>>> PARTICLE_TYPES = key("particle_type");
        public static final RegistryKey<Registry<ContainerType<?>>> CONTAINER_TYPES = key("menu");
        public static final RegistryKey<Registry<TileEntityType<?>>> TILE_ENTITY_TYPES = key("block_entity_type");
        public static final RegistryKey<Registry<IRecipeSerializer<?>>> RECIPE_SERIALIZERS = key("recipe_serializer");
        public static final RegistryKey<Registry<VillagerProfession>> VILLAGER_PROFESSIONS = key("villager_profession");
        public static final RegistryKey<Registry<PointOfInterestType>> POI_TYPES = key("point_of_interest_type");
        public static final RegistryKey<Registry<MemoryModuleType<?>>> MEMORY_MODULE_TYPES = key("memory_module_type");
        public static final RegistryKey<Registry<SensorType<?>>> SENSOR_TYPES = key("sensor_type");
        public static final RegistryKey<Registry<Schedule>> SCHEDULES = key("schedule");
        public static final RegistryKey<Registry<Activity>> ACTIVITIES = key("activity");
        public static final RegistryKey<Registry<WorldCarver<?>>> WORLD_CARVERS = key("worldgen/carver");
        public static final RegistryKey<Registry<SurfaceBuilder<?>>> SURFACE_BUILDERS = key("worldgen/surface_builder");
        public static final RegistryKey<Registry<Feature<?>>> FEATURES = key("worldgen/feature");
        public static final RegistryKey<Registry<Placement<?>>> DECORATORS = key("worldgen/decorator");
        public static final RegistryKey<Registry<ChunkStatus>> CHUNK_STATUS = key("chunk_status");
        public static final RegistryKey<Registry<Structure<?>>> STRUCTURE_FEATURES = key("worldgen/structure_feature");
        public static final RegistryKey<Registry<BlockStateProviderType<?>>> BLOCK_STATE_PROVIDER_TYPES = key("worldgen/block_state_provider_type");
        public static final RegistryKey<Registry<BlockPlacerType<?>>> BLOCK_PLACER_TYPES = key("worldgen/block_placer_type");
        public static final RegistryKey<Registry<FoliagePlacerType<?>>> FOLIAGE_PLACER_TYPES = key("worldgen/foliage_placer_type");
        public static final RegistryKey<Registry<TreeDecoratorType<?>>> TREE_DECORATOR_TYPES = key("worldgen/tree_decorator_type");

        // Vanilla Dynamic
        public static final RegistryKey<Registry<Biome>> BIOMES = key("worldgen/biome");

        //Forge
        public static final RegistryKey<Registry<DataSerializerEntry>> DATA_SERIALIZERS = key("data_serializers");
        public static final RegistryKey<Registry<GlobalLootModifierSerializer<?>>> LOOT_MODIFIER_SERIALIZERS = key("forge:loot_modifier_serializers");
        public static final RegistryKey<Registry<ForgeWorldType>> WORLD_TYPES = key("forge:world_types");

        private static <T> RegistryKey<Registry<T>> key(String name)
        {
            return RegistryKey.getOrCreateRootKey(new ResourceLocation(name));
        }
        private static void init() {}
    }

    /**
     * This function is just to make sure static inializers in other classes have run and setup their registries before we query them.
     */
    private static void init()
    {
        Keys.init();
        GameData.init();
        Bootstrap.register();
        Tags.init();
    }
}
