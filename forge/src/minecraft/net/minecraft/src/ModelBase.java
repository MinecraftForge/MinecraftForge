package net.minecraft.src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ModelBase
{
    public float onGround;
    public boolean isRiding = false;

    /**
     * This is a list of all the boxes (ModelRenderer.class) in the current model.
     */
    public List boxList = new ArrayList();
    public boolean isChild = true;

    /** A mapping for all texture offsets */
    private Map modelTextureMap = new HashMap();
    public int textureWidth = 64;
    public int textureHeight = 32;

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7) {}

    /**
     * Sets the models various rotation angles.
     */
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6) {}

    /**
     * Used for easily adding entity-dependent animations. The second and third float params here are the same second
     * and third as in the setRotationAngles method.
     */
    public void setLivingAnimations(EntityLiving par1EntityLiving, float par2, float par3, float par4) {}

    protected void setTextureOffset(String par1Str, int par2, int par3)
    {
        this.modelTextureMap.put(par1Str, new TextureOffset(par2, par3));
    }

    public TextureOffset getTextureOffset(String par1Str)
    {
        return (TextureOffset)this.modelTextureMap.get(par1Str);
    }
}
