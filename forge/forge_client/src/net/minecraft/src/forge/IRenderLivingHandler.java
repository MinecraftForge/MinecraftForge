package net.minecraft.src.forge;

import net.minecraft.src.EntityLiving;
import net.minecraft.src.ModelBase;
import net.minecraft.src.RenderLiving;

public interface IRenderLivingHandler
{
    /**
     * Called before an EntityLiving's Rendering is done. You can use this to prevent Minecraft from rendering an
     * EntityLiving, and then using postRender for Rendering it yourself however you want it.
     * Prevents subsequent Renderpasses and equipped Items, also the 'Damage' state from being rendered.
     * GLTranslation, Color, Light and Animation considerations are already done here.
     * 
     * @param renderer Instance of RenderLiving for Entity Type
     * @param mainModel EntityLiving Main Model
     * @param renderPassModel EntityLiving Render Pass Model (eg Pig Saddle, Dragon Addons) is null on most Entities
     * @param entity EntityLiving being rendererd
     * @return false to let the main Renderer do it's thing, true to prevent the EntityLiving from being rendered at all
     */
    public boolean overrideRender(RenderLiving renderer, ModelBase mainModel, ModelBase renderPassModel, EntityLiving entity);
    
    /**
     * Called immediatly after an EntityLiving's main Rendering was done (also when overridden).
     * If you want to replace an Entities Renderer on the fly, this is the place.
     * Also, if you want to overlay an EntityLiving with another texture or FX.
     * The GLTranslation, Color, Light and Animation considerations are already done here, so
     * all you have to do is call a render method. RenderPasses (if present) take place after this.
     * 
     * @param renderer Instance of RenderLiving for Entity Type
     * @param mainModel EntityLiving Main Model
     * @param renderPassModel EntityLiving Render Pass Model (eg Pig Saddle, Dragon Addons) is null on most Entities
     * @param entity EntityLiving being rendererd
     */
    public void postRender(RenderLiving renderer, ModelBase mainModel, ModelBase renderPassModel, EntityLiving entity);
    
    /**
     * Called after an EntityLiving's RenderPasses have completed (also when overridden),
     * if you wish to do your own additional RenderPasses.
     * 
     * @param renderer Instance of RenderLiving for Entity Type
     * @param mainModel EntityLiving Main Model
     * @param renderPassModel EntityLiving Render Pass Model (eg Pig Saddle, Dragon Addons) is null on most Entities
     * @param entity EntityLiving being rendererd
     */
    public void postRenderPasses(RenderLiving renderer, ModelBase mainModel, ModelBase renderPassModel, EntityLiving entity);
}
