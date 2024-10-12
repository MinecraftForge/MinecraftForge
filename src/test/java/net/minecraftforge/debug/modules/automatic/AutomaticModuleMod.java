/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.modules.automatic;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.test.BaseTestMod;

@Mod(AutomaticModuleMod.MODID)
@GameTestHolder("forge.module.automatic")
public class AutomaticModuleMod extends BaseTestMod {
    public static final String MODID = "automatic_module";

    public AutomaticModuleMod(FMLJavaModLoadingContext context) {
        super(context);
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void correct_name(GameTestHelper helper) throws ReflectiveOperationException {
        var mod = AutomaticModuleMod.class.getModule();
        if ("net.minecraftforge.debug.modules.automatic".equals(mod.getName()))
            helper.succeed();
        else
            helper.fail("Invalid module name: " + mod.getName());
    }
}
