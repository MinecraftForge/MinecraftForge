package net.minecraftforge.debug.gameplay.item;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.test.BaseTestMod;

@Mod(RarityTest.MOD_ID)
public class RarityTest extends BaseTestMod {
    static final String MOD_ID = "raritytest";

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static final Rarity CUSTOM = Rarity.create("LEGENDARY", 4, "legendary", ChatFormatting.GOLD);

    public static final RegistryObject<Item> TEST_ITEM = ITEMS.register("test", () -> new Item(new Item.Properties().rarity(CUSTOM)));
}
