/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.entity;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;

@Mod("add_entity_attribute_test")
public class AddEntityAttributeTest {
    public static final boolean ENABLE = true;
    private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, "add_entity_attribute_test");
    public static final RegistryObject<Attribute> TEST_ATTR = ATTRIBUTES.register("test_attr", () -> new RangedAttribute("forge.test_attr", 1.0D, 0.0D, 1024.0D).setSyncable(true));

    public AddEntityAttributeTest() {
        if (ENABLE) {
            ATTRIBUTES.register(FMLJavaModLoadingContext.get().getModEventBus());
            FMLJavaModLoadingContext.get().getModEventBus().register(this);
        }
    }

    @SubscribeEvent
    public void entityAttributeSetup(EntityAttributeModificationEvent event) {
        event.getTypes().forEach(entityType -> {
            if (!event.has(entityType, TEST_ATTR.get())){
                event.add(entityType, TEST_ATTR.get());
            }
        });
    }
}
