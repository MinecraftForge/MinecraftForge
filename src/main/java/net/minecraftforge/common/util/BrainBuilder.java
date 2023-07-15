/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.ApiStatus;

/**
 * This object is used to encapsulate state found inside a {@link Brain} instance,
 * to make it easily accessible for modders to manipulate during {@link net.minecraftforge.event.entity.living.LivingMakeBrainEvent}.<br>
 * <br>
 * Provided are a variety of getter/setter methods to access and manipulate the encapsulated state.<br>
 * <br>
 * Methods marked with "INTENDED FOR INTERNAL USE" are only meant to be used inside:
 * <ul>
 * <li>{@link net.minecraftforge.common.ForgeHooks#onLivingMakeBrain(LivingEntity, Brain, Dynamic)}
 * <li>{@link Brain#createBuilder()}
 * <li>{@link Brain#copyFromBuilder(BrainBuilder)}
 * </ul>
 * Of course, nothing egregious will happen should a modder choose to use them for their own purposes.<br>
 */
public class BrainBuilder<E extends LivingEntity> {
    private final Collection<MemoryModuleType<?>> memoryTypes = new HashSet<>();
    private final Collection<SensorType<? extends Sensor<? super E>>> sensorTypes = new HashSet<>();
    private final Map<Integer, Map<Activity, Set<BehaviorControl<? super E>>>> availableBehaviorsByPriority = Maps.newTreeMap();
    private Schedule schedule = Schedule.EMPTY;
    private final Map<Activity, Set<Pair<MemoryModuleType<?>, MemoryStatus>>> activityRequirements = Maps.newHashMap();
    private final Map<Activity, Set<MemoryModuleType<?>>> activityMemoriesToEraseWhenStopped = Maps.newHashMap();
    private final Set<Activity> coreActivities = Sets.newHashSet();
    private Activity defaultActivity = Activity.IDLE;

    public BrainBuilder(Brain<E> ignoredBrain) {}

    public Brain.Provider<E> provider() {
        return Brain.provider(this.memoryTypes, this.sensorTypes);
    }

    public Collection<MemoryModuleType<?>> getMemoryTypes() {
        return this.memoryTypes;
    }

    public Collection<SensorType<? extends Sensor<? super E>>> getSensorTypes() {
        return this.sensorTypes;
    }

    public Map<Integer, Map<Activity, Set<BehaviorControl<? super E>>>> getAvailableBehaviorsByPriority() {
        return this.availableBehaviorsByPriority;
    }

