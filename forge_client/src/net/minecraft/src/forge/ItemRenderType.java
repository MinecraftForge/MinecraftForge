package net.minecraft.src.forge;

@Deprecated //Now consolidated into IITemRenderer
public enum ItemRenderType
{
    ENTITY, // Render type for in-world EntityItems
    EQUIPPED, // Render type for an item equipped in-hand
    INVENTORY; // Render type for items shown in a GUI inventory slot
}
