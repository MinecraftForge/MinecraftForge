package net.minecraftforge.client.event;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

/**
 * Allows users to register custom {@link net.minecraft.client.gui.screens.inventory.AbstractContainerScreen menu screens}.
 *
 * <p>This event is not {@linkplain net.minecraftforge.eventbus.api.Cancelable cancelable},
 * and does not have a {@linkplain net.minecraftforge.eventbus.api.Event.HasResult result}
 * </p>
 * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 */
public class RegisterMenuScreensEvent extends Event implements IModBusEvent
{
    private final Map<MenuType<?>, MenuScreens.ScreenConstructor<?, ?>> map;

    @ApiStatus.Internal
    public RegisterMenuScreensEvent(Map<MenuType<?>, MenuScreens.ScreenConstructor<?, ?>> map)
    {
        this.map = map;
    }

    public void register(MenuType<?> menuType, MenuScreens.ScreenConstructor<?, ?> screenConstructor) {
        map.put(menuType, screenConstructor);
    }
}