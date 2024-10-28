/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.modules.closed;

import java.lang.module.ModuleDescriptor;
import java.util.Set;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.debug.modules.closed.api.PublicUtils;
import net.minecraftforge.debug.modules.closed.internala.InternalA;
import net.minecraftforge.debug.modules.closed.internalb.InternalB;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.test.BaseTestMod;
import net.minecraftforge.test.ModuleProvider;

@Mod(ClosedMod.MODID)
public class ClosedMod extends BaseTestMod {
    public static final String MODID = "closed_module";

    public ClosedMod(FMLJavaModLoadingContext context) {
        super(context);
    }

    @SubscribeEvent
    public void runData(GatherDataEvent event) {
        var out = event.getGenerator().getPackOutput();
        event.getGenerator().addProvider(true, new ModuleProvider(out, module()));
    }

    private ModuleDescriptor module() {
        var self = ClosedMod.class.getPackageName();
        var api = PublicUtils.class.getPackageName();
        var internalA = InternalA.class.getPackageName();
        var internalB = InternalB.class.getPackageName();
        var forge = Set.of(
            "net.minecraftforge.javafmlmod",
            "net.minecraftforge.eventbus",
            "net.minecraftforge.fmlcore",
            "net.minecraftforge.forge"
        );
        var bldr = ModuleDescriptor.newModule(self)
            .packages(Set.of(self, api, internalA, internalB))
            .opens(api)
            .exports(api)
            .opens(self, forge);
        forge.forEach(bldr::requires);
        return bldr.build();
    }
}
