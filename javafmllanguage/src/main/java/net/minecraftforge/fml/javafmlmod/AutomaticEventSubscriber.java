/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.javafmlmod;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;
import net.minecraftforge.eventbus.api.listener.EventListener;
import net.minecraftforge.eventbus.api.listener.ObjBooleanBiConsumer;
import net.minecraftforge.eventbus.api.listener.Priority;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.eventbus.internal.Event;
import net.minecraftforge.fml.Logging;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.forgespi.language.ModFileScanData.AnnotationData;
import net.minecraftforge.forgespi.language.ModFileScanData.EnumData;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.unsafe.UnsafeHacks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.Type;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Automatic eventbus subscriber - reads {@link EventBusSubscriber}
 * annotations and passes the class instances to the {@link Bus}
 * defined by the annotation. Defaults to {@code MinecraftForge#EVENT_BUS}
 */
public class AutomaticEventSubscriber {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Type AUTO_SUBSCRIBER = Type.getType(EventBusSubscriber.class);
    private static final Type MOD_TYPE = Type.getType(Mod.class);
    private static final Type ONLY_IN_TYPE = Type.getType(OnlyIn.class);

    public static void inject(ModContainer mod, ModFileScanData scanData, ClassLoader loader) {
        if (scanData == null) return;
        LOGGER.debug(Logging.LOADING, "Attempting to inject @EventBusSubscriber classes into the eventbus for {}", mod.getModId());

        var targets = scanData.getAnnotations().stream()
            .filter(data -> AUTO_SUBSCRIBER.equals(data.annotationType()))
            .toList();

        var onlyIns = FMLEnvironment.production ? Collections.emptySet() : scanData.getAnnotations().stream()
                .filter(data -> ONLY_IN_TYPE.equals(data.annotationType()))
                .map(data -> data.clazz().getClassName())
                .collect(Collectors.toSet());

        var modids = scanData.getAnnotations().stream()
            .filter(data -> MOD_TYPE.equals(data.annotationType()))
            .collect(Collectors.toMap(a -> a.clazz().getClassName(), a -> (String)a.annotationData().get("value")));

        var defaultSides = List.of(new EnumData(null, "CLIENT"), new EnumData(null, "DEDICATED_SERVER"));
        var defaultBus = new EnumData(null, "FORGE");

        for (var data : targets) {
            if (!FMLEnvironment.production && onlyIns.contains(data.clazz().getClassName())) {
                throw new RuntimeException("Found @OnlyIn on @EventBusSubscriber class " + data.clazz().getClassName() + " - this is not allowed as it causes crashes. Remove the OnlyIn and set value=Dist.CLIENT in the EventBusSubscriber annotation instead");
            }

            var modId = modids.getOrDefault(data.clazz().getClassName(), mod.getModId());
            modId = value(data, "modid", modId);

            var sidesValue = value(data, "value", defaultSides);
            var sides = sidesValue.stream()
                .map(EnumData::value)
                .map(Dist::valueOf)
                .collect(Collectors.toSet());

            var busName = value(data, "bus", defaultBus).value();
            var busTarget = Bus.valueOf(busName);
            if (Objects.equals(mod.getModId(), modId) && sides.contains(FMLEnvironment.dist)) {
                try {
                    LOGGER.debug(Logging.LOADING, "Auto-subscribing {} to {}", data.clazz().getClassName(), busTarget);
                    EventBusSubscriberLogic.register(busTarget.bus().get(), Class.forName(data.clazz().getClassName(), true, loader));
                } catch (ClassNotFoundException e) {
                    LOGGER.fatal(Logging.LOADING, "Failed to load mod class {} for @EventBusSubscriber annotation", data.clazz(), e);
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <R> R value(AnnotationData data, String key, R value) {
        return (R)data.annotationData().getOrDefault(key, value);
    }

    /**
     * This is mostly a carbon copy of EventBus 7's internal EventListenerFactory, with some changes to support adding
     * to different BusGroups based on the event type, among other things.
     */
    private static final class EventBusSubscriberLogic {
        private EventBusSubscriberLogic() {}

        private static final boolean STRICT_RUNTIME_CHECKS = Boolean.getBoolean("eventbus.api.strictRuntimeChecks");

        /**
         * If true, performs exhaustive validation on bulk registration to aid debugging.
         */
        private static final boolean STRICT_REGISTRATION_CHECKS = STRICT_RUNTIME_CHECKS || Boolean.getBoolean("eventbus.api.strictRegistrationChecks");

        private static final MethodType RETURNS_CONSUMER = MethodType.methodType(Consumer.class);
        private static final MethodType RETURNS_PREDICATE = MethodType.methodType(Predicate.class);
        private static final MethodType RETURNS_MONITOR = MethodType.methodType(ObjBooleanBiConsumer.class);

        /** The method type of the {@link Consumer} functional interface ({@code void accept(Object)}) */
        private static final MethodType CONSUMER_FI_TYPE = MethodType.methodType(void.class, Object.class);

        /** The method type of the {@link Predicate} functional interface ({@code boolean test(Object)}) */
        private static final MethodType PREDICATE_FI_TYPE = CONSUMER_FI_TYPE.changeReturnType(boolean.class);

        /** The method type of the {@link ObjBooleanBiConsumer} functional interface ({@code void accept(Object, boolean)}) */
        private static final MethodType MONITOR_FI_TYPE = MethodType.methodType(void.class, Object.class, boolean.class);

        public static void register(@Nullable BusGroup busGroup, Class<?> listenerClass) {
            if (STRICT_REGISTRATION_CHECKS) registerStrict(busGroup, listenerClass);
            else registerLenient(busGroup, listenerClass);
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        public static void registerLenient(@Nullable BusGroup busGroup, Class<?> listenerClass) {
            Method[] declaredMethods = listenerClass.getDeclaredMethods();
            if (declaredMethods.length == 0)
                throw new IllegalArgumentException("No declared methods found in " + listenerClass);

            Class<?> firstValidListenerEventType = null;

            int listenersCount = 0;
            for (var method : declaredMethods) {
                if (!Modifier.isStatic(method.getModifiers()))
                    continue;

                if (method.isSynthetic())
                    continue; // EventBus#89

                int paramCount = method.getParameterCount();
                if (paramCount == 0 || paramCount > 2)
                    continue;

                Class<?> returnType = method.getReturnType();
                if (returnType != void.class && returnType != boolean.class)
                    continue;

                if (!method.isAnnotationPresent(SubscribeEvent.class))
                    continue;

                Class<?>[] parameterTypes = method.getParameterTypes();
                if (!Event.class.isAssignableFrom(parameterTypes[0]))
                    throw new IllegalArgumentException("First parameter of a @SubscribeEvent method must be an event");

                Class<? extends Event> eventType = (Class<? extends Event>) parameterTypes[0];
                var subscribeEventAnnotation = method.getAnnotation(SubscribeEvent.class);

                registerListener(busGroup, paramCount, returnType, eventType, subscribeEventAnnotation, method);
                listenersCount++;

                if (firstValidListenerEventType == null)
                    firstValidListenerEventType = eventType;
            }

            if (listenersCount == 0)
                throw new IllegalArgumentException("No listeners found in " + listenerClass);
            else if (firstValidListenerEventType == null)
                throw new IllegalArgumentException("No valid listeners found in " + listenerClass);
        }

        @SuppressWarnings({"unchecked"})
        public static void registerStrict(BusGroup busGroup, Class<?> listenerClass) {
            Class<? extends Event> firstValidListenerEventType = null;

            List<Method> declaredMethods = Arrays.stream(listenerClass.getDeclaredMethods())
                    .filter(Predicate.not(Method::isSynthetic)) // EventBus#89
                    .toList();
            if (declaredMethods.isEmpty()) {
                var errMsg = "No declared methods found in " + listenerClass.getName();
                var superClass = listenerClass.getSuperclass();
                if (superClass != null && superClass != Record.class && superClass != Enum.class) {
                    errMsg += ". Note that listener inheritance is not supported. " +
                            "If you are trying to inherit listeners, please use @Override and @SubscribeEvent on the method in the subclass.";
                }
                throw fail(listenerClass, errMsg);
            }

            int listenersCount = 0;
            for (var method : declaredMethods) {
                var hasSubscribeEvent = method.isAnnotationPresent(SubscribeEvent.class);
                int paramCount = method.getParameterCount();

                if (hasSubscribeEvent && (paramCount == 0 || paramCount > 2))
                    throw fail(method, "Invalid number of parameters: " + paramCount + " (expected 1 or 2)");

                if (paramCount == 0)
                    continue;

                Class<?> returnType = method.getReturnType();
                Class<?>[] parameterTypes = method.getParameterTypes();
                var firstParamExtendsEvent = Event.class.isAssignableFrom(parameterTypes[0]);

                if (!hasSubscribeEvent && firstParamExtendsEvent)
                    throw fail(method, "Missing @SubscribeEvent annotation");

                if (hasSubscribeEvent) {
                    var firstParamExtendsCancellable = Cancellable.class.isAssignableFrom(parameterTypes[0]);
                    var subscribeEventAnnotation = method.getAnnotation(SubscribeEvent.class);
                    var isMonitoringPriority = subscribeEventAnnotation.priority() == Priority.MONITOR;

                    if (!firstParamExtendsEvent)
                        throw fail(method, "First parameter of a @SubscribeEvent method must be an event");

                    var eventType = (Class<? extends Event>) parameterTypes[0];

                    if (returnType != void.class && returnType != boolean.class)
                        throw fail(method, "Invalid return type: " + returnType.getName() + " (expected void or boolean)");

                    if (!Modifier.isStatic(method.getModifiers()))
                        throw fail(method, "Listener method needs to be static");

                    if (isMonitoringPriority && (returnType == boolean.class || subscribeEventAnnotation.alwaysCancelling()))
                        throw fail(method, "Monitoring listeners cannot cancel events");

                    if (paramCount == 2) {
                        if (!firstParamExtendsCancellable)
                            throw fail(method, "Cancellation-aware monitoring listeners are only valid for cancellable events");

                        if (!boolean.class.isAssignableFrom(parameterTypes[1]))
                            throw fail(method, "Second parameter of a cancellation-aware monitoring listener must be a boolean");

                        if (!isMonitoringPriority)
                            throw fail(method, "Cancellation-aware monitoring listeners must have a priority of MONITOR");
                    }

                    if (!firstParamExtendsCancellable) {
                        if (subscribeEventAnnotation.alwaysCancelling())
                            throw fail(method, "Always cancelling listeners are only valid for cancellable events");

                        if (returnType == boolean.class)
                            throw fail(method, "Return type boolean is only valid for cancellable events");
                    }

                    registerListener(busGroup, paramCount, returnType, eventType, subscribeEventAnnotation, method);
                    listenersCount++;

                    if (firstValidListenerEventType == null)
                        firstValidListenerEventType = eventType;
                }
            }

            if (listenersCount == 0)
                throw fail(listenerClass, "No listeners found");
            else if (firstValidListenerEventType == null)
                throw fail(listenerClass, "No valid listeners found");
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        private static EventListener registerListener(@Nullable BusGroup busGroup,
                                                      int paramCount, Class<?> returnType, Class<? extends Event> eventType,
                                                      SubscribeEvent subscribeEventAnnotation, Method method) {
            if (busGroup == null) {
                busGroup = IModBusEvent.class.isAssignableFrom(eventType)
                        ? FMLJavaModLoadingContext.get().getModBusGroup()
                        : BusGroup.DEFAULT;
            }

            // determine the listener type from its parameters and return type
            if (paramCount == 1) {
                var priority = subscribeEventAnnotation.priority();
                if (returnType == void.class) {
                    if (Cancellable.class.isAssignableFrom(eventType)) {
                        // Consumer<Event & Cancellable>
                        var eventBus = CancellableEventBus.create(busGroup, (Class) eventType);
                        if (subscribeEventAnnotation.alwaysCancelling()) {
                            return eventBus.addListener(priority, true, createConsumer(method));
                        } else {
                            return eventBus.addListener(priority, createConsumer(method));
                        }
                    } else {
                        // Consumer<Event>
                        return EventBus.create(busGroup, eventType)
                                .addListener(priority, createConsumer(method));
                    }
                } else {
                    // Predicate<Event & EventCharacteristic.Cancellable>
                    if (!Cancellable.class.isAssignableFrom(eventType))
                        throw fail(method, "Return type boolean is only valid for cancellable events");

                    if (subscribeEventAnnotation.alwaysCancelling())
                        throw new IllegalArgumentException("Always cancelling listeners must have a void return type");

                    return CancellableEventBus.create(busGroup, (Class) eventType)
                            .addListener(priority, createPredicate(method));
                }
            } else {
                // ObjBooleanBiConsumer<Event & Cancellable>
                if (returnType != void.class)
                    throw new IllegalArgumentException("Cancellation-aware monitoring listeners must have a void return type");

                if (subscribeEventAnnotation.alwaysCancelling())
                    throw new IllegalArgumentException("Monitoring listeners cannot cancel events");

                return CancellableEventBus.create(busGroup, (Class) eventType).addListener(createMonitor(method));
            }
        }

        private static IllegalArgumentException fail(Class<?> listenerClass, String reason) {
            return new IllegalArgumentException("Failed to register " + listenerClass.getName() + ": " + reason);
        }

        private static IllegalArgumentException fail(Method mtd, String reason) {
            return new IllegalArgumentException("Failed to register " + mtd.getDeclaringClass().getName() + "." + mtd.getName() + ": " + reason);
        }

        @SuppressWarnings("unchecked")
        private static <T extends Event> Consumer<T> createConsumer(Method callback) {
            var factoryMH = getOrMakeFactory(callback, RETURNS_CONSUMER, CONSUMER_FI_TYPE, "accept");

            try {
                return (Consumer<T>) factoryMH.invokeExact();
            } catch (Exception e) {
                throw makeRuntimeException(callback, e);
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }

        @SuppressWarnings("unchecked")
        private static <T extends Event> Predicate<T> createPredicate(Method callback) {
            var factoryMH = getOrMakeFactory(callback, RETURNS_PREDICATE, PREDICATE_FI_TYPE, "test");

            try {
                return (Predicate<T>) factoryMH.invokeExact();
            } catch (Exception e) {
                throw makeRuntimeException(callback, e);
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }

        @SuppressWarnings("unchecked")
        private static <T extends Event> ObjBooleanBiConsumer<T> createMonitor(Method callback) {
            var factoryMH = getOrMakeFactory(callback, RETURNS_MONITOR, MONITOR_FI_TYPE, "accept");

            try {
                return (ObjBooleanBiConsumer<T>) factoryMH.invokeExact();
            } catch (Exception e) {
                throw makeRuntimeException(callback, e);
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }

        private static MethodHandle getOrMakeFactory(Method callback, MethodType factoryReturnType,
                                                     MethodType fiMethodType, String fiMethodName) {
            return makeFactory(callback, factoryReturnType, fiMethodType, fiMethodName);
        }

        private static MethodHandle makeFactory(Method callback,
                                                MethodType factoryReturnType,
                                                MethodType fiMethodType, String fiMethodName) {
            final class DodgyLookup {
                private DodgyLookup() {}
                private static final MethodHandles.Lookup INSTANCE;
                static {
                    try {
                        var lookupField = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
                        UnsafeHacks.setAccessible(lookupField);
                        INSTANCE = (MethodHandles.Lookup) lookupField.get(null);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            try {
                var callbackDeclaredClass = callback.getDeclaringClass();
                var lookup = DodgyLookup.INSTANCE.in(callbackDeclaredClass);
                var mh = lookup.unreflect(callback);

                return LambdaMetafactory.metafactory(
                        lookup, fiMethodName, factoryReturnType, fiMethodType, mh, mh.type()
                ).getTarget();
            } catch (Exception e) {
                throw makeRuntimeException(callback, e);
            }
        }

        private static RuntimeException makeRuntimeException(Method callback, Exception e) {
            return switch (e) {
                case IllegalAccessException iae -> {
                    var errMsg = "Failed to create listener";
                    if (!Modifier.isPublic(callback.getModifiers()))
                        errMsg += " - is it public?";

                    yield new RuntimeException(errMsg, iae);
                }
                case NullPointerException npe -> new RuntimeException(
                        "Failed to create listener - was given a non-static method without an instance to invoke it with",
                        npe
                );
                default -> new RuntimeException("Failed to create listener", e);
            };
        }
    }
}
