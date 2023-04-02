/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.mdk;

import net.minecraft.DetectedVersion;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mod(MdkDatagen.MOD_ID)
public class MdkDatagen
{
    static final String MOD_ID = "mdk_datagen";

    public MdkDatagen()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(MdkDatagen::gatherData);
    }

    private static void gatherData(GatherDataEvent event)
    {
        var gen = event.getGenerator();
        var packOutput = gen.getPackOutput();
        gen.addProvider(true, new PackMetadataGenerator(packOutput)
                .add(PackMetadataSection.TYPE, new PackMetadataSection(
                        Component.literal("${mod_id} resources"),
                        DetectedVersion.BUILT_IN.getPackVersion(PackType.CLIENT_RESOURCES),
                        Arrays.stream(PackType.values()).collect(Collectors.toMap(Function.identity(), DetectedVersion.BUILT_IN::getPackVersion))
                ))
        );
    }
}
