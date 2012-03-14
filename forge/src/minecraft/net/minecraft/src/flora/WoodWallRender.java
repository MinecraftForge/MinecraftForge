package net.minecraft.src.flora;

import net.minecraft.src.*;

public class WoodWallRender extends RenderLiving
{
    public WoodWallRender(ModelBase modelbase, float f)
    {
        super(modelbase, f);
    }

    public void renderWall(WoodWallEntity woodwallentity, double d, double d1, double d2,
            float f, float f1)
    {
        super.doRenderLiving(woodwallentity, d, d1, d2, f, f1);
    }

    public void doRenderLiving(EntityLiving entityliving, double d, double d1, double d2,
            float f, float f1)
    {
        renderWall((WoodWallEntity)entityliving, d, d1, d2, f, f1);
    }

    public void doRender(Entity entity, double d, double d1, double d2,
            float f, float f1)
    {
        renderWall((WoodWallEntity)entity, d, d1, d2, f, f1);
    }
}
