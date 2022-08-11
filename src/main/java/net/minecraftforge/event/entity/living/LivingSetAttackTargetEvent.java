/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;

/**
 * LivingSetAttackTargetEvent is fired when an Entity sets a target to attack.<br>
 * This event is fired whenever an Entity sets a target to attack in
 * {@link Mob#setTarget(LivingEntity)}.<br>
 * <br>
 * This event is fired via the {@link ForgeHooks#onLivingSetAttackTarget(LivingEntity, LivingEntity)}.<br>
 * <br>
 * {@link #target} contains the newly targeted Entity.<br>
 * <br>
 * This event is not {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class LivingSetAttackTargetEvent extends LivingEvent
{
    private ILivingTargetType targetType;
    
    public LivingSetAttackTargetEvent(LivingEntity entity, LivingEntity target)
    {
        super(entity);
        this.targetType = LivingTargetType.GOAL_TARGET;
        
        assertLivingIsValid();
    }
    
    public LivingSetAttackTargetEvent(LivingEntity entity, ILivingTargetType targetType)
    {
        super(entity);
        this.targetType = targetType;
        
        assertLivingIsValid();
    }

    /**
     * {@return the current target this living entity has.}
     */
    public @Nullable LivingEntity getTarget()
    {
        return targetType.getTarget(getEntity()).orElse(null);
    }

    /**
     * @param target The living entity that shall be set as a target.
     * {@return {@code true} when the target is valid, otherwise {@code false}.}
     */
    public boolean setTarget(@Nullable LivingEntity target)
    {
        if(targetType.isValidTarget(target))
        {
            targetType.setTarget(getEntity(), target);
            return true;
        }
        
        return false;
    }
    
    /**
     * {@return the target type of this event.}
     */
    public ILivingTargetType getTargetType()
    {
        return targetType;
    }
    
    /**
     * Throws an exception when the living entity is not valid for this target type.
     */
    private void assertLivingIsValid()
    {
        if(!this.targetType.isValidLiving(getEntity()))
        {
            throw new IllegalArgumentException("The living entity provided to this event is not compatible with the chosen target type.");
        }
    }
    
    // Short note on why this is needed: If we were to just let the event "return" data like in the ComputeFovModifierEvent,
    // this would introduce lots of bugs and breaking changes, so we need to do this mess.
    // 
    // Note for any future developer that might stumble across this: Please introduce a breaking change
    // later down the line (like, when porting forge to 1.20 or 1.21; this is the reaons why all the members of this class are marked for removal) 
    // and do all of this like it is done in the ComputeFovModifierEvent. By that time, most mod developers will
    // (hopefully) use LivingSetAttackTargetEvent#setTarget instead of setting the target on their own, which will
    // minimize the impact of such a breaking change.
    public static interface ILivingTargetType
    {
        /**
         * @param living The living entity whose target shall be returned.
         * {@return the current target of this living entity.}
         */
        @Deprecated(forRemoval = true)
        Optional<LivingEntity> getTarget(LivingEntity living);
        
       
        /**
         * Sets the target of the living entity.
         * @param living The entity whose target shall be set.
         * @param target The new target.
         */
        @Deprecated(forRemoval = true)
        void setTarget(LivingEntity living, LivingEntity target);
        
        /**
         * Tests whether the given living entity whose target may be set later is valid for this living target type.
         * 
         * @param living The living entity which shall be tested.
         * {@return {@code true} when the test was successful, {@code false} otherwise}
         */
        @Deprecated(forRemoval = true)
        boolean isValidLiving(LivingEntity living);
        
        /**
         * Tests whether the given target entity is valid or not.
         * 
         * @param The target which shall be tested.
         * {@return {@code true} when the target is valid for this target type, {@code false} otherwise}
         */
        @Deprecated(forRemoval = true)
        boolean isValidTarget(LivingEntity target);
    }
    
    public static enum LivingTargetType implements ILivingTargetType
    {
        GOAL_TARGET(living -> Optional.ofNullable(((Mob) living).getTarget()), (living, target) -> ((Mob) living).setTarget(target), livingValid -> livingValid == null || livingValid instanceof Mob,
                targetValid -> true),
        BEHAVIOR_TARGET(living -> living.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET),
                (living, target) -> living.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, target), livingValid -> true, targetValid -> true);
        
        private Function<LivingEntity, Optional<LivingEntity>> targetGetter;
        private BiConsumer<LivingEntity, LivingEntity> targetSetter;
        private Predicate<LivingEntity> livingValid;
        private Predicate<LivingEntity> targetValid;
        
        private LivingTargetType(Function<LivingEntity, Optional<LivingEntity>> targetGetter, BiConsumer<LivingEntity, LivingEntity> targetSetter,
                Predicate<LivingEntity> livingValid, Predicate<LivingEntity> targetValid)
        {
            this.targetGetter = targetGetter;
            this.targetSetter = targetSetter;
            this.livingValid = livingValid;
            this.targetValid = targetValid;
        }
        
        @Override
        public Optional<LivingEntity> getTarget(LivingEntity living)
        {
            return targetGetter.apply(living);
        }
        
        @Override
        public void setTarget(LivingEntity living, LivingEntity target)
        {
            targetSetter.accept(living, target);
        }
        
        @Override
        public boolean isValidLiving(LivingEntity living)
        {
            return livingValid.test(living);
        }
        
        @Override
        public boolean isValidTarget(LivingEntity target)
        {
            return targetValid.test(target);
        }
    }
}
