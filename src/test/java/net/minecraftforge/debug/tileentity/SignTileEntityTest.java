package net.minecraftforge.debug.tileentity;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.minecraftforge.debug.tileentity.SignTileEntityTest.MOD_ID;

@Mod(MOD_ID)
@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SignTileEntityTest {

    static final String MOD_ID = "sign_tile_entity_test";

    private static final String ITEM_KEY = "test_sign";
    private static final String SIGN_KEY = "test_sign";
    private static final String WALL_SIGN_KEY = "test_wall_sign";

    private static final Block TEST_SIGN = new StandingSignBlock(Block.Properties.create(Material.WOOD, MaterialColor.GREEN).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD));
    private static final Block TEST_WALL_SIGN = new WallSignBlock(Block.Properties.create(Material.WOOD, MaterialColor.GREEN).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD).lootFrom(TEST_SIGN));
    private static final Item TEST_SIGN_ITEM = new SignItem((new Item.Properties()).maxStackSize(16).group(ItemGroup.DECORATIONS), TEST_SIGN, TEST_WALL_SIGN);

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        TEST_SIGN_ITEM.setRegistryName(MOD_ID, ITEM_KEY);
        event.getRegistry().register(TEST_SIGN_ITEM);
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        TEST_SIGN.setRegistryName(MOD_ID, SIGN_KEY);
        event.getRegistry().register(TEST_SIGN);
        TEST_WALL_SIGN.setRegistryName(MOD_ID, WALL_SIGN_KEY);
        event.getRegistry().register(TEST_WALL_SIGN);
    }

    @SubscribeEvent
    public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
        TileEntityType.SIGN.addValidBlocks(TEST_SIGN, TEST_WALL_SIGN);
    }
}
