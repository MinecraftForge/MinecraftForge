package net.minecraftforge.client;

import javax.annotation.Nullable;

import net.minecraft.util.ResourceLocation;

public class SkyLayer {
    private final ResourceLocation id;
    private SkyLayerGroup group = null;
    private IRenderHandler renderer = null;

    SkyLayer(ResourceLocation idIn)
    {
        this.id = idIn;
    }

    /**
     * Gives the sub-layer group of this layer.
     * If this layer is not a group, gives this layer sub-layer group.
     * @return the sub-layer group of this layer
     * */
    public SkyLayerGroup asGroup()
    {
        if(this.group == null)
        {
            this.group = new SkyLayerGroup();
            this.renderer = null;
        }

        return this.group;
    }

    /**
     * Check if this layer is group.
     * @return 
     * */
    public boolean isGroup()
    {
        return this.group != null;
    }

    /**
     * Replace the renderer assigned to this layer with the specified renderer.
     * If this layer was a group, this removes the sub-layer group from this layer
     * @param rendererIn the renderer to set
     * */
    public void replace(IRenderHandler rendererIn)
    {
        this.group = null;
        this.renderer = rendererIn;
    }

    /**
     * @return renderer assigned to this layer, or <code>null</code> if it doesn't present
     * */
    public @Nullable IRenderHandler getRenderer()
    {
        return this.renderer;
    }

    @Override
    public boolean equals(Object o)
    {
        if(o instanceof SkyLayer)
        {
            return this.id.equals(((SkyLayer)o).id);
        }
        else return false;
    }

    @Override
    public int hashCode()
    {
        return this.id.hashCode();
    }
}
