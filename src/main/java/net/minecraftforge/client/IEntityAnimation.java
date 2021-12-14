package net.minecraftforge.client;

import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public interface IEntityAnimation<E extends LivingEntity> extends Comparable<IEntityAnimation<E>>
{
    boolean canRun(E entity);

    void apply(E entity, EntityModel<E> model, Context context);

    default Mode getMode()
    {
        return Mode.ACTIVE;
    }

    default Priority getPriority()
    {
        return Priority.DEFAULT;
    }

    @Override
    default int compareTo(@NotNull IEntityAnimation<E> o)
    {
        if(this.getMode() == o.getMode())
        {
            return this.getPriority().ordinal() - o.getPriority().ordinal();
        }
        else if(o.getMode() == Mode.ACTIVE)
        {
            return -1;
        }
        return 1;
    }

    enum Mode
    {
        PASSIVE, ACTIVE
    }

    enum Priority
    {
        FIRST, DEFAULT, LAST
    }

    record Context(float animateTicks, float animateSpeed, float bobAnimateTicks, float headYaw, float headPitch, float partialTicks) {}
}
