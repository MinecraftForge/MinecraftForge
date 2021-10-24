package net.minecraftforge.debug.item;

import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

@Mod("custom_fishing_rod_test")
public class CustomFishingRodTest {

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "custom_fishing_rod_test");
    private static final RegistryObject<Item> FISHING_ROD = ITEMS.register("fishing_rod", () -> new FishingRodItem(new Item.Properties().stacksTo(1).durability(10).tab(CreativeModeTab.TAB_TOOLS)));

    public CustomFishingRodTest() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::gatherData);
    }

    private void gatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(new CustomItemTagsProvider(event.getGenerator(), "custom_fishing_rod_test", event.getExistingFileHelper()));
    }

    private void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemProperties.register(FISHING_ROD.get(), new ResourceLocation("custom_fishing_rod_test", "cast"), (stack, level, entity, i) -> {
            if (entity != null) {
                boolean holdingRodInMain = entity.getMainHandItem() == stack;
                boolean holdingRodInOff = entity.getOffhandItem() == stack;
                if (entity.getMainHandItem().getItem() instanceof FishingRodItem) {
                    holdingRodInOff = false;
                }

                if (entity instanceof Player player) {
                    if (player.fishing != null && holdingRodInMain || holdingRodInOff) {
                        return 1.0F; // cast
                    }
                }
            }
            return 0.0F;
        }));
    }

    private static class CustomItemTagsProvider extends ItemTagsProvider {

        public CustomItemTagsProvider(DataGenerator generator, String modId, @Nullable ExistingFileHelper existingFileHelper) {
            super(generator, new BlockTagsProvider(generator, modId, existingFileHelper), modId, existingFileHelper);
        }

        @Override
        protected void addTags() {
            this.tag(Tags.Items.FISHING_ROD).add(FISHING_ROD.get());
        }

    }
}
