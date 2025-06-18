package net.minecraftforge.client.event;

import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.function.Function;

public final class RegisterPictureInPictureRendererEvent extends MutableEvent {
    public static final EventBus<RegisterPictureInPictureRendererEvent> BUS = EventBus.create(RegisterPictureInPictureRendererEvent.class);

    private final List<PictureInPictureRenderer<?>> renderers;
    private final MultiBufferSource.BufferSource bufferSource;

    @ApiStatus.Internal
    public RegisterPictureInPictureRendererEvent(List<PictureInPictureRenderer<?>> renderers, MultiBufferSource.BufferSource bufferSource) {
        this.renderers = renderers;
        this.bufferSource = bufferSource;
    }

    public void register(Function<MultiBufferSource.BufferSource, PictureInPictureRenderer<?>> function) {
        this.renderers.add(function.apply(this.bufferSource));
    }
}
