/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.test;

import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
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
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftsingularity.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftsingularity.data.event.GatherDataEvent;
import net.minecraftsingularity.event.BuildCreativeModeTabContentsEvent;
import net.minecraftsingularity.eventbus.api.bus.BusGroup;
import net.minecraftsingularity.fml.common.Mod;
import net.minecraftsingularity.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftsingularity.gametest.singularityGameTestHooks;
import net.minecraftsingularity.registries.DeferredRegister;
import net.minecraftsingularity.registries.DeferredRegisterData;
import net.minecraftsingularity.registries.RegisterEvent;
import net.minecraftsingularity.unsafe.UnsafeHacks;

public abstract class BaseTestMod {
    private final List<Function<HolderLookup.Provider, ItemStack>> testItems = new ArrayList<>();
    protected final BusGroup modBus;

    protected final List<Map<ResourceKey<? extends Registry<?>>, DeferredRegisterData<?>>> dataRegistries = new ArrayList<>();
    protected final Set<DeferredRegisterData<?>> myDataRegistries = new HashSet<>();

    protected final Map<Identifier, singularityGameTestHooks.TestReference> tests;

    public BaseTestMod(FMLJavaModLoadingContext context) {
        this(context, true, true);
    }

    public BaseTestMod(FMLJavaModLoadingContext context, boolean registerSelf, boolean registerDeferred) {
        this.modBus = context.getModBusGroup();

        if (registerSelf) {
            modBus.register(LookupHelper.INSTANCE.in(this.getClass()), this);
            BuildCreativeModeTabContentsEvent.BUS.addListener(this::onCreativeModeTabBuildContents);
        }

        tests = singularityGameTestHooks.gatherTests(getClass(), this);

        if (registerDeferred) {
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
                GatherDataEvent.getBus(modBus).addListener(this::generateDataRegistries);
        }

        if (!tests.isEmpty()) {
            RegisterEvent.getBus(modBus).addListener(this::registerTestFunctions);
            GatherDataEvent.getBus(modBus).addListener(this::generateGameTests);
        }
    }


    private static final class LookupHelper {
        private static final Lookup INSTANCE;
        static {
            try {
                var lookupField = Lookup.class.getDeclaredField("IMPL_LOOKUP");
                UnsafeHacks.setAccessible(lookupField);
                INSTANCE = (Lookup) lookupField.get(null);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
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

    protected static Identifier rl(String path) {
        var modid = modid(StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass());
        return Identifier.fromNamespaceAndPath(modid, path);
    }

    protected static Identifier rl(String namespace, String path) {
        return Identifier.fromNamespaceAndPath(namespace, path);
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

                    var edata = new TestData<Holder<TestEnvironmentDefinition<?>>>(
                        env,
                        rdata.structure(),
                        rdata.maxTicks(),
                        rdata.setupTicks(),
                        rdata.required(),
                        rdata.rotation(),
                        rdata.manualOnly(),
                        rdata.maxAttempts(),
                        rdata.requiredSuccesses(),
                        rdata.skyAccess(),
                        rdata.padding()
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
                return tests.containsKey(key.identifier());
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
