/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug.modules.automatic;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftsingularity.fml.common.Mod;
import net.minecraftsingularity.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftsingularity.gametest.GameTest;
import net.minecraftsingularity.gametest.GameTestNamespace;
import net.minecraftsingularity.test.BaseTestMod;

@GameTestNamespace("singularity")
@Mod(AutomaticModuleMod.MODID)
public class AutomaticModuleMod extends BaseTestMod {
    public static final String MODID = "automatic_module";

    public AutomaticModuleMod(FMLJavaModLoadingContext context) {
        super(context, false, false);
    }

    @GameTest
    public static void correct_name(GameTestHelper helper) throws ReflectiveOperationException {
        var mod = AutomaticModuleMod.class.getModule();
        if ("net.minecraftsingularity.debug.modules.automatic".equals(mod.getName()))
            helper.succeed();
        else
            helper.fail("Invalid module name: " + mod.getName());
    }
}
