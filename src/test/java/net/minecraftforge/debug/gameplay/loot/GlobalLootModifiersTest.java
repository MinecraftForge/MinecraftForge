/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.gameplay.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemEnchantmentsPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemSubPredicates;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.registries.RegistryPatchGenerator;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.common.loot.LootTableIdCondition;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@GameTestHolder("forge.global_loot_modifiers")
@Mod(GlobalLootModifiersTest.MODID)
public class GlobalLootModifiersTest extends BaseTestMod {
    public static final String MODID = "global_loot_test";

    private static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLM = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MODID);
    static {
        GLM.register("multiply_loot", MultiplyDropsModifier.CODEC);
        GLM.register("smelting", SmeltingEnchantmentModifier.CODEC);
        GLM.register("wheat_harvest", WheatSeedsConverterModifier.CODEC);
        GLM.register("silk_touch_bamboo", SilkTouchTestModifier.CODEC);
    }

    private static final ResourceKey<Enchantment> SMELT = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(MODID, "smelt"));
    private static final Supplier<RegistrySetBuilder> ENCHANTMENTS = () -> new RegistrySetBuilder()
        .add(Registries.ENCHANTMENT, ctx -> {
            ctx.register(SMELT, Enchantment.enchantment(
                Enchantment.definition(
                    ctx.lookup(Registries.ITEM).getOrThrow(ItemTags.MINING_LOOT_ENCHANTABLE), 1, 1,
                    Enchantment.constantCost(15), Enchantment.constantCost(15), 5, EquipmentSlotGroup.MAINHAND
                )
            ).build(SMELT.location()));
        });

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final RegistryObject<Block> TEST_BLOCK = BLOCKS.register("test", () -> new Block(BlockBehaviour.Properties.of()));
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final RegistryObject<Item> TEST_ITEM = ITEMS.register("test", () -> new BlockItem(TEST_BLOCK.get(), new Item.Properties()));

    public GlobalLootModifiersTest(FMLJavaModLoadingContext context) {
        super(context);
        testItem(lookup -> getSmelterAxe(lookup.lookup(Registries.ENCHANTMENT).get(), true));
    }

    private static ItemStack getSmelterAxe(HolderLookup<Enchantment> lookup, boolean enchanted) {
        var item = new ItemStack(Items.IRON_AXE);
        if (!enchanted)
            return item;

        var enchants = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(item));
        enchants.set(lookup.getOrThrow(SMELT), 1);
        item.set(DataComponents.ENCHANTMENTS, enchants.toImmutable());
        return item;
    }

    @SubscribeEvent
    public void runData(GatherDataEvent event) {
        var out = event.getGenerator().getPackOutput();
        var lookup = event.getLookupProvider();
        var patched = RegistryPatchGenerator.createLookup(lookup, ENCHANTMENTS.get())
                .thenApply(RegistrySetBuilder.PatchedRegistries::patches);
        event.getGenerator().addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(out, lookup, ENCHANTMENTS.get(), Set.of(MODID)));
        event.getGenerator().addProvider(event.includeServer(), new ModifierProvider(out, MODID, patched));
        event.getGenerator().addProvider(event.includeServer(), new LootProvider(out, lookup));
    }

    // Tests the Enchantment condition, as well as the ability to completely override the returned values.
    @GameTest(template = "forge:empty3x3x3")
    public static void smellting(GameTestHelper helper) {
        var player = helper.makeMockServerPlayer();
        var center = new BlockPos(1, 1, 1);
        var enchants = helper.getLevel().holderLookup(Registries.ENCHANTMENT);
        var smelt = getSmelterAxe(enchants, true);
        var normal = getSmelterAxe(enchants, false);

        // Harvesting with normal axe should just dorp log
        player.setItemSlot(EquipmentSlot.MAINHAND, normal);
        helper.setBlock(center, Blocks.OAK_LOG);
        player.gameMode.destroyBlock(helper.absolutePos(center));
        helper.assertItemEntityPresent(Items.OAK_LOG, center, 1.0);

        //Reset
        helper.killAllEntities();

        // Test smelting enchantment, should result in charcoal
        player.setItemSlot(EquipmentSlot.MAINHAND, smelt);
        helper.setBlock(center, Blocks.OAK_LOG);
        player.gameMode.destroyBlock(helper.absolutePos(center));
        helper.assertItemEntityPresent(Items.CHARCOAL, center, 1.0);

        helper.succeed();
    }

    // Tests the table name condition by duplicating the drops for our test block, but not for anything else.
    @GameTest(template = "forge:empty3x3x3")
    public static void condition_table_name(GameTestHelper helper) {
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


    @GameTest(template = "forge:empty3x3x3")
    public static void silk_reentrant(GameTestHelper helper) {
        var center = new BlockPos(1, 1, 1);
        var player = helper.makeMockServerPlayer();
        var bamboo = new ItemStack(Items.BAMBOO);
        var normal = new ItemStack(Items.IRON_AXE);

        // Should drop nothing
        helper.setBlock(center, Blocks.GLASS);
        player.setItemSlot(EquipmentSlot.MAINHAND, normal);
        player.gameMode.destroyBlock(helper.absolutePos(center));
        helper.assertItemEntityNotPresent(Items.GLASS, center, 1.0);

        // Should drop the glass
        helper.setBlock(center, Blocks.GLASS);
        player.setItemSlot(EquipmentSlot.MAINHAND, bamboo);
        player.gameMode.destroyBlock(helper.absolutePos(center));
        helper.assertItemEntityPresent(Items.GLASS, center, 1.0);

        helper.succeed();
    }

    private static class ModifierProvider extends GlobalLootModifierProvider {
        public ModifierProvider(PackOutput output, String modid, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, modid, registries);
        }

        @Override
        protected void start(HolderLookup.Provider registries) {
            var smelt = registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(SMELT);

            // Tested vis breaking a log and checking if we get charcoal
            add("smelting", new SmeltingEnchantmentModifier(
                new LootItemCondition[]{
                    MatchTool.toolMatches(
                        ItemPredicate.Builder.item()
                            .withSubPredicate(
                                ItemSubPredicates.ENCHANTMENTS,
                                ItemEnchantmentsPredicate.enchantments(List.of(
                                    new EnchantmentPredicate(smelt, MinMaxBounds.Ints.atLeast(1))
                                ))
                            )
                    ).build()
                })
            );


            // This has no game test because it relies on random number generation and I can't be bothered to try and force that right now.
            add("wheat_harvest", new WheatSeedsConverterModifier(
                new LootItemCondition[] {
                    MatchTool.toolMatches(ItemPredicate.Builder.item().of(Items.SHEARS)).build(),
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.WHEAT).build()
                },
                3, Items.WHEAT_SEEDS, Items.WHEAT)
            );

            // Tested by verifying that we drop 2 test blocks when broken instead of the default 1
            add("multiply_loot", new MultiplyDropsModifier(
                new LootItemCondition[] {
                    LootTableIdCondition.builder(TEST_BLOCK.get().getLootTable().location()).build()
                }, 2)
            );

            // Bamboo silk harvests everything, Tested by breaking glass and seeing if we get glass back.
            add("bamboo_silk", new SilkTouchTestModifier(
                new LootItemCondition[] {
                    MatchTool.toolMatches(ItemPredicate.Builder.item().of(Items.BAMBOO)).build()
                })
            );
        }
    }

    private static class LootProvider extends LootTableProvider {
        public LootProvider(PackOutput out, CompletableFuture<HolderLookup.Provider> lookup) {
            super(out, Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(BlockLoot::new, LootContextParamSets.BLOCK)
            ), lookup);
        }

        private static class BlockLoot extends BlockLootSubProvider implements IConditionBuilder {
            public BlockLoot(HolderLookup.Provider lookup) {
                super(Set.of(), FeatureFlags.REGISTRY.allFlags(), lookup);
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

    /**
     * The smelting enchantment causes this modifier to be invoked, via the smelting loot_modifier json
     */
    private static class SmeltingEnchantmentModifier extends LootModifier {
        public static final Supplier<MapCodec<SmeltingEnchantmentModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.mapCodec(inst ->
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
                .getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(stack), context.getLevel())
                .map(holder -> holder.value())
                .map(recipe -> recipe.getResultItem(reg))
                .filter(itemStack -> !itemStack.isEmpty())
                .map(itemStack -> ItemHandlerHelper.copyStackWithSize(itemStack, stack.getCount() * itemStack.getCount()))
                .orElse(stack);
        }

        @Override
        public MapCodec<? extends IGlobalLootModifier> codec() {
            return CODEC.get();
        }
    }

    /**
     * When harvesting blocks with bamboo, this modifier is invoked, via the silk_touch_bamboo loot_modifier json
     */
    private static class SilkTouchTestModifier extends LootModifier {
        public static final Supplier<MapCodec<SilkTouchTestModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.mapCodec(inst ->
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
            var silk = context.getLevel().holderLookup(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH);
            var silkLevel = EnchantmentHelper.getItemEnchantmentLevel(silk, ctxTool);
            if (ctxTool == null || ctxTool.isEmpty() || silkLevel > 0) return generatedLoot;
            var fakeTool = ctxTool.copy();
            fakeTool.enchant(silk, 1);
            var params = new LootParams.Builder(context.getLevel())
                .withParameter(LootContextParams.TOOL, fakeTool)
                .create(LootContextParamSets.EMPTY);

            return context.getLevel().getServer().reloadableRegistries()
                .getLootTable(context.getParamOrNull(LootContextParams.BLOCK_STATE).getBlock().getLootTable())
                .getRandomItems(params);
        }

        @Override
        public MapCodec<? extends IGlobalLootModifier> codec() {
            return CODEC.get();
        }
    }

    /**
     * When harvesting wheat with shears, this modifier is invoked via the wheat_harvest loot_modifier json<br/>
     * This modifier checks how many seeds were harvested and turns X seeds into Y wheat (3:1)
     */
    private static class WheatSeedsConverterModifier extends LootModifier {
        public static final Supplier<MapCodec<WheatSeedsConverterModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.mapCodec(inst -> codecStart(inst).and(
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
        public MapCodec<? extends IGlobalLootModifier> codec() {
            return CODEC.get();
        }
    }

    private static class MultiplyDropsModifier extends LootModifier {
        public static final Supplier<MapCodec<MultiplyDropsModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.mapCodec(inst -> codecStart(inst)
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
        public MapCodec<? extends IGlobalLootModifier> codec() {
            return CODEC.get();
        }
    }
}