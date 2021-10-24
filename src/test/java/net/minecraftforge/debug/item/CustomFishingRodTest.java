package net.minecraftforge.debug.item;

import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod("custom_fishing_rod_test")
public class CustomFishingRodTest {

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "custom_fishing_rod_test");
    private static final RegistryObject<Item> FISHING_ROD = ITEMS.register("fishing_rod", () -> new CustomFishingRodItem(new Item.Properties().stacksTo(1).durability(10).tab(CreativeModeTab.TAB_TOOLS)));

    public CustomFishingRodTest() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
    }

    private void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(FISHING_ROD.get(), new ResourceLocation("custom_fishing_rod_test", "cast"), (stack, level, entity, i) -> {
                if (entity == null) {
                    return 0.0F;
                } else {
                    boolean flag = entity.getMainHandItem() == stack;
                    boolean flag1 = entity.getOffhandItem() == stack;
                    if (entity.getMainHandItem().getItem() instanceof FishingRodItem) {
                        flag1 = false;
                    }

                    return (flag || flag1) && entity instanceof Player && ((Player) entity).fishing != null ? 1.0F : 0.0F;
                }
            });
        });
    }

    private static class CustomFishingRodItem extends FishingRodItem {

        public CustomFishingRodItem(Item.Properties props) {
            super(props);
        }

    }
}
