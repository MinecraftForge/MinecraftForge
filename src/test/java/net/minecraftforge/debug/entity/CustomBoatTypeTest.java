package net.minecraftforge.debug.entity;

import net.minecraft.block.Blocks;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.item.BoatItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod("custom_boat_type_test")
public class CustomBoatTypeTest {

    public static final String MOD_ID = "custom_boat_type_test";
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    private static final BoatEntity.Type CUSTOM_BOAT_TYPE = BoatEntity.Type
            .register(Blocks.STONE, "custom", new ResourceLocation(MOD_ID, "custom_boat"));

    static {
        ITEMS.register("test_boat", () -> new BoatItem(CUSTOM_BOAT_TYPE,
                new Item.Properties().maxStackSize(1).group(ItemGroup.TRANSPORTATION)));
    }

    public CustomBoatTypeTest() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

}
