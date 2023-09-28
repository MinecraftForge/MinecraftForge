/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.test;

import java.util.Optional;

import net.minecraft.DetectedVersion;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

/* A place where I can put common utility stuff for now. Until I re-write the test codebase. */
@Mod(Empty.MOD_ID)
public class Empty {
    public static final String MOD_ID = "empty";
    public static final ResourceKey<CreativeModeTab> TAB = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(MOD_ID, "test_items"));

    public Empty() {
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.register(this);
    }

    @SubscribeEvent
    public void onRegister(RegisterEvent event) {
        event.register(Registries.CREATIVE_MODE_TAB, h -> {
            h.register(TAB, CreativeModeTab.builder()
                .icon(() -> new ItemStack(Items.STICK))
                .title(Component.literal("Test Items"))
                .build()
            );
        });
    }

    @SubscribeEvent
    public void onDataGen(GatherDataEvent event) {
        var gen = event.getGenerator();
        var packOutput = gen.getPackOutput();

        gen.addProvider(true, new PackMetadataGenerator(packOutput)
            .add(PackMetadataSection.TYPE, new PackMetadataSection(
                Component.literal("Forge tests resource pack"),
                DetectedVersion.BUILT_IN.getPackVersion(PackType.CLIENT_RESOURCES),
                Optional.empty()
                //Arrays.stream(PackType.values()).collect(Collectors.toMap(Function.identity(), DetectedVersion.BUILT_IN::getPackVersion))
            ))
        );
    }
}
