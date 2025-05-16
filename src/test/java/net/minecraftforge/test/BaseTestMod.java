/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.mojang.serialization.Lifecycle;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.ForgeGameTestHooks;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.DeferredRegisterData;
import net.minecraftforge.registries.RegisterEvent;

public abstract class BaseTestMod {
    private List<Function<HolderLookup.Provider, ItemStack>> testItems = new ArrayList<>();
    protected final IEventBus modBus;

    protected final List<Map<ResourceKey<? extends Registry<?>>, DeferredRegisterData<?>>> dataRegistries = new ArrayList<>();
    protected final Set<DeferredRegisterData<?>> myDataRegistries = new HashSet<>();

    protected final Map<ResourceLocation, ForgeGameTestHooks.TestReference> tests;

    public BaseTestMod(FMLJavaModLoadingContext context) {
        this(context, true);
    }

    public BaseTestMod(FMLJavaModLoadingContext context, boolean register) {
        this.modBus = context.getModEventBus();

        if (!register) {
            this.tests = Collections.emptyMap();
            return;
        }

        modBus.register(this);

        tests = ForgeGameTestHooks.gatherTests(getClass(), this);

        Class<?> cls = getClass();
        while (cls != BaseTestMod.class) {
            var data = new LinkedHashMap<ResourceKey<? extends Registry<?>>, DeferredRegisterData<?>>();
            for (var field : cls.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers()))
                    continue;

                if (field.getType() == DeferredRegister.class) {
                    DeferredRegister<?> dr = getField(field, null);
                    dr.register(modBus);
                } else if (field.getType() == DeferredRegisterData.class) {
                    DeferredRegisterData<?> dr = getField(field, null);
                    data.put(dr.getRegistryKey(), dr);
                    if (cls == getClass())
                        myDataRegistries.add(dr);
                }
            }

            if (!data.isEmpty())
                dataRegistries.addFirst(data);

            cls = cls.getSuperclass();
        }

        if (!myDataRegistries.isEmpty())
            modBus.addListener(this::generateDataRegistries);

        if (!tests.isEmpty()) {
            modBus.addListener(this::registerTestFunctions);
            modBus.addListener(this::generateGameTests);
        }
    }


    protected String modid() {
        return modid(this.getClass());
    }

    protected static String smodid() {
        return modid(StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass());
    }

    private static String modid(Class<?> cls) {
        var mod = cls.getAnnotation(Mod.class);
        if (mod == null)
            throw new IllegalStateException("Could not find @Mod on " + cls.getName());
        return mod.value();
    }

    protected static ResourceLocation rl(String path) {
        var modid = modid(StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass());
        return ResourceLocation.fromNamespaceAndPath(modid, path);
    }

    protected static ResourceLocation rl(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    protected static BlockBehaviour.Properties name(String namespace, String name, BlockBehaviour.Properties peops) {
        return peops.setId(ResourceKey.create(Registries.BLOCK, rl(namespace, name)));
    }

    protected static Item.Properties name(String namespace, String name, Item.Properties peops) {
        return peops.setId(ResourceKey.create(Registries.ITEM, rl(namespace, name)));
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

    protected void generateDataRegistries(GatherDataEvent event) {
        if (!event.includeServer())
            return;

        var gen = event.getGenerator();
        var packOutput = gen.getPackOutput();
        var registrySet = VanillaRegistries.builder();

        for (var level : this.dataRegistries) {
            for (var reg : level.values())
                registrySet.add(reg);
        }

        var toDump = new HashSet<ResourceKey<?>>();
        for (var reg : myDataRegistries) {
            for (var entry : reg.getEntries())
                toDump.add(entry.getKey());
        }

        var modid = modid();
        gen.addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(packOutput, event.getLookupProvider(), registrySet, modid) {
            @Override
            public String getName() {
                return "Data Registries: " + modid;
            }

            @Override
            protected boolean shouldDump(ResourceKey<?> key) {
                return toDump.contains(key);
            }
        });
    }

    protected void registerTestFunctions(RegisterEvent event) {
        if (event.getRegistryKey() != Registries.TEST_FUNCTION)
            return;

        for (var entry : this.tests.entrySet())
            event.register(Registries.TEST_FUNCTION, entry.getKey(), entry.getValue()::consumer);
    }

    protected void generateGameTests(GatherDataEvent event) {
        if (!event.includeServer())
            return;

        var gen = event.getGenerator();
        var packOutput = gen.getPackOutput();
        var registrySet = VanillaRegistries.builder();

        for (var level : this.dataRegistries) {
            for (var reg : level.values())
                registrySet.add(reg);
        }

        registrySet
            .add(Registries.TEST_INSTANCE, Lifecycle.stable(), ctx -> {
                var envs = ctx.lookup(Registries.TEST_ENVIRONMENT);
                for (var entry : tests.entrySet()) {
                    var rdata = entry.getValue().data();
                    var env = envs.getOrThrow(ResourceKey.create(Registries.TEST_ENVIRONMENT, rdata.environment()));

                    var edata = new TestData<Holder<TestEnvironmentDefinition>>(
                        env,
                        rdata.structure(),
                        rdata.maxTicks(),
                        rdata.setupTicks(),
                        rdata.required(),
                        rdata.rotation(),
                        rdata.manualOnly(),
                        rdata.maxAttempts(),
                        rdata.requiredSuccesses(),
                        rdata.skyAccess()
                    );

                    var funcKey = ResourceKey.create(Registries.TEST_FUNCTION, entry.getKey());
                    var func = BuiltInRegistries.TEST_FUNCTION.get(funcKey).orElse(null);
                    if (func == null)
                        throw new IllegalStateException("Could not find referenced function `" + entry.getKey());

                    var testKey = ResourceKey.create(Registries.TEST_INSTANCE, entry.getKey());
                    ctx.register(testKey, new FunctionGameTestInstance(funcKey, edata));
                }
            });

        var modid = modid();
        gen.addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(packOutput, event.getLookupProvider(), registrySet, modid) {
            @Override
            public String getName() {
                return "Game Tests: " + modid;
            }

            @Override
            protected boolean shouldDump(ResourceKey<?> key) {
                return tests.containsKey(key.location());
            }
        });
    }

    @SuppressWarnings("unchecked")
    protected static <R> R getField(Field field, Object instance) {
        try {
            field.setAccessible(true);
            return (R)field.get(instance);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            return sneak(e);
        }
    }

    @SuppressWarnings("unchecked")
    protected static <E extends Throwable, R> R sneak(Throwable e) throws E {
        throw (E)e;
    }
}
