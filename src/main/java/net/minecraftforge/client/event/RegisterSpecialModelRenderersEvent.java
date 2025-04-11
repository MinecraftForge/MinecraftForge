package net.minecraftforge.client.event;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus;

/**
 * Allows users to register custom {@linkplain SpecialModelRenderer special model renderers}, which can be used with the "minecraft:special" model type.
 *
 * <p>This event is not {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}.
 *
 * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @see RegisterItemModelsEvent
 */
public class RegisterSpecialModelRenderersEvent extends Event implements IModBusEvent {
    @ApiStatus.Internal
    public RegisterSpecialModelRenderersEvent() {}

    /** Register an {@linkplain SpecialModelRenderer.Unbaked unbaked special model renderer} {@linkplain MapCodec map codec} with a specific id. */
    public void register(ResourceLocation id, MapCodec<? extends SpecialModelRenderer.Unbaked> codec) {
        SpecialModelRenderers.register(id, codec);
    }
}