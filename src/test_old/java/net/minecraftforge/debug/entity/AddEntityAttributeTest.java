/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug.entity;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftsingularity.event.entity.EntityAttributeModificationEvent;
import net.minecraftsingularity.eventbus.api.listener.SubscribeEvent;
import net.minecraftsingularity.registries.singularityRegistries;
import net.minecraftsingularity.registries.RegistryObject;
import net.minecraftsingularity.fml.common.Mod;
import net.minecraftsingularity.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftsingularity.registries.DeferredRegister;

@Mod("add_entity_attribute_test")
public class AddEntityAttributeTest {
    public static final boolean ENABLE = true;
    private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(singularityRegistries.ATTRIBUTES, "add_entity_attribute_test");
    public static final RegistryObject<Attribute> TEST_ATTR = ATTRIBUTES.register("test_attr", () -> new RangedAttribute("singularity.test_attr", 1.0D, 0.0D, 1024.0D).setSyncable(true));

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
