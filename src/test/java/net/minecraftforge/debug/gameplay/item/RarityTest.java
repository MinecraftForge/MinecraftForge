package net.minecraftforge.debug.gameplay.item;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ForgeRarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.test.BaseTestMod;

import java.util.function.Supplier;

@Mod(RarityTest.MOD_ID)
public class RarityTest extends BaseTestMod {
    static final String MOD_ID = "raritytest";

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final DeferredRegister<ForgeRarity> RARITIES = DeferredRegister.create(ForgeRegistries.FORGE_RARITY, MOD_ID);

    public static final RegistryObject<ForgeRarity> LEGENDARY = RARITIES.register("legendary", () -> new ForgeRarity(ChatFormatting.GOLD));

    public static final RegistryObject<Item> TEST_ITEM = ITEMS.register("test", () -> new Item(new Item.Properties().rarity(LEGENDARY.get())));
}
