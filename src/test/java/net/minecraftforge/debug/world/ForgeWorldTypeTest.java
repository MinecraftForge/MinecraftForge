/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.world;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ForgeWorldPresetScreens;
import net.minecraftforge.common.world.ForgeWorldPreset;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;

@Mod("forge_world_type_test")
public class ForgeWorldTypeTest
{
    @ObjectHolder(registryName = "forge:world_types", value = "forge_world_type_test:test_world_type")
    public static ForgeWorldPreset testWorldType;

    public ForgeWorldTypeTest()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(ForgeWorldPreset.class, this::registerWorldTypes);
    }

    private void registerWorldTypes(RegistryEvent.Register<ForgeWorldPreset> event)
    {
        event.getRegistry().register(
                new ResourceLocation("forge_world_type_test", "test_world_type"),
                new ForgeWorldPreset(WorldGenSettings::makeDefaultOverworld)
        );
        event.getRegistry().register(
                new ResourceLocation("forge_world_type_test", "test_world_type2"),
                new ForgeWorldPreset(this::createChunkGenerator)
        );
    }

    private ChunkGenerator createChunkGenerator(RegistryAccess registry, long seed, String settings)
    {
        return WorldGenSettings.makeDefaultOverworld(registry, seed);
    }

    @Mod.EventBusSubscriber(modid="forge_world_type_test", value=Dist.CLIENT, bus=Bus.MOD)
    public static class ForgeWorldTypeTestClientModEvents
    {
        @SubscribeEvent
        public static void registerWorldTypeScreenFactories(FMLClientSetupEvent event)
        {
            ForgeWorldPresetScreens.registerPresetEditor(testWorldType, (returnTo, dimensionGeneratorSettings) -> new Screen(testWorldType.getDisplayName())
            {
                @Override
                protected void init()
                {
                    super.init();

                    addRenderableWidget(new Button(0, 0, 120, 20, new TextComponent("close"), btn -> {
                        Minecraft.getInstance().setScreen(returnTo);
                    }));
                }
            });
        }
    }
}
