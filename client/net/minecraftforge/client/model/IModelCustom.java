package net.minecraftforge.client.model;



public interface IModelCustom {
    
    public abstract void load(String fileName);
    
    public abstract void renderAll();
    
    public abstract void renderPart(String partName);

}
