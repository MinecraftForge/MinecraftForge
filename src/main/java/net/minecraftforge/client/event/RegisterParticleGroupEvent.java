package net.minecraftforge.client.event;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import net.minecraftforge.eventbus.api.event.characteristic.SelfDestructing;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Map;

@NullMarked
public final class RegisterParticleGroupEvent extends MutableEvent implements SelfDestructing {
    private final Map<ParticleRenderType, ParticleGroupFactory> factories;
    private final List<ParticleRenderType> renderOrder;
    public static final EventBus<RegisterParticleGroupEvent> BUS = EventBus.create(RegisterParticleGroupEvent.class);

    public RegisterParticleGroupEvent(Map<ParticleRenderType, ParticleGroupFactory> factories, List<ParticleRenderType> renderOrder) {
        this.factories = factories;
        this.renderOrder = renderOrder;
    }

    public void register(ParticleRenderType type, ParticleGroupFactory factory) {
        if (factories.putIfAbsent(type, factory) != null) {
            throw new IllegalArgumentException(type.name() + " already has a factory registered.");
        }
        renderOrder.add(type);
    }

    @FunctionalInterface
    public interface ParticleGroupFactory {
        ParticleGroup<?> createGroup(ParticleEngine particleEngine);
    }
}
