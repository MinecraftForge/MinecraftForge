package net.minecraftforge.debug.gameplay.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.GatherComponentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.test.BaseTestMod;

import java.util.List;

@Mod(GatherComponentsEventTest.MOD_ID)
public class GatherComponentsEventTest extends BaseTestMod {
    public static final String MOD_ID = "gather_components_test_event";

    public GatherComponentsEventTest() {
        IEventBus bus = MinecraftForge.EVENT_BUS;
        bus.addListener(this::onItem);
    }

    public void onItem(GatherComponentsEvent.Item itemEvent) {
        if (!itemEvent.getOriginalComponentMap().has(DataComponents.FOOD) && itemEvent.getOwner() == Items.IRON_NUGGET) {
            itemEvent.register(DataComponents.FOOD, new FoodProperties(
                    2,
                    7,
                    true,
                    3,
                    List.of()
            ));
        }
    }
}
