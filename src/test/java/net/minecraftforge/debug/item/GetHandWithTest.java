package net.minecraftforge.debug.item;

import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(GetHandWithTest.MODID)
public class GetHandWithTest {

    //Testing if the new alternative for ProjectileHelper.getHandWith works
    //Skeletons and Illusioners should be able to use the modded bow.
    //Piglins and Pillagers should be able to use the modded crossbow.

    static final String MODID = "get_hand_with_test";
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    private static RegistryObject<Item> MODDED_BOW = ITEMS.register("modded_bow", () ->
            new BowItem(new Item.Properties().group(ItemGroup.COMBAT).maxDamage(384))
    );
    private static RegistryObject<Item> MODDED_CROSSBOW = ITEMS.register("modded_crossbow", () ->
            new CrossbowItem(new Item.Properties().group(ItemGroup.COMBAT).maxDamage(326))
    );

    public GetHandWithTest()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modEventBus);
        modEventBus.addListener(this::onClientSetup);
        modEventBus.addListener(this::onClientSetup);
    }

    private void onClientSetup(FMLClientSetupEvent event)
    {
        RangedWeaponModeLProperties.initBowModelProperties();
        RangedWeaponModeLProperties.initCrossbowModelProperties();
    }

    @OnlyIn(Dist.CLIENT)
    private static class RangedWeaponModeLProperties
    {
        static void initBowModelProperties(){
            ItemModelsProperties.func_239418_a_(MODDED_BOW.get(), new ResourceLocation("pull"), (p_239429_0_, p_239429_1_, p_239429_2_) -> {
                if (p_239429_2_ == null) {
                    return 0.0F;
                } else {
                    return p_239429_2_.getActiveItemStack() != p_239429_0_ ? 0.0F : (float)(p_239429_0_.getUseDuration() - p_239429_2_.getItemInUseCount()) / 20.0F;
                }
            });
            ItemModelsProperties.func_239418_a_(MODDED_BOW.get(), new ResourceLocation("pulling"), (p_239428_0_, p_239428_1_, p_239428_2_) -> {
                return p_239428_2_ != null && p_239428_2_.isHandActive() && p_239428_2_.getActiveItemStack() == p_239428_0_ ? 1.0F : 0.0F;
            });
        }

        static void initCrossbowModelProperties(){
            ItemModelsProperties.func_239418_a_(MODDED_CROSSBOW.get(), new ResourceLocation("pull"), (p_239427_0_, p_239427_1_, p_239427_2_) -> {
                if (p_239427_2_ == null) {
                    return 0.0F;
                } else {
                    return CrossbowItem.isCharged(p_239427_0_) ? 0.0F : (float)(p_239427_0_.getUseDuration() - p_239427_2_.getItemInUseCount()) / (float)CrossbowItem.getChargeTime(p_239427_0_);
                }
            });
            ItemModelsProperties.func_239418_a_(MODDED_CROSSBOW.get(), new ResourceLocation("pulling"), (p_239426_0_, p_239426_1_, p_239426_2_) -> {
                return p_239426_2_ != null && p_239426_2_.isHandActive() && p_239426_2_.getActiveItemStack() == p_239426_0_ && !CrossbowItem.isCharged(p_239426_0_) ? 1.0F : 0.0F;
            });
            ItemModelsProperties.func_239418_a_(MODDED_CROSSBOW.get(), new ResourceLocation("charged"), (p_239425_0_, p_239425_1_, p_239425_2_) -> {
                return p_239425_2_ != null && CrossbowItem.isCharged(p_239425_0_) ? 1.0F : 0.0F;
            });
            ItemModelsProperties.func_239418_a_(MODDED_CROSSBOW.get(), new ResourceLocation("firework"), (p_239424_0_, p_239424_1_, p_239424_2_) -> {
                return p_239424_2_ != null && CrossbowItem.isCharged(p_239424_0_) && CrossbowItem.hasChargedProjectile(p_239424_0_, Items.FIREWORK_ROCKET) ? 1.0F : 0.0F;
            });
        }
    }
}
