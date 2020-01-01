package net.minecraftforge.debug.gameplay.loot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.common.loot.IGlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

@Mod(GlobalLootModifiersTest.MODID)
public class GlobalLootModifiersTest {
    public static final String MODID = "global_loot_test";
    public static final boolean ENABLE = true;
    @ObjectHolder(value = MODID)
    public static final Enchantment smelt = null;
    public GlobalLootModifiersTest() { }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
    public static class EventHandlers {
        @SubscribeEvent
        public static void registerEnchantments(@Nonnull final RegistryEvent.Register<Enchantment> event) {
            if (ENABLE) {
                event.getRegistry().register(new SmelterEnchantment(Rarity.UNCOMMON, EnchantmentType.DIGGER, new EquipmentSlotType[] {EquipmentSlotType.MAINHAND}).setRegistryName(new ResourceLocation(MODID,"smelt")));
            }
        }

        @SubscribeEvent
        public static void registerModifierSerializers(@Nonnull final RegistryEvent.Register<IGlobalLootModifierSerializer<?>> event) {
            if (ENABLE) {
                event.getRegistry().register(
                		new WheatSeedsConverterModifier.Serializer().setRegistryName(new ResourceLocation(MODID,"wheat_harvest"))
                );
                event.getRegistry().register(new SmeltingEnchantmentModifier.Serializer().setRegistryName(new ResourceLocation(MODID,"smelting")));
                event.getRegistry().register(new SilkTouchTestModifier.Serializer().setRegistryName(new ResourceLocation(MODID,"silk_touch_bamboo")));
            }
        }
    }

    private static class SmelterEnchantment extends Enchantment {
        protected SmelterEnchantment(Rarity rarityIn, EnchantmentType typeIn, EquipmentSlotType[] slots) {
            super(rarityIn, typeIn, slots);
        }
    }

    /**
     * The smelting enchantment causes this modifier to be invoked, via the smelting loot_modifier json
     * @author Draco18s
     *
     */
    private static class SmeltingEnchantmentModifier extends LootModifier {
        public SmeltingEnchantmentModifier(ResourceLocation name, ILootCondition[] conditionsIn) {
            super(conditionsIn);

        }

        @Override
        public List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
            ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
            generatedLoot.forEach((stack) -> ret.add(smelt(stack, context)));
            return ret;
        }

        private static ItemStack smelt(ItemStack stack, LootContext context) {
            return context.getWorld().getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(stack), context.getWorld())
                    .map(FurnaceRecipe::getRecipeOutput)
                    .filter(itemStack -> !itemStack.isEmpty())
                    .map(itemStack -> ItemHandlerHelper.copyStackWithSize(itemStack, stack.getCount() * itemStack.getCount()))
                    .orElse(stack);
        }

        private static class Serializer extends LootModifier.Serializer<SmeltingEnchantmentModifier> {
            @Override
            public SmeltingEnchantmentModifier read(ResourceLocation name, JsonObject json, ILootCondition[] conditionsIn) {
                return new SmeltingEnchantmentModifier(name, conditionsIn);
            }
        }
    }

    /**
     * When harvesting blocks with bamboo, this modifier is invoked, via the silk_touch_bamboo loot_modifier json
     * @author Draco18s
     *
     */
    private static class SilkTouchTestModifier extends LootModifier {
        public SilkTouchTestModifier(ResourceLocation name, ILootCondition[] conditionsIn) {
            super(conditionsIn);
        }

        @Override
        public List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
            ItemStack ctxTool = context.get(LootParameters.TOOL);
            ItemStack fakeTool = ctxTool.copy();
            fakeTool.addEnchantment(Enchantments.SILK_TOUCH, 1);
            if(ItemStack.areItemsEqual(ctxTool, fakeTool) && EnchantmentHelper.getEnchantments(ctxTool).containsKey(Enchantments.SILK_TOUCH)) return generatedLoot;
            LootContext.Builder builder = new LootContext.Builder(context.getWorld())
                    .withParameter(LootParameters.BLOCK_STATE, context.get(LootParameters.BLOCK_STATE))
                    .withRandom(context.getWorld().rand)
                    .withParameter(LootParameters.POSITION, context.get(LootParameters.POSITION))
                    .withParameter(LootParameters.TOOL, fakeTool)
                    .withNullableParameter(LootParameters.BLOCK_ENTITY, context.get(LootParameters.BLOCK_ENTITY));
            if(context.has(LootParameters.THIS_ENTITY)) {
                builder.withParameter(LootParameters.THIS_ENTITY, context.get(LootParameters.THIS_ENTITY));
            }
            LootContext ctx = builder.build(LootParameterSets.BLOCK);
            LootTable loottable = context.getWorld().getServer().getLootTableManager().getLootTableFromLocation(context.get(LootParameters.BLOCK_STATE).getBlock().getLootTable());
            return loottable.generate(ctx);
        }

        private static class Serializer extends LootModifier.Serializer<SilkTouchTestModifier> {
            @Override
            public SilkTouchTestModifier read(ResourceLocation name, JsonObject json, ILootCondition[] conditionsIn) {
                return new SilkTouchTestModifier(name, conditionsIn);
            }
        }
    }

    /**
     * When harvesting wheat with shears, this modifier is invoked via the wheat_harvest loot_modifier json 
     * @author Draco18s
     *
     */
    private static class WheatSeedsConverterModifier extends LootModifier {
        private final int numSeedsToConvert;
        private final Item itemToCheck;
        private final Item itemReward;
        public WheatSeedsConverterModifier(ILootCondition[] conditionsIn, int numSeeds, Item itemCheck, Item reward) {
            super(conditionsIn);
            numSeedsToConvert = numSeeds;
            itemToCheck = itemCheck;
            itemReward = reward;
        }

        @Override
        public List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
            //
            // Additional conditions can be checked, though as much logic as possible should be parameterized via JSON data.
            //
            int numSeeds = 0;
            for(ItemStack stack : generatedLoot) {
                if(stack.getItem() == itemToCheck)
                    numSeeds+=stack.getCount();
            }
            if(numSeeds >= numSeedsToConvert) {
                generatedLoot.removeIf(x -> x.getItem() == itemToCheck);
                generatedLoot.add(new ItemStack(itemReward, (numSeeds/numSeedsToConvert)));
                numSeeds -= (numSeeds/numSeedsToConvert)*numSeedsToConvert;
                if(numSeeds > 0)
                    generatedLoot.add(new ItemStack(itemToCheck, numSeeds));
            }
            return generatedLoot;
        }

        private static class Serializer extends LootModifier.Serializer<WheatSeedsConverterModifier> {

            @Override
            public WheatSeedsConverterModifier read(ResourceLocation name, JsonObject object, ILootCondition[] conditionsIn) {
                int numSeeds = JSONUtils.getInt(object, "numSeeds");
                Item seed = ForgeRegistries.ITEMS.getValue(new ResourceLocation((JSONUtils.getString(object, "seedItem"))));
                Item wheat = ForgeRegistries.ITEMS.getValue(new ResourceLocation(JSONUtils.getString(object, "replacement")));
                return new WheatSeedsConverterModifier(conditionsIn, numSeeds, seed, wheat);
            }
        }
    }
}
