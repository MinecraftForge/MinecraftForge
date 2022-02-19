/*
 * Minecraft Forge
 * Copyright (c) 2016-2022.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.debug.entity;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod("ender_masking_attribute_test")
public class EnderMaskingAttributeTest {
    private static final boolean ENABLED = true;
    private static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, "ender_masking_attribute_test");
    private static final RegistryObject<MobEffect> ENDER_MASKING = EFFECTS.register("ender_masking", () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0x6900a1){}.addAttributeModifier(ForgeMod.ENDER_MASKING.get(), "9bbcc190-1257-470c-9c86-3ddf0157c681", 1, AttributeModifier.Operation.ADDITION));

    public EnderMaskingAttributeTest() {
        if (ENABLED) {
            EFFECTS.register(FMLJavaModLoadingContext.get().getModEventBus());
        }
    }
}
