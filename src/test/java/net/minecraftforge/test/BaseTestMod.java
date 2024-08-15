/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;

public abstract class BaseTestMod {
    private List<Function<HolderLookup.Provider, ItemStack>> testItems = new ArrayList<>();

    protected BaseTestMod() {
        // TODO: Some form of enable flag?

        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.register(this);

        Class<?> cls = getClass();
        while (cls != BaseTestMod.class) {
            for (var field : cls.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) && field.getType() == DeferredRegister.class) {
                    field.setAccessible(true);
                    DeferredRegister<?> dr = getField(field, null);
                    dr.register(modBus);
                }

            }
            cls = cls.getSuperclass();
        }
    }

    protected static ResourceLocation rl(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    protected void testItem(Function<HolderLookup.Provider, ItemStack> supplier) {
        this.testItems.add(supplier);
    }

    @SubscribeEvent
    protected void onCreativeModeTabBuildContents(BuildCreativeModeTabContentsEvent event) {
        var entries = event.getEntries();
        var lookup = event.getParameters().holders();
        if (event.getTabKey() == TestHelperMod.TAB) {
            for (var s : testItems)
                entries.put(s.apply(lookup), TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }

    @SuppressWarnings("unchecked")
    protected static <R> R getField(Field field, Object instance) {
        try {
            return (R)field.get(instance);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            return sneak(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable, R> R sneak(Throwable e) throws E {
        throw (E)e;
    }
}
