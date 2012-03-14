package net.minecraft.src;

import net.minecraft.client.Minecraft;

public class EntityRendererProxy extends EntityRenderer
{
    private Minecraft game;

    public EntityRendererProxy(Minecraft var1)
    {
        super(var1);
        this.game = var1;
    }

    /**
     * Will update any inputs that effect the camera angle (mouse) and then render the world and GUI
     */
    public void updateCameraAndRender(float var1)
    {
        super.updateCameraAndRender(var1);
        ModLoader.onTick(var1, this.game);
    }
}
