/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.modules.closedtest;

import java.lang.module.ModuleDescriptor;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.debug.modules.closed.api.PublicUtils;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.test.BaseTestMod;
import net.minecraftforge.test.ModuleProvider;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

@Mod(ClosedTestsMod.MODID)
@GameTestHolder("forge.module.closed")
public class ClosedTestsMod extends BaseTestMod {
    public static final String MODID = "closed_module_test";

    public ClosedTestsMod(FMLJavaModLoadingContext context) {
        super(context);
    }

    @SubscribeEvent
    public void runData(GatherDataEvent event) {
        var out = event.getGenerator().getPackOutput();
        event.getGenerator().addProvider(true, new ModuleProvider(out, module()));
    }

    private ModuleDescriptor module() {
        return ModuleDescriptor.newOpenModule(getClass().getPackageName())
            .requires("net.minecraftforge.eventbus")
            .requires("net.minecraftforge.fmlcore")
            .requires("net.minecraftforge.forge")
            .requires("net.minecraftforge.javafmlmod")
            .requires("net.minecraftforge.debug.modules.closed")
            .build();
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void can_reflect_exported(GameTestHelper helper) throws ReflectiveOperationException {
        var method = PublicUtils.class.getDeclaredMethod("publicMethod");
        method.invoke(null);
        helper.succeed();
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void cant_reflect_internal(GameTestHelper helper) throws ReflectiveOperationException {
        try {
            var cls = Class.forName("net.minecraftforge.debug.modules.closed.internal.InternalUtils");
            var method = cls.getDeclaredMethod("internalMethod");
            method.invoke(null);
            helper.fail("Invoked internal method without error");
        } catch (IllegalAccessException e) {
            helper.succeed();
        }
    }
}