    public Schedule getSchedule() {
        return this.schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public Map<Activity, Set<Pair<MemoryModuleType<?>, MemoryStatus>>> getActivityRequirements() {
        return this.activityRequirements;
    }

    public Map<Activity, Set<MemoryModuleType<?>>> getActivityMemoriesToEraseWhenStopped() {
        return this.activityMemoriesToEraseWhenStopped;
    }

    public Set<Activity> getCoreActivities() {
        return this.coreActivities;
    }

    public Activity getDefaultActivity() {
        return this.defaultActivity;
    }

    public void setDefaultActivity(Activity defaultActivity) {
        this.defaultActivity = defaultActivity;
    }

    /** You may use this as a helper method for adding a behavior to an Activity by priority to an entity's brain. */
    public void addBehaviorToActivityByPriority(Integer priority, Activity activity, BehaviorControl<? super E> behaviorControl) {
        this.availableBehaviorsByPriority.computeIfAbsent(priority, (i) -> Maps.newHashMap()).computeIfAbsent(activity, (a) -> Sets.newLinkedHashSet()).add(behaviorControl);
    }

    /** You may use this as a helper method for adding memory requirements for an Activity to an entity's brain. */
    public void addRequirementsToActivity(Activity activity, Collection<Pair<MemoryModuleType<?>, MemoryStatus>> requirements) {
        addRequirementsToActivityInternal(this.activityRequirements, activity, requirements);
    }

    /** You may use this as a helper method for adding a collection of memories to erase when an Activity is stopped to entity's brain. */
    public void addMemoriesToEraseWhenActivityStopped(Activity activity, Collection<MemoryModuleType<?>> memories) {
        addMemoriesToEraseWhenActivityStoppedInternal(this.activityMemoriesToEraseWhenStopped, activity, memories);
    }

    //====================================================================================================================//
    // Everything below this is just to implement Brain.createBuilder and Brain.copyFromBuilder, hence marked as internal //
    //====================================================================================================================//

    @ApiStatus.Internal
    public void addAvailableBehaviorsByPriorityFrom(Map<Integer, Map<Activity, Set<BehaviorControl<? super E>>>> addFrom) {
        addFrom.forEach(((priority, activitySetMap) -> activitySetMap.forEach(((activity, behaviorControls) -> this.availableBehaviorsByPriority.computeIfAbsent(priority, (p) -> Maps.newHashMap()).computeIfAbsent(activity, (a) -> Sets.newLinkedHashSet()).addAll(behaviorControls)))));
    }

    @ApiStatus.Internal
    public void addAvailableBehaviorsByPriorityTo(Map<Integer, Map<Activity, Set<BehaviorControl<? super E>>>> addTo){
        this.availableBehaviorsByPriority.forEach(((priority, activitySetMap) -> activitySetMap.forEach(((activity, behaviorControls) -> addTo.computeIfAbsent(priority, (p) -> Maps.newHashMap()).computeIfAbsent(activity, (a) -> Sets.newLinkedHashSet()).addAll(behaviorControls)))));
    }

    @ApiStatus.Internal
    public void addActivityRequirementsFrom(Map<Activity, Set<Pair<MemoryModuleType<?>, MemoryStatus>>> addFrom) {
        addFrom.forEach(this::addRequirementsToActivity);
    }

    @ApiStatus.Internal
    public void addActivityRequirementsTo(Map<Activity, Set<Pair<MemoryModuleType<?>, MemoryStatus>>> addTo) {
        this.activityRequirements.forEach(((activity, requirements) -> addRequirementsToActivityInternal(addTo, activity, requirements)));
    }

    @ApiStatus.Internal
    public void addActivityMemoriesToEraseWhenStoppedFrom(Map<Activity, Set<MemoryModuleType<?>>> addFrom) {
        addFrom.forEach(this::addMemoriesToEraseWhenActivityStopped);
    }

    @ApiStatus.Internal
    public void addActivityMemoriesToEraseWhenStoppedTo(Map<Activity, Set<MemoryModuleType<?>>> addTo) {
        this.activityMemoriesToEraseWhenStopped.forEach(((activity, memories) -> addMemoriesToEraseWhenActivityStoppedInternal(addTo, activity, memories)));
    }

    private static void addMemoriesToEraseWhenActivityStoppedInternal(Map<Activity, Set<MemoryModuleType<?>>> activityMemoriesToEraseWhenStopped, Activity activity, Collection<MemoryModuleType<?>> memories) {
        activityMemoriesToEraseWhenStopped.computeIfAbsent(activity, (a) -> Sets.newHashSet()).addAll(memories);
    }

    private static void addRequirementsToActivityInternal(Map<Activity, Set<Pair<MemoryModuleType<?>, MemoryStatus>>> activityRequirements, Activity activity, Collection<Pair<MemoryModuleType<?>, MemoryStatus>> requirements) {
        activityRequirements.computeIfAbsent(activity, (a) -> Sets.newHashSet()).addAll(requirements);
    }

    @ApiStatus.Internal
    public Brain<E> makeBrain(Dynamic<?> dynamic) {
        Brain<E> brain = Brain.provider(this.memoryTypes, this.sensorTypes).makeBrain(dynamic);
        brain.copyFromBuilder(this);
        return brain;
    }
}
