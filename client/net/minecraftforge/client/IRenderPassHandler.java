package net.minecraftforge.client;

import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IRenderPassHandler {

    /**
     * Return the amount of render passes required.
     * 
     * @param entity The entity
     * @param renderer The renderer
     * @return The amount of render passes
     */
    public int getRenderPassCount(EntityLivingBase entity, RendererLivingEntity renderer);
    
    /**
     * Should this render pass be rendered
     * 
     * @param entity The entity
     * @param renderer The renderer
     * @param renderPass The current render pass
     * @param shouldRenderPass The old return value
     * @return Should this pass be rendered (-1 is no)
     */
    public int shouldRenderPass(EntityLivingBase entity, RendererLivingEntity renderer, int renderPass, int shouldRenderPass);
    
    /**
     * The render pass
     * 
     * @param entity The entity
     * @param renderer The renderer
     * @param renderPass The current render pass
     * @param shouldRenderPass Whether this pass should be rendered
     * @return Should the vanilla be executed (false cancels)
     */
    public boolean renderPass(EntityLivingBase entity, RendererLivingEntity renderer, int renderPass, int shouldRenderPass);
}
