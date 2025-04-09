package net.minecraftforge.client.event;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus;

/**
 * Allows users to register custom {@linkplain ItemModel item model types}.
 *
 * <p>This event is not {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}.
 *
 * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @see RegisterSpecialModelRenderersEvent
 */
public class RegisterItemModelsEvent extends Event implements IModBusEvent
{

    @ApiStatus.Internal
    public RegisterItemModelsEvent() {}

    /**
     * Register an {@linkplain ItemModel.Unbaked unbaked item model} {@linkplain MapCodec map codec} with a specific id.
     */
    public void register(ResourceLocation id, MapCodec<? extends ItemModel.Unbaked> codec) {
        ItemModels.register(id, codec);
    }
}