package net.minecraftforge.client.model;

import net.minecraft.util.Icon;

public interface IModelCustom {
    String getType();
    void renderAll();
    void renderPart(String partName);
    void renderPart(String partName, Icon icon);
}
