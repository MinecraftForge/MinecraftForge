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
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantment.Rarity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.common.loot.LootTableIdCondition;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;
import java.util.stream.Collectors;

@Mod(GlobalLootModifiersTest.MODID)
public class GlobalLootModifiersTest {
    public static final String MODID = "global_loot_test";
    public static final boolean ENABLE = true;

    public GlobalLootModifiersTest()
    {
        if(ENABLE)
        {
            GLM.register(FMLJavaModLoadingContext.get().getModEventBus());
            ENCHANTS.register(FMLJavaModLoadingContext.get().getModEventBus());
        }
    }

    private static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLM = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MODID);
    private static final DeferredRegister<Enchantment> ENCHANTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MODID);

    private static final RegistryObject<Codec<DungeonLootEnhancerModifier>> DUNGEON_LOOT = GLM.register("dungeon_loot", DungeonLootEnhancerModifier.CODEC);
    private static final RegistryObject<Codec<SmeltingEnchantmentModifier>> SMELTING = GLM.register("smelting", SmeltingEnchantmentModifier.CODEC);
    private static final RegistryObject<Codec<WheatSeedsConverterModifier>> WHEATSEEDS = GLM.register("wheat_harvest", WheatSeedsConverterModifier.CODEC);
    private static final RegistryObject<Codec<SilkTouchTestModifier>> SILKTOUCH = GLM.register("silk_touch_bamboo", SilkTouchTestModifier.CODEC);
    private static final RegistryObject<Enchantment> SMELT = ENCHANTS.register("smelt", () -> new SmelterEnchantment(Rarity.UNCOMMON, EnchantmentCategory.DIGGER, EquipmentSlot.MAINHAND));

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
    public static class EventHandlers {
        @SubscribeEvent
        public static void runData(GatherDataEvent event)
        {
            if(ENABLE)
                event.getGenerator().addProvider(event.includeServer(), new DataProvider(event.getGenerator(), MODID));
        }
    }

    private static class DataProvider extends GlobalLootModifierProvider
    {
        public DataProvider(DataGenerator gen, String modid)
        {
            super(gen, modid);
        }

        @Override
        protected void start()
        {
            add("smelting", new SmeltingEnchantmentModifier(
                    new LootItemCondition[]{
                            MatchTool.toolMatches(
                                    ItemPredicate.Builder.item().hasEnchantment(
                                            new EnchantmentPredicate(SMELT.get(), MinMaxBounds.Ints.atLeast(1))))
                                    .build()
                    })
            );

            add("wheat_harvest", new WheatSeedsConverterModifier(
                    new LootItemCondition[] {
                            MatchTool.toolMatches(ItemPredicate.Builder.item().of(Items.SHEARS)).build(),
                            LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.WHEAT).build()
                    },
                    3, Items.WHEAT_SEEDS, Items.WHEAT)
            );

            add("dungeon_loot", new DungeonLootEnhancerModifier(
                    new LootItemCondition[] { LootTableIdCondition.builder(new ResourceLocation("chests/simple_dungeon")).build() },
                    2)
            );
        }
    }

    private static class SmelterEnchantment extends Enchantment {
        protected SmelterEnchantment(Rarity rarityIn, EnchantmentCategory typeIn, EquipmentSlot... slots) {
            super(rarityIn, typeIn, slots);
        }
    }

    /**
     * The smelting enchantment causes this modifier to be invoked, via the smelting loot_modifier json
     *
     */
    private static class SmeltingEnchantmentModifier extends LootModifier {
        public static final Supplier<Codec<SmeltingEnchantmentModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, SmeltingEnchantmentModifier::new)));

        public SmeltingEnchantmentModifier(LootItemCondition[] conditionsIn) {
            super(conditionsIn);
        }

        @NotNull
        @Override
        public ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
            ObjectArrayList<ItemStack> ret = new ObjectArrayList<ItemStack>();
            generatedLoot.forEach((stack) -> ret.add(smelt(stack, context)));
            return ret;
        }

        private static ItemStack smelt(ItemStack stack, LootContext context) {
            return context.getLevel().getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(stack), context.getLevel())
                    .map(SmeltingRecipe::getResultItem)
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
        public static final Supplier<Codec<SilkTouchTestModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, SilkTouchTestModifier::new)));

        public SilkTouchTestModifier(LootItemCondition[] conditionsIn) {
            super(conditionsIn);
        }

        @NotNull
        @Override
        public ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
            ItemStack ctxTool = context.getParamOrNull(LootContextParams.TOOL);
            //return early if silk-touch is already applied (otherwise we'll get stuck in an infinite loop).
            if(ctxTool == null || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, ctxTool) > 0) return generatedLoot;
            ItemStack fakeTool = ctxTool.copy();
            fakeTool.enchant(Enchantments.SILK_TOUCH, 1);
            LootContext.Builder builder = new LootContext.Builder(context);
            builder.withParameter(LootContextParams.TOOL, fakeTool);
            LootContext ctx = builder.create(LootContextParamSets.BLOCK);
            LootTable loottable = context.getLevel().getServer().getLootTables().get(context.getParamOrNull(LootContextParams.BLOCK_STATE).getBlock().getLootTable());
            return loottable.getRandomItems(ctx);
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

    private static class DungeonLootEnhancerModifier extends LootModifier {
        public static final Supplier<Codec<DungeonLootEnhancerModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> codecStart(inst)
                .and(ExtraCodecs.POSITIVE_INT.optionalFieldOf("multiplication_factor", 2).forGetter(m -> m.multiplicationFactor))
                .apply(inst, DungeonLootEnhancerModifier::new)
        ));

        private final int multiplicationFactor;

        public DungeonLootEnhancerModifier(final LootItemCondition[] conditionsIn, final int multiplicationFactor) {
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
