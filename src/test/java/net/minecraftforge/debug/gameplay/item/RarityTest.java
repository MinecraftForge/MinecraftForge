package net.minecraftforge.debug.gameplay.item;

import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ForgeRarity;
import net.minecraftforge.items.IForgeRarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.test.BaseTestMod;

@Mod(RarityTest.MOD_ID)
public class RarityTest extends BaseTestMod {
    static final String MOD_ID = "raritytest";

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final DeferredRegister<IForgeRarity> RARITIES = DeferredRegister.create(ForgeRegistries.FORGE_RARITY, MOD_ID);

    public static final RegistryObject<ForgeRarity> LEGENDARY = RARITIES.register("legendary", () -> new ForgeRarity(ChatFormatting.GOLD));

    public static final RegistryObject<Item> TEST_ITEM = ITEMS.register("test", () -> new TestItem(new Item.Properties().rarity(LEGENDARY::get)));
    public static final RegistryObject<Item> TEST_ITEM_2 = ITEMS.register("test2", () -> new Item(new Item.Properties().rarity(() -> Rarity.UNCOMMON)));


    public static class TestItem extends Item {

        public TestItem(Properties p_41383_) {
            super(p_41383_);
        }

        @Override
        public InteractionResult useOn(UseOnContext p_41427_) {
            var lvl = p_41427_.getLevel();
            if (!lvl.isClientSide()) {
                p_41427_.getItemInHand().set(ForgeMod.FORGE_RARITY_COMPONENT.get(), LEGENDARY.get());
            }
            return super.useOn(p_41427_);
        }
    }
}
