/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.item;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(CustomCrossbowTest.MOD_ID)
public class CustomCrossbowTest
{
    public static final String MOD_ID = "custom_crossbow_test";
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    private static final RegistryObject<Item> TEST_CROSSBOW = ITEMS.register("test_crossbow", () -> new CrossbowItem(new Item.Properties().tab(CreativeModeTab.TAB_COMBAT).durability(500)));

    public CustomCrossbowTest()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        bus.addListener(this::clientSetup);
        bus.addListener(this::gatherData);
    }

    private void clientSetup(final FMLClientSetupEvent event)
    {
        ItemProperties.register(TEST_CROSSBOW.get(), new ResourceLocation("pull"), (itemStack, clientLevel, livingEntity, num) -> {
            if (livingEntity == null)
            {
                return 0.0F;
            }
            else
            {
                if (CrossbowItem.isCharged(itemStack))
                {
                    return 0.0F;
                }
                else {
                    return (itemStack.getUseDuration() - livingEntity.getUseItemRemainingTicks()) / (float) CrossbowItem.getChargeDuration(itemStack);
                }
            }
        });

        ItemProperties.register(TEST_CROSSBOW.get(), new ResourceLocation("pulling"), (itemStack, clientLevel, livingEntity, num) -> {
            return (livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack && !CrossbowItem.isCharged(itemStack)) ? 1.0F : 0.0F;
        });

        ItemProperties.register(TEST_CROSSBOW.get(), new ResourceLocation("charged"), (itemStack, clientLevel, livingEntity, num) -> {
            return (livingEntity != null && CrossbowItem.isCharged(itemStack)) ? 1.0F : 0.0F;
        });

        ItemProperties.register(TEST_CROSSBOW.get(), new ResourceLocation("firework"), (itemStack, clientLevel, livingEntity, num) -> {
            return (livingEntity != null && CrossbowItem.isCharged(itemStack) && CrossbowItem.containsChargedProjectile(itemStack, Items.FIREWORK_ROCKET)) ? 1.0F : 0.0F;
        });
    }

    private void gatherData(final GatherDataEvent event)
    {
        DataGenerator dataGenerator = event.getGenerator();
        dataGenerator.addProvider(new ModelGenerator(dataGenerator, MOD_ID, event.getExistingFileHelper()));
    }

    private static class ModelGenerator extends ItemModelProvider {
        public ModelGenerator(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
            super(generator, modid, existingFileHelper);
        }

        @Override
        protected void registerModels() {
            ModelFile pulling0 = withExistingParent("test_crossbow_pulling_0", mcLoc("item/crossbow"))
                    .texture("layer0", mcLoc("item/crossbow_pulling_0"));
            ModelFile pulling1 = withExistingParent("test_crossbow_pulling_1", mcLoc("item/crossbow"))
                    .texture("layer0", mcLoc("item/crossbow_pulling_1"));
            ModelFile pulling2 = withExistingParent("test_crossbow_pulling_2", mcLoc("item/crossbow"))
                    .texture("layer0", mcLoc("item/crossbow_pulling_2"));
            ModelFile arrow = withExistingParent("test_crossbow_arrow", mcLoc("item/crossbow"))
                    .texture("layer0", mcLoc("item/crossbow_arrow"));
            ModelFile firework = withExistingParent("test_crossbow_firework", mcLoc("item/crossbow"))
                    .texture("layer0", mcLoc("item/crossbow_firework"));

            this.withExistingParent(TEST_CROSSBOW.get().toString(), mcLoc("item/crossbow"))
                    .texture("layer0", mcLoc("item/crossbow_standby"))
                    .override().predicate(mcLoc("pulling"), 1.0f).model(pulling0).end()
                    .override().predicate(mcLoc("pulling"), 1.0f).predicate(mcLoc("pull"), 0.58f).model(pulling1).end()
                    .override().predicate(mcLoc("pulling"), 1.0f).predicate(mcLoc("pull"), 1.0f).model(pulling2).end()
                    .override().predicate(mcLoc("charged"), 1.0f).model(arrow).end()
                    .override().predicate(mcLoc("charged"), 1.0f).predicate(mcLoc("firework"), 1.0f).model(firework).end();
        }
    }
}
