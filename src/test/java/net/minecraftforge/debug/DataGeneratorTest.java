/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug;

import static net.minecraftforge.debug.DataGeneratorTest.MODID;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraftforge.common.data.SoundDefinition;
import net.minecraftforge.common.data.SoundDefinitionsProvider;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.jline.utils.InputStreamReader;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.block.model.BlockModel.GuiLight;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder.Perspective;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.common.crafting.ConditionalAdvancement;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;

@SuppressWarnings("deprecation")
@Mod(MODID)
@Mod.EventBusSubscriber(bus = Bus.MOD)
public class DataGeneratorTest
{
    static final String MODID = "data_gen_test";

    private static Gson GSON = null;

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event)
    {
        GSON = new GsonBuilder()
                .registerTypeAdapter(Variant.class, new Variant.Deserializer())
                .registerTypeAdapter(ItemTransforms.class, new ItemTransforms.Deserializer())
                .registerTypeAdapter(ItemTransform.class, new ItemTransform.Deserializer())
                .create();

        DataGenerator gen = event.getGenerator();

        if (event.includeClient())
        {
            gen.addProvider(new Lang(gen));
            // Let blockstate provider see generated item models by passing its existing file helper
            ItemModelProvider itemModels = new ItemModels(gen, event.getExistingFileHelper());
            gen.addProvider(itemModels);
            gen.addProvider(new BlockStates(gen, itemModels.existingFileHelper));
            gen.addProvider(new SoundDefinitions(gen, event.getExistingFileHelper()));
        }
        if (event.includeServer())
        {
            gen.addProvider(new Recipes(gen));
            gen.addProvider(new Tags(gen, event.getExistingFileHelper()));
            gen.addProvider(new Advancements(gen, event.getExistingFileHelper()));
        }
    }

    public static class Recipes extends RecipeProvider implements IConditionBuilder
    {
        public Recipes(DataGenerator gen)
        {
            super(gen);
        }

        @Override
        protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer)
        {
            ResourceLocation ID = new ResourceLocation("data_gen_test", "conditional");

            ConditionalRecipe.builder()
            .addCondition(
                and(
                    not(modLoaded("minecraft")),
                    itemExists("minecraft", "dirt"),
                    FALSE()
                )
            )
            .addRecipe(
                ShapedRecipeBuilder.shaped(Blocks.DIAMOND_BLOCK, 64)
                .pattern("XXX")
                .pattern("XXX")
                .pattern("XXX")
                .define('X', Blocks.DIRT)
                .group("")
                .unlockedBy("has_dirt", has(Blocks.DIRT)) // DUMMY: Necessary, but not used when a custom advancement is provided through setAdvancement
                ::save
            )
            .setAdvancement(ID,
                ConditionalAdvancement.builder()
                .addCondition(
                    and(
                        not(modLoaded("minecraft")),
                        itemExists("minecraft", "dirt"),
                        FALSE()
                    )
                )
                .addAdvancement(
                    Advancement.Builder.advancement()
                    .parent(new ResourceLocation("minecraft", "root"))
                    .display(Blocks.DIAMOND_BLOCK,
                        new TextComponent("Dirt2Diamonds"),
                        new TextComponent("The BEST crafting recipe in the game!"),
                        null, FrameType.TASK, false, false, false
                    )
                    .rewards(AdvancementRewards.Builder.recipe(ID))
                    .addCriterion("has_dirt", has(Blocks.DIRT))
                )
            )
            .build(consumer, ID);

            ConditionalRecipe.builder()
                    .addCondition(
                            not(
                                and(
                                        not(modLoaded("minecraft")),
                                        itemExists("minecraft", "dirt"),
                                        FALSE()
                                )
                            )
                    )
                    .addRecipe(
                            ShapedRecipeBuilder.shaped(Blocks.DIAMOND_BLOCK, 64)
                                    .pattern("XXX")
                                    .pattern("XXX")
                                    .pattern("XXX")
                                    .define('X', Blocks.DIRT)
                                    .group("")
                                    .unlockedBy("has_dirt", has(Blocks.DIRT))
                                    ::save
                    )
                    .generateAdvancement()
                    .build(consumer, new ResourceLocation("data_gen_test", "conditional2"));
        }
    }

    public static class SoundDefinitions extends SoundDefinitionsProvider
    {
        private static final Logger LOGGER = LogManager.getLogger();
        private final ExistingFileHelper helper;

        public SoundDefinitions(final DataGenerator generator, final ExistingFileHelper helper)
        {
            super(generator, MODID, helper);
            this.helper = helper;
        }

        @Override
        public void registerSounds()
        {
            // ambient.underwater.loop.additions
            this.add(SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS, definition().with(
                    sound("ambient/underwater/additions/bubbles1"),
                    sound("ambient/underwater/additions/bubbles2"),
                    sound("ambient/underwater/additions/bubbles3"),
                    sound("ambient/underwater/additions/bubbles4"),
                    sound("ambient/underwater/additions/bubbles5"),
                    sound("ambient/underwater/additions/bubbles6"),
                    sound("ambient/underwater/additions/water1"),
                    sound("ambient/underwater/additions/water2")
            ));

            //ambient.underwater.loop.additions.ultra_rare
            this.add(SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS_ULTRA_RARE, definition().with(
                    sound("ambient/underwater/additions/animal2"),
                    sound("ambient/underwater/additions/dark1"),
                    sound("ambient/underwater/additions/dark2").volume(0.7),
                    sound("ambient/underwater/additions/dark3"),
                    sound("ambient/underwater/additions/dark4")
            ));

            //block.lava.ambient
            this.add(SoundEvents.LAVA_AMBIENT, definition().with(sound("liquid/lava")).subtitle("subtitles.block.lava.ambient"));

            //entity.dolphin.ambient_water
            this.add(SoundEvents.DOLPHIN_AMBIENT_WATER, definition().with(
                    sound("mob/dolphin/idle_water1").volume(0.8),
                    sound("mob/dolphin/idle_water2"),
                    sound("mob/dolphin/idle_water3"),
                    sound("mob/dolphin/idle_water4"),
                    sound("mob/dolphin/idle_water5"),
                    sound("mob/dolphin/idle_water6"),
                    sound("mob/dolphin/idle_water7").volume(0.75),
                    sound("mob/dolphin/idle_water8").volume(0.75),
                    sound("mob/dolphin/idle_water9"),
                    sound("mob/dolphin/idle_water10").volume(0.8)
            ).subtitle("subtitles.entity.dolphin.ambient_water"));

            //entity.parrot.imitate.drowned
            this.add(SoundEvents.PARROT_IMITATE_DROWNED, definition().with(
                    sound("entity.drowned.ambient", SoundDefinition.SoundType.EVENT).pitch(1.8).volume(0.6)
            ).subtitle("subtitles.entity.parrot.imitate.drowned"));

            //item.trident.return
            this.add(SoundEvents.TRIDENT_RETURN, definition().with(
                    sound("item/trident/return1").volume(0.8),
                    sound("item/trident/return2").volume(0.8),
                    sound("item/trident/return2").pitch(0.8).volume(0.8),
                    sound("item/trident/return2").pitch(1.2).volume(0.8),
                    sound("item/trident/return2").pitch(1.2).volume(0.8),
                    sound("item/trident/return3").volume(0.8),
                    sound("item/trident/return3").pitch(0.8).volume(0.8),
                    sound("item/trident/return3").pitch(0.8).volume(0.8),
                    sound("item/trident/return3").pitch(1.2).volume(0.8)
            ).subtitle("subtitles.item.trident.return"));

            //music_disc.blocks
            this.add(SoundEvents.MUSIC_DISC_BLOCKS, definition().with(sound("records/blocks").stream()));
        }

        @Override
        public void run(HashCache cache) throws IOException
        {
            super.run(cache);
            test();
        }

        private void test() throws IOException
        {
            final JsonObject generated;
            try
            {
                generated = reflect();
            }
            catch (ReflectiveOperationException e)
            {
                throw new RuntimeException("Unable to test for errors due to reflection error", e);
            }
            final JsonObject actual = GSON.fromJson(
                    new InputStreamReader(this.helper.getResource(new ResourceLocation("sounds.json"), PackType.CLIENT_RESOURCES).getInputStream()),
                    JsonObject.class
            );

            final JsonObject filtered = new JsonObject();
            generated.entrySet().forEach(it -> filtered.add(it.getKey(), Optional.ofNullable(actual.get(it.getKey())).orElseGet(JsonNull::new)));

            final List<String> errors = this.compareObjects(filtered, generated);

            if (!errors.isEmpty()) {
                LOGGER.error("Found {} discrepancies between generated and vanilla sound definitions: ", errors.size());
                for (String s : errors) {
                    LOGGER.error("    {}", s);
                }
                throw new RuntimeException("Generated sounds.json differed from vanilla equivalent, check above errors.");
            }
        }

        private JsonObject reflect() throws ReflectiveOperationException
        {
            // This is not supposed to be done by client code, so we just run with reflection to avoid exposing
            // something that shouldn't be exposed in the first place
            final Method mapToJson = this.getClass().getSuperclass().getDeclaredMethod("mapToJson", Map.class);
            mapToJson.setAccessible(true);
            final Field map = this.getClass().getSuperclass().getDeclaredField("sounds");
            map.setAccessible(true);
            //noinspection JavaReflectionInvocation
            return (JsonObject) mapToJson.invoke(this, map.get(this));
        }

        private List<String> compareAndGatherErrors(final Triple<String, JsonElement, JsonElement> triple)
        {
            return this.compare(triple.getMiddle(), triple.getRight()).stream().map(it -> triple.getLeft() + ": " + it).collect(Collectors.toList());
        }

        private List<String> compare(final JsonElement vanilla, @Nullable final JsonElement generated)
        {
            if (generated == null)
            {
                return Collections.singletonList("vanilla element has no generated counterpart");
            }
            else if (vanilla.isJsonPrimitive())
            {
                return this.comparePrimitives(vanilla.getAsJsonPrimitive(), generated);
            }
            else if (vanilla.isJsonObject())
            {
                return this.compareObjects(vanilla.getAsJsonObject(), generated);
            }
            else if (vanilla.isJsonArray())
            {
                return this.compareArrays(vanilla.getAsJsonArray(), generated);
            }
            else if (vanilla.isJsonNull() && !generated.isJsonNull())
            {
                return Collections.singletonList("null value in vanilla doesn't match non-null value in generated");
            }
            throw new RuntimeException("Unable to match " + vanilla + " to any JSON type");
        }

        private List<String> comparePrimitives(final JsonPrimitive vanilla, final JsonElement generated)
        {
            if (!generated.isJsonPrimitive()) return Collections.singletonList("Primitive in vanilla isn't matched by generated " + generated);

            final JsonPrimitive generatedPrimitive = generated.getAsJsonPrimitive();

            if (vanilla.isBoolean())
            {
                if (!generatedPrimitive.isBoolean()) return Collections.singletonList("Boolean in vanilla isn't matched by non-boolean " + generatedPrimitive);

                if (vanilla.getAsBoolean() != generated.getAsBoolean())
                {
                    return Collections.singletonList("Boolean '" + vanilla.getAsBoolean() + "' does not match generated '" + generatedPrimitive.getAsBoolean() + "'");
                }
            }
            else if (vanilla.isNumber())
            {
                if (!generatedPrimitive.isNumber()) return Collections.singletonList("Number in vanilla isn't matched by non-number " + generatedPrimitive);

                // Handle numbers via big decimal so we are sure there isn't any sort of errors due to float/long
                final BigDecimal vanillaNumber = vanilla.getAsBigDecimal();
                final BigDecimal generatedNumber = vanilla.getAsBigDecimal();

                if (vanillaNumber.compareTo(generatedNumber) != 0)
                {
                    return Collections.singletonList("Number '" + vanillaNumber + "' does not match generated '" + generatedNumber + "'");
                }
            }
            else if (vanilla.isString())
            {
                if (!generatedPrimitive.isString()) return Collections.singletonList("String in vanilla isn't matched by non-string " + generatedPrimitive);

                if (!vanilla.getAsString().equals(generatedPrimitive.getAsString()))
                {
                    return Collections.singletonList("String '" + vanilla.getAsString() + "' does not match generated '" + generatedPrimitive.getAsString() + "'");
                }
            }

            return new ArrayList<>();
        }

        private List<String> compareObjects(final JsonObject vanilla, final JsonElement generated)
        {
            if (!generated.isJsonObject()) return Collections.singletonList("Object in vanilla isn't matched by generated " + generated);

            final JsonObject generatedObject = generated.getAsJsonObject();

            return vanilla.entrySet().stream()
                    .map(it -> Triple.of(it.getKey(), it.getValue(), generatedObject.get(it.getKey())))
                    .map(this::compareAndGatherErrors)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        }

        private List<String> compareArrays(final JsonArray vanilla, final JsonElement generated)
        {
            if (!generated.isJsonArray()) return Collections.singletonList("Array in vanilla isn't matched by generated " + generated);

            final JsonArray generatedArray = generated.getAsJsonArray();

            return IntStream.range(0, vanilla.size())
                    .mapToObj(it -> Triple.of("[" + it + "]", vanilla.get(it), generatedArray.get(it)))
                    .map(this::compareAndGatherErrors)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        }
    }

    public static class Tags extends BlockTagsProvider
    {
        public Tags(DataGenerator gen, ExistingFileHelper existingFileHelper)
        {
            super(gen, MODID, existingFileHelper);
        }

        @Override
        protected void addTags()
        {
            tag(BlockTags.create(new ResourceLocation(MODID, "test")))
                .add(Blocks.DIAMOND_BLOCK)
                .addTag(BlockTags.STONE_BRICKS)
                .addTag(net.minecraftforge.common.Tags.Blocks.COBBLESTONE)
                .addOptional(new ResourceLocation("chisel", "marble/raw"))
                .addOptionalTag(new ResourceLocation("forge", "storage_blocks/ruby"));

            // Hopefully sorting issues
            tag(BlockTags.create(new ResourceLocation(MODID, "thing/one")))
                    .add(Blocks.COBBLESTONE);
            tag(BlockTags.create(new ResourceLocation(MODID, "thing/two")))
                    .add(Blocks.DIORITE);
            tag(BlockTags.create(new ResourceLocation(MODID, "thing/three")))
                    .add(Blocks.ANDESITE);

            tag(BlockTags.create(new ResourceLocation(MODID, "things")))
                    .add(Blocks.COBBLESTONE)
                    .add(Blocks.DIORITE)
                    .add(Blocks.ANDESITE);
        }
    }

    public static class Lang extends LanguageProvider
    {
        public Lang(DataGenerator gen)
        {
            super(gen, MODID, "en_us");
        }

        @Override
        protected void addTranslations()
        {
            add(Blocks.STONE, "Stone");
            add(Items.DIAMOND, "Diamond");
            //add(Biomes.BEACH, "Beach");
            add(MobEffects.POISON, "Poison");
            add(Enchantments.SHARPNESS, "Sharpness");
            add(EntityType.CAT, "Cat");
            add(MODID + ".test.unicode", "\u0287s\u01DD\u2534 \u01DDpo\u0254\u1D09u\u2229");
        }
    }

    public static class ItemModels extends ItemModelProvider
    {
        private static final Logger LOGGER = LogManager.getLogger();

        public ItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper)
        {
            super(generator, MODID, existingFileHelper);
        }

        @Override
        protected void registerModels()
        {
            getBuilder("test_generated_model")
                    .parent(new UncheckedModelFile("item/generated"))
                    .texture("layer0", mcLoc("block/stone"));

            getBuilder("test_block_model")
                    .parent(getExistingFile(mcLoc("block/block")))
                    .texture("all", mcLoc("block/dirt"))
                    .texture("top", mcLoc("block/stone"))
                    .element()
                    .cube("#all")
                    .face(Direction.UP)
                    .texture("#top")
                    .tintindex(0)
                    .end()
                    .end();

            // Testing consistency

            // Test overrides
            ModelFile fishingRod = withExistingParent("fishing_rod", "handheld_rod")
                    .texture("layer0", mcLoc("item/fishing_rod"))
                    .override()
                    .predicate(mcLoc("cast"), 1)
                    .model(getExistingFile(mcLoc("item/fishing_rod_cast"))) // Use the vanilla model for validation
                    .end();

            withExistingParent("fishing_rod_cast", modLoc("fishing_rod"))
                    .parent(fishingRod)
                    .texture("layer0", mcLoc("item/fishing_rod_cast"));
        }

        private static final Set<String> IGNORED_MODELS = ImmutableSet.of("test_generated_model", "test_block_model",
                "fishing_rod", "fishing_rod_cast" // Vanilla doesn't generate these yet, so they don't match due to having the minecraft domain
                );

        @Override
        public void run(HashCache cache) throws IOException
        {
            super.run(cache);
            List<String> errors = testModelResults(this.generatedModels, existingFileHelper, IGNORED_MODELS.stream().map(s -> new ResourceLocation(MODID, folder + "/" + s)).collect(Collectors.toSet()));
            if (!errors.isEmpty()) {
                LOGGER.error("Found {} discrepancies between generated and vanilla item models: ", errors.size());
                for (String s : errors) {
                    LOGGER.error("    {}", s);
                }
                throw new AssertionError("Generated item models differed from vanilla equivalents, check above errors.");
            }
        }

        @Override
        public String getName()
        {
            return "Forge Test Item Models";
        }
    }

    public static class BlockStates extends BlockStateProvider
    {
        private static final Logger LOGGER = LogManager.getLogger();

        public BlockStates(DataGenerator gen, ExistingFileHelper exFileHelper)
        {
            super(gen, MODID, exFileHelper);
        }

        @Override
        protected void registerStatesAndModels()
        {
            // Unnecessarily complicated example to showcase how manual building works
            ModelFile birchFenceGate = models().fenceGate("birch_fence_gate", mcLoc("block/birch_planks"));
            ModelFile birchFenceGateOpen = models().fenceGateOpen("birch_fence_gate_open", mcLoc("block/birch_planks"));
            ModelFile birchFenceGateWall = models().fenceGateWall("birch_fence_gate_wall", mcLoc("block/birch_planks"));
            ModelFile birchFenceGateWallOpen = models().fenceGateWallOpen("birch_fence_gate_wall_open", mcLoc("block/birch_planks"));
            ModelFile invisbleModel = new UncheckedModelFile(new ResourceLocation("builtin/generated"));
            VariantBlockStateBuilder builder = getVariantBuilder(Blocks.BIRCH_FENCE_GATE);
            for (Direction dir : FenceGateBlock.FACING.getPossibleValues()) {
                int angle = (int) dir.toYRot();
                builder
                        .partialState()
                        .with(FenceGateBlock.FACING, dir)
                        .with(FenceGateBlock.IN_WALL, false)
                        .with(FenceGateBlock.OPEN, false)
                        .modelForState()
                        .modelFile(invisbleModel)
                        .nextModel()
                        .modelFile(birchFenceGate)
                        .rotationY(angle)
                        .uvLock(true)
                        .weight(100)
                        .addModel()
                        .partialState()
                        .with(FenceGateBlock.FACING, dir)
                        .with(FenceGateBlock.IN_WALL, false)
                        .with(FenceGateBlock.OPEN, true)
                        .modelForState()
                        .modelFile(birchFenceGateOpen)
                        .rotationY(angle)
                        .uvLock(true)
                        .addModel()
                        .partialState()
                        .with(FenceGateBlock.FACING, dir)
                        .with(FenceGateBlock.IN_WALL, true)
                        .with(FenceGateBlock.OPEN, false)
                        .modelForState()
                        .modelFile(birchFenceGateWall)
                        .rotationY(angle)
                        .uvLock(true)
                        .addModel()
                        .partialState()
                        .with(FenceGateBlock.FACING, dir)
                        .with(FenceGateBlock.IN_WALL, true)
                        .with(FenceGateBlock.OPEN, true)
                        .modelForState()
                        .modelFile(birchFenceGateWallOpen)
                        .rotationY(angle)
                        .uvLock(true)
                        .addModel();
            }

            // Realistic examples using helpers
            simpleBlock(Blocks.STONE, model -> ObjectArrays.concat(
                    ConfiguredModel.allYRotations(model, 0, false),
                    ConfiguredModel.allYRotations(model, 180, false),
                    ConfiguredModel.class));

            // From here on, models are 1-to-1 copies of vanilla (except for model locations) and will be tested as such below
            ModelFile block = models().getBuilder("block")
                    .guiLight(GuiLight.SIDE)
                    .transforms()
                    .transform(Perspective.GUI)
                    .rotation(30, 225, 0)
                    .scale(0.625f)
                    .end()
                    .transform(Perspective.GROUND)
                    .translation(0, 3, 0)
                    .scale(0.25f)
                    .end()
                    .transform(Perspective.FIXED)
                    .scale(0.5f)
                    .end()
                    .transform(Perspective.THIRDPERSON_RIGHT)
                    .rotation(75, 45, 0)
                    .translation(0, 2.5f, 0)
                    .scale(0.375f)
                    .end()
                    .transform(Perspective.FIRSTPERSON_RIGHT)
                    .rotation(0, 45, 0)
                    .scale(0.4f)
                    .end()
                    .transform(Perspective.FIRSTPERSON_LEFT)
                    .rotation(0, 225, 0)
                    .scale(0.4f)
                    .end()
                    .end();

            models().getBuilder("cube")
                    .parent(block)
                    .element()
                    .allFaces((dir, face) -> face.texture("#" + dir.getSerializedName()).cullface(dir));

            ModelFile furnace = models().orientable("furnace", mcLoc("block/furnace_side"), mcLoc("block/furnace_front"), mcLoc("block/furnace_top"));
            ModelFile furnaceLit = models().orientable("furnace_on", mcLoc("block/furnace_side"), mcLoc("block/furnace_front_on"), mcLoc("block/furnace_top"));

            getVariantBuilder(Blocks.FURNACE)
                    .forAllStates(state -> ConfiguredModel.builder()
                            .modelFile(state.getValue(FurnaceBlock.LIT) ? furnaceLit : furnace)
                            .rotationY((int) state.getValue(FurnaceBlock.FACING).getOpposite().toYRot())
                            .build()
                    );

            ModelFile barrel = models().cubeBottomTop("barrel", mcLoc("block/barrel_side"), mcLoc("block/barrel_bottom"), mcLoc("block/barrel_top"));
            ModelFile barrelOpen = models().cubeBottomTop("barrel_open", mcLoc("block/barrel_side"), mcLoc("block/barrel_bottom"), mcLoc("block/barrel_top_open"));
            directionalBlock(Blocks.BARREL, state -> state.getValue(BarrelBlock.OPEN) ? barrelOpen : barrel); // Testing custom state interpreter
            
            logBlock((RotatedPillarBlock) Blocks.ACACIA_LOG);

            stairsBlock((StairBlock) Blocks.ACACIA_STAIRS, "acacia", mcLoc("block/acacia_planks"));
            slabBlock((SlabBlock) Blocks.ACACIA_SLAB, Blocks.ACACIA_PLANKS.getRegistryName(), mcLoc("block/acacia_planks"));

            fenceBlock((FenceBlock) Blocks.ACACIA_FENCE, "acacia", mcLoc("block/acacia_planks"));
            fenceGateBlock((FenceGateBlock) Blocks.ACACIA_FENCE_GATE, "acacia", mcLoc("block/acacia_planks"));

            wallBlock((WallBlock) Blocks.COBBLESTONE_WALL, "cobblestone", mcLoc("block/cobblestone"));

            paneBlock((IronBarsBlock) Blocks.GLASS_PANE, "glass", mcLoc("block/glass"), mcLoc("block/glass_pane_top"));

            doorBlock((DoorBlock) Blocks.ACACIA_DOOR, "acacia", mcLoc("block/acacia_door_bottom"), mcLoc("block/acacia_door_top"));
            trapdoorBlock((TrapDoorBlock) Blocks.ACACIA_TRAPDOOR, "acacia", mcLoc("block/acacia_trapdoor"), true);
            trapdoorBlock((TrapDoorBlock) Blocks.OAK_TRAPDOOR, "oak", mcLoc("block/oak_trapdoor"), false); // Test a non-orientable trapdoor

            buttonBlock((ButtonBlock) Blocks.ACACIA_BUTTON, blockTexture(Blocks.ACACIA_PLANKS));
            itemModels().buttonInventory("acacia_button_inventory", blockTexture(Blocks.ACACIA_PLANKS));

            pressurePlateBlock((PressurePlateBlock) Blocks.ACACIA_PRESSURE_PLATE, blockTexture(Blocks.ACACIA_PLANKS));

            signBlock((StandingSignBlock) Blocks.ACACIA_SIGN, (WallSignBlock) Blocks.ACACIA_WALL_SIGN, blockTexture(Blocks.ACACIA_PLANKS));

            simpleBlock(Blocks.TORCH, models().torch("torch", mcLoc("block/torch")));
            horizontalBlock(Blocks.WALL_TORCH, models().torchWall("wall_torch", mcLoc("block/torch")), 90);

            models().cubeAll("test_block", mcLoc("block/stone"));
            itemModels().withExistingParent("test_block", modLoc("block/test_block"));
        }

        // Testing the outputs

        private static final Set<Block> IGNORED_BLOCKS = ImmutableSet.of(Blocks.BIRCH_FENCE_GATE, Blocks.STONE);
        // Vanilla doesn't generate these models yet, so they have minor discrepancies that are hard to test
        // This list should probably be cleared and investigated after each major version update
        private static final Set<ResourceLocation> IGNORED_MODELS = ImmutableSet.of(new ResourceLocation(MODID, "block/cube"));
        private static final Set<ResourceLocation> CUSTOM_MODELS = ImmutableSet.of(new ResourceLocation(MODID, "block/test_block"));

        private List<String> errors = new ArrayList<>();

        @Override
        public void run(HashCache cache) throws IOException
        {
            super.run(cache);
            this.errors.addAll(testModelResults(models().generatedModels, models().existingFileHelper, Sets.union(IGNORED_MODELS, CUSTOM_MODELS)));
            this.registeredBlocks.forEach((block, state) -> {
                if (IGNORED_BLOCKS.contains(block)) return;
                JsonObject generated = state.toJson();
                try {
                    Resource vanillaResource = models().existingFileHelper.getResource(block.getRegistryName(), PackType.CLIENT_RESOURCES, ".json", "blockstates");
                    JsonObject existing = GSON.fromJson(new InputStreamReader(vanillaResource.getInputStream()), JsonObject.class);
                    if (state instanceof VariantBlockStateBuilder) {
                        compareVariantBlockstates(block, generated, existing);
                    } else if (state instanceof MultiPartBlockStateBuilder) {
                        compareMultipartBlockstates(block, generated, existing);
                    } else {
                        throw new IllegalStateException("Unknown blockstate type: " + state.getClass());
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            if (!errors.isEmpty()) {
                LOGGER.error("Found {} discrepancies between generated and vanilla models/blockstates: ", errors.size());
                for (String s : errors) {
                    LOGGER.error("    {}", s);
                }
                throw new AssertionError("Generated blockstates/models differed from vanilla equivalents, check above errors.");
            }
        }

        private void compareVariantBlockstates(Block block, JsonObject generated, JsonObject vanilla) {
            JsonObject generatedVariants = generated.getAsJsonObject("variants");
            JsonObject vanillaVariants = vanilla.getAsJsonObject("variants");
            Stream.concat(generatedVariants.entrySet().stream(), vanillaVariants.entrySet().stream())
                    .map(e -> e.getKey())
                    .distinct()
                    .forEach(key -> {
                        JsonElement generatedVariant = generatedVariants.get(key);
                        JsonElement vanillaVariant = vanillaVariants.get(key);
                        if (generatedVariant.isJsonArray()) {
                            compareArrays(block, "key " + key, "random variants", generatedVariant, vanillaVariant);
                            for (int i = 0; i < generatedVariant.getAsJsonArray().size(); i++) {
                                compareVariant(block, key + "[" + i + "]", generatedVariant.getAsJsonArray().get(i).getAsJsonObject(), vanillaVariant.getAsJsonArray().get(i).getAsJsonObject());
                            }
                        }
                        if (generatedVariant.isJsonObject()) {
                            if (!vanillaVariant.isJsonObject()) {
                                blockstateError(block, "incorrectly does not have an array of variants for key %s", key);
                                return;
                            }
                            compareVariant(block, key, generatedVariant.getAsJsonObject(), vanillaVariant.getAsJsonObject());
                        }
                    });
        }

        private void compareVariant(Block block, String key, JsonObject generatedVariant, JsonObject vanillaVariant) {
            if (generatedVariant == null) {
                blockstateError(block, "missing variant for %s", key);
                return;
            }
            if (vanillaVariant == null) {
                blockstateError(block, "has extra variant %s", key);
                return;
            }
            String generatedModel = toVanillaModel(generatedVariant.get("model").getAsString());
            String vanillaModel = vanillaVariant.get("model").getAsString();
            if (!generatedModel.equals(vanillaModel)) {
                blockstateError(block, "has incorrect model \"%s\" for variant %s. Expecting: %s", generatedModel, key, vanillaModel);
                return;
            }
            generatedVariant.addProperty("model", generatedModel);
            // Parse variants to objects to handle default values in vanilla jsons
            Variant parsedGeneratedVariant = GSON.fromJson(generatedVariant, Variant.class);
            Variant parsedVanillaVariant = GSON.fromJson(vanillaVariant, Variant.class);
            if (!parsedGeneratedVariant.equals(parsedVanillaVariant)) {
                blockstateError(block, "has incorrect variant %s. Expecting: %s, Found: %s", key, vanillaVariant, generatedVariant);
                return;
            }
        }

        private void compareMultipartBlockstates(Block block, JsonObject generated, JsonObject vanilla) {
            JsonElement generatedPartsElement = generated.get("multipart");
            JsonElement vanillaPartsElement = vanilla.getAsJsonArray("multipart");
            compareArrays(block, "parts", "multipart", generatedPartsElement, vanillaPartsElement);
            // String instead of JSON types due to inconsistent hashing
            Multimap<String, String> generatedPartsByCondition = HashMultimap.create();
            Multimap<String, String> vanillaPartsByCondition = HashMultimap.create();

            JsonArray generatedParts = generatedPartsElement.getAsJsonArray();
            JsonArray vanillaParts = vanillaPartsElement.getAsJsonArray();
            for (int i = 0; i < generatedParts.size(); i++) {
                JsonObject generatedPart = generatedParts.get(i).getAsJsonObject();
                String generatedCondition = toEquivalentString(generatedPart.get("when"));
                JsonElement generatedVariants = generatedPart.get("apply");
                if (generatedVariants.isJsonObject()) {
                    correctVariant(generatedVariants.getAsJsonObject());
                } else if (generatedVariants.isJsonArray()) {
                    for (int j = 0; j < generatedVariants.getAsJsonArray().size(); j++) {
                        correctVariant(generatedVariants.getAsJsonArray().get(i).getAsJsonObject());
                    }
                }
                generatedPartsByCondition.put(generatedCondition, toEquivalentString(generatedVariants));

                JsonObject vanillaPart = vanillaParts.get(i).getAsJsonObject();
                String vanillaCondition = toEquivalentString(vanillaPart.get("when"));
                String vanillaVariants = toEquivalentString(vanillaPart.get("apply"));

                vanillaPartsByCondition.put(vanillaCondition, vanillaVariants);
            }

            Stream.concat(generatedPartsByCondition.keySet().stream(), vanillaPartsByCondition.keySet().stream())
                    .distinct()
                    .forEach(cond -> {
                        Collection<String> generatedVariants = generatedPartsByCondition.get(cond);
                        Collection<String> vanillaVariants = vanillaPartsByCondition.get(cond);
                        if (generatedVariants.size() != vanillaVariants.size()) {
                            if (vanillaVariants.isEmpty()) {
                                blockstateError(block, " has extra condition %s", cond);
                            } else if (generatedVariants.isEmpty()) {
                                blockstateError(block, " is missing condition %s", cond);
                            } else {
                                blockstateError(block, " has differing amounts of variant lists matching condition %s. Expected: %d, Found: %d", cond, vanillaVariants.size(), generatedVariants.size());
                            }
                            return;
                        }

                        if (!vanillaVariants.containsAll(generatedVariants) || !generatedVariants.containsAll(vanillaVariants)) {
                            List<String> extra = new ArrayList<>(generatedVariants);
                            extra.removeAll(vanillaVariants);
                            List<String> missing = new ArrayList<>(vanillaVariants);
                            missing.removeAll(generatedVariants);
                            if (!extra.isEmpty()) {
                                blockstateError(block, " has extra variants for condition %s: %s", cond, extra);
                            }
                            if (!missing.isEmpty()) {
                                blockstateError(block, " has missing variants for condition %s: %s", cond, missing);
                            }
                        }
                    });
        }

        // Eliminate some formatting differences that are not meaningful
        private String toEquivalentString(JsonElement element) {
            return Objects.toString(element)
                    .replaceAll("\"(true|false)\"", "$1") // Unwrap booleans in strings
                    .replaceAll("\"(-?(?:0|[1-9]\\d*)(?:\\.\\d+)?(?:[eE][+-]?\\d+)?)\"", "$1"); // Unwrap numbers in strings, regex from https://stackoverflow.com/questions/13340717/json-numbers-regular-expression
        }

        private void correctVariant(JsonObject variant) {
            variant.addProperty("model", toVanillaModel(variant.get("model").getAsString()));
        }

        private boolean compareArrays(Block block, String key, String name, JsonElement generated, JsonElement vanilla) {
            if (!vanilla.isJsonArray()) {
                blockstateError(block, "incorrectly has an array of %s for %s", name, key);
                return false;
            }
            JsonArray generatedArray = generated.getAsJsonArray();
            JsonArray vanillaArray = vanilla.getAsJsonArray();
            if (generatedArray.size() != vanillaArray.size()) {
                blockstateError(block, "has incorrect number of %s for %s. Expecting: %s, Found: %s", name, key, vanillaArray.size(), generatedArray.size());
                return false;
            }
            return true;
        }

        private void blockstateError(Block block, String fmt, Object... args) {
            errors.add("Generated blockstate for block " + block + " " + String.format(Locale.ENGLISH, fmt, args));
        }

        @Override
        public String getName() {
            return "Forge Test Blockstates";
        }
    }

    public static class Advancements extends AdvancementProvider
    {

        public Advancements(DataGenerator generatorIn, ExistingFileHelper fileHelper)
        {
            super(generatorIn, fileHelper);
        }

        @Override
        protected void registerAdvancements(Consumer<Advancement> consumer, ExistingFileHelper fileHelper)
        {
            Advancement.Builder.advancement().display(Items.DIRT,
                            new TranslatableComponent(Items.DIRT.getDescriptionId()),
                            new TranslatableComponent("dirt_description"),
                            new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"),
                            FrameType.TASK,
                            true,
                            true,
                            false)
                    .addCriterion("has_dirt", InventoryChangeTrigger.TriggerInstance.hasItems(Items.DIRT))
                    .save(consumer, new ResourceLocation(MODID, "obtain_dirt"), fileHelper);

            Advancement.Builder.advancement().display(Items.DIAMOND_BLOCK,
                            new TranslatableComponent(Items.DIAMOND_BLOCK.getDescriptionId()),
                            new TextComponent("You obtained a DiamondBlock"),
                            new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"),
                            FrameType.CHALLENGE,
                            true,
                            true,
                            false)
                    .addCriterion("obtained_diamond_block", InventoryChangeTrigger.TriggerInstance.hasItems(Items.DIAMOND_BLOCK))
                    .save(consumer, new ResourceLocation("obtain_diamond_block"), fileHelper);

            Advancement.Builder.advancement()
                    .display(Blocks.GRASS_BLOCK,
                            new TranslatableComponent("advancements.story.root.title"),
                            new TextComponent("Changed Description"),
                            new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"),
                            FrameType.TASK,
                            false,
                            false,
                            false)
                    .addCriterion("crafting_table", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.CRAFTING_TABLE))
                    .save(consumer, new ResourceLocation("story/root"), fileHelper);

            // This should cause an error because of the parent not existing
/*            Advancement.Builder.advancement().display(Blocks.COBBLESTONE,
                    new TranslationTextComponent(Items.COBBLESTONE.getDescriptionId()),
                    new StringTextComponent("You got cobblestone"),
                    new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"),
                    FrameType.TASK,
                    false,
                    false,
                    false)
                    .addCriterion("get_cobbleStone", InventoryChangeTrigger.Instance.hasItems(Items.COBBLESTONE))
                    .parent(new ResourceLocation("not_there/not_here"))
                    .save(consumer, new ResourceLocation("illegal_parent"), fileHelper);*/

            Advancement.Builder.advancement().display(Blocks.COBBLESTONE,
                            new TranslatableComponent(Items.COBBLESTONE.getDescriptionId()),
                            new TextComponent("You got cobblestone"),
                            new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"),
                            FrameType.TASK,
                            false,
                            false,
                            false)
                    .addCriterion("get_cobbleStone", InventoryChangeTrigger.TriggerInstance.hasItems(Items.COBBLESTONE))
                    .parent(new ResourceLocation("forge", "dummy_parent"))
                    .save(consumer, new ResourceLocation("good_parent"), fileHelper);
        }
    }

    private static <T extends ModelBuilder<T>> List<String> testModelResults(Map<ResourceLocation, T> models, ExistingFileHelper existingFileHelper, Set<ResourceLocation> toIgnore) {
        List<String> ret = new ArrayList<>();
        models.forEach((loc, model) -> {
            if (toIgnore.contains(loc)) return;
            JsonObject generated = model.toJson();
            if (generated.has("parent")) {
                generated.addProperty("parent", toVanillaModel(generated.get("parent").getAsString()));
            }
            try {
                Resource vanillaResource = existingFileHelper.getResource(new ResourceLocation(loc.getPath()), PackType.CLIENT_RESOURCES, ".json", "models");
                JsonObject existing = GSON.fromJson(new InputStreamReader(vanillaResource.getInputStream()), JsonObject.class);

                JsonElement generatedDisplay = generated.remove("display");
                JsonElement vanillaDisplay = existing.remove("display");
                if (generatedDisplay == null && vanillaDisplay != null) {
                    ret.add("Model " + loc + " is missing transforms");
                    return;
                } else if (generatedDisplay != null && vanillaDisplay == null) {
                    ret.add("Model " + loc + " has transforms when vanilla equivalent does not");
                    return;
                } else if (generatedDisplay != null) { // Both must be non-null
                    ItemTransforms generatedTransforms = GSON.fromJson(generatedDisplay, ItemTransforms.class);
                    ItemTransforms vanillaTransforms = GSON.fromJson(vanillaDisplay, ItemTransforms.class);
                    for (Perspective type : Perspective.values()) {
                        if (!generatedTransforms.getTransform(type.vanillaType).equals(vanillaTransforms.getTransform(type.vanillaType))) {
                            ret.add("Model " + loc  + " has transforms that differ from vanilla equivalent for perspective " + type.name());
                            return;
                        }
                    }
                }

                JsonElement generatedTextures = generated.remove("textures");
                JsonElement vanillaTextures = existing.remove("textures");
                if (generatedTextures == null && vanillaTextures != null) {
                    ret.add("Model " + loc + " is missing textures");
                } else if (generatedTextures != null && vanillaTextures == null) {
                    ret.add("Model " + loc + " has textures when vanilla equivalent does not");
                } else if (generatedTextures != null) { // Both must be non-null
                    for (Map.Entry<String, JsonElement> e : generatedTextures.getAsJsonObject().entrySet()) {
                        String vanillaTexture = vanillaTextures.getAsJsonObject().get(e.getKey()).getAsString();
                        if (!e.getValue().getAsString().equals(vanillaTexture)) {
                            ret.add("Texture for variable '" + e.getKey() + "' for model " + loc + " does not match vanilla equivalent");
                        }
                    }
                    if (generatedTextures.getAsJsonObject().size() != vanillaTextures.getAsJsonObject().size()) {
                        ret.add("Model " + loc + " is missing textures from vanilla equivalent");
                    }
                }

                if (!existing.equals(generated)) {
                    ret.add("Model " + loc + " does not match vanilla equivalent");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return ret;
    }

    private static String toVanillaModel(String model) {
        // We generate our own model jsons to test model building, but otherwise our blockstates should be identical
        // So remove modid to match
        return model.replaceAll("^\\w+:", "minecraft:");
    }
}
