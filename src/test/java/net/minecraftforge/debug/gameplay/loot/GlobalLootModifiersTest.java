/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.gameplay.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantment.Rarity;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.common.loot.LootTableIdCondition;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.test.BaseTestMod;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@GameTestHolder("forge.global_loot_modifiers")
@Mod(GlobalLootModifiersTest.MODID)
public class GlobalLootModifiersTest extends BaseTestMod {
    public static final String MODID = "global_loot_test";

    private static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLM = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MODID);
    static {
        GLM.register("multiply_loot", MultiplyDropsModifier.CODEC);
        GLM.register("smelting", SmeltingEnchantmentModifier.CODEC);
        GLM.register("wheat_harvest", WheatSeedsConverterModifier.CODEC);
        GLM.register("silk_touch_bamboo", SilkTouchTestModifier.CODEC);
    }

    private static final DeferredRegister<Enchantment> ENCHANTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MODID);
    private static final RegistryObject<Enchantment> SMELT = ENCHANTS.register("smelt", () -> new SmelterEnchantment(Rarity.UNCOMMON, EnchantmentCategory.DIGGER, EquipmentSlot.MAINHAND));

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final RegistryObject<Block> TEST_BLOCK = BLOCKS.register("test", () -> new Block(BlockBehaviour.Properties.of()));
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final RegistryObject<Item> TEST_ITEM = ITEMS.register("test", () -> new BlockItem(TEST_BLOCK.get(), new Item.Properties()));

    public GlobalLootModifiersTest() {
        testItem(() -> {
            var smelt = new ItemStack(Items.IRON_AXE);
            EnchantmentHelper.setEnchantments(Map.of(SMELT.get(), 1), smelt);
            return smelt;
        });
    }

    @SubscribeEvent
    public void runData(GatherDataEvent event) {
        event.getGenerator().addProvider(event.includeServer(), new DataProvider(event.getGenerator().getPackOutput(), MODID));
        event.getGenerator().addProvider(event.includeServer(), new LootProvider(event.getGenerator().getPackOutput()));
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void test_smelting_modifier(GameTestHelper helper) {
        var center = new BlockPos(1, 1, 1);
        helper.setBlock(center, Blocks.OAK_LOG);

        var smelt = new ItemStack(Items.IRON_AXE);
        EnchantmentHelper.setEnchantments(Map.of(SMELT.get(), 1), smelt);

        var player = helper.makeMockServerPlayer();
        player.setItemSlot(EquipmentSlot.MAINHAND, smelt);
        player.gameMode.destroyBlock(helper.absolutePos(center));

        helper.assertItemEntityPresent(Items.CHARCOAL, center, 1.0);

        helper.succeed();
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void table_name_condition(GameTestHelper helper) {
        var center = new BlockPos(1, 1, 1);
        var player = helper.makeMockServerPlayer();

        // Should be doubled
        helper.setBlock(center, TEST_BLOCK.get());
        player.gameMode.destroyBlock(helper.absolutePos(center));
        helper.assertItemEntityCountIs(TEST_ITEM.get(), center, 1.0, 2);

        // Should not be modified
        helper.setBlock(center, Blocks.OAK_LOG);
        player.gameMode.destroyBlock(helper.absolutePos(center));
        helper.assertItemEntityCountIs(Items.OAK_LOG, center, 1.0, 1);

        helper.succeed();
    }

    private static class DataProvider extends GlobalLootModifierProvider {
        public DataProvider(PackOutput output, String modid) {
            super(output, modid);
        }

        @Override
        protected void start() {
            add("smelting", new SmeltingEnchantmentModifier(
                new LootItemCondition[]{
                    MatchTool.toolMatches(
                        ItemPredicate.Builder.item().hasEnchantment(
                            new EnchantmentPredicate(SMELT.get(), MinMaxBounds.Ints.atLeast(1))
                        )
                    ).build()
                })
            );

            add("wheat_harvest", new WheatSeedsConverterModifier(
                new LootItemCondition[] {
                    MatchTool.toolMatches(ItemPredicate.Builder.item().of(Items.SHEARS)).build(),
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.WHEAT).build()
                },
                3, Items.WHEAT_SEEDS, Items.WHEAT)
            );

            add("multiply_loot", new MultiplyDropsModifier(
                new LootItemCondition[] {
                    LootTableIdCondition.builder(TEST_BLOCK.get().getLootTable()).build()
                }, 2)
            );

            // Bamboo silk harvests everything
            add("bamboo_silk", new SilkTouchTestModifier(
                new LootItemCondition[] {
                    MatchTool.toolMatches(ItemPredicate.Builder.item().of(Items.BAMBOO)).build()
                })
            );
        }
    }

    private static class LootProvider extends LootTableProvider {
        public LootProvider(PackOutput out) {
            super(out, Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(BlockLoot::new, LootContextParamSets.BLOCK)
            ));
        }

        private static class BlockLoot extends BlockLootSubProvider implements IConditionBuilder {
            public BlockLoot() {
                super(Set.of(), FeatureFlags.REGISTRY.allFlags());
            }

            @Override
            protected Iterable<Block> getKnownBlocks() {
               return List.of(TEST_BLOCK.get());
            }

            @Override
            protected void generate() {
                this.dropSelf(TEST_BLOCK.get());
            }
        }
    }

    private static class SmelterEnchantment extends Enchantment {
        protected SmelterEnchantment(Rarity rarityIn, EnchantmentCategory typeIn, EquipmentSlot... slots) {
            super(rarityIn, typeIn, slots);
        }
    }

    /**
     * The smelting enchantment causes this modifier to be invoked, via the smelting loot_modifier json
     */
    private static class SmeltingEnchantmentModifier extends LootModifier {
        public static final Supplier<Codec<SmeltingEnchantmentModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.create(inst ->
                codecStart(inst)
                .apply(inst, SmeltingEnchantmentModifier::new)
            )
        );

        public SmeltingEnchantmentModifier(LootItemCondition[] conditionsIn) {
            super(conditionsIn);
        }

        @NotNull
        @Override
        public ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
            var ret = new ObjectArrayList<ItemStack>();
            generatedLoot.forEach(stack -> ret.add(smelt(stack, context)));
            return ret;
        }

        private static ItemStack smelt(ItemStack stack, LootContext context) {
            var mgr = context.getLevel().getRecipeManager();
            var reg = context.getLevel().registryAccess();
            return mgr
                .getRecipeFor(RecipeType.SMELTING, new SimpleContainer(stack), context.getLevel())
                .map(holder -> holder.value())
                .map(recipe -> recipe.getResultItem(reg))
                .filter(itemStack -> !itemStack.isEmpty())
                .map(itemStack -> ItemHandlerHelper.copyStackWithSize(itemStack, stack.getCount() * itemStack.getCount()))
                .orElse(stack);
        }

        @Override
        public Codec<? extends IGlobalLootModifier> codec() {
            return CODEC.get();
        }
    }

    /**
     * When harvesting blocks with bamboo, this modifier is invoked, via the silk_touch_bamboo loot_modifier json
     *
     */
    private static class SilkTouchTestModifier extends LootModifier {
        public static final Supplier<Codec<SilkTouchTestModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.create(inst ->
                codecStart(inst)
                .apply(inst, SilkTouchTestModifier::new)
            )
        );

        public SilkTouchTestModifier(LootItemCondition[] conditionsIn) {
            super(conditionsIn);
        }

        @NotNull
        @Override
        public ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
            var ctxTool = context.getParamOrNull(LootContextParams.TOOL);
            //return early if silk-touch is already applied (otherwise we'll get stuck in an infinite loop).
            if (ctxTool == null || ctxTool.getEnchantmentLevel(Enchantments.SILK_TOUCH) > 0) return generatedLoot;
            var fakeTool = ctxTool.copy();
            fakeTool.enchant(Enchantments.SILK_TOUCH, 1);
            var params = new LootParams.Builder(context.getLevel())
                .withParameter(LootContextParams.TOOL, fakeTool)
                .create(LootContextParamSets.EMPTY);

            return context.getLevel().getServer().getLootData()
                .getLootTable(context.getParamOrNull(LootContextParams.BLOCK_STATE).getBlock().getLootTable())
                .getRandomItems(params);
        }

        @Override
        public Codec<? extends IGlobalLootModifier> codec() {
            return CODEC.get();
        }
    }

    /**
     * When harvesting wheat with shears, this modifier is invoked via the wheat_harvest loot_modifier json<br/>
     * This modifier checks how many seeds were harvested and turns X seeds into Y wheat (3:1)
     *
     */
    private static class WheatSeedsConverterModifier extends LootModifier {
        public static final Supplier<Codec<WheatSeedsConverterModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> codecStart(inst).and(
            inst.group(
                Codec.INT.fieldOf("numSeeds").forGetter(m -> m.numSeedsToConvert),
                ForgeRegistries.ITEMS.getCodec().fieldOf("seedItem").forGetter(m -> m.itemToCheck),
                ForgeRegistries.ITEMS.getCodec().fieldOf("replacement").forGetter(m -> m.itemReward)
            )).apply(inst, WheatSeedsConverterModifier::new)
        ));

        private final int numSeedsToConvert;
        private final Item itemToCheck;
        private final Item itemReward;
        public WheatSeedsConverterModifier(LootItemCondition[] conditionsIn, int numSeeds, Item itemCheck, Item reward) {
            super(conditionsIn);
            numSeedsToConvert = numSeeds;
            itemToCheck = itemCheck;
            itemReward = reward;
        }

        @NotNull
        @Override
        public ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
            //
            // Additional conditions can be checked, though as much as possible should be parameterized via JSON data.
            // It is better to write a new ILootCondition implementation than to do things here.
            //
            int numSeeds = 0;
            for(ItemStack stack : generatedLoot) {
                if(stack.getItem() == itemToCheck)
                    numSeeds+=stack.getCount();
            }
            if(numSeeds >= numSeedsToConvert) {
                generatedLoot.removeIf(x -> x.getItem() == itemToCheck);
                generatedLoot.add(new ItemStack(itemReward, (numSeeds/numSeedsToConvert)));
                numSeeds = numSeeds%numSeedsToConvert;
                if(numSeeds > 0)
                    generatedLoot.add(new ItemStack(itemToCheck, numSeeds));
            }
            return generatedLoot;
        }

        @Override
        public Codec<? extends IGlobalLootModifier> codec() {
            return CODEC.get();
        }
    }

    private static class MultiplyDropsModifier extends LootModifier {
        public static final Supplier<Codec<MultiplyDropsModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> codecStart(inst)
            .and(ExtraCodecs.POSITIVE_INT.optionalFieldOf("multiplication_factor", 2).forGetter(m -> m.multiplicationFactor))
            .apply(inst, MultiplyDropsModifier::new)
        ));

        private final int multiplicationFactor;

        public MultiplyDropsModifier(final LootItemCondition[] conditionsIn, final int multiplicationFactor) {
            super(conditionsIn);
            this.multiplicationFactor = multiplicationFactor;
        }

        @Override
        protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
            return generatedLoot.stream()
                .map(ItemStack::copy)
                .peek(stack -> stack.setCount(Math.min(stack.getMaxStackSize(), stack.getCount() * this.multiplicationFactor)))
                .collect(Collectors.toCollection(ObjectArrayList::new));
        }

        @Override
        public Codec<? extends IGlobalLootModifier> codec() {
            return CODEC.get();
        }
    }
}