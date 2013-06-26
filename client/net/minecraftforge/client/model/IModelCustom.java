package net.minecraftforge.client.model;



public interface IModelCustom {
    String getType();
    void renderAll();
    void renderOnly(String... groupNames);
    void renderPart(String partName);
    void renderAllExcept(String... excludedGroupNames);
}
