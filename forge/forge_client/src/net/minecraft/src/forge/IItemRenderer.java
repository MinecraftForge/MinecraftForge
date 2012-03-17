package net.minecraft.src.forge;

import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.RenderBlocks;

public interface IItemRenderer
{
    /** Checks if this renderer should handle a specific item render type
     * @param type A render type to check if this renderer handles
     * @return true if this renderer should handle the given render type,
     * otherwise false
     */
    public boolean handleRenderType(ItemRenderType type);
    
    /** Called to render an in-world item, e.g. one that has been thrown or
     * dropped. The appropriate OpenGL transformations and scaling have already
     * been applied, so Tessellator location (0,0,0) is the center of the
     * EntityItem.
     * @param render The RenderBlocks instance
     * @param item The in-world item to be rendered
     * @param itemID The item's ID (shifted index)
     * @param metadata The item's metadata or damage value
     */
    public void renderEntityItem(RenderBlocks render, EntityItem item, int itemID, int metadata);
    
    /** Determines if a rotation effect should be used when rendering an
     * EntityItem, like most default blocks do.
     * @return true if the EntityItem should rotate, otherwise false
     */
    public boolean useEntityItemRotationEffect();
    
    /** Determines if an up-and-down bobbing effect should be used when
     * rendering an EntityItem, like all default blocks and items do.
     * @return true if the EntityItem should bob up and down, otherwise false
     */
    public boolean useEntityItemBobbingEffect();
    
    /** Called to render an item currently held in-hand by a living entity. If
     * rendering as a 3D block, the item will be rotated to a 45-degree angle.
     * To render a 2D texture with some thickness (like default items), see
     * net.minecraft.src.ItemRenderer. In either case, rendering should be done
     * in local coordinates from (0,0,0)-(1,1,1).
     * @param render The RenderBlocks instance
     * @param entity The entity holding this item
     * @param itemID The item's ID (shifted index)
     * @param metadata The item's metadata or damage value
     */
    public void renderEquippedItem(RenderBlocks render, EntityLiving entity, int itemID, int metadata);
    
    /** Determines if the currently equipped item should be rendered as a 3D
     * block or as a 2D texture.
     * @return true if the equipped item should be rendered as a 3D block,
     * otherwise false
     */
    public boolean renderEquippedItemAsBlock();
    
    /** Called to render an item in a GUI inventory slot. If rendering as a 3D
     * block, the appropriate OpenGL translations and scaling have already been
     * applied, and the rendering should be done in local coordinates from
     * (0,0,0)-(1,1,1). If rendering as a 2D texture, the rendering should be in
     * GUI pixel coordinates from (0, 0, 0)-(16, 16, 0).
     * @param render The RenderBlocks instance
     * @param itemID The item's ID (shifted index)
     * @param metadata The item's metadata or damage value
     */
    public void renderInventoryItem(RenderBlocks render, int itemID, int metadata);
    
    /** Determines if the item should be rendered in GUI inventory slots as a 3D
     * block or as a 2D texture.
     * @return true if the inventory item should be rendered as a 3D block,
     * otherwise false
     */
    public boolean renderInventoryItemAsBlock();
    
}
