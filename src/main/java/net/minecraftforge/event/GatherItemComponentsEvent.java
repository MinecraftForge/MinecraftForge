package net.minecraftforge.event;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

public class GatherItemComponentsEvent extends Event
{
    private final Item item;
    private final DataComponentMap.Builder components = DataComponentMap.builder();

    public GatherItemComponentsEvent(Item item) {
        this.item = item;
    }

    public <T> void register(DataComponentType<T> componentType, @Nullable T value) {
        components.set(componentType, value);
    }

    public DataComponentMap getDataComponentMap() {
        return components.build();
    }

    public Item getItem() {
        return item;
    }
}
