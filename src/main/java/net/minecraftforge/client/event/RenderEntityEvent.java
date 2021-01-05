package net.minecraftforge.client.event;

import net.minecraft.entity.Entity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class RenderEntityEvent extends Event
{

    private final Entity entity;

    public RenderEntityEvent(Entity entity)
    {
        this.entity = entity;
    }

    public Entity getEntity() { return entity; }

}
