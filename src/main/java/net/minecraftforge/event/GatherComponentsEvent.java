package net.minecraftforge.event;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

/**
 Where all the events fore Gathering Components will exist.
 */

public abstract class GatherComponentsEvent extends Event {
    private final DataComponentMap.Builder components = DataComponentMap.builder();

    public <T> void register(DataComponentType<T> componentType, @Nullable T value) {
        components.set(componentType, value);
    }

    @ApiStatus.Internal
    public DataComponentMap getDataComponentMap() {
        return components.build();
    }


    public static class Item extends GatherComponentsEvent {
        private final net.minecraft.world.item.Item item;
        private final DataComponentMap dataComponents;

        public Item(net.minecraft.world.item.Item item, DataComponentMap dataComponents) {
            this.item = item;
            this.dataComponents = dataComponents;
        }

        public net.minecraft.world.item.Item getItem() {
            return item;
        }

        public DataComponentMap getDataComponentMap() {
            return dataComponents;
        }
    }
}
