/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug.modules.closed;

import java.lang.module.ModuleDescriptor;
import java.util.Set;
import net.minecraftsingularity.data.event.GatherDataEvent;
import net.minecraftsingularity.debug.modules.closed.api.PublicUtils;
import net.minecraftsingularity.debug.modules.closed.internala.InternalA;
import net.minecraftsingularity.debug.modules.closed.internalb.InternalB;
import net.minecraftsingularity.fml.common.Mod;
import net.minecraftsingularity.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftsingularity.test.BaseTestMod;
import net.minecraftsingularity.test.ModuleProvider;

@Mod(ClosedMod.MODID)
public class ClosedMod extends BaseTestMod {
    public static final String MODID = "closed_module";

    public ClosedMod(FMLJavaModLoadingContext context) {
        super(context, false, false);
        GatherDataEvent.getBus(modBus).addListener(this::gatherData);
    }

    private void gatherData(GatherDataEvent event) {
        var out = event.getGenerator().getPackOutput();
        event.getGenerator().addProvider(true, new ModuleProvider(out, module()));
    }

    private ModuleDescriptor module() {
        var self = ClosedMod.class.getPackageName();
        var api = PublicUtils.class.getPackageName();
        var internalA = InternalA.class.getPackageName();
        var internalB = InternalB.class.getPackageName();
        var singularity = Set.of(
            "net.minecraftsingularity.javafmlmod",
            "net.minecraftsingularity.eventbus",
            "net.minecraftsingularity.fmlcore",
            "net.minecraftsingularity.singularity"
        );
        var bldr = ModuleDescriptor.newModule(self)
            .packages(Set.of(self, api, internalA, internalB))
            .opens(api)
            .exports(api)
            .opens(self, singularity);
        singularity.forEach(bldr::requires);
        return bldr.build();
    }
}
