package net.minecraftforge.fml.client.registry;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;

public interface IRenderFactory<T extends Entity>
{
    public Render<? super T> createRenderFor(RenderManager manager);
}
