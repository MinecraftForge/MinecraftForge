package net.minecraftforge.common.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface IModelCustom {
    String getType();
    @SideOnly(Side.CLIENT)
    void renderAll();
    @SideOnly(Side.CLIENT)
    void renderOnly(String... groupNames);
    @SideOnly(Side.CLIENT)
    void renderPart(String partName);
    @SideOnly(Side.CLIENT)
    void renderAllExcept(String... excludedGroupNames);
}
